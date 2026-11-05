package net.kuzuanpa.cemekanism.storage;

import mekcore.item.ItemSlotKind;
import mekcore.machine.Direction;
import net.kuzuanpa.cemekanism.machine.MachineBlockEntityController;
import org.bukkit.block.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/** Main-thread inventory transactions preserving Bukkit item metadata and machine side/security rules. */
public record InventoryAccess(Inventory inventory, List<Integer> slots, Runnable changed) {
    public static Block neighbour(MachineBlockEntityController c, Direction d) {
        BlockFace face = BlockFace.valueOf(d.name());
        int x = c.x() + face.getModX(), z = c.z() + face.getModZ();
        return c.world().isChunkLoaded(x >> 4, z >> 4) ? c.world().getBlockAt(x, c.y() + face.getModY(), z) : null;
    }
    public static InventoryAccess adjacent(MachineBlockEntityController c, Direction d, boolean insert) {
        Block block = neighbour(c, d); if (block == null) return null;
        var target = c.plugin().machineAtQuiet(c.world(), block.getX(), block.getY(), block.getZ());
        if (target != null) {
            var m = target.machine(); var mode = m.itemSide(d.opposite());
            if (!m.canAccess(c.machine().owner()) || (insert ? !mode.allowsInsert() : !mode.allowsExtract())) return null;
            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i < target.inventory().getSize(); i++) {
                if (target.handler().kindAt(i) == (insert || PersonalStorageLogic.isStorage(target.blockId()) ? ItemSlotKind.INPUT : ItemSlotKind.OUTPUT)) indices.add(i);
            }
            return new InventoryAccess(target.inventory(), indices, target::markChanged);
        }
        BlockState state = block.getState();
        // These containers expose every slot on every side. Other vanilla machines need their own sided policy.
        if (!(state instanceof Chest || state instanceof Barrel || state instanceof ShulkerBox || state instanceof Hopper)) return null;
        Inventory inventory = ((Container) state).getInventory();
        return new InventoryAccess(inventory, IntStream.range(0, inventory.getSize()).boxed().toList(), () -> { });
    }
    public int insert(ItemStack item, int request, boolean execute) {
        int remaining = request;
        for (int pass = 0; pass < 2; pass++) for (int slot : slots) {
            ItemStack existing = inventory.getItem(slot);
            boolean empty = StoredItems.empty(existing);
            if (pass == 0 && empty || pass == 1 && !empty || !empty && !existing.isSimilar(item)) continue;
            int amount = Math.min(remaining, Math.min(item.getMaxStackSize(), inventory.getMaxStackSize()) - (empty ? 0 : existing.getAmount()));
            if (amount <= 0) continue;
            if (execute) { ItemStack next = item.clone(); next.setAmount((empty ? 0 : existing.getAmount()) + amount); inventory.setItem(slot, next); }
            remaining -= amount; if (remaining == 0) break;
        }
        if (execute && remaining != request) changed.run();
        return request - remaining;
    }

    /** Extract matching complete item identities from the already filtered, side-aware slot list. */
    public int extract(ItemStack template, int request, boolean execute) {
        if (StoredItems.empty(template) || request <= 0) {
            return 0;
        }
        int remaining = request;
        for (int slot : slots) {
            ItemStack existing = inventory.getItem(slot);
            if (StoredItems.empty(existing) || !existing.isSimilar(template)) {
                continue;
            }
            int amount = Math.min(remaining, existing.getAmount());
            if (execute) {
                ItemStack next = existing.clone();
                next.setAmount(existing.getAmount() - amount);
                inventory.setItem(slot, next.getAmount() == 0 ? null : next);
            }
            remaining -= amount;
            if (remaining == 0) {
                break;
            }
        }
        if (execute && remaining != request) {
            changed.run();
        }
        return request - remaining;
    }

    public void take(int slot, int amount) {
        ItemStack stack = inventory.getItem(slot).clone(); stack.setAmount(stack.getAmount() - amount);
        inventory.setItem(slot, stack.getAmount() == 0 ? null : stack); changed.run();
    }
}
