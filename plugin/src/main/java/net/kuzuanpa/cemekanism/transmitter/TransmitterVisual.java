package net.kuzuanpa.cemekanism.transmitter;

import net.kuzuanpa.cemekanism.i18n.I18n;

/** 一根资源元件当前要画的内容层；颜色已经按充满度压成有限亮度档。 */
public record TransmitterVisual(boolean visible, int rgb, int brightnessPercent, String resourceId) {
    public static final TransmitterVisual NONE = new TransmitterVisual(false, 0xFFFFFF, 0, null);

    public String describe() {
        if (!this.visible) {
            return I18n.tr("transmitter.visual.none");
        }
        return I18n.tr("transmitter.visual.content",
                this.resourceId == null ? I18n.tr("transmitter.visual.resource") : this.resourceId,
                String.format("%06X", this.rgb), this.brightnessPercent);
    }
}
