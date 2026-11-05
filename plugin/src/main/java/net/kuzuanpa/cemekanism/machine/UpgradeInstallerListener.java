package net.kuzuanpa.cemekanism.machine;

import mekcore.upgrade.UpgradeDefinition;
import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.kuzuanpa.cemekanism.platform.BukkitItems;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/** 升级安装器物品与独立升级界面的点击/拖动接管。 */
public final class UpgradeInstallerListener implements Listener {

    private static final String UPGRADE_INSTALLER = "mek:upgrade_installer";

    private final CeMekanismPlugin plugin;

    public UpgradeInstallerListener(CeMekanismPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK
                || event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        ItemStack held = event.getItem();
        if (held == null || !UPGRADE_INSTALLER.equals(BukkitItems.idOf(held))) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        MachineBlockEntityController controller = this.plugin.machineAt(block.getWorld(),
                block.getX(), block.getY(), block.getZ());
        if (controller == null) {
            return;
        }
        event.setCancelled(true);
        controller.openUpgradeInstaller(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        UpgradeInstallerGui gui = UpgradeInstallerGui.of(event.getInventory());
        if (gui == null) {
            return;
        }
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        if (!gui.controller().machine().canAccess(player.getUniqueId().toString())) {
            player.closeInventory();
            return;
        }

        int raw = event.getRawSlot();
        if (raw >= 0 && raw < event.getView().getTopInventory().getSize()) {
            UpgradeDefinition definition = gui.definitionAt(raw);
            if (definition == null) {
                return;
            }
            if (event.isShiftClick()) {
                gui.withdrawAllToInventory(player, definition);
                return;
            }
            if (event.getClick() != ClickType.LEFT && event.getClick() != ClickType.RIGHT) {
                return;
            }
            ItemStack cursor = player.getItemOnCursor();
            if (cursor == null || cursor.getType().isAir()) {
                gui.withdrawToCursor(player, definition, event.getClick() == ClickType.RIGHT ? 1
                        : gui.controller().machine().upgrades().count(definition.id()));
            } else {
                gui.installFromCursor(player, definition);
            }
            return;
        }

        if (event.isShiftClick() && event.getClickedInventory() instanceof org.bukkit.inventory.PlayerInventory) {
            ItemStack source = event.getCurrentItem();
            UpgradeDefinition definition = gui.definitionOf(source);
            if (definition != null) {
                gui.installFromPlayerSlot(player, event.getSlot());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onDrag(InventoryDragEvent event) {
        UpgradeInstallerGui gui = UpgradeInstallerGui.of(event.getInventory());
        if (gui == null) {
            return;
        }
        Inventory top = event.getView().getTopInventory();
        if (event.getRawSlots().stream().anyMatch(slot -> slot >= 0 && slot < top.getSize())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClose(InventoryCloseEvent event) {
        UpgradeInstallerGui gui = UpgradeInstallerGui.of(event.getInventory());
        if (gui != null) {
            gui.controller().forgetUpgradeInstaller(gui);
        }
    }
}
