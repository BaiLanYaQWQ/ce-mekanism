package net.kuzuanpa.cemekanism.storage;

import mekcore.machine.*;
import mekcore.persist.DataTag;
import mekcore.persist.MapDataTag;
import net.kuzuanpa.cemekanism.machine.MachineBlockEntityController;
import net.kuzuanpa.cemekanism.i18n.I18n;
import net.kuzuanpa.cemekanism.platform.BukkitItems;
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Bed;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Incremental digital-miner scan. It mirrors the upstream split between a search pass and a mining
 * pass while keeping the cursor, filter configuration and overflow buffer restartable.
 */
public final class DigitalMinerLogic implements MachineLogic {
    public static final int DEFAULT_RADIUS = 10;
    public static final int DEFAULT_HEIGHT_RANGE = 60;
    public static final int MAX_RADIUS = 32;
    public static final int DEFAULT_TICKS_PER_MINE = 80;

    public static final class State {
        public int radius = DEFAULT_RADIUS;
        public int minY = 0;
        public int maxY = DEFAULT_HEIGHT_RANGE;
        public int progress;
        public long cursor;
        public long mined;
        public boolean running;
        public boolean silk;
        public boolean autoEject;
        public boolean autoPull;
        public boolean inverseRequiresReplacement;
        public boolean visuals;
        public String replace = "";
        public String status = "stopped";
        public final ItemSelection filter = new ItemSelection();
        public final CountedStore buffer = new CountedStore(1728, 27);

        public MiningCursor scan() {
            return new MiningCursor(radius, minY, maxY, cursor);
        }
    }

    private record Replacement(String id, Material material, boolean required) {
        boolean none() {
            return id.isEmpty();
        }
    }

    private final Map<Machine, State> states = new WeakHashMap<>();

    public State state(Machine machine) {
        return states.computeIfAbsent(machine, ignored -> new State());
    }

    public void configure(Machine machine, int radius, int minY, int maxY) {
        MachineBlockEntityController controller = StorageRuntime.of(machine);
        if (controller == null) {
            throw new IllegalStateException("machine unloaded");
        }
        new MiningCursor(radius, minY, maxY, 0);
        if (minY < controller.world().getMinHeight() || maxY >= controller.world().getMaxHeight()) {
            throw new IllegalArgumentException(I18n.tr("miner.height_out_of_range"));
        }
        State state = state(machine);
        state.radius = radius;
        state.minY = minY;
        state.maxY = maxY;
        reset(machine);
    }

    public void setRadius(Machine machine, int radius) {
        if (radius < 0 || radius > MAX_RADIUS) {
            throw new IllegalArgumentException(I18n.tr("miner.radius_out_of_range"));
        }
        State state = state(machine);
        configure(machine, radius, state.minY, state.maxY);
    }

    public void setMinY(Machine machine, int minY) {
        State state = state(machine);
        configure(machine, state.radius, Math.min(minY, state.maxY), state.maxY);
    }

    public void setMaxY(Machine machine, int maxY) {
        State state = state(machine);
        configure(machine, state.radius, state.minY, Math.max(maxY, state.minY));
    }

    public void start(Machine machine) {
        State state = state(machine);
        if (state.scan().done()) {
            state.cursor = 0;
            state.progress = 0;
        }
        state.running = true;
        state.status = "scanning";
        machine.setState(WorkState.IDLE);
    }

    public void stop(Machine machine) {
        State state = state(machine);
        state.running = false;
        state.status = "stopped";
        machine.setState(WorkState.IDLE);
    }

    public void reset(Machine machine) {
        State state = state(machine);
        state.cursor = 0;
        state.progress = 0;
        state.running = false;
        state.status = "waiting";
        machine.setState(WorkState.IDLE);
    }

    public boolean needsTick(Machine machine) {
        MachineBlockEntityController controller = StorageRuntime.of(machine);
        State state = state(machine);
        return controller != null && (state.running && !state.scan().done()
                || state.buffer.total() > 0 && StoredItems.empty(controller.slotContent("out"))
                || state.autoEject && !StoredItems.empty(controller.slotContent("out")));
    }

    private Block candidate(Machine machine) {
        MachineBlockEntityController controller = StorageRuntime.of(machine);
        State state = state(machine);
        if (controller == null || state.scan().done()) {
            return null;
        }
        MiningCursor.Offset offset = state.scan().current();
        int x = controller.x() + offset.x();
        int z = controller.z() + offset.z();
        if (!controller.world().isChunkLoaded(x >> 4, z >> 4)) {
            return null;
        }
        return controller.world().getBlockAt(x, offset.y(), z);
    }

    private boolean eligible(Machine machine, Block block) {
        MachineBlockEntityController controller = StorageRuntime.of(machine);
        State state = state(machine);
        Material type = block.getType();
        if (insideMinerFootprint(controller, block) || type.isAir() || block.isLiquid()
                || type.getHardness() < 0 || isSecondaryDoubleBlock(block)) {
            return false;
        }
        boolean customBlock = CraftEngineBlocks.getCustomBlockState(block) != null;
        if (customBlock && !controller.plugin().getConfig().getBoolean("b5.miner_custom_blocks", false)) {
            return false;
        }
        String id = type.getKey().toString();
        if (isReplacementTarget(state, id)) {
            return false;
        }
        return state.filter.matches(id, type);
    }

    private static boolean insideMinerFootprint(MachineBlockEntityController controller, Block block) {
        int dx = block.getX() - controller.x();
        int dy = block.getY() - controller.y();
        int dz = block.getZ() - controller.z();
        // The upstream rendering model is 3x3x2; protect all of its bounding cells.
        return dx >= -1 && dx <= 1 && dz >= -1 && dz <= 1 && dy >= 0 && dy <= 1;
    }

    private static boolean isSecondaryDoubleBlock(Block block) {
        if (block.getBlockData() instanceof Bisected bisected
                && bisected.getHalf() == Bisected.Half.TOP) {
            return true;
        }
        return block.getBlockData() instanceof Bed bed && bed.getPart() == Bed.Part.FOOT;
    }

    private static boolean isReplacementTarget(State state, String blockId) {
        if (state.replace.equals(blockId)) {
            return true;
        }
        if (state.filter.inverse) {
            return false;
        }
        for (String selector : state.filter.rules) {
            if (state.filter.enabled(selector)
                    && state.filter.replacementTarget(selector).equals(blockId)) {
                return true;
            }
        }
        return false;
    }

    private List<ItemStack> drops(Machine machine, Block block) {
        ItemStack tool = new ItemStack(Material.NETHERITE_PICKAXE);
        if (state(machine).silk) {
            tool.addUnsafeEnchantment(Enchantment.SILK_TOUCH, 1);
        }
        List<ItemStack> result = new ArrayList<>();
        try {
            result.addAll(block.getDrops(tool));
            // Bukkit's loot query does not consistently include block-entity inventories.
            // Upstream handles these through the fallback collector; collect them transactionally here.
            if (CraftEngineBlocks.getCustomBlockState(block) == null
                    && block.getState() instanceof InventoryHolder holder) {
                for (ItemStack item : holder.getInventory().getContents()) {
                    if (!StoredItems.empty(item)) {
                        result.add(item.clone());
                    }
                }
            }
        } catch (RuntimeException ignored) {
            return List.of();
        }
        return result;
    }

    private boolean fits(State state, List<ItemStack> drops) {
        MapDataTag tag = new MapDataTag();
        state.buffer.save(tag);
        CountedStore copy = new CountedStore(state.buffer.capacity(), state.buffer.typeLimit());
        copy.load(tag);
        for (ItemStack drop : drops) {
            if (copy.insert(StoredItems.key(drop), drop.getAmount(), true) != drop.getAmount()) {
                return false;
            }
        }
        return true;
    }

    private Replacement replacement(Machine machine, ItemSelection.Rule rule) {
        State state = state(machine);
        String target;
        boolean required;
        if (state.filter.inverse) {
            target = state.replace;
            required = state.inverseRequiresReplacement;
        } else {
            target = rule == null ? "" : rule.replacementTarget();
            if (target.isBlank()) {
                target = state.replace;
            }
            required = rule != null && rule.requiresReplacement()
                    || target.equals(state.replace) && !state.replace.isBlank();
        }
        target = target == null ? "" : target.trim();
        if (target.isEmpty()) {
            return new Replacement("", Material.AIR, false);
        }
        Material material = Material.matchMaterial(target);
        if (material == null || !material.isBlock() || material.isAir()
                || material.getHardness() < 0 || material == Material.WATER || material == Material.LAVA) {
            return new Replacement(target, Material.AIR, required);
        }
        return new Replacement(material.getKey().toString(), material, required);
    }

    private boolean stoneGenerator(Machine machine, Replacement replacement) {
        return (replacement.material() == Material.COBBLESTONE || replacement.material() == Material.STONE)
                && machine.upgrades().count("mek:upgrade_stone_generator") > 0;
    }

    private boolean hasReplacement(Machine machine, Replacement replacement) {
        if (replacement.none()) {
            return true;
        }
        if (replacement.material().isAir()) {
            return !replacement.required();
        }
        if (stoneGenerator(machine, replacement)) {
            return true;
        }
        MachineBlockEntityController controller = StorageRuntime.of(machine);
        ItemStack stored = controller.slotContent("replace");
        if (!StoredItems.empty(stored) && BukkitItems.idOf(stored).equals(replacement.id())) {
            return true;
        }
        if (!state(machine).autoPull) {
            return !replacement.required();
        }
        InventoryAccess above = InventoryAccess.adjacent(controller, Direction.UP, false);
        return above != null && above.extract(new ItemStack(replacement.material()), 1, false) == 1
                || !replacement.required();
    }

    private boolean consumeReplacement(Machine machine, Replacement replacement) {
        if (replacement.none() || replacement.material().isAir()
                || stoneGenerator(machine, replacement)) {
            return true;
        }
        MachineBlockEntityController controller = StorageRuntime.of(machine);
        ItemStack stored = controller.slotContent("replace");
        if (!StoredItems.empty(stored) && BukkitItems.idOf(stored).equals(replacement.id())) {
            ItemStack next = stored.clone();
            next.setAmount(next.getAmount() - 1);
            controller.putInSlot("replace", next.getAmount() == 0 ? null : next);
            return true;
        }
        if (state(machine).autoPull) {
            InventoryAccess above = InventoryAccess.adjacent(controller, Direction.UP, false);
            if (above != null && above.extract(new ItemStack(replacement.material()), 1, true) == 1) {
                return true;
            }
        }
        return !replacement.required();
    }

    @Override
    public long energyPerTick(Machine machine) {
        State state = state(machine);
        if (!state.running) {
            return 0;
        }
        Block block = candidate(machine);
        if (block == null || !eligible(machine, block)) {
            return 0;
        }
        ItemSelection.Rule matched = state.filter.matchingRule(
                block.getType().getKey().toString(), block.getType());
        Replacement replacement = replacement(machine, matched);
        return energyForBlock(machine, state, block, replacement);
    }

    private long energyForBlock(Machine machine, State state, Block block, Replacement replacement) {
        List<ItemStack> drops = drops(machine, block);
        if (!fits(state, drops) || !hasReplacement(machine, replacement)) {
            return 0;
        }
        MachineBlockEntityController controller = StorageRuntime.of(machine);
        int silkMultiplier = Math.max(1, controller.plugin().getConfig().getInt(
                "b5.miner_silk_multiplier", 12));
        double radiusRange = MAX_RADIUS - DEFAULT_RADIUS;
        double radiusCost = radiusRange == 0 ? 0
                : Math.max((state.radius - DEFAULT_RADIUS) / radiusRange, 0);
        double heightRange = controller.world().getMaxHeight() - 1 - DEFAULT_HEIGHT_RANGE;
        double heightCost = heightRange == 0 ? 0
                : Math.max((state.maxY - state.minY - DEFAULT_HEIGHT_RANGE) / heightRange, 0);
        return (long) Math.ceil(machine.energyPerTick()
                * (state.silk ? silkMultiplier : 1)
                * (1 + radiusCost)
                * (1 + heightCost));
    }

    @Override
    public long tick(Machine machine) {
        MachineBlockEntityController controller = StorageRuntime.of(machine);
        if (controller == null) {
            return 0;
        }
        State state = state(machine);
        BinLogic.flush(controller, state.buffer);
        eject(controller, state);
        if (!state.running) {
            return 0;
        }
        visualize(controller, state);
        int budget = Math.max(1, Math.min(4096,
                controller.plugin().getConfig().getInt("b5.miner_scan_budget", 128)));
        for (int inspected = 0; inspected < budget && !state.scan().done(); inspected++) {
            Block block = candidate(machine);
            if (block == null) {
                state.status = "chunk";
                machine.setState(WorkState.IDLE);
                controller.markChanged();
                return 0;
            }
            if (!eligible(machine, block)) {
                advance(state);
                continue;
            }
            ItemSelection.Rule matched = state.filter.matchingRule(
                    block.getType().getKey().toString(), block.getType());
            Replacement replacement = replacement(machine, matched);
            List<ItemStack> drops = drops(machine, block);
            if (!fits(state, drops)) {
                state.status = "buffer_full";
                machine.setState(WorkState.OUTPUT_BLOCKED);
                controller.markChanged();
                return 0;
            }
            if (!hasReplacement(machine, replacement)) {
                state.status = "missing_material";
                machine.setState(WorkState.NO_INPUT);
                controller.markChanged();
                return 0;
            }
            // The first target of a scan has already been quoted by energyPerTick().
            // Finding a later target returns so the next server tick can charge for it.
            if (inspected > 0) {
                state.status = "target";
                machine.setState(WorkState.IDLE);
                controller.markChanged();
                return 0;
            }
            state.status = "mining";
            machine.setState(WorkState.RUNNING);
            int requiredTicks = Math.max(1,
                    (int) Math.ceil(DEFAULT_TICKS_PER_MINE / machine.progressRate()));
            if (++state.progress < requiredTicks) {
                controller.markChanged();
                return 0;
            }
            if (!canBreak(machine, block, state)) {
                advance(state);
                controller.markChanged();
                return 0;
            }
            if (!consumeReplacement(machine, replacement)) {
                state.status = "missing_material";
                state.progress = 0;
                machine.setState(WorkState.NO_INPUT);
                controller.markChanged();
                return 0;
            }
            if (replacement.material().isAir()) {
                removeBlock(block);
            } else {
                removeBlock(block);
                block.setType(replacement.material(), true);
            }
            for (ItemStack drop : drops) {
                state.buffer.insert(StoredItems.key(drop), drop.getAmount(), true);
            }
            advance(state);
            state.mined++;
            BinLogic.flush(controller, state.buffer);
            eject(controller, state);
            controller.markChanged();
            return 0;
        }
        if (state.scan().done()) {
            state.running = false;
            state.status = "complete";
            machine.setState(WorkState.IDLE);
        }
        controller.markChanged();
        return 0;
    }

    private static void removeBlock(Block block) {
        if (CraftEngineBlocks.getCustomBlockState(block) != null) {
            CraftEngineBlocks.remove(block);
        } else {
            block.setType(Material.AIR, false);
        }
    }

    private static boolean canBreak(Machine machine, Block block, State state) {
        Player owner = null;
        try {
            if (machine.owner() != null && !machine.owner().isEmpty()) {
                owner = Bukkit.getPlayer(UUID.fromString(machine.owner()));
            }
        } catch (IllegalArgumentException ignored) {
        }
        if (owner == null) {
            return true;
        }
        BlockBreakEvent event = new BlockBreakEvent(block, owner);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            state.status = "protected";
            state.progress = 0;
            return false;
        }
        return true;
    }

    private static void advance(State state) {
        state.cursor = Math.min(state.scan().volume(), state.cursor + 1);
        state.progress = 0;
    }

    private static void eject(MachineBlockEntityController controller, State state) {
        if (!state.autoEject) {
            return;
        }
        ItemStack output = controller.slotContent("out");
        if (StoredItems.empty(output)) {
            return;
        }
        InventoryAccess target = InventoryAccess.adjacent(controller, controller.facing().opposite(), true);
        if (target == null) {
            return;
        }
        int accepted = target.insert(output, output.getAmount(), false);
        if (accepted <= 0) {
            return;
        }
        target.insert(output, accepted, true);
        ItemStack remaining = output.clone();
        remaining.setAmount(output.getAmount() - accepted);
        controller.putInSlot("out", remaining.getAmount() == 0 ? null : remaining);
    }

    private static void visualize(MachineBlockEntityController controller, State state) {
        if (!state.visuals || Bukkit.getCurrentTick() % 10 != 0) {
            return;
        }
        World world = controller.world();
        Location center = new Location(world, controller.x() + 0.5,
                controller.y() + 0.5, controller.z() + 0.5);
        if (world.getNearbyPlayers(center, 64).isEmpty()) {
            return;
        }
        int x0 = controller.x() - state.radius;
        int x1 = controller.x() + state.radius + 1;
        int z0 = controller.z() - state.radius;
        int z1 = controller.z() + state.radius + 1;
        for (int y : new int[]{state.minY, state.maxY + 1}) {
            for (int x : new int[]{x0, x1}) {
                for (int z : new int[]{z0, z1}) {
                    world.spawnParticle(Particle.END_ROD, x, y, z, 1, 0, 0, 0, 0);
                }
            }
        }
        int step = Math.max(1, (state.maxY - state.minY + 2) / 8);
        for (int y = state.minY; y <= state.maxY + 1; y += step) {
            world.spawnParticle(Particle.END_ROD, x0, y, z0, 1, 0, 0, 0, 0);
            world.spawnParticle(Particle.END_ROD, x1, y, z1, 1, 0, 0, 0, 0);
        }
    }

    public void save(Machine machine, DataTag tag) {
        State state = state(machine);
        tag.putInt("radius", state.radius);
        tag.putInt("min_y", state.minY);
        tag.putInt("max_y", state.maxY);
        tag.putLong("cursor", state.cursor);
        tag.putLong("mined", state.mined);
        tag.putInt("progress", state.progress);
        tag.putBoolean("running", state.running);
        tag.putBoolean("silk", state.silk);
        tag.putBoolean("auto_eject", state.autoEject);
        tag.putBoolean("auto_pull", state.autoPull);
        tag.putBoolean("inverse_requires_replacement", state.inverseRequiresReplacement);
        tag.putBoolean("visuals", state.visuals);
        tag.putString("replace", state.replace);
        tag.putString("status", state.status);
        state.filter.save(tag.child("filter"));
        state.buffer.save(tag.child("buffer"));
    }

    public void load(Machine machine, DataTag tag) {
        State state = state(machine);
        state.radius = Math.max(0, Math.min(MAX_RADIUS, tag.getInt("radius", DEFAULT_RADIUS)));
        state.minY = tag.getInt("min_y", 0);
        state.maxY = Math.max(state.minY, tag.getInt("max_y", DEFAULT_HEIGHT_RANGE));
        state.cursor = new MiningCursor(state.radius, state.minY, state.maxY,
                tag.getLong("cursor", 0)).index();
        state.mined = tag.getLong("mined", 0);
        state.progress = tag.getInt("progress", 0);
        state.running = tag.getBoolean("running", false);
        state.silk = tag.getBoolean("silk", false);
        state.autoEject = tag.getBoolean("auto_eject", false);
        state.autoPull = tag.getBoolean("auto_pull", false);
        state.inverseRequiresReplacement = tag.getBoolean("inverse_requires_replacement", false);
        state.visuals = tag.getBoolean("visuals", false);
        state.replace = tag.getString("replace", "");
        state.status = I18n.normalizeMinerStatus(tag.getString("status",
                state.running ? "scanning" : "stopped"));
        state.filter.load(tag.child("filter"));
        state.buffer.load(tag.child("buffer"));
    }
}
