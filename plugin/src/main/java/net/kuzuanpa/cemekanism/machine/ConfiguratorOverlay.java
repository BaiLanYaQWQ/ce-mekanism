package net.kuzuanpa.cemekanism.machine;

import mekcore.machine.Direction;
import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.kuzuanpa.cemekanism.platform.BukkitItems;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import net.momirealms.craftengine.bukkit.api.event.CraftEngineReloadEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** A visible face model and text preview, using the same selection rule as actual clicks. */
public final class ConfiguratorOverlay implements Listener {
    private final CeMekanismPlugin plugin;
    private final Set<UUID> showing = new HashSet<>();
    private final ConfiguratorFaceRenderer renderer;

    public ConfiguratorOverlay(CeMekanismPlugin plugin) {
        this.plugin = plugin;
        this.renderer = new ConfiguratorFaceRenderer(plugin);
    }

    public void tick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!preview(player)) remove(player.getUniqueId());
        }
        for (UUID id : Set.copyOf(this.showing)) {
            if (Bukkit.getPlayer(id) == null) remove(id);
        }
    }

    private boolean preview(Player player) {
        if (player.isDead()) return false;
        if (!"mek:configurator".equals(BukkitItems.idOf(player.getInventory().getItemInMainHand()))) return false;
        ConfiguratorMode mode = ConfiguratorMode.of(player.getInventory().getItemInMainHand());
        var hit = ConfiguratorRay.trace(player);
        if (hit == null) return functionOnly(player, mode);
        Block block = hit.block();
        var machine = this.plugin.machineAtQuiet(block.getWorld(), block.getX(), block.getY(), block.getZ());
        var pipe = machine == null ? this.plugin.transmitterAt(block.getWorld(), block.getX(), block.getY(), block.getZ()) : null;
        if (machine == null && pipe == null) return functionOnly(player, mode);
        if (machine != null && !machine.machine().canAccess(player.getUniqueId().toString())) return functionOnly(player, mode);
        Direction face = hit.face();
        Direction selected = hit.selected();
        String status = machine != null ? mode.status(machine, selected) : mode.status(pipe, selected);
        player.sendActionBar(mode.actionBar(player, selected, status));
        this.showing.add(player.getUniqueId());
        this.renderer.show(player, block, face, selected);
        return true;
    }

    private boolean functionOnly(Player player, ConfiguratorMode mode) {
        this.renderer.remove(player.getUniqueId());
        player.sendActionBar(mode.actionBar(player, null, null));
        this.showing.add(player.getUniqueId());
        return true;
    }

    private void remove(UUID id) {
        this.renderer.remove(id);
        if (this.showing.remove(id)) {
            Player player = Bukkit.getPlayer(id);
            if (player != null) player.sendActionBar(Component.empty());
        }
    }

    public void close() {
        for (UUID id : Set.copyOf(this.showing)) remove(id);
        this.renderer.close();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHeldItem(PlayerItemHeldEvent event) {
        remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSwapHands(PlayerSwapHandItemsEvent event) {
        remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onReload(CraftEngineReloadEvent event) {
        close();
    }
}
