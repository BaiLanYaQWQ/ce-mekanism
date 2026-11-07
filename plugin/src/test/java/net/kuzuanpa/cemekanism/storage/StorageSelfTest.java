package net.kuzuanpa.cemekanism.storage;

import mekcore.api.MekCore;
import mekcore.machine.Direction;
import mekcore.machine.FactoryTier;
import mekcore.machine.Machine;
import mekcore.machine.SideMode;
import mekcore.persist.MapDataTag;
import net.kuzuanpa.cemekanism.generated.GeneratedContent;
import java.util.HashSet;
import java.util.List;

public final class StorageSelfTest {
    private static int checks;
    private static void check(boolean condition, String message) { checks++; if (!condition) throw new AssertionError(message); }
    public static void main(String[] args) {
        CountedStore drive = new CountedStore(16_000_000_000L, 2);
        check(drive.insert("named sword", 4_000_000_000L, false) == 4_000_000_000L && drive.total() == 0, "simulation mutated");
        check(drive.insert("named sword", 4_000_000_000L, true) == 4_000_000_000L, "32-bit overflow");
        check(drive.insert("other sword", 2, true) == 2 && drive.entries().size() == 2, "metadata folded");
        check(drive.insert("third type", 1, true) == 0, "type limit bypass");
        check(drive.insert("named sword", Long.MAX_VALUE, true) == 11_999_999_998L && drive.total() == 16_000_000_000L, "capacity overflow");
        check(drive.extract("other sword", -1, true) == 0, "negative extract");
        check(drive.extract("other sword", 2, false) == 2 && drive.count("other sword") == 2, "extract simulation mutated");
        check(drive.extract("other sword", 20, true) == 2 && drive.insert("third type", 2, true) == 2, "type slot not freed");
        MapDataTag tag = new MapDataTag(); drive.save(tag); drive.save(tag);
        CountedStore restored = new CountedStore(drive.capacity(), drive.typeLimit()); restored.load(tag);
        check(restored.entries().equals(drive.entries()), "restart duplicated or lost items");
        CountedStore tiny = new CountedStore(1, 1); tiny.insert("keep", 1, true);
        try { tiny.load(tag); throw new AssertionError("corrupt/oversized save accepted"); }
        catch (IllegalArgumentException expected) { check(tiny.count("keep") == 1, "failed load discarded state"); }
        CountedStore max = new CountedStore(Long.MAX_VALUE, 1); max.insert("x", Long.MAX_VALUE, true);
        check(max.insert("x", 1, true) == 0 && max.extract("x", Long.MAX_VALUE, true) == Long.MAX_VALUE, "long boundary overflow");
        CountedStore a = new CountedStore(10, 1), b = new CountedStore(10, 1);
        a.insert("a", 1, true); b.insert("b", 1, true);
        check(a.insert("c", 1, false) + b.insert("c", 1, false) == 0, "aggregate drive types bypass per-drive type limit");
        CountedStore merged = CountedStore.merge(20, 2, List.of(a, b));
        check(merged.total() == 2 && merged.count("a") == 1 && merged.count("b") == 1, "drive upgrade lost one source");
        try { CountedStore.merge(20, 1, List.of(a, b)); throw new AssertionError("combined type overflow accepted"); }
        catch (IllegalArgumentException expected) { check(a.total() == 1 && b.total() == 1, "rejected upgrade mutated sources"); }
        CountedStore big = new CountedStore(8_000_000_000L, 1); big.insert("shared", 4_000_000_000L, true);
        check(CountedStore.merge(16_000_000_000L, 1, List.of(big, big, big, big)).count("shared") == 16_000_000_000L,
                "four-drive same-type merge overflow");
        try { CountedStore.merge(10, 1, List.of(big)); throw new AssertionError("combined capacity overflow accepted"); }
        catch (IllegalArgumentException expected) { checks++; }
        var cursor = new MiningCursor(32, -64, -63, 0); var visited = new HashSet<MiningCursor.Offset>();
        while (!cursor.done()) { check(visited.add(cursor.current()), "scan revisited block"); cursor.advance(); }
        check(visited.size() == 65 * 65 * 2 && cursor.volume() == visited.size(), "65x65 coverage wrong");
        check(visited.contains(new MiningCursor.Offset(-32,-64,-32)) && visited.contains(new MiningCursor.Offset(32,-63,32)), "edge excluded");
        var resume = new MiningCursor(32, -64, 60, 8123); var original = resume.current();
        check(new MiningCursor(32,-64,60,resume.index()).current().equals(original), "resume moved cursor");
        check(new MiningCursor(0,5,5,0).volume() == 1, "single block scan");
        try { new MiningCursor(33,0,1,0); throw new AssertionError("radius >32"); } catch (IllegalArgumentException expected) { checks++; }
        ItemSelection selection = new ItemSelection();
        check(selection.add("Minecraft:Diamond_Ore") && !selection.add("minecraft:diamond_ore"), "filter normalization/duplicate");
        selection.add("minecraft:deepslate_diamond_ore");
        selection.setReplacementTarget("minecraft:diamond_ore", "minecraft:cobblestone");
        selection.setRequiresReplacement("minecraft:diamond_ore", true);
        selection.setEnabled("minecraft:deepslate_diamond_ore", false);
        MapDataTag filterTag = new MapDataTag(); selection.save(filterTag);
        ItemSelection restoredSelection = new ItemSelection(); restoredSelection.load(filterTag);
        check(restoredSelection.rules.equals(selection.rules), "filter order not restored");
        check(restoredSelection.replacementTarget("minecraft:diamond_ore").equals("minecraft:cobblestone")
                && restoredSelection.requiresReplacement("minecraft:diamond_ore")
                && !restoredSelection.enabled("minecraft:deepslate_diamond_ore"),
                "per-filter settings not restored");
        MapDataTag legacyFilter = new MapDataTag();
        legacyFilter.putString("rules", "minecraft:iron_ore,minecraft:gold_ore");
        ItemSelection legacyRestored = new ItemSelection(); legacyRestored.load(legacyFilter);
        check(legacyRestored.rules.size() == 2 && legacyRestored.enabled("minecraft:iron_ore"),
                "legacy filter migration");
        defaultEnergyFaces();
        System.out.println("B5 storage self-test: " + checks + " checks passed");
    }

    private static void defaultEnergyFaces() {
        var core = MekCore.create(GeneratedContent::register);
        var separator = new Machine(core.registrars().machines().require("mek:electrolytic_separator"),
                FactoryTier.SINGLE, core.registrars().recipes(), 1, null);
        StorageRuntime.defaults(separator, Direction.NORTH);
        for (Direction side : Direction.values()) {
            check(separator.side("energy", side) == SideMode.INPUT, "consumer energy input on " + side);
            check(separator.itemSide(side) == SideMode.NONE, "default energy does not enable items on " + side);
            for (String port : separator.portNames()) {
                if (!"energy".equals(port)) {
                    check(separator.side(port, side) == SideMode.NONE,
                            "default energy does not enable " + port + " on " + side);
                }
            }
        }

        var heater = new Machine(core.registrars().machines().require("mek:resistive_heater"),
                FactoryTier.SINGLE, core.registrars().recipes(), 1, null);
        StorageRuntime.defaults(heater, Direction.NORTH);
        check(heater.side("energy", Direction.EAST) == SideMode.INPUT
                        && heater.side("energy", Direction.WEST) == SideMode.INPUT
                        && heater.side("energy", Direction.NORTH) == SideMode.NONE,
                "special machine energy faces stay upstream-compatible");

        var cube = new Machine(core.registrars().machines().require("mek:ultimate_energy_cube"),
                FactoryTier.SINGLE, core.registrars().recipes(), 1, null);
        StorageRuntime.defaults(cube, Direction.NORTH);
        for (Direction side : Direction.values()) {
            check(cube.side("energy", side) == SideMode.BOTH, "energy cube remains bidirectional on " + side);
        }
    }
}
