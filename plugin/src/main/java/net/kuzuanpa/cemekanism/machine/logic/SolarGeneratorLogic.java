package net.kuzuanpa.cemekanism.machine.logic;

import mekcore.machine.Machine;
import mekcore.machine.WorkState;
import net.kuzuanpa.cemekanism.machine.GeneratorEnvironment;
import net.kuzuanpa.cemekanism.machine.MachineContexts;
import net.kuzuanpa.cemekanism.i18n.I18n;
import net.kuzuanpa.cemekanism.platform.GeneratorConfig;
import org.bukkit.World;

/**
 * 太阳能（文档 11 G2.2）：基础版看自己头顶；高级版看控制器上方第 2 层的 3×3 九个点。
 * 九点里至少 5 点见太阳才工作，产能按九点平均（看不见的点按 0 计）。
 */
public final class SolarGeneratorLogic extends GeneratorLogic {

    private final boolean advanced;

    public SolarGeneratorLogic(boolean advanced) {
        this.advanced = advanced;
    }

    @Override
    public boolean needsTick(Machine machine) {
        return hasEnergyRoom(machine) && seesSun(machine);
    }

    @Override
    public long tick(Machine machine) {
        MachineContexts.Place place = MachineContexts.of(machine);
        if (place == null) {
            machine.setState(WorkState.IDLE);
            return 0L;
        }
        World world = place.world();
        if (!seesSun(world, place.x(), place.y(), place.z())) {
            machine.setState(WorkState.IDLE);
            return 0L;
        }
        float multiplier = generationMultiplier(world, place.x(), place.y(), place.z());
        int configured = advanced
                ? GeneratorConfig.intValue("solar/advanced_generation_j", 300)
                : GeneratorConfig.intValue("solar/generation_j", 50);
        long production = Math.round(configured * (GeneratorEnvironment.sunBrightness(world) * multiplier));
        long inserted = insertEnergy(machine, production);
        machine.setState(inserted > 0 ? WorkState.RUNNING : WorkState.IDLE);
        return inserted;
    }

    @Override
    public String name() {
        return I18n.machineName(advanced ? "mek:advanced_solar_generator" : "mek:solar_generator");
    }

    private boolean seesSun(Machine machine) {
        MachineContexts.Place place = MachineContexts.of(machine);
        return place != null && seesSun(place.world(), place.x(), place.y(), place.z());
    }

    private boolean seesSun(World world, int x, int y, int z) {
        if (!advanced) {
            return GeneratorEnvironment.canSeeSunAbove(world, x, y, z);
        }
        int points = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (GeneratorEnvironment.canSeeSunAt(world, x + dx, y + 2, z + dz)) {
                    points++;
                }
            }
        }
        return points >= GeneratorConfig.intValue("solar/min_sun_points", 5);
    }

    private float generationMultiplier(World world, int x, int y, int z) {
        if (!advanced) {
            return GeneratorEnvironment.canSeeSunAbove(world, x, y, z)
                    ? GeneratorEnvironment.peakMultiplier(world, x, y, z) : 0.0F;
        }
        float total = 0.0F;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                int sx = x + dx;
                int sz = z + dz;
                if (GeneratorEnvironment.canSeeSunAt(world, sx, y + 2, sz)) {
                    total += GeneratorEnvironment.peakMultiplier(world, sx, y + 2, sz);
                }
            }
        }
        return total / 9.0F;
    }
}
