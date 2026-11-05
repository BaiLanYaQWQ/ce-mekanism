package net.kuzuanpa.cemekanism.platform;

import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 六台单方块发电机的运行参数（文档 11）：默认值由 tools/mek.py generate 写进
 * {@code mek-systems.yml} 的 {@code generator:} 段，这里只做只读访问。
 *
 * <p>方块模型、碰撞盒、方块 id 不在这里——那几项是生成期数据，见 GeneratedContent 与资源包。
 */
public final class GeneratorConfig {

    public record Fuel(long energyDensity, double maxBurnMb, int burnTicks) {
        public long energyPerBurnTick() { return energyDensity / burnTicks; }
    }

    private static ConfigurationSection root;

    private GeneratorConfig() {
    }

    public static void install(CeMekanismPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "mek-systems.yml");
        YamlConfiguration user = YamlConfiguration.loadConfiguration(file);
        root = user.getConfigurationSection("generator");
        // 老服升级时数据目录里的 mek-systems.yml 没有 generator 段：用 jar 里的默认值补齐，
        // 用户已经写过的键不动。否则发电机会因为没有默认面/燃气表而完全不工作。
        try (var stream = plugin.getResource("mek-systems.yml")) {
            if (stream != null) {
                YamlConfiguration bundled = YamlConfiguration.loadConfiguration(
                        new InputStreamReader(stream, StandardCharsets.UTF_8));
                ConfigurationSection defaults = bundled.getConfigurationSection("generator");
                if (defaults != null) {
                    if (root == null) {
                        root = user.createSection("generator");
                    }
                    merge(root, defaults);
                }
            }
        } catch (java.io.IOException ignored) {
            // 资源缺失时保留文件里的值；GeneratorConfig 的调用方都有兜底默认。
        }
    }

    private static void merge(ConfigurationSection target, ConfigurationSection defaults) {
        for (String key : defaults.getKeys(false)) {
            Object value = defaults.get(key);
            if (value instanceof ConfigurationSection child) {
                ConfigurationSection existing = target.getConfigurationSection(key);
                // 已有燃料条目按完整的自定义规格处理；不要为旧规格补 burn_ticks 改变燃烧速率。
                if (existing != null && defaults.getCurrentPath().equals("generator.gas.fuels")) {
                    continue;
                }
                merge(existing == null ? target.createSection(key) : existing, child);
            } else if (!target.contains(key)) {
                target.set(key, value);
            }
        }
    }

    private static ConfigurationSection section(String path) {
        return root == null ? null : root.getConfigurationSection(dot(path));
    }

    public static int intValue(String path, int fallback) {
        return root == null ? fallback : root.getInt(dot(path), fallback);
    }

    public static long longValue(String path, long fallback) {
        return root == null ? fallback : root.getLong(dot(path), fallback);
    }

    public static double doubleValue(String path, double fallback) {
        return root == null ? fallback : root.getDouble(dot(path), fallback);
    }

    public static String stringValue(String path, String fallback) {
        return root == null ? fallback : root.getString(dot(path), fallback);
    }

    /** 配置里沿用 core 的斜杠口径；Bukkit 的 ConfigurationSection 只认点。 */
    private static String dot(String path) {
        return path.replace('/', '.');
    }

    /** 发电机的默认面：端口名 → 相对面列表（front/left/right/back/top/bottom）。 */
    public static Map<String, List<String>> defaultSides(String family) {
        ConfigurationSection sides = section("defaults/" + family + "/sides");
        if (sides == null) {
            return Map.of();
        }
        Map<String, List<String>> out = new LinkedHashMap<>();
        for (String port : sides.getKeys(false)) {
            out.put(port, List.copyOf(sides.getStringList(port)));
        }
        return out;
    }

    public static Set<String> noWindDimensions() {
        return Set.copyOf(root == null ? List.of() : root.getStringList("wind.no_wind_dimensions"));
    }

    public static Map<String, Fuel> gasFuels() {
        ConfigurationSection fuels = section("gas/fuels");
        if (fuels == null) {
            return Map.of();
        }
        Map<String, Fuel> out = new LinkedHashMap<>();
        for (String id : fuels.getKeys(false)) {
            ConfigurationSection fuel = fuels.getConfigurationSection(id);
            if (fuel == null) {
                continue;
            }
            long density = fuel.getLong("energy_density", 0);
            double maxBurn = fuel.getDouble("max_burn_mb", 1);
            int burnTicks = fuel.getInt("burn_ticks", 1);
            if (burnTicks > 0 && density >= burnTicks && density % burnTicks == 0
                    && Double.isFinite(maxBurn) && maxBurn > 0) {
                out.put(id, new Fuel(density, maxBurn, burnTicks));
            }
        }
        return out;
    }

    public static int burnTicks(String itemId, int fallback) {
        return root == null ? fallback : root.getInt("heat.burn_ticks." + itemId, fallback);
    }
}
