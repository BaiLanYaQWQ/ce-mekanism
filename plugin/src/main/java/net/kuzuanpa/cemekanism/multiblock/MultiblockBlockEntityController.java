package net.kuzuanpa.cemekanism.multiblock;

import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.kuzuanpa.cemekanism.gui.GuiSessions;
import net.kuzuanpa.cemekanism.gui.GuiLayout;
import net.kuzuanpa.cemekanism.gui.MultiblockGui;
import net.kuzuanpa.cemekanism.platform.CompoundDataTag;
import net.momirealms.craftengine.bukkit.plugin.gui.BukkitInventory;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.libraries.nbt.CompoundTag;
import org.bukkit.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.List;

/**
 * 锚点方块实体：只做四件事——认领/释放自己那座结构的 host、存档"尺寸 + 原点"、把端口存量接回来、
 * 右键报状态（成型与否、缺什么、端口位置）。逐刻逻辑全在核心里（文档 05 第四节）。
 */
public final class MultiblockBlockEntityController extends BlockEntityController {

    private static final String ROOT_KEY = "cemekanism:multiblock";
    private static final String SIZE_KEY = "size";
    private static final String STATE_KEY = "state";

    private final CeMekanismPlugin plugin;
    private final String blockId;
    private MultiblockHosts.Host host;
    private boolean owner;
    /** 读档先收下：CE 调 loadCustomData 时 world 还是 null，成型要等 onLoad。 */
    private int[] recordedSize;
    private CompoundTag pendingState;
    private boolean loaded;
    private final java.util.List<net.kuzuanpa.cemekanism.machine.RecipeBrowserGui> recipeBrowsers = new java.util.ArrayList<>();
    private final java.util.Set<GuiHolder> guiHolders =
            java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());

    public MultiblockBlockEntityController(BlockEntity blockEntity, CeMekanismPlugin plugin) {
        super(blockEntity);
        this.plugin = plugin;
        this.blockId = blockEntity.blockState().owner().value().id().asString();
    }

    public String blockId() {
        return this.blockId;
    }

    public CeMekanismPlugin plugin() { return this.plugin; }

    public void openRecipeBrowser(org.bukkit.entity.Player player) {
        if (this.host == null || !this.host.formed()) return;
        var gui = net.kuzuanpa.cemekanism.machine.RecipeBrowserGui.create(this);
        this.recipeBrowsers.add(gui);
        gui.open(player);
    }

    public void forgetRecipeBrowser(net.kuzuanpa.cemekanism.machine.RecipeBrowserGui gui) {
        this.recipeBrowsers.remove(gui);
    }

    public MultiblockHosts.Host host() {
        return this.host;
    }

    /** 右键：把"缺什么"念给玩家（配置器右击走同一条路，见 05 第四节）。 */
    public void report(Player player) {
        if (this.host == null || !this.host.formed() || this.blockId.equals("mek:teleporter")) {
            form();
        }
        MultiblockHosts.Host current = this.host;
        if (!(player.platformPlayer() instanceof org.bukkit.entity.Player bukkit)) {
            return;
        }
        if (current == null) {
            bukkit.sendActionBar(net.kyori.adventure.text.Component.text("请点击多方块控制器或阀门"));
            return;
        }
        if (current.formed()) {
            openGui(bukkit);
            return;
        }
        MultiblockDiagnosticParticles.show(current, bukkit);
    }

    /** 成型后右键开只读面板；没成型仍然把缺什么念出来。 */
    public boolean openFor(Player player) {
        if (this.host == null) {
            form();
        }
        if (this.host == null || !this.host.formed()) {
            report(player);
            return false;
        }
        if (!(player.platformPlayer() instanceof org.bukkit.entity.Player bukkit)) {
            return false;
        }
        openGui(bukkit);
        return true;
    }

    private void openGui(org.bukkit.entity.Player player) {
        MultiblockHosts.Host current = this.host;
        if (current == null || !current.formed()) {
            return;
        }
        GuiHolder holder = new GuiHolder(this, current);
        Inventory inventory = Bukkit.createInventory(holder, GuiLayout.multiblock().rows() * 9);
        holder.inventory(inventory);
        MultiblockGui.updateAnchors(current, inventory);
        net.momirealms.craftengine.core.entity.player.Player cePlayer =
                net.momirealms.craftengine.bukkit.api.BukkitAdaptor.adapt(player);
        if (cePlayer == null) {
            return;
        }
        MultiblockDiagnosticParticles.clear(player);
        this.guiHolders.add(holder);
        new BukkitInventory(inventory).open(cePlayer,
                net.kuzuanpa.cemekanism.gui.GuiComponents.toCe(MultiblockGui.title(current), cePlayer));
        Location at = new Location(world(), this.blockEntity.pos.x() + 0.5,
                this.blockEntity.pos.y() + 0.5, this.blockEntity.pos.z() + 0.5);
        this.plugin.guiSessions().open(player, new GuiSessions.View(holder, inventory, at,
                () -> MultiblockGui.title(current, holder.page()),
                () -> MultiblockGui.updateAnchors(current, inventory, holder.page()),
                () -> Integer.toHexString(java.util.Arrays.hashCode(inventory.getContents())),
                () -> this.guiHolders.remove(holder),
                () -> this.host == current && current.formed()));
    }

    private void closeGui() {
        for (var browser : java.util.List.copyOf(this.recipeBrowsers)) browser.closeViewers();
        this.recipeBrowsers.clear();
        for (GuiHolder holder : new java.util.ArrayList<>(this.guiHolders)) {
            this.plugin.guiSessions().close(holder);
        }
        this.guiHolders.clear();
    }

    /** 只读面板的容器标记：点击全拦，免得空槽位被玩家塞东西。 */
    public static final class GuiHolder implements InventoryHolder {
        private final MultiblockBlockEntityController controller;
        private final MultiblockHosts.Host host;
        private Inventory inventory;
        private int page;

        GuiHolder(MultiblockBlockEntityController controller, MultiblockHosts.Host host) {
            this.controller = controller;
            this.host = host;
        }

        public MultiblockBlockEntityController controller() {
            return this.controller;
        }

        public int page() {
            return this.page;
        }

        public MultiblockHosts.Host host() {
            return this.host;
        }

        public void page(int page) {
            this.page = page;
        }

        void inventory(Inventory inventory) {
            this.inventory = inventory;
        }

        @Override
        public Inventory getInventory() {
            return this.inventory;
        }
    }

    /** 成型（右键、`/mek structure form`、`/mek build` 都走这里）：认领盒子并校验一遍。 */
    public MultiblockHosts.Host form() {
        World world = world();
        if (world == null || !this.loaded) {
            return null;
        }
        int[] recorded = this.recordedSize != null ? this.recordedSize
                : this.owner && this.host != null
                ? new int[] {this.host.box().sizeX(), this.host.box().sizeY(), this.host.box().sizeZ()}
                : null;
        MultiblockHosts.Host fallback = null;
        for (MultiblockDef def : MultiblockBlocks.anchorsOf(this.blockId)) {
            MultiblockHosts.Host found = this.plugin.multiblocks().acquire(world,
                    this.blockEntity.pos.x(), this.blockEntity.pos.y(), this.blockEntity.pos.z(), def,
                    recorded, this::markChanged);
            if (found.formed()) {
                this.plugin.multiblocks().release(fallback);
                return adopt(found);
            }
            if (fallback == null) {
                fallback = found;                   // 本次搜索的第一个候选用于预览
            } else {
                this.plugin.multiblocks().release(found);
            }
        }
        return fallback == null ? this.host : adopt(fallback);
    }

    private MultiblockHosts.Host adopt(MultiblockHosts.Host found) {
        // acquire 已增加一份引用；即使仍未成型，也要采用本次的原点和尺寸。
        // 同一宿主重试时释放的是新增引用，不丢掉控制器原本持有的那一份。
        this.plugin.multiblocks().release(this.host);
        this.host = found;
        return found;
    }

    public String describe() {
        return this.host == null ? this.blockId + "（还没成型过）" : this.host.describe();
    }

    private static String ports(MultiblockHosts.Host host) {
        StringBuilder sb = new StringBuilder();
        for (String name : host.def().ports()) {
            sb.append(name).append('=').append(host.instance().tank(name).describe()).append(' ');
        }
        return sb.toString().trim();
    }

    public void markChanged() {
        if (this.blockEntity.world != null) {
            this.blockEntity.world.blockEntityChanged(this.blockEntity.pos);
        }
    }

    @Override
    public void onLoad() {
        this.loaded = true;
        if (this.pendingState != null) {
            // Neighbouring chunks may not have finished CE block activation yet. Keep the original
            // snapshot until a full valid structure with the saved dimensions can accept it.
            Bukkit.getScheduler().runTaskLater(this.plugin, this::restorePending, 1L);
            Bukkit.getScheduler().runTaskLater(this.plugin, this::restorePending, 20L);
            Bukkit.getScheduler().runTaskLater(this.plugin, this::restorePending, 100L);
        } else {
            MultiblockHosts.Host current = form();
            if (current != null && current.formed()) this.recordedSize = null;
            else {
                Bukkit.getScheduler().runTaskLater(this.plugin, this::retryForm, 1L);
                Bukkit.getScheduler().runTaskLater(this.plugin, this::retryForm, 20L);
            }
        }
    }

    private void restorePending() {
        if (!this.loaded || world() == null || this.pendingState == null) return;
        MultiblockHosts.Host current = form();
        boolean teleporter = this.blockId.equals("mek:teleporter");
        if (current == null || !current.formed() && !teleporter || !isPrimary(current)) return;
        if (!teleporter && this.recordedSize != null && (current.box().sizeX() != this.recordedSize[0]
                || current.box().sizeY() != this.recordedSize[1] || current.box().sizeZ() != this.recordedSize[2])) return;
        // refresh has already established the real shape capacities. Logic-dependent capacity is
        // restored before contents by MultiblockInstance.load; rebind restored per-valve modes too.
        this.plugin.logistics(world()).detachMultiblock(current);
        current.load(new CompoundDataTag(this.pendingState));
        current.refresh();
        this.plugin.logistics(world()).attachMultiblock(current);
        this.pendingState = null;
        this.recordedSize = null;
        this.owner = true;
        markChanged();
    }

    private void retryForm() {
        if (!this.loaded) return;
        if (this.pendingState != null) { restorePending(); return; }
        if (world() == null || this.host != null && this.host.formed()) return;
        MultiblockHosts.Host current = form();
        if (current != null && current.formed() && isPrimary(current)) markChanged();
    }

    @Override
    public void onUnload() {
        this.loaded = false;
        closeGui();
        if (this.host != null) {
            // 区块卸载后同一个控制器可能被重新激活（loadCustomData 不会再来一次）：
            // 先把端口/温度/叶片快照下来，加载时再接回。
            if (this.pendingState == null && isPrimary(this.host)) {
                CompoundTag snapshot = new CompoundTag();
                this.host.save(new CompoundDataTag(snapshot));
                this.pendingState = snapshot;
            }
            if (this.recordedSize == null) this.recordedSize = new int[] {this.host.box().sizeX(), this.host.box().sizeY(),
                    this.host.box().sizeZ()};
            this.plugin.multiblocks().release(this.host);
            this.host = null;
        }
    }

    @Override
    public void onRemove() {
        closeGui();
        onUnload();
    }

    @Override
    public void saveCustomData(CompoundTag root) {
        MultiblockHosts.Host current = this.host;
        if (current == null || this.pendingState != null) {
            if (this.recordedSize != null) {
                CompoundTag mine = new CompoundTag();
                CompoundTag size = new CompoundTag();
                size.putInt("x", this.recordedSize[0]);
                size.putInt("y", this.recordedSize[1]);
                size.putInt("z", this.recordedSize[2]);
                mine.put(SIZE_KEY, size);
                if (this.pendingState != null) {
                    mine.put(STATE_KEY, this.pendingState);
                }
                root.put(ROOT_KEY, mine);
            }
            return;
        }
        CompoundTag mine = new CompoundTag();
        CompoundTag size = new CompoundTag();
        size.putInt("x", current.box().sizeX());
        size.putInt("y", current.box().sizeY());
        size.putInt("z", current.box().sizeZ());
        mine.put(SIZE_KEY, size);
        if (isPrimary(current)) {
            // 只有盒子的最小角锚点写共享状态；同一座结构的其它阀门只写尺寸，避免用影子盒子覆盖水量
            CompoundTag state = new CompoundTag();
            current.save(new CompoundDataTag(state));
            mine.put(STATE_KEY, state);
        }
        root.put(ROOT_KEY, mine);
    }

    private boolean isPrimary(MultiblockHosts.Host host) {
        if (this.blockId.equals("mek:teleporter")) return host != null;
        return host != null
                && this.blockEntity.pos.x() == host.box().originX() + host.def().anchorOffsetX()
                && this.blockEntity.pos.y() == host.box().originY() + host.def().anchorOffsetY()
                && this.blockEntity.pos.z() == host.box().originZ() + host.def().anchorOffsetZ();
    }

    @Override
    public void loadCustomData(CompoundTag root) {
        CompoundTag mine = root.getCompound(ROOT_KEY);
        if (mine == null) {
            return;                                  // 刚放下的锚点：还没有记录
        }
        CompoundTag size = mine.getCompound(SIZE_KEY);
        if (size != null) {
            this.recordedSize = new int[] {size.getInt("x", 0), size.getInt("y", 0), size.getInt("z", 0)};
        }
        CompoundTag state = mine.getCompound(STATE_KEY);
        if (state != null) {
            this.pendingState = state;
        }
    }

    /** 世界侧的量法：读档后先还原方块再 validate（结构本身不进存档）。 */
    private World world() {
        return this.blockEntity.world == null
                ? null : (World) this.blockEntity.world.world().platformWorld();
    }

    @Override
    public String toString() {
        return "锚点 " + this.blockId + " @ " + this.blockEntity.pos.x() + ',' + this.blockEntity.pos.y()
                + ',' + this.blockEntity.pos.z();
    }

    /** 结构族候选（自检用）。 */
    public List<MultiblockDef> candidates() {
        return MultiblockBlocks.anchorsOf(this.blockId);
    }
}
