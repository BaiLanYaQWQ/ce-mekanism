package net.kuzuanpa.cemekanism.nuclear;

import mekcore.machine.Machine;
import mekcore.machine.MachineLogic;
import mekcore.persist.DataTag;
import mekcore.resource.ResourceStack;
import mekcore.resource.Transaction;
import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.kuzuanpa.cemekanism.machine.MachineBlockEntityController;
import net.kuzuanpa.cemekanism.machine.MachineContexts;
import net.kuzuanpa.cemekanism.multiblock.MultiblockConfig;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.WeakHashMap;

/** 放射性废料桶：只在上下面接线、自动衰减，并在被破坏时把存量释放成辐射。 */
public final class WasteBarrelLogic implements MachineLogic {

    public static final String ID = "mek:radioactive_waste_barrel";

    public static final class State {
        public int processTicks;
        public boolean outputDown;
    }

    private final Map<Machine, State> states = new WeakHashMap<>();

    public State state(Machine machine) {
        return this.states.computeIfAbsent(machine, ignored -> new State());
    }

    @Override
    public long tick(Machine machine) {
        var tank = machine.tank("stored").orElse(null);
        if (tank == null || tank.amount() <= 0) {
            return 0;
        }
        State state = state(machine);
        if (++state.processTicks >= MultiblockConfig.wasteBarrelProcessTicks()) {
            state.processTicks = 0;
            if (tank.storedId() != null && MultiblockConfig.wasteBarrelDecays(tank.storedId())) {
                tank.extract(Math.min(tank.amount(), MultiblockConfig.wasteBarrelDecayAmount()),
                        Transaction.EXECUTE);
            }
        }
        if (state.outputDown) {
            outputDown(machine, tank.storedId(), tank.amount());
        }
        return 0;
    }

    @Override
    public boolean needsTick(Machine machine) {
        return machine.tank("stored").map(tank -> tank.amount() > 0).orElse(false);
    }

    public void toggleOutput(Machine machine) {
        State state = state(machine);
        state.outputDown = !state.outputDown;
    }

    private static void outputDown(Machine machine, String chemical, long amount) {
        if (chemical == null || amount <= 0) {
            return;
        }
        MachineContexts.Place place = MachineContexts.of(machine);
        if (place == null) {
            return;
        }
        CeMekanismPlugin plugin = JavaPlugin.getPlugin(CeMekanismPlugin.class);
        MachineBlockEntityController below = plugin.machineAtQuiet(place.world(),
                place.x(), place.y() - 1, place.z());
        if (below == null || !ID.equals(below.blockId())) {
            return;
        }
        var target = below.machine().tank("stored").orElse(null);
        if (target == null) {
            return;
        }
        long moved = target.insert(ResourceStack.chemical(chemical, amount), Transaction.EXECUTE);
        if (moved > 0) {
            machine.tank("stored").ifPresent(tank -> tank.extract(moved, Transaction.EXECUTE));
        }
    }

    @Override
    public void save(Machine machine, DataTag tag) {
        State state = state(machine);
        tag.putInt("process_ticks", state.processTicks);
        tag.putBoolean("output_down", state.outputDown);
    }

    @Override
    public void load(Machine machine, DataTag tag) {
        State state = state(machine);
        state.processTicks = tag.getInt("process_ticks", 0);
        state.outputDown = tag.getBoolean("output_down", false);
    }
}
