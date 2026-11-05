package net.kuzuanpa.cemekanism.storage;

import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.kuzuanpa.cemekanism.gui.GuiConfig;
import net.kuzuanpa.cemekanism.gui.GuiSessions;
import net.kuzuanpa.cemekanism.gui.GuiText;
import net.kuzuanpa.cemekanism.machine.MachineBlockEntityController;
import net.kuzuanpa.cemekanism.platform.BukkitItems;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import net.kyori.adventure.text.Component;
import java.util.*;
import java.util.stream.IntStream;

/** Virtual, paginated view. Display stacks are never real inventory contents and all clicks are intercepted. */
public final class QioDashboard implements InventoryHolder {
    final CeMekanismPlugin plugin;
    final MachineBlockEntityController controller;
    final String frequency, query;
    final Inventory inventory;
    final List<String> keys = new ArrayList<>();
    private final Map<String, ItemStack> templates = new HashMap<>();
    private final Map<String, String> itemIds = new HashMap<>();
    int page;
    boolean sortByCount;
    QioDashboard(CeMekanismPlugin plugin, MachineBlockEntityController controller, String frequency, String query, int page) {
        this.plugin = plugin; this.controller = controller; this.frequency = frequency; this.query = query; this.page = page;
        inventory = Bukkit.createInventory(this, 54, Component.text("QIO · " + frequency.substring(frequency.indexOf('/') + 1)));
    }
    public Inventory getInventory() { return inventory; }
    public static void open(MachineBlockEntityController c, Player player, int page) {
        open(c.plugin(), c, QioService.state(c).frequency, "", player, page);
    }
    public static void open(CeMekanismPlugin plugin, MachineBlockEntityController c, String frequency, String query, Player player, int page) {
        QioDashboard view = new QioDashboard(plugin, c, frequency, query, page);
        try {
            view.refresh(player); player.openInventory(view.inventory);
            plugin.guiSessions().open(player, new GuiSessions.View(view, view.inventory,
                    c == null ? null : new Location(c.world(), c.x() + .5, c.y() + .5, c.z() + .5),
                    () -> Component.text("QIO · " + frequency.substring(frequency.indexOf('/') + 1)),
                    () -> view.refresh(player), () -> Integer.toHexString(Arrays.hashCode(view.inventory.getContents())),
                    () -> { }, () -> view.valid(player)));
        } catch (IllegalArgumentException e) { player.sendMessage(e.getMessage()); QioFrequencyGui.open(plugin, c, player, 0); }
    }
    private boolean valid(Player player) {
        try { authorize(player); return true; } catch (IllegalArgumentException ex) { return false; }
    }
    void authorize(Player player) {
        if (controller != null) {
            authorizeController(controller, player);
            if (!frequency.equals(QioService.state(controller).frequency)) throw new IllegalArgumentException("QIO 频率已变更，请重新打开仪表板");
        }
        plugin.qio().require(frequency, player.getUniqueId().toString());
    }
    static void authorizeController(MachineBlockEntityController c, Player player) {
        double distance = GuiConfig.maxDistance();
        if (!c.registered() || !c.machine().canAccess(player.getUniqueId().toString()) || c.world() != player.getWorld()
                || player.getLocation().distanceSquared(new Location(c.world(), c.x() + .5, c.y() + .5, c.z() + .5)) > distance * distance)
            throw new IllegalArgumentException("无权访问、机器已卸载或距离过远");
    }
    void refresh(Player player) {
        authorize(player); inventory.clear(); keys.clear();
        Map<String, Long> contents = plugin.qio().entries(frequency, player.getUniqueId().toString());
        templates.keySet().retainAll(contents.keySet()); itemIds.keySet().retainAll(contents.keySet());
        for (String key : contents.keySet()) {
            ItemStack template = templates.computeIfAbsent(key, k -> StoredItems.item(k, 1));
            itemIds.computeIfAbsent(key, k -> BukkitItems.idOf(template));
        }
        Comparator<String> order = Comparator.comparing(itemIds::get);
        if (sortByCount) order = Comparator.<String>comparingLong(contents::get).reversed().thenComparing(order);
        String search = query.toLowerCase(Locale.ROOT);
        List<String> selected = contents.keySet().stream().filter(k -> {
            ItemStack item = templates.get(k);
            return search.isEmpty() || itemIds.get(k).toLowerCase(Locale.ROOT).contains(search)
                    || item.getItemMeta().getDisplayName().toLowerCase(Locale.ROOT).contains(search);
        }).sorted(order.thenComparing(Comparator.naturalOrder())).toList();
        int maxPage = Math.max(0, (selected.size() - 1) / 45); page = Math.max(0, Math.min(page, maxPage));
        for (int i = page * 45; i < Math.min(selected.size(), (page + 1) * 45); i++) {
            String key = selected.get(i); keys.add(key); ItemStack icon = templates.get(key).clone();
            var meta = icon.getItemMeta(); meta.setLore(List.of(GuiText.lore("库存 " + contents.get(key)),
                    GuiText.lore("左键取一组，右键取一个；点击背包存入")));
            icon.setItemMeta(meta); inventory.setItem(i - page * 45, icon);
        }
        button(45, Material.ARROW, "上一页"); button(49, Material.PAPER, "第 " + (page + 1) + '/' + (maxPage + 1) + " 页 · 刷新"); button(53, Material.ARROW, "下一页");
        button(46, Material.ENDER_EYE, "选择频率"); button(50, Material.COMPARATOR, "排序：" + (sortByCount ? "数量" : "物品 ID"));
    }
    private void button(int slot, Material material, String title) {
        ItemStack item = new ItemStack(material); var meta = item.getItemMeta();
        GuiText.applyTooltip(meta, title, List.of()); item.setItemMeta(meta); inventory.setItem(slot, item);
    }
    String keyAt(int raw) { return raw >= 0 && raw < keys.size() ? keys.get(raw) : null; }
    void click(Player player, int raw, boolean right, String expectedKey) {
        authorize(player); String actor = player.getUniqueId().toString();
        if (raw == 46) { QioFrequencyGui.open(plugin, controller, player, 0); return; }
        if (raw == 50) { sortByCount = !sortByCount; page = 0; }
        if (raw == 45) page--; else if (raw == 53) page++;
        else if (raw >= 0 && raw < keys.size()) {
            // A live refresh may have reordered the view between the click and its deferred handler.
            if (!Objects.equals(expectedKey, keyAt(raw))) { refresh(player); return; }
            String key = keys.get(raw); ItemStack template = StoredItems.item(key, 1);
            InventoryAccess target = new InventoryAccess(player.getInventory(), IntStream.range(0, 36).boxed().toList(), () -> { });
            int room = target.insert(template, right ? 1 : template.getMaxStackSize(), false);
            int n = (int) plugin.qio().extract(frequency, actor, key, room, true);
            if (n > 0) target.insert(template, n, true);
        } else if (raw >= 54) {
            // Raw-slot conversion is supplied by the inventory view listener.
        }
        refresh(player);
    }
    void deposit(Player player, int inventorySlot) {
        authorize(player); ItemStack item = player.getInventory().getItem(inventorySlot);
        if (!StoredItems.empty(item)) {
            int n = (int) plugin.qio().insert(frequency, player.getUniqueId().toString(), item, item.getAmount(), true);
            if (n > 0) { item = item.clone(); item.setAmount(item.getAmount() - n); player.getInventory().setItem(inventorySlot, item.getAmount() == 0 ? null : item); }
        }
        refresh(player);
    }
}
