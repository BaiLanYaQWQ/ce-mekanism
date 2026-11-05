package net.kuzuanpa.cemekanism.machine;

import mekcore.machine.Direction;
import mekcore.machine.Machine;
import mekcore.machine.SecurityLevel;
import net.kuzuanpa.cemekanism.i18n.I18n;
import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.kuzuanpa.cemekanism.platform.BukkitItems;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * 配置器：潜行右击空气切换功能；旋转模式把机器正面指向选中面，其余模式轮换对应资源面状态。
 * GT 九宫格选面：中央选当前面，边条选邻面，四角选背面；手持预览由 ConfiguratorOverlay 绘制。
 * 全部落在核心的侧面/档位 API 上，这里只负责"点的是哪台机器、哪个面"。
 * 机器默认每个端口在每个面都是关的，所以配置器是让管道动起来的必要一步。
 */
public final class ConfiguratorListener implements Listener {

    private static final String CONFIGURATOR = "mek:configurator";

    private final CeMekanismPlugin plugin;

    public ConfiguratorListener(CeMekanismPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        // Paper may predict an air click as block-DENY; an explicit item denial still wins.
        boolean air = event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_AIR;
        if (event.isCancelled() && (!air || event.useItemInHand() == org.bukkit.event.Event.Result.DENY)) return;
        boolean leftClick = event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR;
        if (!leftClick && event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }
        // 左键在部分服务端实现里 getHand() 为 null；右键只认主手，避免副手重复触发。
        if (event.getHand() != null && event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        ItemStack hand = event.getItem();
        if (hand == null || !CONFIGURATOR.equals(BukkitItems.idOf(hand))) {
            return;
        }
        Player player = event.getPlayer();
        ConfiguratorMode mode = ConfiguratorMode.of(hand);
        var hit = ConfiguratorRay.trace(player);
        // Custom blocks may also arrive as AIR clicks; a real ray target still configures its selected face.
        if (!leftClick && event.getAction() == Action.RIGHT_CLICK_AIR && player.isSneaking() && hit == null) {
            event.setCancelled(true);
            ConfiguratorMode next = mode.next();
            next.store(hand);
            player.getInventory().setItemInMainHand(hand);
            player.sendActionBar(next.actionBar(player, null, null));
            return;
        }
        if (hit == null) {
            return;
        }
        Block block = hit.block();
        MachineBlockEntityController controller = this.plugin.machineAtQuiet(block.getWorld(),
                block.getX(), block.getY(), block.getZ());
        if (controller == null) {
            return;
        }
        event.setCancelled(true);
        Machine machine = controller.machine();
        if (machine.owner() == null || machine.owner().isEmpty()) {
            machine.setOwner(player.getUniqueId().toString());
            controller.markChanged();
        }
        if (!machine.canAccess(player.getUniqueId().toString())) {
            I18n.send(player, "machine.not_owner", I18n.enumName(machine.security(), machine.security().zhName()));
            return;
        }
        if (leftClick) {
            if (!isOwnerOrAdmin(player, machine)) {
                I18n.send(player, "machine.owner_only_security");
                return;
            }
            SecurityLevel next = nextSecurity(machine.security());
            machine.setSecurity(next);
            controller.markChanged();
            I18n.send(player, "machine.security_changed",
                    I18n.machineName(I18n.locale(player), machine.blueprint()),
                    I18n.enumName(next, next.zhName()));
            return;
        }
        Direction face = hit.selected();
        boolean changed = mode.cycle(controller, face);
            player.sendActionBar(mode.actionBar(player, face, mode.status(controller, face)));
        if (!changed) return;
        controller.markChanged();
        // 面配置变了：挨着的管道要重算端点方向（文档 06 第十节第 3 条）
        this.plugin.logistics(block.getWorld()).refreshAround(block.getX(), block.getY(), block.getZ());
    }

    private static SecurityLevel nextSecurity(SecurityLevel current) {
        SecurityLevel[] levels = SecurityLevel.values();
        return levels[(current.ordinal() + 1) % levels.length];
    }

    private static boolean isOwnerOrAdmin(Player player, Machine machine) {
        String owner = machine.owner();
        return owner == null || owner.isEmpty() || owner.equals(player.getUniqueId().toString())
                || player.hasPermission("cemekanism.admin");
    }
}
