package net.kuzuanpa.cemekanism.gui;

import net.kuzuanpa.cemekanism.i18n.I18n;
import mekcore.item.ItemSlotKind;
import mekcore.item.ItemSlotSpec;
import mekcore.item.ItemSlotView;
import mekcore.machine.FactoryTier;
import mekcore.machine.Machine;
import mekcore.machine.MachineBlueprint;
import mekcore.machine.MachineSnapshot;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 面板几何与槽位映射的唯一运行时定义，数据来自 tools/gui_assets.py 生成的 gui-layout.yml。
 * 槽位只决定"画哪一格"，真正的容器索引仍由 ItemSlotView.name → BukkitItemHandler.indexOf 决定。
 */
public final class GuiLayout {

    public static final int LEVELS = 21;
    private static final String FILE = "/gui-layout.yml";

    public record Rect(int x, int y, int w, int h) {
        public static Rect of(int x, int y, int w, int h) {
            return new Rect(x, y, w, h);
        }
    }

    public record Cell(int col, int row) {
        public int index() {
            return row * 9 + col;
        }
    }

    /** 标签页、端口顺序与面板来自同一份生成表，不能用运行时 Map 的迭代顺序定位。 */
    public record MultiblockPage(String key, String label, String panelId, List<String> ports,
                                 List<Integer> columns) {
    }

    public record Profile(int rows, int width, int height, int gridX, int gridY, List<Integer> playerRows,
                          int hotbarY, int labelY, Rect energy, Rect energyFrame, Rect tank, Rect tankFrame,
                          Rect progress, Rect progressFrame, int arrowX, int arrowY, int arrowFrameY,
                          List<Cell> anchorEnergy, List<Cell> anchorTank,
                          int portX0, int portDx, int portY, List<Cell> portAnchors) {
    }

    public record TankGauge(String port, String role, Rect rect, Rect frame, String sprite, List<Cell> anchors) { }

    private static final Map<String, List<TankGauge>> TANK_GAUGES = new LinkedHashMap<>();

    public record MachineLayout(Profile profile, Map<String, Cell> slots, boolean factory, int parallel,
                                int inputsPerGroup, boolean hasTank, boolean hasEnergy,
                                String panelId, String energySprite, String tankSprite,
                                String progressSprite, boolean hasProgress,
                                List<Cell> energyAnchors, List<Cell> tankAnchors,
                                List<Cell> progressAnchors, List<TankGauge> tankGauges) {
    }

    /** 一个方块 id 的面板与槽位格：PNG 与运行时都按这份表落，避免两边各推一套规则。 */
    public record BlockPlan(String blockId, String panelId, String profile, int rows, boolean factory,
                            int parallel, int maxInputs, int maxOutputs, boolean progress,
                            boolean hasTank, boolean hasEnergy, int progressRow, int upgradeRow, int extraRow,
                            Map<String, Cell> slots) {
    }

    private static Profile single;
    private static Profile factory;
    private static Profile multiblock;
    private static Profile digitalMiner;
    private static int margin = 3;
    private static int ascent = 18;
    private static int shift = -11;
    private static int titleY = 6;
    private static int textX = 10;
    private static int textWidth = 158;
    private static final Map<String, BlockPlan> BLOCKS = new LinkedHashMap<>();
    private static final Map<String, List<MultiblockPage>> MULTIBLOCKS = new LinkedHashMap<>();
    private static int portHeight;

    private GuiLayout() {
    }

    public static synchronized void load() {
        if (single != null) {
            return;
        }
        try (InputStreamReader reader = new InputStreamReader(
                GuiLayout.class.getResourceAsStream(FILE), StandardCharsets.UTF_8)) {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(reader);
            margin = yaml.getInt("panel.margin", 3);
            ascent = yaml.getInt("panel.ascent", 18);
            shift = yaml.getInt("panel.shift", -11);
            titleY = yaml.getInt("panel.title_y", 6);
            textX = yaml.getInt("panel.text_x", 10);
            textWidth = yaml.getInt("panel.text_width", 158);
            single = profile(yaml.getConfigurationSection("profiles.single"));
            factory = profile(yaml.getConfigurationSection("profiles.factory"));
            multiblock = profile(yaml.getConfigurationSection("profiles.multiblock"));
            digitalMiner = profile(yaml.getConfigurationSection("profiles.digital_miner"));
            portHeight = yaml.getInt("profiles.multiblock.port_h", 38);
            ConfigurationSection multiblocks = yaml.getConfigurationSection("multiblocks");
            if (multiblocks != null) {
                for (String id : multiblocks.getKeys(false)) {
                    ConfigurationSection pages = multiblocks.getConfigurationSection(id);
                    List<MultiblockPage> plan = new ArrayList<>();
                    for (String key : pages.getKeys(false)) {
                        ConfigurationSection page = pages.getConfigurationSection(key);
                        plan.add(new MultiblockPage(key, page.getString("label", key),
                                page.getString("panel"), List.copyOf(page.getStringList("ports")),
                                List.copyOf(page.getIntegerList("columns"))));
                    }
                    MULTIBLOCKS.put(id, List.copyOf(plan));
                }
            }
            BLOCKS.clear();
            ConfigurationSection blocks = yaml.getConfigurationSection("blocks");
            if (blocks != null) {
                for (String blockId : blocks.getKeys(false)) {
                    ConfigurationSection section = blocks.getConfigurationSection(blockId);
                    if (section == null) {
                        continue;
                    }
                    Map<String, Cell> cells = new LinkedHashMap<>();
                    ConfigurationSection slots = section.getConfigurationSection("slots");
                    if (slots != null) {
                        for (String name : slots.getKeys(false)) {
                            List<Integer> pair = slots.getIntegerList(name);
                            if (pair.size() == 2) {
                                cells.put(name, new Cell(pair.get(0), pair.get(1)));
                            }
                        }
                    }
                    List<TankGauge> gauges = new ArrayList<>();
                    ConfigurationSection tankSection = section.getConfigurationSection("tank_gauges");
                    if (tankSection != null) for (String port : tankSection.getKeys(false)) {
                        ConfigurationSection gauge = tankSection.getConfigurationSection(port);
                        gauges.add(new TankGauge(port, gauge.getString("role", "both"), rect(gauge, "rect"),
                                rect(gauge, "frame"), gauge.getString("sprite"), cells(gauge, "anchors")));
                    }
                    TANK_GAUGES.put(blockId, List.copyOf(gauges));
                    BLOCKS.put(blockId, new BlockPlan(blockId,
                            section.getString("panel", machinePanelId(blockId)),
                            section.getString("profile", section.getBoolean("factory", false)
                                    ? "factory4" : "single"),
                            section.getInt("rows", 3),
                            section.getBoolean("factory", false),
                            section.getInt("parallel", 1),
                            section.getInt("max_inputs", 1),
                            section.getInt("max_outputs", 1),
                            section.getBoolean("progress", true),
                            section.getBoolean("has_tank", false),
                            section.getBoolean("has_energy", true),
                            section.getInt("progress_row", 1),
                            section.getInt("upgrade_row", 3),
                            section.getInt("extra_row", -1),
                            cells));
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException(I18n.tr("gui.layout.load_failed"), e);
        }
    }

    private static Profile profile(ConfigurationSection section) {
        if (section == null) {
            throw new IllegalStateException(I18n.tr("gui.layout.profiles_missing"));
        }
        return new Profile(
                section.getInt("rows", 3),
                section.getInt("width", 176), section.getInt("height", 168),
                section.getInt("grid_x", 8), section.getInt("grid_y", 18),
                section.getIntegerList("player_rows"), section.getInt("hotbar_y", 143),
                section.getInt("label_y", 74),
                rect(section, "energy"), rect(section, "energy_frame"),
                rect(section, "tank"), rect(section, "tank_frame"),
                rect(section, "progress"), rect(section, "progress_frame"),
                section.getInt("arrow_x", 12), section.getInt("arrow_y", 36),
                section.getInt("arrow_frame_y", 35),
                cells(section, "anchor_energy"), cells(section, "anchor_tank"),
                section.getInt("port_x0", 24), section.getInt("port_dx", 28),
                section.getInt("port_y", 36), cells(section, "anchor_slots"));
    }

    private static Rect rect(ConfigurationSection section, String key) {
        List<Integer> values = section.getIntegerList(key);
        if (values.size() < 4) {
            return Rect.of(0, 0, 0, 0);
        }
        return Rect.of(values.get(0), values.get(1), values.get(2), values.get(3));
    }

    private static List<Cell> cells(ConfigurationSection section, String key) {
        List<Cell> result = new ArrayList<>();
        for (List<?> pair : section.getList(key, List.of()).stream()
                .map(value -> value instanceof List<?> list ? list : null).toList()) {
            if (pair != null && pair.size() >= 2 && pair.get(0) instanceof Number col
                    && pair.get(1) instanceof Number row) {
                result.add(new Cell(col.intValue(), row.intValue()));
            }
        }
        return result;
    }

    public static Profile single() {
        load();
        return single;
    }

    /**
     * 工厂几何不按 profile 名共用：同样 5 行的「2 进 1 出」和「1 进 2 出」
     * 进度行/输出行不同。这里用模板 + 每块的行号算唯一几何。
     */
    public static Profile factory(BlockPlan plan) {
        load();
        int rows = plan.rows();
        int width = factory.width();
        int height = 114 + rows * 18;
        int gridY = factory.gridY();
        int firstPlayer = gridY + rows * 18 + 13;
        List<Integer> playerRows = List.of(firstPlayer, firstPlayer + 18, firstPlayer + 36);
        int upgradeY = gridY + plan.upgradeRow() * 18;
        int extraY = plan.extraRow() >= 0 ? gridY + plan.extraRow() * 18 : -1;
        // 工厂整条进度轨（空轨 + 填充）按实机标定左 1px；纵向放回格内。单机不动。
        int progressY = gridY + plan.progressRow() * 18;
        Rect energy = offset(factory.energy(), upgradeY);
        Rect energyFrame = offset(factory.energyFrame(), upgradeY);
        Rect tank = extraY >= 0 ? offset(factory.tank(), extraY) : Rect.of(0, 0, 0, 0);
        Rect tankFrame = extraY >= 0 ? offset(factory.tankFrame(), extraY) : Rect.of(0, 0, 0, 0);
        Rect progress = Rect.of(factory.gridX() + factory.progress().x(), progressY,
                factory.progress().w(), factory.progress().h());
        Rect progressFrame = Rect.of(factory.gridX() + factory.progressFrame().x(), progressY,
                factory.progressFrame().w(), factory.progressFrame().h());
        List<Cell> energyAnchors = coveredCells(energyFrame, factory.gridX(), gridY, rows);
        List<Cell> tankAnchors = plan.hasTank() && plan.extraRow() >= 0
                ? coveredCells(tankFrame, factory.gridX(), gridY, rows) : List.of();
        return new Profile(rows, width, height, factory.gridX(), gridY, playerRows,
                firstPlayer + 58, firstPlayer - 11, energy, energyFrame, tank, tankFrame,
                progress, progressFrame, factory.arrowX(), progressY + 1, progressY,
                energyAnchors, tankAnchors, 24, 28, 36, List.of());
    }

    private static Rect offset(Rect rect, int y) {
        return Rect.of(rect.x(), rect.y() + y, rect.w(), rect.h());
    }

    private static List<Cell> coveredCells(Rect rect, int gridX, int gridY, int rows) {
        List<Cell> cells = new ArrayList<>();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < 9; col++) {
                if (insideCell(col, row, rect, gridX, gridY)) {
                    cells.add(new Cell(col, row));
                }
            }
        }
        return cells;
    }

    public static Profile multiblock() {
        load();
        return multiblock;
    }

    public static List<MultiblockPage> multiblockPages(String defId) {
        load();
        List<MultiblockPage> pages = MULTIBLOCKS.get(defId);
        if (pages == null || pages.isEmpty()) {
            throw new IllegalStateException(I18n.tr("gui.layout.multiblock_plan_missing", defId));
        }
        return pages;
    }

    public static Rect multiblockGauge(MultiblockPage page, int index) {
        Profile profile = multiblock();
        return Rect.of(profile.portX0() + page.columns().get(index) * 18, profile.portY(), 10, portHeight);
    }

    public static List<Cell> multiblockGaugeCells(MultiblockPage page, int index) {
        Profile profile = multiblock();
        return coveredCells(multiblockGauge(page, index), profile.gridX(), profile.gridY(), profile.rows());
    }

    public static int margin() {
        load();
        return margin;
    }

    public static int ascent() {
        load();
        return ascent;
    }

    public static int shift() {
        load();
        return shift;
    }

    public static int titleY() {
        load();
        return titleY;
    }

    public static int textX() {
        load();
        return textX;
    }

    public static int textWidth() {
        load();
        return textWidth;
    }

    public static int containerSize(String blockId, FactoryTier tier, int computed) {
        load();
        BlockPlan plan = BLOCKS.get(blockId);
        int rows = plan != null ? plan.rows() : tier == FactoryTier.SINGLE ? 3 : 4;
        return Math.max(rows * 9, computed);
    }

    /** 进度轨/能量条/罐条覆盖的格子不能放“面板底色”占位模型，否则会盖住动态条。 */
    public static boolean overlayCell(MachineLayout layout, int index) {
        int col = index % 9;
        int row = index / 9;
        Profile profile = layout.profile();
        if ((layout.hasEnergy() && insideCell(col, row, profile.energyFrame()))
                || layout.tankGauges().stream().anyMatch(gauge -> insideCell(col, row, gauge.frame()))) {
            return true;
        }
        if (!layout.hasProgress()) {
            return false;
        }
        if (!layout.factory()) {
            return insideCell(col, row, profile.progressFrame());
        }
        int progressRow = (profile.progressFrame().y() + 1 - profile.gridY()) / 18;
        return row == progressRow && col < layout.parallel();
    }

    private static boolean insideCell(int col, int row, Rect rect) {
        return insideCell(col, row, rect, 8, 18);
    }

    private static boolean insideCell(int col, int row, Rect rect, int gridX, int gridY) {
        if (rect.w() <= 0 || rect.h() <= 0) {
            return false;
        }
        int cellX = gridX + col * 18;
        int cellY = gridY + row * 18;
        int overlapW = Math.min(cellX + 18, rect.x() + rect.w()) - Math.max(cellX, rect.x());
        int overlapH = Math.min(cellY + 18, rect.y() + rect.h()) - Math.max(cellY, rect.y());
        // 只碰到 1px 边的邻格不算；4~6px 高的横条至少要压住 2px。
        return overlapW >= 2 && overlapH >= 2;
    }

    public static String sanitize(String id) {
        int colon = id.indexOf(':');
        String path = colon >= 0 ? id.substring(colon + 1) : id;
        return path.replace('/', '_');
    }

    public static String machinePanelId(String blockId) {
        String override = GuiConfig.panelOverride(blockId);
        return override != null ? override : "mek:gui/panel_" + sanitize(blockId);
    }

    public static String multiblockPanelId(String defId) {
        return "mek:gui/panel_mb_" + sanitize(defId);
    }

    public static BlockPlan plan(String blockId) {
        load();
        return BLOCKS.get(blockId);
    }

    /**
     * 核心槽位顺序 → 容器格号。顺序必须与 BukkitItemHandler 展开 ItemSlotSpec 的顺序一致。
     * 没有生成过 GUI 表的方块返回 null，调用方退回旧的连续编号。
     */
    public static List<Integer> containerIndices(MachineBlueprint blueprint, FactoryTier tier, String blockId) {
        BlockPlan plan = plan(blockId);
        if (plan == null) {
            return null;
        }
        List<Integer> indices = new ArrayList<>();
        for (ItemSlotSpec spec : blueprint.itemSlots()) {
            if (spec.perParallel()) {
                for (int group = 0; group < Math.max(1, tier.parallel()); group++) {
                    Cell cell = plan.slots().get(spec.slotName(group));
                    if (cell == null) {
                        throw new IllegalStateException(I18n.tr("gui.layout.slot_missing_named",
                                blockId, spec.slotName(group)));
                    }
                    indices.add(cell.index());
                }
            } else {
                Cell cell = plan.slots().get(spec.name());
                if (cell == null) {
                    throw new IllegalStateException(I18n.tr("gui.layout.slot_missing",
                            blockId, spec.name()));
                }
                indices.add(cell.index());
            }
        }
        if (indices.size() != indices.stream().distinct().count()) {
            throw new IllegalStateException(I18n.tr("gui.layout.slot_overlap", blockId, indices));
        }
        return indices;
    }

    public static MachineLayout forMachine(Machine machine, FactoryTier tier, String blockId) {
        MachineSnapshot snapshot = machine.snapshot();
        BlockPlan plan = plan(blockId);
        boolean isFactory = plan != null ? plan.factory() : tier != FactoryTier.SINGLE;
        FactoryCells factoryCells = plan == null && isFactory ? factoryCells(snapshot, tier.parallel()) : null;
        Map<String, Cell> cells = plan != null ? new LinkedHashMap<>(plan.slots())
                : isFactory ? factoryCells.cells() : singleCells(snapshot);
        applyOverrides(cells, blockId);
        boolean hasTank = plan != null ? plan.hasTank() : snapshot.tanks().stream()
                .anyMatch(tank -> tank.capacity() > 0 && !tank.type().continuous());
        boolean hasEnergy = plan != null ? plan.hasEnergy() : snapshot.energyCapacityJ() > 0;
        boolean hasProgress = plan != null ? plan.progress() : isFactory;
        if (plan == null && isFactory) {
            int maxInputs = factoryCells == null ? 1 : factoryCells.maxInputs();
            int upgradeRow = maxInputs + 2;
            int extraRow = hasTank ? upgradeRow + 1 : -1;
            plan = new BlockPlan(blockId, machinePanelId(blockId), "factory4",
                    4 + (maxInputs - 1) + (hasTank ? 1 : 0),
                    true, tier.parallel(), maxInputs, 1, true, hasTank,
                    hasEnergy, maxInputs, upgradeRow, extraRow, factoryCells.cells());
        }
        Profile profile;
        String energySprite;
        String tankSprite;
        String progressSprite;
        if (plan != null && plan.factory()) {
            profile = factory(plan);
            energySprite = "mek:gui/bar_h_r" + plan.upgradeRow();
            tankSprite = plan.extraRow() >= 0
                    ? "mek:gui/bar_h_tank_r" + plan.extraRow() : "mek:gui/bar_v";
            progressSprite = "mek:gui/progress_v_r" + plan.progressRow();
        } else if (plan != null && plan.profile().equals("digital_miner")) {
            profile = digitalMiner;
            energySprite = "mek:gui/bar_h_r4";
            tankSprite = "mek:gui/bar_v";
            progressSprite = "mek:gui/miner_scan";
        } else {
            profile = single;
            energySprite = "mek:gui/bar_v";
            tankSprite = "mek:gui/bar_v";
            progressSprite = "mek:gui/progress_h";
        }
        List<Cell> progressAnchors = new ArrayList<>();
        if (hasProgress) {
            if (plan != null && plan.factory()) {
                for (int group = 0; group < Math.max(1, plan.parallel()); group++) {
                    progressAnchors.add(new Cell(group, plan.progressRow()));
                }
            } else {
                progressAnchors.addAll(coveredCells(profile.progressFrame(),
                        profile.gridX(), profile.gridY(), profile.rows()));
            }
        }
        List<Cell> energyAnchors = hasEnergy && snapshot.energyCapacityJ() > 0
                ? profile.anchorEnergy() : List.of();
        return new MachineLayout(profile, cells, isFactory, plan != null ? plan.parallel() : tier.parallel(),
                plan != null ? plan.maxInputs() : factoryCells == null ? 1 : factoryCells.maxInputs(), hasTank,
                hasEnergy,
                plan != null ? plan.panelId() : machinePanelId(blockId),
                energySprite, tankSprite, progressSprite, hasProgress,
                energyAnchors, TANK_GAUGES.getOrDefault(blockId, List.of()).stream()
                        .flatMap(gauge -> gauge.anchors().stream()).toList(), progressAnchors,
                TANK_GAUGES.getOrDefault(blockId, List.of()));
    }

    private static Map<String, Cell> singleCells(MachineSnapshot snapshot) {
        Map<String, Cell> cells = new LinkedHashMap<>();
        int inputIndex = 0;
        int outputIndex = 0;
        List<ItemSlotView> extras = new ArrayList<>();
        for (ItemSlotView slot : snapshot.itemSlots()) {
            if (slot.kind() == ItemSlotKind.INPUT || "infuse_in".equals(slot.name())) {
                int col = 2 + inputIndex;
                cells.put(slot.name(), new Cell(col > 6 ? col - 5 : col, col > 6 ? 1 : 0));
                inputIndex++;
            } else if (slot.kind() == ItemSlotKind.OUTPUT) {
                cells.put(slot.name(), new Cell(5 + outputIndex, 2));
                outputIndex++;
            } else {
                extras.add(slot);
            }
        }
        int extraCol = 2;
        for (ItemSlotView slot : extras) {
            while (cells.containsValue(new Cell(extraCol, 1)) && extraCol < 7) {
                extraCol++;
            }
            cells.put(slot.name(), new Cell(Math.min(extraCol, 8), 1));
            extraCol++;
        }
        return cells;
    }

    private record FactoryCells(Map<String, Cell> cells, int maxInputs) {
    }

    private static FactoryCells factoryCells(MachineSnapshot snapshot, int parallel) {
        Map<String, Cell> cells = new LinkedHashMap<>();
        int extraCol = 2;
        Map<Integer, Integer> inputCounts = new LinkedHashMap<>();
        for (ItemSlotView slot : snapshot.itemSlots()) {
            if (slot.group() >= 0) {
                if (slot.kind() == ItemSlotKind.OUTPUT) {
                    cells.put(slot.name(), new Cell(Math.min(slot.group(), 8), 2));
                } else {
                    int inputIndex = inputCounts.merge(slot.group(), 1, Integer::sum) - 1;
                    cells.put(slot.name(), new Cell(Math.min(slot.group(), 8), Math.min(inputIndex, 1)));
                }
            } else if ("infuse_in".equals(slot.name())) {
                cells.put(slot.name(), new Cell(0, 3));
            } else {
                cells.put(slot.name(), new Cell(Math.min(extraCol, 8), 3));
                extraCol++;
            }
        }
        int maxInputs = inputCounts.values().stream().mapToInt(Integer::intValue).max().orElse(1);
        return new FactoryCells(cells, maxInputs);
    }

    /**
     * v2 存档只存了容器格号。容器从 4 行切 6 行后，升级槽等格号会变；
     * 用旧版 27/36 格规则把旧格号反查成槽位名，再落到当前映射。
     */
    public static String legacySlotName(MachineSnapshot snapshot, FactoryTier tier, int containerIndex) {
        Map<String, Cell> cells = tier == FactoryTier.SINGLE
                ? singleCells(snapshot)
                : factoryCells(snapshot, tier.parallel()).cells();
        for (Map.Entry<String, Cell> entry : cells.entrySet()) {
            if (entry.getValue().index() == containerIndex) {
                return entry.getKey();
            }
        }
        return null;
    }

    private static void applyOverrides(Map<String, Cell> cells, String blockId) {
        for (Map.Entry<String, Cell> entry : new ArrayList<>(cells.entrySet())) {
            String override = GuiConfig.slotOverride(blockId, entry.getKey());
            if (override == null) {
                continue;
            }
            String[] parts = override.split(",");
            if (parts.length == 2) {
                try {
                    cells.put(entry.getKey(), new Cell(Integer.parseInt(parts[0].trim()),
                            Integer.parseInt(parts[1].trim())));
                } catch (NumberFormatException ignored) {
                    // 配置写错就保留默认格，不阻塞开界面。
                }
            }
        }
    }
}
