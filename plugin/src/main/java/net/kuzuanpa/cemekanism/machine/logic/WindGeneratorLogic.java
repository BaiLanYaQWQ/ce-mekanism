package net.kuzuanpa.cemekanism.machine.logic;

import mekcore.machine.Machine;
import mekcore.machine.WorkState;
import net.kuzuanpa.cemekanism.machine.GeneratorEnvironment;
import net.kuzuanpa.cemekanism.machine.MachineContexts;
import net.kuzuanpa.cemekanism.platform.GeneratorConfig;
import org.bukkit.World;

/**
 * 风力发电机（文档 11 G2.3）：控制器上方第 4 格要能看见天空且不是流体；
 * 发电量按顶部高度在 minY..maxY 之间线性插值，禁风维度直接 0。
 */
public final class WindGeneratorLogic extends GeneratorLogic {

    @Override
    public boolean needsTick(Machine machine) {
        MachineContexts.Place place = MachineContexts.of(machine);
        if (place == null || !hasEnergyRoom(machine)) {
            return false;
        }
        World world = place.world();
        return !GeneratorEnvironment.noWindDimension(world)
                && GeneratorEnvironment.windTopOpen(world, place.x(), place.y(), place.z());
    }

    @Override
    public long tick(Machine machine) {
        MachineContexts.Place place = MachineContexts.of(machine);
        if (place == null) {
            machine.setState(WorkState.IDLE);
            return 0L;
        }
        World world = place.world();
        if (GeneratorEnvironment.noWindDimension(world)
                || !GeneratorEnvironment.windTopOpen(world, place.x(), place.y(), place.z())) {
            machine.setState(WorkState.IDLE);
            return 0L;
        }
        int topY = place.y() + 4;
        int minY = GeneratorConfig.intValue("wind/min_y", 24);
        int maxY = GeneratorConfig.intValue("wind/max_y", 320);
        long min = GeneratorConfig.longValue("wind/generation_min_j", 60);
        long max = GeneratorConfig.longValue("wind/generation_max_j", 480);
        int clamped = Math.max(minY, Math.min(maxY, topY));
        double ratio = maxY <= minY ? 1.0 : (clamped - minY) / (double) (maxY - minY);
        long production = Math.round(min + (max - min) * ratio);
        long inserted = insertEnergy(machine, production);
        machine.setState(inserted > 0 ? WorkState.RUNNING : WorkState.IDLE);
        return inserted;
    }

    @Override
    public String name() {
        return "风力发电机";
    }
}
