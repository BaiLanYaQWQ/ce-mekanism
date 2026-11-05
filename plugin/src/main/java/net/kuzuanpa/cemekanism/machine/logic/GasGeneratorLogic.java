package net.kuzuanpa.cemekanism.machine.logic;

import mekcore.machine.Machine;
import mekcore.machine.WorkState;
import mekcore.persist.DataTag;
import mekcore.resource.ResourceTank;
import mekcore.resource.Transaction;
import net.kuzuanpa.cemekanism.platform.GeneratorConfig;

import java.util.Map;
import java.util.WeakHashMap;

/** 燃气按燃烧刻结算：乙烯每 mB 提供 40 × 705 J，未用完的燃烧刻随机器存档。 */
public final class GasGeneratorLogic extends GeneratorLogic {
    private static final class State {
        String fuelId;
        GeneratorConfig.Fuel fuel;
        long remainingTicks;
    }

    private final Map<Machine, State> states = new WeakHashMap<>();

    private State state(Machine machine) {
        return states.computeIfAbsent(machine, ignored -> new State());
    }

    @Override
    public boolean needsTick(Machine machine) {
        return hasEnergyRoom(machine) && (state(machine).remainingTicks > 0 || fuel(machine) != null);
    }

    @Override
    public long tick(Machine machine) {
        ResourceTank tank = tank(machine, "fuel");
        State state = state(machine);
        if (state.remainingTicks == 0) {
            state.fuel = fuel(machine);
            state.fuelId = tank.storedId();
        }
        GeneratorConfig.Fuel fuel = state.fuel;
        long room = machine.energyTank().map(t -> t.capacity() - t.amount()).orElse(0L);
        if (fuel == null || room < fuel.energyPerBurnTick()) {
            machine.setState(WorkState.IDLE);
            return 0;
        }
        // 剩余燃烧刻属于已经扣掉的燃料。换气时先烧完它，不能把它当成新燃料。
        long stored = state.fuelId.equals(tank.storedId()) ? tank.amount() : 0;
        double fullness = Math.min(1, (stored + state.remainingTicks / (double) fuel.burnTicks()) / tank.capacity());
        long limit = (long) Math.ceil(fuel.maxBurnMb() * fuel.burnTicks() * fullness);
        long available = stored * fuel.burnTicks() + state.remainingTicks;
        long ticks = Math.min(Math.min(available, limit), room / fuel.energyPerBurnTick());
        if (ticks <= 0) {
            machine.setState(WorkState.IDLE);
            return 0;
        }
        long needed = Math.max(0, ticks - state.remainingTicks);
        long used = needed / fuel.burnTicks() + (needed % fuel.burnTicks() == 0 ? 0 : 1);
        if (tank.extract(used, Transaction.EXECUTE) != used) {
            machine.setState(WorkState.IDLE);
            return 0;
        }
        long inserted = insertEnergy(machine, ticks * fuel.energyPerBurnTick());
        state.remainingTicks += used * fuel.burnTicks() - ticks;
        machine.setState(WorkState.RUNNING);
        return inserted;
    }

    @Override
    public void save(Machine machine, DataTag tag) {
        State state = state(machine);
        if (state.remainingTicks > 0 && state.fuel != null) {
            tag.putString("fuel_id", state.fuelId);
            tag.putLong("remaining_burn_ticks", state.remainingTicks);
            tag.putLong("energy_density", state.fuel.energyDensity());
            tag.putInt("burn_ticks", state.fuel.burnTicks());
            tag.putDouble("max_burn_mb", state.fuel.maxBurnMb());
        }
    }

    @Override
    public void load(Machine machine, DataTag tag) {
        State state = new State();
        long density = tag.getLong("energy_density", 0);
        int burnTicks = tag.getInt("burn_ticks", 1);
        double maxBurn = tag.getDouble("max_burn_mb", 1);
        if (burnTicks > 0 && density >= burnTicks && density % burnTicks == 0
                && Double.isFinite(maxBurn) && maxBurn > 0) {
            state.fuelId = tag.getString("fuel_id", "");
            state.fuel = new GeneratorConfig.Fuel(density, maxBurn, burnTicks);
            state.remainingTicks = Math.max(0, Math.min(burnTicks - 1, tag.getLong("remaining_burn_ticks", 0)));
        }
        states.put(machine, state);
    }

    @Override
    public String name() { return "燃气发电机"; }

    private static GeneratorConfig.Fuel fuel(Machine machine) {
        ResourceTank tank = tank(machine, "fuel");
        return tank.amount() > 0 ? GeneratorConfig.gasFuels().get(tank.storedId()) : null;
    }
}
