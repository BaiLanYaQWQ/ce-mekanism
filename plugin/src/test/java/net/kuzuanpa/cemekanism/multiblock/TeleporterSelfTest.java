package net.kuzuanpa.cemekanism.multiblock;

import mekcore.api.MekCore;
import mekcore.multiblock.BlockRoles;
import mekcore.multiblock.StructureValidator;
import mekcore.persist.MapDataTag;
import net.kuzuanpa.cemekanism.generated.GeneratedContent;
import net.kuzuanpa.cemekanism.multiblock.logic.TeleporterLogic;

/** Real generated blueprint: planar sizes, hollow frames, exact controller count and saved bindings. */
public final class TeleporterSelfTest {
    private static int checks;
    private static void check(boolean ok, String message) {
        checks++; if (!ok) throw new AssertionError(message);
    }

    public static void main(String[] args) {
        var core = MekCore.create(GeneratedContent::register);
        var def = MultiblockBlocks.require("mek:teleporter");
        for (int axis = 0; axis < 3; axis++) for (int a : new int[] {3, 4, 9, 23})
            for (int b : new int[] {3, 4, 15, 23}) {
                int x = axis == 0 ? 1 : a, y = axis == 1 ? 1 : axis == 0 ? a : b;
                int z = axis == 2 ? 1 : b;
                check(def.accepts(x, y, z), "planar size accepted");
                var instance = core.factories().multiblock(def.id(), x, y, z);
                int[] anchor = def.buildAnchor(x, y, z);
                var plan = instance.buildPlan();
                check(plan.size() == x * y * z, "complete build plan");
                check(instance.validate((px, py, pz) -> px == anchor[0] && py == anchor[1] && pz == anchor[2]
                        ? BlockRoles.CONTROLLER : StructureValidator.sectionOf(px, py, pz, x, y, z)
                        == mekcore.multiblock.BlockSection.EDGE ? BlockRoles.FRAME : BlockRoles.AIR).ok(),
                        "frame forms in all planes");
                check(instance.roleCount(BlockRoles.FRAME) == 2 * (a + b) - 5, "perimeter scales");
                check(!instance.validate((px, py, pz) -> BlockRoles.FRAME).ok(), "filled frame rejected");
                check(!instance.validate((px, py, pz) -> BlockRoles.AIR).ok(), "missing frame rejected");
            }
        for (int[] size : new int[][] {{1, 2, 4}, {2, 4, 1}, {1, 24, 4}, {4, 4, 4}, {1, 1, 4}})
            check(!def.accepts(size[0], size[1], size[2]), "invalid plane/size rejected");
        var instance = core.factories().multiblock(def.id(), 3, 4, 1);
        check(!instance.validate((x, y, z) -> y == 0 ? BlockRoles.CONTROLLER
                : x == 0 || x == 2 || y == 3 ? BlockRoles.FRAME : BlockRoles.AIR).ok(), "multiple controllers rejected");
        var logic = (TeleporterLogic) instance.blueprint().logic();
        logic.state(instance).frequency = "测试频率 alpha";
        instance.tank("energy").set(null, 123456);
        var saved = new MapDataTag(); instance.save(saved);
        var restored = core.factories().multiblock(def.id(), 7, 8, 1); restored.load(saved);
        check(logic.state(restored).frequency.equals("测试频率 alpha"), "frequency survives resized-instance reload");
        check(restored.tank("energy").amount() == 123456, "energy survives reload");
        check(logic.state(instance) != logic.state(restored), "binding state isolated per portal");
        check(TeleporterLogic.normalizeFrequency("  alpha  ").equals("alpha"), "frequency whitespace normalized");
        try { TeleporterLogic.normalizeFrequency("x".repeat(33)); throw new AssertionError("long frequency accepted"); }
        catch (IllegalArgumentException expected) { checks++; }
        check(TeleporterLogic.energyCost(0, 0, 0, 1, 3, 4, 0, 1, true) == 110, "distance energy cost");
        check(TeleporterLogic.energyCost(80, 10, 80, 1, 10, 10, 10, 8, false) == 1100, "dimension scale energy cost");
        check(TeleporterLogic.energyCost(10, 10, 10, 8, 80, 10, 80, 1, false) == 1100, "reverse dimension scale");
        System.out.println("Teleporter self-test: " + checks + " checks passed");
    }
}
