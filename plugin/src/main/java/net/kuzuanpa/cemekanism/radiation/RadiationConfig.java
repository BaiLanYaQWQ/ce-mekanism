package net.kuzuanpa.cemekanism.radiation;

import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 辐射的插件侧参数（`mek-systems.yml` 的 `radiation:` 段）。
 * 默认值 = 上游 GeneralConfig 的 radiation*（衰减 0.9995/s → 半衰期 27719 刻）与
 * ItemHazmatSuitArmor 的四件屏蔽值；MekaSuit 的屏蔽单元要等 B7 的模块系统。
 */
public final class RadiationConfig {

    private static final String FILE_NAME = "mek-systems.yml";

    private static boolean enabled = true;
    private static int intervalTicks = 20;
    private static int effectIntervalTicks = 20;
    private static int maxDistance = 80;
    private static double halfLifeTicks = 27_719;
    private static double openFactor = 0.001;
    private static double damage = 1.0;
    private static double lethalDamage = 10.0;
    private static int hunger = 6;
    private static final Map<String, Double> shielding = new LinkedHashMap<>();

    static {
        // 上游 ItemHazmatSuitArmor.getShieldingByArmor
        shielding.put("mek:hazmat_mask", 0.25);
        shielding.put("mek:hazmat_gown", 0.4);
        shielding.put("mek:hazmat_pants", 0.2);
        shielding.put("mek:hazmat_boots", 0.15);
    }

    private RadiationConfig() {
    }

    public static void install(CeMekanismPlugin plugin) {
        File file = new File(plugin.getDataFolder(), FILE_NAME);
        if (!file.isFile()) {
            plugin.saveResource(FILE_NAME, false);
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        enabled = config.getBoolean("radiation.enabled", enabled);
        intervalTicks = Math.max(1, config.getInt("radiation.interval_ticks", intervalTicks));
        effectIntervalTicks = Math.max(1, config.getInt("radiation.effect_interval_ticks", effectIntervalTicks));
        maxDistance = Math.max(1, config.getInt("radiation.max_distance", maxDistance));
        halfLifeTicks = Math.max(1.0, config.getDouble("radiation.half_life_ticks", halfLifeTicks));
        openFactor = Math.max(0.0, config.getDouble("radiation.open_factor", openFactor));
        damage = Math.max(0.0, config.getDouble("radiation.damage", damage));
        lethalDamage = Math.max(0.0, config.getDouble("radiation.lethal_damage", lethalDamage));
        hunger = Math.max(0, config.getInt("radiation.hunger", hunger));
        ConfigurationSection section = config.getConfigurationSection("radiation.shielding");
        if (section != null) {
            shielding.clear();
            for (String item : section.getKeys(false)) {
                shielding.put(item, Math.clamp(section.getDouble(item, 0.0), 0.0, 1.0));
            }
        }
    }

    public static boolean enabled() {
        return enabled;
    }

    public static int intervalTicks() {
        return intervalTicks;
    }

    public static int effectIntervalTicks() {
        return effectIntervalTicks;
    }

    public static int maxDistance() {
        return maxDistance;
    }

    public static double halfLifeTicks() {
        return halfLifeTicks;
    }

    public static double openFactor() {
        return openFactor;
    }

    public static double damage() {
        return damage;
    }

    public static double lethalDamage() {
        return lethalDamage;
    }

    public static int hunger() {
        return hunger;
    }

    public static Map<String, Double> shielding() {
        return Map.copyOf(shielding);
    }
}
