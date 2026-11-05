package net.kuzuanpa.cemekanism.i18n;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Loads the packaged catalogs and verifies player-visible names resolve in both languages. */
public final class I18nSelfTest {
    private static final Pattern ARGUMENT = Pattern.compile("\\{(\\d+)}");
    private static int checks;

    private I18nSelfTest() {
    }

    public static void main(String[] args) {
        YamlConfiguration zh = load("lang/zh_cn.yml");
        YamlConfiguration en = load("lang/en_us.yml");
        Set<String> zhKeys = stringKeys(zh);
        Set<String> enKeys = stringKeys(en);
        check(zhKeys.equals(enKeys), "zh/en message keys differ");
        for (String key : zhKeys) {
            check(arguments(zh.getString(key)).equals(arguments(en.getString(key))),
                    "placeholder mismatch: " + key);
            check(zh.isString(key) && en.isString(key), "non-string message: " + key);
        }
        check(I18n.tr(Locale.SIMPLIFIED_CHINESE, "gui.energy.label").equals("能量"),
                "zh runtime lookup: " + I18n.tr(Locale.SIMPLIFIED_CHINESE, "gui.energy.label"));
        check(I18n.tr(Locale.US, "gui.energy.label").equals("Energy"),
                "en runtime lookup: " + I18n.tr(Locale.US, "gui.energy.label"));
        check(I18n.tr(Locale.FRANCE, "gui.energy.label").equals("Energy"),
                "unsupported locale fallback: " + I18n.tr(Locale.FRANCE, "gui.energy.label"));
        check(I18n.itemName(Locale.US, "mek:osmium_ore").equals("Osmium Ore"), "item name translation");
        check(I18n.machineName(Locale.US, "mek:crusher").equals("Crusher"), "machine name translation");
        check(I18n.structureName(Locale.US, "mek:fission_reactor").equals("Fission Reactor"),
                "structure name translation");
        System.out.println("I18n self-test: " + checks + " checks passed");
    }

    private static YamlConfiguration load(String path) {
        try (InputStream stream = I18nSelfTest.class.getClassLoader().getResourceAsStream(path)) {
            if (stream == null) {
                throw new IllegalStateException("missing resource " + path);
            }
            return YamlConfiguration.loadConfiguration(
                    new InputStreamReader(stream, StandardCharsets.UTF_8));
        } catch (Exception error) {
            throw new IllegalStateException("cannot load " + path, error);
        }
    }

    private static Set<String> arguments(String text) {
        Set<String> result = new HashSet<>();
        Matcher matcher = ARGUMENT.matcher(text == null ? "" : text);
        while (matcher.find()) {
            result.add(matcher.group(1));
        }
        return result;
    }

    private static Set<String> stringKeys(YamlConfiguration yaml) {
        Set<String> result = new HashSet<>();
        for (String key : yaml.getKeys(true)) {
            if (yaml.isString(key)) {
                result.add(key);
            }
        }
        return result;
    }

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
