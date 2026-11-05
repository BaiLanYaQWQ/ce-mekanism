package net.kuzuanpa.cemekanism.storage;

import mekcore.machine.*;
import mekcore.persist.DataTag;
import mekcore.resource.ResourceStack;
import mekcore.resource.Transaction;
import net.kuzuanpa.cemekanism.machine.MachineBlockEntityController;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import java.util.*;

/** Connected source frontier, bounded work per tick, filter upgrade produces 10 mB heavy water per source. */
public final class PumpLogic implements MachineLogic {
    public static final class State {
        public int progress;
        public final LinkedHashSet<String> frontier = new LinkedHashSet<>();
    }
    private final Map<Machine, State> states = new WeakHashMap<>();
    private State state(Machine m) { return states.computeIfAbsent(m, k -> new State()); }
    private String pos(Block b) { return b.getX() + "," + b.getY() + "," + b.getZ(); }
    private Block source(Machine m) {
        var c = StorageRuntime.of(m); if (c == null) return null; State s = state(m);
        Block below = InventoryAccess.neighbour(c, Direction.DOWN);
        if (below != null && sourceType(below)) return below;
        int scanned = 0;
        for (String key : new ArrayList<>(s.frontier)) {
            if (++scanned > 128) break;
            String[] xyz = key.split(","); int x = Integer.parseInt(xyz[0]), y = Integer.parseInt(xyz[1]), z = Integer.parseInt(xyz[2]);
            if (!c.world().isChunkLoaded(x >> 4, z >> 4)) continue;
            Block old = c.world().getBlockAt(x, y, z);
            if (sourceType(old)) return old;
            for (BlockFace face : List.of(BlockFace.DOWN, BlockFace.UP, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST)) {
                int nx = x + face.getModX(), ny = y + face.getModY(), nz = z + face.getModZ();
                if (ny < c.world().getMinHeight() || ny >= c.world().getMaxHeight()
                        || (long)(nx-c.x())*(nx-c.x()) + (long)(ny-c.y())*(ny-c.y()) + (long)(nz-c.z())*(nz-c.z()) > 6400
                        || !c.world().isChunkLoaded(nx >> 4, nz >> 4)) continue;
                Block next = c.world().getBlockAt(nx, ny, nz); if (sourceType(next)) return next;
            }
            s.frontier.remove(key);
        }
        return null;
    }
    private boolean sourceType(Block block) {
        return (block.getType() == Material.WATER || block.getType() == Material.LAVA)
                && block.getBlockData() instanceof Levelled level && level.getLevel() == 0;
    }
    private ResourceStack output(Machine m, Block source) {
        if (source.getType() == Material.WATER && m.upgrades().count("mek:filter_upgrade") > 0) return ResourceStack.fluid("mek:heavy_water", 10);
        return ResourceStack.fluid(source.getType() == Material.WATER ? "minecraft:water" : "minecraft:lava", 1000);
    }
    public boolean needsTick(Machine m) {
        Block b = source(m); if (b == null) return false;
        ResourceStack out = output(m, b); return m.handler("fluid").insert(out, Transaction.SIMULATE) == out.amount();
    }
    public long energyPerTick(Machine m) { return needsTick(m) ? m.energyPerTick() : 0; }
    public long tick(Machine m) {
        var c = StorageRuntime.of(m); Block source = source(m); if (c == null || source == null) return 0;
        ResourceStack out = output(m, source);
        if (m.handler("fluid").insert(out, Transaction.SIMULATE) != out.amount()) { m.setState(WorkState.OUTPUT_BLOCKED); return 0; }
        State s = state(m); m.setState(WorkState.RUNNING);
        if (++s.progress < Math.max(1, (int)Math.ceil(19 / m.progressRate()))) return 0;
        // The default upstream setting consumes source blocks; water can regenerate naturally.
        m.handler("fluid").insert(out, Transaction.EXECUTE); s.frontier.add(pos(source)); source.setType(Material.AIR, true);
        s.progress = 0; c.markChanged(); return 0;
    }
    public void save(Machine m, DataTag t) {
        State s = state(m); t.putInt("progress", s.progress); t.remove("frontier");
        for (String key : s.frontier) t.newElement("frontier").putString("pos", key);
    }
    public void load(Machine m, DataTag t) {
        State s = state(m); s.progress = t.getInt("progress", 0); s.frontier.clear();
        for (DataTag row : t.list("frontier")) s.frontier.add(row.getString("pos", "0,0,0"));
    }
}
