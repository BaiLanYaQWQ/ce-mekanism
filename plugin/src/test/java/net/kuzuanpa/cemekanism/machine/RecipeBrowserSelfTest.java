package net.kuzuanpa.cemekanism.machine;

import mekcore.api.MekCore;
import mekcore.filter.Matcher;
import mekcore.item.ItemMatcher;
import mekcore.item.ItemRef;
import mekcore.item.ItemTags;
import mekcore.recipe.Ingredient;
import mekcore.recipe.ItemIngredient;
import mekcore.recipe.Recipe;
import mekcore.recipe.RecipeOutput;
import mekcore.registry.Tags;
import mekcore.resource.ResourceTypes;
import net.kuzuanpa.cemekanism.generated.GeneratedContent;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

/** Player browsing scenarios without Paper: tag contents, both click directions and return paths. */
public final class RecipeBrowserSelfTest {
    private static int checks;

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }

    public static void main(String[] args) {
        ItemTags.define("browser-test:ores", "minecraft:iron_ore", "mek:ore_osmium");
        var enrich = Recipe.builder("test:enrich", "mek:enrichment_chamber")
                .input(ItemIngredient.tagged(3, "browser-test:ores"))
                .output(RecipeOutput.item("mek:dust_iron", 2)).build();
        var crush = Recipe.builder("test:crush", "mek:crusher")
                .input(ItemIngredient.item(9, "minecraft:iron_ore"))
                .input(ItemIngredient.item(1, "minecraft:iron_ore"))
                .output(RecipeOutput.chance(ItemRef.of("mek:dust_iron", 1), 25)).build();
        var smelt = Recipe.builder("test:smelt", "mek:energized_smelter")
                .input(ItemIngredient.item(1, "mek:dust_iron"))
                .output(RecipeOutput.item("minecraft:iron_ingot", 1)).build();
        var zeroChance = Recipe.builder("test:never", "mek:crusher")
                .output(RecipeOutput.chance(ItemRef.of("mek:dust_iron", 1), 0)).build();
        var recipes = List.of(enrich, crush, smelt, zeroChance);
        var model = new RecipeBrowserModel("富集仓", List.of(enrich), recipes);

        check(model.select(1, false), "click tag opens members");
        check(model.screen().membersView(), "tag gets a separate screen");
        check(model.screen().members().stream().map(RecipeBrowserModel.Focus::id).toList()
                .equals(List.of("minecraft:iron_ore", "mek:ore_osmium")), "all tag members in registration order");
        check(model.select(0, true), "right click tag member queries usage");
        check(model.screen().recipes().equals(List.of(enrich, crush)),
                "usage spans machines, matches tag inputs, ignores quantity, lists recipes once");
        check(model.back() && model.screen().membersView(), "back returns to members");
        check(model.select(1, false) && model.screen().recipes().isEmpty(), "member with no source shows empty results");
        check(model.pageCount() == 1 && !model.select(22, false), "empty screen stays safe");
        check(model.home() && model.screen().recipes().equals(List.of(enrich)), "home returns to machine recipes");
        check(!model.back(), "home clears browsing history");

        check(model.select(5, false), "left click output queries sources");
        check(model.screen().recipes().equals(List.of(enrich, crush)), "sources include chance outputs, exclude zero chance");
        check(model.home() && model.select(5, true), "right click output queries usages");
        check(model.screen().recipes().equals(List.of(smelt)), "output usage follows next processing step");
        check(model.select(1, false) && model.screen().recipes().equals(List.of(enrich, crush)),
                "left click plain input follows previous processing step");
        for (int slot : List.of(-999, 4, 7, 8, 44, 45, 49, 53, 54, 80)) {
            check(!model.select(slot, true), "noningredient slot ignored: " + slot);
        }

        var constrained = ItemIngredient.of(1, ItemMatcher.and(ItemMatcher.tag("browser-test:ores"),
                ItemMatcher.not(ItemMatcher.id("minecraft:iron_ore"))), "筛选矿石");
        check(RecipeBrowserModel.members(constrained).stream().map(RecipeBrowserModel.Focus::id).toList()
                .equals(List.of("mek:ore_osmium")), "member list respects the entire input predicate");
        var alternatives = ItemIngredient.of(1, ItemMatcher.or(ItemMatcher.id("minecraft:stone"),
                ItemMatcher.id("minecraft:stone"), ItemMatcher.id("minecraft:coal")), "石头或煤");
        check(RecipeBrowserModel.expandable(alternatives) && RecipeBrowserModel.members(alternatives).size() == 2,
                "multiple explicit alternatives expand without duplicates");
        var empty = Recipe.builder("test:empty", "mek:crusher")
                .input(ItemIngredient.tagged(1, "browser-test:missing")).build();
        model = new RecipeBrowserModel("空标签", List.of(empty), List.of(empty));
        check(model.select(1, true) && model.screen().membersView() && model.screen().count() == 0,
                "either click on an empty tag opens an empty member screen");
        check(!model.select(0, false) && model.back(), "empty tag has no phantom member and allows return");

        String[] manyIds = IntStream.range(0, 100).mapToObj(i -> "test:member_" + i).toArray(String[]::new);
        ItemTags.define("browser-test:many", manyIds);
        var many = Recipe.builder("test:many", "mek:crusher")
                .input(ItemIngredient.tagged(1, "browser-test:many")).build();
        var rootRecipes = IntStream.range(0, 12).mapToObj(i -> many).toList();
        model = new RecipeBrowserModel("分页", rootRecipes, List.of(many));
        check(model.pageCount() == 3 && model.turn(1), "recipe pagination");
        check(model.select(1, false) && model.page() == 0 && model.pageCount() == 3, "tag pagination starts at first page");
        check(model.turn(10) && model.page() == 2, "shift page step clamps to last member page");
        check(!model.select(10, true), "last member page ignores blank slots");
        check(model.select(0, true) && model.screen().recipes().equals(List.of(many)), "member on last page queries correct id");
        check(model.back() && model.screen().membersView() && model.page() == 2, "back preserves member page");
        check(model.back() && !model.screen().membersView() && model.page() == 1, "back preserves original recipe page");
        check(model.turn(-10) && model.page() == 0 && !model.turn(-1), "first page clamping");

        Tags.define(ResourceTypes.FLUID, "browser-test:liquids", "test:shared", "minecraft:water");
        Tags.define(ResourceTypes.CHEMICAL, "browser-test:liquids", "mek:hydrogen");
        var fluid = Recipe.builder("test:fluid", "mek:pump")
                .input(Ingredient.tagged(ResourceTypes.FLUID, 1000, "browser-test:liquids"))
                .output(RecipeOutput.fluid("test:shared", 1000)).build();
        var chemical = Recipe.builder("test:chemical", "mek:chemical_infuser")
                .input(Ingredient.chemical(100, "test:shared"))
                .output(RecipeOutput.chemical("test:shared", 100)).build();
        var sameItem = Recipe.builder("test:item", "mek:crusher")
                .input(ItemIngredient.item(2, "test:shared"))
                .output(RecipeOutput.item("test:shared", 1)).build();
        model = new RecipeBrowserModel("流体", List.of(fluid), List.of(fluid, chemical, sameItem));
        check(model.select(1, false) && model.screen().members().size() == 2, "fluid tags expand within their resource type");
        check(model.select(0, false) && model.screen().recipes().equals(List.of(fluid)), "fluid sources cannot match items or chemicals");
        check(model.home() && model.select(5, true) && model.screen().recipes().equals(List.of(fluid)), "fluid output usage matches fluid tag");
        check(RecipeDisplay.hasTag(Matcher.or(Matcher.tag("browser-test:liquids"), Matcher.id("test:other"))),
                "nested resource tag detected");
        check(!RecipeDisplay.hasTag(Matcher.not(Matcher.tag("browser-test:liquids"))), "excluded resource tag is not expandable");

        // The real generated registration must support the same cross-machine chain as the fixtures.
        var core = MekCore.create(GeneratedContent::register);
        var registry = core.registrars().recipes();
        var all = registry.usageHistogram().keySet().stream().flatMap(usage -> registry.byUsage(usage).stream()).toList();
        var expected = all.stream().filter(recipe -> recipe.itemInputs().stream()
                .anyMatch(input -> input.matcher().matches(ItemRef.of("mek:dust_iron", 1)))).toList();
        check(!expected.isEmpty(), "generated content has iron dust usages");
        model = new RecipeBrowserModel("生成配方", List.of(enrich), all);
        check(model.select(5, true) && model.screen().recipes().equals(expected), "real registration used for global usage lookup");
        check(all.size() == registry.size(), "global recipe list covers all registered machines");

        catalogScenarios();

        System.out.println("Recipe browser self-test: " + checks + " checks passed");
    }

    private static void catalogScenarios() {
        var catalog = new RecipeCatalog();
        for (var type : net.momirealms.craftengine.core.item.recipe.RecipeType.values()) {
            catalog.addExternal(external(type));
        }
        check(catalog.recipes.size() == 8, "CE recipe adapter includes crafting, cooking, cutting, smithing and brewing");
        catalog.addExternal(external(net.momirealms.craftengine.core.item.recipe.RecipeType.CRAFTING));
        check(catalog.recipes.size() == 8, "same CE recipe is not added twice");
        for (var recipe : catalog.recipes) {
            check(recipe.inputs().getFirst().amount() == 3 && ((RecipeOutput.Item) recipe.outputs().getFirst()).stack().amount() == 4,
                    "external recipe input and result counts preserved: " + recipe.usage());
            check(RecipeBrowserModel.expandable(recipe.inputs().getFirst()), "singleton CE tag remains expandable");
        }

        var ingredient = externalIngredient();
        @SuppressWarnings("unchecked")
        Optional<net.momirealms.craftengine.core.item.recipe.Ingredient>[] grid = new Optional[] {
                Optional.of(ingredient), Optional.empty(), Optional.of(ingredient),
                Optional.empty(), Optional.of(ingredient), Optional.empty(),
                Optional.of(ingredient), Optional.empty(), Optional.of(ingredient)
        };
        var inputs = new ArrayList<mekcore.recipe.RecipeInput>();
        var slots = new ArrayList<Integer>();
        RecipeCatalog.shapedInputs(new net.momirealms.craftengine.core.item.recipe.CustomShapedRecipe.ParsedPattern(3, 3, grid), inputs, slots);
        check(slots.equals(List.of(10, 12, 20, 28, 30)), "shaped recipe holes and all three rows preserved");
        check(inputs.size() == 5, "repeated symbols count as separate grid ingredients");
        var shaped = new Recipe("test:grid", "minecraft:crafting", inputs,
                List.of(RecipeOutput.item("test:shaped_result", 1)), 0, 0, false, "有序合成");
        var core = Recipe.builder("test:machine_source", "mek:crusher")
                .output(RecipeOutput.item("minecraft:stone", 1)).build();
        var all = new ArrayList<>(catalog.recipes);
        all.add(shaped);
        all.add(core);
        all.addAll(MultiblockRecipeDisplays.recipes());
        var model = new RecipeBrowserModel("全部配方", List.of(), all, Map.of(shaped.id(), slots), Map.of(), true);
        check(model.screen().categoriesView() && model.screen().categories().contains("minecraft:stonecutting"), "global category entry includes stonecutter");
        int crafting = model.screen().categories().indexOf("minecraft:crafting");
        check(model.select(crafting, false) && model.screen().detailView() && model.screen().recipes().size() == 2,
                "native category opens full recipe detail pages");
        check(model.turn(1) && model.page() == 1, "native details support paging");
        check(!model.select(11, false), "hole in shaped recipe is not clickable");
        check(model.select(30, false) && model.screen().membersView(), "bottom row ingredient supports tag browsing");
        check(model.select(0, false) && model.screen().recipes().equals(List.of(core)), "native ingredient can find machine source");
        check(model.back() && model.back() && model.screen().detailView() && model.page() == 1, "nested return restores native detail page");
        check(model.select(24, false) && model.screen().recipes().equals(List.of(shaped)), "detail output supports source lookup");
        check(model.home() && model.screen().categoriesView(), "global home returns to categories");

        var definitions = GeneratedContent.MULTIBLOCKS.stream()
                .map(net.kuzuanpa.cemekanism.multiblock.MultiblockDef::id).toList();
        for (var recipe : MultiblockRecipeDisplays.recipes()) {
            check(definitions.contains(recipe.usage()), "multiblock display uses real structure id: " + recipe.usage());
        }
        var polonium = Recipe.builder("test:polonium", "mek:solar_neutron_activator")
                .output(RecipeOutput.chemical("mek:polonium", 1)).build();
        all.add(polonium);
        model = new RecipeBrowserModel("单机", List.of(polonium), all);
        check(model.select(5, true) && model.screen().recipes().stream().anyMatch(recipe -> recipe.usage().equals("mek:sps")),
                "single-machine output links to SPS usage");
        var antimatter = Recipe.builder("test:antimatter_use", "mek:chemical_crystallizer")
                .input(Ingredient.chemical(1, "mek:antimatter")).build();
        model = new RecipeBrowserModel("反物质", List.of(antimatter), all);
        check(model.select(1, false) && model.screen().recipes().stream().anyMatch(recipe -> recipe.id().equals("display:sps/antimatter")),
                "antimatter source query reaches multiblock display");
        var sps = model.screen().recipes().getFirst();
        check(sps.inputs().getFirst().amount() == net.kuzuanpa.cemekanism.multiblock.MultiblockConfig.spsInputPerAntimatter()
                && sps.energyJoules() == net.kuzuanpa.cemekanism.multiblock.MultiblockConfig.spsInputPerAntimatter()
                * net.kuzuanpa.cemekanism.multiblock.MultiblockConfig.spsEnergyPerInputJ(), "SPS display follows current configuration");
    }

    private static net.momirealms.craftengine.core.item.recipe.Ingredient externalIngredient() {
        var id = net.momirealms.craftengine.core.util.Key.of("minecraft:stone");
        var keys = Set.of(net.momirealms.craftengine.core.util.UniqueKey.create(id));
        return net.momirealms.craftengine.core.item.recipe.Ingredient.of(
                List.of(net.momirealms.craftengine.core.item.recipe.IngredientElement.tag(
                        net.momirealms.craftengine.core.util.Key.of("minecraft:stone_crafting_materials"))), keys, keys, false, 3, null);
    }

    private static net.momirealms.craftengine.core.item.recipe.FixedResultRecipe external(
            net.momirealms.craftengine.core.item.recipe.RecipeType type) {
        var item = new net.momirealms.craftengine.core.item.BuildableItem() {
            public net.momirealms.craftengine.core.util.Key id() { return net.momirealms.craftengine.core.util.Key.of("minecraft:diamond"); }
            public net.momirealms.craftengine.core.item.Item buildItem(net.momirealms.craftengine.core.item.ItemBuildContext context, int count) {
                throw new AssertionError("catalog conversion must not build display items");
            }
        };
        return (net.momirealms.craftengine.core.item.recipe.FixedResultRecipe) Proxy.newProxyInstance(
                RecipeBrowserSelfTest.class.getClassLoader(),
                new Class<?>[] {net.momirealms.craftengine.core.item.recipe.FixedResultRecipe.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "id" -> net.momirealms.craftengine.core.util.Key.of("test:" + type.id());
                    case "type" -> type;
                    case "canBeSearched" -> true;
                    case "ingredientsInUse" -> List.of(externalIngredient());
                    case "result" -> new net.momirealms.craftengine.core.item.recipe.result.CustomRecipeResult(item, 4, null);
                    default -> throw new AssertionError("unexpected recipe adapter call: " + method.getName());
                });
    }
}
