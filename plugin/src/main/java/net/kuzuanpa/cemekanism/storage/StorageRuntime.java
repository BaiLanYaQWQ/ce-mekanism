package net.kuzuanpa.cemekanism.storage;

import mekcore.machine.*;
import mekcore.spec.V10Tables;
import net.kuzuanpa.cemekanism.machine.MachineBlockEntityController;
import java.util.Map;
import java.util.WeakHashMap;

/** MAIN lane only. Owns platform contexts, never shared mutable state on blueprint logic instances. */
public final class StorageRuntime {
    private static final Map<Machine, MachineBlockEntityController> CONTROLLERS = new WeakHashMap<>();
    private StorageRuntime() { }
    public static MachineBlockEntityController of(Machine m) { return CONTROLLERS.get(m); }
    public static void attach(MachineBlockEntityController c) {
        CONTROLLERS.put(c.machine(), c);
        resize(c.machine());
        c.plugin().qio().attach(c);
    }
    public static void sync(MachineBlockEntityController c) {
        MiningChunkTickets.sync(c);
        if (c.blockId().equals("mek:qio_redstone_adapter") && !c.machine().redstoneActive()) c.plugin().qio().clearSignal(c);
    }
    public static void detach(MachineBlockEntityController c) {
        MiningChunkTickets.release(c);
        c.plugin().qio().detach(c); CONTROLLERS.remove(c.machine());
    }
    public static void defaults(Machine m, Direction facing) {
        String id = m.blueprint().id();
        if (id.endsWith("_energy_cube")) {
            for (Direction d : Direction.values()) m.defaultSide("energy", d, SideMode.BOTH);
        } else if (id.endsWith("_fluid_tank") || id.endsWith("_chemical_tank")) {
            for (Direction d : Direction.values()) m.defaultSide("stored", d, SideMode.BOTH);
        } else if (id.equals("mek:electric_pump")) {
            m.defaultSide("fluid", Direction.UP, SideMode.OUTPUT);
            for (Direction d : Direction.values()) m.defaultSide("energy", d, SideMode.INPUT);
        } else if (id.equals("mek:resistive_heater")) {
            for (Direction d : Direction.values()) if (d != Direction.UP && d != Direction.DOWN
                    && d != facing && d != facing.opposite()) m.defaultSide("energy", d, SideMode.INPUT);
        } else if (id.equals("mek:laser")) {
            m.defaultSide("energy", facing.opposite(), SideMode.INPUT);
        } else if (id.equals("mek:digital_miner")) {
            Direction left = switch (facing) {
                case NORTH -> Direction.EAST;
                case EAST -> Direction.SOUTH;
                case SOUTH -> Direction.WEST;
                case WEST -> Direction.NORTH;
                default -> Direction.EAST;
            };
            m.defaultItemSide(Direction.UP, SideMode.INPUT);
            m.defaultItemSide(facing.opposite(), SideMode.OUTPUT);
            m.defaultSide("energy", Direction.DOWN, SideMode.INPUT);
            m.defaultSide("energy", left, SideMode.INPUT);
            m.defaultSide("energy", left.opposite(), SideMode.INPUT);
        } else if (id.endsWith("_bin") || PersonalStorageLogic.isStorage(id)) {
            for (Direction d : Direction.values()) m.defaultItemSide(d, SideMode.BOTH);
            for (String port : m.portNames()) for (Direction d : Direction.values()) m.defaultSide(port, d, SideMode.INPUT);
        }
    }
    /** Manual water/lava bucket transfers share the real tank and its tick budget. */
    public static boolean bucket(MachineBlockEntityController c, org.bukkit.entity.Player player) {
        if (!c.blockId().endsWith("_fluid_tank")) return false;
        org.bukkit.inventory.ItemStack held = player.getInventory().getItemInMainHand();
        org.bukkit.Material type = held.getType();
        if (type != org.bukkit.Material.BUCKET && type != org.bukkit.Material.WATER_BUCKET && type != org.bukkit.Material.LAVA_BUCKET) return false;
        if (!c.machine().canAccess(player.getUniqueId().toString())) { player.sendMessage("无权操作此储罐"); return true; }
        var tank = c.machine().tank("stored").orElseThrow(); org.bukkit.Material result;
        if (type == org.bukkit.Material.BUCKET) {
            result = "minecraft:water".equals(tank.resourceId()) ? org.bukkit.Material.WATER_BUCKET
                    : "minecraft:lava".equals(tank.resourceId()) ? org.bukkit.Material.LAVA_BUCKET : null;
            if (result == null || tank.extract(1000, mekcore.resource.Transaction.SIMULATE) != 1000) { player.sendMessage("不足一桶或无对应桶物品"); return true; }
            tank.extract(1000, mekcore.resource.Transaction.EXECUTE);
        } else {
            var fluid = mekcore.resource.ResourceStack.fluid(type == org.bukkit.Material.WATER_BUCKET ? "minecraft:water" : "minecraft:lava", 1000);
            if (tank.insert(fluid, mekcore.resource.Transaction.SIMULATE) != 1000) { player.sendMessage("储罐已满、资源不符或本刻速率已满"); return true; }
            tank.insert(fluid, mekcore.resource.Transaction.EXECUTE); result = org.bukkit.Material.BUCKET;
        }
        held.setAmount(held.getAmount()-1);
        for (var remaining : player.getInventory().addItem(new org.bukkit.inventory.ItemStack(result)).values()) player.getWorld().dropItemNaturally(player.getLocation(), remaining);
        c.markChanged(); return true;
    }

    public static void reload() { for (Machine m : java.util.List.copyOf(CONTROLLERS.keySet())) resize(m); }
    public static void resize(Machine m) {
        String id = m.blueprint().id().substring(4);
        for (MachineTier tier : MachineTier.values()) {
            if (!id.startsWith(tier.name().toLowerCase() + "_")) continue;
            if (id.endsWith("_energy_cube")) {
                var p = V10Tables.energyCube(tier);
                m.resizeEnergyBuffer(p.capacityJ());
                m.energyTank().ifPresent(t -> t.withRateLimit(p.ratePerTick()).withTickClock(org.bukkit.Bukkit::getCurrentTick));
            } else if (id.endsWith("_fluid_tank") || id.endsWith("_chemical_tank")) {
                var p = id.endsWith("_fluid_tank") ? V10Tables.fluidTank(tier) : V10Tables.chemicalTank(tier);
                m.tank("stored").ifPresent(t -> { t.resize(p.capacity()); t.withRateLimit(p.outputRatePerTick()).withTickClock(org.bukkit.Bukkit::getCurrentTick); });
            }
        }
    }
}
