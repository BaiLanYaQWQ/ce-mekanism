package net.kuzuanpa.cemekanism.machine;

import mekcore.machine.Direction;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.property.Property;

/** Use the block's actual facing property so horizontal and six-way machines share the tool. */
final class MachineOrientation {
    private MachineOrientation() {}

    static boolean supports(ImmutableBlockState state, Direction face) {
        Property<net.momirealms.craftengine.core.util.Direction> property = state.getProperty("facing");
        return property != null && property.valueClass() == net.momirealms.craftengine.core.util.Direction.class
                && property.possibleValues().contains(toCraftEngine(face));
    }

    static ImmutableBlockState facing(ImmutableBlockState state, Direction face) {
        if (!supports(state, face)) return state;
        return state.with(state.<net.momirealms.craftengine.core.util.Direction>getProperty("facing"),
                toCraftEngine(face));
    }

    private static net.momirealms.craftengine.core.util.Direction toCraftEngine(Direction face) {
        return net.momirealms.craftengine.core.util.Direction.valueOf(face.name());
    }
}
