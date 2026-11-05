package net.kuzuanpa.cemekanism.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import java.util.ArrayList;
import java.util.List;

/**
 * 窗口标题的像素拼装器：面板图 → 动态字形 → 文字。
 * 客户端把标题画在面板 x=8，面板 PNG 左移 11px（含 3px 透明边）后内容从 x=0 开始；
 * 负位移字块只能往左挪，所以调用方按 x 从大到小摆动态元素，最后画文字。
 */
final class TitleBuilder {

    private static final int ORIGIN_X = 8;
    private static final int[] SHIFT_BRICKS = {128, 64, 32, 16, 8, 4, 2, 1};
    /**
     * BitmapProvider 的 advance = round(实际像素宽 × scale) + 1，这个 +1 是字体自带的字距，
     * 不是贴图里的透明列。生成器把定位像素放在最后一列只能保证“实际像素宽 = 整格宽”，
     * 这里仍要按整格宽 + 1 记账；否则每张图后面的内容右移 1px。
     */
    private static final int GLYPH_SPACING = 1;

    private final List<Component> parts = new ArrayList<>();
    private int pen = ORIGIN_X;
    private boolean panelDrawn;

    TitleBuilder panel(String id, int profileWidth) {
        int width = profileWidth + GuiLayout.margin() * 2;
        moveTo(-GuiLayout.margin());
        Component glyph = GuiSprites.glyph(id);
        if (glyph == null) {
            return this;
        }
        parts.add(glyph);
        pen += width + GLYPH_SPACING;
        panelDrawn = true;
        return this;
    }

    boolean hasPanel() {
        return panelDrawn;
    }

    /** 在面板坐标 x 处放一张字形图；width 是该字形的像素步进。 */
    TitleBuilder overlay(String id, int level, int x, int width, int rgb) {
        return sprite(id, Math.max(0, Math.min(GuiLayout.LEVELS - 1, level)), x, width, rgb);
    }

    TitleBuilder sprite(String id, int column, int x, int width, int rgb) {
        moveTo(x);
        Component glyph = GuiSprites.glyph(id, 0, column);
        if (glyph == null) {
            return this;
        }
        parts.add(Component.text("").color(TextColor.color(rgb)).append(glyph));
        pen += width + GLYPH_SPACING;
        return this;
    }

    TitleBuilder text(String text, TextColor color) {
        moveTo(GuiLayout.textX());
        parts.add(Component.text(text).color(color));
        pen += width(text);
        return this;
    }

    Component build() {
        // 图像字形按文字色相乘：根必须是白色，否则皮肤会暗成暗灰。
        return Component.empty().color(NamedTextColor.WHITE).append(parts);
    }

    private void moveTo(int target) {
        if (target > pen) {
            // 只有面板缺失的降级路径会走到这里；用空格补一点，别让文字压在图外。
            int spaces = Math.max(1, (target - pen + 3) / 4);
            parts.add(Component.text(" ".repeat(spaces)));
            pen += spaces * 4;
            return;
        }
        int remaining = pen - target;
        for (int brick : SHIFT_BRICKS) {
            while (remaining >= brick) {
                Component shift = GuiSprites.glyph("internal:neg_" + brick);
                if (shift == null) {
                    return;
                }
                parts.add(shift);
                remaining -= brick;
                pen -= brick;
            }
        }
    }

    static int width(String text) {
        int total = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            total += c == ' ' ? 4 : (c < 128 ? 6 : 9);
        }
        return total;
    }
}
