package net.kuzuanpa.cemekanism.machine;

import mekcore.machine.Machine;
import mekcore.machine.WorkState;
import net.kuzuanpa.cemekanism.storage.BinLogic;
import net.momirealms.craftengine.bukkit.api.BukkitAdaptor;
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.UpdateFlags;
import net.momirealms.craftengine.core.block.property.Property;
import org.bukkit.World;

/** 把机器的运行状态写回 CE 的 active 属性；模型/贴图变体由资源包生成。 */
final class MachineActivityRenderer {
    private static final String ACTIVE = "active";
    private static final String[] QIO_BLOCKS = {
            "mek:qio_dashboard", "mek:qio_drive_array", "mek:qio_importer", "mek:qio_exporter"
    };

    private final MachineBlockEntityController controller;

    MachineActivityRenderer(MachineBlockEntityController controller) {
        this.controller = controller;
    }

    void refresh() {
        World world = this.controller.world();
        if (world == null) {
            return;
        }
        int x = this.controller.x(), y = this.controller.y(), z = this.controller.z();
        ImmutableBlockState state = CraftEngineBlocks.getCustomBlockState(world.getBlockAt(x, y, z));
        if (state == null) {
            return;
        }
        Property<Boolean> property = state.getProperty(ACTIVE);
        if (property == null) {
            return;
        }
        boolean active = active();
        if (Boolean.valueOf(active).equals(state.get(property))) {
            return;
        }
        BukkitAdaptor.adapt(world).storageWorld().world().setBlockState(
                x, y, z, state.with(property, active), UpdateFlags.UPDATE_ALL);
    }

    private boolean active() {
        String blockId = this.controller.blockId();
        Machine machine = this.controller.machine();
        if (blockId.endsWith("_bin") && machine.blueprint().logic() instanceof BinLogic bins) {
            return bins.store(machine).total() > 0;
        }
        if (blockId.endsWith("_fluid_tank")) {
            for (String port : machine.portNames()) {
                var tank = machine.tank(port);
                if (tank.isPresent() && tank.get().amount() > 0) {
                    return true;
                }
            }
            return false;
        }
        for (String qio : QIO_BLOCKS) {
            if (qio.equals(blockId)) {
                return this.controller.plugin().qio().online(this.controller);
            }
        }
        // 保留的配方也可能缺电/堵输出；最后一次加工结束后 RUNNING 又会留到下一次 tick。
        // 休眠机器不会再 tick，因此同时检查当前是否仍有工作，不能只读上一次的状态。
        return machine.redstoneActive() && machine.state() == WorkState.RUNNING && machine.needsTick();
    }
}
