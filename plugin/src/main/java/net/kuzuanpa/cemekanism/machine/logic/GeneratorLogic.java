package net.kuzuanpa.cemekanism.machine.logic;

import net.kuzuanpa.cemekanism.i18n.I18n;
import mekcore.machine.Machine;
import mekcore.machine.MachineLogic;
import mekcore.resource.ResourceStack;
import mekcore.resource.ResourceTank;
import mekcore.resource.Transaction;

/**
 * 发电机的公共口径（文档 11 G2）：
 * 发电机不从自己的能量缓存扣电，逻辑的返回值是本刻真正插进缓存的能量，电网再从缓存拉走。
 */
public abstract class GeneratorLogic implements MachineLogic {

    @Override
    public long energyPerTick(Machine machine) {
        return 0L;
    }

    protected static ResourceTank tank(Machine machine, String name) {
        return machine.tank(name).orElseThrow(
                () -> new IllegalStateException(I18n.tr("machine.tank_missing", machine.blueprint().id(), name)));
    }

    protected static boolean hasEnergyRoom(Machine machine) {
        return machine.energyTank()
                .map(tank -> tank.amount() < tank.capacity())
                .orElse(false);
    }

    protected static long insertEnergy(Machine machine, long joules) {
        if (joules <= 0) {
            return 0L;
        }
        return machine.energyTank()
                .map(tank -> tank.insert(ResourceStack.energy(joules), Transaction.EXECUTE))
                .orElse(0L);
    }
}
