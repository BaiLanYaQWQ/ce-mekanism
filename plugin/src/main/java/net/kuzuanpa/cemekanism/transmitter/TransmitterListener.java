package net.kuzuanpa.cemekanism.transmitter;

import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.kuzuanpa.cemekanism.platform.BukkitItems;
import net.kuzuanpa.cemekanism.machine.ConfiguratorRay;
import net.kuzuanpa.cemekanism.machine.ConfiguratorMode;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * 世界侧的"谁挨着谁"（文档 06 第二节）：放置/破坏/爆炸都只是"把这一格和六个邻居重算一遍"，
 * 结构状态、分网、端点归属都不在这里算——那是核心的事。
 *
 * <p>区块加载/卸载不走事件：CE 会调方块实体的 `onLoad` / `onUnload`（装载是按需的，事件里反而拿不到实体）。
 */
public final class TransmitterListener implements Listener {

    private static final String CONFIGURATOR = "mek:configurator";
    private static final String NETWORK_READER = "mek:network_reader";

    private final CeMekanismPlugin plugin;

    public TransmitterListener(CeMekanismPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        refreshAround(event.getBlock());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        refreshAround(event.getBlock());
        // 事件触发时方块还没真正消失；下一拍再算一次，保证邻居手臂和端点都按"已拆除"更新。
        Block block = event.getBlock();
        Bukkit.getScheduler().runTask(this.plugin, () -> refreshAround(block));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().forEach(this::refreshAround);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().forEach(this::refreshAround);
    }

    private void refreshAround(Block block) {
        this.plugin.logistics(block.getWorld()).refreshAround(block.getX(), block.getY(), block.getZ());
    }

    /** 配置器修改面模式；网络读取器主动查询摘要。 */
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.isCancelled() && (event.getAction() != Action.RIGHT_CLICK_AIR
                || event.useItemInHand() == org.bukkit.event.Event.Result.DENY)) return;
        if ((event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR)
                || event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack hand = event.getItem();
        String item = hand == null ? "" : BukkitItems.idOf(hand);
        var hit = CONFIGURATOR.equals(item) ? ConfiguratorRay.trace(player) : null;
        Block block = event.getClickedBlock();
        if (CONFIGURATOR.equals(item)) {
            block = hit == null ? null : hit.block();
        }
        if (block == null) {
            return;
        }
        TransmitterBlockEntityController controller = this.plugin.transmitterAt(block.getWorld(),
                block.getX(), block.getY(), block.getZ());
        if (controller == null) {
            return;
        }
        Logistics logistics = this.plugin.logistics(block.getWorld());
        if (CONFIGURATOR.equals(item)) {
            event.setCancelled(true);                     // 别顺手把方块点了
            ConfiguratorMode mode = ConfiguratorMode.of(hand);
            if (mode == ConfiguratorMode.REDSTONE) {
                controller.toggleRedstone();
            } else if (mode.supports(controller)) {
                controller.cyclePort(hit.selected());
            }
            player.sendActionBar(mode.actionBar(player, hit.selected(), mode.status(controller, hit.selected())));
            return;
        }
        if (NETWORK_READER.equals(item)) {
            event.setCancelled(true);
            report(player, logistics, controller);
            return;
        }

    }

    private static void report(Player player, Logistics logistics, TransmitterBlockEntityController controller) {
        for (String line : logistics.playerSummary(controller).split("\n")) {
            player.sendMessage(line);
        }
    }

}
