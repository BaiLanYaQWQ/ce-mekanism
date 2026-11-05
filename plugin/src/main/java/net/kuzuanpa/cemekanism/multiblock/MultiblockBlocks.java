package net.kuzuanpa.cemekanism.multiblock;

import mekcore.multiblock.BlockRole;
import mekcore.multiblock.BlockRoles;
import net.kuzuanpa.cemekanism.generated.GeneratedContent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 方块 id ↔ 结构族 ↔ 角色（表来自 GeneratedContent，别在别处再抄一份）。 */
public final class MultiblockBlocks {

    private static final Map<String, MultiblockDef> BY_ID = new LinkedHashMap<>();
    private static final Map<String, List<MultiblockDef>> BY_ANCHOR = new LinkedHashMap<>();

    static {
        for (MultiblockDef def : GeneratedContent.MULTIBLOCKS) {
            BY_ID.put(def.id(), def);
            BY_ANCHOR.computeIfAbsent(def.anchor(), key -> new ArrayList<>()).add(def);
        }
    }

    private MultiblockBlocks() {
    }

    public static List<MultiblockDef> all() {
        return GeneratedContent.MULTIBLOCKS;
    }

    public static MultiblockDef require(String id) {
        MultiblockDef def = BY_ID.get(id);
        if (def == null) {
            throw new IllegalArgumentException("没有登记结构 " + id);
        }
        return def;
    }

    public static List<MultiblockDef> anchorsOf(String blockId) {
        return BY_ANCHOR.getOrDefault(blockId, List.of());
    }

    public static boolean isAnchor(String blockId) {
        return BY_ANCHOR.containsKey(blockId);
    }

    /** 角色 id → 核心角色对象（内置的直接用常量，其余按名字造）。 */
    public static BlockRole role(String roleId) {
        return switch (roleId) {
            case "frame" -> BlockRoles.FRAME;
            case "valve" -> BlockRoles.VALVE;
            case "port" -> BlockRoles.PORT;
            case "glass" -> BlockRoles.GLASS;
            case "controller" -> BlockRoles.CONTROLLER;
            case "logic_adapter" -> BlockRoles.LOGIC_ADAPTER;
            case "empty" -> BlockRoles.INTERIOR_EMPTY;
            case "any" -> BlockRoles.ANY;
            default -> ROLE_CACHE.computeIfAbsent(roleId, key -> BlockRole.of(key, key));
        };
    }

    private static final Map<String, BlockRole> ROLE_CACHE = new LinkedHashMap<>();
}
