package net.kuzuanpa.cemekanism.machine.logic;

import mekcore.machine.Machine;
import mekcore.machine.MachineLogic;
import mekcore.machine.WorkState;
import mekcore.resource.Transaction;
import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.kuzuanpa.cemekanism.machine.MachineContexts;
import net.kuzuanpa.cemekanism.multiblock.MultiblockConfig;
import net.kuzuanpa.cemekanism.multiblock.logic.FusionReactorLogic;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

/** Basic laser emitter. Receivers are the existing amplifier and fusion laser focus matrix. */
public final class LaserLogic implements MachineLogic {
    @Override
    public long energyPerTick(Machine machine) {
        return Math.min(machine.energyPerTick(), machine.energyTank().map(t -> t.amount()).orElse(0L));
    }

    @Override
    public long energyDemand(Machine machine) { return machine.energyPerTick(); }

    @Override
    public boolean needsTick(Machine machine) {
        return machine.energyTank().map(t -> t.amount() > 0).orElse(false);
    }

    @Override
    public long tick(Machine machine) {
        var place = MachineContexts.of(machine);
        if (place == null) return 0;
        var plugin = JavaPlugin.getPlugin(CeMekanismPlugin.class);
        var controller = plugin.machineAtQuiet(place.world(), place.x(), place.y(), place.z());
        if (controller == null) return 0;
        var direction = controller.facing();
        Vector ray = switch (direction) {
            case NORTH -> new Vector(0, 0, -1);
            case SOUTH -> new Vector(0, 0, 1);
            case EAST -> new Vector(1, 0, 0);
            case WEST -> new Vector(-1, 0, 0);
            case UP -> new Vector(0, 1, 0);
            case DOWN -> new Vector(0, -1, 0);
        };
        Location start = new Location(place.world(), place.x() + .5, place.y() + .5, place.z() + .5).add(ray.clone().multiply(.51));
        var hit = place.world().rayTraceBlocks(start, ray, MultiblockConfig.laserRange(), FluidCollisionMode.NEVER, true);
        long energy = machine.lastEnergyUsed();
        if (energy <= 0) { machine.setState(WorkState.NO_ENERGY); return 0; }
        if (hit != null && hit.getHitBlock() != null) {
            var block = hit.getHitBlock();
            var target = plugin.machineAtQuiet(block.getWorld(), block.getX(), block.getY(), block.getZ());
            if (target != null && target.machine().blueprint().logic() instanceof LaserAmplifierLogic) {
                target.machine().energyTank().ifPresent(t -> t.insert(mekcore.resource.ResourceStack.energy(energy), Transaction.EXECUTE));
                target.markChanged();
            } else {
                var host = plugin.multiblocks().at(block.getWorld(), block.getX(), block.getY(), block.getZ());
                if (host != null && host.formed() && host.instance().blueprint().logic() instanceof FusionReactorLogic fusion
                        && "laser_focus".equals(host.box().roleAt(block.getX() - host.box().originX(),
                        block.getY() - host.box().originY(), block.getZ() - host.box().originZ()).id())) {
                    fusion.addLaserEnergy(host.instance(), energy);
                    host.markChanged();
                }
            }
        }
        machine.setState(WorkState.RUNNING);
        return 0;
    }
}
