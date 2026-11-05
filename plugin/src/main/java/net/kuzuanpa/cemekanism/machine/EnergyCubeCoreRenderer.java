package net.kuzuanpa.cemekanism.machine;

import net.kuzuanpa.cemekanism.platform.BukkitItems;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/** 能量立方内部的核心层；方块外壳由 CE 的 item_display 绘制。 */
final class EnergyCubeCoreRenderer {

    static final NamespacedKey CORE_KEY = new NamespacedKey("cemekanism", "energy_cube_core");
    private static final String ITEM_ID = "mek:render/energy_core";
    private static final float CORE_SCALE = 0.4f;
    private static final float INV_SQRT_TWO = (float) (1.0 / Math.sqrt(2.0));
    private static final int STORAGE_LEVELS = 20;

    private final MachineBlockEntityController controller;
    private ItemDisplay display;
    private int shownLevel = -1;
    private boolean visible;

    EnergyCubeCoreRenderer(MachineBlockEntityController controller) {
        this.controller = controller;
    }

    void spawn() {
        remove();
        World world = controller.world();
        ItemStack stack = BukkitItems.create(ITEM_ID);
        if (world == null || stack == null) {
            return;
        }
        Location at = new Location(world, controller.x() + 0.5, controller.y() + 0.5,
                controller.z() + 0.5);
        this.display = world.spawn(at, ItemDisplay.class, entity -> {
            entity.setPersistent(false);
            entity.setInvulnerable(true);
            entity.setSilent(true);
            entity.setGravity(false);
            entity.setBillboard(ItemDisplay.Billboard.FIXED);
            entity.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.NONE);
            entity.setBrightness(new Display.Brightness(15, 15));
            entity.setInterpolationDuration(0);
            entity.setTeleportDuration(0);
            entity.setViewRange(1.0f);
            entity.setItemStack(stack);
            entity.addScoreboardTag("cemekanism_energy_cube_core");
            entity.getPersistentDataContainer().set(CORE_KEY, PersistentDataType.BYTE, (byte) 1);
            entity.setTransformation(hiddenTransform());
        });
        apply();
    }

    void tick() {
        if (this.display == null || !this.display.isValid()) {
            spawn();
            return;
        }
        apply();
    }

    private void apply() {
        var tank = controller.machine().energyTank().orElse(null);
        if (tank == null || tank.amount() <= 0 || tank.capacity() <= 0) {
            if (this.visible) {
                this.display.setTransformation(hiddenTransform());
                this.visible = false;
            }
            return;
        }
        double ratio = Math.min(1.0, tank.amount() / (double) tank.capacity());
        int level = Math.max(1, (int) Math.ceil(ratio * STORAGE_LEVELS));
        if (level != this.shownLevel) {
            applyColor(level);
            this.shownLevel = level;
        }
        this.display.setTransformation(coreTransform());
        this.visible = true;
    }

    private void applyColor(int level) {
        ItemStack stack = this.display.getItemStack();
        if (!(stack.getItemMeta() instanceof LeatherArmorMeta meta)) {
            return;
        }
        String id = controller.blockId();
        int base = id.startsWith("mek:advanced_") ? 0xFF806A
                : id.startsWith("mek:elite_") ? 0x4BF8FF
                : id.startsWith("mek:ultimate_") ? 0xF787FF
                : 0x5FFFB8;
        double brightness = 0.25 + 0.75 * level / STORAGE_LEVELS;
        int red = (int) Math.round(((base >> 16) & 0xFF) * brightness);
        int green = (int) Math.round(((base >> 8) & 0xFF) * brightness);
        int blue = (int) Math.round((base & 0xFF) * brightness);
        meta.setColor(Color.fromRGB(red, green, blue));
        stack.setItemMeta(meta);
        this.display.setItemStack(stack);
    }

    private Transformation coreTransform() {
        long tick = Bukkit.getCurrentTick();
        float animationTime = 4.0f * tick;
        float bob = (float) (Math.sin(Math.toRadians(3.0 * tick)) / 7.0);
        Quaternionf rotation = new Quaternionf()
                .rotationY((float) Math.toRadians(animationTime))
                .rotateAxis((float) Math.toRadians(36.0f + animationTime),
                        0.0f, INV_SQRT_TWO, INV_SQRT_TWO);
        return new Transformation(new Vector3f(0.0f, bob, 0.0f), rotation,
                new Vector3f(CORE_SCALE, CORE_SCALE, CORE_SCALE), new Quaternionf());
    }

    private static Transformation hiddenTransform() {
        return new Transformation(new Vector3f(), new Quaternionf(),
                new Vector3f(), new Quaternionf());
    }

    void remove() {
        if (this.display != null && this.display.isValid()) {
            this.display.remove();
        }
        this.display = null;
        this.shownLevel = -1;
        this.visible = false;
    }
}
