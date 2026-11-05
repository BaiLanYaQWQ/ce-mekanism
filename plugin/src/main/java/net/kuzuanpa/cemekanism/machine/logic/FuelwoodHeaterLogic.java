package net.kuzuanpa.cemekanism.machine.logic;

import mekcore.item.ItemMatcher;
import mekcore.item.ItemRef;
import mekcore.item.ItemSlotKind;
import mekcore.item.ItemSlotView;
import mekcore.machine.Machine;
import mekcore.machine.MachineLogic;
import mekcore.machine.WorkState;
import mekcore.persist.DataTag;
import net.kuzuanpa.cemekanism.platform.BukkitItems;
import net.kuzuanpa.cemekanism.platform.GeneratorConfig;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 固体燃料加热器（上游 TileEntityFuelwoodHeater）：
 * 每烧掉一个可燃烧物品，按燃料刻数给热节点加 {@code 400 J × 刻数 × 倍率}。
 * 燃烧剩余刻按机器实例保存，避免读档后重置燃料。
 */
public final class FuelwoodHeaterLogic implements MachineLogic {

    private static final class Burn {
        private int remaining;
        private int maximum;
    }

    private final Map<Machine, Burn> burns = Collections.synchronizedMap(new WeakHashMap<>());

    @Override
    public long tick(Machine machine) {
        Burn burn = burns.computeIfAbsent(machine, ignored -> new Burn());
        if (burn.remaining <= 0) {
            ItemRef fuel = firstFuel(machine);
            if (fuel == null) {
                machine.setState(WorkState.NO_INPUT);
                return 0L;
            }
            burn.maximum = GeneratorConfig.burnTicks(fuel.id(),
                    GeneratorConfig.intValue("heat/default_burn_ticks", 200));
            burn.remaining = burn.maximum;
            machine.items().consume(-1, ItemMatcher.id(fuel.id()), 1);
        }
        int ticks = Math.min(burn.remaining,
                Math.max(1, GeneratorConfig.intValue("heater/fuelwood_tick_multiplier", 1)));
        burn.remaining -= ticks;
        double perTick = GeneratorConfig.doubleValue("heater/fuelwood_heat_per_tick_j", 400);
        machine.heatNode().addHeat(perTick * ticks);
        machine.setState(WorkState.RUNNING);
        return 0L;
    }

    @Override
    public boolean needsTick(Machine machine) {
        Burn burn = burns.get(machine);
        return (burn != null && burn.remaining > 0) || firstFuel(machine) != null;
    }

    @Override
    public void save(Machine machine, DataTag tag) {
        Burn burn = burns.get(machine);
        if (burn == null) {
            return;
        }
        tag.putInt("burn_time", burn.remaining);
        tag.putInt("max_burn_time", burn.maximum);
    }

    @Override
    public void load(Machine machine, DataTag tag) {
        Burn burn = burns.computeIfAbsent(machine, ignored -> new Burn());
        burn.remaining = tag.getInt("burn_time", 0);
        burn.maximum = tag.getInt("max_burn_time", 0);
    }

    @Override
    public String name() {
        return "固体燃料加热器";
    }

    public int burnTime(Machine machine) {
        Burn burn = burns.get(machine);
        return burn == null ? 0 : burn.remaining;
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
