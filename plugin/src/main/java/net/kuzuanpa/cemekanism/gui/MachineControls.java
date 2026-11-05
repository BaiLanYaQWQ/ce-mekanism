package net.kuzuanpa.cemekanism.gui;

import net.kuzuanpa.cemekanism.machine.MachineBlockEntityController;
import net.kuzuanpa.cemekanism.machine.logic.ResistiveHeaterLogic;
import net.kuzuanpa.cemekanism.platform.GeneratorConfig;
import net.kuzuanpa.cemekanism.storage.QioLogic;
import net.kuzuanpa.cemekanism.storage.QioService;
import net.kuzuanpa.cemekanism.storage.QioFrequencyGui;
import net.kuzuanpa.cemekanism.storage.DigitalMinerFilterGui;
import net.kuzuanpa.cemekanism.storage.DigitalMinerLogic;
import net.kuzuanpa.cemekanism.storage.StoredItems;
import net.kuzuanpa.cemekanism.platform.BukkitItems;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.Locale;

/** Explicit controls over virtual overlay slots; never stored as machine inventory. */
public final class MachineControls {
    private MachineControls() { }
    public static boolean tankControl(MachineBlockEntityController c, int slot) {
        return !c.machine().dumpablePorts().isEmpty() && GuiLayout.forMachine(c.machine(), c.machine().factoryTier(), c.blockId())
                .tankGauges().stream().anyMatch(gauge -> c.machine().dumpablePorts().contains(gauge.port())
                        && gauge.anchors().stream().anyMatch(cell -> cell.index() == slot));
    }
    public static boolean handles(MachineBlockEntityController c, int slot) {
        if (tankControl(c, slot)) return true;
        if (c.machine().blueprint().logic() instanceof ResistiveHeaterLogic) return slot == 10 || slot == 12 || slot == 14 || slot == 19;
        if (c.machine().blueprint().logic() instanceof DigitalMinerLogic) {
            return DigitalMinerGui.handles(slot);
        }
        if (c.machine().blueprint().logic() instanceof QioLogic) {
            boolean filtered = !c.blockId().equals("mek:qio_drive_array") && !c.blockId().equals("mek:qio_dashboard");
            return slot == 26 || filtered && (slot == 20 || slot == 21 || slot == 22)
                    || slot == 24 && (c.blockId().equals("mek:qio_importer") || c.blockId().equals("mek:qio_exporter"))
                    || c.blockId().equals("mek:qio_redstone_adapter") && (slot == 10 || slot == 12 || slot == 14 || slot == 19);
        }
        return false;
    }
    public static void render(MachineBlockEntityController c) {
        var machine = c.machine();
        if (machine.blueprint().logic() instanceof ResistiveHeaterLogic heater) {
            long usage = heater.usage(machine);
            c.inventory().setItem(10, GuiText.button("minecraft:redstone", "降低功率", List.of("每次 -25 J/t，Shift -250 J/t")));
            c.inventory().setItem(14, GuiText.button("minecraft:glowstone_dust", "提高功率", List.of("每次 +25 J/t，Shift +250 J/t")));
            c.inventory().setItem(19, GuiText.button("minecraft:lever", "停止加热", List.of("设为 0 J/t；缓存随功率缩小")));
            c.inventory().setItem(12, GuiText.button("minecraft:blaze_powder", "电阻加热器 · " + usage + " J/t", List.of(
                    "实际消耗 " + machine.lastEnergyUsed() + " J/t", "温度 " + String.format(Locale.ROOT, "%.2f K", machine.heatNode().temperatureK()),
                    "左键恢复默认功率",
                    "降低功率会缩小缓存并截断多余电量")));
        } else if (machine.blueprint().logic() instanceof QioLogic) {
            c.inventory().setItem(26, GuiText.button("minecraft:ender_eye", "选择 QIO 频率", List.of("当前：" + QioService.state(c).frequency)));
            var state = QioService.state(c);
            if (handles(c, 20)) {
                c.inventory().setItem(20, GuiText.button("minecraft:barrier", "清空过滤器", List.of("清除全部 " + state.filter.rules.size() + " 条规则")));
                c.inventory().setItem(21, GuiText.button("minecraft:paper", "过滤模式：" + (state.filter.inverse ? "黑名单" : "白名单"), List.of(state.filter.describe())));
                c.inventory().setItem(22, GuiText.button("minecraft:hopper", "添加物品过滤器", List.of("用光标拿着物品点击此处；不消耗物品", "标签与命名空间过滤：/mek b5 <x y z> filter add <#标签|@命名空间>")));
            }
            if (c.blockId().equals("mek:qio_redstone_adapter")) {
                c.inventory().setItem(10, GuiText.button("minecraft:redstone", "降低阈值", List.of("每次 -1，Shift -64")));
                c.inventory().setItem(14, GuiText.button("minecraft:glowstone_dust", "提高阈值", List.of("每次 +1，Shift +64")));
                c.inventory().setItem(12, GuiText.button("minecraft:comparator", "阈值 " + state.threshold, List.of("信号：" + state.signal)));
                c.inventory().setItem(19, GuiText.button("minecraft:lever", "反转信号：" + state.inverseSignal, List.of()));
            }
            if (handles(c, 24)) {
                boolean enabled = c.blockId().equals("mek:qio_importer") ? state.importWithoutFilter : state.exportWithoutFilter;
                c.inventory().setItem(24, GuiText.button("minecraft:comparator", "无过滤器传输：" + (enabled ? "开" : "关"), List.of("已有过滤器时按过滤器处理")));
            }
        } else if (machine.blueprint().logic() instanceof DigitalMinerLogic miner) {
            renderMiner(c, miner, miner.state(machine));
        }
    }
    public static void click(MachineBlockEntityController c, Player player, int slot, boolean shift, boolean right) {
        if (tankControl(c, slot)) { TankDumpGui.open(c, player); return; }
        if (c.machine().blueprint().logic() instanceof ResistiveHeaterLogic heater) {
            long usage = heater.usage(c.machine()), step = shift ? 250 : 25;
            long next = switch (slot) {
                case 10 -> Math.max(0, usage - step);
                case 14 -> Math.addExact(usage, step);
                case 19 -> 0;
                default -> GeneratorConfig.longValue("heater/resistive_usage_j", 250);
            };
            heater.setUsage(c.machine(), next);
        } else if (c.machine().blueprint().logic() instanceof DigitalMinerLogic miner) {
            clickMiner(c, player, miner, miner.state(c.machine()), slot, shift, right);
        } else if (slot == 26) {
            QioFrequencyGui.open(c.plugin(), c, player, 0);
            return;
        } else {
            var state = QioService.state(c);
            switch (slot) {
                case 20 -> state.filter.rules.clear();
                case 21 -> state.filter.inverse = !state.filter.inverse;
                case 22 -> {
                    var item = player.getItemOnCursor();
                    if (!StoredItems.empty(item)) {
                        String id = BukkitItems.idOf(item);
                        if (!state.filter.rules.contains(id)) state.filter.rules.add(id);
                    }
                }
                case 10 -> state.threshold = Math.max(0, state.threshold - (shift ? 64 : 1));
                case 14 -> { long step = shift ? 64 : 1; state.threshold = state.threshold > Long.MAX_VALUE - step ? Long.MAX_VALUE : state.threshold + step; }
                case 19 -> state.inverseSignal = !state.inverseSignal;
                case 24 -> {
                    if (c.blockId().equals("mek:qio_importer")) state.importWithoutFilter = !state.importWithoutFilter;
                    else state.exportWithoutFilter = !state.exportWithoutFilter;
                }
                default -> { }
            }
        }
        c.markChanged(); MachineGui.updateAnchors(c);
    }

    private static void renderMiner(MachineBlockEntityController c, DigitalMinerLogic miner,
                                    DigitalMinerLogic.State state) {
        c.inventory().setItem(0, GuiText.button(DigitalMinerGui.icon(state.running ? "stop" : "start"),
                state.running ? "停止采矿" : "开始采矿",
                List.of()));
        c.inventory().setItem(1, GuiText.button(DigitalMinerGui.icon("reset"), "重置扫描",
                List.of("游标归零并停止", "过滤器与已采物品保留")));
        c.inventory().setItem(3, GuiText.button(DigitalMinerGui.toggle("silk", state.silk),
                "精准采集",
                List.of("开启后单刻耗能按配置倍数增加")));
        c.inventory().setItem(4, GuiText.button(DigitalMinerGui.toggle("inverse", state.filter.inverse),
                "过滤模式：" + (state.filter.inverse ? "黑名单" : "白名单"),
                List.of()));
        c.inventory().setItem(5, GuiText.button(DigitalMinerGui.toggle("eject", state.autoEject),
                "自动弹出",
                List.of("把产物推到机器背面相邻容器")));
        c.inventory().setItem(6, GuiText.button(DigitalMinerGui.toggle("pull", state.autoPull),
                "自动抽取回填料",
                List.of("从机器上方容器取出所需回填方块")));
        c.inventory().setItem(8, GuiText.button(DigitalMinerGui.icon("filter"), "打开过滤器配置",
                List.of(state.filter.rules.size() + " 条规则", "逐条设置替换目标与要求")));
        c.inventory().setItem(9, GuiText.button(DigitalMinerGui.icon("minus"), "半径：减 1",
                List.of("Shift：-8")));
        c.inventory().setItem(10, GuiText.invisiblePlaceholder());
        c.inventory().setItem(11, GuiText.button(DigitalMinerGui.icon("plus"), "半径：加 1",
                List.of("Shift：+8")));
        c.inventory().setItem(12, GuiText.button(DigitalMinerGui.icon("minus"), "最低 Y：减 1",
                List.of("Shift：-16")));
        c.inventory().setItem(13, GuiText.invisiblePlaceholder());
        c.inventory().setItem(14, GuiText.button(DigitalMinerGui.icon("plus"), "最低 Y：加 1",
                List.of("Shift：+16")));
        c.inventory().setItem(18, GuiText.button(DigitalMinerGui.icon("stats"), "扫描状态",
                List.of(state.status,
                        "游标 " + state.cursor + '/' + state.scan().volume(),
                        "已采 " + state.mined + " 格")));
        c.inventory().setItem(15, GuiText.button(DigitalMinerGui.icon("minus"), "最高 Y：减 1",
                List.of("Shift：-16")));
        c.inventory().setItem(16, GuiText.invisiblePlaceholder());
        c.inventory().setItem(17, GuiText.button(DigitalMinerGui.icon("plus"), "最高 Y：加 1",
                List.of("Shift：+16")));
        c.inventory().setItem(27, GuiText.button(
                state.replace.isBlank() ? DigitalMinerGui.icon("replace") : state.replace,
                "反选模式替换目标：" + (state.replace.isBlank() ? "无" : mekcore.item.ItemNames.nameOf(state.replace)),
                List.of("手持方块左键设置", "右键清除", "反选模式使用此目标")));
        c.inventory().setItem(28, GuiText.button(DigitalMinerGui.toggle("required", state.inverseRequiresReplacement),
                "反选模式要求替换",
                List.of("缺料时暂停，不直接采空")));
        var scan = GuiText.invisiblePlaceholder();
        for (int slot = 21; slot <= 26; slot++) c.inventory().setItem(slot, scan.clone());
        c.inventory().setItem(19, GuiText.button(DigitalMinerGui.icon("buffer"), "产物缓冲",
                List.of("存量 " + state.buffer.total() + "/" + state.buffer.capacity(),
                        "开启自动弹出以输出到背面容器")));
        c.inventory().setItem(36, GuiText.button(DigitalMinerGui.icon("energy"), "能量",
                List.of(GuiText.format(c.machine().snapshot().energyJoules()) + " / "
                        + GuiText.format(c.machine().snapshot().energyCapacityJ()) + " J")));
        c.inventory().setItem(7, GuiText.button(DigitalMinerGui.toggle("visuals", state.visuals),
                "范围可视化",
                List.of("使用低粒子密度显示采矿包围盒")));
        c.inventory().setItem(29, GuiText.button(DigitalMinerGui.icon("clear"), "直接采空",
                List.of("点击清除全局反选替换目标",
                        "逐规则替换请打开过滤器配置")));
    }

    private static void clickMiner(MachineBlockEntityController c, Player player,
                                   DigitalMinerLogic miner, DigitalMinerLogic.State state,
                                   int slot, boolean shift, boolean right) {
        int step = shift ? 8 : 1;
        switch (slot) {
            case 0 -> {
                if (state.running) miner.stop(c.machine()); else miner.start(c.machine());
            }
            case 1 -> miner.reset(c.machine());
            case 3 -> state.silk = !state.silk;
            case 4 -> state.filter.inverse = !state.filter.inverse;
            case 5 -> state.autoEject = !state.autoEject;
            case 6 -> state.autoPull = !state.autoPull;
            case 8 -> {
                DigitalMinerFilterGui.open(c, player, 0);
                return;
            }
            case 9, 11 -> {
                int delta = slot == 9 ? -step : step;
                miner.setRadius(c.machine(), Math.max(0,
                        Math.min(DigitalMinerLogic.MAX_RADIUS, state.radius + delta)));
            }
            case 12, 14 -> {
                int delta = slot == 12 ? -(shift ? 16 : 1) : (shift ? 16 : 1);
                miner.setMinY(c.machine(), state.minY + delta);
            }
            case 15, 17 -> {
                int delta = slot == 15 ? -(shift ? 16 : 1) : (shift ? 16 : 1);
                miner.setMaxY(c.machine(), state.maxY + delta);
            }
            case 27 -> {
                if (right) {
                    state.replace = "";
                } else {
                    var held = player.getItemOnCursor();
                    if (!StoredItems.empty(held) && held.getType().isBlock()) {
                        state.replace = BukkitItems.idOf(held);
                    } else {
                        player.sendActionBar(net.kyori.adventure.text.Component.text("请手持一个方块"));
                    }
                }
            }
            case 28 -> state.inverseRequiresReplacement = !state.inverseRequiresReplacement;
            case 7 -> state.visuals = !state.visuals;
            case 29 -> state.replace = "";
            default -> { }
        }
        if (slot == 4) {
            miner.reset(c.machine());
        }
    }

}
