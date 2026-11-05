package net.kuzuanpa.cemekanism.platform;

import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.item.BukkitItemDefinition;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/** 内容表的物品 id ↔ Bukkit 物品堆。核心那边只认 id，转换全在这里。 */
public final class BukkitItems {

    private BukkitItems() {
    }

    /** 物品堆的 id：资源包里的自定义物品取它的注册 id，原版取 material 的键。 */
    public static String idOf(ItemStack stack) {
        Key custom = CraftEngineItems.getCustomItemId(stack);
        return custom != null ? custom.asString() : stack.getType().getKey().toString();
    }

    /** 表里的 id → 物品堆；资源包与 Paper 都不认识这个 id 时返回 null。 */
    public static ItemStack create(String id) {
        BukkitItemDefinition definition = CraftEngineItems.byId(id);
        if (definition != null) {
            return definition.buildBukkitItem();
        }
        Material material = Material.matchMaterial(id);
        return material == null ? null : new ItemStack(material);
    }
}
