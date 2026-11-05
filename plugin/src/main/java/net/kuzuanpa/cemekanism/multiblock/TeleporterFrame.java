package net.kuzuanpa.cemekanism.multiblock;

/** Planar rectangular frame geometry, shared by discovery, validation and construction. */
public final class TeleporterFrame {
    private TeleporterFrame() { }

    public static boolean accepts(int x, int y, int z) {
        return x == 1 && side(y) && side(z) || y == 1 && side(x) && side(z)
                || z == 1 && side(x) && side(y);
    }

    private static boolean side(int size) { return size >= 3 && size <= 23; }

    public static int[] anchor(int x, int y, int z) {
        if (x == 1) return new int[] {0, 0, z / 2};
        return new int[] {x / 2, 0, 0};
    }
}
