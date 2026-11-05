package net.kuzuanpa.cemekanism.transmitter;

import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import org.bukkit.World;

import java.util.LinkedHashMap;
import java.util.Map;

/** 一世界一份物流（文档 06 第七节第 1 条）：按世界惰性建，插件停用时统一把任务摘干净。 */
public final class LogisticsWorlds {

    private final CeMekanismPlugin plugin;
    private final Map<String, Logistics> byWorld = new LinkedHashMap<>();
    private boolean closed;

    public LogisticsWorlds(CeMekanismPlugin plugin) {
        this.plugin = plugin;
    }

    public Logistics of(World world) {
        return this.byWorld.computeIfAbsent(world.getName(), name -> {
            Logistics logistics = new Logistics(this.plugin, world);
            if (!this.closed) {
                logistics.start();                    // 停服之后还会有人 onUnload：那时别再挂任务
            }
            return logistics;
        });
    }

    public Map<String, Logistics> all() {
        return Map.copyOf(this.byWorld);
    }

    public void stopAll() {
        this.closed = true;
        this.byWorld.values().forEach(Logistics::stop);
        this.byWorld.clear();
    }
}
