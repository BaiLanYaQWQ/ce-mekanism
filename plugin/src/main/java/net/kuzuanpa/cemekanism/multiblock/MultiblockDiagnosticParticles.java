package net.kuzuanpa.cemekanism.multiblock;

import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Short, private building feedback; repeated clicks replace the previous preview. */
public final class MultiblockDiagnosticParticles extends BukkitRunnable {
    private static final Map<UUID, MultiblockDiagnosticParticles> ACTIVE = new HashMap<>();
    private static final Particle.DustOptions OUTLINE = new Particle.DustOptions(Color.fromRGB(100, 170, 210), .7f);
    private static final Particle.DustOptions MISSING = new Particle.DustOptions(Color.fromRGB(255, 200, 50), 1.2f);
    private static final Particle.DustOptions INVALID = new Particle.DustOptions(Color.fromRGB(255, 70, 60), 1.2f);
    private final MultiblockHosts.Host host;
    private final Player player;
    private final StructureBox box;
    private final MultiblockDiagnosticHint hint;
    private final mekcore.multiblock.ValidationResult validation;
    private int pulses;

    private MultiblockDiagnosticParticles(MultiblockHosts.Host host, Player player) {
        this.host = host;
        this.player = player;
        this.box = host.box();
        this.validation = host.validation();
        this.hint = MultiblockDiagnosticHint.of(validation, host.extraProblems());
    }

    public static void show(MultiblockHosts.Host host, Player player) {
        clear(player);
        var preview = new MultiblockDiagnosticParticles(host, player);
        ACTIVE.put(player.getUniqueId(), preview);
        preview.runTaskTimer(host.plugin(), 0L, 10L);
    }

    public static void clear(Player player) {
        var previous = ACTIVE.remove(player.getUniqueId());
        if (previous != null) previous.cancel();
    }

    public static void close() {
        ACTIVE.values().forEach(BukkitRunnable::cancel);
        ACTIVE.clear();
    }

    @Override public void run() {
        if (++pulses > 10 || !player.isOnline() || player.isDead() || player.getWorld() != box.world()
                || player.getLocation().distanceSquared(host.controllerLocation()) > 64 * 64
                || host.formed() || host.box() != box || host.validation() != validation
                || !box.world().isChunkLoaded(host.controllerLocation().getBlockX() >> 4,
                        host.controllerLocation().getBlockZ() >> 4)) {
            ACTIVE.remove(player.getUniqueId(), this);
            cancel();
            return;
        }
        String text = "未成型：" + hint.text();
        if (!hint.markers().isEmpty()) {
            text += hint.markers().size() <= 12 ? " · 已标出 " + hint.markers().size() + " 处问题"
                    : " · 共 " + hint.markers().size() + " 处，标出附近 12 处";
        }
        player.sendActionBar(Component.text(text));
        cuboid(box.originX(), box.originY(), box.originZ(), box.sizeX(), box.sizeY(), box.sizeZ(), OUTLINE, true);
        // Limit dense failures to the nearest 12 cells; the full report remains available by command.
        Location eye = player.getEyeLocation();
        hint.markers().stream().sorted(Comparator.comparingDouble(marker -> {
            var p = marker.position();
            double dx = box.originX() + p.x() + .5 - eye.getX();
            double dy = box.originY() + p.y() + .5 - eye.getY();
            double dz = box.originZ() + p.z() + .5 - eye.getZ();
            return dx * dx + dy * dy + dz * dz;
        })).limit(12).forEach(marker -> {
            var p = marker.position();
            cuboid(box.originX() + p.x() - .03, box.originY() + p.y() - .03,
                    box.originZ() + p.z() - .03, 1.06, 1.06, 1.06,
                    marker.missing() ? MISSING : INVALID, false);
        });
    }

    private void cuboid(double x, double y, double z, double w, double h, double d,
                        Particle.DustOptions color, boolean edges) {
        for (int a = 0; a < 2; a++) for (int b = 0; b < 2; b++) {
            line(x, y + a * h, z + b * d, w, 0, 0, color, edges);
            line(x + a * w, y, z + b * d, 0, h, 0, color, edges);
            line(x + a * w, y + b * h, z, 0, 0, d, color, edges);
        }
    }

    private void line(double x, double y, double z, double dx, double dy, double dz,
                      Particle.DustOptions color, boolean edges) {
        int steps = edges ? Math.min(12, Math.max(1, (int) Math.ceil(Math.max(dx, Math.max(dy, dz))))) : 1;
        for (int i = 0; i <= steps; i++) {
            double ratio = (double) i / steps;
            player.spawnParticle(Particle.DUST, x + dx * ratio, y + dy * ratio, z + dz * ratio,
                    1, 0, 0, 0, 0, color);
        }
    }
}
