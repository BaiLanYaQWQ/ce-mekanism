package net.kuzuanpa.cemekanism.storage;

import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.kuzuanpa.cemekanism.nuclear.WasteBarrelLogic;
import net.kuzuanpa.cemekanism.platform.BukkitItems;
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.core.block.parser.BlockStateParser;
import net.momirealms.craftengine.libraries.nbt.*;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

/** A packed box is an item containing a block snapshot. Right-click another face to unpack it. */
public final class CardboardBox implements Listener {
    private static final NamespacedKey PAYLOAD = new NamespacedKey("cemekanism", "cardboard_payload");
    private final CeMekanismPlugin plugin;
    public CardboardBox(CeMekanismPlugin plugin) { this.plugin = plugin; }
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void interact(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND || e.getAction() != Action.RIGHT_CLICK_BLOCK || StoredItems.empty(e.getItem())
                || !BukkitItems.idOf(e.getItem()).equals("mek:cardboard_box")) return;
        e.setCancelled(true);
        try {
            byte[] bytes = e.getItem().getItemMeta().getPersistentDataContainer().get(PAYLOAD, PersistentDataType.BYTE_ARRAY);
            if (bytes == null) pack(e.getPlayer(), e.getClickedBlock(), e.getItem());
            else unpack(e.getPlayer(), e.getClickedBlock().getRelative(e.getBlockFace()), e.getClickedBlock(), e.getItem(), NBT.readCompressed(bytes, false));
        } catch (IllegalArgumentException ex) { e.getPlayer().sendMessage(ex.getMessage()); }
    }
    public void pack(Player player, Block block, ItemStack held) {
        var c = plugin.machineAtQuiet(block.getWorld(), block.getX(), block.getY(), block.getZ());
        var custom = CraftEngineBlocks.getCustomBlockState(block);
        if (custom != null && c == null) throw new IllegalArgumentException("此结构件或自定义方块暂不支持装箱");
        if (c != null && WasteBarrelLogic.ID.equals(c.blockId())) {
            throw new IllegalArgumentException("放射性废料桶不能装箱");
        }
        if (c != null && !c.machine().canAccess(player.getUniqueId().toString())) throw new IllegalArgumentException("无权装箱此机器");
        if (block.getType().getHardness() < 0 || block.isLiquid() || block.getType().isAir()) throw new IllegalArgumentException("此方块不能装箱");
        BlockState state = block.getState();
        if (state instanceof TileState && !(state instanceof Chest || state instanceof Barrel || state instanceof ShulkerBox)) throw new IllegalArgumentException("此原版方块实体暂不支持装箱");
        BlockBreakEvent permission = new BlockBreakEvent(block, player); Bukkit.getPluginManager().callEvent(permission);
        if (permission.isCancelled()) throw new IllegalArgumentException("此处禁止装箱");
        CompoundTag tag = new CompoundTag();
        if (c != null) {
            tag.putString("custom", custom.owner().value().id().asString() + '[' + custom.getPropertiesAsString() + ']');
            CompoundTag root = new CompoundTag(); c.saveCustomData(root); tag.put("machine", root);
        } else {
            tag.putString("vanilla", block.getBlockData().getAsString());
            if (state instanceof Container container) {
                Inventory inv = state instanceof Chest chest ? chest.getBlockInventory() : container.getInventory();
                for (int i=0; i<inv.getSize(); i++) if (!StoredItems.empty(inv.getItem(i))) {
                    CompoundTag entry = new CompoundTag(); entry.putInt("slot", i); entry.putString("item", StoredItems.key(inv.getItem(i))); entry.putInt("amount", inv.getItem(i).getAmount());
                    ListTag items = tag.getList("items"); if (items == null) { items = new ListTag(); tag.put("items", items); } items.add(entry);
                }
                if (state instanceof org.bukkit.Nameable named && named.getCustomName() != null) tag.putString("name", named.getCustomName());
                if (state instanceof org.bukkit.block.Lockable locked) tag.putString("lock", locked.getLock());
                if (state instanceof org.bukkit.loot.Lootable loot && loot.getLootTable() != null) { tag.putString("loot", loot.getLootTable().getKey().toString()); tag.putLong("loot_seed", loot.getSeed()); }
            }
        }
        ItemStack box = BukkitItems.create("mek:cardboard_box"); var meta = box.getItemMeta();
        meta.getPersistentDataContainer().set(PAYLOAD, PersistentDataType.BYTE_ARRAY, NBT.writeCompressed(tag, false)); meta.setMaxStackSize(1);
        meta.lore(List.of(net.kyori.adventure.text.Component.text("已装箱 · ").append(c == null
                ? net.kyori.adventure.text.Component.translatable(block.getType().translationKey())
                : net.kyori.adventure.text.Component.text(c.machine().blueprint().zhName())))); box.setItemMeta(meta);
        if (c != null) c.setSuppressStorageDrops(true);
        else if (state instanceof Container container) (state instanceof Chest chest ? chest.getBlockInventory() : container.getInventory()).clear();
        if (custom != null) CraftEngineBlocks.remove(block); else block.setType(Material.AIR, false);
        held.setAmount(held.getAmount()-1);
        for (var leftover : player.getInventory().addItem(box).values()) player.getWorld().dropItemNaturally(player.getLocation(), leftover);
    }
    private void unpack(Player player, Block target, Block against, ItemStack held, CompoundTag tag) {
        if (!target.getType().isAir()) throw new IllegalArgumentException("需要空位置拆箱");
        BlockState replaced = target.getState();
        BlockPlaceEvent permission = new BlockPlaceEvent(target, replaced, against, held, player, true, EquipmentSlot.HAND);
        Bukkit.getPluginManager().callEvent(permission);
        if (permission.isCancelled() || !permission.canBuild()) throw new IllegalArgumentException("此处禁止拆箱");
        String custom = tag.getString("custom");
        if (custom != null) {
            var state = BlockStateParser.deserialize(custom); if (state == null) throw new IllegalArgumentException("箱内机器未注册");
            if (!CraftEngineBlocks.place(target.getLocation(), state, true)) throw new IllegalArgumentException("机器放置失败");
            // Refresh the CE cache before resolving the new block entity.
            var ce = net.momirealms.craftengine.bukkit.api.BukkitAdaptor.adapt(target.getWorld()).storageWorld();
            ce.setBlockStateAtIfLoaded(new net.momirealms.craftengine.core.world.BlockPos(target.getX(), target.getY(), target.getZ()), state);
            var c = plugin.machineAtQuiet(target.getWorld(), target.getX(), target.getY(), target.getZ());
            if (c == null) throw new IllegalStateException("装箱机器实体未创建");
            c.loadCustomData(tag.getCompound("machine")); StorageRuntime.resize(c.machine());
            plugin.logistics(target.getWorld()).detachMachine(c); plugin.logistics(target.getWorld()).attachMachine(c); c.markChanged();
        } else {
            target.setBlockData(Bukkit.createBlockData(tag.getString("vanilla")), false); BlockState state = target.getState();
            if (state instanceof Container container) {
                Inventory inv = container.getSnapshotInventory();
                ListTag items = tag.getList("items");
                if (items != null) for (int i=0; i<items.size(); i++) { CompoundTag entry = items.getCompound(i); inv.setItem(entry.getInt("slot"), StoredItems.item(entry.getString("item"), entry.getInt("amount"))); }
                if (state instanceof org.bukkit.Nameable named) named.setCustomName(tag.getString("name"));
                if (state instanceof org.bukkit.block.Lockable locked) locked.setLock(tag.getString("lock", ""));
                if (state instanceof org.bukkit.loot.Lootable loot && tag.containsKey("loot")) { loot.setLootTable(Bukkit.getLootTable(NamespacedKey.fromString(tag.getString("loot")))); loot.setSeed(tag.getLong("loot_seed")); }
                state.update(true, false);
            }
        }
        held.setAmount(held.getAmount()-1);
        for (var leftover : player.getInventory().addItem(BukkitItems.create("mek:cardboard_box")).values()) player.getWorld().dropItemNaturally(player.getLocation(), leftover);
    }
}
