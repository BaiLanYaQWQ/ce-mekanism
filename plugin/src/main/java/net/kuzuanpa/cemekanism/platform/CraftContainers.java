package net.kuzuanpa.cemekanism.platform;

import net.kuzuanpa.cemekanism.i18n.I18n;
import net.momirealms.craftengine.core.plugin.CraftEngine;
import net.momirealms.craftengine.core.world.Container;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * CE 的世界级容器桥（台账 P13）：机器的格子必须由 CE 的 NMS 容器承载，
 * 原版漏斗/比较器才看得见；用 Bukkit.createInventory 造的容器只有玩家能开。
 *
 * proxy 是 CE 的独立依赖（PUBLIC，运行时会加到共享 classpath），这里用反射调用，
 * 免得把 CE 的版本相关代理类钉进编译期；失败时退回普通 Bukkit 容器并打一行警告。
 */
public final class CraftContainers {

    private static final Map<Object, World> LEVELS = Collections.synchronizedMap(new WeakHashMap<>());
    private static boolean warned;

    private CraftContainers() {
    }

    /** NMS level → Bukkit world：方块实体加载时登记一次，漏斗回调里就不用再猜。 */
    public static void rememberLevel(Object minecraftLevel, World world) {
        if (minecraftLevel != null && world != null) {
            LEVELS.put(minecraftLevel, world);
        }
    }

    public static World worldOfLevel(Object minecraftLevel) {
        World known = LEVELS.get(minecraftLevel);
        if (known != null) {
            return known;
        }
        try {
            Class<?> type = load("net.momirealms.craftengine.proxy.minecraft.world.level.LevelProxy");
            Object instance = type.getField("INSTANCE").get(null);
            return (World) type.getMethod("getWorld", Object.class).invoke(instance, minecraftLevel);
        } catch (ReflectiveOperationException | LinkageError e) {
            warn(I18n.tr("platform.container.level_proxy_failed"), e);
            return null;
        }
    }

    /** CE 容器 → Bukkit Inventory + 给 WorldlyContainerHolder 的 NMS 句柄。 */
    public record Wrapped(Inventory inventory, Object handle) {
    }

    /** 失败返回 null，调用方退回 Bukkit.createInventory。 */
    public static Wrapped wrap(Container container) {
        Object nms = null;
        try {
            nms = CraftEngine.instance().platform().createContainer(container);
            Class<?> proxy = load("net.momirealms.craftengine.proxy.bukkit.craftbukkit.inventory.CraftInventoryProxy");
            Object instance = proxy.getField("INSTANCE").get(null);
            Inventory inventory = (Inventory) proxy.getMethod("newInstance", Object.class).invoke(instance, nms);
            return new Wrapped(inventory, nms);
        } catch (ReflectiveOperationException | LinkageError e) {
            return fallbackInventory(nms, e);
        }
    }

    private static Wrapped fallbackInventory(Object nms, Throwable cause) {
        if (nms != null) {
            try {
                Class<?> inventoryType = Class.forName("org.bukkit.craftbukkit.inventory.CraftInventory");
                Class<?> containerType = Class.forName("net.minecraft.world.Container");
                Constructor<?> constructor = inventoryType.getConstructor(containerType);
                return new Wrapped((Inventory) constructor.newInstance(nms), nms);
            } catch (ReflectiveOperationException | LinkageError e) {
                cause = e;
            }
        }
        warn(I18n.tr("platform.container.inventory_proxy_failed"), cause);
        return null;
    }

    private static Class<?> load(String name) throws ClassNotFoundException {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException first) {
            Plugin craftEngine = Bukkit.getPluginManager().getPlugin("CraftEngine");
            if (craftEngine == null) {
                throw first;
            }
            return Class.forName(name, true, craftEngine.getClass().getClassLoader());
        }
    }

    private static void warn(String message, Throwable cause) {
        if (warned) {
            return;
        }
        warned = true;
        Bukkit.getLogger().warning("[CeMekanism] " + message + "：" + cause);
    }
}
