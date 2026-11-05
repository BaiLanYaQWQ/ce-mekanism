package net.kuzuanpa.cemekanism.storage;

import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.kuzuanpa.cemekanism.machine.MachineBlockEntityController;
import net.kuzuanpa.cemekanism.platform.BukkitItems;
import net.momirealms.craftengine.bukkit.api.event.CustomBlockBreakEvent;
import net.momirealms.craftengine.bukkit.api.event.CustomBlockPlaceEvent;
import net.momirealms.craftengine.libraries.nbt.*;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.*;
import org.bukkit.persistence.PersistentDataType;

/** Storage block drops carry their complete state; removable QIO disks already carry their own data. */
public final class StorageListener implements Listener {
    static final NamespacedKey MACHINE = new NamespacedKey("cemekanism", "stored_machine");
    static final NamespacedKey PORTABLE = new NamespacedKey("cemekanism", "qio_frequency");
    private final CeMekanismPlugin plugin;
    public StorageListener(CeMekanismPlugin plugin) { this.plugin = plugin; }
    public static boolean isB5(MachineBlockEntityController c) {
        String id = c.blockId(); return PersonalStorageLogic.isStorage(id) || id.endsWith("_energy_cube") || id.endsWith("_fluid_tank") || id.endsWith("_chemical_tank")
                || id.endsWith("_bin") || id.startsWith("mek:qio_") || id.equals("mek:digital_miner") || id.equals("mek:electric_pump") || id.equals("mek:logistical_sorter")
                || id.equals("mek:resistive_heater");
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void protect(CustomBlockBreakEvent e) {
        var b = e.bukkitBlock(); var c = plugin.machineAtQuiet(b.getWorld(), b.getX(), b.getY(), b.getZ());
        if (c != null && isB5(c) && !c.machine().canAccess(e.getPlayer().getUniqueId().toString())) e.setCancelled(true);
    }
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void broken(CustomBlockBreakEvent e) {
        var b = e.bukkitBlock(); var c = plugin.machineAtQuiet(b.getWorld(), b.getX(), b.getY(), b.getZ());
        if (c == null || !isB5(c) || c.suppressStorageDrops()) return;
        ItemStack item = BukkitItems.create(c.blockId()); if (item == null) return;
        CompoundTag tag = new CompoundTag(); c.saveCustomData(tag);
        var meta = item.getItemMeta(); meta.getPersistentDataContainer().set(MACHINE, PersistentDataType.BYTE_ARRAY, NBT.writeCompressed(tag, false));
        meta.setMaxStackSize(1); item.setItemMeta(meta);
        c.setSuppressStorageDrops(true); e.setDropItems(false);
        b.getWorld().dropItemNaturally(b.getLocation().add(.5, .5, .5), item);
    }
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void placed(CustomBlockPlaceEvent e) {
        ItemStack held = e.getPlayer().getInventory().getItem(e.hand() == net.momirealms.craftengine.core.entity.player.InteractionHand.MAIN_HAND
                ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND);
        if (StoredItems.empty(held)) return;
        byte[] data = held.getItemMeta().getPersistentDataContainer().get(MACHINE, PersistentDataType.BYTE_ARRAY);
        if (data == null) return;
        var b = e.bukkitBlock();
        // CE writes the placed state after firing its event. Restore once that state is visible.
        Bukkit.getScheduler().runTask(plugin, () -> {
            var c = plugin.machineAtQuiet(b.getWorld(), b.getX(), b.getY(), b.getZ()); if (c == null) return;
            c.loadCustomData(NBT.readCompressed(data, false)); StorageRuntime.resize(c.machine());
            plugin.logistics(b.getWorld()).detachMachine(c); plugin.logistics(b.getWorld()).attachMachine(c); c.markChanged();
        });
    }
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void clicked(InventoryClickEvent e) {
        var holder = e.getInventory().getHolder();
        if (!(holder instanceof QioDashboard) && !(holder instanceof QioFrequencyGui)
                && !(holder instanceof DigitalMinerFilterGui.Holder)) return;
        // Let players pick up and return their sample in their own inventory.
        // Shift moves, hotbar swaps and double-click collection stay intercepted below.
        if (holder instanceof DigitalMinerFilterGui.Holder
                && e.getClickedInventory() == e.getWhoClicked().getInventory()
                && (e.getClick() == ClickType.LEFT || e.getClick() == ClickType.RIGHT)) return;
        e.setCancelled(true); if (!(e.getWhoClicked() instanceof Player p)) return;
        // Only explicit normal clicks are actions. Creative clone, drop, swaps and collection stay cancelled.
        if (e.getClick() != ClickType.LEFT && e.getClick() != ClickType.RIGHT
                && e.getClick() != ClickType.SHIFT_LEFT && e.getClick() != ClickType.SHIFT_RIGHT) return;
        // The miner editor reads the cursor as a non-consuming filter/replacement sample.
        if (!(holder instanceof DigitalMinerFilterGui.Holder) && !StoredItems.empty(e.getCursor())) return;
        var inventory = e.getInventory(); int raw = e.getRawSlot(), slot = e.getSlot();
        String expectedKey = holder instanceof QioDashboard view ? view.keyAt(raw) : null;
        DigitalMinerFilterGui miner = DigitalMinerFilterGui.of(inventory);
        String expectedRule = miner == null ? null : miner.ruleAt(raw);
        boolean bottom = e.getClickedInventory() == p.getInventory(), right = e.isRightClick();
        boolean shift = e.isShiftClick();
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (p.getOpenInventory().getTopInventory() != inventory
                    || miner == null && !StoredItems.empty(p.getItemOnCursor())) return;
            try {
                if (holder instanceof QioDashboard view) {
                    if (bottom) view.deposit(p, slot); else view.click(p, raw, right, expectedKey);
                } else if (holder instanceof QioFrequencyGui frequency && !bottom) {
                    frequency.click(p, raw);
                } else if (miner != null && !bottom) {
                    miner.click(p, raw, shift, right, expectedRule);
                }
            } catch (IllegalArgumentException | IllegalStateException ex) { p.closeInventory(); p.sendMessage(ex.getMessage()); }
        });
    }
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void dragged(InventoryDragEvent e) {
        if (e.getInventory().getHolder() instanceof QioDashboard
                || e.getInventory().getHolder() instanceof QioFrequencyGui
                || e.getInventory().getHolder() instanceof DigitalMinerFilterGui.Holder) e.setCancelled(true);
    }
    private static boolean driveUpgrade(ItemStack result) {
        if (StoredItems.empty(result)) return false;
        String id = BukkitItems.idOf(result);
        return QioService.TIERS.containsKey(id) && !id.equals("mek:qio_drive_base");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void prepareDriveUpgrade(PrepareItemCraftEvent e) {
        ItemStack result = e.getInventory().getResult(); if (!driveUpgrade(result)) return;
        try { e.getInventory().setResult(plugin.qio().mergeDrives(result, e.getInventory().getMatrix(), false)); }
        catch (IllegalArgumentException | IllegalStateException ex) { e.getInventory().setResult(null); }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void craftDriveUpgrade(CraftItemEvent e) {
        ItemStack result = e.getInventory().getResult(); if (!driveUpgrade(result)) return;
        e.setCancelled(true);
        if (!(e.getWhoClicked() instanceof Player player) || !StoredItems.empty(e.getCursor())) return;
        if (e.getClick() != ClickType.LEFT && e.getClick() != ClickType.RIGHT
                && e.getClick() != ClickType.SHIFT_LEFT && e.getClick() != ClickType.SHIFT_RIGHT) return;
        try {
            InventoryAccess target = new InventoryAccess(player.getInventory(), java.util.stream.IntStream.range(0, 36).boxed().toList(), () -> { });
            // Upgraded drives have unique data and need an empty slot, even if the preview looks stackable.
            if (e.isShiftClick() && java.util.stream.IntStream.range(0, 36).noneMatch(i -> StoredItems.empty(player.getInventory().getItem(i)))) return;
            ItemStack[] matrix = e.getInventory().getMatrix();
            ItemStack upgraded = plugin.qio().mergeDrives(result, matrix, true);
            for (int i = 0; i < matrix.length; i++) if (!StoredItems.empty(matrix[i])) {
                matrix[i] = matrix[i].clone(); matrix[i].setAmount(matrix[i].getAmount() - 1);
                if (matrix[i].getAmount() == 0) matrix[i] = null;
            }
            e.getInventory().setResult(null); e.getInventory().setMatrix(matrix);
            if (e.isShiftClick()) target.insert(upgraded, 1, true); else player.setItemOnCursor(upgraded);
            Bukkit.getScheduler().runTask(plugin, player::updateInventory);
        } catch (IllegalArgumentException | IllegalStateException ex) { player.sendMessage("QIO 升级：" + ex.getMessage()); }
    }
    // Bukkit pre-cancels no-op air interactions, just as CE's onInteractAir expects.
    @EventHandler(priority = EventPriority.HIGH)
    public void portable(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND || (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK)
                || StoredItems.empty(e.getItem()) || !BukkitItems.idOf(e.getItem()).equals("mek:portable_qio_dashboard")) return;
        if (e.getPlayer().getGameMode() == GameMode.SPECTATOR
                || e.getAction() == Action.RIGHT_CLICK_BLOCK && e.isCancelled()) return;
        String frequency = e.getItem().getItemMeta().getPersistentDataContainer().get(PORTABLE, PersistentDataType.STRING);
        e.setCancelled(true);
        if (frequency == null || e.getPlayer().isSneaking()) QioFrequencyGui.open(plugin, null, e.getPlayer(), 0);
        else QioDashboard.open(plugin, null, frequency, "", e.getPlayer(), 0);
    }
    public static void refundBuffer(MachineBlockEntityController c) {
        if (c.suppressStorageDrops()) return;
        CountedStore store = c.machine().blueprint().logic() instanceof BinLogic bin ? bin.store(c.machine())
                : c.machine().blueprint().logic() instanceof DigitalMinerLogic miner ? miner.state(c.machine()).buffer : null;
        if (store == null || c.world() == null) return;
        for (var e : store.entries().entrySet()) {
            long remaining = e.getValue(); ItemStack template = StoredItems.item(e.getKey(), 1);
            while (remaining > 0) {
                int n = (int)Math.min(remaining, template.getMaxStackSize());
                c.world().dropItemNaturally(new Location(c.world(), c.x()+.5, c.y()+.5, c.z()+.5), StoredItems.item(e.getKey(), n)); remaining -= n;
            }
            store.extract(e.getKey(), e.getValue(), true);
        }
    }
}
