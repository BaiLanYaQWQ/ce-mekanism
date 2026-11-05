package net.kuzuanpa.cemekanism.gui;

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
                ? "GUI 面板 " + panels().size() + " 张、条形图 " + bars().size() + " 张就绪（21 档/张）"
                : "GUI 缺失：" + missing;
    }

    public static List<String> lines() {
        List<String> lines = new ArrayList<>();
        for (String panel : panels()) {
            lines.add("GUI 面板 " + panel + " = " + (GuiSprites.present(panel) ? "已注册" : "缺失"));
        }
        for (String bar : bars()) {
            lines.add("GUI 条形图 " + bar + " = " + GuiSprites.columns(bar) + " 档"
                    + (GuiSprites.present(bar) ? "" : "（缺失）"));
        }
        for (String item : overlayItems()) {
            lines.add("GUI 物品 " + item + " = "
                    + (CraftEngineItems.byId(Key.of(item)) == null ? "缺失" : "已注册"));
        }
        return lines;
    }

    private static List<String> missing() {
        List<String> missing = new ArrayList<>();
        for (String panel : panels()) {
            if (!GuiSprites.present(panel)) {
                missing.add("面板 " + panel);
            }
        }
        for (String bar : bars()) {
            if (!GuiSprites.present(bar)) {
                missing.add("条形图 " + bar);
            } else if (GuiSprites.columns(bar) != GuiLayout.LEVELS) {
                missing.add("条形图 " + bar + " 档数=" + GuiSprites.columns(bar));
            }
        }
        for (String item : overlayItems()) {
            if (CraftEngineItems.byId(Key.of(item)) == null) {
                missing.add("GUI 物品 " + item);
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
