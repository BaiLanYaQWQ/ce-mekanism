package net.kuzuanpa.cemekanism.machine;

import mekcore.api.MekCore;
import mekcore.chemical.Chemical;
import mekcore.chemical.Chemicals;
import mekcore.machine.Direction;
import mekcore.multiblock.*;
import mekcore.resource.ResourceTypes;
import net.kuzuanpa.cemekanism.generated.GeneratedContent;
import net.kuzuanpa.cemekanism.gui.GuiText;
import net.kuzuanpa.cemekanism.multiblock.MultiblockBlocks;
import net.kuzuanpa.cemekanism.platform.ResourceColors;


/** Six-sided selection, structural glass permissions and generated resource colors. */
public final class AppearanceSelfTest {
    private static int checks;
    private static void check(boolean ok, String message) {
        checks++;
        if (!ok) throw new AssertionError(message);
    }

    public static void main(String[] args) {
        var core = MekCore.create(GeneratedContent::register);
        checks += ConfiguratorRaySelfTest.run();
        checks += ConfiguratorModeSelfTest.run();
        for (Direction face : Direction.values()) {
            // Expected world directions, independent of projection/drawing code.
            Direction left = switch (face) { case WEST, EAST -> Direction.NORTH; default -> Direction.WEST; };
            Direction right = left == Direction.NORTH ? Direction.SOUTH : Direction.EAST;
            Direction bottom = switch (face) { case UP, DOWN -> Direction.NORTH; default -> Direction.DOWN; };
            Direction top = bottom == Direction.NORTH ? Direction.SOUTH : Direction.UP;
            Direction[][] expected = {{face.opposite(), left, face.opposite()},
                    {bottom, face, top}, {face.opposite(), right, face.opposite()}};
            double[] centers = {0.125, 0.5, 0.875};
            for (int u = 0; u < 3; u++) for (int v = 0; v < 3; v++) {
                double[] p = ConfiguratorGrid.point(face, centers[u], centers[v]);
                check(ConfiguratorGrid.select(face, p[0], p[1], p[2]) == expected[u][v], face + " cell " + u + ',' + v);
                var region = u == 1 && v == 1 ? ConfiguratorGrid.Region.CENTER
                        : u != 1 && v != 1 ? ConfiguratorGrid.Region.BACK
                        : u == 0 ? ConfiguratorGrid.Region.LOW_U : u == 2 ? ConfiguratorGrid.Region.HIGH_U
                        : v == 0 ? ConfiguratorGrid.Region.LOW_V : ConfiguratorGrid.Region.HIGH_V;
                check(ConfiguratorGrid.region(face, expected[u][v]) == region, face + " visual region " + u + ',' + v);
                var transform = ConfiguratorFaceRenderer.transform(face);
                // Include the client's observed fixed-item horizontal reversal before server transforms.
                var offset = new org.joml.Vector3f((float) (centers[u] - .5), (float) (centers[v] - .5), 0)
                        .rotateY((float) Math.PI).rotate(transform.getRightRotation())
                        .rotate(transform.getLeftRotation()).add(transform.getTranslation());
                double[] center = ConfiguratorGrid.point(face, .5, .5);
                check(Math.abs(center[0] + offset.x - p[0]) < .000001
                        && Math.abs(center[1] + offset.y - p[1]) < .000001
                        && Math.abs(center[2] + offset.z - p[2]) < .000001,
                        face + " model projection matches actual selected cell " + u + ',' + v);
                check(transform.getScale().equals(new org.joml.Vector3f(1, 1, 1)), "overlay is one block at 1:1 scale");
            }
            for (double edge : new double[] {0.25, 0.75}) {
                double[] p = ConfiguratorGrid.point(face, edge, 0.5);
                check(ConfiguratorGrid.select(face, p[0], p[1], p[2]) == face, face + " border selects center");
            }
        }

        for (var def : MultiblockBlocks.all()) {
            if (def.family().equals("qio") || def.family().equals("teleporter")) continue;
            check("glass".equals(def.roleOf("mek:reactor_glass")), def.family() + " reactor glass");
            boolean reactor = def.family().contains("reactor");
            check((def.roleOf("mek:structural_glass") == null) == reactor, def.family() + " structural glass compatibility");
            var spec = core.registrars().multiblocks().require(def.id()).spec();
            if (def.fixed()) {
                var layout = spec.fixedLayout().orElseThrow();
                int w = layout.width(), h = layout.layerCount(), d = layout.depth();
                var result = StructureValidator.validate(spec, w, h, d, (x, y, z) -> {
                    var allowed = layout.rolesAt(x, y, z);
                    return allowed.contains(BlockRoles.GLASS) ? BlockRoles.GLASS : layout.roleAt(x, y, z);
                });
                check(result.issues().isEmpty(), def.family() + " fixed shell accepts glass");
                var bad = StructureValidator.validate(spec, w, h, d, (x, y, z) -> {
                    var allowed = layout.rolesAt(x, y, z);
                    return allowed.contains(BlockRoles.GLASS) ? BlockRole.of("unknown", "unknown") : layout.roleAt(x, y, z);
                });
                check(!bad.issues().isEmpty(), def.family() + " fixed shell rejects unknown blocks");
                var frames = StructureValidator.validate(spec, w, h, d, (x, y, z) ->
                        layout.roleAt(x, y, z) == BlockRoles.FRAME && layout.rolesAt(x, y, z).size() == 1
                                ? BlockRoles.GLASS : layout.roleAt(x, y, z));
                check(!frames.issues().isEmpty(), def.family() + " fixed frame rejects glass");
            } else {
                int w = def.wMin(), h = def.hMin();
                var result = StructureValidator.validate(spec, w, h, w, (x, y, z) -> {
                    var section = StructureValidator.sectionOf(x, y, z, w, h, w);
                    if (section == BlockSection.EDGE) return BlockRoles.FRAME;
                    if (section == BlockSection.FACE) {
                        if (spec.hasOpenTop() && y == h - 1) return BlockRoles.AIR;
                        return BlockRoles.GLASS;
                    }
                    return BlockRoles.AIR;
                });
                check(result.issues().isEmpty(), def.family() + " glass walls satisfy positional validator");
                var bad = StructureValidator.validate(spec, w, h, w, (x, y, z) ->
                        x == 0 && y == 0 && z == 0 ? BlockRoles.GLASS : BlockRoles.FRAME);
                check(bad.issues().stream().anyMatch(i -> i.x() == 0 && i.y() == 0 && i.z() == 0),
                        def.family() + " glass cannot replace a frame corner");
            }
        }
        var chemicals = Chemicals.registry().all();
        check(chemicals.size() == 65, "all chemical definitions present");
        for (Chemical chemical : chemicals) {
            check(chemical.colorArgb() != Chemical.NO_COLOR, chemical.id() + " has color");
            check((chemical.colorArgb() >>> 24) == 255, chemical.id() + " opaque ARGB");
        }
        for (var pair : GeneratedContent.ROTARY_PAIRS) {
            if (!pair.fluid().startsWith("mek:")) continue;
            int rgb = Chemicals.registry().require(pair.chemical()).colorArgb() & 0xFFFFFF;
            check(ResourceColors.fluid(pair.fluid()) == rgb, pair.fluid() + " rotary color");
            check(GuiText.resourceColor(ResourceTypes.FLUID, pair.fluid()) == rgb, pair.fluid() + " tank color");
        }
        check(GuiText.resourceColor(ResourceTypes.FLUID, "mek:heavy_water") == 0x0D1455, "heavy water upstream tint");
        check(GuiText.resourceColor(ResourceTypes.FLUID, "mek:bioethanol") == 0xCEEB3D, "bioethanol upstream tint");
        check(GuiText.resourceColor(ResourceTypes.CHEMICAL, "mek:blue") == 0x366BD0, "blue pigment uses dye registry prefix");
        check(GuiText.resourceColor(ResourceTypes.INFUSION, "mek:bio") == 0x5A4630, "bio uses representative color");
        check(GuiText.resourceColor(ResourceTypes.FLUID, "minecraft:water") == 0x3F76E4, "water keeps its liquid color");
        check(ResourceColors.fluid("unknown:fluid") == null, "unknown fluid keeps caller fallback");
        System.out.println("Appearance self-test: " + checks + " checks passed");
    }
}
