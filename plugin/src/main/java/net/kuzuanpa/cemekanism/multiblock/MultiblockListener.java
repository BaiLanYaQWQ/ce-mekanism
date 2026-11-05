package net.kuzuanpa.cemekanism.multiblock;

import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.kuzuanpa.cemekanism.platform.BukkitItems;
import net.kuzuanpa.cemekanism.gui.MultiblockGui;
import net.kuzuanpa.cemekanism.multiblock.logic.FusionReactorLogic;
import net.kuzuanpa.cemekanism.multiblock.logic.HohlraumItems;
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.bukkit.api.event.CustomBlockPlaceEvent;
import net.momirealms.craftengine.bukkit.api.event.CustomBlockBreakEvent;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * Bukkit 与 CE 的结构变更通知共享宿主；同一刻的多次编辑合并到下一主线程刻检查。
 */
public final class MultiblockListener implements Listener {

    private final CeMekanismPlugin plugin;

    public MultiblockListener(CeMekanismPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        this.plugin.multiblocks().markDirtyAt(event.getBlock().getWorld(), event.getBlock().getX(),
                event.getBlock().getY(), event.getBlock().getZ());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        this.plugin.multiblocks().markDirtyAt(event.getBlock().getWorld(), event.getBlock().getX(),
                event.getBlock().getY(), event.getBlock().getZ());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCustomPlace(CustomBlockPlaceEvent event) {
        changed(event.bukkitBlock());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCustomBreak(CustomBlockBreakEvent event) {
        changed(event.bukkitBlock());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockExplode(org.bukkit.event.block.BlockExplodeEvent event) {
        event.blockList().forEach(this::changed);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityExplode(org.bukkit.event.entity.EntityExplodeEvent event) {
        event.blockList().forEach(this::changed);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChunkUnload(org.bukkit.event.world.ChunkUnloadEvent event) {
        this.plugin.multiblocks().hideEffectsInChunk(event.getWorld(), event.getChunk().getX(), event.getChunk().getZ());
    }

    private void changed(Block block) {
        this.plugin.multiblocks().markDirtyAt(block.getWorld(), block.getX(), block.getY(), block.getZ());
    }

    /**
     * 涡轮叶片（上游 TileEntityTurbineRotor.addBlade/removeBlade）：每根转子最多 2 片。
     * 右击装、空手或潜行右击拆；拆下的叶片还给玩家，放不下就掉在脚边。
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onTurbineRotor(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        ImmutableBlockState state = CraftEngineBlocks.getCustomBlockState(block);
        if (state == null || !"mek:turbine_rotor".equals(state.owner().value().id().asString())) {
            return;
        }
        MultiblockHosts.Host host = this.plugin.multiblocks().at(block.getWorld(), block.getX(),
                block.getY(), block.getZ());
        if (host == null || !"turbine".equals(host.def().family())) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack held = event.getItem();
        boolean blade = held != null && "mek:turbine_blade".equals(BukkitItems.idOf(held));
        long rotors = host.instance().shape().roleCount(MultiblockBlocks.role("rotor"));
        long maximum = Math.max(0, rotors * 2);
        if (blade && !player.isSneaking()) {
            if (host.blades() >= maximum) {
                return;
            }
            held.setAmount(held.getAmount() - 1);
            host.setBlades(host.blades() + 1);
        } else {
            if (host.blades() <= 0) {
                return;
            }
            host.setBlades(host.blades() - 1);
            ItemStack drop = BukkitItems.create("mek:turbine_blade");
            if (drop != null && (!player.getInventory().addItem(drop).isEmpty())) {
                player.getWorld().dropItemNaturally(player.getLocation(), drop);
            }
        }
        host.markChanged();
        event.setCancelled(true);
    }

    /** 配置器右击端口：循环该方块的资源模式与热交换模式。 */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPortMode(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        ItemStack held = event.getItem();
        if (held == null || !"mek:configurator".equals(BukkitItems.idOf(held))) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        MultiblockHosts.Host host = this.plugin.multiblocks().at(block.getWorld(), block.getX(),
                block.getY(), block.getZ());
        if (host == null || !host.formed()) {
            return;
        }
        String next = host.cyclePortMode(block.getX(), block.getY(), block.getZ());
        if (next.isEmpty()) {
            return;
        }
        event.setCancelled(true);
        String label = switch (next) {
            case "HEAT" -> "热量交换（双向）";
            case "RESOURCE" -> "资源输入 / 输出";
            case "INPUT" -> "输入";
            case "OUTPUT" -> "输出";
            default -> next;
        };
        event.getPlayer().sendActionBar(net.kyori.adventure.text.Component.text("端口：" + label));
        host.markChanged();
    }

    /** 聚变控制器的黑体辐射腔槽；创造模式右击聚焦矩阵可像上游一样直接注入 1 GK 调试。 */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onFusionInteraction(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        MultiblockHosts.Host host = this.plugin.multiblocks().at(block.getWorld(), block.getX(),
                block.getY(), block.getZ());
        if (host == null || !host.formed() || !"fusion_reactor".equals(host.def().family())
                || !(host.instance().blueprint().logic() instanceof FusionReactorLogic fusion)) {
            return;
        }
        String role = host.box().roleAt(block.getX() - host.box().originX(),
                block.getY() - host.box().originY(), block.getZ() - host.box().originZ()).id();
        Player player = event.getPlayer();
        if ("laser_focus".equals(role) && player.getGameMode() == org.bukkit.GameMode.CREATIVE) {
            fusion.setPlasmaTemperature(host.instance(), 1_000_000_000.0);
            host.markChanged();
            player.sendMessage("聚变等离子体温度 → 1 GK（创造模式调试）");
            event.setCancelled(true);
            return;
        }
        if (!"controller".equals(role)) {
            return;
        }
        ItemStack held = event.getItem();
        if (HohlraumItems.isHohlraum(held)) {
            if (fusion.insertHohlraum(host.instance(), held)) {
                held.setAmount(held.getAmount() - 1);
                host.markChanged();
                player.sendMessage("已装入黑体辐射腔");
            } else {
                player.sendMessage("控制器里已经有一个黑体辐射腔");
            }
            event.setCancelled(true);
        } else if (player.isSneaking() && (held == null || held.getType().isAir())) {
            ItemStack removed = fusion.takeHohlraum(host.instance());
            if (removed != null) {
                for (ItemStack leftover : player.getInventory().addItem(removed).values()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), leftover);
                }
                host.markChanged();
                player.sendMessage("已取回黑体辐射腔");
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onGuiClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof MultiblockBlockEntityController.GuiHolder holder)) {
            return;
        }
        // 包括背包 Shift、双击收集、数字键交换、丢弃和创造复制；任何原版路径都不能碰视图物品。
        event.setCancelled(true);
        int raw = event.getRawSlot();
        if (raw < 0 || raw >= event.getInventory().getSize()
                || !(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        var click = event.getClick();
        ItemStack expectedCursor = player.getItemOnCursor().clone();
        int expectedPage = holder.page();
        // 容器事件完成后才改光标/视图；关闭、切窗或结构失效会让排队的点击直接作废。
        org.bukkit.Bukkit.getScheduler().runTask(this.plugin, () -> {
            MultiblockHosts.Host host = holder.host();
            if (player.getOpenInventory().getTopInventory().getHolder() != holder
                    || holder.controller().host() != host || !host.formed()
                    || holder.page() != expectedPage || !expectedCursor.equals(player.getItemOnCursor())) {
                return;
            }
            if (raw == net.kuzuanpa.cemekanism.gui.MultiblockGui.RECIPES_SLOT
                    && (click == org.bukkit.event.inventory.ClickType.LEFT || click == org.bukkit.event.inventory.ClickType.RIGHT)) {
                holder.controller().openRecipeBrowser(player);
                return;
            }
            holder.page(MultiblockGui.click(host, holder.page(), raw, click, player));
            MultiblockGui.updateAnchors(host, holder.getInventory(), holder.page());
        });
    }

    @EventHandler
    public void onGuiClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof MultiblockBlockEntityController.GuiHolder holder) {
            this.plugin.guiSessions().forget(event.getPlayer().getUniqueId(), holder);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onGuiDrag(InventoryDragEvent event) {
        if (!(event.getInventory().getHolder() instanceof MultiblockBlockEntityController.GuiHolder)) {
            return;
        }
        for (int raw : event.getRawSlots()) {
            if (raw < event.getInventory().getSize()) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
