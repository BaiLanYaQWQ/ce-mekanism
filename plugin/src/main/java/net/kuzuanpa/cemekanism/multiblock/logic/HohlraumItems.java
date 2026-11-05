package net.kuzuanpa.cemekanism.multiblock.logic;

import net.kuzuanpa.cemekanism.platform.BukkitItems;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

/** 黑体辐射腔是普通物品，只在自己的 PDC 里记录一个 D-T 燃料罐。 */
public final class HohlraumItems {

    public static final String HOHLRAUM = "mek:hohlraum";
    private static final NamespacedKey CHEMICAL = new NamespacedKey("cemekanism", "hohlraum_chemical");
    private static final NamespacedKey AMOUNT = new NamespacedKey("cemekanism", "hohlraum_amount");

    private HohlraumItems() {
    }

    public static boolean isHohlraum(ItemStack stack) {
        return stack != null && HOHLRAUM.equals(BukkitItems.idOf(stack));
    }

    public static void fill(ItemStack stack, String chemical, long amount) {
        ItemMeta meta = stack.getItemMeta();
        meta.getPersistentDataContainer().set(CHEMICAL, PersistentDataType.STRING, chemical);
        meta.getPersistentDataContainer().set(AMOUNT, PersistentDataType.LONG, Math.max(0, amount));
        stack.setItemMeta(meta);
    }

    public static long amount(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) {
            return 0;
        }
        return stack.getItemMeta().getPersistentDataContainer()
                .getOrDefault(AMOUNT, PersistentDataType.LONG, 0L);
    }

    public static String chemical(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) {
            return "";
        }
        return stack.getItemMeta().getPersistentDataContainer()
                .getOrDefault(CHEMICAL, PersistentDataType.STRING, "");
    }

    public static String encode(ItemStack stack) {
        if (stack == null || stack.getType().isAir()) {
            return "";
        }
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
             BukkitObjectOutputStream out = new BukkitObjectOutputStream(bytes)) {
            out.writeObject(stack);
            return Base64.getEncoder().encodeToString(bytes.toByteArray());
        } catch (Exception ignored) {
            return "";
        }
    }

    public static ItemStack decode(String encoded) {
        if (encoded == null || encoded.isEmpty()) {
            return null;
        }
        try (ByteArrayInputStream bytes = new ByteArrayInputStream(Base64.getDecoder().decode(encoded));
             BukkitObjectInputStream in = new BukkitObjectInputStream(bytes)) {
            Object value = in.readObject();
            return value instanceof ItemStack stack ? stack : null;
        } catch (Exception ignored) {
            return null;
        }
    }
}
