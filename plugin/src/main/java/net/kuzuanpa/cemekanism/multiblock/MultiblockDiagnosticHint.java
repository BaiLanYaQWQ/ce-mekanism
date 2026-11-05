package net.kuzuanpa.cemekanism.multiblock;

import mekcore.multiblock.BlockRoles;
import mekcore.multiblock.StructurePosition;
import mekcore.multiblock.ValidationResult;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/** The same validation drives both the short explanation and the particle positions. */
public record MultiblockDiagnosticHint(String text, List<Marker> markers) {
    public record Marker(StructurePosition position, boolean missing) { }

    public MultiblockDiagnosticHint {
        markers = List.copyOf(markers);
    }

    public static MultiblockDiagnosticHint of(ValidationResult validation, List<MultiblockChecks.Problem> extra) {
        var markers = new LinkedHashMap<StructurePosition, Marker>();
        for (var issue : validation.issues()) {
            var position = new StructurePosition(issue.x(), issue.y(), issue.z());
            boolean missing = issue.found().equals(BlockRoles.AIR) || issue.found().equals(BlockRoles.INTERIOR_EMPTY);
            markers.put(position, new Marker(position, missing));
        }
        for (var problem : extra) {
            if (problem.position() != null) {
                markers.putIfAbsent(problem.position(), new Marker(problem.position(), false));
            }
        }
        String reason;
        if (!validation.issues().isEmpty()) {
            var issue = validation.issues().getFirst();
            reason = "需要" + issue.expected().stream().map(role -> role.zhName()).distinct().sorted()
                    .limit(3).collect(Collectors.joining("／"));
        } else if (!validation.unmet().isEmpty()) {
            reason = validation.unmet().getFirst();
        } else if (!extra.isEmpty()) {
            // Coordinates are conveyed spatially; retain the actual building rule in text.
            reason = extra.getFirst().message().split("[：（]", 2)[0];
        } else {
            reason = validation.summary();
        }
        return new MultiblockDiagnosticHint(reason, List.copyOf(markers.values()));
    }
}
