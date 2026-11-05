package net.kuzuanpa.cemekanism.i18n;

import mekcore.chemical.Chemical;
import mekcore.chemical.Chemicals;
import mekcore.item.ItemNames;
import mekcore.machine.FactoryTier;
import mekcore.machine.MachineBlueprint;
import mekcore.machine.MachineSnapshot;
import mekcore.resource.ResourceNames;
import mekcore.resource.ResourceType;
import mekcore.resource.ResourceTypes;
import net.kyori.adventure.text.Component;
import net.momirealms.craftengine.bukkit.api.BukkitAdaptor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Plugin-side translation catalog. Player messages follow the player's CraftEngine locale;
 * shared inventories use {@code language} from config.yml. Unsupported languages fall back to English.
 */
public final class I18n {
    private static final Locale ZH_CN = Locale.forLanguageTag("zh-CN");
    private static final Locale EN_US = Locale.US;
    private static Map<String, String> zhCn = loadAll("lang/zh_cn.yml", "lang/content/zh_cn.yml");
    private static Map<String, String> enUs = loadAll("lang/en_us.yml", "lang/content/en_us.yml");
    private static Locale defaultLocale = ZH_CN;

    private I18n() {
    }

    public static void install(JavaPlugin plugin) {
        zhCn = loadAll("lang/zh_cn.yml", "lang/content/zh_cn.yml");
        enUs = loadAll("lang/en_us.yml", "lang/content/en_us.yml");
        defaultLocale = normalize(plugin.getConfig().getString("language", "zh_cn"));
    }

    public static Locale locale(CommandSender sender) {
        if (sender instanceof Player player) {
            try {
                return normalize(BukkitAdaptor.adapt(player).locale());
            } catch (LinkageError | RuntimeException ignored) {
                return normalize(player.locale());
            }
        }
        return defaultLocale;
    }

    public static String tr(String key, Object... arguments) {
        return tr(defaultLocale, key, arguments);
    }

    public static String tr(CommandSender sender, String key, Object... arguments) {
        return tr(locale(sender), key, arguments);
    }

    public static String tr(Locale locale, String key, Object... arguments) {
        return trOrDefault(locale, key, key, arguments);
    }

    public static String trOrDefault(String key, String fallback, Object... arguments) {
        return trOrDefault(defaultLocale, key, fallback, arguments);
    }

    public static String trOrDefault(Locale locale, String key, String fallback, Object... arguments) {
        Locale normalized = normalize(locale);
        String value = normalized.equals(ZH_CN)
                ? zhCn.getOrDefault(key, enUs.get(key))
                : enUs.getOrDefault(key, zhCn.get(key));
        if (value == null) {
            value = fallback;
        }
        return format(value, arguments);
    }

    public static Component component(String key, Object... arguments) {
        return Component.text(tr(key, arguments));
    }

    public static Component component(CommandSender sender, String key, Object... arguments) {
        return Component.text(tr(sender, key, arguments));
    }

    public static void send(CommandSender sender, String key, Object... arguments) {
        sender.sendMessage(tr(sender, key, arguments));
    }

    public static String itemName(String id) {
        return itemName(defaultLocale, id);
    }

    public static String itemName(Locale locale, String id) {
        return content(locale, "mek.item." + path(id), ItemNames.nameOf(id));
    }

    public static String machineName(String id) {
        return machineName(defaultLocale, id);
    }

    public static String machineName(Locale locale, String id) {
        return content(locale, "mek.machine." + path(id), id);
    }

    public static String structureName(String id) {
        return structureName(defaultLocale, id);
    }

    public static String structureName(Locale locale, String id) {
        return content(locale, "mek.structure." + path(id), id);
    }

    public static String transmitterName(String id) {
        return transmitterName(defaultLocale, id);
    }

    public static String transmitterName(Locale locale, String id) {
        return content(locale, "mek.transmitter." + path(id), id);
    }

    public static String upgradeName(String id, String fallback) {
        return upgradeName(defaultLocale, id, fallback);
    }

    public static String upgradeName(Locale locale, String id, String fallback) {
        if (id == null) {
            return fallback;
        }
        return content(locale, "mek.item." + path(id), fallback);
    }

    public static String resourceName(ResourceType type, String id) {
        return resourceName(defaultLocale, type, id);
    }

    public static String resourceName(Locale locale, ResourceType type, String id) {
        if (id == null) {
            return tr(locale, "resource_type." + type.id());
        }
        if (type.equals(ResourceTypes.CHEMICAL) || type.equals(ResourceTypes.INFUSION)) {
            Chemical chemical = Chemicals.registry().find(id).orElse(null);
            if (chemical != null) {
                String fallback = normalize(locale).equals(ZH_CN) ? chemical.zhName() : chemical.enName();
                String translated = content(locale, "mek.resource." + path(chemical.id()), fallback);
                if (!translated.equals(id)) {
                    return translated;
                }
            }
        }
        return content(locale, "mek.resource." + path(id), ResourceNames.nameOf(type, id));
    }

    public static String chemicalName(Chemical chemical) {
        String key = "mek.resource." + path(chemical.id());
        String fallback = defaultLocale.equals(ZH_CN) ? chemical.zhName() : chemical.enName();
        return content(defaultLocale, key, fallback);
    }

    public static String resourceType(ResourceType type) {
        return tr("resource_type." + type.id(), type.zhName());
    }

    public static String machineName(MachineSnapshot snapshot) {
        return machineName(defaultLocale, snapshot);
    }

    public static String machineName(Locale locale, MachineSnapshot snapshot) {
        String name = machineName(locale, snapshot.id());
        if (snapshot.factoryTier() == FactoryTier.SINGLE) {
            return name;
        }
        return tr(locale, "machine.factory_name", name,
                tr(locale, "factory_tier." + snapshot.factoryTier().name().toLowerCase(Locale.ROOT)));
    }

    public static String machineName(MachineBlueprint blueprint) {
        return machineName(blueprint.id());
    }

    public static String machineName(Locale locale, MachineBlueprint blueprint) {
        return machineName(locale, blueprint.id());
    }

    public static String factoryTier(FactoryTier tier) {
        return tr("factory_tier." + tier.name().toLowerCase(Locale.ROOT), tier.zhName());
    }

    public static String tabName(String key, String fallback) {
        return trOrDefault("multiblock.tab." + key, fallback);
    }

    public static String roleName(String key, String fallback) {
        return trOrDefault("multiblock.role." + key, fallback);
    }

    public static String minerStatus(String status) {
        String key = normalizeMinerStatus(status);
        return trOrDefault("miner.status." + key, status);
    }

    public static String normalizeMinerStatus(String status) {
        // Status strings are persisted by earlier releases; accept those labels while storing stable keys now.
        return switch (status) {
            case "已停止" -> "stopped";
            case "扫描中" -> "scanning";
            case "等待扫描" -> "waiting";
            case "等待区块加载" -> "chunk";
            case "缓冲已满" -> "buffer_full";
            case "缺少回填材料" -> "missing_material";
            case "找到目标" -> "target";
            case "采集中" -> "mining";
            case "扫描完成" -> "complete";
            case "区域禁止采集" -> "protected";
            default -> status;
        };
    }

    public static String enumName(Enum<?> value, String fallback) {
        if (value == null) {
            return fallback;
        }
        String type = value.getDeclaringClass().getSimpleName()
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .toLowerCase(Locale.ROOT);
        return tr("enum." + type + "." + value.name().toLowerCase(Locale.ROOT), fallback);
    }

    public static Locale defaultLocale() {
        return defaultLocale;
    }

    public static Locale normalize(String language) {
        return language != null && language.toLowerCase(Locale.ROOT).startsWith("en") ? EN_US : ZH_CN;
    }

    public static Locale normalize(Locale locale) {
        return locale != null && "zh".equalsIgnoreCase(locale.getLanguage()) ? ZH_CN : EN_US;
    }

    private static String content(Locale locale, String key, String fallback) {
        String value = normalize(locale).equals(ZH_CN)
                ? zhCn.getOrDefault(key, enUs.get(key))
                : enUs.getOrDefault(key, zhCn.get(key));
        return value == null ? fallback : value;
    }

    private static Map<String, String> load(String resource) {
        try (InputStream stream = I18n.class.getClassLoader().getResourceAsStream(resource)) {
            if (stream == null) {
                return Map.of();
            }
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(stream, StandardCharsets.UTF_8));
            Map<String, String> result = new LinkedHashMap<>();
            for (String key : yaml.getKeys(true)) {
                Object value = yaml.get(key);
                if (value instanceof String text) {
                    result.put(key, text);
                } else if (value instanceof Number || value instanceof Boolean) {
                    result.put(key, value.toString());
                }
            }
            // CraftEngine content packs wrap their entries in lang.<locale>.
            String prefix = "lang." + path(resource.substring(resource.lastIndexOf('/') + 1, resource.length() - 4)) + ".";
            for (Map.Entry<String, String> entry : Map.copyOf(result).entrySet()) {
                if (entry.getKey().startsWith(prefix)) {
                    result.put(entry.getKey().substring(prefix.length()), entry.getValue());
                }
            }
            return Map.copyOf(result);
        } catch (Exception ignored) {
            return Map.of();
        }
    }

    private static Map<String, String> loadAll(String... resources) {
        Map<String, String> result = new LinkedHashMap<>();
        for (String resource : resources) {
            result.putAll(load(resource));
        }
        return Map.copyOf(result);
    }

    private static String path(String id) {
        if (id == null || id.isEmpty()) {
            return "";
        }
        int colon = id.indexOf(':');
        return colon >= 0 ? id.substring(colon + 1) : id;
    }

    private static String format(String pattern, Object... arguments) {
        if (arguments == null || arguments.length == 0) {
            return pattern;
        }
        String result = pattern;
        for (int i = 0; i < arguments.length; i++) {
            result = result.replace("{" + i + "}", String.valueOf(arguments[i]));
        }
        return result;
    }
}
