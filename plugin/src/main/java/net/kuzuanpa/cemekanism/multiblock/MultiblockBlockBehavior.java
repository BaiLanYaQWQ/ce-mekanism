package net.kuzuanpa.cemekanism.multiblock;

import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.behavior.EntityBlock;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.entity.player.InteractionResult;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.CEWorld;
import net.momirealms.craftengine.core.world.context.UseOnContext;

/**
 * 锚点方块的行为（文档 05 第四节）：控制器 / 阀门 / 端口 / 逻辑适配器 / 传送机本体。
 * 结构件（外壳、玻璃、转子……）是普通方块，不挂行为也不挂方块实体。
 */
public final class MultiblockBlockBehavior extends BukkitBlockBehavior implements EntityBlock {

    public static final Key KEY = Key.of("cemekanism", "multiblock");

    public static BlockBehaviorFactory<MultiblockBlockBehavior> factory(CeMekanismPlugin plugin) {
        return (block, section) -> new MultiblockBlockBehavior(plugin, block);
    }

    private final CeMekanismPlugin plugin;
    private int controllerId;

    private MultiblockBlockBehavior(CeMekanismPlugin plugin, BlockDefinition block) {
        super(block);
        this.plugin = plugin;
    }

    @Override
    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        return new MultiblockBlockEntityController(blockEntity, this.plugin);
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
        MultiblockBlockEntityController controller = controllerAt(context.getLevel().storageWorld(),
                context.getClickedPos());
        if (controller == null) {
            return InteractionResult.PASS;
        }
        controller.report(player);
        return InteractionResult.SUCCESS_AND_CANCEL;
    }

    private MultiblockBlockEntityController controllerAt(CEWorld world, BlockPos pos) {
        BlockEntity entity = world.getBlockEntityAtIfLoaded(pos);
        return entity == null ? null
                : entity.controller.getAt(MultiblockBlockEntityController.class, this.controllerId);
    }
}
