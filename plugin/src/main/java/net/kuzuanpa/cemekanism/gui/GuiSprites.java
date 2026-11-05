package net.kuzuanpa.cemekanism.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.momirealms.craftengine.bukkit.api.CraftEngineImages;
import net.momirealms.craftengine.core.font.BitmapImage;
import net.momirealms.craftengine.core.font.Image;
import net.momirealms.craftengine.core.util.Key;

/**
 * CE 字形图的取用层。Image.componentAt() 返回的是 CE 重定位过的 adventure 类型，
 * 喂不进 Bukkit 的 Component，所以统一走 miniMessageAt() 再用 Paper 的 MiniMessage 解回来。
 */
public final class GuiSprites {

    private static final MiniMessage MINI = MiniMessage.miniMessage();

    private GuiSprites() {
    }

    public static boolean present(String id) {
        return id != null && CraftEngineImages.byId(Key.of(id)) != null;
    }

    public static Component glyph(String id) {
        return glyph(id, 0, 0);
    }

    public static Component glyph(String id, int row, int column) {
        Image image = CraftEngineImages.byId(Key.of(id));
        if (image == null) {
            return null;
        }
        return MINI.deserialize(image.miniMessageAt(row, column));
    }

    /** 雪碧图的列数；不是 BitmapImage（引用图）或缺失时返回 1。 */
    public static int columns(String id) {
        Image image = CraftEngineImages.byId(Key.of(id));
        return image instanceof BitmapImage bitmap ? bitmap.columns() : 1;
    }

    /** 21 档填充图的第 level 格（0..20）。 */
    public static Component fill(String id, int level, int levels) {
        int column = Math.max(0, Math.min(levels - 1, level));
        return glyph(id, 0, column);
    }
}
