package net.kuzuanpa.cemekanism.gui;

import mekcore.machine.MachineSnapshot;
import net.kuzuanpa.cemekanism.machine.MachineBlockEntityController;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** 单机/工厂的界面：面板字形 + 能量/流体/进度条字形 + 顶部一行状态文字。 */
public final class MachineGui {

    private MachineGui() {
    }

    public static Component title(MachineBlockEntityController controller) {
        if (controller.machine().blueprint().logic() instanceof net.kuzuanpa.cemekanism.storage.DigitalMinerLogic) {
            return DigitalMinerGui.title(controller);
        }
        MachineSnapshot snapshot = controller.machine().snapshot();
        GuiLayout.MachineLayout layout = GuiLayout.forMachine(controller.machine(),
                controller.machine().factoryTier(), controller.blockId());
        TitleBuilder builder = new TitleBuilder();
        builder.panel(layout.panelId(), layout.profile().width());

        List<Overlay> overlays = new ArrayList<>();
        if (snapshot.energyCapacityJ() > 0) {
            int level = level(snapshot.energyJoules(), snapshot.energyCapacityJ());
            int x = layout.profile().energy().x();
            overlays.add(new Overlay(x, () -> builder.overlay(layout.energySprite(), level, x,
                    layout.profile().energy().w(), GuiConfig.energyColor())));
        }
        for (GuiLayout.TankGauge gauge : layout.tankGauges()) {
            MachineSnapshot.TankSnapshot tank = snapshot.tanks().stream()
                    .filter(value -> value.name().equals(gauge.port())).findFirst().orElse(null);
            if (tank == null) continue;
            int level = level(tank.amount(), tank.capacity());
            int x = gauge.rect().x();
            overlays.add(new Overlay(x, () -> builder.overlay(gauge.sprite(), level, x,
                    gauge.rect().w(), GuiText.tankColor(tank))));
        }
        if (layout.hasProgress() && layout.factory()) {
            for (MachineSnapshot.ParallelSnapshot parallel : snapshot.parallels()) {
                int x = layout.profile().progress().x() + Math.min(parallel.group(), 8) * 18;
                int level = (int) Math.round(Math.max(0.0, Math.min(1.0, parallel.progress())) * (GuiLayout.LEVELS - 1));
                overlays.add(new Overlay(x, () -> builder.overlay(layout.progressSprite(), level, x,
                        layout.profile().progress().w(),
                        GuiConfig.progressColor())));
            }
        } else if (layout.hasProgress()) {
            int level = (int) Math.round(Math.max(0.0, Math.min(1.0, snapshot.progress()))
                    * (GuiLayout.LEVELS - 1));
            int x = layout.profile().progress().x();
            overlays.add(new Overlay(x, () -> builder.overlay(layout.progressSprite(), level, x,
                    layout.profile().progress().w(), GuiConfig.progressColor())));
        }

        // 标题只能往左挪，按 x 从大到小画；文字最后画在最左边。
        overlays.sort(Comparator.comparingInt(Overlay::x).reversed());
        overlays.forEach(overlay -> overlay.draw().run());
        builder.text(GuiText.title(snapshot), TextColor.color(GuiConfig.titleColor()));
        return builder.build();
    }

    /** 把隐形 tooltip 锚点放进预留格；它们不参与机器逻辑、存档与掉落。 */
    public static void updateAnchors(MachineBlockEntityController controller) {
        controller.beginOverlayUpdate();
        try {
            if (!GuiConfig.anchorTooltips()) {
                clearAnchors(controller);
                fillPlaceholders(controller);
                MachineControls.render(controller);
                return;
            }
            fillPlaceholders(controller);
            GuiLayout.MachineLayout layout = GuiLayout.forMachine(controller.machine(),
                    controller.machine().factoryTier(), controller.blockId());
            MachineSnapshot snapshot = controller.machine().snapshot();
            ItemStack energy = GuiText.energyAnchor(snapshot);
            if (energy != null && !layout.hasProgress()
                    && snapshot.state() != mekcore.machine.WorkState.RUNNING
                    && snapshot.state() != mekcore.machine.WorkState.IDLE) {
                GuiText.appendLore(energy, snapshot.state().zhName());
            }
            for (GuiLayout.Cell cell : layout.energyAnchors()) {
                setIfChanged(controller, cell.index(), energy);
            }
            for (GuiLayout.TankGauge gauge : layout.tankGauges()) {
                var tank = snapshot.tanks().stream().filter(value -> value.name().equals(gauge.port()))
                        .findFirst().orElse(null);
                if (tank == null) continue;
                ItemStack anchor = GuiText.tankAnchor(tank);
                if (controller.machine().dumpablePorts().contains(gauge.port())) {
                    GuiText.appendLore(anchor, "点击设置化学品输出罐排出模式");
                }
                for (GuiLayout.Cell cell : gauge.anchors()) setIfChanged(controller, cell.index(), anchor);
            }
            if (layout.hasProgress()) {
                if (layout.factory()) {
                    for (int group = 0; group < layout.progressAnchors().size(); group++) {
                        setIfChanged(controller, layout.progressAnchors().get(group).index(),
                                GuiText.progressAnchor(snapshot, group));
                    }
                } else {
                    ItemStack progress = GuiText.progressAnchor(snapshot, -1);
                    for (GuiLayout.Cell cell : layout.progressAnchors()) {
                        setIfChanged(controller, cell.index(), progress);
                    }
                }
            }
            MachineControls.render(controller);
        } finally {
            controller.endOverlayUpdate();
        }
    }

    /** 空白格放占位物品：普通空位盖面板底色，动态条覆盖的格子保持空模型。 */
    private static void fillPlaceholders(MachineBlockEntityController controller) {
        GuiLayout.MachineLayout layout = GuiLayout.forMachine(controller.machine(),
                controller.machine().factoryTier(), controller.blockId());
        ItemStack placeholder = GuiText.placeholder();
        ItemStack invisible = GuiText.invisiblePlaceholder();
        boolean miner = controller.machine().blueprint().logic() instanceof net.kuzuanpa.cemekanism.storage.DigitalMinerLogic;
        for (int index = 0; index < controller.inventory().getSize(); index++) {
            if (controller.handler().kindAt(index) == null) {
                setIfChanged(controller, index,
                        miner || GuiLayout.overlayCell(layout, index) ? invisible : placeholder);
            }
        }
    }

    public static void clearAnchors(MachineBlockEntityController controller) {
        GuiLayout.MachineLayout layout = GuiLayout.forMachine(controller.machine(),
                controller.machine().factoryTier(), controller.blockId());
        for (GuiLayout.Cell cell : layout.energyAnchors()) {
            setIfChanged(controller, cell.index(), null);
        }
        for (GuiLayout.Cell cell : layout.tankAnchors()) {
            setIfChanged(controller, cell.index(), null);
        }
        for (GuiLayout.Cell cell : layout.progressAnchors()) {
            setIfChanged(controller, cell.index(), null);
        }
    }

    public static boolean isAnchor(ItemStack stack) {
        return GuiText.isAnchor(stack);
    }

    private static int level(long amount, long capacity) {
        if (capacity <= 0) {
            return 0;
        }
        double ratio = Math.max(0.0, Math.min(1.0, (double) amount / capacity));
        return (int) Math.round(ratio * (GuiLayout.LEVELS - 1));
    }

    private static void setIfChanged(MachineBlockEntityController controller, int index, ItemStack stack) {
        ItemStack current = controller.inventory().getItem(index);
        ItemStack next = stack == null ? null : stack.clone();
        if (java.util.Objects.equals(current, next)) {
            return;
        }
        controller.inventory().setItem(index, next);
    }

    private record Overlay(int x, Runnable draw) {
    }
}
