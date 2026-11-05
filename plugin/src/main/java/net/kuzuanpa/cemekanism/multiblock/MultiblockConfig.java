package net.kuzuanpa.cemekanism.multiblock;

import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.kuzuanpa.cemekanism.platform.SectionDataTag;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 * 多方块的系统参数（`mek-systems.yml` 的 `multiblock:` 段）：默认值 = 随库上游 10.8 的 config，
 * 涡轮那几个在核心的 {@link mekcore.spec.SteamMath}（同样可配）。
 */
public final class MultiblockConfig {

    private static final String FILE_NAME = "mek-systems.yml";

    public static final String WATER = "minecraft:water";
    public static final String STEAM = "mek:steam";
    public static final String BRINE = "mek:brine";
    public static final String LITHIUM = "mek:lithium";

    // 上游 MekanismConfig.general 的锅炉/蒸馏塔默认值
    private static double ambientK = 300.0;
    private static double boilerBaseTemperatureK = 373.15;
    private static double boilerWaterConductivity = 0.7;
    private static double boilerSuperheatingTransfer = 16_000_000.0;
    private static double boilerSteamEfficiency = 0.2;
    private static double boilerWaterEnthalpy = 10.0;
    // 上游 BoilerMultiblockData.CASING_HEAT_CAPACITY = 50（× 方块数）。
    // 核心的 HeatNode 把 1 K 记成 heatCapacity × joulesPerKelvin 焦耳（默认 1000），
    // 所以内容层按上游值 ÷ 1000 落地，和单方块机器的 heat_spec 口径一致。
    private static double boilerCasingHeatCapacity = 0.05;
    private static double evaporationTempMultiplier = 0.4;
    private static double evaporationMaxTemperatureK = 3000.0;
    private static int evaporationMaxHeight = 18;
    // 上游 GeneralConfig.evaporationHeatCapacity 默认 100（× 高度），同样 ÷ 1000。
    private static double evaporationHeatCapacity = 0.1;
    // 上游 GeneratorsConfig.turbine.bladesPerCoil
    private static int turbineBladesPerCoil = 4;
    // 上游 GeneratorsConfig.fission_reactor；热容已按核心 1000 J/K 口径折算。
    private static double fissionCasingHeatCapacity = 1.0;
    private static long fissionEnergyPerFuelJ = 1_000_000L;
    private static double fissionSurfaceAreaTarget = 4.0;
    private static double fissionDefaultBurnRate = 0.1;
    private static int fissionBurnPerAssembly = 1;
    private static long fissionFuelPerAssembly = 8_000L;
    private static int fissionMinDamageTemperatureK = 1_200;
    private static int fissionMaxDamageTemperatureK = 1_800;
    private static boolean fissionMeltdownsEnabled = true;
    private static double fissionMeltdownChance = 0.001;
    private static int fissionMeltdownRadius = 8;
    private static double fissionMeltdownRadiationMultiplier = 50.0;
    private static double fissionPostMeltdownDamage = 75.0;
    // 上游 GeneratorsConfig.fusion_reactor。
    private static double fusionCaseHeatCapacity = 0.001;
    private static long fusionEnergyPerFuelJ = 10_000_000L;
    private static double fusionThermocoupleEfficiency = 0.05;
    private static double fusionCasingThermalConductivity = 0.1;
    private static double fusionWaterHeatingRatio = 0.3;
    private static long fusionFuelCapacity = 1_000L;
    private static long fusionEnergyCapacityJ = 1_000_000_000L;
    private static long fusionWaterPerInjection = 1_000_000L;
    private static long fusionSteamPerInjection = 100_000_000L;
    private static int fusionDefaultInjection = 2;
    private static int fusionMaxInjection = 98;
    private static double fusionBurnTemperatureK = 100_000_000.0;
    private static double fusionPlasmaHeatCapacity = 100.0;
    private static double fusionPlasmaCaseConductivity = 0.2;
    private static long fusionLaserEnergyPerTickJ = 1_000_000L;
    // 上游 GeneralConfig.sps 与 StorageConfig.spsPort
    private static long spsInputPerAntimatter = 1_000L;
    private static long spsEnergyPerInputJ = 1_000_000L;
    private static long spsOutputCapacity = 1_000L;
    private static long spsEnergyCapacityJ = 1_000_000_000L;
    // 上游 laser usage/storage，以及黑体辐射腔容量。
    private static long laserAmplifierCapacityJ = 5_000_000L;
    private static long laserUsageJ = 20L;
    private static int laserRange = 64;
    private static long hohlraumCapacity = 10L;
    // 上游 GeneralConfig.radiation.wasteBarrel*
    private static long wasteBarrelCapacity = 512_000L;
    private static int wasteBarrelProcessTicks = 20;
    private static int wasteBarrelDecayAmount = 1;
    private static final java.util.Set<String> wasteBarrelNoDecay = java.util.Set.of(
            "mek:plutonium", "mek:polonium");

    private MultiblockConfig() {
    }

    public static void install(CeMekanismPlugin plugin) {
        File file = new File(plugin.getDataFolder(), FILE_NAME);
        if (!file.isFile()) {
            plugin.saveResource(FILE_NAME, false);
        }
        SectionDataTag tag = new SectionDataTag(YamlConfiguration.loadConfiguration(file));
        tag.getDouble("multiblock/ambient_k").ifPresent(value -> ambientK = value);
        tag.getDouble("multiblock/boiler/base_temperature_k").ifPresent(value -> boilerBaseTemperatureK = value);
        tag.getDouble("multiblock/boiler/water_conductivity").ifPresent(value -> boilerWaterConductivity = value);
        tag.getDouble("multiblock/boiler/superheating_transfer").ifPresent(value -> boilerSuperheatingTransfer = value);
        tag.getDouble("multiblock/boiler/steam_efficiency").ifPresent(value -> boilerSteamEfficiency = value);
        tag.getDouble("multiblock/boiler/water_enthalpy").ifPresent(value -> boilerWaterEnthalpy = value);
        tag.getDouble("multiblock/boiler/casing_heat_capacity").ifPresent(value -> boilerCasingHeatCapacity = value);
        tag.getDouble("multiblock/evaporation/temp_multiplier").ifPresent(value -> evaporationTempMultiplier = value);
        tag.getDouble("multiblock/evaporation/max_temperature_k").ifPresent(value -> evaporationMaxTemperatureK = value);
        tag.getInt("multiblock/evaporation/max_height").ifPresent(value -> evaporationMaxHeight = value);
        tag.getDouble("multiblock/evaporation/heat_capacity_per_layer")
                .ifPresent(value -> evaporationHeatCapacity = value);
        tag.getInt("multiblock/turbine/blades_per_coil").ifPresent(value -> turbineBladesPerCoil = value);
        tag.getDouble("multiblock/fission/casing_heat_capacity")
                .ifPresent(value -> fissionCasingHeatCapacity = value);
        tag.getLong("multiblock/fission/energy_per_fuel_j")
                .ifPresent(value -> fissionEnergyPerFuelJ = value);
        tag.getDouble("multiblock/fission/surface_area_target")
                .ifPresent(value -> fissionSurfaceAreaTarget = value);
        tag.getDouble("multiblock/fission/default_burn_rate")
                .ifPresent(value -> fissionDefaultBurnRate = value);
        tag.getInt("multiblock/fission/burn_per_assembly")
                .ifPresent(value -> fissionBurnPerAssembly = value);
        tag.getLong("multiblock/fission/fuel_per_assembly")
                .ifPresent(value -> fissionFuelPerAssembly = value);
        tag.getInt("multiblock/fission/min_damage_temperature_k")
                .ifPresent(value -> fissionMinDamageTemperatureK = value);
        tag.getInt("multiblock/fission/max_damage_temperature_k")
                .ifPresent(value -> fissionMaxDamageTemperatureK = value);
        tag.getBoolean("multiblock/fission/meltdown_enabled")
                .ifPresent(value -> fissionMeltdownsEnabled = value);
        tag.getDouble("multiblock/fission/meltdown_chance")
                .ifPresent(value -> fissionMeltdownChance = value);
        tag.getInt("multiblock/fission/meltdown_radius")
                .ifPresent(value -> fissionMeltdownRadius = value);
        tag.getDouble("multiblock/fission/meltdown_radiation_multiplier")
                .ifPresent(value -> fissionMeltdownRadiationMultiplier = value);
        tag.getDouble("multiblock/fission/post_meltdown_damage")
                .ifPresent(value -> fissionPostMeltdownDamage = value);
        tag.getDouble("multiblock/fusion/case_heat_capacity")
                .ifPresent(value -> fusionCaseHeatCapacity = value);
        tag.getLong("multiblock/fusion/energy_per_fuel_j")
                .ifPresent(value -> fusionEnergyPerFuelJ = value);
        tag.getDouble("multiblock/fusion/thermocouple_efficiency")
                .ifPresent(value -> fusionThermocoupleEfficiency = value);
        tag.getDouble("multiblock/fusion/casing_thermal_conductivity")
                .ifPresent(value -> fusionCasingThermalConductivity = value);
        tag.getDouble("multiblock/fusion/water_heating_ratio")
                .ifPresent(value -> fusionWaterHeatingRatio = value);
        tag.getLong("multiblock/fusion/fuel_capacity_mb")
                .ifPresent(value -> fusionFuelCapacity = value);
        tag.getLong("multiblock/fusion/energy_capacity_j")
                .ifPresent(value -> fusionEnergyCapacityJ = value);
        tag.getLong("multiblock/fusion/water_per_injection_mb")
                .ifPresent(value -> fusionWaterPerInjection = value);
        tag.getLong("multiblock/fusion/steam_per_injection_mb")
                .ifPresent(value -> fusionSteamPerInjection = value);
        tag.getInt("multiblock/fusion/default_injection")
                .ifPresent(value -> fusionDefaultInjection = value);
        tag.getInt("multiblock/fusion/max_injection")
                .ifPresent(value -> fusionMaxInjection = value);
        tag.getDouble("multiblock/fusion/burn_temperature_k")
                .ifPresent(value -> fusionBurnTemperatureK = value);
        tag.getDouble("multiblock/fusion/plasma_heat_capacity")
                .ifPresent(value -> fusionPlasmaHeatCapacity = value);
        tag.getDouble("multiblock/fusion/plasma_case_conductivity")
                .ifPresent(value -> fusionPlasmaCaseConductivity = value);
        tag.getLong("multiblock/fusion/laser_energy_per_tick_j")
                .ifPresent(value -> fusionLaserEnergyPerTickJ = value);
        tag.getLong("multiblock/sps/input_per_antimatter_mb")
                .ifPresent(value -> spsInputPerAntimatter = value);
        tag.getLong("multiblock/sps/energy_per_input_j")
                .ifPresent(value -> spsEnergyPerInputJ = value);
        tag.getLong("multiblock/sps/output_capacity_mb")
                .ifPresent(value -> spsOutputCapacity = value);
        tag.getLong("multiblock/sps/energy_capacity_j")
                .ifPresent(value -> spsEnergyCapacityJ = value);
        tag.getLong("laser/amplifier_capacity_j").ifPresent(value -> laserAmplifierCapacityJ = value);
        tag.getLong("laser/usage_j").ifPresent(value -> laserUsageJ = value);
        tag.getInt("laser/range").ifPresent(value -> laserRange = value);
        tag.getLong("laser/hohlraum_capacity_mb").ifPresent(value -> hohlraumCapacity = value);
        tag.getLong("waste_barrel/capacity_mb").ifPresent(value -> wasteBarrelCapacity = value);
        tag.getInt("waste_barrel/process_ticks").ifPresent(value -> wasteBarrelProcessTicks = value);
        tag.getInt("waste_barrel/decay_per_process_mb").ifPresent(value -> wasteBarrelDecayAmount = value);
    }

    public static double ambientK() {
        return ambientK;
    }

    public static double boilerBaseTemperatureK() {
        return boilerBaseTemperatureK;
    }

    public static double boilerWaterConductivity() {
        return boilerWaterConductivity;
    }

    public static double boilerSuperheatingTransfer() {
        return boilerSuperheatingTransfer;
    }

    public static double boilerSteamEfficiency() {
        return boilerSteamEfficiency;
    }

    public static double boilerWaterEnthalpy() {
        return boilerWaterEnthalpy;
    }

    public static double boilerCasingHeatCapacity() {
        return boilerCasingHeatCapacity;
    }

    public static double evaporationTempMultiplier() {
        return evaporationTempMultiplier;
    }

    public static double evaporationMaxTemperatureK() {
        return evaporationMaxTemperatureK;
    }

    public static int evaporationMaxHeight() {
        return evaporationMaxHeight;
    }

    public static double evaporationHeatCapacity() {
        return evaporationHeatCapacity;
    }

    public static int turbineBladesPerCoil() {
        return turbineBladesPerCoil;
    }

    public static double fissionCasingHeatCapacity() {
        return fissionCasingHeatCapacity;
    }

    public static long fissionEnergyPerFuelJ() {
        return fissionEnergyPerFuelJ;
    }

    public static double fissionSurfaceAreaTarget() {
        return fissionSurfaceAreaTarget;
    }

    public static double fissionDefaultBurnRate() {
        return fissionDefaultBurnRate;
    }

    public static int fissionBurnPerAssembly() {
        return fissionBurnPerAssembly;
    }

    public static long fissionFuelPerAssembly() {
        return fissionFuelPerAssembly;
    }

    public static int fissionMinDamageTemperatureK() {
        return fissionMinDamageTemperatureK;
    }

    public static int fissionMaxDamageTemperatureK() {
        return fissionMaxDamageTemperatureK;
    }

    public static boolean fissionMeltdownsEnabled() {
        return fissionMeltdownsEnabled;
    }

    public static double fissionMeltdownChance() {
        return fissionMeltdownChance;
    }

    public static int fissionMeltdownRadius() {
        return fissionMeltdownRadius;
    }

    public static double fissionMeltdownRadiationMultiplier() {
        return fissionMeltdownRadiationMultiplier;
    }

    public static double fissionPostMeltdownDamage() {
        return fissionPostMeltdownDamage;
    }

    public static double fusionCaseHeatCapacity() {
        return fusionCaseHeatCapacity;
    }

    public static long fusionEnergyPerFuelJ() {
        return fusionEnergyPerFuelJ;
    }

    public static double fusionThermocoupleEfficiency() {
        return fusionThermocoupleEfficiency;
    }

    public static double fusionCasingThermalConductivity() {
        return fusionCasingThermalConductivity;
    }

    public static double fusionWaterHeatingRatio() {
        return fusionWaterHeatingRatio;
    }

    public static long fusionFuelCapacity() {
        return fusionFuelCapacity;
    }

    public static long fusionEnergyCapacityJ() {
        return fusionEnergyCapacityJ;
    }

    public static long fusionWaterPerInjection() {
        return fusionWaterPerInjection;
    }

    public static long fusionSteamPerInjection() {
        return fusionSteamPerInjection;
    }

    public static int fusionDefaultInjection() {
        return fusionDefaultInjection;
    }

    public static int fusionMaxInjection() {
        return fusionMaxInjection;
    }

    public static double fusionBurnTemperatureK() {
        return fusionBurnTemperatureK;
    }

    public static double fusionPlasmaHeatCapacity() {
        return fusionPlasmaHeatCapacity;
    }

    public static double fusionPlasmaCaseConductivity() {
        return fusionPlasmaCaseConductivity;
    }

    public static long fusionLaserEnergyPerTickJ() {
        return fusionLaserEnergyPerTickJ;
    }

    public static long spsInputPerAntimatter() {
        return spsInputPerAntimatter;
    }

    public static long spsEnergyPerInputJ() {
        return spsEnergyPerInputJ;
    }

    public static long spsOutputCapacity() {
        return spsOutputCapacity;
    }

    public static long spsEnergyCapacityJ() {
        return spsEnergyCapacityJ;
    }

    public static long laserAmplifierCapacityJ() {
        return laserAmplifierCapacityJ;
    }

    public static long laserUsageJ() {
        return laserUsageJ;
    }

    public static int laserRange() {
        return laserRange;
    }

    public static long hohlraumCapacity() {
        return hohlraumCapacity;
    }

    public static long wasteBarrelCapacity() {
        return wasteBarrelCapacity;
    }

    public static int wasteBarrelProcessTicks() {
        return wasteBarrelProcessTicks;
    }

    public static int wasteBarrelDecayAmount() {
        return wasteBarrelDecayAmount;
    }

    public static boolean wasteBarrelDecays(String chemicalId) {
        return !wasteBarrelNoDecay.contains(chemicalId);
    }
}
