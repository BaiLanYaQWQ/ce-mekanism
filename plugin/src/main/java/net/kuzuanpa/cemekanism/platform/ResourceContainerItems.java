package net.kuzuanpa.cemekanism.platform;

import mekcore.machine.MachineTier;
import mekcore.resource.*;
import mekcore.spec.V10Tables;
import net.kuzuanpa.cemekanism.gui.GuiText;
import net.kuzuanpa.cemekanism.multiblock.MultiblockConfig;
import net.kuzuanpa.cemekanism.multiblock.logic.FusionReactorLogic;
import net.kuzuanpa.cemekanism.multiblock.logic.HohlraumItems;
import net.momirealms.craftengine.libraries.nbt.CompoundTag;
import net.momirealms.craftengine.libraries.nbt.NBT;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Locale;

/** Cursor containers share the real GUI tank, including its filters and transfer budget. */
public final class ResourceContainerItems {
    private static final NamespacedKey CONTENT_TYPE = new NamespacedKey("cemekanism", "container_type");
    private static final NamespacedKey CONTENT_ID = new NamespacedKey("cemekanism", "container_id");
    private static final NamespacedKey CONTENT_AMOUNT = new NamespacedKey("cemekanism", "container_amount");
    private static final NamespacedKey MACHINE = new NamespacedKey("cemekanism", "stored_machine");

    private ResourceContainerItems() { }

    private record Container(ResourceType type, String id, long amount, long capacity, long rate,
                             boolean hohlraum, boolean storedMachine, boolean bucket) {
        boolean accepts(ResourceType candidateType, String candidateId) {
            return (type == null || type.equals(candidateType))
                    && (amount == 0 || id.equals(candidateId))
                    && (!hohlraum || FusionReactorLogic.FUSION_FUEL.equals(candidateId))
                    && (!bucket || "minecraft:water".equals(candidateId) || "minecraft:lava".equals(candidateId));
        }
    }

    public static boolean isContainer(ItemStack item) { return container(item) != null; }

    /** Left extracts into the cursor, right inserts from it. Null means no transfer.
     * A single cursor item is required so a payload can never be duplicated across a stack. */
    public static ItemStack transfer(ItemStack item, ResourceHandler tank, boolean put) {
        Container container = container(item);
        if (container == null || item.getAmount() != 1 || tank == null
                || !tank.type().equals(ResourceTypes.FLUID) && !tank.type().equals(ResourceTypes.CHEMICAL)) return null;
        synchronized (tank) {
            String id = put ? container.id : tank.resourceId();
            if (id == null || id.isEmpty() || !container.accepts(tank.type(), id)) return null;
            long request = Math.min(container.rate, put ? container.amount : Math.max(0, container.capacity - container.amount));
            if (request <= 0) return null;
            ResourceStack resource = new ResourceStack(tank.type(), id, request);
            long moved = put ? tank.insert(resource, Transaction.SIMULATE) : tank.extract(request, Transaction.SIMULATE);
            if (moved <= 0 || container.bucket && moved != 1000) return null;
            // Prepare metadata before touching the live tank. Never mutate the caller's stack.
            ItemStack result = item.clone();
            write(result, container, tank.type(), id, put ? container.amount - moved : container.amount + moved);
            long actual = put ? tank.insert(resource.withAmount(moved), Transaction.EXECUTE)
                    : tank.extract(moved, Transaction.EXECUTE);
            if (actual != moved) throw new IllegalStateException("槽位转移的模拟结果与执行结果不一致");
            return result;
        }
    }

    private static Container container(ItemStack item) {
        if (item == null || item.getType().isAir()) return null;
        String id = BukkitItems.idOf(item);
        if (HohlraumItems.HOHLRAUM.equals(id)) {
            return new Container(ResourceTypes.CHEMICAL, HohlraumItems.chemical(item),
                    Math.max(0, HohlraumItems.amount(item)), MultiblockConfig.hohlraumCapacity(), Long.MAX_VALUE, true, false, false);
        }
        if ("mek:gauge_dropper".equals(id)) {
            var pdc = item.getItemMeta().getPersistentDataContainer();
            ResourceType type = ResourceTypes.find(pdc.getOrDefault(CONTENT_TYPE, PersistentDataType.STRING, "")).orElse(null);
            String content = pdc.get(CONTENT_ID, PersistentDataType.STRING);
            long amount = Math.max(0, pdc.getOrDefault(CONTENT_AMOUNT, PersistentDataType.LONG, 0L));
            if (amount > 0 && (type == null || content == null || content.isEmpty())) return null;
            return new Container(amount == 0 ? null : type, content, amount, MekSystems.gaugeDropperCapacity(),
                    MekSystems.gaugeDropperTransferRate(), false, false, false);
        }
        for (MachineTier tier : MachineTier.values()) {
            String prefix = "mek:" + tier.name().toLowerCase(Locale.ROOT);
            boolean fluid = id.equals(prefix + "_fluid_tank");
            if (!fluid && !id.equals(prefix + "_chemical_tank")) continue;
            var profile = fluid ? V10Tables.fluidTank(tier) : V10Tables.chemicalTank(tier);
            var stored = new CompoundDataTag(machineRoot(item)).child("cemekanism:machine").child("state").child("tanks").child("stored");
            long amount = Math.max(0, stored.getLong("amount").orElse(0L));
            String content = stored.getString("id").orElse("");
            if (amount > 0 && content.isEmpty()) return null;
            return new Container(fluid ? ResourceTypes.FLUID : ResourceTypes.CHEMICAL, content, amount,
                    profile.capacity(), profile.outputRatePerTick(), false, true, false);
        }
        return switch (id) {
            case "minecraft:bucket" -> new Container(ResourceTypes.FLUID, null, 0, 1000, 1000, false, false, true);
            case "minecraft:water_bucket" -> new Container(ResourceTypes.FLUID, "minecraft:water", 1000, 1000, 1000, false, false, true);
            case "minecraft:lava_bucket" -> new Container(ResourceTypes.FLUID, "minecraft:lava", 1000, 1000, 1000, false, false, true);
            default -> null;
        };
    }

    private static CompoundTag machineRoot(ItemStack item) {
        byte[] bytes = item.getItemMeta().getPersistentDataContainer().get(MACHINE, PersistentDataType.BYTE_ARRAY);
        return bytes == null ? new CompoundTag() : NBT.readCompressed(bytes, false);
    }

    private static void write(ItemStack item, Container container, ResourceType type, String id, long amount) {
        if (container.bucket) {
            item.setType(amount == 0 ? Material.BUCKET : "minecraft:water".equals(id) ? Material.WATER_BUCKET : Material.LAVA_BUCKET);
            return;
        }
        if (container.hohlraum) HohlraumItems.fill(item, amount == 0 ? "" : id, amount);
        var meta = item.getItemMeta();
        if (container.storedMachine) {
            CompoundTag root = machineRoot(item);
            var state = new CompoundDataTag(root).child("cemekanism:machine").child("state");
            state.putString("blueprint", BukkitItems.idOf(item));
            state.putString("factory_tier", "SINGLE");
            var stored = state.child("tanks").child("stored");
            stored.putString("id", amount == 0 ? "" : id);
            stored.putLong("amount", amount);
            meta.getPersistentDataContainer().set(MACHINE, PersistentDataType.BYTE_ARRAY, NBT.writeCompressed(root, false));
        } else if (!container.hohlraum) {
            var data = meta.getPersistentDataContainer();
            if (amount == 0) {
                data.remove(CONTENT_TYPE); data.remove(CONTENT_ID); data.remove(CONTENT_AMOUNT);
            } else {
                data.set(CONTENT_TYPE, PersistentDataType.STRING, type.id());
                data.set(CONTENT_ID, PersistentDataType.STRING, id);
                data.set(CONTENT_AMOUNT, PersistentDataType.LONG, amount);
            }
        }
        meta.setMaxStackSize(1);
        meta.setLore(List.of(GuiText.lore(amount == 0 ? "空容器" : ResourceNames.nameOf(type, id)),
                GuiText.lore(GuiText.format(amount) + " / " + GuiText.format(container.capacity) + " mB")));
        item.setItemMeta(meta);
    }
}
