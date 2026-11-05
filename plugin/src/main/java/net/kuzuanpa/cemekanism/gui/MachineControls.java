package net.kuzuanpa.cemekanism.gui;

import net.kuzuanpa.cemekanism.machine.MachineBlockEntityController;
import net.kuzuanpa.cemekanism.machine.logic.ResistiveHeaterLogic;
import net.kuzuanpa.cemekanism.i18n.I18n;
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
            c.inventory().setItem(10, GuiText.button("minecraft:redstone",
                    I18n.tr("heater.decrease"), List.of(I18n.tr("heater.decrease_hint"))));
            c.inventory().setItem(14, GuiText.button("minecraft:glowstone_dust",
                    I18n.tr("heater.increase"), List.of(I18n.tr("heater.increase_hint"))));
            c.inventory().setItem(19, GuiText.button("minecraft:lever",
                    I18n.tr("heater.stop"), List.of(I18n.tr("heater.stop_hint"))));
            c.inventory().setItem(12, GuiText.button("minecraft:blaze_powder",
                    I18n.tr("heater.title", usage), List.of(
                            I18n.tr("heater.actual_usage", machine.lastEnergyUsed()),
                            I18n.tr("heater.temperature",
                                    String.format(Locale.ROOT, "%.2f", machine.heatNode().temperatureK())),
                            I18n.tr("heater.reset_hint"),
                            I18n.tr("heater.cache_hint"))));
        } else if (machine.blueprint().logic() instanceof QioLogic) {
            c.inventory().setItem(26, GuiText.button("minecraft:ender_eye",
                    I18n.tr("qio.select_frequency"),
                    List.of(I18n.tr("qio.current_frequency", QioService.state(c).frequency))));
            var state = QioService.state(c);
            if (handles(c, 20)) {
                c.inventory().setItem(20, GuiText.button("minecraft:barrier",
                        I18n.tr("qio.clear_filter"),
                        List.of(I18n.tr("qio.clear_filter_hint", state.filter.rules.size()))));
                c.inventory().setItem(21, GuiText.button("minecraft:paper",
                        I18n.tr("qio.filter_mode",
                                I18n.tr(state.filter.inverse ? "filter.blacklist" : "filter.whitelist")),
                        List.of(state.filter.describe())));
                c.inventory().setItem(22, GuiText.button("minecraft:hopper",
                        I18n.tr("qio.add_filter"), List.of(
                                I18n.tr("qio.add_filter_hint"),
                                I18n.tr("qio.add_filter_command"))));
            }
            if (c.blockId().equals("mek:qio_redstone_adapter")) {
                c.inventory().setItem(10, GuiText.button("minecraft:redstone",
                        I18n.tr("qio.threshold_decrease"), List.of(I18n.tr("qio.threshold_decrease_hint"))));
                c.inventory().setItem(14, GuiText.button("minecraft:glowstone_dust",
                        I18n.tr("qio.threshold_increase"), List.of(I18n.tr("qio.threshold_increase_hint"))));
                c.inventory().setItem(12, GuiText.button("minecraft:comparator",
                        I18n.tr("qio.threshold", state.threshold),
                        List.of(I18n.tr("qio.signal", state.signal))));
                c.inventory().setItem(19, GuiText.button("minecraft:lever",
                        I18n.tr("qio.inverse_signal", I18n.tr(state.inverseSignal ? "state.on" : "state.off")),
                        List.of()));
            }
            if (handles(c, 24)) {
                boolean enabled = c.blockId().equals("mek:qio_importer") ? state.importWithoutFilter : state.exportWithoutFilter;
                c.inventory().setItem(24, GuiText.button("minecraft:comparator",
                        I18n.tr("qio.unfiltered_transfer", I18n.tr(enabled ? "state.on" : "state.off")),
                        List.of(I18n.tr("qio.unfiltered_transfer_hint"))));
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
                I18n.tr(state.running ? "miner.stop" : "miner.start"),
                List.of()));
        c.inventory().setItem(1, GuiText.button(DigitalMinerGui.icon("reset"), I18n.tr("miner.reset"),
                List.of(I18n.tr("miner.reset_hint"), I18n.tr("miner.reset_keep"))));
        c.inventory().setItem(3, GuiText.button(DigitalMinerGui.toggle("silk", state.silk),
                I18n.tr("miner.silk"),
                List.of(I18n.tr("miner.silk_hint"))));
        c.inventory().setItem(4, GuiText.button(DigitalMinerGui.toggle("inverse", state.filter.inverse),
                I18n.tr("qio.filter_mode",
                        I18n.tr(state.filter.inverse ? "filter.blacklist" : "filter.whitelist")),
                List.of()));
        c.inventory().setItem(5, GuiText.button(DigitalMinerGui.toggle("eject", state.autoEject),
                I18n.tr("miner.auto_eject"),
                List.of(I18n.tr("miner.auto_eject_hint"))));
        c.inventory().setItem(6, GuiText.button(DigitalMinerGui.toggle("pull", state.autoPull),
                I18n.tr("miner.auto_pull"),
                List.of(I18n.tr("miner.auto_pull_hint"))));
        c.inventory().setItem(8, GuiText.button(DigitalMinerGui.icon("filter"), I18n.tr("miner.open_filters"),
                List.of(I18n.tr("filter.rule_count", state.filter.rules.size()),
                        I18n.tr("miner.filter_edit_hint"))));
        c.inventory().setItem(9, GuiText.button(DigitalMinerGui.icon("minus"), I18n.tr("miner.radius_decrease"),
                List.of(I18n.tr("miner.shift_minus_8"))));
        c.inventory().setItem(10, GuiText.invisiblePlaceholder());
        c.inventory().setItem(11, GuiText.button(DigitalMinerGui.icon("plus"), I18n.tr("miner.radius_increase"),
                List.of(I18n.tr("miner.shift_plus_8"))));
        c.inventory().setItem(12, GuiText.button(DigitalMinerGui.icon("minus"), I18n.tr("miner.min_y_decrease"),
                List.of(I18n.tr("miner.shift_minus_16"))));
        c.inventory().setItem(13, GuiText.invisiblePlaceholder());
        c.inventory().setItem(14, GuiText.button(DigitalMinerGui.icon("plus"), I18n.tr("miner.min_y_increase"),
                List.of(I18n.tr("miner.shift_plus_16"))));
        c.inventory().setItem(18, GuiText.button(DigitalMinerGui.icon("stats"), I18n.tr("miner.scan_status"),
                List.of(I18n.minerStatus(state.status),
                        I18n.tr("miner.cursor", state.cursor, state.scan().volume()),
                        I18n.tr("miner.mined", state.mined))));
        c.inventory().setItem(15, GuiText.button(DigitalMinerGui.icon("minus"), I18n.tr("miner.max_y_decrease"),
                List.of(I18n.tr("miner.shift_minus_16"))));
        c.inventory().setItem(16, GuiText.invisiblePlaceholder());
        c.inventory().setItem(17, GuiText.button(DigitalMinerGui.icon("plus"), I18n.tr("miner.max_y_increase"),
                List.of(I18n.tr("miner.shift_plus_16"))));
        c.inventory().setItem(27, GuiText.button(
                state.replace.isBlank() ? DigitalMinerGui.icon("replace") : state.replace,
                I18n.tr("miner.replace_target",
                        state.replace.isBlank() ? I18n.tr("state.none") : I18n.itemName(state.replace)),
                List.of(I18n.tr("miner.replace_set"), I18n.tr("miner.replace_clear"),
                        I18n.tr("miner.replace_use"))));
        c.inventory().setItem(28, GuiText.button(DigitalMinerGui.toggle("required", state.inverseRequiresReplacement),
                I18n.tr("miner.require_replacement"),
                List.of(I18n.tr("miner.require_replacement_hint"))));
        var scan = GuiText.invisiblePlaceholder();
        for (int slot = 21; slot <= 26; slot++) c.inventory().setItem(slot, scan.clone());
        c.inventory().setItem(19, GuiText.button(DigitalMinerGui.icon("buffer"), I18n.tr("miner.output_buffer"),
                List.of(I18n.tr("miner.buffer_contents", state.buffer.total(), state.buffer.capacity()),
                        I18n.tr("miner.buffer_hint"))));
        c.inventory().setItem(36, GuiText.button(DigitalMinerGui.icon("energy"), I18n.tr("gui.energy.label"),
                List.of(GuiText.format(c.machine().snapshot().energyJoules()) + " / "
                        + GuiText.format(c.machine().snapshot().energyCapacityJ()) + " J")));
        c.inventory().setItem(7, GuiText.button(DigitalMinerGui.toggle("visuals", state.visuals),
                I18n.tr("miner.visuals"),
                List.of(I18n.tr("miner.visuals_hint"))));
        c.inventory().setItem(29, GuiText.button(DigitalMinerGui.icon("clear"), I18n.tr("miner.mine_empty"),
                List.of(I18n.tr("miner.mine_empty_hint"),
                        I18n.tr("miner.per_rule_hint"))));
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
                        player.sendActionBar(I18n.component(player, "miner.hold_block"));
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
