package net.kuzuanpa.cemekanism.transmitter;

/**
 * 传输元件的世界侧映射（{@code GeneratedContent.TRANSMITTERS} 生成）：方块 id ↔ 族 ↔ 级。
 * 数值不在这里：管道与物品管道的分级数由 `mek-systems.yml` 的 `transmitter:` / `item_pipe:` 段覆盖核心。
 */
public record TransmitterDef(String blockId, String family, String tier, String zh) {

    /** 物流管道单开一条管线（不跟资源族共用 TransmitterTopology）。 */
    public boolean itemPipe() {
        return "logistical_transporter".equals(this.family);
    }

    /** 热导线缆的元件是 HeatNode（不是 TransmitterNode），等级只决定热损。 */
    public boolean heat() {
        return "thermodynamic_conductor".equals(this.family);
    }
}
