package net.kuzuanpa.cemekanism.storage;

import org.bukkit.inventory.ItemStack;
import java.util.Base64;

public final class StoredItems {
    private StoredItems() { }
    public static String key(ItemStack stack) {
        ItemStack one = stack.clone(); one.setAmount(1);
        return Base64.getEncoder().encodeToString(one.serializeAsBytes());
    }
    public static ItemStack item(String key, int amount) {
        ItemStack stack = ItemStack.deserializeBytes(Base64.getDecoder().decode(key));
        stack.setAmount(amount); return stack;
    }
    public static boolean empty(ItemStack stack) { return stack == null || stack.getType().isAir() || stack.getAmount() <= 0; }
}
