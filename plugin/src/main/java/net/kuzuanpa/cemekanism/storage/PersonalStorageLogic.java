package net.kuzuanpa.cemekanism.storage;

import mekcore.machine.Machine;
import mekcore.machine.MachineLogic;

/** 54 real slots; side views, hoppers, GUI and QIO all operate on the same inventory. */
public final class PersonalStorageLogic implements MachineLogic {
    public static boolean isStorage(String id) { return id.equals("mek:personal_chest") || id.equals("mek:personal_barrel"); }
    public long energyPerTick(Machine machine) { return 0; }
    public long tick(Machine machine) { return 0; }
    public boolean needsTick(Machine machine) { return false; }
}
