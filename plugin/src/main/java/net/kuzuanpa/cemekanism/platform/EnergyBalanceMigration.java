package net.kuzuanpa.cemekanism.platform;

import net.kuzuanpa.cemekanism.i18n.I18n;
import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

/** 只升级旧版内置发电参数；自定义值保留，首次升级前备份原配置。 */
public final class EnergyBalanceMigration {
    private EnergyBalanceMigration() {}

    public static void install(CeMekanismPlugin plugin, File file) {
        YamlConfiguration user = YamlConfiguration.loadConfiguration(file);
        if (user.getInt("energy_balance_version", 0) >= 1) return;
        try (var stream = plugin.getResource("mek-systems.yml")) {
            if (stream == null) return;
            YamlConfiguration defaults = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(stream, StandardCharsets.UTF_8));
            if (migrate(user, defaults)) {
                File backup = new File(file.getPath() + ".before-energy-balance-v1.bak");
                if (!backup.exists()) Files.copy(file.toPath(), backup.toPath());
                user.save(file);
                plugin.getLogger().info(I18n.tr("platform.energy_migration.done", backup.getName()));
            }
        } catch (IOException exception) {
            throw new IllegalStateException(I18n.tr("platform.energy_migration.failed", file), exception);
        }
    }

    public static boolean migrate(ConfigurationSection user, ConfigurationSection defaults) {
        if (user.getInt("energy_balance_version", 0) >= 1) return false;
        Map<String, Number> legacy = Map.ofEntries(
                Map.entry("heat.generation_j", 100), Map.entry("heat.lava_per_side_j", 7),
                Map.entry("heat.nether_bonus_j", 10), Map.entry("heat.max_output_j", 159),
                Map.entry("heat.lava_per_tick_mb", 100), Map.entry("solar.generation_j", 2),
                Map.entry("solar.advanced_generation_j", 12), Map.entry("wind.generation_min_j", 1),
                Map.entry("wind.generation_max_j", 5), Map.entry("bio.generation_j", 25),
                Map.entry("gas.tank_capacity_mb", 1000));
        legacy.forEach((path, old) -> replaceLegacy(user, defaults, "generator." + path, old));
        for (String id : new String[]{"mek:hydrogen", "mek:ethene"}) {
            String path = "generator.gas.fuels." + id;
            ConfigurationSection fuel = user.getConfigurationSection(path);
            long oldDensity = id.equals("mek:hydrogen") ? 2 : 8;
            if (fuel == null || (fuel.getKeys(false).size() == 2
                    && fuel.getLong("energy_density") == oldDensity && fuel.getDouble("max_burn_mb") == 10)) {
                user.createSection(path, defaults.getConfigurationSection(path).getValues(false));
            }
        }
        replaceLegacy(user, defaults, "steam.storage_per_block_j", 250);
        if (!user.contains("steam.energy_per_steam_mb")) {
            user.set("steam.energy_per_steam_mb", defaults.get("steam.energy_per_steam_mb"));
        }
        user.set("energy_balance_version", 1);
        return true;
    }

    private static void replaceLegacy(ConfigurationSection user, ConfigurationSection defaults,
                                      String path, Number old) {
        Object value = user.get(path);
        if (value == null || (value instanceof Number number && number.doubleValue() == old.doubleValue())) {
            user.set(path, defaults.get(path));
        }
    }
}
