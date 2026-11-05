package net.kuzuanpa.cemekanism;

import mekcore.item.ItemRef;
import mekcore.item.ItemSlotKind;
import mekcore.item.ItemMatcher;
import mekcore.machine.Direction;
import mekcore.machine.Machine;
import mekcore.machine.SecurityLevel;
import mekcore.machine.SideMode;
import mekcore.resource.ResourceStack;
import mekcore.resource.Transaction;
import mekcore.sim.TickScheduler;
import net.kuzuanpa.cemekanism.machine.GeneratorEnvironment;
import net.kuzuanpa.cemekanism.machine.HeaterCommand;
import net.kuzuanpa.cemekanism.machine.MachineBlockEntityController;
import net.kuzuanpa.cemekanism.gui.GuiLayout;
import net.kuzuanpa.cemekanism.gui.GuiSprites;
import net.kuzuanpa.cemekanism.gui.GuiText;
import net.kuzuanpa.cemekanism.gui.GuiConfig;
import net.kuzuanpa.cemekanism.gui.MachineGui;
import net.kuzuanpa.cemekanism.gui.MultiblockGui;
import net.kuzuanpa.cemekanism.i18n.I18n;
import net.kuzuanpa.cemekanism.machine.RecipeBrowserGui;
import net.kuzuanpa.cemekanism.multiblock.MultiblockConfig;
import net.kuzuanpa.cemekanism.nuclear.B6Acceptance;
import net.kuzuanpa.cemekanism.radiation.RadiationConfig;
import net.kuzuanpa.cemekanism.radiation.RadiationStore;
import net.kuzuanpa.cemekanism.radiation.RadiationWorld;
import net.kuzuanpa.cemekanism.storage.B5Acceptance;
import net.kuzuanpa.cemekanism.storage.StorageCommand;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kuzuanpa.cemekanism.multiblock.MultiblockBlockEntityController;
import net.kuzuanpa.cemekanism.multiblock.MultiblockBlocks;
import net.kuzuanpa.cemekanism.multiblock.MultiblockDef;
import net.kuzuanpa.cemekanism.multiblock.MultiblockHosts;
import net.kuzuanpa.cemekanism.multiblock.MultiblockPlacements;
import net.kuzuanpa.cemekanism.multiblock.logic.FissionReactorLogic;
import net.kuzuanpa.cemekanism.multiblock.logic.FusionReactorLogic;
import net.kuzuanpa.cemekanism.multiblock.logic.HohlraumItems;
import net.kuzuanpa.cemekanism.multiblock.logic.SpsLogic;
import net.kuzuanpa.cemekanism.platform.BukkitItems;
import net.kuzuanpa.cemekanism.platform.GeneratorConfig;
import net.kuzuanpa.cemekanism.platform.MekSystems;
import net.kuzuanpa.cemekanism.transmitter.LogisticsConfig;
import net.kuzuanpa.cemekanism.transmitter.TransmitterBlockEntityController;
import net.kuzuanpa.cemekanism.transmitter.TransmitterShapes;
import net.momirealms.craftengine.libraries.nbt.CompoundTag;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * /mek：注册自检 + 机器调试。调试那几个子命令就是文档 03 第七节 SOP 第 8 步说的"接自检命令"，
 * 用来在没有管道、没有界面皮肤的当下把机器跑起来（06/08 落地后它们仍可用于排错）。
 */
final class MekCommand implements CommandExecutor {

    private final CeMekanismPlugin plugin;

    MekCommand(CeMekanismPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        String sub = command.getName().equalsIgnoreCase("mekrecipes") ? "recipes"
                : args.length == 0 ? "selfcheck" : args[0].toLowerCase(Locale.ROOT);
        switch (sub) {
            case "recipes", "recipe" -> {
                if (!(sender instanceof Player player)) {
                    I18n.send(sender, "recipe.player_only");
                } else if (!player.hasPermission("mek.recipes")) {
                    I18n.send(sender, "command.missing_permission", "mek.recipes");
                } else {
                    RecipeBrowserGui.openCatalog(this.plugin, player);
                }
            }
            case "selfcheck" -> selfcheck(sender);
            case "b5check" -> B5Acceptance.run(this.plugin, sender, args);
            case "b5" -> StorageCommand.run(this.plugin, sender, args);
            case "heater" -> HeaterCommand.run(this.plugin, sender, args);
            case "b6check" -> B6Acceptance.run(this.plugin, sender, args);
            case "reload" -> reload(sender);
            case "machine" -> machine(sender, args);
            case "security" -> security(sender, args);
            case "pipe" -> pipe(sender, args);
            case "net" -> net(sender, args);
            case "debug" -> debug(sender, args);
            case "structure" -> structure(sender, args);
            case "build" -> build(sender, args);
            case "demo" -> demo(sender, args);
            case "showcase" -> ShowcaseCommand.run(this.plugin, sender, args);
            case "radiation" -> radiation(sender, args);
            case "reactor" -> reactor(sender, args);
            default -> I18n.send(sender, "command.mek.usage", label);
        }
        return true;
    }

    /** /mek radiation：status/get/add/heal/removeAll/sources（文档 07 第七节）。 */
    private void radiation(CommandSender sender, String[] args) {
        String action = args.length > 1 ? args[1].toLowerCase(Locale.ROOT) : "status";
        switch (action) {
            case "status", "get" -> {
                Player target = target(sender, args, 2);
                if (target == null) {
                    return;
                }
                mekcore.chemical.DoseTracker dose = this.plugin.radiation(target.getWorld()).dose(target);
                double exposure = this.plugin.radiation(target.getWorld())
                        .exposureAt(target.getLocation());
                I18n.send(sender, "radiation.status",
                        target.getName(), String.format("%.2f", dose.microSv()),
                        I18n.enumName(dose.effect(), dose.effect().zhName()),
                        RadiationWorld.format(exposure));
            }
            case "add" -> {
                if (args.length < 3) {
                    I18n.send(sender, "radiation.add_usage");
                    return;
                }
                Player target = target(sender, args, 3);
                if (target == null) {
                    return;
                }
                double amount = Double.parseDouble(args[2]);
                this.plugin.radiation(target.getWorld()).dose(target).add(amount);
                I18n.send(sender, "radiation.added", target.getName(), amount);
            }
            case "heal" -> {
                Player target = target(sender, args, 2);
                if (target == null) {
                    return;
                }
                this.plugin.radiation(target.getWorld()).dose(target).clear();
                RadiationStore.save(target.getUniqueId(), 0.0);
                target.removePotionEffect(org.bukkit.potion.PotionEffectType.DARKNESS);
                target.removePotionEffect(org.bukkit.potion.PotionEffectType.NAUSEA);
                target.removePotionEffect(org.bukkit.potion.PotionEffectType.HUNGER);
                I18n.send(sender, "radiation.cleared", target.getName());
            }
            case "removeall" -> {
                int cleared = this.plugin.radiationAll().removeAll();
                I18n.send(sender, "radiation.cleared_all", cleared);
            }
            case "sources" -> {
                World world = world(sender);
                if (world == null) {
                    return;
                }
                org.bukkit.Location location;
                int radius;
                if (sender instanceof Player player && args.length <= 4) {
                    location = player.getLocation();
                    radius = args.length > 2 ? Integer.parseInt(args[2])
                            : RadiationConfig.maxDistance();
                } else if (args.length >= 5) {
                    location = new org.bukkit.Location(world, Double.parseDouble(args[2]),
                            Double.parseDouble(args[3]), Double.parseDouble(args[4]));
                    radius = args.length > 5 ? Integer.parseInt(args[5])
                            : RadiationConfig.maxDistance();
                } else {
                    I18n.send(sender, "radiation.sources_usage");
                    return;
                }
                I18n.send(sender, "radiation.environment", RadiationWorld.format(
                        this.plugin.radiation(world).exposureAt(location)));
                List<String> lines = this.plugin.radiation(world)
                        .sourceLines(location, radius);
                if (lines.isEmpty()) {
                    I18n.send(sender, "radiation.no_sources", radius);
                    return;
                }
                lines.forEach(sender::sendMessage);
            }
            default -> I18n.send(sender, "radiation.usage");
        }
    }

    private Player target(CommandSender sender, String[] args, int index) {
        if (args.length > index) {
            Player named = Bukkit.getPlayerExact(args[index]);
            if (named == null) {
                I18n.send(sender, "command.player_missing", args[index]);
            }
            return named;
        }
        if (sender instanceof Player player) {
            return player;
        }
        I18n.send(sender, "command.console_player_required");
        return null;
    }

    /**
     * /mek pipe &lt;x y z&gt;：一根元件的六个面、它这一侧的端点、所在网络（等价于网络读取器右击）。
     * headless 下没有玩家，这一条就是文档 09 说的"管道接上了没"的验法。
     */
    private void pipe(CommandSender sender, String[] args) {
        if (args.length < 4) {
            I18n.send(sender, "command.pipe.usage");
            return;
        }
        World world = world(sender);
        if (world == null) {
            return;
        }
        int x = Integer.parseInt(args[1]);
        int y = Integer.parseInt(args[2]);
        int z = Integer.parseInt(args[3]);
        TransmitterBlockEntityController controller = this.plugin.transmitterAt(world, x, y, z);
        if (controller == null) {
            I18n.send(sender, "command.transmitter_missing");
            return;
        }
        for (String line : this.plugin.logistics(world).describe(controller).split("\n")) {
            sender.sendMessage(line);
        }
    }

    /** /mek net [世界]：这个世界的四条网络 + 电网一行摘要（睡眠/搬运/红石响应都在里面）。 */
    private void net(CommandSender sender, String[] args) {
        String name = args.length > 1 ? args[1] : null;
        if (name == null) {
            World world = world(sender);
            if (world == null) {
                return;
            }
            name = world.getName();
        }
        World world = Bukkit.getWorld(name);
        if (world == null) {
            I18n.send(sender, "command.world_missing_named", name);
            return;
        }
        for (String line : this.plugin.logistics(world).summary().split("\n")) {
            sender.sendMessage(line);
        }
    }

    /**
     * /mek debug cable &lt;x y z&gt; info|charge &lt;J&gt;|fill &lt;资源 id&gt; &lt;数量&gt;|item &lt;物品 id&gt; &lt;数量&gt;|port &lt;面&gt;|channel &lt;色&gt;：
     * headless 下没有发电机与别的机器时，往线缆缓存里灌一笔料，看它能不能自己流到端点上。
     */
    private void cableDebug(CommandSender sender, String[] args) {
        if (args.length < 6) {
            I18n.send(sender, "command.cable.usage");
            return;
        }
        World world = world(sender);
        if (world == null) {
            return;
        }
        int x = Integer.parseInt(args[2]);
        int y = Integer.parseInt(args[3]);
        int z = Integer.parseInt(args[4]);
        TransmitterBlockEntityController controller = this.plugin.transmitterAt(world, x, y, z);
        if (controller == null) {
            I18n.send(sender, "command.transmitter_missing");
            return;
        }
        switch (args[5].toLowerCase(Locale.ROOT)) {
            case "info" -> {
                for (String line : this.plugin.logistics(world).describe(controller).split("\n")) {
                    sender.sendMessage(line);
                }
            }
            case "port" -> {
                if (args.length < 7) {
                    I18n.send(sender, "command.cable.port_usage");
                    return;
                }
                mekcore.machine.Direction direction = null;
                for (mekcore.machine.Direction candidate : mekcore.machine.Direction.values()) {
                    if (candidate.name().equalsIgnoreCase(args[6])) {
                        direction = candidate;
                    }
                }
                if (direction == null) {
                    I18n.send(sender, "command.side_usage");
                    return;
                }
                var mode = controller.cyclePort(direction);
                I18n.send(sender, "command.cable.port_changed", direction.name(),
                        I18n.enumName(mode, mode.zhName()));
            }
            case "channel" -> {
                if (args.length < 7) {
                    I18n.send(sender, "command.cable.channel_usage");
                    return;
                }
                int channel = Integer.parseInt(args[6]);
                if (controller.itemPipe()) {
                    controller.itemNode().setChannel(channel);
                } else {
                    controller.node().setChannel(channel);
                }
                controller.syncSelf();
                I18n.send(sender, "command.cable.channel_changed", controller.nodeSummary());
            }
            case "charge" -> {
                if (controller.node() == null) {
                    I18n.send(sender, "command.cable.not_resource");
                    return;
                }
                if (!controller.node().tier().family().resource().equals(mekcore.resource.ResourceTypes.ENERGY)) {
                    I18n.send(sender, "command.cable.not_energy");
                    return;
                }
                long joules = Long.parseLong(args[6]);
                long done = controller.node().buffer().insert(
                        mekcore.resource.ResourceStack.energy(joules),
                        mekcore.resource.Transaction.EXECUTE);
                this.plugin.logistics(world).wake();
                I18n.send(sender, "command.cable.charged", done, joules, controller.nodeSummary());
            }
            case "fill" -> {
                if (controller.node() == null) {
                    I18n.send(sender, "command.cable.not_resource");
                    return;
                }
                if (args.length < 8) {
                    I18n.send(sender, "command.cable.fill_usage");
                    return;
                }
                long amount = Long.parseLong(args[7]);
                mekcore.resource.ResourceStack stack = controller.node().tier().family().resource()
                        .equals(mekcore.resource.ResourceTypes.FLUID)
                        ? mekcore.resource.ResourceStack.fluid(args[6], amount)
                        : mekcore.resource.ResourceStack.chemical(args[6], amount);
                long done = controller.node().buffer().insert(stack, mekcore.resource.Transaction.EXECUTE);
                this.plugin.logistics(world).wake();
                I18n.send(sender, "command.cable.filled", done, amount, args[6], controller.nodeSummary());
            }
            case "item" -> {
                if (args.length < 8) {
                    I18n.send(sender, "command.cable.item_usage");
                    return;
                }
                if (controller.itemNode() == null) {
                    I18n.send(sender, "command.cable.not_logistics");
                    return;
                }
                long done = controller.itemNode().insert(
                        mekcore.item.ItemRef.of(args[6], Long.parseLong(args[7])));
                this.plugin.logistics(world).wake();
                I18n.send(sender, "command.cable.items_filled", done, args[6], controller.nodeSummary());
            }
            case "redstone" -> {
                boolean enabled = controller.toggleRedstone();
                I18n.send(sender, "command.cable.redstone",
                        I18n.tr(sender, enabled ? "state.redstone_enabled" : "state.off"));
            }
            case "visual" -> {
                sender.sendMessage(controller.visual().describe());
                I18n.send(sender, "command.cable.visual",
                        LogisticsConfig.transmitterVisualInterval(),
                        I18n.tr(sender, LogisticsConfig.transmitterContentsEnabled() ? "state.on" : "state.off"));
            }
            default -> I18n.send(sender, "command.cable.unknown", args[6]);
        }
    }

    /** /mek debug heat &lt;x y z&gt; info|set &lt;K&gt;：给热导线缆定一个温度（没有热源时的替身）。 */
    private void heatDebug(CommandSender sender, String[] args) {
        if (args.length < 6) {
            I18n.send(sender, "command.heat.usage");
            return;
        }
        World world = world(sender);
        if (world == null) {
            return;
        }
        int x = Integer.parseInt(args[2]);
        int y = Integer.parseInt(args[3]);
        int z = Integer.parseInt(args[4]);
        TransmitterBlockEntityController controller = this.plugin.transmitterAt(world, x, y, z);
        if (controller == null || controller.heatNode() == null) {
            I18n.send(sender, "command.heat.not_conductor");
            return;
        }
        if ("set".equalsIgnoreCase(args[5]) && args.length > 6) {
            controller.heatNode().setTemperatureK(Double.parseDouble(args[6]));
            this.plugin.logistics(world).wake();
        }
        sender.sendMessage(controller.nodeSummary());
        sender.sendMessage(this.plugin.logistics(world).heatTopology().describe());
    }

    /** /mek debug gen &lt;x y z&gt;：发电机状态 + 它看到的天空/风力/熔岩面（文档 11 的 headless 入口）。 */
    private void generatorDebug(CommandSender sender, String[] args) {
        MachineBlockEntityController controller = controller(sender, args, 2);
        if (controller == null) {
            return;
        }
        Machine machine = controller.machine();
        World world = controller.world();
        sender.sendMessage(machine.describe());
        if (machine.heatNode() != null) {
            I18n.send(sender, "debug.generator.heat",
                    String.format("%.2f", machine.heatNode().temperatureK()),
                    String.format("%.3f", machine.heatNode().heatCapacity()),
                    String.format("%.0f", machine.heatNode().config().joulesPerKelvin()));
        }
        I18n.send(sender, "debug.generator.totals", machine.energyConsumedTotal(), machine.operations());
        for (String port : machine.portNames()) {
            machine.tank(port).ifPresent(tank ->
                    I18n.send(sender, "debug.generator.port", port, tank.describe()));
        }
        if (world != null) {
            int x = controller.x();
            int y = controller.y();
            int z = controller.z();
            int sunPoints = 0;
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (GeneratorEnvironment.canSeeSunAt(world, x + dx, y + 2, z + dz)) {
                        sunPoints++;
                    }
                }
            }
            I18n.send(sender, "debug.generator.environment",
                    GeneratorEnvironment.canSeeSunAbove(world, x, y, z),
                    sunPoints, GeneratorEnvironment.windTopOpen(world, x, y, z),
                    GeneratorEnvironment.noWindDimension(world),
                    GeneratorEnvironment.lavaSides(world, x, y, z));
        }
    }

    /** /mek structure <x y z>：成型 + 逐位置报"期望什么、实际什么" + 端口位置（文档 05 第四节）。 */
    private void structure(CommandSender sender, String[] args) {
        if (args.length < 4) {
            I18n.send(sender, "command.structure.usage");
            return;
        }
        World world = world(sender);
        if (world == null) {
            return;
        }
        int x = Integer.parseInt(args[1]);
        int y = Integer.parseInt(args[2]);
        int z = Integer.parseInt(args[3]);
        MultiblockBlockEntityController anchor = this.plugin.multiblockAt(world, x, y, z);
        MultiblockHosts.Host host = anchor != null ? anchor.form() : this.plugin.multiblocks().at(world, x, y, z);
        if (host == null) {
            I18n.send(sender, "command.structure.missing");
            return;
        }
        sender.sendMessage(host.describe());
        sender.sendMessage("  " + host.validation().describe());
        for (String note : host.extraIssues()) {
            sender.sendMessage("  " + note);
        }
        for (String port : host.def().ports()) {
            I18n.send(sender, "command.structure.port", port,
                    host.instance().tank(port).describe(), host.def().capacityRule(port),
                    host.instance().portPositions(port));
        }
    }

    /** /mek build <x y z> <结构 id>：一键搭建（锚点在最小角，清单来自核心的 buildPlan）。 */
    private void build(CommandSender sender, String[] args) {
        if (args.length < 5) {
            I18n.send(sender, "command.build.usage",
                    MultiblockBlocks.all().stream().map(MultiblockDef::id).toList());
            return;
        }
        World world = world(sender);
        if (world == null) {
            return;
        }
        int x = Integer.parseInt(args[1]);
        int y = Integer.parseInt(args[2]);
        int z = Integer.parseInt(args[3]);
        MultiblockDef def = MultiblockBlocks.require(args[4]);
        // 固定模板的默认尺寸必须取模板本身；
        // 可缩放结构仍从最小尺寸起。
        mekcore.multiblock.StructureLayout fixed = this.plugin.core().registrars().multiblocks()
                .require(def.id()).spec().fixedLayout().orElse(null);
        int sizeX = args.length > 5 ? Integer.parseInt(args[5]) : fixed != null ? fixed.width() : def.wMin();
        int sizeY = args.length > 6 ? Integer.parseInt(args[6]) : fixed != null ? fixed.layerCount() : def.hMin();
        int sizeZ = args.length > 7 ? Integer.parseInt(args[7]) : fixed != null ? fixed.depth() : sizeX;
        if (def.family().equals("teleporter")) {
            sizeX = args.length > 5 ? sizeX : 3;
            sizeY = args.length > 6 ? sizeY : 4;
            sizeZ = args.length > 7 ? sizeZ : 1;
        }
        if (!def.accepts(sizeX, sizeY, sizeZ)) {
            I18n.send(sender, "command.build.bad_size", I18n.structureName(I18n.locale(sender), def.id()),
                    def.wMin(), def.wMax(), def.hMin(), def.hMax());
            return;
        }
        MultiblockPlacements.Result result = MultiblockPlacements.build(
                this.plugin, world, x, y, z, def, sizeX, sizeY, sizeZ);
        I18n.send(sender, "command.build.placed", result.placed(), result.kept(), result.cleared());
        int[] offset = def.buildAnchor(sizeX, sizeY, sizeZ);
        MultiblockBlockEntityController anchor = this.plugin.multiblockAt(world,
                x + offset[0], y + offset[1], z + offset[2]);
        MultiblockHosts.Host host = anchor == null ? null : anchor.form();
        if (host == null) {
            I18n.send(sender, "command.build.entity_missing");
            return;
        }
        sender.sendMessage(host.describe());
        sender.sendMessage("  " + host.validation().describe());
        for (String note : host.extraIssues()) {
            sender.sendMessage("  " + note);
        }
    }

    /**
     * /mek demo [x y z]：摆一条能从头看到尾的最小产线——
     * 漏斗 → 机器 A → 物品管道 → 机器 B → 漏斗 → 箱子；
     * 旁边两座动态储罐用机械管道连通（灌水后能看到 2500/2500 的均衡）。
     * 它只是排错/观测试例，摆放清单与起始料在 mek-systems.yml 的 demo 段。
     */
    private void demo(CommandSender sender, String[] args) {
        World world = world(sender);
        int x;
        int y;
        int z;
        if (args.length >= 4) {
            x = Integer.parseInt(args[1]);
            y = Integer.parseInt(args[2]);
            z = Integer.parseInt(args[3]);
        } else if (sender instanceof Player player) {
            x = player.getLocation().getBlockX();
            y = player.getLocation().getBlockY();
            z = player.getLocation().getBlockZ();
        } else {
            x = world.getSpawnLocation().getBlockX();
            y = world.getSpawnLocation().getBlockY();
            z = world.getSpawnLocation().getBlockZ();
        }
        MekSystems.DemoSettings cfg = MekSystems.demo();
        MultiblockDef def = MultiblockBlocks.require(cfg.multiblock());
        if (def.wMin() > 3 || def.hMin() > 3) {
            I18n.send(sender, "command.demo.tank_too_small", def.id());
            return;
        }
        if (!def.ports().contains("fluid")) {
            I18n.send(sender, "command.demo.missing_fluid_port", def.id(), def.ports());
            return;
        }

        // 机器与两条原版物流：漏斗喂 A，A 的产物走物品管道进 B，B 的产物被下面的漏斗抽进箱子。
        MultiblockPlacements.place(world, x, y, z, cfg.machineA());
        MultiblockPlacements.place(world, x + 2, y, z, cfg.machineB());
        vanillarize(world, x, y + 1, z, Material.HOPPER, BlockFace.DOWN);
        vanillarize(world, x + 2, y - 1, z, Material.HOPPER, BlockFace.DOWN);
        vanillarize(world, x + 2, y - 2, z, Material.CHEST, null);
        MultiblockPlacements.place(world, x + 1, y, z, cfg.itemPipe());
        MultiblockPlacements.place(world, x, y, z - 1, cfg.cable());
        MultiblockPlacements.place(world, x + 2, y + 1, z, cfg.cable());

        MachineBlockEntityController a = this.plugin.machineAt(world, x, y, z);
        MachineBlockEntityController b = this.plugin.machineAt(world, x + 2, y, z);
        if (a == null || b == null) {
            I18n.send(sender, "command.demo.entity_missing", x >> 4, z >> 4);
            return;
        }
        a.machine().setItemSide(Direction.UP, SideMode.INPUT);
        a.machine().setItemSide(Direction.EAST, SideMode.OUTPUT);
        a.machine().setSide("energy", Direction.NORTH, SideMode.INPUT);
        b.machine().setItemSide(Direction.WEST, SideMode.INPUT);
        b.machine().setItemSide(Direction.DOWN, SideMode.OUTPUT);
        b.machine().setSide("energy", Direction.UP, SideMode.INPUT);
        a.markChanged();
        b.markChanged();
        this.plugin.logistics(world).refreshAround(x, y, z);
        this.plugin.logistics(world).refreshAround(x + 2, y, z);

        // 两座储罐：锚点都在最小角，fluid 端口在底面中心 (1,0,1)；
        // 机械管道走储罐下方，直接从两个端口往下引，再沿 z 串起来。
        int tankZ = z + 5;
        MultiblockPlacements.Result tankA = MultiblockPlacements.build(this.plugin, world,
                x + 4, y, z, def, 3, 3, 3);
        MultiblockPlacements.Result tankB = MultiblockPlacements.build(this.plugin, world,
                x + 4, y, tankZ, def, 3, 3, 3);
        MultiblockHosts.Host hostA = formAt(world, x + 4 + def.anchorOffsetX(),
                y + def.anchorOffsetY(), z + def.anchorOffsetZ());
        MultiblockHosts.Host hostB = formAt(world, x + 4 + def.anchorOffsetX(),
                y + def.anchorOffsetY(), tankZ + def.anchorOffsetZ());
        for (int pipeZ = z + 1; pipeZ <= tankZ + 1; pipeZ++) {
            MultiblockPlacements.place(world, x + 5, y - 1, pipeZ, cfg.fluidPipe());
            forceTransmitter(world, x + 5, y - 1, pipeZ);
        }
        if (hostA != null && hostA.instance().tank("fluid").amount() == 0) {
            hostA.instance().tank("fluid").insert(ResourceStack.fluid(cfg.tankFluid(), cfg.tankAmount()),
                    Transaction.EXECUTE);
        }

        // 机器与管道的端点：上面改过侧面配置，这里再催一遍，避免命令与 CORE 车道的先后差。
        forceTransmitter(world, x + 1, y, z);
        this.plugin.logistics(world).refreshAround(x, y, z);
        this.plugin.logistics(world).refreshAround(x + 2, y, z);
        long chargedA = chargeCable(world, x, y, z - 1, cfg.chargeJ());
        long chargedB = chargeCable(world, x + 2, y + 1, z, cfg.chargeJ());
        int starter = fillHopper(world, x, y + 1, z, cfg.starterItem(), cfg.starterAmount());
        this.plugin.logistics(world).wake();

        I18n.send(sender, "command.demo.created", x, y, z);
        I18n.send(sender, "command.demo.machine_a", cfg.machineA(), x, y, z);
        I18n.send(sender, "command.demo.machine_b", cfg.machineB(), x + 2, y, z);
        I18n.send(sender, "command.demo.item_pipe", x + 1, y, z);
        I18n.send(sender, "command.demo.tanks", x + 4, y, z, x + 4, y, tankZ,
                tankA.placed() + tankB.placed(), x + 5, y - 1);
        I18n.send(sender, "command.demo.power_and_feed", chargedA, chargedB, starter, cfg.starterItem());
        if (hostA == null || hostB == null) {
            I18n.send(sender, "command.demo.tanks_unformed");
        } else {
            I18n.send(sender, "command.demo.tank_ports",
                    hostA.instance().tank("fluid").describe(),
                    hostB.instance().tank("fluid").describe());
        }
        I18n.send(sender, "command.demo.inspect", x, y, z, x + 1, y, z, x + 4, y, z);
        I18n.send(sender, "command.demo.notice");
    }

    private MultiblockHosts.Host formAt(World world, int x, int y, int z) {
        MultiblockBlockEntityController anchor = this.plugin.multiblockAt(world, x, y, z);
        return anchor == null ? null : anchor.form();
    }

    private void forceTransmitter(World world, int x, int y, int z) {
        TransmitterBlockEntityController controller = this.plugin.transmitterAt(world, x, y, z);
        if (controller != null) {
            this.plugin.logistics(world).refreshAround(x, y, z);
        }
    }

    private static void vanillarize(World world, int x, int y, int z, Material material, BlockFace facing) {
        Block block = world.getBlockAt(x, y, z);
        block.setType(material, false);
        if (facing != null && block.getBlockData() instanceof org.bukkit.block.data.Directional data) {
            data.setFacing(facing);
            block.setBlockData(data, false);
        }
    }

    private static int fillHopper(World world, int x, int y, int z, String itemId, int amount) {
        Block block = world.getBlockAt(x, y, z);
        if (!(block.getState() instanceof Container container)) {
            return 0;
        }
        // 容器快照要拿 getSnapshotInventory()：getInventory() 在未放置的 state 上
        // 会另造一份，update() 写回的是快照那份，物品就丢了。
        Inventory inventory = container.getSnapshotInventory();
        for (ItemStack existing : inventory.getContents()) {
            if (existing != null && !existing.getType().isAir()) {
                return 0;                              // 已经有料就不再加，重复跑 demo 不会无限灌
            }
        }
        ItemStack stack = BukkitItems.create(itemId);
        if (stack == null) {
            return 0;
        }
        stack.setAmount(Math.max(1, amount));
        inventory.setItem(0, stack);
        container.update(true, false);
        return stack.getAmount();
    }

    private long chargeCable(World world, int x, int y, int z, long wanted) {
        TransmitterBlockEntityController controller = this.plugin.transmitterAt(world, x, y, z);
        if (controller == null || controller.node() == null) {
            return 0;
        }
        long amount = wanted < 0 ? controller.node().buffer().capacity() : wanted;
        return controller.node().buffer().insert(ResourceStack.energy(amount), Transaction.EXECUTE);
    }

    /**
     * /mek debug mb <x y z> info|blades <n>|put <端口> <数量> [资源 id]：
     * headless 下没有管道与热网，端口的内容与涡轮叶片数只能从这里灌进去验证逻辑。
     */
    private void multiblockDebug(CommandSender sender, String[] args) {
        if (args.length < 6) {
            I18n.send(sender, "command.multiblock_debug.usage");
            return;
        }
        World world = world(sender);
        if (world == null) {
            return;
        }
        int x = Integer.parseInt(args[2]);
        int y = Integer.parseInt(args[3]);
        int z = Integer.parseInt(args[4]);
        MultiblockHosts.Host host = this.plugin.multiblocks().at(world, x, y, z);
        if (host == null) {
            MultiblockBlockEntityController anchor = this.plugin.multiblockAt(world, x, y, z);
            host = anchor == null ? null : anchor.form();
        }
        if (host == null) {
            I18n.send(sender, "command.structure.missing");
            return;
        }
        switch (args[5].toLowerCase(Locale.ROOT)) {
            case "info" -> {
                sender.sendMessage(host.describe());
                for (String port : host.def().ports()) {
                    I18n.send(sender, "command.multiblock_debug.port", port,
                            host.instance().tank(port).describe(), host.instance().tank(port).capacity(),
                            host.def().capacityRule(port));
                }
                if (host.instance().heatNode() != null) {
                    I18n.send(sender, "command.multiblock_debug.heat",
                            String.format("%.2f", host.instance().heatNode().temperatureK()),
                            host.instance().heatNode().heatCapacity());
                }
                I18n.send(sender, "command.multiblock_debug.blades_and_shape",
                        host.blades(), host.instance().shape().describe());
            }
            case "dump" -> {
                if (args.length < 8) { I18n.send(sender, "command.multiblock_debug.dump_usage"); return; }
                var tank = host.instance().tank(args[6]);
                if (!tank.type().equals(mekcore.resource.ResourceTypes.CHEMICAL)) {
                    I18n.send(sender, "tank_dump.chemical_only");
                    return;
                }
                if (!"info".equalsIgnoreCase(args[7])) {
                    tank.setDumpMode(mekcore.resource.DumpMode.parse(args[7])); host.markChanged();
                }
                I18n.send(sender, "tank_dump.state", args[6],
                        I18n.enumName(tank.dumpMode(), tank.dumpMode().label()));
            }
            case "blades" -> {
                host.setBlades(Long.parseLong(args[6]));
                I18n.send(sender, "command.multiblock_debug.blade_count", host.blades());
            }
            case "put" -> {
                if (args.length < 8) {
                    I18n.send(sender, "command.multiblock_debug.put_usage");
                    return;
                }
                String port = args[6];
                long amount = Long.parseLong(args[7]);
                String id = args.length > 8 ? args[8] : null;
                mekcore.resource.ResourceTank tank = host.instance().tank(port);
                mekcore.resource.ResourceStack stack = switch (tank.type().id()) {
                    case "mek:energy" -> mekcore.resource.ResourceStack.energy(amount);
                    case "mek:chemical" -> mekcore.resource.ResourceStack.chemical(
                            id == null ? MultiblockConfig.STEAM : id, amount);
                    default -> mekcore.resource.ResourceStack.fluid(
                            id == null ? MultiblockConfig.WATER : id, amount);
                };
                long done = tank.insert(stack, mekcore.resource.Transaction.EXECUTE);
                I18n.send(sender, "command.multiblock_debug.inserted",
                        port, done, amount, stack.id(), tank.describe());
            }
            default -> I18n.send(sender, "command.multiblock_debug.usage");
        }
    }

    /** /mek reactor：裂变/聚变/SPS 的状态与无客户端调试入口。 */
    private void reactor(CommandSender sender, String[] args) {
        if (args.length < 5) {
            I18n.send(sender, "command.reactor.usage");
            return;
        }
        World world = world(sender);
        if (world == null) {
            return;
        }
        int x = Integer.parseInt(args[1]);
        int y = Integer.parseInt(args[2]);
        int z = Integer.parseInt(args[3]);
        MultiblockHosts.Host host = this.plugin.multiblocks().at(world, x, y, z);
        if (host == null) {
            MultiblockBlockEntityController anchor = this.plugin.multiblockAt(world, x, y, z);
            host = anchor == null ? null : anchor.form();
        }
        if (host == null) {
            I18n.send(sender, "command.structure.missing");
            return;
        }
        String action = args[4].toLowerCase(Locale.ROOT);
        if ("mode".equals(action)) {
            if (args.length < 8) {
                I18n.send(sender, "command.reactor.mode_usage");
                return;
            }
            String next = host.cyclePortMode(Integer.parseInt(args[5]), Integer.parseInt(args[6]),
                    Integer.parseInt(args[7]));
            I18n.send(sender, next.isEmpty() ? "command.reactor.no_port_mode"
                    : "command.reactor.port_mode", I18n.structureName(I18n.locale(sender), host.def().id()), next);
            host.markChanged();
            return;
        }
        if ("info".equals(action)) {
            sender.sendMessage(host.describe());
            for (String port : host.def().ports()) {
                I18n.send(sender, "debug.port_value", port, host.instance().tank(port).describe());
            }
            if (host.instance().heatNode() != null) {
                I18n.send(sender, "command.reactor.shell_heat",
                        String.format("%.2f", host.instance().heatNode().temperatureK()),
                        String.format("%.4f", host.instance().heatNode().heatCapacity()));
            }
            if (host.instance().blueprint().logic() instanceof FissionReactorLogic fission) {
                FissionReactorLogic.State state = fission.state(host.instance());
                I18n.send(sender, "command.reactor.fission_status", state.active, state.forceDisabled,
                        String.format("%.3f", state.rateLimit), String.format("%.3f", state.burnRemaining),
                        String.format("%.2f", state.damage), String.format("%.3f", state.partialWaste));
            } else if (host.instance().blueprint().logic() instanceof FusionReactorLogic fusion) {
                FusionReactorLogic.State state = fusion.state(host.instance());
                I18n.send(sender, "command.reactor.fusion_status", state.burning,
                        String.format("%.2f", state.plasmaTemperatureK / 1_000_000),
                        state.injectionRate,
                        state.hohlraum == null ? I18n.tr(sender, "state.none") : state.hohlraum.getAmount());
            } else if (host.instance().blueprint().logic() instanceof SpsLogic sps) {
                SpsLogic.State state = sps.state(host.instance());
                I18n.send(sender, "command.reactor.sps_status",
                        String.format("%.1f", state.progress), state.lastEnergy);
            }
            return;
        }
        if (host.instance().blueprint().logic() instanceof FissionReactorLogic fission) {
            switch (action) {
                case "on" -> fission.setActive(host.instance(), true);
                case "off" -> fission.setActive(host.instance(), false);
                case "rate" -> {
                    if (args.length < 6) {
                        I18n.send(sender, "command.reactor.rate_usage");
                        return;
                    }
                    fission.setRate(host.instance(), Double.parseDouble(args[5]));
                }
                default -> I18n.send(sender, "command.reactor.fission_actions");
            }
            host.markChanged();
            I18n.send(sender, "command.reactor.updated",
                    I18n.structureName(I18n.locale(sender), "mek:fission_reactor"));
            return;
        }
        if (host.instance().blueprint().logic() instanceof FusionReactorLogic fusion) {
            switch (action) {
                case "injection" -> {
                    if (args.length < 6) {
                        I18n.send(sender, "command.reactor.injection_usage");
                        return;
                    }
                    fusion.setInjectionRate(host.instance(), Integer.parseInt(args[5]));
                }
                case "plasma" -> {
                    if (args.length < 6) {
                        I18n.send(sender, "command.reactor.plasma_usage");
                        return;
                    }
                    fusion.setPlasmaTemperature(host.instance(), Double.parseDouble(args[5]));
                }
                case "hohlraum" -> {
                    ItemStack hohlraum = BukkitItems.create("mek:hohlraum");
                    if (hohlraum == null) {
                        I18n.send(sender, "command.reactor.hohlraum_missing");
                        return;
                    }
                    HohlraumItems.fill(hohlraum, FusionReactorLogic.FUSION_FUEL,
                            MultiblockConfig.hohlraumCapacity());
                    fusion.insertHohlraum(host.instance(), hohlraum);
                }
                default -> I18n.send(sender, "command.reactor.fusion_actions");
            }
            host.markChanged();
            I18n.send(sender, "command.reactor.updated",
                    I18n.structureName(I18n.locale(sender), "mek:fusion_reactor"));
            return;
        }
        I18n.send(sender, "command.reactor.no_nuclear_logic");
    }

    private void selfcheck(CommandSender sender) {
        sender.sendMessage(RegistrationCheck.describe(this.plugin));
        for (String line : RegistrationCheck.lines()) {
            sender.sendMessage("  " + line);
        }
        sender.sendMessage(this.plugin.core().summary());
        sender.sendMessage(this.plugin.scheduler().report());
    }

    private void reload(CommandSender sender) {
        this.plugin.reloadConfig();
        I18n.install(this.plugin);
        MekSystems.install(this.plugin);
        GeneratorConfig.install(this.plugin);
        RadiationConfig.install(this.plugin);
        TransmitterShapes.apply(this.plugin, MekSystems.transmitterCollision6px());
        I18n.send(sender, "command.reload.done", mekcore.spec.V10Tables.summary());
    }

    private void machine(CommandSender sender, String[] args) {
        MachineBlockEntityController controller = controller(sender, args, 1);
        if (controller == null) {
            return;
        }
        if (denied(sender, controller.machine())) {
            return;
        }
        I18n.send(sender, controller.registered() ? "machine.registered" : "machine.unregistered",
                controller.taskId());
        I18n.send(sender, "machine.state_line", state(sender, controller));
        TickScheduler.Entry entry = this.plugin.scheduler().task(controller.taskId());
        if (entry == null) {
            I18n.send(sender, "machine.scheduler_missing");
        } else {
            I18n.send(sender, "machine.scheduler_state", entry.runs(), entry.skips(),
                    I18n.tr(sender, entry.sleeping() ? "state.sleeping" : "state.awake"));
        }
    }

    /**
     * /mek security &lt;x y z&gt; [public|trusted|private|claim|trust add|remove &lt;uuid&gt;]：
     * 文档 04 第六节的安全等级与信任名单。实机左键配置器切等级；这里是 headless 等价入口。
     */
    private void security(CommandSender sender, String[] args) {
        MachineBlockEntityController controller = controller(sender, args, 1);
        if (controller == null) {
            return;
        }
        Machine machine = controller.machine();
        if (args.length < 5) {
            I18n.send(sender, "machine.security_summary",
                    I18n.machineName(I18n.locale(sender), machine.blueprint()),
                    I18n.enumName(machine.security(), machine.security().zhName()),
                    ownerText(sender, machine), machine.trusted());
            return;
        }
        String action = args[4].toLowerCase(Locale.ROOT);
        if ("claim".equals(action)) {
            String uuid = args.length > 5 ? args[5] : senderUuid(sender);
            if (uuid == null) {
                I18n.send(sender, "command.security.claim_uuid");
                return;
            }
            if (!validUuid(uuid)) {
                I18n.send(sender, "command.security.invalid_uuid", uuid);
                return;
            }
            if (!controller.claim(uuid)) {
                I18n.send(sender, "command.security.already_owned", machine.owner());
                return;
            }
            I18n.send(sender, "command.security.owner_changed", uuid);
            return;
        }
        if (!canAdmin(sender, machine)) {
            I18n.send(sender, "command.security.admin_only");
            return;
        }
        if ("trust".equals(action)) {
            if (args.length < 7) {
                I18n.send(sender, "command.security.trust_usage");
                return;
            }
            String verb = args[5].toLowerCase(Locale.ROOT);
            String uuid = args[6];
            if (!validUuid(uuid)) {
                I18n.send(sender, "command.security.invalid_trust_uuid", uuid);
                return;
            }
            if ("add".equals(verb)) {
                machine.addTrusted(uuid);
            } else if ("remove".equals(verb)) {
                machine.removeTrusted(uuid);
            } else {
                I18n.send(sender, "command.security.trust_action");
                return;
            }
            controller.markChanged();
            I18n.send(sender, "command.security.trusted_changed", machine.trusted());
            return;
        }
        SecurityLevel level = securityOf(action);
        if (level == null) {
            I18n.send(sender, "command.security.level_usage");
            return;
        }
        machine.setSecurity(level);
        controller.markChanged();
        I18n.send(sender, "command.security.level_changed",
                I18n.enumName(level, level.zhName()));
    }

    private void debug(CommandSender sender, String[] args) {
        if (args.length < 2) {
            I18n.send(sender, "command.debug.usage");
            return;
        }
        String action = args[1].toLowerCase(Locale.ROOT);
        if ("mb".equals(action)) {
            multiblockDebug(sender, args);
            return;
        }
        if ("cable".equals(action)) {
            cableDebug(sender, args);
            return;
        }
        if ("heat".equals(action)) {
            heatDebug(sender, args);
            return;
        }
        if ("gen".equals(action) || "generator".equals(action)) {
            generatorDebug(sender, args);
            return;
        }
        if ("block".equals(action)) {
            if (args.length < 5) {
                I18n.send(sender, "command.coordinates_required");
                return;
            }
            block(sender, args);
            return;
        }
        if ("gui".equals(action)) {
            guiDebug(sender, args);
            return;
        }
        MachineBlockEntityController controller = controller(sender, args, 2);
        if (controller == null) {
            return;
        }
        if (denied(sender, controller.machine())) {
            return;
        }
        switch (action) {
            case "put" -> put(sender, controller, args);
            case "putslot" -> putslot(sender, controller, args);
            case "tank" -> tank(sender, controller, args);
            case "take" -> take(sender, controller);
            case "power" -> power(sender, controller, args);
            case "save" -> roundtrip(sender, controller);
            case "upgrade" -> upgrade(sender, controller, args);
            case "run" -> run(sender, controller, args);
            case "spread" -> spread(sender, controller, args);
            case "side" -> side(sender, controller, args);
            case "defaults" -> defaults(sender, controller);
            case "block" -> block(sender, args);
            default -> I18n.send(sender, "command.debug.unknown", action);
        }
    }

    /** /mek debug defaults <x y z>：把发电机的默认面按当前配置重落一遍（旧存档也能修正）。 */
    private void defaults(CommandSender sender, MachineBlockEntityController controller) {
        String blueprint = controller.machine().blueprint().id();
        String family = blueprint.contains(":") ? blueprint.split(":", 2)[1] : blueprint;
        if (GeneratorConfig.defaultSides(family).isEmpty()) {
            I18n.send(sender, "debug.defaults.no_sides", blueprint);
            return;
        }
        controller.resetGeneratorDefaults();
        World world = controller.world();
        if (world != null) {
            this.plugin.logistics(world).refreshAround(controller.x(), controller.y(), controller.z());
        }
        I18n.send(sender, "debug.defaults.reset", controller.taskId(), family);
        for (Direction direction : Direction.values()) {
            StringBuilder line = new StringBuilder("  ").append(direction.name()).append(": ");
            for (String port : controller.machine().portNames()) {
                var side = controller.machine().side(port, direction);
                line.append(port).append('=').append(I18n.enumName(side, side.zhName()))
                        .append(' ');
            }
            var itemSide = controller.machine().itemSide(direction);
            line.append("items=").append(I18n.enumName(itemSide, itemSide.zhName()));
            sender.sendMessage(line.toString());
        }
    }

    /** /mek debug gui <x y z>：没有客户端时也能核对面板 id、槽位格与条的比例。 */
    private void guiDebug(CommandSender sender, String[] args) {
        MachineBlockEntityController controller = controller(sender, args, 2);
        if (controller == null) {
            World world = world(sender);
            int x = parseInt(args[2], 0);
            int y = parseInt(args[3], 0);
            int z = parseInt(args[4], 0);
            MultiblockHosts.Host host = this.plugin.multiblocks().at(world, x, y, z);
            if (host == null) {
                I18n.send(sender, "debug.gui.not_machine_or_structure");
                return;
            }
            String panel = GuiLayout.multiblockPanelId(host.def().id());
            I18n.send(sender, "debug.gui.panel", panel,
                    I18n.tr(sender, GuiSprites.present(panel) ? "debug.gui.ready" : "debug.gui.missing"));
            I18n.send(sender, "debug.gui.multiblock",
                    I18n.structureName(I18n.locale(sender), host.def().id()),
                    host.box().sizeX(), host.box().sizeY(),
                    host.box().sizeZ(), I18n.tr(sender, host.formed() ? "multiblock.formed" : "multiblock.unformed"),
                    GuiSprites.columns("mek:gui/bar_v_mb"));
            host.instance().ports().forEach((name, port) ->
                    I18n.send(sender, "debug.gui.port", name, port.amount(), port.capacity(),
                            port.type().unit()));
            I18n.send(sender, "debug.gui.container", GuiLayout.multiblock().rows() * 9,
                    GuiLayout.multiblock().width(), GuiLayout.multiblock().height());
            for (GuiLayout.MultiblockPage page : GuiLayout.multiblockPages(host.def().id())) {
                I18n.send(sender, "debug.gui.tab", I18n.tabName(page.key(), page.label()),
                        page.panelId(), I18n.tr(sender,
                                GuiSprites.present(page.panelId()) ? "debug.gui.ready" : "debug.gui.missing"),
                        page.ports());
                for (int i = 0; i < page.ports().size(); i++) {
                    I18n.send(sender, "debug.gui.tab_port", page.ports().get(i),
                            GuiLayout.multiblockGauge(page, i), GuiLayout.multiblockGaugeCells(page, i));
                }
                int selected = GuiLayout.multiblockPages(host.def().id()).indexOf(page);
                Inventory preview = Bukkit.createInventory(null, GuiLayout.multiblock().rows() * 9);
                MultiblockGui.updateAnchors(host, preview, selected);
                multiblockViewDebug(sender, host, selected, preview);
            }
            I18n.send(sender, "debug.gui.title_text",
                    I18n.structureName(I18n.locale(sender), host.def().id()));
            return;
        }
        var snapshot = controller.machine().snapshot();
        GuiLayout.MachineLayout layout = GuiLayout.forMachine(controller.machine(),
                controller.machine().factoryTier(), controller.blockId());
        I18n.send(sender, "debug.gui.panel", layout.panelId(),
                I18n.tr(sender, GuiSprites.present(layout.panelId()) ? "debug.gui.ready" : "debug.gui.missing"));
        I18n.send(sender, "debug.gui.machine",
                I18n.tr(sender, layout.factory() ? "debug.gui.factory" : "debug.gui.single"),
                controller.inventory().getSize(), layout.profile().width(), layout.profile().height(),
                layout.profile().rows(),
                I18n.tr(sender, layout.hasProgress() ? "debug.gui.present" : "debug.gui.absent"));
        GuiLayout.BlockPlan plan = GuiLayout.plan(controller.blockId());
        if (plan != null && plan.factory()) {
            I18n.send(sender, "debug.gui.rows", plan.maxInputs() - 1, plan.progressRow(),
                    plan.progressRow() + 1, plan.progressRow() + plan.maxOutputs(), plan.upgradeRow(),
                    plan.extraRow() < 0 ? I18n.tr(sender, "state.none") : plan.extraRow());
        }
        StringBuilder slots = new StringBuilder();
        layout.slots().forEach((name, cell) -> slots.append(name).append('→')
                .append('(').append(cell.col()).append(',').append(cell.row()).append(") "));
        I18n.send(sender, "debug.gui.slots", slots.toString().trim());
        String titleText = GuiText.title(snapshot);
        I18n.send(sender, "debug.gui.title", titleText, GuiText.textWidth(titleText),
                GuiLayout.textX(), layout.profile().width(),
                String.format("%06X", GuiConfig.titleColor()));
        I18n.send(sender, "debug.gui.anchors", layout.energyAnchors(),
                layout.hasTank() ? layout.tankAnchors() : I18n.tr(sender, "debug.gui.no_tank"),
                layout.energySprite(), GuiSprites.columns(layout.energySprite()),
                layout.tankSprite(), GuiSprites.columns(layout.tankSprite()),
                layout.progressSprite(), GuiSprites.columns(layout.progressSprite()));
        I18n.send(sender, "debug.gui.progress_anchors", layout.progressAnchors());
        I18n.send(sender, "debug.gui.recipes", controller.supportedRecipes().size());
        if (layout.hasProgress() && !controller.supportedRecipes().isEmpty()) {
            var browser = RecipeBrowserGui.create(controller);
            I18n.send(sender, "debug.gui.recipe_page", browser.inventory().getSize(),
                    browser.pageCount(), controller.supportedRecipes().getFirst().id());
            browser.closeViewers();
        }
        if (layout.hasProgress()) {
            StringBuilder bars = new StringBuilder();
            for (var parallel : snapshot.parallels()) {
                int level = (int) Math.round(Math.max(0.0, Math.min(1.0, parallel.progress()))
                        * (GuiLayout.LEVELS - 1));
                bars.append(parallel.group()).append('=').append(level)
                        .append('/').append(GuiLayout.LEVELS - 1).append(' ');
            }
            I18n.send(sender, "debug.gui.progress_levels", bars.toString().trim());
        }
        long energyPercent = snapshot.energyCapacityJ() <= 0 ? 0
                : Math.round(100.0 * snapshot.energyJoules() / snapshot.energyCapacityJ());
        I18n.send(sender, "debug.gui.energy", snapshot.energyJoules(),
                snapshot.energyCapacityJ(), energyPercent);
        if (GuiText.firstTank(snapshot) != null) {
            var tank = GuiText.firstTank(snapshot);
            I18n.send(sender, "debug.gui.tank", GuiText.tankName(tank), tank.amount(),
                    tank.capacity(), tank.type().unit(), Math.round(tank.fillRatio() * 100));
        }
        ItemStack anchor = GuiText.energyAnchor(snapshot);
        I18n.send(sender, "debug.gui.energy_anchor", anchor == null ? I18n.tr(sender, "debug.gui.disabled")
                : BukkitItems.idOf(anchor) + " / " + anchor.getItemMeta().displayName()
                + " / lore " + (anchor.getItemMeta().lore() == null ? 0 : anchor.getItemMeta().lore().size()));
        ItemStack tankAnchor = layout.hasTank() ? GuiText.tankAnchor(snapshot) : null;
        I18n.send(sender, "debug.gui.tank_anchor", tankAnchor == null ? I18n.tr(sender, "debug.gui.disabled")
                : BukkitItems.idOf(tankAnchor) + " / " + tankAnchor.getItemMeta().displayName()
                + " / lore " + (tankAnchor.getItemMeta().lore() == null ? 0 : tankAnchor.getItemMeta().lore().size()));
        // 无玩家时也能验证占位/锚点的落格；这些 item 会被存档与掉落跳过。
        MachineGui.updateAnchors(controller);
        int placeholders = 0;
        int visiblePlaceholders = 0;
        int overlays = 0;
        boolean empty = true;
        for (int index = 0; index < controller.inventory().getSize(); index++) {
            ItemStack stack = controller.inventory().getItem(index);
            if (GuiText.isPlaceholder(stack)) {
                placeholders++;
                if ("mek:gui_empty_slot".equals(BukkitItems.idOf(stack))) {
                    visiblePlaceholders++;
                }
            }
            if (GuiText.isOverlay(stack)) {
                overlays++;
            } else if (stack != null && !stack.getType().isAir()) {
                empty = false;
            }
        }
        I18n.send(sender, "debug.gui.placeholders", placeholders, visiblePlaceholders,
                placeholders - visiblePlaceholders, overlays,
                I18n.tr(sender, empty ? "debug.gui.empty_container" : "debug.gui.real_items"));
        I18n.send(sender, "debug.gui.title_text", GuiText.title(snapshot));
        String mini = MiniMessage.miniMessage().serialize(MachineGui.title(controller));
        I18n.send(sender, "debug.gui.minimessage_length", mini.length(),
                mini.split("<font:", -1).length - 1);
        I18n.send(sender, "debug.gui.minimessage_preview",
                mini.substring(0, Math.min(160, mini.length())));
    }

    private static void multiblockViewDebug(CommandSender sender, MultiblockHosts.Host host,
                                            int selected, Inventory preview) {
        long anchors = Arrays.stream(preview.getContents()).filter(GuiText::isAnchor).count();
        I18n.send(sender, "debug.gui.multiblock_view", anchors,
                Integer.toHexString(MultiblockGui.title(host, selected).hashCode()));
    }

    /**
     * /mek debug side &lt;x y z&gt; &lt;端口|items|all&gt; &lt;面&gt; &lt;模式&gt;：
     * 等价于配置器右击那一刻（headless 下没有玩家，机器默认每个面都是"关"，管道连上也不通）。
     * 模式写 关/输入/输出/输入输出（或 0/1/2/3）。
     */
    private void side(CommandSender sender, MachineBlockEntityController controller, String[] args) {
        if (args.length < 8) {
            I18n.send(sender, "command.side.usage");
            return;
        }
        Direction direction = null;
        for (Direction candidate : Direction.values()) {
            if (candidate.name().equalsIgnoreCase(args[6])) {
                direction = candidate;
            }
        }
        SideMode mode = modeOf(args[7]);
        if (direction == null || mode == null) {
            I18n.send(sender, "command.side.mode_usage");
            return;
        }
        Machine machine = controller.machine();
        String port = args[5];
        if ("items".equalsIgnoreCase(port)) {
            machine.setItemSide(direction, mode);
        } else if ("all".equalsIgnoreCase(port)) {
            machine.setSide(direction, mode);
        } else if (machine.portNames().contains(port)) {
            machine.setSide(port, direction, mode);
        } else {
            I18n.send(sender, "machine.port_missing", port, machine.portNames());
            return;
        }
        controller.markChanged();
        World world = controller.world();
        if (world != null) {
            this.plugin.logistics(world).refreshAround(controller.x(), controller.y(), controller.z());
        }
        I18n.send(sender, "command.side.changed", direction.name(), port,
                I18n.enumName(mode, mode.zhName()));
        for (String name : machine.portNames()) {
            var side = machine.side(name, direction);
            I18n.send(sender, "command.side.port_value", name,
                    I18n.enumName(side, side.zhName()));
        }
        var itemSide = machine.itemSide(direction);
        I18n.send(sender, "command.side.item_value",
                I18n.enumName(itemSide, itemSide.zhName()));
    }

    private static SideMode modeOf(String text) {
        switch (text.toLowerCase(Locale.ROOT)) {
            case "0", "关", "none" -> {
                return SideMode.NONE;
            }
            case "1", "输入", "input" -> {
                return SideMode.INPUT;
            }
            case "2", "输出", "output" -> {
                return SideMode.OUTPUT;
            }
            case "3", "输入输出", "both" -> {
                return SideMode.BOTH;
            }
            default -> {
                return null;
            }
        }
    }

    /** 无玩家界面的调试入口；玩家侧走独立的升级安装器物品。 */
    private void upgrade(CommandSender sender, MachineBlockEntityController controller, String[] args) {
        if (args.length < 6 || "list".equalsIgnoreCase(args[5])) {
            listUpgrades(sender, controller);
            return;
        }
        String action = args[5].toLowerCase(Locale.ROOT);
        Machine machine = controller.machine();
        if ("put".equals(action)) {
            if (args.length < 7) {
                I18n.send(sender, "command.upgrade.put_usage");
                return;
            }
            int amount = args.length > 7 ? parseInt(args[7], 1) : 1;
            ItemStack stack = BukkitItems.create(args[6]);
            if (stack == null) {
                I18n.send(sender, "command.item_unknown", args[6]);
                return;
            }
            if (!machine.upgrades().allows(args[6])) {
                I18n.send(sender, "machine.upgrade_rejected",
                        I18n.machineName(I18n.locale(sender), machine.blueprint()), args[6]);
                return;
            }
            int installed = controller.installUpgrade(args[6], amount);
            I18n.send(sender, "command.upgrade.installed", installed, amount, args[6],
                    machine.upgrades().installed());
            I18n.send(sender, "machine.performance", String.format("%.3f", machine.progressRate()),
                    machine.energyPerTick(), machine.energyCapacity());
            return;
        }
        if ("take".equals(action)) {
            if (args.length < 7) {
                I18n.send(sender, "command.upgrade.take_usage");
                return;
            }
            int amount = args.length > 7 ? parseInt(args[7], 1) : 1;
            int removed = controller.uninstallUpgrade(args[6], amount);
            I18n.send(sender, "command.upgrade.removed", removed, args[6]);
            return;
        }
        I18n.send(sender, "command.upgrade.usage");
    }

    /**
     * 逐刻跑机器（每次都先按"电网到货"灌一笔电）：headless 下没有管道与电网，
     * 这一条就是文档 09 说的"配方能跑完、耗时与预期一致"的验法。
     */
    private void run(CommandSender sender, MachineBlockEntityController controller, String[] args) {
        int ticks = args.length > 5 ? parseInt(args[5], 20) : 20;
        long joules = args.length > 6 ? parseLong(args[6], 10_000L) : 10_000L;
        Machine machine = controller.machine();
        long consumedBefore = machine.energyConsumedTotal();
        long operationsBefore = machine.operations();
        long progressBefore = Math.round(machine.progressFraction() * 100);
        for (int index = 0; index < ticks; index++) {
            machine.energyTank().ifPresent(tank -> tank.insert(ResourceStack.energy(joules), Transaction.EXECUTE));
            machine.tick();
        }
        I18n.send(sender, "command.run.done", ticks, joules, machine.describe());
        I18n.send(sender, "command.run.summary", machine.operations() - operationsBefore,
                machine.energyConsumedTotal() - consumedBefore, progressBefore,
                Math.round(machine.progressFraction() * 100), controller.machine().items().describe());
    }

    private static String describe(ItemStack stack) {
        return stack == null || stack.getType().isAir()
                ? I18n.tr("state.empty") : stack.getType() + "×" + stack.getAmount();
    }

    /** 每个并行位各放一个（headless 下验工厂并行：/mek debug put 只会把一堆料塞进第一格）。 */
    private void spread(CommandSender sender, MachineBlockEntityController controller, String[] args) {
        if (args.length < 6) {
            I18n.send(sender, "command.spread.usage");
            return;
        }
        ItemStack template = BukkitItems.create(args[5]);
        if (template == null) {
            I18n.send(sender, "command.item_unknown", args[5]);
            return;
        }
        int placed = 0;
        for (int group = 0; group < controller.machine().factoryTier().parallel(); group++) {
            ItemStack one = template.clone();
            one.setAmount(1);
            if (controller.putInSlot("in#" + group, one)) {
                placed++;
            }
        }
        I18n.send(sender, "command.spread.done", placed, args[5],
                controller.machine().items().describe());
    }

    private void listUpgrades(CommandSender sender, MachineBlockEntityController controller) {
        Machine machine = controller.machine();
        I18n.send(sender, "machine.allowed_upgrades",
                I18n.machineName(I18n.locale(sender), machine.blueprint()),
                machine.blueprint().upgrades().stream()
                        .map(definition -> definition.id() + "×" + definition.maxInstalled())
                        .toList());
        I18n.send(sender, "machine.upgrade_performance", machine.upgrades().installed(),
                String.format("%.3f", machine.progressRate()),
                machine.energyPerTick(), machine.energyCapacity());
    }

    /** 排错用：这个坐标上 Bukkit 看到什么、CE 看到什么、方块实体在不在。 */
    private void block(CommandSender sender, String[] args) {
        World world = world(sender);
        int x = parseInt(args[2], 0);
        int y = parseInt(args[3], 0);
        int z = parseInt(args[4], 0);
        sender.sendMessage(this.plugin.describePos(world, x, y, z));
    }

    /** 走的就是机器自己的 ItemHandler（只进输入槽），跟以后管道走的是同一条路。 */
    private void put(CommandSender sender, MachineBlockEntityController controller, String[] args) {
        if (args.length < 6) {
            I18n.send(sender, "command.put.usage");
            return;
        }
        String id = args[5];
        long amount = args.length > 6 ? parseLong(args[6], 1L) : 1L;
        ItemStack stack = BukkitItems.create(id);
        if (stack == null) {
            I18n.send(sender, "command.item_unknown", id);
            return;
        }
        ItemRef ref = ItemRef.withPayload(BukkitItems.idOf(stack), amount, stack);
        long inserted = controller.machine().items().insert(ref, Transaction.EXECUTE);
        I18n.send(sender, "command.put.done", inserted, amount, id,
                controller.machine().items().roomFor(ref));
        sender.sendMessage("  " + controller.machine().items().describe());
    }

    /** /mek debug putslot &lt;x y z&gt; &lt;槽位&gt; &lt;物品&gt; [数量]：往指定槽位放（辅料槽这类 EXTRA 槽用）。 */
    private void putslot(CommandSender sender, MachineBlockEntityController controller, String[] args) {
        if (args.length < 7) {
            I18n.send(sender, "command.putslot.usage");
            return;
        }
        String slot = args[5];
        String id = args[6];
        int amount = args.length > 7 ? (int) parseLong(args[7], 1L) : 1;
        ItemStack stack = BukkitItems.create(id);
        if (stack == null) {
            I18n.send(sender, "command.item_unknown", id);
            return;
        }
        stack.setAmount(Math.max(1, amount));
        if (!controller.putInSlot(slot, stack)) {
            I18n.send(sender, "machine.slot_missing", slot, controller.handler().slots());
            return;
        }
        I18n.send(sender, "command.putslot.done", slot, id, amount);
        sender.sendMessage("  " + controller.machine().items().describe());
    }

    /** /mek debug tank &lt;x y z&gt; &lt;端口&gt; &lt;资源 id&gt; &lt;数量&gt;：headless 下灌化学罐（辐射测试用）。 */
    private void tank(CommandSender sender, MachineBlockEntityController controller, String[] args) {
        if (args.length >= 7 && "dump".equalsIgnoreCase(args[6])) {
            String port = args[5];
            if (!controller.machine().dumpablePorts().contains(port)) {
                I18n.send(sender, "tank_dump.not_dumpable"); return;
            }
            var tank = controller.machine().tank(port).orElseThrow();
            if (args.length <= 7 || !"info".equalsIgnoreCase(args[7]))
                tank.setDumpMode(args.length > 7 ? mekcore.resource.DumpMode.parse(args[7]) : tank.dumpMode().next());
            controller.markChanged();
            I18n.send(sender, "tank_dump.state_with_amount", port,
                    I18n.enumName(tank.dumpMode(), tank.dumpMode().label()), tank.amount());
            return;
        }
        if (args.length < 8) {
            I18n.send(sender, "command.tank.usage");
            return;
        }
        String port = args[5];
        String id = args[6];
        long amount = parseLong(args[7], 1L);
        mekcore.resource.ResourceTank tank = controller.machine().tank(port).orElse(null);
        if (tank == null) {
            I18n.send(sender, "machine.port_missing", port, controller.machine().portNames());
            return;
        }
        long accepted = tank.insert(new ResourceStack(tank.type(), id, amount), Transaction.EXECUTE);
        I18n.send(sender, "command.tank.filled", port, accepted, amount, tank.describe());
    }

    /** 抽取只出输出槽，与核心的规则一致；执行者是玩家就塞进背包。 */
    private void take(CommandSender sender, MachineBlockEntityController controller) {
        Player player = sender instanceof Player online ? online : null;
        long total = 0;
        while (true) {
            ItemRef candidate = controller.machine().items()
                    .extractCandidate(ref -> !ref.empty());
            if (candidate == null) {
                break;
            }
            long extracted = controller.machine().items().extract(candidate, Transaction.EXECUTE);
            if (extracted <= 0) {
                break;
            }
            if (player != null && candidate.payload() instanceof ItemStack stack) {
                ItemStack give = stack.clone();
                give.setAmount((int) extracted);
                player.getInventory().addItem(give).values()
                        .forEach(left -> player.getWorld().dropItemNaturally(player.getLocation(), left));
            }
            total += extracted;
        }
        I18n.send(sender, "command.take.done", total,
                controller.machine().items().count(-1, ItemSlotKind.OUTPUT, ItemMatcher.any()));
    }

    /** 没管道之前，这一条就是"电到货"：直接往机器的能量端口灌。 */
    private void power(CommandSender sender, MachineBlockEntityController controller, String[] args) {
        long joules = args.length > 5 ? parseLong(args[5], 100_000L) : 100_000L;
        long accepted = controller.machine().energyTank()
                .map(tank -> tank.insert(ResourceStack.energy(joules), Transaction.EXECUTE))
                .orElse(0L);
        I18n.send(sender, "command.power.done", accepted, joules, state(sender, controller));
    }

    /** 存档往返：存 → 故意改现场 → 读回来，两遍状态必须一样（DataTag 适配器的自检）。 */
    private void roundtrip(CommandSender sender, MachineBlockEntityController controller) {
        // 工作状态在读档时按核心口径回到"待机"，所以比较时把它摘掉
        String before = comparable(controller);
        CompoundTag tag = new CompoundTag();
        controller.saveCustomData(tag);
        controller.inventory().clear();
        controller.machine().energyTank().ifPresent(tank -> tank.drain());
        String changed = comparable(controller);
        controller.loadCustomData(tag);
        String after = comparable(controller);
        I18n.send(sender, "command.roundtrip.result",
                before.equals(changed) ? "==" : "!=",
                changed.equals(after) ? "==" : "!=",
                I18n.tr(sender, before.equals(after) ? "command.roundtrip.match" : "command.roundtrip.mismatch"));
        I18n.send(sender, "command.roundtrip.before", before);
        I18n.send(sender, "command.roundtrip.after", after);
    }

    private static String comparable(MachineBlockEntityController controller) {
        return state(null, controller, false);
    }

    private MachineBlockEntityController controller(CommandSender sender, String[] args, int from) {
        if (args.length < from + 3) {
            I18n.send(sender, "command.coordinates_required");
            return null;
        }
        World world = world(sender);
        MachineBlockEntityController controller = this.plugin.machineAt(world, parseInt(args[from], 0),
                parseInt(args[from + 1], 0), parseInt(args[from + 2], 0));
        if (controller == null) {
            I18n.send(sender, "machine.entity_missing_in_world", world.getName());
            sender.sendMessage("  " + this.plugin.describePos(world, parseInt(args[from], 0),
                    parseInt(args[from + 1], 0), parseInt(args[from + 2], 0)));
        }
        return controller;
    }

    /** 玩家就用他自己的世界，控制台用主世界（本地测试服只有一个世界）。 */
    private static World world(CommandSender sender) {
        return sender instanceof Player player ? player.getWorld() : Bukkit.getWorlds().get(0);
    }

    private static String state(CommandSender sender, MachineBlockEntityController controller) {
        return state(sender, controller, true);
    }

    private static String state(CommandSender sender, MachineBlockEntityController controller,
                                boolean includeWorkState) {
        Machine machine = controller.machine();
        StringBuilder sb = new StringBuilder(I18n.machineName(machine.blueprint()));
        if (includeWorkState) {
            sb.append(' ').append(I18n.tr(sender, "machine.working_state",
                            I18n.enumName(machine.state(), machine.state().zhName())))
                    .append(' ').append(I18n.tr(sender, "machine.progress_value",
                            Math.round(machine.progressFraction() * 100)));
        }
        sb.append(' ').append(I18n.tr(sender, "machine.security_line",
                        I18n.enumName(machine.security(), machine.security().zhName()),
                        ownerText(sender, machine), machine.trusted()));
        sb.append(' ').append(I18n.tr(sender, "machine.container"));
        for (int index = 0; index < controller.inventory().getSize(); index++) {
            ItemStack stack = controller.inventory().getItem(index);
            if (GuiText.isOverlay(stack)) {
                continue;
            }
            sb.append(' ').append(index).append('=')
                    .append(stack == null ? I18n.tr(sender, "state.empty")
                            : BukkitItems.idOf(stack) + "×" + stack.getAmount());
        }
        return sb.toString();
    }

    private static String state(MachineBlockEntityController controller) {
        return state(null, controller);
    }

    private static String ownerText(CommandSender sender, Machine machine) {
        return machine.owner() == null || machine.owner().isEmpty()
                ? I18n.tr(sender, "state.none") : machine.owner();
    }

    private static String ownerText(Machine machine) {
        return ownerText(null, machine);
    }

    private static String senderUuid(CommandSender sender) {
        return sender instanceof Player player ? player.getUniqueId().toString() : null;
    }

    private static boolean canAdmin(CommandSender sender, Machine machine) {
        if (!(sender instanceof Player player)) {
            return true;
        }
        if (player.hasPermission("cemekanism.admin")) {
            return true;
        }
        if (machine.owner() == null || machine.owner().isEmpty()) {
            machine.setOwner(player.getUniqueId().toString());
            return true;
        }
        return machine.owner().equals(player.getUniqueId().toString());
    }

    private static boolean denied(CommandSender sender, Machine machine) {
        if (!(sender instanceof Player player)) {
            return false;
        }
        if (machine.canAccess(player.getUniqueId().toString())) {
            return false;
        }
        I18n.send(player, "machine.not_owner",
                I18n.enumName(machine.security(), machine.security().zhName()));
        return true;
    }

    private static SecurityLevel securityOf(String text) {
        for (SecurityLevel level : SecurityLevel.values()) {
            if (level.name().equalsIgnoreCase(text) || level.zhName().equals(text)) {
                return level;
            }
        }
        return null;
    }

    private static boolean validUuid(String text) {
        try {
            UUID.fromString(text);
            return true;
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    private static int parseInt(String text, int fallback) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static long parseLong(String text, long fallback) {
        try {
            return Long.parseLong(text);
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }
}
