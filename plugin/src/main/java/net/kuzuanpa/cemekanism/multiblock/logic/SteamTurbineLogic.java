package net.kuzuanpa.cemekanism.multiblock.logic;

import mekcore.multiblock.MultiblockInstance;
import mekcore.multiblock.MultiblockLogic;
import mekcore.resource.ResourceStack;
import mekcore.resource.Transaction;
import mekcore.spec.SteamMath;
import net.kuzuanpa.cemekanism.multiblock.MultiblockConfig;
import net.kuzuanpa.cemekanism.multiblock.MultiblockContexts;
import net.kuzuanpa.cemekanism.multiblock.MultiblockHosts;

/**
 * 工业蒸汽涡轮：蒸汽 → 电。数值全在 {@link SteamMath}（可配），叶片数是"装在转子里的物品数"
 * （上游 getHousedBlades），能量因子按 min(叶片, 线圈数 × 每线圈叶片数) 取。
 */
public final class SteamTurbineLogic implements MultiblockLogic {
    @Override public void configure(MultiblockInstance instance) {
        net.kuzuanpa.cemekanism.multiblock.MultiblockResources.configure(instance);
    }


    @Override
    public long tick(MultiblockInstance instance) {
        MultiblockHosts.Host host = MultiblockContexts.hostOf(instance);
        // 上游 TurbineValidator 数的是"复杂旋钮下方连续的转子立柱"，不是整座结构的高度。
        int rotorHeight = Math.max(1, instance.shape().roleCount(
                net.kuzuanpa.cemekanism.multiblock.MultiblockBlocks.role("rotor")));
        int dispersers = instance.shape().roleCount(
                net.kuzuanpa.cemekanism.multiblock.MultiblockBlocks.role("disperser"));
        int vents = instance.shape().roleCount(
                net.kuzuanpa.cemekanism.multiblock.MultiblockBlocks.role("vent"));
        long coils = instance.shape().roleCount(
                net.kuzuanpa.cemekanism.multiblock.MultiblockBlocks.role("coil"));
        long condensers = instance.shape().roleCount(
                net.kuzuanpa.cemekanism.multiblock.MultiblockBlocks.role("condenser"));
        long blades = host == null ? 0 : host.blades();
        long usableBlades = Math.min(blades, coils * MultiblockConfig.turbineBladesPerCoil());

        long flow = Math.min(SteamMath.disperserFlow(instance.sizeX(), rotorHeight, dispersers),
                SteamMath.ventFlow(vents));
        long steam = instance.tank("steam").extract(Math.min(flow, instance.tank("steam").amount()),
                Transaction.SIMULATE);
        if (steam <= 0) {
            return 0;
        }
        double factor = SteamMath.energyFactor((int) usableBlades);
        if (factor <= 0) {
            return 0;                                // 没有叶片就不发电，也不吞蒸汽
        }
        long room = Math.max(0, instance.tank("energy").capacity() - instance.tank("energy").amount());
        steam = Math.min(steam, (long) Math.floor(room / factor));
        if (steam <= 0) {
            return 0;                                // 缓存满了就停在原地，不吞蒸汽
        }
        long joules = Math.round(steam * factor);
        long accepted = instance.tank("energy").insert(ResourceStack.energy(joules), Transaction.SIMULATE);
        if (accepted < joules) {
            steam = (long) Math.floor(accepted / factor);
            if (steam <= 0) {
                return 0;
            }
            joules = Math.round(steam * factor);
        }
        long water = SteamMath.condenserOutput((int) condensers);
        water = Math.min(steam, water);
        if (water > 0 && instance.tank("water").insert(
                ResourceStack.fluid(MultiblockConfig.WATER, water), Transaction.SIMULATE) < water) {
            return 0;                                // 回水放不下时整笔交易作废，和上游一致
        }
        instance.tank("steam").extract(steam, Transaction.EXECUTE);
        instance.tank("energy").insert(ResourceStack.energy(joules), Transaction.EXECUTE);
        if (water > 0) {
            instance.tank("water").insert(ResourceStack.fluid(MultiblockConfig.WATER, water), Transaction.EXECUTE);
        }
        return joules;
    }

    @Override
    public boolean needsTick(MultiblockInstance instance) {
        return instance.tank("steam").amount() > 0
                && instance.tank("energy").amount() < instance.tank("energy").capacity();
    }
}
