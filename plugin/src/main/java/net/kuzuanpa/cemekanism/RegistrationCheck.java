package net.kuzuanpa.cemekanism;

import net.kuzuanpa.cemekanism.machine.ConfiguratorFaceRenderer;
import net.kuzuanpa.cemekanism.machine.MachineShapes;
import net.kuzuanpa.cemekanism.multiblock.MultiblockEffectRenderer;
import net.kuzuanpa.cemekanism.platform.MekSystems;
import net.kuzuanpa.cemekanism.radiation.RadiationWorld;
import net.kuzuanpa.cemekanism.transmitter.TransmitterShapes;
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.api.event.CraftEngineReloadEvent;
import net.momirealms.craftengine.core.registry.BuiltInRegistries;
import net.momirealms.craftengine.core.util.Key;
import net.kuzuanpa.cemekanism.machine.MachineBlockBehavior;
import org.bukkit.Bukkit;
import net.kuzuanpa.cemekanism.gui.GuiRegistry;
import net.kuzuanpa.cemekanism.i18n.I18n;
import net.kuzuanpa.cemekanism.transmitter.TransmitterBlockBehavior;
import net.kuzuanpa.cemekanism.transmitter.TransmitterBlocks;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import net.kuzuanpa.cemekanism.generated.GeneratedContent;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * 注册自检：清单来自 {@link GeneratedContent}（tools/mek.py 生成），
 * 每次载入资源包后强制检查，缺少必需资源时输出安装帮助并停服；/mek selfcheck 逐条列。
 */
final class RegistrationCheck implements Listener {

    /** 表驱动：物品清单与 方块→蓝图 映射都来自生成的登记片段 */
    private static final List<Key> ITEMS = GeneratedContent.ITEMS.stream().map(RegistrationCheck::key).toList();
    /** 方块清单 = 机器方块 + 传输元件（两类都要有方块实体才跑得起来）。 */
    private static final List<Key> BLOCKS = blockIds();

    private final CeMekanismPlugin plugin;

    RegistrationCheck(CeMekanismPlugin plugin) {
        this.plugin = plugin;
    }

    private static Key key(String id) {
        int colon = id.indexOf(':');
        return Key.of(id.substring(0, colon), id.substring(colon + 1));
    }

    private static List<Key> blockIds() {
        List<Key> ids = new ArrayList<>();
        GeneratedContent.BLOCK_TO_BLUEPRINT.keySet().stream().map(RegistrationCheck::key).forEach(ids::add);
        GeneratedContent.TRANSMITTERS.stream()
                .map(def -> key(def.blockId())).forEach(ids::add);
        GeneratedContent.RESOURCE_BLOCKS.stream().map(RegistrationCheck::key).forEach(ids::add);
        return List.copyOf(ids);
    }

    @EventHandler
    public void onCraftEngineReload(CraftEngineReloadEvent event) {
        List<String> missing = missingIds();
        if (!missing.isEmpty()) {
            this.plugin.getLogger().severe(I18n.tr("registration.failed", missing.size()));
            for (String id : missing) this.plugin.getLogger().severe(I18n.tr("registration.missing_item", id));
            this.plugin.getLogger().severe(I18n.tr("registration.help.title"));
            this.plugin.getLogger().severe(I18n.tr("registration.help.1"));
            this.plugin.getLogger().severe(I18n.tr("registration.help.2"));
            this.plugin.getLogger().severe(I18n.tr("registration.help.3"));
            this.plugin.getLogger().severe(I18n.tr("registration.help.4"));
            Bukkit.shutdown();
            return;
        }
        TransmitterShapes.apply(this.plugin,
                MekSystems.transmitterCollision6px());
        MachineShapes.apply(this.plugin);
        this.plugin.multiblocks().closeEffects();
        this.plugin.getLogger().info(describe(this.plugin));
    }

    static String describe(CeMekanismPlugin plugin) {
        List<String> missing = missingIds();
        long mapped = GeneratedContent.BLOCK_TO_BLUEPRINT.size();
        return missing.isEmpty()
                ? I18n.tr("registration.ready", ITEMS.size(), BLOCKS.size(), mapped,
                MachineBlockBehavior.KEY, behaviorState(),
                TransmitterBlockBehavior.KEY, transmitterState(), TransmitterBlocks.size(),
                plugin.radiationAll().all().values().stream()
                        .mapToInt(RadiationWorld::sourceCount).sum(),
                machineSummary(plugin), GuiRegistry.status())
                : I18n.tr("registration.missing", missing);
    }

    /** 元件行为的注册状态：管道方块全靠它挂方块实体。 */
    private static String transmitterState() {
        return BuiltInRegistries.BLOCK_BEHAVIOR_TYPE.getValue(TransmitterBlockBehavior.KEY) == null
                ? I18n.tr("gui.registry.missing_value") : I18n.tr("gui.registry.registered");
    }

    /** 机器自检（文档 04 步 9）：蓝图有没有登记、配方机器有没有配方、槽位与能量端口齐不齐。 */
    private static String machineSummary(CeMekanismPlugin plugin) {
        List<String> problems = new ArrayList<>();
        int machines = 0;
        int generators = 0;
        for (String blueprintId : new LinkedHashSet<>(GeneratedContent.BLOCK_TO_BLUEPRINT.values())) {
            mekcore.machine.MachineBlueprint blueprint = plugin.core().registrars().machines()
                    .find(blueprintId).orElse(null);
            if (blueprint == null) {
                problems.add(I18n.tr("registration.machine.unregistered", blueprintId));
                continue;
            }
            machines++;
            // 没有物品槽是合法的（充电台通过玩家背包充能，太阳能/风力也只有升级槽）。
            if (blueprint.energyPerTickJ() > 0 && !blueprint.hasEnergyPort()) {
                problems.add(I18n.tr("registration.machine.energy_without_port", blueprintId));
            }
            if ("source".equals(GeneratedContent.MACHINE_ROLES.get(blueprintId))) {
                generators++;
                if (blueprint.logic() == null) {
                    problems.add(I18n.tr("registration.machine.generator_without_logic", blueprintId));
                }
                if (!blueprint.hasEnergyPort()) {
                    problems.add(I18n.tr("registration.machine.generator_without_buffer", blueprintId));
                }
            }
            if (blueprint.logic() == null
                    && plugin.core().registrars().recipes().byUsage(blueprintId).isEmpty()) {
                problems.add(I18n.tr("registration.machine.recipe_without_recipe", blueprintId));
            }
        }
        return I18n.tr(problems.isEmpty() ? "registration.machine.ready" : "registration.machine.problems",
                machines, generators, problems);
    }

    /** 行为注册得晚一步的话，资源包里的机器方块会解析不出 type，所以这一条要报出来。 */
    private static String behaviorState() {
        return BuiltInRegistries.BLOCK_BEHAVIOR_TYPE.getValue(MachineBlockBehavior.KEY) == null
                ? I18n.tr("gui.registry.missing_value") : I18n.tr("gui.registry.registered");
    }

    /** 逐条状态，命令里展开看；名字与数量都来自清单，加内容时只改上面两个列表 */
    static List<String> lines() {
        List<String> lines = new ArrayList<>();
        for (Key id : ITEMS) {
            lines.add(I18n.tr("registration.line.item", id, built(id)));
        }
        for (Key id : BLOCKS) {
            lines.add(I18n.tr("registration.line.block", id,
                    I18n.tr(CraftEngineBlocks.byId(id) == null
                            ? "gui.registry.missing_value" : "gui.registry.registered")));
        }
        lines.addAll(GuiRegistry.lines());
        return lines;
    }

    private static List<String> missingIds() {
        List<String> missing = new ArrayList<>();
        for (Key id : ITEMS) {
            if (CraftEngineItems.byId(id) == null) {
                missing.add(I18n.tr("registration.line.item_name", id));
            }
        }
        for (String id : ConfiguratorFaceRenderer.ITEMS) {
            if (CraftEngineItems.byId(key(id)) == null) {
                missing.add(I18n.tr("registration.render.configurator", id));
            }
        }
        for (String id : MultiblockEffectRenderer.ITEMS) {
            if (CraftEngineItems.byId(key(id)) == null) {
                missing.add(I18n.tr("registration.render.multiblock", id));
            }
        }
        for (String id : GeneratedContent.GENERATOR_RENDER_ITEMS) {
            if (CraftEngineItems.byId(key(id)) == null) {
                missing.add(I18n.tr("registration.render.generator", id));
            }
        }
        for (String id : GeneratedContent.MACHINE_RENDER_ITEMS) {
            if (CraftEngineItems.byId(key(id)) == null) {
                missing.add(I18n.tr("registration.render.machine", id));
            }
        }
        for (Key id : BLOCKS) {
            if (CraftEngineBlocks.byId(id) == null) {
                missing.add(I18n.tr("registration.line.block_name", id));
            }
        }
        missing.addAll(GuiRegistry.missing());
        return missing;
    }

    /** 真构建一次物品堆：注册了但构建炸掉的情况也能看出来 */
    private static String built(Key id) {
        if (CraftEngineItems.byId(id) == null) {
            return I18n.tr("gui.registry.missing_value");
        }
        ItemStack stack = CraftEngineItems.byId(id).buildBukkitItem();
        return I18n.tr("registration.built", stack.getType(), stack.getAmount(),
                stack.hasItemMeta() ? I18n.tr("registration.components") : "");
    }
}
