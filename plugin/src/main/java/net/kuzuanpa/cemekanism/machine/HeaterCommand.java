package net.kuzuanpa.cemekanism.machine;

import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.kuzuanpa.cemekanism.machine.logic.ResistiveHeaterLogic;
import net.kuzuanpa.cemekanism.i18n.I18n;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/** Exact J/t input, shared by players and headless validation. */
public final class HeaterCommand {
    private HeaterCommand() { }
    public static void run(CeMekanismPlugin plugin, CommandSender sender, String[] args) {
        if (args.length < 4) { I18n.send(sender, "heater.usage"); return; }
        try {
            World world = sender instanceof Player player ? player.getWorld() : Bukkit.getWorlds().getFirst();
            String last = args[args.length - 1];
            if (last.startsWith("world=")) world = Bukkit.getWorld(last.substring(6));
            if (world == null) throw new IllegalArgumentException(I18n.tr(sender, "command.world_missing"));
            var c = plugin.machineAtQuiet(world, Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
            if (c == null || !(c.machine().blueprint().logic() instanceof ResistiveHeaterLogic heater)) {
                throw new IllegalArgumentException(I18n.tr(sender, "heater.not_heater"));
            }
            if (sender instanceof Player p && !c.machine().canAccess(p.getUniqueId().toString())) {
                throw new IllegalArgumentException(I18n.tr(sender, "machine.no_operate_permission"));
            }
            if (args.length > 4 && !args[4].startsWith("world=")) { heater.setUsage(c.machine(), Long.parseLong(args[4])); c.markChanged(); }
            I18n.send(sender, "heater.status", heater.usage(c.machine()), c.machine().lastEnergyUsed(),
                    c.machine().energyCapacity(), c.machine().heatNode().temperatureK());
        } catch (IllegalArgumentException ex) { I18n.send(sender, "heater.error", ex.getMessage()); }
    }
}
