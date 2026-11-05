package net.kuzuanpa.cemekanism.machine;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

/** 配方/标签浏览始终只读，查询使用配方身份而非图标的 Material 或模型。 */
public final class RecipeBrowserListener implements Listener {

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        RecipeBrowserGui gui = RecipeBrowserGui.of(event.getInventory());
        if (gui == null) {
            return;
        }
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        if (!gui.canAccess(player)) {
            player.closeInventory();
            return;
        }
        int raw = event.getRawSlot();
        if (raw < 0 || raw >= RecipeBrowserGui.SIZE) return;
        if (event.getClick() != ClickType.LEFT && event.getClick() != ClickType.RIGHT
                && event.getClick() != ClickType.SHIFT_LEFT && event.getClick() != ClickType.SHIFT_RIGHT) return;
        int step = event.isShiftClick() ? 10 : 1;
        boolean right = event.isRightClick();
        // Screen changes also update the window title; wait for the click transaction to finish.
        Bukkit.getScheduler().runTask(gui.plugin(), () -> {
            if (player.getOpenInventory().getTopInventory() != gui.inventory()) return;
            if (!gui.canAccess(player)) {
                player.closeInventory();
                return;
            }
            switch (raw) {
                case RecipeBrowserGui.FIRST_PAGE -> gui.jump(player, false);
                case RecipeBrowserGui.PREVIOUS_PAGE -> gui.turn(player, -step);
                case RecipeBrowserGui.NEXT_PAGE -> gui.turn(player, step);
                case RecipeBrowserGui.LAST_PAGE -> gui.jump(player, true);
                case RecipeBrowserGui.BACK -> gui.back(player);
                case RecipeBrowserGui.HOME -> gui.home(player);
                default -> gui.select(player, raw, right);
            }
        });
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onDrag(InventoryDragEvent event) {
        RecipeBrowserGui gui = RecipeBrowserGui.of(event.getInventory());
        if (gui == null) {
            return;
        }
        int topSize = event.getView().getTopInventory().getSize();
        if (event.getRawSlots().stream().anyMatch(slot -> slot >= 0 && slot < topSize)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClose(InventoryCloseEvent event) {
        RecipeBrowserGui gui = RecipeBrowserGui.of(event.getInventory());
        if (gui != null) {
            gui.forget();
        }
    }
}
