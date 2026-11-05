package net.kuzuanpa.cemekanism.nuclear;

import mekcore.multiblock.MultiblockInstance;
import mekcore.resource.ResourceStack;
import mekcore.resource.Transaction;
import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.kuzuanpa.cemekanism.i18n.I18n;
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
import java.util.logging.Level;

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
            I18n.send(sender, "acceptance.admin_required");
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
            I18n.send(sender, "acceptance.b6.fission", test.checks);
            test.fusion();
            I18n.send(sender, "acceptance.b6.fusion", test.checks);
            test.sps();
            I18n.send(sender, "acceptance.b6.sps", test.checks);
            test.wasteBarrel();
            I18n.send(sender, "acceptance.b6.complete", test.checks, x, y, z);
        } catch (RuntimeException error) {
            I18n.send(sender, "acceptance.b6.aborted", test.checks, error.getMessage());
            plugin.getLogger().log(Level.SEVERE, "B6 acceptance", error);
        }
    }

    private void check(boolean condition, String label) {
        this.checks++;
        if (!condition) {
            throw new IllegalStateException(I18n.tr("acceptance.failed", label));
        }
    }

    private void fission() {
        var def = MultiblockBlocks.require("mek:fission_reactor");
        check(def.hMin() == 4, I18n.tr("acceptance.b6.check.fission_min_height"));
        MultiblockPlacements.build(this.plugin, this.world, this.x, this.y, this.z,
                def, 3, 4, 3);
        MultiblockPlacements.place(this.world, this.x + 1, this.y + 1, this.z + 1,
                "mek:fission_fuel_assembly");
        MultiblockPlacements.place(this.world, this.x + 1, this.y + 2, this.z + 1,
                "mek:control_rod_assembly");
        MultiblockHosts.Host host = this.plugin.multiblocks().acquire(this.world,
                this.x, this.y, this.z, def, new int[] {3, 4, 3}, () -> { });
        check(host.refresh() && host.formed(), I18n.tr("acceptance.b6.check.fission_formed"));
        MultiblockInstance instance = host.instance();
        check(instance.tank("fuel").capacity() == 8_000L, I18n.tr("acceptance.b6.check.fuel_capacity"));
        check(instance.tank("waste").capacity() == 8_000L, I18n.tr("acceptance.b6.check.waste_capacity"));
        check(instance.tank("water").capacity() == 200_000L, I18n.tr("acceptance.b6.check.water_capacity"));
        check(instance.heatNode().heatCapacity() == 36.0, I18n.tr("acceptance.b6.check.heat_capacity"));
        check(!host.def().ports().contains("energy"), I18n.tr("acceptance.b6.check.no_energy_output"));

        List<mekcore.multiblock.StructurePosition> positions = instance.portPositions("fuel");
        check(!positions.isEmpty(), I18n.tr("acceptance.b6.check.port_found"));
        var position = positions.getFirst();
        int px = host.box().originX() + position.x();
        int py = host.box().originY() + position.y();
        int pz = host.box().originZ() + position.z();
        check(host.isPortAt("fuel", px, py, pz), I18n.tr("acceptance.b6.check.input_exposes_fuel"));
        check(!host.isPortAt("waste", px, py, pz), I18n.tr("acceptance.b6.check.default_no_waste"));
        check("OUTPUT_WASTE".equals(host.cyclePortMode(px, py, pz)),
                I18n.tr("acceptance.b6.check.port_waste_mode"));
        check(host.isPortAt("waste", px, py, pz), I18n.tr("acceptance.b6.check.waste_mode"));

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
        check(state.burnRemaining > 0, I18n.tr("acceptance.b6.check.burn_remainder"));
        check(instance.tank("waste").amount() >= 1, I18n.tr("acceptance.b6.check.waste_output"));
        check(instance.tank("steam").amount() > 0, I18n.tr("acceptance.b6.check.steam_output"));
        double damage = state.damage;
        CompoundTag snapshot = new CompoundTag();
        host.save(new CompoundDataTag(snapshot));
        state.damage = 0;
        host.load(new CompoundDataTag(snapshot));
        check(logic.state(instance).damage == damage, I18n.tr("acceptance.b6.check.fission_roundtrip"));
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
        check(host.refresh() && host.formed(), I18n.tr("acceptance.b6.check.fusion_formed"));
        MultiblockInstance instance = host.instance();
        FusionReactorLogic logic = (FusionReactorLogic) instance.blueprint().logic();
        placeMachine(ox - 1, this.y + 2, this.z + 2, "mek:laser_amplifier", "east");
        MachineBlockEntityController laser = this.plugin.machineAtQuiet(this.world,
                ox - 1, this.y + 2, this.z + 2);
        check(laser != null && laser.machine().energyTank().orElseThrow().capacity() == 5_000_000L,
                I18n.tr("acceptance.b6.check.laser_buffer"));
        laser.machine().energyTank().orElseThrow().set(null, 5_000_000L);
        double beforeBeam = logic.state(instance).plasmaTemperatureK;
        laser.machine().tick();
        check(logic.state(instance).plasmaTemperatureK > beforeBeam,
                I18n.tr("acceptance.b6.check.laser_focus"));
        check(laser.machine().energyTank().orElseThrow().amount() == 0,
                I18n.tr("acceptance.b6.check.laser_fire"));
        logic.setInjectionRate(instance, 4);
        check(instance.tank("water").capacity() == 4_000_000L,
                I18n.tr("acceptance.b6.check.water_scaling"));
        check(instance.tank("steam").capacity() == 400_000_000L,
                I18n.tr("acceptance.b6.check.steam_scaling"));

        ItemStack hohlraum = BukkitItems.create("mek:hohlraum");
        check(hohlraum != null, I18n.tr("acceptance.b6.check.hohlraum_registered"));
        HohlraumItems.fill(hohlraum, FusionReactorLogic.FUSION_FUEL,
                MultiblockConfig.hohlraumCapacity());
        check(HohlraumItems.amount(hohlraum) == 10, I18n.tr("acceptance.b6.check.hohlraum_fuel"));
        check(logic.insertHohlraum(instance, hohlraum), I18n.tr("acceptance.b6.check.hohlraum_insert"));
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
                I18n.tr("acceptance.b6.check.ignition", state.burning)
                        + " fuel=" + instance.tank("fuel").describe()
                        + " plasma=" + state.plasmaTemperatureK
                        + " " + I18n.tr("acceptance.b6.check.hohlraum_value",
                        state.hohlraum == null ? I18n.tr("acceptance.b6.hohlraum_consumed")
                                : HohlraumItems.amount(state.hohlraum)));
        check(instance.heatNode().temperatureK() > MultiblockConfig.ambientK(),
                I18n.tr("acceptance.b6.check.fusion_heat"));
        double plasma = state.plasmaTemperatureK;
        CompoundTag snapshot = new CompoundTag();
        host.save(new CompoundDataTag(snapshot));
        state.plasmaTemperatureK = 0;
        host.load(new CompoundDataTag(snapshot));
        check(logic.state(instance).plasmaTemperatureK == plasma,
                I18n.tr("acceptance.b6.check.plasma_roundtrip"));
        check(logic.state(instance).injectionRate == 4,
                I18n.tr("acceptance.b6.check.injection_roundtrip"));
        check(instance.tank("water").capacity() == 4_000_000L
                        && instance.tank("steam").capacity() == 400_000_000L,
                I18n.tr("acceptance.b6.check.fusion_restore_capacity"));
    }

    private void sps() {
        var def = MultiblockBlocks.require("mek:sps");
        int ox = this.x + 24;
        MultiblockPlacements.build(this.plugin, this.world, ox, this.y, this.z,
                def, 7, 7, 7);
        MultiblockHosts.Host host = this.plugin.multiblocks().acquire(this.world,
                ox, this.y, this.z, def, new int[] {7, 7, 7}, () -> { });
        check(host.refresh() && host.formed(), I18n.tr("acceptance.b6.check.sps_formed"));
        MultiblockInstance instance = host.instance();
        SpsLogic logic = (SpsLogic) instance.blueprint().logic();
        instance.tank("polonium").insert(
                ResourceStack.chemical(SpsLogic.POLONIUM, 1_000), Transaction.EXECUTE);
        instance.tank("energy").insert(ResourceStack.energy(1_000_000_000L),
                Transaction.EXECUTE);
        instance.tick();
        check(instance.tank("antimatter").amount() == 1, I18n.tr("acceptance.b6.check.sps_output"));
        check(instance.tank("polonium").amount() == 0, I18n.tr("acceptance.b6.check.sps_input"));
        CompoundTag snapshot = new CompoundTag();
        host.save(new CompoundDataTag(snapshot));
        logic.state(instance).progress = 99;
        host.load(new CompoundDataTag(snapshot));
        check(logic.state(instance).progress == 0, I18n.tr("acceptance.b6.check.sps_roundtrip"));
    }

    private void wasteBarrel() {
        int ox = this.x + 34;
        placeMachine(ox, this.y, this.z, "mek:radioactive_waste_barrel");
        MachineBlockEntityController controller = this.plugin.machineAtQuiet(this.world,
                ox, this.y, this.z);
        check(controller != null, I18n.tr("acceptance.b6.check.waste_entity"));
        var tank = controller.machine().tank("stored").orElseThrow();
        check(tank.capacity() == 512_000L, I18n.tr("acceptance.b6.check.waste_capacity"));
        tank.insert(ResourceStack.chemical("mek:nuclear_waste", 100), Transaction.EXECUTE);
        for (int i = 0; i < MultiblockConfig.wasteBarrelProcessTicks(); i++) {
            controller.machine().tick();
        }
        check(tank.amount() == 99, I18n.tr("acceptance.b6.check.waste_decay"));
        tank.set("mek:nuclear_waste", 10);
        new WasteBarrelListener(this.plugin).leak(this.world.getBlockAt(ox, this.y, this.z));
        check(tank.amount() == 0 && controller.suppressStorageDrops(),
                I18n.tr("acceptance.b6.check.waste_break"));
        tank.set("mek:plutonium", 100);
        for (int i = 0; i < MultiblockConfig.wasteBarrelProcessTicks() * 2; i++) {
            controller.machine().tick();
        }
        check(tank.amount() == 100, I18n.tr("acceptance.b6.check.plutonium_no_decay"));
        CompoundTag snapshot = new CompoundTag();
        controller.saveCustomData(snapshot);
        tank.drain();
        controller.loadCustomData(snapshot);
        check(tank.amount() == 100, I18n.tr("acceptance.b6.check.waste_roundtrip"));
    }

    private void placeMachine(int x, int y, int z, String id) {
        placeMachine(x, y, z, id, "north");
    }

    private void placeMachine(int x, int y, int z, String id, String facing) {
        this.world.getChunkAt(x >> 4, z >> 4).setForceLoaded(true);
        var parsed = BlockStateParser.deserialize(id + "[facing=" + facing + "]");
        if (parsed == null) {
            throw new IllegalArgumentException(I18n.tr("acceptance.unknown_block", id));
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
