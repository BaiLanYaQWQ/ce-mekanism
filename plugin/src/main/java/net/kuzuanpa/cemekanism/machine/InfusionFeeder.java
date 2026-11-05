package net.kuzuanpa.cemekanism.machine;

import mekcore.machine.Machine;
import mekcore.resource.ResourceStack;
import mekcore.resource.ResourceTank;
import mekcore.resource.ResourceTypes;
import mekcore.resource.Transaction;
import net.kuzuanpa.cemekanism.generated.GeneratedContent;
import net.kuzuanpa.cemekanism.platform.BukkitItems;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/** 灌注机的辅料槽：物品 → 点数（上游 ChemicalInventorySlot.fillOrConvert 的等价物）。 */
public final class InfusionFeeder {
    private static final String TANK = "infuse";
    private static final String SLOT = "infuse_in";

    private InfusionFeeder() {
    }

    public static Optional<ResourceTank> tank(Machine machine) {
        return machine.tank(TANK).filter(tank -> tank.type().equals(ResourceTypes.INFUSION));
    }

    public static boolean canFeed(Machine machine, Inventory inventory, int slot) {
        GeneratedContent.InfusionSource source = sourceOf(inventory.getItem(slot));
        if (source == null) {
            return false;
        }
        return tank(machine).map(tank -> tank.roomFor(source.infusion()) >= source.points())
                .orElse(false);
    }

    public static boolean feed(Machine machine, Inventory inventory, int slot) {
        ItemStack stack = inventory.getItem(slot);
        GeneratedContent.InfusionSource source = sourceOf(stack);
        if (source == null) {
            return false;
        }
        ResourceTank tank = tank(machine).orElse(null);
        if (tank == null || tank.roomFor(source.infusion()) < source.points()) {
            return false;
        }
        long accepted = tank.insert(ResourceStack.infusion(source.infusion(), source.points()),
                Transaction.SIMULATE);
        if (accepted < source.points()) {
            return false;
        }
        tank.insert(ResourceStack.infusion(source.infusion(), source.points()), Transaction.EXECUTE);
        if (stack.getAmount() <= 1) {
            inventory.setItem(slot, null);
        } else {
            stack.setAmount(stack.getAmount() - 1);
            inventory.setItem(slot, stack);
        }
        return true;
    }

    public static String slotName() {
        return SLOT;
    }

    private static GeneratedContent.InfusionSource sourceOf(ItemStack stack) {
        if (stack == null || stack.getType().isAir()) {
            return null;
        }
        String id = BukkitItems.idOf(stack);
        for (GeneratedContent.InfusionSource source : GeneratedContent.INFUSION_SOURCES) {
            if (source.item().equals(id)) {
                return source;
            }
        }
        return null;
    }
}
