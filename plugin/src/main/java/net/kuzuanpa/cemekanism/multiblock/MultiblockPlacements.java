package net.kuzuanpa.cemekanism.multiblock;

import mekcore.machine.Direction;
import mekcore.multiblock.MultiblockInstance;
import mekcore.multiblock.Placement;
import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.EmptyBlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.CEWorld;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/** 一键搭建（文档 05 第四节）：核心给放置清单（带朝向与原点偏移），这里把角色换回方块再摆。 */
public final class MultiblockPlacements {

    private MultiblockPlacements() {
    }

    /** 一次搭建的账：新放几格、原本就对的几格、内部清掉几格。 */
    public record Result(int placed, int kept, int cleared) {
    }

    public static Result build(CeMekanismPlugin plugin, World world, int originX, int originY, int originZ,
                               MultiblockDef def, int sizeX, int sizeY, int sizeZ) {
        MultiblockInstance instance = plugin.core().factories().multiblock(def.id(), sizeX, sizeY, sizeZ);
        Map<String, String> byRole = byRole(def);
        int[] offset = def.buildAnchor(sizeX, sizeY, sizeZ);
        int anchorX = originX + offset[0];
        int anchorY = originY + offset[1];
        int anchorZ = originZ + offset[2];

        // 先算"每格最终该是什么"（放置清单 → 锚点 → 端口），再落世界：
        // 同一座结构重复 /mek build 不会先摆框架再换阀门。
        Map<String, String> desired = new LinkedHashMap<>();
        for (Placement placement : instance.buildPlan(Direction.NORTH, originX, originY, originZ)) {
            desired.put(key(placement.x(), placement.y(), placement.z()),
                    byRole.getOrDefault(placement.role().id(), ""));
        }

        // 锚点固定在表里写的偏移处：宿主按"锚点 - 偏移 = 最小角"反推盒子。
        if (!def.anchor().isEmpty()) {
            desired.put(key(anchorX, anchorY, anchorZ), def.anchor());
        }

        // 每个端口角色按 min 数补到对应段：动态储罐至少一个面阀门，聚变堆的面中心补端口。
        // 不同角色不能抢同一格：涡轮的 steam/energy 用阀门，water 用排气口，
        // 旧写法会把第一个面先摆阀门、再被排气口覆盖，结果一个阀门都不剩。
        Set<String> done = new LinkedHashSet<>();
        Set<String> used = new LinkedHashSet<>();
        for (String port : def.ports()) {
            String role = def.portRole(port);
            String section = def.portSection(port);
            String key = section + '/' + role;
            if (!done.add(key)) {
                continue;                          // 同一角色/段只补一次（fluid 与 chemical 共用阀门）
            }
            if ("controller".equals(role)) {
                continue;                          // 控制器就是锚点，已经在 anchor 那一步摆好了
            }
            if ("coil".equals(role) && "sps".equals(def.family())) {
                continue;                          // SPS 的线圈由端口里侧那几格决定，下面统一补
            }
            int need = 0;
            for (String other : def.ports()) {
                if (section.equals(def.portSection(other)) && role.equals(def.portRole(other))) {
                    need = Math.max(need, def.portMin(other));
                }
            }
            List<int[]> candidates = positionsIn(instance, section, originX, originY, originZ);
            if ("vent".equals(role)) {
                // 排气口必须高于涡轮的复杂旋钮；建造时优先放到最高一排的面格上。
                candidates = new ArrayList<>(candidates);
                Collections.reverse(candidates);
            }
            for (int[] pos : candidates) {
                if (need <= 0) {
                    break;
                }
                if (pos[0] == anchorX && pos[1] == anchorY && pos[2] == anchorZ) {
                    continue;                      // 别把锚点覆盖掉
                }
                if (def.fixed()) {
                    var layout = instance.blueprint().spec().fixedLayout().orElseThrow();
                    var allowed = layout.rolesAt(pos[0] - originX, pos[1] - originY, pos[2] - originZ);
                    if (!allowed.contains(mekcore.multiblock.BlockRoles.ANY)
                            && !allowed.contains(MultiblockBlocks.role(role))) {
                        continue;
                    }
                }
                if (!used.add(key(pos[0], pos[1], pos[2]))) {
                    continue;                      // 同一格不能同时当阀门和排气口
                }
                String blockId = byRole.get(role);
                if (blockId == null || blockId.isEmpty()) {
                    break;
                }
                desired.put(key(pos[0], pos[1], pos[2]), blockId);
                need--;
            }
        }

        if ("sps".equals(def.family())) {
            // 上游 SPSValidator.postcheck：每个端口里侧各一根增压线圈，且所有线圈都必须贴着端口。
            // 不是整个 5×5×5 内腔都填满线圈。
            String coil = byRole.get("coil");
            if (coil != null && !coil.isEmpty()) {
                Map<String, String> coils = new LinkedHashMap<>();
                for (Map.Entry<String, String> cell : desired.entrySet()) {
                    String role = cell.getValue().isEmpty() ? null : def.roleOf(cell.getValue());
                    if (!"port".equals(role)) {
                        continue;
                    }
                    String[] parts = cell.getKey().split(",");
                    int x = Integer.parseInt(parts[0]) - originX;
                    int y = Integer.parseInt(parts[1]) - originY;
                    int z = Integer.parseInt(parts[2]) - originZ;
                    int dx = x == 0 ? 1 : x == sizeX - 1 ? -1 : 0;
                    int dy = y == 0 ? 1 : y == sizeY - 1 ? -1 : 0;
                    int dz = z == 0 ? 1 : z == sizeZ - 1 ? -1 : 0;
                    int ix = x + dx;
                    int iy = y + dy;
                    int iz = z + dz;
                    if (ix > 0 && ix < sizeX - 1 && iy > 0 && iy < sizeY - 1
                            && iz > 0 && iz < sizeZ - 1) {
                        coils.put(key(originX + ix, originY + iy, originZ + iz), coil);
                    }
                }
                desired.putAll(coils);
            }
        }

        if (def.checks().contains("top_layer_edges")) {
            // 蒸馏塔顶层是"棱上外壳、中间开口"：通用分段把顶层中间算成 FACE，
            // 会顺手铺满外壳，随后被 top_layer_edges check 判死。这里按 check 清空中间。
            for (int x = 1; x < sizeX - 1; x++) {
                for (int z = 1; z < sizeZ - 1; z++) {
                    desired.put(key(originX + x, originY + sizeY - 1, originZ + z), "");
                }
            }
        }

        int placed = 0;
        int kept = 0;
        int cleared = 0;
        for (Map.Entry<String, String> cell : desired.entrySet()) {
            String[] parts = cell.getKey().split(",");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            int z = Integer.parseInt(parts[2]);
            if (cell.getValue().isEmpty()) {
                if (clear(world, x, y, z)) {
                    cleared++;
                }
            } else if (put(world, x, y, z, cell.getValue())) {
                placed++;
            } else {
                kept++;
            }
        }
        if (cleared > 0) {
            plugin.getLogger().info("一键搭建清空内部 " + cleared + " 格");
        }
        return new Result(placed, kept, cleared);
    }

    private static String key(int x, int y, int z) {
        return x + "," + y + "," + z;
    }

    /** 段里的所有位置（绝对坐标），顺序固定：层 → x → z。 */
    private static List<int[]> positionsIn(MultiblockInstance instance, String section,
                                           int originX, int originY, int originZ) {
        List<int[]> out = new ArrayList<>();
        for (int y = 0; y < instance.sizeY(); y++) {
            for (int x = 0; x < instance.sizeX(); x++) {
                for (int z = 0; z < instance.sizeZ(); z++) {
                    String here = switch (mekcore.multiblock.StructureValidator.sectionOf(
                            x, y, z, instance.sizeX(), instance.sizeY(), instance.sizeZ())) {
                        case EDGE -> "edge";
                        case FACE -> "face";
                        case INTERIOR -> "interior";
                    };
                    if (here.equals(section)) {
                        out.add(new int[] {originX + x, originY + y, originZ + z});
                    }
                }
            }
        }
        return out;
    }

    /** 放一格并把 CE 的区块缓存同步成新状态；已经是对的就只算"保留"。 */
    private static boolean put(World world, int x, int y, int z, String blockId) {
        return place(world, x, y, z, blockId);
    }

    /** 供 demo/命令复用：放一格 CE 方块并同步区块缓存，返回是否真的换了方块。 */
    public static boolean place(World world, int x, int y, int z, String blockId) {
        if (blockId.equals(blockIdAt(world, x, y, z))) {
            return false;
        }
        BlockDefinition block = CraftEngineBlocks.byId(Key.of(blockId));
        if (block == null) {
            return false;
        }
        boolean changed = CraftEngineBlocks.place(new Location(world, x, y, z), block.defaultState(), false);
        sync(world, x, y, z);
        return changed;
    }

    /** 清成空气（只对非空气格动手），同样同步 CE 缓存。 */
    private static boolean clear(World world, int x, int y, int z) {
        org.bukkit.block.Block block = world.getBlockAt(x, y, z);
        if (block.getType().isAir()) {
            sync(world, x, y, z);
            return false;
        }
        block.setType(Material.AIR, false);
        sync(world, x, y, z);
        return true;
    }

    /** CraftEngineBlocks.place 只写世界，不刷新 CE 的区块缓存；不刷新的话方块实体会按旧状态建。 */
    public static void sync(World world, int x, int y, int z) {
        CEWorld ceWorld = net.momirealms.craftengine.bukkit.api.BukkitAdaptor.adapt(world).storageWorld();
        ImmutableBlockState state = CraftEngineBlocks.getCustomBlockState(world.getBlockAt(x, y, z));
        ceWorld.setBlockStateAtIfLoaded(new BlockPos(x, y, z),
                state == null ? EmptyBlockDefinition.STATE : state);
    }

    /** 这个世界坐标上的 CE 方块 id；空气返回空串，原版方块返回 null。 */
    private static String blockIdAt(World world, int x, int y, int z) {
        org.bukkit.block.Block block = world.getBlockAt(x, y, z);
        if (block.getType().isAir()) {
            return "";
        }
        ImmutableBlockState state = CraftEngineBlocks.getCustomBlockState(block);
        return state == null ? null : state.owner().value().id().asString();
    }

    /** 角色 id → 该族用哪个方块（同一角色有多个方块时取表里的第一个）。 */
    private static Map<String, String> byRole(MultiblockDef def) {
        Map<String, String> map = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : new TreeMap<>(def.roleBlocks()).entrySet()) {
            map.putIfAbsent(entry.getKey(), entry.getValue());
        }
        map.putIfAbsent("controller", def.anchor());
        map.put("empty", "");
        // 固定模板的 ANY：聚变堆 6 个面中心放端口方块；SPS 的 ANY 是外壳，
        // 只有端口补格才把其中几格换成 sps_port。
        String anyBlock = "";
        for (String port : def.ports()) {
            String candidate = map.get(def.portRole(port));
            if (candidate != null && !candidate.isEmpty()) {
                anyBlock = candidate;
                break;
            }
        }
        if ("sps".equals(def.family())) {
            String casing = map.get("frame");
            if (casing != null && !casing.isEmpty()) {
                anyBlock = casing;
            }
        }
        map.put("any", anyBlock);
        // 内部件（分压元件/转子/元件/燃料……）由玩家自己摆：一键搭建只负责外壳、锚点与端口。
        // 端口角色是例外：SPS 的能量口长在线圈上，聚变堆的激光口长在 laser_focus 上。
        Set<String> portRoles = new LinkedHashSet<>();
        for (String port : def.ports()) {
            portRoles.add(def.portRole(port));
        }
        for (String role : List.of("disperser", "element", "rotor", "complex", "coil",
                "condenser", "cell", "provider", "fuel", "control_rod", "laser_focus", "hohlraum")) {
            if (portRoles.contains(role)) {
                continue;                          // 端口补格要能放这个角色
            }
            map.put(role, "");
        }
        return map;
    }

    /** 结构里用到的方块清单（自检用）。 */
    public static List<String> blocks(MultiblockDef def) {
        return List.copyOf(def.blockRoles().keySet());
    }
}
