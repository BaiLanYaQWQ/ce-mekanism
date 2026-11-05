package net.kuzuanpa.cemekanism.multiblock;

import mekcore.chemical.Chemical;
import mekcore.chemical.Chemicals;
import mekcore.chemical.Radiation;
import mekcore.multiblock.MultiblockInstance;
import mekcore.multiblock.ValidationResult;
import mekcore.persist.DataTag;
import mekcore.resource.ResourceTank;
import mekcore.resource.ResourceTypes;
import mekcore.sim.Tickable;
import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import org.bukkit.World;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * 世界侧的结构注册表（文档 05 第四节）：一个盒子一份 {@link Host}，多个锚点共用（引用计数）。
 * 结构状态不进存档：锚点方块实体只记"尺寸 + 原点"，读档后重新 validate。
 */
public final class MultiblockHosts {

    private final CeMekanismPlugin plugin;
    private final Map<String, Host> hosts = new LinkedHashMap<>();
    private record ChunkKey(UUID world, int x, int z) { }
    private final Map<ChunkKey, Set<Host>> hostsByChunk = new LinkedHashMap<>();
    private final Set<Host> pendingChecks = new LinkedHashSet<>();
    private boolean checkQueued;

    public MultiblockHosts(CeMekanismPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * 认领一个盒子：锚点在盒子的最小角（{@link MultiblockDef#anchorOffsetX()} 之外的情况由表里给偏移）。
     * 已经有同盒子登记就复用（引用计数 +1），否则量一遍尺寸再校验。
     */
    public Host acquire(World world, int anchorX, int anchorY, int anchorZ, MultiblockDef def,
                        int[] recordedSize, Runnable marker) {
        if (def.family().equals("teleporter")) {
            // A teleporter belongs to its controller, even when expansion moves the minimum corner.
            String key = key(world, def, anchorX, anchorY, anchorZ);
            Host host = this.hosts.get(key);
            if (host == null) {
                int[] size = recordedSize != null && recordedSize.length == 3
                        && def.accepts(recordedSize[0], recordedSize[1], recordedSize[2])
                        ? recordedSize : new int[] {3, 4, 1};
                int[] offset = def.buildAnchor(size[0], size[1], size[2]);
                host = new Host(this.plugin, world, def, anchorX - offset[0], anchorY - offset[1],
                        anchorZ - offset[2], size[0], size[1], size[2], marker);
                host.controller = new int[] {anchorX, anchorY, anchorZ};
                this.hosts.put(key, host);
                index(host);
                MultiblockContexts.attach(host.instance(), host);
            }
            host.refs++;
            host.marker = marker;
            host.refresh();
            return host;
        }
        // 从被点的阀门/端口向外认连通的外壳：这样阀门在任意一面都能作为成型入口，
        // 不再要求"玩家必须点最小角"（上游 VoxelPlane 的最小可用替代，见台账 G35）。
        // 每次显式成型都重新认一遍：锚点刚放下时只看到一个方块，会先按最小尺寸造出宿主；
        // 其余方块摆好后如果不按发现到的尺寸改这个宿主，它会永远停在 3×3（2026-10-19 实测）。
        Discovered discovered = discover(world, def, anchorX, anchorY, anchorZ);
        int originX;
        int originY;
        int originZ;
        int[] size;
        if (discovered != null) {
            originX = discovered.originX();
            originY = discovered.originY();
            originZ = discovered.originZ();
            size = new int[] {discovered.sizeX(), discovered.sizeY(), discovered.sizeZ()};
        } else {
            // 连通块还不成形（缺一大块）时退回旧口径：给报错一个合理的盒子。
            originX = anchorX - def.anchorOffsetX();
            originY = anchorY - def.anchorOffsetY();
            originZ = anchorZ - def.anchorOffsetZ();
            size = recordedSize != null && recordedSize.length == 3
                    ? recordedSize : measure(world, def, originX, originY, originZ);
        }
        String key = key(world, def, originX, originY, originZ);
        Host host = this.hosts.get(key);
        if (host != null) {
            host.refs++;
            if (anchorX == originX + def.anchorOffsetX() && anchorY == originY + def.anchorOffsetY()
                    && anchorZ == originZ + def.anchorOffsetZ()) host.marker = marker;
            if ((discovered != null || !host.formed()) && (host.box().sizeX() != size[0]
                    || host.box().sizeY() != size[1] || host.box().sizeZ() != size[2])) {
                // 先放阀门再补外壳时，旧宿主是"只有一个锚点"量出来的最小盒子；
                // 发现到真实盒子后必须在原地改尺寸，否则 3×3 以上的结构永远不成型。
                host.resize(size[0], size[1], size[2]);
            }
        } else {
            // 已成型、包含这块的宿主只在没有发现到新盒子时兜底；能发现到真实盒子时优先按新尺寸建，
            // 这样玩家在已成型结构外继续扩一层不会被旧盒子挡住。
            if (discovered == null) {
                Host containing = formedContaining(world, def, anchorX, anchorY, anchorZ);
                if (containing != null) {
                    containing.refs++;
                    if (anchorX == containing.originX + def.anchorOffsetX()
                            && anchorY == containing.originY + def.anchorOffsetY()
                            && anchorZ == containing.originZ + def.anchorOffsetZ()) containing.marker = marker;
                    containing.refresh();
                    return containing;
                }
            }
            host = new Host(this.plugin, world, def, originX, originY, originZ, size[0], size[1], size[2],
                    marker);
            // 先入表再 refresh：refresh 里的 attachMultiblock 会扫外壳一圈重绑管道端点，
            // 那时 at(...) 必须能查到自己（否则第一个 refresh 时端口永远绑不上）。
            host.refs = 1;
            this.hosts.put(key, host);
            index(host);
        }
        // 已登记的未成型盒子同样需要重测与候选尺寸校验，不能只校验旧尺寸就提前返回。
        host.refresh();
        if (!host.formed() && discovered == null && recordedSize != null) {
            // 存档里的尺寸可能来自一次失败的重建：先按世界重新量，别让坏账一直传下去
            int[] fresh = measure(world, def, originX, originY, originZ);
            host.resize(fresh[0], fresh[1], fresh[2]);
            host.refresh();
            size = fresh;
        }
        if (!host.formed()) {
            // 量出来的尺寸不一定对（顶层的角可以不摆这类）：试邻近的几组，挑能成型的
            int[] measured = size.clone();
            for (int[] candidate : candidates(size)) {
                if (candidate[0] == size[0] && candidate[1] == size[1] && candidate[2] == size[2]) {
                    continue;
                }
                host.resize(candidate[0], candidate[1], candidate[2]);
                host.refresh();
                if (host.formed()) {
                    break;
                }
            }
            if (!host.formed()) {
                // 一组都没成：把盒子还原成量出来的尺寸，别让报错落在被候选污染的形状上
                host.resize(measured[0], measured[1], measured[2]);
                host.refresh();
            }
        }
        MultiblockContexts.attach(host.instance(), host);
        return host;
    }

    public void release(Host host) {
        if (host == null) {
            return;
        }
        host.refs--;
        if (host.refs > 0) {
            return;
        }
        this.hosts.remove(host.key());
        unindex(host);
        this.pendingChecks.remove(host);
        host.drop();
        MultiblockContexts.detach(host.instance());
    }

    /** 某个坐标落在哪座结构里（命令与 06 的管道用；重叠时取第一个）。 */
    public Host at(World world, int x, int y, int z) {
        Host fallback = null;
        for (Host host : this.hosts.values()) {
            if (host.world() == world && host.contains(x, y, z)) {
                if (host.formed()) {
                    return host;                       // 成型的那座优先，别被同格里的影子盒子挡住
                }
                if (fallback == null) {
                    fallback = host;
                }
            }
        }
        return fallback;
    }

    private Host formedContaining(World world, MultiblockDef def, int x, int y, int z) {
        for (Host host : this.hosts.values()) {
            if (host.world() == world && host.def().id().equals(def.id()) && host.formed()
                    && host.contains(x, y, z)) {
                return host;
            }
        }
        return null;
    }

    public Host byKey(String key) {
        return this.hosts.get(key);
    }

    public List<Host> all() {
        return new ArrayList<>(this.hosts.values());
    }

    private void index(Host host) {
        if (this.hosts.get(host.key()) != host) return;
        for (int x = host.originX >> 4; x <= (host.originX + host.box.sizeX() - 1) >> 4; x++)
            for (int z = host.originZ >> 4; z <= (host.originZ + host.box.sizeZ() - 1) >> 4; z++)
                this.hostsByChunk.computeIfAbsent(new ChunkKey(host.world.getUID(), x, z),
                        ignored -> new LinkedHashSet<>()).add(host);
    }

    private void unindex(Host host) {
        for (int x = host.originX >> 4; x <= (host.originX + host.box.sizeX() - 1) >> 4; x++)
            for (int z = host.originZ >> 4; z <= (host.originZ + host.box.sizeZ() - 1) >> 4; z++) {
                ChunkKey key = new ChunkKey(host.world.getUID(), x, z);
                Set<Host> members = this.hostsByChunk.get(key);
                if (members == null) continue;
                members.remove(host);
                if (members.isEmpty()) this.hostsByChunk.remove(key);
            }
    }

    /** Route edits to their shared host; failed structures need checks even without a tick task. */
    public void markDirtyAt(World world, int x, int y, int z) {
        for (Host host : this.hostsByChunk.getOrDefault(new ChunkKey(world.getUID(), x >> 4, z >> 4), Set.of())) {
            if (host.contains(x, y, z)) {
                host.dirty = true;
                this.pendingChecks.add(host);
            }
        }
        // The new edge can be outside the old box, including across a chunk boundary.
        for (Host host : this.hosts.values()) {
            if (host.world == world && host.controller != null
                    && Math.abs(x - host.controller[0]) <= host.def.wMax()
                    && Math.abs(y - host.controller[1]) <= host.def.hMax()
                    && Math.abs(z - host.controller[2]) <= host.def.wMax()) {
                host.dirty = true;
                this.pendingChecks.add(host);
            }
        }
        if (this.pendingChecks.isEmpty() || this.checkQueued) return;
        this.checkQueued = true;
        // Break/place events can still expose the old block. Read the final world next main tick.
        org.bukkit.Bukkit.getScheduler().runTask(this.plugin, this::checkChangedStructures);
    }

    private void checkChangedStructures() {
        this.checkQueued = false;
        List<Host> changed = List.copyOf(this.pendingChecks);
        this.pendingChecks.clear();
        for (Host host : changed) {
            if (this.hosts.get(host.key()) == host && host.dirty) {
                host.refresh();
                host.markChanged();
            }
        }
    }

    /** Effects keep updating while fluid contents are stationary and the simulation is asleep. */
    public void tickEffects() {
        for (Host host : List.copyOf(this.hosts.values())) host.effects.tick();
    }

    public void closeEffects() {
        for (Host host : this.hosts.values()) host.effects.remove();
    }

    public void hideEffectsInChunk(World world, int x, int z) {
        for (Host host : this.hostsByChunk.getOrDefault(new ChunkKey(world.getUID(), x, z), Set.of()))
            host.effects.remove();
    }

    public String describe() {
        StringBuilder sb = new StringBuilder("多方块 " + this.hosts.size() + " 座");
        for (Host host : this.hosts.values()) {
            sb.append("\n  ").append(host.describe());
        }
        return sb.toString();
    }

    private static String key(World world, MultiblockDef def, int x, int y, int z) {
        return world.getName() + '@' + def.id() + ':' + x + ',' + y + ',' + z;
    }

    private record Discovered(int originX, int originY, int originZ, int sizeX, int sizeY, int sizeZ) {
    }

    /**
     * 从被点的结构方块出发，沿六向收集本族方块，取连通块的包围盒。
     * 显式成型与未成型重试都从当前世界重搜，旧的诊断盒子不能限制后续搭建的原点或尺寸。
     * 空腔不用进队列：外壳本身是连通的，内部元件即使连到外壳也只影响包围盒，不影响成型。
     */
    private static Discovered discover(World world, MultiblockDef def, int startX, int startY, int startZ) {
        if (!belongs(world, def, startX, startY, startZ)) {
            return null;
        }
        int minX = startX - def.wMax() + 1;
        int maxX = startX + def.wMax() - 1;
        int minY = startY - def.hMax() + 1;
        int maxY = startY + def.hMax() - 1;
        int minZ = startZ - def.wMax() + 1;
        int maxZ = startZ + def.wMax() - 1;
        Set<Long> seen = new HashSet<>();
        ArrayDeque<int[]> queue = new ArrayDeque<>();
        int[] start = {startX, startY, startZ};
        queue.add(start);
        seen.add(key(start));
        int loX = startX;
        int hiX = startX;
        int loY = startY;
        int hiY = startY;
        int loZ = startZ;
        int hiZ = startZ;
        int[][] directions = {{1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}};
        while (!queue.isEmpty()) {
            int[] here = queue.removeFirst();
            for (int[] direction : directions) {
                int x = here[0] + direction[0];
                int y = here[1] + direction[1];
                int z = here[2] + direction[2];
                if (x < minX || x > maxX || y < minY || y > maxY || z < minZ || z > maxZ) {
                    continue;
                }
                int[] next = {x, y, z};
                if (!seen.add(key(next)) || !belongs(world, def, x, y, z)) {
                    continue;
                }
                queue.addLast(next);
                loX = Math.min(loX, x);
                hiX = Math.max(hiX, x);
                loY = Math.min(loY, y);
                hiY = Math.max(hiY, y);
                loZ = Math.min(loZ, z);
                hiZ = Math.max(hiZ, z);
            }
        }
        int sizeX = hiX - loX + 1;
        int sizeY = hiY - loY + 1;
        int sizeZ = hiZ - loZ + 1;
        if (!def.accepts(sizeX, sizeY, sizeZ)) {
            return null;
        }
        return new Discovered(loX, loY, loZ, sizeX, sizeY, sizeZ);
    }

    private static long key(int[] pos) {
        return key(pos[0], pos[1], pos[2]);
    }

    private static long key(int x, int y, int z) {
        return ((long) (x & 0x3FFFFFF) << 38) | ((long) (y & 0xFFF) << 26) | (z & 0x3FFFFFF);
    }

    private static boolean belongs(World world, MultiblockDef def, int x, int y, int z) {
        org.bukkit.block.Block block = world.getBlockAt(x, y, z);
        if (block.getType().isAir()) {
            return false;
        }
        ImmutableBlockState state = CraftEngineBlocks.getCustomBlockState(block);
        return state != null && def.roleOf(state.owner().value().id().asString()) != null;
    }

    /** 从最小角沿三条棱量尺寸：棱上一定是外壳（量不准由候选组兜）。 */
    private static int[] measure(World world, MultiblockDef def, int originX, int originY, int originZ) {
        StructureBox probe = new StructureBox(world, def, originX, originY, originZ,
                def.wMax(), def.hMax(), def.wMax());
        int width = walk(probe, 1, 0, 0, def.wMin(), def.wMax());
        int depth = walk(probe, 0, 0, 1, def.wMin(), def.wMax());
        int height = walk(probe, 0, 1, 0, def.hMin(), def.hMax());
        return new int[] {width, height, depth};
    }

    private static int walk(StructureBox probe, int dx, int dy, int dz, int min, int max) {
        int count = 1;
        while (count < max && probe.belongs(dx * count, dy * count, dz * count)) {
            count++;
        }
        return Math.max(count, min);
    }

    private static List<int[]> candidates(int[] size) {
        return List.of(size,
                new int[] {size[0], size[1] + 1, size[2]},
                new int[] {size[0] + 1, size[1], size[2]},
                new int[] {size[0], size[1], size[2] + 1});
    }

    /** 一座已登记的结构：端口、热量节点、逐刻逻辑都在 {@link MultiblockInstance} 里。 */
    public static final class Host implements Tickable {
        private final CeMekanismPlugin plugin;
        private final World world;
        private final MultiblockDef def;
        private int originX;
        private int originY;
        private int originZ;
        private int[] controller;
        private final MultiblockInstance instance;
        private StructureBox box;
        private ValidationResult validation;
        private List<MultiblockChecks.Problem> extraProblems = List.of();
        private boolean dirty = true;
        private boolean checksOk;
        private long blades;
        private final Map<String, String> portModes = new LinkedHashMap<>();
        private int refs;
        private Runnable marker;
        private boolean registered;
        private int[] attachedSize;
        private final MultiblockEffectRenderer effects;
        private Boolean shownActive;
        private Boolean shownHot;

        private Host(CeMekanismPlugin plugin, World world, MultiblockDef def, int originX, int originY,
                     int originZ, int sizeX, int sizeY, int sizeZ, Runnable marker) {
            this.plugin = plugin;
            this.world = world;
            this.def = def;
            this.originX = originX;
            this.originY = originY;
            this.originZ = originZ;
            this.marker = marker;
            this.instance = plugin.core().factories().multiblock(def.id(), sizeX, sizeY, sizeZ);
            this.box = new StructureBox(world, def, originX, originY, originZ, sizeX, sizeY, sizeZ);
            this.validation = this.instance.lastValidation();
            this.effects = new MultiblockEffectRenderer(this);
        }

        public String key() {
            return this.controller == null
                    ? MultiblockHosts.key(this.world, this.def, this.originX, this.originY, this.originZ)
                    : MultiblockHosts.key(this.world, this.def, this.controller[0], this.controller[1], this.controller[2]);
        }

        public World world() {
            return this.world;
        }

        public org.bukkit.Location controllerLocation() {
            return this.controller == null
                    ? new org.bukkit.Location(this.world, this.originX + this.def.anchorOffsetX() + .5,
                            this.originY + this.def.anchorOffsetY() + .5, this.originZ + this.def.anchorOffsetZ() + .5)
                    : new org.bukkit.Location(this.world, this.controller[0] + .5, this.controller[1] + .5, this.controller[2] + .5);
        }

        public CeMekanismPlugin plugin() {
            return this.plugin;
        }

        public MultiblockDef def() {
            return this.def;
        }

        public StructureBox box() {
            return this.box;
        }

        public MultiblockInstance instance() {
            return this.instance;
        }

        public long blades() {
            return this.blades;
        }

        public void setBlades(long value) {
            this.blades = Math.max(value, 0);
        }

        /** 成型 = 核心校验通过 + 本族的 check 也过（check 报的就是"缺什么"）。 */
        public boolean formed() {
            return !this.dirty && this.checksOk && this.validation != null && this.validation.ok();
        }

        public ValidationResult validation() {
            return this.validation;
        }

        public List<String> extraIssues() {
            return this.extraProblems.stream().map(MultiblockChecks.Problem::message).toList();
        }

        public List<MultiblockChecks.Problem> extraProblems() {
            return this.extraProblems;
        }

        public boolean contains(int x, int y, int z) {
            return x >= this.box.originX() && x < this.box.originX() + this.box.sizeX()
                    && y >= this.box.originY() && y < this.box.originY() + this.box.sizeY()
                    && z >= this.box.originZ() && z < this.box.originZ() + this.box.sizeZ();
        }

        /**
         * 这一格是不是某个端口可用的方块：按"端口角色"判，而不是按核心预先挑好的那一个坐标。
         * 上游一个阀门可以在任意墙面；我们至少让任意一个阀门/端口方块都能绑端点（台账 G35）。
         */
        public boolean isPortAt(String port, int x, int y, int z) {
            if (!contains(x, y, z)) {
                return false;
            }
            String block = this.box.blockId(x - this.originX, y - this.originY, z - this.originZ);
            if (block == null || block.isEmpty()) return false;
            String role = this.box.roleAt(x - this.originX, y - this.originY, z - this.originZ).id();
            return MultiblockPortModes.allows(this.def, port, role, this.def.rolesOf(block), portMode(x, y, z));
        }

        /** 这个端口方块当前可循环的模式；同一角色上的多个资源共用一组模式。 */
        public List<String> availablePortModes(int x, int y, int z) {
            if (!contains(x, y, z)) {
                return List.of();
            }
            String block = this.box.blockId(x - this.originX, y - this.originY, z - this.originZ);
            if (block == null || block.isEmpty()) return List.of();
            return MultiblockPortModes.choices(this.def, this.def.rolesOf(block), this.instance.heatNode() != null);
        }

        public String portFlowAt(String port, int x, int y, int z) {
            return MultiblockPortModes.flow(this.def.flow(port), portMode(x, y, z));
        }

        /** Only an explicitly selected valve/port conducts heat; casing never does. */
        public boolean isHeatPortAt(int x, int y, int z) {
            return formed() && this.instance.heatNode() != null && "HEAT".equals(portMode(x, y, z));
        }

        public String portVisualAt(int x, int y, int z) {
            if (isHeatPortAt(x, y, z)) return "heat";
            String mode = portMode(x, y, z);
            if ("INPUT".equals(mode)) return "input";
            if ("OUTPUT".equals(mode)) return "output";
            if ("OUTPUT_WASTE".equals(mode)) return "waste";
            if ("OUTPUT_COOLANT".equals(mode)) return "coolant";
            boolean input = false, output = false;
            for (String port : this.def.ports()) if (isPortAt(port, x, y, z)) {
                String flow = this.def.flow(port);
                input |= !"out".equals(flow);
                output |= !"in".equals(flow);
            }
            return input && output ? "both" : output ? "output" : "input";
        }

        /** 候选盒子可以与其它结构重叠；成型结构里的外观只能由实际宿主更新。 */
        private boolean mayUpdateVisualAt(int x, int y, int z) {
            Host owner = this.plugin.multiblocks().at(this.world, x, y, z);
            return owner == null || !owner.formed() || owner == this;
        }

        private void updatePortVisuals() {
            for (int x = 0; x < this.box.sizeX(); x++) for (int y = 0; y < this.box.sizeY(); y++)
                for (int z = 0; z < this.box.sizeZ(); z++) {
                    if (x != 0 && y != 0 && z != 0 && x != this.box.sizeX()-1
                            && y != this.box.sizeY()-1 && z != this.box.sizeZ()-1) continue;
                    int wx = this.originX+x, wy = this.originY+y, wz = this.originZ+z;
                    if (!this.world.isChunkLoaded(wx >> 4, wz >> 4)) continue;
                    var state = CraftEngineBlocks.getCustomBlockState(this.world.getBlockAt(wx, wy, wz));
                    if (state == null) continue;
                    if (this.def.roleOf(state.owner().value().id().asString()) == null) continue;
                    net.momirealms.craftengine.core.block.property.Property<String> property = state.getProperty("port_visual");
                    if (property == null || !mayUpdateVisualAt(wx, wy, wz)) continue;
                    String visual = portVisualAt(wx, wy, wz);
                    if (!visual.equals(state.get(property)))
                        net.momirealms.craftengine.bukkit.api.BukkitAdaptor.adapt(this.world).storageWorld().world()
                                .setBlockState(wx, wy, wz, state.with(property, visual),
                                        net.momirealms.craftengine.core.block.UpdateFlags.UPDATE_ALL);
                }
        }

        /** 控制器和内部过热元件等带 active 属性的结构件；只在状态变化时遍历结构。 */
        private void updateActiveVisuals(boolean force) {
            boolean active = formed();
            boolean hot = boilerHot();
            if (!force && Objects.equals(active, this.shownActive)
                    && Objects.equals(hot, this.shownHot)) {
                return;
            }
            for (int x = 0; x < this.box.sizeX(); x++) for (int y = 0; y < this.box.sizeY(); y++)
                for (int z = 0; z < this.box.sizeZ(); z++) {
                    int wx = this.originX + x, wy = this.originY + y, wz = this.originZ + z;
                    if (!this.world.isChunkLoaded(wx >> 4, wz >> 4)) continue;
                    String blockId = this.box.blockId(x, y, z);
                    if (blockId == null || this.def.roleOf(blockId) == null) continue;
                    var state = CraftEngineBlocks.getCustomBlockState(this.world.getBlockAt(wx, wy, wz));
                    if (state == null) continue;
                    net.momirealms.craftengine.core.block.property.Property<Boolean> property =
                            state.getProperty("active");
                    if (property == null || !mayUpdateVisualAt(wx, wy, wz)) continue;
                    boolean value = blockId.endsWith(":superheating_element") ? hot : active;
                    if (!Boolean.valueOf(value).equals(state.get(property))) {
                        net.momirealms.craftengine.bukkit.api.BukkitAdaptor.adapt(this.world)
                                .storageWorld().world().setBlockState(wx, wy, wz,
                                        state.with(property, value),
                                        net.momirealms.craftengine.core.block.UpdateFlags.UPDATE_ALL);
                    }
                }
            this.shownActive = active;
            this.shownHot = hot;
        }

        private boolean boilerHot() {
            return formed() && this.instance.heatNode() != null
                    && this.instance.heatNode().temperatureK() > MultiblockConfig.boilerBaseTemperatureK();
        }

        public String portMode(int x, int y, int z) {
            List<String> modes = availablePortModes(x, y, z);
            if (modes.isEmpty()) {
                return "";
            }
            String selected = this.portModes.getOrDefault(localKey(x, y, z), modes.get(0));
            return modes.contains(selected) ? selected : modes.get(0);
        }

        /** 配置器右击端口：只轮换该方块本身，不把同角色其它端口的模式一起改掉。 */
        public String cyclePortMode(int x, int y, int z) {
            List<String> modes = availablePortModes(x, y, z);
            if (modes.isEmpty()) {
                return "";
            }
            String key = localKey(x, y, z);
            String current = this.portModes.getOrDefault(key, modes.get(0));
            int index = modes.indexOf(current);
            String next = modes.get((index + 1) % modes.size());
            this.portModes.put(key, next);
            updatePortVisuals();
            markChanged();
            this.plugin.logistics(this.world).detachMultiblock(this);
            this.plugin.logistics(this.world).attachMultiblock(this);
            return next;
        }

        private String localKey(int x, int y, int z) {
            return (x - this.originX) + "," + (y - this.originY) + "," + (z - this.originZ);
        }

        public void resize(int sizeX, int sizeY, int sizeZ) {
            relocate(this.originX, this.originY, this.originZ, sizeX, sizeY, sizeZ);
        }

        private void relocate(int originX, int originY, int originZ, int sizeX, int sizeY, int sizeZ) {
            this.effects.remove();
            this.effects.invalidate();
            this.plugin.multiblocks().unindex(this);
            this.originX = originX;
            this.originY = originY;
            this.originZ = originZ;
            this.box = new StructureBox(this.world, this.def, this.originX, this.originY, this.originZ,
                    sizeX, sizeY, sizeZ);
            this.box.invalidateRoles();
            // 实例的尺寸不能被构造时定死：盒子重测后校验与形状面都要按新三边跑。
            this.instance.resize(sizeX, sizeY, sizeZ);
            this.plugin.multiblocks().index(this);
        }

        /** 重新校验 + 成型后按形状 resize 端口（容量/速率是形状推出来的，见 05 第七节第 4 条）。 */
        public boolean refresh() {
            boolean wasFormed = this.attachedSize != null;
            if (this.controller != null) {
                Discovered found = discover(this.world, this.def,
                        this.controller[0], this.controller[1], this.controller[2]);
                if (found != null && (found.originX != this.originX || found.originY != this.originY
                        || found.originZ != this.originZ || found.sizeX != this.box.sizeX()
                        || found.sizeY != this.box.sizeY() || found.sizeZ != this.box.sizeZ())) {
                    this.plugin.logistics(this.world).detachMultiblock(this);
                    relocate(found.originX, found.originY, found.originZ, found.sizeX, found.sizeY, found.sizeZ);
                }
            }
            this.dirty = false;
            this.box.invalidateRoles();
            this.validation = this.instance.validate(this.box);
            this.extraProblems = this.validation.ok() ? MultiblockChecks.inspect(this.box) : List.of();
            this.checksOk = this.extraProblems.isEmpty();
            if (formed()) {
                MultiblockSizing.apply(this);
                register();
                int[] size = {this.box.sizeX(), this.box.sizeY(), this.box.sizeZ()};
                // A valve can move/change roles without changing the bounding box.
                this.attachedSize = size;
                this.plugin.logistics(this.world).attachMultiblock(this);
                this.plugin.radiation(this.world).register("mb:" + key(),
                        this.originX + this.box.sizeX() / 2, this.originY + this.box.sizeY() / 2,
                        this.originZ + this.box.sizeZ() / 2, this::sourceMagnitude);
            } else {
                unregister();
                if (wasFormed) {
                    this.attachedSize = null;
                    this.plugin.logistics(this.world).detachMultiblock(this);
                    this.plugin.radiation(this.world).unregister("mb:" + key());
                }
            }
            updatePortVisuals();
            updateActiveVisuals(true);
            this.effects.invalidate();
            this.effects.tick();
            return formed();
        }

        public void markChanged() {
            if (this.marker != null) {
                this.marker.run();
            }
        }

        private void register() {
            if (!this.registered) {
                this.plugin.scheduler().register("mb:" + key(), this);
                this.registered = true;
            }
        }

        private void unregister() {
            if (this.registered) {
                this.plugin.scheduler().unregister("mb:" + key());
                this.registered = false;
            }
        }

        public void drop() {
            this.effects.remove();
            unregister();
            if (this.attachedSize != null) {
                this.attachedSize = null;
                this.plugin.logistics(this.world).detachMultiblock(this);
                this.plugin.radiation(this.world).unregister("mb:" + key());
            }
        }

        private double sourceMagnitude() {
            double total = 0.0;
            for (String port : this.def.ports()) {
                ResourceTank tank = this.instance.tank(port);
                if (!tank.type().equals(ResourceTypes.CHEMICAL)
                        || tank.storedId() == null || tank.amount() <= 0) {
                    continue;
                }
                Chemical chemical = Chemicals.registry().find(tank.storedId()).orElse(null);
                if (chemical != null) {
                    total += Radiation.dose(chemical, tank.amount());
                }
            }
            return total;
        }

        @Override
        public boolean needsTick() {
            return !this.dirty && formed() && (this.instance.needsTick()
                    || !Objects.equals(this.shownActive, formed())
                    || !Objects.equals(this.shownHot, boilerHot()));
        }

        @Override
        public long tick() {
            if (this.dirty || !formed()) {
                return 0;
            }
            long output = this.instance.tick();
            updateActiveVisuals(false);
            // Reactor/boiler/evaporation ticks mutate inventory and heat even when returning 0 J.
            markChanged();
            return output;
        }

        @Override
        public String describe() {
            return this.def.zh() + ' ' + this.box.sizeX() + '×' + this.box.sizeY() + '×' + this.box.sizeZ()
                    + "@(" + this.originX + ',' + this.originY + ',' + this.originZ + ") "
                    + (formed() ? "已成型" : "未成型") + (this.registered ? " 已挂调度器" : "")
                    + (this.dirty ? " 待校验" : "");
        }

        public void save(DataTag tag) {
            this.instance.save(tag);
            tag.putLong("blades", this.blades);
            DataTag modes = tag.child("port_modes");
            for (Map.Entry<String, String> entry : this.portModes.entrySet()) {
                DataTag item = modes.newElement("items");
                item.putString("key", entry.getKey());
                item.putString("mode", entry.getValue());
            }
        }

        public void load(DataTag tag) {
            this.instance.load(tag);
            this.blades = tag.getLong("blades", this.blades);
            this.portModes.clear();
            for (DataTag item : tag.child("port_modes").list("items")) {
                String key = item.getString("key", "");
                String mode = item.getString("mode", "");
                if (!key.isEmpty() && !mode.isEmpty()) {
                    this.portModes.put(key, mode);
                }
            }
        }
    }
}
