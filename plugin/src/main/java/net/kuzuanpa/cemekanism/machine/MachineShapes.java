package net.kuzuanpa.cemekanism.machine;

import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.kuzuanpa.cemekanism.i18n.I18n;
import net.kuzuanpa.cemekanism.generated.GeneratedContent;
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.BlockShape;
import net.momirealms.craftengine.core.block.DelegatingBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.util.Key;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * 非满机器与发电机的碰撞：客户端是 transparent 的 leaves 载体 + ItemDisplay 模型，
 * 服务端用上游 BlockShapes 的 box 列表绑到 CE 的 DelegatingBlock.shapeDelegate。
 *
 * <p>这一层只做碰撞与支撑面；客户端选中框仍跟着 leaves 载体（CE 目前没有声明式的
 * per-block collision_shape）。碰撞按"朝北"那一份模型绑定，四个朝向共用。
 */
public final class MachineShapes {

    private static boolean warned;

    private MachineShapes() {
    }

    public static void apply(CeMekanismPlugin plugin) {
        int applied = apply(GeneratedContent.MACHINE_SHAPES);
        applied += apply(GeneratedContent.GENERATOR_SHAPES);
        plugin.getLogger().info(I18n.tr("machine.shape.applied", applied));
    }

    private static int apply(Map<String, List<GeneratedContent.ShapeBox>> shapes) {
        int applied = 0;
        int skipped = 0;
        for (Map.Entry<String, List<GeneratedContent.ShapeBox>> entry
                : shapes.entrySet()) {
            Object custom = shape(entry.getValue());
            if (custom == null) {
                skipped++;
                continue;
            }
            BlockDefinition definition = CraftEngineBlocks.byId(Key.of(entry.getKey()));
            if (definition == null) {
                skipped++;
                continue;
            }
            ImmutableBlockState state = definition.defaultState();
            if (state.customBlockState() == null) {
                skipped++;
                continue;
            }
            Object owner = BlockStateUtils.getBlockOwner(state.customBlockState().minecraftState());
            if (!(owner instanceof DelegatingBlock delegating)) {
                skipped++;
                continue;
            }
            BlockShape current = delegating.shapeDelegate().value();
            BlockShape base = current instanceof Shape previous ? previous.previous() : current;
            delegating.shapeDelegate().bindValue(new Shape(base, custom));
            applied++;
        }
        if (skipped > 0) {
            org.bukkit.Bukkit.getLogger().warning(
                    I18n.tr("machine.shape.skipped", skipped));
        }
        return applied;
    }

    private static Object shape(List<GeneratedContent.ShapeBox> boxes) {
        if (boxes == null || boxes.isEmpty()) {
            return null;
        }
        try {
            Class<?> aabbType = load("net.momirealms.craftengine.proxy.minecraft.world.phys.AABBProxy");
            Object aabbProxy = aabbType.getField("INSTANCE").get(null);
            Method newAabb = aabbType.getMethod("newInstance",
                    double.class, double.class, double.class, double.class, double.class, double.class);
            Class<?> shapesType = load("net.momirealms.craftengine.proxy.minecraft.world.phys.shapes.ShapesProxy");
            Object shapesProxy = shapesType.getField("INSTANCE").get(null);
            Method create = shapesType.getMethod("create", Object.class);
            Method or = shapesType.getMethod("or", Object.class, Object.class);
            Object result = null;
            for (GeneratedContent.ShapeBox box : boxes) {
                Object aabb = newAabb.invoke(aabbProxy,
                        box.minX() / 16.0, box.minY() / 16.0, box.minZ() / 16.0,
                        box.maxX() / 16.0, box.maxY() / 16.0, box.maxZ() / 16.0);
                Object part = create.invoke(shapesProxy, aabb);
                result = result == null ? part : or.invoke(shapesProxy, result, part);
            }
            return result;
        } catch (ReflectiveOperationException | LinkageError error) {
            if (!warned) {
                warned = true;
                org.bukkit.Bukkit.getLogger().warning(
                        I18n.tr("machine.shape.unavailable", error));
            }
            return null;
        }
    }

    private static Class<?> load(String name) throws ClassNotFoundException {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException first) {
            ClassLoader loader = org.bukkit.Bukkit.getPluginManager()
                    .getPlugin("CraftEngine").getClass().getClassLoader();
            return Class.forName(name, true, loader);
        }
    }

    private record Shape(BlockShape previous, Object shape) implements BlockShape {
        @Override
        public Object getShape(Object thisObj, Object[] args) {
            return this.shape;
        }

        @Override
        public Object getCollisionShape(Object thisObj, Object[] args) {
            return this.shape;
        }

        @Override
        public Object getSupportShape(Object thisObj, Object[] args) {
            return this.previous == null ? this.shape : this.previous.getSupportShape(thisObj, args);
        }
    }
}
