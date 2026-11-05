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
import net.kuzuanpa.cemekanism.gui.GuiRegistry;
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
 * 载入资源包时打一行日志，/mek selfcheck 逐条列。
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
                ? "资源包 mek 自检：物品 " + ITEMS.size() + " 项 + 方块 " + BLOCKS.size()
                + " 项就绪，方块→蓝图映射 " + mapped + " 条，"
                + MachineBlockBehavior.KEY + " = " + behaviorState() + "，"
                + TransmitterBlockBehavior.KEY + " = " + transmitterState()
                + "，传输元件 " + TransmitterBlocks.size() + " 个（五族 × 四级），"
                + "辐射源 " + plugin.radiationAll().all().values().stream()
                        .mapToInt(RadiationWorld::sourceCount).sum() + "，"
                + "机器蓝图 " + machineSummary(plugin) + "，" + GuiRegistry.status()
                : "资源包 mek 自检：缺失 " + missing;
    }

    /** 元件行为的注册状态：管道方块全靠它挂方块实体。 */
    private static String transmitterState() {
        return BuiltInRegistries.BLOCK_BEHAVIOR_TYPE.getValue(TransmitterBlockBehavior.KEY) == null
                ? "缺失" : "已注册";
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
                problems.add(blueprintId + " 没登记");
                continue;
            }
            machines++;
            // 没有物品槽是合法的（充电台通过玩家背包充能，太阳能/风力也只有升级槽）。
            if (blueprint.energyPerTickJ() > 0 && !blueprint.hasEnergyPort()) {
                problems.add(blueprintId + " 有耗能却没有能量端口");
            }
            if ("source".equals(GeneratedContent.MACHINE_ROLES.get(blueprintId))) {
                generators++;
                if (blueprint.logic() == null) {
                    problems.add(blueprintId + " 是发电机但没有逻辑");
                }
                if (!blueprint.hasEnergyPort()) {
                    problems.add(blueprintId + " 是发电机但没有能量缓存");
                }
            }
            if (blueprint.logic() == null
                    && plugin.core().registrars().recipes().byUsage(blueprintId).isEmpty()) {
                problems.add(blueprintId + " 是配方机器但没有配方");
            }
        }
        return machines + " 台（发电机 " + generators + "）"
                + (problems.isEmpty() ? "，槽位/端口/配方齐全" : "，问题：" + problems);
    }

    /** 行为注册得晚一步的话，资源包里的机器方块会解析不出 type，所以这一条要报出来。 */
    private static String behaviorState() {
        return BuiltInRegistries.BLOCK_BEHAVIOR_TYPE.getValue(MachineBlockBehavior.KEY) == null
                ? "缺失" : "已注册";
    }

    /** 逐条状态，命令里展开看；名字与数量都来自清单，加内容时只改上面两个列表 */
    static List<String> lines() {
        List<String> lines = new ArrayList<>();
        for (Key id : ITEMS) {
            lines.add("物品 " + id + " = " + built(id));
        }
        for (Key id : BLOCKS) {
            lines.add("方块 " + id + " = " + (CraftEngineBlocks.byId(id) == null ? "缺失" : "已注册"));
        }
        lines.addAll(GuiRegistry.lines());
        return lines;
    }

    private static List<String> missingIds() {
        List<String> missing = new ArrayList<>();
        for (Key id : ITEMS) {
            if (CraftEngineItems.byId(id) == null) {
                missing.add("物品 " + id);
            }
        }
        for (String id : ConfiguratorFaceRenderer.ITEMS) {
            if (CraftEngineItems.byId(key(id)) == null) missing.add("配置器网格渲染物品 " + id);
        }
        for (String id : MultiblockEffectRenderer.ITEMS) {
            if (CraftEngineItems.byId(key(id)) == null) missing.add("多方块特效渲染物品 " + id);
        }
        for (String id : GeneratedContent.GENERATOR_RENDER_ITEMS) {
            if (CraftEngineItems.byId(key(id)) == null) {
                missing.add("发电机渲染物品 " + id);
            }
        }
        for (String id : GeneratedContent.MACHINE_RENDER_ITEMS) {
            if (CraftEngineItems.byId(key(id)) == null) {
                missing.add("机器渲染物品 " + id);
            }
        }
        for (Key id : BLOCKS) {
            if (CraftEngineBlocks.byId(id) == null) {
                missing.add("方块 " + id);
            }
        }
        return missing;
    }

    /** 真构建一次物品堆：注册了但构建炸掉的情况也能看出来 */
    private static String built(Key id) {
        if (CraftEngineItems.byId(id) == null) {
            return "缺失";
        }
        ItemStack stack = CraftEngineItems.byId(id).buildBukkitItem();
        return "已注册，构建 = " + stack.getType() + " x" + stack.getAmount()
                + (stack.hasItemMeta() ? " +组件" : "");
    }
}
