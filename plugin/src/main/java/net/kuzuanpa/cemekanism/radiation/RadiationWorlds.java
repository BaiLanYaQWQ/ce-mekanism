package net.kuzuanpa.cemekanism.radiation;

import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;

/** 一世界一份辐射账；按世界惰性建，停服时统一摘任务。 */
public final class RadiationWorlds {
    private final CeMekanismPlugin plugin;
    private final Map<String, RadiationWorld> byWorld = new LinkedHashMap<>();
    private boolean closed;

    public RadiationWorlds(CeMekanismPlugin plugin) {
        this.plugin = plugin;
    }

    public RadiationWorld of(World world) {
        return byWorld.computeIfAbsent(world.getName(), name -> {
            RadiationWorld radiation = new RadiationWorld(plugin, world);
            if (closed) {
                radiation.stop();
            }
            return radiation;
        });
    }

    public Map<String, RadiationWorld> all() {
        return Map.copyOf(byWorld);
    }

    public void stopAll() {
        closed = true;
        byWorld.values().forEach(RadiationWorld::stop);
        byWorld.clear();
    }

    /** `/mek radiation removeAll`：在线 + 离线玩家一起清（台账 P28）。 */
    public int removeAll() {
        for (RadiationWorld radiation : byWorld.values()) {
            radiation.clearAll();
        }
        return RadiationStore.clearAll();
    }

    public void savePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            RadiationWorld radiation = byWorld.get(player.getWorld().getName());
            if (radiation != null) {
                radiation.saveAll();
            }
        }
        RadiationStore.flush();
    }
}
