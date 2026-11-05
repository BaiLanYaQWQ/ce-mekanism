package net.kuzuanpa.cemekanism.multiblock;

import mekcore.machine.MachineTier;
import mekcore.spec.StorageProfile;
import mekcore.spec.V10Tables;

/** 端口容量/速率的算法（文档 05 第七节第 4 条）：成型后按形状算，逻辑只读。 */
public final class MultiblockSizing {

    private MultiblockSizing() {
    }

    public static void apply(MultiblockHosts.Host host) {
        StructureBox box = host.box();
        if (box == null) {
            return;
        }
        for (String port : host.def().ports()) {
            long capacity = host.def().family().equals("turbine") && port.equals("energy")
                    ? mekcore.spec.SteamMath.storageJoules(box.sizeX(), box.sizeY())
                    : value(host.def().capacityRule(port), box, host);
            long rate = value(host.def().rateRule(port), box, host);
            host.instance().tank(port).resize(Math.max(capacity, 1L));
            if (rate > 0) {
                host.instance().tank(port).withRateLimit(rate);
                if (host.def().family().equals("matrix")) host.instance().tank(port).withTickClock(org.bukkit.Bukkit::getCurrentTick);
            }
        }
        applyHeatCapacity(host, box);
    }

    /**
     * 形状相关的热容（上游 BoilerMultiblockData 50 × locations、EvaporationMultiblockData 100 × height）。
     * 系数进 `mek-systems.yml`，核心只提供 setter，不在核心里写内容数值。
     */
    private static void applyHeatCapacity(MultiblockHosts.Host host, StructureBox box) {
        if (host.instance().heatNode() == null) {
            return;
        }
        switch (host.def().family()) {
            case "boiler" -> host.instance().heatNode().setHeatCapacity(
                    MultiblockConfig.boilerCasingHeatCapacity()
                            * (double) box.sizeX() * box.sizeY() * box.sizeZ());
            case "evaporation" -> host.instance().heatNode().setHeatCapacity(
                    MultiblockConfig.evaporationHeatCapacity() * box.sizeY());
            case "fission_reactor" -> host.instance().heatNode().setHeatCapacity(
                    MultiblockConfig.fissionCasingHeatCapacity()
                            * (double) box.sizeX() * box.sizeY() * box.sizeZ());
            case "fusion_reactor" -> host.instance().heatNode().setHeatCapacity(
                    MultiblockConfig.fusionCaseHeatCapacity());
            default -> {
            }
        }
    }

    private static long value(String rule, StructureBox box, MultiblockHosts.Host host) {
        if (rule == null || rule.isEmpty()) {
            return 0;
        }
        int colon = rule.indexOf(':');
        String kind = colon < 0 ? rule : rule.substring(0, colon);
        long factor = colon < 0 ? 0 : Long.parseLong(rule.substring(colon + 1));
        return switch (kind) {
            case "fixed" -> factor;
            case "volume" -> interiorVolume(box) * factor;
            case "total_volume" -> (long) box.sizeX() * box.sizeY() * box.sizeZ() * factor;
            case "lower_volume" -> (long) box.sizeX() * box.sizeZ() * rotorHeight(box) * factor;
            case "condensers" -> roleCount(box, "condenser") * factor;
            case "assemblies" -> roleCount(box, "fuel") * factor;
            case "injection" -> fusionInjection(host) * factor;
            case "cells" -> storage(box, "cell", true);
            case "providers" -> storage(box, "provider", false);
            default -> 0;
        };
    }

    private static long interiorVolume(StructureBox box) {
        long inner = (long) Math.max(box.sizeX() - 2, 0) * Math.max(box.sizeY() - 2, 0)
                * Math.max(box.sizeZ() - 2, 0);
        return Math.max(inner, 1L);
    }

    private static int rotorHeight(StructureBox box) {
        for (String block : box.blockCounts().keySet()) {
            if ("rotor".equals(box.def().roleOf(block))) {
                return box.count(block);
            }
        }
        return 1;
    }

    /** 某个角色在结构里数到的方块数（涡轮的饱和冷凝器按它算回水容量）。 */
    private static long roleCount(StructureBox box, String roleId) {
        long total = 0;
        for (var entry : box.blockCounts().entrySet()) {
            if (roleId.equals(box.def().roleOf(entry.getKey()))) {
                total += entry.getValue();
            }
        }
        return total;
    }

    private static int fusionInjection(MultiblockHosts.Host host) {
        if (host.instance().blueprint().logic()
                instanceof net.kuzuanpa.cemekanism.multiblock.logic.FusionReactorLogic fusion) {
            return fusion.state(host.instance()).injectionRate;
        }
        return MultiblockConfig.fusionDefaultInjection();
    }

    /** 感应元件/供应器：把数到的方块按各自 tier 加起来（核心 V10Tables 的储能分级表）。 */
    private static long storage(StructureBox box, String roleId, boolean capacity) {
        long total = 0;
        for (var entry : box.blockCounts().entrySet()) {
            if (!roleId.equals(box.def().roleOf(entry.getKey()))) {
                continue;
            }
            MachineTier tier = tierOf(entry.getKey());
            if (tier == null) {
                continue;
            }
            StorageProfile profile = capacity ? V10Tables.inductionElement(tier) : V10Tables.inductionProvider(tier);
            total += entry.getValue() * (capacity ? profile.capacityJ() : profile.ratePerTick());
        }
        return total;
    }

    private static MachineTier tierOf(String blockId) {
        String path = blockId.substring(blockId.indexOf(':') + 1);
        for (MachineTier tier : MachineTier.values()) {
            if (path.startsWith(tier.name().toLowerCase())) {
                return tier;
            }
        }
        return null;
    }
}
