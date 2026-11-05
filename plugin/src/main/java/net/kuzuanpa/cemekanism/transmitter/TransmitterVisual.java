package net.kuzuanpa.cemekanism.transmitter;

/** 一根资源元件当前要画的内容层；颜色已经按充满度压成有限亮度档。 */
public record TransmitterVisual(boolean visible, int rgb, int brightnessPercent, String resourceId) {
    public static final TransmitterVisual NONE = new TransmitterVisual(false, 0xFFFFFF, 0, null);

    public String describe() {
        if (!this.visible) {
            return "内容：无";
        }
        return String.format("内容：%s，颜色 #%06X，亮度 %d%%",
                this.resourceId == null ? "资源" : this.resourceId,
                this.rgb, this.brightnessPercent);
    }
}
