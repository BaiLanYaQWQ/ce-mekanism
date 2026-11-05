package net.kuzuanpa.cemekanism.multiblock;

import mekcore.multiblock.MultiblockInstance;
import java.util.Set;

/** The multiblock owns resource identity; player valve modes only control flow. */
public final class MultiblockResources {
    private MultiblockResources() { }
    private static void only(MultiblockInstance instance, String port, String... ids) {
        Set<String> allowed = Set.of(ids);
        instance.tank(port).withFilter(id -> id != null && allowed.contains(id));
    }
    public static void configure(MultiblockInstance instance) {
        switch (instance.blueprint().id()) {
            case "mek:fusion_reactor" -> {
                only(instance, "deuterium", "mek:deuterium"); only(instance, "tritium", "mek:tritium");
                only(instance, "fuel", "mek:fusion_fuel"); only(instance, "water", "minecraft:water");
                only(instance, "steam", "mek:steam");
            }
            case "mek:fission_reactor" -> {
                only(instance, "fuel", "mek:fissile_fuel"); only(instance, "waste", "mek:nuclear_waste");
                only(instance, "water", "minecraft:water"); only(instance, "steam", "mek:steam");
            }
            case "mek:thermoelectric_boiler" -> { only(instance, "water", "minecraft:water"); only(instance, "steam", "mek:steam"); }
            case "mek:industrial_turbine" -> { only(instance, "water", "minecraft:water"); only(instance, "steam", "mek:steam"); }
            case "mek:thermal_evaporation_plant" -> {
                only(instance, "water", "minecraft:water", "mek:brine"); only(instance, "output", "mek:brine", "mek:lithium");
            }
            case "mek:sps" -> { only(instance, "polonium", "mek:polonium"); only(instance, "antimatter", "mek:antimatter"); }
            default -> { }
        }
    }
}
