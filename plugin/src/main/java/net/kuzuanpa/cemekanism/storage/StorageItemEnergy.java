package net.kuzuanpa.cemekanism.storage;

import mekcore.machine.MachineTier;
import mekcore.spec.StorageProfile;
import mekcore.spec.V10Tables;
import net.kuzuanpa.cemekanism.platform.BukkitItems;
import net.kuzuanpa.cemekanism.platform.CompoundDataTag;
import net.momirealms.craftengine.libraries.nbt.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

/** EnergyItems' cube adapter uses the same machine snapshot as placement and packed drops. */
public final class StorageItemEnergy {
    private StorageItemEnergy() { }
    private static StorageProfile profile(ItemStack item) {
        if (StoredItems.empty(item)) return null;
        String id = BukkitItems.idOf(item);
        for (MachineTier tier : MachineTier.values()) if (id.equals("mek:" + tier.name().toLowerCase() + "_energy_cube")) return V10Tables.energyCube(tier);
        return null;
    }
    public static boolean isCube(ItemStack item) { return profile(item) != null; }
    private static CompoundTag root(ItemStack item) {
        byte[] bytes = item.getItemMeta().getPersistentDataContainer().get(StorageListener.MACHINE, PersistentDataType.BYTE_ARRAY);
        return bytes == null ? new CompoundTag() : NBT.readCompressed(bytes, false);
    }
    public static long stored(ItemStack item) {
        if (!isCube(item)) return 0;
        var root = root(item); var machine = root.getCompound("cemekanism:machine"); if (machine == null) return 0;
        var state = machine.getCompound("state"); if (state == null) return 0;
        var tanks = state.getCompound("tanks"); if (tanks == null) return 0;
        var energy = tanks.getCompound("energy"); return energy == null ? 0 : energy.getLong("amount",0);
    }
    public static long charge(ItemStack item, long request) {
        StorageProfile p = profile(item); if (p == null || request <= 0 || item.getAmount() != 1) return 0;
        long old = stored(item); long amount = Math.min(Math.min(request,p.ratePerTick()),Math.max(0,p.capacityJ()-old));
        if (amount == 0) return 0;
        var root = root(item); var tag = new CompoundDataTag(root).child("cemekanism:machine").child("state");
        tag.putString("blueprint",BukkitItems.idOf(item)); tag.putString("factory_tier","SINGLE"); tag.child("tanks").child("energy").putLong("amount",old+amount);
        var meta=item.getItemMeta(); meta.getPersistentDataContainer().set(StorageListener.MACHINE,PersistentDataType.BYTE_ARRAY,NBT.writeCompressed(root,false));
        meta.setMaxStackSize(1); item.setItemMeta(meta); return amount;
    }
}
