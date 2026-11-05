package net.kuzuanpa.cemekanism.multiblock;

import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/** Rendering math from Mekanism's ModelRenderer, RenderFusionReactor and RenderSPS. */
final class MultiblockEffectGeometry {
    private static final float INV_SQRT_TWO = (float) (1 / Math.sqrt(2));
    private static final int[][] ROTATIONS = {{-6, -7, 0, 36}, {4, 4, 0, 36}, {5, -3, -35, 106}};
    static final int[] FUSION_COLORS = {0xFF383C, 0xD55ECB, 0xFFA160};

    private MultiblockEffectGeometry() { }

    static double ratio(long amount, long capacity) {
        return capacity <= 0 ? 0 : Math.clamp(amount / (double) capacity, 0, 1);
    }

    static float height(int height, double ratio, boolean gas) {
        int stages = Math.max(height * 1_000, 1);
        int stage = gas ? stages : Math.min(stages, (int) (ratio * stages));
        return Math.max(0.02f, height * (stage / (float) stages) - 0.02f);
    }

    static int density(double ratio) {
        return Math.clamp((int) Math.ceil(Math.min(1, ratio + 0.2) * 8), 1, 8);
    }

    static Transformation fusion(int layer, double temperatureK, long tick) {
        long scaled = Math.round(temperatureK / 100_000_000);
        float scale = switch (layer) {
            case 0 -> 1 + 0.7f * sin(3.14 * scaled + 135);
            case 1 -> 1 + 0.8f * sin(3 * scaled);
            default -> 1 - 0.9f * sin(4 * scaled + 90);
        };
        int[] rotation = ROTATIONS[layer];
        Quaternionf quaternion = new Quaternionf().rotationY(radians(tick * rotation[0] + rotation[2]))
                .rotateAxis(radians(tick * rotation[1] + rotation[3]), 0, INV_SQRT_TWO, INV_SQRT_TWO);
        return new Transformation(new Vector3f(), quaternion, new Vector3f(scale), new Quaternionf());
    }

    static float energyScale(double processed) {
        return processed <= 0 ? 0 : (float) Math.clamp((Math.log10(processed) + 2) / 4, 0, 1);
    }

    /** Unit ribbons run along model +Y, with their endpoints centered at y=-.5 and y=.5. */
    static Transformation bolt(Vector3f start, Vector3f end, float width) {
        Vector3f delta = new Vector3f(end).sub(start);
        float length = delta.length();
        float horizontal = (float) Math.hypot(delta.x, delta.z);
        // atan2 remains accurate for almost vertical/antiparallel coil bolts.
        Quaternionf rotation = horizontal < 1.0e-6f
                ? new Quaternionf().rotationX(delta.y < 0 ? (float) Math.PI : 0)
                : new Quaternionf().rotationAxis((float) Math.atan2(horizontal, delta.y),
                        delta.z / horizontal, 0, -delta.x / horizontal);
        return new Transformation(new Vector3f(start).add(end).mul(0.5f), rotation,
                new Vector3f(width, length, width), new Quaternionf());
    }

    private static float radians(double degrees) { return (float) Math.toRadians(degrees % 360); }
    private static float sin(double degrees) { return (float) Math.sin(radians(degrees)); }
}
