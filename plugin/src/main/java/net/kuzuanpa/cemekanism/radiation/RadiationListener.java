package net.kuzuanpa.cemekanism.radiation;

import mekcore.chemical.DoseTracker;
import mekcore.chemical.Radiation;
import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.kuzuanpa.cemekanism.platform.BukkitItems;
import net.kuzuanpa.cemekanism.i18n.I18n;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** 玩家侧的剂量生命周期 + 盖革计数器/放射量测定器（上游 0.5 秒冷却，潜行不触发）。 */
public final class RadiationListener implements Listener {
    private static final long COOLDOWN_MILLIS = 500L;

    private final CeMekanismPlugin plugin;
    private final Map<UUID, Long> lastUse = new HashMap<>();

    public RadiationListener(CeMekanismPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.radiation(event.getPlayer().getWorld()).join(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        RadiationWorld radiation = plugin.radiation(event.getPlayer().getWorld());
        radiation.quit(event.getPlayer());
    }

    @EventHandler
    public void onChangedWorld(PlayerChangedWorldEvent event) {
        plugin.radiation(event.getFrom()).quit(event.getPlayer());
        plugin.radiation(event.getPlayer().getWorld()).join(event.getPlayer());
    }

    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Player player = event.getPlayer();
        if (player.isSneaking()) {
            return;
        }
        ItemStack stack = event.getItem();
        if (stack == null || stack.getType().isAir()) {
            return;
        }
        String id = BukkitItems.idOf(stack);
        if (!"mek:geiger_counter".equals(id) && !"mek:dosimeter".equals(id)) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - lastUse.getOrDefault(player.getUniqueId(), 0L) < COOLDOWN_MILLIS) {
            return;
        }
        lastUse.put(player.getUniqueId(), now);
        RadiationWorld radiation = plugin.radiation(player.getWorld());
        if ("mek:geiger_counter".equals(id)) {
            double exposure = radiation.exposureAt(player.getLocation());
            I18n.send(player, "radiation.environment", RadiationWorld.format(exposure));
            return;
        }
        DoseTracker dose = radiation.dose(player);
        I18n.send(player, "radiation.personal",
                String.format("%.2f", dose.microSv()),
                I18n.enumName(dose.effect(), dose.effect().zhName()));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (plugin.radiation(player.getWorld()).dose(player).effect() == Radiation.Effect.LETHAL) {
            event.deathMessage(I18n.component(player, "radiation.death", player.getName()));
        }
    }
}
