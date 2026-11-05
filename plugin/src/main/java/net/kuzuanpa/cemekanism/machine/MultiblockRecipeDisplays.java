package net.kuzuanpa.cemekanism.machine;

import mekcore.filter.Matcher;
import mekcore.recipe.Ingredient;
import mekcore.recipe.Recipe;
import mekcore.recipe.RecipeOutput;
import mekcore.resource.ResourceStack;
import mekcore.resource.ResourceTypes;
import mekcore.spec.SteamMath;
import net.kuzuanpa.cemekanism.i18n.I18n;
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
                        .note(I18n.tr("recipe.multiblock.evaporation")).build(),
                Recipe.builder("display:evaporation/lithium", "mek:thermal_evaporation_plant")
                        .input(Ingredient.fluid(10, MultiblockConfig.BRINE))
                        .output(RecipeOutput.fluid(MultiblockConfig.LITHIUM, 1)).ticks(0)
                        .note(I18n.tr("recipe.multiblock.evaporation")).build(),
                Recipe.builder("display:boiler/steam", "mek:thermoelectric_boiler")
                        .input(Ingredient.fluid(1, MultiblockConfig.WATER))
                        .output(RecipeOutput.chemical(MultiblockConfig.STEAM, 1)).ticks(0)
                        .note(I18n.tr("recipe.multiblock.boiler")).build(),
                Recipe.builder("display:turbine/steam", "mek:industrial_turbine")
                        .input(Ingredient.chemical(1000, MultiblockConfig.STEAM))
                        .output(RecipeOutput.energy(Math.round(1000 * SteamMath.energyPerSteamMB())))
                        .output(RecipeOutput.fluid(MultiblockConfig.WATER, 1000)).ticks(0)
                        .note(I18n.tr("recipe.multiblock.turbine")).build(),
                Recipe.builder("display:fission/fuel", "mek:fission_reactor")
                        .input(Ingredient.chemical(1, FissionReactorLogic.FISSILE_FUEL))
                        .output(RecipeOutput.chemical(FissionReactorLogic.NUCLEAR_WASTE, 1))
                        .output(new RecipeOutput.Resource(ResourceStack.heat(MultiblockConfig.fissionEnergyPerFuelJ()), 100)).ticks(0)
                        .note(I18n.tr("recipe.multiblock.fission_fuel")).build(),
                Recipe.builder("display:fission/cooling", "mek:fission_reactor")
                        .input(Ingredient.fluid(1, MultiblockConfig.WATER))
                        .output(RecipeOutput.chemical(MultiblockConfig.STEAM, 1)).ticks(0)
                        .note(I18n.tr("recipe.multiblock.fission_cooling")).build(),
                Recipe.builder("display:fusion/injection", "mek:fusion_reactor")
                        .input(Ingredient.chemical(1, FusionReactorLogic.DEUTERIUM))
                        .input(Ingredient.chemical(1, FusionReactorLogic.TRITIUM))
                        .output(RecipeOutput.chemical(FusionReactorLogic.FUSION_FUEL, 2)).ticks(0)
                        .note(I18n.tr("recipe.multiblock.fusion_injection")).build(),
                Recipe.builder("display:fusion/fuel", "mek:fusion_reactor")
                        .input(Ingredient.chemical(1, FusionReactorLogic.FUSION_FUEL))
                        .output(new RecipeOutput.Resource(ResourceStack.heat(MultiblockConfig.fusionEnergyPerFuelJ()), 100)).ticks(0)
                        .note(I18n.tr("recipe.multiblock.fusion_fuel")).build(),
                Recipe.builder("display:fusion/cooling", "mek:fusion_reactor")
                        .input(Ingredient.fluid(1, MultiblockConfig.WATER))
                        .output(RecipeOutput.chemical(MultiblockConfig.STEAM, 1)).ticks(0)
                        .note(I18n.tr("recipe.multiblock.fusion_cooling")).build(),
                Recipe.builder("display:fusion/thermocouple", "mek:fusion_reactor")
                        .input(Ingredient.of(ResourceTypes.HEAT, 1000, Matcher.any(), I18n.tr("multiblock.reactor_heat")))
                        .output(RecipeOutput.energy((long) Math.floor(1000 * MultiblockConfig.fusionThermocoupleEfficiency()))).ticks(0)
                        .note(I18n.tr("recipe.multiblock.thermocouple")).build(),
                Recipe.builder("display:sps/antimatter", "mek:sps")
                        .input(Ingredient.chemical(MultiblockConfig.spsInputPerAntimatter(), SpsLogic.POLONIUM))
                        .output(RecipeOutput.chemical(SpsLogic.ANTIMATTER, 1)).ticks(0)
                        .energy(Math.multiplyExact(MultiblockConfig.spsInputPerAntimatter(), MultiblockConfig.spsEnergyPerInputJ()))
                        .note(I18n.tr("recipe.multiblock.sps")).build());
    }
}
