package net.kuzuanpa.cemekanism.machine.logic;

import mekcore.item.ItemMatcher;
import mekcore.item.ItemRef;
import mekcore.item.ItemSlotKind;
import mekcore.item.ItemSlotView;
import mekcore.machine.Machine;
import mekcore.machine.WorkState;
import mekcore.resource.ResourceStack;
import mekcore.resource.ResourceTank;
import mekcore.resource.ResourceTypes;
import mekcore.resource.Transaction;
import net.kuzuanpa.cemekanism.i18n.I18n;
import net.kuzuanpa.cemekanism.platform.GeneratorConfig;

/**
 * 生物能发电机（文档 11 G2.4）：生物燃料物品 → 生物乙醇 → 1 mB/t、350 J/t。
 * 普通生物燃料 64 mB，生物燃料块 576 mB；缓存放不下 350 J 时不烧（上游要求整 25 插得进去）。
 */
public final class BioGeneratorLogic extends GeneratorLogic {

    @Override
    public boolean needsTick(Machine machine) {
        if (!hasEnergyRoom(machine)) {
            return false;
        }
        ResourceTank bio = tank(machine, "biofuel");
        return bio.amount() > 0 || fuelItem(machine) != null;
    }

    @Override
    public long tick(Machine machine) {
        ResourceTank bio = tank(machine, "biofuel");
        fillFromFuel(machine, bio);
        long generation = GeneratorConfig.longValue("bio/generation_j", 350);
        String fluid = GeneratorConfig.stringValue("bio/fluid", "mek:bioethanol");
        long room = machine.energyTank().map(tank -> tank.capacity() - tank.amount()).orElse(0L);
        if (!fluid.equals(bio.storedId()) || bio.amount() <= 0 || room < generation) {
            machine.setState(WorkState.IDLE);
            return 0L;
        }
        if (bio.extract(1, Transaction.EXECUTE) != 1) {
            machine.setState(WorkState.IDLE);
            return 0L;
        }
        long inserted = insertEnergy(machine, generation);
        machine.setState(inserted > 0 ? WorkState.RUNNING : WorkState.IDLE);
        return inserted;
    }

    @Override
    public String name() {
        return I18n.machineName("mek:bio_generator");
    }

    private static void fillFromFuel(Machine machine, ResourceTank bio) {
        ItemRef fuel = fuelItem(machine);
        if (fuel == null) {
            return;
        }
        long produced = "mek:block_bio_fuel".equals(fuel.id())
                ? GeneratorConfig.longValue("bio/fuel_per_block_mb", 576)
                : GeneratorConfig.longValue("bio/fuel_per_item_mb", 64);
        String fluid = GeneratorConfig.stringValue("bio/fluid", "mek:bioethanol");
        long room = bio.roomFor(fluid);
        if (room < produced) {
            return;
        }
        bio.insert(new ResourceStack(ResourceTypes.FLUID, fluid, produced), Transaction.EXECUTE);
        machine.items().consume(-1, ItemMatcher.id(fuel.id()), 1);
    }

    private static ItemRef fuelItem(Machine machine) {
        String item = "mek:bio_fuel";
        String block = "mek:block_bio_fuel";
        for (ItemSlotView slot : machine.items().slots(ItemSlotKind.INPUT)) {
            if (slot.empty()) {
                continue;
            }
            if (item.equals(slot.content().id()) || block.equals(slot.content().id())) {
                return slot.content();
            }
        }
        return null;
    }
}
