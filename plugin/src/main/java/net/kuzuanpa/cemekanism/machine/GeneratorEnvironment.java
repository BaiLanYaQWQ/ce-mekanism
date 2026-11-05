package net.kuzuanpa.cemekanism.machine;

import net.kuzuanpa.cemekanism.platform.GeneratorConfig;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * 发电机要问世界的几件事（文档 11 G1）：天空、天气、生物群系、熔岩邻面、禁风维度。
 * 这里不碰核心：核心只拿"这一台机器"和它的罐，世界判断留在插件侧。
 */
public final class GeneratorEnvironment {

    private GeneratorEnvironment() {
    }

    /** 方块上方一格能见太阳（基础太阳能：控制器本身就是面板）。 */
    public static boolean canSeeSunAbove(World world, int x, int y, int z) {
        if (!world.isDayTime()) {
            return false;
        }
        if (world.getHighestBlockYAt(x, z) > y) {
            return false;
        }
        Block above = world.getBlockAt(x, y + 1, z);
        return above.getType().isAir() && above.getLightFromSky() >= 15;
    }

    /** 指定格自己能见太阳（高级太阳能：九个采光点都在 y+2 的空气里）。 */
    public static boolean canSeeSunAt(World world, int x, int y, int z) {
        if (!world.isDayTime() || world.getHighestBlockYAt(x, z) > y) {
            return false;
        }
        Block block = world.getBlockAt(x, y, z);
        return block.getType().isAir() && block.getLightFromSky() >= 15;
    }

    /** 太阳亮度（0..1）：昼夜曲线 + 雨/雷惩罚，形状按上游 LightLevelUtils.getSunBrightness。 */
    public static float sunBrightness(World world) {
        if (!world.isDayTime()) {
            return 0.0F;
        }
        long time = Math.floorMod(world.getTime(), 24000L);
        double angle = time / 24000.0 * Math.PI * 2.0;
        double daylight = Math.max(0.0, Math.sin(angle));
        float brightness = (float) (0.2 + 0.8 * daylight);
        if (world.hasStorm()) {
            brightness *= 0.7F;
        }
        if (world.isThundering()) {
            brightness *= 0.5F;
        }
        return Math.max(0.0F, Math.min(1.0F, brightness));
    }

    /** 生物群系给出的峰值倍率：温度 0.8 最优，降雨/湿度拖后腿（上游 SolarCheck）。 */
    public static float peakMultiplier(World world, int x, int y, int z) {
        double temperature = world.getTemperature(x, y, z);
        double humidity = world.getHumidity(x, y, z);
        double temperatureEffect = 0.3 * (0.8 - temperature);
        double humidityEffect = world.hasStorm() ? -0.3 * humidity : 0.0;
        return (float) (1.0 + temperatureEffect + humidityEffect);
    }

    /** 风力顶部 y+4 必须不是流体且能看见天空。 */
    public static boolean windTopOpen(World world, int x, int y, int z) {
        int top = y + 4;
        if (world.getHighestBlockYAt(x, z) > top) {
            return false;
        }
        Block block = world.getBlockAt(x, top, z);
        return block.getType().isAir() && block.getLightFromSky() >= 15;
    }

    public static boolean noWindDimension(World world) {
        String id = world.getKey().toString();
        return GeneratorConfig.noWindDimensions().contains(id)
                || world.getEnvironment() == World.Environment.NETHER
                || world.getEnvironment() == World.Environment.THE_END;
    }

    /** 六个邻面里有几面是熔岩（上游还把被熔岩浸没的自己算一面；本工程方块不带 fluid state）。 */
    public static int lavaSides(World world, int x, int y, int z) {
        int sides = 0;
        for (int[] offset : new int[][] {{0, -1, 0}, {0, 1, 0}, {0, 0, -1}, {0, 0, 1}, {-1, 0, 0}, {1, 0, 0}}) {
            Block block = world.getBlockAt(x + offset[0], y + offset[1], z + offset[2]);
            if (block.getType() == Material.LAVA) {
                sides++;
            }
        }
        return sides;
    }

    public static boolean isNether(World world) {
        return world.getEnvironment() == World.Environment.NETHER
                || "minecraft:the_nether".equals(world.getKey().toString());
    }
}
