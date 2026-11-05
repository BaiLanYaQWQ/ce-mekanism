package net.kuzuanpa.cemekanism.platform;

import net.kuzuanpa.cemekanism.storage.StorageItemEnergy;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

/**
 * 随身储电物品的能量面（文档 04 的逻辑机器分支：充电台充的就是这些）。
 * 数值挂在物品的 PDC 上（键 cemekanism:energy，单位 J），容量与充电速度来自 mek-systems.yml 的 gear 段，
 * 默认值取上游 GearConfig（energy_tablet：maxEnergy 1,000,000 / chargeRate 5,000）。
 */
public final class EnergyItems {

    private static final NamespacedKey ENERGY = new NamespacedKey("cemekanism", "energy");

    private EnergyItems() {
    }

    public static long stored(ItemStack stack) {
        if (stack == null || stack.getType().isAir() || !stack.hasItemMeta()) {
            return 0L;
        }
        if (StorageItemEnergy.isCube(stack)) return StorageItemEnergy.stored(stack);
        Long value = stack.getItemMeta().getPersistentDataContainer().get(ENERGY, PersistentDataType.LONG);
        return value == null ? 0L : value;
    }

    /** 充进去多少 J（返回实际充入量）；容量与速率上限取自 gear 配置。 */
    public static long charge(ItemStack stack, long joules) {
        if (stack == null || joules <= 0) {
            return 0L;
        }
        if (StorageItemEnergy.isCube(stack)) return StorageItemEnergy.charge(stack, joules);
        if (!MekSystems.energyItemIds().contains(BukkitItems.idOf(stack))) {
            return 0L;                            // 只会存电的那些物品
        }
        long capacity = MekSystems.energyTabletMaxJ();
        long rate = Math.min(joules, MekSystems.energyTabletChargeRateJ());
        long room = Math.max(0L, capacity - stored(stack));
        long given = Math.min(rate, room);
        if (given <= 0) {
            return 0L;
        }
        ItemMeta meta = stack.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(ENERGY, PersistentDataType.LONG, stored(stack) + given);
        stack.setItemMeta(meta);
        return given;
    }
}
