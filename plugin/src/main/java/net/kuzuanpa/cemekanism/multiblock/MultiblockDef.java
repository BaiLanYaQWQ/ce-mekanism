package net.kuzuanpa.cemekanism.multiblock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 结构族（GeneratedContent.MULTIBLOCKS 生成）：世界侧要用的尺寸规则、锚点、端口名、check 清单、方块 → 角色。
 * 结构状态本身不进存档，这里只是"规则表"。
 */
public record MultiblockDef(String id, String family, String zh, int wMin, int wMax, int hMin, int hMax,
                            boolean square, boolean odd, String mode, String anchor,
                            int anchorOffsetX, int anchorOffsetY, int anchorOffsetZ,
                            List<String> checks, List<String> ports, Map<String, String> blockRoles,
                            Map<String, String> roleBlocks,
                            Map<String, String> portCapacity, Map<String, String> portRates,
                            Map<String, String> portFlows, Map<String, String> portRoles,
                            Map<String, String> portSections, Map<String, Integer> portMins,
                            Map<String, String> portModes) {

    /** 端口容量的算法（文档 05 第七节第 4 条：成型后按形状算，别在逻辑里再算一遍）。 */
    public String capacityRule(String port) {
        return this.portCapacity.getOrDefault(port, "fixed:0");
    }

    public String rateRule(String port) {
        return this.portRates.getOrDefault(port, "fixed:0");
    }

    /**
     * 端口的进出方向（文档 06）：`in` 只收、`out` 只出、`store` 当储能、`both` 两头都挂。
     * 上游是"一个阀门切模式"（玩家自己配），我们这边没有阀门的面配置，所以方向进数据（见 10 的 D12）。
     */
    public String flow(String port) {
        return this.portFlows.getOrDefault(port, "both");
    }

    /** 端口长在哪个角色上（阀门/端口/逻辑适配器）；一键搭建按它补齐端口方块。 */
    public String portRole(String port) {
        return this.portRoles.getOrDefault(port, "valve");
    }

    /** 端口所在的段（face / edge / interior），与核心 PortDefinition 的 section 一致。 */
    public String portSection(String port) {
        return this.portSections.getOrDefault(port, "face");
    }

    public int portMin(String port) {
        return this.portMins.getOrDefault(port, 1);
    }

    /** 上游阀门模式；空串表示这个端口没有模式门槛。 */
    public String portMode(String port) {
        return this.portModes.getOrDefault(port, "");
    }

    /** 锚点方块在该族里的角色（阀门/端口/控制器/逻辑适配器）。 */
    public String anchorRole() {
        return this.blockRoles.getOrDefault(this.anchor, "controller");
    }

    public boolean fixed() {
        return "fixed".equals(this.mode);
    }

    public boolean accepts(int width, int height, int depth) {
        if (this.family.equals("teleporter")) return TeleporterFrame.accepts(width, height, depth);
        if (width < this.wMin || width > this.wMax || depth < this.wMin || depth > this.wMax
                || height < this.hMin || height > this.hMax) {
            return false;
        }
        if (this.square && width != depth) {
            return false;
        }
        return !this.odd || (width % 2 == 1 && depth % 2 == 1);
    }

    /** 这个方块在这个族里是什么角色（不认识返回 null）。 */
    public String roleOf(String blockId) {
        return this.blockRoles.get(blockId);
    }

    public int[] buildAnchor(int x, int y, int z) {
        return this.family.equals("teleporter") ? TeleporterFrame.anchor(x, y, z)
                : new int[] {this.anchorOffsetX, this.anchorOffsetY, this.anchorOffsetZ};
    }

    /** 这个角色由哪个方块承担（同一种方块可以承担多个角色，见锅炉/裂变堆的阀门）。 */
    public String blockOf(String role) {
        return this.roleBlocks.get(role);
    }

    /** 这个方块能承担的全部角色；没有别名时只有 roleOf 那一个。 */
    public List<String> rolesOf(String blockId) {
        String primary = roleOf(blockId);
        if (primary == null) {
            return List.of();
        }
        List<String> roles = new ArrayList<>();
        for (Map.Entry<String, String> entry : this.roleBlocks.entrySet()) {
            if (blockId.equals(entry.getValue()) && !roles.contains(entry.getKey())) {
                roles.add(entry.getKey());
            }
        }
        if (!roles.contains(primary)) {
            roles.add(primary);
        }
        return roles;
    }

    public boolean isAnchor(String blockId) {
        return this.anchor.equals(blockId);
    }

    public String describe() {
        return this.zh + '(' + this.id + ", " + this.mode + ", " + this.wMin + '~' + this.wMax + '×'
                + this.hMin + '~' + this.hMax + ", 锚点 " + this.anchor + ", check "
                + this.checks + ')';
    }
}
