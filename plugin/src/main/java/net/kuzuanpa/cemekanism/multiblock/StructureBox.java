package net.kuzuanpa.cemekanism.multiblock;

import mekcore.multiblock.BlockRole;
import mekcore.multiblock.BlockRoles;
import mekcore.multiblock.BlockSection;
import mekcore.multiblock.StructureQuery;
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * 世界侧的一盒方块视图：坐标 ↔ 方块 id ↔ 角色（文档 05 第四节）。
 * 分类器就是这里：不认识的角色一律当"别的方块"，空气当 {@link BlockRoles#AIR}。
 */
public final class StructureBox implements StructureQuery {
    private final World world;
    private final MultiblockDef def;
    private final int originX;
    private final int originY;
    private final int originZ;
    private final int sizeX;
    private final int sizeY;
    private final int sizeZ;
    private final Map<String, Integer> blockCounts = new LinkedHashMap<>();
    private Map<String, String> roleAssignments;
    private boolean countsValid;

    public StructureBox(World world, MultiblockDef def, int originX, int originY, int originZ,
                        int sizeX, int sizeY, int sizeZ) {
        this.world = world;
        this.def = def;
        this.originX = originX;
        this.originY = originY;
        this.originZ = originZ;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
    }

    public int originX() {
        return this.originX;
    }

    public int originY() {
        return this.originY;
    }

    public int originZ() {
        return this.originZ;
    }

    public int sizeX() {
        return this.sizeX;
    }

    public int sizeY() {
        return this.sizeY;
    }

    public int sizeZ() {
        return this.sizeZ;
    }

    public World world() {
        return this.world;
    }

    public MultiblockDef def() {
        return this.def;
    }

    /** 方块 id（本族的视角：不是本包方块返回 null，空气返回空串）。 */
    public String blockId(int x, int y, int z) {
        if (x < 0 || y < 0 || z < 0 || x >= this.sizeX || y >= this.sizeY || z >= this.sizeZ) {
            return null;
        }
        org.bukkit.block.Block block = this.world.getBlockAt(this.originX + x, this.originY + y,
                this.originZ + z);
        if (block.getType().isAir()) {
            return "";
        }
        ImmutableBlockState state = CraftEngineBlocks.getCustomBlockState(block);
        return state == null ? null : state.owner().value().id().asString();
    }

    @Override
    public BlockRole roleAt(int x, int y, int z) {
        String block = blockId(x, y, z);
        if (block == null) return OTHER;
        if (block.isEmpty()) {
            return BlockRoles.AIR;
        }
        String role = assignment(block, x, y, z);
        if (role == null) {
            return OTHER;
        }
        return MultiblockBlocks.role(role);
    }

    /**
     * 上游一个方块可以承担多个端口角色（锅炉阀门：水/蒸汽/热冷却液/冷冷却液；裂变堆端口：燃料/废料/两种冷却液/电）。
     * 我们没有阀门模式配置，所以同一方块在同一个段里的多个实例按位置确定性分配角色：
     * 先按结构端口声明顺序，每格轮换一个角色；多出来的格子回到主角色。
     * 这样 /mek build 与读档后的校验对同一份世界得到同一结果，端口也不会全部挤在同一个阀门上。
     */
    private String assignment(String block, int x, int y, int z) {
        List<String> roles = this.def.rolesOf(block);
        if (roles.size() <= 1) {
            return this.def.roleOf(block);
        }
        if (this.roleAssignments == null) {
            this.roleAssignments = computeAssignments();
        }
        return this.roleAssignments.getOrDefault(key(x, y, z), this.def.roleOf(block));
    }

    private Map<String, String> computeAssignments() {
        Map<String, Map<BlockSection, List<int[]>>> bySection = new LinkedHashMap<>();
        for (int y = 0; y < this.sizeY; y++) {
            for (int x = 0; x < this.sizeX; x++) {
                for (int z = 0; z < this.sizeZ; z++) {
                    String block = blockId(x, y, z);
                    if (block == null || block.isEmpty() || this.def.rolesOf(block).size() <= 1) {
                        continue;
                    }
                    BlockSection section = mekcore.multiblock.StructureValidator.sectionOf(
                            x, y, z, this.sizeX, this.sizeY, this.sizeZ);
                    bySection.computeIfAbsent(block, ignored -> new LinkedHashMap<>())
                            .computeIfAbsent(section, ignored -> new ArrayList<>()).add(new int[] {x, y, z});
                }
            }
        }
        Map<String, String> out = new LinkedHashMap<>();
        for (Map.Entry<String, Map<BlockSection, List<int[]>>> entry : bySection.entrySet()) {
            String block = entry.getKey();
            List<String> ordered = orderedRoles(block);
            for (Map.Entry<BlockSection, List<int[]>> section : entry.getValue().entrySet()) {
                List<String> applicable = new ArrayList<>();
                for (String role : ordered) {
                    if (appliesTo(role, section.getKey())) {
                        applicable.add(role);
                    }
                }
                if (applicable.isEmpty()) {
                    String primary = this.def.roleOf(block);
                    applicable.add(primary == null ? ordered.get(0) : primary);
                }
                List<int[]> positions = new ArrayList<>(section.getValue());
                positions.sort(Comparator.comparingInt((int[] p) -> p[1])
                        .thenComparingInt(p -> p[0]).thenComparingInt(p -> p[2]));
                for (int i = 0; i < positions.size(); i++) {
                    int[] pos = positions.get(i);
                    out.put(key(pos[0], pos[1], pos[2]), applicable.get(i % applicable.size()));
                }
            }
        }
        return out;
    }

    /** 端口声明顺序优先（与一键搭建的补格顺序一致），其余角色（通常是结构主角色）随后。 */
    private List<String> orderedRoles(String block) {
        LinkedHashSet<String> ordered = new LinkedHashSet<>();
        for (String port : this.def.ports()) {
            String role = this.def.portRole(port);
            if (this.def.rolesOf(block).contains(role)) {
                ordered.add(role);
            }
        }
        ordered.addAll(this.def.rolesOf(block));
        return List.copyOf(ordered);
    }

    /** 这个角色在哪些段里有端口声明；不是端口角色时（结构角色）对所有段都算。 */
    private boolean appliesTo(String role, BlockSection section) {
        boolean isPortRole = false;
        boolean matches = false;
        for (String port : this.def.ports()) {
            if (!this.def.portRole(port).equals(role)) {
                continue;
            }
            isPortRole = true;
            String name = this.def.portSection(port);
            BlockSection declared = switch (name) {
                case "edge" -> BlockSection.EDGE;
                case "interior" -> BlockSection.INTERIOR;
                default -> BlockSection.FACE;
            };
            if (declared == section) {
                matches = true;
            }
        }
        return !isPortRole || matches;
    }

    private static String key(int x, int y, int z) {
        return x + "," + y + "," + z;
    }

    /** 世界里的方块变了（成型/拆阀）后必须重算多角色分配。 */
    public void invalidateRoles() {
        this.roleAssignments = null;
        this.countsValid = false;
        this.blockCounts.clear();
    }

    /** 上一次校验期间数到的方块（感应元件/供应器的容量、涡轮的线圈数都要它）。 */
    public Map<String, Integer> blockCounts() {
        if (!this.countsValid) {
            this.blockCounts.clear();
            for (int y = 0; y < this.sizeY; y++) for (int x = 0; x < this.sizeX; x++) for (int z = 0; z < this.sizeZ; z++) {
                String block = blockId(x, y, z);
                if (block != null && this.def.roleOf(block) != null) this.blockCounts.merge(block, 1, Integer::sum);
            }
            this.countsValid = true;
        }
        return Collections.unmodifiableMap(this.blockCounts);
    }

    public int count(String blockId) {
        return blockCounts().getOrDefault(blockId, 0);
    }

    /** 这个坐标上的方块属于本族（外壳类），用来量结构尺寸。 */
    public boolean belongs(int x, int y, int z) {
        String block = blockId(x, y, z);
        return block != null && !block.isEmpty() && this.def.roleOf(block) != null;
    }

    public static final BlockRole OTHER = BlockRole.of("mek_other", "其它方块");
}
