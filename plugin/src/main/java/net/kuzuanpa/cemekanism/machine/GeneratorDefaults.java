package net.kuzuanpa.cemekanism.machine;

import mekcore.machine.Direction;
import mekcore.machine.Machine;
import mekcore.machine.SideMode;
import net.kuzuanpa.cemekanism.platform.GeneratorConfig;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 发电机默认端口（文档 11 G1）：上游写的是 FRONT/BOTTOM 这类相对面，
 * 核心的 {@link Machine} 只认世界方向，所以放置时按方块 facing 翻一次。
 *
 * <p>只在"新机器"上设默认值；存档里写过的面仍以存档为准（v1 旧档先落回默认再叠，
 * v2 档按完整六面表恢复）。
 */
public final class GeneratorDefaults {

    private GeneratorDefaults() {
    }

    public static void apply(Machine machine, String blueprintId, Direction facing) {
        String family = blueprintId.contains(":") ? blueprintId.split(":", 2)[1] : blueprintId;
        Map<String, List<String>> sides = GeneratorConfig.defaultSides(family);
        // 重读配置或换默认面时，旧的默认表必须先清掉；否则旧方向会继续参与 load 的回落。
        machine.clearSideDefaults();
        Direction stand = facing == null || facing == Direction.UP || facing == Direction.DOWN
                ? Direction.NORTH : facing;
        for (Map.Entry<String, List<String>> entry : sides.entrySet()) {
            String port = entry.getKey();
            for (String relative : entry.getValue()) {
                Direction direction = resolve(relative, stand);
                if ("items".equalsIgnoreCase(port)) {
                    machine.defaultItemSide(direction, SideMode.INPUT);
                } else if (machine.portNames().contains(port)) {
                    machine.defaultSide(port, direction, SideMode.OUTPUT);
                }
            }
        }
    }

    static Direction resolve(String relative, Direction facing) {
        if (relative == null) {
            return facing;
        }
        // 与上游 RelativeSide.fromDirections 一一对应：FRONT 就是 blockstate facing，
        // BACK 是反向，LEFT 是顺时针 90°，RIGHT 是逆时针 90°。
        // 生成期的 appearance rotation 已经负责把模型转到这个 facing 上，这里不要再翻一次。
        return switch (relative.toLowerCase(Locale.ROOT)) {
            case "front" -> facing;
            case "back" -> facing.opposite();
            case "left" -> clockwise(facing);
            case "right" -> counterClockwise(facing);
            case "top" -> Direction.UP;
            case "bottom" -> Direction.DOWN;
            default -> facing;
        };
    }

    private static Direction clockwise(Direction direction) {
        return switch (direction) {
            case NORTH -> Direction.EAST;
            case EAST -> Direction.SOUTH;
            case SOUTH -> Direction.WEST;
            case WEST -> Direction.NORTH;
            default -> direction;
        };
    }

    private static Direction counterClockwise(Direction direction) {
        return switch (direction) {
            case NORTH -> Direction.WEST;
            case WEST -> Direction.SOUTH;
            case SOUTH -> Direction.EAST;
            case EAST -> Direction.NORTH;
            default -> direction;
        };
    }
}
