package net.kuzuanpa.cemekanism.machine;

import mekcore.item.ItemSlotKind;
import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.kuzuanpa.cemekanism.gui.GuiLayout;
import net.kuzuanpa.cemekanism.gui.MachineControls;
import net.kuzuanpa.cemekanism.gui.MachineGui;
import net.kuzuanpa.cemekanism.gui.GuiText;
import net.kuzuanpa.cemekanism.platform.ResourceContainerItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * 机器容器的点击接管（文档 04 第八节 / 08 第六节）：
 * 输出格只能取不能放，空白格/隐形 tooltip 锚点全拦，输入格可放；
 * 升级已移到独立安装器界面，本容器不再包含升级槽。
 */
public final class MachineContainerListener implements Listener {

    private final CeMekanismPlugin plugin;

    public MachineContainerListener(CeMekanismPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        MachineBlockEntityController controller = controllerOf(event.getInventory());
        if (controller == null) {
            return;
        }
        if (!controller.machine().canAccess(event.getWhoClicked().getUniqueId().toString())) {
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
            return;
        }
        boolean top = event.getRawSlot() >= 0 && event.getRawSlot() < controller.inventory().getSize();
        if (top) {
            GuiLayout.MachineLayout layout = GuiLayout.forMachine(controller.machine(),
                    controller.machine().factoryTier(), controller.blockId());
            var gauge = layout.tankGauges().stream().filter(value -> value.anchors().stream()
                    .anyMatch(cell -> cell.index() == event.getRawSlot())).findFirst().orElse(null);
            if (gauge != null && ResourceContainerItems.isContainer(event.getCursor())) {
                event.setCancelled(true);
                if (event.getClick() != ClickType.LEFT && event.getClick() != ClickType.RIGHT) return;
                Player player = (Player) event.getWhoClicked();
                ItemStack expected = event.getCursor().clone();
                boolean put = event.isRightClick();
                Bukkit.getScheduler().runTask(plugin, () -> {
                    if (player.getOpenInventory().getTopInventory() != controller.inventory()
                            || !controller.registered() || !controller.machine().canAccess(player.getUniqueId().toString())
                            || !expected.equals(player.getItemOnCursor())) return;
                    var result = ResourceContainerItems.transfer(expected, controller.machine().handler(gauge.port()), put);
                    if (result != null) {
                        player.setItemOnCursor(result);
                        controller.markChanged();
                        MachineGui.updateAnchors(controller);
                        player.updateInventory();
                    }
                });
                return;
            }
            if (MachineControls.handles(controller, event.getRawSlot())) {
                event.setCancelled(true);
                if (event.getClick() != ClickType.LEFT && event.getClick() != ClickType.RIGHT
                        && event.getClick() != ClickType.SHIFT_LEFT && event.getClick() != ClickType.SHIFT_RIGHT) return;
                Player player = (Player) event.getWhoClicked();
                int slot = event.getRawSlot(); boolean shift = event.isShiftClick(), right = event.isRightClick();
                Bukkit.getScheduler().runTask(plugin, () -> {
                    if (player.getOpenInventory().getTopInventory() == controller.inventory()
                            && controller.registered() && controller.machine().canAccess(player.getUniqueId().toString())) {
                        try { MachineControls.click(controller, player, slot, shift, right); }
                        catch (IllegalArgumentException ex) { player.sendMessage(ex.getMessage()); }
                    }
                });
                return;
            }
            if (layout.hasProgress() && layout.progressAnchors().stream()
                    .anyMatch(cell -> cell.index() == event.getRawSlot())) {
                event.setCancelled(true);
                Player player = (Player) event.getWhoClicked();
                // 新顶层菜单要等当前点击事件结束，否则客户端仍会把点击投到旧机器容器。
                Bukkit.getScheduler().runTask(this.plugin,
                        () -> controller.openRecipeBrowser(player));
                return;
            }
            ItemSlotKind kind = controller.handler().kindAt(event.getRawSlot());
            boolean anchor = GuiText.isOverlay(controller.inventory().getItem(event.getRawSlot()));
            if (kind == null || anchor) {
                event.setCancelled(true);
                refresh((Player) event.getWhoClicked());
                return;
            }
            if (kind == ItemSlotKind.OUTPUT) {
                // 产物：空手/Shift 取出可以，往格子里放不行；双击会把输入槽的同种料也收走，直接拦。
                boolean placing = event.getCursor() != null && !event.getCursor().getType().isAir();
                if (placing || event.getClick() == ClickType.DOUBLE_CLICK
                        || event.getClick() == ClickType.NUMBER_KEY
                        || event.getClick() == ClickType.SWAP_OFFHAND) {
                    event.setCancelled(true);
                    refresh((Player) event.getWhoClicked());
                    return;
                }
            }
        } else if (event.isShiftClick() && event.getClickedInventory() instanceof org.bukkit.inventory.PlayerInventory) {
            // 玩家背包 Shift 点机器：只往输入/灌注格塞，塞不下就取消。
            if (quickMoveIntoMachine(controller, event)) {
                refresh((Player) event.getWhoClicked());
                return;
            }
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onDrag(InventoryDragEvent event) {
        MachineBlockEntityController controller = controllerOf(event.getInventory());
        if (controller == null) {
            return;
        }
        if (!controller.machine().canAccess(event.getWhoClicked().getUniqueId().toString())) {
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
            return;
        }
        for (int raw : event.getRawSlots()) {
            if (raw < controller.inventory().getSize()) {
                ItemSlotKind kind = controller.handler().kindAt(raw);
                if (kind == null || kind == ItemSlotKind.OUTPUT
                        || GuiText.isOverlay(controller.inventory().getItem(raw))) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    private static MachineBlockEntityController controllerOf(Inventory inventory) {
        if (inventory != null && inventory.getHolder() instanceof MachineBlockEntityController.MachineHolder holder) {
            return holder.controller();
        }
        return null;
    }

    private static boolean quickMoveIntoMachine(MachineBlockEntityController controller, InventoryClickEvent event) {
        ItemStack source = event.getCurrentItem();
        if (source == null || source.getType().isAir()) {
            return false;
        }
        Inventory machine = controller.inventory();
        for (int index = 0; index < machine.getSize(); index++) {
            if (!controller.handler().inputLike(index)) {
                continue;
            }
            ItemStack target = machine.getItem(index);
            if (target == null || target.getType().isAir()) {
                machine.setItem(index, source.clone());
                event.getClickedInventory().setItem(event.getSlot(), null);
                event.setCancelled(true);
                return true;
            }
            if (target.isSimilar(source) && target.getAmount() < target.getMaxStackSize()) {
                int move = Math.min(source.getAmount(), target.getMaxStackSize() - target.getAmount());
                target.setAmount(target.getAmount() + move);
                if (source.getAmount() == move) {
                    event.getClickedInventory().setItem(event.getSlot(), null);
                } else {
                    source.setAmount(source.getAmount() - move);
                }
                event.setCancelled(true);
                return true;
            }
        }
        return false;
    }

    private static void refresh(Player player) {
        if (player != null) {
            player.updateInventory();
        }
    }
}
