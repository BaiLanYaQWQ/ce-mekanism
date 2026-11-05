package net.kuzuanpa.cemekanism.transmitter;

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

/**
 * 传输元件的服务端碰撞（CE 的 DelegatingBlock.shapeDelegate API）。
 *
 * 客户端载体是共享的清空 leaves 状态：选中框是满方块、leaves 不遮挡邻居；
 * 服务端实际碰撞在这里收成 6px 立方，和上游小管径一致。
 * CE 没有 YAML 的 collision_shape，只能在 Java 侧 bindValue。
 */
public final class TransmitterShapes {

    private static boolean warned;

    private TransmitterShapes() {
    }

    public static void apply(CeMekanismPlugin plugin, boolean sixPx) {
        Object custom = sixPx ? sixPxShape() : null;
        if (sixPx && custom == null) {
            sixPx = false;                         // 反射失败：回退完整方块，别让碰撞和客户端脱节
        }
        int full = 0;
        int small = 0;
        for (TransmitterDef transmitter : GeneratedContent.TRANSMITTERS) {
            BlockDefinition definition = CraftEngineBlocks.byId(Key.of(transmitter.blockId()));
            if (definition == null) {
                continue;
            }
            ImmutableBlockState state = definition.defaultState();
            if (state.customBlockState() == null) {
                continue;
            }
            Object owner = BlockStateUtils.getBlockOwner(state.customBlockState().minecraftState());
            if (!(owner instanceof DelegatingBlock delegating)) {
                continue;
            }
            BlockShape current = delegating.shapeDelegate().value();
            BlockShape base = current instanceof Shape shape ? shape.previous() : current;
            if (sixPx) {
                delegating.shapeDelegate().bindValue(new Shape(base, custom));
                small++;
            } else {
                delegating.shapeDelegate().bindValue(base);
                full++;
            }
        }
        plugin.getLogger().info(sixPx
                ? I18n.tr("transmitter.shape.applied_six", small)
                : I18n.tr("transmitter.shape.applied_full", full));
    }

    private static Object sixPxShape() {
        try {
            Class<?> aabbType = load("net.momirealms.craftengine.proxy.minecraft.world.phys.AABBProxy");
            Object aabbProxy = aabbType.getField("INSTANCE").get(null);
            Object aabb = aabbType.getMethod("newInstance",
                            double.class, double.class, double.class,
                            double.class, double.class, double.class)
                    .invoke(aabbProxy, 5.0 / 16, 5.0 / 16, 5.0 / 16,
                            11.0 / 16, 11.0 / 16, 11.0 / 16);
            Class<?> shapesType = load("net.momirealms.craftengine.proxy.minecraft.world.phys.shapes.ShapesProxy");
            Object shapesProxy = shapesType.getField("INSTANCE").get(null);
            Method create = shapesType.getMethod("create", Object.class);
            return create.invoke(shapesProxy, aabb);
        } catch (ReflectiveOperationException | LinkageError e) {
            if (!warned) {
                warned = true;
                org.bukkit.Bukkit.getLogger().warning(
                        I18n.tr("transmitter.shape.unavailable", e));
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
