package net.kuzuanpa.cemekanism.multiblock;

import mekcore.multiblock.MultiblockInstance;

import java.util.Map;
import java.util.WeakHashMap;

/** 结构托管位：逻辑只拿得到 {@link MultiblockInstance}，世界侧的东西（位置、叶片数…）从这里取。 */
public final class MultiblockContexts {

    private static final Map<MultiblockInstance, MultiblockHosts.Host> HOSTS = new WeakHashMap<>();

    private MultiblockContexts() {
    }

    static void attach(MultiblockInstance instance, MultiblockHosts.Host host) {
        HOSTS.put(instance, host);
    }

    static void detach(MultiblockInstance instance) {
        HOSTS.remove(instance);
    }

    public static MultiblockHosts.Host hostOf(MultiblockInstance instance) {
        return HOSTS.get(instance);
    }
}
