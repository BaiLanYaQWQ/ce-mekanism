package net.kuzuanpa.cemekanism.machine;

import mekcore.item.ItemMatcher;
import mekcore.recipe.ItemIngredient;
import mekcore.recipe.Recipe;
import mekcore.recipe.RecipeInput;
import mekcore.recipe.RecipeOutput;
import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.kuzuanpa.cemekanism.generated.GeneratedContent;
import net.momirealms.craftengine.core.item.recipe.*;
import net.momirealms.craftengine.core.plugin.CraftEngine;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** A display snapshot of the live CE registry and the two Mekanism processing systems. */
final class RecipeCatalog {
    record Category(String name, String icon) {}
    final List<Recipe> recipes = new ArrayList<>();
    final Map<String, Category> categories = new LinkedHashMap<>();
    final Map<String, List<Integer>> layouts = new LinkedHashMap<>();
    final Map<String, FixedResultRecipe> external = new LinkedHashMap<>();

    static RecipeCatalog load(CeMekanismPlugin plugin) {
        RecipeCatalog catalog = new RecipeCatalog();
        var manager = CraftEngine.instance().recipeManager();
        for (RecipeType type : RecipeType.values()) {
            catalog.categories.put(usage(type), new Category(typeName(type), typeIcon(type)));
            for (var recipe : manager.recipesByType(type)) catalog.addExternal(recipe);
        }
        var registrars = plugin.core().registrars();
        for (var blueprint : registrars.machines().all()) {
            catalog.categories.put(blueprint.id(), new Category(blueprint.zhName(), blueprint.id()));
            catalog.recipes.addAll(registrars.recipes().byUsage(blueprint.id()));
        }
        for (var def : GeneratedContent.MULTIBLOCKS) {
            catalog.categories.put(def.id(), new Category(def.zh(), def.anchor()));
        }
        catalog.recipes.addAll(MultiblockRecipeDisplays.recipes());
        return catalog;
    }

    void addExternal(net.momirealms.craftengine.core.item.recipe.Recipe source) {
        if (!(source instanceof FixedResultRecipe result) || !source.canBeSearched()) return;
        // Include the source domain in the display id so core recipes cannot overwrite CE recipes.
        String id = "ce/" + source.id().asString();
        if (this.external.containsKey(id)) return;
        List<RecipeInput> inputs = new ArrayList<>();
        List<Integer> slots = new ArrayList<>();
        if (source instanceof CustomShapedRecipe shaped) {
            shapedInputs(shaped.parsedPattern(), inputs, slots);
        } else {
            for (var ingredient : source.ingredientsInUse()) {
                slots.add(10 + inputs.size() / 3 * 9 + inputs.size() % 3);
                inputs.add(convert(ingredient));
            }
        }
        if (inputs.size() > 9) return;
        int ticks = source instanceof CustomCookingRecipe cooking ? cooking.cookingTime() : 0;
        String note = source instanceof CustomShapedRecipe ? "有序合成：按详情页的摆位放置"
                : source instanceof CustomShapelessRecipe ? "无序合成：原料摆位不限" : "";
        if (source instanceof ConditionalRecipe conditional && conditional.hasCondition()) note += "；使用条件由配方配置决定";
        if (source.ingredientsInUse().stream().anyMatch(ingredient -> ingredient.predicate() != null)) {
            note += "；原料还需满足物品组件条件";
        }
        this.recipes.add(new Recipe(id, usage(source.type()), inputs,
                List.of(RecipeOutput.item(result.result().item().id().asString(), result.result().count())),
                ticks, 0, false, note));
        this.layouts.put(id, List.copyOf(slots));
        this.external.put(id, result);
    }

    static void shapedInputs(CustomShapedRecipe.ParsedPattern pattern, List<RecipeInput> inputs, List<Integer> slots) {
        var ingredients = pattern.ingredients();
        for (int row = 0; row < pattern.height(); row++) {
            for (int col = 0; col < pattern.width(); col++) {
                var ingredient = ingredients[row * pattern.width() + col];
                if (ingredient.isPresent()) {
                    inputs.add(convert(ingredient.get()));
                    slots.add(10 + row * 9 + col);
                }
            }
        }
    }

    private static ItemIngredient convert(net.momirealms.craftengine.core.item.recipe.Ingredient ingredient) {
        var ids = ingredient.items().stream().map(key -> key.key().asString()).toList();
        var tags = ingredient.elements().stream().filter(IngredientElement.Tag.class::isInstance)
                .map(IngredientElement.Tag.class::cast).map(tag -> "#" + tag.tag().asString()).toList();
        boolean alternatives = !tags.isEmpty() || ids.size() != 1;
        ItemMatcher matcher = alternatives ? new ItemMatcher.Or(ids.stream().map(ItemMatcher::id).toList())
                : ItemMatcher.id(ids.getFirst());
        String display = !tags.isEmpty() ? String.join(" / ", tags)
                : ids.size() == 1 ? mekcore.item.ItemNames.nameOf(ids.getFirst()) : "可选原料";
        return ItemIngredient.of(ingredient.count(), matcher, display);
    }

    static String usage(RecipeType type) { return "minecraft:" + type.id(); }

    static String typeName(RecipeType type) {
        return switch (type) {
            case CRAFTING -> "工作台";
            case SMELTING -> "熔炉";
            case BLASTING -> "高炉";
            case SMOKING -> "烟熏炉";
            case CAMPFIRE_COOKING -> "营火";
            case STONECUTTING -> "切石机";
            case SMITHING -> "锻造台";
            case BREWING -> "酿造台";
        };
    }

    static String typeIcon(RecipeType type) {
        return "minecraft:" + switch (type) {
            case CRAFTING -> "crafting_table";
            case SMELTING -> "furnace";
            case BLASTING -> "blast_furnace";
            case SMOKING -> "smoker";
            case CAMPFIRE_COOKING -> "campfire";
            case STONECUTTING -> "stonecutter";
            case SMITHING -> "smithing_table";
            case BREWING -> "brewing_stand";
        };
    }
}
