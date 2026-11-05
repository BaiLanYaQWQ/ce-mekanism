package net.kuzuanpa.cemekanism.machine;

import mekcore.api.MekCore;
import mekcore.machine.*;
import mekcore.resource.ResourceTypes;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.LinkedHashMap;
import java.util.Map;

/** Catch cross-type/cross-face writes, including multiple same-type ports with different settings. */
final class ConfiguratorModeSelfTest {
    private static int checks;
    private static void check(boolean ok, String message) {
        checks++;
        if (!ok) throw new AssertionError(message);
    }

    static int run() {
        var blueprint = MachineBlueprint.builder("test:configurator", "配置器测试")
                .energyBuffer(1000)
                .tank(TankSpec.of("water", ResourceTypes.FLUID, 1000))
                .tank(TankSpec.of("steam", ResourceTypes.CHEMICAL, 1000))
                .tank(TankSpec.of("fluid_named_gas", ResourceTypes.CHEMICAL, 1000))
                .tank(TankSpec.of("heat", ResourceTypes.HEAT, 1000))
                .tank(TankSpec.of("infuse", ResourceTypes.INFUSION, 1000)).build();
        var core = MekCore.create(r -> r.machine(blueprint));
        Map<String, ConfiguratorMode> functions = Map.of("energy", ConfiguratorMode.ENERGY,
                "water", ConfiguratorMode.FLUID, "steam", ConfiguratorMode.CHEMICAL,
                "fluid_named_gas", ConfiguratorMode.CHEMICAL, "heat", ConfiguratorMode.HEAT,
                "infuse", ConfiguratorMode.INFUSION);
        for (ConfiguratorMode mode : ConfiguratorMode.values()) for (Direction face : Direction.values()) {
            Machine machine = new Machine(blueprint, FactoryTier.SINGLE, core.registrars().recipes(), 1, null);
            for (Direction side : Direction.values()) {
                machine.setItemSide(side, SideMode.OUTPUT);
                for (String port : machine.portNames()) machine.setSide(port, side,
                        port.equals("fluid_named_gas") ? SideMode.INPUT : SideMode.BOTH);
            }
            var before = snapshot(machine);
            var redstone = machine.redstoneControl();
            check(mode.cycle(machine, face) == (mode != ConfiguratorMode.ROTATE),
                    mode + " rotation requires a block controller; other modes have a matching function");
            for (Direction side : Direction.values()) {
                SideMode item = before.get("items:" + side);
                check(machine.itemSide(side) == (mode == ConfiguratorMode.ITEMS && side == face
                        ? next(item) : item), mode + " only changes selected item face");
                for (String port : machine.portNames()) {
                    SideMode old = before.get(port + ":" + side);
                    check(machine.side(port, side) == (mode == functions.get(port) && side == face
                            ? next(old) : old), mode + " respects type and face for " + port + ":" + side);
                }
            }
            check((machine.redstoneControl() != redstone) == (mode == ConfiguratorMode.REDSTONE),
                    mode + " leaves redstone alone unless selected");
            if (mode == ConfiguratorMode.CHEMICAL) {
                String status = mode.status(machine, face);
                check(status.contains("steam 关") && status.contains("fluid_named_gas 输出"),
                        "mixed chemical port states are preserved and shown");
            }
            for (int i = 0; i < 3; i++) mode.cycle(machine, face);
            check(snapshot(machine).equals(before), mode + " four clicks restore all port modes");
        }

        var empty = MachineBlueprint.builder("test:no_ports", "无资源端口").build();
        Machine machine = new Machine(empty, FactoryTier.SINGLE, core.registrars().recipes(), 1, null);
        var before = snapshot(machine);
        for (ConfiguratorMode mode : functions.values()) {
            check(!mode.cycle(machine, Direction.NORTH), "missing resource is a no-op");
            check("不支持".equals(mode.status(machine, Direction.NORTH)), "missing resource preview");
        }
        check(snapshot(machine).equals(before), "unsupported functions never change item modes");
        TextComponent bar = (TextComponent) ConfiguratorMode.FLUID.actionBar(Direction.NORTH, "输入");
        check(bar.content().equals("流体 · 输入"), "face highlight replaces repeated face text");
        check(NamedTextColor.GREEN.equals(bar.color()), "green actionbar");
        check(((TextComponent) ConfiguratorMode.ENERGY.actionBar(null, null)).content().equals("能源"),
                "air preview shows current function");
        ConfiguratorMode mode = ConfiguratorMode.ITEMS;
        for (int i = 0; i < ConfiguratorMode.values().length; i++) mode = mode.next();
        check(mode == ConfiguratorMode.ITEMS, "function cycle wraps");
        return checks;
    }

    private static SideMode next(SideMode mode) {
        return switch (mode) { case NONE -> SideMode.INPUT; case INPUT -> SideMode.OUTPUT;
            case OUTPUT -> SideMode.BOTH; case BOTH -> SideMode.NONE; };
    }

    private static Map<String, SideMode> snapshot(Machine machine) {
        Map<String, SideMode> result = new LinkedHashMap<>();
        for (Direction face : Direction.values()) {
            result.put("items:" + face, machine.itemSide(face));
            for (String port : machine.portNames()) result.put(port + ":" + face, machine.side(port, face));
        }
        return result;
    }
}
