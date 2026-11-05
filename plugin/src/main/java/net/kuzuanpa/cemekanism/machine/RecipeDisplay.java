package net.kuzuanpa.cemekanism.machine;

import mekcore.filter.Matcher;
import mekcore.item.ItemMatcher;
import mekcore.item.ItemRef;
import mekcore.item.ItemTags;
import mekcore.registry.Tags;
import mekcore.resource.ResourceStack;
import mekcore.resource.ResourceType;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Select representatives in registration order, checking the entire predicate. */
final class RecipeDisplay {
    private RecipeDisplay() {}

    static String itemId(ItemMatcher matcher) {
        return itemIds(matcher).stream().findFirst().orElse(null);
    }

    static List<String> itemIds(ItemMatcher matcher) {
        Set<String> candidates = new LinkedHashSet<>();
        itemCandidates(matcher, candidates);
        return candidates.stream().filter(id -> matcher.matches(ItemRef.of(id, 1)))
                .toList();
    }

    static boolean hasTag(ItemMatcher matcher) {
        if (matcher instanceof ItemMatcher.Tag) return true;
        if (matcher instanceof ItemMatcher.Or or) return or.parts().stream().anyMatch(RecipeDisplay::hasTag);
        if (matcher instanceof ItemMatcher.And and) return and.parts().stream().anyMatch(RecipeDisplay::hasTag);
        return false;
    }

    private static void itemCandidates(ItemMatcher matcher, Set<String> candidates) {
        if (matcher instanceof ItemMatcher.Id id) candidates.add(id.id());
        else if (matcher instanceof ItemMatcher.Tag tag) candidates.addAll(ItemTags.ids(tag.tag()));
        else if (matcher instanceof ItemMatcher.Or or) or.parts().forEach(part -> itemCandidates(part, candidates));
        else if (matcher instanceof ItemMatcher.And and) and.parts().forEach(part -> itemCandidates(part, candidates));
        // Not is a constraint, never a source of positive examples.
    }

    static String resourceId(ResourceType type, Matcher matcher) {
        return resourceIds(type, matcher).stream().findFirst().orElse(null);
    }

    static List<String> resourceIds(ResourceType type, Matcher matcher) {
        Set<String> candidates = new LinkedHashSet<>();
        resourceCandidates(type, matcher, candidates);
        return candidates.stream().filter(id -> matcher.matches(new ResourceStack(type, id, 1)))
                .toList();
    }

    static boolean hasTag(Matcher matcher) {
        if (matcher instanceof Matcher.Tag) return true;
        return matcher instanceof Matcher.Or or && or.parts().stream().anyMatch(RecipeDisplay::hasTag);
    }

    private static void resourceCandidates(ResourceType type, Matcher matcher, Set<String> candidates) {
        if (matcher instanceof Matcher.Id id) candidates.add(id.id());
        else if (matcher instanceof Matcher.Tag tag) candidates.addAll(Tags.ids(type, tag.tag()));
        else if (matcher instanceof Matcher.Or or) or.parts().forEach(part -> resourceCandidates(type, part, candidates));
    }
}
