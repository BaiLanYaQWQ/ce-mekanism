package net.kuzuanpa.cemekanism.multiblock.logic;

import mekcore.multiblock.MultiblockInstance;
import mekcore.multiblock.MultiblockLogic;

/** 感应矩阵：容量 = 元件容量和、速率 = 供应器输出和（都在成型时 resize 进端口），逐刻不做事。 */
public final class InductionMatrixLogic implements MultiblockLogic {

    @Override
    public long tick(MultiblockInstance instance) {
        return 0;
    }
}
