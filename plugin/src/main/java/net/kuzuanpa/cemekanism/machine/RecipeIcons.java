package net.kuzuanpa.cemekanism.machine;

import mekcore.resource.ResourceType;
import mekcore.resource.ResourceTypes;
import net.kuzuanpa.cemekanism.gui.GuiText;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Display-only composite models; never change an ingredient's actual item definition. */
final class RecipeIcons {
    private static final Set<String> TAG_MODELS = loadModels("recipe-tag-icons.txt");
    private static final Set<String> STATION_MODELS = loadModels("recipe-station-icons.txt");

    private RecipeIcons() {}

    static ItemStack tag(ItemStack stack, String id) {
        if (id != null && TAG_MODELS.contains(id)) {
            apply(stack, "tag/" + id.replace(':', '/'), null);
        }
        return stack;
    }

    static ItemStack station(ItemStack stack, String id) {
        if (id != null && STATION_MODELS.contains(id)) {
            apply(stack, "station/" + id.replace(':', '/'), null);
        }
        return stack;
    }

    static ItemStack resource(ResourceType type, String id) {
        String model;
        if (type.equals(ResourceTypes.FLUID)) {
            model = "fluid_" + switch (id == null ? "" : id) {
                case "minecraft:water", "mek:heavy_water" -> "water";
                case "minecraft:lava" -> "lava";
                case "mek:steam" -> "steam";
                default -> "liquid";
            };
        } else if (type.equals(ResourceTypes.CHEMICAL) || type.equals(ResourceTypes.INFUSION)) {
            model = "chemical";
        } else {
            return null;
        }
        ItemStack stack = new ItemStack(Material.PAPER);
        apply(stack, model, GuiText.resourceColor(type, id));
        return stack;
    }

    private static void apply(ItemStack stack, String model, Integer rgb) {
        ItemMeta meta = stack.getItemMeta();
        meta.setItemModel(new NamespacedKey("mek", "recipe/" + model));
        if (rgb != null) {
            var data = meta.getCustomModelDataComponent();
            data.setColors(List.of(Color.fromRGB(rgb)));
            meta.setCustomModelDataComponent(data);
        }
        stack.setItemMeta(meta);
    }

    private static Set<String> loadModels(String manifest) {
        var source = RecipeIcons.class.getResourceAsStream("/" + manifest);
        if (source == null) throw new IllegalStateException("Missing generated " + manifest);
        try (var reader = new BufferedReader(new InputStreamReader(source, StandardCharsets.UTF_8))) {
            return reader.lines().filter(line -> !line.isBlank()).collect(Collectors.toUnmodifiableSet());
        } catch (java.io.IOException exception) {
            throw new IllegalStateException("Cannot read recipe icon manifest: " + manifest, exception);
        }
    }
}
