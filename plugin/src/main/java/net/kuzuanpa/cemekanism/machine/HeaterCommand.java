package net.kuzuanpa.cemekanism.machine;

import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.kuzuanpa.cemekanism.machine.logic.ResistiveHeaterLogic;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/** Exact J/t input, shared by players and headless validation. */
public final class HeaterCommand {
    private HeaterCommand() { }
    public static void run(CeMekanismPlugin plugin, CommandSender sender, String[] args) {
        if (args.length < 4) { sender.sendMessage("/mek heater <x y z> [J/t] [world=世界]"); return; }
        try {
            World world = sender instanceof Player player ? player.getWorld() : Bukkit.getWorlds().getFirst();
            String last = args[args.length - 1];
            if (last.startsWith("world=")) world = Bukkit.getWorld(last.substring(6));
            if (world == null) throw new IllegalArgumentException("世界不存在");
            var c = plugin.machineAtQuiet(world, Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
            if (c == null || !(c.machine().blueprint().logic() instanceof ResistiveHeaterLogic heater)) throw new IllegalArgumentException("该位置不是电阻加热器");
            if (sender instanceof Player p && !c.machine().canAccess(p.getUniqueId().toString())) throw new IllegalArgumentException("无权操作此机器");
            if (args.length > 4 && !args[4].startsWith("world=")) { heater.setUsage(c.machine(), Long.parseLong(args[4])); c.markChanged(); }
            sender.sendMessage("电阻加热器：设定 " + heater.usage(c.machine()) + " J/t · 实际 " + c.machine().lastEnergyUsed()
                    + " J/t · 缓存 " + c.machine().energyCapacity() + " J · 温度 " + c.machine().heatNode().temperatureK() + " K");
        } catch (IllegalArgumentException ex) { sender.sendMessage("加热器：" + ex.getMessage()); }
    }
}
