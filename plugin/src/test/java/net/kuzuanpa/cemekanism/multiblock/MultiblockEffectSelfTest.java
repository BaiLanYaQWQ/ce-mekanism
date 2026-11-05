package net.kuzuanpa.cemekanism.multiblock;

import org.joml.Vector3f;

/** No server needed: protects fluid bounds, full-height gases and the upstream core/bolt transforms. */
public final class MultiblockEffectSelfTest {
    private static int checks;
    private static void check(boolean value, String message) {
        checks++; if (!value) throw new AssertionError(message);
    }
    private static void near(double a, double b, String message) {
        check(Math.abs(a-b) < 1e-5, message + ": " + a + " != " + b);
    }
    public static void main(String[] args) {
        near(MultiblockEffectGeometry.ratio(50, 100), .5, "half tank");
        near(MultiblockEffectGeometry.ratio(100, 0), 0, "zero capacity");
        near(MultiblockEffectGeometry.ratio(200, 100), 1, "clamped capacity");
        for (int height = 1; height <= 16; height++) {
            for (int level = 1; level <= 100; level++) {
                double ratio = level / 100.0;
                near(MultiblockEffectGeometry.height(height, ratio, true), height-.02, "gas fills chamber");
                float liquid = MultiblockEffectGeometry.height(height, ratio, false);
                check(liquid >= .019999 && liquid <= height-.01999, "liquid stays inside chamber");
            }
            near(MultiblockEffectGeometry.height(height, .5, false), height*.5-.02, "liquid level");
        }
        check(MultiblockEffectGeometry.density(.01) < MultiblockEffectGeometry.density(.5), "gas opacity follows amount");
        check(MultiblockEffectGeometry.density(.8) == 8, "upstream gas alpha reaches 1 at 80 percent");
        Vector3f[] points = {new Vector3f(0,3,0), new Vector3f(0,-3,0), new Vector3f(3,0,0),
                new Vector3f(-2,1,3), new Vector3f(), new Vector3f(.01f, -.02f, .03f)};
        for (Vector3f start : points) for (Vector3f end : points) {
            var bolt = MultiblockEffectGeometry.bolt(start,end,.2f);
            Vector3f lower = new Vector3f(0,-.5f,0).mul(bolt.getScale()).rotate(bolt.getLeftRotation()).add(bolt.getTranslation());
            Vector3f upper = new Vector3f(0,.5f,0).mul(bolt.getScale()).rotate(bolt.getLeftRotation()).add(bolt.getTranslation());
            check(lower.distance(start) < 1e-5, "bolt begins at coil " + start + " -> " + end + " actual " + lower);
            check(upper.distance(end) < 1e-5, "bolt ends at center/wall " + start + " -> " + end + " actual " + upper);
        }
        for (int layer = 0; layer < 3; layer++) {
            var a = MultiblockEffectGeometry.fusion(layer,100_000_000,20);
            var b = MultiblockEffectGeometry.fusion(layer,100_000_000,22);
            check(!a.getLeftRotation().equals(b.getLeftRotation()), "each core animates");
            near(a.getLeftRotation().lengthSquared(),1,"normalized core rotation");
            check(a.getScale().x > 0 && a.getScale().x < 2, "upstream core scale range");
        }
        near(MultiblockEffectGeometry.energyScale(.01),0,"SPS minimum power");
        near(MultiblockEffectGeometry.energyScale(100),1,"SPS maximum power");
        System.out.println("Multiblock effects self-test: " + checks + " checks passed");
    }
}
