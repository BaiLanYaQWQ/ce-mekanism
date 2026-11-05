package net.kuzuanpa.cemekanism.multiblock.logic;

import mekcore.multiblock.MultiblockInstance;
import mekcore.multiblock.MultiblockLogic;
import mekcore.persist.DataTag;
import mekcore.resource.ResourceStack;
import mekcore.resource.Transaction;
import net.kuzuanpa.cemekanism.multiblock.MultiblockConfig;
import net.kuzuanpa.cemekanism.multiblock.MultiblockContexts;
import net.kuzuanpa.cemekanism.multiblock.MultiblockHosts;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.Map;
import java.util.WeakHashMap;

/** 超临界移相器：钋 + 线圈能量 -> 反物质；内部每 20 刻按本刻输入能量伤害实体。 */
public final class SpsLogic implements MultiblockLogic {
    @Override public void configure(MultiblockInstance instance) {
        net.kuzuanpa.cemekanism.multiblock.MultiblockResources.configure(instance);
    }


    public static final String POLONIUM = "mek:polonium";
    public static final String ANTIMATTER = "mek:antimatter";

    public static final class State {
        public long inputProcessed;
        public double progress;
        public long lastEnergy;
    }

    private final Map<MultiblockInstance, State> states = new WeakHashMap<>();

    public State state(MultiblockInstance instance) {
        return this.states.computeIfAbsent(instance, ignored -> new State());
    }

    @Override
    public long tick(MultiblockInstance instance) {
        State state = state(instance);
        var input = instance.tank("polonium");
        var output = instance.tank("antimatter");
        var energy = instance.tank("energy");
        if (!POLONIUM.equals(input.storedId()) || input.amount() <= 0
                || output.amount() >= output.capacity()) {
            state.lastEnergy = 0;
            return 0;
        }
        long received = energy.extract(energy.amount(), Transaction.EXECUTE);
        if (received <= 0) {
            state.lastEnergy = 0;
            return 0;
        }
        state.lastEnergy = received;
        double processable = received / (double) MultiblockConfig.spsEnergyPerInputJ();
        long target = (long) Math.floor(state.progress + processable);
        long outputRoom = output.capacity() - output.amount();
        long inputLimit = outputRoom * MultiblockConfig.spsInputPerAntimatter()
                + (long) Math.ceil(Math.max(0, MultiblockConfig.spsInputPerAntimatter() - state.progress)) - 1;
        long operations = Math.min(Math.min(target, input.amount()), inputLimit);
        if (operations <= 0) {
            return 0;
        }
        long extracted = input.extract(operations, Transaction.EXECUTE);
        double total = state.progress + extracted;
        long antimatter = (long) Math.floor(total / MultiblockConfig.spsInputPerAntimatter());
        antimatter = Math.min(antimatter, outputRoom);
        if (antimatter > 0) {
            output.insert(ResourceStack.chemical(ANTIMATTER, antimatter), Transaction.EXECUTE);
            total -= antimatter * MultiblockConfig.spsInputPerAntimatter();
        }
        state.progress = total;
        state.inputProcessed += extracted;
        if (MultiblockContexts.hostOf(instance) != null) {
            kill(instance, state);
        }
        return 0;
    }

    @Override
    public boolean needsTick(MultiblockInstance instance) {
        return instance.tank("energy").amount() > 0
                || instance.tank("polonium").amount() > 0
                        && instance.tank("antimatter").amount() < instance.tank("antimatter").capacity();
    }

    private static void kill(MultiblockInstance instance, State state) {
        MultiblockHosts.Host host = MultiblockContexts.hostOf(instance);
        if (host == null || state.lastEnergy <= 0 || host.world().getFullTime() % 20 != 0) {
            return;
        }
        int sx = Math.max(1, host.box().sizeX() - 2);
        int sy = Math.max(1, host.box().sizeY() - 2);
        int sz = Math.max(1, host.box().sizeZ() - 2);
        Location center = new Location(host.world(),
                host.box().originX() + 1 + sx / 2.0,
                host.box().originY() + 1 + sy / 2.0,
                host.box().originZ() + 1 + sz / 2.0);
        float damage = Math.max(0, state.lastEnergy / 1_000f);
        for (Entity entity : host.world().getNearbyEntities(center, sx / 2.0, sy / 2.0, sz / 2.0)) {
            if (entity instanceof LivingEntity living) {
                living.damage(damage);
            }
        }
    }

    @Override
    public void save(MultiblockInstance instance, DataTag tag) {
        State state = state(instance);
        tag.putLong("input_processed", state.inputProcessed);
        tag.putDouble("progress", state.progress);
        tag.putLong("last_energy", state.lastEnergy);
    }

    @Override
    public void load(MultiblockInstance instance, DataTag tag) {
        State state = state(instance);
        state.inputProcessed = tag.getLong("input_processed", 0);
        state.progress = tag.getDouble("progress", 0);
        state.lastEnergy = tag.getLong("last_energy", 0);
    }
}
