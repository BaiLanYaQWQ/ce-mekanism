package net.kuzuanpa.cemekanism.storage;

import mekcore.machine.SecurityLevel;
import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.kuzuanpa.cemekanism.i18n.I18n;
import net.kuzuanpa.cemekanism.platform.BukkitItems;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import java.util.*;
import java.util.stream.IntStream;

/** Player and headless entry points use the same storage engines and access checks. */
public final class StorageCommand {
    private StorageCommand() { }
    public static void run(CeMekanismPlugin plugin, CommandSender sender, String[] args) {
        try {
            if (args.length >= 3 && args[1].equals("portable")) {
                if (!(sender instanceof Player p)) throw new IllegalArgumentException(I18n.tr(sender, "storage.portable_player_only"));
                ItemStack item = p.getInventory().getItemInMainHand();
                if (StoredItems.empty(item) || !BukkitItems.idOf(item).equals("mek:portable_qio_dashboard")) {
                    throw new IllegalArgumentException(I18n.tr(sender, "storage.portable_missing"));
                }
                plugin.qio().require(args[2], p.getUniqueId().toString()); var meta = item.getItemMeta();
                meta.getPersistentDataContainer().set(StorageListener.PORTABLE, PersistentDataType.STRING, args[2]);
                item.setItemMeta(meta);
                I18n.send(sender, "storage.portable_bound", args[2]);
                return;
            }
            if (args.length < 5) {
                I18n.send(sender, "storage.command.usage");
                return;
            }
            World world = sender instanceof Player p ? p.getWorld() : Bukkit.getWorlds().get(0);
            String last = args[args.length-1]; if (last.startsWith("world=")) world = Bukkit.getWorld(last.substring(6));
            if (world == null) throw new IllegalArgumentException(I18n.tr(sender, "command.world_missing"));
            var c = plugin.machineAtQuiet(world, Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
            if (c == null || !StorageListener.isB5(c)) throw new IllegalArgumentException(I18n.tr(sender, "storage.not_b5"));
            String actor = sender instanceof Player p ? p.getUniqueId().toString() : "console";
            if (c.machine().owner() == null || c.machine().owner().isEmpty()) c.claim(actor);
            if (sender instanceof Player && !c.machine().canAccess(actor)) {
                throw new IllegalArgumentException(I18n.tr(sender, "machine.no_operate_permission"));
            }
            Object logic = c.machine().blueprint().logic();
            switch (args[4]) {
                case "status" -> {
                    sender.sendMessage(c.machine().describe());
                    if (logic instanceof DigitalMinerLogic miner) {
                        var s = miner.state(c.machine());
                        I18n.send(sender, "storage.miner.status", I18n.minerStatus(s.status), s.running,
                                s.radius, 2 * s.radius + 1, 2 * s.radius + 1, s.minY, s.maxY,
                                s.cursor, s.scan().volume(), s.mined, s.buffer.total(), s.silk,
                                s.autoEject, s.autoPull,
                                s.replace.isBlank() ? I18n.tr(sender, "state.none") : s.replace,
                                s.filter.describe());
                    } else if (logic instanceof QioLogic) sender.sendMessage(plugin.qio().describe(c, actor));
                    else if (logic instanceof BinLogic bin) {
                        I18n.send(sender, "storage.bin", bin.store(c.machine()).total(),
                                bin.store(c.machine()).capacity());
                    } else if (logic instanceof SorterLogic sorter) {
                        I18n.send(sender, "storage.sorter.status", sorter.state(c.machine()).filter.describe(),
                                sorter.state(c.machine()).colour);
                    }
                }
                case "miner" -> {
                    if (!(logic instanceof DigitalMinerLogic miner)) throw new IllegalArgumentException(I18n.tr(sender, "storage.not_miner"));
                    var s = miner.state(c.machine());
                    switch (args[5]) {
                        case "range" -> miner.configure(c.machine(), Integer.parseInt(args[6]), Integer.parseInt(args[7]), Integer.parseInt(args[8]));
                        case "start" -> miner.start(c.machine());
                        case "stop" -> miner.stop(c.machine());
                        case "reset" -> miner.reset(c.machine());
                        case "silk" -> s.silk = bool(args[6]);
                        case "eject" -> s.autoEject = bool(args[6]);
                        case "pull" -> s.autoPull = bool(args[6]);
                        case "visuals" -> s.visuals = bool(args[6]);
                        case "inverse" -> s.filter.inverse = bool(args[6]);
                        case "requires" -> s.inverseRequiresReplacement = bool(args[6]);
                        case "replace" -> {
                            String id = args[6]; Material type = Material.matchMaterial(id);
                            if (!id.equals("none") && (type == null || !type.isBlock() || type.isAir() || (type == Material.WATER || type == Material.LAVA) || type.getHardness() < 0)) {
                                throw new IllegalArgumentException(I18n.tr(sender, "storage.miner.bad_replacement"));
                            }
                            s.replace = id.equals("none") ? "" : type.getKey().toString();
                        }
                        default -> throw new IllegalArgumentException(I18n.tr(sender, "storage.miner.unknown_action"));
                    }
                    c.markChanged(); I18n.send(sender, "storage.miner.updated");
                }
                case "filter" -> {
                    ItemSelection filter = logic instanceof DigitalMinerLogic miner ? miner.state(c.machine()).filter
                            : logic instanceof QioLogic q ? q.state(c.machine()).filter
                            : logic instanceof SorterLogic sorter ? sorter.state(c.machine()).filter : null;
                    if (filter == null) throw new IllegalArgumentException(I18n.tr(sender, "storage.filter_unsupported"));
                    switch (args[5]) {
                        case "white" -> filter.inverse = false;
                        case "black" -> filter.inverse = true;
                        case "clear" -> filter.rules.clear();
                        case "add" -> filter.add(args[6]);
                        case "remove" -> filter.remove(args[6]);
                        case "enabled" -> filter.setEnabled(args[6], bool(args[7]));
                        case "replace" -> {
                            String target = args[7].equalsIgnoreCase("none") ? "" : args[7];
                            if (!target.isEmpty()) {
                                Material type = Material.matchMaterial(target);
                                if (type == null || !type.isBlock() || type.isAir()
                                        || type.getHardness() < 0 || type == Material.WATER || type == Material.LAVA) {
                                    throw new IllegalArgumentException(I18n.tr(sender, "storage.filter.bad_target"));
                                }
                                target = type.getKey().toString();
                            }
                            filter.setReplacementTarget(args[6], target);
                        }
                        case "requires" -> filter.setRequiresReplacement(args[6], bool(args[7]));
                        default -> throw new IllegalArgumentException(I18n.tr(sender, "storage.filter.usage"));
                    }
                    if (logic instanceof DigitalMinerLogic miner) miner.reset(c.machine());
                    c.markChanged(); sender.sendMessage(filter.describe());
                }
                case "qio" -> {
                    if (!(logic instanceof QioLogic)) throw new IllegalArgumentException(I18n.tr(sender, "storage.not_qio"));
                    var s = QioService.state(c);
                    switch (args[5]) {
                        case "create" -> { String id = plugin.qio().create(args[6], actor); plugin.qio().join(c, id, actor); I18n.send(sender, "storage.qio.created", id); }
                        case "join" -> { plugin.qio().join(c, args[6], actor); I18n.send(sender, "storage.qio.joined", args[6]); }
                        case "leave" -> { s.frequency = ""; I18n.send(sender, "storage.qio.left"); }
                        case "unfiltered" -> {
                            boolean enabled = bool(args[6]);
                            if (c.blockId().equals("mek:qio_importer")) s.importWithoutFilter = enabled;
                            else if (c.blockId().equals("mek:qio_exporter")) s.exportWithoutFilter = enabled;
                            else throw new IllegalArgumentException(I18n.tr(sender, "storage.qio.unfiltered_unsupported"));
                            I18n.send(sender, "qio.unfiltered_transfer", I18n.tr(sender, enabled ? "state.on" : "state.off"));
                        }
                        case "security" -> { plugin.qio().security(s.frequency, actor, SecurityLevel.valueOf(args[6].toUpperCase(Locale.ROOT)), null, false); I18n.send(sender, "storage.qio.security_updated"); }
                        case "trust" -> { plugin.qio().security(s.frequency, actor, null, args[7], args[6].equals("add")); I18n.send(sender, "storage.qio.trust_updated"); }
                        case "list" -> {
                            sender.sendMessage(plugin.qio().describe(c, actor));
                            int i = 0; for (var e : plugin.qio().entries(s.frequency, actor).entrySet()) sender.sendMessage((i++) + ": " + BukkitItems.idOf(StoredItems.item(e.getKey(), 1)) + " ×" + e.getValue());
                        }
                        case "deposit" -> {
                            if (!(sender instanceof Player p)) throw new IllegalArgumentException(I18n.tr(sender, "storage.qio.deposit_player"));
                            ItemStack held = p.getInventory().getItemInMainHand();
                            if (!StoredItems.empty(held)) { int n = (int)plugin.qio().insert(s.frequency, actor, held, held.getAmount(), true); held.setAmount(held.getAmount()-n); I18n.send(sender, "storage.qio.deposited", n); }
                        }
                        case "open" -> {
                            if (!(sender instanceof Player p)) throw new IllegalArgumentException(I18n.tr(sender, "storage.qio.open_player"));
                            QioDashboard.open(plugin, c, s.frequency, args.length > 6 ? args[6] : "", p, 0);
                        }
                        case "threshold" -> {
                            long threshold = Long.parseLong(args[6]);
                            if (threshold < 0) throw new IllegalArgumentException(I18n.tr(sender, "storage.qio.negative_threshold"));
                            boolean inverse = args.length > 7 && !args[7].startsWith("world=") && bool(args[7]);
                            s.threshold = threshold; s.inverseSignal = inverse;
                        }
                        case "debug-insert" -> {
                            if (sender instanceof Player && !sender.hasPermission("cemekanism.admin")) throw new IllegalArgumentException(I18n.tr(sender, "storage.admin_required"));
                            ItemStack item = BukkitItems.create(args[6]); if (item == null) throw new IllegalArgumentException(I18n.tr(sender, "storage.item_missing"));
                            I18n.send(sender, "storage.qio.inserted",
                                    plugin.qio().insert(s.frequency, actor, item, Long.parseLong(args[7]), true));
                        }
                        case "debug-extract" -> {
                            if (sender instanceof Player && !sender.hasPermission("cemekanism.admin")) throw new IllegalArgumentException(I18n.tr(sender, "storage.admin_required"));
                            ItemStack item = BukkitItems.create(args[6]); if (item == null) throw new IllegalArgumentException(I18n.tr(sender, "storage.item_missing"));
                            I18n.send(sender, "storage.qio.extracted",
                                    plugin.qio().extract(s.frequency, actor, StoredItems.key(item), Long.parseLong(args[7]), true));
                        }
                        default -> throw new IllegalArgumentException(I18n.tr(sender, "storage.qio.unknown_action"));
                    }
                    c.markChanged();
                }
                case "drive" -> {
                    if (!c.blockId().equals("mek:qio_drive_array")) throw new IllegalArgumentException(I18n.tr(sender, "storage.drive.not_array"));
                    int slot = Integer.parseInt(args[6]); if (slot < 0 || slot >= 12) throw new IllegalArgumentException(I18n.tr(sender, "storage.drive.bad_slot"));
                    String name = "drive" + slot;
                    if (args[5].equals("put")) {
                        if (!StoredItems.empty(c.slotContent(name))) throw new IllegalArgumentException(I18n.tr(sender, "storage.drive.slot_occupied"));
                        ItemStack held;
                        if (sender instanceof Player p) held = p.getInventory().getItemInMainHand();
                        else held = BukkitItems.create(args[7]);
                        if (StoredItems.empty(held) || !QioService.TIERS.containsKey(BukkitItems.idOf(held))) throw new IllegalArgumentException(I18n.tr(sender, "storage.drive.required"));
                        ItemStack one = held.clone(); one.setAmount(1); c.putInSlot(name, one);
                        if (sender instanceof Player) held.setAmount(held.getAmount()-1);
                    } else if (args[5].equals("take")) {
                        if (!(sender instanceof Player p)) throw new IllegalArgumentException(I18n.tr(sender, "storage.drive.take_player"));
                        ItemStack item = c.slotContent(name); if (StoredItems.empty(item)) return;
                        InventoryAccess target = new InventoryAccess(p.getInventory(), IntStream.range(0,36).boxed().toList(), () -> { });
                        if (target.insert(item, 1, false) == 0) throw new IllegalArgumentException(I18n.tr(sender, "storage.inventory_full"));
                        target.insert(item, 1, true); c.putInSlot(name, null);
                    } else throw new IllegalArgumentException(I18n.tr(sender, "storage.drive.usage"));
                    I18n.send(sender, "storage.drive.updated");
                }
                case "sorter" -> {
                    if (!(logic instanceof SorterLogic sorter)) throw new IllegalArgumentException(I18n.tr(sender, "storage.sorter.not_sorter"));
                    var s = sorter.state(c.machine());
                    switch (args[5]) {
                        case "colour" -> { int colour = Integer.parseInt(args[6]); if (colour < -1 || colour >= 12) throw new IllegalArgumentException(I18n.tr(sender, "storage.sorter.bad_colour")); s.colour = colour; }
                        case "single" -> s.single = bool(args[6]);
                        case "roundrobin" -> s.roundRobin = bool(args[6]);
                        default -> throw new IllegalArgumentException(I18n.tr(sender, "storage.sorter.usage"));
                    }
                    c.markChanged(); I18n.send(sender, "storage.sorter.updated");
                }
                default -> throw new IllegalArgumentException(I18n.tr(sender, "storage.unknown"));
            }
        } catch (IndexOutOfBoundsException e) { I18n.send(sender, "storage.args_missing"); }
        catch (IllegalArgumentException | IllegalStateException e) { I18n.send(sender, "storage.error", e.getMessage()); }
    }
    private static boolean bool(String input) {
        if (!input.equalsIgnoreCase("true") && !input.equalsIgnoreCase("false")) {
            throw new IllegalArgumentException(I18n.tr("storage.boolean_required"));
        }
        return Boolean.parseBoolean(input);
    }
}
