package net.kuzuanpa.cemekanism.machine;

import mekcore.machine.Direction;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

/** Unit-cube targeting must never consult an irregular block's shape or native hit face. */
final class ConfiguratorRaySelfTest {
    private static int checks;
    private static void check(boolean ok, String label) {
        checks++;
        if (!ok) throw new AssertionError(label);
    }

    static int run() {
        checks = 0;
        for (int[] at : new int[][] {{10, 20, 30}, {-11, 20, -31}}) {
            Set<String> occupied = new HashSet<>();
            occupied.add(key(at[0], at[1], at[2]));
            World world = world(occupied);
            for (Direction face : Direction.values()) {
                double nx = face == Direction.WEST ? -1 : face == Direction.EAST ? 1 : 0;
                double ny = face == Direction.DOWN ? -1 : face == Direction.UP ? 1 : 0;
                double nz = face == Direction.NORTH ? -1 : face == Direction.SOUTH ? 1 : 0;
                for (double u : new double[] {.01, .125, .249, .251, .5, .749, .751, .875, .99}) {
                    for (double v : new double[] {.01, .125, .249, .251, .5, .749, .751, .875, .99}) {
                        Vector point = switch (face) {
                            case WEST -> new Vector(0, v, u); case EAST -> new Vector(1, v, u);
                            case DOWN -> new Vector(u, 0, v); case UP -> new Vector(u, 1, v);
                            case NORTH -> new Vector(u, v, 0); case SOUTH -> new Vector(u, v, 1);
                        };
                        Vector origin = point.clone().add(new Vector(at[0] + nx * 2, at[1] + ny * 2, at[2] + nz * 2));
                        Vector ray = new Vector(-nx, -ny, -nz);
                        var hit = ConfiguratorRay.trace(world, origin, ray, 4.5);
                        check(hit != null && hit.block().getX() == at[0] && hit.block().getY() == at[1]
                                && hit.block().getZ() == at[2], "full cube hit even through an apparent gap");
                        check(hit.face() == face, "face remains stable across a recessed model");
                        check(Math.abs(hit.x() - point.getX()) < 1e-9 && Math.abs(hit.y() - point.getY()) < 1e-9
                                && Math.abs(hit.z() - point.getZ()) < 1e-9, "outer face coordinates, not recessed surface");
                        check(hit.selected() == ConfiguratorGrid.select(face, point.getX(), point.getY(), point.getZ()),
                                "preview/click use the outer-cube grid");
                    }
                }
            }
            Vector eye = new Vector(at[0] + .5, at[1] + .5, at[2] - 2);
            check(ConfiguratorRay.trace(world, eye, new Vector(0, 0, 1), 1.99) == null, "range limit");
            occupied.add(key(at[0], at[1], at[2] - 1));
            check(ConfiguratorRay.trace(world, eye, new Vector(0, 0, 1), 4.5).block().getZ() == at[2] - 1,
                    "first occupied cube occludes the machine");
            occupied.clear();
            check(ConfiguratorRay.trace(world, eye, new Vector(0, 0, 1), 4.5) == null, "empty space");
        }
        return checks;
    }

    private static String key(int x, int y, int z) { return x + "," + y + "," + z; }

    private static World world(Set<String> occupied) {
        return (World) Proxy.newProxyInstance(World.class.getClassLoader(), new Class<?>[] {World.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("getBlockAt")) {
                        return block((World) proxy, occupied, (int) args[0], (int) args[1], (int) args[2]);
                    }
                    throw new AssertionError("must not query native world ray tracing: " + method.getName());
                });
    }

    private static Block block(World world, Set<String> occupied, int x, int y, int z) {
        return (Block) Proxy.newProxyInstance(Block.class.getClassLoader(), new Class<?>[] {Block.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "isEmpty" -> !occupied.contains(key(x, y, z));
                    case "getX" -> x; case "getY" -> y; case "getZ" -> z;
                    case "getWorld" -> world;
                    case "getLocation" -> new Location(world, x, y, z);
                    case "getRelative" -> {
                        if (args[0] instanceof BlockFace face) {
                            int steps = args.length == 1 ? 1 : (int) args[1];
                            yield block(world, occupied, x + face.getModX() * steps,
                                    y + face.getModY() * steps, z + face.getModZ() * steps);
                        }
                        yield block(world, occupied, x + (int) args[0], y + (int) args[1], z + (int) args[2]);
                    }
                    default -> throw new AssertionError("must not query irregular block shape: " + method.getName());
                });
    }
}
