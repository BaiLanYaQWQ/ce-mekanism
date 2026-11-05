package net.kuzuanpa.cemekanism.radiation;

import mekcore.chemical.DoseTracker;
import mekcore.sim.TickAffinity;
import mekcore.sim.Tickable;
import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.kuzuanpa.cemekanism.platform.BukkitItems;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.DoubleSupplier;

/**
 * 一个世界的放射源与玩家剂量。源是"现算 supplier"：机器/多方块的罐内容变了不用重登记；
 * 每 {@code interval_ticks} 跑一次，主线程（要碰玩家、伤害与 PDC）。
 */
public final class RadiationWorld {
    private record Source(int x, int y, int z, DoubleSupplier magnitude) {
    }

    private final CeMekanismPlugin plugin;
    private final World world;
    private final Map<String, Source> sources = new LinkedHashMap<>();
    private final Map<UUID, DoseTracker> doses = new LinkedHashMap<>();
    private final Tickable task;
    private int runs;

    RadiationWorld(CeMekanismPlugin plugin, World world) {
        this.plugin = plugin;
        this.world = world;
        this.task = new Tickable() {
            @Override
            public boolean needsTick() {
                return RadiationConfig.enabled() && !RadiationWorld.this.world.getPlayers().isEmpty();
            }

            @Override
            public long tick() {
                RadiationWorld.this.tick();
                return 0;
            }

            @Override
            public String describe() {
                return "辐射源 " + RadiationWorld.this.sources.size()
                        + "，玩家 " + RadiationWorld.this.doses.size();
            }
        };
        plugin.scheduler().register("radiation:" + world.getName(), task, TickAffinity.MAIN,
                RadiationConfig.intervalTicks());
    }

    public World world() {
        return world;
    }

    public void register(String key, int x, int y, int z, DoubleSupplier magnitude) {
        sources.put(key, new Source(x, y, z, magnitude));
    }

    public void unregister(String key) {
        sources.remove(key);
    }

    public int sourceCount() {
        return sources.size();
    }

    public void join(Player player) {
        DoseTracker tracker = new DoseTracker(RadiationConfig.halfLifeTicks());
        tracker.add(RadiationStore.load(player.getUniqueId()));
        doses.put(player.getUniqueId(), tracker);
    }

    public void quit(Player player) {
        DoseTracker tracker = doses.remove(player.getUniqueId());
        if (tracker != null) {
            RadiationStore.save(player.getUniqueId(), tracker.microSv());
        }
    }

    public DoseTracker dose(Player player) {
        return doses.computeIfAbsent(player.getUniqueId(), id -> {
            DoseTracker tracker = new DoseTracker(RadiationConfig.halfLifeTicks());
            tracker.add(RadiationStore.load(player.getUniqueId()));
            return tracker;
        });
    }

    /** 一次性的"打开放射性容器"剂量：强度 × open_factor，和上游一样只在动作时加。 */
    public void containerOpened(Player player, double magnitudeMicroSvPerHour) {
        if (magnitudeMicroSvPerHour <= 0) {
            return;
        }
        dose(player).add(magnitudeMicroSvPerHour * RadiationConfig.openFactor()
                * (1.0 - resistance(player)));
    }

    /**
     * 一次性泄漏：按当前距离衰减给玩家加一秒暴露。
     * 用于裂变堆溢出的废料、熔毁和废料桶被破坏，不伪造一个长期环境源。
     */
    public void pulse(Location location, double magnitudeMicroSvPerHour) {
        if (!RadiationConfig.enabled() || magnitudeMicroSvPerHour <= 0) {
            return;
        }
        double maxSq = (double) RadiationConfig.maxDistance() * RadiationConfig.maxDistance();
        for (Player player : this.world.getPlayers()) {
            Location at = player.getLocation();
            double dx = at.getX() - location.getX();
            double dy = at.getY() - location.getY();
            double dz = at.getZ() - location.getZ();
            double distSq = dx * dx + dy * dy + dz * dz;
            if (distSq > maxSq) {
                continue;
            }
            double rate = magnitudeMicroSvPerHour / Math.max(1.0, distSq);
            dose(player).add(rate / 3_600.0 * (1.0 - resistance(player)));
        }
    }

    /** 当前坐标的环境剂量率（μSv/h）：Σ 源强度 / max(1, 距离²)。 */
    public double exposureAt(Location location) {
        double maxSq = (double) RadiationConfig.maxDistance() * RadiationConfig.maxDistance();
        double total = 0.0;
        for (Source source : sources.values()) {
            double dx = location.getX() - (source.x + 0.5);
            double dy = location.getY() - (source.y + 0.5);
            double dz = location.getZ() - (source.z + 0.5);
            double distSq = dx * dx + dy * dy + dz * dz;
            if (distSq > maxSq) {
                continue;
            }
            double magnitude = magnitudeOf(source);
            if (magnitude > 0) {
                total += magnitude / Math.max(1.0, distSq);
            }
        }
        return total;
    }

    /** 附近已登记源的一行行文本（命令与盖革计数器共用）。 */
    public List<String> sourceLines(Location location, int radius) {
        List<String> lines = new ArrayList<>();
        double maxSq = (double) radius * radius;
        for (Map.Entry<String, Source> entry : sources.entrySet()) {
            Source source = entry.getValue();
            double dx = location.getX() - (source.x + 0.5);
            double dy = location.getY() - (source.y + 0.5);
            double dz = location.getZ() - (source.z + 0.5);
            if (dx * dx + dy * dy + dz * dz > maxSq) {
                continue;
            }
            double magnitude = magnitudeOf(source);
            if (magnitude > 0) {
                lines.add(entry.getKey() + " @ " + source.x + "," + source.y + "," + source.z
                        + " → " + format(magnitude));
            }
        }
        return lines;
    }

    public void clearAll() {
        for (Player player : world.getPlayers()) {
            dose(player).clear();
            RadiationStore.save(player.getUniqueId(), 0.0);
            RadiationEffects.clear(player);
        }
        doses.values().forEach(DoseTracker::clear);
    }

    public void saveAll() {
        for (Player player : world.getPlayers()) {
            DoseTracker tracker = doses.get(player.getUniqueId());
            if (tracker != null) {
                RadiationStore.save(player.getUniqueId(), tracker.microSv());
            }
        }
    }

    public void stop() {
        saveAll();
        plugin.scheduler().unregister("radiation:" + world.getName());
        doses.clear();
        sources.clear();
    }

    private void tick() {
        int seconds = Math.max(1, RadiationConfig.intervalTicks() / 20);
        runs++;
        boolean effects = runs % Math.max(1,
                RadiationConfig.effectIntervalTicks() / RadiationConfig.intervalTicks()) == 0;
        for (Player player : world.getPlayers()) {
            DoseTracker tracker = dose(player);
            tracker.tick(RadiationConfig.intervalTicks());
            tracker.add(exposureAt(player.getLocation()) / 3_600.0 * seconds
                    * (1.0 - resistance(player)));
            if (effects) {
                RadiationEffects.apply(player, tracker);
            }
        }
        if (runs % 15 == 0) {
            saveAll();
            RadiationStore.flush();
        }
    }

    private static double magnitudeOf(Source source) {
        try {
            return Math.max(0.0, source.magnitude.getAsDouble());
        } catch (RuntimeException ignored) {
            return 0.0;
        }
    }

    /** 防辐射服四件之和，上限 1（上游 RadiationUtil.getRadiationResistance 的口径）。 */
    private static double resistance(Player player) {
        double total = 0.0;
        for (org.bukkit.inventory.ItemStack armor : player.getInventory().getArmorContents()) {
            if (armor == null || armor.getType().isAir()) {
                continue;
            }
            total += RadiationConfig.shielding().getOrDefault(BukkitItems.idOf(armor), 0.0);
        }
        return Math.min(1.0, total);
    }

    public static String format(double microSvPerHour) {
        if (microSvPerHour >= 1_000_000) {
            return String.format("%.3f Sv/h", microSvPerHour / 1_000_000);
        }
        if (microSvPerHour >= 1_000) {
            return String.format("%.3f mSv/h", microSvPerHour / 1_000);
        }
        return String.format("%.3f μSv/h", microSvPerHour);
    }
}
