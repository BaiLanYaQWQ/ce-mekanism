package net.kuzuanpa.cemekanism.storage;

import net.kuzuanpa.cemekanism.machine.MachineBlockEntityController;
import org.bukkit.World;
import java.util.*;

/**
 * The anchor upgrade follows the active cursor instead of permanently loading the whole configured
 * square. This mirrors upstream's miner-chunk-plus-target-chunk set while keeping scanning incremental.
 */
final class MiningChunkTickets {
    private record Chunk(World world, int x, int z) { }
    private static final Map<MachineBlockEntityController, Set<Chunk>> HELD = new IdentityHashMap<>();
    private static final Map<Chunk, Integer> REFERENCES = new HashMap<>();
    private MiningChunkTickets() { }
    static void sync(MachineBlockEntityController c) {
        if (!(c.machine().blueprint().logic() instanceof DigitalMinerLogic miner)
                || c.machine().upgrades().count("mek:anchor_upgrade") == 0) { release(c); return; }
        var state = miner.state(c.machine());
        Set<Chunk> wanted=new HashSet<>();
        wanted.add(new Chunk(c.world(), c.x() >> 4, c.z() >> 4));
        if (state.running && !state.scan().done()) {
            var target = state.scan().current();
            wanted.add(new Chunk(c.world(), (c.x() + target.x()) >> 4, (c.z() + target.z()) >> 4));
        }
        Set<Chunk> previous=HELD.getOrDefault(c,Set.of());
        if(previous.equals(wanted))return;
        for(Chunk chunk:previous)if(!wanted.contains(chunk))remove(c,chunk);
        for(Chunk chunk:wanted)if(!previous.contains(chunk)) {
            int refs=REFERENCES.getOrDefault(chunk,0);
            if(refs==0)chunk.world.addPluginChunkTicket(chunk.x,chunk.z,c.plugin());
            REFERENCES.put(chunk,refs+1);
        }
        HELD.put(c,wanted);
    }
    static void release(MachineBlockEntityController c) {
        for(Chunk chunk:HELD.getOrDefault(c,Set.of()))remove(c,chunk);
        HELD.remove(c);
    }
    private static void remove(MachineBlockEntityController c,Chunk chunk) {
        int refs=REFERENCES.getOrDefault(chunk,0);
        if(refs<=1){REFERENCES.remove(chunk);chunk.world.removePluginChunkTicket(chunk.x,chunk.z,c.plugin());}
        else REFERENCES.put(chunk,refs-1);
    }
}
