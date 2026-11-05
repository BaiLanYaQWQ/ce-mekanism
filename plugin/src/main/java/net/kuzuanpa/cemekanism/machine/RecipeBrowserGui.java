package net.kuzuanpa.cemekanism.machine;

import mekcore.recipe.Ingredient;
import mekcore.recipe.ItemIngredient;
import mekcore.recipe.Recipe;
import mekcore.recipe.RecipeInput;
import mekcore.recipe.RecipeOutput;
import mekcore.resource.ResourceStack;
import mekcore.resource.ResourceType;
import mekcore.resource.ResourceTypes;
import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.kuzuanpa.cemekanism.i18n.I18n;
import net.kuzuanpa.cemekanism.multiblock.MultiblockBlockEntityController;
import net.kuzuanpa.cemekanism.gui.GuiText;
import net.kuzuanpa.cemekanism.platform.BukkitItems;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 从当前机器进入的只读配方浏览页，支持标签成员和跨机器来源/用途查询。
 * 配方每页五行、每行一条完整配方，
 * 标签用首个匹配物品 + 命名牌角标，流体用贴图 + 桶角标，化学品用纯色 + 滴管角标。
 */
public final class RecipeBrowserGui {

    public static final int ROWS = 6;
    public static final int SIZE = ROWS * 9;
    public static final int RECIPES_PER_PAGE = RecipeBrowserModel.RECIPES_PER_PAGE;
    public static final int FIRST_PAGE = 45;
    public static final int PREVIOUS_PAGE = 46;
    public static final int BACK = 48;
    public static final int PAGE_INFO = 49;
    public static final int HOME = 50;
    public static final int NEXT_PAGE = 52;
    public static final int LAST_PAGE = 53;

    public static final class Holder implements InventoryHolder {
        private Inventory inventory;
        private RecipeBrowserGui gui;

        @Override
        public Inventory getInventory() {
            return this.inventory;
        }

        public RecipeBrowserGui gui() {
            return this.gui;
        }
    }

    private final CeMekanismPlugin plugin;
    private final Predicate<Player> access;
    private final Consumer<RecipeBrowserGui> close;
    private final String entryTitle;
    private final RecipeCatalog catalog;
    private final Holder holder;
    private final Inventory inventory;
    private final RecipeBrowserModel model;

    private RecipeBrowserGui(CeMekanismPlugin plugin, String title, String usage, boolean catalogRoot,
                             Predicate<Player> access, Consumer<RecipeBrowserGui> close) {
        this.plugin = plugin;
        this.access = access;
        this.close = close;
        this.entryTitle = title;
        this.catalog = RecipeCatalog.load(plugin);
        var names = new LinkedHashMap<String, String>();
        this.catalog.categories.forEach((id, category) -> names.put(id, category.name()));
        this.model = new RecipeBrowserModel(title,
                this.catalog.recipes.stream().filter(recipe -> recipe.usage().equals(usage)).toList(),
                this.catalog.recipes, this.catalog.layouts, names, catalogRoot);
        this.holder = new Holder();
        this.inventory = Bukkit.createInventory(this.holder, SIZE,
                Component.text(I18n.tr("recipe.window_title", this.model.screen().title())));
        this.holder.inventory = this.inventory;
        this.holder.gui = this;
        render();
    }

    public static RecipeBrowserGui create(MachineBlockEntityController controller) {
        return new RecipeBrowserGui(controller.plugin(), I18n.machineName(controller.machine().blueprint()),
                controller.machine().blueprint().id(), false,
                player -> controller.registered() && controller.machine().canAccess(player.getUniqueId().toString()),
                controller::forgetRecipeBrowser);
    }

    public static RecipeBrowserGui create(MultiblockBlockEntityController controller) {
        var host = controller.host();
        return new RecipeBrowserGui(controller.plugin(), I18n.structureName(host.def().id()), host.def().id(), false,
                player -> controller.host() == host && host.formed(), controller::forgetRecipeBrowser);
    }

    public static void openCatalog(CeMekanismPlugin plugin, Player player) {
        new RecipeBrowserGui(plugin, I18n.tr(player, "recipe.all"), null, true,
                viewer -> viewer.hasPermission("mek.recipes"), gui -> {}).open(player);
    }

    public static RecipeBrowserGui of(Inventory inventory) {
        return inventory != null && inventory.getHolder() instanceof Holder holder
                ? holder.gui() : null;
    }

    public CeMekanismPlugin plugin() { return this.plugin; }
    public boolean canAccess(Player player) { return this.access.test(player); }
    public void forget() {
        this.plugin.forgetRecipeBrowser(this);
        this.close.accept(this);
    }

    public Inventory inventory() {
        return this.inventory;
    }

    public int recipeCount() {
        return this.model.screen().recipes().size();
    }

    public int page() {
        return this.model.page();
    }

    public int pageCount() {
        return this.model.pageCount();
    }

    public void open(Player player) {
        this.plugin.trackRecipeBrowser(this);
        player.openInventory(this.inventory);
    }

    public void turn(Player player, int delta) {
        refresh(player, this.model.turn(delta));
    }

    public void jump(Player player, boolean last) {
        turn(player, last ? pageCount() - 1 - page() : -page());
    }

    public void back(Player player) {
        refresh(player, this.model.back());
    }

    public void home(Player player) {
        refresh(player, this.model.home());
    }

    public void select(Player player, int slot, boolean usages) {
        refresh(player, this.model.select(slot, usages));
    }

    private void refresh(Player player, boolean changed) {
        if (!changed) return;
        render();
        player.getOpenInventory().setTitle(I18n.tr("recipe.window_title", this.model.screen().title()));
        player.updateInventory();
    }

    public void closeViewers() {
        for (var viewer : List.copyOf(this.inventory.getViewers())) {
            viewer.closeInventory();
        }
        this.inventory.clear();
    }

    private void render() {
        ItemStack filler = named(Material.GRAY_STAINED_GLASS_PANE, " ", List.of(), 1);
        GuiText.hideTooltip(filler);
        for (int index = 0; index < SIZE; index++) {
            this.inventory.setItem(index, filler);
        }

        var screen = this.model.screen();
        int start = page() * screen.perPage();
        int end = Math.min(screen.count(), start + screen.perPage());
        for (int index = start; index < end; index++) {
            if (screen.categoriesView()) {
                String usage = screen.categories().get(index);
                var category = this.catalog.categories.getOrDefault(usage, new RecipeCatalog.Category(usage, "minecraft:book"));
                ItemStack icon = BukkitItems.create(category.icon());
                if (icon == null) icon = new ItemStack(Material.BOOK);
                this.inventory.setItem(index - start, describe(icon, category.name(),
                        List.of(I18n.tr("recipe.count", this.model.byUsage(usage).size())), 1));
            } else if (screen.detailView()) {
                renderDetail(screen.recipes().get(index));
            } else if (screen.membersView()) {
                this.inventory.setItem(index - start, memberIcon(screen.members().get(index)));
            } else {
                renderRecipe(index - start, screen.recipes().get(index));
            }
        }
        if (screen.count() == 0) {
            this.inventory.setItem(22, named(Material.BARRIER,
                    I18n.tr(screen.membersView() ? "recipe.no_members" : "recipe.no_matches"),
                    List.of(), 1));
        }
        renderNavigation();
    }

    private void renderRecipe(int row, Recipe recipe) {
        int base = row * 9;
        var category = this.catalog.categories.getOrDefault(recipe.usage(),
                new RecipeCatalog.Category(recipe.usage(), "minecraft:knowledge_book"));
        ItemStack station = BukkitItems.create(category.icon());
        if (station == null) station = new ItemStack(Material.KNOWLEDGE_BOOK);
        RecipeIcons.station(station, category.icon());
        this.inventory.setItem(base, describe(station, category.name(),
                List.of(I18n.tr("recipe.open_detail")), 1));

        List<RecipeInput> inputs = recipe.inputs();
        for (int i = 0; i < inputs.size() && i < 3; i++) {
            this.inventory.setItem(base + 1 + i, inputIcon(inputs.get(i)));
        }
        this.inventory.setItem(base + 4, processingArrow(recipe));
        List<RecipeOutput> outputs = recipe.outputs();
        for (int i = 0; i < outputs.size() && i < 2; i++) {
            this.inventory.setItem(base + 5 + i, recipeOutputIcon(recipe, i));
        }
    }

    private ItemStack recipeOutputIcon(Recipe recipe, int index) {
        var external = this.catalog.external.get(recipe.id());
        if (external != null && index == 0) {
            var item = external.result(net.momirealms.craftengine.core.item.ItemBuildContext.empty());
            if (item.platformItem() instanceof ItemStack stack) {
                return itemIcon(stack, external.result().count(), "", List.of());
            }
        }
        return outputIcon(recipe.outputs().get(index));
    }

    private void renderDetail(Recipe recipe) {
        this.inventory.setItem(4, named(Material.KNOWLEDGE_BOOK, I18n.tr("recipe.detail"),
                recipe.note() == null || recipe.note().isBlank() ? List.of() : List.of(recipe.note()), 1));
        var slots = this.model.inputSlots(recipe);
        for (int index = 0; index < recipe.inputs().size(); index++) {
            this.inventory.setItem(slots.get(index), inputIcon(recipe.inputs().get(index)));
        }
        this.inventory.setItem(23, processingArrow(recipe));
        for (int index = 0; index < recipe.outputs().size(); index++) {
            this.inventory.setItem(RecipeBrowserModel.outputSlot(index), recipeOutputIcon(recipe, index));
        }
    }

    private static ItemStack processingArrow(Recipe recipe) {
        String duration = recipe.continuous() ? I18n.tr("recipe.continuous")
                : recipe.baseTicks() > 0 ? I18n.tr("recipe.ticks", GuiText.format(recipe.baseTicks()))
                : I18n.tr("recipe.duration_conditional");
        return named(Material.ARROW, "→", List.of(I18n.tr("recipe.duration", duration),
                I18n.tr("recipe.energy", GuiText.format(recipe.energyJoules()),
                        recipe.continuous() ? "J/t" : "J")), 1);
    }

    private void renderNavigation() {
        boolean first = page() == 0;
        boolean last = page() >= pageCount() - 1;
        var screen = this.model.screen();
        this.inventory.setItem(FIRST_PAGE, formattedNamed(Material.SPECTRAL_ARROW,
                GuiText.name(I18n.tr("navigation.first"), first ? "§8" : "§f"),
                List.of(), 1));
        this.inventory.setItem(PREVIOUS_PAGE, formattedNamed(Material.ARROW,
                GuiText.name(I18n.tr("navigation.previous"), first ? "§8" : "§f"),
                List.of(GuiText.lore(I18n.tr("navigation.shift_pages"))), 1));
        this.inventory.setItem(PAGE_INFO, named(Material.BOOK,
                I18n.tr("navigation.page", page() + 1, pageCount()),
                List.of(
                        screen.title(),
                        I18n.tr(screen.categoriesView() ? "recipe.category_count"
                                : screen.membersView() ? "recipe.member_count" : "recipe.count",
                                screen.count()),
                        I18n.tr("recipe.click_hint"),
                        I18n.tr("recipe.expand_hint")), 1));
        this.inventory.setItem(BACK, named(Material.OAK_DOOR, I18n.tr("navigation.back"),
                List.of(), 1));
        this.inventory.setItem(HOME, named(Material.COMPASS, I18n.tr("navigation.home"),
                List.of(this.entryTitle), 1));
        this.inventory.setItem(NEXT_PAGE, formattedNamed(Material.ARROW,
                GuiText.name(I18n.tr("navigation.next"), last ? "§8" : "§f"),
                List.of(GuiText.lore(I18n.tr("navigation.shift_pages"))), 1));
        this.inventory.setItem(LAST_PAGE, formattedNamed(Material.SPECTRAL_ARROW,
                GuiText.name(I18n.tr("navigation.last"), last ? "§8" : "§f"),
                List.of(), 1));
    }

    private static ItemStack inputIcon(RecipeInput input) {
        List<String> hints = inputHints(input);
        if (input instanceof ItemIngredient item) {
            String id = RecipeDisplay.itemId(item.matcher());
            ItemStack stack = id == null ? null : BukkitItems.create(id);
            if (stack == null) stack = new ItemStack(Material.NAME_TAG);
            boolean tag = RecipeBrowserModel.expandable(input);
            if (tag) RecipeIcons.tag(stack, id);
            else return itemIcon(stack, item.amount(), "", hints);
            return describe(stack, item.display() + " ×" + GuiText.format(item.amount()),
                    hints,
                    tag ? 1 : item.amount());
        }
        Ingredient ingredient = (Ingredient) input;
        return resourceIcon(ingredient.type(), RecipeDisplay.resourceId(ingredient.type(), ingredient.matcher()), ingredient.display(),
                ingredient.amount(), hints);
    }

    private static ItemStack outputIcon(RecipeOutput output) {
        if (output instanceof RecipeOutput.Item item) {
            ItemStack stack = BukkitItems.create(item.stack().id());
            if (stack == null) {
                stack = new ItemStack(Material.PAPER);
            }
            String chance = item.guaranteed() ? "" : I18n.tr("recipe.chance", formatChance(item.chancePercent()));
            return itemIcon(stack, item.stack().amount(), chance, List.of());
        }
        RecipeOutput.Resource resource = (RecipeOutput.Resource) output;
        String name = resourceName(resource.stack());
        String chance = resource.guaranteed() ? "" : I18n.tr("recipe.chance", formatChance(resource.chancePercent()));
        return resourceIcon(resource.stack().type(), resource.stack().id(),
                name + chance,
                resource.stack().amount(), List.of());
    }

    private static List<String> inputHints(RecipeInput input) {
        List<String> hints = new ArrayList<>();
        if (RecipeBrowserModel.expandable(input)) {
            hints.add(I18n.tr("recipe.expand_members", RecipeBrowserModel.members(input).size()));
        }
        return hints;
    }

    private static ItemStack memberIcon(RecipeBrowserModel.Focus focus) {
        if (focus.type() != null) {
            return resourceIcon(focus.type(), focus.id(), focus.name(), 1, List.of());
        }
        ItemStack stack = BukkitItems.create(focus.id());
        if (stack == null) stack = new ItemStack(Material.PAPER);
        return itemIcon(stack, 1, "", List.of());
    }

    private static ItemStack resourceIcon(ResourceType type, String id, String display, long amount,
                                          List<String> hints) {
        ItemStack stack = RecipeIcons.resource(type, id);
        if (stack == null) stack = new ItemStack(type.equals(ResourceTypes.ENERGY)
                ? Material.REDSTONE : Material.PAPER);
        List<String> lore = new ArrayList<>();
        hints.forEach(hint -> lore.add(GuiText.lore(hint)));
        ItemMeta meta = stack.getItemMeta();
        GuiText.writeTooltip(meta,
                GuiText.name(display + " ×" + GuiText.format(amount) + " "
                        + type.unit(), GuiText.resourceColor(type, id)),
                lore);
        stack.setItemMeta(meta);
        // Counts are in the tooltip; vanilla stack numerals would cover the bottom-right badge.
        return stack;
    }

    private static String resourceName(ResourceStack stack) {
        if (stack.id() == null) {
            return I18n.resourceType(stack.type());
        }
        return I18n.resourceName(stack.type(), stack.id());
    }

    private static String formatChance(double chance) {
        return chance == Math.rint(chance) ? Long.toString(Math.round(chance))
                : String.format(Locale.ROOT, "%.2f", chance);
    }

    private static ItemStack itemIcon(ItemStack source, long amount, String chance, List<String> hints) {
        ItemStack stack = source.clone();
        stack.setAmount((int) Math.max(1, Math.min(stack.getMaxStackSize(), amount)));
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(Component.empty().color(net.kyori.adventure.text.format.NamedTextColor.WHITE)
                .decoration(net.kyori.adventure.text.format.TextDecoration.ITALIC, false)
                .append(GuiText.itemName(source))
                .append(Component.text(amount == 1 && chance.isEmpty() ? "" : " ×" + GuiText.format(amount) + chance)));
        meta.setLore(hints.stream().map(GuiText::lore).toList());
        stack.setItemMeta(meta);
        return stack;
    }

    private static ItemStack describe(ItemStack source, String title, List<String> lore, long amount) {
        ItemStack stack = source.clone();
        int stackAmount = (int) Math.max(1, Math.min(stack.getMaxStackSize(), amount));
        stack.setAmount(stackAmount);
        ItemMeta meta = stack.getItemMeta();
        GuiText.applyTooltip(meta, title, lore);
        stack.setItemMeta(meta);
        return stack;
    }

    private static ItemStack named(Material material, String title, List<String> lore, int amount) {
        ItemStack stack = new ItemStack(material, Math.max(1, amount));
        ItemMeta meta = stack.getItemMeta();
        GuiText.applyTooltip(meta, title, lore);
        stack.setItemMeta(meta);
        return stack;
    }

    private static ItemStack formattedNamed(Material material, String title, List<String> lore, int amount) {
        ItemStack stack = new ItemStack(material, Math.max(1, amount));
        ItemMeta meta = stack.getItemMeta();
        GuiText.writeTooltip(meta, title, lore);
        stack.setItemMeta(meta);
        return stack;
    }
}
