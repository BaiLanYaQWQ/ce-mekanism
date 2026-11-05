package net.kuzuanpa.cemekanism.multiblock.logic;

import mekcore.multiblock.MultiblockInstance;
import mekcore.multiblock.MultiblockLogic;
import mekcore.resource.ResourceStack;
import mekcore.resource.Transaction;
import net.kuzuanpa.cemekanism.multiblock.MultiblockConfig;

/**
 * 热力锅炉：把水烧成蒸汽，热量由热网给（或外部热源通过阀门）。
 * 上游口径：heatAvailable = min((T − 373.15) × 热容 × 水导热, 过热传热 × 元件数)；
 * 每刻可烧 = 效率 0.2 × heatAvailable ÷ 水的汽化焓（= general.maxEnergyPerSteam，默认 10）。
 */
public final class BoilerLogic implements MultiblockLogic {
    @Override public void configure(MultiblockInstance instance) {
        net.kuzuanpa.cemekanism.multiblock.MultiblockResources.configure(instance);
    }


    @Override
    public long tick(MultiblockInstance instance) {
        double temperature = instance.heatNode() == null
                ? MultiblockConfig.ambientK() : instance.heatNode().temperatureK();
        long elements = instance.shape().roleCount(net.kuzuanpa.cemekanism.multiblock.MultiblockBlocks.role("element"));
        double capacity = instance.heatNode() == null ? 1.0 : instance.heatNode().heatCapacity();
        double joulesPerKelvin = instance.heatNode() == null
                ? 1.0 : instance.heatNode().config().joulesPerKelvin();
        // 上游的热量以"热容 × K"记；核心把 1K 定义成 heatCapacity × joulesPerKelvin 焦耳，
        // 所以可烧热量、汽化焓、过热上限都要先换成焦耳，否则温度会少掉整整一个换算系数。
        double enthalpyJ = MultiblockConfig.boilerWaterEnthalpy() * joulesPerKelvin;
        double available = Math.min(
                (temperature - MultiblockConfig.boilerBaseTemperatureK()) * capacity
                        * joulesPerKelvin
                        * MultiblockConfig.boilerWaterConductivity(),
                MultiblockConfig.boilerSuperheatingTransfer() * Math.max(elements, 1L)
                        * joulesPerKelvin);
        long boiled = (long) Math.floor(
                MultiblockConfig.boilerSteamEfficiency() * available / enthalpyJ);
        if (boiled <= 0) {
            return 0;
        }
        long water = instance.tank("water").extract(boiled, Transaction.SIMULATE);
        long room = instance.tank("steam").insert(
                ResourceStack.chemical(MultiblockConfig.STEAM, water), Transaction.SIMULATE);
        long done = Math.min(water, room);
        if (done <= 0) {
            return 0;
        }
        instance.tank("water").extract(done, Transaction.EXECUTE);
        instance.tank("steam").insert(ResourceStack.chemical(MultiblockConfig.STEAM, done), Transaction.EXECUTE);
        if (instance.heatNode() != null) {
            instance.heatNode().addHeat(-done * enthalpyJ
                    / MultiblockConfig.boilerSteamEfficiency());
        }
        return 0;
    }

    @Override
    public boolean needsTick(MultiblockInstance instance) {
        if (instance.heatNode() == null || instance.tank("water").amount() <= 0) {
            return false;
        }
        if (instance.tank("steam").amount() >= instance.tank("steam").capacity()) {
            return false;
        }
        return instance.heatNode().temperatureK() > MultiblockConfig.boilerBaseTemperatureK();
    }
}
