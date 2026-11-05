package net.kuzuanpa.cemekanism.transmitter;

import mekcore.energy.EnergyGrid;
import mekcore.heat.HeatNode;
import mekcore.heat.HeatTier;
import mekcore.heat.HeatTopology;
import mekcore.item.ItemEndpoint;
import mekcore.item.ItemHandler;
import mekcore.logistics.NetworkEndpoint;
import mekcore.logistics.NetworkReport;
import mekcore.logistics.PortMode;
import mekcore.logistics.TransmitterNetwork;
import mekcore.logistics.topology.ItemTopology;
import mekcore.logistics.topology.TransmitterTopology;
import mekcore.machine.Direction;
import mekcore.machine.Machine;
import mekcore.machine.MachineBlueprint;
import mekcore.machine.SideMode;
import mekcore.multiblock.MultiblockInstance;
import mekcore.resource.ResourceHandler;
import mekcore.resource.ResourceTank;
import mekcore.resource.ResourceType;
import mekcore.resource.ResourceTypes;
import mekcore.sim.TickAffinity;
import mekcore.sim.Tickable;
import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.kuzuanpa.cemekanism.i18n.I18n;
import net.kuzuanpa.cemekanism.generated.GeneratedContent;
import net.kuzuanpa.cemekanism.machine.MachineBlockEntityController;
import net.kuzuanpa.cemekanism.multiblock.MultiblockHosts;
import net.kuzuanpa.cemekanism.nuclear.WasteBarrelLogic;
import net.kuzuanpa.cemekanism.platform.BukkitItems;
import net.kuzuanpa.cemekanism.storage.PassiveStorageLogic;
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.LongSupplier;

/**
 * 一个世界的物流（文档 06）：四条网络 + 电网 + 端点对账。
 *
 * 世界侧只提供两件事——谁挨着谁、区块在不在；分网、合并、拆分、端点归属、能量结算全在核心。
 * 本类的方法都读写世界，**只在主线程调**（事件、方块实体生命周期、命令）；网络与电网自己是纯核心数据，
 * 物品网络挂 MAIN，资源/热量/电网挂 CORE。能量族那份拓扑不挂 tick：电网自己结算。
 */
public final class Logistics {

    /** 电网上的一条连线。 */
    private sealed interface EnergyLink permits MachineLink, MultiblockLink {
        String id();

        void attach(EnergyGrid grid);
    }

    private enum LinkKind {
        SOURCE, CONSUMER, STORE
    }

    private record MachineLink(String id, Machine machine, LinkKind kind, LongSupplier demand, boolean charge, boolean discharge) implements EnergyLink {
        @Override
        public void attach(EnergyGrid grid) {
            switch (this.kind) {
                case SOURCE -> grid.addMachineSource(this.id, this.machine);
                case STORE -> this.machine.energyTank().ifPresent(tank -> grid.addStore(tank, this.charge, this.discharge));
                case CONSUMER -> this.machine.energyTank().ifPresent(tank ->
                        grid.addConsumer(this.id, this.demand, tank));
            }
        }
    }

    private record MultiblockLink(String id, MultiblockInstance instance, String port, LinkKind kind)
            implements EnergyLink {
        @Override
        public void attach(EnergyGrid grid) {
            switch (this.kind) {
                case SOURCE -> grid.addMultiblockSource(this.id, this.instance, this.port);
                case STORE -> grid.addStore(this.instance.tank(this.port));
                case CONSUMER -> {
                    ResourceTank tank = this.instance.tank(this.port);
                    grid.addConsumer(this.id, () -> Math.max(0, tank.capacity() - tank.amount()), tank);
                }
            }
        }
    }

    private final CeMekanismPlugin plugin;
    private final World world;

    /** 流体 + 化学品：拓扑自己 tick。 */
    private final TransmitterTopology resources;
    /** 能量：单独一份拓扑，**不挂 tick**（电网自己 tick，见类注释）。 */
    private final TransmitterTopology energy;
    private final ItemTopology items;
    private final HeatTopology heat;

    private final Map<String, Map<String, NetworkEndpoint>> resourceEndpoints = new LinkedHashMap<>();
    private final Map<String, Map<String, ItemEndpoint>> itemEndpoints = new LinkedHashMap<>();
    private final Map<String, Map<String, EnergyLink>> energyLinks = new LinkedHashMap<>();
    private final Map<String, Set<String>> heatPartners = new LinkedHashMap<>();
    /** 电网 + 它自己的任务对象（每张网一个，由一条世界级任务轮询，别动态注册 CORE 任务）。 */
    private final Map<String, Grid> grids = new LinkedHashMap<>();
    private final Map<String, TransmitterBlockEntityController> redstone = new LinkedHashMap<>();
    private final Map<String, TransmitterBlockEntityController> visuals = new LinkedHashMap<>();
    private final Set<String> placed = new LinkedHashSet<>();

    private boolean started;

    private record Grid(EnergyGrid grid, Tickable task) {
    }

    Logistics(CeMekanismPlugin plugin, World world) {
        this.plugin = plugin;
        this.world = world;
        this.resources = plugin.core().factories().topology();
        this.energy = plugin.core().factories().topology();
        this.items = plugin.core().factories().itemTopology();
        this.heat = plugin.core().factories().heatTopology();
    }

    public World world() {
        return this.world;
    }

    public TransmitterTopology resourceTopology() {
        return this.resources;
    }

    public TransmitterTopology energyTopology() {
        return this.energy;
    }

    public HeatTopology heatTopology() {
        return this.heat;
    }

    // ────────────────────────────── 调度器接线（一世界一份）

    void start() {
        if (this.started) {
            return;
        }
        this.started = true;
        // 资源拓扑/热网/电网挂 CORE；物品网络直接读写 Bukkit 库存和显示实体，必须挂 MAIN。
        // 任务本身只注册一次，改拓扑是主线程的事，
        // 所以所有入口都上同一把锁（synchronized），免得 CORE 线程正在遍历时被改（见 10 的 P24/P25）。
        this.plugin.scheduler().register(taskId("resources"), guarded(this.resources), TickAffinity.CORE);
        this.plugin.scheduler().register(taskId("items"), guarded(this.items), TickAffinity.MAIN);
        this.plugin.scheduler().register(taskId("heat"), guarded(this.heat), TickAffinity.CORE);
        this.plugin.scheduler().register(taskId("energy"), guarded(gridPump()), TickAffinity.CORE);
        // 红石：元件没有自己的刻任务，这里一条节流任务扫"响应红石的元件"（没开的一个都不扫）
        this.plugin.scheduler().register(taskId("redstone"), new Tickable() {
            @Override
            public boolean needsTick() {
                return !Logistics.this.redstone.isEmpty();
            }

            @Override
            public long tick() {
                for (TransmitterBlockEntityController controller
                        : List.copyOf(Logistics.this.redstone.values())) {
                    controller.refreshRedstone();
                }
                return 0;
            }

            @Override
            public String describe() {
                return I18n.tr("transmitter.log.redstone", Logistics.this.redstone.size());
            }
        }, TickAffinity.MAIN, LogisticsConfig.redstoneInterval());
        this.plugin.scheduler().register(taskId("visual"), new Tickable() {
            @Override
            public boolean needsTick() {
                return LogisticsConfig.transmitterContentsEnabled()
                        && !Logistics.this.visuals.isEmpty();
            }

            @Override
            public long tick() {
                for (TransmitterBlockEntityController controller
                        : List.copyOf(Logistics.this.visuals.values())) {
                    controller.refreshContents();
                }
                return 0;
            }

            @Override
            public String describe() {
                return I18n.tr("transmitter.log.visual", Logistics.this.visuals.size());
            }
        }, TickAffinity.MAIN, LogisticsConfig.transmitterVisualInterval());
    }

    void stop() {
        if (!this.started) {
            return;
        }
        this.started = false;
        for (String task : List.of(taskId("resources"), taskId("items"), taskId("heat"), taskId("energy"),
                taskId("redstone"), taskId("visual"))) {
            this.plugin.scheduler().unregister(task);
        }
        this.grids.clear();
        this.visuals.clear();
    }

    private String taskId(String kind) {
        return "mek:net:" + this.world.getName() + ':' + kind;
    }

    /** 同一把锁包一层：CORE 线程只在锁里跑，主线程改拓扑也只在锁里改。 */
    private Tickable guarded(Tickable delegate) {
        return new Tickable() {
            @Override
            public boolean needsTick() {
                synchronized (Logistics.this) {
                    return delegate.needsTick();
                }
            }

            @Override
            public long tick() {
                synchronized (Logistics.this) {
                    return delegate.tick();
                }
            }

            @Override
            public String describe() {
                synchronized (Logistics.this) {
                    return delegate.describe();
                }
            }
        };
    }

    /** 电网的轮询：每张网跑它自己那份 tickable（就是 EnergyGrid 的结算入口）。 */
    private Tickable gridPump() {
        return new Tickable() {
            @Override
            public boolean needsTick() {
                for (Grid grid : Logistics.this.grids.values()) {
                    if (grid.task().needsTick()) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public long tick() {
                long stored = 0;
                for (Grid grid : Logistics.this.grids.values()) {
                    grid.task().tick();
                    stored += grid.grid().network().stored();
                }
                return stored;
            }

            @Override
            public String describe() {
                return I18n.tr("transmitter.log.grids", Logistics.this.grids.size());
            }
        };
    }

    // ────────────────────────────── 方块实体的三个入口

    /** 元件加载：登记节点、连边、绑端点。 */
    public synchronized void place(TransmitterBlockEntityController controller) {
        this.placed.add(controller.key());
        if (!controller.itemPipe()) {
            this.visuals.put(controller.key(), controller);
        }
        if (controller.heat()) {
            // 热导线缆卸载时会被摘出热网（HeatTopology 没有 loaded 标志），温度从存档接回来
            this.heat.addNode(controller.key(), controller.heatNode());
        }
        reconcile(controller);
        // 方块实体读档时 ItemNode/TransmitterNode 自己带着 loaded=false（卸载时写的），
        // reconcile 会把新节点登记进拓扑，之后必须把加载状态翻回来；
        // 否则网络会一直显示"被断开"、缓存也不再搬（台账 U23）。
        if (controller.itemPipe()) {
            this.items.setLoaded(controller.key(), true);
        } else if (!controller.heat()) {
            controller.topology().setLoaded(controller.key(), true);
        }
    }

    /** 元件卸载：留在网里但不搬运（缓存跟着方块实体存档走）。 */
    public synchronized void setLoaded(TransmitterBlockEntityController controller, boolean loaded) {
        if (loaded) {
            if (!controller.itemPipe()) {
                this.visuals.put(controller.key(), controller);
            }
        } else {
            this.visuals.remove(controller.key());
        }
        if (controller.itemPipe()) {
            this.items.setLoaded(controller.key(), loaded);
        } else if (controller.heat()) {
            if (!loaded) {
                dropHeatLinks(controller.key());
                this.heat.removeNode(controller.key());
                this.heatPartners.remove(controller.key());
            }
        } else {
            controller.topology().setLoaded(controller.key(), loaded);
        }
    }

    /** 元件被拆：解绑端点、断开与邻居的边、从拓扑与热网里摘掉。 */
    public synchronized void remove(TransmitterBlockEntityController controller) {
        String key = controller.key();
        unbind(controller, Map.of(), Map.of());
        this.placed.remove(key);
        this.visuals.remove(key);
        if (controller.itemPipe()) {
            // A removed physical transporter refunds its real payload before discarding its buffer.
            for (var ref : controller.itemNode().buffer()) {
                org.bukkit.inventory.ItemStack item = ref.payload() instanceof org.bukkit.inventory.ItemStack payload
                        ? payload.clone() : BukkitItems.create(ref.id());
                if (item != null) {
                    long left = ref.amount();
                    while (left > 0) {
                        int amount = (int) Math.min(left, item.getMaxStackSize());
                        org.bukkit.inventory.ItemStack drop = item.clone(); drop.setAmount(amount);
                        this.world.dropItemNaturally(new org.bukkit.Location(this.world, controller.x() + .5,
                                controller.y() + .5, controller.z() + .5), drop);
                        left -= amount;
                    }
                }
            }
            controller.itemNode().clearBuffer();
            disconnectItem(controller);
            this.items.removeNode(key);
        } else if (controller.heat()) {
            dropHeatLinks(key);
            this.heat.removeNode(key);
            this.heatPartners.remove(key);
        } else {
            disconnectResources(controller);
            controller.topology().removeNode(key);
            this.energyLinks.remove(key);
        }
        watchRedstone(controller, false);
        // 拆掉这一格后，邻居的拓扑与手臂必须重算：BlockBreakEvent 在方块真正消失前触发，
        // 只靠事件里的 refreshAround 会看到"旧方块还在"，邻接手臂就留在原地。
        refreshAround(controller.x(), controller.y(), controller.z());
        syncEnergy();
    }

    // ────────────────────────────── 对账：六个面重算一遍

    /** 一个元件的六个面：连边、端点（资源/物品）、热连接，最后重建电网。 */
    synchronized void reconcile(TransmitterBlockEntityController controller) {
        int x = controller.x();
        int y = controller.y();
        int z = controller.z();
        if (controller.itemPipe()) {
            this.items.addNode(controller.key(), controller.itemNode());
            Map<String, ItemEndpoint> fresh = new LinkedHashMap<>();
            for (Direction direction : Direction.values()) {
                int[] offset = offset(direction);
                int nx = x + offset[0];
                int ny = y + offset[1];
                int nz = z + offset[2];
                TransmitterBlockEntityController neighbour = transmitterAt(nx, ny, nz);
                if (neighbour != null) {
                    if (neighbour.itemPipe() && registered(this.items, controller.key(), neighbour.key())) {
                        if (connected(controller, direction, neighbour)) {
                            this.items.connect(controller.key(), neighbour.key());
                        } else {
                            this.items.disconnect(controller.key(), neighbour.key());
                        }
                    }
                    continue;
                }
                bindMachineItems(controller, direction, nx, ny, nz, fresh);
            }
            unbindItems(controller, fresh);
        } else if (controller.heat()) {
            this.heat.addNode(controller.key(), controller.heatNode());
            for (Direction direction : Direction.values()) {
                int[] offset = offset(direction);
                int nx = x + offset[0];
                int ny = y + offset[1];
                int nz = z + offset[2];
                String partner = heatPartnerAt(controller, direction, nx, ny, nz);
                if (partner != null && registered(this.heat, controller.key(), partner)) {
                    this.heat.connect(controller.key(), partner,
                            worse(controller.heatTier(), tierAt(nx, ny, nz)));
                }
            }
            reconcileHeatLinks(controller, x, y, z);
        } else {
            TransmitterTopology topology = controller.topology();
            topology.addNode(controller.key(), controller.node());
            Map<String, NetworkEndpoint> fresh = new LinkedHashMap<>();
            Map<String, EnergyLink> links = new LinkedHashMap<>();
            for (Direction direction : Direction.values()) {
                int[] offset = offset(direction);
                int nx = x + offset[0];
                int ny = y + offset[1];
                int nz = z + offset[2];
                TransmitterBlockEntityController neighbour = transmitterAt(nx, ny, nz);
                if (neighbour != null) {
                    if (neighbour.itemPipe() || neighbour.heat()) {
                        continue;                       // 不同管线永不连通
                    }
                    if (!neighbour.node().tier().family().id()
                            .equals(controller.node().tier().family().id())) {
                        continue;                       // 不同族不在同一张网里
                    }
                    if (registered(topology, controller.key(), neighbour.key())) {
                        if (connected(controller, direction, neighbour)) {
                            topology.connect(controller.key(), neighbour.key());
                        } else {
                            topology.disconnect(controller.key(), neighbour.key());
                        }
                    }
                    continue;
                }
                bindMachineResource(controller, direction, nx, ny, nz, fresh);
                bindMultiblockResource(controller, direction, nx, ny, nz, fresh);
                if (controller.isEnergy()) {
                    linkMachineEnergy(controller, direction, nx, ny, nz, links);
                    linkMultiblockEnergy(controller, direction, nx, ny, nz, links);
                }
            }
            unbind(controller, fresh, Map.of());
            this.energyLinks.put(controller.key(), controller.isEnergy() ? links : Map.of());
        }
        controller.refreshRenderer();
        syncEnergy();
    }

    /** 机器/方块变化：周围一圈的元件重算（机器的面配置、机器装卸、结构成型都走它）。 */
    public synchronized void refreshAround(int x, int y, int z) {
        refreshAt(x, y, z);
        for (Direction direction : Direction.values()) {
            int[] offset = offset(direction);
            refreshAt(x + offset[0], y + offset[1], z + offset[2]);
        }
        syncEnergy();
    }

    /** 这一格是已加载的元件就重算它（结构外壳扫描与事件都走它）。 */
    public synchronized void refreshAt(int x, int y, int z) {
        TransmitterBlockEntityController controller = transmitterAt(x, y, z);
        if (controller != null && this.placed.contains(controller.key())) {
            reconcile(controller);
        }
    }

    // ────────────────────────────── 机器与多方块

    /** 机器加载：它的热量节点进热网，并通知周围一圈元件重绑端点。 */
    /** Sorter injection shares the topology lock with the CORE item network. */
    public synchronized int insertSorted(TransmitterBlockEntityController pipe, Direction side, int colour,
                                         org.bukkit.inventory.ItemStack item, int amount) {
        if (!pipe.itemPipe() || pipe.port(side) == PortMode.NONE || !pipe.itemNode().active()
                || colour >= 0 && pipe.itemNode().channel() != colour) return 0;
        org.bukkit.inventory.ItemStack one = item.clone(); one.setAmount(1);
        int inserted = (int) pipe.itemNode().insert(mekcore.item.ItemRef.withPayload(
                BukkitItems.idOf(item), amount, one));
        if (inserted > 0) pipe.markChanged();
        return inserted;
    }

    public synchronized void attachMachine(MachineBlockEntityController controller) {
        HeatNode node = controller.machine().heatNode();
        if (node != null) {
            this.heat.addNode(machineHeatKey(controller.x(), controller.y(), controller.z()), node);
        }
        refreshAround(controller.x(), controller.y(), controller.z());
    }

    public synchronized void detachMachine(MachineBlockEntityController controller) {
        this.heat.removeNode(machineHeatKey(controller.x(), controller.y(), controller.z()));
        refreshAround(controller.x(), controller.y(), controller.z());
    }

    /** 结构成型：热量节点进热网、端口端点绑上、外壳一圈的元件重算。 */
    public synchronized void attachMultiblock(MultiblockHosts.Host host) {
        HeatNode node = host.instance().heatNode();
        if (node != null) {
            this.heat.addNode(multiblockHeatKey(host.key()), node);
        }
        refreshShell(host);
    }

    /** 结构失效/拆掉：反之。 */
    public synchronized void detachMultiblock(MultiblockHosts.Host host) {
        String key = multiblockHeatKey(host.key());
        this.heat.removeNode(key);
        dropHeatLinks(key);
        this.heatPartners.remove(key);
        refreshShell(host);
    }

    /** 盒子外一层的元件重算；热导线缆只连接热交换模式端口。 */
    private void refreshShell(MultiblockHosts.Host host) {
        for (int x = host.box().originX() - 1; x <= host.box().originX() + host.box().sizeX(); x++) {
            for (int y = host.box().originY() - 1; y <= host.box().originY() + host.box().sizeY(); y++) {
                for (int z = host.box().originZ() - 1; z <= host.box().originZ() + host.box().sizeZ(); z++) {
                    if (!host.contains(x, y, z)) {
                        refreshAt(x, y, z);
                    }
                }
            }
        }
        syncEnergy();
    }

    // ────────────────────────────── 红石

    /** 响应红石的元件才进这条节流任务（没开的元件一律睡着，见文档 06 第七节）。 */
    public synchronized void watchRedstone(TransmitterBlockEntityController controller, boolean watch) {
        if (watch) {
            this.redstone.put(controller.key(), controller);
        } else {
            this.redstone.remove(controller.key());
        }
    }

    // ────────────────────────────── 电网

    /** 按当前拓扑重建电网：网络没了就从轮询里摘掉；活着的每张网清端点后重新挂（端点只能由电网自己挂）。 */
    public synchronized void syncEnergy() {
        List<String> alive = this.energy.networkIds();
        for (String network : List.copyOf(this.grids.keySet())) {
            if (!alive.contains(network)) {
                this.grids.remove(network);
            }
        }
        for (String network : alive) {
            TransmitterNetwork target = this.energy.networkOf(network);
            if (target == null || target.nodes().isEmpty()) {
                continue;
            }
            target.clearEndpoints();
            EnergyGrid grid = new EnergyGrid(target);
            // 同一台机器的两面各贴一根线缆时，两条线缆会各记一条同 id 的连线；
            // 电网里只该挂一份端点，否则需求/提取都会被重复计算。
            Map<String, EnergyLink> links = new LinkedHashMap<>();
            for (String key : this.energy.graph().members(network)) {
                links.putAll(this.energyLinks.getOrDefault(key, Map.of()));
            }
            for (EnergyLink link : links.values()) {
                link.attach(grid);
            }
            this.grids.put(network, new Grid(grid, guarded(grid.tickable())));
        }
    }

    /** 命令灌完料之后催一趟：CORE 车道每 50 ms 问一次，这里只是让"改完马上看得见"。 */
    public synchronized void wake() {
        for (String kind : List.of("resources", "items", "heat", "energy", "redstone")) {
            this.plugin.scheduler().wake(taskId(kind));
        }
    }

    // ────────────────────────────── 端点

    private void unbind(TransmitterBlockEntityController controller,
                        Map<String, NetworkEndpoint> fresh, Map<String, ItemEndpoint> freshItems) {
        Map<String, NetworkEndpoint> previous = this.resourceEndpoints.getOrDefault(controller.key(), Map.of());
        for (String id : previous.keySet()) {
            if (!fresh.containsKey(id)) {
                controller.topology().unbindEndpoint(controller.key(), id);
            }
        }
        for (NetworkEndpoint endpoint : fresh.values()) {
            controller.topology().bindEndpoint(controller.key(), endpoint);
        }
        this.resourceEndpoints.put(controller.key(), fresh);
        unbindItems(controller, freshItems);
    }

    private void unbindItems(TransmitterBlockEntityController controller, Map<String, ItemEndpoint> fresh) {
        Map<String, ItemEndpoint> previous = this.itemEndpoints.getOrDefault(controller.key(), Map.of());
        for (String id : previous.keySet()) {
            if (!fresh.containsKey(id)) {
                this.items.unbindEndpoint(controller.key(), id);
            }
        }
        for (ItemEndpoint endpoint : fresh.values()) {
            this.items.bindEndpoint(controller.key(), endpoint);
        }
        this.itemEndpoints.put(controller.key(), fresh);
    }

    /** 机器的资源端口：管道的面模式 × 机器那一面的 SideMode → 拉/推（文档 06 第十节第 3 条）。 */
    private void bindMachineResource(TransmitterBlockEntityController controller, Direction direction,
                                     int nx, int ny, int nz, Map<String, NetworkEndpoint> fresh) {
        if (controller.port(direction) == PortMode.NONE) {
            return;
        }
        MachineBlockEntityController neighbour = this.plugin.machineAtQuiet(this.world, nx, ny, nz);
        if (neighbour == null) {
            return;
        }
        Machine machine = neighbour.machine();
        Direction back = direction.opposite();
        if (WasteBarrelLogic.ID.equals(machine.blueprint().id())
                && back != Direction.UP && back != Direction.DOWN) {
            return;
        }
        ResourceType wanted = controller.family().resource();
        String owner = machine.blueprint().id() + '@' + nx + ',' + ny + ',' + nz;
        for (String port : machine.portNames()) {
            if (machine.tank(port).filter(tank -> tank.type().equals(wanted)).isEmpty()) {
                continue;
            }
            ResourceHandler handler = machine.sideView(port, back).orElse(null);
            if (handler == null) {
                continue;
            }
            for (PortMode mode : directionsFor(controller.port(direction), machine.side(port, back))) {
                String id = endpointId(owner, port, mode);
                // Buffers receive surplus after consumers, avoiding self-refill through two side views.
                int priority = mode == PortMode.PUSH && machine.blueprint().logic()
                        instanceof PassiveStorageLogic ? 100 : 0;
                fresh.put(id, NetworkEndpoint.of(id, handler, mode).withPriority(priority));
            }
        }
    }

    /** 多方块的端口：方向来自表里的 `flow:`（in/out/both/store），端口位置按盒子的局部坐标回查。 */
    private void bindMultiblockResource(TransmitterBlockEntityController controller, Direction direction,
                                        int nx, int ny, int nz, Map<String, NetworkEndpoint> fresh) {
        if (controller.port(direction) == PortMode.NONE) {
            return;
        }
        MultiblockHosts.Host host = this.plugin.multiblocks().at(this.world, nx, ny, nz);
        if (host == null || !host.formed()) {
            return;
        }
        ResourceType wanted = controller.family().resource();
        for (String port : host.def().ports()) {
            if (!host.isPortAt(port, nx, ny, nz)) {
                continue;
            }
            if ("laser_focus".equals(host.def().portRole(port))) {
                continue;
            }
            ResourceTank tank = host.instance().tank(port);
            if (!tank.type().equals(wanted)) {
                continue;
            }
            String owner = host.def().id() + '@' + nx + ',' + ny + ',' + nz;
            for (PortMode mode : flowDirections(host.portFlowAt(port, nx, ny, nz), controller.port(direction))) {
                String id = endpointId(owner, port, mode);
                fresh.put(id, NetworkEndpoint.of(id, tank, mode));
            }
        }
    }

    private void bindMachineItems(TransmitterBlockEntityController controller, Direction direction,
                                  int nx, int ny, int nz, Map<String, ItemEndpoint> fresh) {
        if (controller.port(direction) == PortMode.NONE) {
            return;
        }
        MachineBlockEntityController neighbour = this.plugin.machineAtQuiet(this.world, nx, ny, nz);
        if (neighbour == null) {
            return;
        }
        Machine machine = neighbour.machine();
        Direction back = direction.opposite();
        ItemHandler handler = machine.itemView(back).orElse(null);
        if (handler == null) {
            return;
        }
        String owner = machine.blueprint().id() + '@' + nx + ',' + ny + ',' + nz;
        for (PortMode mode : directionsFor(controller.port(direction), machine.itemSide(back))) {
            String id = endpointId(owner, "items", mode);
            fresh.put(id, ItemEndpoint.of(id, handler, mode));
        }
    }

    private void linkMachineEnergy(TransmitterBlockEntityController controller, Direction direction,
                                   int nx, int ny, int nz, Map<String, EnergyLink> fresh) {
        if (controller.port(direction) == PortMode.NONE) {
            return;
        }
        MachineBlockEntityController neighbour = this.plugin.machineAtQuiet(this.world, nx, ny, nz);
        if (neighbour == null) {
            return;
        }
        Machine machine = neighbour.machine();
        ResourceTank tank = machine.energyTank().orElse(null);
        if (tank == null || tank.capacity() <= 0) {
            return;
        }
        String owner = machine.blueprint().id() + '@' + nx + ',' + ny + ',' + nz;
        LinkKind kind = switch (GeneratedContent.MACHINE_ROLES
                .getOrDefault(machine.blueprint().id(), "machine")) {
            case "source" -> LinkKind.SOURCE;
            case "store" -> LinkKind.STORE;
            default -> LinkKind.CONSUMER;
        };
        // 能量端口和资源端口一样要匹配侧面模式（10 的 G19）：消费者只接输入面、电源只接输出面、
        // 储能只接输入输出面；"关"当然不接。
        SideMode mode = machine.side(MachineBlueprint.ENERGY_PORT, direction.opposite());
        if (!energySideAllowed(machine.blueprint().id(), mode)) {
            return;
        }
        boolean charge = mode.allowsInsert() && controller.port(direction) != PortMode.PULL;
        boolean discharge = mode.allowsExtract() && controller.port(direction) != PortMode.PUSH;
        String linkId = kind == LinkKind.STORE ? owner + ':' + charge + ':' + discharge : owner;
        if (kind == LinkKind.STORE && !charge && !discharge) return;
        fresh.put(linkId, new MachineLink(linkId, machine, kind, neighbour::gridDemand, charge, discharge));
    }

    /**
     * 机器能量面能不能在这个方向上挂端点：电源只能出、消费者只能进、储能必须双向。
     * 手臂判断也走这一条，避免"画了接头却没有端点"。
     */
    static boolean energySideAllowed(String blueprintId, SideMode mode) {
        return switch (GeneratedContent.MACHINE_ROLES.getOrDefault(blueprintId, "machine")) {
            case "source" -> mode.allowsExtract();
            case "store" -> mode.allowsInsert() || mode.allowsExtract();
            default -> mode.allowsInsert();
        };
    }

    private void linkMultiblockEnergy(TransmitterBlockEntityController controller, Direction direction,
                                      int nx, int ny, int nz, Map<String, EnergyLink> fresh) {
        if (controller.port(direction) == PortMode.NONE) {
            return;
        }
        MultiblockHosts.Host host = this.plugin.multiblocks().at(this.world, nx, ny, nz);
        if (host == null || !host.formed()) {
            return;
        }
        for (String port : host.def().ports()) {
            if (!host.isPortAt(port, nx, ny, nz)) {
                continue;
            }
            if ("laser_focus".equals(host.def().portRole(port))) {
                continue;
            }
            ResourceTank tank = host.instance().tank(port);
            if (!tank.type().equals(ResourceTypes.ENERGY) || tank.capacity() <= 0) {
                continue;
            }
            LinkKind kind = switch (host.portFlowAt(port, nx, ny, nz)) {
                case "out" -> LinkKind.SOURCE;
                case "in" -> LinkKind.CONSUMER;
                case "store" -> LinkKind.STORE;
                default -> null;
            };
            if (kind == null) {
                continue;
            }
            // 同一个端口角色的多个阀门共用 instance.tank(port)；按宿主 + 端口名去重，
            // 不能把阀门坐标写进 id，否则两面各贴一根线缆会给同一口罐挂两份端点。
            String id = host.key() + ':' + port;
            fresh.put(id, new MultiblockLink(id, host.instance(), port, kind));
        }
    }

    // ────────────────────────────── 热网

    /** 这一格上有没有"热量节点"（机器、成型结构、别的热导线缆）。 */
    private String heatPartnerAt(TransmitterBlockEntityController controller, Direction direction, int nx, int ny, int nz) {
        if (controller.port(direction) == PortMode.NONE || !chunkLoaded(nx, nz)) {
            return null;
        }
        String machine = machineHeatKey(nx, ny, nz);
        if (this.heat.node(machine) != null) {
            return machine;
        }
        MultiblockHosts.Host host = this.plugin.multiblocks().at(this.world, nx, ny, nz);
        if (host != null && host.isHeatPortAt(nx, ny, nz)) {
            String key = multiblockHeatKey(host.key());
            if (this.heat.node(key) != null) {
                return key;
            }
        }
        TransmitterBlockEntityController neighbour = transmitterAt(nx, ny, nz);
        if (neighbour != null && neighbour.heat() && neighbour.port(direction.opposite()) != PortMode.NONE
                && !neighbour.key().equals(controller.key())
                && this.heat.node(neighbour.key()) != null) {
            return neighbour.key();
        }
        return null;
    }

    /** 断掉不再相邻的热连接（拆方块、搬走机器都会走到这里）。 */
    private void reconcileHeatLinks(TransmitterBlockEntityController controller, int x, int y, int z) {
        Set<String> fresh = new LinkedHashSet<>();
        for (Direction direction : Direction.values()) {
            int[] offset = offset(direction);
            String partner = heatPartnerAt(controller, direction, x + offset[0], y + offset[1], z + offset[2]);
            if (partner != null) {
                fresh.add(partner);
            }
        }
        for (String previous : this.heatPartners.getOrDefault(controller.key(), Set.of())) {
            if (!fresh.contains(previous) && registered(this.heat, controller.key(), previous)) {
                this.heat.disconnect(controller.key(), previous);
            }
        }
        this.heatPartners.put(controller.key(), fresh);
    }

    private void dropHeatLinks(String key) {
        for (String partner : this.heatPartners.getOrDefault(key, Set.of())) {
            if (registered(this.heat, key, partner)) {
                this.heat.disconnect(key, partner);
            }
        }
    }

    private HeatTier tierAt(int x, int y, int z) {
        TransmitterBlockEntityController controller = transmitterAt(x, y, z);
        return controller != null && controller.heat() ? controller.heatTier() : HeatTier.BASIC;
    }

    /** 两个不同等级的元件连一起：按热损大的那个算（保守口径）。 */
    private static HeatTier worse(HeatTier a, HeatTier b) {
        return a.ordinal() <= b.ordinal() ? a : b;
    }

    // ────────────────────────────── 邻居与工具

    /** 这一格是不是元件（不论区块装载与否），是就返回它的控制器；区块没装载返回 null。 */
    private TransmitterBlockEntityController transmitterAt(int x, int y, int z) {
        if (!chunkLoaded(x, z) || !isTransmitterBlock(x, y, z)) {
            return null;
        }
        return this.plugin.transmitterAt(this.world, x, y, z);
    }

    private boolean isTransmitterBlock(int x, int y, int z) {
        ImmutableBlockState state = CraftEngineBlocks.getCustomBlockState(this.world.getBlockAt(x, y, z));
        return state != null && TransmitterBlocks.isTransmitter(state.owner().value().id().asString());
    }

    private boolean chunkLoaded(int x, int z) {
        return this.world.isChunkLoaded(x >> 4, z >> 4);
    }

    /** 两个元件的这个面对得上吗：两面都不是"不连接"，颜色也合得来。 */
    private static boolean connected(TransmitterBlockEntityController self, Direction direction,
                                     TransmitterBlockEntityController other) {
        return self.port(direction) != PortMode.NONE
                && other.port(direction.opposite()) != PortMode.NONE
                && self.channelMatches(other);
    }

    private void disconnectResources(TransmitterBlockEntityController controller) {
        for (Direction direction : Direction.values()) {
            int[] offset = offset(direction);
            TransmitterBlockEntityController neighbour = transmitterAt(controller.x() + offset[0],
                    controller.y() + offset[1], controller.z() + offset[2]);
            if (neighbour != null && !neighbour.itemPipe() && !neighbour.heat()
                    && registered(controller.topology(), controller.key(), neighbour.key())) {
                controller.topology().disconnect(controller.key(), neighbour.key());
            }
        }
    }

    private void disconnectItem(TransmitterBlockEntityController controller) {
        for (Direction direction : Direction.values()) {
            int[] offset = offset(direction);
            TransmitterBlockEntityController neighbour = transmitterAt(controller.x() + offset[0],
                    controller.y() + offset[1], controller.z() + offset[2]);
            if (neighbour != null && neighbour.itemPipe()
                    && registered(this.items, controller.key(), neighbour.key())) {
                this.items.disconnect(controller.key(), neighbour.key());
            }
        }
    }

    /**
     * 两个 key 都在这个拓扑里才连/断：读档时方块实体的加载顺序不定，
     * 邻居的节点可能还没登记，而核心的 connect/disconnect 都要求两端已注册
     * （对没登记的 key 调用 disconnect 会踩 NetworkGraph 的不可变 Set，见 10 的 G23）。
     */
    private static boolean registered(TransmitterTopology topology, String a, String b) {
        return topology.node(a) != null && topology.node(b) != null;
    }

    private static boolean registered(HeatTopology topology, String a, String b) {
        return topology.node(a) != null && topology.node(b) != null;
    }

    private static boolean registered(ItemTopology topology, String a, String b) {
        return topology.node(a) != null && topology.node(b) != null;
    }

    /**
     * 端点方向 = 管道的面模式 × 对面那一侧的配置。
     * `NORMAL` 在核心里不是"又拉又推"（网络只服务 PULL/PUSH 端点），所以"被动"必须翻译成具体方向。
     */
    static List<PortMode> directionsFor(PortMode face, SideMode side) {
        if (face == PortMode.NONE || side == SideMode.NONE) {
            return List.of();
        }
        List<PortMode> modes = new ArrayList<>(2);
        if (face != PortMode.PULL && side.allowsInsert()) {
            modes.add(PortMode.PUSH);
        }
        if (face != PortMode.PUSH && side.allowsExtract()) {
            modes.add(PortMode.PULL);
        }
        return modes;
    }

    /** 多方块端口方向 = 表里的 `flow:` × 管道的面模式（flow 是"多方块那一侧"的口径）。 */
    static List<PortMode> flowDirections(String flow, PortMode face) {
        if (face == PortMode.NONE) {
            return List.of();
        }
        if (face == PortMode.PUSH) {
            return "out".equals(flow) ? List.of() : List.of(PortMode.PUSH);
        }
        if (face == PortMode.PULL) {
            return "in".equals(flow) ? List.of() : List.of(PortMode.PULL);
        }
        List<PortMode> modes = new ArrayList<>(2);
        if (!"out".equals(flow)) {
            modes.add(PortMode.PUSH);
        }
        if (!"in".equals(flow)) {
            modes.add(PortMode.PULL);
        }
        return modes;
    }

    private static String endpointId(String owner, String port, PortMode mode) {
        return owner + ':' + port + (mode == PortMode.PULL ? ":pull" : ":push");
    }

    static String machineHeatKey(int x, int y, int z) {
        return "m:" + x + ',' + y + ',' + z;
    }

    static String multiblockHeatKey(String hostKey) {
        return "mb:" + hostKey;
    }

    static int[] offset(Direction direction) {
        return switch (direction) {
            case DOWN -> new int[] {0, -1, 0};
            case UP -> new int[] {0, 1, 0};
            case NORTH -> new int[] {0, 0, -1};
            case SOUTH -> new int[] {0, 0, 1};
            case WEST -> new int[] {-1, 0, 0};
            case EAST -> new int[] {1, 0, 0};
        };
    }

    // ────────────────────────────── 读给玩家/命令看

    /** 玩家主动读取时只显示网络规模和实际传输情况。 */
    public synchronized String playerSummary(TransmitterBlockEntityController controller) {
        String title = I18n.transmitterName(controller.def().blockId());
        if (controller.heat()) {
            return I18n.tr("transmitter.player.heat", title,
                    String.format(Locale.ROOT, "%.1f", controller.heatNode().temperatureK()),
                    this.heatPartners.getOrDefault(controller.key(), Set.of()).size());
        }
        if (controller.itemPipe()) {
            var network = this.items.networkOf(controller.key());
            if (network == null) return I18n.tr("transmitter.player.no_network", title);
            var report = network.report();
            return I18n.tr("transmitter.player.item", title, report.nodeCount(),
                    I18n.tr(report.active() ? "transmitter.connected" : "transmitter.disconnected"),
                    report.stored(), report.movedPerTick());
        }
        var network = controller.topology().networkOf(controller.key());
        if (network == null) return I18n.tr("transmitter.player.no_network", title);
        var report = network.report();
        if (controller.isEnergy()) {
            Grid grid = this.grids.get(this.energy.graph().componentOf(controller.key()).orElse(""));
            var power = grid == null ? null : grid.grid().lastReport();
            String supply = power == null ? I18n.tr("transmitter.player.no_power")
                    : I18n.tr("transmitter.player.supply", power.delivered(), power.demanded());
            return I18n.tr("transmitter.player.energy", title, report.nodeCount(),
                    I18n.tr(report.active() ? "transmitter.connected" : "transmitter.redstone_off"),
                    report.stored(), report.capacity(), supply);
        }
        String unit = controller.family().resource().unit();
        return I18n.tr("transmitter.player.resource", title, report.nodeCount(),
                I18n.tr(report.active() ? "transmitter.connected" : "transmitter.redstone_off"),
                report.stored(), report.capacity(), unit, report.movedPerTick());
    }

    /** 一根元件的现状：节点、六个面的模式、它这一侧的端点、所在网络（网络读取器与 `/mek pipe` 读它）。 */
    public synchronized String describe(TransmitterBlockEntityController controller) {
        StringBuilder sb = new StringBuilder(I18n.tr("transmitter.describe.header",
                controller.def().blockId(), I18n.transmitterName(controller.def().blockId()),
                controller.x(), controller.y(), controller.z()));
        sb.append('\n').append(I18n.tr("transmitter.describe.sides"));
        for (Direction direction : Direction.values()) {
            var mode = controller.port(direction);
            sb.append(direction.name()).append('=').append(I18n.enumName(mode, mode.zhName())).append(' ');
        }
        // 手臂是纯外观，但仍要能 headless 对账：半边是端点方向、半边是手臂时会在这里露出来。
        sb.append('\n').append(I18n.tr("transmitter.describe.arms"));
        for (Direction direction : Direction.values()) {
            String part = controller.armPart(direction);
            sb.append(direction.name()).append('=').append(part == null ? "-" : part).append(' ');
        }
        sb.append("\n  ").append(controller.nodeSummary());
        sb.append("\n  ").append(controller.visual().describe());
        if (controller.itemPipe()) {
            Map<String, ItemEndpoint> bindings = this.itemEndpoints.getOrDefault(controller.key(), Map.of());
            sb.append('\n').append(I18n.tr("transmitter.describe.endpoints", bindings.size()));
            for (ItemEndpoint endpoint : bindings.values()) {
                sb.append("\n    - ").append(endpoint.id()).append('[')
                        .append(I18n.enumName(endpoint.mode(), endpoint.mode().zhName()))
                        .append("] ").append(endpoint.handler().describe());
            }
            mekcore.item.ItemNetwork network = this.items.networkOf(controller.key());
            sb.append('\n').append("  ")
                    .append(network == null ? I18n.tr("transmitter.describe.no_network")
                            : itemReport(network.report()));
            return sb.toString();
        }
        if (controller.heat()) {
            sb.append('\n').append(I18n.tr("transmitter.describe.heat_links",
                    this.heatPartners.getOrDefault(controller.key(), Set.of())));
            sb.append("\n  ").append(this.heat.describe());
            return sb.toString();
        }
        Map<String, NetworkEndpoint> bindings = this.resourceEndpoints.getOrDefault(controller.key(), Map.of());
        sb.append('\n').append(I18n.tr("transmitter.describe.endpoints", bindings.size()));
        for (NetworkEndpoint endpoint : bindings.values()) {
            sb.append("\n    - ").append(endpoint.id()).append('[')
                    .append(I18n.enumName(endpoint.mode(), endpoint.mode().zhName()))
                    .append("] ").append(endpoint.handler().describe());
        }
        TransmitterNetwork network = controller.topology().networkOf(controller.key());
        sb.append('\n').append("  ").append(network == null
                ? I18n.tr("transmitter.describe.no_network")
                : networkReport(network.report(), controller.family().resource()));
        if (controller.isEnergy() && network != null) {
            Grid grid = this.grids.get(this.energy.graph().componentOf(controller.key()).orElse(""));
            EnergyGrid energy = grid == null ? null : grid.grid();
            if (energy != null && energy.lastReport() != null) {
                sb.append('\n').append(I18n.tr("transmitter.describe.grid", energy.lastReport()));
            }
        }
        return sb.toString();
    }

    /** 自检/汇总几行。 */
    public synchronized String summary() {
        StringBuilder sb = new StringBuilder(I18n.tr("transmitter.summary.head",
                this.world.getName(), this.placed.size(), (int) this.resourceEndpoints.values().stream()
                        .filter(map -> !map.isEmpty()).count()));
        sb.append(I18n.tr("transmitter.summary.body",
                (int) this.itemEndpoints.values().stream().filter(map -> !map.isEmpty()).count(),
                this.heatPartners.size(), this.resources.networkIds().size(),
                this.energy.networkIds().size(), this.items.networkIds().size(),
                this.grids.size(), this.redstone.size()));
        for (Grid grid : this.grids.values()) {
            if (grid.grid().lastReport() != null) {
                sb.append("\n  ").append(grid.grid().lastReport());
            }
        }
        return sb.toString();
    }

    private static String networkReport(NetworkReport report, ResourceType type) {
        StringBuilder sb = new StringBuilder(I18n.tr("transmitter.report.head",
                I18n.resourceType(type), report.nodeCount(), channelText(report.channel()),
                I18n.tr(report.active() ? "transmitter.connected" : "transmitter.redstone_off"),
                report.transferRatePerTick(), report.extractRatePerTick(),
                report.stored(), report.capacity(), report.movedPerTick()));
        for (NetworkReport.EndpointReport endpoint : report.endpoints()) {
            sb.append('\n').append("  - ").append(endpoint.id()).append('[')
                    .append(I18n.enumName(endpoint.mode(), endpoint.mode().zhName())).append("] ")
                    .append(endpoint.resourceId() == null ? ""
                            : endpoint.resourceId() + " ")
                    .append(endpoint.amount()).append('/').append(endpoint.capacity());
        }
        return sb.toString();
    }

    private static String itemReport(mekcore.item.ItemNetworkReport report) {
        StringBuilder sb = new StringBuilder(I18n.tr("transmitter.item_report.head",
                report.nodeCount(), I18n.tr(report.active() ? "transmitter.connected" : "transmitter.disconnected"),
                String.format(Locale.ROOT, "%.2f", report.itemsPerSecond()),
                String.format(Locale.ROOT, "%.2f", report.extractPerSecond()),
                report.stored(), report.movedPerTick()));
        for (mekcore.item.ItemNetworkReport.EndpointReport endpoint : report.endpoints()) {
            sb.append('\n').append("  - ").append(endpoint.id()).append('[')
                    .append(I18n.enumName(endpoint.mode(), endpoint.mode().zhName())).append("] ")
                    .append(endpoint.amount()).append('/').append(endpoint.capacity());
        }
        return sb.toString();
    }

    private static String channelText(int channel) {
        return channel < 0 ? I18n.tr("transmitter.channel.unset")
                : I18n.tr("transmitter.channel.number", channel + 1);
    }
}
