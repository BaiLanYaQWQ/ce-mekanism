package net.kuzuanpa.cemekanism.transmitter;

import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.behavior.EntityBlock;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.util.Key;

/**
 * 元件方块的行为（文档 06）：不存状态、不装 ticker，只负责建控制器。
 * 资源包里的 `behavior: {type: cemekanism:transmitter}` 指到这里。
 *
 * <p>交互（配置器、网络读取器、空手看状态）在 {@link TransmitterListener} 里做：
 * 那几件事都要看玩家手里的物品，走 Bukkit 事件比走行为回调直接。
 */
public final class TransmitterBlockBehavior extends BukkitBlockBehavior implements EntityBlock {

    public static final Key KEY = Key.of("cemekanism", "transmitter");

    public static BlockBehaviorFactory<TransmitterBlockBehavior> factory(CeMekanismPlugin plugin) {
        return (block, section) -> new TransmitterBlockBehavior(plugin, block);
    }

    private final CeMekanismPlugin plugin;

    private TransmitterBlockBehavior(CeMekanismPlugin plugin, BlockDefinition block) {
        super(block);
        this.plugin = plugin;
    }

    @Override
    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        return new TransmitterBlockEntityController(blockEntity, this.plugin);
    }

    @Override
    public void initControllerId(int id) {
        // 元件是单态方块（没有 facing 这类硬编码属性），控制器只有一个，所以这里不用记 id
    }
}
