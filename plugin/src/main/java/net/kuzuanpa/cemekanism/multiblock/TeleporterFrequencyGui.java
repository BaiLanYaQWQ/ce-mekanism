package net.kuzuanpa.cemekanism.multiblock;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kuzuanpa.cemekanism.gui.GuiConfig;
import net.kuzuanpa.cemekanism.gui.GuiSessions;
import net.kuzuanpa.cemekanism.gui.GuiText;
import net.kuzuanpa.cemekanism.multiblock.logic.TeleporterLogic;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Public teleporter frequencies. Chat input is consumed once and applied on the main thread. */
public final class TeleporterFrequencyGui implements InventoryHolder {
    private record Input(MultiblockHosts.Host host, long expires) { }
    private static final Map<UUID, Input> INPUTS = new ConcurrentHashMap<>();
    private final MultiblockHosts.Host host;
    private final Inventory inventory;
    private final List<String> frequencies = new ArrayList<>();
    private int page;

    private TeleporterFrequencyGui(MultiblockHosts.Host host) {
        this.host = host;
        this.inventory = Bukkit.createInventory(this, 54, Component.text("传送器 · 绑定公共频率"));
    }

    @Override public Inventory getInventory() { return inventory; }

    public static void open(MultiblockHosts.Host host, Player player) {
        if (!valid(host, player)) return;
        INPUTS.remove(player.getUniqueId());
        var view = new TeleporterFrequencyGui(host);
        view.refresh();
        player.openInventory(view.inventory);
        host.plugin().guiSessions().open(player, new GuiSessions.View(view, view.inventory,
                host.controllerLocation(), () -> Component.text("传送器 · 绑定公共频率"),
                view::refresh, () -> java.util.Arrays.toString(view.inventory.getContents()),
                () -> { }, () -> valid(host, player)));
    }

    private static boolean valid(MultiblockHosts.Host host, Player player) {
        return player.isOnline() && host.plugin().multiblocks().byKey(host.key()) == host
                && host.formed() && TeleporterLogic.loaded(host.box()) && player.getWorld() == host.world()
                && player.getLocation().distanceSquared(host.controllerLocation())
                <= GuiConfig.maxDistance() * GuiConfig.maxDistance();
    }

    private void refresh() {
        inventory.clear(); frequencies.clear();
        List<String> available = host.plugin().multiblocks().all().stream()
                .filter(other -> TeleporterLogic.of(other) != null)
                .map(other -> TeleporterLogic.of(other).state(other.instance()).frequency)
                .filter(name -> !name.isEmpty()).distinct().sorted().toList();
        int max = Math.max(0, (available.size() - 1) / 45);
        page = Math.max(0, Math.min(page, max));
        frequencies.addAll(available.subList(Math.min(page * 45, available.size()),
                Math.min((page + 1) * 45, available.size())));
        String current = TeleporterLogic.of(host).state(host.instance()).frequency;
        for (int i = 0; i < frequencies.size(); i++) {
            button(i, Material.ENDER_EYE, frequencies.get(i), List.of());
            if (frequencies.get(i).equals(current)) {
                var selected = inventory.getItem(i); var meta = selected.getItemMeta();
                meta.setEnchantmentGlintOverride(true); selected.setItemMeta(meta);
            }
        }
        button(45, Material.ARROW, "上一页", List.of());
        button(48, Material.BARRIER, "解除绑定", List.of());
        button(49, Material.NAME_TAG, "新建 / 输入频率", List.of("点击后在聊天中输入名称", "输入 cancel 取消，名称限 32 字符"));
        button(50, Material.PAPER, "第 " + (page + 1) + "/" + (max + 1) + " 页", List.of());
        button(53, Material.ARROW, "下一页", List.of());
    }

    private void button(int slot, Material material, String name, List<String> lore) {
        var item = new ItemStack(material); var meta = item.getItemMeta();
        GuiText.applyTooltip(meta, name, lore); item.setItemMeta(meta); inventory.setItem(slot, item);
    }

    public static final class Events implements Listener {
        @EventHandler(ignoreCancelled = true)
        public void onClick(InventoryClickEvent event) {
            if (!(event.getInventory().getHolder() instanceof TeleporterFrequencyGui view)) return;
            event.setCancelled(true);
            if (event.getClick() != ClickType.LEFT || !(event.getWhoClicked() instanceof Player player)) return;
            int raw = event.getRawSlot();
            // Resolve the selected name now, so a refresh cannot retarget an already queued click.
            String frequency = raw >= 0 && raw < view.frequencies.size() ? view.frequencies.get(raw) : null;
            Bukkit.getScheduler().runTask(view.host.plugin(), () -> {
                if (player.getOpenInventory().getTopInventory().getHolder() != view || !valid(view.host, player)) return;
                if (raw == 45 || raw == 53) { view.page += raw == 45 ? -1 : 1; view.refresh(); return; }
                if (raw == 49) {
                    INPUTS.put(player.getUniqueId(), new Input(view.host, System.currentTimeMillis() + 60_000));
                    player.closeInventory();
                    player.sendMessage("请在 60 秒内输入公共频率名称（同名传送器互通），输入 cancel 取消。");
                } else if (raw == 48 || frequency != null) {
                    TeleporterLogic.of(view.host).bind(view.host, raw == 48 ? "" : frequency);
                    player.closeInventory();
                    player.sendActionBar(Component.text(raw == 48 ? "已解除绑定" : "频率：" + frequency));
                }
            });
        }

        @EventHandler(ignoreCancelled = true)
        public void onDrag(InventoryDragEvent event) {
            if (event.getInventory().getHolder() instanceof TeleporterFrequencyGui) event.setCancelled(true);
        }

        @EventHandler public void onClose(InventoryCloseEvent event) {
            if (event.getInventory().getHolder() instanceof TeleporterFrequencyGui view)
                view.host.plugin().guiSessions().forget(event.getPlayer().getUniqueId(), view);
        }

        @EventHandler public void onQuit(PlayerQuitEvent event) { INPUTS.remove(event.getPlayer().getUniqueId()); }

        @EventHandler(ignoreCancelled = true)
        public void onChat(AsyncChatEvent event) {
            Input input = INPUTS.remove(event.getPlayer().getUniqueId());
            if (input == null || System.currentTimeMillis() > input.expires) return;
            event.setCancelled(true);
            String name = PlainTextComponentSerializer.plainText().serialize(event.message()).strip();
            Bukkit.getScheduler().runTask(input.host.plugin(), () -> {
                Player player = event.getPlayer();
                if (name.equalsIgnoreCase("cancel")) { player.sendMessage("已取消频率输入"); return; }
                if (!valid(input.host, player)) { player.sendMessage("传送器已失效或距离太远，请重新打开绑定界面"); return; }
                try {
                    if (name.isEmpty()) throw new IllegalArgumentException("频率名称不能为空");
                    TeleporterLogic.of(input.host).bind(input.host, name);
                    player.sendMessage("已绑定传送频率：" + name);
                } catch (IllegalArgumentException ex) { player.sendMessage(ex.getMessage()); }
            });
        }
    }
}
