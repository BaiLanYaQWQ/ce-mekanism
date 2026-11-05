package net.kuzuanpa.cemekanism.multiblock;

import net.kuzuanpa.cemekanism.i18n.I18n;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 结构的位置规则（文档 05 第七节第 5 条）：上游 postcheck 判的那些东西，核心的通用 spec 表达不了。
 * 每个 check 只报"缺什么/多余什么"，不碰容量与速率。
 */
public final class MultiblockChecks {

    private MultiblockChecks() {
    }

    public record Problem(String message, mekcore.multiblock.StructurePosition position) {
        public Problem(String message) { this(message, null); }
    }

    private static final class Problems extends ArrayList<Problem> {
        void add(String message) { super.add(new Problem(message)); }
        void at(int x, int y, int z, String message) {
            super.add(new Problem(message, new mekcore.multiblock.StructurePosition(x, y, z)));
        }
    }

    public static List<String> run(StructureBox box) {
        return inspect(box).stream().map(Problem::message).toList();
    }

    public static List<Problem> inspect(StructureBox box) {
        Problems issues = new Problems();
        for (String check : box.def().checks()) {
            switch (check) {
                case "disperser_plane" -> disperserPlane(box, issues);
                case "element_below" -> elementBelow(box, issues);
                case "rotor_column" -> rotorColumn(box, issues);
                case "coil_above_complex" -> coilAboveComplex(box, issues);
                case "condenser_above_complex" -> condenserAboveComplex(box, issues);
                case "vent_above_complex" -> ventAboveComplex(box, issues);
                case "top_layer_edges" -> topLayerEdges(box, issues);
                case "cell_and_provider" -> cellAndProvider(box, issues);
                case "assembly_columns" -> assemblyColumns(box, issues);
                case "fusion_centers" -> fusionCenters(box, issues);
                case "coil_at_port" -> coilAtPort(box, issues);
                case "teleporter_frame" -> teleporterFrame(box, issues);
                default -> issues.add(I18n.tr("multiblock.check.unknown", check));
            }
        }
        return List.copyOf(issues);
    }

    // —— 工具

    private static String role(StructureBox box, int x, int y, int z) {
        String block = box.blockId(x, y, z);
        return block == null || block.isEmpty() ? null : box.def().roleOf(block);
    }

    private static List<int[]> positions(StructureBox box, String roleId) {
        List<int[]> found = new ArrayList<>();
        for (int y = 0; y < box.sizeY(); y++) {
            for (int x = 0; x < box.sizeX(); x++) {
                for (int z = 0; z < box.sizeZ(); z++) {
                    if (roleId.equals(role(box, x, y, z))) {
                        found.add(new int[] {x, y, z});
                    }
                }
            }
        }
        return found;
    }

    // —— 锅炉 / 涡轮：分压元件铺满一层、过热元件在层下

    private static void disperserPlane(StructureBox box, Problems issues) {
        List<int[]> dispersers = positions(box, "disperser");
        if (dispersers.isEmpty()) {
            issues.add(I18n.tr("multiblock.check.disperser_missing"));
            return;
        }
        int level = dispersers.get(0)[1];
        for (int[] pos : dispersers) {
            if (pos[1] != level) {
                issues.at(pos[0], pos[1], pos[2], I18n.tr("multiblock.check.disperser_same_level",
                        pos[0], pos[1], pos[2], level));
                return;
            }
        }
        for (int x = 1; x < box.sizeX() - 1; x++) {
            for (int z = 1; z < box.sizeZ() - 1; z++) {
                String here = role(box, x, level, z);
                if ("complex".equals(here)) {
                    continue;                       // 涡轮的中心那格是复杂旋钮装置
                }
                if (!"disperser".equals(here)) {
                    issues.at(x, level, z, I18n.tr("multiblock.check.disperser_cell_missing",
                            level, x, z));
                    return;
                }
            }
        }
    }

    private static void elementBelow(StructureBox box, Problems issues) {
        List<int[]> dispersers = positions(box, "disperser");
        List<int[]> elements = positions(box, "element");
        if (elements.isEmpty()) {
            issues.add(I18n.tr("multiblock.check.element_missing"));
            return;
        }
        if (dispersers.isEmpty()) {
            return;
        }
        int level = dispersers.get(0)[1];
        for (int[] pos : elements) {
            if (pos[1] >= level) {
                issues.at(pos[0], pos[1], pos[2], I18n.tr("multiblock.check.element_below",
                        pos[0], pos[1], pos[2], level));
                return;
            }
        }
    }

    // —— 涡轮：转子立柱、线圈与冷凝器在复杂旋钮之上、排气口在墙上

    private static int centerX(StructureBox box) {
        return (box.sizeX() - 1) / 2;
    }

    private static int centerZ(StructureBox box) {
        return (box.sizeZ() - 1) / 2;
    }

    private static int complexLevel(StructureBox box, Problems issues) {
        List<int[]> complex = positions(box, "complex");
        if (complex.size() != 1) {
            issues.add(I18n.tr("multiblock.check.complex_count", complex.size()));
            return -1;
        }
        int[] pos = complex.get(0);
        if (pos[0] != centerX(box) || pos[2] != centerZ(box)) {
            issues.at(pos[0], pos[1], pos[2], I18n.tr("multiblock.check.complex_center",
                    centerX(box), centerZ(box), pos[0], pos[1], pos[2]));
            return -1;
        }
        return pos[1];
    }

    private static void rotorColumn(StructureBox box, Problems issues) {
        int level = complexLevel(box, issues);
        List<int[]> rotors = positions(box, "rotor");
        if (rotors.isEmpty()) {
            issues.add(I18n.tr("multiblock.check.rotor_missing"));
            return;
        }
        Set<Integer> levels = new LinkedHashSet<>();
        for (int[] pos : rotors) {
            if (pos[0] != centerX(box) || pos[2] != centerZ(box)) {
                issues.at(pos[0], pos[1], pos[2], I18n.tr("multiblock.check.rotor_center",
                        pos[0], pos[1], pos[2], centerX(box), centerZ(box)));
                return;
            }
            levels.add(pos[1]);
        }
        int top = level < 0 ? box.sizeY() - 2 : level - 1;
        for (int y = 1; y <= top; y++) {
            if (!levels.contains(y)) {
                issues.at(centerX(box), y, centerZ(box), I18n.tr("multiblock.check.rotor_gap", y));
                return;
            }
        }
    }

    private static String centerRole(StructureBox box, int y) {
        return role(box, centerX(box), y, centerZ(box));
    }

    private static void coilAboveComplex(StructureBox box, Problems issues) {
        int level = complexLevel(box, issues);
        List<int[]> coils = positions(box, "coil");
        if (coils.isEmpty()) {
            issues.add(I18n.tr("multiblock.check.coil_missing"));
            return;
        }
        if (level < 0) {
            return;
        }
        int top = level;
        for (int[] pos : coils) {
            if (pos[0] != centerX(box) || pos[2] != centerZ(box) || pos[1] <= level) {
                issues.at(pos[0], pos[1], pos[2], I18n.tr("multiblock.check.coil_above",
                        pos[0], pos[1], pos[2]));
                return;
            }
            top = Math.max(top, pos[1]);
        }
        for (int y = level + 1; y <= top; y++) {
            if (!"coil".equals(centerRole(box, y))) {
                issues.at(centerX(box), y, centerZ(box), I18n.tr("multiblock.check.coil_gap", y));
                return;
            }
        }
    }

    private static void condenserAboveComplex(StructureBox box, Problems issues) {
        int level = complexLevel(box, issues);
        if (level < 0) {
            return;
        }
        for (int[] pos : positions(box, "condenser")) {
            if (pos[1] <= level) {
                issues.at(pos[0], pos[1], pos[2], I18n.tr("multiblock.check.condenser_above",
                        pos[0], pos[1], pos[2]));
                return;
            }
        }
    }

    private static void ventAboveComplex(StructureBox box, Problems issues) {
        int level = complexLevel(box, issues);
        List<int[]> vents = positions(box, "vent");
        if (vents.isEmpty()) {
            issues.add(I18n.tr("multiblock.check.vent_missing"));
            return;
        }
        if (level < 0) {
            return;
        }
        for (int[] pos : vents) {
            if (pos[1] <= level) {
                issues.at(pos[0], pos[1], pos[2], I18n.tr("multiblock.check.vent_above",
                        pos[0], pos[1], pos[2]));
                return;
            }
        }
    }

    // —— 蒸馏塔：顶层四角忽略、棱要外壳、面必须空

    private static void topLayerEdges(StructureBox box, Problems issues) {
        int y = box.sizeY() - 1;
        for (int x = 0; x < box.sizeX(); x++) {
            for (int z = 0; z < box.sizeZ(); z++) {
                boolean borderX = x == 0 || x == box.sizeX() - 1;
                boolean borderZ = z == 0 || z == box.sizeZ() - 1;
                if (borderX && borderZ) {
                    continue;                       // 四角忽略
                }
                String here = role(box, x, y, z);
                if (borderX || borderZ) {
                    if (here == null) {
                        issues.at(x, y, z, I18n.tr("multiblock.check.top_edge", x, y, z));
                        return;
                    }
                } else if (here != null) {
                    issues.at(x, y, z, I18n.tr("multiblock.check.top_interior", x, y, z));
                    return;
                }
            }
        }
    }

    // —— 感应矩阵 / 裂变堆 / 聚变堆 / SPS / 传送器

    private static void cellAndProvider(StructureBox box, Problems issues) {
        if (positions(box, "cell").isEmpty()) {
            issues.add(I18n.tr("multiblock.check.cell_missing"));
        }
        if (positions(box, "provider").isEmpty()) {
            issues.add(I18n.tr("multiblock.check.provider_missing"));
        }
    }

    private static void assemblyColumns(StructureBox box, Problems issues) {
        Set<String> columns = new LinkedHashSet<>();
        for (int[] pos : positions(box, "fuel")) {
            columns.add(pos[0] + "," + pos[2]);
        }
        for (int[] pos : positions(box, "control_rod")) {
            columns.add(pos[0] + "," + pos[2]);
        }
        if (columns.isEmpty()) {
            issues.add(I18n.tr("multiblock.check.assembly_missing"));
            return;
        }
        for (String column : columns) {
            String[] parts = column.split(",");
            int x = Integer.parseInt(parts[0]);
            int z = Integer.parseInt(parts[1]);
            int rods = 0;
            int top = -1;
            for (int y = 0; y < box.sizeY(); y++) {
                String here = role(box, x, y, z);
                if ("fuel".equals(here)) {
                    if (top >= 0 && y != top + 1) {
                        issues.at(x, y - 1, z, I18n.tr("multiblock.check.assembly_gap", x, y, z));
                        return;
                    }
                    top = y;
                } else if ("control_rod".equals(here)) {
                    rods++;
                    if (y != top + 1) {
                        issues.at(x, y, z, I18n.tr("multiblock.check.control_rod_position", x, y, z));
                        return;
                    }
                    top = y;
                }
            }
            if (rods != 1) {
                issues.at(x, Math.min(top + 1, box.sizeY() - 2), z,
                        I18n.tr("multiblock.check.control_rod_count", x, z, rods));
                return;
            }
        }
    }

    private static void fusionCenters(StructureBox box, Problems issues) {
        int[][] centers = {
                {0, box.sizeY() / 2, box.sizeZ() / 2},
                {box.sizeX() - 1, box.sizeY() / 2, box.sizeZ() / 2},
                {box.sizeX() / 2, box.sizeY() / 2, 0},
                {box.sizeX() / 2, box.sizeY() / 2, box.sizeZ() - 1},
                {box.sizeX() / 2, 0, box.sizeZ() / 2},
                {box.sizeX() / 2, box.sizeY() - 1, box.sizeZ() / 2}};
        Set<String> allowed = Set.of("frame", "glass", "port", "controller", "logic_adapter", "laser_focus");
        int controllers = 0;
        for (int[] pos : centers) {
            String here = role(box, pos[0], pos[1], pos[2]);
            if (here == null || !allowed.contains(here)) {
                issues.at(pos[0], pos[1], pos[2], I18n.tr("multiblock.check.fusion_center",
                        pos[0], pos[1], pos[2],
                        here == null ? I18n.tr("multiblock.check.other_block")
                                : I18n.roleName(here, here)));
                return;
            }
            if ("controller".equals(here)) {
                controllers++;
            }
        }
        if (controllers != 1 || !"controller".equals(role(box, box.sizeX() / 2,
                box.sizeY() - 1, box.sizeZ() / 2))) {
            issues.at(box.sizeX() / 2, box.sizeY() - 1, box.sizeZ() / 2,
                    I18n.tr("multiblock.check.fusion_controller"));
        }
    }

    private static void coilAtPort(StructureBox box, Problems issues) {
        List<int[]> ports = positions(box, "port");
        if (ports.isEmpty()) {
            issues.add(I18n.tr("multiblock.check.port_missing"));
            return;
        }
        Set<String> fed = new LinkedHashSet<>();
        for (int[] pos : ports) {
            int dx = pos[0] == 0 ? 1 : (pos[0] == box.sizeX() - 1 ? -1 : 0);
            int dy = pos[1] == 0 ? 1 : (pos[1] == box.sizeY() - 1 ? -1 : 0);
            int dz = pos[2] == 0 ? 1 : (pos[2] == box.sizeZ() - 1 ? -1 : 0);
            int x = pos[0] + dx;
            int y = pos[1] + dy;
            int z = pos[2] + dz;
            if (!"coil".equals(role(box, x, y, z))) {
                issues.at(x, y, z, I18n.tr("multiblock.check.port_coil", pos[0], pos[1], pos[2]));
                return;
            }
            fed.add(x + "," + y + "," + z);
        }
        for (int[] pos : positions(box, "coil")) {
            if (!fed.contains(pos[0] + "," + pos[1] + "," + pos[2])) {
                issues.at(pos[0], pos[1], pos[2],
                        I18n.tr("multiblock.check.coil_port", pos[0], pos[1], pos[2]));
                return;
            }
        }
    }

    private static void teleporterFrame(StructureBox box, Problems issues) {
        if (!TeleporterFrame.accepts(box.sizeX(), box.sizeY(), box.sizeZ()))
            issues.add(I18n.tr("multiblock.check.teleporter_frame"));
    }
}
