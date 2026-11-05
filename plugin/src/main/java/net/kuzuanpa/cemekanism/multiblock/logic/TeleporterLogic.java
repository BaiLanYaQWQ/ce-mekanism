package net.kuzuanpa.cemekanism.multiblock.logic;

import mekcore.multiblock.MultiblockInstance;
import mekcore.multiblock.MultiblockLogic;
import mekcore.persist.DataTag;
import mekcore.resource.Transaction;
import net.kuzuanpa.cemekanism.multiblock.MultiblockContexts;
import net.kuzuanpa.cemekanism.multiblock.MultiblockHosts;
import net.kuzuanpa.cemekanism.multiblock.StructureBox;
import net.kuzuanpa.cemekanism.i18n.I18n;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.BoundingBox;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

/** Same-frequency transport on the host's MAIN scheduler lane. Only successful teleports consume energy. */
public final class TeleporterLogic implements MultiblockLogic {
    public static final class State {
        public String frequency = "";
        // A destination keeps arrivals blocked until they actually leave its portal.
        public final Set<UUID> arrivals = new HashSet<>();
    }

    private final Map<MultiblockInstance, State> states = new WeakHashMap<>();

    public State state(MultiblockInstance instance) {
        return states.computeIfAbsent(instance, ignored -> new State());
    }

    public static TeleporterLogic of(MultiblockHosts.Host host) {
        return host.instance().blueprint().logic() instanceof TeleporterLogic logic ? logic : null;
    }

    public static String normalizeFrequency(String name) {
        String clean = name.strip();
        if (clean.length() > 32 || clean.codePoints().anyMatch(Character::isISOControl))
            throw new IllegalArgumentException(I18n.tr("teleporter.name_length"));
        return clean;
    }

    public void bind(MultiblockHosts.Host host, String frequency) {
        state(host.instance()).frequency = normalizeFrequency(frequency);
        host.markChanged();
    }

    public MultiblockHosts.Host destination(MultiblockHosts.Host source) {
        String frequency = state(source.instance()).frequency;
        if (frequency.isEmpty()) return null;
        Location start = target(source.box());
        return source.plugin().multiblocks().all().stream()
                .filter(host -> host != source && host.formed() && of(host) != null && loaded(host.box())
                        && frequency.equals(of(host).state(host.instance()).frequency))
                // Prefer the nearest portal in this dimension, then a stable portal in another world.
                .min(Comparator.comparingDouble((MultiblockHosts.Host host) -> host.world() == source.world()
                        ? start.distanceSquared(target(host.box())) : Double.MAX_VALUE)
                        .thenComparing(MultiblockHosts.Host::key)).orElse(null);
    }

    public static boolean loaded(StructureBox box) {
        for (int x = box.originX() >> 4; x <= (box.originX() + box.sizeX() - 1) >> 4; x++)
            for (int z = box.originZ() >> 4; z <= (box.originZ() + box.sizeZ() - 1) >> 4; z++)
                if (!box.world().isChunkLoaded(x, z)) return false;
        return true;
    }

    public static BoundingBox bounds(StructureBox box) {
        return new BoundingBox(box.originX() + (box.sizeX() == 1 ? 0 : 1),
                box.originY() + (box.sizeY() == 1 ? 0 : 1),
                box.originZ() + (box.sizeZ() == 1 ? 0 : 1),
                box.originX() + box.sizeX() - (box.sizeX() == 1 ? 0 : 1),
                box.originY() + box.sizeY() - (box.sizeY() == 1 ? 0 : 1),
                box.originZ() + box.sizeZ() - (box.sizeZ() == 1 ? 0 : 1));
    }

    public static Location target(StructureBox box) {
        return new Location(box.world(), box.originX() + box.sizeX() / 2.0,
                box.originY() + (box.sizeY() == 1 ? 0 : 1), box.originZ() + box.sizeZ() / 2.0);
    }

    public static long cost(Location from, Location to) {
        double sourceScale = from.getWorld().getEnvironment() == World.Environment.NETHER ? 8 : 1;
        double targetScale = to.getWorld().getEnvironment() == World.Environment.NETHER ? 8 : 1;
        return energyCost(from.getX(), from.getY(), from.getZ(), sourceScale,
                to.getX(), to.getY(), to.getZ(), targetScale, from.getWorld() == to.getWorld());
    }

    public static long energyCost(double x, double y, double z, double scale,
                                  double tx, double ty, double tz, double targetScale, boolean sameWorld) {
        // Upstream measures cross-dimension distance in the less expensive coordinate system.
        double dx = sameWorld ? x - tx : (x * scale - tx * targetScale) / Math.max(scale, targetScale);
        double dz = sameWorld ? z - tz : (z * scale - tz * targetScale) / Math.max(scale, targetScale);
        double distance = Math.sqrt(dx * dx + (y - ty) * (y - ty) + dz * dz);
        return Math.min(Integer.MAX_VALUE, 100L + (sameWorld ? 0 : 1000L) + Math.round(2 * distance));
    }

    public String status(MultiblockHosts.Host host) {
        if (state(host.instance()).frequency.isEmpty()) {
            return I18n.tr("teleporter.status.unbound");
        }
        var destination = destination(host);
        if (destination == null) return I18n.tr("teleporter.status.no_destination");
        long cost = cost(target(host.box()), target(destination.box()));
        return host.instance().tank("energy").amount() < cost
                ? I18n.tr("teleporter.status.no_energy", cost)
                : I18n.tr("teleporter.status.ready");
    }

    @Override public boolean needsTick(MultiblockInstance instance) {
        return !state(instance).frequency.isEmpty();
    }

    @Override public long tick(MultiblockInstance instance) {
        var host = MultiblockContexts.hostOf(instance);
        if (host == null || !host.formed() || !loaded(host.box())) return 0;
        State state = state(instance);
        var entities = host.world().getNearbyEntities(bounds(host.box()));
        Set<UUID> present = new HashSet<>();
        entities.forEach(entity -> present.add(entity.getUniqueId()));
        state.arrivals.retainAll(present);
        var destination = destination(host);
        if (destination == null) return 0;
        Location exit = target(destination.box());
        var energy = instance.tank("energy");
        for (Entity entity : entities) {
            if (!(entity instanceof LivingEntity || entity instanceof Item) || !entity.isValid() || entity.isDead()
                    || entity.isInsideVehicle() || !entity.getPassengers().isEmpty()
                    || entity instanceof Player player && player.getGameMode() == GameMode.SPECTATOR
                    || state.arrivals.contains(entity.getUniqueId())) continue;
            Location at = entity.getLocation();
            Location target = exit.clone();
            target.setYaw(at.getYaw()); target.setPitch(at.getPitch());
            // Fit the whole entity in the destination aperture; a short/blocked frame must not trap it.
            BoundingBox body = entity.getBoundingBox().clone().shift(target.getX() - at.getX(),
                    target.getY() - at.getY(), target.getZ() - at.getZ());
            if (!fits(destination.box(), body) || destination.world().hasCollisionsIn(body)) continue;
            long required = cost(at, target);
            if (energy.extract(required, Transaction.SIMULATE) < required) continue;
            State destinationState = of(destination).state(destination.instance());
            destinationState.arrivals.add(entity.getUniqueId());
            if (entity.teleport(target, PlayerTeleportEvent.TeleportCause.PLUGIN)) {
                energy.extract(required, Transaction.EXECUTE);
                host.markChanged();
            } else destinationState.arrivals.remove(entity.getUniqueId());
        }
        return 0;
    }

    private static boolean fits(StructureBox box, BoundingBox body) {
        BoundingBox portal = bounds(box);
        // Thickness is the travel direction, so an entity may extend beyond that axis.
        return (box.sizeX() == 1 || body.getMinX() >= portal.getMinX() && body.getMaxX() <= portal.getMaxX())
                && (box.sizeY() == 1 || body.getMinY() >= portal.getMinY() && body.getMaxY() <= portal.getMaxY())
                && (box.sizeZ() == 1 || body.getMinZ() >= portal.getMinZ() && body.getMaxZ() <= portal.getMaxZ());
    }

    @Override public void save(MultiblockInstance instance, DataTag tag) {
        tag.putString("frequency", state(instance).frequency);
    }

    @Override public void load(MultiblockInstance instance, DataTag tag) {
        state(instance).frequency = normalizeFrequency(tag.getString("frequency", ""));
        state(instance).arrivals.clear();
    }
}
