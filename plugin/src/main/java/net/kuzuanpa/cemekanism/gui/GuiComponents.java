package net.kuzuanpa.cemekanism.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.plugin.context.PlayerOptionalContext;
import net.momirealms.craftengine.core.util.AdventureHelper;

/**
 * Paper 的 Component → CE 重定位过的 Component。
 * CE 的 BukkitInventory/updateInventoryTitle 只收它自己那份 adventure 类型；
 * 走 MiniMessage 文本中转，字形字体与颜色都能保住。
 */
public final class GuiComponents {

    private GuiComponents() {
    }

    public static net.momirealms.craftengine.libraries.adventure.text.Component toCe(
            Component component, Player player) {
        String mini = MiniMessage.miniMessage().serialize(component);
        return AdventureHelper.deserialize(mini, PlayerOptionalContext.of(player));
    }
}
