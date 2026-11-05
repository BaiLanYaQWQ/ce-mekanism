package net.kuzuanpa.cemekanism.machine;

import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.kuzuanpa.cemekanism.i18n.I18n;
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * 机器放置时写 owner（文档 04 第四节的"安全"）：
 * 方块实体不一定在 BlockPlaceEvent 当刻建好，所以当刻先试一次，没拿到就下一趟再试。
 * 已有 owner 的方块（读档回来的）不覆盖。
 */
public final class MachineSecurityListener implements Listener {

    private final CeMekanismPlugin plugin;

    public MachineSecurityListener(CeMekanismPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        ImmutableBlockState state = CraftEngineBlocks.getCustomBlockState(block);
        // CE 可能在本事件之后才把自定义状态写上去；state 为空时留到下一趟再认。
        if (state != null && !MachineBlocks.isMachine(state.owner().value().id().asString())) {
            return;
        }
        String owner = event.getPlayer().getUniqueId().toString();
        if (!claim(block, owner)) {
            Bukkit.getScheduler().runTask(this.plugin,
                    () -> claim(block, owner));
        }
    }

    private boolean claim(Block block, String owner) {
        MachineBlockEntityController controller = this.plugin.machineAtQuiet(
                block.getWorld(), block.getX(), block.getY(), block.getZ());
        if (controller == null) {
            return false;
        }
        if (controller.claim(owner)) {
            return true;
        }
        this.plugin.getLogger().info(I18n.tr("machine.owner_exists",
                controller.taskId(), controller.machine().owner(), owner));
        return true;
    }
}
