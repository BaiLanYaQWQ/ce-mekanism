package net.kuzuanpa.cemekanism.machine;

import mekcore.filter.Matcher;
import mekcore.recipe.Ingredient;
import mekcore.recipe.Recipe;
import mekcore.recipe.RecipeOutput;
import mekcore.resource.ResourceStack;
import mekcore.resource.ResourceTypes;
import mekcore.spec.SteamMath;
import net.kuzuanpa.cemekanism.multiblock.MultiblockConfig;
import net.kuzuanpa.cemekanism.multiblock.logic.FissionReactorLogic;
import net.kuzuanpa.cemekanism.multiblock.logic.FusionReactorLogic;
import net.kuzuanpa.cemekanism.multiblock.logic.SpsLogic;

import java.util.List;

/** Display ratios for the actual multiblock logic; these are never registered as runnable core recipes. */
final class MultiblockRecipeDisplays {
    private MultiblockRecipeDisplays() {}

    static List<Recipe> recipes() {
        return List.of(
                Recipe.builder("display:evaporation/brine", "mek:thermal_evaporation_plant")
                        .input(Ingredient.fluid(10, MultiblockConfig.WATER))
                        .output(RecipeOutput.fluid(MultiblockConfig.BRINE, 1)).ticks(0)
                        .note("转化比例 10:1；速度取决于温度和塔高").build(),
                Recipe.builder("display:evaporation/lithium", "mek:thermal_evaporation_plant")
                        .input(Ingredient.fluid(10, MultiblockConfig.BRINE))
                        .output(RecipeOutput.fluid(MultiblockConfig.LITHIUM, 1)).ticks(0)
                        .note("转化比例 10:1；速度取决于温度和塔高").build(),
                Recipe.builder("display:boiler/steam", "mek:thermoelectric_boiler")
                        .input(Ingredient.fluid(1, MultiblockConfig.WATER))
                        .output(RecipeOutput.chemical(MultiblockConfig.STEAM, 1)).ticks(0)
                        .note("需要外部热源；水与蒸汽 1:1，速率取决于温度与过热元件").build(),
                Recipe.builder("display:turbine/steam", "mek:industrial_turbine")
                        .input(Ingredient.chemical(1000, MultiblockConfig.STEAM))
                        .output(RecipeOutput.energy(Math.round(1000 * SteamMath.energyPerSteamMB())))
                        .output(RecipeOutput.fluid(MultiblockConfig.WATER, 1000)).ticks(0)
                        .note("显示满叶片效率下的发电上限与最大回水；实际受叶片、线圈、通风口和冷凝器限制").build(),
                Recipe.builder("display:fission/fuel", "mek:fission_reactor")
                        .input(Ingredient.chemical(1, FissionReactorLogic.FISSILE_FUEL))
                        .output(RecipeOutput.chemical(FissionReactorLogic.NUCLEAR_WASTE, 1))
                        .output(new RecipeOutput.Resource(ResourceStack.heat(MultiblockConfig.fissionEnergyPerFuelJ()), 100)).ticks(0)
                        .note("燃烧燃料产生核废料与热量；水冷过程另列，燃烧率由燃料组件和设置决定").build(),
                Recipe.builder("display:fission/cooling", "mek:fission_reactor")
                        .input(Ingredient.fluid(1, MultiblockConfig.WATER))
                        .output(RecipeOutput.chemical(MultiblockConfig.STEAM, 1)).ticks(0)
                        .note("堆体温度超过 373.15 K 时水冷；水与蒸汽 1:1，冷却速度受结构表面积影响").build(),
                Recipe.builder("display:fusion/injection", "mek:fusion_reactor")
                        .input(Ingredient.chemical(1, FusionReactorLogic.DEUTERIUM))
                        .input(Ingredient.chemical(1, FusionReactorLogic.TRITIUM))
                        .output(RecipeOutput.chemical(FusionReactorLogic.FUSION_FUEL, 2)).ticks(0)
                        .note("内部混合：氘与氚 1:1；也可直接输入 D-T 燃料").build(),
                Recipe.builder("display:fusion/fuel", "mek:fusion_reactor")
                        .input(Ingredient.chemical(1, FusionReactorLogic.FUSION_FUEL))
                        .output(new RecipeOutput.Resource(ResourceStack.heat(MultiblockConfig.fusionEnergyPerFuelJ()), 100)).ticks(0)
                        .note("需黑体辐射腔与激光点火；等离子体达到燃烧温度后释放热量").build(),
                Recipe.builder("display:fusion/cooling", "mek:fusion_reactor")
                        .input(Ingredient.fluid(1, MultiblockConfig.WATER))
                        .output(RecipeOutput.chemical(MultiblockConfig.STEAM, 1)).ticks(0)
                        .note("热堆体水冷产生蒸汽，水与蒸汽 1:1；速率随堆体温度变化").build(),
                Recipe.builder("display:fusion/thermocouple", "mek:fusion_reactor")
                        .input(Ingredient.of(ResourceTypes.HEAT, 1000, Matcher.any(), "堆体热量"))
                        .output(RecipeOutput.energy((long) Math.floor(1000 * MultiblockConfig.fusionThermocoupleEfficiency()))).ticks(0)
                        .note("热电偶转换效率取当前配置；实际功率取决于堆体温度和导热").build(),
                Recipe.builder("display:sps/antimatter", "mek:sps")
                        .input(Ingredient.chemical(MultiblockConfig.spsInputPerAntimatter(), SpsLogic.POLONIUM))
                        .output(RecipeOutput.chemical(SpsLogic.ANTIMATTER, 1)).ticks(0)
                        .energy(Math.multiplyExact(MultiblockConfig.spsInputPerAntimatter(), MultiblockConfig.spsEnergyPerInputJ()))
                        .note("总耗能按当前 SPS 配置计算；处理速度由线圈供能决定").build());
    }
}
