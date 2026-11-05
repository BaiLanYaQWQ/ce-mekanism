package net.kuzuanpa.cemekanism.gui;

import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.kyori.adventure.text.Component;
import net.momirealms.craftengine.bukkit.api.BukkitAdaptor;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.plugin.CraftEngine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * 已打开界面的刷新表。标题里的字形条必须随机器状态动，而 Bukkit 没有"改标题"的 API：
 * 走 CE 的 updateInventoryTitle（同一容器 id 重发 open-screen），再 updateInventory()
 * 把 27/36 格里内容全量重发一遍（Paper 的 CraftPlayer.updateInventory → sendAllDataToRemote）。
 * 5 刻一次、内容没变就不发包；机器卸载/拆掉/走远时关掉。
 */
public final class GuiSessions {

    public record View(InventoryHolder holder, Inventory inventory, Location location,
                       Supplier<Component> title, Runnable anchors, Supplier<String> anchorHash,
                       Runnable close, Supplier<Boolean> valid) {
        public View(InventoryHolder holder, Inventory inventory, Location location,
                    Supplier<Component> title, Runnable anchors, Supplier<String> anchorHash, Runnable close) {
            this(holder, inventory, location, title, anchors, anchorHash, close, () -> true);
        }
    }

    private record Session(UUID player, View view, String lastTitle, String lastHash) {
    }

    private final CeMekanismPlugin plugin;
    private final Map<UUID, Session> sessions = new HashMap<>();
    private int ticks;

    public GuiSessions(CeMekanismPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(org.bukkit.entity.Player player, View view) {
        String title = view.title().get().toString();
        String hash = title + '|' + view.anchorHash().get();
        Session previous = this.sessions.put(player.getUniqueId(),
                new Session(player.getUniqueId(), view, title, hash));
        if (previous != null) {
            previous.view().close().run();
        }
    }

    /** 玩家自行关窗：只清理会话，不再发送一次 closeInventory。 */
    public void forget(UUID player, InventoryHolder holder) {
        Session session = this.sessions.get(player);
        if (session != null && session.view().holder() == holder) {
            this.sessions.remove(player);
            session.view().close().run();
        }
    }

    public void tick() {
        if (++this.ticks % GuiConfig.refreshTicks() != 0) {
            return;
        }
        for (UUID id : new ArrayList<>(this.sessions.keySet())) {
            Session session = this.sessions.get(id);
            if (session == null) {
                continue;
            }
            org.bukkit.entity.Player player = Bukkit.getPlayer(id);
            if (player == null || !player.isOnline()) {
                this.sessions.remove(id);
                session.view().close().run();
                continue;
            }
            HumanEntity human = player;
            Inventory top = human.getOpenInventory().getTopInventory();
            if (top.getHolder() != session.view().holder()) {
                this.sessions.remove(id);
                session.view().close().run();
                continue;
            }
            Location at = session.view().location();
            if (!session.view().valid().get() || (at != null && at.getWorld() != null
                    && (player.getWorld() != at.getWorld() || player.getLocation().distanceSquared(at)
                    > GuiConfig.maxDistance() * GuiConfig.maxDistance()))) {
                this.sessions.remove(id);
                player.closeInventory();
                session.view().close().run();
                continue;
            }
            session.view().anchors().run();
            Component title = session.view().title().get();
            String titleHash = title.toString();
            String hash = titleHash + '|' + session.view().anchorHash().get();
            if (hash.equals(session.lastHash())) {
                continue;
            }
            Player cePlayer = BukkitAdaptor.adapt(player);
            if (cePlayer == null) {
                continue;
            }
            try {
                if (!titleHash.equals(session.lastTitle())) {
                    CraftEngine.instance().guiManager().updateInventoryTitle(cePlayer,
                            GuiComponents.toCe(title, cePlayer));
                }
                // 重发标题会重建客户端菜单；紧接着全量发内容，槽位/光标/锚点 tooltip 才不会丢。
                player.updateInventory();
                this.sessions.put(id, new Session(id, session.view(), titleHash, hash));
            } catch (RuntimeException e) {
                this.plugin.getLogger().warning("刷新 GUI 标题失败（" + id + "）：" + e.getMessage());
                this.sessions.remove(id);
                session.view().close().run();
            }
        }
    }

    /** 机器被拆/区块卸载/结构失效：关掉所有看着它的人。 */
    public void close(InventoryHolder holder) {
        for (UUID id : new ArrayList<>(this.sessions.keySet())) {
            Session session = this.sessions.get(id);
            if (session == null || session.view().holder() != holder) {
                continue;
            }
            this.sessions.remove(id);
            org.bukkit.entity.Player player = Bukkit.getPlayer(id);
            if (player != null) {
                player.closeInventory();
            }
            session.view().close().run();
        }
    }

    public void closeAll() {
        for (Session session : new ArrayList<>(this.sessions.values())) {
            org.bukkit.entity.Player player = Bukkit.getPlayer(session.player());
            if (player != null) {
                player.closeInventory();
            }
            session.view().close().run();
        }
        this.sessions.clear();
    }

    public int size() {
        return this.sessions.size();
    }
}
