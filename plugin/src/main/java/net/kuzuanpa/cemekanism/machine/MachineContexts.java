package net.kuzuanpa.cemekanism.machine;

import mekcore.machine.Machine;
import org.bukkit.World;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 机器实例 → 它在世界里的位置（文档 04 第 3 节：世界交互由使用方自己做）。
 * 逻辑机器是"每个蓝图一个实例"，拿不到自己那块方块的位置，所以位置由控制器在这里挂一份。
 */
public final class MachineContexts {

    public record Place(World world, int x, int y, int z) {
    }

    private static final Map<Machine, Place> PLACES = new ConcurrentHashMap<>();

    private MachineContexts() {
    }

    public static void attach(Machine machine, Place place) {
        PLACES.put(machine, place);
    }

    public static void detach(Machine machine) {
        PLACES.remove(machine);
    }

    public static Place of(Machine machine) {
        return PLACES.get(machine);
    }
}
