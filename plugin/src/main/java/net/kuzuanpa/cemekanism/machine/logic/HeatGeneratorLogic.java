package net.kuzuanpa.cemekanism.machine.logic;

import mekcore.heat.HeatNode;
import mekcore.item.ItemRef;
import mekcore.item.ItemSlotKind;
import mekcore.item.ItemSlotView;
import mekcore.machine.Machine;
import mekcore.machine.WorkState;
import mekcore.resource.ResourceStack;
import mekcore.resource.ResourceTank;
import mekcore.resource.ResourceTypes;
import mekcore.resource.Transaction;
import net.kuzuanpa.cemekanism.machine.GeneratorEnvironment;
import net.kuzuanpa.cemekanism.machine.MachineContexts;
import net.kuzuanpa.cemekanism.platform.BukkitItems;
import net.kuzuanpa.cemekanism.platform.GeneratorConfig;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

/**
 * 热力发电机（文档 11 G2.1）：燃料 → 熔岩罐 → 机器热容 → 卡诺效率 → 电。
 *
 * <p>上游把主动燃烧（200 J/t）、邻面熔岩（每面 30 J/t）和下界加成（100 J/t）都先变成热量，
 * 再由 {@code 0.5 × (T - 环境)} 的散热和卡诺效率折算成电；本类保持这个顺序。
 */
public final class HeatGeneratorLogic extends GeneratorLogic {

    private static final long LAVA_BUCKET_TICKS = 20000L;
    private static final long LAVA_BUCKET_MB = 1000L;

    @Override
    public boolean needsTick(Machine machine) {
        if (!hasEnergyRoom(machine)) {
            return false;
        }
        ResourceTank lava = tank(machine, "lava");
        HeatNode heat = machine.heatNode();
        if (("minecraft:lava".equals(lava.storedId()) && lava.amount() > 0)
                || firstFuel(machine) != null) {
            return true;
        }
        MachineContexts.Place place = MachineContexts.of(machine);
        if (place == null) {
            return false;
        }
        if (heat.temperatureK() > heat.config().ambientK() + 0.01) {
            return true;
        }
        return GeneratorEnvironment.lavaSides(place.world(), place.x(), place.y(), place.z()) > 0
                || GeneratorEnvironment.isNether(place.world());
    }

    @Override
    public long tick(Machine machine) {
        MachineContexts.Place place = MachineContexts.of(machine);
        if (place == null) {
            machine.setState(WorkState.IDLE);
            return 0L;
        }
        World world = place.world();
        ResourceTank lava = tank(machine, "lava");
        HeatNode heat = machine.heatNode();

        if (hasEnergyRoom(machine)) {
            burnFuel(machine, lava);
        }

        boolean active = false;
        int lavaPerTick = GeneratorConfig.intValue("heat/lava_per_tick_mb", 10);
        boolean hasLava = "minecraft:lava".equals(lava.storedId());
        if (hasEnergyRoom(machine) && hasLava && lava.amount() >= lavaPerTick
                && lava.extract(lavaPerTick, Transaction.EXECUTE) == lavaPerTick) {
            heat.addHeat(GeneratorConfig.doubleValue("heat/generation_j", 200));
            active = true;
        }

        int lavaSides = GeneratorEnvironment.lavaSides(world, place.x(), place.y(), place.z());
        double passive = lavaSides * GeneratorConfig.doubleValue("heat/lava_per_side_j", 30);
        if (GeneratorEnvironment.isNether(world)) {
            passive += GeneratorConfig.doubleValue("heat/nether_bonus_j", 100);
        }
        if (passive > 0) {
            heat.addHeat(passive);
        }

        double ambient = heat.config().ambientK();
        double temperature = heat.temperatureK();
        double carnot = 1.0 - Math.min(ambient, temperature) / Math.max(ambient, temperature);
        double heatLost = 0.5 * (temperature - ambient);
        heat.addHeat(-heatLost);
        long energy = Math.min(GeneratorConfig.longValue("heat/max_output_j", 510),
                (long) (Math.abs(heatLost) * carnot));
        long inserted = insertEnergy(machine, energy);

        machine.setState(active || inserted > 0 ? WorkState.RUNNING : WorkState.IDLE);
        return inserted;
    }

    @Override
    public String name() {
        return "热力发电机";
    }

    private static void burnFuel(Machine machine, ResourceTank lava) {
        ItemRef fuel = firstFuel(machine);
        if (fuel == null) {
            return;
        }
        ItemStack stack = BukkitItems.create(fuel.id());
        if (stack == null || !stack.getType().isFuel()) {
            return;
        }
        int ticks = GeneratorConfig.burnTicks(fuel.id(),
                GeneratorConfig.intValue("heat/default_burn_ticks", 200));
        long produced = Math.max(1L, LAVA_BUCKET_MB * ticks / LAVA_BUCKET_TICKS);
        long inserted = lava.insert(
                new ResourceStack(ResourceTypes.FLUID, "minecraft:lava", produced),
                Transaction.SIMULATE);
        if (inserted < produced) {
            return;
        }
        lava.insert(new ResourceStack(ResourceTypes.FLUID, "minecraft:lava", produced),
                Transaction.EXECUTE);
        machine.items().consume(-1, mekcore.item.ItemMatcher.id(fuel.id()), 1);
    }

    private static ItemRef firstFuel(Machine machine) {
        for (ItemSlotView slot : machine.items().slots(ItemSlotKind.INPUT)) {
            if (slot.empty()) {
                continue;
            }
            ItemStack stack = BukkitItems.create(slot.content().id());
            if (stack != null && stack.getType().isFuel()) {
                return slot.content();
            }
        }
        return null;
    }
}
