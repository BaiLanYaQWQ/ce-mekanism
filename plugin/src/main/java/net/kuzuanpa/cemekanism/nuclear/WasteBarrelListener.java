package net.kuzuanpa.cemekanism.nuclear;

import mekcore.chemical.Chemical;
import mekcore.chemical.Chemicals;
import mekcore.chemical.Radiation;
import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.kuzuanpa.cemekanism.machine.MachineBlockEntityController;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

/** 废料桶被拆除时清空罐并把存量折算成一次辐射泄漏。 */
public final class WasteBarrelListener implements Listener {

    private final CeMekanismPlugin plugin;

    public WasteBarrelListener(CeMekanismPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        var block = event.getBlock();
        leak(block);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onExplode(EntityExplodeEvent event) {
        for (org.bukkit.block.Block block : java.util.List.copyOf(event.blockList())) {
            leak(block);
        }
    }

    void leak(org.bukkit.block.Block block) {
        MachineBlockEntityController controller = this.plugin.machineAtQuiet(
                block.getWorld(), block.getX(), block.getY(), block.getZ());
        if (controller == null || !WasteBarrelLogic.ID.equals(controller.blockId())) {
            return;
        }
        var tank = controller.machine().tank("stored").orElse(null);
        if (tank == null || tank.amount() <= 0 || tank.storedId() == null) {
            return;
        }
        Chemical chemical = Chemicals.registry().find(tank.storedId()).orElse(null);
        if (chemical != null) {
            double magnitude = Radiation.dose(chemical, tank.amount());
            this.plugin.radiation(block.getWorld()).pulse(new Location(block.getWorld(),
                    block.getX() + 0.5, block.getY() + 0.5, block.getZ() + 0.5), magnitude);
        }
        controller.setSuppressStorageDrops(true);
        tank.set(null, 0);
        controller.markChanged();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getHand() != EquipmentSlot.HAND
                || !event.getPlayer().isSneaking()) {
            return;
        }
        var held = event.getItem();
        if (held != null && !held.getType().isAir()) {
            return;
        }
        var block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        MachineBlockEntityController controller = this.plugin.machineAtQuiet(
                block.getWorld(), block.getX(), block.getY(), block.getZ());
        if (controller == null || !WasteBarrelLogic.ID.equals(controller.blockId())
                || !(controller.machine().blueprint().logic() instanceof WasteBarrelLogic logic)) {
            return;
        }
        event.setCancelled(true);
        logic.toggleOutput(controller.machine());
        controller.markChanged();
        event.getPlayer().sendActionBar(net.kyori.adventure.text.Component.text("向下输出："
                + (logic.state(controller.machine()).outputDown ? "开" : "关")));
    }
}
