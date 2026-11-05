package net.kuzuanpa.cemekanism.machine;

import mekcore.machine.Machine;
import mekcore.machine.MachineGate;

/**
 * 需要直接看到天空与太阳的配方机器（太阳能中子活化器）。
 * 上游检查的是方块上方一格，白天与天光 15 都由 {@link GeneratorEnvironment} 统一判断。
 */
public final class SunlightGate implements MachineGate {

    @Override
    public boolean canRun(Machine machine) {
        MachineContexts.Place place = MachineContexts.of(machine);
        return place != null && GeneratorEnvironment.canSeeSunAt(
                place.world(), place.x(), place.y() + 1, place.z());
    }
}
