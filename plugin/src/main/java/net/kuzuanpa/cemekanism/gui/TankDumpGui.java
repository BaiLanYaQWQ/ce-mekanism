package net.kuzuanpa.cemekanism.gui;

import mekcore.resource.DumpMode;
import net.kuzuanpa.cemekanism.machine.MachineBlockEntityController;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import java.util.List;

/** Controls virtual buttons only; opening and changing modes never moves player items. */
public final class TankDumpGui implements InventoryHolder {
    private final MachineBlockEntityController controller;
    private final List<String> ports;
    private final Inventory inventory;
    private TankDumpGui(MachineBlockEntityController controller) {
        this.controller = controller;
        this.ports = controller.machine().dumpablePorts();
        this.inventory = Bukkit.createInventory(this, Math.max(27, Math.min(54, ports.size() * 9)), "化学品罐排出设置");
        render();
    }
    public static void open(MachineBlockEntityController controller, Player player) {
        if (controller.registered() && controller.machine().canAccess(player.getUniqueId().toString()))
            player.openInventory(new TankDumpGui(controller).inventory);
    }
    private void render() {
        for (int row = 0; row < ports.size() && row < inventory.getSize() / 9; row++) {
            String name = ports.get(row);
            var tank = controller.machine().tank(name).orElseThrow();
            String resource = tank.resourceId() == null ? "空罐"
                    : mekcore.resource.ResourceNames.nameOf(tank.type(), tank.resourceId());
            for (DumpMode mode : DumpMode.values()) inventory.setItem(row * 9 + 2 + mode.ordinal() * 2,
                    GuiText.button(mode == tank.dumpMode() ? "minecraft:lime_dye" : "minecraft:gray_dye",
                            resource + " · " + mode.label(), switch (mode) {
                                case IDLE -> List.of("保留罐内资源");
                                case DUMPING_EXCESS -> List.of("保留 85%，持续排出多余资源");
                                case DUMPING -> List.of("持续排出罐内资源");
                            }));
        }
    }
    @Override public Inventory getInventory() { return inventory; }
    public static final class Controls implements Listener {
        @EventHandler public void click(InventoryClickEvent event) {
            if (!(event.getInventory().getHolder() instanceof TankDumpGui gui)) return;
            event.setCancelled(true);
            if (!(event.getWhoClicked() instanceof Player player) || !gui.controller.registered()
                    || !gui.controller.machine().canAccess(player.getUniqueId().toString())) { event.getWhoClicked().closeInventory(); return; }
            if (!event.isLeftClick() && !event.isRightClick()) return;
            int slot = event.getRawSlot(), row = slot / 9, column = slot % 9;
            if (slot < 0 || slot >= gui.inventory.getSize() || row >= gui.ports.size()
                    || column != 2 && column != 4 && column != 6) return;
            DumpMode mode = DumpMode.values()[(column - 2) / 2];
            Bukkit.getScheduler().runTask(gui.controller.plugin(), () -> {
                if (player.getOpenInventory().getTopInventory() != gui.inventory || !gui.controller.registered()
                        || !gui.controller.machine().canAccess(player.getUniqueId().toString())) return;
                gui.controller.machine().tank(gui.ports.get(row)).orElseThrow().setDumpMode(mode);
                gui.controller.markChanged(); gui.render();
            });
        }
        @EventHandler public void drag(InventoryDragEvent event) {
            if (event.getInventory().getHolder() instanceof TankDumpGui) event.setCancelled(true);
        }
    }
}
