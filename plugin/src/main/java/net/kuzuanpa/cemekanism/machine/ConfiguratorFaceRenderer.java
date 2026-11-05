package net.kuzuanpa.cemekanism.machine;

import mekcore.machine.Direction;
import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.kuzuanpa.cemekanism.platform.BukkitItems;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** One reusable, owner-only model display per player holding the configurator. */
public final class ConfiguratorFaceRenderer {
    public static final List<String> ITEMS = Arrays.stream(ConfiguratorGrid.Region.values())
            .map(ConfiguratorGrid.Region::itemId).toList();
    private static final NamespacedKey OWNER_KEY = new NamespacedKey("cemekanism", "configurator_overlay");

    private final CeMekanismPlugin plugin;
    private final Map<UUID, Preview> previews = new HashMap<>();

    private record Target(UUID world, int x, int y, int z, Direction face) {}
    private static final class Preview {
        ItemDisplay display;
        Target target;
        ConfiguratorGrid.Region region;
    }

    public ConfiguratorFaceRenderer(CeMekanismPlugin plugin) { this.plugin = plugin; }

    public void show(Player player, Block block, Direction face, Direction selected) {
        UUID owner = player.getUniqueId();
        Target target = new Target(block.getWorld().getUID(), block.getX(), block.getY(), block.getZ(), face);
        ConfiguratorGrid.Region region = ConfiguratorGrid.region(face, selected);
        Preview preview = this.previews.get(owner);
        if (preview != null && (!preview.display.isValid() || !preview.target.world().equals(target.world()))) {
            remove(owner);
            preview = null;
        }
        var item = preview == null || preview.region != region ? BukkitItems.create(region.itemId()) : null;
        if ((preview == null || preview.region != region) && item == null) {
            remove(owner);
            return;
        }
        if (preview == null) {
            preview = new Preview();
            var shownItem = item;
            preview.display = block.getWorld().spawn(location(block, face), ItemDisplay.class, display -> {
                // Set before adding to the world: other players must never receive its spawn packet.
                display.setVisibleByDefault(false);
                display.setPersistent(false);
                display.setInvulnerable(true);
                display.setSilent(true);
                display.setGravity(false);
                display.setBillboard(Display.Billboard.FIXED);
                display.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.FIXED);
                display.setBrightness(new Display.Brightness(15, 15));
                display.setInterpolationDuration(0);
                display.setTeleportDuration(0);
                display.setDisplayWidth(2);
                display.setDisplayHeight(2);
                display.setViewRange(1);
                display.setTransformation(transform(face));
                display.setItemStack(shownItem);
                display.getPersistentDataContainer().set(OWNER_KEY, PersistentDataType.STRING, owner.toString());
            });
            player.showEntity(this.plugin, preview.display);
            preview.target = target;
            preview.region = region;
            this.previews.put(owner, preview);
            return;
        }
        if (!target.equals(preview.target)) {
            preview.display.teleport(location(block, face));
            preview.display.setTransformation(transform(face));
            preview.target = target;
        }
        if (preview.region != region) {
            preview.display.setItemStack(item);
            preview.region = region;
        }
    }

    private static Location location(Block block, Direction face) {
        double[] center = ConfiguratorGrid.point(face, .5, .5);
        return block.getLocation().add(center[0], center[1], center[2]);
    }

    /** Compensate the client's horizontal reversal in the model's own plane; retain U/V projection. */
    static Transformation transform(Direction face) {
        Quaternionf rotation = switch (face) {
            case EAST, WEST -> new Quaternionf().rotationY((float) (-Math.PI / 2));
            case UP, DOWN -> new Quaternionf().rotationX((float) (Math.PI / 2));
            default -> new Quaternionf();
        };
        return new Transformation(new Vector3f(), rotation, new Vector3f(1, 1, 1),
                new Quaternionf().rotationY((float) Math.PI));
    }

    public void remove(UUID owner) {
        Preview preview = this.previews.remove(owner);
        if (preview != null) preview.display.remove();
    }

    public void close() {
        this.previews.values().forEach(preview -> preview.display.remove());
        this.previews.clear();
    }
}
