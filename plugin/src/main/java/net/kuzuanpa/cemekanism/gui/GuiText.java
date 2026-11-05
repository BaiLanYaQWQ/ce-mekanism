package net.kuzuanpa.cemekanism.gui;

import mekcore.chemical.Chemical;
import mekcore.chemical.Chemicals;
import mekcore.machine.FactoryTier;
import mekcore.machine.MachineSnapshot;
import mekcore.resource.ResourceTypes;
import net.kuzuanpa.cemekanism.i18n.I18n;
import net.kuzuanpa.cemekanism.platform.BukkitItems;
import net.kuzuanpa.cemekanism.platform.ResourceColors;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** 标题文字、tooltip 文本与颜色；只读 snapshot 字段，不解析 describe()。 */
public final class GuiText {

    public static final NamespacedKey ANCHOR_KEY = new NamespacedKey("cemekanism", "gui_anchor");
    private static final NamespacedKey TYPE_KEY = new NamespacedKey("cemekanism", "gui_overlay_type");

    private GuiText() {
    }

    /** 窗口标题只放机器名；状态、能量、存量都走条与锚点 tooltip。 */
    public static String title(MachineSnapshot snapshot) {
        return machineName(snapshot);
    }

    public static String machineName(MachineSnapshot snapshot) {
        return I18n.machineName(snapshot);
    }

    public static String multiTitle(String name, boolean formed, String extra) {
        List<String> segments = new ArrayList<>();
        segments.add(name);
        segments.add(I18n.tr(formed ? "multiblock.formed" : "multiblock.unformed"));
        if (extra != null && !extra.isEmpty()) {
            segments.add(extra);
        }
        return trimToWidth(segments, Math.min(GuiConfig.titleWidth(), GuiLayout.textWidth()));
    }

    public static MachineSnapshot.TankSnapshot firstTank(MachineSnapshot snapshot) {
        for (MachineSnapshot.TankSnapshot tank : snapshot.tanks()) {
            // snapshot 里能量也算一口"罐"；界面的罐条只画真正的流体/化学品/辅料。
            if (tank.capacity() > 0 && !tank.type().continuous()) {
                return tank;
            }
        }
        return null;
    }

    public static String tankName(MachineSnapshot.TankSnapshot tank) {
        if (tank.resourceId() == null) {
            return I18n.resourceType(tank.type());
        }
        return I18n.resourceName(tank.type(), tank.resourceId());
    }

    public static int tankColor(MachineSnapshot.TankSnapshot tank) {
        return resourceColor(tank.type(), tank.resourceId());
    }

    /** 资源条颜色：化学品取数据表颜色，常见流体用上游贴图色，其余用配置的兜底色。 */
    public static int resourceColor(mekcore.resource.ResourceType type, String id) {
        if (id == null) {
            return GuiConfig.fluidColor();
        }
        if (type.equals(ResourceTypes.CHEMICAL) || type.equals(ResourceTypes.INFUSION)) {
            Chemical chemical = Chemicals.registry().find(id).orElse(null);
            if (chemical != null && chemical.colorArgb() != Chemical.NO_COLOR) {
                return chemical.colorArgb() & 0xFFFFFF;
            }
        }
        if (type.equals(ResourceTypes.FLUID)) {
            Integer color = ResourceColors.fluid(id);
            if (color != null) {
                return color;
            }
            return switch (id) {
                case "minecraft:water" -> 0x3F76E4;
                case "minecraft:lava" -> 0xFF8A00;
                default -> GuiConfig.fluidColor();
            };
        }
        return GuiConfig.fluidColor();
    }

    public static ItemStack energyAnchor(MachineSnapshot snapshot) {
        if (snapshot.energyCapacityJ() <= 0) {
            return null;
        }
        List<String> lore = new ArrayList<>();
        lore.add(format(snapshot.energyJoules()) + " / " + format(snapshot.energyCapacityJ()) + " J");
        if (snapshot.energyGeneratedPerTickJ() != 0) {
            lore.add(I18n.tr("gui.energy.generating", format(snapshot.energyGeneratedPerTickJ())));
        } else if (snapshot.energyPerTickJ() > 0) {
            lore.add(I18n.tr("gui.energy.consuming", format(snapshot.energyPerTickJ())));
        }
        return anchor("mek:gui_energy_slot", I18n.tr("gui.energy.label"), lore);
    }

    public static ItemStack tankAnchor(MachineSnapshot snapshot) {
        MachineSnapshot.TankSnapshot tank = firstTank(snapshot);
        if (tank == null) {
            return null;
        }
        return tankAnchor(tank);
    }

    public static ItemStack tankAnchor(MachineSnapshot.TankSnapshot tank) {
        List<String> lore = new ArrayList<>();
        lore.add(format(tank.amount()) + " / " + format(tank.capacity()) + " " + tank.type().unit());
        if (tank.type().equals(ResourceTypes.FLUID) || tank.type().equals(ResourceTypes.CHEMICAL)) {
            lore.add(I18n.tr("gui.tank.container_hint"));
        }
        return anchor("mek:gui_tank_slot", tankName(tank), lore);
    }

    public static ItemStack progressAnchor(MachineSnapshot snapshot, int group) {
        List<String> lore = new ArrayList<>();
        String name = I18n.tr("gui.progress.label");
        if (group >= 0) {
            MachineSnapshot.ParallelSnapshot parallel = snapshot.parallels().stream()
                    .filter(value -> value.group() == group)
                    .findFirst().orElse(null);
            name = I18n.tr("gui.progress_group", group);
            if (parallel == null || !parallel.busy()) {
                lore.add(I18n.tr("state.idle"));
            } else {
                lore.add(I18n.tr("gui.remaining_ticks", format(Math.round(parallel.remainingTicks()))));
            }
        } else {
            snapshot.parallels().stream().filter(MachineSnapshot.ParallelSnapshot::busy).findFirst()
                    .ifPresent(parallel -> lore.add(I18n.tr("gui.remaining_ticks", format(Math.round(parallel.remainingTicks())))));
        }
        if (snapshot.state() != mekcore.machine.WorkState.RUNNING && snapshot.state() != mekcore.machine.WorkState.IDLE) {
            lore.add(I18n.enumName(snapshot.state(), snapshot.state().zhName()));
        }
        lore.add(I18n.tr("gui.progress.open_recipes"));
        return item("mek:gui_progress_slot", name, lore, "anchor");
    }

    public static ItemStack placeholder() {
        return emptyItem("mek:gui_empty_slot", "placeholder");
    }

    /** 被进度条/能量条覆盖的格子：同样拦点击，但保持空模型，别把动态条盖住。 */
    public static ItemStack invisiblePlaceholder() {
        return emptyItem("mek:gui_tooltip_anchor", "placeholder");
    }

    public static ItemStack anchor(String itemId, String name, List<String> lore) {
        if (!GuiConfig.anchorTooltips()) {
            return null;
        }
        return item(itemId, name, lore, "anchor");
    }

    /** Controlled buttons remain usable when numeric anchor tooltips are disabled. */
    public static ItemStack button(String itemId, String name, List<String> lore) {
        return item(itemId, name, lore, "button");
    }

    private static ItemStack item(String itemId, String name, List<String> lore, String type) {
        ItemStack stack = overlay(itemId, type);
        ItemMeta meta = stack.getItemMeta();
        applyTooltip(meta, name, lore);
        stack.setItemMeta(meta);
        return stack;
    }

    /** 空槽位不显示任何文字；仍保留锚点数据供点击拦截与刷新判断。 */
    private static ItemStack emptyItem(String itemId, String type) {
        ItemStack stack = overlay(itemId, type);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("§r§f");
        meta.setLore(List.of());
        meta.setHideTooltip(true);
        stack.setItemMeta(meta);
        return stack;
    }

    private static ItemStack overlay(String itemId, String type) {
        ItemStack stack = BukkitItems.create(itemId);
        if (stack == null) {
            stack = new ItemStack(Material.PAPER);
        }
        ItemMeta meta = stack.getItemMeta();
        meta.getPersistentDataContainer().set(ANCHOR_KEY, PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(TYPE_KEY, PersistentDataType.STRING, type);
        stack.setItemMeta(meta);
        return stack;
    }

    /** §r 先清掉物品 tooltip 继承的斜体，再上正文颜色。 */
    public static String name(String text) {
        return "§r§f" + text;
    }

    public static String name(String text, String color) {
        return "§r" + color + text;
    }

    public static String name(String text, int rgb) {
        return "§r" + legacyRgb(rgb) + text;
    }

    public static net.kyori.adventure.text.Component itemName(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        var custom = meta.displayName();
        if (custom != null) return custom;
        var configured = meta.itemName();
        return configured != null ? configured
                : net.kyori.adventure.text.Component.translatable(stack.getType().translationKey());
    }

    public static String lore(String text) {
        return "§r§7" + text;
    }

    public static String darkLore(String text) {
        return "§r§8" + text;
    }

    public static void applyTooltip(ItemMeta meta, String title, List<String> lore) {
        meta.setDisplayName(name(title));
        meta.setLore(lore.stream().map(GuiText::lore).toList());
    }

    public static void writeTooltip(ItemMeta meta, String formattedName, List<String> formattedLore) {
        meta.setDisplayName(formattedName);
        meta.setLore(formattedLore);
    }

    public static void appendLore(ItemStack stack, String line) {
        ItemMeta meta = stack.getItemMeta();
        List<String> lore = meta.getLore();
        lore = lore == null ? new ArrayList<>() : new ArrayList<>(lore);
        lore.add(lore(line));
        meta.setLore(lore);
        stack.setItemMeta(meta);
    }

    public static void hideTooltip(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        meta.setHideTooltip(true);
        stack.setItemMeta(meta);
    }

    private static String legacyRgb(int rgb) {
        String hex = String.format(Locale.ROOT, "%06x", rgb & 0xFFFFFF);
        StringBuilder color = new StringBuilder("§x");
        for (int i = 0; i < hex.length(); i++) {
            color.append('§').append(hex.charAt(i));
        }
        return color.toString();
    }

    public static boolean isOverlay(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        ItemMeta meta = stack.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(ANCHOR_KEY, PersistentDataType.BYTE);
    }

    public static boolean isAnchor(ItemStack stack) {
        return "anchor".equals(typeOf(stack));
    }

    public static boolean isPlaceholder(ItemStack stack) {
        return "placeholder".equals(typeOf(stack));
    }

    private static String typeOf(ItemStack stack) {
        if (!isOverlay(stack)) {
            return null;
        }
        return stack.getItemMeta().getPersistentDataContainer().get(TYPE_KEY, PersistentDataType.STRING);
    }

    public static String format(long value) {
        return String.format(Locale.ROOT, "%,d", value);
    }

    /** 标题文字的像素宽（CJK 按 9px、ASCII 按 6px），用于自检文字是否超出面板。 */
    public static int textWidth(String text) {
        return TitleBuilder.width(text);
    }

    public static String compact(long value) {
        if (value < 10_000) {
            return Long.toString(value);
        }
        if (value < 1_000_000) {
            return String.format(Locale.ROOT, "%.1fk", value / 1000.0);
        }
        return String.format(Locale.ROOT, "%.1fM", value / 1_000_000.0);
    }

    private static String join(List<String> segments) {
        return String.join(" ", segments);
    }

    private static String trimToWidth(List<String> segments, int width) {
        StringBuilder sb = new StringBuilder();
        for (String segment : segments) {
            String next = sb.isEmpty() ? segment : sb + " " + segment;
            if (TitleBuilder.width(next) > width && !sb.isEmpty()) {
                break;
            }
            sb.setLength(0);
            sb.append(next);
        }
        return sb.toString();
    }
}
