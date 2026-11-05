package net.kuzuanpa.cemekanism.storage;

import net.kuzuanpa.cemekanism.platform.BukkitItems;
import net.momirealms.craftengine.libraries.nbt.NBT;
import org.bukkit.NamespacedKey;
import org.bukkit.event.*;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

/** Tier upgrades retain energy, fluids, bin contents and removable-drive identity/data. */
public final class StorageCrafting implements Listener {
    private static final NamespacedKey DRIVE_CONTENT = new NamespacedKey("cemekanism", "qio_contents");
    private static final NamespacedKey DRIVE_ID = new NamespacedKey("cemekanism", "qio_drive_id");
    private static String family(String id) {
        if (QioService.TIERS.containsKey(id)) return "drive";
        for (String suffix : new String[]{"_energy_cube", "_fluid_tank", "_chemical_tank", "_bin"}) if (id.endsWith(suffix)) return suffix;
        return "";
    }
    static boolean carriesContents(ItemStack item) {
        return !StoredItems.empty(item) && (item.getItemMeta().getPersistentDataContainer().has(StorageListener.MACHINE)
                || item.getItemMeta().getPersistentDataContainer().has(DRIVE_CONTENT)
                || item.getItemMeta().getPersistentDataContainer().has(DRIVE_ID));
    }
    public static ItemStack upgrade(ItemStack source, ItemStack target) {
        String from = family(BukkitItems.idOf(source)), to = family(BukkitItems.idOf(target));
        if (from.isEmpty() || !from.equals(to)) throw new IllegalArgumentException("有内容的存储物品不能用于此配方");
        ItemStack result = target.clone(); var src = source.getItemMeta().getPersistentDataContainer(); var meta = result.getItemMeta(); var dst = meta.getPersistentDataContainer();
        byte[] machine = src.get(StorageListener.MACHINE, PersistentDataType.BYTE_ARRAY);
        if (machine != null) {
            var root = NBT.readCompressed(machine, false);
            root.getCompound("cemekanism:machine").getCompound("state").putString("blueprint", BukkitItems.idOf(target));
            dst.set(StorageListener.MACHINE, PersistentDataType.BYTE_ARRAY, NBT.writeCompressed(root, false));
        }
        byte[] drive = src.get(DRIVE_CONTENT, PersistentDataType.BYTE_ARRAY); String id = src.get(DRIVE_ID, PersistentDataType.STRING);
        if (drive != null) dst.set(DRIVE_CONTENT, PersistentDataType.BYTE_ARRAY, drive);
        if (id != null) dst.set(DRIVE_ID, PersistentDataType.STRING, id);
        NamespacedKey countKey = new NamespacedKey("cemekanism", "qio_drive_count");
        Long count = src.get(countKey, PersistentDataType.LONG);
        if (count != null) dst.set(countKey, PersistentDataType.LONG, count);
        meta.setMaxStackSize(1); result.setItemMeta(meta); return result;
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void prepare(PrepareItemCraftEvent e) {
        ItemStack result = e.getInventory().getResult(); if (StoredItems.empty(result)) return;
        // QIO upgrades merge all four disks through StorageListener, including their combined type limit.
        if (QioService.TIERS.containsKey(BukkitItems.idOf(result))) return;
        int stored = 0; ItemStack source = null;
        for (ItemStack item : e.getInventory().getMatrix()) if (carriesContents(item)) { stored++; source = item; }
        if (stored == 0) return;
        if (stored > 1) { e.getInventory().setResult(null); return; }
        try { e.getInventory().setResult(upgrade(source, result)); }
        catch (IllegalArgumentException exception) { e.getInventory().setResult(null); }
    }
}
