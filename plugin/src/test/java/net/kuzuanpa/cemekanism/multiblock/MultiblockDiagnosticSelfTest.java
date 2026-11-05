package net.kuzuanpa.cemekanism.multiblock;

import mekcore.multiblock.BlockRoles;
import mekcore.multiblock.StructureIssue;
import mekcore.multiblock.StructurePosition;
import mekcore.multiblock.ValidationResult;

import java.util.List;
import java.util.Map;
import java.util.Set;

/** Location and explanation regressions without starting a Paper world. */
public final class MultiblockDiagnosticSelfTest {
    private static int checks;

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }

    public static void main(String[] args) {
        var position = new StructurePosition(2, 3, 4);
        var missing = new StructureIssue(2, 3, 4, "面", Set.of(BlockRoles.FRAME), BlockRoles.INTERIOR_EMPTY);
        var wrong = new StructureIssue(1, 2, 3, "内部", Set.of(BlockRoles.INTERIOR_EMPTY), BlockRoles.FRAME);
        var invalid = ValidationResult.failed("共 2 处不符", 5, 5, 5,
                List.of(missing, wrong), List.of(), Map.of(), Map.of());
        var hint = MultiblockDiagnosticHint.of(invalid, List.of(
                new MultiblockChecks.Problem("同一格的附加问题", position),
                new MultiblockChecks.Problem("缺少组件")));
        check(hint.markers().size() == 2, "overlapping checks do not duplicate particles");
        check(hint.markers().getFirst().position().equals(position), "validation cell keeps all three local axes");
        check(hint.markers().getFirst().missing(), "missing casing uses missing-block particles");
        check(!hint.markers().getLast().missing(), "obstructed interior uses invalid-block particles");
        check(hint.text().equals("需要" + BlockRoles.FRAME.zhName()), "explanation identifies the required block");
        check(!hint.text().contains("(2,"), "coordinates are conveyed by particles");

        var formed = ValidationResult.formed(5, 5, 5, Map.of(), Map.of());
        hint = MultiblockDiagnosticHint.of(formed, List.of(
                new MultiblockChecks.Problem("电磁线圈不连续：第 3 层缺一格", position)));
        check(hint.markers().getFirst().position().equals(position), "postchecks also identify the faulty cell");
        check(hint.text().equals("电磁线圈不连续"), "postcheck provides a short building rule");
        hint = MultiblockDiagnosticHint.of(formed, List.of(new MultiblockChecks.Problem("缺少感应元件：至少一个")));
        check(hint.markers().isEmpty(), "missing counts do not invent a required placement");
        check(hint.text().equals("缺少感应元件"), "count-only failures still explain what is missing");

        var size = ValidationResult.failed("尺寸必须是 5×5×5", 3, 3, 3, List.of(), List.of(), Map.of(), Map.of());
        check(MultiblockDiagnosticHint.of(size, List.of()).text().equals(size.summary()), "size errors keep the required dimensions");
        var count = ValidationResult.failed("数量不符", 5, 5, 5, List.of(), List.of("至少需要一个端口"), Map.of(), Map.of());
        check(MultiblockDiagnosticHint.of(count, List.of()).text().equals("至少需要一个端口"), "unlocated requirements retain actionable text");
        System.out.println("Multiblock diagnostics: " + checks + " checks passed");
    }
}
