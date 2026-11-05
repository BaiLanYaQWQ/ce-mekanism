package net.kuzuanpa.cemekanism.storage;

import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.kuzuanpa.cemekanism.gui.GuiSessions;
import net.kuzuanpa.cemekanism.gui.GuiText;
import net.kuzuanpa.cemekanism.gui.DigitalMinerGui;
import net.kuzuanpa.cemekanism.gui.MachineGui;
import net.kuzuanpa.cemekanism.i18n.I18n;
import net.kuzuanpa.cemekanism.machine.MachineBlockEntityController;
import net.kuzuanpa.cemekanism.platform.BukkitItems;
import net.momirealms.craftengine.bukkit.api.BukkitAdaptor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Player-facing miner filter editor. All stacks are virtual icons; clicks are always intercepted. */
public final class DigitalMinerFilterGui implements InventoryHolder {
    public static final class Holder implements InventoryHolder {
        private Inventory inventory;
        private DigitalMinerFilterGui gui;

        @Override
        public Inventory getInventory() {
            return inventory;
        }
    }

    private static final int RULE_SLOTS = 36;
    private final CeMekanismPlugin plugin;
    private final MachineBlockEntityController controller;
    private final Holder holder = new Holder();
    private final Inventory inventory;
    private final List<String> visibleRules = new ArrayList<>();
    private int page;

    private DigitalMinerFilterGui(MachineBlockEntityController controller, int page) {
        this.plugin = controller.plugin();
        this.controller = controller;
        this.page = Math.max(0, page);
        this.inventory = Bukkit.createInventory(holder, 54,
                DigitalMinerGui.filterTitle());
        this.holder.inventory = inventory;
        this.holder.gui = this;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public MachineBlockEntityController controller() {
        return controller;
    }

    public static DigitalMinerFilterGui of(Inventory inventory) {
        return inventory != null && inventory.getHolder() instanceof Holder holder
                ? holder.gui : null;
    }

    public static void open(MachineBlockEntityController controller, Player player, int page) {
        DigitalMinerFilterGui gui = new DigitalMinerFilterGui(controller, page);
        try {
            gui.authorize(player);
            gui.refresh(player);
            player.openInventory(gui.inventory);
            controller.plugin().guiSessions().open(player, new GuiSessions.View(
                    gui.holder, gui.inventory,
                    new Location(controller.world(), controller.x() + .5,
                            controller.y() + .5, controller.z() + .5),
                    DigitalMinerGui::filterTitle,
                    () -> gui.refresh(player),
                    () -> Integer.toHexString(Arrays.hashCode(gui.inventory.getContents())),
                    () -> { },
                    () -> gui.valid(player)));
        } catch (IllegalArgumentException exception) {
            player.sendMessage(exception.getMessage());
        }
    }

    private DigitalMinerLogic.State state() {
        return ((DigitalMinerLogic) controller.machine().blueprint().logic()).state(controller.machine());
    }

    private void authorize(Player player) {
        QioDashboard.authorizeController(controller, player);
    }

    private boolean valid(Player player) {
        try {
            authorize(player);
            return controller.blockId().equals("mek:digital_miner");
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private void refresh(Player player) {
        authorize(player);
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            inventory.setItem(slot, GuiText.invisiblePlaceholder());
        }
        visibleRules.clear();
        ItemSelection filter = state().filter;
        int maxPage = Math.max(0, (filter.rules.size() - 1) / RULE_SLOTS);
        page = Math.max(0, Math.min(page, maxPage));
        for (int i = page * RULE_SLOTS;
             i < Math.min(filter.rules.size(), (page + 1) * RULE_SLOTS); i++) {
            String selector = filter.rules.get(i);
            visibleRules.add(selector);
            inventory.setItem(i - page * RULE_SLOTS, icon(filter.rule(selector)));
        }
        button(36, DigitalMinerGui.icon("stats"), I18n.tr("filter.rules_title", filter.rules.size()),
                List.of(pageLabel(maxPage)));
        button(45, DigitalMinerGui.icon("back"), I18n.tr("filter.back"), List.of());
        button(46, DigitalMinerGui.icon("add"), I18n.tr("filter.add_item"),
                List.of(I18n.tr("filter.add_item_hint")));
        button(47, DigitalMinerGui.toggle("inverse", filter.inverse),
                I18n.tr("qio.filter_mode",
                        I18n.tr(filter.inverse ? "filter.blacklist" : "filter.whitelist")),
                List.of(I18n.tr("filter.whitelist_hint"), I18n.tr("filter.blacklist_hint")));
        button(48, DigitalMinerGui.icon("delete"), I18n.tr("filter.clear"),
                List.of(I18n.tr("filter.clear_hint")));
        button(49, DigitalMinerGui.icon("namespace"), I18n.tr("filter.add_namespace"),
                List.of(I18n.tr("filter.namespace_example")));
        button(50, state().replace.isBlank() ? DigitalMinerGui.icon("replace") : state().replace,
                I18n.tr("filter.inverse_target"),
                List.of(I18n.tr("filter.current_target", displayId(state().replace)),
                        I18n.tr("filter.set_target"), I18n.tr("filter.clear_target")));
        button(51, DigitalMinerGui.toggle("required", state().inverseRequiresReplacement),
                I18n.tr("filter.require_replacement"),
                List.of(I18n.tr("filter.require_replacement_hint")));
        button(52, DigitalMinerGui.icon("previous"), I18n.tr("navigation.previous"), List.of(pageLabel(maxPage)));
        button(53, DigitalMinerGui.icon("next"), I18n.tr("navigation.next"), List.of(pageLabel(maxPage)));
    }

    private String pageLabel(int maxPage) {
        return I18n.tr("navigation.page", page + 1, maxPage + 1);
    }

    private static String yesNo(boolean value) {
        return I18n.tr(value ? "state.on" : "state.off");
    }

    private static String displayId(String id) {
        return id == null || id.isBlank() ? I18n.tr("filter.no_replacement") : I18n.itemName(id);
    }

    private static ItemStack icon(ItemSelection.Rule rule) {
        String itemId = rule.selector().startsWith("#") ? DigitalMinerGui.icon("tag")
                : rule.selector().startsWith("@") ? DigitalMinerGui.icon("namespace") : rule.selector();
        ItemStack stack = BukkitItems.create(itemId);
        if (stack == null || stack.getType().isAir()) {
            stack = BukkitItems.create(DigitalMinerGui.icon("filter"));
        }
        if (stack == null) stack = GuiText.invisiblePlaceholder();
        ItemMeta meta = stack.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add(I18n.tr("filter.replace_with", displayId(rule.replacementTarget())));
        lore.add(I18n.tr("filter.requires_replacement", yesNo(rule.requiresReplacement())));
        lore.add(I18n.tr("filter.rule_click"));
        lore.add(I18n.tr("filter.rule_set_target"));
        lore.add(I18n.tr("filter.rule_toggle_requirement"));
        GuiText.applyTooltip(meta,
                (rule.enabled() ? "§a" : "§8") + (rule.selector().startsWith("#") || rule.selector().startsWith("@")
                        ? rule.selector() : I18n.itemName(rule.selector())), lore);
        stack.setItemMeta(meta);
        return stack;
    }

    private void button(int slot, String itemId, String title, List<String> lore) {
        inventory.setItem(slot, GuiText.button(itemId, title, lore));
    }

    public void click(Player player, int rawSlot, boolean shift, boolean right, String expectedRule) {
        authorize(player);
        ItemSelection filter = state().filter;
        switch (rawSlot) {
            case 45 -> {
                player.closeInventory();
                Bukkit.getScheduler().runTask(plugin,
                        () -> controller.openFor(BukkitAdaptor.adapt(player)));
                return;
            }
            case 46 -> {
                ItemStack held = player.getItemOnCursor();
                if (addItemRule(filter, held)) {
                    resetScan();
                }
            }
            case 47 -> filter.inverse = !filter.inverse;
            case 48 -> filter.rules.clear();
            case 49 -> {
                ItemStack held = player.getItemOnCursor();
                if (!StoredItems.empty(held)) {
                    String id = BukkitItems.idOf(held);
                    int split = id.indexOf(':');
                    if (split > 0 && filter.add("@" + id.substring(0, split))) {
                        resetScan();
                    }
                }
            }
            case 50 -> {
                ItemStack held = player.getItemOnCursor();
                if (right && StoredItems.empty(held)) {
                    setInverseTarget(player, "");
                } else {
                    setInverseTarget(player, blockIdOf(held));
                }
            }
            case 51 -> state().inverseRequiresReplacement = !state().inverseRequiresReplacement;
            case 52, 53 -> {
                page += rawSlot == 52 ? -1 : 1;
                refresh(player);
                return;
            }
            default -> {
                if (rawSlot < 0 || rawSlot >= visibleRules.size()
                        || !visibleRules.get(rawSlot).equals(expectedRule)) {
                    break;
                }
                String selector = visibleRules.get(rawSlot);
                if (shift && !right) {
                    filter.remove(selector);
                    resetScan();
                } else if (right && shift) {
                    filter.setRequiresReplacement(selector, !filter.requiresReplacement(selector));
                } else if (right) {
                    String target = blockIdOf(player.getItemOnCursor());
                    if (target == null && StoredItems.empty(player.getItemOnCursor())) {
                        target = "";
                    }
                    if (target != null) {
                        filter.setReplacementTarget(selector, target);
                    }
                } else {
                    filter.setEnabled(selector, !filter.enabled(selector));
                    resetScan();
                }
            }
        }
        if (rawSlot == 47 || rawSlot == 48 || rawSlot == 50
                || rawSlot == 51) {
            resetScan();
        }
        controller.markChanged();
        refresh(player);
        MachineGui.updateAnchors(controller);
    }

    public String ruleAt(int rawSlot) {
        return rawSlot >= 0 && rawSlot < visibleRules.size() ? visibleRules.get(rawSlot) : null;
    }

    private void setInverseTarget(Player player, String target) {
        if (target != null) {
            state().replace = target;
        } else {
            I18n.send(player, "filter.not_placeable");
        }
    }

    private static boolean addItemRule(ItemSelection filter, ItemStack held) {
        String id = blockIdOf(held);
        return id != null && filter.add(id);
    }

    private static String blockIdOf(ItemStack item) {
        if (StoredItems.empty(item) || !item.getType().isBlock()) {
            return null;
        }
        return item.getType().getKey().toString();
    }

    private void resetScan() {
        ((DigitalMinerLogic) controller.machine().blueprint().logic())
                .reset(controller.machine());
    }
}
