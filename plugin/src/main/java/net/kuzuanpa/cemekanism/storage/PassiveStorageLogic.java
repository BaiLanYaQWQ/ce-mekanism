package net.kuzuanpa.cemekanism.storage;

import mekcore.machine.Machine;
import mekcore.machine.MachineLogic;

/** Passive buffers sleep; networks own transfers and their accounting. */
public final class PassiveStorageLogic implements MachineLogic {
    public long energyPerTick(Machine machine) { return 0; }
    public long tick(Machine machine) { return 0; }
    public boolean needsTick(Machine machine) { return false; }
}
