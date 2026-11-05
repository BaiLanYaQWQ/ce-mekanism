package net.kuzuanpa.cemekanism.storage;

import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.kuzuanpa.cemekanism.gui.GuiText;
import net.kuzuanpa.cemekanism.i18n.I18n;
import net.kuzuanpa.cemekanism.machine.MachineBlockEntityController;
import net.kuzuanpa.cemekanism.platform.BukkitItems;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import java.util.ArrayList;
import java.util.List;

/** Access-filtered frequency selector for placed and portable QIO components. */
public final class QioFrequencyGui implements InventoryHolder {
    private final CeMekanismPlugin plugin;
    private final MachineBlockEntityController controller;
    private final Inventory inventory;
    private final List<String> keys = new ArrayList<>();
    private int page;
    private QioFrequencyGui(CeMekanismPlugin plugin, MachineBlockEntityController c, int page) {
        this.plugin = plugin; this.controller = c; this.page = page;
        inventory = Bukkit.createInventory(this, 54, Component.text(I18n.tr("qio.frequency_title")));
    }
    @Override public Inventory getInventory() { return inventory; }
    public static void open(CeMekanismPlugin plugin, MachineBlockEntityController c, Player player, int page) {
        var view = new QioFrequencyGui(plugin, c, page);
        try { view.refresh(player); player.openInventory(view.inventory); }
        catch (IllegalArgumentException ex) { player.sendMessage(ex.getMessage()); }
    }
    private void authorize(Player player) {
        if (controller != null) QioDashboard.authorizeController(controller, player);
        else if (!portable(player.getInventory().getItemInMainHand())) {
            throw new IllegalArgumentException(I18n.tr(player, "qio.hold_dashboard"));
        }
    }
    private static boolean portable(ItemStack item) {
        return !StoredItems.empty(item) && BukkitItems.idOf(item).equals("mek:portable_qio_dashboard");
    }
    private void refresh(Player player) {
        authorize(player); inventory.clear(); keys.clear();
        var available = plugin.qio().available(player.getUniqueId().toString());
        int maxPage = Math.max(0, (available.size() - 1) / 45);
        page = Math.max(0, Math.min(page, maxPage));
        for (int i = page * 45; i < Math.min(available.size(), (page + 1) * 45); i++) {
            var frequency = available.get(i); keys.add(frequency.id());
            int slot = i - page * 45;
            button(slot, Material.ENDER_EYE, frequency.id(), List.of(I18n.tr(player, "qio.permission",
                    I18n.enumName(frequency.security(), frequency.security().zhName()))));
            if (controller != null && frequency.id().equals(QioService.state(controller).frequency)
                    || controller == null && frequency.id().equals(player.getInventory().getItemInMainHand()
                            .getItemMeta().getPersistentDataContainer().get(StorageListener.PORTABLE, PersistentDataType.STRING))) {
                var selected = inventory.getItem(slot);
                var meta = selected.getItemMeta(); meta.setEnchantmentGlintOverride(true); selected.setItemMeta(meta);
            }
        }
        button(45, Material.ARROW, I18n.tr(player, "navigation.previous"), List.of());
        button(48, Material.BARRIER, I18n.tr(player, "qio.leave"), List.of());
        button(49, Material.NETHER_STAR, I18n.tr(player, "qio.create_main"),
                List.of(I18n.tr(player, "qio.create_hint")));
        button(50, Material.PAPER, I18n.tr(player, "navigation.page", page + 1, maxPage + 1), List.of());
        button(53, Material.ARROW, I18n.tr(player, "navigation.next"), List.of());
    }
    private void button(int slot, Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material); var meta = item.getItemMeta();
        GuiText.applyTooltip(meta, name, lore);
        item.setItemMeta(meta); inventory.setItem(slot, item);
    }
    void click(Player player, int slot) {
        authorize(player); String actor = player.getUniqueId().toString();
        if (slot == 45 || slot == 53) { page += slot == 45 ? -1 : 1; refresh(player); return; }
        String frequency;
        if (slot == 49) frequency = plugin.qio().create("main", actor);
        else if (slot == 48) frequency = "";
        else if (slot >= 0 && slot < keys.size()) frequency = keys.get(slot);
        else return;
        if (controller != null) {
            if (frequency.isEmpty()) QioService.state(controller).frequency = "";
            else plugin.qio().join(controller, frequency, actor);
            controller.markChanged();
            player.closeInventory();
            if (controller.blockId().equals("mek:qio_dashboard") && !frequency.isEmpty()) QioDashboard.open(controller, player, 0);
        } else {
            ItemStack held = player.getInventory().getItemInMainHand(); var meta = held.getItemMeta();
            if (frequency.isEmpty()) meta.getPersistentDataContainer().remove(StorageListener.PORTABLE);
            else { plugin.qio().require(frequency, actor); meta.getPersistentDataContainer().set(StorageListener.PORTABLE, PersistentDataType.STRING, frequency); }
            held.setItemMeta(meta); player.closeInventory();
            if (!frequency.isEmpty()) QioDashboard.open(plugin, null, frequency, "", player, 0);
        }
    }
}
