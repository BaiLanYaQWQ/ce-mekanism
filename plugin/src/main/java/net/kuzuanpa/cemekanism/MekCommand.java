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
import net.kuzuanpa.cemekanism.machine.MachineBlockEntityController;
import net.kuzuanpa.cemekanism.gui.GuiLayout;
import net.kuzuanpa.cemekanism.gui.GuiSprites;
import net.kuzuanpa.cemekanism.gui.GuiText;
import net.kuzuanpa.cemekanism.gui.GuiConfig;
import net.kuzuanpa.cemekanism.gui.MachineGui;
import net.kuzuanpa.cemekanism.gui.MultiblockGui;
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
                    sender.sendMessage("请在游戏内执行以打开配方浏览页");
                } else if (!player.hasPermission("mek.recipes")) {
                    sender.sendMessage("你没有 mek.recipes 权限");
                } else {
                    net.kuzuanpa.cemekanism.machine.RecipeBrowserGui.openCatalog(this.plugin, player);
                }
            }
            case "selfcheck" -> selfcheck(sender);
            case "b5check" -> net.kuzuanpa.cemekanism.storage.B5Acceptance.run(this.plugin, sender, args);
            case "b5" -> net.kuzuanpa.cemekanism.storage.StorageCommand.run(this.plugin, sender, args);
            case "heater" -> net.kuzuanpa.cemekanism.machine.HeaterCommand.run(this.plugin, sender, args);
            case "b6check" -> net.kuzuanpa.cemekanism.nuclear.B6Acceptance.run(this.plugin, sender, args);
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
            default -> sender.sendMessage("用法：/" + label
                    + " selfcheck | reload | machine <x> <y> <z> | security <x> <y> <z> [...]"
                    + " | pipe <x> <y> <z> | net [世界]"
                    + " | structure <x> <y> <z> | build <x> <y> <z> <结构 id>"
                    + " | demo [x y z]"
                    + " | heater <x y z> [J/t]"
                    + " | showcase <x> <y> <z> [confirm|status]"
                    + " | radiation <status|get|add|heal|removeAll|sources>"
                    + " | reactor <x> <y> <z> <info|on|off|rate|injection|plasma|hohlraum|mode> [...]"
                    + " | debug put|take|power|save|gui|gen|defaults <x> <y> <z> [...]");
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
                sender.sendMessage(target.getName() + " 剂量 " + String.format("%.2f μSv（%s）",
                        dose.microSv(), dose.effect().zhName())
                        + "，环境 " + net.kuzuanpa.cemekanism.radiation.RadiationWorld.format(exposure));
            }
            case "add" -> {
                if (args.length < 3) {
                    sender.sendMessage("用法：/mek radiation add <μSv> [玩家]");
                    return;
                }
                Player target = target(sender, args, 3);
                if (target == null) {
                    return;
                }
                double amount = Double.parseDouble(args[2]);
                this.plugin.radiation(target.getWorld()).dose(target).add(amount);
                sender.sendMessage(target.getName() + " 剂量 +" + amount + " μSv");
            }
            case "heal" -> {
                Player target = target(sender, args, 2);
                if (target == null) {
                    return;
                }
                this.plugin.radiation(target.getWorld()).dose(target).clear();
                net.kuzuanpa.cemekanism.radiation.RadiationStore.save(target.getUniqueId(), 0.0);
                target.removePotionEffect(org.bukkit.potion.PotionEffectType.DARKNESS);
                target.removePotionEffect(org.bukkit.potion.PotionEffectType.NAUSEA);
                target.removePotionEffect(org.bukkit.potion.PotionEffectType.HUNGER);
                sender.sendMessage(target.getName() + " 的辐射剂量已清零");
            }
            case "removeall" -> {
                int cleared = this.plugin.radiationAll().removeAll();
                sender.sendMessage("已清空在线与离线玩家的辐射剂量（" + cleared + " 条记录）");
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
                            : net.kuzuanpa.cemekanism.radiation.RadiationConfig.maxDistance();
                } else if (args.length >= 5) {
                    location = new org.bukkit.Location(world, Double.parseDouble(args[2]),
                            Double.parseDouble(args[3]), Double.parseDouble(args[4]));
                    radius = args.length > 5 ? Integer.parseInt(args[5])
                            : net.kuzuanpa.cemekanism.radiation.RadiationConfig.maxDistance();
                } else {
                    sender.sendMessage("sources 需要玩家执行，或写坐标：/mek radiation sources <x> <y> <z> [半径]");
                    return;
                }
                sender.sendMessage("环境剂量率：" + net.kuzuanpa.cemekanism.radiation.RadiationWorld.format(
                        this.plugin.radiation(world).exposureAt(location)));
                java.util.List<String> lines = this.plugin.radiation(world)
                        .sourceLines(location, radius);
                if (lines.isEmpty()) {
                    sender.sendMessage("半径 " + radius + " 内没有已登记的放射源");
                    return;
                }
                lines.forEach(sender::sendMessage);
            }
            default -> sender.sendMessage("用法：/mek radiation status|get|add|heal|removeAll|sources");
        }
    }

    private Player target(CommandSender sender, String[] args, int index) {
        if (args.length > index) {
            Player named = Bukkit.getPlayerExact(args[index]);
            if (named == null) {
                sender.sendMessage("没有在线玩家：" + args[index]);
            }
            return named;
        }
        if (sender instanceof Player player) {
            return player;
        }
        sender.sendMessage("控制台执行要写玩家名");
        return null;
    }

    /**
     * /mek pipe &lt;x y z&gt;：一根元件的六个面、它这一侧的端点、所在网络（等价于网络读取器右击）。
     * headless 下没有玩家，这一条就是文档 09 说的"管道接上了没"的验法。
     */
    private void pipe(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("用法：/mek pipe <x> <y> <z>");
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
            sender.sendMessage("这个坐标上不是（已加载的）传输元件");
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
            sender.sendMessage("没有这个世界：" + name);
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
            sender.sendMessage("用法：/mek debug cable <x> <y> <z> info | charge <J> | fill <资源 id> <数量>"
                    + " | item <物品 id> <数量> | port <面> | channel <色> | redstone | visual");
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
            sender.sendMessage("这个坐标上不是（已加载的）传输元件");
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
                    sender.sendMessage("要一个面：/mek debug cable <x y z> port <面>");
                    return;
                }
                mekcore.machine.Direction direction = null;
                for (mekcore.machine.Direction candidate : mekcore.machine.Direction.values()) {
                    if (candidate.name().equalsIgnoreCase(args[6])) {
                        direction = candidate;
                    }
                }
                if (direction == null) {
                    sender.sendMessage("面要写 north/south/east/west/up/down");
                    return;
                }
                sender.sendMessage(direction.name() + " 面 → " + controller.cyclePort(direction).zhName());
            }
            case "channel" -> {
                if (args.length < 7) {
                    sender.sendMessage("要一个频道号（-1 = 未设色，0~11 = 第 1~12 色）");
                    return;
                }
                int channel = Integer.parseInt(args[6]);
                if (controller.itemPipe()) {
                    controller.itemNode().setChannel(channel);
                } else {
                    controller.node().setChannel(channel);
                }
                controller.syncSelf();
                sender.sendMessage("颜色 → " + controller.nodeSummary());
            }
            case "charge" -> {
                if (controller.node() == null) {
                    sender.sendMessage("这不是资源族元件（物品管道要放 item）");
                    return;
                }
                if (!controller.node().tier().family().resource().equals(mekcore.resource.ResourceTypes.ENERGY)) {
                    sender.sendMessage("这不是能量线缆（流体/化学品用 fill <资源 id> <数量>）");
                    return;
                }
                long joules = Long.parseLong(args[6]);
                long done = controller.node().buffer().insert(
                        mekcore.resource.ResourceStack.energy(joules),
                        mekcore.resource.Transaction.EXECUTE);
                this.plugin.logistics(world).wake();
                sender.sendMessage("线缆缓存放进 " + done + "/" + joules + " J → " + controller.nodeSummary());
            }
            case "fill" -> {
                if (controller.node() == null) {
                    sender.sendMessage("这不是资源族元件（物品管道要放 item）");
                    return;
                }
                if (args.length < 8) {
                    sender.sendMessage("用法：/mek debug cable <x y z> fill <资源 id> <数量>");
                    return;
                }
                long amount = Long.parseLong(args[7]);
                mekcore.resource.ResourceStack stack = controller.node().tier().family().resource()
                        .equals(mekcore.resource.ResourceTypes.FLUID)
                        ? mekcore.resource.ResourceStack.fluid(args[6], amount)
                        : mekcore.resource.ResourceStack.chemical(args[6], amount);
                long done = controller.node().buffer().insert(stack, mekcore.resource.Transaction.EXECUTE);
                this.plugin.logistics(world).wake();
                sender.sendMessage("管道缓存放进 " + done + "/" + amount + ' ' + args[6]
                        + " → " + controller.nodeSummary());
            }
            case "item" -> {
                if (args.length < 8) {
                    sender.sendMessage("用法：/mek debug cable <x y z> item <物品 id> <数量>");
                    return;
                }
                if (controller.itemNode() == null) {
                    sender.sendMessage("这不是物流管道（物品要放箱柜/机器里）");
                    return;
                }
                long done = controller.itemNode().insert(
                        mekcore.item.ItemRef.of(args[6], Long.parseLong(args[7])));
                this.plugin.logistics(world).wake();
                sender.sendMessage("管道缓存放进 " + done + " 个 " + args[6]
                        + " → " + controller.nodeSummary());
            }
            case "redstone" -> {
                boolean enabled = controller.toggleRedstone();
                sender.sendMessage("红石响应 → " + (enabled ? "开（通着电就不搬运）" : "关"));
            }
            case "visual" -> {
                sender.sendMessage(controller.visual().describe());
                sender.sendMessage("刷新间隔：" + LogisticsConfig.transmitterVisualInterval()
                        + " 刻，内容层" + (LogisticsConfig.transmitterContentsEnabled() ? "开" : "关"));
            }
            default -> sender.sendMessage("不知道 cable " + args[6]);
        }
    }

    /** /mek debug heat &lt;x y z&gt; info|set &lt;K&gt;：给热导线缆定一个温度（没有热源时的替身）。 */
    private void heatDebug(CommandSender sender, String[] args) {
        if (args.length < 6) {
            sender.sendMessage("用法：/mek debug heat <x> <y> <z> info | set <K>");
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
            sender.sendMessage("这个坐标上不是热导线缆");
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
            sender.sendMessage("  温度 " + String.format("%.2f", machine.heatNode().temperatureK())
                    + " K，热容 " + String.format("%.3f", machine.heatNode().heatCapacity())
                    + "（J/K 换算 " + String.format("%.0f", machine.heatNode().config().joulesPerKelvin()) + "）");
        }
        sender.sendMessage("  累计耗电 " + machine.energyConsumedTotal() + " J，累计操作 " + machine.operations());
        for (String port : machine.portNames()) {
            machine.tank(port).ifPresent(tank -> sender.sendMessage("  " + port + "：" + tank.describe()));
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
            sender.sendMessage("  见太阳（上方）=" + GeneratorEnvironment.canSeeSunAbove(world, x, y, z)
                    + "，九个采光点=" + sunPoints + "/9"
                    + "，风力顶部通=" + GeneratorEnvironment.windTopOpen(world, x, y, z)
                    + "，禁风维度=" + GeneratorEnvironment.noWindDimension(world)
                    + "，熔岩邻面=" + GeneratorEnvironment.lavaSides(world, x, y, z));
        }
    }

    /** /mek structure <x y z>：成型 + 逐位置报"期望什么、实际什么" + 端口位置（文档 05 第四节）。 */
    private void structure(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("用法：/mek structure <x> <y> <z>");
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
            sender.sendMessage("这个坐标上不是结构锚点，也不在任何已登记的结构里");
            return;
        }
        sender.sendMessage(host.describe());
        sender.sendMessage("  " + host.validation().describe());
        for (String note : host.extraIssues()) {
            sender.sendMessage("  " + note);
        }
        for (String port : host.def().ports()) {
            sender.sendMessage("  端口 " + port + "：" + host.instance().tank(port).describe()
                    + " 容量规则 " + host.def().capacityRule(port)
                    + "，位置 " + host.instance().portPositions(port));
        }
    }

    /** /mek build <x y z> <结构 id>：一键搭建（锚点在最小角，清单来自核心的 buildPlan）。 */
    private void build(CommandSender sender, String[] args) {
        if (args.length < 5) {
            sender.sendMessage("用法：/mek build <x> <y> <z> <结构 id>；已知结构："
                    + MultiblockBlocks.all().stream().map(MultiblockDef::id).toList());
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
            sender.sendMessage("尺寸不满足：" + def.zh() + " 要 " + def.wMin() + "~" + def.wMax() + "×"
                    + def.hMin() + "~" + def.hMax());
            return;
        }
        MultiblockPlacements.Result result = MultiblockPlacements.build(
                this.plugin, world, x, y, z, def, sizeX, sizeY, sizeZ);
        sender.sendMessage("按清单摆了 " + result.placed() + " 格（已就位 " + result.kept()
                + "，清空内部 " + result.cleared() + "）");
        int[] offset = def.buildAnchor(sizeX, sizeY, sizeZ);
        MultiblockBlockEntityController anchor = this.plugin.multiblockAt(world,
                x + offset[0], y + offset[1], z + offset[2]);
        MultiblockHosts.Host host = anchor == null ? null : anchor.form();
        if (host == null) {
            sender.sendMessage("锚点方块实体还没建起来（区块是否加载？）");
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
            sender.sendMessage("demo 的储罐需要 3×3×3 的最小结构；" + def.id() + " 放不下");
            return;
        }
        if (!def.ports().contains("fluid")) {
            sender.sendMessage("demo 的储罐需要一个叫 fluid 的端口；" + def.id() + " 的端口是 " + def.ports());
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
            sender.sendMessage("机器方块实体没建起来（先 /forceload add " + (x >> 4) + ' ' + (z >> 4)
                    + "，再重跑一次 demo）");
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

        sender.sendMessage("demo → (" + x + ',' + y + ',' + z + ")");
        sender.sendMessage("  A " + cfg.machineA() + " @(" + x + ',' + y + ',' + z + ")");
        sender.sendMessage("  B " + cfg.machineB() + " @(" + (x + 2) + ',' + y + ',' + z + ")");
        sender.sendMessage("  物品管道 @(" + (x + 1) + ',' + y + ',' + z + ")；"
                + "漏斗 → A → 管道 → B → 漏斗 → 箱子");
        sender.sendMessage("  双储罐 @(" + (x + 4) + ',' + y + ',' + z + ") / ("
                + (x + 4) + ',' + y + ',' + tankZ + ")，摆了 "
                + (tankA.placed() + tankB.placed()) + " 格；机械管道 x=" + (x + 5) + " y=" + (y - 1));
        sender.sendMessage("  线缆充电 " + chargedA + " / " + chargedB + " J；起始料 "
                + starter + "×" + cfg.starterItem());
        if (hostA == null || hostB == null) {
            sender.sendMessage("  储罐没有成型：看 /mek structure 或 /mek build 的报错");
        } else {
            sender.sendMessage("  储罐端口 " + hostA.instance().tank("fluid").describe() + " / "
                    + hostB.instance().tank("fluid").describe()
                    + "（等十几秒应变成 2500/2500）");
        }
        sender.sendMessage("  观察：/mek machine " + x + ' ' + y + ' ' + z
                + "；/mek pipe " + (x + 1) + ' ' + y + ' ' + z
                + "；/mek debug mb " + (x + 4) + ' ' + y + ' ' + z + " info");
        sender.sendMessage("  注意：demo 会占用从锚点起 9×5×8 的格子；坐标、id 与起始料在 mek-systems.yml 的 demo 段");
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
            sender.sendMessage("用法：/mek debug mb <x> <y> <z> info | blades <n> | put <端口> <数量> [资源 id]");
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
            sender.sendMessage("这个坐标不在任何已登记的结构里");
            return;
        }
        switch (args[5].toLowerCase(Locale.ROOT)) {
            case "info" -> {
                sender.sendMessage(host.describe());
                for (String port : host.def().ports()) {
                    sender.sendMessage("  " + port + "=" + host.instance().tank(port).describe()
                            + "（容量 " + host.instance().tank(port).capacity()
                            + "，规则 " + host.def().capacityRule(port) + "）");
                }
                if (host.instance().heatNode() != null) {
                    sender.sendMessage("  温度 " + String.format("%.2f", host.instance().heatNode().temperatureK())
                            + "K，热容 " + host.instance().heatNode().heatCapacity());
                }
                sender.sendMessage("  叶片 " + host.blades() + "，角色计数 " + host.instance().shape().describe());
            }
            case "dump" -> {
                if (args.length < 8) { sender.sendMessage("/mek debug mb <x y z> dump <端口> <idle|excess|dumping>"); return; }
                var tank = host.instance().tank(args[6]);
                if (!tank.type().equals(mekcore.resource.ResourceTypes.CHEMICAL)) { sender.sendMessage("只支持化学品罐"); return; }
                if (!"info".equalsIgnoreCase(args[7])) {
                    tank.setDumpMode(mekcore.resource.DumpMode.parse(args[7])); host.markChanged();
                }
                sender.sendMessage(args[6] + " 排出状态=" + tank.dumpMode().label());
            }
            case "blades" -> {
                host.setBlades(Long.parseLong(args[6]));
                sender.sendMessage("叶片数设为 " + host.blades());
            }
            case "put" -> {
                if (args.length < 8) {
                    sender.sendMessage("用法：/mek debug mb <x> <y> <z> put <端口> <数量> [资源 id]");
                    return;
                }
                String port = args[6];
                long amount = Long.parseLong(args[7]);
                String id = args.length > 8 ? args[8] : null;
                mekcore.resource.ResourceTank tank = host.instance().tank(port);
                mekcore.resource.ResourceStack stack = switch (tank.type().id()) {
                    case "mek:energy" -> mekcore.resource.ResourceStack.energy(amount);
                    case "mek:chemical" -> mekcore.resource.ResourceStack.chemical(
                            id == null ? net.kuzuanpa.cemekanism.multiblock.MultiblockConfig.STEAM : id, amount);
                    default -> mekcore.resource.ResourceStack.fluid(
                            id == null ? net.kuzuanpa.cemekanism.multiblock.MultiblockConfig.WATER : id, amount);
                };
                long done = tank.insert(stack, mekcore.resource.Transaction.EXECUTE);
                sender.sendMessage("端口 " + port + " 放入 " + done + "/" + amount + " " + stack.id()
                        + " → " + tank.describe());
            }
            default -> sender.sendMessage("用法：/mek debug mb <x> <y> <z> info | blades <n> | put <端口> <数量> [资源 id]");
        }
    }

    /** /mek reactor：裂变/聚变/SPS 的状态与无客户端调试入口。 */
    private void reactor(CommandSender sender, String[] args) {
        if (args.length < 5) {
            sender.sendMessage("用法：/mek reactor <x> <y> <z> info|on|off|rate <值>|injection <值>|plasma <K>|hohlraum|mode <px> <py> <pz>");
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
            sender.sendMessage("这个坐标不在任何已登记的结构里");
            return;
        }
        String action = args[4].toLowerCase(Locale.ROOT);
        if ("mode".equals(action)) {
            if (args.length < 8) {
                sender.sendMessage("用法：/mek reactor <x> <y> <z> mode <端口方块 x> <y> <z>");
                return;
            }
            String next = host.cyclePortMode(Integer.parseInt(args[5]), Integer.parseInt(args[6]),
                    Integer.parseInt(args[7]));
            sender.sendMessage(next.isEmpty() ? "这个方块没有可切换的端口模式"
                    : host.def().zh() + " 端口模式 → " + next);
            host.markChanged();
            return;
        }
        if ("info".equals(action)) {
            sender.sendMessage(host.describe());
            for (String port : host.def().ports()) {
                sender.sendMessage("  " + port + "=" + host.instance().tank(port).describe());
            }
            if (host.instance().heatNode() != null) {
                sender.sendMessage("  壳体温度 " + String.format("%.2f K", host.instance().heatNode().temperatureK())
                        + "，热容 " + String.format("%.4f", host.instance().heatNode().heatCapacity()));
            }
            if (host.instance().blueprint().logic() instanceof FissionReactorLogic fission) {
                FissionReactorLogic.State state = fission.state(host.instance());
                sender.sendMessage("  裂变 运行=" + state.active + " 强制停=" + state.forceDisabled
                        + " 燃耗=" + String.format("%.3f", state.rateLimit)
                        + " 余量=" + String.format("%.3f", state.burnRemaining)
                        + " 损伤=" + String.format("%.2f%%", state.damage)
                        + " 废料余数=" + String.format("%.3f", state.partialWaste));
            } else if (host.instance().blueprint().logic() instanceof FusionReactorLogic fusion) {
                FusionReactorLogic.State state = fusion.state(host.instance());
                sender.sendMessage("  聚变 燃烧=" + state.burning
                        + " 等离子体=" + String.format("%.2f MK", state.plasmaTemperatureK / 1_000_000)
                        + " 注入=" + state.injectionRate
                        + " 黑体辐射腔=" + (state.hohlraum == null ? "无" : state.hohlraum.getAmount()));
            } else if (host.instance().blueprint().logic() instanceof SpsLogic sps) {
                SpsLogic.State state = sps.state(host.instance());
                sender.sendMessage("  SPS 进度=" + String.format("%.1f/1000 mB", state.progress)
                        + " 上刻能量=" + state.lastEnergy + " J");
            }
            return;
        }
        if (host.instance().blueprint().logic() instanceof FissionReactorLogic fission) {
            switch (action) {
                case "on" -> fission.setActive(host.instance(), true);
                case "off" -> fission.setActive(host.instance(), false);
                case "rate" -> {
                    if (args.length < 6) {
                        sender.sendMessage("用法：/mek reactor <x> <y> <z> rate <mB/t>");
                        return;
                    }
                    fission.setRate(host.instance(), Double.parseDouble(args[5]));
                }
                default -> sender.sendMessage("裂变堆支持 info|on|off|rate|mode");
            }
            host.markChanged();
            sender.sendMessage("裂变反应堆配置已更新");
            return;
        }
        if (host.instance().blueprint().logic() instanceof FusionReactorLogic fusion) {
            switch (action) {
                case "injection" -> {
                    if (args.length < 6) {
                        sender.sendMessage("用法：/mek reactor <x> <y> <z> injection <偶数>");
                        return;
                    }
                    fusion.setInjectionRate(host.instance(), Integer.parseInt(args[5]));
                }
                case "plasma" -> {
                    if (args.length < 6) {
                        sender.sendMessage("用法：/mek reactor <x> <y> <z> plasma <K>");
                        return;
                    }
                    fusion.setPlasmaTemperature(host.instance(), Double.parseDouble(args[5]));
                }
                case "hohlraum" -> {
                    ItemStack hohlraum = BukkitItems.create("mek:hohlraum");
                    if (hohlraum == null) {
                        sender.sendMessage("mek:hohlraum 没有注册");
                        return;
                    }
                    HohlraumItems.fill(hohlraum, FusionReactorLogic.FUSION_FUEL,
                            net.kuzuanpa.cemekanism.multiblock.MultiblockConfig.hohlraumCapacity());
                    fusion.insertHohlraum(host.instance(), hohlraum);
                }
                default -> sender.sendMessage("聚变堆支持 info|injection|plasma|hohlraum|mode");
            }
            host.markChanged();
            sender.sendMessage("聚变反应堆配置已更新");
            return;
        }
        sender.sendMessage("这台结构没有 B6 核工业逻辑");
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
        MekSystems.install(this.plugin);
        GeneratorConfig.install(this.plugin);
        net.kuzuanpa.cemekanism.radiation.RadiationConfig.install(this.plugin);
        TransmitterShapes.apply(this.plugin, MekSystems.transmitterCollision6px());
        sender.sendMessage("系统参数已重读：" + mekcore.spec.V10Tables.summary());
    }

    private void machine(CommandSender sender, String[] args) {
        MachineBlockEntityController controller = controller(sender, args, 1);
        if (controller == null) {
            return;
        }
        if (denied(sender, controller.machine())) {
            return;
        }
        sender.sendMessage(controller.taskId() + (controller.registered() ? "（已挂调度器）" : "（不在调度器里）"));
        sender.sendMessage("  " + state(controller));
        TickScheduler.Entry entry = this.plugin.scheduler().task(controller.taskId());
        sender.sendMessage(entry == null ? "  调度器里没有这个任务"
                : "  调度器：跑 " + entry.runs() + " 跳 " + entry.skips()
                + (entry.sleeping() ? "（睡着）" : "（醒着）"));
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
            sender.sendMessage(machine.blueprint().zhName() + " 安全=" + machine.security().zhName()
                    + " owner=" + ownerText(machine) + " trusted=" + machine.trusted());
            return;
        }
        String action = args[4].toLowerCase(Locale.ROOT);
        if ("claim".equals(action)) {
            String uuid = args.length > 5 ? args[5] : senderUuid(sender);
            if (uuid == null) {
                sender.sendMessage("控制台认领要给 uuid：/mek security <x> <y> <z> claim <uuid>");
                return;
            }
            if (!validUuid(uuid)) {
                sender.sendMessage("owner 要写玩家 UUID：" + uuid);
                return;
            }
            if (!controller.claim(uuid)) {
                sender.sendMessage("已有主人 " + machine.owner() + "，先由主人或控制台改");
                return;
            }
            sender.sendMessage("owner → " + uuid);
            return;
        }
        if (!canAdmin(sender, machine)) {
            sender.sendMessage("只有主人或 cemekanism.admin 能改这台机器的安全");
            return;
        }
        if ("trust".equals(action)) {
            if (args.length < 7) {
                sender.sendMessage("用法：/mek security <x> <y> <z> trust add|remove <uuid>");
                return;
            }
            String verb = args[5].toLowerCase(Locale.ROOT);
            String uuid = args[6];
            if (!validUuid(uuid)) {
                sender.sendMessage("信任名单要写玩家 UUID：" + uuid);
                return;
            }
            if ("add".equals(verb)) {
                machine.addTrusted(uuid);
            } else if ("remove".equals(verb)) {
                machine.removeTrusted(uuid);
            } else {
                sender.sendMessage("trust 后面写 add 或 remove");
                return;
            }
            controller.markChanged();
            sender.sendMessage("trusted → " + machine.trusted());
            return;
        }
        SecurityLevel level = securityOf(action);
        if (level == null) {
            sender.sendMessage("等级写 public / trusted / private");
            return;
        }
        machine.setSecurity(level);
        controller.markChanged();
        sender.sendMessage("安全等级 → " + level.zhName());
    }

    private void debug(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("用法：/mek debug put|take|power|save|gui|defaults|block <x> <y> <z> [...]");
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
                sender.sendMessage("要坐标：/mek debug block <x> <y> <z>");
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
            default -> sender.sendMessage("不知道 debug " + action);
        }
    }

    /** /mek debug defaults <x y z>：把发电机的默认面按当前配置重落一遍（旧存档也能修正）。 */
    private void defaults(CommandSender sender, MachineBlockEntityController controller) {
        String blueprint = controller.machine().blueprint().id();
        String family = blueprint.contains(":") ? blueprint.split(":", 2)[1] : blueprint;
        if (GeneratorConfig.defaultSides(family).isEmpty()) {
            sender.sendMessage(blueprint + " 在 generator.defaults 里没有默认面，未改动。");
            return;
        }
        controller.resetGeneratorDefaults();
        World world = controller.world();
        if (world != null) {
            this.plugin.logistics(world).refreshAround(controller.x(), controller.y(), controller.z());
        }
        sender.sendMessage(controller.taskId() + " 已按 generator.defaults." + family + " 与当前 facing 重落默认面");
        for (Direction direction : Direction.values()) {
            StringBuilder line = new StringBuilder("  " + direction.name() + "：");
            for (String port : controller.machine().portNames()) {
                line.append(port).append('=').append(controller.machine().side(port, direction).zhName())
                        .append(' ');
            }
            line.append("items=").append(controller.machine().itemSide(direction).zhName());
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
                sender.sendMessage("这里不是机器，也不在任何已登记的多方块结构里。");
                return;
            }
            String panel = GuiLayout.multiblockPanelId(host.def().id());
            sender.sendMessage("GUI 面板 " + panel + " = "
                    + (GuiSprites.present(panel) ? "字形就绪" : "缺失"));
            sender.sendMessage("  多方块 " + host.def().zh() + " " + host.box().sizeX() + "×"
                    + host.box().sizeY() + "×" + host.box().sizeZ() + "，"
                    + (host.formed() ? "已成型" : "未成型") + "，端口字形 "
                    + GuiSprites.columns("mek:gui/bar_v_mb") + " 档");
            host.instance().ports().forEach((name, port) ->
                    sender.sendMessage("  端口 " + name + "：" + port.amount() + "/" + port.capacity()
                            + " " + port.type().unit()));
            sender.sendMessage("  容器 " + GuiLayout.multiblock().rows() * 9 + " 格，面板 "
                    + GuiLayout.multiblock().width() + "×" + GuiLayout.multiblock().height());
            for (GuiLayout.MultiblockPage page : GuiLayout.multiblockPages(host.def().id())) {
                sender.sendMessage("  标签 " + page.label() + "：" + page.panelId() + " "
                        + (GuiSprites.present(page.panelId()) ? "字形就绪" : "缺失")
                        + "，端口 " + page.ports());
                for (int i = 0; i < page.ports().size(); i++) {
                    sender.sendMessage("    " + page.ports().get(i) + " → "
                            + GuiLayout.multiblockGauge(page, i) + "，悬停格 " + GuiLayout.multiblockGaugeCells(page, i));
                }
                int selected = GuiLayout.multiblockPages(host.def().id()).indexOf(page);
                Inventory preview = Bukkit.createInventory(null, GuiLayout.multiblock().rows() * 9);
                MultiblockGui.updateAnchors(host, preview, selected);
                multiblockViewDebug(sender, host, selected, preview);
            }
            sender.sendMessage("  标题文字：" + host.def().zh());
            return;
        }
        var snapshot = controller.machine().snapshot();
        GuiLayout.MachineLayout layout = GuiLayout.forMachine(controller.machine(),
                controller.machine().factoryTier(), controller.blockId());
        sender.sendMessage("GUI 面板 " + layout.panelId() + " = "
                + (GuiSprites.present(layout.panelId()) ? "字形就绪" : "缺失"));
        sender.sendMessage("  档位 " + (layout.factory() ? "工厂" : "单机")
                + "，容器 " + controller.inventory().getSize() + " 格，面板 "
                + layout.profile().width() + "×" + layout.profile().height()
                + "（" + layout.profile().rows() + " 行）"
                + "，进度 " + (layout.hasProgress() ? "有" : "无"));
        GuiLayout.BlockPlan plan = GuiLayout.plan(controller.blockId());
        if (plan != null && plan.factory()) {
            sender.sendMessage("  行序：输入 0.." + (plan.maxInputs() - 1)
                    + "，进度 " + plan.progressRow()
                    + "，输出 " + (plan.progressRow() + 1) + ".."
                    + (plan.progressRow() + plan.maxOutputs())
                    + "，能源 " + plan.upgradeRow()
                    + "，额外 " + (plan.extraRow() < 0 ? "无" : plan.extraRow()));
        }
        StringBuilder slots = new StringBuilder();
        layout.slots().forEach((name, cell) -> slots.append(name).append('→')
                .append('(').append(cell.col()).append(',').append(cell.row()).append(") "));
        sender.sendMessage("  槽位：" + slots.toString().trim());
        String titleText = GuiText.title(snapshot);
        sender.sendMessage("  标题：" + titleText + "（" + GuiText.textWidth(titleText)
                + "px，起点 x=" + GuiLayout.textX() + "/" + layout.profile().width()
                + "，步进=字形宽+1，文字色 #"
                + String.format("%06X", GuiConfig.titleColor()) + "）");
        sender.sendMessage("  能量锚点 " + layout.energyAnchors() + "，罐锚点 "
                + (layout.hasTank() ? layout.tankAnchors() : "无罐")
                + "；能量条 " + layout.energySprite() + "=" + GuiSprites.columns(layout.energySprite())
                + "，罐条 " + layout.tankSprite() + "=" + GuiSprites.columns(layout.tankSprite())
                + " 档，进度字形 " + layout.progressSprite() + "="
                + GuiSprites.columns(layout.progressSprite()) + " 档");
        sender.sendMessage("  进度锚点 " + layout.progressAnchors());
        sender.sendMessage("  配方浏览：" + controller.supportedRecipes().size()
                + " 条，每页 5 条，点击进度锚点打开");
        if (layout.hasProgress() && !controller.supportedRecipes().isEmpty()) {
            var browser = net.kuzuanpa.cemekanism.machine.RecipeBrowserGui.create(controller);
            sender.sendMessage("  配方页：" + browser.inventory().getSize() + " 格，"
                    + browser.pageCount() + " 页；首页配方 "
                    + controller.supportedRecipes().getFirst().id());
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
            sender.sendMessage("  进度档：" + bars.toString().trim());
        }
        long energyPercent = snapshot.energyCapacityJ() <= 0 ? 0
                : Math.round(100.0 * snapshot.energyJoules() / snapshot.energyCapacityJ());
        sender.sendMessage("  能量 " + snapshot.energyJoules() + "/" + snapshot.energyCapacityJ()
                + " J（" + energyPercent + "%）");
        if (GuiText.firstTank(snapshot) != null) {
            var tank = GuiText.firstTank(snapshot);
            sender.sendMessage("  罐 " + GuiText.tankName(tank) + " "
                    + tank.amount() + "/" + tank.capacity() + " " + tank.type().unit()
                    + "（" + Math.round(tank.fillRatio() * 100) + "%）");
        }
        ItemStack anchor = GuiText.energyAnchor(snapshot);
        sender.sendMessage("  能量锚点物品：" + (anchor == null ? "关闭/无"
                : BukkitItems.idOf(anchor) + " / " + anchor.getItemMeta().displayName()
                + " / lore " + (anchor.getItemMeta().lore() == null ? 0 : anchor.getItemMeta().lore().size())));
        ItemStack tankAnchor = layout.hasTank() ? GuiText.tankAnchor(snapshot) : null;
        sender.sendMessage("  罐锚点物品：" + (tankAnchor == null ? "关闭/无"
                : BukkitItems.idOf(tankAnchor) + " / " + tankAnchor.getItemMeta().displayName()
                + " / lore " + (tankAnchor.getItemMeta().lore() == null ? 0 : tankAnchor.getItemMeta().lore().size())));
        // 无玩家时也能验证占位/锚点的落格；这些 item 会被存档与掉落跳过。
        MachineGui.updateAnchors(controller);
        int placeholders = 0;
        int visiblePlaceholders = 0;
        int overlays = 0;
        boolean empty = true;
        for (int index = 0; index < controller.inventory().getSize(); index++) {
            ItemStack stack = controller.inventory().getItem(index);
            if (net.kuzuanpa.cemekanism.gui.GuiText.isPlaceholder(stack)) {
                placeholders++;
                if ("mek:gui_empty_slot".equals(BukkitItems.idOf(stack))) {
                    visiblePlaceholders++;
                }
            }
            if (net.kuzuanpa.cemekanism.gui.GuiText.isOverlay(stack)) {
                overlays++;
            } else if (stack != null && !stack.getType().isAir()) {
                empty = false;
            }
        }
        sender.sendMessage("  占位 " + placeholders + " 格（底色 " + visiblePlaceholders
                + " / 空模型 " + (placeholders - visiblePlaceholders) + "），锚点/占位合计 "
                + overlays + " 格；忽略占位后容器" + (empty ? "为空" : "有真实物品"));
        sender.sendMessage("  标题文字：" + GuiText.title(snapshot));
        String mini = MiniMessage.miniMessage().serialize(MachineGui.title(controller));
        sender.sendMessage("  MiniMessage 长度 " + mini.length() + "，字形标签 "
                + (mini.split("<font:", -1).length - 1) + " 个");
        sender.sendMessage("  MiniMessage 预览：" + mini.substring(0, Math.min(160, mini.length())));
    }

    private static void multiblockViewDebug(CommandSender sender, MultiblockHosts.Host host,
                                            int selected, Inventory preview) {
        long anchors = java.util.Arrays.stream(preview.getContents()).filter(GuiText::isAnchor).count();
        sender.sendMessage("    视图构建就绪，数值锚点 " + anchors + "，标题组件 "
                + Integer.toHexString(MultiblockGui.title(host, selected).hashCode()));
    }

    /**
     * /mek debug side &lt;x y z&gt; &lt;端口|items|all&gt; &lt;面&gt; &lt;模式&gt;：
     * 等价于配置器右击那一刻（headless 下没有玩家，机器默认每个面都是"关"，管道连上也不通）。
     * 模式写 关/输入/输出/输入输出（或 0/1/2/3）。
     */
    private void side(CommandSender sender, MachineBlockEntityController controller, String[] args) {
        if (args.length < 8) {
            sender.sendMessage("用法：/mek debug side <x> <y> <z> <端口|items|all> <面> <模式>");
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
            sender.sendMessage("面写 north/south/east/west/up/down；模式写 关/输入/输出/输入输出（或 0~3）");
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
            sender.sendMessage("这台机器没有端口 " + port + "；它有 " + machine.portNames());
            return;
        }
        controller.markChanged();
        World world = controller.world();
        if (world != null) {
            this.plugin.logistics(world).refreshAround(controller.x(), controller.y(), controller.z());
        }
        sender.sendMessage(direction.name() + " 面的 " + port + " → " + mode.zhName());
        for (String name : machine.portNames()) {
            sender.sendMessage("  " + name + " = " + machine.side(name, direction).zhName());
        }
        sender.sendMessage("  物品面 = " + machine.itemSide(direction).zhName());
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
                sender.sendMessage("用法：/mek debug upgrade <x> <y> <z> put <升级 id> [数量]");
                return;
            }
            int amount = args.length > 7 ? parseInt(args[7], 1) : 1;
            ItemStack stack = BukkitItems.create(args[6]);
            if (stack == null) {
                sender.sendMessage("资源包与 Paper 都不认识 " + args[6]);
                return;
            }
            if (!machine.upgrades().allows(args[6])) {
                sender.sendMessage(machine.blueprint().zhName() + " 不接受 " + args[6]);
                return;
            }
            int installed = controller.installUpgrade(args[6], amount);
            sender.sendMessage("装入 " + installed + " / " + amount + " 个 " + args[6] + " → 已装 "
                    + machine.upgrades().installed());
            sender.sendMessage("  速度 ×" + String.format("%.3f", machine.progressRate())
                    + "，耗能 " + machine.energyPerTick() + " J/t，缓存 " + machine.energyCapacity() + " J");
            return;
        }
        if ("take".equals(action)) {
            if (args.length < 7) {
                sender.sendMessage("用法：/mek debug upgrade <x> <y> <z> take <升级 id> [数量]");
                return;
            }
            int amount = args.length > 7 ? parseInt(args[7], 1) : 1;
            int removed = controller.uninstallUpgrade(args[6], amount);
            sender.sendMessage("卸下 " + removed + " 个 " + args[6] + "；物品已落到机器上方");
            return;
        }
        sender.sendMessage("用法：/mek debug upgrade <x> <y> <z> put|take|list [...]");
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
        sender.sendMessage("跑了 " + ticks + " 刻（每刻先灌 " + joules + " J）：" + machine.describe());
        sender.sendMessage("  操作 +" + (machine.operations() - operationsBefore)
                + "，耗电 " + (machine.energyConsumedTotal() - consumedBefore) + " J"
                + "，进度 " + progressBefore + "% → " + Math.round(machine.progressFraction() * 100) + "%"
                + "；" + controller.machine().items().describe());
    }

    private static String describe(ItemStack stack) {
        return stack == null || stack.getType().isAir()
                ? "空" : stack.getType() + "×" + stack.getAmount();
    }

    /** 每个并行位各放一个（headless 下验工厂并行：/mek debug put 只会把一堆料塞进第一格）。 */
    private void spread(CommandSender sender, MachineBlockEntityController controller, String[] args) {
        if (args.length < 6) {
            sender.sendMessage("用法：/mek debug spread <x> <y> <z> <物品 id>");
            return;
        }
        ItemStack template = BukkitItems.create(args[5]);
        if (template == null) {
            sender.sendMessage("资源包与 Paper 都不认识 " + args[5]);
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
        sender.sendMessage("往 " + placed + " 个并行位的输入槽各放了 1 个 " + args[5] + "；"
                + controller.machine().items().describe());
    }

    private void listUpgrades(CommandSender sender, MachineBlockEntityController controller) {
        Machine machine = controller.machine();
        sender.sendMessage(machine.blueprint().zhName() + " 允许的升级："
                + machine.blueprint().upgrades().stream()
                        .map(definition -> definition.id() + "×" + definition.maxInstalled())
                        .toList());
        sender.sendMessage("  已装 " + machine.upgrades().installed() + " 个"
                + "；速度 ×" + String.format("%.3f", machine.progressRate())
                + "，耗能 " + machine.energyPerTick() + " J/t，缓存 " + machine.energyCapacity() + " J");
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
            sender.sendMessage("用法：/mek debug put <x> <y> <z> <物品 id> [数量]");
            return;
        }
        String id = args[5];
        long amount = args.length > 6 ? parseLong(args[6], 1L) : 1L;
        ItemStack stack = BukkitItems.create(id);
        if (stack == null) {
            sender.sendMessage("资源包与 Paper 都不认识 " + id);
            return;
        }
        ItemRef ref = ItemRef.withPayload(BukkitItems.idOf(stack), amount, stack);
        long inserted = controller.machine().items().insert(ref, Transaction.EXECUTE);
        sender.sendMessage("放入 " + inserted + "/" + amount + " 个 " + id
                + "；现在能塞 " + controller.machine().items().roomFor(ref) + " 个");
        sender.sendMessage("  " + controller.machine().items().describe());
    }

    /** /mek debug putslot &lt;x y z&gt; &lt;槽位&gt; &lt;物品&gt; [数量]：往指定槽位放（辅料槽这类 EXTRA 槽用）。 */
    private void putslot(CommandSender sender, MachineBlockEntityController controller, String[] args) {
        if (args.length < 7) {
            sender.sendMessage("用法：/mek debug putslot <x> <y> <z> <槽位> <物品 id> [数量]");
            return;
        }
        String slot = args[5];
        String id = args[6];
        int amount = args.length > 7 ? (int) parseLong(args[7], 1L) : 1;
        ItemStack stack = BukkitItems.create(id);
        if (stack == null) {
            sender.sendMessage("资源包与 Paper 都不认识 " + id);
            return;
        }
        stack.setAmount(Math.max(1, amount));
        if (!controller.putInSlot(slot, stack)) {
            sender.sendMessage("这台机器没有槽位 " + slot + "；它有 " + controller.handler().slots());
            return;
        }
        sender.sendMessage("已放入 " + slot + "：" + id + " ×" + amount);
        sender.sendMessage("  " + controller.machine().items().describe());
    }

    /** /mek debug tank &lt;x y z&gt; &lt;端口&gt; &lt;资源 id&gt; &lt;数量&gt;：headless 下灌化学罐（辐射测试用）。 */
    private void tank(CommandSender sender, MachineBlockEntityController controller, String[] args) {
        if (args.length >= 7 && "dump".equalsIgnoreCase(args[6])) {
            String port = args[5];
            if (!controller.machine().dumpablePorts().contains(port)) {
                sender.sendMessage("该端口不是可设置排出的化学品输出罐"); return;
            }
            var tank = controller.machine().tank(port).orElseThrow();
            if (args.length <= 7 || !"info".equalsIgnoreCase(args[7]))
                tank.setDumpMode(args.length > 7 ? mekcore.resource.DumpMode.parse(args[7]) : tank.dumpMode().next());
            controller.markChanged();
            sender.sendMessage(port + " 排出状态=" + tank.dumpMode().label() + " 存量=" + tank.amount());
            return;
        }
        if (args.length < 8) {
            sender.sendMessage("用法：/mek debug tank <x> <y> <z> <端口> <资源 id> <数量>");
            return;
        }
        String port = args[5];
        String id = args[6];
        long amount = parseLong(args[7], 1L);
        mekcore.resource.ResourceTank tank = controller.machine().tank(port).orElse(null);
        if (tank == null) {
            sender.sendMessage("这台机器没有端口 " + port + "；它有 " + controller.machine().portNames());
            return;
        }
        long accepted = tank.insert(new ResourceStack(tank.type(), id, amount), Transaction.EXECUTE);
        sender.sendMessage(port + " 灌入 " + accepted + "/" + amount + "：" + tank.describe());
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
        sender.sendMessage("从输出槽取走 " + total + " 个；剩余输出 "
                + controller.machine().items().count(-1, ItemSlotKind.OUTPUT, ItemMatcher.any()));
    }

    /** 没管道之前，这一条就是"电到货"：直接往机器的能量端口灌。 */
    private void power(CommandSender sender, MachineBlockEntityController controller, String[] args) {
        long joules = args.length > 5 ? parseLong(args[5], 100_000L) : 100_000L;
        long accepted = controller.machine().energyTank()
                .map(tank -> tank.insert(ResourceStack.energy(joules), Transaction.EXECUTE))
                .orElse(0L);
        sender.sendMessage("灌入 " + accepted + "/" + joules + " J；" + state(controller));
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
        sender.sendMessage("存档往返：存前" + (before.equals(changed) ? " == " : " != ") + "改动后"
                + "，改动后" + (changed.equals(after) ? " == " : " != ") + "读档后"
                + " → " + (before.equals(after) ? "一致" : "不一致！"));
        sender.sendMessage("  存前：" + before);
        sender.sendMessage("  读后：" + after);
    }

    private static String comparable(MachineBlockEntityController controller) {
        return state(controller).replaceAll(" 状态=\\S+", "");
    }

    private MachineBlockEntityController controller(CommandSender sender, String[] args, int from) {
        if (args.length < from + 3) {
            sender.sendMessage("要坐标：<x> <y> <z>");
            return null;
        }
        World world = world(sender);
        MachineBlockEntityController controller = this.plugin.machineAt(world, parseInt(args[from], 0),
                parseInt(args[from + 1], 0), parseInt(args[from + 2], 0));
        if (controller == null) {
            sender.sendMessage("那里没有机器方块实体（世界 " + world.getName() + "）");
            sender.sendMessage("  " + this.plugin.describePos(world, parseInt(args[from], 0),
                    parseInt(args[from + 1], 0), parseInt(args[from + 2], 0)));
        }
        return controller;
    }

    /** 玩家就用他自己的世界，控制台用主世界（本地测试服只有一个世界）。 */
    private static World world(CommandSender sender) {
        return sender instanceof Player player ? player.getWorld() : Bukkit.getWorlds().get(0);
    }

    private static String state(MachineBlockEntityController controller) {
        StringBuilder sb = new StringBuilder(controller.machine().describe());
        Machine machine = controller.machine();
        sb.append(" 安全=").append(machine.security().zhName())
                .append(" owner=").append(ownerText(machine))
                .append(" trusted=").append(machine.trusted());
        sb.append(" 容器");
        for (int index = 0; index < controller.inventory().getSize(); index++) {
            ItemStack stack = controller.inventory().getItem(index);
            if (net.kuzuanpa.cemekanism.gui.GuiText.isOverlay(stack)) {
                continue;
            }
            sb.append(' ').append(index).append('=')
                    .append(stack == null ? "空" : BukkitItems.idOf(stack) + "×" + stack.getAmount());
        }
        return sb.toString();
    }

    private static String ownerText(Machine machine) {
        return machine.owner() == null || machine.owner().isEmpty() ? "无" : machine.owner();
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
        player.sendMessage("这台机器不是你的（安全等级 " + machine.security().zhName() + "）");
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
