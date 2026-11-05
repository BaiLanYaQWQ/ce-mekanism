package net.kuzuanpa.cemekanism.machine;

import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.kuzuanpa.cemekanism.platform.CraftContainers;
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.behavior.EntityBlock;
import net.momirealms.craftengine.core.block.behavior.WorldlyContainerHolder;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.entity.player.InteractionResult;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.CEWorld;
import net.momirealms.craftengine.core.world.context.UseOnContext;
import org.bukkit.World;

/**
 * 机器方块的行为（文档 03 适配层 3）：
 * 行为本身不存任何状态，只负责建控制器、把右键转给控制器开容器。
 * 资源包里的 `behavior: {type: cemekanism:machine}` 指到这里。
 *
 * 红石不走这里：CE 的 neighborChanged 只给原始 level（拿它换世界要走 proxy 内部依赖，见 10 的 P10），
 * 所以红石由控制器在自己的刻任务里按世界真值刷新（调度器每趟都问 needsTick()，不会漏）。
 */
public final class MachineBlockBehavior extends BukkitBlockBehavior implements EntityBlock, WorldlyContainerHolder {

    public static final Key KEY = Key.of("cemekanism", "machine");

    public static BlockBehaviorFactory<MachineBlockBehavior> factory(CeMekanismPlugin plugin) {
        return (block, section) -> new MachineBlockBehavior(plugin, block);
    }

    private final CeMekanismPlugin plugin;
    private int controllerId;
    private final boolean qioAdapter;

    private MachineBlockBehavior(CeMekanismPlugin plugin, BlockDefinition block) {
        super(block);
        this.plugin = plugin;
        this.qioAdapter = block.id().asString().equals("mek:qio_redstone_adapter");
    }

    @Override
    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        return new MachineBlockEntityController(blockEntity, this.plugin);
    }

    @Override
    public void initControllerId(int id) {
        this.controllerId = id;
    }

    @Override
    public InteractionResult useWithoutItem(UseOnContext context, ImmutableBlockState state) {
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        MachineBlockEntityController controller = controllerAt(
                context.getLevel().storageWorld(), context.getClickedPos());
        if (controller == null) {
            return InteractionResult.PASS;
        }
        if (player.platformPlayer() instanceof org.bukkit.entity.Player bukkit
                && net.kuzuanpa.cemekanism.storage.StorageRuntime.bucket(controller, bukkit)) return InteractionResult.SUCCESS_AND_CANCEL;
        if (player.platformPlayer() instanceof org.bukkit.entity.Player bukkit && bukkit.isSneaking()) {
            // 潜行检查仅提示异常；数值与物品在机器界面中查看。
            controller.report(bukkit);
        } else {
            controller.openFor(player);               // 拒绝时 openFor 自己发提示
        }
        return InteractionResult.SUCCESS_AND_CANCEL;  // 无论开没开都别再让手里的物品作用到方块
    }

    private MachineBlockEntityController controllerAt(CEWorld world, BlockPos pos) {
        BlockEntity entity = world.getBlockEntityAtIfLoaded(pos);
        return entity == null ? null
                : entity.controller.getAt(MachineBlockEntityController.class, this.controllerId);
    }

    @Override
    public boolean isSignalSource(Object thisBlock, Object[] args) { return this.qioAdapter; }

    @Override
    public int getSignal(Object thisBlock, Object[] args) {
        if (!this.qioAdapter) return 0;
        World world = CraftContainers.worldOfLevel(args[1]);
        if (world == null) return 0;
        BlockPos pos = LocationUtils.fromBlockPos(args[2]);
        MachineBlockEntityController c = this.plugin.machineAtQuiet(world, pos.x(), pos.y(), pos.z());
        return c != null && net.kuzuanpa.cemekanism.storage.QioService.state(c).signal ? 15 : 0;
    }

    /**
     * 原版漏斗/比较器问"这个方块有没有容器"时走这里（台账 P13）。
     * 返回 CE 的 NMS 容器；位置上的控制器还没建起来就返回 null（漏斗这一趟什么也不做）。
     */
    @Override
    public Object getContainer(Object thisBlock, Object[] args) {
        World world = CraftContainers.worldOfLevel(args[1]);
        if (world == null) {
            return null;
        }
        BlockPos pos = LocationUtils.fromBlockPos(args[2]);
        MachineBlockEntityController controller = this.plugin.machineAtQuiet(world, pos.x(), pos.y(), pos.z());
        return controller == null ? null : controller.containerHandle();
    }
}
