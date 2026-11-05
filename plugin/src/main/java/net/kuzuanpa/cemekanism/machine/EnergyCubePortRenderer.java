package net.kuzuanpa.cemekanism.machine;

import java.util.EnumMap;
import java.util.Map;
import mekcore.machine.Direction;
import mekcore.machine.SideMode;
import net.kuzuanpa.cemekanism.platform.BukkitItems;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/** 六个独立端口层；按核心的 energy 侧面配置切换关闭、输入和输出外观。 */
final class EnergyCubePortRenderer {

    static final NamespacedKey PORT_KEY =
            new NamespacedKey("cemekanism", "energy_cube_port");
    private static final float FACE_DEPTH = 1.0f / 16.0f;
    private static final BlockFace[] FACES = {
            BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH,
            BlockFace.WEST, BlockFace.UP, BlockFace.DOWN
    };

    private final MachineBlockEntityController controller;
    private final Map<BlockFace, ItemDisplay> displays = new EnumMap<>(BlockFace.class);
    private final Map<BlockFace, String> shown = new EnumMap<>(BlockFace.class);

    EnergyCubePortRenderer(MachineBlockEntityController controller) {
        this.controller = controller;
    }

    void spawn() {
        remove();
        World world = controller.world();
        if (world == null) {
            return;
        }
        for (BlockFace face : FACES) {
            String state = state(face);
            ItemStack stack = BukkitItems.create(itemId(state));
            if (stack == null) {
                continue;
            }
            Location at = location(world, face);
            ItemDisplay display = world.spawn(at, ItemDisplay.class, entity -> {
                entity.setPersistent(false);
                entity.setInvulnerable(true);
                entity.setSilent(true);
                entity.setGravity(false);
                entity.setBillboard(ItemDisplay.Billboard.FIXED);
                entity.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.NONE);
                entity.setInterpolationDuration(0);
                entity.setTeleportDuration(0);
                entity.setViewRange(1.0f);
                entity.setItemStack(stack);
                entity.addScoreboardTag("cemekanism_energy_cube_port");
                entity.getPersistentDataContainer().set(PORT_KEY, PersistentDataType.BYTE, (byte) 1);
                entity.setTransformation(transform(face));
            });
            this.displays.put(face, display);
            this.shown.put(face, state);
        }
    }

    void tick() {
        if (this.displays.size() != FACES.length) {
            spawn();
            return;
        }
        for (BlockFace face : FACES) {
            ItemDisplay display = this.displays.get(face);
            if (display == null || !display.isValid()) {
                spawn();
                return;
            }
            String state = state(face);
            if (state.equals(this.shown.get(face))) {
                continue;
            }
            ItemStack stack = BukkitItems.create(itemId(state));
            if (stack == null) {
                continue;
            }
            display.setItemStack(stack);
            this.shown.put(face, state);
        }
    }

    private String state(BlockFace face) {
        SideMode mode = controller.machine().side("energy", direction(face));
        return mode == SideMode.NONE ? "off" : mode == SideMode.INPUT ? "input" : "output";
    }

    private static String itemId(String state) {
        return "mek:render/energy_cube_port_" + state;
    }

    private Location location(World world, BlockFace face) {
        double x = controller.x() + 0.5;
        double y = controller.y() + 0.5;
        double z = controller.z() + 0.5;
        switch (face) {
            case NORTH -> z = controller.z() + FACE_DEPTH;
            case SOUTH -> z = controller.z() + 1.0 - FACE_DEPTH;
            case WEST -> x = controller.x() + FACE_DEPTH;
            case EAST -> x = controller.x() + 1.0 - FACE_DEPTH;
            case UP -> y = controller.y() + 1.0 - FACE_DEPTH;
            case DOWN -> y = controller.y() + FACE_DEPTH;
            default -> throw new IllegalArgumentException("Unexpected face: " + face);
        }
        return new Location(world, x, y, z);
    }

    private static Transformation transform(BlockFace face) {
        // ItemDisplay 在绘制内置物品模型后还会额外绕 Y 轴转 180°。
        Quaternionf itemDisplayFlip = new Quaternionf().rotationY((float) Math.PI);
        Quaternionf rotation = switch (face) {
            case NORTH -> new Quaternionf(itemDisplayFlip);
            case SOUTH -> new Quaternionf();
            case EAST -> new Quaternionf().rotationY((float) Math.toRadians(90));
            case WEST -> new Quaternionf().rotationY((float) Math.toRadians(-90));
            case UP -> new Quaternionf().rotationX((float) Math.toRadians(90))
                    .mul(itemDisplayFlip);
            case DOWN -> new Quaternionf().rotationX((float) Math.toRadians(-90))
                    .mul(itemDisplayFlip);
            default -> throw new IllegalArgumentException("Unexpected face: " + face);
        };
        return new Transformation(new Vector3f(), rotation,
                new Vector3f(1.0f, 1.0f, 1.0f), new Quaternionf());
    }

    private static Direction direction(BlockFace face) {
        return switch (face) {
            case NORTH -> Direction.NORTH;
            case EAST -> Direction.EAST;
            case SOUTH -> Direction.SOUTH;
            case WEST -> Direction.WEST;
            case UP -> Direction.UP;
            case DOWN -> Direction.DOWN;
            default -> throw new IllegalArgumentException("Unexpected face: " + face);
        };
    }

    void remove() {
        for (ItemDisplay display : this.displays.values()) {
            if (display != null && display.isValid()) {
                display.remove();
            }
        }
        this.displays.clear();
        this.shown.clear();
    }
}
