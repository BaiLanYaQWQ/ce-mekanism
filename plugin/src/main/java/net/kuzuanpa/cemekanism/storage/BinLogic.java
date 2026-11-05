package net.kuzuanpa.cemekanism.storage;

import mekcore.machine.*;
import mekcore.persist.DataTag;
import net.kuzuanpa.cemekanism.machine.MachineBlockEntityController;
import org.bukkit.inventory.ItemStack;
import java.util.Map;
import java.util.WeakHashMap;

/** Single item type bulk storage, plus physical input/output staging for hoppers and transporters. */
public final class BinLogic implements MachineLogic {
    private final Map<Machine, CountedStore> stores = new WeakHashMap<>();
    public CountedStore store(Machine m) {
        return stores.computeIfAbsent(m, key -> new CountedStore(capacity(m.blueprint().id()), 1));
    }
    private static long capacity(String id) {
        if (id.contains(":ultimate_")) return 262144;
        if (id.contains(":elite_")) return 32768;
        if (id.contains(":advanced_")) return 8192;
        return 4096;
    }
    public long energyPerTick(Machine m) { return 0; }
    public boolean needsTick(Machine m) {
        var c = StorageRuntime.of(m); if (c == null) return false;
        ItemStack in = c.slotContent("in"), out = c.slotContent("out"); CountedStore s = store(m);
        return !StoredItems.empty(in) && s.insert(StoredItems.key(in), in.getAmount(), false) > 0
                || s.total() > 0 && StoredItems.empty(out);
    }
    public long tick(Machine m) {
        var c = StorageRuntime.of(m); if (c == null) return 0;
        CountedStore s = store(m); ItemStack in = c.slotContent("in");
        if (!StoredItems.empty(in)) {
            int n = (int) s.insert(StoredItems.key(in), in.getAmount(), true);
            if (n > 0) { in = in.clone(); in.setAmount(in.getAmount() - n); c.putInSlot("in", in.getAmount() == 0 ? null : in); }
        }
        flush(c, s); c.markChanged(); m.setState(WorkState.RUNNING); return 0;
    }
    static void flush(MachineBlockEntityController c, CountedStore s) {
        ItemStack out = c.slotContent("out");
        for (var e : s.entries().entrySet()) {
            ItemStack item = StoredItems.item(e.getKey(), 1);
            if (!StoredItems.empty(out) && !out.isSimilar(item)) continue;
            int old = StoredItems.empty(out) ? 0 : out.getAmount();
            int n = (int) Math.min(e.getValue(), item.getMaxStackSize() - old);
            if (n > 0) { item.setAmount(old + n); c.putInSlot("out", item); s.extract(e.getKey(), n, true); }
            return;
        }
    }
    public void save(Machine m, DataTag tag) { store(m).save(tag.child("buffer")); }
    public void load(Machine m, DataTag tag) { store(m).load(tag.child("buffer")); }
}
