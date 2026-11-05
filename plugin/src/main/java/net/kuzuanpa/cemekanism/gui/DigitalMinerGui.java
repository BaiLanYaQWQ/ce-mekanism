package net.kuzuanpa.cemekanism.gui;

import net.kuzuanpa.cemekanism.machine.MachineBlockEntityController;
import net.kuzuanpa.cemekanism.storage.DigitalMinerLogic;
import net.kuzuanpa.cemekanism.i18n.I18n;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Miner-specific skin and readouts; virtual controls never enter the machine's storage. */
public final class DigitalMinerGui {
    private DigitalMinerGui() { }

    public static String icon(String name) {
        return "mek:gui_miner_" + name;
    }

    public static String toggle(String name, boolean enabled) {
        return icon(name + (enabled ? "_on" : "_off"));
    }

    public static boolean handles(int slot) {
        return switch (slot) {
            case 0, 1, 3, 4, 5, 6, 7, 8, 9, 11, 12, 14, 15, 17, 27, 28, 29 -> true;
            default -> false;
        };
    }

    public static boolean scanCell(int index) {
        return index >= 21 && index <= 26;
    }

    public static Component filterTitle() {
        return new TitleBuilder().panel("mek:gui/panel_digital_miner_filters", 176)
                .text(I18n.tr("miner.filter_title"), TextColor.color(GuiConfig.titleColor())).build();
    }

    public static Component title(MachineBlockEntityController controller) {
        var layout = GuiLayout.forMachine(controller.machine(), controller.machine().factoryTier(), controller.blockId());
        var state = ((DigitalMinerLogic) controller.machine().blueprint().logic()).state(controller.machine());
        var snapshot = controller.machine().snapshot();
        var builder = new TitleBuilder().panel(layout.panelId(), layout.profile().width());
        List<Readout> readouts = new ArrayList<>();
        number(readouts, state.radius, 1);
        number(readouts, state.minY, 4);
        number(readouts, state.maxY, 7);
        readouts.add(new Readout(62, () -> builder.overlay(layout.energySprite(),
                level(snapshot.energyJoules(), snapshot.energyCapacityJ()), 62, 108, GuiConfig.energyColor())));
        readouts.add(new Readout(62, () -> builder.overlay("mek:gui/miner_scan",
                level(state.cursor, state.scan().volume()), 62, 108, GuiConfig.progressColor())));
        readouts.sort(Comparator.comparingInt(Readout::x).reversed());
        for (Readout readout : readouts) {
            if (readout.draw() != null) readout.draw().run();
            else builder.sprite("mek:gui/miner_digits", readout.digit(), readout.x(), 4, 0x8CFFAA);
        }
        return builder.text(GuiText.title(snapshot), TextColor.color(GuiConfig.titleColor())).build();
    }

    private static void number(List<Readout> readouts, int value, int column) {
        String text = Integer.toString(value);
        int x = 8 + column * 18 + (16 - (text.length() * 5 - 2)) / 2;
        for (int i = 0; i < text.length(); i++) {
            char digit = text.charAt(i);
            readouts.add(new Readout(x + i * 5, null, digit == '-' ? 10 : digit - '0'));
        }
    }

    private static int level(long value, long capacity) {
        return capacity <= 0 ? 0 : (int) Math.round(20 * Math.max(0, Math.min(1, (double) value / capacity)));
    }

    private record Readout(int x, Runnable draw, int digit) {
        Readout(int x, Runnable draw) { this(x, draw, 0); }
    }
}
