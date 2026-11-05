package net.kuzuanpa.cemekanism.transmitter;

import mekcore.machine.Direction;
import net.kuzuanpa.cemekanism.platform.BukkitItems;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.EnumMap;
import java.util.Map;

/**
 * 传输元件的六向动态外观（08 的 entity_renderer 路线，先走 Bukkit ItemDisplay）。
 *
 * 方块本体只画 6px 中心；每个连出去的面放一个 ItemDisplay，模型是单臂：
 * normal 是 6px 直臂，push 是带箭头的收窄臂，pull 是宽端臂。
 * 这样连接不存在时不会画手臂，正常/推/拉也能从模型本身看出来，不再受 4^6 方块状态预算限制。
 */
final class TransmitterArmRenderer {

    private final TransmitterBlockEntityController controller;
    private final Map<Direction, ItemDisplay> arms = new EnumMap<>(Direction.class);
    private final Map<Direction, String> armParts = new EnumMap<>(Direction.class);
    private final Map<Direction, String> armStates = new EnumMap<>(Direction.class);
    private ItemDisplay center;
    private String centerState;

    TransmitterArmRenderer(TransmitterBlockEntityController controller) {
        this.controller = controller;
    }

    void spawn() {
        if (controller.world() == null) {
            return;
        }
        refresh();
    }

    void refresh() {
        World world = controller.world();
        if (world == null) {
            return;
        }
        if (this.center == null || !this.center.isValid()) {
            this.center = spawnDisplay(world, null);
            this.centerState = null;
        }

        armParts.clear();
        for (Direction direction : Direction.values()) {
            String part = controller.armPart(direction);
            if (part != null) {
                armParts.put(direction, part);
            } else {
                remove(direction);
            }
        }
        applyVisual(controller.visual());
    }

    void refreshContents() {
        if (controller.world() != null && this.center != null && this.center.isValid()) {
            applyVisual(controller.visual());
        }
    }

    private void applyVisual(TransmitterVisual visual) {
        World world = controller.world();
        if (world == null) {
            return;
        }
        String centerItem = controller.renderItemId("center", visual.visible() && !armParts.isEmpty());
        String centerKey = centerItem + '|' + (visual.visible() ? visual.rgb() : 0);
        if (!centerKey.equals(this.centerState)) {
            applyItem(this.center, centerItem, visual.visible() ? visual.rgb() : -1);
            this.centerState = centerKey;
        }

        for (Map.Entry<Direction, String> entry : armParts.entrySet()) {
            Direction direction = entry.getKey();
            ItemDisplay display = arms.get(direction);
            if (display == null || !display.isValid()) {
                display = spawnDisplay(world, direction);
                arms.put(direction, display);
            }
            String itemId = controller.renderItemId(entry.getValue(), visual.visible());
            String state = itemId + '|' + (visual.visible() ? visual.rgb() : 0);
            if (!state.equals(armStates.get(direction))) {
                applyItem(display, itemId, visual.visible() ? visual.rgb() : -1);
                armStates.put(direction, state);
            }
        }
    }

    private ItemDisplay spawnDisplay(World world, Direction direction) {
        Location at = new Location(world, controller.x() + 0.5, controller.y() + 0.5,
                controller.z() + 0.5);
        return world.spawn(at, ItemDisplay.class, display -> {
            display.setPersistent(false);
            display.setInvulnerable(true);
            display.setSilent(true);
            display.setGravity(false);
            display.setBillboard(ItemDisplay.Billboard.FIXED);
            display.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.FIXED);
            display.setInterpolationDuration(0);
            display.setTeleportDuration(0);
            display.setViewRange(1.0f);
            display.setTransformation(direction == null ? identity() : transform(direction));
        });
    }

    private static void applyItem(ItemDisplay display, String itemId, int rgb) {
        ItemStack stack = BukkitItems.create(itemId);
        if (stack == null) {
            return;
        }
        if (rgb >= 0 && stack.getItemMeta() instanceof LeatherArmorMeta meta) {
            meta.setColor(org.bukkit.Color.fromRGB(rgb));
            stack.setItemMeta(meta);
        }
        display.setItemStack(stack);
    }

    private static Transformation transform(Direction direction) {
        Quaternionf rotation = new Quaternionf();
        // 物品模型在 ItemDisplay 的 display 上下文里相对方块模型多转了 180°（+X 会画成 -X）；
        // 因此这里的基准方向整体翻过来，手臂才会从中心指向真正相邻的那一面。
        switch (direction) {
            case EAST -> rotation.rotationY((float) Math.PI);
            case WEST -> { }
            case SOUTH -> rotation.rotationY((float) (Math.PI / 2));
            case NORTH -> rotation.rotationY((float) (-Math.PI / 2));
            case UP -> rotation.rotationZ((float) (-Math.PI / 2));
            case DOWN -> rotation.rotationZ((float) (Math.PI / 2));
        }
        // 方块模型是 1:1；ItemDisplay 的物品模型基础约 0.5×，放大 2× 后手臂才会和 6px 中心同尺寸。
        return new Transformation(new Vector3f(), rotation, new Vector3f(2, 2, 2), new Quaternionf());
    }

    private static Transformation identity() {
        return new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(2, 2, 2),
                new Quaternionf());
    }

    private void remove(Direction direction) {
        ItemDisplay display = arms.remove(direction);
        armStates.remove(direction);
        if (display != null && display.isValid()) {
            display.remove();
        }
    }

    void remove() {
        for (Direction direction : Direction.values()) {
            remove(direction);
        }
        arms.clear();
        armParts.clear();
        armStates.clear();
        if (this.center != null && this.center.isValid()) {
            this.center.remove();
        }
        this.center = null;
        this.centerState = null;
    }
}
