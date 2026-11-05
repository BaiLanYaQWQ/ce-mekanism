package net.kuzuanpa.cemekanism.storage;

import mekcore.persist.DataTag;
import net.kuzuanpa.cemekanism.platform.BukkitItems;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Reusable id / block-or-item tag / namespace filters with whitelist and blacklist semantics. */
public final class ItemSelection {
    public record Rule(String selector, boolean enabled, String replacementTarget, boolean requiresReplacement) {
        public Rule {
            replacementTarget = replacementTarget == null ? "" : replacementTarget;
        }
    }

    public final List<String> rules = new ArrayList<>();
    public boolean inverse;
    public boolean matches(ItemStack item) {
        return matches(BukkitItems.idOf(item), item.getType());
    }

    public boolean matches(String id, Material material) {
        return inverse != (matchingRule(id, material) != null);
    }

    public Rule matchingRule(String id, Material material) {
        for (String selector : rules) {
            if (enabled(selector) && selectorMatches(selector, id, material)) {
                return rule(selector);
            }
        }
        return null;
    }

    private static boolean selectorMatches(String rule, String id, Material material) {
        if (rule.startsWith("@")) {
            return id.startsWith(rule.substring(1) + ':');
        }
        if (rule.startsWith("#")) {
            NamespacedKey key = NamespacedKey.fromString(rule.substring(1));
            if (key == null) {
                return false;
            }
            Tag<Material> blocks = Bukkit.getTag(Tag.REGISTRY_BLOCKS, key, Material.class);
            Tag<Material> items = Bukkit.getTag(Tag.REGISTRY_ITEMS, key, Material.class);
            return blocks != null && blocks.isTagged(material) || items != null && items.isTagged(material)
                    || mekcore.item.ItemTags.has(rule.substring(1), id);
        }
        return id.equals(rule);
    }

    public boolean add(String selector) {
        String normalized = normalize(selector);
        if (normalized.isEmpty() || rules.contains(normalized)) {
            return false;
        }
        rules.add(normalized);
        return true;
    }

    public boolean remove(String selector) {
        return rules.remove(normalize(selector));
    }

    public boolean enabled(String selector) {
        Rule settings = ruleSettings.get(normalize(selector));
        return settings == null || settings.enabled();
    }

    public void setEnabled(String selector, boolean value) {
        update(normalize(selector), new Rule(normalize(selector), value,
                replacementTarget(selector), requiresReplacement(selector)));
    }

    public String replacementTarget(String selector) {
        Rule settings = ruleSettings.get(normalize(selector));
        return settings == null ? "" : settings.replacementTarget();
    }

    public void setReplacementTarget(String selector, String target) {
        String normalized = normalize(selector);
        String replacement = target == null ? "" : target.trim().toLowerCase(Locale.ROOT);
        update(normalized, new Rule(normalized, enabled(normalized), replacement, requiresReplacement(normalized)));
    }

    public boolean requiresReplacement(String selector) {
        Rule settings = ruleSettings.get(normalize(selector));
        return settings != null && settings.requiresReplacement();
    }

    public void setRequiresReplacement(String selector, boolean value) {
        String normalized = normalize(selector);
        update(normalized, new Rule(normalized, enabled(normalized), replacementTarget(normalized), value));
    }

    public Rule rule(String selector) {
        String normalized = normalize(selector);
        return new Rule(normalized, enabled(normalized), replacementTarget(normalized),
                requiresReplacement(normalized));
    }

    private void update(String selector, Rule value) {
        if (rules.contains(selector)) {
            ruleSettings.put(selector, value);
        }
    }

    private static String normalize(String selector) {
        return selector == null ? "" : selector.trim().toLowerCase(Locale.ROOT);
    }

    public void save(DataTag tag) {
        tag.putBoolean("inverse", inverse);
        // Keep the v1 string for old readers while v2 preserves per-filter settings.
        tag.putString("rules", String.join(",", rules));
        tag.remove("filter_rules");
        for (String selector : rules) {
            Rule value = rule(selector);
            DataTag row = tag.newElement("filter_rules");
            row.putString("selector", value.selector());
            row.putBoolean("enabled", value.enabled());
            row.putString("replace", value.replacementTarget());
            row.putBoolean("requires_replacement", value.requiresReplacement());
        }
    }

    public void load(DataTag tag) {
        inverse = tag.getBoolean("inverse", false);
        rules.clear();
        ruleSettings.clear();
        List<DataTag> configured = tag.list("filter_rules");
        if (!configured.isEmpty()) {
            for (DataTag row : configured) {
                String selector = normalize(row.getString("selector", ""));
                if (!selector.isEmpty() && !rules.contains(selector)) {
                    rules.add(selector);
                    Rule value = new Rule(selector, row.getBoolean("enabled", true),
                            normalize(row.getString("replace", "")),
                            row.getBoolean("requires_replacement", false));
                    update(selector, value);
                }
            }
        } else {
            for (String raw : tag.getString("rules", "").split(",")) {
                add(raw);
            }
        }
    }

    public String describe() {
        long disabled = rules.stream().filter(rule -> !enabled(rule)).count();
        return (inverse ? "黑名单 " : "白名单 ") + rules
                + (disabled == 0 ? "" : "（停用 " + disabled + "）");
    }

    private final Map<String, Rule> ruleSettings = new LinkedHashMap<>();
}
