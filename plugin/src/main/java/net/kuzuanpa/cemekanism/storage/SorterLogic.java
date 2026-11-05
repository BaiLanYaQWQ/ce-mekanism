package net.kuzuanpa.cemekanism.storage;

import mekcore.machine.*;
import mekcore.persist.DataTag;
import net.kuzuanpa.cemekanism.platform.BukkitItems;
import org.bukkit.inventory.ItemStack;
import java.util.Map;
import java.util.WeakHashMap;

/** Moves filtered items from the back inventory to the front inventory or a matching 12-colour transporter. */
public final class SorterLogic implements MachineLogic {
    public static final class State {
        public final ItemSelection filter = new ItemSelection();
        public int colour = -1, ticks, nextSlot;
        public boolean single, roundRobin;
        public State() { filter.inverse = true; }
    }
    private final Map<Machine, State> states = new WeakHashMap<>();
    public State state(Machine m) { return states.computeIfAbsent(m, k -> new State()); }
    public long energyPerTick(Machine m) { return 0; }
    public boolean needsTick(Machine m) { return StorageRuntime.of(m) != null; }
    public long tick(Machine m) {
        var c = StorageRuntime.of(m); if (c == null) return 0; State s = state(m);
        if (++s.ticks < 10) return 0; s.ticks = 0;
        var source = InventoryAccess.adjacent(c, c.facing().opposite(), false); if (source == null) return 0;
        var dest = InventoryAccess.adjacent(c, c.facing(), true);
        var block = InventoryAccess.neighbour(c, c.facing());
        var pipe = block == null ? null : c.plugin().transmitterAt(c.world(), block.getX(), block.getY(), block.getZ());
        for (int i = 0; i < source.slots().size(); i++) {
            int offset = (i + (s.roundRobin ? s.nextSlot : 0)) % source.slots().size(); int slot = source.slots().get(offset);
            ItemStack item = source.inventory().getItem(slot); if (StoredItems.empty(item) || !s.filter.matches(item)) continue;
            int request = s.single ? 1 : item.getAmount(); int n = 0;
            if (dest != null && s.colour == -1) n = dest.insert(item, request, true);
            else if (pipe != null && pipe.itemPipe()) {
                n = c.plugin().logistics(c.world()).insertSorted(pipe, c.facing().opposite(), s.colour, item, request);
            }
            if (n > 0) { source.take(slot, n); s.nextSlot = (offset + 1) % source.slots().size(); c.markChanged(); m.setState(WorkState.RUNNING); return 0; }
        }
        m.setState(WorkState.IDLE); return 0;
    }
    public void save(Machine m, DataTag t) {
        State s = state(m); t.putInt("colour", s.colour); t.putInt("next_slot", s.nextSlot);
        t.putBoolean("single", s.single); t.putBoolean("round_robin", s.roundRobin); s.filter.save(t.child("filter"));
    }
    public void load(Machine m, DataTag t) {
        State s = state(m); s.colour = t.getInt("colour", -1); s.nextSlot = t.getInt("next_slot", 0);
        s.single = t.getBoolean("single", false); s.roundRobin = t.getBoolean("round_robin", false); if (t.has("filter")) s.filter.load(t.child("filter"));
    }
}
