package net.kuzuanpa.cemekanism.storage;

import net.kuzuanpa.cemekanism.machine.MachineBlockEntityController;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * 在箱柜、私人箱子和私人桶的正面显示一个代表物品。
 * 内容仍由各自容器持有；这里只把当时的第一项抄成不会存档的 ItemDisplay 物品模型。
 */
public final class StorageContentRenderer {
    static final NamespacedKey PREVIEW_KEY =
            new NamespacedKey("cemekanism", "storage_content_preview");
    private static final float PREVIEW_SCALE = 0.8f;
    private static final double FACE_OFFSET = 0.505;

    private final MachineBlockEntityController controller;
    private ItemDisplay display;
    private String displayedKey;

    public StorageContentRenderer(MachineBlockEntityController controller) {
        this.controller = controller;
    }

    static boolean supports(String blockId) {
        return blockId.endsWith("_bin") || PersonalStorageLogic.isStorage(blockId);
    }

    public void spawn() {
        remove();
        refresh();
    }

    public void refresh() {
        if (!supports(controller.blockId())) {
            return;
        }
        ItemStack content = content();
        if (StoredItems.empty(content)) {
            remove();
            return;
        }
        World world = controller.world();
        if (world == null) {
            return;
        }
        if (this.display == null || !this.display.isValid()) {
            this.display = world.spawn(location(world), ItemDisplay.class, entity -> {
                entity.setPersistent(false);
                entity.setInvulnerable(true);
                entity.setSilent(true);
                entity.setGravity(false);
                entity.setBillboard(ItemDisplay.Billboard.FIXED);
                entity.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.FIXED);
                entity.setInterpolationDuration(0);
                entity.setTeleportDuration(0);
                entity.setViewRange(1.0f);
                entity.setTransformation(new Transformation(
                        new Vector3f(),
                        new Quaternionf().rotationY((float) Math.toRadians(yaw())),
                        new Vector3f(PREVIEW_SCALE, PREVIEW_SCALE, PREVIEW_SCALE),
                        new Quaternionf()));
                entity.getPersistentDataContainer().set(PREVIEW_KEY, PersistentDataType.BYTE, (byte) 1);
            });
            this.displayedKey = null;
        } else {
            this.display.teleport(location(world));
        }

        ItemStack shown = content.clone();
        shown.setAmount(1);
        String key = StoredItems.key(shown);
        if (!key.equals(this.displayedKey)) {
            this.display.setItemStack(shown);
            this.displayedKey = key;
        }
    }

    private ItemStack content() {
        if (controller.blockId().endsWith("_bin")
                && controller.machine().blueprint().logic() instanceof BinLogic bin) {
            for (var entry : bin.store(controller.machine()).entries().entrySet()) {
                if (entry.getValue() > 0) {
                    return StoredItems.item(entry.getKey(), 1);
                }
            }
            return null;
        }
        for (ItemStack stack : controller.inventory().getContents()) {
            if (!StoredItems.empty(stack)) {
                return stack;
            }
        }
        return null;
    }

    private Location location(World world) {
        double x = controller.x() + 0.5;
        double z = controller.z() + 0.5;
        switch (controller.facing()) {
            case EAST -> x += FACE_OFFSET;
            case WEST -> x -= FACE_OFFSET;
            case SOUTH -> z += FACE_OFFSET;
            default -> z -= FACE_OFFSET;
        }
        double y = controller.y() + (controller.blockId().endsWith("_bin") ? 0.55 : 0.37);
        return new Location(world, x, y, z);
    }

    private float yaw() {
        return switch (controller.facing()) {
            case EAST -> 90.0f;
            case WEST -> 270.0f;
            case SOUTH -> 0.0f;
            default -> 180.0f;
        };
    }

    public void remove() {
        if (this.display != null && this.display.isValid()) {
            this.display.remove();
        }
        this.display = null;
        this.displayedKey = null;
    }
}
