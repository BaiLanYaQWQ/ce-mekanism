package net.kuzuanpa.cemekanism.storage;

import mekcore.machine.SecurityLevel;
import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.kuzuanpa.cemekanism.platform.BukkitItems;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import java.util.*;

/** Player and headless entry points use the same storage engines and access checks. */
public final class StorageCommand {
    private StorageCommand() { }
    public static void run(CeMekanismPlugin plugin, CommandSender sender, String[] args) {
        try {
            if (args.length >= 3 && args[1].equals("portable")) {
                if (!(sender instanceof Player p)) throw new IllegalArgumentException("需要玩家手持便携式 QIO 仪表板");
                ItemStack item = p.getInventory().getItemInMainHand();
                if (StoredItems.empty(item) || !BukkitItems.idOf(item).equals("mek:portable_qio_dashboard")) throw new IllegalArgumentException("手中不是便携式 QIO 仪表板");
                plugin.qio().require(args[2], p.getUniqueId().toString()); var meta = item.getItemMeta();
                meta.getPersistentDataContainer().set(StorageListener.PORTABLE, PersistentDataType.STRING, args[2]); item.setItemMeta(meta); sender.sendMessage("已绑定 " + args[2]); return;
            }
            if (args.length < 5) {
                sender.sendMessage("/mek b5 <x y z> status | miner range <半径0..32> <最低Y> <最高Y> | miner start/stop/reset/silk/eject/pull/visuals/replace/inverse/requires [...] | filter white/black/clear/add/remove/enabled/replace/requires [...] | qio create/join/security/trust/list/deposit/open [...] | drive put/take <槽0..11> | sorter colour/single/roundrobin [...]");
                return;
            }
            World world = sender instanceof Player p ? p.getWorld() : Bukkit.getWorlds().get(0);
            String last = args[args.length-1]; if (last.startsWith("world=")) world = Bukkit.getWorld(last.substring(6));
            if (world == null) throw new IllegalArgumentException("世界不存在");
            var c = plugin.machineAtQuiet(world, Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
            if (c == null || !StorageListener.isB5(c)) throw new IllegalArgumentException("该位置不是 B5 机器");
            String actor = sender instanceof Player p ? p.getUniqueId().toString() : "console";
            if (c.machine().owner() == null || c.machine().owner().isEmpty()) c.claim(actor);
            if (sender instanceof Player && !c.machine().canAccess(actor)) throw new IllegalArgumentException("无权操作此机器");
            Object logic = c.machine().blueprint().logic();
            switch (args[4]) {
                case "status" -> {
                    sender.sendMessage(c.machine().describe());
                    if (logic instanceof DigitalMinerLogic miner) {
                        var s = miner.state(c.machine()); sender.sendMessage("采矿 " + s.status + " · 运行 " + s.running + " · 半径 " + s.radius + " (" + (2*s.radius+1) + "×" + (2*s.radius+1) + ") · Y " + s.minY + ".." + s.maxY
                                + " · 游标 " + s.cursor + '/' + s.scan().volume() + " · 已采 " + s.mined + " · 缓冲 " + s.buffer.total() + " · 精准 " + s.silk
                                + " · 弹出/抽取 " + s.autoEject + '/' + s.autoPull + " · 回填 " + (s.replace.isBlank() ? "无" : s.replace) + " · " + s.filter.describe());
                    } else if (logic instanceof QioLogic) sender.sendMessage(plugin.qio().describe(c, actor));
                    else if (logic instanceof BinLogic bin) sender.sendMessage("箱柜缓冲 " + bin.store(c.machine()).total() + '/' + bin.store(c.machine()).capacity());
                    else if (logic instanceof SorterLogic sorter) sender.sendMessage("分类 " + sorter.state(c.machine()).filter.describe() + " · 颜色 " + sorter.state(c.machine()).colour);
                }
                case "miner" -> {
                    if (!(logic instanceof DigitalMinerLogic miner)) throw new IllegalArgumentException("不是采矿机");
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
                            if (!id.equals("none") && (type == null || !type.isBlock() || type.isAir() || (type == Material.WATER || type == Material.LAVA) || type.getHardness() < 0)) throw new IllegalArgumentException("回填必须是原版可放置方块或 none");
                            s.replace = id.equals("none") ? "" : type.getKey().toString();
                        }
                        default -> throw new IllegalArgumentException("未知采矿机操作");
                    }
                    c.markChanged(); sender.sendMessage("采矿机已更新");
                }
                case "filter" -> {
                    ItemSelection filter = logic instanceof DigitalMinerLogic miner ? miner.state(c.machine()).filter
                            : logic instanceof QioLogic q ? q.state(c.machine()).filter
                            : logic instanceof SorterLogic sorter ? sorter.state(c.machine()).filter : null;
                    if (filter == null) throw new IllegalArgumentException("此机器不使用过滤器");
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
                                    throw new IllegalArgumentException("替换目标必须是原版可放置方块");
                                }
                                target = type.getKey().toString();
                            }
                            filter.setReplacementTarget(args[6], target);
                        }
                        case "requires" -> filter.setRequiresReplacement(args[6], bool(args[7]));
                        default -> throw new IllegalArgumentException("过滤器操作：white/black/clear/add/remove/enabled/replace/requires");
                    }
                    if (logic instanceof DigitalMinerLogic miner) miner.reset(c.machine());
                    c.markChanged(); sender.sendMessage(filter.describe());
                }
                case "qio" -> {
                    if (!(logic instanceof QioLogic)) throw new IllegalArgumentException("不是 QIO 组件");
                    var s = QioService.state(c);
                    switch (args[5]) {
                        case "create" -> { String id = plugin.qio().create(args[6], actor); plugin.qio().join(c, id, actor); sender.sendMessage("频率 " + id); }
                        case "join" -> { plugin.qio().join(c, args[6], actor); sender.sendMessage("已加入 " + args[6]); }
                        case "leave" -> { s.frequency = ""; sender.sendMessage("已离开频率"); }
                        case "unfiltered" -> {
                            boolean enabled = bool(args[6]);
                            if (c.blockId().equals("mek:qio_importer")) s.importWithoutFilter = enabled;
                            else if (c.blockId().equals("mek:qio_exporter")) s.exportWithoutFilter = enabled;
                            else throw new IllegalArgumentException("仅输入器/输出器支持无过滤器传输开关");
                            sender.sendMessage("无过滤器传输：" + enabled);
                        }
                        case "security" -> { plugin.qio().security(s.frequency, actor, SecurityLevel.valueOf(args[6].toUpperCase(Locale.ROOT)), null, false); sender.sendMessage("频率权限已更新"); }
                        case "trust" -> { plugin.qio().security(s.frequency, actor, null, args[7], args[6].equals("add")); sender.sendMessage("信任名单已更新"); }
                        case "list" -> {
                            sender.sendMessage(plugin.qio().describe(c, actor));
                            int i = 0; for (var e : plugin.qio().entries(s.frequency, actor).entrySet()) sender.sendMessage((i++) + ": " + BukkitItems.idOf(StoredItems.item(e.getKey(), 1)) + " ×" + e.getValue());
                        }
                        case "deposit" -> {
                            if (!(sender instanceof Player p)) throw new IllegalArgumentException("玩家手持物品后使用 deposit");
                            ItemStack held = p.getInventory().getItemInMainHand();
                            if (!StoredItems.empty(held)) { int n = (int)plugin.qio().insert(s.frequency, actor, held, held.getAmount(), true); held.setAmount(held.getAmount()-n); sender.sendMessage("已存入 " + n); }
                        }
                        case "open" -> {
                            if (!(sender instanceof Player p)) throw new IllegalArgumentException("需要玩家打开仪表板");
                            QioDashboard.open(plugin, c, s.frequency, args.length > 6 ? args[6] : "", p, 0);
                        }
                        case "threshold" -> {
                            long threshold = Long.parseLong(args[6]);
                            if (threshold < 0) throw new IllegalArgumentException("阈值不可为负");
                            boolean inverse = args.length > 7 && !args[7].startsWith("world=") && bool(args[7]);
                            s.threshold = threshold; s.inverseSignal = inverse;
                        }
                        case "debug-insert" -> {
                            if (sender instanceof Player && !sender.hasPermission("cemekanism.admin")) throw new IllegalArgumentException("调试注入需要管理员权限");
                            ItemStack item = BukkitItems.create(args[6]); if (item == null) throw new IllegalArgumentException("物品不存在");
                            sender.sendMessage("QIO 插入 " + plugin.qio().insert(s.frequency, actor, item, Long.parseLong(args[7]), true));
                        }
                        case "debug-extract" -> {
                            if (sender instanceof Player && !sender.hasPermission("cemekanism.admin")) throw new IllegalArgumentException("调试提取需要管理员权限");
                            ItemStack item = BukkitItems.create(args[6]); if (item == null) throw new IllegalArgumentException("物品不存在");
                            sender.sendMessage("QIO 提取 " + plugin.qio().extract(s.frequency, actor, StoredItems.key(item), Long.parseLong(args[7]), true));
                        }
                        default -> throw new IllegalArgumentException("未知 QIO 操作");
                    }
                    c.markChanged();
                }
                case "drive" -> {
                    if (!c.blockId().equals("mek:qio_drive_array")) throw new IllegalArgumentException("不是驱动器阵列");
                    int slot = Integer.parseInt(args[6]); if (slot < 0 || slot >= 12) throw new IllegalArgumentException("槽位为0..11");
                    String name = "drive" + slot;
                    if (args[5].equals("put")) {
                        if (!StoredItems.empty(c.slotContent(name))) throw new IllegalArgumentException("槽位非空");
                        ItemStack held;
                        if (sender instanceof Player p) held = p.getInventory().getItemInMainHand();
                        else held = BukkitItems.create(args[7]);
                        if (StoredItems.empty(held) || !QioService.TIERS.containsKey(BukkitItems.idOf(held))) throw new IllegalArgumentException("需要 QIO 驱动器");
                        ItemStack one = held.clone(); one.setAmount(1); c.putInSlot(name, one);
                        if (sender instanceof Player) held.setAmount(held.getAmount()-1);
                    } else if (args[5].equals("take")) {
                        if (!(sender instanceof Player p)) throw new IllegalArgumentException("取盘需要玩家接收");
                        ItemStack item = c.slotContent(name); if (StoredItems.empty(item)) return;
                        InventoryAccess target = new InventoryAccess(p.getInventory(), java.util.stream.IntStream.range(0,36).boxed().toList(), () -> { });
                        if (target.insert(item, 1, false) == 0) throw new IllegalArgumentException("背包已满");
                        target.insert(item, 1, true); c.putInSlot(name, null);
                    } else throw new IllegalArgumentException("驱动器操作：put/take");
                    sender.sendMessage("驱动器槽已更新");
                }
                case "sorter" -> {
                    if (!(logic instanceof SorterLogic sorter)) throw new IllegalArgumentException("不是分类机");
                    var s = sorter.state(c.machine());
                    switch (args[5]) {
                        case "colour" -> { int colour = Integer.parseInt(args[6]); if (colour < -1 || colour >= 12) throw new IllegalArgumentException("颜色为-1或0..11"); s.colour = colour; }
                        case "single" -> s.single = bool(args[6]);
                        case "roundrobin" -> s.roundRobin = bool(args[6]);
                        default -> throw new IllegalArgumentException("分类机操作：colour/single/roundrobin");
                    }
                    c.markChanged(); sender.sendMessage("分类机已更新");
                }
                default -> throw new IllegalArgumentException("未知 B5 操作");
            }
        } catch (IndexOutOfBoundsException e) { sender.sendMessage("B5 参数不足，用 /mek b5 查看用法"); }
        catch (IllegalArgumentException | IllegalStateException e) { sender.sendMessage("B5：" + e.getMessage()); }
    }
    private static boolean bool(String input) {
        if (!input.equalsIgnoreCase("true") && !input.equalsIgnoreCase("false")) throw new IllegalArgumentException("需要 true/false");
        return Boolean.parseBoolean(input);
    }
}
