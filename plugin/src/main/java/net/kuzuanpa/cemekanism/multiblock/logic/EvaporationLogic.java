package net.kuzuanpa.cemekanism.multiblock.logic;

import mekcore.multiblock.MultiblockInstance;
import mekcore.multiblock.MultiblockLogic;
import mekcore.resource.ResourceStack;
import mekcore.resource.Transaction;
import net.kuzuanpa.cemekanism.multiblock.MultiblockConfig;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 热力蒸馏塔：水 → 盐水 → 锂（上游 evaporating 配方 10 mB 进 1 mB 出）。
 * 每刻可处理的份数 = n = (min(3000, T) − 环境) × 倍率 × (高度 / 18)，n<1 就每 ceil(1/n) 刻做一份。
 */
public final class EvaporationLogic implements MultiblockLogic {
    @Override public void configure(MultiblockInstance instance) {
        net.kuzuanpa.cemekanism.multiblock.MultiblockResources.configure(instance);
    }


    /** 同一家族的多座蒸馏塔共用一个逻辑实例，进度必须按结构分开。 */
    private final Map<MultiblockInstance, Double> credits = Collections.synchronizedMap(new WeakHashMap<>());

    @Override
    public long tick(MultiblockInstance instance) {
        double temperature = instance.heatNode() == null
                ? MultiblockConfig.ambientK() : instance.heatNode().temperatureK();
        double factor = (Math.min(MultiblockConfig.evaporationMaxTemperatureK(), temperature)
                - MultiblockConfig.ambientK()) * MultiblockConfig.evaporationTempMultiplier()
                * ((double) instance.sizeY() / MultiblockConfig.evaporationMaxHeight());
        if (factor <= 0) {
            return 0;
        }
        double credit = credits.getOrDefault(instance, 0.0) + factor;
        long operations = (long) Math.floor(credit + 1e-9);
        if (operations > 0) operate(instance, operations);
        // Keep only the fraction, so blocked output cannot store unbounded future production.
        credits.put(instance, Math.max(0, credit - operations));
        return 0;
    }

    @Override public void save(MultiblockInstance instance, mekcore.persist.DataTag tag) {
        tag.putDouble("operation_credit", credits.getOrDefault(instance, 0.0));
    }
    @Override public void load(MultiblockInstance instance, mekcore.persist.DataTag tag) {
        credits.put(instance, Math.max(0, Math.min(.999999999999, tag.getDouble("operation_credit", 0))));
    }

    @Override
    public boolean needsTick(MultiblockInstance instance) {
        if (instance.tank("water").amount() <= 0) {
            return false;
        }
        if (instance.heatNode() == null) {
            return false;
        }
        double factor = (Math.min(MultiblockConfig.evaporationMaxTemperatureK(),
                instance.heatNode().temperatureK()) - MultiblockConfig.ambientK())
                * MultiblockConfig.evaporationTempMultiplier()
                * ((double) instance.sizeY() / MultiblockConfig.evaporationMaxHeight());
        return factor > 0;
    }

    private long operate(MultiblockInstance instance, long operations) {
        String input = instance.tank("water").resourceId() == null
                ? MultiblockConfig.WATER : instance.tank("water").resourceId();
        String output = MultiblockConfig.WATER.equals(input) ? MultiblockConfig.BRINE
                : (MultiblockConfig.BRINE.equals(input) ? MultiblockConfig.LITHIUM : null);
        if (output == null) {
            return 0;
        }
        long done = Math.min(operations, instance.tank("water").amount() / 10);
        done = Math.min(done, instance.tank("output").insert(ResourceStack.fluid(output, done), Transaction.SIMULATE));
        if (done > 0) {
            instance.tank("water").extract(done * 10, Transaction.EXECUTE);
            instance.tank("output").insert(ResourceStack.fluid(output, done), Transaction.EXECUTE);
        }
        return 0;
    }
}
