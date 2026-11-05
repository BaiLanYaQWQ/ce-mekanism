package net.kuzuanpa.cemekanism;

import mekcore.machine.Direction;
import mekcore.machine.Machine;
import mekcore.machine.SideMode;
import mekcore.logistics.PortMode;
import net.kuzuanpa.cemekanism.machine.MachineBlockEntityController;
import net.kuzuanpa.cemekanism.i18n.I18n;
import net.kuzuanpa.cemekanism.machine.logic.ResistiveHeaterLogic;
import net.kuzuanpa.cemekanism.multiblock.MultiblockBlocks;
import net.kuzuanpa.cemekanism.multiblock.MultiblockDef;
import net.kuzuanpa.cemekanism.multiblock.MultiblockHosts;
import net.kuzuanpa.cemekanism.multiblock.MultiblockPlacements;
import net.kuzuanpa.cemekanism.multiblock.logic.FissionReactorLogic;
import net.kuzuanpa.cemekanism.multiblock.logic.FusionReactorLogic;
import net.kuzuanpa.cemekanism.multiblock.logic.HohlraumItems;
import net.kuzuanpa.cemekanism.platform.BukkitItems;
import net.kuzuanpa.cemekanism.storage.BinLogic;
import net.kuzuanpa.cemekanism.storage.StoredItems;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Real showcase plants: only mineral bins, commissioning energy and ignition are seeded.
 * Every production transfer is performed by ordinary, persisted world pipes and machines. */
final class ShowcasePlants {
    static final int START_Z = 96;
    static final int ROW_DEPTH = 28;
    private final CeMekanismPlugin plugin;
    private final World world;
    private final int x, floor, z;
    private final Map<Pos, Device> devices = new LinkedHashMap<>();
    private final Map<Pos, String> pipes = new HashMap<>();
    private final Map<Pos, String> families = new HashMap<>();
    private final Map<Pos, String> reserved = new HashMap<>();
    private final Map<Pos, String> reservedFamilies = new HashMap<>();
    private final List<Route> routes = new ArrayList<>();
    private final Set<Pos> structures = new HashSet<>();
    private final Set<Pos> skyColumns = new HashSet<>();
    private final List<MultiblockHosts.Host> hosts = new ArrayList<>();

    private ShowcasePlants(CeMekanismPlugin plugin, World world, int x, int floor, int z) {
        this.plugin = plugin; this.world = world; this.x = x; this.floor = floor; this.z = z;
    }

    static int build(CeMekanismPlugin plugin, World world, int x, int floor, int z) {
        int count = 0;
        for (int row = 0; row < 3; row++) {
            ShowcasePlants plant = new ShowcasePlants(plugin, world, x, floor, z + START_Z + row * ROW_DEPTH);
            switch (row) {
                case 0 -> plant.fusion();
                case 1 -> plant.fission();
                case 2 -> plant.fiveTimes();
                default -> throw new AssertionError();
            }
            plant.finish();
            count += plant.hosts.size();
        }
        return count;
    }

    static List<String> report(CeMekanismPlugin plugin, World world, int x, int floor, int originZ) {
        List<String> lines = new ArrayList<>();
        lines.add(I18n.tr("showcase.plants.report_title"));
        for (int row = 0; row < 3; row++) {
            int baseZ = originZ + START_Z + row * ROW_DEPTH;
            lines.add(I18n.tr(switch (row) {
                case 0 -> "showcase.plants.fusion.title";
                case 1 -> "showcase.plants.fission.title";
                default -> "showcase.plants.five_times.title";
            }));
            int[][] samples = switch (row) {
                case 0 -> new int[][] {{4,4}, {10,4}, {26,13}, {80,15}};
                case 1 -> new int[][] {{4,4}, {34,4}, {50,16}, {56,16}, {80,16}};
                default -> new int[][] {{4,4}, {8,4}, {16,4}, {36,4}, {40,4}, {80,16}};
            };
            for (int[] point : samples) {
                MachineBlockEntityController controller = plugin.machineAtQuiet(world, x + point[0], floor + 2, baseZ + point[1]);
                if (controller == null) {
                    lines.add(I18n.tr("showcase.plants.missing_machine",
                            x + point[0], floor + 2, baseZ + point[1]));
                    continue;
                }
                Machine m = controller.machine();
                StringBuilder line = new StringBuilder(I18n.tr("showcase.plants.machine",
                        controller.blockId(), I18n.enumName(m.state(), m.state().zhName()),
                        m.operations(), m.lostOutputs()));
                if (m.blueprint().logic() instanceof BinLogic bin) {
                    line.append(I18n.tr("showcase.plants.bin", bin.store(m).total()));
                    for (String slot : List.of("in", "out")) {
                        var item = controller.slotContent(slot);
                        if (!StoredItems.empty(item)) line.append(" ").append(slot).append("=").append(BukkitItems.idOf(item)).append("×").append(item.getAmount());
                    }
                }
                for (String port : m.portNames()) {
                    var tank = m.tank(port).orElseThrow();
                    if (tank.amount() > 0) line.append(" ").append(port).append("=").append(tank.storedId()).append(":").append(tank.amount());
                }
                lines.add(line.toString());
            }
            if (row < 2) {
                MultiblockHosts.Host host = plugin.multiblocks().at(world, x + (row == 0 ? 42 : 44), floor + (row == 0 ? 5 : 1), baseZ + (row == 0 ? 5 : 3));
                if (host == null) { lines.add(I18n.tr("showcase.plants.reactor_missing")); continue; }
                lines.add(I18n.tr("showcase.plants.reactor",
                        I18n.structureName(host.def().id()), host.formed(),
                        host.instance().heatNode().temperatureK()));
                for (String port : host.def().ports()) {
                    var tank = host.instance().tank(port);
                    lines.add("    " + port + "=" + tank.storedId() + ":" + tank.amount() + "/" + tank.capacity());
                }
                if (host.instance().blueprint().logic() instanceof FusionReactorLogic logic)
                    lines.add(I18n.tr("showcase.plants.fusion_status",
                            logic.state(host.instance()).burning,
                            logic.state(host.instance()).plasmaTemperatureK));
                if (host.instance().blueprint().logic() instanceof FissionReactorLogic logic)
                    lines.add(I18n.tr("showcase.plants.fission_status",
                            logic.state(host.instance()).active, logic.state(host.instance()).damage));
            }
        }
        return lines;
    }

    private void fusion() {
        label(2, 0, I18n.tr("showcase.plants.line.fusion.title"),
                I18n.tr("showcase.plants.line.fusion.subtitle"));
        Device pump = pump(4, 4, true);
        Device separator = machine(10, 4, "electrolytic_separator", 0);
        Device oxygen = byproduct(16, 4);
        Device lithium = bin(4, 13, "mek:dust_lithium", 16_384);
        Device oxidizer = machine(10, 13, "chemical_oxidizer", 4);
        Device activator = machine(18, 13, "solar_neutron_activator", 0);
        Device activator2 = machine(18, 19, "solar_neutron_activator", 0);
        Device activator3 = machine(26, 19, "solar_neutron_activator", 0);
        skyColumns.addAll(List.of(activator.pos, activator2.pos, activator3.pos));
        Device tritium = machine(26, 13, "ultimate_chemical_tank", 0);
        MultiblockHosts.Host reactor = structure("fusion_reactor", 40, 3, 5, 5, 5);
        Device dPort = port(reactor, 0, 2, 2, "fusion_reactor_port", "INPUT", Direction.WEST);
        Device tPort = port(reactor, 2, 2, 4, "fusion_reactor_port", "INPUT", Direction.SOUTH);
        MultiblockHosts.Host boiler = structure("thermoelectric_boiler", 52, 3, 5, 4, 5);
        Device boilerWater = port(boiler, 2, 1, 4, "boiler_valve", "", Direction.SOUTH);
        Device boilerSteam = port(boiler, 4, 1, 2, "boiler_valve", "", Direction.EAST);
        Device reactorHeat = port(reactor, 4, 2, 2, "fusion_reactor_port", "HEAT", Direction.EAST);
        Device boilerHeat = port(boiler, 0, 2, 2, "boiler_valve", "HEAT", Direction.WEST);
        MultiblockHosts.Host turbine = turbine(68, 3);
        Device turbineSteam = port(turbine, 0, 1, 2, "turbine_valve", "", Direction.WEST);
        Device turbineWater = port(turbine, 2, 5, 4, "turbine_vent", "", Direction.SOUTH);
        Device turbinePower = port(turbine, 4, 1, 2, "turbine_valve", "", Direction.EAST);
        Device water = pump(52, 15, false);
        Device battery = battery(80, 15);
        load(80, 5, 20_000);
        formAll();
        link("heavy-water", "fluid", pump, "fluid", separator, "input");
        link("deuterium", "chemical", separator, "left", dPort, "deuterium");
        link("oxygen", "chemical", separator, "right", oxygen, "stored");
        link("lithium-items", "item", lithium, "items", oxidizer, "items");
        link("lithium", "chemical", oxidizer, "output", activator, "input");
        link("tritium", "chemical", activator, "output", tritium, "stored");
        link("lithium", "chemical", oxidizer, "output", activator2, "input");
        link("lithium", "chemical", oxidizer, "output", activator3, "input");
        link("tritium", "chemical", activator2, "output", tritium, "stored");
        link("tritium", "chemical", activator3, "output", tritium, "stored");
        link("tritium", "chemical", tritium, "stored", tPort, "tritium");
        link("water", "fluid", water, "fluid", boilerWater, "water");
        link("steam", "chemical", boilerSteam, "steam", turbineSteam, "steam");
        link("water", "fluid", turbineWater, "water", boilerWater, "water");
        route("heat", "heat", reactorHeat.pos.move(Direction.EAST), boilerHeat.pos.move(Direction.WEST));
        power(battery, turbinePower);
        FusionReactorLogic logic = (FusionReactorLogic) reactor.instance().blueprint().logic();
        logic.setInjectionRate(reactor.instance(), 2);
        var hohlraum = BukkitItems.create("mek:hohlraum");
        HohlraumItems.fill(hohlraum, FusionReactorLogic.FUSION_FUEL, 1_000);
        require(logic.insertHohlraum(reactor.instance(), hohlraum),
                I18n.tr("showcase.plants.error.hohlraum_insert"));
        // One commissioning laser pulse, equivalent to /mek reactor ... plasma; never refilled on tick.
        new org.bukkit.scheduler.BukkitRunnable() {
            private int attempts;
            @Override public void run() {
                if (!reactor.formed() || ++attempts > 300) { cancel(); return; }
                if (!FusionReactorLogic.DEUTERIUM.equals(reactor.instance().tank("deuterium").storedId())
                        || !FusionReactorLogic.TRITIUM.equals(reactor.instance().tank("tritium").storedId())
                        || reactor.instance().tank("deuterium").amount() < 100
                        || reactor.instance().tank("tritium").amount() < 100) return;
                logic.addLaserEnergy(reactor.instance(), 1_200_000_000L);
                reactor.markChanged();
                cancel();
            }
        }.runTaskTimer(plugin, 100L, 20L);
        reactor.markChanged();
        label(40, 12, I18n.tr("showcase.plants.fusion.ignition"),
                I18n.tr("showcase.plants.fusion.ignition_sub"));
        label(4, 22, I18n.tr("showcase.plants.fusion.maintenance"),
                I18n.tr("showcase.plants.fusion.maintenance_sub"));
    }

    private void fission() {
        label(2, 0, I18n.tr("showcase.plants.line.fission.title"),
                I18n.tr("showcase.plants.line.fission.subtitle"));
        Device uranium = bin(4, 4, "mek:raw_uranium", 65_536);
        Device enrichment = machine(8, 4, "enrichment_chamber", 4);
        Device smelter = machine(12, 4, "energized_smelter", 4);
        Device cake = machine(16, 4, "enrichment_chamber", 4);
        Device oxide = machine(20, 4, "chemical_oxidizer", 4);
        Device fluoride = machine(28, 4, "chemical_infuser", 0);
        Device centrifuge = machine(34, 4, "isotopic_centrifuge", 0);
        MultiblockHosts.Host reactor = structure("fission_reactor", 44, 3, 5, 4, 5);
        Device fuel = port(reactor, 0, 1, 2, "fission_reactor_port", "INPUT", Direction.WEST);
        Device cooling = port(reactor, 2, 1, 4, "fission_reactor_port", "INPUT", Direction.SOUTH);
        Device waste = port(reactor, 2, 1, 0, "fission_reactor_port", "OUTPUT_WASTE", Direction.NORTH);
        Device steam = port(reactor, 4, 1, 2, "fission_reactor_port", "OUTPUT_COOLANT", Direction.EAST);
        MultiblockHosts.Host turbine = turbine(62, 3);
        Device turbineSteam = port(turbine, 0, 1, 2, "turbine_valve", "", Direction.WEST);
        Device turbineWater = port(turbine, 2, 5, 4, "turbine_vent", "", Direction.SOUTH);
        Device turbinePower = port(turbine, 4, 1, 2, "turbine_valve", "", Direction.EAST);
        Device wasteCentrifuge = machine(50, 16, "isotopic_centrifuge", 0);
        Device plutonium = machine(56, 16, "ultimate_chemical_tank", 0);
        Device water = pump(44, 16, false);
        Device fluorite = bin(4, 18, "mek:fluorite_gem", 16_384);
        Device acidDissolution = machine(24, 13, "chemical_dissolution_chamber", 4);
        Device acid = acid(8, 10);
        Device battery = battery(80, 16);
        load(80, 5, 5_000);
        formAll();
        itemChain(uranium, enrichment, smelter, cake, oxide);
        link("uranium-oxide", "chemical", oxide, "output", fluoride, "right");
        link("fluorite", "item", fluorite, "items", acidDissolution, "items");
        link("sulfuric-acid", "chemical", acid, "center", acidDissolution, "chemical");
        link("hydrofluoric-acid", "chemical", acidDissolution, "output", fluoride, "left");
        link("uranium-hexafluoride", "chemical", fluoride, "center", centrifuge, "input");
        link("fissile-fuel", "chemical", centrifuge, "output", fuel, "fuel");
        link("nuclear-waste", "chemical", waste, "waste", wasteCentrifuge, "input");
        link("plutonium", "chemical", wasteCentrifuge, "output", plutonium, "stored");
        link("water", "fluid", water, "fluid", cooling, "water");
        link("steam", "chemical", steam, "steam", turbineSteam, "steam");
        link("water", "fluid", turbineWater, "water", cooling, "water");
        power(battery, turbinePower);
        FissionReactorLogic logic = (FissionReactorLogic) reactor.instance().blueprint().logic();
        logic.setRate(reactor.instance(), 0.05);
        logic.setActive(reactor.instance(), true);
        reactor.markChanged();
        label(44, 23, I18n.tr("showcase.plants.fission.burn"),
                I18n.tr("showcase.plants.fission.burn_sub"));
    }

    private void fiveTimes() {
        label(2, 0, I18n.tr("showcase.plants.line.five_times.title"),
                I18n.tr("showcase.plants.line.five_times.subtitle"));
        Device ore = bin(4, 4, "minecraft:iron_ore", 4_096);
        Device dissolution = machine(8, 4, "chemical_dissolution_chamber", 4);
        Device washer = machine(12, 4, "chemical_washer", 4);
        Device crystallizer = machine(16, 4, "chemical_crystallizer", 4);
        Device injection = machine(20, 4, "chemical_injection_chamber", 4);
        Device purification = machine(24, 4, "purification_chamber", 4);
        Device crusher = machine(28, 4, "crusher", 4);
        Device enrichment = machine(32, 4, "enrichment_chamber", 4);
        Device smelter = machine(36, 4, "energized_smelter", 4);
        Device output = bin(40, 4, null, 0);
        Device acid = acid(4, 12);
        Device washingWater = pump(34, 14, false);
        MultiblockHosts.Host evaporation = structure("thermal_evaporation_plant", 50, 12, 4, 5, 4);
        Device evapWater = port(evaporation, 0, 1, 1, "thermal_evaporation_valve", "", Direction.WEST);
        Device brine = port(evaporation, 3, 1, 1, "thermal_evaporation_valve", "", Direction.EAST);
        Device evapPump = pump(44, 16, false);
        Device heater = load(51, 17, 1_000);
        Device evapHeat = port(evaporation, 1, 1, 3, "thermal_evaporation_valve", "HEAT", Direction.SOUTH);
        Device brineSeparator = machine(60, 13, "electrolytic_separator", 0);
        Device sodium = byproduct(66, 18);
        Device hydrogenChloride = machine(34, 21, "chemical_infuser", 0);
        Device waterSeparator = machine(26, 14, "electrolytic_separator", 0);
        Device battery = battery(80, 16);
        formAll();
        link("ore", "item", ore, "items", dissolution, "items");
        link("dirty-iron", "chemical", dissolution, "output", washer, "chemical");
        link("clean-iron", "chemical", washer, "output", crystallizer, "chemical");
        itemChain(crystallizer, injection, purification, crusher, enrichment, smelter, output);
        link("sulfuric-acid", "chemical", acid, "center", dissolution, "chemical");
        link("water", "fluid", washingWater, "fluid", washer, "fluid");
        link("water", "fluid", washingWater, "fluid", waterSeparator, "input");
        link("hydrogen", "chemical", waterSeparator, "left", hydrogenChloride, "left");
        link("oxygen", "chemical", waterSeparator, "right", purification, "gas");
        link("water", "fluid", evapPump, "fluid", evapWater, "water");
        link("brine", "fluid", brine, "output", brineSeparator, "input");
        link("sodium", "chemical", brineSeparator, "left", sodium, "stored");
        link("chlorine", "chemical", brineSeparator, "right", hydrogenChloride, "right");
        link("hydrogen-chloride", "chemical", hydrogenChloride, "center", injection, "gas");
        route("evap-heat", "heat", heater.pos.move(Direction.NORTH), evapHeat.pos.move(Direction.SOUTH));
        power(battery, null);
        label(4, 24, I18n.tr("showcase.plants.five_times.ratio"),
                I18n.tr("showcase.plants.five_times.ratio_sub"));
    }

    /** Sulfur bin -> SO2 -> SO3 -> acid, with water pump, electrolysis and water vaporization. */
    private Device acid(int ax, int az) {
        Device sulfur = bin(ax, az, "mek:dust_sulfur", 16_384);
        Device oxidizer = machine(ax + 4, az, "chemical_oxidizer", 4);
        Device trioxide = machine(ax + 8, az, "chemical_infuser", 0);
        Device acid = machine(ax + 12, az + 6, "chemical_infuser", 0);
        Device rotary = machine(ax + 4, az + 6, "rotary_condensentrator", 0);
        Device water = pump(ax, az + 6, false);
        Device separator = machine(ax + 8, az + 6, "electrolytic_separator", 0);
        Device hydrogen = byproduct(ax + 16, az + 6);
        link("sulfur", "item", sulfur, "items", oxidizer, "items");
        link("sulfur-dioxide", "chemical", oxidizer, "output", trioxide, "right");
        link("water", "fluid", water, "fluid", rotary, "fluid");
        link("water", "fluid", water, "fluid", separator, "input");
        link("oxygen", "chemical", separator, "right", trioxide, "left");
        link("hydrogen", "chemical", separator, "left", hydrogen, "stored");
        link("sulfur-trioxide", "chemical", trioxide, "center", acid, "left");
        link("water-vapor", "chemical", rotary, "chemical", acid, "right");
        return acid;
    }

    private Device machine(int dx, int dz, String id, int speed) {
        Pos pos = new Pos(x + dx, floor + 2, z + dz);
        require(!devices.containsKey(pos), I18n.tr("showcase.plants.error.duplicate_machine", pos));
        MachineBlockEntityController controller = ShowcaseCommand.machine(plugin, world, pos.x, pos.y, pos.z, "mek:" + id);
        require(controller != null, I18n.tr("showcase.plants.error.missing_machine", id, pos));
        controller.claim("console");
        Machine m = controller.machine();
        for (Direction side : Direction.values()) {
            m.setItemSide(side, SideMode.NONE);
            for (String port : m.portNames()) m.setSide(port, side, SideMode.NONE);
        }
        m.installUpgrade("mek:energy_upgrade", 8);
        m.installUpgrade("mek:speed_upgrade", speed);
        m.installUpgrade("mek:chemical_upgrade", 8);
        controller.markChanged();
        Device device = new Device(pos, controller, null, null);
        devices.put(pos, device);
        return device;
    }

    private Device bin(int dx, int dz, String id, long amount) {
        Device device = machine(dx, dz, "ultimate_bin", 0);
        if (id != null) {
            var item = BukkitItems.create(id);
            require(item != null, I18n.tr("showcase.plants.error.missing_bin_input", id));
            BinLogic logic = (BinLogic) device.controller.machine().blueprint().logic();
            require(logic.store(device.controller.machine()).insert(StoredItems.key(item), amount, true) == amount,
                    I18n.tr("showcase.plants.error.bin_capacity", id));
            device.controller.markChanged();
        }
        label(dx, dz - 1, id == null ? I18n.tr("showcase.plants.finished_bin")
                : id.substring(id.indexOf(':') + 1), I18n.tr("showcase.plants.bin_label", amount));
        return device;
    }

    private Device byproduct(int dx, int dz) {
        Device tank = machine(dx, dz, "ultimate_chemical_tank", 0);
        tank.controller.machine().tank("stored").orElseThrow().setDumpMode(mekcore.resource.DumpMode.DUMPING_EXCESS);
        return tank;
    }

    private Device pump(int dx, int dz, boolean filtered) {
        Device pump = machine(dx, dz, "electric_pump", 8);
        if (filtered) require(pump.controller.machine().installUpgrade("mek:filter_upgrade", 1) == 1,
                I18n.tr("showcase.plants.pump_filter_missing"));
        // A 3x3 source pool with a solid floor regenerates the source consumed beneath the pump.
        for (int px = -1; px <= 1; px++) for (int pz = -1; pz <= 1; pz++) {
            world.getBlockAt(pump.pos.x + px, floor, pump.pos.z + pz).setType(Material.SEA_LANTERN, false);
            world.getBlockAt(pump.pos.x + px, floor + 1, pump.pos.z + pz).setType(Material.WATER, false);
        }
        pump.controller.markChanged();
        return pump;
    }

    private Device battery(int dx, int dz) {
        Device battery = machine(dx, dz, "ultimate_energy_cube", 0);
        var tank = battery.controller.machine().energyTank().orElseThrow();
        tank.set(null, tank.capacity());
        battery.controller.markChanged();
        label(dx, dz - 1, I18n.tr("showcase.plants.battery"),
                I18n.tr("showcase.plants.battery_sub"));
        return battery;
    }

    private Device load(int dx, int dz, long usage) {
        Device device = machine(dx, dz, "resistive_heater", 0);
        ((ResistiveHeaterLogic) device.controller.machine().blueprint().logic()).setUsage(device.controller.machine(), usage);
        device.controller.markChanged();
        return device;
    }

    private MultiblockHosts.Host structure(String id, int dx, int dz, int w, int h, int d) {
        MultiblockDef def = MultiblockBlocks.require("mek:" + id);
        int by = floor + 1;
        MultiblockPlacements.build(plugin, world, x + dx, by, z + dz, def, w, h, d);
        ShowcaseCommand.MultiblockSpec spec = new ShowcaseCommand.MultiblockSpec(def.id(), w, h, d, x + dx, z + dz);
        ShowcaseCommand.patchMultiblock(world, spec, by);
        MultiblockHosts.Host host = ShowcaseCommand.acquire(plugin, world, spec, def, by);
        require(host != null, I18n.tr("showcase.plants.error.missing_host", id));
        hosts.add(host);
        for (int xx = 0; xx < w; xx++) for (int yy = 0; yy < h; yy++) for (int zz = 0; zz < d; zz++)
            structures.add(new Pos(x + dx + xx, by + yy, z + dz + zz));
        return host;
    }

    private MultiblockHosts.Host turbine(int dx, int dz) {
        MultiblockHosts.Host host = structure("industrial_turbine", dx, dz, 5, 7, 5);
        // One rotor accepts two blades. Fill the upper chamber with condensers around one coil.
        host.setBlades(2);
        for (int yy = 3; yy <= 5; yy++) for (int xx = 1; xx <= 3; xx++) for (int zz = 1; zz <= 3; zz++) {
            if (xx == 2 && zz == 2 && yy == 3) continue;
            MultiblockPlacements.place(world, x + dx + xx, floor + 1 + yy, z + dz + zz, "mek:saturating_condenser");
        }
        return host;
    }

    private Device port(MultiblockHosts.Host host, int dx, int dy, int dz, String block, String mode, Direction outside) {
        Pos pos = new Pos(host.box().originX() + dx, host.box().originY() + dy, host.box().originZ() + dz);
        MultiblockPlacements.place(world, pos.x, pos.y, pos.z, "mek:" + block);
        Device device = new Device(pos, null, host, outside);
        devices.put(pos, device);
        // Mode selection needs the freshly placed role, so it is applied after refresh in formAll().
        device.mode = mode;
        return device;
    }

    private void formAll() {
        for (MultiblockHosts.Host host : hosts) {
            require(host.refresh() && host.formed(),
                    I18n.tr("showcase.plants.error.unformed", host.def().id(), host.extraIssues()));
        }
        for (Device device : devices.values()) if (device.host != null && !device.mode.isEmpty()) {
            for (int i = 0; i < 4 && !device.mode.equals(device.host.portMode(device.pos.x, device.pos.y, device.pos.z)); i++)
                device.host.cyclePortMode(device.pos.x, device.pos.y, device.pos.z);
            require(device.mode.equals(device.host.portMode(device.pos.x, device.pos.y, device.pos.z)),
                    I18n.tr("showcase.plants.error.port_mode"));
            device.host.markChanged();
        }
    }

    private void itemChain(Device... chain) {
        for (int i = 1; i < chain.length; i++) link("items-" + i + "-" + chain[0].pos, "item", chain[i - 1], "items", chain[i], "items");
    }

    private void power(Device battery, Device turbine) {
        // Resource routes already occupy their selected faces. Power takes a remaining free face.
        List<Device> consumers = new ArrayList<>(devices.values());
        for (Device device : consumers) {
            if (device == battery || device.controller == null || device.controller.machine().energyTank().isEmpty()) continue;
            link("power", "energy", battery, "energy", device, "energy");
        }
        if (turbine != null) link("power", "energy", turbine, "energy", battery, "energy");
        // Stores need BOTH on every connected face so the same battery can start and receive power.
        for (Direction side : Direction.values()) if (battery.controller.machine().side("energy", side) != SideMode.NONE)
            battery.controller.machine().setSide("energy", side, SideMode.BOTH);
    }

    private void link(String network, String family, Device from, String out, Device to, String in) {
        Pos a = from.socket(network, family, out, false);
        Pos b = to.socket(network, family, in, true);
        route(network, family, a, b);
    }

    /** Bounded deterministic routing, keeping unlike chemical/item networks physically separate.
     * No scheduled fake producers or transfer shortcuts are installed by the fixture. */
    private void route(String network, String family, Pos start, Pos end) {
        reserve(start, network, family);
        reserve(end, network, family);
        routes.add(new Route(network, family, start, end));
    }

    private void reserve(Pos pos, String network, String family) {
        require(!reserved.containsKey(pos) || (network.equals(reserved.get(pos))
                && family.equals(reservedFamilies.get(pos))),
                I18n.tr("showcase.plants.error.duplicate_port", pos));
        reserved.put(pos, network); reservedFamilies.put(pos, family);
    }

    private void layRoute(String network, String family, Pos start, Pos end) {
        Map<Pos, Pos> previous = new HashMap<>();
        ArrayDeque<Pos> queue = new ArrayDeque<>();
        require(available(start, network, family) && available(end, network, family),
                I18n.tr("showcase.plants.error.port_occupied", start, end));
        previous.put(start, start); queue.add(start);
        while (!queue.isEmpty() && !previous.containsKey(end)) {
            Pos here = queue.removeFirst();
            for (Direction side : WALK) {
                Pos next = here.move(side);
                if (next.x < x || next.x > x + 90 || next.z < z + 1 || next.z > z + 26
                        || next.y < floor + 1 || next.y > floor + 12 || previous.containsKey(next)
                        || structures.contains(next) || !available(next, network, family)) continue;
                previous.put(next, here); queue.addLast(next);
            }
        }
        require(previous.containsKey(end),
                I18n.tr("showcase.plants.error.route_failed", network, start, end));
        List<Pos> path = new ArrayList<>();
        for (Pos at = end; ; at = previous.get(at)) { path.add(at); if (at.equals(start)) break; }
        String block = switch (family) {
            case "item" -> "ultimate_logistical_transporter";
            case "fluid" -> "ultimate_mechanical_pipe";
            case "chemical" -> "ultimate_pressurized_tube";
            case "energy" -> "ultimate_universal_cable";
            case "heat" -> "ultimate_thermodynamic_conductor";
            default -> throw new IllegalArgumentException(family);
        };
        for (Pos at : path) {
            if (!pipes.containsKey(at)) {
                MultiblockPlacements.place(world, at.x, at.y, at.z, "mek:" + block);
                require(plugin.transmitterAt(world, at.x, at.y, at.z) != null,
                        I18n.tr("showcase.plants.error.pipe_place_failed", at));
                pipes.put(at, network); families.put(at, family);
            }
        }
    }

    private static final Direction[] WALK = {Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.UP, Direction.DOWN};
    private boolean available(Pos pos, String network, String family) {
        if (reserved.containsKey(pos) && (!network.equals(reserved.get(pos)) || !family.equals(reservedFamilies.get(pos)))) return false;
        if (pipes.containsKey(pos)) return network.equals(pipes.get(pos)) && family.equals(families.get(pos));
        if (!world.getBlockAt(pos.x, pos.y, pos.z).getType().isAir()) return false;
        for (Pos sky : skyColumns) if (pos.x == sky.x && pos.z == sky.z && pos.y > sky.y) return false;
        for (Direction side : WALK) {
            Pos neighbour = pos.move(side);
            if (family.equals(families.get(neighbour)) && !network.equals(pipes.get(neighbour))) return false;
            if (family.equals(reservedFamilies.get(neighbour)) && !network.equals(reserved.get(neighbour))) return false;
        }
        return true;
    }

    private void finish() {
        for (Route route : routes) layRoute(route.network, route.family, route.start, route.end);
        // A shared valve exposes several resources. Explicit push/pull prevents the brine
        // output from being stolen by the water inlet; close incidental faces beside casings.
        for (Pos at : pipes.keySet()) {
            if (families.get(at).equals("heat")) continue;
            var pipe = plugin.transmitterAt(world, at.x, at.y, at.z);
            for (Direction face : WALK) {
                Pos neighbour = at.move(face);
                if (!structures.contains(neighbour)) continue;
                Device device = devices.get(neighbour);
                String socket = device == null ? null : device.sockets.get(face.opposite());
                PortMode mode = socket == null ? PortMode.NONE
                        : socket.endsWith(":true") ? PortMode.PUSH : PortMode.PULL;
                for (int i = 0; i < 4 && pipe.port(face) != mode; i++) pipe.cyclePort(face);
                require(pipe.port(face) == mode,
                        I18n.tr("showcase.plants.error.pipe_mode_failed", at));
            }
        }
        for (Device device : devices.values()) if (device.controller != null) {
            device.controller.markChanged();
            plugin.logistics(world).refreshAround(device.pos.x, device.pos.y, device.pos.z);
        }
        for (MultiblockHosts.Host host : hosts) { host.refresh(); host.markChanged(); }
        plugin.logistics(world).wake();
    }

    private void label(int dx, int dz, String title, String detail) {
        List<String> lines = new ArrayList<>();
        lines.add(title);
        for (int at = 0; at < detail.length() && lines.size() < 4; at += 15)
            lines.add(detail.substring(at, Math.min(detail.length(), at + 15)));
        ShowcaseCommand.sign(world, x + dx, floor + 1, z + dz, lines);
    }
    private static void require(boolean condition, String message) { if (!condition) throw new IllegalStateException(message); }

    private final class Device {
        final Pos pos;
        final MachineBlockEntityController controller;
        final MultiblockHosts.Host host;
        final Direction outside;
        final Map<Direction, String> sockets = new LinkedHashMap<>();
        String mode = "";
        Device(Pos pos, MachineBlockEntityController controller, MultiblockHosts.Host host, Direction outside) {
            this.pos = pos; this.controller = controller; this.host = host; this.outside = outside;
        }
        Pos socket(String network, String family, String port, boolean input) {
            String key = family + ":" + network + ":" + port + ":" + input;
            for (var entry : sockets.entrySet()) if (entry.getValue().equals(key)) return pos.move(entry.getKey());
            Direction[] choices = outside == null ? (family.equals("energy")
                    ? new Direction[] {Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.UP, Direction.WEST, Direction.EAST}
                    : input ? new Direction[] {Direction.WEST, Direction.NORTH, Direction.SOUTH, Direction.UP, Direction.EAST, Direction.DOWN}
                    : new Direction[] {Direction.EAST, Direction.SOUTH, Direction.NORTH, Direction.UP, Direction.WEST, Direction.DOWN})
                    : new Direction[] {outside};
            for (Direction side : choices) {
                Pos socket = pos.move(side);
                if (sockets.containsKey(side) || structures.contains(socket) || !available(socket, network, family)) continue;
                sockets.put(side, key);
                reserve(socket, network, family);
                if (controller != null) {
                    if (port.equals("items")) controller.machine().setItemSide(side, input ? SideMode.INPUT : SideMode.OUTPUT);
                    else controller.machine().setSide(port, side, input ? SideMode.INPUT : SideMode.OUTPUT);
                }
                return socket;
            }
            throw new IllegalStateException(I18n.tr("showcase.plants.error.no_socket",
                    pos, port, network));
        }
    }

    private record Route(String network, String family, Pos start, Pos end) { }

    private record Pos(int x, int y, int z) {
        Pos move(Direction side) { return switch (side) {
            case EAST -> new Pos(x + 1, y, z); case WEST -> new Pos(x - 1, y, z);
            case SOUTH -> new Pos(x, y, z + 1); case NORTH -> new Pos(x, y, z - 1);
            case UP -> new Pos(x, y + 1, z); case DOWN -> new Pos(x, y - 1, z);
        }; }
    }
}
