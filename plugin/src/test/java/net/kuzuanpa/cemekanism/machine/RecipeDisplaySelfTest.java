package net.kuzuanpa.cemekanism.machine;

import mekcore.filter.Matcher;
import mekcore.item.ItemMatcher;
import mekcore.item.ItemTags;
import mekcore.registry.Tags;
import mekcore.resource.ResourceTypes;

/** Regression cases for tag representatives and nested matching constraints. */
public final class RecipeDisplaySelfTest {
    private static int checks;

    private static void check(boolean value, String message) {
        checks++;
        if (!value) throw new AssertionError(message);
    }

    public static void main(String[] args) {
        ItemTags.define("recipe-test:ores", "minecraft:iron_ore", "mek:ore_osmium");
        var tag = ItemMatcher.tag("recipe-test:ores");
        check("minecraft:iron_ore".equals(RecipeDisplay.itemId(tag)), "first registered tag member");
        check(RecipeDisplay.hasTag(tag), "tag badge required");
        var constrained = ItemMatcher.and(tag, ItemMatcher.not(ItemMatcher.id("minecraft:iron_ore")));
        check("mek:ore_osmium".equals(RecipeDisplay.itemId(constrained)), "representative must match every constraint");
        check(RecipeDisplay.hasTag(constrained), "nested tag badge required");
        check(RecipeDisplay.itemId(ItemMatcher.and(tag, ItemMatcher.id("minecraft:stone"))) == null,
                "never display a nonmatching item");
        check(RecipeDisplay.itemId(ItemMatcher.tag("recipe-test:empty")) == null, "empty tag fallback");
        check(RecipeDisplay.itemId(ItemMatcher.not(tag)) == null, "excluded tag is not a representative");
        check(!RecipeDisplay.hasTag(ItemMatcher.not(tag)), "excluded tag does not get a tag badge");
        check("minecraft:stone".equals(RecipeDisplay.itemId(ItemMatcher.or(
                ItemMatcher.tag("recipe-test:empty"), ItemMatcher.id("minecraft:stone")))), "OR fallback");
        check(!RecipeDisplay.hasTag(ItemMatcher.id("minecraft:stone")), "plain items have no badge");
        Tags.define(ResourceTypes.FLUID, "recipe-test:water", "minecraft:water", "mek:heavy_water");
        check("minecraft:water".equals(RecipeDisplay.resourceId(ResourceTypes.FLUID,
                Matcher.tag("recipe-test:water"))), "fluid tag resolves texture identity");
        check(RecipeDisplay.resourceId(ResourceTypes.CHEMICAL, Matcher.tag("recipe-test:water")) == null,
                "resource tags are scoped to their type");
        check(RecipeDisplay.resourceId(ResourceTypes.FLUID, Matcher.not(Matcher.id("minecraft:water"))) == null,
                "never use excluded fluid texture");
        check("mek:hydrogen".equals(RecipeDisplay.resourceId(ResourceTypes.CHEMICAL, Matcher.or(
                Matcher.tag("recipe-test:empty"), Matcher.id("mek:hydrogen")))), "chemical alternatives");
        System.out.println("Recipe display self-test: " + checks + " checks passed");
    }
}
