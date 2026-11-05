package net.kuzuanpa.cemekanism.machine;

import mekcore.item.ItemNames;
import mekcore.item.ItemRef;
import mekcore.recipe.Ingredient;
import mekcore.recipe.ItemIngredient;
import mekcore.recipe.Recipe;
import mekcore.recipe.RecipeInput;
import mekcore.recipe.RecipeOutput;
import mekcore.resource.ResourceNames;
import mekcore.resource.ResourceStack;
import mekcore.resource.ResourceType;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Read-only browsing state, independent of Bukkit inventories and display item metadata. */
final class RecipeBrowserModel {
    static final int RECIPES_PER_PAGE = 5;
    static final int MEMBERS_PER_PAGE = 45;

    /** A null resource type denotes an item, never a fluid/chemical with the same id. */
    record Focus(ResourceType type, String id) {
        String name() {
            return type == null ? ItemNames.nameOf(id) : ResourceNames.nameOf(type, id);
        }

        boolean accepts(RecipeInput input) {
            if (type == null) {
                return input instanceof ItemIngredient item && item.matcher().matches(ItemRef.of(id, 1));
            }
            return input instanceof Ingredient resource && type.equals(resource.type())
                    && resource.matcher().matches(new ResourceStack(type, id, 1));
        }

        boolean producedBy(RecipeOutput output) {
            if (output.chancePercent() <= 0) return false;
            if (type == null) {
                return output instanceof RecipeOutput.Item item && id.equals(item.stack().id());
            }
            return output instanceof RecipeOutput.Resource resource && type.equals(resource.stack().type())
                    && Objects.equals(id, resource.stack().id());
        }
    }

    record Screen(String title, List<Recipe> recipes, List<Focus> members, boolean membersView,
                  List<String> categories, boolean categoriesView, boolean detailView) {
        Screen(String title, List<Recipe> recipes, List<Focus> members, boolean membersView) {
            this(title, recipes, members, membersView, List.of(), false, false);
        }

        Screen {
            recipes = List.copyOf(recipes);
            members = List.copyOf(members);
            categories = List.copyOf(categories);
        }

        int count() { return categoriesView ? categories.size() : membersView ? members.size() : recipes.size(); }
        int perPage() { return detailView ? 1 : categoriesView || membersView ? MEMBERS_PER_PAGE : RECIPES_PER_PAGE; }
    }

    private record State(Screen screen, int page) {}

    private final List<Recipe> allRecipes;
    private final Screen root;
    private final Map<String, List<Integer>> layouts;
    private final Map<String, String> categoryNames;
    private final Deque<State> history = new ArrayDeque<>();
    private Screen screen;
    private int page;

    RecipeBrowserModel(String title, List<Recipe> recipes, List<Recipe> allRecipes) {
        this(title, recipes, allRecipes, Map.of(), Map.of(), false);
    }

    RecipeBrowserModel(String title, List<Recipe> recipes, List<Recipe> allRecipes,
                       Map<String, List<Integer>> layouts, Map<String, String> categoryNames, boolean catalogRoot) {
        this.allRecipes = List.copyOf(allRecipes);
        this.layouts = Map.copyOf(layouts);
        this.categoryNames = Map.copyOf(categoryNames);
        this.root = catalogRoot ? categoryScreen() : new Screen(title, recipes, List.of(), false);
        this.screen = this.root;
    }

    private Screen categoryScreen() {
        return new Screen("全部配方 · 选择分类", List.of(), List.of(), false,
                this.allRecipes.stream().map(Recipe::usage).distinct().toList(), true, false);
    }

    boolean catalog() {
        if (this.screen.categoriesView()) return false;
        show(categoryScreen());
        return true;
    }

    List<Recipe> byUsage(String usage) {
        return this.allRecipes.stream().filter(recipe -> recipe.usage().equals(usage)).toList();
    }

    List<Integer> inputSlots(Recipe recipe) {
        return this.layouts.getOrDefault(recipe.id(), java.util.stream.IntStream.range(0, recipe.inputs().size())
                .map(index -> 10 + index / 3 * 9 + index % 3).boxed().toList());
    }

    static int outputSlot(int index) { return 24 + index / 2 * 9 + index % 2; }

    Screen screen() { return this.screen; }
    int page() { return this.page; }
    int pageCount() { return Math.max(1, (this.screen.count() + this.screen.perPage() - 1) / this.screen.perPage()); }
    boolean canGoBack() { return !this.history.isEmpty(); }

    boolean turn(int delta) {
        int target = Math.max(0, Math.min(pageCount() - 1, this.page + delta));
        if (target == this.page) return false;
        this.page = target;
        return true;
    }

    boolean back() {
        if (this.history.isEmpty()) return false;
        State previous = this.history.removeLast();
        this.screen = previous.screen();
        this.page = previous.page();
        return true;
    }

    boolean home() {
        if (!canGoBack()) return false;
        this.history.clear();
        this.screen = this.root;
        this.page = 0;
        return true;
    }

    private void show(Screen next) {
        // Bound memory even when a player repeatedly follows cyclic production chains.
        if (this.history.size() == 64) this.history.removeFirst();
        this.history.addLast(new State(this.screen, this.page));
        this.screen = next;
        this.page = 0;
    }

    boolean select(int slot, boolean usages) {
        if (slot < 0 || slot >= MEMBERS_PER_PAGE) return false;
        if (this.screen.categoriesView()) {
            int index = this.page * MEMBERS_PER_PAGE + slot;
            if (index >= this.screen.categories().size()) return false;
            String usage = this.screen.categories().get(index);
            show(new Screen(this.categoryNames.getOrDefault(usage, usage), byUsage(usage), List.of(), false,
                    List.of(), false, usage.startsWith("minecraft:")));
            return true;
        }
        if (this.screen.membersView()) {
            int index = this.page * MEMBERS_PER_PAGE + slot;
            if (index >= this.screen.members().size()) return false;
            query(this.screen.members().get(index), usages);
            return true;
        }
        if (this.screen.detailView()) {
            Recipe recipe = this.screen.recipes().get(this.page);
            int input = inputSlots(recipe).indexOf(slot);
            if (input >= 0) return selectInput(recipe.inputs().get(input), usages);
            for (int output = 0; output < recipe.outputs().size(); output++) {
                if (slot == outputSlot(output)) return selectOutput(recipe.outputs().get(output), usages);
            }
            return false;
        }
        int index = this.page * RECIPES_PER_PAGE + slot / 9;
        if (index >= this.screen.recipes().size()) return false;
        Recipe recipe = this.screen.recipes().get(index);
        int column = slot % 9;
        if (column == 0) {
            show(new Screen("配方详情 · " + this.categoryNames.getOrDefault(recipe.usage(), recipe.usage()),
                    List.of(recipe), List.of(), false, List.of(), false, true));
            return true;
        }
        if (column >= 1 && column <= 3 && column <= recipe.inputs().size()) {
            return selectInput(recipe.inputs().get(column - 1), usages);
        }
        if (column >= 5 && column <= 6 && column - 5 < recipe.outputs().size()) {
            return selectOutput(recipe.outputs().get(column - 5), usages);
        }
        return false;
    }

    private boolean selectInput(RecipeInput input, boolean usages) {
        List<Focus> members = members(input);
        if (expandable(input)) {
            show(new Screen("可用成员 · " + input.display(), List.of(), members, true));
            return true;
        }
        if (members.isEmpty()) return false;
        query(members.getFirst(), usages);
        return true;
    }

    private boolean selectOutput(RecipeOutput output, boolean usages) {
        Focus focus = output instanceof RecipeOutput.Item item ? new Focus(null, item.stack().id())
                : new Focus(((RecipeOutput.Resource) output).stack().type(),
                ((RecipeOutput.Resource) output).stack().id());
        query(focus, usages);
        return true;
    }

    private void query(Focus focus, boolean usages) {
        List<Recipe> matches = this.allRecipes.stream().filter(recipe -> usages
                ? recipe.inputs().stream().anyMatch(focus::accepts)
                : recipe.outputs().stream().anyMatch(focus::producedBy)).toList();
        show(new Screen((usages ? "用途配方 · " : "来源配方 · ") + focus.name(), matches, List.of(), false));
    }

    static List<Focus> members(RecipeInput input) {
        if (input instanceof ItemIngredient item) {
            return RecipeDisplay.itemIds(item.matcher()).stream().map(id -> new Focus(null, id)).toList();
        }
        Ingredient resource = (Ingredient) input;
        if (resource.type().continuous()) return List.of(new Focus(resource.type(), null));
        return RecipeDisplay.resourceIds(resource.type(), resource.matcher()).stream()
                .map(id -> new Focus(resource.type(), id)).toList();
    }

    static boolean expandable(RecipeInput input) {
        boolean tag = input instanceof ItemIngredient item ? RecipeDisplay.hasTag(item.matcher())
                : RecipeDisplay.hasTag(((Ingredient) input).matcher());
        return tag || input instanceof ItemIngredient item && item.matcher() instanceof mekcore.item.ItemMatcher.Or
                || members(input).size() > 1;
    }
}
