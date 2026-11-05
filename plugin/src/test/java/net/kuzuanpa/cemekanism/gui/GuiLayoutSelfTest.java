package net.kuzuanpa.cemekanism.gui;

import mekcore.api.MekCore;
import net.kuzuanpa.cemekanism.generated.GeneratedContent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Checks the generated page plan through the runtime loader, without starting Paper. */
public final class GuiLayoutSelfTest {
    private static int checks;

    public static void main(String[] args) {
        check(GuiText.name("机器").equals("§r§f机器"), "tooltip title resets italic and uses white");
        check(GuiText.lore("说明").equals("§r§7说明"), "tooltip lore resets italic and uses gray");
        check(GuiText.name("能量", 0x123456).equals("§r§x§1§2§3§4§5§6能量"),
                "tooltip resource color uses section-sign RGB");
        var profile = GuiLayout.multiblock();
        check(profile.rows() * 9 == 45, "multiblock container size");
        check(profile.height() == 114 + 18 * profile.rows(), "chest panel height");
        check(profile.playerRows().equals(List.of(121, 139, 157)), "player inventory rows");
        check(profile.hotbarY() == 179, "hotbar position");
        for (var def : GeneratedContent.MULTIBLOCKS) {
            var pages = GuiLayout.multiblockPages(def.id());
            Set<String> shown = new HashSet<>();
            Set<String> keys = new HashSet<>();
            Set<String> panels = new HashSet<>();
            check(pages.size() <= 4, def.id() + " tab hit regions fit row 0");
            check(pages.getFirst().key().equals("main"), def.id() + " default page");
            check(pages.getFirst().panelId().equals(GuiLayout.multiblockPanelId(def.id())),
                    def.id() + " default panel compatibility");
            for (var page : pages) {
                check(keys.add(page.key()), def.id() + " duplicate page key");
                check(panels.add(page.panelId()), def.id() + " duplicate page panel");
                check(page.ports().size() == page.columns().size(), def.id() + " gauge columns");
                Set<Integer> covered = new HashSet<>();
                for (int i = 0; i < page.ports().size(); i++) {
                    check(def.ports().contains(page.ports().get(i)), def.id() + " unknown port");
                    check(shown.add(page.ports().get(i)), def.id() + " duplicate port");
                    var gauge = GuiLayout.multiblockGauge(page, i);
                    check(gauge.x() >= 8 && gauge.x() + gauge.w() <= 168, def.id() + " gauge x bounds");
                    check(gauge.y() >= 36 && gauge.y() + gauge.h() <= 90, def.id() + " gauge y bounds");
                    var cells = GuiLayout.multiblockGaugeCells(page, i);
                    check(gauge.y() >= profile.gridY() + 2 * 18,
                            def.id() + " gauge leaves row 1 free for the port icon");
                    check(cells.size() == 2, def.id() + " tooltip covers both gauge rows");
                    check(cells.stream().map(GuiLayout.Cell::row).toList().equals(List.of(2, 3)),
                            def.id() + " tooltip row mapping");
                    for (var cell : cells) {
                        check(cell.col() == page.columns().get(i), def.id() + " tooltip column mapping");
                        check(covered.add(cell.index()), def.id() + " tooltip overlap");
                    }
                }
            }
            check(shown.equals(new HashSet<>(def.ports())), def.id() + " omitted port");
        }
        var fusion = GuiLayout.multiblockPages("mek:fusion_reactor");
        check(fusion.stream().map(GuiLayout.MultiblockPage::key).toList()
                .equals(List.of("main", "heat", "fuel", "stats")), "fusion upstream pages");
        check(fusion.get(1).ports().equals(List.of("water", "steam")), "fusion water/steam visibility");
        check(fusion.get(2).ports().equals(List.of("deuterium", "tritium", "fuel")), "fusion fuel visibility");
        check(fusion.stream().mapToInt(page -> page.ports().size()).sum() == 7, "all seven fusion ports");

        MekCore core = MekCore.create(GeneratedContent::register);
        int browsableBlueprints = 0;
        int browsableRecipes = 0;
        for (var blueprint : core.registrars().machines().all()) {
            var recipes = core.registrars().recipes().byUsage(blueprint.id());
            if (recipes.isEmpty()) {
                continue;
            }
            browsableBlueprints++;
            browsableRecipes += recipes.size();
            for (var recipe : recipes) {
                check(recipe.inputs().size() <= 3, blueprint.id() + " recipe viewer input columns");
                check(recipe.outputs().size() <= 2, blueprint.id() + " recipe viewer output columns");
            }
        }
        check(browsableBlueprints > 0, "at least one browsable recipe machine");
        check(browsableRecipes == core.registrars().recipes().size(), "all recipes reachable by usage");
        for (var entry : GeneratedContent.BLOCK_TO_BLUEPRINT.entrySet()) {
            var recipes = core.registrars().recipes().byUsage(entry.getValue());
            if (!recipes.isEmpty()) {
                check(GuiLayout.plan(entry.getKey()).progress(),
                        entry.getKey() + " recipe machines need a clickable progress row");
            }
        }
        for (var entry : GeneratedContent.BLOCK_TO_BLUEPRINT.entrySet()) {
            var tier = mekcore.machine.FactoryTier.valueOf(GeneratedContent.BLOCK_TO_FACTORY_TIER
                    .getOrDefault(entry.getKey(), "SINGLE"));
            var machine = new mekcore.machine.Machine(core.registrars().machines().require(entry.getValue()),
                    tier, core.registrars().recipes(), 1, null);
            if (GuiLayout.plan(entry.getKey()) == null) continue;
            var layout = GuiLayout.forMachine(machine, tier, entry.getKey());
            if (entry.getKey().equals("mek:digital_miner")) {
                check(GuiLayout.containerSize(entry.getKey(), tier, 0) == 45, "miner five-row container");
                check(layout.profile().height() == 204, "miner panel matches chest height");
                check(layout.profile().playerRows().equals(List.of(121, 139, 157)), "miner backpack alignment");
                check(layout.energyAnchors().stream().map(GuiLayout.Cell::index).toList()
                        .equals(List.of(39, 40, 41, 42, 43, 44)), "miner energy tooltip coverage");
                var indices = GuiLayout.containerIndices(machine.blueprint(), tier, entry.getKey());
                check(indices.equals(List.of(31, 34)), "miner real backfill and output slots");
                for (int index : indices) {
                    check(!DigitalMinerGui.handles(index), "miner controls never overwrite stored items");
                    check(!DigitalMinerGui.scanCell(index), "miner scan never overwrites stored items");
                    check(!GuiLayout.overlayCell(layout, index), "miner bars never cover stored items");
                }
                for (var cell : layout.energyAnchors()) {
                    check(!DigitalMinerGui.handles(cell.index()), "miner controls leave energy readable");
                }
                check(DigitalMinerGui.handles(15) && DigitalMinerGui.handles(17)
                        && !DigitalMinerGui.handles(16), "miner max Y buttons flank its value");
            }
            var tanks = machine.snapshot().tanks().stream()
                    .filter(tank -> !tank.type().continuous() && tank.capacity() > 0)
                    .map(mekcore.machine.MachineSnapshot.TankSnapshot::name).collect(Collectors.toSet());
            check(layout.tankGauges().stream().map(GuiLayout.TankGauge::port)
                    .collect(Collectors.toSet()).equals(tanks), entry.getKey() + " all tanks visible");
            Set<Integer> anchors = new HashSet<>();
            var itemCells = layout.slots().values().stream().map(GuiLayout.Cell::index).toList();
            for (var gauge : layout.tankGauges()) for (var cell : gauge.anchors()) {
                check(anchors.add(cell.index()), entry.getKey() + " gauges have separate tooltips");
                check(!itemCells.contains(cell.index()), entry.getKey() + " tank does not cover an item slot");
                check(!layout.energyAnchors().contains(cell), entry.getKey() + " tank does not cover energy");
                check(!layout.progressAnchors().contains(cell), entry.getKey() + " tank does not cover progress");
                check(GuiLayout.overlayCell(layout, cell.index()), entry.getKey() + " gauge is not hidden by placeholder");
            }
        }
        System.out.println("GUI layout and recipe browser self-test: " + checks + " checks passed");
    }

    private static void check(boolean ok, String message) {
        checks++;
        if (!ok) {
            throw new AssertionError(message);
        }
    }
}
