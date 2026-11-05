package net.kuzuanpa.cemekanism.machine;

import mekcore.upgrade.UpgradeDefinition;
import net.kuzuanpa.cemekanism.gui.GuiText;
import net.kuzuanpa.cemekanism.platform.BukkitItems;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 独立升级界面。格子里的物品只是已安装计数的视图，不保存第二份物品：
 * 放入时直接写入 {@code machine.upgrades()}，取出时再创建对应的真实升级物品。
 */
public final class UpgradeInstallerGui {

    private static final int INVENTORY_SIZE = 27;

    private record Slot(UpgradeDefinition definition, int inventoryIndex) {
    }

    public static final class Holder implements InventoryHolder {
        private final MachineBlockEntityController controller;
        private Inventory inventory;
        private UpgradeInstallerGui gui;

        private Holder(MachineBlockEntityController controller) {
            this.controller = controller;
        }

        @Override
        public Inventory getInventory() {
            return this.inventory;
        }

        public MachineBlockEntityController controller() {
            return this.controller;
        }

        public UpgradeInstallerGui gui() {
            return this.gui;
        }
    }

    private final MachineBlockEntityController controller;
    private final Holder holder;
    private final Inventory inventory;
    private final List<Slot> slots = new ArrayList<>();

    private UpgradeInstallerGui(MachineBlockEntityController controller) {
        this.controller = controller;
        this.holder = new Holder(controller);
        this.inventory = Bukkit.createInventory(this.holder, INVENTORY_SIZE,
                Component.text("升级安装器 · " + controller.machine().blueprint().zhName()));
        this.holder.inventory = this.inventory;
        this.holder.gui = this;

        List<UpgradeDefinition> definitions = new ArrayList<>(
                controller.machine().upgrades().allowed());
        definitions.sort(Comparator.comparingInt((UpgradeDefinition definition) ->
                definition.kind().ordinal()).thenComparing(UpgradeDefinition::id));
        for (int index = 0; index < definitions.size() && index < 9; index++) {
            this.slots.add(new Slot(definitions.get(index), index));
        }
        refresh();
    }

    public static UpgradeInstallerGui create(MachineBlockEntityController controller) {
        return new UpgradeInstallerGui(controller);
    }

    public static UpgradeInstallerGui of(Inventory inventory) {
        return inventory != null && inventory.getHolder() instanceof Holder holder
                ? holder.gui() : null;
    }

    public MachineBlockEntityController controller() {
        return this.controller;
    }

    public Inventory inventory() {
        return this.inventory;
    }

    public void open(Player player) {
        player.openInventory(this.inventory);
    }

    public UpgradeDefinition definitionAt(int inventoryIndex) {
        for (Slot slot : this.slots) {
            if (slot.inventoryIndex == inventoryIndex) {
                return slot.definition;
            }
        }
        return null;
    }

    public UpgradeDefinition definitionOf(ItemStack stack) {
        if (stack == null || stack.getType().isAir()) {
            return null;
        }
        String id = BukkitItems.idOf(stack);
        for (Slot slot : this.slots) {
            if (slot.definition.id().equals(id)) {
                return slot.definition;
            }
        }
        return null;
    }

    public void refresh() {
        for (Slot slot : this.slots) {
            int installed = this.controller.machine().upgrades().count(slot.definition.id());
            this.inventory.setItem(slot.inventoryIndex, icon(slot.definition, installed));
        }
    }

    public int installFromCursor(Player player, UpgradeDefinition definition) {
        ItemStack cursor = player.getItemOnCursor();
        if (cursor == null || cursor.getType().isAir()) {
            return 0;
        }
        if (!definition.id().equals(BukkitItems.idOf(cursor))) {
            player.sendActionBar(Component.text("这个格子只能放入 " + definition.zhName()));
            return 0;
        }
        int installed = this.controller.installUpgrade(definition.id(), cursor.getAmount());
        if (installed <= 0) {
            player.sendActionBar(Component.text(definition.zhName() + " 已装满"));
            return 0;
        }
        int left = cursor.getAmount() - installed;
        if (left <= 0) {
            player.setItemOnCursor(null);
        } else {
            cursor.setAmount(left);
            player.setItemOnCursor(cursor);
        }
        player.updateInventory();
        return installed;
    }

    public int installFromPlayerSlot(Player player, int slot) {
        ItemStack source = player.getInventory().getItem(slot);
        UpgradeDefinition definition = definitionOf(source);
        if (definition == null) {
            return 0;
        }
        int installed = this.controller.installUpgrade(definition.id(), source.getAmount());
        if (installed <= 0) {
            player.sendActionBar(Component.text(definition.zhName() + " 已装满"));
            return 0;
        }
        int left = source.getAmount() - installed;
        if (left <= 0) {
            player.getInventory().setItem(slot, null);
        } else {
            source.setAmount(left);
            player.getInventory().setItem(slot, source);
        }
        player.updateInventory();
        return installed;
    }

    public void withdrawToCursor(Player player, UpgradeDefinition definition, int requested) {
        int installed = this.controller.machine().upgrades().count(definition.id());
        if (installed <= 0) {
            player.sendActionBar(Component.text("没有可卸下的" + definition.zhName()));
            return;
        }
        ItemStack template = BukkitItems.create(definition.id());
        if (template == null) {
            player.sendActionBar(Component.text("找不到物品 " + definition.id()));
            return;
        }
        ItemStack cursor = player.getItemOnCursor();
        int room;
        if (cursor == null || cursor.getType().isAir()) {
            room = template.getMaxStackSize();
        } else if (cursor.isSimilar(template)) {
            room = template.getMaxStackSize() - cursor.getAmount();
        } else {
            player.sendActionBar(Component.text("光标里已有其他物品"));
            return;
        }
        int amount = Math.min(Math.min(requested, installed), room);
        if (amount <= 0) {
            player.sendActionBar(Component.text("光标放不下更多升级"));
            return;
        }
        int removed = this.controller.removeUpgrade(definition.id(), amount);
        if (removed <= 0) {
            return;
        }
        if (cursor == null || cursor.getType().isAir()) {
            template.setAmount(removed);
            player.setItemOnCursor(template);
        } else {
            cursor.setAmount(cursor.getAmount() + removed);
            player.setItemOnCursor(cursor);
        }
        player.updateInventory();
    }

    public void withdrawAllToInventory(Player player, UpgradeDefinition definition) {
        int installed = this.controller.machine().upgrades().count(definition.id());
        if (installed <= 0) {
            player.sendActionBar(Component.text("没有可卸下的" + definition.zhName()));
            return;
        }
        ItemStack refund = BukkitItems.create(definition.id());
        if (refund == null) {
            player.sendActionBar(Component.text("找不到物品 " + definition.id()));
            return;
        }
        int removed = this.controller.removeUpgrade(definition.id(), installed);
        if (removed <= 0) {
            return;
        }
        refund.setAmount(removed);
        Map<Integer, ItemStack> leftovers = player.getInventory().addItem(refund);
        if (!leftovers.isEmpty()) {
            Location at = player.getLocation();
            leftovers.values().forEach(stack -> player.getWorld().dropItemNaturally(at, stack));
        }
        player.updateInventory();
    }

    public void closeViewers() {
        for (HumanEntity viewer : List.copyOf(this.inventory.getViewers())) {
            viewer.closeInventory();
        }
        this.inventory.clear();
    }

    private static ItemStack icon(UpgradeDefinition definition, int installed) {
        ItemStack stack = BukkitItems.create(definition.id());
        if (stack == null) {
            stack = new ItemStack(Material.PAPER);
        }
        stack.setAmount(Math.max(1, installed));
        ItemMeta meta = stack.getItemMeta();
        meta.setLore(List.of(
                GuiText.lore("已安装 " + installed + " / " + definition.maxInstalled()),
                GuiText.lore(installed > 0 ? "左键取出全部，右键取出一枚" : "手持该升级点击此格安装")
        ));
        stack.setItemMeta(meta);
        return stack;
    }
}
