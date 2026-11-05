package net.kuzuanpa.cemekanism.storage;

import net.kuzuanpa.cemekanism.machine.MachineBlockEntityController;
import net.momirealms.craftengine.bukkit.api.*;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import java.lang.reflect.Method;

/** CE exposes native neighbour notification via its version proxy, outside the compile-only Bukkit artifact. */
final class StorageRedstone {
    private static Object stateProxy, levelProxy;
    private static Method getBlock, update;
    private StorageRedstone() { }
    static void update(MachineBlockEntityController c) {
        try {
            if (update == null) {
                ClassLoader loader=org.bukkit.Bukkit.getPluginManager().getPlugin("CraftEngine").getClass().getClassLoader();
                Class<?> state=Class.forName("net.momirealms.craftengine.proxy.minecraft.world.level.block.state.BlockBehaviourProxy$BlockStateBaseProxy",true,loader);
                Class<?> level=Class.forName("net.momirealms.craftengine.proxy.minecraft.world.level.LevelAccessorProxy",true,loader);
                stateProxy=state.getField("INSTANCE").get(null); levelProxy=level.getField("INSTANCE").get(null);
                getBlock=state.getMethod("getBlock",Object.class); update=level.getMethod("updateNeighborsAt",Object.class,Object.class,Object.class);
            }
            var state=CraftEngineBlocks.getCustomBlockState(c.world().getBlockAt(c.x(),c.y(),c.z()));
            if(state==null)return;
            Object source=getBlock.invoke(stateProxy,state.customBlockState().minecraftState());
            update.invoke(levelProxy,BukkitAdaptor.adapt(c.world()).minecraftWorld(),LocationUtils.toBlockPos(c.x(),c.y(),c.z()),source);
        } catch (ReflectiveOperationException e) { throw new IllegalStateException("QIO 红石邻居更新失败",e); }
    }
}
