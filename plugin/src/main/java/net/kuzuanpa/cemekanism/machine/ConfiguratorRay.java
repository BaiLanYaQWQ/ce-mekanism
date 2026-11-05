package net.kuzuanpa.cemekanism.machine;

import mekcore.machine.Direction;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

/** Configurator targeting always treats each occupied block as a standard unit cube. */
public final class ConfiguratorRay {
    private ConfiguratorRay() {}

    public record Hit(Block block, Direction face, double x, double y, double z) {
        public Direction selected() { return ConfiguratorGrid.select(face, x, y, z); }
    }

    public static Hit trace(Player player) {
        var eye = player.getEyeLocation();
        double reach = player.getAttribute(org.bukkit.attribute.Attribute.BLOCK_INTERACTION_RANGE).getValue();
        return trace(eye.getWorld(), eye.toVector(), eye.getDirection(), reach);
    }

    static Hit trace(World world, Vector origin, Vector direction, double reach) {
        if (reach <= 0) return null;
        var blocks = new BlockIterator(world, origin, direction, 0, (int) Math.ceil(reach));
        while (blocks.hasNext()) {
            Block block = blocks.next();
            if (block.isEmpty()) continue;
            var hit = new BoundingBox(block.getX(), block.getY(), block.getZ(),
                    block.getX() + 1, block.getY() + 1, block.getZ() + 1).rayTrace(origin, direction, reach);
            if (hit == null) continue;
            Vector point = hit.getHitPosition();
            return new Hit(block, Direction.valueOf(hit.getHitBlockFace().name()),
                    point.getX() - block.getX(), point.getY() - block.getY(), point.getZ() - block.getZ());
        }
        return null;
    }
}
