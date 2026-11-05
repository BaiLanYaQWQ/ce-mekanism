package net.kuzuanpa.cemekanism.storage;

/** Bounded, restartable scan over (2r+1)^2 columns, top down. Never allocates an entire 65x65 volume. */
public final class MiningCursor {
    public record Offset(int x, int y, int z) { }
    private final int radius, minY, maxY, width;
    private final long volume;
    private long index;
    public MiningCursor(int radius, int minY, int maxY, long index) {
        if (radius < 0 || radius > 32 || minY > maxY) throw new IllegalArgumentException("invalid mining range");
        this.radius = radius; this.minY = minY; this.maxY = maxY; width = 2 * radius + 1;
        volume = (long) width * width * ((long) maxY - minY + 1);
        this.index = Math.max(0, Math.min(index, volume));
    }
    public long index() { return index; }
    public long volume() { return volume; }
    public boolean done() { return index >= volume; }
    public Offset current() {
        if (done()) throw new IllegalStateException("scan complete");
        return new Offset((int) (index % width) - radius, maxY - (int) (index / ((long) width * width)),
                (int) ((index / width) % width) - radius);
    }
    public void advance() { if (!done()) index++; }
}
