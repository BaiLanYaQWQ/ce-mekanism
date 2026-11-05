package net.kuzuanpa.cemekanism.machine;

import mekcore.machine.FactoryTier;
import net.kuzuanpa.cemekanism.generated.GeneratedContent;

/**
 * 世界侧记账 1（文档 03 第五节）：CE 方块 id ↔ 核心蓝图 id ↔ 工厂等级。
 * 两张表都来自 tools/mek.py 生成的 GeneratedContent，别在别处再抄一份。
 */
public final class MachineBlocks {

    private MachineBlocks() {
    }

    public static boolean isMachine(String blockId) {
        return GeneratedContent.BLOCK_TO_BLUEPRINT.containsKey(blockId);
    }

    public static String blueprintId(String blockId) {
        String blueprint = GeneratedContent.BLOCK_TO_BLUEPRINT.get(blockId);
        if (blueprint == null) {
            throw new IllegalArgumentException(blockId + " 不是机器方块（映射表里没有）");
        }
        return blueprint;
    }

    public static FactoryTier factoryTier(String blockId) {
        String tier = GeneratedContent.BLOCK_TO_FACTORY_TIER.get(blockId);
        return tier == null ? FactoryTier.SINGLE : FactoryTier.valueOf(tier);
    }

    public static int size() {
        return GeneratedContent.BLOCK_TO_BLUEPRINT.size();
    }
}
