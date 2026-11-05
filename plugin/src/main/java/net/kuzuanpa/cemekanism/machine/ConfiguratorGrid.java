package net.kuzuanpa.cemekanism.machine;

import mekcore.machine.Direction;
import net.kuzuanpa.cemekanism.i18n.I18n;

import java.util.Locale;

/** GT6 face grid: center = front, edge strips = adjacent sides, four corners = back. */
public final class ConfiguratorGrid {
    public static final double[] EDGES = {0, 0.25, 0.75, 1};

    private ConfiguratorGrid() {}

    public static Direction select(Direction face, double x, double y, double z) {
        double u = switch (face) { case EAST, WEST -> z; default -> x; };
        double v = switch (face) { case UP, DOWN -> z; default -> y; };
        boolean outerU = u < 0.25 || u > 0.75;
        boolean outerV = v < 0.25 || v > 0.75;
        if (outerU && outerV) return face.opposite();
        if (outerU) {
            return switch (face) {
                case EAST, WEST -> u < 0.25 ? Direction.NORTH : Direction.SOUTH;
                default -> u < 0.25 ? Direction.WEST : Direction.EAST;
            };
        }
        if (outerV) {
            return switch (face) {
                case UP, DOWN -> v < 0.25 ? Direction.NORTH : Direction.SOUTH;
                default -> v < 0.25 ? Direction.DOWN : Direction.UP;
            };
        }
        return face;
    }

    /** Point on the exposed face, also used to draw the exact grid used by select(). */
    public static double[] point(Direction face, double u, double v) {
        return switch (face) {
            case WEST -> new double[] {-0.008, v, u};
            case EAST -> new double[] {1.008, v, u};
            case DOWN -> new double[] {u, -0.008, v};
            case UP -> new double[] {u, 1.008, v};
            case NORTH -> new double[] {u, v, -0.008};
            case SOUTH -> new double[] {u, v, 1.008};
        };
    }

    public enum Region {
        CENTER, BACK, LOW_U, HIGH_U, LOW_V, HIGH_V;

        public String itemId() {
            return "mek:render/configurator_" + name().toLowerCase(Locale.ROOT);
        }
    }

    /** The visual region is independent of viewing direction; U/V follow point(). */
    public static Region region(Direction face, Direction selected) {
        if (selected == face) return Region.CENTER;
        if (selected == face.opposite()) return Region.BACK;
        Direction lowU = switch (face) { case EAST, WEST -> Direction.NORTH; default -> Direction.WEST; };
        Direction highU = lowU == Direction.NORTH ? Direction.SOUTH : Direction.EAST;
        if (selected == lowU) return Region.LOW_U;
        if (selected == highU) return Region.HIGH_U;
        Direction lowV = switch (face) { case UP, DOWN -> Direction.NORTH; default -> Direction.DOWN; };
        return selected == lowV ? Region.LOW_V : Region.HIGH_V;
    }

    public static String name(Direction side) {
        return I18n.tr("direction." + side.name().toLowerCase(Locale.ROOT));
    }
}
