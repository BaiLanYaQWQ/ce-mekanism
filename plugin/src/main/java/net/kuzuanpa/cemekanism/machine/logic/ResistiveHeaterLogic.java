package net.kuzuanpa.cemekanism.machine.logic;

import mekcore.machine.Machine;
import mekcore.machine.MachineLogic;
import mekcore.machine.WorkState;
import mekcore.persist.DataTag;
import net.kuzuanpa.cemekanism.platform.GeneratorConfig;
import java.util.Map;
import java.util.WeakHashMap;

/** Per-machine adjustable power, proportional buffer and partial-energy heating, as upstream. */
public final class ResistiveHeaterLogic implements MachineLogic {
    private final Map<Machine, Long> usages = new WeakHashMap<>();

    public long usage(Machine machine) {
        Long value = usages.get(machine);
        if (value == null) {
            setUsage(machine, Math.max(0, GeneratorConfig.longValue("heater/resistive_usage_j", 250)));
            value = usages.get(machine);
        }
        return value;
    }

    private long bufferMultiplier(Machine machine) {
        // Includes the project's configured buffer multiplier, generated from curation.yml.
        return Math.max(1, machine.blueprint().energyCapacityJ() / Math.max(1, machine.blueprint().energyPerTickJ()));
    }

    public void setUsage(Machine machine, long joulesPerTick) {
        if (joulesPerTick < 0 || joulesPerTick > Long.MAX_VALUE / bufferMultiplier(machine)) {
            throw new IllegalArgumentException("功率超出允许范围");
        }
        usages.put(machine, joulesPerTick);
        machine.resizeEnergyBuffer(joulesPerTick * bufferMultiplier(machine));
        if (joulesPerTick == 0) machine.setState(WorkState.IDLE);
    }

    @Override
    public long energyPerTick(Machine machine) {
        // Upstream heats with whatever energy is available, even below the selected rate.
        return Math.min(usage(machine), machine.energyTank().map(t -> t.amount()).orElse(0L));
    }

    @Override
    public long energyDemand(Machine machine) { return usage(machine); }

    @Override
    public long tick(Machine machine) {
        long used = machine.lastEnergyUsed();
        if (used <= 0) {
            machine.setState(usage(machine) == 0 ? WorkState.IDLE : WorkState.NO_ENERGY);
            return 0;
        }
        machine.heatNode().addHeat(used * GeneratorConfig.doubleValue("heater/resistive_efficiency", 0.6));
        machine.setState(WorkState.RUNNING);
        return 0;
    }

    @Override
    public boolean needsTick(Machine machine) {
        return usage(machine) > 0 && machine.energyTank().map(t -> t.amount() > 0).orElse(false);
    }

    @Override
    public void save(Machine machine, DataTag tag) { tag.putLong("usage_j", usage(machine)); }

    @Override
    public void load(Machine machine, DataTag tag) {
        setUsage(machine, tag.getLong("usage_j", Math.max(0, GeneratorConfig.longValue("heater/resistive_usage_j", 250))));
    }

    @Override
    public String name() { return "电阻型加热器"; }
}
