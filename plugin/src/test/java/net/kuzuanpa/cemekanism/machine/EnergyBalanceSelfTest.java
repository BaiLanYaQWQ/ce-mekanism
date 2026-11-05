package net.kuzuanpa.cemekanism.machine;

import mekcore.api.MekCore;
import mekcore.machine.FactoryTier;
import mekcore.machine.Machine;
import mekcore.persist.MapDataTag;
import mekcore.spec.SteamMath;
import net.kuzuanpa.cemekanism.generated.GeneratedContent;
import net.kuzuanpa.cemekanism.multiblock.MultiblockBlocks;
import net.kuzuanpa.cemekanism.platform.EnergyBalanceMigration;
import net.kuzuanpa.cemekanism.platform.GeneratorConfig;
import net.kuzuanpa.cemekanism.platform.SectionDataTag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/** Generated blueprints: fuel conservation, hydrogen balance, reloads and config migration. */
public final class EnergyBalanceSelfTest {
    private static int checks;
    private static MekCore core;
    private static YamlConfiguration defaults;
    private static void check(boolean ok, String message) {
        checks++; if (!ok) throw new AssertionError(message);
    }
    public static void main(String[] args) throws Exception {
        try (var in = EnergyBalanceSelfTest.class.getResourceAsStream("/mek-systems.yml")) {
            defaults = YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8));
        }
        var root = GeneratorConfig.class.getDeclaredField("root"); root.setAccessible(true);
        root.set(null, defaults.getConfigurationSection("generator"));
        core = MekCore.create(GeneratedContent::register);
        bio(); gas(); electrolysis(); buffers(); migration();
        System.out.println("Energy balance self-test: " + checks + " checks passed");
    }
    private static Machine machine(String family) {
        return new Machine(core.registrars().machines().require("mek:" + family), FactoryTier.SINGLE,
                core.registrars().recipes(), 123, null);
    }
    private static void bio() {
        var m = machine("bio_generator"); m.tank("biofuel").orElseThrow().set("mek:bioethanol", 64);
        long total = 0;
        for (int i = 0; i < 64; i++) { m.tick(); total += m.energyGeneratedLastTick(); }
        check(total == 22400 && m.tank("biofuel").orElseThrow().amount() == 0, "64 mB biofuel yields 22400 J");
    }
    private static void gas() {
        var full = machine("gas_burning_generator"); var tank = full.tank("fuel").orElseThrow();
        tank.set("mek:ethene", tank.capacity()); full.tick();
        check(full.energyGeneratedLastTick() == 180480, "full ethene output fits buffer");
        check(tank.amount() == tank.capacity() - 7, "6.4 mB burn prepays seven mB, retaining 0.6 mB");
        var hydrogen = machine("gas_burning_generator");
        hydrogen.tank("fuel").orElseThrow().set("mek:hydrogen", 18000); hydrogen.tick();
        check(hydrogen.energyGeneratedLastTick() == 51200, "full hydrogen output");
        var one = machine("gas_burning_generator"); one.tank("fuel").orElseThrow().set("mek:ethene", 1);
        one.energyTank().orElseThrow().set(null, one.energyCapacity() - 704); one.tick();
        check(one.tank("fuel").orElseThrow().amount() == 1 && one.energyGeneratedLastTick() == 0, "small energy room never wastes fuel");
        one.energyTank().orElseThrow().drain(); one.tick();
        check(one.energyGeneratedLastTick() == 705 && one.tank("fuel").orElseThrow().amount() == 0, "one mB retains 39 burn ticks");
        var tag = new MapDataTag(); one.save(tag);
        var loaded = machine("gas_burning_generator"); loaded.load(tag);
        long total = 705;
        for (int i = 0; i < 39; i++) { loaded.tick(); total += loaded.energyGeneratedLastTick(); }
        check(total == 28200, "all ethene energy survives reload with an empty fuel tank");
        loaded.tick(); check(loaded.energyGeneratedLastTick() == 0, "spent fuel cannot generate extra energy");
        var switched = machine("gas_burning_generator");
        switched.tank("fuel").orElseThrow().set("mek:ethene", 1); switched.tick();
        switched.tank("fuel").orElseThrow().set("mek:hydrogen", 1);
        for (int i = 0; i < 39; i++) switched.tick();
        check(switched.tank("fuel").orElseThrow().amount() == 1, "fuel switch finishes prepaid ethene first");
        switched.tick(); check(switched.energyGeneratedLastTick() == 200, "hydrogen keeps its own density");
    }
    private static void electrolysis() {
        for (int speed : new int[]{0,4,8}) for (int energy : new int[]{0,8}) {
            var m = machine("electrolytic_separator");
            m.installUpgrade("mek:speed_upgrade", speed); m.installUpgrade("mek:energy_upgrade", energy);
            m.tank("input").orElseThrow().set("minecraft:water", 24000);
            m.energyTank().orElseThrow().set(null, m.energyCapacity()); m.tick();
            long hydrogen = 0;
            for (String port : new String[]{"left","right"}) {
                var output = m.tank(port).orElseThrow();
                if ("mek:hydrogen".equals(output.storedId())) hydrogen += output.amount();
            }
            check(hydrogen > 0 && m.lastEnergyUsed() == hydrogen * 200,
                    "hydrogen energy neutrality at speed " + speed + ", energy " + energy);
        }
    }
    private static void buffers() {
        for (String family : new String[]{"heat_generator","solar_generator","advanced_solar_generator",
                "wind_generator","bio_generator","gas_burning_generator"}) {
            var old = machine(family); old.energyTank().orElseThrow().set(null, 31);
            var tag = new MapDataTag(); old.save(tag); var loaded = machine(family); loaded.load(tag);
            check(loaded.energyTank().orElseThrow().amount() == 31, "stored joules survive " + family + " reload");
        }
        SteamMath.load(new SectionDataTag(defaults.getConfigurationSection("steam")));
        long capacity = SteamMath.storageJoules(5,5);
        check(capacity == 2000000000L && capacity > SteamMath.maxEnergyOut(5,1,2,8,9), "turbine buffer accommodates steam flow");
        check(MultiblockBlocks.require("mek:industrial_turbine").capacityRule("energy").equals("total_volume:16000000"), "world turbine uses same capacity");
        check(GeneratorConfig.longValue("solar/generation_j",0) == 50
                && GeneratorConfig.longValue("solar/advanced_generation_j",0) == 300
                && GeneratorConfig.longValue("wind/generation_min_j",0) == 60
                && GeneratorConfig.longValue("wind/generation_max_j",0) == 480, "renewables use 1.21 values");
    }
    private static void migration() throws Exception {
        var user = new YamlConfiguration();
        user.set("generator.solar.generation_j",2); user.set("generator.wind.generation_max_j",777);
        user.set("generator.gas.fuels.mek:hydrogen.energy_density",2); user.set("generator.gas.fuels.mek:hydrogen.max_burn_mb",10);
        user.set("generator.gas.fuels.mek:ethene.energy_density",1234); user.set("generator.gas.fuels.mek:ethene.max_burn_mb",3);
        user.set("steam.energy_per_steam_mb",7.5); user.set("steam.storage_per_block_j",250);
        check(EnergyBalanceMigration.migrate(user,defaults), "old configuration migrates");
        check(user.getLong("generator.solar.generation_j") == 50 && user.getLong("generator.wind.generation_max_j") == 777, "custom output remains");
        check(user.getLong("generator.gas.fuels.mek:hydrogen.energy_density") == 200
                && user.getLong("generator.gas.fuels.mek:ethene.energy_density") == 1234
                && !user.contains("generator.gas.fuels.mek:ethene.burn_ticks"), "custom fuel remains a complete spec");
        check(user.getLong("steam.storage_per_block_j") == 16000000 && user.getDouble("steam.energy_per_steam_mb") == 7.5, "custom turbine efficiency remains");
        check(!EnergyBalanceMigration.migrate(user,defaults), "migration is idempotent");
        var merge = GeneratorConfig.class.getDeclaredMethod("merge",ConfigurationSection.class,ConfigurationSection.class); merge.setAccessible(true);
        merge.invoke(null,user.getConfigurationSection("generator"),defaults.getConfigurationSection("generator"));
        check(!user.contains("generator.gas.fuels.mek:ethene.burn_ticks"), "merging retains custom fuel semantics");
    }
}
