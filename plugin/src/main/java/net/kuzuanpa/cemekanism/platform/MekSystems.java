package net.kuzuanpa.cemekanism.platform;

import mekcore.spec.V10Tables;
import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.kuzuanpa.cemekanism.gui.GuiConfig;
import net.kuzuanpa.cemekanism.storage.StorageRuntime;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 系统参数（文档 02 第五节）：插件目录下的 mek-systems.yml → DataTag → {@code V10Tables.load(...)}。
 * 首次启动从 jar 里释放一份默认值（= 核心内置 = 随库上游分级表），之后改文件、`/mek reload` 即可；
 * 配置里没写的项一律落回核心默认。
 */
public final class MekSystems {

    private static final String FILE_NAME = "mek-systems.yml";

    private static long energyTabletMaxJ = 1_000_000L;
    private static long energyTabletChargeRateJ = 5_000L;
    private static Set<String> energyItems = Set.of("mek:energy_tablet");
    private static long gaugeDropperCapacity = 16_000L;
    private static long gaugeDropperTransferRate = 250L;
    private static DemoSettings demo = DemoSettings.defaults();
    private static boolean transmitterCollision6px;

    /** `/mek demo` 的摆放清单：内容 id 与起始料都从配置来，坐标布局在命令里。 */
    public record DemoSettings(String machineA, String machineB, String itemPipe, String cable,
                               String fluidPipe, String multiblock, String starterItem, int starterAmount,
                               String tankFluid, long tankAmount, long chargeJ) {
        public static DemoSettings defaults() {
            return new DemoSettings("mek:enrichment_chamber", "mek:energized_smelter",
                    "mek:basic_logistical_transporter", "mek:basic_universal_cable",
                    "mek:basic_mechanical_pipe", "mek:dynamic_tank",
                    "minecraft:iron_ore", 4, "minecraft:water", 5000L, -1L);
        }
    }

    private MekSystems() {
    }

    public static void install(CeMekanismPlugin plugin) {
        File file = new File(plugin.getDataFolder(), FILE_NAME);
        if (!file.isFile()) {
            plugin.saveResource(FILE_NAME, false);
        }
        EnergyBalanceMigration.install(plugin, file);
        SectionDataTag tag = new SectionDataTag(YamlConfiguration.loadConfiguration(file));
        V10Tables.load(tag);
        StorageRuntime.reload();
        GuiConfig.install(tag);
        gaugeDropperCapacity = Math.max(1, tag.getLong("gear/gauge_dropper/capacity").orElse(16_000L));
        gaugeDropperTransferRate = Math.max(1, tag.getLong("gear/gauge_dropper/transfer_rate").orElse(250L));
        // gear 段：随身储电物品（充电台充的那些），默认值 = 上游 GearConfig.energy_tablet
        tag.getLong("gear/energy_tablet/max_energy").ifPresent(value -> energyTabletMaxJ = value);
        tag.getLong("gear/energy_tablet/charge_rate").ifPresent(value -> energyTabletChargeRateJ = value);
        List<String> items = new ArrayList<>();
        for (int index = 0; index < 64; index++) {
            Optional<String> item = tag.getString("gear/energy_items/" + index);
            if (item.isEmpty()) {
                break;
            }
            items.add(item.get());
        }
        if (!items.isEmpty()) {
            energyItems = Set.copyOf(items);
        }
        transmitterCollision6px = tag.getString("logistics/transmitter_collision")
                .map(value -> "6px".equalsIgnoreCase(value))
                .orElse(false);
        DemoSettings defaults = DemoSettings.defaults();
        demo = new DemoSettings(
                tag.getString("demo/machine_a").orElse(defaults.machineA()),
                tag.getString("demo/machine_b").orElse(defaults.machineB()),
                tag.getString("demo/item_pipe").orElse(defaults.itemPipe()),
                tag.getString("demo/cable").orElse(defaults.cable()),
                tag.getString("demo/fluid_pipe").orElse(defaults.fluidPipe()),
                tag.getString("demo/multiblock").orElse(defaults.multiblock()),
                tag.getString("demo/starter_item").orElse(defaults.starterItem()),
                tag.getInt("demo/starter_amount").orElse(defaults.starterAmount()),
                tag.getString("demo/tank_fluid").orElse(defaults.tankFluid()),
                tag.getLong("demo/tank_amount").orElse(defaults.tankAmount()),
                tag.getLong("demo/charge_j").orElse(defaults.chargeJ()));
    }

    public static long gaugeDropperCapacity() { return gaugeDropperCapacity; }
    public static long gaugeDropperTransferRate() { return gaugeDropperTransferRate; }

    /** 随身储电物品的容量（J）。 */
    public static long energyTabletMaxJ() {
        return energyTabletMaxJ;
    }

    /** 随身储电物品每刻最多吃多少（J）。 */
    public static long energyTabletChargeRateJ() {
        return energyTabletChargeRateJ;
    }

    /** 能存电的物品 id（充电台只充这些；哪些东西存电是内容口径）。 */
    public static Set<String> energyItemIds() {
        return energyItems;
    }

    public static DemoSettings demo() {
        return demo;
    }

    /** 传输元件是否用服务端 6px 碰撞；默认 false = 与客户端叶子满方块一致。 */
    public static boolean transmitterCollision6px() {
        return transmitterCollision6px;
    }
}
