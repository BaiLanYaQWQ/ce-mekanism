package net.kuzuanpa.cemekanism.nuclear;

import mekcore.multiblock.MultiblockInstance;
import mekcore.resource.ResourceStack;
import mekcore.resource.Transaction;
import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.kuzuanpa.cemekanism.machine.MachineBlockEntityController;
import net.kuzuanpa.cemekanism.multiblock.MultiblockBlocks;
import net.kuzuanpa.cemekanism.multiblock.MultiblockConfig;
import net.kuzuanpa.cemekanism.multiblock.MultiblockHosts;
import net.kuzuanpa.cemekanism.multiblock.MultiblockPlacements;
import net.kuzuanpa.cemekanism.multiblock.logic.FissionReactorLogic;
import net.kuzuanpa.cemekanism.multiblock.logic.FusionReactorLogic;
import net.kuzuanpa.cemekanism.multiblock.logic.HohlraumItems;
import net.kuzuanpa.cemekanism.multiblock.logic.SpsLogic;
import net.kuzuanpa.cemekanism.platform.BukkitItems;
import net.kuzuanpa.cemekanism.platform.CompoundDataTag;
import net.momirealms.craftengine.bukkit.api.BukkitAdaptor;
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.core.block.parser.BlockStateParser;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.libraries.nbt.CompoundTag;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** B6 实服验收：真实 CE 多方块、端口模式、反应堆状态往返与废料桶行为。 */
public final class B6Acceptance {

    private final CeMekanismPlugin plugin;
    private final World world;
    private final int x;
    private final int y;
    private final int z;
    private int checks;

    private B6Acceptance(CeMekanismPlugin plugin, World world, int x, int y, int z) {
        this.plugin = plugin;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static void run(CeMekanismPlugin plugin, CommandSender sender, String[] args) {
        if (sender instanceof org.bukkit.entity.Player && !sender.hasPermission("cemekanism.admin")) {
            sender.sendMessage("需要管理员权限");
            return;
        }
        int x = args.length > 1 ? Integer.parseInt(args[1]) : 5300;
        int y = args.length > 2 ? Integer.parseInt(args[2]) : 120;
        int z = args.length > 3 ? Integer.parseInt(args[3]) : 5000;
        World world = sender instanceof org.bukkit.entity.Player player
                ? player.getWorld() : Bukkit.getWorlds().getFirst();
        B6Acceptance test = new B6Acceptance(plugin, world, x, y, z);
        try {
            test.fission();
            sender.sendMessage("B6 裂变：" + test.checks + " 项通过");
            test.fusion();
            sender.sendMessage("B6 聚变与激光：" + test.checks + " 项通过");
            test.sps();
            sender.sendMessage("B6 SPS：" + test.checks + " 项通过");
            test.wasteBarrel();
            sender.sendMessage("B6 验收完成：" + test.checks + " 项通过，位置 "
                    + x + ',' + y + ',' + z);
        } catch (RuntimeException error) {
            sender.sendMessage("B6 已通过 " + test.checks + " 项后中止：" + error.getMessage());
            plugin.getLogger().log(java.util.logging.Level.SEVERE, "B6 acceptance", error);
        }
    }

    private void check(boolean condition, String label) {
        this.checks++;
        if (!condition) {
            throw new IllegalStateException("验收失败 " + label);
        }
    }

    private void fission() {
        var def = MultiblockBlocks.require("mek:fission_reactor");
        check(def.hMin() == 4, "裂变堆最小高度为 4");
        MultiblockPlacements.build(this.plugin, this.world, this.x, this.y, this.z,
                def, 3, 4, 3);
        MultiblockPlacements.place(this.world, this.x + 1, this.y + 1, this.z + 1,
                "mek:fission_fuel_assembly");
        MultiblockPlacements.place(this.world, this.x + 1, this.y + 2, this.z + 1,
                "mek:control_rod_assembly");
        MultiblockHosts.Host host = this.plugin.multiblocks().acquire(this.world,
                this.x, this.y, this.z, def, new int[] {3, 4, 3}, () -> { });
        check(host.refresh() && host.formed(), "3×4×3 裂变堆成型");
        MultiblockInstance instance = host.instance();
        check(instance.tank("fuel").capacity() == 8_000L, "单组件燃料容量 8000 mB");
        check(instance.tank("waste").capacity() == 8_000L, "单组件废料容量 8000 mB");
        check(instance.tank("water").capacity() == 200_000L, "水容量按内部体积");
        check(instance.heatNode().heatCapacity() == 36.0, "裂变热容按上游 J/K 口径换算");
        check(!host.def().ports().contains("energy"), "裂变堆不直接输出能量");

        List<mekcore.multiblock.StructurePosition> positions = instance.portPositions("fuel");
        check(!positions.isEmpty(), "裂变端口已定位");
        var position = positions.getFirst();
        int px = host.box().originX() + position.x();
        int py = host.box().originY() + position.y();
        int pz = host.box().originZ() + position.z();
        check(host.isPortAt("fuel", px, py, pz), "默认 INPUT 模式暴露燃料");
        check(!host.isPortAt("waste", px, py, pz), "默认模式不串出废料");
        check("OUTPUT_WASTE".equals(host.cyclePortMode(px, py, pz)), "端口循环到废料模式");
        check(host.isPortAt("waste", px, py, pz), "废料模式只暴露废料");

        FissionReactorLogic logic = (FissionReactorLogic) instance.blueprint().logic();
        instance.tank("fuel").insert(ResourceStack.chemical(FissionReactorLogic.FISSILE_FUEL, 8_000),
                Transaction.EXECUTE);
        instance.tank("water").insert(ResourceStack.fluid(MultiblockConfig.WATER, 100_000),
                Transaction.EXECUTE);
        logic.setRate(instance, 0.1);
        logic.setActive(instance, true);
        for (int i = 0; i < 401; i++) {
            instance.tick();
        }
        FissionReactorLogic.State state = logic.state(instance);
        check(state.burnRemaining > 0, "裂变燃耗保留小数余量");
        check(instance.tank("waste").amount() >= 1, "裂变产出核废料");
        check(instance.tank("steam").amount() > 0, "水冷产出蒸汽");
        double damage = state.damage;
        CompoundTag snapshot = new CompoundTag();
        host.save(new CompoundDataTag(snapshot));
        state.damage = 0;
        host.load(new CompoundDataTag(snapshot));
        check(logic.state(instance).damage == damage, "裂变损伤与燃耗状态往返");
    }

    private void fusion() {
        var def = MultiblockBlocks.require("mek:fusion_reactor");
        int ox = this.x + 12;
        MultiblockPlacements.build(this.plugin, this.world, ox, this.y, this.z,
                def, 5, 5, 5);
        // 西面中心的 any 格换成聚焦矩阵，激光口必须独立于普通能量线缆。
        MultiblockPlacements.place(this.world, ox, this.y + 2, this.z + 2,
                "mek:laser_focus_matrix");
        MultiblockHosts.Host host = this.plugin.multiblocks().acquire(this.world,
                ox + 2, this.y + 4, this.z + 2, def, new int[] {5, 5, 5}, () -> { });
        check(host.refresh() && host.formed(), "5×5×5 聚变堆成型");
        MultiblockInstance instance = host.instance();
        FusionReactorLogic logic = (FusionReactorLogic) instance.blueprint().logic();
        placeMachine(ox - 1, this.y + 2, this.z + 2, "mek:laser_amplifier", "east");
        MachineBlockEntityController laser = this.plugin.machineAtQuiet(this.world,
                ox - 1, this.y + 2, this.z + 2);
        check(laser != null && laser.machine().energyTank().orElseThrow().capacity() == 5_000_000L,
                "激光增幅器缓存 5 MJ");
        laser.machine().energyTank().orElseThrow().set(null, 5_000_000L);
        double beforeBeam = logic.state(instance).plasmaTemperatureK;
        laser.machine().tick();
        check(logic.state(instance).plasmaTemperatureK > beforeBeam,
                "增幅器朝向聚焦矩阵注入整束能量");
        check(laser.machine().energyTank().orElseThrow().amount() == 0,
                "激光发射时整束能量离缓存");
        logic.setInjectionRate(instance, 4);
        check(instance.tank("water").capacity() == 4_000_000L, "注入速率缩放水罐");
        check(instance.tank("steam").capacity() == 400_000_000L, "注入速率缩放蒸汽罐");

        ItemStack hohlraum = BukkitItems.create("mek:hohlraum");
        check(hohlraum != null, "黑体辐射腔物品已注册");
        HohlraumItems.fill(hohlraum, FusionReactorLogic.FUSION_FUEL,
                MultiblockConfig.hohlraumCapacity());
        check(HohlraumItems.amount(hohlraum) == 10, "黑体辐射腔保存 D-T 燃料");
        check(logic.insertHohlraum(instance, hohlraum), "黑体辐射腔装入控制器");
        instance.tank("deuterium").insert(ResourceStack.chemical(FusionReactorLogic.DEUTERIUM, 100),
                Transaction.EXECUTE);
        instance.tank("tritium").insert(ResourceStack.chemical(FusionReactorLogic.TRITIUM, 100),
                Transaction.EXECUTE);
        instance.tank("water").insert(ResourceStack.fluid(MultiblockConfig.WATER, 1_000_000),
                Transaction.EXECUTE);
        logic.addLaserEnergy(instance, 1_001_000_000L);
        instance.tick();
        FusionReactorLogic.State state = logic.state(instance);
        check(state.burning,
                "激光点火并开始燃烧 burning=" + state.burning
                        + " fuel=" + instance.tank("fuel").describe()
                        + " plasma=" + state.plasmaTemperatureK
                        + " hohlraum=" + (state.hohlraum == null ? "已消耗" : HohlraumItems.amount(state.hohlraum)));
        check(instance.heatNode().temperatureK() > MultiblockConfig.ambientK(), "聚变壳体吸热");
        double plasma = state.plasmaTemperatureK;
        CompoundTag snapshot = new CompoundTag();
        host.save(new CompoundDataTag(snapshot));
        state.plasmaTemperatureK = 0;
        host.load(new CompoundDataTag(snapshot));
        check(logic.state(instance).plasmaTemperatureK == plasma, "聚变等离子体温度往返");
        check(logic.state(instance).injectionRate == 4, "聚变注入速率往返");
        check(instance.tank("water").capacity() == 4_000_000L
                        && instance.tank("steam").capacity() == 400_000_000L,
                "聚变读档按恢复的注入速率重放容量");
    }

    private void sps() {
        var def = MultiblockBlocks.require("mek:sps");
        int ox = this.x + 24;
        MultiblockPlacements.build(this.plugin, this.world, ox, this.y, this.z,
                def, 7, 7, 7);
        MultiblockHosts.Host host = this.plugin.multiblocks().acquire(this.world,
                ox, this.y, this.z, def, new int[] {7, 7, 7}, () -> { });
        check(host.refresh() && host.formed(), "7×7×7 SPS 成型");
        MultiblockInstance instance = host.instance();
        SpsLogic logic = (SpsLogic) instance.blueprint().logic();
        instance.tank("polonium").insert(
                ResourceStack.chemical(SpsLogic.POLONIUM, 1_000), Transaction.EXECUTE);
        instance.tank("energy").insert(ResourceStack.energy(1_000_000_000L),
                Transaction.EXECUTE);
        instance.tick();
        check(instance.tank("antimatter").amount() == 1, "1000 mB 钋 + 1e9 J 产出 1 mB 反物质");
        check(instance.tank("polonium").amount() == 0, "SPS 按输入配比扣料");
        CompoundTag snapshot = new CompoundTag();
        host.save(new CompoundDataTag(snapshot));
        logic.state(instance).progress = 99;
        host.load(new CompoundDataTag(snapshot));
        check(logic.state(instance).progress == 0, "SPS 进度往返");
    }

    private void wasteBarrel() {
        int ox = this.x + 34;
        placeMachine(ox, this.y, this.z, "mek:radioactive_waste_barrel");
        MachineBlockEntityController controller = this.plugin.machineAtQuiet(this.world,
                ox, this.y, this.z);
        check(controller != null, "放射性废料桶实体已创建");
        var tank = controller.machine().tank("stored").orElseThrow();
        check(tank.capacity() == 512_000L, "废料桶容量 512000 mB");
        tank.insert(ResourceStack.chemical("mek:nuclear_waste", 100), Transaction.EXECUTE);
        for (int i = 0; i < MultiblockConfig.wasteBarrelProcessTicks(); i++) {
            controller.machine().tick();
        }
        check(tank.amount() == 99, "核废料每 20 刻衰减 1 mB");
        tank.set("mek:nuclear_waste", 10);
        new WasteBarrelListener(this.plugin).leak(this.world.getBlockAt(ox, this.y, this.z));
        check(tank.amount() == 0 && controller.suppressStorageDrops(),
                "废料桶破坏时清空内容并阻止快照回收");
        tank.set("mek:plutonium", 100);
        for (int i = 0; i < MultiblockConfig.wasteBarrelProcessTicks() * 2; i++) {
            controller.machine().tick();
        }
        check(tank.amount() == 100, "钚在废料桶黑名单中不衰减");
        CompoundTag snapshot = new CompoundTag();
        controller.saveCustomData(snapshot);
        tank.drain();
        controller.loadCustomData(snapshot);
        check(tank.amount() == 100, "废料桶存量往返");
    }

    private void placeMachine(int x, int y, int z, String id) {
        placeMachine(x, y, z, id, "north");
    }

    private void placeMachine(int x, int y, int z, String id, String facing) {
        this.world.getChunkAt(x >> 4, z >> 4).setForceLoaded(true);
        var parsed = BlockStateParser.deserialize(id + "[facing=" + facing + "]");
        if (parsed == null) {
            throw new IllegalArgumentException("未知方块 " + id);
        }
        MachineBlockEntityController existing = this.plugin.machineAtQuiet(this.world, x, y, z);
        if (existing != null) {
            existing.setSuppressStorageDrops(true);
        }
        CraftEngineBlocks.remove(this.world.getBlockAt(x, y, z));
        CraftEngineBlocks.place(new Location(this.world, x, y, z), parsed, false);
        BukkitAdaptor.adapt(this.world).storageWorld().setBlockStateAtIfLoaded(
                new BlockPos(x, y, z), parsed);
    }
}
