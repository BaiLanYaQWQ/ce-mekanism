package net.kuzuanpa.cemekanism.machine.logic;

import mekcore.machine.Direction;
import mekcore.machine.Machine;
import mekcore.machine.MachineLogic;
import mekcore.machine.WorkState;
import mekcore.resource.Transaction;
import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.kuzuanpa.cemekanism.machine.MachineBlockEntityController;
import net.kuzuanpa.cemekanism.machine.MachineContexts;
import net.kuzuanpa.cemekanism.multiblock.MultiblockConfig;
import net.kuzuanpa.cemekanism.multiblock.MultiblockHosts;
import net.kuzuanpa.cemekanism.multiblock.logic.FusionReactorLogic;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

/**
 * 激光增幅器：上游在触发时把缓存里的整束能量一次打向朝向的受体。
 * B6 只接聚变聚焦矩阵，不做挖方块、打实体和牵引光束。
 */
public final class LaserAmplifierLogic implements MachineLogic {

    @Override
    public long tick(Machine machine) {
        MachineContexts.Place place = MachineContexts.of(machine);
        if (place == null || !machine.redstoneActive()) {
            return 0;
        }
        var buffer = machine.energyTank().orElse(null);
        if (buffer == null || buffer.amount() <= 0) {
            machine.setState(WorkState.IDLE);
            return 0;
        }
        CeMekanismPlugin plugin = JavaPlugin.getPlugin(CeMekanismPlugin.class);
        MachineBlockEntityController controller = plugin.machineAtQuiet(
                place.world(), place.x(), place.y(), place.z());
        if (controller == null) {
            return 0;
        }
        Direction facing = controller.facing();
        Location start = new Location(place.world(), place.x() + 0.5, place.y() + 0.5,
                place.z() + 0.5).add(vector(facing).multiply(0.51));
        RayTraceResult hit = place.world().rayTraceBlocks(start, vector(facing),
                MultiblockConfig.laserRange(), FluidCollisionMode.NEVER, true);
        if (hit == null || hit.getHitBlock() == null) {
            machine.setState(WorkState.IDLE);
            return 0;
        }
        Block block = hit.getHitBlock();
        MultiblockHosts.Host host = plugin.multiblocks().at(
                block.getWorld(), block.getX(), block.getY(), block.getZ());
        if (host == null || !host.formed() || !"fusion_reactor".equals(host.def().family())) {
            machine.setState(WorkState.IDLE);
            return 0;
        }
        String role = host.box().roleAt(block.getX() - host.box().originX(),
                block.getY() - host.box().originY(), block.getZ() - host.box().originZ()).id();
        if (!"laser_focus".equals(role)
                || !(host.instance().blueprint().logic() instanceof FusionReactorLogic fusion)) {
            machine.setState(WorkState.IDLE);
            return 0;
        }
        long energy = buffer.amount();
        buffer.extract(energy, Transaction.EXECUTE);
        fusion.addLaserEnergy(host.instance(), energy);
        host.markChanged();
        machine.setState(WorkState.RUNNING);
        return 0;
    }

    @Override
    public boolean needsTick(Machine machine) {
        return machine.redstoneActive()
                && machine.energyTank().map(tank -> tank.amount() > 0).orElse(false);
    }

    @Override
    public long energyPerTick(Machine machine) {
        return 0;
    }

    @Override
    public long energyDemand(Machine machine) {
        return machine.energyTank().map(tank -> Math.max(0, tank.capacity() - tank.amount()))
                .orElse(0L);
    }

    private static Vector vector(Direction direction) {
        return switch (direction) {
            case DOWN -> new Vector(0, -1, 0);
            case UP -> new Vector(0, 1, 0);
            case NORTH -> new Vector(0, 0, -1);
            case SOUTH -> new Vector(0, 0, 1);
            case WEST -> new Vector(-1, 0, 0);
            case EAST -> new Vector(1, 0, 0);
        };
    }
}
