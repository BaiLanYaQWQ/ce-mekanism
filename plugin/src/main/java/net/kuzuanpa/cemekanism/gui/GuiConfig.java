package net.kuzuanpa.cemekanism.gui;

import net.kuzuanpa.cemekanism.platform.SectionDataTag;

import java.util.HashMap;
import java.util.Map;

/**
 * GUI 旋钮（mek-systems.yml 的 gui: 段）。面板/字形的几何在 gui-layout.yml 里，
 * 这里只放运行时能安全改的东西：刷新频率、颜色、多方块标题密度与逐方块皮肤覆盖。
 */
public final class GuiConfig {

    public enum Detail {
        AUTO, FULL, COMPACT
    }

    private static int refreshTicks = 5;
    private static int titleWidth = 158;
    private static Detail detail = Detail.AUTO;
    private static int titleColor = 0x000000;
    private static int energyColor = 0x3BFB98;
    private static int progressColor = 0x7DFFB2;
    private static int fluidColor = 0x3F76E4;
    private static boolean anchorTooltips = true;
    private static double maxDistance = 8.0;
    private static final Map<String, String> panelOverrides = new HashMap<>();
    private static final Map<String, String> slotOverrides = new HashMap<>();

    private GuiConfig() {
    }

    public static void install(SectionDataTag tag) {
        refreshTicks = Math.max(1, tag.getInt("gui/refresh_ticks").orElse(refreshTicks));
        titleWidth = Math.max(80, tag.getInt("gui/title_width").orElse(titleWidth));
        detail = tag.getString("gui/title_detail")
                .map(value -> switch (value.toLowerCase()) {
                    case "full" -> Detail.FULL;
                    case "compact" -> Detail.COMPACT;
                    default -> Detail.AUTO;
                })
                .orElse(Detail.AUTO);
        titleColor = color(tag, "gui/colors/title", titleColor);
        energyColor = color(tag, "gui/colors/energy", energyColor);
        progressColor = color(tag, "gui/colors/progress", progressColor);
        fluidColor = color(tag, "gui/colors/fluid", fluidColor);
        anchorTooltips = tag.getBoolean("gui/anchor_tooltips").orElse(anchorTooltips);
        maxDistance = tag.getDouble("gui/max_distance").orElse(maxDistance);
        panelOverrides.clear();
        for (int index = 0; index < 256; index++) {
            String block = tag.getString("gui/panel_overrides/" + index + "/block").orElse(null);
            if (block == null) {
                break;
            }
            tag.getString("gui/panel_overrides/" + index + "/image")
                    .ifPresent(image -> panelOverrides.put(block, image));
        }
        slotOverrides.clear();
        for (int index = 0; index < 512; index++) {
            String block = tag.getString("gui/slot_overrides/" + index + "/block").orElse(null);
            String slot = tag.getString("gui/slot_overrides/" + index + "/slot").orElse(null);
            String cell = tag.getString("gui/slot_overrides/" + index + "/cell").orElse(null);
            if (block == null || slot == null || cell == null) {
                break;
            }
            slotOverrides.put(block + "/" + slot, cell);
        }
    }

    private static int color(SectionDataTag tag, String path, int fallback) {
        return tag.getString(path).map(GuiConfig::parseColor).orElse(fallback);
    }

    private static int parseColor(String value) {
        String text = value.trim();
        if (text.startsWith("#")) {
            text = text.substring(1);
        }
        try {
            return (int) Long.parseLong(text, 16) & 0xFFFFFF;
        } catch (NumberFormatException e) {
            return 0xFFFFFF;
        }
    }

    public static int refreshTicks() {
        return refreshTicks;
    }

    public static int titleWidth() {
        return titleWidth;
    }

    public static Detail detail() {
        return detail;
    }

    public static int titleColor() {
        return titleColor;
    }

    public static int energyColor() {
        return energyColor;
    }

    public static int progressColor() {
        return progressColor;
    }

    public static int fluidColor() {
        return fluidColor;
    }

    public static boolean anchorTooltips() {
        return anchorTooltips;
    }

    public static double maxDistance() {
        return maxDistance;
    }

    public static String panelOverride(String blockId) {
        return panelOverrides.get(blockId);
    }

    public static String slotOverride(String blockId, String slotName) {
        return slotOverrides.get(blockId + "/" + slotName);
    }
}
