package net.kuzuanpa.cemekanism.multiblock.logic;

import mekcore.multiblock.MultiblockInstance;
import mekcore.multiblock.MultiblockLogic;

/** 动态储罐：结构成型、端口容量按体积 resize 之后没有逐刻工作（纯容器）。 */
public final class DynamicTankLogic implements MultiblockLogic {

    @Override
    public long tick(MultiblockInstance instance) {
        return 0;
    }
}
