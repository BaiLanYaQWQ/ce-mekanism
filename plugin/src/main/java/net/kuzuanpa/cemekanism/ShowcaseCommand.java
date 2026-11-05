package net.kuzuanpa.cemekanism;

import mekcore.chemical.Chemical;
import mekcore.chemical.Chemicals;
import mekcore.item.ItemRef;
import mekcore.machine.Direction;
import mekcore.machine.Machine;
import mekcore.machine.SideMode;
import mekcore.multiblock.MultiblockInstance;
import mekcore.resource.ResourceStack;
import mekcore.resource.ResourceTank;
import mekcore.resource.Transaction;
import net.kuzuanpa.cemekanism.generated.GeneratedContent;
import net.kuzuanpa.cemekanism.i18n.I18n;
import net.kuzuanpa.cemekanism.machine.MachineBlockEntityController;
import net.kuzuanpa.cemekanism.multiblock.MultiblockBlockEntityController;
import net.kuzuanpa.cemekanism.multiblock.MultiblockBlocks;
import net.kuzuanpa.cemekanism.multiblock.MultiblockDef;
import net.kuzuanpa.cemekanism.multiblock.MultiblockHosts;
import net.kuzuanpa.cemekanism.multiblock.MultiblockPlacements;
import net.kuzuanpa.cemekanism.multiblock.logic.FissionReactorLogic;
import net.kuzuanpa.cemekanism.multiblock.logic.FusionReactorLogic;
import net.kuzuanpa.cemekanism.platform.BukkitItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;

/**
 * Headless fixture for the complete currently registered content set.
 *
 * <p>The region is intentionally destructive only after the literal {@code confirm}. It is kept
 * in the normal test world so the resulting blocks, block entities, pipes and saved inventories
 * survive a normal server restart instead of living in a throw-away process.
 */
final class ShowcaseCommand {
    private static final int PAD_WIDTH = 96;
    private static final int PAD_DEPTH = 182;
    private static final int CLEAR_HEIGHT = 14;

    private ShowcaseCommand() {
    }

    static void run(CeMekanismPlugin plugin, CommandSender sender, String[] args) {
        if (sender instanceof Player player && !sender.hasPermission("cemekanism.admin")) {
            I18n.send(sender, "command.missing_permission", "cemekanism.admin");
            return;
        }
        if (args.length < 4) {
            I18n.send(sender, "showcase.usage");
            I18n.send(sender, "showcase.usage_detail", PAD_WIDTH, PAD_DEPTH, CLEAR_HEIGHT);
            return;
        }
        World world = sender instanceof Player player ? player.getWorld()
                : (Bukkit.getWorlds().isEmpty() ? null : Bukkit.getWorlds().getFirst());
        if (world == null) {
            I18n.send(sender, "showcase.no_world");
            return;
        }
        int x;
        int y;
        int z;
        try {
            x = Integer.parseInt(args[1]);
            y = Integer.parseInt(args[2]);
            z = Integer.parseInt(args[3]);
        } catch (NumberFormatException error) {
            I18n.send(sender, "showcase.integer_coordinates");
            return;
        }
        if (args.length > 4 && "status".equalsIgnoreCase(args[4])) {
            for (String line : ShowcasePlants.report(plugin, world, x + 2, y, z)) sender.sendMessage(line);
            return;
        }
        boolean confirmed = args.length > 4 && "confirm".equalsIgnoreCase(args[4]);
        if (!confirmed) {
            I18n.send(sender, "showcase.confirm", world.getName(), x, y, z,
                    PAD_WIDTH, PAD_DEPTH, CLEAR_HEIGHT);
            I18n.send(sender, "showcase.confirm_command", x, y, z);
            return;
        }

        try {
            BuildResult result = build(plugin, world, x, y, z);
            sender.sendMessage(result.summary());
        } catch (RuntimeException error) {
            plugin.getLogger().log(Level.SEVERE, I18n.tr("showcase.failed_log"), error);
            I18n.send(sender, "showcase.failed", error.getMessage());
        }
    }

    private static BuildResult build(CeMekanismPlugin plugin, World world, int originX, int originY, int originZ) {
        for (int cx = (originX - 4) >> 4; cx <= (originX + PAD_WIDTH + 4) >> 4; cx++) {
            for (int cz = (originZ - 6) >> 4; cz <= (originZ + PAD_DEPTH + 4) >> 4; cz++) {
                world.getChunkAt(cx, cz).setForceLoaded(true);
            }
        }
        clearAndFloor(world, originX, originY, originZ);
        world.setTime(1_000L);
        world.setStorm(false);
        world.setThundering(false);

        Hub hub = new Hub();
        Count machines = placeMachineGallery(plugin, world, originX + 2, originY + 1, originZ + 2);
        Count items = placeItemCatalog(plugin, world, originX + 38, originY + 1, originZ + 2);
        Count chemicals = placeChemicalCatalog(plugin, world, originX + 38, originY + 1, originZ + 40);
        Count transmitters = placeTransmitterGallery(plugin, world, originX + 2, originY + 1, originZ + 54);
        Count ore = placeOreLine(plugin, world, originX + 20, originY, originZ + 54);
        Count electrolysis = placeElectrolysisLine(plugin, world, originX + 20, originY, originZ + 60);
        Count fiveTimes = placeFiveTimesLine(plugin, world, originX + 20, originY, originZ + 66);
        Count qio = placeQioLine(plugin, world, originX + 20, originY, originZ + 72);
        Count generators = placeGeneratorBank(plugin, world, originX + 76, originY, originZ + 54);
        Count multiblocks = placeMultiblocks(plugin, world, originX + 40, originY + 1, originZ + 54);
        int plantStructures = ShowcasePlants.build(plugin, world, originX + 2, originY, originZ);
        hub.machines = machines.created();
        hub.items = items.created();
        hub.chemicals = chemicals.created();
        hub.transmitters = transmitters.created();
        hub.multiblocks = multiblocks.created() + plantStructures;
        hub.lines = 7;
        hub.problems = machines.missing() + items.missing() + chemicals.missing()
                + transmitters.missing() + ore.missing() + electrolysis.missing()
                + fiveTimes.missing() + qio.missing() + generators.missing()
                + multiblocks.missing();
        placeHub(world, originX, originY + 1, originZ - 3);

        world.setSpawnLocation(originX + 2, originY + 1, originZ - 3);
        for (int cx = (originX - 4) >> 4; cx <= (originX + PAD_WIDTH + 4) >> 4; cx++) {
            for (int cz = (originZ - 6) >> 4; cz <= (originZ + PAD_DEPTH + 4) >> 4; cz++) {
                world.getChunkAt(cx, cz).setForceLoaded(true);
            }
        }
        plugin.logistics(world).wake();
        return new BuildResult(originX, originY, originZ, hub.machines, hub.multiblocks,
                hub.transmitters, hub.items, hub.chemicals, hub.lines, hub.problems);
    }

    private static void clearAndFloor(World world, int originX, int originY, int originZ) {
        int minX = originX - 4;
        int maxX = originX + PAD_WIDTH;
        int minZ = originZ - 6;
        int maxZ = originZ + PAD_DEPTH;
        for (int y = originY + 1; y <= originY + CLEAR_HEIGHT; y++) {
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    clear(world.getBlockAt(x, y, z));
                }
            }
        }
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                boolean border = x == minX || x == maxX || z == minZ || z == maxZ;
                Block floor = world.getBlockAt(x, originY, z);
                clear(floor);
                floor.setType(border ? Material.BLACK_CONCRETE : Material.LIGHT_GRAY_CONCRETE, false);
            }
        }
    }

    private static void clear(Block block) {
        if (net.momirealms.craftengine.bukkit.api.CraftEngineBlocks.getCustomBlockState(block) != null) {
            net.momirealms.craftengine.bukkit.api.CraftEngineBlocks.remove(block);
        }
        block.setType(Material.AIR, false);
    }

    private static Count placeMachineGallery(CeMekanismPlugin plugin, World world, int x, int y, int z) {
        int placed = 0;
        int missing = 0;
        List<String> blocks = new ArrayList<>(GeneratedContent.BLOCK_TO_BLUEPRINT.keySet());
        Collections.sort(blocks);
        for (int i = 0; i < blocks.size(); i++) {
            String blockId = blocks.get(i);
            int mx = x + (i % 8) * 4;
            int mz = z + (i / 8) * 4;
            if (!MultiblockPlacements.place(world, mx, y, mz, blockId)) {
                if (CraftMissing.block(world, mx, y, mz, blockId)) {
                    missing++;
                }
            }
            placed++;
            String blueprintId = GeneratedContent.BLOCK_TO_BLUEPRINT.get(blockId);
            String name;
            try {
                name = I18n.machineName(plugin.core().registrars().machines().require(blueprintId).id());
            } catch (RuntimeException error) {
                name = blueprintId;
            }
            String tier = GeneratedContent.BLOCK_TO_FACTORY_TIER.getOrDefault(blockId, "");
            String suffix = tier.isEmpty() ? "" : " · " + tier.toLowerCase(Locale.ROOT);
            sign(world, mx, y, mz + 1, List.of(name + suffix, blockId));
        }
        groupSign(world, x, y, z - 1, I18n.tr("showcase.machines.title"),
                I18n.tr("showcase.machines.subtitle", blocks.size()));
        plugin.logistics(world).wake();
        return new Count(placed, missing);
    }

    private static Count placeItemCatalog(CeMekanismPlugin plugin, World world, int x, int y, int z) {
        Set<String> ids = new LinkedHashSet<>(GeneratedContent.ITEMS);
        List<ItemStack> items = new ArrayList<>();
        int missing = 0;
        for (String id : ids) {
            ItemStack item = BukkitItems.create(id);
            if (item == null) {
                missing++;
                continue;
            }
            item.setAmount(1);
            items.add(item);
        }
        int chestIndex = 0;
        int index = 0;
        while (index < items.size() && chestIndex < 12) {
            int cz = z + chestIndex * 3;
            world.getBlockAt(x, y, cz).setType(Material.CHEST, false);
            world.getBlockAt(x + 1, y, cz).setType(Material.CHEST, false);
            Chest chest = (Chest) world.getBlockAt(x, y, cz).getState();
            Inventory inventory = chest.getInventory();
            inventory.clear();
            for (int slot = 0; slot < inventory.getSize() && index < items.size(); slot++, index++) {
                inventory.setItem(slot, items.get(index));
            }
            chest.update(true, false);
            sign(world, x, y, cz + 1, List.of(I18n.tr("showcase.items.chest", chestIndex + 1),
                    I18n.tr("showcase.items.total", items.size())));
            chestIndex++;
        }
        groupSign(world, x, y, z - 1, I18n.tr("showcase.items.title"),
                I18n.tr("showcase.items.subtitle", ids.size(), items.size()));
        return new Count(items.size(), missing);
    }

    private static Count placeChemicalCatalog(CeMekanismPlugin plugin, World world, int x, int y, int z) {
        List<Chemical> chemicals = new ArrayList<>(Chemicals.registry().all());
        Collections.sort(chemicals, Comparator.comparing(Chemical::id));
        int placed = 0;
        for (int i = 0; i < chemicals.size(); i++) {
            Chemical chemical = chemicals.get(i);
            int cx = x + (i % 13) * 2;
            int cz = z + (i / 13) * 2;
            if (!MultiblockPlacements.place(world, cx, y, cz, "mek:basic_chemical_tank")) {
                continue;
            }
            MachineBlockEntityController controller = plugin.machineAtQuiet(world, cx, y, cz);
            if (controller != null) {
                controller.claim("console");
                controller.machine().tank("stored").ifPresent(tank ->
                        tank.insert(ResourceStack.chemical(chemical.id(), 1_000), Transaction.EXECUTE));
                placed++;
            }
        }
        groupSign(world, x, y, z - 1, I18n.tr("showcase.chemicals.title"),
                I18n.tr("showcase.chemicals.subtitle", chemicals.size()));
        return new Count(placed, chemicals.size() - placed);
    }

    private static Count placeTransmitterGallery(CeMekanismPlugin plugin, World world,
                                                 int x, int y, int z) {
        int placed = 0;
        String[] families = {"universal_cable", "mechanical_pipe", "pressurized_tube",
                "logistical_transporter", "thermodynamic_conductor"};
        for (int row = 0; row < families.length; row++) {
            for (int tier = 0; tier < 4; tier++) {
                String level = switch (tier) {
                    case 0 -> "basic";
                    case 1 -> "advanced";
                    case 2 -> "elite";
                    default -> "ultimate";
                };
                String id = "mek:" + level + '_' + families[row];
                if (MultiblockPlacements.place(world, x + tier * 3, y, z + row * 3, id)) {
                    placed++;
                }
            }
            sign(world, x, y, z + row * 3 - 1, List.of(families[row], I18n.tr("showcase.transmitters.range")));
        }
        plugin.logistics(world).wake();
        groupSign(world, x, y, z - 2, I18n.tr("showcase.transmitters.title"),
                I18n.tr("showcase.transmitters.subtitle"));
        return new Count(placed, 20 - placed);
    }

    private static Count placeOreLine(CeMekanismPlugin plugin, World world, int x, int floorY, int z) {
        int y = floorY + 1;
        MachineBlockEntityController a = machine(plugin, world, x, y, z, "mek:enrichment_chamber");
        MachineBlockEntityController b = machine(plugin, world, x + 2, y, z, "mek:energized_smelter");
        MachineBlockEntityController bin = machine(plugin, world, x + 4, y, z, "mek:advanced_bin");
        MultiblockPlacements.place(world, x + 1, y, z, "mek:basic_logistical_transporter");
        MultiblockPlacements.place(world, x + 3, y, z, "mek:basic_logistical_transporter");
        for (int cx = x; cx <= x + 2; cx++) {
            MultiblockPlacements.place(world, cx, floorY, z, "mek:ultimate_universal_cable");
        }
        if (a != null) {
            a.claim("console");
            a.machine().setItemSide(Direction.UP, SideMode.INPUT);
            a.machine().setItemSide(Direction.EAST, SideMode.OUTPUT);
            a.machine().setSide("energy", Direction.DOWN, SideMode.INPUT);
            a.machine().items().insert(ItemRef.of("minecraft:raw_iron", 30), Transaction.EXECUTE);
            a.markChanged();
        }
        if (b != null) {
            b.claim("console");
            b.machine().setItemSide(Direction.WEST, SideMode.INPUT);
            b.machine().setItemSide(Direction.EAST, SideMode.OUTPUT);
            b.machine().setSide("energy", Direction.DOWN, SideMode.INPUT);
            b.markChanged();
        }
        if (bin != null) {
            bin.claim("console");
            bin.machine().setItemSide(Direction.WEST, SideMode.INPUT);
            bin.markChanged();
        }
        vanilla(world, x, y + 1, z, Material.HOPPER, BlockFace.DOWN);
        fillContainer(world, x, y + 1, z, "minecraft:raw_iron", 30);
        chargeCableLine(plugin, world, x, floorY, z, x + 2, floorY, z, 20_000_000L);
        refresh(plugin, world, a);
        refresh(plugin, world, b);
        refresh(plugin, world, bin);
        groupSign(world, x, y, z - 1, I18n.tr("showcase.line.ore.title"),
                I18n.tr("showcase.line.ore.subtitle"));
        return new Count(6, 0);
    }

    private static Count placeElectrolysisLine(CeMekanismPlugin plugin, World world, int x, int floorY, int z) {
        int y = floorY + 1;
        MachineBlockEntityController tank = machine(plugin, world, x, y, z, "mek:basic_fluid_tank");
        MachineBlockEntityController separator = machine(plugin, world, x + 2, y, z,
                "mek:electrolytic_separator");
        MachineBlockEntityController gas = machine(plugin, world, x + 4, y, z, "mek:gas_burning_generator");
        MultiblockPlacements.place(world, x + 1, y, z, "mek:advanced_mechanical_pipe");
        MultiblockPlacements.place(world, x + 3, y, z, "mek:advanced_pressurized_tube");
        for (int cx = x + 2; cx <= x + 4; cx++) {
            MultiblockPlacements.place(world, cx, floorY, z, "mek:ultimate_universal_cable");
        }
        if (tank != null) {
            tank.claim("console");
            tank.machine().setSide("stored", Direction.EAST, SideMode.OUTPUT);
            tank.machine().tank("stored").ifPresent(t ->
                    t.insert(ResourceStack.fluid("minecraft:water", 16_000), Transaction.EXECUTE));
            tank.markChanged();
        }
        if (separator != null) {
            separator.claim("console");
            separator.machine().setSide("input", Direction.WEST, SideMode.INPUT);
            separator.machine().setSide("left", Direction.EAST, SideMode.OUTPUT);
            separator.machine().setSide("right", Direction.EAST, SideMode.OUTPUT);
            separator.machine().setSide("energy", Direction.DOWN, SideMode.INPUT);
            separator.markChanged();
        }
        if (gas != null) {
            gas.claim("console");
            gas.machine().setSide("fuel", Direction.WEST, SideMode.INPUT);
            gas.machine().setSide("energy", Direction.DOWN, SideMode.OUTPUT);
            gas.machine().tank("fuel").ifPresent(t ->
                    t.insert(ResourceStack.chemical("mek:hydrogen", 1_000), Transaction.EXECUTE));
            gas.markChanged();
        }
        refresh(plugin, world, tank);
        refresh(plugin, world, separator);
        refresh(plugin, world, gas);
        groupSign(world, x, y, z - 1, I18n.tr("showcase.line.electrolysis.title"),
                I18n.tr("showcase.line.electrolysis.subtitle"));
        return new Count(6, 0);
    }

    private static Count placeFiveTimesLine(CeMekanismPlugin plugin, World world, int x, int floorY, int z) {
        int y = floorY + 1;
        MachineBlockEntityController dissolution = machine(plugin, world, x, y, z,
                "mek:chemical_dissolution_chamber");
        MachineBlockEntityController washer = machine(plugin, world, x + 2, y, z, "mek:chemical_washer");
        MachineBlockEntityController crystallizer = machine(plugin, world, x + 4, y, z,
                "mek:chemical_crystallizer");
        MachineBlockEntityController bin = machine(plugin, world, x + 6, y, z, "mek:advanced_bin");
        MultiblockPlacements.place(world, x + 1, y, z, "mek:ultimate_pressurized_tube");
        MultiblockPlacements.place(world, x + 3, y, z, "mek:ultimate_pressurized_tube");
        MultiblockPlacements.place(world, x + 5, y, z, "mek:ultimate_logistical_transporter");
        for (int cx = x; cx <= x + 4; cx++) {
            MultiblockPlacements.place(world, cx, floorY, z, "mek:ultimate_universal_cable");
        }
        if (dissolution != null) {
            dissolution.claim("console");
            dissolution.machine().setSide("chemical", Direction.WEST, SideMode.INPUT);
            dissolution.machine().setSide("output", Direction.EAST, SideMode.OUTPUT);
            dissolution.machine().setSide("energy", Direction.DOWN, SideMode.INPUT);
            dissolution.machine().items().insert(ItemRef.of("minecraft:raw_iron", 9), Transaction.EXECUTE);
            dissolution.machine().tank("chemical").ifPresent(t ->
                    t.insert(ResourceStack.chemical("mek:sulfuric_acid", 9_000), Transaction.EXECUTE));
            dissolution.markChanged();
        }
        if (washer != null) {
            washer.claim("console");
            washer.machine().setSide("chemical", Direction.WEST, SideMode.INPUT);
            washer.machine().setSide("fluid", Direction.UP, SideMode.INPUT);
            washer.machine().setSide("output", Direction.EAST, SideMode.OUTPUT);
            washer.machine().setSide("energy", Direction.DOWN, SideMode.INPUT);
            washer.machine().tank("fluid").ifPresent(t ->
                    t.insert(ResourceStack.fluid("minecraft:water", 9_000), Transaction.EXECUTE));
            washer.markChanged();
        }
        if (crystallizer != null) {
            crystallizer.claim("console");
            crystallizer.machine().setSide("chemical", Direction.WEST, SideMode.INPUT);
            crystallizer.machine().setSide("energy", Direction.DOWN, SideMode.INPUT);
            crystallizer.machine().setItemSide(Direction.EAST, SideMode.OUTPUT);
            crystallizer.markChanged();
        }
        if (bin != null) {
            bin.claim("console");
            bin.machine().setItemSide(Direction.WEST, SideMode.INPUT);
            bin.markChanged();
        }
        chargeCableLine(plugin, world, x, floorY, z, x + 4, floorY, z, 50_000_000L);
        refresh(plugin, world, dissolution);
        refresh(plugin, world, washer);
        refresh(plugin, world, crystallizer);
        refresh(plugin, world, bin);
        groupSign(world, x, y, z - 1, I18n.tr("showcase.line.slurry.title"),
                I18n.tr("showcase.line.slurry.subtitle"));
        return new Count(8, 0);
    }

    private static Count placeQioLine(CeMekanismPlugin plugin, World world, int x, int floorY, int z) {
        int y = floorY + 1;
        MachineBlockEntityController array = machine(plugin, world, x, y, z, "mek:qio_drive_array");
        MachineBlockEntityController dashboard = machine(plugin, world, x + 2, y, z,
                "mek:qio_dashboard");
        MachineBlockEntityController importer = machine(plugin, world, x + 4, y, z, "mek:qio_importer");
        MachineBlockEntityController exporter = machine(plugin, world, x + 6, y, z, "mek:qio_exporter");
        MachineBlockEntityController redstone = machine(plugin, world, x + 8, y, z,
                "mek:qio_redstone_adapter");
        world.getBlockAt(x + 4, y, z + 1).setType(Material.CHEST, false);
        world.getBlockAt(x + 6, y, z + 1).setType(Material.CHEST, false);
        String frequency = plugin.qio().create("showcase", "console");
        for (MachineBlockEntityController controller : List.of(array, dashboard, importer, exporter, redstone)) {
            if (controller == null) {
                continue;
            }
            controller.claim("console");
            plugin.qio().join(controller, frequency, "console");
        }
        if (array != null) {
            array.putInSlot("drive0", BukkitItems.create("mek:qio_drive_supermassive"));
        }
        if (frequency != null) {
            plugin.qio().insert(frequency, "console", new ItemStack(Material.IRON_INGOT), 10_000L, true);
            plugin.qio().insert(frequency, "console", new ItemStack(Material.GOLD_INGOT), 256L, true);
        }
        groupSign(world, x, y, z - 1, I18n.tr("showcase.line.qio.title"),
                I18n.tr("showcase.line.qio.subtitle"));
        return new Count(7, 0);
    }

    private static Count placeGeneratorBank(CeMekanismPlugin plugin, World world, int x, int floorY, int z) {
        int y = floorY + 1;
        String[] generators = {
                "mek:heat_generator", "mek:solar_generator", "mek:advanced_solar_generator",
                "mek:wind_generator", "mek:bio_generator", "mek:gas_burning_generator"
        };
        int placed = 0;
        for (int i = 0; i < generators.length; i++) {
            int gz = z + i * 4;
            MachineBlockEntityController controller = machine(plugin, world, x, y, gz, generators[i]);
            if (controller == null) {
                continue;
            }
            placed++;
            controller.claim("console");
            Machine machine = controller.machine();
            machine.setSide("energy", Direction.DOWN, SideMode.OUTPUT);
            switch (generators[i]) {
                case "mek:heat_generator" -> {
                    machine.tank("lava").ifPresent(t ->
                            t.insert(ResourceStack.fluid("minecraft:lava", 1_000), Transaction.EXECUTE));
                    if (machine.heatNode() != null) {
                        machine.heatNode().setTemperatureK(1_000.0);
                    }
                }
                case "mek:bio_generator" ->
                        machine.items().insert(ItemRef.of("mek:bio_fuel", 64), Transaction.EXECUTE);
                case "mek:gas_burning_generator" ->
                        machine.tank("fuel").ifPresent(t ->
                                t.insert(ResourceStack.chemical("mek:hydrogen", 1_000), Transaction.EXECUTE));
                default -> {
                }
            }
            controller.markChanged();
            sign(world, x, y, gz + 1, List.of(generators[i].substring(4),
                    I18n.tr("showcase.generators.power")));
        }
        for (int i = 0; i <= generators.length * 4; i++) {
            MultiblockPlacements.place(world, x, floorY, z + i, "mek:ultimate_universal_cable");
        }
        MachineBlockEntityController cube = machine(plugin, world, x, y, z + generators.length * 4,
                "mek:ultimate_energy_cube");
        if (cube != null) {
            cube.claim("console");
            cube.machine().setSide("energy", Direction.DOWN, SideMode.INPUT);
            cube.markChanged();
        sign(world, x, y, z + generators.length * 4 + 1,
                List.of(I18n.tr("showcase.generators.cube"), I18n.tr("showcase.generators.bus")));
        }
        groupSign(world, x, y, z - 1, I18n.tr("showcase.generators.title"),
                I18n.tr("showcase.generators.subtitle"));
        plugin.logistics(world).wake();
        return new Count(placed + 1, 0);
    }

    private static Count placeMultiblocks(CeMekanismPlugin plugin, World world, int x, int y, int z) {
        List<MultiblockSpec> layouts = List.of(
                new MultiblockSpec("mek:thermoelectric_boiler", 5, 4, 5, x, z),
                new MultiblockSpec("mek:industrial_turbine", 5, 7, 5, x + 12, z),
                new MultiblockSpec("mek:thermal_evaporation_plant", 4, 5, 4, x + 24, z),
                new MultiblockSpec("mek:dynamic_tank", 5, 5, 5, x, z + 12),
                new MultiblockSpec("mek:induction_matrix", 5, 5, 5, x + 12, z + 12),
                new MultiblockSpec("mek:fission_reactor", 5, 4, 5, x + 24, z + 12),
                new MultiblockSpec("mek:fusion_reactor", 5, 5, 5, x, z + 24),
                new MultiblockSpec("mek:sps", 7, 7, 7, x + 12, z + 24),
                new MultiblockSpec("mek:teleporter", 3, 4, 1, x + 24, z + 24)
        );
        int formed = 0;
        int missing = 0;
        for (MultiblockSpec layout : layouts) {
            MultiblockDef def = MultiblockBlocks.require(layout.id());
            MultiblockPlacements.build(plugin, world, layout.x(), y, layout.z(), def,
                    layout.width(), layout.height(), layout.depth());
            patchMultiblock(world, layout, y);
            MultiblockHosts.Host host = acquire(plugin, world, layout, def, y);
            if (host != null && host.refresh() && host.formed()) {
                formed++;
                seedMultiblock(world, host);
                attachPortSamples(plugin, world, host);
            } else {
                missing++;
            }
            List<String> lines = new ArrayList<>();
            lines.add(I18n.structureName(def.id()));
            lines.add(layout.width() + "×" + layout.height() + "×" + layout.depth());
            if (host != null && !host.extraIssues().isEmpty()) {
                lines.addAll(host.extraIssues().subList(0, Math.min(2, host.extraIssues().size())));
            } else {
                lines.add(I18n.tr(host != null && host.refresh()
                        ? "multiblock.formed" : "multiblock.unformed"));
            }
            sign(world, layout.x(), y, layout.z() - 1, lines);
        }
        sign(world, x, y, z - 2, List.of(I18n.tr("showcase.multiblocks.title"),
                I18n.tr("showcase.multiblocks.subtitle")));
        return new Count(formed, missing);
    }

    static void patchMultiblock(World world, MultiblockSpec spec, int y) {
        int x = spec.x();
        int z = spec.z();
        switch (spec.id()) {
            case "mek:thermoelectric_boiler" -> {
                for (int dx = 1; dx <= 3; dx++) {
                    for (int dz = 1; dz <= 3; dz++) {
                        MultiblockPlacements.place(world, x + dx, y + 2, z + dz,
                                "mek:pressure_disperser");
                    }
                }
                MultiblockPlacements.place(world, x + 1, y + 1, z + 1, "mek:superheating_element");
            }
            case "mek:industrial_turbine" -> {
                MultiblockPlacements.place(world, x + 2, y + 1, z + 2, "mek:turbine_rotor");
                MultiblockPlacements.place(world, x + 2, y + 2, z + 2, "mek:rotational_complex");
                MultiblockPlacements.place(world, x + 2, y + 3, z + 2, "mek:electromagnetic_coil");
                MultiblockPlacements.place(world, x + 1, y + 3, z + 1, "mek:saturating_condenser");
                for (int dx = 1; dx <= 3; dx++) {
                    for (int dz = 1; dz <= 3; dz++) {
                        if (dx != 2 || dz != 2) {
                            MultiblockPlacements.place(world, x + dx, y + 2, z + dz,
                                    "mek:pressure_disperser");
                        }
                    }
                }
            }
            case "mek:induction_matrix" -> {
                MultiblockPlacements.place(world, x + 1, y + 1, z + 1, "mek:ultimate_induction_cell");
                MultiblockPlacements.place(world, x + 2, y + 1, z + 1, "mek:ultimate_induction_cell");
                MultiblockPlacements.place(world, x + 3, y + 1, z + 1, "mek:elite_induction_cell");
                MultiblockPlacements.place(world, x + 1, y + 1, z + 2, "mek:ultimate_induction_provider");
                MultiblockPlacements.place(world, x + 2, y + 1, z + 2, "mek:elite_induction_provider");
                MultiblockPlacements.place(world, x + 3, y + 1, z + 2, "mek:advanced_induction_provider");
            }
            case "mek:fission_reactor" -> {
                for (int dx = 1; dx <= 3; dx++) {
                    for (int dz = 1; dz <= 3; dz++) {
                        MultiblockPlacements.place(world, x + dx, y + 1, z + dz,
                                "mek:fission_fuel_assembly");
                        MultiblockPlacements.place(world, x + dx, y + 2, z + dz,
                                "mek:control_rod_assembly");
                    }
                }
            }
            default -> {
            }
        }
    }

    static MultiblockHosts.Host acquire(CeMekanismPlugin plugin, World world,
                                                MultiblockSpec layout, MultiblockDef def, int y) {
        int[] offset = def.buildAnchor(layout.width(), layout.height(), layout.depth());
        int ax = layout.x() + offset[0];
        int ay = y + offset[1];
        int az = layout.z() + offset[2];
        MultiblockBlockEntityController anchor = plugin.multiblockAt(world, ax, ay, az);
        MultiblockHosts.Host host = anchor == null ? null : anchor.form();
        if (host == null) {
            host = plugin.multiblocks().acquire(world, ax, ay, az, def,
                    new int[] {layout.width(), layout.height(), layout.depth()}, () -> { });
        }
        return host;
    }

    private static void seedMultiblock(World world, MultiblockHosts.Host host) {
        MultiblockInstance instance = host.instance();
        switch (host.def().family()) {
            case "boiler" -> {
                instance.tank("water").insert(ResourceStack.fluid("minecraft:water", 100_000),
                        Transaction.EXECUTE);
                if (instance.heatNode() != null) {
                    instance.heatNode().setTemperatureK(900.0);
                }
            }
            case "turbine" -> {
                host.setBlades(64);
                instance.tank("steam").insert(ResourceStack.chemical("mek:steam", 1_000_000),
                        Transaction.EXECUTE);
                instance.tank("water").insert(ResourceStack.fluid("minecraft:water", 64_000),
                        Transaction.EXECUTE);
            }
            case "evaporation" -> {
                instance.tank("water").insert(ResourceStack.fluid("mek:brine", 64_000),
                        Transaction.EXECUTE);
                if (instance.heatNode() != null) {
                    instance.heatNode().setTemperatureK(650.0);
                }
            }
            case "tank" -> {
                instance.tank("fluid").insert(ResourceStack.fluid("minecraft:water", 32_000),
                        Transaction.EXECUTE);
                instance.tank("chemical").insert(ResourceStack.chemical("mek:hydrogen", 32_000),
                        Transaction.EXECUTE);
            }
            case "matrix" -> instance.tank("energy").insert(
                    ResourceStack.energy(8_000_000_000L), Transaction.EXECUTE);
            case "fission_reactor" -> {
                instance.tank("fuel").insert(ResourceStack.chemical("mek:fissile_fuel", 72_000),
                        Transaction.EXECUTE);
                instance.tank("water").insert(ResourceStack.fluid("minecraft:water", 200_000),
                        Transaction.EXECUTE);
                if (instance.blueprint().logic() instanceof FissionReactorLogic logic) {
                    logic.setRate(instance, 0.1);
                    // Keep the showcase reactor cold until a player explicitly starts it. Starting
                    // an unattended reactor without a water line eventually melts the fixture down.
                    logic.setActive(instance, false);
                }
            }
            case "fusion_reactor" -> {
                instance.tank("deuterium").insert(ResourceStack.chemical("mek:deuterium", 1_000),
                        Transaction.EXECUTE);
                instance.tank("tritium").insert(ResourceStack.chemical("mek:tritium", 1_000),
                        Transaction.EXECUTE);
                instance.tank("fuel").insert(ResourceStack.chemical("mek:fusion_fuel", 1_000),
                        Transaction.EXECUTE);
                instance.tank("water").insert(ResourceStack.fluid("minecraft:water", 2_000_000),
                        Transaction.EXECUTE);
                if (instance.blueprint().logic() instanceof FusionReactorLogic logic) {
                    logic.setInjectionRate(instance, 4);
                }
            }
            case "sps" -> {
                instance.tank("polonium").insert(ResourceStack.chemical("mek:polonium", 2_000),
                        Transaction.EXECUTE);
                instance.tank("energy").insert(ResourceStack.energy(1_000_000_000L),
                        Transaction.EXECUTE);
            }
            default -> {
            }
        }
        host.markChanged();
    }

    private static void attachPortSamples(CeMekanismPlugin plugin, World world, MultiblockHosts.Host host) {
        for (String port : host.def().ports()) {
            List<mekcore.multiblock.StructurePosition> positions = host.instance().portPositions(port);
            if (positions.isEmpty()) {
                continue;
            }
            mekcore.multiblock.StructurePosition position = positions.getFirst();
            int px = host.box().originX() + position.x();
            int py = host.box().originY() + position.y();
            int pz = host.box().originZ() + position.z();
            int[] outside = outside(host, px, py, pz);
            if (outside == null) {
                continue;
            }
            String id = switch (host.instance().tank(port).type().id()) {
                case "mek:energy" -> "mek:ultimate_universal_cable";
                case "mek:chemical" -> "mek:ultimate_pressurized_tube";
                default -> "mek:ultimate_mechanical_pipe";
            };
            MultiblockPlacements.place(world, outside[0], outside[1], outside[2], id);
            plugin.logistics(world).refreshAround(px, py, pz);
        }
    }

    private static int[] outside(MultiblockHosts.Host host, int x, int y, int z) {
        int ox = host.box().originX();
        int oy = host.box().originY();
        int oz = host.box().originZ();
        int sx = host.box().sizeX();
        int sy = host.box().sizeY();
        int sz = host.box().sizeZ();
        List<int[]> candidates = new ArrayList<>();
        if (x == ox) candidates.add(new int[] {x - 1, y, z});
        if (x == ox + sx - 1) candidates.add(new int[] {x + 1, y, z});
        if (y == oy) candidates.add(new int[] {x, y - 1, z});
        if (y == oy + sy - 1) candidates.add(new int[] {x, y + 1, z});
        if (z == oz) candidates.add(new int[] {x, y, z - 1});
        if (z == oz + sz - 1) candidates.add(new int[] {x, y, z + 1});
        for (int[] candidate : candidates) {
            Block block = host.world().getBlockAt(candidate[0], candidate[1], candidate[2]);
            if (block.getType().isAir() || block.getType() == Material.LIGHT_GRAY_CONCRETE
                    || block.getType() == Material.BLACK_CONCRETE) {
                return candidate;
            }
        }
        return candidates.isEmpty() ? null : candidates.getFirst();
    }

    private static void placeHub(World world, int x, int y, int z) {
        groupSign(world, x + 1, y, z, I18n.tr("showcase.hub.title"), I18n.tr("showcase.hub.subtitle"));
        groupSign(world, x + 3, y, z, I18n.tr("showcase.hub.machines"), I18n.tr("showcase.hub.machines_sub"));
        groupSign(world, x + 5, y, z, I18n.tr("showcase.hub.lines"), I18n.tr("showcase.hub.lines_sub"));
        groupSign(world, x + 7, y, z, I18n.tr("showcase.hub.tips"), I18n.tr("showcase.hub.tips_sub"));
        groupSign(world, x + 9, y, z, I18n.tr("showcase.hub.south"), I18n.tr("showcase.hub.south_sub"));
        world.getBlockAt(x + 1, y + 1, z).setType(Material.SEA_LANTERN, false);
    }

    static MachineBlockEntityController machine(CeMekanismPlugin plugin, World world,
                                                        int x, int y, int z, String id) {
        if (!MultiblockPlacements.place(world, x, y, z, id)) {
            MachineBlockEntityController existing = plugin.machineAtQuiet(world, x, y, z);
            if (existing != null) {
                return existing;
            }
        }
        return plugin.machineAtQuiet(world, x, y, z);
    }

    private static void refresh(CeMekanismPlugin plugin, World world, MachineBlockEntityController controller) {
        if (controller != null) {
            plugin.logistics(world).refreshAround(controller.x(), controller.y(), controller.z());
        }
    }

    private static void chargeCableLine(CeMekanismPlugin plugin, World world,
                                        int x1, int y1, int z1, int x2, int y2, int z2, long amount) {
        var controller = plugin.transmitterAt(world, x1, y1, z1);
        if (controller == null && x1 != x2) {
            controller = plugin.transmitterAt(world, x2, y2, z2);
        }
        if (controller != null && controller.node() != null) {
            controller.node().buffer().insert(ResourceStack.energy(amount), Transaction.EXECUTE);
            plugin.logistics(world).wake();
        }
    }

    private static void vanilla(World world, int x, int y, int z, Material material, BlockFace facing) {
        Block block = world.getBlockAt(x, y, z);
        block.setType(material, false);
        if (facing != null && block.getBlockData() instanceof Directional directional) {
            directional.setFacing(facing);
            block.setBlockData(directional, false);
        }
    }

    private static void fillContainer(World world, int x, int y, int z, String id, int amount) {
        Block block = world.getBlockAt(x, y, z);
        if (!(block.getState() instanceof Container container)) {
            return;
        }
        ItemStack stack = BukkitItems.create(id);
        if (stack == null) {
            return;
        }
        stack.setAmount(Math.min(amount, stack.getMaxStackSize()));
        Inventory inventory = container.getSnapshotInventory();
        inventory.clear();
        inventory.setItem(0, stack);
        container.update(true, false);
    }

    static void sign(World world, int x, int y, int z, List<String> lines) {
        Block block = world.getBlockAt(x, y, z);
        if (!(block.getBlockData() instanceof Rotatable)) {
            block.setType(Material.OAK_SIGN, false);
        }
        if (block.getBlockData() instanceof Rotatable rotatable) {
            rotatable.setRotation(BlockFace.SOUTH);
            block.setBlockData(rotatable, false);
        }
        if (!(block.getState() instanceof Sign sign)) {
            return;
        }
        for (int i = 0; i < 4; i++) {
            String line = i < lines.size() ? lines.get(i) : "";
            sign.setLine(i, line.length() > 15 ? line.substring(0, 15) : line);
        }
        sign.update(true, false);
    }

    private static void groupSign(World world, int x, int y, int z, String first, String second) {
        sign(world, x, y, z, List.of(first, second));
    }

    record MultiblockSpec(String id, int width, int height, int depth, int x, int z) {
    }

    private record Count(int created, int missing) {
    }

    private static final class Hub {
        private int machines;
        private int multiblocks;
        private int transmitters;
        private int items;
        private int chemicals;
        private int lines;
        private int problems;


    }

    private record BuildResult(int x, int y, int z, int machines, int multiblocks,
                               int transmitters, int items, int chemicals, int lines, int problems) {
        private String summary() {
            return I18n.tr("showcase.summary", x, y, z, machines,
                    GeneratedContent.BLOCK_TO_BLUEPRINT.size(), multiblocks, transmitters,
                    items, chemicals, lines, problems);
        }
    }

    /**
     * Distinguishes "the target was already this block" from "the CE id did not exist".
     * Kept local so the showcase does not depend on a public helper in the placement API.
     */
    private static final class CraftMissing {
        private CraftMissing() {
        }

        private static boolean block(World world, int x, int y, int z, String id) {
            return net.momirealms.craftengine.bukkit.api.CraftEngineBlocks.byId(
                    net.momirealms.craftengine.core.util.Key.of(id)) == null;
        }
    }
}
