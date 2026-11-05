package net.kuzuanpa.cemekanism.transmitter;

import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.kuzuanpa.cemekanism.platform.ResourceColors;
import net.kuzuanpa.cemekanism.platform.SectionDataTag;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 物流的插件侧参数（`mek-systems.yml` 的 `logistics:` 段）。
 * 元件数值不在这里：管道/物品管道的分级数走 `transmitter:` / `item_pipe:`，热量走 `heat:`（都由核心读）。
 */
public final class LogisticsConfig {

    private static final String FILE_NAME = "mek-systems.yml";

    /** 红石响应的刷新间隔（趟）：元件没有自己的刻任务，红石由每个世界一条节流任务扫。 */
    private static int redstoneInterval = 4;
    /** 热导线缆自己的热容（K·热容单位）；机器/多方块的热容来自各自的 HeatSpec。 */
    private static double heatConductorCapacity = 1.0;
    /** 外壳之下的资源内容层；物流管道不受这几个开关影响，始终只画外壳。 */
    private static boolean transmitterContentsEnabled = true;
    private static int transmitterVisualInterval = 10;
    private static int transmitterBrightnessSteps = 8;
    private static double transmitterMinBrightness = 0.35;
    private static int energyContentsColor = 0x3EF090;
    private static int heatContentsColor = 0xFFFFFF;
    private static int fluidContentsFallbackColor = 0x7FB7E8;
    private static Map<String, Integer> fluidContentsColors = Map.of(
            "minecraft:water", 0x3F76E4,
            "minecraft:lava", 0xD45A12,
            "mekanism:steam", 0xFFFFFF);

    private LogisticsConfig() {
    }

    public static void install(CeMekanismPlugin plugin) {
        File file = new File(plugin.getDataFolder(), FILE_NAME);
        if (!file.isFile()) {
            plugin.saveResource(FILE_NAME, false);
        }
        SectionDataTag tag = new SectionDataTag(YamlConfiguration.loadConfiguration(file));
        tag.getInt("logistics/redstone_interval").ifPresent(value -> redstoneInterval = Math.max(1, value));
        tag.getDouble("logistics/heat_conductor_capacity").ifPresent(value -> heatConductorCapacity = value);
        tag.getBoolean("logistics/transmitter_contents/enabled")
                .ifPresent(value -> transmitterContentsEnabled = value);
        tag.getInt("logistics/transmitter_contents/refresh_interval")
                .ifPresent(value -> transmitterVisualInterval = Math.max(1, value));
        tag.getInt("logistics/transmitter_contents/brightness_steps")
                .ifPresent(value -> transmitterBrightnessSteps = Math.max(1, value));
        tag.getDouble("logistics/transmitter_contents/min_brightness")
                .ifPresent(value -> transmitterMinBrightness = Math.clamp(value, 0.0, 1.0));
        tag.getString("logistics/transmitter_contents/energy_color")
                .ifPresent(value -> energyContentsColor = color(value, energyContentsColor));
        tag.getString("logistics/transmitter_contents/heat_color")
                .ifPresent(value -> heatContentsColor = color(value, heatContentsColor));
        tag.getString("logistics/transmitter_contents/fluid_fallback_color")
                .ifPresent(value -> fluidContentsFallbackColor = color(value, fluidContentsFallbackColor));
        mekcore.persist.DataTag configuredFluids = tag.child(
                "logistics/transmitter_contents/fluid_colors");
        Map<String, Integer> fluids = new HashMap<>(fluidContentsColors);
        for (String id : configuredFluids.keys()) {
            configuredFluids.getString(id).ifPresent(value ->
                    fluids.put(id, color(value, fluidContentsFallbackColor)));
        }
        fluidContentsColors = Map.copyOf(fluids);
    }

    public static int redstoneInterval() {
        return redstoneInterval;
    }

    public static double heatConductorCapacity() {
        return heatConductorCapacity;
    }

    public static boolean transmitterContentsEnabled() {
        return transmitterContentsEnabled;
    }

    public static int transmitterVisualInterval() {
        return transmitterVisualInterval;
    }

    public static int transmitterBrightnessSteps() {
        return transmitterBrightnessSteps;
    }

    public static double transmitterMinBrightness() {
        return transmitterMinBrightness;
    }

    public static int energyContentsColor() {
        return energyContentsColor;
    }

    public static int heatContentsColor() {
        return heatContentsColor;
    }

    public static int fluidContentsColor(String id) {
        if (id == null) return fluidContentsFallbackColor;
        Integer configured = fluidContentsColors.get(id);
        if (configured != null) return configured;
        Integer chemical = ResourceColors.fluid(id);
        return chemical == null ? fluidContentsFallbackColor : chemical;
    }

    private static int color(String value, int fallback) {
        String hex = value == null ? "" : value.trim();
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        try {
            return Integer.parseInt(hex, 16) & 0xFFFFFF;
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }
}
