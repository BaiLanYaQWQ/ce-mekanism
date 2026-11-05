package net.kuzuanpa.cemekanism.platform;

import mekcore.chemical.Chemical;
import mekcore.chemical.Chemicals;
import net.kuzuanpa.cemekanism.generated.GeneratedContent;

/** Liquid forms use the registered chemical color; conversion data is the source of the pairing. */
public final class ResourceColors {
    private ResourceColors() {}

    public static Integer fluid(String id) {
        if (id == null || !id.startsWith("mek:")) return null;
        Integer explicit = GeneratedContent.FLUID_COLORS.get(id);
        if (explicit != null) return explicit;
        for (var pair : GeneratedContent.ROTARY_PAIRS) {
            if (id.equals(pair.fluid())) return chemical(pair.chemical());
        }
        return chemical(id);
    }

    private static Integer chemical(String id) {
        Chemical chemical = Chemicals.registry().find(id).orElse(null);
        return chemical == null || chemical.colorArgb() == Chemical.NO_COLOR
                ? null : chemical.colorArgb() & 0xFFFFFF;
    }
}
