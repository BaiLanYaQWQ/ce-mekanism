package net.kuzuanpa.cemekanism.gui;

import mekcore.resource.ResourceHandler;
import mekcore.resource.ResourceNames;
import mekcore.resource.ResourceTypes;
import net.kuzuanpa.cemekanism.multiblock.MultiblockBlocks;
import net.kuzuanpa.cemekanism.multiblock.MultiblockConfig;
import net.kuzuanpa.cemekanism.multiblock.MultiblockHosts;
import net.kuzuanpa.cemekanism.multiblock.TeleporterFrequencyGui;
import net.kuzuanpa.cemekanism.multiblock.logic.FissionReactorLogic;
import net.kuzuanpa.cemekanism.multiblock.logic.FusionReactorLogic;
import net.kuzuanpa.cemekanism.multiblock.logic.HohlraumItems;
import net.kuzuanpa.cemekanism.multiblock.logic.SpsLogic;
import net.kuzuanpa.cemekanism.multiblock.logic.TeleporterLogic;
import net.kuzuanpa.cemekanism.platform.BukkitItems;
import net.kuzuanpa.cemekanism.platform.ResourceContainerItems;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/** 多方块分页：面板、端口顺序、柱形和全部悬停格都来自生成的 GUI 计划。 */
public final class MultiblockGui {
    public static final int RECIPES_SLOT = 44;

    private MultiblockGui() {
    }

    public static Component title(MultiblockHosts.Host host) {
        return title(host, 0);
    }

    public static Component title(MultiblockHosts.Host host, int selected) {
        GuiLayout.MultiblockPage page = page(host, selected);
        TitleBuilder builder = new TitleBuilder();
        builder.panel(page.panelId(), GuiLayout.multiblock().width());
        // TitleBuilder 只往左挪；从最右一根柱开始画。
        for (int i = page.ports().size() - 1; i >= 0; i--) {
            ResourceHandler port = host.instance().ports().get(page.ports().get(i));
            if (port != null) {
                GuiLayout.Rect gauge = GuiLayout.multiblockGauge(page, i);
                builder.overlay("mek:gui/bar_v_mb", level(port.amount(), port.capacity()),
                        gauge.x(), gauge.w(), color(port));
            }
        }
        builder.text(host.def().zh(), TextColor.color(GuiConfig.titleColor()));
        return builder.build();
    }

    public static GuiLayout.MultiblockPage page(MultiblockHosts.Host host, int selected) {
        List<GuiLayout.MultiblockPage> pages = GuiLayout.multiblockPages(host.def().id());
        return pages.get(Math.max(0, Math.min(selected, pages.size() - 1)));
    }

    public static void updateAnchors(MultiblockHosts.Host host, Inventory inventory) {
        updateAnchors(host, inventory, 0);
    }

    public static void updateAnchors(MultiblockHosts.Host host, Inventory inventory, int selected) {
        // 空模型不能被面板底色物品替代，否则会盖住条和标签框。
        ItemStack[] contents = new ItemStack[inventory.getSize()];
        ItemStack empty = GuiText.invisiblePlaceholder();
        for (int i = 0; i < contents.length; i++) {
            contents[i] = empty.clone();
        }
        List<GuiLayout.MultiblockPage> pages = GuiLayout.multiblockPages(host.def().id());
        GuiLayout.MultiblockPage page = page(host, selected);
        for (int i = 0; i < pages.size(); i++) {
            GuiLayout.MultiblockPage tab = pages.get(i);
            List<String> hint = List.of();
            contents[i * 2] = button("mek:gui_mb_" + tab.key(), tab.label(), hint);
            contents[i * 2 + 1] = button("mek:gui_tooltip_anchor", tab.label(), hint);
        }
        for (int i = 0; i < page.ports().size(); i++) {
            String name = page.ports().get(i);
            ResourceHandler port = host.instance().ports().get(name);
            if (port == null) {
                continue;
            }
            List<String> lore = new ArrayList<>(GuiConfig.anchorTooltips() ? List.of(
                    GuiText.format(port.amount()) + " / " + GuiText.format(port.capacity())
                            + " " + port.type().unit()) : List.of());
            if (port.type().equals(ResourceTypes.CHEMICAL)) {
                lore.add("排出状态：" + host.instance().tank(name).dumpMode().label());
                lore.add("空光标点击罐条：不排出 / 排出过量 / 排出");
            }
            if (port.type().equals(ResourceTypes.FLUID) || port.type().equals(ResourceTypes.CHEMICAL)) {
                lore.add("持有一个容器：左键提取，右键放入");
            }
            // row1 是资源图标；row2/3 的空模型锚点覆盖整根柱形。
            String resourceName = port.resourceId() == null ? label(name) : ResourceNames.nameOf(port.type(), port.resourceId());
            contents[9 + page.columns().get(i)] = button(portIcon(name), resourceName, lore);
            if (!GuiConfig.anchorTooltips()) {
                continue;
            }
            ItemStack stack = GuiText.anchor(port.type().equals(ResourceTypes.ENERGY)
                    ? "mek:gui_energy_slot" : "mek:gui_tank_slot", resourceName, lore);
            for (GuiLayout.Cell cell : GuiLayout.multiblockGaugeCells(page, i)) {
                contents[cell.index()] = stack.clone();
            }
        }
        contents[36] = button("mek:gui_mb_structure", "结构状态", structureInfo(host));
        contents[38] = button("mek:gui_mb_operation", "运行状态", operationInfo(host, page.key()));
        ItemStack recipes = new ItemStack(Material.KNOWLEDGE_BOOK);
        var recipesMeta = recipes.getItemMeta();
        GuiText.applyTooltip(recipesMeta, "查看配方", List.of());
        recipes.setItemMeta(recipesMeta);
        contents[RECIPES_SLOT] = recipes;
        if ("stats".equals(page.key())) {
            contents[36] = empty.clone();
            contents[38] = empty.clone();
            contents[20] = button("mek:gui_mb_structure", "结构参数", structureInfo(host));
            contents[24] = button("mek:gui_mb_stats", "运行数据", operationInfo(host, page.key()));
        }
        controls(host, page.key(), contents);
        for (int i = 0; i < contents.length; i++) {
            if (!Objects.equals(inventory.getItem(i), contents[i])) {
                inventory.setItem(i, contents[i]);
            }
        }
    }

    private static void controls(MultiblockHosts.Host host, String page, ItemStack[] contents) {
        var instance = host.instance();
        if (instance.blueprint().logic() instanceof TeleporterLogic teleporter) {
            String frequency = teleporter.state(instance).frequency;
            contents[40] = button("mek:gui_mb_operation", "传送频率：" + (frequency.isEmpty() ? "未绑定" : frequency),
                    List.of("左键选择频率，右键解除绑定"));
        } else if (instance.blueprint().logic() instanceof FusionReactorLogic fusion) {
            var state = fusion.state(instance);
            if ("fuel".equals(page) || "stats".equals(page)) {
                contents[42] = button("mek:gui_mb_rate", "注入率：" + state.injectionRate + " mB/t",
                        List.of("左键 +2，右键 -2", "Shift 点击：步长 10", "上限：" + MultiblockConfig.fusionMaxInjection()));
            }
            if ("main".equals(page) || "fuel".equals(page)) {
                contents[40] = state.hohlraum == null
                        ? button("mek:gui_mb_hohlraum", "黑体辐射腔（未装入）", List.of("光标持有辐射腔时左键装入", "空光标左键取出"))
                        : namedHohlraum(state.hohlraum);
            }
        } else if (instance.blueprint().logic() instanceof FissionReactorLogic fission) {
            var state = fission.state(instance);
            if ("main".equals(page)) {
                contents[40] = button(state.active ? "mek:gui_mb_on" : "mek:gui_mb_off",
                        state.active ? "反应堆已启用" : "反应堆已停用",
                        state.forceDisabled ? List.of("过热保护锁定，无法启动") : List.of());
            } else if ("stats".equals(page)) {
                contents[42] = button("mek:gui_mb_rate", "燃烧率上限：" + decimal(state.rateLimit) + " mB/t",
                        List.of("左键 +0.1，右键 -0.1", "Shift 点击：步长 1"));
            }
        }
    }

    /** 所有原版物品搬运已取消；只对普通左右键解释标签/按钮，数字键和双击都不生效。 */
    public static int click(MultiblockHosts.Host host, int selected, int raw, ClickType click, Player player) {
        boolean left = click == ClickType.LEFT || click == ClickType.SHIFT_LEFT;
        boolean right = click == ClickType.RIGHT || click == ClickType.SHIFT_RIGHT;
        boolean shift = click == ClickType.SHIFT_LEFT || click == ClickType.SHIFT_RIGHT;
        if (!left && !right) {
            return selected;
        }
        if (raw >= 0 && raw < 8 && !shift && left) {
            int tab = raw / 2;
            return tab < GuiLayout.multiblockPages(host.def().id()).size() ? tab : selected;
        }
        var instance = host.instance();
        var currentPage = page(host, selected);
        for (int i = 0; i < currentPage.ports().size(); i++) {
            String name = currentPage.ports().get(i);
            var tank = instance.tank(name);
            boolean gauge = raw == 9 + currentPage.columns().get(i)
                    || GuiLayout.multiblockGaugeCells(currentPage, i).stream().anyMatch(cell -> cell.index() == raw);
            if (gauge && ResourceContainerItems.isContainer(player.getItemOnCursor())) {
                if (!shift) {
                    ItemStack result = ResourceContainerItems.transfer(player.getItemOnCursor(), tank, right);
                    if (result != null) {
                        player.setItemOnCursor(result);
                        host.markChanged();
                    }
                }
                return selected;
            }
            if (tank.type().equals(ResourceTypes.CHEMICAL) && gauge
                    && (player.getItemOnCursor() == null || player.getItemOnCursor().getType().isAir())) {
                tank.setDumpMode(tank.dumpMode().next()); host.markChanged(); return selected;
            }
        }
        String page = currentPage.key();
        if (instance.blueprint().logic() instanceof TeleporterLogic teleporter) {
            if (raw == 40 && !shift) {
                if (right) teleporter.bind(host, "");
                else TeleporterFrequencyGui.open(host, player);
            }
        } else if (instance.blueprint().logic() instanceof FusionReactorLogic fusion) {
            if (raw == 42 && ("fuel".equals(page) || "stats".equals(page))) {
                int step = shift ? 10 : 2;
                fusion.setInjectionRate(instance, fusion.state(instance).injectionRate
                        + (left ? step : -step));
                host.markChanged();
            } else if (raw == 40 && ("main".equals(page) || "fuel".equals(page))
                    && !shift && left) {
                ItemStack cursor = player.getItemOnCursor();
                if (cursor == null || cursor.getType().isAir()) {
                    ItemStack removed = fusion.takeHohlraum(instance);
                    if (removed != null) {
                        player.setItemOnCursor(removed);
                        host.markChanged();
                    }
                } else if (HohlraumItems.isHohlraum(cursor) && fusion.insertHohlraum(instance, cursor)) {
                    ItemStack rest = cursor.clone();
                    rest.setAmount(rest.getAmount() - 1);
                    player.setItemOnCursor(rest.getAmount() == 0 ? null : rest);
                    host.markChanged();
                }
            }
        } else if (instance.blueprint().logic() instanceof FissionReactorLogic fission) {
            if (raw == 40 && "main".equals(page) && left && !shift) {
                fission.setActive(instance, !fission.state(instance).active);
                host.markChanged();
            } else if (raw == 42 && "stats".equals(page)) {
                double step = shift ? 1 : 0.1;
                double rate = fission.state(instance).rateLimit + (left ? step : -step);
                fission.setRate(instance, Math.round(rate * 100.0) / 100.0);
                host.markChanged();
            }
        }
        return selected;
    }

    private static ItemStack namedHohlraum(ItemStack stored) {
        ItemStack view = stored.clone();
        var meta = view.getItemMeta();
        GuiText.applyTooltip(meta, "黑体辐射腔", List.of(
                "载荷：" + GuiText.format(HohlraumItems.amount(stored)) + " mB",
                "空光标左键取出"));
        view.setItemMeta(meta);
        return view;
    }

    private static List<String> structureInfo(MultiblockHosts.Host host) {
        List<String> lines = new ArrayList<>();
        var instance = host.instance();
        lines.add("尺寸：" + instance.sizeX() + " × " + instance.sizeY() + " × " + instance.sizeZ());
        for (String role : host.def().roleBlocks().keySet()) {
            if (Set.of("frame", "glass", "valve", "heated_valve", "cooled_valve", "port",
                    "controller", "logic_adapter", "laser_focus").contains(role)) continue;
            int count = instance.shape().roleCount(MultiblockBlocks.role(role));
            if (count > 0) {
                lines.add(roleLabel(role) + "：" + count);
            }
        }
        if ("turbine".equals(host.def().family())) {
            lines.add("涡轮叶片：" + host.blades());
        }
        return lines;
    }

    private static List<String> operationInfo(MultiblockHosts.Host host, String page) {
        List<String> lines = new ArrayList<>();
        var instance = host.instance();
        if (instance.blueprint().logic() instanceof TeleporterLogic teleporter) {
            lines.add(teleporter.status(host));
        }
        if (instance.heatNode() != null) {
            lines.add("外壳温度：" + decimal(instance.heatNode().temperatureK()) + " K");
        }
        if (instance.blueprint().logic() instanceof FusionReactorLogic fusion) {
            var state = fusion.state(instance);
            lines.add(state.burning ? "等离子体正在燃烧" : "等离子体未点燃");
            lines.add("等离子体温度：" + decimal(state.plasmaTemperatureK) + " K");
            lines.add("点火温度：" + decimal(MultiblockConfig.fusionBurnTemperatureK()) + " K");
            if (!"fuel".equals(page) && !"stats".equals(page)) lines.add("注入率：" + state.injectionRate + " mB/t");
        } else if (instance.blueprint().logic() instanceof FissionReactorLogic fission) {
            var state = fission.state(instance);
            if (!"main".equals(page)) lines.add(state.active ? "反应堆已启用" : "反应堆已停用");
            if (!"stats".equals(page)) lines.add("燃烧率上限：" + decimal(state.rateLimit) + " mB/t");
            lines.add("结构损伤：" + decimal(state.damage) + "%");
            if (state.forceDisabled) lines.add("过热保护锁定");
        } else if (instance.blueprint().logic() instanceof SpsLogic sps) {
            var state = sps.state(instance);
            lines.add("已处理钋：" + GuiText.format(state.inputProcessed) + " mB");
            lines.add("反物质进度：" + decimal(state.progress) + " / " + MultiblockConfig.spsInputPerAntimatter());
            lines.add("本刻耗能：" + GuiText.format(state.lastEnergy) + " J/t");
        }
        return lines.isEmpty() ? List.of("结构已就绪") : lines;
    }

    private static ItemStack button(String id, String name, List<String> lore) {
        // 导航不受 anchor_tooltips 开关控制；关闭数值提示仍必须能切换页面。
        ItemStack stack = BukkitItems.create(id);
        if (stack == null) {
            stack = new ItemStack(Material.PAPER);
        }
        return describe(stack, name, lore);
    }

    private static String portIcon(String name) {
        return "mek:gui_mb_" + switch (name) {
            case "energy" -> "energy";
            case "laser_energy" -> "laser";
            case "water", "fluid", "output" -> "fluid";
            case "heated_coolant", "cooled_coolant" -> "heat";
            case "fuel" -> "fuel";
            case "waste" -> "waste";
            case "polonium" -> "polonium";
            case "antimatter" -> "antimatter";
            default -> "chemical";
        };
    }

    private static ItemStack describe(ItemStack stack, String name, List<String> lore) {
        var meta = stack.getItemMeta();
        GuiText.applyTooltip(meta, name, lore);
        stack.setItemMeta(meta);
        return stack;
    }

    private static String decimal(double value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }

    private static int color(ResourceHandler port) {
        return port.type().equals(ResourceTypes.ENERGY) || port.type().equals(ResourceTypes.HEAT)
                ? GuiConfig.energyColor() : GuiText.resourceColor(port.type(), port.resourceId());
    }

    private static int level(long amount, long capacity) {
        return capacity <= 0 ? 0 : (int) Math.round(Math.max(0.0,
                Math.min(1.0, (double) amount / capacity)) * (GuiLayout.LEVELS - 1));
    }

    private static String roleLabel(String role) {
        return switch (role) {
            case "frame" -> "外壳";
            case "glass" -> "结构玻璃";
            case "valve" -> "阀门";
            case "heated_valve" -> "热冷却液阀门";
            case "cooled_valve" -> "冷冷却液阀门";
            case "complex" -> "复杂旋转结构";
            case "port" -> "端口";
            case "controller" -> "控制器";
            case "element" -> "过热元件";
            case "disperser" -> "压力分散器";
            case "rotor" -> "涡轮转子";
            case "coil" -> "线圈";
            case "vent" -> "排气口";
            case "condenser" -> "饱和冷凝器";
            case "cell" -> "感应元件";
            case "provider" -> "感应供应器";
            case "fuel" -> "燃料组件";
            case "control_rod" -> "控制棒组件";
            case "logic_adapter" -> "逻辑适配器";
            case "laser_focus" -> "激光聚焦矩阵";
            default -> role;
        };
    }

    private static String label(String port) {
        return switch (port) {
            case "deuterium" -> "氘";
            case "tritium" -> "氚";
            case "water" -> "水";
            case "steam" -> "蒸汽";
            case "energy" -> "能量";
            case "heated_coolant" -> "热冷却液";
            case "cooled_coolant" -> "冷冷却液";
            case "fuel" -> "燃料";
            case "waste" -> "废料";
            case "laser_energy" -> "激光能量";
            case "polonium" -> "钋";
            case "antimatter" -> "反物质";
            case "fluid" -> "流体";
            case "chemical" -> "化学品";
            case "output" -> "产出";
            default -> port;
        };
    }
}
