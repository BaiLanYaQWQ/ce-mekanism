package net.kuzuanpa.cemekanism.radiation;

import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 玩家剂量存档：UUID → μSv，落在插件目录的 doses.yml。
 * 用插件自己的文件而不是玩家 PDC：Paper 的离线玩家 PDC 是只读视图，
 * `removeAll` 要能连离线玩家一起清（台账 P28）。
 */
public final class RadiationStore {
    private static final String FILE_NAME = "doses.yml";
    private static final Map<UUID, Double> VALUES = new LinkedHashMap<>();
    private static File file;
    private static boolean dirty;

    private RadiationStore() {
    }

    public static void install(CeMekanismPlugin plugin) {
        file = new File(plugin.getDataFolder(), FILE_NAME);
        VALUES.clear();
        if (!file.isFile()) {
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String key : config.getKeys(false)) {
            try {
                double value = config.getDouble(key, 0.0);
                if (value > 0.0) {
                    VALUES.put(UUID.fromString(key), value);
                }
            } catch (IllegalArgumentException ignored) {
                // 坏键跳过，别让一条脏数据拖垮整个存档
            }
        }
    }

    public static double load(UUID player) {
        return VALUES.getOrDefault(player, 0.0);
    }

    public static void save(UUID player, double microSv) {
        if (microSv <= 0.0) {
            VALUES.remove(player);
        } else {
            VALUES.put(player, microSv);
        }
        dirty = true;
    }

    public static int size() {
        return VALUES.size();
    }

    public static int clearAll() {
        int count = VALUES.size();
        VALUES.clear();
        dirty = true;
        flush();
        return count;
    }

    public static void flush() {
        if (!dirty || file == null) {
            return;
        }
        YamlConfiguration config = new YamlConfiguration();
        VALUES.forEach((uuid, value) -> config.set(uuid.toString(), value));
        try {
            config.save(file);
            dirty = false;
        } catch (IOException ignored) {
            // 下一次 tick 还会再试；不把存档异常抛到主线程
        }
    }
}
