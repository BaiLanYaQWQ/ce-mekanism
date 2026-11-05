package net.kuzuanpa.cemekanism.transmitter;

import mekcore.logistics.TransmitterFamily;
import mekcore.logistics.TransmitterTier;
import mekcore.logistics.Transmitters;
import mekcore.machine.MachineTier;
import net.kuzuanpa.cemekanism.generated.GeneratedContent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 世界侧记账 1（文档 06）：方块 id ↔ 族 ↔ 级 ↔ 中文名。
 * 表来自 {@link GeneratedContent#TRANSMITTERS}，别在别处再抄一份。
 */
public final class TransmitterBlocks {

    private static final Map<String, TransmitterDef> BY_ID = new LinkedHashMap<>();

    static {
        for (TransmitterDef def : GeneratedContent.TRANSMITTERS) {
            BY_ID.put(def.blockId(), def);
        }
    }

    private TransmitterBlocks() {
    }

    public static int size() {
        return BY_ID.size();
    }

    public static boolean isTransmitter(String blockId) {
        return BY_ID.containsKey(blockId);
    }

    public static TransmitterDef require(String blockId) {
        TransmitterDef def = BY_ID.get(blockId);
        if (def == null) {
            throw new IllegalArgumentException(blockId + " 不是传输元件（映射表里没有）");
        }
        return def;
    }

    public static MachineTier tier(TransmitterDef def) {
        return MachineTier.valueOf(def.tier());
    }

    /** 资源族的族对象；物品管道没有族（它单开一条管线）。 */
    public static Optional<TransmitterFamily> family(TransmitterDef def) {
        return def.itemPipe() ? Optional.empty() : Transmitters.families().find(def.family());
    }

    /**
     * 元件这一级的分级数值。热导线缆只登记了基础级：传输速率对热量没有意义，
     * 四级之间差的是热损（{@link mekcore.heat.HeatTier}，值是配置项）。
     */
    public static TransmitterTier transmitterTier(TransmitterDef def) {
        TransmitterFamily family = family(def).orElseThrow(
                () -> new IllegalArgumentException(def.blockId() + " 不是资源族元件"));
        MachineTier wanted = def.heat() ? MachineTier.BASIC : tier(def);
        return Transmitters.tier(family, wanted).orElseThrow(
                () -> new IllegalArgumentException("未注册 " + family.id() + " 的 " + wanted + " 级数值"));
    }
}
