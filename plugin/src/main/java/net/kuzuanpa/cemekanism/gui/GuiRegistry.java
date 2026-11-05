package net.kuzuanpa.cemekanism.gui;

import net.kuzuanpa.cemekanism.i18n.I18n;
import org.bukkit.configuration.file.YamlConfiguration;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.util.Key;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** 载包自检：面板字形与全部 21 档条形/进度雪碧图是否都在。 */
public final class GuiRegistry {

    private GuiRegistry() {
    }

    public static String status() {
        List<String> missing = missing();
        return missing.isEmpty()
                ? I18n.tr("gui.registry.ready", panels().size(), bars().size(), GuiLayout.LEVELS)
                : I18n.tr("gui.registry.missing", missing);
    }

    public static List<String> lines() {
        List<String> lines = new ArrayList<>();
        for (String panel : panels()) {
            lines.add(I18n.tr("gui.registry.panel", panel,
                    I18n.tr(GuiSprites.present(panel) ? "gui.registry.registered" : "gui.registry.missing_value")));
        }
        for (String bar : bars()) {
            lines.add(I18n.tr("gui.registry.bar", bar, GuiSprites.columns(bar),
                    GuiSprites.present(bar) ? "" : I18n.tr("gui.registry.missing_suffix")));
        }
        for (String item : overlayItems()) {
            lines.add(I18n.tr("gui.registry.item", item,
                    I18n.tr(CraftEngineItems.byId(Key.of(item)) == null
                            ? "gui.registry.missing_value" : "gui.registry.registered")));
        }
        return lines;
    }

    public static List<String> missing() {
        List<String> missing = new ArrayList<>();
        for (String panel : panels()) {
            if (!GuiSprites.present(panel)) {
                missing.add(I18n.tr("gui.registry.panel_name", panel));
            }
        }
        for (String bar : bars()) {
            if (!GuiSprites.present(bar)) {
                missing.add(I18n.tr("gui.registry.bar_name", bar));
            } else if (GuiSprites.columns(bar) != GuiLayout.LEVELS) {
                missing.add(I18n.tr("gui.registry.bar_levels", bar, GuiSprites.columns(bar)));
            }
        }
        for (String item : overlayItems()) {
            if (CraftEngineItems.byId(Key.of(item)) == null) {
                missing.add(I18n.tr("gui.registry.item_name", item));
            }
        }
        return missing;
    }

    static List<String> panels() {
        List<String> result = new ArrayList<>();
        YamlConfiguration yaml = load();
        result.addAll(yaml.getStringList("panels"));
        result.addAll(yaml.getStringList("multiblock_panels"));
        return result;
    }

    static List<String> bars() {
        return load().getStringList("bars");
    }

    static List<String> overlayItems() {
        List<String> items = new ArrayList<>(List.of("mek:gui_energy_slot", "mek:gui_tank_slot", "mek:gui_empty_slot",
                "mek:gui_tooltip_anchor", "mek:gui_progress_slot"));
        items.addAll(load().getStringList("icon_items"));
        return items;
    }

    private static YamlConfiguration load() {
        try (InputStreamReader reader = new InputStreamReader(
                GuiRegistry.class.getResourceAsStream("/gui-layout.yml"), StandardCharsets.UTF_8)) {
            return YamlConfiguration.loadConfiguration(reader);
        } catch (Exception e) {
            return new YamlConfiguration();
        }
    }
}
