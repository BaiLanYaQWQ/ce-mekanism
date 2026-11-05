package net.kuzuanpa.cemekanism.generated;

// mek.py generate 生成，勿手改：改 tools/curation.yml 后重新生成。
// 一张内容表两个出口：这份是核心登记那个出口（CE 侧在 pack/configuration）。

import java.util.Map;
import java.util.List;
import mekcore.api.Registrars;
import mekcore.chemical.Chemical;
import mekcore.chemical.ChemicalState;
import mekcore.item.ItemSlotSpec;
import mekcore.item.ItemMatcher;
import mekcore.item.ItemRef;
import mekcore.machine.MachineBlueprint;
import mekcore.machine.HeatSpec;
import mekcore.multiblock.BlockRole;
import mekcore.multiblock.BlockRoles;
import mekcore.multiblock.BlockSection;
import mekcore.multiblock.MultiblockBlueprint;
import mekcore.multiblock.PortDefinition;
import mekcore.multiblock.RatioRequirement;
import mekcore.multiblock.RoleRequirement;
import mekcore.multiblock.SizeRule;
import mekcore.multiblock.StructureLayout;
import mekcore.multiblock.StructureSpec;
import mekcore.recipe.Ingredient;
import mekcore.recipe.ItemIngredient;
import mekcore.recipe.Recipe;
import mekcore.recipe.RecipeOutput;
import mekcore.resource.ResourceStack;
import mekcore.resource.ResourceTypes;
import mekcore.upgrade.Upgrades;
import net.kuzuanpa.cemekanism.multiblock.MultiblockDef;
import net.kuzuanpa.cemekanism.transmitter.TransmitterDef;

public final class GeneratedContent {

    /** Explicit fluid tints from the upstream fluid registries. */
    public static final Map<String, Integer> FLUID_COLORS = Map.ofEntries(
            Map.entry("mek:bioethanol", 0xCEEB3D),
            Map.entry("mek:brine", 0xFEEF9C),
            Map.entry("mek:heavy_water", 0x0D1455),
            Map.entry("mek:nutritional_paste", 0xEB6CA3));

    private GeneratedContent() {
    }

    /** 内容表登记进核心（物品名 / 化学品 / 机器蓝图）。*/
    public static void register(Registrars r) {
        names(r);
        chemicals(r);
        machines(r);
        multiblocks(r);
        recipes(r);
    }

    /** 化学品（气体/浆液/颜料/辅料）：颜色与放射性来自上游 datagen，密度来自 ChemicalConstants。*/
    public static void chemicals(Registrars r) {
        r.chemical(Chemical.of("mek:antimatter", "反物质", "Antimatter", ChemicalState.GAS).withColor(0xFFA464B3));
        r.chemical(Chemical.of("mek:brine", "气态盐水", "Gaseous Brine", ChemicalState.GAS).withColor(0xFFFEEF9C));
        r.chemical(Chemical.of("mek:chlorine", "氯气", "Chlorine", ChemicalState.GAS).withColor(0xFFCFE800).withDensity(1_422.92));
        r.chemical(Chemical.of("mek:deuterium", "氘", "Deuterium", ChemicalState.GAS).withColor(0xFFFF3232));
        r.chemical(Chemical.of("mek:ethene", "乙烯", "Ethene", ChemicalState.GAS).withColor(0xFFEACCF9).withDensity(577));
        r.chemical(Chemical.of("mek:fissile_fuel", "裂变燃料", "Fissile Fuel", ChemicalState.GAS).withColor(0xFF2E332F));
        r.chemical(Chemical.of("mek:fusion_fuel", "D-T燃料/氘氚燃料", "D-T Fuel", ChemicalState.GAS).withColor(0xFF7E007D));
        r.chemical(Chemical.of("mek:hydrofluoric_acid", "氢氟酸", "Hydrofluoric Acid", ChemicalState.GAS).withColor(0xFFC6C7BD).withDensity(1_150));
        r.chemical(Chemical.of("mek:hydrogen", "氢气", "Hydrogen", ChemicalState.GAS).withColor(0xFFFFFFFF).withDensity(70.85));
        r.chemical(Chemical.of("mek:hydrogen_chloride", "氯化氢", "Hydrogen Chloride", ChemicalState.GAS).withColor(0xFFA8F1E9).withDensity(821.43));
        r.chemical(Chemical.of("mek:lithium", "锂", "Lithium", ChemicalState.GAS).withColor(0xFFEBA400).withDensity(512));
        r.chemical(Chemical.of("mek:nuclear_waste", "核废料", "Nuclear Waste", ChemicalState.GAS).withColor(0xFF4F412A).withRadioactivity(0.01));
        r.chemical(Chemical.of("mek:osmium", "锇", "Osmium", ChemicalState.GAS).withColor(0xFF52BDCA));
        r.chemical(Chemical.of("mek:oxygen", "氧气", "Oxygen", ChemicalState.GAS).withColor(0xFF6CE2FF).withDensity(1_141));
        r.chemical(Chemical.of("mek:plutonium", "钚", "Plutonium", ChemicalState.GAS).withColor(0xFF1F919C).withRadioactivity(0.02));
        r.chemical(Chemical.of("mek:polonium", "钋", "Polonium", ChemicalState.GAS).withColor(0xFF1B9E7B).withRadioactivity(0.05));
        r.chemical(Chemical.of("mek:sodium", "钠蒸汽", "Sodium", ChemicalState.GAS).withColor(0xFFE9FEF4).withDensity(927));
        r.chemical(Chemical.of("mek:spent_nuclear_waste", "用尽的核废料", "Spent Nuclear Waste", ChemicalState.GAS).withColor(0xFF262015).withRadioactivity(0.01));
        r.chemical(Chemical.of("mek:steam", "蒸汽", "Steam", ChemicalState.GAS).withColor(0xFFFFFFFF));
        r.chemical(Chemical.of("mek:sulfur_dioxide", "二氧化硫", "Sulfur Dioxide", ChemicalState.GAS).withColor(0xFFA99D90).withDensity(1_400));
        r.chemical(Chemical.of("mek:sulfur_trioxide", "三氧化硫", "Sulfur Trioxide", ChemicalState.GAS).withColor(0xFFCE6C6C).withDensity(1_920));
        r.chemical(Chemical.of("mek:sulfuric_acid", "硫酸", "Sulfuric Acid", ChemicalState.GAS).withColor(0xFF82802B).withDensity(1_840));
        r.chemical(Chemical.of("mek:superheated_sodium", "过热的钠蒸汽", "Superheated Sodium", ChemicalState.GAS).withColor(0xFFD19469).withDensity(927));
        r.chemical(Chemical.of("mek:tritium", "氚", "Tritium", ChemicalState.GAS).withColor(0xFF64FF70));
        r.chemical(Chemical.of("mek:uranium_hexafluoride", "六氟化铀", "Uranium Hexafluoride", ChemicalState.GAS).withColor(0xFF809960).withDensity(5_090));
        r.chemical(Chemical.of("mek:uranium_oxide", "氧化铀", "Uranium Oxide", ChemicalState.GAS).withColor(0xFFE1F573).withDensity(10_970));
        r.chemical(Chemical.of("mek:water_vapor", "水蒸气", "Water Vapor", ChemicalState.GAS).withColor(0xFFFFFFFF));
        r.chemical(Chemical.of("mek:bio", "生物质", "Biomass", ChemicalState.INFUSION).withColor(0xFF5A4630));
        r.chemical(Chemical.of("mek:carbon", "碳", "Carbon", ChemicalState.INFUSION).withColor(0xFF2C2C2C));
        r.chemical(Chemical.of("mek:diamond", "钻石", "Diamond", ChemicalState.INFUSION).withColor(0xFF6CEDD8));
        r.chemical(Chemical.of("mek:fungi", "蘑菇", "Fungi", ChemicalState.INFUSION).withColor(0xFF74656A));
        r.chemical(Chemical.of("mek:gold", "金", "Gold", ChemicalState.INFUSION).withColor(0xFFF2CD67));
        r.chemical(Chemical.of("mek:redstone", "红石", "Redstone", ChemicalState.INFUSION).withColor(0xFFB30505));
        r.chemical(Chemical.of("mek:refined_obsidian", "强化黑曜石", "Refined Obsidian", ChemicalState.INFUSION).withColor(0xFF7C00ED));
        r.chemical(Chemical.of("mek:tin", "锡", "Tin", ChemicalState.INFUSION).withColor(0xFFCCCCD9));
        r.chemical(Chemical.of("mek:black", "黑色颜料", "Black Pigment", ChemicalState.PIGMENT).withColor(0xFF404040));
        r.chemical(Chemical.of("mek:blue", "蓝色颜料", "Blue Pigment", ChemicalState.PIGMENT).withColor(0xFF366BD0));
        r.chemical(Chemical.of("mek:brown", "棕色颜料", "Brown Pigment", ChemicalState.PIGMENT).withColor(0xFFA17649));
        r.chemical(Chemical.of("mek:cyan", "青色颜料", "Cyan Pigment", ChemicalState.PIGMENT).withColor(0xFF00F3D0));
        r.chemical(Chemical.of("mek:gray", "灰色颜料", "Gray Pigment", ChemicalState.PIGMENT).withColor(0xFF7A7A7A));
        r.chemical(Chemical.of("mek:green", "绿色颜料", "Green Pigment", ChemicalState.PIGMENT).withColor(0xFF59C15F));
        r.chemical(Chemical.of("mek:light_blue", "淡蓝色颜料", "Light Blue Pigment", ChemicalState.PIGMENT).withColor(0xFF559EFF));
        r.chemical(Chemical.of("mek:light_gray", "淡灰色颜料", "Light Gray Pigment", ChemicalState.PIGMENT).withColor(0xFFCFCFCF));
        r.chemical(Chemical.of("mek:lime", "黄绿色颜料", "Lime Pigment", ChemicalState.PIGMENT).withColor(0xFF75FF89));
        r.chemical(Chemical.of("mek:magenta", "品红色颜料", "Magenta Pigment", ChemicalState.PIGMENT).withColor(0xFFD55ECB));
        r.chemical(Chemical.of("mek:orange", "橙色颜料", "Orange Pigment", ChemicalState.PIGMENT).withColor(0xFFFFA160));
        r.chemical(Chemical.of("mek:pink", "粉红色颜料", "Pink Pigment", ChemicalState.PIGMENT).withColor(0xFFFFBCC4));
        r.chemical(Chemical.of("mek:purple", "紫色颜料", "Purple Pigment", ChemicalState.PIGMENT).withColor(0xFFA460D9));
        r.chemical(Chemical.of("mek:red", "红色颜料", "Red Pigment", ChemicalState.PIGMENT).withColor(0xFFFF383C));
        r.chemical(Chemical.of("mek:white", "白色颜料", "White Pigment", ChemicalState.PIGMENT).withColor(0xFFFFFFFF));
        r.chemical(Chemical.of("mek:yellow", "黄色颜料", "Yellow Pigment", ChemicalState.PIGMENT).withColor(0xFFFFDD4F));
        r.chemical(Chemical.of("mek:clean_copper", "纯净的铜浆液", "Clean Copper Slurry", ChemicalState.SLURRY).withColor(0xFFAA4B19));
        r.chemical(Chemical.of("mek:clean_gold", "纯净的金浆液", "Clean Gold Slurry", ChemicalState.SLURRY).withColor(0xFFF2CD67));
        r.chemical(Chemical.of("mek:clean_iron", "纯净的铁浆液", "Clean Iron Slurry", ChemicalState.SLURRY).withColor(0xFFAF8E77));
        r.chemical(Chemical.of("mek:clean_lead", "纯净的铅浆液", "Clean Lead Slurry", ChemicalState.SLURRY).withColor(0xFF3A404A));
        r.chemical(Chemical.of("mek:clean_osmium", "纯净的锇浆液", "Clean Osmium Slurry", ChemicalState.SLURRY).withColor(0xFF1E79C3));
        r.chemical(Chemical.of("mek:clean_tin", "纯净的锡浆液", "Clean Tin Slurry", ChemicalState.SLURRY).withColor(0xFFCCCCD9));
        r.chemical(Chemical.of("mek:clean_uranium", "纯净的铀浆液", "Clean Uranium Slurry", ChemicalState.SLURRY).withColor(0xFF46664F));
        r.chemical(Chemical.of("mek:dirty_copper", "污浊的铜浆液", "Dirty Copper Slurry", ChemicalState.SLURRY).withColor(0xFFAA4B19));
        r.chemical(Chemical.of("mek:dirty_gold", "污浊的金浆液", "Dirty Gold Slurry", ChemicalState.SLURRY).withColor(0xFFF2CD67));
        r.chemical(Chemical.of("mek:dirty_iron", "污浊的铁浆液", "Dirty Iron Slurry", ChemicalState.SLURRY).withColor(0xFFAF8E77));
        r.chemical(Chemical.of("mek:dirty_lead", "污浊的铅浆液", "Dirty Lead Slurry", ChemicalState.SLURRY).withColor(0xFF3A404A));
        r.chemical(Chemical.of("mek:dirty_osmium", "污浊的锇浆液", "Dirty Osmium Slurry", ChemicalState.SLURRY).withColor(0xFF1E79C3));
        r.chemical(Chemical.of("mek:dirty_tin", "污浊的锡浆液", "Dirty Tin Slurry", ChemicalState.SLURRY).withColor(0xFFCCCCD9));
        r.chemical(Chemical.of("mek:dirty_uranium", "污浊的铀浆液", "Dirty Uranium Slurry", ChemicalState.SLURRY).withColor(0xFF46664F));
        r.name(ResourceTypes.FLUID, "mek:bioethanol", "生物乙醇");
        r.name(ResourceTypes.FLUID, "mek:brine", "气态盐水");
        r.name(ResourceTypes.FLUID, "mek:chlorine", "氯气");
        r.name(ResourceTypes.FLUID, "mek:deuterium", "氘");
        r.name(ResourceTypes.FLUID, "mek:ethene", "乙烯");
        r.name(ResourceTypes.FLUID, "mek:flowing_brine", "flowing_brine");
        r.name(ResourceTypes.FLUID, "mek:flowing_chlorine", "flowing_chlorine");
        r.name(ResourceTypes.FLUID, "mek:flowing_deuterium", "flowing_deuterium");
        r.name(ResourceTypes.FLUID, "mek:flowing_ethene", "flowing_ethene");
        r.name(ResourceTypes.FLUID, "mek:flowing_fusion_fuel", "flowing_fusion_fuel");
        r.name(ResourceTypes.FLUID, "mek:flowing_heavy_water", "flowing_heavy_water");
        r.name(ResourceTypes.FLUID, "mek:flowing_hydrofluoric_acid", "flowing_hydrofluoric_acid");
        r.name(ResourceTypes.FLUID, "mek:flowing_hydrogen", "flowing_hydrogen");
        r.name(ResourceTypes.FLUID, "mek:flowing_hydrogen_chloride", "flowing_hydrogen_chloride");
        r.name(ResourceTypes.FLUID, "mek:flowing_lithium", "flowing_lithium");
        r.name(ResourceTypes.FLUID, "mek:flowing_oxygen", "flowing_oxygen");
        r.name(ResourceTypes.FLUID, "mek:flowing_sodium", "flowing_sodium");
        r.name(ResourceTypes.FLUID, "mek:flowing_steam", "flowing_steam");
        r.name(ResourceTypes.FLUID, "mek:flowing_sulfur_dioxide", "flowing_sulfur_dioxide");
        r.name(ResourceTypes.FLUID, "mek:flowing_sulfur_trioxide", "flowing_sulfur_trioxide");
        r.name(ResourceTypes.FLUID, "mek:flowing_sulfuric_acid", "flowing_sulfuric_acid");
        r.name(ResourceTypes.FLUID, "mek:flowing_superheated_sodium", "flowing_superheated_sodium");
        r.name(ResourceTypes.FLUID, "mek:flowing_tritium", "flowing_tritium");
        r.name(ResourceTypes.FLUID, "mek:flowing_uranium_hexafluoride", "flowing_uranium_hexafluoride");
        r.name(ResourceTypes.FLUID, "mek:flowing_uranium_oxide", "flowing_uranium_oxide");
        r.name(ResourceTypes.FLUID, "mek:fusion_fuel", "D-T燃料/氘氚燃料");
        r.name(ResourceTypes.FLUID, "mek:heavy_water", "heavy_water");
        r.name(ResourceTypes.FLUID, "mek:hydrofluoric_acid", "氢氟酸");
        r.name(ResourceTypes.FLUID, "mek:hydrogen", "氢气");
        r.name(ResourceTypes.FLUID, "mek:hydrogen_chloride", "氯化氢");
        r.name(ResourceTypes.FLUID, "mek:lithium", "锂");
        r.name(ResourceTypes.FLUID, "mek:oxygen", "氧气");
        r.name(ResourceTypes.FLUID, "mek:sodium", "钠蒸汽");
        r.name(ResourceTypes.FLUID, "mek:steam", "蒸汽");
        r.name(ResourceTypes.FLUID, "mek:sulfur_dioxide", "二氧化硫");
        r.name(ResourceTypes.FLUID, "mek:sulfur_trioxide", "三氧化硫");
        r.name(ResourceTypes.FLUID, "mek:sulfuric_acid", "硫酸");
        r.name(ResourceTypes.FLUID, "mek:superheated_sodium", "过热的钠蒸汽");
        r.name(ResourceTypes.FLUID, "mek:tritium", "氚");
        r.name(ResourceTypes.FLUID, "mek:uranium_hexafluoride", "六氟化铀");
        r.name(ResourceTypes.FLUID, "mek:uranium_oxide", "氧化铀");
        r.name(ResourceTypes.FLUID, "minecraft:water", "水");
    }

    /** 机器蓝图：数值取上游源码（UsageConfig 的 J/t、AttributeEnergy 的 4×、TileEntity*.BASE_TICKS_REQUIRED 的耗时）。*/
    public static void machines(Registrars r) {
        r.machine(MachineBlueprint.builder("mek:advanced_bin", "高级箱柜")
                .itemSlots(ItemSlotSpec.input("in"), ItemSlotSpec.output("out"))
                .noUpgrades()
                .logic(new net.kuzuanpa.cemekanism.storage.BinLogic())
                .build());
        // mek:advanced_bin：逻辑机器（无配方）——已挂逻辑
        r.machine(MachineBlueprint.builder("mek:advanced_chemical_tank", "高级化学品储罐")
                .itemSlots()
                .chemicalTank("stored", 256000L)
                .noUpgrades()
                .logic(new net.kuzuanpa.cemekanism.storage.PassiveStorageLogic())
                .build());
        // mek:advanced_chemical_tank：逻辑机器（无配方）——已挂逻辑
        r.machine(MachineBlueprint.builder("mek:advanced_energy_cube", "高级基础能量立方")
                .energyBuffer(16000000L)   // 上游 GeneratorsStorageConfig；产出由逻辑写入这个缓存
                .itemSlots()
                .noUpgrades()
                .logic(new net.kuzuanpa.cemekanism.storage.PassiveStorageLogic())
                .build());
        // mek:advanced_energy_cube：逻辑机器（无配方）——已挂逻辑
        r.machine(MachineBlueprint.builder("mek:advanced_fluid_tank", "高级流体储罐")
                .itemSlots()
                .fluidTank("stored", 64000L)
                .noUpgrades()
                .logic(new net.kuzuanpa.cemekanism.storage.PassiveStorageLogic())
                .build());
        // mek:advanced_fluid_tank：逻辑机器（无配方）——已挂逻辑
        r.machine(MachineBlueprint.builder("mek:advanced_solar_generator", "高级太阳能发电机")
                .energyBuffer(200000L)   // 上游 GeneratorsStorageConfig；产出由逻辑写入这个缓存
                .itemSlots()
                .upgrades(Upgrades.MUFFLING)
                .logic(new net.kuzuanpa.cemekanism.machine.logic.SolarGeneratorLogic(true))
                .build());
        // mek:advanced_solar_generator：逻辑机器（无配方）——已挂逻辑
        r.machine(MachineBlueprint.builder("mek:antiprotonic_nucleosynthesizer", "反质子核合成器")
                .energy(15L, 240L)   // 上游 UsageConfig.antiprotonicNucleosynthesizer（J/t；1 FE = 2.5 J）；上游 AttributeEnergy.STORAGE_MULTIPLIER × 单刻耗能（4×）；项目机器缓存倍率 ×4
                .itemSlots(ItemSlotSpec.input("in"), ItemSlotSpec.output("out"))
                .inputTank("chemical", ResourceTypes.CHEMICAL, 10000L)
                .upgrades(Upgrades.MUFFLING)
                .build());
        r.machine(MachineBlueprint.builder("mek:basic_bin", "基础箱柜")
                .itemSlots(ItemSlotSpec.input("in"), ItemSlotSpec.output("out"))
                .noUpgrades()
                .logic(new net.kuzuanpa.cemekanism.storage.BinLogic())
                .build());
        // mek:basic_bin：逻辑机器（无配方）——已挂逻辑
        r.machine(MachineBlueprint.builder("mek:basic_chemical_tank", "基础化学品储罐")
                .itemSlots()
                .chemicalTank("stored", 64000L)
                .noUpgrades()
                .logic(new net.kuzuanpa.cemekanism.storage.PassiveStorageLogic())
                .build());
        // mek:basic_chemical_tank：逻辑机器（无配方）——已挂逻辑
        r.machine(MachineBlueprint.builder("mek:basic_energy_cube", "基础基础能量立方")
                .energyBuffer(4000000L)   // 上游 GeneratorsStorageConfig；产出由逻辑写入这个缓存
                .itemSlots()
                .noUpgrades()
                .logic(new net.kuzuanpa.cemekanism.storage.PassiveStorageLogic())
                .build());
        // mek:basic_energy_cube：逻辑机器（无配方）——已挂逻辑
        r.machine(MachineBlueprint.builder("mek:basic_fluid_tank", "基础流体储罐")
                .itemSlots()
                .fluidTank("stored", 32000L)
                .noUpgrades()
                .logic(new net.kuzuanpa.cemekanism.storage.PassiveStorageLogic())
                .build());
        // mek:basic_fluid_tank：逻辑机器（无配方）——已挂逻辑
        r.machine(MachineBlueprint.builder("mek:bio_generator", "生物能发电机")
                .energyBuffer(160000L)   // 上游 GeneratorsStorageConfig；产出由逻辑写入这个缓存
                .itemSlots(ItemSlotSpec.input("fuel"))
                .fluidTank("biofuel", 24000L)
                .upgrades(Upgrades.MUFFLING)
                .logic(new net.kuzuanpa.cemekanism.machine.logic.BioGeneratorLogic())
                .build());
        // mek:bio_generator：逻辑机器（无配方）——已挂逻辑
        r.machine(MachineBlueprint.builder("mek:chargepad", "充电台")
                .energy(1024L, 16384L)   // 上游 UsageConfig.chargePad（J/t；1 FE = 2.5 J）；上游 AttributeEnergy.STORAGE_MULTIPLIER × 单刻耗能（4×）；项目机器缓存倍率 ×4
                .itemSlots()
                .noUpgrades()
                .logic(new net.kuzuanpa.cemekanism.machine.logic.ChargepadLogic())
                .build());
        // mek:chargepad：逻辑机器（无配方）——已挂逻辑
        r.machine(MachineBlueprint.builder("mek:chemical_crystallizer", "化学结晶器")
                .energy(15L, 240L)   // 上游 UsageConfig.chemicalCrystallizer（J/t；1 FE = 2.5 J）；上游 AttributeEnergy.STORAGE_MULTIPLIER × 单刻耗能（4×）；项目机器缓存倍率 ×4
                .itemSlots(ItemSlotSpec.output("out"))
                .inputTank("chemical", ResourceTypes.CHEMICAL, 10000L)
                .upgrades(Upgrades.ENERGY, Upgrades.MUFFLING, Upgrades.SPEED)
                .build());
        r.machine(MachineBlueprint.builder("mek:chemical_dissolution_chamber", "化学溶解室")
                .energy(16L, 256L)   // 上游 UsageConfig.chemicalDissolutionChamber（J/t；1 FE = 2.5 J）；上游 AttributeEnergy.STORAGE_MULTIPLIER × 单刻耗能（4×）；项目机器缓存倍率 ×4
                .itemSlots(ItemSlotSpec.input("in"))
                .inputTank("chemical", ResourceTypes.CHEMICAL, 10000L)
                .outputTank("output", ResourceTypes.CHEMICAL, 10000L)
                .upgrades(Upgrades.CHEMICAL, Upgrades.ENERGY, Upgrades.MUFFLING, Upgrades.SPEED)
                .build());
        r.machine(MachineBlueprint.builder("mek:chemical_infuser", "化学灌注器")
                .exponentialSpeed()
                .energy(15L, 240L)   // 上游 UsageConfig.chemicalInfuser（J/t；1 FE = 2.5 J）；上游 AttributeEnergy.STORAGE_MULTIPLIER × 单刻耗能（4×）；项目机器缓存倍率 ×4
                .itemSlots()
                .inputTank("left", ResourceTypes.CHEMICAL, 10000L)
                .inputTank("right", ResourceTypes.CHEMICAL, 10000L)
                .outputTank("center", ResourceTypes.CHEMICAL, 10000L)
                .upgrades(Upgrades.ENERGY, Upgrades.MUFFLING, Upgrades.SPEED)
                .build());
        r.machine(MachineBlueprint.builder("mek:chemical_injection_chamber", "化学压射室")
                .factoryCapable()
                .energy(15L, 2160L)   // 上游 UsageConfig.chemicalInjectionChamber（J/t；1 FE = 2.5 J）；上游 AttributeEnergy.STORAGE_MULTIPLIER × 单刻耗能（4×），再 × 最大工厂并行数 9（核心按每并行位扣电）；项目机器缓存倍率 ×4
                .itemSlots(ItemSlotSpec.parallelInput("in"), ItemSlotSpec.parallelOutput("out"))
                .inputTank("gas", ResourceTypes.CHEMICAL, 210L)
                .upgrades(Upgrades.CHEMICAL, Upgrades.ENERGY, Upgrades.MUFFLING, Upgrades.SPEED)
                .build());
        r.machine(MachineBlueprint.builder("mek:chemical_oxidizer", "化学氧化机")
                .energy(15L, 240L)   // 上游 UsageConfig.chemicalOxidizer（J/t；1 FE = 2.5 J）；上游 AttributeEnergy.STORAGE_MULTIPLIER × 单刻耗能（4×）；项目机器缓存倍率 ×4
                .itemSlots(ItemSlotSpec.input("in"))
                .outputTank("output", ResourceTypes.CHEMICAL, 10000L)
                .upgrades(Upgrades.ENERGY, Upgrades.MUFFLING, Upgrades.SPEED)
                .build());
        r.machine(MachineBlueprint.builder("mek:chemical_washer", "化学清洗机")
                .energy(5L, 80L)   // 上游 UsageConfig.chemicalWasher（J/t；1 FE = 2.5 J）；上游 AttributeEnergy.STORAGE_MULTIPLIER × 单刻耗能（4×）；项目机器缓存倍率 ×4
                .itemSlots()
                .inputTank("chemical", ResourceTypes.CHEMICAL, 10000L)
                .inputTank("fluid", ResourceTypes.FLUID, 10000L)
                .outputTank("output", ResourceTypes.CHEMICAL, 10000L)
                .upgrades(Upgrades.ENERGY, Upgrades.MUFFLING, Upgrades.SPEED)
                .build());
        r.machine(MachineBlueprint.builder("mek:combiner", "融合机")
                .factoryCapable()
                .energy(5L, 720L)   // 上游 UsageConfig.combiner（J/t；1 FE = 2.5 J）；上游 AttributeEnergy.STORAGE_MULTIPLIER × 单刻耗能（4×），再 × 最大工厂并行数 9（核心按每并行位扣电）；项目机器缓存倍率 ×4
                .itemSlots(ItemSlotSpec.parallelInput("in_a"), ItemSlotSpec.parallelInput("in_b"), ItemSlotSpec.parallelOutput("out"))
                .upgrades(Upgrades.ENERGY, Upgrades.MUFFLING, Upgrades.SPEED)
                .build());
        r.machine(MachineBlueprint.builder("mek:crusher", "粉碎机")
                .factoryCapable()
                .energy(8L, 1152L)   // 上游 UsageConfig.crusher（J/t；1 FE = 2.5 J）；上游 AttributeEnergy.STORAGE_MULTIPLIER × 单刻耗能（4×），再 × 最大工厂并行数 9（核心按每并行位扣电）；项目机器缓存倍率 ×4
                .itemSlots(ItemSlotSpec.parallelInput("in"), ItemSlotSpec.parallelOutput("out"))
                .upgrades(Upgrades.ENERGY, Upgrades.MUFFLING, Upgrades.SPEED)
                .build());
        r.machine(MachineBlueprint.builder("mek:digital_miner", "数字型采矿机")
                .energy(40L, 200000L)   // 上游 UsageConfig.digitalMiner（J/t；1 FE = 2.5 J）；curation.yml 指定基础缓存；项目机器缓存倍率 ×4
                .itemSlots(ItemSlotSpec.input("replace"), ItemSlotSpec.output("out"))
                .upgrades(Upgrades.ANCHOR, Upgrades.ENERGY, Upgrades.SPEED)
                .logic(new net.kuzuanpa.cemekanism.storage.DigitalMinerLogic())
                .build());
        // mek:digital_miner：逻辑机器（无配方）——已挂逻辑
        r.machine(MachineBlueprint.builder("mek:electric_pump", "电动泵")
                .energy(2L, 32L)   // 上游 UsageConfig.electricPump（J/t；1 FE = 2.5 J）；上游 AttributeEnergy.STORAGE_MULTIPLIER × 单刻耗能（4×）；项目机器缓存倍率 ×4
                .itemSlots()
                .outputTank("fluid", ResourceTypes.FLUID, 10000L)
                .upgrades(Upgrades.ENERGY, Upgrades.FILTER, Upgrades.SPEED)
                .logic(new net.kuzuanpa.cemekanism.storage.PumpLogic())
                .build());
        // mek:electric_pump：逻辑机器（无配方）——已挂逻辑
        r.machine(MachineBlueprint.builder("mek:electrolytic_separator", "电解分离器")
                .exponentialSpeed()
                .energyUpgradeKeepsConsumption()
                .energy(400L, 102400L)   // ；curation.yml 指定基础缓存；项目机器缓存倍率 ×4
                .itemSlots()
                .inputTank("input", ResourceTypes.FLUID, 24000L)
                .outputTank("left", ResourceTypes.CHEMICAL, 2400L)
                .outputTank("right", ResourceTypes.CHEMICAL, 2400L)
                .upgrades(Upgrades.ENERGY, Upgrades.MUFFLING, Upgrades.SPEED)
                .build());
        r.machine(MachineBlueprint.builder("mek:elite_bin", "精英箱柜")
                .itemSlots(ItemSlotSpec.input("in"), ItemSlotSpec.output("out"))
                .noUpgrades()
                .logic(new net.kuzuanpa.cemekanism.storage.BinLogic())
                .build());
        // mek:elite_bin：逻辑机器（无配方）——已挂逻辑
        r.machine(MachineBlueprint.builder("mek:elite_chemical_tank", "精英化学品储罐")
                .itemSlots()
                .chemicalTank("stored", 1024000L)
                .noUpgrades()
                .logic(new net.kuzuanpa.cemekanism.storage.PassiveStorageLogic())
                .build());
        // mek:elite_chemical_tank：逻辑机器（无配方）——已挂逻辑
        r.machine(MachineBlueprint.builder("mek:elite_energy_cube", "精英基础能量立方")
                .energyBuffer(64000000L)   // 上游 GeneratorsStorageConfig；产出由逻辑写入这个缓存
                .itemSlots()
                .noUpgrades()
                .logic(new net.kuzuanpa.cemekanism.storage.PassiveStorageLogic())
                .build());
        // mek:elite_energy_cube：逻辑机器（无配方）——已挂逻辑
        r.machine(MachineBlueprint.builder("mek:elite_fluid_tank", "精英流体储罐")
                .itemSlots()
                .fluidTank("stored", 128000L)
                .noUpgrades()
                .logic(new net.kuzuanpa.cemekanism.storage.PassiveStorageLogic())
                .build());
        // mek:elite_fluid_tank：逻辑机器（无配方）——已挂逻辑
        r.machine(MachineBlueprint.builder("mek:energized_smelter", "电力熔炼炉")
                .factoryCapable()
                .energy(5L, 720L)   // 上游 UsageConfig.energizedSmelter（J/t；1 FE = 2.5 J）；上游 AttributeEnergy.STORAGE_MULTIPLIER × 单刻耗能（4×），再 × 最大工厂并行数 9（核心按每并行位扣电）；项目机器缓存倍率 ×4
                .itemSlots(ItemSlotSpec.parallelInput("in"), ItemSlotSpec.parallelOutput("out"))
                .upgrades(Upgrades.ENERGY, Upgrades.MUFFLING, Upgrades.SPEED)
                .build());
        r.machine(MachineBlueprint.builder("mek:enrichment_chamber", "富集仓")
                .factoryCapable()
                .energy(5L, 720L)   // 上游 UsageConfig.enrichmentChamber（J/t；1 FE = 2.5 J）；上游 AttributeEnergy.STORAGE_MULTIPLIER × 单刻耗能（4×），再 × 最大工厂并行数 9（核心按每并行位扣电）；项目机器缓存倍率 ×4
                .itemSlots(ItemSlotSpec.parallelInput("in"), ItemSlotSpec.parallelOutput("out"))
                .upgrades(Upgrades.ENERGY, Upgrades.MUFFLING, Upgrades.SPEED)
                .build());
        r.machine(MachineBlueprint.builder("mek:fuelwood_heater", "固体燃料加热器")
                .heat(new HeatSpec(0.1, 3000.0, 0.0))   // 发电机自身温度（热力发电机用；逻辑按条件加/减热）
                .itemSlots(ItemSlotSpec.input("fuel"))
                .noUpgrades()
                .logic(new net.kuzuanpa.cemekanism.machine.logic.FuelwoodHeaterLogic())
                .build());
        // mek:fuelwood_heater：逻辑机器（无配方）——已挂逻辑
        r.machine(MachineBlueprint.builder("mek:gas_burning_generator", "燃气发电机")
                .energyBuffer(200000L)   // 上游 GeneratorsStorageConfig；产出由逻辑写入这个缓存
                .itemSlots()
                .inputTank("fuel", ResourceTypes.CHEMICAL, 18000L)
                .upgrades(Upgrades.MUFFLING)
                .logic(new net.kuzuanpa.cemekanism.machine.logic.GasGeneratorLogic())
                .build());
        // mek:gas_burning_generator：逻辑机器（无配方）——已挂逻辑
        r.machine(MachineBlueprint.builder("mek:heat_generator", "热力发电机")
                .energyBuffer(160000L)   // 上游 GeneratorsStorageConfig；产出由逻辑写入这个缓存
                .heat(new HeatSpec(0.01, 3000.0, 0.0))   // 发电机自身温度（热力发电机用；逻辑按条件加/减热）
                .itemSlots(ItemSlotSpec.input("fuel"))
                .inputTank("lava", ResourceTypes.FLUID, 24000L)
                .upgrades(Upgrades.MUFFLING)
                .logic(new net.kuzuanpa.cemekanism.machine.logic.HeatGeneratorLogic())
                .build());
        // mek:heat_generator：逻辑机器（无配方）——已挂逻辑
        r.machine(MachineBlueprint.builder("mek:isotopic_centrifuge", "同位素离心机")
                .exponentialSpeed()
                .energy(8L, 128L)   // 上游 UsageConfig.isotopicCentrifuge（J/t；1 FE = 2.5 J）；上游 AttributeEnergy.STORAGE_MULTIPLIER × 单刻耗能（4×）；项目机器缓存倍率 ×4
                .itemSlots()
                .inputTank("input", ResourceTypes.CHEMICAL, 10000L)
                .outputTank("output", ResourceTypes.CHEMICAL, 10000L)
                .upgrades(Upgrades.ENERGY, Upgrades.MUFFLING, Upgrades.SPEED)
                .build());
        r.machine(MachineBlueprint.builder("mek:laser", "激光")
                .energy(20L, 320L)   // 上游 UsageConfig.laser（J/t；1 FE = 2.5 J）；上游 AttributeEnergy.STORAGE_MULTIPLIER × 单刻耗能（4×）；项目机器缓存倍率 ×4
                .itemSlots()
                .noUpgrades()
                .logic(new net.kuzuanpa.cemekanism.machine.logic.LaserLogic())
                .build());
        // mek:laser：逻辑机器（无配方）——已挂逻辑
        r.machine(MachineBlueprint.builder("mek:laser_amplifier", "激光增幅器")
                .energy(0L, 20000000L)   // ；curation.yml 指定基础缓存；项目机器缓存倍率 ×4
                .itemSlots()
                .noUpgrades()
                .logic(new net.kuzuanpa.cemekanism.machine.logic.LaserAmplifierLogic())
                .build());
        // mek:laser_amplifier：逻辑机器（无配方）——已挂逻辑
        r.machine(MachineBlueprint.builder("mek:logistical_sorter", "物流分类机")
                .itemSlots()
                .noUpgrades()
                .logic(new net.kuzuanpa.cemekanism.storage.SorterLogic())
                .build());
        // mek:logistical_sorter：逻辑机器（无配方）——已挂逻辑
        r.machine(MachineBlueprint.builder("mek:metallurgic_infuser", "冶金灌注机")
                .factoryCapable()
                .requiresItemInputToWake()
                .energy(5L, 720L)   // 上游 UsageConfig.metallurgicInfuser（J/t；1 FE = 2.5 J）；上游 AttributeEnergy.STORAGE_MULTIPLIER × 单刻耗能（4×），再 × 最大工厂并行数 9（核心按每并行位扣电）；项目机器缓存倍率 ×4
                .itemSlots(ItemSlotSpec.parallelInput("in"), ItemSlotSpec.extra("infuse_in"), ItemSlotSpec.parallelOutput("out"))
                .infusionTank("infuse", 1000L)
                .upgrades(Upgrades.ENERGY, Upgrades.MUFFLING, Upgrades.SPEED)
                .build());
        r.machine(MachineBlueprint.builder("mek:osmium_compressor", "锇压缩机")
                .factoryCapable()
                .energy(15L, 2160L)   // 上游 UsageConfig.osmiumCompressor（J/t；1 FE = 2.5 J）；上游 AttributeEnergy.STORAGE_MULTIPLIER × 单刻耗能（4×），再 × 最大工厂并行数 9（核心按每并行位扣电）；项目机器缓存倍率 ×4
                .itemSlots(ItemSlotSpec.parallelInput("in"), ItemSlotSpec.parallelOutput("out"))
                .inputTank("gas", ResourceTypes.CHEMICAL, 210L)
                .upgrades(Upgrades.ENERGY, Upgrades.MUFFLING, Upgrades.SPEED)
                .build());
        r.machine(MachineBlueprint.builder("mek:painting_machine", "上色机")
                .energy(5L, 80L)   // 上游 UsageConfig.paintingMachine（J/t；1 FE = 2.5 J）；上游 AttributeEnergy.STORAGE_MULTIPLIER × 单刻耗能（4×）；项目机器缓存倍率 ×4
                .itemSlots(ItemSlotSpec.input("in"), ItemSlotSpec.output("out"))
                .inputTank("pigment", ResourceTypes.CHEMICAL, 10000L)
                .upgrades(Upgrades.ENERGY, Upgrades.MUFFLING, Upgrades.SPEED)
                .build());
        r.machine(MachineBlueprint.builder("mek:personal_barrel", "私人桶")
                .itemSlots(ItemSlotSpec.input("storage0"), ItemSlotSpec.input("storage1"), ItemSlotSpec.input("storage2"), ItemSlotSpec.input("storage3"), ItemSlotSpec.input("storage4"), ItemSlotSpec.input("storage5"), ItemSlotSpec.input("storage6"), ItemSlotSpec.input("storage7"), ItemSlotSpec.input("storage8"), ItemSlotSpec.input("storage9"), ItemSlotSpec.input("storage10"), ItemSlotSpec.input("storage11"), ItemSlotSpec.input("storage12"), ItemSlotSpec.input("storage13"), ItemSlotSpec.input("storage14"), ItemSlotSpec.input("storage15"), ItemSlotSpec.input("storage16"), ItemSlotSpec.input("storage17"), ItemSlotSpec.input("storage18"), ItemSlotSpec.input("storage19"), ItemSlotSpec.input("storage20"), ItemSlotSpec.input("storage21"), ItemSlotSpec.input("storage22"), ItemSlotSpec.input("storage23"), ItemSlotSpec.input("storage24"), ItemSlotSpec.input("storage25"), ItemSlotSpec.input("storage26"), ItemSlotSpec.input("storage27"), ItemSlotSpec.input("storage28"), ItemSlotSpec.input("storage29"), ItemSlotSpec.input("storage30"), ItemSlotSpec.input("storage31"), ItemSlotSpec.input("storage32"), ItemSlotSpec.input("storage33"), ItemSlotSpec.input("storage34"), ItemSlotSpec.input("storage35"), ItemSlotSpec.input("storage36"), ItemSlotSpec.input("storage37"), ItemSlotSpec.input("storage38"), ItemSlotSpec.input("storage39"), ItemSlotSpec.input("storage40"), ItemSlotSpec.input("storage41"), ItemSlotSpec.input("storage42"), ItemSlotSpec.input("storage43"), ItemSlotSpec.input("storage44"), ItemSlotSpec.input("storage45"), ItemSlotSpec.input("storage46"), ItemSlotSpec.input("storage47"), ItemSlotSpec.input("storage48"), ItemSlotSpec.input("storage49"), ItemSlotSpec.input("storage50"), ItemSlotSpec.input("storage51"), ItemSlotSpec.input("storage52"), ItemSlotSpec.input("storage53"))
                .noUpgrades()
                .logic(new net.kuzuanpa.cemekanism.storage.PersonalStorageLogic())
                .build());
        // mek:personal_barrel：逻辑机器（无配方）——已挂逻辑
        r.machine(MachineBlueprint.builder("mek:personal_chest", "私人箱子")
                .itemSlots(ItemSlotSpec.input("storage0"), ItemSlotSpec.input("storage1"), ItemSlotSpec.input("storage2"), ItemSlotSpec.input("storage3"), ItemSlotSpec.input("storage4"), ItemSlotSpec.input("storage5"), ItemSlotSpec.input("storage6"), ItemSlotSpec.input("storage7"), ItemSlotSpec.input("storage8"), ItemSlotSpec.input("storage9"), ItemSlotSpec.input("storage10"), ItemSlotSpec.input("storage11"), ItemSlotSpec.input("storage12"), ItemSlotSpec.input("storage13"), ItemSlotSpec.input("storage14"), ItemSlotSpec.input("storage15"), ItemSlotSpec.input("storage16"), ItemSlotSpec.input("storage17"), ItemSlotSpec.input("storage18"), ItemSlotSpec.input("storage19"), ItemSlotSpec.input("storage20"), ItemSlotSpec.input("storage21"), ItemSlotSpec.input("storage22"), ItemSlotSpec.input("storage23"), ItemSlotSpec.input("storage24"), ItemSlotSpec.input("storage25"), ItemSlotSpec.input("storage26"), ItemSlotSpec.input("storage27"), ItemSlotSpec.input("storage28"), ItemSlotSpec.input("storage29"), ItemSlotSpec.input("storage30"), ItemSlotSpec.input("storage31"), ItemSlotSpec.input("storage32"), ItemSlotSpec.input("storage33"), ItemSlotSpec.input("storage34"), ItemSlotSpec.input("storage35"), ItemSlotSpec.input("storage36"), ItemSlotSpec.input("storage37"), ItemSlotSpec.input("storage38"), ItemSlotSpec.input("storage39"), ItemSlotSpec.input("storage40"), ItemSlotSpec.input("storage41"), ItemSlotSpec.input("storage42"), ItemSlotSpec.input("storage43"), ItemSlotSpec.input("storage44"), ItemSlotSpec.input("storage45"), ItemSlotSpec.input("storage46"), ItemSlotSpec.input("storage47"), ItemSlotSpec.input("storage48"), ItemSlotSpec.input("storage49"), ItemSlotSpec.input("storage50"), ItemSlotSpec.input("storage51"), ItemSlotSpec.input("storage52"), ItemSlotSpec.input("storage53"))
                .noUpgrades()
                .logic(new net.kuzuanpa.cemekanism.storage.PersonalStorageLogic())
                .build());
        // mek:personal_chest：逻辑机器（无配方）——已挂逻辑
        r.machine(MachineBlueprint.builder("mek:pigment_extractor", "颜料提取器")
                .energy(5L, 80L)   // 上游 UsageConfig.pigmentExtractor（J/t；1 FE = 2.5 J）；上游 AttributeEnergy.STORAGE_MULTIPLIER × 单刻耗能（4×）；项目机器缓存倍率 ×4
                .itemSlots(ItemSlotSpec.input("in"))
                .outputTank("output", ResourceTypes.CHEMICAL, 10000L)
                .upgrades(Upgrades.ENERGY, Upgrades.MUFFLING, Upgrades.SPEED)
                .build());
        r.machine(MachineBlueprint.builder("mek:pigment_mixer", "颜料混合器")
                .energy(5L, 80L)   // 上游 UsageConfig.pigmentMixer（J/t；1 FE = 2.5 J）；上游 AttributeEnergy.STORAGE_MULTIPLIER × 单刻耗能（4×）；项目机器缓存倍率 ×4
                .itemSlots()
                .inputTank("left", ResourceTypes.CHEMICAL, 10000L)
                .inputTank("right", ResourceTypes.CHEMICAL, 10000L)
                .outputTank("output", ResourceTypes.CHEMICAL, 10000L)
                .upgrades(Upgrades.ENERGY, Upgrades.MUFFLING, Upgrades.SPEED)
                .build());
        r.machine(MachineBlueprint.builder("mek:precision_sawmill", "精密锯木机")
                .factoryCapable()
                .energy(5L, 720L)   // 上游 UsageConfig.precisionSawmill（J/t；1 FE = 2.5 J）；上游 AttributeEnergy.STORAGE_MULTIPLIER × 单刻耗能（4×），再 × 最大工厂并行数 9（核心按每并行位扣电）；项目机器缓存倍率 ×4
                .itemSlots(ItemSlotSpec.parallelInput("in"), ItemSlotSpec.parallelOutput("out"))
                .upgrades(Upgrades.ENERGY, Upgrades.MUFFLING, Upgrades.SPEED)
                .build());
        r.machine(MachineBlueprint.builder("mek:pressurized_reaction_chamber", "加压反应室")
                .energy(2L, 32L)   // 上游 UsageConfig.pressurizedReactionBase（J/t；1 FE = 2.5 J）；上游 AttributeEnergy.STORAGE_MULTIPLIER × 单刻耗能（4×）；项目机器缓存倍率 ×4
                .itemSlots(ItemSlotSpec.input("in"), ItemSlotSpec.output("out"))
                .inputTank("chemical", ResourceTypes.CHEMICAL, 10000L)
                .inputTank("fluid", ResourceTypes.FLUID, 10000L)
                .outputTank("output", ResourceTypes.CHEMICAL, 10000L)
                .upgrades(Upgrades.ENERGY, Upgrades.MUFFLING, Upgrades.SPEED)
                .build());
        r.machine(MachineBlueprint.builder("mek:purification_chamber", "提纯仓")
                .factoryCapable()
                .energy(10L, 1440L)   // 上游 UsageConfig.purificationChamber（J/t；1 FE = 2.5 J）；上游 AttributeEnergy.STORAGE_MULTIPLIER × 单刻耗能（4×），再 × 最大工厂并行数 9（核心按每并行位扣电）；项目机器缓存倍率 ×4
                .itemSlots(ItemSlotSpec.parallelInput("in"), ItemSlotSpec.parallelOutput("out"))
                .inputTank("gas", ResourceTypes.CHEMICAL, 210L)
                .upgrades(Upgrades.CHEMICAL, Upgrades.ENERGY, Upgrades.MUFFLING, Upgrades.SPEED)
                .build());
        r.machine(MachineBlueprint.builder("mek:qio_dashboard", "QIO 仪表板")
                .itemSlots()
                .noUpgrades()
                .logic(new net.kuzuanpa.cemekanism.storage.QioLogic())
                .build());
        // mek:qio_dashboard：逻辑机器（无配方）——已挂逻辑
        r.machine(MachineBlueprint.builder("mek:qio_drive_array", "QIO 驱动器阵列")
                .itemSlots(ItemSlotSpec.extra("drive0"), ItemSlotSpec.extra("drive1"), ItemSlotSpec.extra("drive2"), ItemSlotSpec.extra("drive3"), ItemSlotSpec.extra("drive4"), ItemSlotSpec.extra("drive5"), ItemSlotSpec.extra("drive6"), ItemSlotSpec.extra("drive7"), ItemSlotSpec.extra("drive8"), ItemSlotSpec.extra("drive9"), ItemSlotSpec.extra("drive10"), ItemSlotSpec.extra("drive11"))
                .noUpgrades()
                .logic(new net.kuzuanpa.cemekanism.storage.QioLogic())
                .build());
        // mek:qio_drive_array：逻辑机器（无配方）——已挂逻辑
        r.machine(MachineBlueprint.builder("mek:qio_exporter", "QIO 输出端口")
                .itemSlots()
                .upgrades(Upgrades.SPEED)
                .logic(new net.kuzuanpa.cemekanism.storage.QioLogic())
                .build());
        // mek:qio_exporter：逻辑机器（无配方）——已挂逻辑
        r.machine(MachineBlueprint.builder("mek:qio_importer", "QIO 输入端口")
                .itemSlots()
                .upgrades(Upgrades.SPEED)
                .logic(new net.kuzuanpa.cemekanism.storage.QioLogic())
                .build());
        // mek:qio_importer：逻辑机器（无配方）——已挂逻辑
        r.machine(MachineBlueprint.builder("mek:qio_redstone_adapter", "QIO 红石适配器")
                .itemSlots()
                .noUpgrades()
                .logic(new net.kuzuanpa.cemekanism.storage.QioLogic())
                .build());
        // mek:qio_redstone_adapter：逻辑机器（无配方）——已挂逻辑
        r.machine(MachineBlueprint.builder("mek:radioactive_waste_barrel", "放射性废料桶")
                .itemSlots()
                .chemicalTank("stored", 512000L)
                .noUpgrades()
                .logic(new net.kuzuanpa.cemekanism.nuclear.WasteBarrelLogic())
                .build());
        // mek:radioactive_waste_barrel：逻辑机器（无配方）——已挂逻辑
        r.machine(MachineBlueprint.builder("mek:resistive_heater", "电阻型加热器")
                .energy(250L, 4000L)   // ；curation.yml 指定基础缓存；项目机器缓存倍率 ×4
                .heat(new HeatSpec(0.1, 1.7976931348623157e308, 0.0))   // 发电机自身温度（热力发电机用；逻辑按条件加/减热）
                .itemSlots()
                .upgrades(Upgrades.MUFFLING)
                .logic(new net.kuzuanpa.cemekanism.machine.logic.ResistiveHeaterLogic())
                .build());
        // mek:resistive_heater：逻辑机器（无配方）——已挂逻辑
        r.machine(MachineBlueprint.builder("mek:rotary_condensentrator", "回旋式气液转换机")
                .exponentialSpeed()
                .energy(2L, 32L)   // 上游 UsageConfig.rotaryCondensentrator（J/t；1 FE = 2.5 J）；上游 AttributeEnergy.STORAGE_MULTIPLIER × 单刻耗能（4×）；项目机器缓存倍率 ×4
                .itemSlots()
                .chemicalTank("chemical", 10000L)
                .fluidTank("fluid", 10000L)
                .upgrades(Upgrades.ENERGY, Upgrades.MUFFLING, Upgrades.SPEED)
                .build());
        r.machine(MachineBlueprint.builder("mek:solar_generator", "太阳能发电机")
                .energyBuffer(96000L)   // 上游 GeneratorsStorageConfig；产出由逻辑写入这个缓存
                .itemSlots()
                .upgrades(Upgrades.MUFFLING)
                .logic(new net.kuzuanpa.cemekanism.machine.logic.SolarGeneratorLogic(false))
                .build());
        // mek:solar_generator：逻辑机器（无配方）——已挂逻辑
        r.machine(MachineBlueprint.builder("mek:solar_neutron_activator", "太阳能中子活化器")
                .exponentialSpeed()
                .itemSlots()
                .inputTank("input", ResourceTypes.CHEMICAL, 10000L)
                .outputTank("output", ResourceTypes.CHEMICAL, 10000L)
                .noUpgrades()
                .gate(new net.kuzuanpa.cemekanism.machine.SunlightGate())
                .build());
        r.machine(MachineBlueprint.builder("mek:ultimate_bin", "终极箱柜")
                .itemSlots(ItemSlotSpec.input("in"), ItemSlotSpec.output("out"))
                .noUpgrades()
                .logic(new net.kuzuanpa.cemekanism.storage.BinLogic())
                .build());
        // mek:ultimate_bin：逻辑机器（无配方）——已挂逻辑
        r.machine(MachineBlueprint.builder("mek:ultimate_chemical_tank", "终极化学品储罐")
                .itemSlots()
                .chemicalTank("stored", 8192000L)
                .noUpgrades()
                .logic(new net.kuzuanpa.cemekanism.storage.PassiveStorageLogic())
                .build());
        // mek:ultimate_chemical_tank：逻辑机器（无配方）——已挂逻辑
        r.machine(MachineBlueprint.builder("mek:ultimate_energy_cube", "终极基础能量立方")
                .energyBuffer(256000000L)   // 上游 GeneratorsStorageConfig；产出由逻辑写入这个缓存
                .itemSlots()
                .noUpgrades()
                .logic(new net.kuzuanpa.cemekanism.storage.PassiveStorageLogic())
                .build());
        // mek:ultimate_energy_cube：逻辑机器（无配方）——已挂逻辑
        r.machine(MachineBlueprint.builder("mek:ultimate_fluid_tank", "终极流体储罐")
                .itemSlots()
                .fluidTank("stored", 256000L)
                .noUpgrades()
                .logic(new net.kuzuanpa.cemekanism.storage.PassiveStorageLogic())
                .build());
        // mek:ultimate_fluid_tank：逻辑机器（无配方）——已挂逻辑
        r.machine(MachineBlueprint.builder("mek:wind_generator", "风力发电机")
                .energyBuffer(200000L)   // 上游 GeneratorsStorageConfig；产出由逻辑写入这个缓存
                .itemSlots()
                .upgrades(Upgrades.MUFFLING)
                .logic(new net.kuzuanpa.cemekanism.machine.logic.WindGeneratorLogic())
                .build());
        // mek:wind_generator：逻辑机器（无配方）——已挂逻辑
    }

    /** 多方块结构（文档 05）：结构规则来自上游 *Validator.java 与 curation.yml，
     *  端口容量是未成型的占位值，成型后由插件按形状 resize；逐刻逻辑在插件侧 multiblock/logic。*/
    public static void multiblocks(Registrars r) {
        r.multiblock(MultiblockBlueprint.builder("mek:thermoelectric_boiler", "热力锅炉",   // main/java/mekanism/common/content/boiler/BoilerValidator.java
                new StructureSpec(new StructureLayout().legend('#', BlockRoles.FRAME).legend('-', BlockRoles.FRAME).legend('.', BlockRoles.INTERIOR_EMPTY).layer("###", "#-#", "###").layer("#-#", "-.-", "#-#").layer("#-#", "-.-", "#-#").layer("###", "#-#", "###"), SizeRule.of(3, 18, 4, 18)).edges(BlockRoles.FRAME, BlockRoles.VALVE).faces(BlockRoles.FRAME, BlockRoles.VALVE, BlockRole.of("heated_valve", "heated_valve"), BlockRole.of("cooled_valve", "cooled_valve"), BlockRoles.GLASS).interior(BlockRole.of("disperser", "分压元件"), BlockRole.of("element", "过热元件"), BlockRoles.INTERIOR_EMPTY)
                .require(RoleRequirement.in(BlockRoles.VALVE, BlockSection.FACE, 1, 9999))
                .require(RoleRequirement.in(BlockRole.of("heated_valve", "heated_valve"), BlockSection.FACE, 1, 9999))
                .require(RoleRequirement.in(BlockRole.of("cooled_valve", "cooled_valve"), BlockSection.FACE, 1, 9999))
                .addPort(new PortDefinition("water", ResourceTypes.FLUID, 16000L, 0L, BlockRoles.VALVE, BlockSection.FACE, 1))
                .addPort(new PortDefinition("steam", ResourceTypes.CHEMICAL, 160000L, 0L, BlockRoles.VALVE, BlockSection.FACE, 1))
                .addPort(new PortDefinition("heated_coolant", ResourceTypes.CHEMICAL, 256000L, 0L, BlockRole.of("heated_valve", "heated_valve"), BlockSection.FACE, 1))
                .addPort(new PortDefinition("cooled_coolant", ResourceTypes.CHEMICAL, 256000L, 0L, BlockRole.of("cooled_valve", "cooled_valve"), BlockSection.FACE, 1))
                )
                .heat(new HeatSpec(0.05, 3000.0, 0.0))
                .logic(new net.kuzuanpa.cemekanism.multiblock.logic.BoilerLogic())
                .build());
        r.multiblock(MultiblockBlueprint.builder("mek:industrial_turbine", "工业蒸汽涡轮",   // generators/java/mekanism/generators/common/content/turbine/TurbineValidator.java
                new StructureSpec(new StructureLayout().legend('#', BlockRoles.FRAME).legend('-', BlockRoles.FRAME).legend('.', BlockRoles.INTERIOR_EMPTY).layer("#####", "#---#", "#---#", "#---#", "#####").layer("#---#", "-...-", "-...-", "-...-", "#---#").layer("#####", "#---#", "#---#", "#---#", "#####"), SizeRule.squareOddBase(5, 17, 3, 18)).edges(BlockRoles.FRAME, BlockRoles.VALVE).faces(BlockRoles.FRAME, BlockRoles.VALVE, BlockRole.of("vent", "排气口"), BlockRoles.GLASS).interior(BlockRole.of("disperser", "分压元件"), BlockRole.of("rotor", "转子"), BlockRole.of("complex", "复杂旋钮装置"), BlockRole.of("coil", "线圈"), BlockRole.of("condenser", "饱和冷凝器"), BlockRoles.INTERIOR_EMPTY)
                .require(RoleRequirement.in(BlockRoles.VALVE, BlockSection.FACE, 1, 9999))
                .require(RoleRequirement.in(BlockRole.of("vent", "排气口"), BlockSection.FACE, 1, 9999))
                .addPort(new PortDefinition("steam", ResourceTypes.CHEMICAL, 64000L, 0L, BlockRoles.VALVE, BlockSection.FACE, 1))
                .addPort(new PortDefinition("energy", ResourceTypes.ENERGY, 0L, 0L, BlockRoles.VALVE, BlockSection.FACE, 1))
                .addPort(new PortDefinition("water", ResourceTypes.FLUID, 0L, 0L, BlockRole.of("vent", "排气口"), BlockSection.FACE, 1))
                )
                .logic(new net.kuzuanpa.cemekanism.multiblock.logic.SteamTurbineLogic())
                .build());
        r.multiblock(MultiblockBlueprint.builder("mek:thermal_evaporation_plant", "热力蒸馏塔",   // main/java/mekanism/common/content/evaporation/EvaporationValidator.java
                new StructureSpec(new StructureLayout().legend('#', BlockRoles.FRAME).legend('-', BlockRoles.FRAME).legend('.', BlockRoles.INTERIOR_EMPTY).layer("####", "#--#", "#--#", "####").layer("#--#", "-..-", "-..-", "#--#").layer("####", "#--#", "#--#", "####"), SizeRule.square(4, 4, 3, 18)).edges(BlockRoles.FRAME, BlockRoles.CONTROLLER).faces(BlockRoles.FRAME, BlockRoles.VALVE, BlockRoles.CONTROLLER, BlockRoles.GLASS).interior(BlockRoles.INTERIOR_EMPTY)
                .require(RoleRequirement.in(BlockRoles.CONTROLLER, null, 1, 1))
                .openTop()
                .addPort(new PortDefinition("water", ResourceTypes.FLUID, 64000L, 0L, BlockRoles.VALVE, BlockSection.FACE, 1))
                .addPort(new PortDefinition("output", ResourceTypes.FLUID, 10000L, 0L, BlockRoles.VALVE, BlockSection.FACE, 1))
                )
                .heat(new HeatSpec(0.1, 3000.0, 0.0))
                .logic(new net.kuzuanpa.cemekanism.multiblock.logic.EvaporationLogic())
                .build());
        r.multiblock(MultiblockBlueprint.builder("mek:dynamic_tank", "动态储罐",   // main/java/mekanism/common/content/tank/TankValidator.java
                new StructureSpec(new StructureLayout().legend('#', BlockRoles.FRAME).legend('-', BlockRoles.FRAME).legend('.', BlockRoles.INTERIOR_EMPTY).layer("###", "#-#", "###").layer("#-#", "-.-", "#-#").layer("###", "#-#", "###"), SizeRule.of(3, 18, 3, 18)).edges(BlockRoles.FRAME, BlockRoles.VALVE).faces(BlockRoles.FRAME, BlockRoles.VALVE, BlockRoles.GLASS).interior(BlockRoles.INTERIOR_EMPTY)
                .require(RoleRequirement.in(BlockRoles.VALVE, BlockSection.FACE, 1, 9999))
                .addPort(new PortDefinition("fluid", ResourceTypes.FLUID, 16000L, 0L, BlockRoles.VALVE, BlockSection.FACE, 1))
                .addPort(new PortDefinition("chemical", ResourceTypes.CHEMICAL, 16000L, 0L, BlockRoles.VALVE, BlockSection.FACE, 1))
                )
                .logic(new net.kuzuanpa.cemekanism.multiblock.logic.DynamicTankLogic())
                .build());
        r.multiblock(MultiblockBlueprint.builder("mek:induction_matrix", "感应矩阵",   // main/java/mekanism/common/content/matrix/MatrixValidator.java
                new StructureSpec(new StructureLayout().legend('#', BlockRoles.FRAME).legend('-', BlockRoles.FRAME).legend('.', BlockRoles.INTERIOR_EMPTY).layer("###", "#-#", "###").layer("#-#", "-.-", "#-#").layer("###", "#-#", "###"), SizeRule.of(3, 18, 3, 18)).edges(BlockRoles.FRAME, BlockRoles.PORT).faces(BlockRoles.FRAME, BlockRoles.PORT, BlockRoles.GLASS).interior(BlockRole.of("cell", "感应元件"), BlockRole.of("provider", "感应供应器"), BlockRoles.INTERIOR_EMPTY)
                .require(RoleRequirement.in(BlockRole.of("cell", "感应元件"), BlockSection.INTERIOR, 1, 9999))
                .require(RoleRequirement.in(BlockRole.of("provider", "感应供应器"), BlockSection.INTERIOR, 1, 9999))
                .addPort(new PortDefinition("energy", ResourceTypes.ENERGY, 0L, 0L, BlockRoles.PORT, BlockSection.FACE, 1))
                )
                .logic(new net.kuzuanpa.cemekanism.multiblock.logic.InductionMatrixLogic())
                .build());
        r.multiblock(MultiblockBlueprint.builder("mek:fission_reactor", "裂变反应堆",   // generators/java/mekanism/generators/common/content/fission/FissionReactorValidator.java
                new StructureSpec(new StructureLayout().legend('#', BlockRoles.FRAME).legend('-', BlockRoles.FRAME).legend('.', BlockRoles.INTERIOR_EMPTY).layer("###", "#-#", "###").layer("#-#", "-.-", "#-#").layer("#-#", "-.-", "#-#").layer("###", "#-#", "###"), SizeRule.of(3, 18, 4, 18)).edges(BlockRoles.FRAME, BlockRoles.PORT).faces(BlockRoles.FRAME, BlockRoles.PORT, BlockRoles.LOGIC_ADAPTER, BlockRoles.GLASS).interior(BlockRole.of("fuel", "燃料组件"), BlockRole.of("control_rod", "控制棒"), BlockRoles.INTERIOR_EMPTY)
                .require(RoleRequirement.in(BlockRoles.LOGIC_ADAPTER, null, 0, 9999))
                .addPort(new PortDefinition("fuel", ResourceTypes.CHEMICAL, 0L, 0L, BlockRoles.PORT, BlockSection.FACE, 1))
                .addPort(new PortDefinition("water", ResourceTypes.FLUID, 100000L, 0L, BlockRoles.PORT, BlockSection.FACE, 1))
                .addPort(new PortDefinition("waste", ResourceTypes.CHEMICAL, 0L, 0L, BlockRoles.PORT, BlockSection.FACE, 1))
                .addPort(new PortDefinition("steam", ResourceTypes.CHEMICAL, 1000000L, 0L, BlockRoles.PORT, BlockSection.FACE, 1))
                )
                .heat(new HeatSpec(1.0, 1000000000000.0, 0.0))
                .logic(new net.kuzuanpa.cemekanism.multiblock.logic.FissionReactorLogic())
                .build());
        r.multiblock(MultiblockBlueprint.builder("mek:fusion_reactor", "聚变反应堆",   // generators/java/mekanism/generators/common/content/fusion/FusionReactorValidator.java
                new StructureSpec(new StructureLayout().legend('#', BlockRoles.FRAME).legend('s', BlockRoles.FRAME, BlockRoles.PORT, BlockRoles.CONTROLLER, BlockRoles.LOGIC_ADAPTER, BlockRole.of("laser_focus", "激光聚焦矩阵"), BlockRoles.GLASS).legend('.', BlockRoles.INTERIOR_EMPTY).legend('o', BlockRoles.ANY).layer("oo#oo", "o#s#o", "#sss#", "o#s#o", "oo#oo").layer("o#s#o", "#...#", "s...s", "#...#", "o#s#o").layer("#sss#", "s...s", "s...s", "s...s", "#sss#").layer("o#s#o", "#...#", "s...s", "#...#", "o#s#o").layer("oo#oo", "o#s#o", "#sss#", "o#s#o", "oo#oo"), SizeRule.of(5, 5, 5, 5)).fixedLayout(new StructureLayout().legend('#', BlockRoles.FRAME).legend('s', BlockRoles.FRAME, BlockRoles.PORT, BlockRoles.CONTROLLER, BlockRoles.LOGIC_ADAPTER, BlockRole.of("laser_focus", "激光聚焦矩阵"), BlockRoles.GLASS).legend('.', BlockRoles.INTERIOR_EMPTY).legend('o', BlockRoles.ANY).layer("oo#oo", "o#s#o", "#sss#", "o#s#o", "oo#oo").layer("o#s#o", "#...#", "s...s", "#...#", "o#s#o").layer("#sss#", "s...s", "s...s", "s...s", "#sss#").layer("o#s#o", "#...#", "s...s", "#...#", "o#s#o").layer("oo#oo", "o#s#o", "#sss#", "o#s#o", "oo#oo"))
                .require(RoleRequirement.in(BlockRoles.CONTROLLER, null, 1, 1))
                .addPort(new PortDefinition("deuterium", ResourceTypes.CHEMICAL, 1000L, 0L, BlockRoles.PORT, BlockSection.FACE, 1))
                .addPort(new PortDefinition("tritium", ResourceTypes.CHEMICAL, 1000L, 0L, BlockRoles.PORT, BlockSection.FACE, 1))
                .addPort(new PortDefinition("fuel", ResourceTypes.CHEMICAL, 1000L, 0L, BlockRoles.PORT, BlockSection.FACE, 1))
                .addPort(new PortDefinition("laser_energy", ResourceTypes.ENERGY, 1000000L, 0L, BlockRole.of("laser_focus", "激光聚焦矩阵"), BlockSection.FACE, 1))
                .addPort(new PortDefinition("energy", ResourceTypes.ENERGY, 1000000000L, 0L, BlockRoles.PORT, BlockSection.FACE, 1))
                .addPort(new PortDefinition("water", ResourceTypes.FLUID, 0L, 0L, BlockRoles.PORT, BlockSection.FACE, 1))
                .addPort(new PortDefinition("steam", ResourceTypes.CHEMICAL, 0L, 0L, BlockRoles.PORT, BlockSection.FACE, 1))
                )
                .heat(new HeatSpec(0.001, 1000000000000.0, 0.0))
                .logic(new net.kuzuanpa.cemekanism.multiblock.logic.FusionReactorLogic())
                .build());
        r.multiblock(MultiblockBlueprint.builder("mek:sps", "超临界移相器",   // main/java/mekanism/common/content/sps/SPSValidator.java
                new StructureSpec(new StructureLayout().legend('#', BlockRoles.FRAME).legend('s', BlockRoles.FRAME, BlockRoles.PORT, BlockRoles.GLASS).legend('.', BlockRoles.INTERIOR_EMPTY).legend('o', BlockRoles.ANY).layer("oo###oo", "o#sss#o", "#sssss#", "#sssss#", "#sssss#", "o#sss#o", "oo###oo").layer("o#sss#o", "#.....#", "s.....s", "s.....s", "s.....s", "#.....#", "o#sss#o").layer("#sssss#", "s.....s", "s.....s", "s.....s", "s.....s", "s.....s", "#sssss#").layer("#sssss#", "s.....s", "s.....s", "s.....s", "s.....s", "s.....s", "#sssss#").layer("#sssss#", "s.....s", "s.....s", "s.....s", "s.....s", "s.....s", "#sssss#").layer("o#sss#o", "#.....#", "s.....s", "s.....s", "s.....s", "#.....#", "o#sss#o").layer("oo###oo", "o#sss#o", "#sssss#", "#sssss#", "#sssss#", "o#sss#o", "oo###oo"), SizeRule.of(7, 7, 7, 7)).fixedLayout(new StructureLayout().legend('#', BlockRoles.FRAME).legend('s', BlockRoles.FRAME, BlockRoles.PORT, BlockRoles.GLASS).legend('.', BlockRoles.INTERIOR_EMPTY).legend('o', BlockRoles.ANY).layer("oo###oo", "o#sss#o", "#sssss#", "#sssss#", "#sssss#", "o#sss#o", "oo###oo").layer("o#sss#o", "#.....#", "s.....s", "s.....s", "s.....s", "#.....#", "o#sss#o").layer("#sssss#", "s.....s", "s.....s", "s.....s", "s.....s", "s.....s", "#sssss#").layer("#sssss#", "s.....s", "s.....s", "s.....s", "s.....s", "s.....s", "#sssss#").layer("#sssss#", "s.....s", "s.....s", "s.....s", "s.....s", "s.....s", "#sssss#").layer("o#sss#o", "#.....#", "s.....s", "s.....s", "s.....s", "#.....#", "o#sss#o").layer("oo###oo", "o#sss#o", "#sssss#", "#sssss#", "#sssss#", "o#sss#o", "oo###oo")).interior(BlockRole.of("coil", "线圈"), BlockRoles.INTERIOR_EMPTY)
                .addPort(new PortDefinition("polonium", ResourceTypes.CHEMICAL, 2000L, 0L, BlockRoles.PORT, BlockSection.FACE, 1))
                .addPort(new PortDefinition("antimatter", ResourceTypes.CHEMICAL, 1000L, 0L, BlockRoles.PORT, BlockSection.FACE, 1))
                .addPort(new PortDefinition("energy", ResourceTypes.ENERGY, 1000000000L, 0L, BlockRole.of("coil", "线圈"), BlockSection.INTERIOR, 1))
                )
                .logic(new net.kuzuanpa.cemekanism.multiblock.logic.SpsLogic())
                .build());
        r.multiblock(MultiblockBlueprint.builder("mek:teleporter", "传送器",   // curation
                new StructureSpec(new StructureLayout().legend('#', BlockRoles.FRAME).legend('-', BlockRoles.INTERIOR_EMPTY).legend('.', BlockRoles.INTERIOR_EMPTY).layer("###"), SizeRule.of(1, 23, 1, 23)).edges(BlockRoles.FRAME, BlockRoles.CONTROLLER).faces(BlockRoles.INTERIOR_EMPTY).interior(BlockRoles.INTERIOR_EMPTY)
                .require(RoleRequirement.in(BlockRoles.CONTROLLER, null, 1, 1))
                .addPort(new PortDefinition("energy", ResourceTypes.ENERGY, 1000000L, 0L, BlockRoles.CONTROLLER, BlockSection.EDGE, 1))
                )
                .logic(new net.kuzuanpa.cemekanism.multiblock.logic.TeleporterLogic())
                .build());
    }

    /** 结构族（世界侧用：尺寸规则、锚点、端口名、check 清单、方块 → 角色）。*/
    public static final List<MultiblockDef> MULTIBLOCKS = List.of(
            new MultiblockDef("mek:thermoelectric_boiler", "boiler", "热力锅炉", 3, 18, 4, 18, false, false, "sections", "mek:boiler_valve", 0, 0, 0, List.of("disperser_plane", "element_below"), List.of("water", "steam", "heated_coolant", "cooled_coolant"), Map.ofEntries(Map.entry("mek:boiler_casing", "frame"), Map.entry("mek:boiler_valve", "valve"), Map.entry("mek:pressure_disperser", "disperser"), Map.entry("mek:reactor_glass", "glass"), Map.entry("mek:structural_glass", "glass"), Map.entry("mek:superheating_element", "element")), Map.ofEntries(Map.entry("cooled_valve", "mek:boiler_valve"), Map.entry("disperser", "mek:pressure_disperser"), Map.entry("element", "mek:superheating_element"), Map.entry("frame", "mek:boiler_casing"), Map.entry("glass", "mek:structural_glass"), Map.entry("heated_valve", "mek:boiler_valve"), Map.entry("valve", "mek:boiler_valve")), Map.ofEntries(Map.entry("cooled_coolant", "volume:256000"), Map.entry("heated_coolant", "volume:256000"), Map.entry("steam", "volume:160000"), Map.entry("water", "volume:16000")), Map.ofEntries(Map.entry("cooled_coolant", "fixed:0"), Map.entry("heated_coolant", "fixed:0"), Map.entry("steam", "fixed:0"), Map.entry("water", "fixed:0")), Map.ofEntries(Map.entry("cooled_coolant", "in"), Map.entry("heated_coolant", "out"), Map.entry("steam", "out"), Map.entry("water", "in")), Map.ofEntries(Map.entry("cooled_coolant", "cooled_valve"), Map.entry("heated_coolant", "heated_valve"), Map.entry("steam", "valve"), Map.entry("water", "valve")), Map.ofEntries(Map.entry("cooled_coolant", "face"), Map.entry("heated_coolant", "face"), Map.entry("steam", "face"), Map.entry("water", "face")), Map.ofEntries(Map.entry("cooled_coolant", 1), Map.entry("heated_coolant", 1), Map.entry("steam", 1), Map.entry("water", 1)), Map.ofEntries(Map.entry("cooled_coolant", ""), Map.entry("heated_coolant", ""), Map.entry("steam", ""), Map.entry("water", ""))),
            new MultiblockDef("mek:industrial_turbine", "turbine", "工业蒸汽涡轮", 5, 17, 3, 18, true, true, "sections", "mek:turbine_valve", 0, 0, 0, List.of("rotor_column", "coil_above_complex", "condenser_above_complex", "vent_above_complex", "disperser_plane"), List.of("steam", "energy", "water"), Map.ofEntries(Map.entry("mek:electromagnetic_coil", "coil"), Map.entry("mek:pressure_disperser", "disperser"), Map.entry("mek:reactor_glass", "glass"), Map.entry("mek:rotational_complex", "complex"), Map.entry("mek:saturating_condenser", "condenser"), Map.entry("mek:structural_glass", "glass"), Map.entry("mek:turbine_casing", "frame"), Map.entry("mek:turbine_rotor", "rotor"), Map.entry("mek:turbine_valve", "valve"), Map.entry("mek:turbine_vent", "vent")), Map.ofEntries(Map.entry("coil", "mek:electromagnetic_coil"), Map.entry("complex", "mek:rotational_complex"), Map.entry("condenser", "mek:saturating_condenser"), Map.entry("disperser", "mek:pressure_disperser"), Map.entry("frame", "mek:turbine_casing"), Map.entry("glass", "mek:structural_glass"), Map.entry("rotor", "mek:turbine_rotor"), Map.entry("valve", "mek:turbine_valve"), Map.entry("vent", "mek:turbine_vent")), Map.ofEntries(Map.entry("energy", "total_volume:16000000"), Map.entry("steam", "lower_volume:64000"), Map.entry("water", "condensers:64000")), Map.ofEntries(Map.entry("energy", "fixed:0"), Map.entry("steam", "fixed:0"), Map.entry("water", "fixed:0")), Map.ofEntries(Map.entry("energy", "out"), Map.entry("steam", "in"), Map.entry("water", "out")), Map.ofEntries(Map.entry("energy", "valve"), Map.entry("steam", "valve"), Map.entry("water", "vent")), Map.ofEntries(Map.entry("energy", "face"), Map.entry("steam", "face"), Map.entry("water", "face")), Map.ofEntries(Map.entry("energy", 1), Map.entry("steam", 1), Map.entry("water", 1)), Map.ofEntries(Map.entry("energy", ""), Map.entry("steam", ""), Map.entry("water", ""))),
            new MultiblockDef("mek:thermal_evaporation_plant", "evaporation", "热力蒸馏塔", 4, 4, 3, 18, true, false, "sections", "mek:thermal_evaporation_controller", 0, 0, 0, List.of("top_layer_edges"), List.of("water", "output"), Map.ofEntries(Map.entry("mek:reactor_glass", "glass"), Map.entry("mek:structural_glass", "glass"), Map.entry("mek:thermal_evaporation_block", "frame"), Map.entry("mek:thermal_evaporation_controller", "controller"), Map.entry("mek:thermal_evaporation_valve", "valve")), Map.ofEntries(Map.entry("controller", "mek:thermal_evaporation_controller"), Map.entry("frame", "mek:thermal_evaporation_block"), Map.entry("glass", "mek:structural_glass"), Map.entry("valve", "mek:thermal_evaporation_valve")), Map.ofEntries(Map.entry("output", "fixed:10000"), Map.entry("water", "volume:64000")), Map.ofEntries(Map.entry("output", "fixed:0"), Map.entry("water", "fixed:0")), Map.ofEntries(Map.entry("output", "out"), Map.entry("water", "in")), Map.ofEntries(Map.entry("output", "valve"), Map.entry("water", "valve")), Map.ofEntries(Map.entry("output", "face"), Map.entry("water", "face")), Map.ofEntries(Map.entry("output", 1), Map.entry("water", 1)), Map.ofEntries(Map.entry("output", ""), Map.entry("water", ""))),
            new MultiblockDef("mek:dynamic_tank", "tank", "动态储罐", 3, 18, 3, 18, false, false, "sections", "mek:dynamic_valve", 0, 0, 0, List.of(), List.of("fluid", "chemical"), Map.ofEntries(Map.entry("mek:dynamic_tank", "frame"), Map.entry("mek:dynamic_valve", "valve"), Map.entry("mek:reactor_glass", "glass"), Map.entry("mek:structural_glass", "glass")), Map.ofEntries(Map.entry("frame", "mek:dynamic_tank"), Map.entry("glass", "mek:structural_glass"), Map.entry("valve", "mek:dynamic_valve")), Map.ofEntries(Map.entry("chemical", "volume:16000"), Map.entry("fluid", "volume:16000")), Map.ofEntries(Map.entry("chemical", "fixed:0"), Map.entry("fluid", "fixed:0")), Map.ofEntries(Map.entry("chemical", "both"), Map.entry("fluid", "both")), Map.ofEntries(Map.entry("chemical", "valve"), Map.entry("fluid", "valve")), Map.ofEntries(Map.entry("chemical", "face"), Map.entry("fluid", "face")), Map.ofEntries(Map.entry("chemical", 1), Map.entry("fluid", 1)), Map.ofEntries(Map.entry("chemical", ""), Map.entry("fluid", ""))),
            new MultiblockDef("mek:induction_matrix", "matrix", "感应矩阵", 3, 18, 3, 18, false, false, "sections", "mek:induction_port", 0, 0, 0, List.of("cell_and_provider"), List.of("energy"), Map.ofEntries(Map.entry("mek:advanced_induction_cell", "cell"), Map.entry("mek:advanced_induction_provider", "provider"), Map.entry("mek:basic_induction_cell", "cell"), Map.entry("mek:basic_induction_provider", "provider"), Map.entry("mek:elite_induction_cell", "cell"), Map.entry("mek:elite_induction_provider", "provider"), Map.entry("mek:induction_casing", "frame"), Map.entry("mek:induction_port", "port"), Map.entry("mek:reactor_glass", "glass"), Map.entry("mek:structural_glass", "glass"), Map.entry("mek:ultimate_induction_cell", "cell"), Map.entry("mek:ultimate_induction_provider", "provider")), Map.ofEntries(Map.entry("cell", "mek:basic_induction_cell"), Map.entry("frame", "mek:induction_casing"), Map.entry("glass", "mek:structural_glass"), Map.entry("port", "mek:induction_port"), Map.entry("provider", "mek:basic_induction_provider")), Map.ofEntries(Map.entry("energy", "cells")), Map.ofEntries(Map.entry("energy", "providers")), Map.ofEntries(Map.entry("energy", "store")), Map.ofEntries(Map.entry("energy", "port")), Map.ofEntries(Map.entry("energy", "face")), Map.ofEntries(Map.entry("energy", 1)), Map.ofEntries(Map.entry("energy", ""))),
            new MultiblockDef("mek:fission_reactor", "fission_reactor", "裂变反应堆", 3, 18, 4, 18, false, false, "sections", "mek:fission_reactor_port", 0, 0, 0, List.of("assembly_columns"), List.of("fuel", "water", "waste", "steam"), Map.ofEntries(Map.entry("mek:control_rod_assembly", "control_rod"), Map.entry("mek:fission_fuel_assembly", "fuel"), Map.entry("mek:fission_reactor_casing", "frame"), Map.entry("mek:fission_reactor_logic_adapter", "logic_adapter"), Map.entry("mek:fission_reactor_port", "port"), Map.entry("mek:reactor_glass", "glass")), Map.ofEntries(Map.entry("control_rod", "mek:control_rod_assembly"), Map.entry("frame", "mek:fission_reactor_casing"), Map.entry("fuel", "mek:fission_fuel_assembly"), Map.entry("glass", "mek:reactor_glass"), Map.entry("logic_adapter", "mek:fission_reactor_logic_adapter"), Map.entry("port", "mek:fission_reactor_port")), Map.ofEntries(Map.entry("fuel", "assemblies:8000"), Map.entry("steam", "volume:1000000"), Map.entry("waste", "assemblies:8000"), Map.entry("water", "volume:100000")), Map.ofEntries(Map.entry("fuel", "fixed:0"), Map.entry("steam", "fixed:0"), Map.entry("waste", "fixed:0"), Map.entry("water", "fixed:0")), Map.ofEntries(Map.entry("fuel", "in"), Map.entry("steam", "out"), Map.entry("waste", "out"), Map.entry("water", "in")), Map.ofEntries(Map.entry("fuel", "port"), Map.entry("steam", "port"), Map.entry("waste", "port"), Map.entry("water", "port")), Map.ofEntries(Map.entry("fuel", "face"), Map.entry("steam", "face"), Map.entry("waste", "face"), Map.entry("water", "face")), Map.ofEntries(Map.entry("fuel", 1), Map.entry("steam", 1), Map.entry("waste", 1), Map.entry("water", 1)), Map.ofEntries(Map.entry("fuel", "INPUT"), Map.entry("steam", "OUTPUT_COOLANT"), Map.entry("waste", "OUTPUT_WASTE"), Map.entry("water", "INPUT"))),
            new MultiblockDef("mek:fusion_reactor", "fusion_reactor", "聚变反应堆", 5, 5, 5, 5, false, false, "fixed", "mek:fusion_reactor_controller", 2, 4, 2, List.of("fusion_centers"), List.of("deuterium", "tritium", "fuel", "laser_energy", "energy", "water", "steam"), Map.ofEntries(Map.entry("mek:fusion_reactor_controller", "controller"), Map.entry("mek:fusion_reactor_frame", "frame"), Map.entry("mek:fusion_reactor_logic_adapter", "logic_adapter"), Map.entry("mek:fusion_reactor_port", "port"), Map.entry("mek:laser_focus_matrix", "laser_focus"), Map.entry("mek:reactor_glass", "glass")), Map.ofEntries(Map.entry("controller", "mek:fusion_reactor_controller"), Map.entry("frame", "mek:fusion_reactor_frame"), Map.entry("glass", "mek:reactor_glass"), Map.entry("laser_focus", "mek:laser_focus_matrix"), Map.entry("logic_adapter", "mek:fusion_reactor_logic_adapter"), Map.entry("port", "mek:fusion_reactor_port")), Map.ofEntries(Map.entry("deuterium", "fixed:1000"), Map.entry("energy", "fixed:1000000000"), Map.entry("fuel", "fixed:1000"), Map.entry("laser_energy", "fixed:1000000"), Map.entry("steam", "injection:100000000"), Map.entry("tritium", "fixed:1000"), Map.entry("water", "injection:1000000")), Map.ofEntries(Map.entry("deuterium", "fixed:0"), Map.entry("energy", "fixed:0"), Map.entry("fuel", "fixed:0"), Map.entry("laser_energy", "fixed:0"), Map.entry("steam", "fixed:0"), Map.entry("tritium", "fixed:0"), Map.entry("water", "fixed:0")), Map.ofEntries(Map.entry("deuterium", "in"), Map.entry("energy", "out"), Map.entry("fuel", "in"), Map.entry("laser_energy", "in"), Map.entry("steam", "out"), Map.entry("tritium", "in"), Map.entry("water", "in")), Map.ofEntries(Map.entry("deuterium", "port"), Map.entry("energy", "port"), Map.entry("fuel", "port"), Map.entry("laser_energy", "laser_focus"), Map.entry("steam", "port"), Map.entry("tritium", "port"), Map.entry("water", "port")), Map.ofEntries(Map.entry("deuterium", "face"), Map.entry("energy", "face"), Map.entry("fuel", "face"), Map.entry("laser_energy", "face"), Map.entry("steam", "face"), Map.entry("tritium", "face"), Map.entry("water", "face")), Map.ofEntries(Map.entry("deuterium", 1), Map.entry("energy", 1), Map.entry("fuel", 1), Map.entry("laser_energy", 1), Map.entry("steam", 1), Map.entry("tritium", 1), Map.entry("water", 1)), Map.ofEntries(Map.entry("deuterium", "INPUT"), Map.entry("energy", "OUTPUT"), Map.entry("fuel", "INPUT"), Map.entry("laser_energy", ""), Map.entry("steam", "OUTPUT"), Map.entry("tritium", "INPUT"), Map.entry("water", "INPUT"))),
            new MultiblockDef("mek:sps", "sps", "超临界移相器", 7, 7, 7, 7, false, false, "fixed", "mek:sps_port", 0, 0, 0, List.of("coil_at_port"), List.of("polonium", "antimatter", "energy"), Map.ofEntries(Map.entry("mek:reactor_glass", "glass"), Map.entry("mek:sps_casing", "frame"), Map.entry("mek:sps_port", "port"), Map.entry("mek:structural_glass", "glass"), Map.entry("mek:supercharged_coil", "coil")), Map.ofEntries(Map.entry("coil", "mek:supercharged_coil"), Map.entry("frame", "mek:sps_casing"), Map.entry("glass", "mek:structural_glass"), Map.entry("port", "mek:sps_port")), Map.ofEntries(Map.entry("antimatter", "fixed:1000"), Map.entry("energy", "fixed:1000000000"), Map.entry("polonium", "fixed:2000")), Map.ofEntries(Map.entry("antimatter", "fixed:0"), Map.entry("energy", "fixed:0"), Map.entry("polonium", "fixed:0")), Map.ofEntries(Map.entry("antimatter", "out"), Map.entry("energy", "in"), Map.entry("polonium", "in")), Map.ofEntries(Map.entry("antimatter", "port"), Map.entry("energy", "coil"), Map.entry("polonium", "port")), Map.ofEntries(Map.entry("antimatter", "face"), Map.entry("energy", "interior"), Map.entry("polonium", "face")), Map.ofEntries(Map.entry("antimatter", 1), Map.entry("energy", 1), Map.entry("polonium", 1)), Map.ofEntries(Map.entry("antimatter", "OUTPUT"), Map.entry("energy", ""), Map.entry("polonium", "INPUT"))),
            new MultiblockDef("mek:teleporter", "teleporter", "传送器", 1, 23, 1, 23, false, false, "sections", "mek:teleporter", 1, 0, 0, List.of("teleporter_frame"), List.of("energy"), Map.ofEntries(Map.entry("mek:teleporter", "controller"), Map.entry("mek:teleporter_frame", "frame")), Map.ofEntries(Map.entry("controller", "mek:teleporter"), Map.entry("frame", "mek:teleporter_frame")), Map.ofEntries(Map.entry("energy", "fixed:1000000")), Map.ofEntries(Map.entry("energy", "fixed:0")), Map.ofEntries(Map.entry("energy", "in")), Map.ofEntries(Map.entry("energy", "controller")), Map.ofEntries(Map.entry("energy", "edge")), Map.ofEntries(Map.entry("energy", 1)), Map.ofEntries(Map.entry("energy", ""))));

    /** 结构件方块 → 结构族（分类器与自检用；同一个方块可以在多个族里当同一角色）。*/
    public static final Map<String, String> BLOCK_TO_MULTIBLOCK = Map.ofEntries(
            Map.entry("mek:advanced_induction_cell", "matrix"),
            Map.entry("mek:advanced_induction_provider", "matrix"),
            Map.entry("mek:basic_induction_cell", "matrix"),
            Map.entry("mek:basic_induction_provider", "matrix"),
            Map.entry("mek:boiler_casing", "boiler"),
            Map.entry("mek:boiler_valve", "boiler"),
            Map.entry("mek:control_rod_assembly", "fission_reactor"),
            Map.entry("mek:dynamic_tank", "tank"),
            Map.entry("mek:dynamic_valve", "tank"),
            Map.entry("mek:electromagnetic_coil", "turbine"),
            Map.entry("mek:elite_induction_cell", "matrix"),
            Map.entry("mek:elite_induction_provider", "matrix"),
            Map.entry("mek:fission_fuel_assembly", "fission_reactor"),
            Map.entry("mek:fission_reactor_casing", "fission_reactor"),
            Map.entry("mek:fission_reactor_logic_adapter", "fission_reactor"),
            Map.entry("mek:fission_reactor_port", "fission_reactor"),
            Map.entry("mek:fusion_reactor_controller", "fusion_reactor"),
            Map.entry("mek:fusion_reactor_frame", "fusion_reactor"),
            Map.entry("mek:fusion_reactor_logic_adapter", "fusion_reactor"),
            Map.entry("mek:fusion_reactor_port", "fusion_reactor"),
            Map.entry("mek:induction_casing", "matrix"),
            Map.entry("mek:induction_port", "matrix"),
            Map.entry("mek:laser_focus_matrix", "fusion_reactor"),
            Map.entry("mek:pressure_disperser", "boiler"),
            Map.entry("mek:reactor_glass", "boiler"),
            Map.entry("mek:rotational_complex", "turbine"),
            Map.entry("mek:saturating_condenser", "turbine"),
            Map.entry("mek:sps_casing", "sps"),
            Map.entry("mek:sps_port", "sps"),
            Map.entry("mek:structural_glass", "boiler"),
            Map.entry("mek:supercharged_coil", "sps"),
            Map.entry("mek:superheating_element", "boiler"),
            Map.entry("mek:teleporter", "teleporter"),
            Map.entry("mek:teleporter_frame", "teleporter"),
            Map.entry("mek:thermal_evaporation_block", "evaporation"),
            Map.entry("mek:thermal_evaporation_controller", "evaporation"),
            Map.entry("mek:thermal_evaporation_valve", "evaporation"),
            Map.entry("mek:turbine_casing", "turbine"),
            Map.entry("mek:turbine_rotor", "turbine"),
            Map.entry("mek:turbine_valve", "turbine"),
            Map.entry("mek:turbine_vent", "turbine"),
            Map.entry("mek:ultimate_induction_cell", "matrix"),
            Map.entry("mek:ultimate_induction_provider", "matrix"));

    /** 机器配方：数据来自上游 datagen 的 recipe 目录，耗时取机器自己的基准刻数；
     *  标签成员按 02 第七节 D5 的口径登记，落法与 CE 侧一致。*/
    public static void recipes(Registrars r) {
        recipes0(r);
        recipes1(r);
        recipes2(r);
        recipes3(r);
        recipes4(r);
        recipes5(r);
        recipes6(r);
        recipes7(r);
        recipes8(r);
        recipes9(r);
        recipeTags(r);
    }

    private static void recipes0(Registrars r) {
        r.recipe(Recipe.builder("mek:energized_smelter/baked_potato", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:potato"))
                    .output(RecipeOutput.item("minecraft:baked_potato", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/black_glazed_terracotta", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:black_terracotta"))
                    .output(RecipeOutput.item("minecraft:black_glazed_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/blue_glazed_terracotta", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:blue_terracotta"))
                    .output(RecipeOutput.item("minecraft:blue_glazed_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/brick", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:clay_ball"))
                    .output(RecipeOutput.item("minecraft:brick", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/brown_glazed_terracotta", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:brown_terracotta"))
                    .output(RecipeOutput.item("minecraft:brown_glazed_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/charcoal", "mek:energized_smelter")
                    .input(ItemIngredient.tagged(1L, "mek:logs_that_burn"))
                    .output(RecipeOutput.item("minecraft:charcoal", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/coal_from_smelting_coal_ore", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:coal_ore"))
                    .output(RecipeOutput.item("minecraft:coal", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/coal_from_smelting_deepslate_coal_ore", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:deepslate_coal_ore"))
                    .output(RecipeOutput.item("minecraft:coal", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/cooked_beef", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:beef"))
                    .output(RecipeOutput.item("minecraft:cooked_beef", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/cooked_chicken", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:chicken"))
                    .output(RecipeOutput.item("minecraft:cooked_chicken", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/cooked_cod", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:cod"))
                    .output(RecipeOutput.item("minecraft:cooked_cod", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/cooked_mutton", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:mutton"))
                    .output(RecipeOutput.item("minecraft:cooked_mutton", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/cooked_porkchop", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:porkchop"))
                    .output(RecipeOutput.item("minecraft:cooked_porkchop", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/cooked_rabbit", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:rabbit"))
                    .output(RecipeOutput.item("minecraft:cooked_rabbit", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/cooked_salmon", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:salmon"))
                    .output(RecipeOutput.item("minecraft:cooked_salmon", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/copper_ingot_from_smelting_copper_ore", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:copper_ore"))
                    .output(RecipeOutput.item("minecraft:copper_ingot", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/copper_ingot_from_smelting_deepslate_copper_ore", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:deepslate_copper_ore"))
                    .output(RecipeOutput.item("minecraft:copper_ingot", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/copper_ingot_from_smelting_raw_copper", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:raw_copper"))
                    .output(RecipeOutput.item("minecraft:copper_ingot", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/copper_nugget_from_smelting", "mek:energized_smelter")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("minecraft:copper_axe"), ItemMatcher.id("minecraft:copper_boots"), ItemMatcher.id("minecraft:copper_chestplate"), ItemMatcher.id("minecraft:copper_helmet"), ItemMatcher.id("minecraft:copper_hoe"), ItemMatcher.id("minecraft:copper_horse_armor"), ItemMatcher.id("minecraft:copper_leggings"), ItemMatcher.id("minecraft:copper_nautilus_armor"), ItemMatcher.id("minecraft:copper_pickaxe"), ItemMatcher.id("minecraft:copper_shovel"), ItemMatcher.id("minecraft:copper_spear"), ItemMatcher.id("minecraft:copper_sword")), "minecraft:copper_axe"))
                    .output(RecipeOutput.item("minecraft:copper_nugget", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/cracked_deepslate_bricks", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:deepslate_bricks"))
                    .output(RecipeOutput.item("minecraft:cracked_deepslate_bricks", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/cracked_deepslate_tiles", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:deepslate_tiles"))
                    .output(RecipeOutput.item("minecraft:cracked_deepslate_tiles", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/cracked_nether_bricks", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:nether_bricks"))
                    .output(RecipeOutput.item("minecraft:cracked_nether_bricks", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/cracked_polished_blackstone_bricks", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:polished_blackstone_bricks"))
                    .output(RecipeOutput.item("minecraft:cracked_polished_blackstone_bricks", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/cracked_stone_bricks", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:stone_bricks"))
                    .output(RecipeOutput.item("minecraft:cracked_stone_bricks", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/cyan_glazed_terracotta", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:cyan_terracotta"))
                    .output(RecipeOutput.item("minecraft:cyan_glazed_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/deepslate", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:cobbled_deepslate"))
                    .output(RecipeOutput.item("minecraft:deepslate", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/diamond_from_smelting_deepslate_diamond_ore", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:deepslate_diamond_ore"))
                    .output(RecipeOutput.item("minecraft:diamond", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/diamond_from_smelting_diamond_ore", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:diamond_ore"))
                    .output(RecipeOutput.item("minecraft:diamond", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/dried_kelp_from_smelting", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:kelp"))
                    .output(RecipeOutput.item("minecraft:dried_kelp", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/emerald_from_smelting_deepslate_emerald_ore", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:deepslate_emerald_ore"))
                    .output(RecipeOutput.item("minecraft:emerald", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/emerald_from_smelting_emerald_ore", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:emerald_ore"))
                    .output(RecipeOutput.item("minecraft:emerald", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/glass", "mek:energized_smelter")
                    .input(ItemIngredient.tagged(1L, "mek:smelts_to_glass"))
                    .output(RecipeOutput.item("minecraft:glass", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/gold_ingot_from_smelting_deepslate_gold_ore", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:deepslate_gold_ore"))
                    .output(RecipeOutput.item("minecraft:gold_ingot", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/gold_ingot_from_smelting_gold_ore", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:gold_ore"))
                    .output(RecipeOutput.item("minecraft:gold_ingot", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/gold_ingot_from_smelting_nether_gold_ore", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:nether_gold_ore"))
                    .output(RecipeOutput.item("minecraft:gold_ingot", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/gold_ingot_from_smelting_raw_gold", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:raw_gold"))
                    .output(RecipeOutput.item("minecraft:gold_ingot", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/gold_nugget_from_smelting", "mek:energized_smelter")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("minecraft:golden_axe"), ItemMatcher.id("minecraft:golden_boots"), ItemMatcher.id("minecraft:golden_chestplate"), ItemMatcher.id("minecraft:golden_helmet"), ItemMatcher.id("minecraft:golden_hoe"), ItemMatcher.id("minecraft:golden_horse_armor"), ItemMatcher.id("minecraft:golden_leggings"), ItemMatcher.id("minecraft:golden_nautilus_armor"), ItemMatcher.id("minecraft:golden_pickaxe"), ItemMatcher.id("minecraft:golden_shovel"), ItemMatcher.id("minecraft:golden_spear"), ItemMatcher.id("minecraft:golden_sword")), "minecraft:golden_axe"))
                    .output(RecipeOutput.item("minecraft:gold_nugget", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/gray_glazed_terracotta", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:gray_terracotta"))
                    .output(RecipeOutput.item("minecraft:gray_glazed_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/green_dye", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:cactus"))
                    .output(RecipeOutput.item("minecraft:green_dye", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/green_glazed_terracotta", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:green_terracotta"))
                    .output(RecipeOutput.item("minecraft:green_glazed_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/iron_ingot_from_smelting_deepslate_iron_ore", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:deepslate_iron_ore"))
                    .output(RecipeOutput.item("minecraft:iron_ingot", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/iron_ingot_from_smelting_iron_ore", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:iron_ore"))
                    .output(RecipeOutput.item("minecraft:iron_ingot", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/iron_ingot_from_smelting_raw_iron", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:raw_iron"))
                    .output(RecipeOutput.item("minecraft:iron_ingot", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/iron_nugget_from_smelting", "mek:energized_smelter")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("minecraft:chainmail_boots"), ItemMatcher.id("minecraft:chainmail_chestplate"), ItemMatcher.id("minecraft:chainmail_helmet"), ItemMatcher.id("minecraft:chainmail_leggings"), ItemMatcher.id("minecraft:iron_axe"), ItemMatcher.id("minecraft:iron_boots"), ItemMatcher.id("minecraft:iron_chestplate"), ItemMatcher.id("minecraft:iron_helmet"), ItemMatcher.id("minecraft:iron_hoe"), ItemMatcher.id("minecraft:iron_horse_armor"), ItemMatcher.id("minecraft:iron_leggings"), ItemMatcher.id("minecraft:iron_nautilus_armor"), ItemMatcher.id("minecraft:iron_pickaxe"), ItemMatcher.id("minecraft:iron_shovel"), ItemMatcher.id("minecraft:iron_spear"), ItemMatcher.id("minecraft:iron_sword")), "minecraft:chainmail_boots"))
                    .output(RecipeOutput.item("minecraft:iron_nugget", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/lapis_lazuli_from_smelting_deepslate_lapis_ore", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:deepslate_lapis_ore"))
                    .output(RecipeOutput.item("minecraft:lapis_lazuli", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/lapis_lazuli_from_smelting_lapis_ore", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:lapis_ore"))
                    .output(RecipeOutput.item("minecraft:lapis_lazuli", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/leaf_litter", "mek:energized_smelter")
                    .input(ItemIngredient.tagged(1L, "mek:leaves"))
                    .output(RecipeOutput.item("minecraft:leaf_litter", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/light_blue_glazed_terracotta", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:light_blue_terracotta"))
                    .output(RecipeOutput.item("minecraft:light_blue_glazed_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/light_gray_glazed_terracotta", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:light_gray_terracotta"))
                    .output(RecipeOutput.item("minecraft:light_gray_glazed_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/lime_dye_from_smelting", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:sea_pickle"))
                    .output(RecipeOutput.item("minecraft:lime_dye", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/lime_glazed_terracotta", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:lime_terracotta"))
                    .output(RecipeOutput.item("minecraft:lime_glazed_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/magenta_glazed_terracotta", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:magenta_terracotta"))
                    .output(RecipeOutput.item("minecraft:magenta_glazed_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/nether_brick", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:netherrack"))
                    .output(RecipeOutput.item("minecraft:nether_brick", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/netherite_scrap", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:ancient_debris"))
                    .output(RecipeOutput.item("minecraft:netherite_scrap", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/orange_glazed_terracotta", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:orange_terracotta"))
                    .output(RecipeOutput.item("minecraft:orange_glazed_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/pink_glazed_terracotta", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:pink_terracotta"))
                    .output(RecipeOutput.item("minecraft:pink_glazed_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/popped_chorus_fruit", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:chorus_fruit"))
                    .output(RecipeOutput.item("minecraft:popped_chorus_fruit", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/processing/bronze/ingot/from_dust_smelting", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "mek:dust_bronze"))
                    .output(RecipeOutput.item("mek:ingot_bronze", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/processing/copper/ingot/from_dust_smelting", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "mek:dust_copper"))
                    .output(RecipeOutput.item("minecraft:copper_ingot", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/processing/gold/ingot/from_dust_smelting", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "mek:dust_gold"))
                    .output(RecipeOutput.item("minecraft:gold_ingot", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/processing/iron/ingot/from_dust_smelting", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "mek:dust_iron"))
                    .output(RecipeOutput.item("minecraft:iron_ingot", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/processing/lead/ingot/from_dust_smelting", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "mek:dust_lead"))
                    .output(RecipeOutput.item("mek:ingot_lead", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/processing/lead/ingot/from_ore_smelting", "mek:energized_smelter")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("mek:deepslate_lead_ore"), ItemMatcher.id("mek:lead_ore")), "mek:deepslate_lead_ore"))
                    .output(RecipeOutput.item("mek:ingot_lead", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/processing/lead/ingot/from_raw_smelting", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "mek:raw_lead"))
                    .output(RecipeOutput.item("mek:ingot_lead", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/processing/netherite/ingot_from_dust_smelting", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "mek:dust_netherite"))
                    .output(RecipeOutput.item("minecraft:netherite_ingot", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/processing/osmium/ingot/from_dust_smelting", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "mek:dust_osmium"))
                    .output(RecipeOutput.item("mek:ingot_osmium", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/processing/osmium/ingot/from_ore_smelting", "mek:energized_smelter")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("mek:deepslate_osmium_ore"), ItemMatcher.id("mek:osmium_ore")), "mek:deepslate_osmium_ore"))
                    .output(RecipeOutput.item("mek:ingot_osmium", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/processing/osmium/ingot/from_raw_smelting", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "mek:raw_osmium"))
                    .output(RecipeOutput.item("mek:ingot_osmium", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/processing/steel/ingot/from_dust_smelting", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "mek:dust_steel"))
                    .output(RecipeOutput.item("mek:ingot_steel", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/processing/tin/ingot/from_dust_smelting", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "mek:dust_tin"))
                    .output(RecipeOutput.item("mek:ingot_tin", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/processing/tin/ingot/from_ore_smelting", "mek:energized_smelter")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("mek:deepslate_tin_ore"), ItemMatcher.id("mek:tin_ore")), "mek:deepslate_tin_ore"))
                    .output(RecipeOutput.item("mek:ingot_tin", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/processing/tin/ingot/from_raw_smelting", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "mek:raw_tin"))
                    .output(RecipeOutput.item("mek:ingot_tin", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/processing/uranium/ingot/from_dust_smelting", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "mek:dust_uranium"))
                    .output(RecipeOutput.item("mek:ingot_uranium", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/processing/uranium/ingot/from_ore_smelting", "mek:energized_smelter")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("mek:deepslate_uranium_ore"), ItemMatcher.id("mek:uranium_ore")), "mek:deepslate_uranium_ore"))
                    .output(RecipeOutput.item("mek:ingot_uranium", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/processing/uranium/ingot/from_raw_smelting", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "mek:raw_uranium"))
                    .output(RecipeOutput.item("mek:ingot_uranium", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/purple_glazed_terracotta", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:purple_terracotta"))
                    .output(RecipeOutput.item("minecraft:purple_glazed_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/quartz", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:nether_quartz_ore"))
                    .output(RecipeOutput.item("minecraft:quartz", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/red_glazed_terracotta", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:red_terracotta"))
                    .output(RecipeOutput.item("minecraft:red_glazed_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/redstone_from_smelting_deepslate_redstone_ore", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:deepslate_redstone_ore"))
                    .output(RecipeOutput.item("minecraft:redstone", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/redstone_from_smelting_redstone_ore", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:redstone_ore"))
                    .output(RecipeOutput.item("minecraft:redstone", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/resin_brick", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:resin_clump"))
                    .output(RecipeOutput.item("minecraft:resin_brick", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/smooth_basalt", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:basalt"))
                    .output(RecipeOutput.item("minecraft:smooth_basalt", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/smooth_quartz", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:quartz_block"))
                    .output(RecipeOutput.item("minecraft:smooth_quartz", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/smooth_red_sandstone", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:red_sandstone"))
                    .output(RecipeOutput.item("minecraft:smooth_red_sandstone", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/smooth_sandstone", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:sandstone"))
                    .output(RecipeOutput.item("minecraft:smooth_sandstone", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/smooth_stone", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:stone"))
                    .output(RecipeOutput.item("minecraft:smooth_stone", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/sponge", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:wet_sponge"))
                    .output(RecipeOutput.item("minecraft:sponge", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/stone", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:cobblestone"))
                    .output(RecipeOutput.item("minecraft:stone", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/terracotta", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:clay"))
                    .output(RecipeOutput.item("minecraft:terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/white_glazed_terracotta", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:white_terracotta"))
                    .output(RecipeOutput.item("minecraft:white_glazed_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:energized_smelter/yellow_glazed_terracotta", "mek:energized_smelter")
                    .input(ItemIngredient.item(1L, "minecraft:yellow_terracotta"))
                    .output(RecipeOutput.item("minecraft:yellow_glazed_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:activating/tritium", "mek:solar_neutron_activator")
                    .input(Ingredient.chemical(1L, "mek:lithium"))
                    .output(RecipeOutput.chemical("mek:tritium", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:chemical_infusing/fusion_fuel", "mek:chemical_infuser")
                    .input(Ingredient.chemical(1L, "mek:deuterium"))
                    .input(Ingredient.chemical(1L, "mek:tritium"))
                    .output(RecipeOutput.chemical("mek:fusion_fuel", 2L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:chemical_infusing/hydrogen_chloride", "mek:chemical_infuser")
                    .input(Ingredient.chemical(1L, "mek:hydrogen"))
                    .input(Ingredient.chemical(1L, "mek:chlorine"))
                    .output(RecipeOutput.chemical("mek:hydrogen_chloride", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:chemical_infusing/sulfur_trioxide", "mek:chemical_infuser")
                    .input(Ingredient.chemical(1L, "mek:oxygen"))
                    .input(Ingredient.chemical(2L, "mek:sulfur_dioxide"))
                    .output(RecipeOutput.chemical("mek:sulfur_trioxide", 2L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:chemical_infusing/sulfuric_acid", "mek:chemical_infuser")
                    .input(Ingredient.chemical(1L, "mek:sulfur_trioxide"))
                    .input(Ingredient.chemical(1L, "mek:water_vapor"))
                    .output(RecipeOutput.chemical("mek:sulfuric_acid", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:combining/dye/black_to_light_gray", "mek:combiner")
                    .input(ItemIngredient.tagged(1L, "mek:dyes/black"))
                    .input(ItemIngredient.tagged(2L, "mek:dyes/white"))
                    .output(RecipeOutput.item("minecraft:light_gray_dye", 6L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/dye/cyan", "mek:combiner")
                    .input(ItemIngredient.tagged(1L, "mek:dyes/blue"))
                    .input(ItemIngredient.tagged(1L, "mek:dyes/green"))
                    .output(RecipeOutput.item("minecraft:cyan_dye", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/dye/gray_to_light_gray", "mek:combiner")
                    .input(ItemIngredient.tagged(1L, "mek:dyes/gray"))
                    .input(ItemIngredient.tagged(1L, "mek:dyes/white"))
                    .output(RecipeOutput.item("minecraft:light_gray_dye", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/dye/light_blue", "mek:combiner")
                    .input(ItemIngredient.tagged(1L, "mek:dyes/blue"))
                    .input(ItemIngredient.tagged(1L, "mek:dyes/white"))
                    .output(RecipeOutput.item("minecraft:light_blue_dye", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/dye/lime", "mek:combiner")
                    .input(ItemIngredient.tagged(1L, "mek:dyes/green"))
                    .input(ItemIngredient.tagged(1L, "mek:dyes/white"))
                    .output(RecipeOutput.item("minecraft:lime_dye", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/dye/magenta", "mek:combiner")
                    .input(ItemIngredient.tagged(1L, "mek:dyes/purple"))
                    .input(ItemIngredient.tagged(1L, "mek:dyes/pink"))
                    .output(RecipeOutput.item("minecraft:magenta_dye", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/dye/orange", "mek:combiner")
                    .input(ItemIngredient.tagged(1L, "mek:dyes/red"))
                    .input(ItemIngredient.tagged(1L, "mek:dyes/yellow"))
                    .output(RecipeOutput.item("minecraft:orange_dye", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/dye/pink", "mek:combiner")
                    .input(ItemIngredient.tagged(1L, "mek:dyes/red"))
                    .input(ItemIngredient.tagged(1L, "mek:dyes/white"))
                    .output(RecipeOutput.item("minecraft:pink_dye", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/dye/purple", "mek:combiner")
                    .input(ItemIngredient.tagged(1L, "mek:dyes/blue"))
                    .input(ItemIngredient.tagged(1L, "mek:dyes/red"))
                    .output(RecipeOutput.item("minecraft:purple_dye", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/glow/berries", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:sweet_berries"))
                    .input(ItemIngredient.tagged(1L, "mek:dusts/glowstone"))
                    .output(RecipeOutput.item("minecraft:glow_berries", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/glow/ink_sac", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:ink_sac"))
                    .input(ItemIngredient.tagged(1L, "mek:dusts/glowstone"))
                    .output(RecipeOutput.item("minecraft:glow_ink_sac", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/glow/item_frame", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:item_frame"))
                    .input(ItemIngredient.tagged(1L, "mek:dusts/glowstone"))
                    .output(RecipeOutput.item("minecraft:glow_item_frame", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/gravel", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:flint"))
                    .input(ItemIngredient.tagged(1L, "mek:cobblestones/normal"))
                    .output(RecipeOutput.item("minecraft:gravel", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/muddy_mangrove_roots", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:mangrove_roots"))
                    .input(ItemIngredient.item(1L, "minecraft:mud"))
                    .output(RecipeOutput.item("minecraft:muddy_mangrove_roots", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/obsidian", "mek:combiner")
                    .input(ItemIngredient.tagged(4L, "mek:dusts/obsidian"))
                    .input(ItemIngredient.tagged(1L, "mek:cobblestones/deepslate"))
                    .output(RecipeOutput.item("minecraft:obsidian", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/packed_mud", "mek:combiner")
                    .input(ItemIngredient.tagged(1L, "mek:crops/wheat"))
                    .input(ItemIngredient.item(1L, "minecraft:mud"))
                    .output(RecipeOutput.item("minecraft:packed_mud", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/rooted_dirt", "mek:combiner")
                    .input(ItemIngredient.item(3L, "minecraft:hanging_roots"))
                    .input(ItemIngredient.item(1L, "minecraft:dirt"))
                    .output(RecipeOutput.item("minecraft:rooted_dirt", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_chiseled_copper", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:chiseled_copper"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_chiseled_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_copper_bars", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:copper_bars"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_copper_bars", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_copper_block", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:copper_block"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_copper_block", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_copper_bulb", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:copper_bulb"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_copper_bulb", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_copper_chain", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:copper_chain"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_copper_chain", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_copper_chest", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:copper_chest"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_copper_chest", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_copper_door", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:copper_door"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_copper_door", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_copper_golem_statue", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:copper_golem_statue"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_copper_golem_statue", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_copper_grate", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:copper_grate"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_copper_grate", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_copper_lantern", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:copper_lantern"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_copper_lantern", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_copper_trapdoor", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:copper_trapdoor"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_copper_trapdoor", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_cut_copper", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:cut_copper"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_cut_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_cut_copper_slab", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:cut_copper_slab"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_cut_copper_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_cut_copper_stairs", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:cut_copper_stairs"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_cut_copper_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_exposed_chiseled_copper", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_chiseled_copper"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_exposed_chiseled_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_exposed_copper", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_copper"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_exposed_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_exposed_copper_bars", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_copper_bars"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_exposed_copper_bars", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_exposed_copper_bulb", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_copper_bulb"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_exposed_copper_bulb", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_exposed_copper_chain", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_copper_chain"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_exposed_copper_chain", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_exposed_copper_chest", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_copper_chest"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_exposed_copper_chest", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_exposed_copper_door", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_copper_door"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_exposed_copper_door", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_exposed_copper_golem_statue", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_copper_golem_statue"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_exposed_copper_golem_statue", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_exposed_copper_grate", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_copper_grate"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_exposed_copper_grate", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_exposed_copper_lantern", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_copper_lantern"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_exposed_copper_lantern", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_exposed_copper_trapdoor", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_copper_trapdoor"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_exposed_copper_trapdoor", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_exposed_cut_copper", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_cut_copper"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_exposed_cut_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_exposed_cut_copper_slab", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_cut_copper_slab"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_exposed_cut_copper_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_exposed_cut_copper_stairs", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_cut_copper_stairs"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_exposed_cut_copper_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_exposed_lightning_rod", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_lightning_rod"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_exposed_lightning_rod", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_lightning_rod", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:lightning_rod"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_lightning_rod", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_oxidized_chiseled_copper", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:oxidized_chiseled_copper"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_oxidized_chiseled_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_oxidized_copper", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:oxidized_copper"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_oxidized_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_oxidized_copper_bars", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:oxidized_copper_bars"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_oxidized_copper_bars", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_oxidized_copper_bulb", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:oxidized_copper_bulb"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_oxidized_copper_bulb", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_oxidized_copper_chain", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:oxidized_copper_chain"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_oxidized_copper_chain", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_oxidized_copper_chest", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:oxidized_copper_chest"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_oxidized_copper_chest", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_oxidized_copper_door", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:oxidized_copper_door"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_oxidized_copper_door", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_oxidized_copper_golem_statue", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:oxidized_copper_golem_statue"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_oxidized_copper_golem_statue", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_oxidized_copper_grate", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:oxidized_copper_grate"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_oxidized_copper_grate", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_oxidized_copper_lantern", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:oxidized_copper_lantern"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_oxidized_copper_lantern", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_oxidized_copper_trapdoor", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:oxidized_copper_trapdoor"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_oxidized_copper_trapdoor", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_oxidized_cut_copper", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:oxidized_cut_copper"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_oxidized_cut_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_oxidized_cut_copper_slab", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:oxidized_cut_copper_slab"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_oxidized_cut_copper_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_oxidized_cut_copper_stairs", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:oxidized_cut_copper_stairs"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_oxidized_cut_copper_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_oxidized_lightning_rod", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:oxidized_lightning_rod"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_oxidized_lightning_rod", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_weathered_chiseled_copper", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_chiseled_copper"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_weathered_chiseled_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_weathered_copper", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_copper"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_weathered_copper", 1L))
                    .ticks(200)
                    .build());
    }

    private static void recipes1(Registrars r) {
        r.recipe(Recipe.builder("mek:combining/wax/waxed_weathered_copper_bars", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_copper_bars"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_weathered_copper_bars", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_weathered_copper_bulb", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_copper_bulb"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_weathered_copper_bulb", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_weathered_copper_chain", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_copper_chain"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_weathered_copper_chain", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_weathered_copper_chest", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_copper_chest"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_weathered_copper_chest", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_weathered_copper_door", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_copper_door"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_weathered_copper_door", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_weathered_copper_golem_statue", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_copper_golem_statue"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_weathered_copper_golem_statue", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_weathered_copper_grate", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_copper_grate"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_weathered_copper_grate", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_weathered_copper_lantern", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_copper_lantern"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_weathered_copper_lantern", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_weathered_copper_trapdoor", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_copper_trapdoor"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_weathered_copper_trapdoor", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_weathered_cut_copper", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_cut_copper"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_weathered_cut_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_weathered_cut_copper_slab", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_cut_copper_slab"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_weathered_cut_copper_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_weathered_cut_copper_stairs", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_cut_copper_stairs"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_weathered_cut_copper_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:combining/wax/waxed_weathered_lightning_rod", "mek:combiner")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_lightning_rod"))
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb"))
                    .output(RecipeOutput.item("minecraft:waxed_weathered_lightning_rod", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:control_circuit/basic", "mek:metallurgic_infuser")
                    .input(ItemIngredient.tagged(1L, "mek:ingots/osmium"))
                    .input(Ingredient.tagged(ResourceTypes.INFUSION, 20L, "mek:redstone"))
                    .output(RecipeOutput.item("mek:basic_control_circuit", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:control_circuit/infused_advanced", "mek:metallurgic_infuser")
                    .input(ItemIngredient.tagged(1L, "mek:circuits/basic"))
                    .input(Ingredient.tagged(ResourceTypes.INFUSION, 60L, "mek:redstone"))
                    .output(RecipeOutput.item("mek:advanced_control_circuit", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:control_circuit/infused_elite", "mek:metallurgic_infuser")
                    .input(ItemIngredient.tagged(1L, "mek:circuits/advanced"))
                    .input(Ingredient.tagged(ResourceTypes.INFUSION, 120L, "mek:diamond"))
                    .output(RecipeOutput.item("mek:elite_control_circuit", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:control_circuit/infused_ultimate", "mek:metallurgic_infuser")
                    .input(ItemIngredient.tagged(1L, "mek:circuits/elite"))
                    .input(Ingredient.tagged(ResourceTypes.INFUSION, 240L, "mek:refined_obsidian"))
                    .output(RecipeOutput.item("mek:ultimate_control_circuit", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/andesite/from_polished", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:polished_andesite"))
                    .output(RecipeOutput.item("minecraft:andesite", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/andesite/slab_from_polished_slab", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:polished_andesite_slab"))
                    .output(RecipeOutput.item("minecraft:andesite_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/andesite/stairs_from_polished_stairs", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:polished_andesite_stairs"))
                    .output(RecipeOutput.item("minecraft:andesite_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/apple", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:apple"))
                    .output(RecipeOutput.item("mek:bio_fuel", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/baked_potato", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:baked_potato"))
                    .output(RecipeOutput.item("mek:bio_fuel", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/bamboo", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:bamboo"))
                    .output(RecipeOutput.item("mek:bio_fuel", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/berries", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:foods/berry"))
                    .output(RecipeOutput.item("mek:bio_fuel", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/big_dripleaf", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:big_dripleaf"))
                    .output(RecipeOutput.item("mek:bio_fuel", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/bread", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:bread"))
                    .output(RecipeOutput.item("mek:bio_fuel", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/bush", "mek:crusher")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("minecraft:bush"), ItemMatcher.id("minecraft:red_shrub")), "minecraft:bush"))
                    .output(RecipeOutput.item("mek:bio_fuel", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/cactus_flower", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:cactus_flower"))
                    .output(RecipeOutput.item("mek:bio_fuel", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/cake", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:cake"))
                    .output(RecipeOutput.item("mek:bio_fuel", 6L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/carved_pumpkin", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:pumpkins/carved"))
                    .output(RecipeOutput.item("mek:bio_fuel", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/cookie", "mek:crusher")
                    .input(ItemIngredient.item(4L, "minecraft:cookie"))
                    .output(RecipeOutput.item("mek:bio_fuel", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/crimson_roots", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:crimson_roots"))
                    .output(RecipeOutput.item("mek:bio_fuel", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/crops", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:poisonous_potato"))
                    .output(RecipeOutput.item("mek:bio_fuel", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/crops_tags", "mek:crusher")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("minecraft:cactus"), ItemMatcher.id("minecraft:cocoa_beans"), ItemMatcher.id("minecraft:wheat")), "minecraft:cactus"))
                    .output(RecipeOutput.item("mek:bio_fuel", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/dried_kelp", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:dried_kelp"))
                    .output(RecipeOutput.item("mek:bio_fuel", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/dried_kelp_block", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:storage_blocks/dried_kelp"))
                    .output(RecipeOutput.item("mek:block_bio_fuel", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/fern", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:fern"))
                    .output(RecipeOutput.item("mek:bio_fuel", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/firefly_bush", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:firefly_bush"))
                    .output(RecipeOutput.item("mek:bio_fuel", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/fungus", "mek:crusher")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("minecraft:crimson_fungus"), ItemMatcher.id("minecraft:warped_fungus")), "minecraft:crimson_fungus"))
                    .output(RecipeOutput.item("mek:bio_fuel", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/glow_lichen", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:glow_lichen"))
                    .output(RecipeOutput.item("mek:bio_fuel", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/hanging_roots", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:hanging_roots"))
                    .output(RecipeOutput.item("mek:bio_fuel", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/hay_block", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:storage_blocks/wheat"))
                    .output(RecipeOutput.item("mek:block_bio_fuel", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/kelp", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:kelp"))
                    .output(RecipeOutput.item("mek:bio_fuel", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/large_fern", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:large_fern"))
                    .output(RecipeOutput.item("mek:bio_fuel", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/leaf_litter", "mek:crusher")
                    .input(ItemIngredient.item(10L, "minecraft:leaf_litter"))
                    .output(RecipeOutput.item("mek:bio_fuel", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/leaves", "mek:crusher")
                    .input(ItemIngredient.tagged(10L, "mek:leaves"))
                    .output(RecipeOutput.item("mek:bio_fuel", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/lily_pad", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:lily_pad"))
                    .output(RecipeOutput.item("mek:bio_fuel", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/mangrove_roots", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:mangrove_roots"))
                    .output(RecipeOutput.item("mek:bio_fuel", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/melon", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:crops/melon"))
                    .output(RecipeOutput.item("mek:bio_fuel", 6L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/melon_slice", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:melon_slice"))
                    .output(RecipeOutput.item("mek:bio_fuel", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/moss_block", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:moss_blocks"))
                    .output(RecipeOutput.item("mek:bio_fuel", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/moss_carpet", "mek:crusher")
                    .input(ItemIngredient.of(3L, ItemMatcher.or(ItemMatcher.id("minecraft:moss_carpet"), ItemMatcher.id("minecraft:pale_moss_carpet")), "minecraft:moss_carpet"))
                    .output(RecipeOutput.item("mek:bio_fuel", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/mushroom_blocks", "mek:crusher")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("minecraft:brown_mushroom_block"), ItemMatcher.id("minecraft:mushroom_stem"), ItemMatcher.id("minecraft:red_mushroom_block")), "minecraft:brown_mushroom_block"))
                    .output(RecipeOutput.item("mek:bio_fuel", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/mushrooms", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:mushrooms"))
                    .output(RecipeOutput.item("mek:bio_fuel", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/nether_sprouts", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:nether_sprouts"))
                    .output(RecipeOutput.item("mek:bio_fuel", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/nether_wart_block", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:nether_wart_block"))
                    .output(RecipeOutput.item("mek:block_bio_fuel", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/pale_hanging_moss", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:pale_hanging_moss"))
                    .output(RecipeOutput.item("mek:bio_fuel", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/pink_petals", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:pink_petals"))
                    .output(RecipeOutput.item("mek:bio_fuel", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/pitcher_pod", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:pitcher_pod"))
                    .output(RecipeOutput.item("mek:bio_fuel", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/pumpkin", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:crops/pumpkin"))
                    .output(RecipeOutput.item("mek:bio_fuel", 6L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/pumpkin_pie", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:pumpkin_pie"))
                    .output(RecipeOutput.item("mek:bio_fuel", 7L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/rotten_flesh", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:rotten_flesh"))
                    .output(RecipeOutput.item("mek:bio_fuel", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/saplings", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:saplings"))
                    .output(RecipeOutput.item("mek:bio_fuel", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/sea_pickle", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:sea_pickle"))
                    .output(RecipeOutput.item("mek:bio_fuel", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/seagrass", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:seagrass"))
                    .output(RecipeOutput.item("mek:bio_fuel", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/seeds", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:seeds"))
                    .output(RecipeOutput.item("mek:bio_fuel", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/short_grass", "mek:crusher")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("minecraft:short_dry_grass"), ItemMatcher.id("minecraft:short_grass")), "minecraft:short_dry_grass"))
                    .output(RecipeOutput.item("mek:bio_fuel", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/shroomlight", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:shroomlight"))
                    .output(RecipeOutput.item("mek:bio_fuel", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/small_dripleaf", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:small_dripleaf"))
                    .output(RecipeOutput.item("mek:bio_fuel", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/small_flowers", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:flowers/small"))
                    .output(RecipeOutput.item("mek:bio_fuel", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/spore_blossom", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:spore_blossom"))
                    .output(RecipeOutput.item("mek:bio_fuel", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/straw_bed", "mek:crusher")
                    .input(ItemIngredient.item(2L, "minecraft:straw_bed"))
                    .output(RecipeOutput.item("mek:block_bio_fuel", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/sugar_cane", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:crops/sugar_cane"))
                    .output(RecipeOutput.item("mek:bio_fuel", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/tall_flowers", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:flowers/tall"))
                    .output(RecipeOutput.item("mek:bio_fuel", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/tall_grass", "mek:crusher")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("minecraft:tall_dry_grass"), ItemMatcher.id("minecraft:tall_grass")), "minecraft:tall_dry_grass"))
                    .output(RecipeOutput.item("mek:bio_fuel", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/vines", "mek:crusher")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("minecraft:twisting_vines"), ItemMatcher.id("minecraft:vine"), ItemMatcher.id("minecraft:weeping_vines")), "minecraft:twisting_vines"))
                    .output(RecipeOutput.item("mek:bio_fuel", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/warped_roots", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:warped_roots"))
                    .output(RecipeOutput.item("mek:bio_fuel", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/warped_wart_block", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:warped_wart_block"))
                    .output(RecipeOutput.item("mek:bio_fuel", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/biofuel/wildflowers", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:wildflowers"))
                    .output(RecipeOutput.item("mek:bio_fuel", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/blackstone/bricks_to_cracked_bricks", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:polished_blackstone_bricks"))
                    .output(RecipeOutput.item("minecraft:cracked_polished_blackstone_bricks", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/blackstone/chiseled_bricks_to_bricks", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:chiseled_polished_blackstone"))
                    .output(RecipeOutput.item("minecraft:polished_blackstone_bricks", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/blackstone/from_cracked_bricks", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:cracked_polished_blackstone_bricks"))
                    .output(RecipeOutput.item("minecraft:polished_blackstone", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/blackstone/from_polished", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:polished_blackstone"))
                    .output(RecipeOutput.item("minecraft:blackstone", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/blackstone/polished_slabs_to_slabs", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:polished_blackstone_slab"))
                    .output(RecipeOutput.item("minecraft:blackstone_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/blackstone/polished_stairs_to_stairs", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:polished_blackstone_stairs"))
                    .output(RecipeOutput.item("minecraft:blackstone_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/blackstone/polished_wall_to_wall", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:polished_blackstone_wall"))
                    .output(RecipeOutput.item("minecraft:blackstone_wall", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/blaze_rod", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:rods/blaze"))
                    .output(RecipeOutput.item("minecraft:blaze_powder", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/bone", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:bone"))
                    .output(RecipeOutput.item("minecraft:bone_meal", 6L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/bone_block", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:storage_blocks/bone_meal"))
                    .output(RecipeOutput.item("minecraft:bone_meal", 9L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/break_disc_5", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:music_disc_5"))
                    .output(RecipeOutput.item("minecraft:disc_fragment_5", 9L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/breeze_rod", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:rods/breeze"))
                    .output(RecipeOutput.item("minecraft:wind_charge", 6L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/charcoal_dust", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:charcoal"))
                    .output(RecipeOutput.item("mek:dust_charcoal", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/chiseled_nether_bricks_to_nether_bricks", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:chiseled_nether_bricks"))
                    .output(RecipeOutput.item("minecraft:nether_bricks", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/cinnabar/brick_slabs_to_polished", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:cinnabar_brick_slab"))
                    .output(RecipeOutput.item("minecraft:polished_cinnabar_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/cinnabar/brick_stairs_to_polished", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:cinnabar_brick_stairs"))
                    .output(RecipeOutput.item("minecraft:polished_cinnabar_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/cinnabar/brick_wall_to_polished", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:cinnabar_brick_wall"))
                    .output(RecipeOutput.item("minecraft:polished_cinnabar_wall", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/cinnabar/bricks_to_polished", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:cinnabar_bricks"))
                    .output(RecipeOutput.item("minecraft:polished_cinnabar", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/cinnabar/chiseled_to_brick", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:chiseled_cinnabar"))
                    .output(RecipeOutput.item("minecraft:cinnabar_bricks", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/cinnabar/from_polished", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:polished_cinnabar"))
                    .output(RecipeOutput.item("minecraft:cinnabar", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/cinnabar/slabs_from_polished", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:polished_cinnabar_slab"))
                    .output(RecipeOutput.item("minecraft:cinnabar_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/cinnabar/stairs_from_polished", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:polished_cinnabar_stairs"))
                    .output(RecipeOutput.item("minecraft:cinnabar_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/cinnabar/wall_from_polished", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:polished_cinnabar_wall"))
                    .output(RecipeOutput.item("minecraft:cinnabar_wall", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/cobblestone_to_gravel", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:cobblestones/normal"))
                    .output(RecipeOutput.item("minecraft:gravel", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/deepslate/brick_slabs_to_tile", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:deepslate_brick_slab"))
                    .output(RecipeOutput.item("minecraft:deepslate_tile_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/deepslate/brick_stairs_to_tile", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:deepslate_brick_stairs"))
                    .output(RecipeOutput.item("minecraft:deepslate_tile_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/deepslate/brick_wall_to_tile", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:deepslate_brick_wall"))
                    .output(RecipeOutput.item("minecraft:deepslate_tile_wall", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/deepslate/bricks_to_cracked_bricks", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:deepslate_bricks"))
                    .output(RecipeOutput.item("minecraft:cracked_deepslate_bricks", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/deepslate/cracked_bricks_to_tile", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:cracked_deepslate_bricks"))
                    .output(RecipeOutput.item("minecraft:deepslate_tiles", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/deepslate/cracked_tile_to_chiseled", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:cracked_deepslate_tiles"))
                    .output(RecipeOutput.item("minecraft:chiseled_deepslate", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/deepslate/from_chiseled", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:chiseled_deepslate"))
                    .output(RecipeOutput.item("minecraft:deepslate", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/deepslate/polished_slabs_to_brick", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:polished_deepslate_slab"))
                    .output(RecipeOutput.item("minecraft:deepslate_brick_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/deepslate/polished_stairs_to_brick", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:polished_deepslate_stairs"))
                    .output(RecipeOutput.item("minecraft:deepslate_brick_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/deepslate/polished_to_bricks", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:polished_deepslate"))
                    .output(RecipeOutput.item("minecraft:deepslate_bricks", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/deepslate/polished_wall_to_brick", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:polished_deepslate_wall"))
                    .output(RecipeOutput.item("minecraft:deepslate_brick_wall", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/deepslate/tile_slabs_to_cobbled", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:deepslate_tile_slab"))
                    .output(RecipeOutput.item("minecraft:cobbled_deepslate_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/deepslate/tile_stairs_to_cobbled", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:deepslate_tile_stairs"))
                    .output(RecipeOutput.item("minecraft:cobbled_deepslate_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/deepslate/tile_to_cracked_tile", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:deepslate_tiles"))
                    .output(RecipeOutput.item("minecraft:cracked_deepslate_tiles", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/deepslate/tile_wall_to_cobbled", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:deepslate_tile_wall"))
                    .output(RecipeOutput.item("minecraft:cobbled_deepslate_wall", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/deepslate/to_cobbled", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:deepslate"))
                    .output(RecipeOutput.item("minecraft:cobbled_deepslate", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/chiseled_copper", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_chiseled_copper"))
                    .output(RecipeOutput.item("minecraft:chiseled_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/copper_bars", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_copper_bars"))
                    .output(RecipeOutput.item("minecraft:copper_bars", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/copper_block", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_copper_block"))
                    .output(RecipeOutput.item("minecraft:copper_block", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/copper_bulb", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_copper_bulb"))
                    .output(RecipeOutput.item("minecraft:copper_bulb", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/copper_chain", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_copper_chain"))
                    .output(RecipeOutput.item("minecraft:copper_chain", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/copper_chest", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_copper_chest"))
                    .output(RecipeOutput.item("minecraft:copper_chest", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/copper_door", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_copper_door"))
                    .output(RecipeOutput.item("minecraft:copper_door", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/copper_golem_statue", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_copper_golem_statue"))
                    .output(RecipeOutput.item("minecraft:copper_golem_statue", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/copper_grate", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_copper_grate"))
                    .output(RecipeOutput.item("minecraft:copper_grate", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/copper_lantern", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_copper_lantern"))
                    .output(RecipeOutput.item("minecraft:copper_lantern", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/copper_trapdoor", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_copper_trapdoor"))
                    .output(RecipeOutput.item("minecraft:copper_trapdoor", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/cut_copper", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_cut_copper"))
                    .output(RecipeOutput.item("minecraft:cut_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/cut_copper_slab", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_cut_copper_slab"))
                    .output(RecipeOutput.item("minecraft:cut_copper_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/cut_copper_stairs", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_cut_copper_stairs"))
                    .output(RecipeOutput.item("minecraft:cut_copper_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/exposed_chiseled_copper", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_exposed_chiseled_copper"))
                    .output(RecipeOutput.item("minecraft:exposed_chiseled_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/exposed_copper", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_exposed_copper"))
                    .output(RecipeOutput.item("minecraft:exposed_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/exposed_copper_bars", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_exposed_copper_bars"))
                    .output(RecipeOutput.item("minecraft:exposed_copper_bars", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/exposed_copper_bulb", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_exposed_copper_bulb"))
                    .output(RecipeOutput.item("minecraft:exposed_copper_bulb", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/exposed_copper_chain", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_exposed_copper_chain"))
                    .output(RecipeOutput.item("minecraft:exposed_copper_chain", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/exposed_copper_chest", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_exposed_copper_chest"))
                    .output(RecipeOutput.item("minecraft:exposed_copper_chest", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/exposed_copper_door", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_exposed_copper_door"))
                    .output(RecipeOutput.item("minecraft:exposed_copper_door", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/exposed_copper_golem_statue", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_exposed_copper_golem_statue"))
                    .output(RecipeOutput.item("minecraft:exposed_copper_golem_statue", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/exposed_copper_grate", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_exposed_copper_grate"))
                    .output(RecipeOutput.item("minecraft:exposed_copper_grate", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/exposed_copper_lantern", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_exposed_copper_lantern"))
                    .output(RecipeOutput.item("minecraft:exposed_copper_lantern", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/exposed_copper_trapdoor", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_exposed_copper_trapdoor"))
                    .output(RecipeOutput.item("minecraft:exposed_copper_trapdoor", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/exposed_cut_copper", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_exposed_cut_copper"))
                    .output(RecipeOutput.item("minecraft:exposed_cut_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/exposed_cut_copper_slab", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_exposed_cut_copper_slab"))
                    .output(RecipeOutput.item("minecraft:exposed_cut_copper_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/exposed_cut_copper_stairs", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_exposed_cut_copper_stairs"))
                    .output(RecipeOutput.item("minecraft:exposed_cut_copper_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/exposed_lightning_rod", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_exposed_lightning_rod"))
                    .output(RecipeOutput.item("minecraft:exposed_lightning_rod", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/lightning_rod", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_lightning_rod"))
                    .output(RecipeOutput.item("minecraft:lightning_rod", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/oxidized_chiseled_copper", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_oxidized_chiseled_copper"))
                    .output(RecipeOutput.item("minecraft:oxidized_chiseled_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/oxidized_copper", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_oxidized_copper"))
                    .output(RecipeOutput.item("minecraft:oxidized_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/oxidized_copper_bars", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_oxidized_copper_bars"))
                    .output(RecipeOutput.item("minecraft:oxidized_copper_bars", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/oxidized_copper_bulb", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_oxidized_copper_bulb"))
                    .output(RecipeOutput.item("minecraft:oxidized_copper_bulb", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/oxidized_copper_chain", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_oxidized_copper_chain"))
                    .output(RecipeOutput.item("minecraft:oxidized_copper_chain", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/oxidized_copper_chest", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_oxidized_copper_chest"))
                    .output(RecipeOutput.item("minecraft:oxidized_copper_chest", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/oxidized_copper_door", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_oxidized_copper_door"))
                    .output(RecipeOutput.item("minecraft:oxidized_copper_door", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/oxidized_copper_golem_statue", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_oxidized_copper_golem_statue"))
                    .output(RecipeOutput.item("minecraft:oxidized_copper_golem_statue", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/oxidized_copper_grate", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_oxidized_copper_grate"))
                    .output(RecipeOutput.item("minecraft:oxidized_copper_grate", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/oxidized_copper_lantern", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_oxidized_copper_lantern"))
                    .output(RecipeOutput.item("minecraft:oxidized_copper_lantern", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/oxidized_copper_trapdoor", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_oxidized_copper_trapdoor"))
                    .output(RecipeOutput.item("minecraft:oxidized_copper_trapdoor", 1L))
                    .ticks(200)
                    .build());
    }

    private static void recipes2(Registrars r) {
        r.recipe(Recipe.builder("mek:crushing/dewax/oxidized_cut_copper", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_oxidized_cut_copper"))
                    .output(RecipeOutput.item("minecraft:oxidized_cut_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/oxidized_cut_copper_slab", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_oxidized_cut_copper_slab"))
                    .output(RecipeOutput.item("minecraft:oxidized_cut_copper_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/oxidized_cut_copper_stairs", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_oxidized_cut_copper_stairs"))
                    .output(RecipeOutput.item("minecraft:oxidized_cut_copper_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/oxidized_lightning_rod", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_oxidized_lightning_rod"))
                    .output(RecipeOutput.item("minecraft:oxidized_lightning_rod", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/weathered_chiseled_copper", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_weathered_chiseled_copper"))
                    .output(RecipeOutput.item("minecraft:weathered_chiseled_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/weathered_copper", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_weathered_copper"))
                    .output(RecipeOutput.item("minecraft:weathered_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/weathered_copper_bars", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_weathered_copper_bars"))
                    .output(RecipeOutput.item("minecraft:weathered_copper_bars", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/weathered_copper_bulb", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_weathered_copper_bulb"))
                    .output(RecipeOutput.item("minecraft:weathered_copper_bulb", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/weathered_copper_chain", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_weathered_copper_chain"))
                    .output(RecipeOutput.item("minecraft:weathered_copper_chain", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/weathered_copper_chest", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_weathered_copper_chest"))
                    .output(RecipeOutput.item("minecraft:weathered_copper_chest", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/weathered_copper_door", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_weathered_copper_door"))
                    .output(RecipeOutput.item("minecraft:weathered_copper_door", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/weathered_copper_golem_statue", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_weathered_copper_golem_statue"))
                    .output(RecipeOutput.item("minecraft:weathered_copper_golem_statue", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/weathered_copper_grate", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_weathered_copper_grate"))
                    .output(RecipeOutput.item("minecraft:weathered_copper_grate", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/weathered_copper_lantern", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_weathered_copper_lantern"))
                    .output(RecipeOutput.item("minecraft:weathered_copper_lantern", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/weathered_copper_trapdoor", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_weathered_copper_trapdoor"))
                    .output(RecipeOutput.item("minecraft:weathered_copper_trapdoor", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/weathered_cut_copper", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_weathered_cut_copper"))
                    .output(RecipeOutput.item("minecraft:weathered_cut_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/weathered_cut_copper_slab", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_weathered_cut_copper_slab"))
                    .output(RecipeOutput.item("minecraft:weathered_cut_copper_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/weathered_cut_copper_stairs", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_weathered_cut_copper_stairs"))
                    .output(RecipeOutput.item("minecraft:weathered_cut_copper_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dewax/weathered_lightning_rod", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:waxed_weathered_lightning_rod"))
                    .output(RecipeOutput.item("minecraft:weathered_lightning_rod", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/diorite/from_polished", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:polished_diorite"))
                    .output(RecipeOutput.item("minecraft:diorite", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/diorite/slab_from_polished_slab", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:polished_diorite_slab"))
                    .output(RecipeOutput.item("minecraft:diorite_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/diorite/stairs_from_polished_stairs", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:polished_diorite_stairs"))
                    .output(RecipeOutput.item("minecraft:diorite_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/dried_ghast", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:dried_ghast"))
                    .output(RecipeOutput.item("minecraft:ghast_tear", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/flint_to_gunpowder", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:flint"))
                    .output(RecipeOutput.item("minecraft:gunpowder", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/granite/from_polished", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:polished_granite"))
                    .output(RecipeOutput.item("minecraft:granite", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/granite/slab_from_polished_slab", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:polished_granite_slab"))
                    .output(RecipeOutput.item("minecraft:granite_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/granite/stairs_from_polished_stairs", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:polished_granite_stairs"))
                    .output(RecipeOutput.item("minecraft:granite_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/gravel_to_sand", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:gravels"))
                    .output(RecipeOutput.item("minecraft:sand", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/honeycomb_from_block", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:honeycomb_block"))
                    .output(RecipeOutput.item("minecraft:honeycomb", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/mud_bricks_to_packed", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:mud_bricks"))
                    .output(RecipeOutput.item("minecraft:packed_mud", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/nether_bricks_to_cracked_nether_bricks", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:nether_bricks"))
                    .output(RecipeOutput.item("minecraft:cracked_nether_bricks", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/obsidian_to_dust", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:obsidians/normal"))
                    .output(RecipeOutput.item("mek:dust_obsidian", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/pointed_dripstone_from_block", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:dripstone_block"))
                    .output(RecipeOutput.item("minecraft:pointed_dripstone", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/polished_or_smooth_basalt_to_basalt", "mek:crusher")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("minecraft:polished_basalt"), ItemMatcher.id("minecraft:smooth_basalt")), "minecraft:polished_basalt"))
                    .output(RecipeOutput.item("minecraft:basalt", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/prismarine/shard_from_block", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:prismarine"))
                    .output(RecipeOutput.item("minecraft:prismarine_shard", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/prismarine/shard_from_brick", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:prismarine_bricks"))
                    .output(RecipeOutput.item("minecraft:prismarine_shard", 9L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/prismarine/shard_from_brick_slabs", "mek:crusher")
                    .input(ItemIngredient.item(2L, "minecraft:prismarine_brick_slab"))
                    .output(RecipeOutput.item("minecraft:prismarine_shard", 9L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/prismarine/shard_from_brick_stairs", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:prismarine_brick_stairs"))
                    .output(RecipeOutput.item("minecraft:prismarine_shard", 9L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/prismarine/shard_from_slabs", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:prismarine_slab"))
                    .output(RecipeOutput.item("minecraft:prismarine_shard", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/prismarine/shard_from_stairs", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:prismarine_stairs"))
                    .output(RecipeOutput.item("minecraft:prismarine_shard", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/prismarine/shard_from_wall", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:prismarine_wall"))
                    .output(RecipeOutput.item("minecraft:prismarine_shard", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/purpur_block_from_pillar", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:purpur_pillar"))
                    .output(RecipeOutput.item("minecraft:purpur_block", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/quartz/bricks_to_chiseled", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:quartz_bricks"))
                    .output(RecipeOutput.item("minecraft:chiseled_quartz_block", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/quartz/chiseled_to_pillar", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:chiseled_quartz_block"))
                    .output(RecipeOutput.item("minecraft:quartz_pillar", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/quartz/from_pillar", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:quartz_pillar"))
                    .output(RecipeOutput.item("minecraft:quartz_block", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/quartz/slab_to_smooth_slab", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:quartz_slab"))
                    .output(RecipeOutput.item("minecraft:smooth_quartz_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/quartz/smooth_to_bricks", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:smooth_quartz"))
                    .output(RecipeOutput.item("minecraft:quartz_bricks", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/quartz/stairs_to_smooth_stairs", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:quartz_stairs"))
                    .output(RecipeOutput.item("minecraft:smooth_quartz_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/quartz/to_smooth_quartz", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:quartz_block"))
                    .output(RecipeOutput.item("minecraft:smooth_quartz", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/red_sandstone_to_sand", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:sandstone/red_blocks"))
                    .output(RecipeOutput.item("minecraft:red_sand", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/resin/chiseled_resin_bricks_to_bricks", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:chiseled_resin_bricks"))
                    .output(RecipeOutput.item("minecraft:resin_bricks", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/sandstone_to_sand", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:sandstone/uncolored_blocks"))
                    .output(RecipeOutput.item("minecraft:sand", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/soul_soil_to_soul_sand", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:soul_soil"))
                    .output(RecipeOutput.item("minecraft:soul_sand", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/stone/bricks_to_cracked_bricks", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:stone_bricks"))
                    .output(RecipeOutput.item("minecraft:cracked_stone_bricks", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/stone/chiseled_bricks_to_bricks", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:chiseled_stone_bricks"))
                    .output(RecipeOutput.item("minecraft:stone_bricks", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/stone/from_cracked_bricks", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:cracked_stone_bricks"))
                    .output(RecipeOutput.item("minecraft:stone", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/stone/slabs_to_cobblestone_slabs", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:stone_slab"))
                    .output(RecipeOutput.item("minecraft:cobblestone_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/stone/stairs_to_cobblestone_stairs", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:stone_stairs"))
                    .output(RecipeOutput.item("minecraft:cobblestone_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/stone/to_cobblestone", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:stone"))
                    .output(RecipeOutput.item("minecraft:cobblestone", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/string/from_cushions", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:cushions"))
                    .output(RecipeOutput.item("minecraft:string", 6L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/string/from_wool", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:wool"))
                    .output(RecipeOutput.item("minecraft:string", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/string/from_wool_slabs", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:wool_slabs"))
                    .output(RecipeOutput.item("minecraft:string", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/string/from_wool_stairs", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:wool_stairs"))
                    .output(RecipeOutput.item("minecraft:string", 6L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/sulfur/brick_slabs_to_polished", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:sulfur_brick_slab"))
                    .output(RecipeOutput.item("minecraft:polished_sulfur_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/sulfur/brick_stairs_to_polished", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:sulfur_brick_stairs"))
                    .output(RecipeOutput.item("minecraft:polished_sulfur_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/sulfur/brick_wall_to_polished", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:sulfur_brick_wall"))
                    .output(RecipeOutput.item("minecraft:polished_sulfur_wall", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/sulfur/bricks_to_polished", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:sulfur_bricks"))
                    .output(RecipeOutput.item("minecraft:polished_sulfur", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/sulfur/chiseled_to_brick", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:chiseled_sulfur"))
                    .output(RecipeOutput.item("minecraft:sulfur_bricks", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/sulfur/from_polished", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:polished_sulfur"))
                    .output(RecipeOutput.item("minecraft:sulfur", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/sulfur/potent_to_dust", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:potent_sulfur"))
                    .output(RecipeOutput.item("mek:dust_sulfur", 36L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/sulfur/slabs_from_polished", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:polished_sulfur_slab"))
                    .output(RecipeOutput.item("minecraft:sulfur_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/sulfur/spike_to_dust", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:sulfur_spike"))
                    .output(RecipeOutput.item("mek:dust_sulfur", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/sulfur/stairs_from_polished", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:polished_sulfur_stairs"))
                    .output(RecipeOutput.item("minecraft:sulfur_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/sulfur/to_dust", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:sulfur"))
                    .output(RecipeOutput.item("mek:dust_sulfur", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/sulfur/wall_from_polished", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:polished_sulfur_wall"))
                    .output(RecipeOutput.item("minecraft:sulfur_wall", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/tuff/brick_slabs_to_polished", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:tuff_brick_slab"))
                    .output(RecipeOutput.item("minecraft:polished_tuff_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/tuff/brick_stairs_to_polished", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:tuff_brick_stairs"))
                    .output(RecipeOutput.item("minecraft:polished_tuff_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/tuff/brick_wall_to_polished", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:tuff_brick_wall"))
                    .output(RecipeOutput.item("minecraft:polished_tuff_wall", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/tuff/bricks_to_polished", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:tuff_bricks"))
                    .output(RecipeOutput.item("minecraft:polished_tuff", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/tuff/chiseled_to_brick", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:chiseled_tuff"))
                    .output(RecipeOutput.item("minecraft:tuff_bricks", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/tuff/from_polished", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:polished_tuff"))
                    .output(RecipeOutput.item("minecraft:tuff", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/tuff/slabs_from_polished", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:polished_tuff_slab"))
                    .output(RecipeOutput.item("minecraft:tuff_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/tuff/stairs_from_polished", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:polished_tuff_stairs"))
                    .output(RecipeOutput.item("minecraft:tuff_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crushing/tuff/wall_from_polished", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:polished_tuff_wall"))
                    .output(RecipeOutput.item("minecraft:tuff_wall", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crystallizing/lithium", "mek:chemical_crystallizer")
                    .input(Ingredient.chemical(100L, "mek:lithium"))
                    .output(RecipeOutput.item("mek:dust_lithium", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:crystallizing/salt", "mek:chemical_crystallizer")
                    .input(Ingredient.chemical(15L, "mek:brine"))
                    .output(RecipeOutput.item("mek:salt", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/charcoal", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:dusts/charcoal"))
                    .output(RecipeOutput.item("minecraft:charcoal", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/charcoal_dust", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(8L, "mek:dusts/wood"))
                    .output(RecipeOutput.item("mek:dust_charcoal", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/clay_ball", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:clay"))
                    .output(RecipeOutput.item("minecraft:clay_ball", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/andesite/slab_to_polished_slab", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:andesite_slab"))
                    .output(RecipeOutput.item("minecraft:polished_andesite_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/andesite/stairs_to_polished_stairs", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:andesite_stairs"))
                    .output(RecipeOutput.item("minecraft:polished_andesite_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/andesite/to_polished", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:andesite"))
                    .output(RecipeOutput.item("minecraft:polished_andesite", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/basalt_or_smooth_to_polished_basalt", "mek:enrichment_chamber")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("minecraft:basalt"), ItemMatcher.id("minecraft:smooth_basalt")), "minecraft:basalt"))
                    .output(RecipeOutput.item("minecraft:polished_basalt", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/blackstone/bricks_to_chiseled_bricks", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:polished_blackstone_bricks"))
                    .output(RecipeOutput.item("minecraft:chiseled_polished_blackstone", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/blackstone/cracked_bricks_to_bricks", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:cracked_polished_blackstone_bricks"))
                    .output(RecipeOutput.item("minecraft:polished_blackstone_bricks", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/blackstone/to_cracked_bricks", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:polished_blackstone"))
                    .output(RecipeOutput.item("minecraft:cracked_polished_blackstone_bricks", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/cinnabar/brick_to_chiseled", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:cinnabar_bricks"))
                    .output(RecipeOutput.item("minecraft:chiseled_cinnabar", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/cinnabar/polished_slabs_to_brick", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:polished_cinnabar_slab"))
                    .output(RecipeOutput.item("minecraft:cinnabar_brick_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/cinnabar/polished_stairs_to_brick", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:polished_cinnabar_stairs"))
                    .output(RecipeOutput.item("minecraft:cinnabar_brick_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/cinnabar/polished_to_brick", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:polished_cinnabar"))
                    .output(RecipeOutput.item("minecraft:cinnabar_bricks", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/cinnabar/polished_wall_to_brick", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:polished_cinnabar_wall"))
                    .output(RecipeOutput.item("minecraft:cinnabar_brick_wall", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/cinnabar/slabs_to_polished", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:cinnabar_slab"))
                    .output(RecipeOutput.item("minecraft:polished_cinnabar_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/cinnabar/stairs_to_polished", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:cinnabar_stairs"))
                    .output(RecipeOutput.item("minecraft:polished_cinnabar_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/cinnabar/to_polished", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:cinnabar"))
                    .output(RecipeOutput.item("minecraft:polished_cinnabar", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/cinnabar/wall_to_polished", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:cinnabar_wall"))
                    .output(RecipeOutput.item("minecraft:polished_cinnabar_wall", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/cracked_nether_bricks_to_nether_bricks", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:cracked_nether_bricks"))
                    .output(RecipeOutput.item("minecraft:nether_bricks", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/deepslate/chiseled_to_cracked_tile", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:chiseled_deepslate"))
                    .output(RecipeOutput.item("minecraft:cracked_deepslate_tiles", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/deepslate/cobbled_slabs_to_polished", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:cobbled_deepslate_slab"))
                    .output(RecipeOutput.item("minecraft:polished_deepslate_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/deepslate/cobbled_stairs_to_polished", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:cobbled_deepslate_stairs"))
                    .output(RecipeOutput.item("minecraft:polished_deepslate_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/deepslate/cobbled_wall_to_polished", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:cobbled_deepslate_wall"))
                    .output(RecipeOutput.item("minecraft:polished_deepslate_wall", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/deepslate/cracked_bricks_to_bricks", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:cracked_deepslate_bricks"))
                    .output(RecipeOutput.item("minecraft:deepslate_bricks", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/deepslate/cracked_tile_to_tile", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:cracked_deepslate_tiles"))
                    .output(RecipeOutput.item("minecraft:deepslate_tiles", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/deepslate/from_cobbled", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:cobblestones/deepslate"))
                    .output(RecipeOutput.item("minecraft:deepslate", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/deepslate/polished_chiseled", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:polished_deepslate"))
                    .output(RecipeOutput.item("minecraft:chiseled_deepslate", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/deepslate/polished_slabs_to_tile", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:polished_deepslate_slab"))
                    .output(RecipeOutput.item("minecraft:deepslate_tile_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/deepslate/polished_stairs_to_tile", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:polished_deepslate_stairs"))
                    .output(RecipeOutput.item("minecraft:deepslate_tile_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/deepslate/polished_wall_to_tile", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:polished_deepslate_wall"))
                    .output(RecipeOutput.item("minecraft:deepslate_tile_wall", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/deepslate/tile_slabs_to_brick", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:deepslate_tile_slab"))
                    .output(RecipeOutput.item("minecraft:deepslate_brick_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/deepslate/tile_stairs_to_brick", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:deepslate_tile_stairs"))
                    .output(RecipeOutput.item("minecraft:deepslate_brick_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/deepslate/tile_wall_to_brick", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:deepslate_tile_wall"))
                    .output(RecipeOutput.item("minecraft:deepslate_brick_wall", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/deepslate/tiles_to_cracked_bricks", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:deepslate_tiles"))
                    .output(RecipeOutput.item("minecraft:cracked_deepslate_bricks", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/deepslate/to_polished", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:deepslate"))
                    .output(RecipeOutput.item("minecraft:polished_deepslate", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/diorite/slab_to_polished_slab", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:diorite_slab"))
                    .output(RecipeOutput.item("minecraft:polished_diorite_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/diorite/stairs_to_polished_stairs", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:diorite_stairs"))
                    .output(RecipeOutput.item("minecraft:polished_diorite_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/diorite/to_polished", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:diorite"))
                    .output(RecipeOutput.item("minecraft:polished_diorite", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/granite/slab_to_polished_slab", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:granite_slab"))
                    .output(RecipeOutput.item("minecraft:polished_granite_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/granite/stairs_to_polished_stairs", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:granite_stairs"))
                    .output(RecipeOutput.item("minecraft:polished_granite_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/granite/to_polished", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:granite"))
                    .output(RecipeOutput.item("minecraft:polished_granite", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/gravel_to_flint", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:gravels"))
                    .output(RecipeOutput.item("minecraft:flint", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/gunpowder_to_flint", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:gunpowders"))
                    .output(RecipeOutput.item("minecraft:flint", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/mossy/cobblestone", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:cobblestones/mossy"))
                    .output(RecipeOutput.item("minecraft:cobblestone", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/mossy/cobblestone_slabs", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:mossy_cobblestone_slab"))
                    .output(RecipeOutput.item("minecraft:cobblestone_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/mossy/cobblestone_stairs", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:mossy_cobblestone_stairs"))
                    .output(RecipeOutput.item("minecraft:cobblestone_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/mossy/cobblestone_walls", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:mossy_cobblestone_wall"))
                    .output(RecipeOutput.item("minecraft:cobblestone_wall", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/mossy/stone_brick_slabs", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:mossy_stone_brick_slab"))
                    .output(RecipeOutput.item("minecraft:stone_brick_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/mossy/stone_brick_stairs", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:mossy_stone_brick_stairs"))
                    .output(RecipeOutput.item("minecraft:stone_brick_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/mossy/stone_brick_walls", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:mossy_stone_brick_wall"))
                    .output(RecipeOutput.item("minecraft:stone_brick_wall", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/mossy/stone_bricks", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:mossy_stone_bricks"))
                    .output(RecipeOutput.item("minecraft:stone_bricks", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/mud/packed_to_bricks", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:packed_mud"))
                    .output(RecipeOutput.item("minecraft:mud_bricks", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/mud/to_clay", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:mud"))
                    .output(RecipeOutput.item("minecraft:clay", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/nether_bricks_to_chiseled_nether_bricks", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:nether_bricks"))
                    .output(RecipeOutput.item("minecraft:chiseled_nether_bricks", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/purpur_pillar_from_block", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:purpur_block"))
                    .output(RecipeOutput.item("minecraft:purpur_pillar", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/quartz/from_smooth_quartz", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:smooth_quartz"))
                    .output(RecipeOutput.item("minecraft:quartz_block", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/quartz/smooth_slab_to_slab", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:smooth_quartz_slab"))
                    .output(RecipeOutput.item("minecraft:quartz_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/quartz/smooth_stairs_to_stairs", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:smooth_quartz_stairs"))
                    .output(RecipeOutput.item("minecraft:quartz_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/quartz/to_item", "mek:enrichment_chamber")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("minecraft:chiseled_quartz_block"), ItemMatcher.id("minecraft:quartz_block"), ItemMatcher.id("minecraft:quartz_bricks"), ItemMatcher.id("minecraft:quartz_pillar")), "minecraft:chiseled_quartz_block"))
                    .output(RecipeOutput.item("minecraft:quartz", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/sand_to_gravel", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:sands"))
                    .output(RecipeOutput.item("minecraft:gravel", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/soul_sand_to_soul_soil", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:soul_sand"))
                    .output(RecipeOutput.item("minecraft:soul_soil", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/stone/bricks_to_chiseled_bricks", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:stone_bricks"))
                    .output(RecipeOutput.item("minecraft:chiseled_stone_bricks", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/stone/cracked_bricks_to_bricks", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:cracked_stone_bricks"))
                    .output(RecipeOutput.item("minecraft:stone_bricks", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/stone/to_cracked_bricks", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:stone"))
                    .output(RecipeOutput.item("minecraft:cracked_stone_bricks", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/sulfur/brick_to_chiseled", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:sulfur_bricks"))
                    .output(RecipeOutput.item("minecraft:chiseled_sulfur", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/sulfur/dust_to_spike", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:dusts/sulfur"))
                    .output(RecipeOutput.item("minecraft:sulfur_spike", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/sulfur/polished_slabs_to_brick", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:polished_sulfur_slab"))
                    .output(RecipeOutput.item("minecraft:sulfur_brick_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/sulfur/polished_stairs_to_brick", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:polished_sulfur_stairs"))
                    .output(RecipeOutput.item("minecraft:sulfur_brick_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/sulfur/polished_to_brick", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:polished_sulfur"))
                    .output(RecipeOutput.item("minecraft:sulfur_bricks", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/sulfur/polished_wall_to_brick", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:polished_sulfur_wall"))
                    .output(RecipeOutput.item("minecraft:sulfur_brick_wall", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/sulfur/slabs_to_polished", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:sulfur_slab"))
                    .output(RecipeOutput.item("minecraft:polished_sulfur_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/sulfur/spike_to_gunpowder", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:sulfur_spike"))
                    .output(RecipeOutput.item("minecraft:gunpowder", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/sulfur/stairs_to_polished", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:sulfur_stairs"))
                    .output(RecipeOutput.item("minecraft:polished_sulfur_stairs", 1L))
                    .ticks(200)
                    .build());
    }

    private static void recipes3(Registrars r) {
        r.recipe(Recipe.builder("mek:enriching/conversion/sulfur/to_polished", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:sulfur"))
                    .output(RecipeOutput.item("minecraft:polished_sulfur", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/sulfur/wall_to_polished", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:sulfur_wall"))
                    .output(RecipeOutput.item("minecraft:polished_sulfur_wall", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/tuff/brick_to_chiseled", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:tuff_bricks"))
                    .output(RecipeOutput.item("minecraft:chiseled_tuff", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/tuff/polished_slabs_to_brick", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:polished_tuff_slab"))
                    .output(RecipeOutput.item("minecraft:tuff_brick_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/tuff/polished_stairs_to_brick", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:polished_tuff_stairs"))
                    .output(RecipeOutput.item("minecraft:tuff_brick_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/tuff/polished_to_brick", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:polished_tuff"))
                    .output(RecipeOutput.item("minecraft:tuff_bricks", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/tuff/polished_wall_to_brick", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:polished_tuff_wall"))
                    .output(RecipeOutput.item("minecraft:tuff_brick_wall", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/tuff/slabs_to_polished", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:tuff_slab"))
                    .output(RecipeOutput.item("minecraft:polished_tuff_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/tuff/stairs_to_polished", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:tuff_stairs"))
                    .output(RecipeOutput.item("minecraft:polished_tuff_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/tuff/to_polished", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:tuff"))
                    .output(RecipeOutput.item("minecraft:polished_tuff", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/conversion/tuff/wall_to_polished", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:tuff_wall"))
                    .output(RecipeOutput.item("minecraft:polished_tuff_wall", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/chiseled_copper", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_chiseled_copper"))
                    .output(RecipeOutput.item("minecraft:chiseled_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/copper_bars", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_copper_bars"))
                    .output(RecipeOutput.item("minecraft:copper_bars", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/copper_block", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_copper"))
                    .output(RecipeOutput.item("minecraft:copper_block", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/copper_bulb", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_copper_bulb"))
                    .output(RecipeOutput.item("minecraft:copper_bulb", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/copper_chain", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_copper_chain"))
                    .output(RecipeOutput.item("minecraft:copper_chain", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/copper_chest", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_copper_chest"))
                    .output(RecipeOutput.item("minecraft:copper_chest", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/copper_door", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_copper_door"))
                    .output(RecipeOutput.item("minecraft:copper_door", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/copper_golem_statue", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_copper_golem_statue"))
                    .output(RecipeOutput.item("minecraft:copper_golem_statue", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/copper_grate", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_copper_grate"))
                    .output(RecipeOutput.item("minecraft:copper_grate", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/copper_lantern", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_copper_lantern"))
                    .output(RecipeOutput.item("minecraft:copper_lantern", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/copper_trapdoor", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_copper_trapdoor"))
                    .output(RecipeOutput.item("minecraft:copper_trapdoor", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/cut_copper", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_cut_copper"))
                    .output(RecipeOutput.item("minecraft:cut_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/cut_copper_slab", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_cut_copper_slab"))
                    .output(RecipeOutput.item("minecraft:cut_copper_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/cut_copper_stairs", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_cut_copper_stairs"))
                    .output(RecipeOutput.item("minecraft:cut_copper_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/exposed_chiseled_copper", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_chiseled_copper"))
                    .output(RecipeOutput.item("minecraft:exposed_chiseled_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/exposed_copper", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_copper"))
                    .output(RecipeOutput.item("minecraft:exposed_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/exposed_copper_bars", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_copper_bars"))
                    .output(RecipeOutput.item("minecraft:exposed_copper_bars", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/exposed_copper_bulb", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_copper_bulb"))
                    .output(RecipeOutput.item("minecraft:exposed_copper_bulb", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/exposed_copper_chain", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_copper_chain"))
                    .output(RecipeOutput.item("minecraft:exposed_copper_chain", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/exposed_copper_chest", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_copper_chest"))
                    .output(RecipeOutput.item("minecraft:exposed_copper_chest", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/exposed_copper_door", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_copper_door"))
                    .output(RecipeOutput.item("minecraft:exposed_copper_door", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/exposed_copper_golem_statue", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_copper_golem_statue"))
                    .output(RecipeOutput.item("minecraft:exposed_copper_golem_statue", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/exposed_copper_grate", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_copper_grate"))
                    .output(RecipeOutput.item("minecraft:exposed_copper_grate", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/exposed_copper_lantern", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_copper_lantern"))
                    .output(RecipeOutput.item("minecraft:exposed_copper_lantern", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/exposed_copper_trapdoor", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_copper_trapdoor"))
                    .output(RecipeOutput.item("minecraft:exposed_copper_trapdoor", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/exposed_cut_copper", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_cut_copper"))
                    .output(RecipeOutput.item("minecraft:exposed_cut_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/exposed_cut_copper_slab", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_cut_copper_slab"))
                    .output(RecipeOutput.item("minecraft:exposed_cut_copper_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/exposed_cut_copper_stairs", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_cut_copper_stairs"))
                    .output(RecipeOutput.item("minecraft:exposed_cut_copper_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/exposed_lightning_rod", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_lightning_rod"))
                    .output(RecipeOutput.item("minecraft:exposed_lightning_rod", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/lightning_rod", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_lightning_rod"))
                    .output(RecipeOutput.item("minecraft:lightning_rod", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/weathered_chiseled_copper", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:oxidized_chiseled_copper"))
                    .output(RecipeOutput.item("minecraft:weathered_chiseled_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/weathered_copper", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:oxidized_copper"))
                    .output(RecipeOutput.item("minecraft:weathered_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/weathered_copper_bars", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:oxidized_copper_bars"))
                    .output(RecipeOutput.item("minecraft:weathered_copper_bars", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/weathered_copper_bulb", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:oxidized_copper_bulb"))
                    .output(RecipeOutput.item("minecraft:weathered_copper_bulb", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/weathered_copper_chain", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:oxidized_copper_chain"))
                    .output(RecipeOutput.item("minecraft:weathered_copper_chain", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/weathered_copper_chest", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:oxidized_copper_chest"))
                    .output(RecipeOutput.item("minecraft:weathered_copper_chest", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/weathered_copper_door", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:oxidized_copper_door"))
                    .output(RecipeOutput.item("minecraft:weathered_copper_door", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/weathered_copper_golem_statue", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:oxidized_copper_golem_statue"))
                    .output(RecipeOutput.item("minecraft:weathered_copper_golem_statue", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/weathered_copper_grate", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:oxidized_copper_grate"))
                    .output(RecipeOutput.item("minecraft:weathered_copper_grate", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/weathered_copper_lantern", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:oxidized_copper_lantern"))
                    .output(RecipeOutput.item("minecraft:weathered_copper_lantern", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/weathered_copper_trapdoor", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:oxidized_copper_trapdoor"))
                    .output(RecipeOutput.item("minecraft:weathered_copper_trapdoor", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/weathered_cut_copper", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:oxidized_cut_copper"))
                    .output(RecipeOutput.item("minecraft:weathered_cut_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/weathered_cut_copper_slab", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:oxidized_cut_copper_slab"))
                    .output(RecipeOutput.item("minecraft:weathered_cut_copper_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/weathered_cut_copper_stairs", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:oxidized_cut_copper_stairs"))
                    .output(RecipeOutput.item("minecraft:weathered_cut_copper_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/deoxidizing/weathered_lightning_rod", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:oxidized_lightning_rod"))
                    .output(RecipeOutput.item("minecraft:weathered_lightning_rod", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/dye/black", "mek:enrichment_chamber")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("minecraft:ink_sac"), ItemMatcher.id("minecraft:wither_rose")), "minecraft:ink_sac"))
                    .output(RecipeOutput.item("minecraft:black_dye", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/dye/blue", "mek:enrichment_chamber")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("minecraft:cornflower"), ItemMatcher.id("minecraft:lapis_lazuli")), "minecraft:cornflower"))
                    .output(RecipeOutput.item("minecraft:blue_dye", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/dye/brown", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:crops/cocoa_bean"))
                    .output(RecipeOutput.item("minecraft:brown_dye", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/dye/gray", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:closed_eyeblossom"))
                    .output(RecipeOutput.item("minecraft:gray_dye", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/dye/green", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:crops/cactus"))
                    .output(RecipeOutput.item("minecraft:green_dye", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/dye/large_cyan", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:pitcher_plant"))
                    .output(RecipeOutput.item("minecraft:cyan_dye", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/dye/large_magenta", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:lilac"))
                    .output(RecipeOutput.item("minecraft:magenta_dye", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/dye/large_pink", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:peony"))
                    .output(RecipeOutput.item("minecraft:pink_dye", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/dye/large_red", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:rose_bush"))
                    .output(RecipeOutput.item("minecraft:red_dye", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/dye/large_yellow", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:sunflower"))
                    .output(RecipeOutput.item("minecraft:yellow_dye", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/dye/light_blue", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:blue_orchid"))
                    .output(RecipeOutput.item("minecraft:light_blue_dye", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/dye/light_gray", "mek:enrichment_chamber")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("minecraft:azure_bluet"), ItemMatcher.id("minecraft:oxeye_daisy"), ItemMatcher.id("minecraft:white_tulip")), "minecraft:azure_bluet"))
                    .output(RecipeOutput.item("minecraft:light_gray_dye", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/dye/lime", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:sea_pickle"))
                    .output(RecipeOutput.item("minecraft:lime_dye", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/dye/orange", "mek:enrichment_chamber")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("minecraft:open_eyeblossom"), ItemMatcher.id("minecraft:orange_tulip"), ItemMatcher.id("minecraft:torchflower")), "minecraft:open_eyeblossom"))
                    .output(RecipeOutput.item("minecraft:orange_dye", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/dye/small_magenta", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:allium"))
                    .output(RecipeOutput.item("minecraft:magenta_dye", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/dye/small_pink", "mek:enrichment_chamber")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("minecraft:cactus_flower"), ItemMatcher.id("minecraft:pink_petals"), ItemMatcher.id("minecraft:pink_tulip")), "minecraft:cactus_flower"))
                    .output(RecipeOutput.item("minecraft:pink_dye", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/dye/small_red", "mek:enrichment_chamber")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("minecraft:beetroot"), ItemMatcher.id("minecraft:poppy"), ItemMatcher.id("minecraft:red_tulip")), "minecraft:beetroot"))
                    .output(RecipeOutput.item("minecraft:red_dye", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/dye/small_yellow", "mek:enrichment_chamber")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("minecraft:dandelion"), ItemMatcher.id("minecraft:golden_dandelion"), ItemMatcher.id("minecraft:wildflowers")), "minecraft:dandelion"))
                    .output(RecipeOutput.item("minecraft:yellow_dye", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/dye/white", "mek:enrichment_chamber")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("minecraft:bone_meal"), ItemMatcher.id("minecraft:lily_of_the_valley")), "minecraft:bone_meal"))
                    .output(RecipeOutput.item("minecraft:white_dye", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/enriched/carbon", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:coals"))
                    .output(RecipeOutput.item("mek:enriched_carbon", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/enriched/diamond", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:gems/diamond"))
                    .output(RecipeOutput.item("mek:enriched_diamond", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/enriched/gold", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:dusts/gold"))
                    .output(RecipeOutput.item("mek:enriched_gold", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/enriched/redstone", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:dusts/redstone"))
                    .output(RecipeOutput.item("mek:enriched_redstone", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/enriched/refined_obsidian", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:dusts/refined_obsidian"))
                    .output(RecipeOutput.item("mek:enriched_refined_obsidian", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/enriched/tin", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:dusts/tin"))
                    .output(RecipeOutput.item("mek:enriched_tin", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/glowstone_dust", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:glowstone"))
                    .output(RecipeOutput.item("minecraft:glowstone_dust", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/hdpe_sheet", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(3L, "mek:hdpe_pellet"))
                    .output(RecipeOutput.item("mek:hdpe_sheet", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/salt", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "mek:block_salt"))
                    .output(RecipeOutput.item("mek:salt", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:enriching/string", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:cobweb"))
                    .output(RecipeOutput.item("minecraft:string", 9L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:hohlraum", "mek:metallurgic_infuser")
                    .input(ItemIngredient.tagged(4L, "mek:dusts/gold"))
                    .input(Ingredient.tagged(ResourceTypes.INFUSION, 10L, "mek:carbon"))
                    .output(RecipeOutput.item("mek:hohlraum", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/concrete/black", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:black_concrete_powder"))
                    .input(Ingredient.tagged(ResourceTypes.CHEMICAL, 1L, "mek:water_vapor").perTickUsage())
                    .output(RecipeOutput.item("minecraft:black_concrete", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/concrete/blue", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:blue_concrete_powder"))
                    .input(Ingredient.tagged(ResourceTypes.CHEMICAL, 1L, "mek:water_vapor").perTickUsage())
                    .output(RecipeOutput.item("minecraft:blue_concrete", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/concrete/brown", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:brown_concrete_powder"))
                    .input(Ingredient.tagged(ResourceTypes.CHEMICAL, 1L, "mek:water_vapor").perTickUsage())
                    .output(RecipeOutput.item("minecraft:brown_concrete", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/concrete/cyan", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:cyan_concrete_powder"))
                    .input(Ingredient.tagged(ResourceTypes.CHEMICAL, 1L, "mek:water_vapor").perTickUsage())
                    .output(RecipeOutput.item("minecraft:cyan_concrete", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/concrete/gray", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:gray_concrete_powder"))
                    .input(Ingredient.tagged(ResourceTypes.CHEMICAL, 1L, "mek:water_vapor").perTickUsage())
                    .output(RecipeOutput.item("minecraft:gray_concrete", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/concrete/green", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:green_concrete_powder"))
                    .input(Ingredient.tagged(ResourceTypes.CHEMICAL, 1L, "mek:water_vapor").perTickUsage())
                    .output(RecipeOutput.item("minecraft:green_concrete", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/concrete/light_blue", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:light_blue_concrete_powder"))
                    .input(Ingredient.tagged(ResourceTypes.CHEMICAL, 1L, "mek:water_vapor").perTickUsage())
                    .output(RecipeOutput.item("minecraft:light_blue_concrete", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/concrete/light_gray", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:light_gray_concrete_powder"))
                    .input(Ingredient.tagged(ResourceTypes.CHEMICAL, 1L, "mek:water_vapor").perTickUsage())
                    .output(RecipeOutput.item("minecraft:light_gray_concrete", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/concrete/lime", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:lime_concrete_powder"))
                    .input(Ingredient.tagged(ResourceTypes.CHEMICAL, 1L, "mek:water_vapor").perTickUsage())
                    .output(RecipeOutput.item("minecraft:lime_concrete", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/concrete/magenta", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:magenta_concrete_powder"))
                    .input(Ingredient.tagged(ResourceTypes.CHEMICAL, 1L, "mek:water_vapor").perTickUsage())
                    .output(RecipeOutput.item("minecraft:magenta_concrete", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/concrete/orange", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:orange_concrete_powder"))
                    .input(Ingredient.tagged(ResourceTypes.CHEMICAL, 1L, "mek:water_vapor").perTickUsage())
                    .output(RecipeOutput.item("minecraft:orange_concrete", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/concrete/pink", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:pink_concrete_powder"))
                    .input(Ingredient.tagged(ResourceTypes.CHEMICAL, 1L, "mek:water_vapor").perTickUsage())
                    .output(RecipeOutput.item("minecraft:pink_concrete", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/concrete/purple", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:purple_concrete_powder"))
                    .input(Ingredient.tagged(ResourceTypes.CHEMICAL, 1L, "mek:water_vapor").perTickUsage())
                    .output(RecipeOutput.item("minecraft:purple_concrete", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/concrete/red", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:red_concrete_powder"))
                    .input(Ingredient.tagged(ResourceTypes.CHEMICAL, 1L, "mek:water_vapor").perTickUsage())
                    .output(RecipeOutput.item("minecraft:red_concrete", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/concrete/white", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:white_concrete_powder"))
                    .input(Ingredient.tagged(ResourceTypes.CHEMICAL, 1L, "mek:water_vapor").perTickUsage())
                    .output(RecipeOutput.item("minecraft:white_concrete", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/concrete/yellow", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:yellow_concrete_powder"))
                    .input(Ingredient.tagged(ResourceTypes.CHEMICAL, 1L, "mek:water_vapor").perTickUsage())
                    .output(RecipeOutput.item("minecraft:yellow_concrete", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/coral/brain_coral", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:dead_brain_coral"))
                    .input(Ingredient.tagged(ResourceTypes.CHEMICAL, 3L, "mek:water_vapor").perTickUsage())
                    .output(RecipeOutput.item("minecraft:brain_coral", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/coral/brain_coral_block", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:dead_brain_coral_block"))
                    .input(Ingredient.tagged(ResourceTypes.CHEMICAL, 5L, "mek:water_vapor").perTickUsage())
                    .output(RecipeOutput.item("minecraft:brain_coral_block", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/coral/brain_coral_fan", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:dead_brain_coral_fan"))
                    .input(Ingredient.tagged(ResourceTypes.CHEMICAL, 3L, "mek:water_vapor").perTickUsage())
                    .output(RecipeOutput.item("minecraft:brain_coral_fan", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/coral/bubble_coral", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:dead_bubble_coral"))
                    .input(Ingredient.tagged(ResourceTypes.CHEMICAL, 3L, "mek:water_vapor").perTickUsage())
                    .output(RecipeOutput.item("minecraft:bubble_coral", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/coral/bubble_coral_block", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:dead_bubble_coral_block"))
                    .input(Ingredient.tagged(ResourceTypes.CHEMICAL, 5L, "mek:water_vapor").perTickUsage())
                    .output(RecipeOutput.item("minecraft:bubble_coral_block", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/coral/bubble_coral_fan", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:dead_bubble_coral_fan"))
                    .input(Ingredient.tagged(ResourceTypes.CHEMICAL, 3L, "mek:water_vapor").perTickUsage())
                    .output(RecipeOutput.item("minecraft:bubble_coral_fan", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/coral/fire_coral", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:dead_fire_coral"))
                    .input(Ingredient.tagged(ResourceTypes.CHEMICAL, 3L, "mek:water_vapor").perTickUsage())
                    .output(RecipeOutput.item("minecraft:fire_coral", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/coral/fire_coral_block", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:dead_fire_coral_block"))
                    .input(Ingredient.tagged(ResourceTypes.CHEMICAL, 5L, "mek:water_vapor").perTickUsage())
                    .output(RecipeOutput.item("minecraft:fire_coral_block", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/coral/fire_coral_fan", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:dead_fire_coral_fan"))
                    .input(Ingredient.tagged(ResourceTypes.CHEMICAL, 3L, "mek:water_vapor").perTickUsage())
                    .output(RecipeOutput.item("minecraft:fire_coral_fan", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/coral/horn_coral", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:dead_horn_coral"))
                    .input(Ingredient.tagged(ResourceTypes.CHEMICAL, 3L, "mek:water_vapor").perTickUsage())
                    .output(RecipeOutput.item("minecraft:horn_coral", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/coral/horn_coral_block", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:dead_horn_coral_block"))
                    .input(Ingredient.tagged(ResourceTypes.CHEMICAL, 5L, "mek:water_vapor").perTickUsage())
                    .output(RecipeOutput.item("minecraft:horn_coral_block", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/coral/horn_coral_fan", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:dead_horn_coral_fan"))
                    .input(Ingredient.tagged(ResourceTypes.CHEMICAL, 3L, "mek:water_vapor").perTickUsage())
                    .output(RecipeOutput.item("minecraft:horn_coral_fan", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/coral/tube_coral", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:dead_tube_coral"))
                    .input(Ingredient.tagged(ResourceTypes.CHEMICAL, 3L, "mek:water_vapor").perTickUsage())
                    .output(RecipeOutput.item("minecraft:tube_coral", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/coral/tube_coral_block", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:dead_tube_coral_block"))
                    .input(Ingredient.tagged(ResourceTypes.CHEMICAL, 5L, "mek:water_vapor").perTickUsage())
                    .output(RecipeOutput.item("minecraft:tube_coral_block", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/coral/tube_coral_fan", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:dead_tube_coral_fan"))
                    .input(Ingredient.tagged(ResourceTypes.CHEMICAL, 3L, "mek:water_vapor").perTickUsage())
                    .output(RecipeOutput.item("minecraft:tube_coral_fan", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/dirt_to_mud", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:dirt"))
                    .input(Ingredient.tagged(ResourceTypes.CHEMICAL, 1L, "mek:water_vapor").perTickUsage())
                    .output(RecipeOutput.item("minecraft:mud", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/gunpowder_to_sulfur", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:gunpowders"))
                    .input(Ingredient.chemical(1L, "mek:hydrogen_chloride").perTickUsage())
                    .output(RecipeOutput.item("mek:dust_sulfur", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/exposed_chiseled_copper", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:chiseled_copper"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:exposed_chiseled_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/exposed_copper", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:copper_block"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:exposed_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/exposed_copper_bars", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:copper_bars"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:exposed_copper_bars", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/exposed_copper_bulb", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:copper_bulb"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:exposed_copper_bulb", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/exposed_copper_chain", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:copper_chain"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:exposed_copper_chain", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/exposed_copper_chest", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:copper_chest"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:exposed_copper_chest", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/exposed_copper_door", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:copper_door"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:exposed_copper_door", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/exposed_copper_golem_statue", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:copper_golem_statue"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:exposed_copper_golem_statue", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/exposed_copper_grate", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:copper_grate"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:exposed_copper_grate", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/exposed_copper_lantern", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:copper_lantern"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:exposed_copper_lantern", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/exposed_copper_trapdoor", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:copper_trapdoor"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:exposed_copper_trapdoor", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/exposed_cut_copper", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:cut_copper"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:exposed_cut_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/exposed_cut_copper_slab", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:cut_copper_slab"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:exposed_cut_copper_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/exposed_cut_copper_stairs", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:cut_copper_stairs"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:exposed_cut_copper_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/exposed_lightning_rod", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:lightning_rod"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:exposed_lightning_rod", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/oxidized_chiseled_copper", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_chiseled_copper"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:oxidized_chiseled_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/oxidized_copper", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_copper"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:oxidized_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/oxidized_copper_bars", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_copper_bars"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:oxidized_copper_bars", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/oxidized_copper_bulb", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_copper_bulb"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:oxidized_copper_bulb", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/oxidized_copper_chain", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_copper_chain"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:oxidized_copper_chain", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/oxidized_copper_chest", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_copper_chest"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:oxidized_copper_chest", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/oxidized_copper_door", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_copper_door"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:oxidized_copper_door", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/oxidized_copper_golem_statue", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_copper_golem_statue"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:oxidized_copper_golem_statue", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/oxidized_copper_grate", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_copper_grate"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:oxidized_copper_grate", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/oxidized_copper_lantern", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_copper_lantern"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:oxidized_copper_lantern", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/oxidized_copper_trapdoor", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_copper_trapdoor"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:oxidized_copper_trapdoor", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/oxidized_cut_copper", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_cut_copper"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:oxidized_cut_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/oxidized_cut_copper_slab", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_cut_copper_slab"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:oxidized_cut_copper_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/oxidized_cut_copper_stairs", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_cut_copper_stairs"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:oxidized_cut_copper_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/oxidized_lightning_rod", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:weathered_lightning_rod"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:oxidized_lightning_rod", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/weathered_chiseled_copper", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_chiseled_copper"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:weathered_chiseled_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/weathered_copper", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_copper"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:weathered_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/weathered_copper_bars", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_copper_bars"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:weathered_copper_bars", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/weathered_copper_bulb", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_copper_bulb"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:weathered_copper_bulb", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/weathered_copper_chain", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_copper_chain"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:weathered_copper_chain", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/weathered_copper_chest", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_copper_chest"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:weathered_copper_chest", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/weathered_copper_door", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_copper_door"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:weathered_copper_door", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/weathered_copper_golem_statue", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_copper_golem_statue"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:weathered_copper_golem_statue", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/weathered_copper_grate", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_copper_grate"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:weathered_copper_grate", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/weathered_copper_lantern", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_copper_lantern"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:weathered_copper_lantern", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/weathered_copper_trapdoor", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_copper_trapdoor"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:weathered_copper_trapdoor", 1L))
                    .ticks(200)
                    .build());
    }

    private static void recipes4(Registrars r) {
        r.recipe(Recipe.builder("mek:injecting/oxidizing/weathered_cut_copper", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_cut_copper"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:weathered_cut_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/weathered_cut_copper_slab", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_cut_copper_slab"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:weathered_cut_copper_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/weathered_cut_copper_stairs", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_cut_copper_stairs"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:weathered_cut_copper_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/oxidizing/weathered_lightning_rod", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:exposed_lightning_rod"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("minecraft:weathered_lightning_rod", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:injecting/terracotta_to_clay", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.item(1L, "minecraft:terracotta"))
                    .input(Ingredient.tagged(ResourceTypes.CHEMICAL, 1L, "mek:water_vapor").perTickUsage())
                    .output(RecipeOutput.item("minecraft:clay", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:metallurgic_infusing/alloy/atomic", "mek:metallurgic_infuser")
                    .input(ItemIngredient.tagged(1L, "mek:alloys/reinforced"))
                    .input(Ingredient.tagged(ResourceTypes.INFUSION, 40L, "mek:refined_obsidian"))
                    .output(RecipeOutput.item("mek:alloy_atomic", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:metallurgic_infusing/alloy/infused", "mek:metallurgic_infuser")
                    .input(ItemIngredient.tagged(1L, "mek:ingots/copper"))
                    .input(Ingredient.tagged(ResourceTypes.INFUSION, 10L, "mek:redstone"))
                    .output(RecipeOutput.item("mek:alloy_infused", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:metallurgic_infusing/alloy/reinforced", "mek:metallurgic_infuser")
                    .input(ItemIngredient.tagged(1L, "mek:alloys/infused"))
                    .input(Ingredient.tagged(ResourceTypes.INFUSION, 20L, "mek:diamond"))
                    .output(RecipeOutput.item("mek:alloy_reinforced", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:metallurgic_infusing/blackstone_to_gilded_blackstone", "mek:metallurgic_infuser")
                    .input(ItemIngredient.item(1L, "minecraft:blackstone"))
                    .input(Ingredient.tagged(ResourceTypes.INFUSION, 100L, "mek:gold"))
                    .output(RecipeOutput.item("minecraft:gilded_blackstone", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:metallurgic_infusing/clay_to_slime_ball", "mek:metallurgic_infuser")
                    .input(ItemIngredient.item(1L, "minecraft:clay_ball"))
                    .input(Ingredient.tagged(ResourceTypes.INFUSION, 10L, "mek:bio"))
                    .output(RecipeOutput.item("minecraft:slime_ball", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:metallurgic_infusing/clay_to_slime_block", "mek:metallurgic_infuser")
                    .input(ItemIngredient.item(1L, "minecraft:clay"))
                    .input(Ingredient.tagged(ResourceTypes.INFUSION, 40L, "mek:bio"))
                    .output(RecipeOutput.item("minecraft:slime_ball", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:metallurgic_infusing/crimson_nylium_to_warped_nylium", "mek:metallurgic_infuser")
                    .input(ItemIngredient.item(1L, "minecraft:crimson_nylium"))
                    .input(Ingredient.tagged(ResourceTypes.INFUSION, 10L, "mek:fungi"))
                    .output(RecipeOutput.item("minecraft:warped_nylium", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:metallurgic_infusing/dirt_to_mycelium", "mek:metallurgic_infuser")
                    .input(ItemIngredient.item(1L, "minecraft:dirt"))
                    .input(Ingredient.tagged(ResourceTypes.INFUSION, 10L, "mek:fungi"))
                    .output(RecipeOutput.item("minecraft:mycelium", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:metallurgic_infusing/dirt_to_podzol", "mek:metallurgic_infuser")
                    .input(ItemIngredient.item(1L, "minecraft:dirt"))
                    .input(Ingredient.tagged(ResourceTypes.INFUSION, 10L, "mek:bio"))
                    .output(RecipeOutput.item("minecraft:podzol", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:metallurgic_infusing/mossy/cobblestone", "mek:metallurgic_infuser")
                    .input(ItemIngredient.tagged(1L, "mek:cobblestones/normal"))
                    .input(Ingredient.tagged(ResourceTypes.INFUSION, 10L, "mek:bio"))
                    .output(RecipeOutput.item("minecraft:mossy_cobblestone", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:metallurgic_infusing/mossy/cobblestone_slab", "mek:metallurgic_infuser")
                    .input(ItemIngredient.item(1L, "minecraft:cobblestone_slab"))
                    .input(Ingredient.tagged(ResourceTypes.INFUSION, 10L, "mek:bio"))
                    .output(RecipeOutput.item("minecraft:mossy_cobblestone_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:metallurgic_infusing/mossy/cobblestone_stairs", "mek:metallurgic_infuser")
                    .input(ItemIngredient.item(1L, "minecraft:cobblestone_stairs"))
                    .input(Ingredient.tagged(ResourceTypes.INFUSION, 10L, "mek:bio"))
                    .output(RecipeOutput.item("minecraft:mossy_cobblestone_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:metallurgic_infusing/mossy/cobblestone_wall", "mek:metallurgic_infuser")
                    .input(ItemIngredient.item(1L, "minecraft:cobblestone_wall"))
                    .input(Ingredient.tagged(ResourceTypes.INFUSION, 10L, "mek:bio"))
                    .output(RecipeOutput.item("minecraft:mossy_cobblestone_wall", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:metallurgic_infusing/mossy/stone_brick", "mek:metallurgic_infuser")
                    .input(ItemIngredient.item(1L, "minecraft:stone_bricks"))
                    .input(Ingredient.tagged(ResourceTypes.INFUSION, 10L, "mek:bio"))
                    .output(RecipeOutput.item("minecraft:mossy_stone_bricks", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:metallurgic_infusing/mossy/stone_brick_slab", "mek:metallurgic_infuser")
                    .input(ItemIngredient.item(1L, "minecraft:stone_brick_slab"))
                    .input(Ingredient.tagged(ResourceTypes.INFUSION, 10L, "mek:bio"))
                    .output(RecipeOutput.item("minecraft:mossy_stone_brick_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:metallurgic_infusing/mossy/stone_brick_stairs", "mek:metallurgic_infuser")
                    .input(ItemIngredient.item(1L, "minecraft:stone_brick_stairs"))
                    .input(Ingredient.tagged(ResourceTypes.INFUSION, 10L, "mek:bio"))
                    .output(RecipeOutput.item("minecraft:mossy_stone_brick_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:metallurgic_infusing/mossy/stone_brick_wall", "mek:metallurgic_infuser")
                    .input(ItemIngredient.item(1L, "minecraft:stone_brick_wall"))
                    .input(Ingredient.tagged(ResourceTypes.INFUSION, 10L, "mek:bio"))
                    .output(RecipeOutput.item("minecraft:mossy_stone_brick_wall", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:metallurgic_infusing/netherrack_to_crimson_nylium", "mek:metallurgic_infuser")
                    .input(ItemIngredient.tagged(1L, "mek:netherracks"))
                    .input(Ingredient.tagged(ResourceTypes.INFUSION, 10L, "mek:fungi"))
                    .output(RecipeOutput.item("minecraft:crimson_nylium", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:metallurgic_infusing/sand_to_dirt", "mek:metallurgic_infuser")
                    .input(ItemIngredient.tagged(1L, "mek:sands"))
                    .input(Ingredient.tagged(ResourceTypes.INFUSION, 10L, "mek:bio"))
                    .output(RecipeOutput.item("minecraft:dirt", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:nucleosynthesizing/chorus_flower", "mek:antiprotonic_nucleosynthesizer")
                    .input(ItemIngredient.tagged(1L, "mek:flowers/small"))
                    .input(Ingredient.chemical(2L, "mek:antimatter"))
                    .output(RecipeOutput.item("minecraft:chorus_flower", 1L))
                    .ticks(500)
                    .build());
        r.recipe(Recipe.builder("mek:nucleosynthesizing/crossbow", "mek:antiprotonic_nucleosynthesizer")
                    .input(ItemIngredient.item(1L, "minecraft:bow"))
                    .input(Ingredient.chemical(1L, "mek:antimatter"))
                    .output(RecipeOutput.item("minecraft:crossbow", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:nucleosynthesizing/crying_obsidian", "mek:antiprotonic_nucleosynthesizer")
                    .input(ItemIngredient.tagged(1L, "mek:obsidians/normal"))
                    .input(Ingredient.chemical(1L, "mek:antimatter"))
                    .output(RecipeOutput.item("minecraft:crying_obsidian", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:nucleosynthesizing/diamond", "mek:antiprotonic_nucleosynthesizer")
                    .input(ItemIngredient.item(1L, "minecraft:coal"))
                    .input(Ingredient.chemical(4L, "mek:antimatter"))
                    .output(RecipeOutput.item("minecraft:diamond", 1L))
                    .ticks(1000)
                    .build());
        r.recipe(Recipe.builder("mek:nucleosynthesizing/dragon_egg", "mek:antiprotonic_nucleosynthesizer")
                    .input(ItemIngredient.tagged(1L, "mek:eggs"))
                    .input(Ingredient.chemical(4L, "mek:antimatter"))
                    .output(RecipeOutput.item("minecraft:dragon_egg", 1L))
                    .ticks(1000)
                    .build());
        r.recipe(Recipe.builder("mek:nucleosynthesizing/emerald", "mek:antiprotonic_nucleosynthesizer")
                    .input(ItemIngredient.tagged(1L, "mek:gems/diamond"))
                    .input(Ingredient.chemical(4L, "mek:antimatter"))
                    .output(RecipeOutput.item("minecraft:emerald", 1L))
                    .ticks(1000)
                    .build());
        r.recipe(Recipe.builder("mek:nucleosynthesizing/enchanted_golden_apple", "mek:antiprotonic_nucleosynthesizer")
                    .input(ItemIngredient.item(1L, "minecraft:golden_apple"))
                    .input(Ingredient.chemical(3L, "mek:antimatter"))
                    .output(RecipeOutput.item("minecraft:enchanted_golden_apple", 1L))
                    .ticks(750)
                    .build());
        r.recipe(Recipe.builder("mek:nucleosynthesizing/end_crystal", "mek:antiprotonic_nucleosynthesizer")
                    .input(ItemIngredient.item(1L, "minecraft:beacon"))
                    .input(Ingredient.chemical(3L, "mek:antimatter"))
                    .output(RecipeOutput.item("minecraft:end_crystal", 1L))
                    .ticks(750)
                    .build());
        r.recipe(Recipe.builder("mek:nucleosynthesizing/ender_chest", "mek:antiprotonic_nucleosynthesizer")
                    .input(ItemIngredient.tagged(1L, "mek:chests/wooden"))
                    .input(Ingredient.chemical(2L, "mek:antimatter"))
                    .output(RecipeOutput.item("minecraft:ender_chest", 1L))
                    .ticks(500)
                    .build());
        r.recipe(Recipe.builder("mek:nucleosynthesizing/glowstone_block", "mek:antiprotonic_nucleosynthesizer")
                    .input(ItemIngredient.item(1L, "minecraft:yellow_wool"))
                    .input(Ingredient.chemical(2L, "mek:antimatter"))
                    .output(RecipeOutput.item("minecraft:glowstone", 1L))
                    .ticks(500)
                    .build());
        r.recipe(Recipe.builder("mek:nucleosynthesizing/iron", "mek:antiprotonic_nucleosynthesizer")
                    .input(ItemIngredient.tagged(1L, "mek:ingots/tin"))
                    .input(Ingredient.chemical(1L, "mek:antimatter"))
                    .output(RecipeOutput.item("minecraft:iron_ingot", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:nucleosynthesizing/lapis_block", "mek:antiprotonic_nucleosynthesizer")
                    .input(ItemIngredient.item(1L, "minecraft:blue_wool"))
                    .input(Ingredient.chemical(2L, "mek:antimatter"))
                    .output(RecipeOutput.item("minecraft:lapis_block", 1L))
                    .ticks(500)
                    .build());
        r.recipe(Recipe.builder("mek:nucleosynthesizing/nautilus_shell", "mek:antiprotonic_nucleosynthesizer")
                    .input(ItemIngredient.item(1L, "minecraft:flower_pot"))
                    .input(Ingredient.chemical(3L, "mek:antimatter"))
                    .output(RecipeOutput.item("minecraft:nautilus_shell", 1L))
                    .ticks(750)
                    .build());
        r.recipe(Recipe.builder("mek:nucleosynthesizing/quartz_block", "mek:antiprotonic_nucleosynthesizer")
                    .input(ItemIngredient.item(1L, "minecraft:light_gray_wool"))
                    .input(Ingredient.chemical(2L, "mek:antimatter"))
                    .output(RecipeOutput.item("minecraft:quartz_block", 1L))
                    .ticks(500)
                    .build());
        r.recipe(Recipe.builder("mek:nucleosynthesizing/quartz_slab", "mek:antiprotonic_nucleosynthesizer")
                    .input(ItemIngredient.item(1L, "minecraft:light_gray_wool_slab"))
                    .input(Ingredient.chemical(1L, "mek:antimatter"))
                    .output(RecipeOutput.item("minecraft:quartz_slab", 1L))
                    .ticks(250)
                    .build());
        r.recipe(Recipe.builder("mek:nucleosynthesizing/quartz_stairs", "mek:antiprotonic_nucleosynthesizer")
                    .input(ItemIngredient.item(1L, "minecraft:light_gray_wool_stairs"))
                    .input(Ingredient.chemical(2L, "mek:antimatter"))
                    .output(RecipeOutput.item("minecraft:quartz_stairs", 1L))
                    .ticks(500)
                    .build());
        r.recipe(Recipe.builder("mek:nucleosynthesizing/redstone_block", "mek:antiprotonic_nucleosynthesizer")
                    .input(ItemIngredient.item(1L, "minecraft:red_wool"))
                    .input(Ingredient.chemical(2L, "mek:antimatter"))
                    .output(RecipeOutput.item("minecraft:redstone_block", 1L))
                    .ticks(500)
                    .build());
        r.recipe(Recipe.builder("mek:nucleosynthesizing/respawn_anchor", "mek:antiprotonic_nucleosynthesizer")
                    .input(ItemIngredient.tagged(1L, "mek:beds"))
                    .input(Ingredient.chemical(3L, "mek:antimatter"))
                    .output(RecipeOutput.item("minecraft:respawn_anchor", 1L))
                    .ticks(750)
                    .build());
        r.recipe(Recipe.builder("mek:nucleosynthesizing/trident", "mek:antiprotonic_nucleosynthesizer")
                    .input(ItemIngredient.item(1L, "minecraft:diamond_sword"))
                    .input(Ingredient.chemical(4L, "mek:antimatter"))
                    .output(RecipeOutput.item("minecraft:trident", 1L))
                    .ticks(1000)
                    .build());
        r.recipe(Recipe.builder("mek:nucleosynthesizing/wither_skeleton_skull", "mek:antiprotonic_nucleosynthesizer")
                    .input(ItemIngredient.item(1L, "minecraft:skeleton_skull"))
                    .input(Ingredient.chemical(5L, "mek:antimatter"))
                    .output(RecipeOutput.item("minecraft:wither_skeleton_skull", 1L))
                    .ticks(1250)
                    .build());
        r.recipe(Recipe.builder("mek:oxidizing/bio/from_bio_fuel", "mek:chemical_oxidizer")
                    .input(ItemIngredient.tagged(1L, "mek:fuels/bio"))
                    .output(RecipeOutput.chemical("mek:bio", 5L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:oxidizing/bio/from_bio_fuel_block", "mek:chemical_oxidizer")
                    .input(ItemIngredient.tagged(1L, "mek:fuels/block/bio"))
                    .output(RecipeOutput.chemical("mek:bio", 45L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:oxidizing/brine", "mek:chemical_oxidizer")
                    .input(ItemIngredient.tagged(1L, "mek:dusts/salt"))
                    .output(RecipeOutput.chemical("mek:brine", 15L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:oxidizing/carbon/from_charcoal", "mek:chemical_oxidizer")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("mek:dust_charcoal"), ItemMatcher.id("minecraft:charcoal")), "mek:dust_charcoal"))
                    .output(RecipeOutput.chemical("mek:carbon", 20L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:oxidizing/carbon/from_charcoal_block", "mek:chemical_oxidizer")
                    .input(ItemIngredient.tagged(1L, "mek:storage_blocks/charcoal"))
                    .output(RecipeOutput.chemical("mek:carbon", 180L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:oxidizing/carbon/from_coal", "mek:chemical_oxidizer")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("mek:dust_coal"), ItemMatcher.id("minecraft:coal")), "mek:dust_coal"))
                    .output(RecipeOutput.chemical("mek:carbon", 10L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:oxidizing/carbon/from_coal_block", "mek:chemical_oxidizer")
                    .input(ItemIngredient.tagged(1L, "mek:storage_blocks/coal"))
                    .output(RecipeOutput.chemical("mek:carbon", 90L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:oxidizing/carbon/from_enriched", "mek:chemical_oxidizer")
                    .input(ItemIngredient.tagged(1L, "mek:enriched/carbon"))
                    .output(RecipeOutput.chemical("mek:carbon", 80L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:oxidizing/diamond/from_dust", "mek:chemical_oxidizer")
                    .input(ItemIngredient.tagged(1L, "mek:dusts/diamond"))
                    .output(RecipeOutput.chemical("mek:diamond", 10L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:oxidizing/diamond/from_enriched", "mek:chemical_oxidizer")
                    .input(ItemIngredient.tagged(1L, "mek:enriched/diamond"))
                    .output(RecipeOutput.chemical("mek:diamond", 80L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:oxidizing/fungi/from_mushrooms", "mek:chemical_oxidizer")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("minecraft:brown_mushroom"), ItemMatcher.id("minecraft:crimson_fungus"), ItemMatcher.id("minecraft:red_mushroom"), ItemMatcher.id("minecraft:shelf_mushroom"), ItemMatcher.id("minecraft:warped_fungus")), "minecraft:brown_mushroom"))
                    .output(RecipeOutput.chemical("mek:fungi", 10L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:oxidizing/gold/from_dust", "mek:chemical_oxidizer")
                    .input(ItemIngredient.tagged(1L, "mek:dusts/gold"))
                    .output(RecipeOutput.chemical("mek:gold", 10L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:oxidizing/gold/from_enriched", "mek:chemical_oxidizer")
                    .input(ItemIngredient.tagged(1L, "mek:enriched/gold"))
                    .output(RecipeOutput.chemical("mek:gold", 80L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:oxidizing/lithium", "mek:chemical_oxidizer")
                    .input(ItemIngredient.tagged(1L, "mek:dusts/lithium"))
                    .output(RecipeOutput.chemical("mek:lithium", 100L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:oxidizing/redstone/from_block", "mek:chemical_oxidizer")
                    .input(ItemIngredient.tagged(1L, "mek:storage_blocks/redstone"))
                    .output(RecipeOutput.chemical("mek:redstone", 90L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:oxidizing/redstone/from_dust", "mek:chemical_oxidizer")
                    .input(ItemIngredient.tagged(1L, "mek:dusts/redstone"))
                    .output(RecipeOutput.chemical("mek:redstone", 10L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:oxidizing/redstone/from_enriched", "mek:chemical_oxidizer")
                    .input(ItemIngredient.tagged(1L, "mek:enriched/redstone"))
                    .output(RecipeOutput.chemical("mek:redstone", 80L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:oxidizing/refined_obsidian/from_dust", "mek:chemical_oxidizer")
                    .input(ItemIngredient.tagged(1L, "mek:dusts/refined_obsidian"))
                    .output(RecipeOutput.chemical("mek:refined_obsidian", 10L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:oxidizing/refined_obsidian/from_enriched", "mek:chemical_oxidizer")
                    .input(ItemIngredient.tagged(1L, "mek:enriched/obsidian"))
                    .output(RecipeOutput.chemical("mek:refined_obsidian", 80L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:oxidizing/sulfur_dioxide", "mek:chemical_oxidizer")
                    .input(ItemIngredient.tagged(1L, "mek:dusts/sulfur"))
                    .output(RecipeOutput.chemical("mek:sulfur_dioxide", 100L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:oxidizing/tin/from_dust", "mek:chemical_oxidizer")
                    .input(ItemIngredient.tagged(1L, "mek:dusts/tin"))
                    .output(RecipeOutput.chemical("mek:tin", 10L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:oxidizing/tin/from_enriched", "mek:chemical_oxidizer")
                    .input(ItemIngredient.tagged(1L, "mek:enriched/tin"))
                    .output(RecipeOutput.chemical("mek:tin", 80L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:painting/banner/black", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/banners"))
                    .input(Ingredient.chemical(256L, "mek:black"))
                    .output(RecipeOutput.item("minecraft:black_banner", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/banner/blue", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/banners"))
                    .input(Ingredient.chemical(256L, "mek:blue"))
                    .output(RecipeOutput.item("minecraft:blue_banner", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/banner/brown", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/banners"))
                    .input(Ingredient.chemical(256L, "mek:brown"))
                    .output(RecipeOutput.item("minecraft:brown_banner", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/banner/cyan", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/banners"))
                    .input(Ingredient.chemical(256L, "mek:cyan"))
                    .output(RecipeOutput.item("minecraft:cyan_banner", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/banner/gray", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/banners"))
                    .input(Ingredient.chemical(256L, "mek:gray"))
                    .output(RecipeOutput.item("minecraft:gray_banner", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/banner/green", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/banners"))
                    .input(Ingredient.chemical(256L, "mek:green"))
                    .output(RecipeOutput.item("minecraft:green_banner", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/banner/light_blue", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/banners"))
                    .input(Ingredient.chemical(256L, "mek:light_blue"))
                    .output(RecipeOutput.item("minecraft:light_blue_banner", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/banner/light_gray", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/banners"))
                    .input(Ingredient.chemical(256L, "mek:light_gray"))
                    .output(RecipeOutput.item("minecraft:light_gray_banner", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/banner/lime", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/banners"))
                    .input(Ingredient.chemical(256L, "mek:lime"))
                    .output(RecipeOutput.item("minecraft:lime_banner", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/banner/magenta", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/banners"))
                    .input(Ingredient.chemical(256L, "mek:magenta"))
                    .output(RecipeOutput.item("minecraft:magenta_banner", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/banner/orange", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/banners"))
                    .input(Ingredient.chemical(256L, "mek:orange"))
                    .output(RecipeOutput.item("minecraft:orange_banner", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/banner/pink", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/banners"))
                    .input(Ingredient.chemical(256L, "mek:pink"))
                    .output(RecipeOutput.item("minecraft:pink_banner", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/banner/purple", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/banners"))
                    .input(Ingredient.chemical(256L, "mek:purple"))
                    .output(RecipeOutput.item("minecraft:purple_banner", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/banner/red", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/banners"))
                    .input(Ingredient.chemical(256L, "mek:red"))
                    .output(RecipeOutput.item("minecraft:red_banner", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/banner/white", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/banners"))
                    .input(Ingredient.chemical(256L, "mek:white"))
                    .output(RecipeOutput.item("minecraft:white_banner", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/banner/yellow", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/banners"))
                    .input(Ingredient.chemical(256L, "mek:yellow"))
                    .output(RecipeOutput.item("minecraft:yellow_banner", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/bed/black", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/beds"))
                    .input(Ingredient.chemical(256L, "mek:black"))
                    .output(RecipeOutput.item("minecraft:black_bed", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/bed/blue", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/beds"))
                    .input(Ingredient.chemical(256L, "mek:blue"))
                    .output(RecipeOutput.item("minecraft:blue_bed", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/bed/brown", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/beds"))
                    .input(Ingredient.chemical(256L, "mek:brown"))
                    .output(RecipeOutput.item("minecraft:brown_bed", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/bed/cyan", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/beds"))
                    .input(Ingredient.chemical(256L, "mek:cyan"))
                    .output(RecipeOutput.item("minecraft:cyan_bed", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/bed/gray", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/beds"))
                    .input(Ingredient.chemical(256L, "mek:gray"))
                    .output(RecipeOutput.item("minecraft:gray_bed", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/bed/green", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/beds"))
                    .input(Ingredient.chemical(256L, "mek:green"))
                    .output(RecipeOutput.item("minecraft:green_bed", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/bed/light_blue", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/beds"))
                    .input(Ingredient.chemical(256L, "mek:light_blue"))
                    .output(RecipeOutput.item("minecraft:light_blue_bed", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/bed/light_gray", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/beds"))
                    .input(Ingredient.chemical(256L, "mek:light_gray"))
                    .output(RecipeOutput.item("minecraft:light_gray_bed", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/bed/lime", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/beds"))
                    .input(Ingredient.chemical(256L, "mek:lime"))
                    .output(RecipeOutput.item("minecraft:lime_bed", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/bed/magenta", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/beds"))
                    .input(Ingredient.chemical(256L, "mek:magenta"))
                    .output(RecipeOutput.item("minecraft:magenta_bed", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/bed/orange", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/beds"))
                    .input(Ingredient.chemical(256L, "mek:orange"))
                    .output(RecipeOutput.item("minecraft:orange_bed", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/bed/pink", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/beds"))
                    .input(Ingredient.chemical(256L, "mek:pink"))
                    .output(RecipeOutput.item("minecraft:pink_bed", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/bed/purple", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/beds"))
                    .input(Ingredient.chemical(256L, "mek:purple"))
                    .output(RecipeOutput.item("minecraft:purple_bed", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/bed/red", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/beds"))
                    .input(Ingredient.chemical(256L, "mek:red"))
                    .output(RecipeOutput.item("minecraft:red_bed", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/bed/white", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/beds"))
                    .input(Ingredient.chemical(256L, "mek:white"))
                    .output(RecipeOutput.item("minecraft:white_bed", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/bed/yellow", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/beds"))
                    .input(Ingredient.chemical(256L, "mek:yellow"))
                    .output(RecipeOutput.item("minecraft:yellow_bed", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/candle/black", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/candle"))
                    .input(Ingredient.chemical(256L, "mek:black"))
                    .output(RecipeOutput.item("minecraft:black_candle", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/candle/blue", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/candle"))
                    .input(Ingredient.chemical(256L, "mek:blue"))
                    .output(RecipeOutput.item("minecraft:blue_candle", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/candle/brown", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/candle"))
                    .input(Ingredient.chemical(256L, "mek:brown"))
                    .output(RecipeOutput.item("minecraft:brown_candle", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/candle/cyan", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/candle"))
                    .input(Ingredient.chemical(256L, "mek:cyan"))
                    .output(RecipeOutput.item("minecraft:cyan_candle", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/candle/gray", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/candle"))
                    .input(Ingredient.chemical(256L, "mek:gray"))
                    .output(RecipeOutput.item("minecraft:gray_candle", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/candle/green", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/candle"))
                    .input(Ingredient.chemical(256L, "mek:green"))
                    .output(RecipeOutput.item("minecraft:green_candle", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/candle/light_blue", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/candle"))
                    .input(Ingredient.chemical(256L, "mek:light_blue"))
                    .output(RecipeOutput.item("minecraft:light_blue_candle", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/candle/light_gray", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/candle"))
                    .input(Ingredient.chemical(256L, "mek:light_gray"))
                    .output(RecipeOutput.item("minecraft:light_gray_candle", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/candle/lime", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/candle"))
                    .input(Ingredient.chemical(256L, "mek:lime"))
                    .output(RecipeOutput.item("minecraft:lime_candle", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/candle/magenta", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/candle"))
                    .input(Ingredient.chemical(256L, "mek:magenta"))
                    .output(RecipeOutput.item("minecraft:magenta_candle", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/candle/orange", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/candle"))
                    .input(Ingredient.chemical(256L, "mek:orange"))
                    .output(RecipeOutput.item("minecraft:orange_candle", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/candle/pink", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/candle"))
                    .input(Ingredient.chemical(256L, "mek:pink"))
                    .output(RecipeOutput.item("minecraft:pink_candle", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/candle/purple", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/candle"))
                    .input(Ingredient.chemical(256L, "mek:purple"))
                    .output(RecipeOutput.item("minecraft:purple_candle", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/candle/red", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/candle"))
                    .input(Ingredient.chemical(256L, "mek:red"))
                    .output(RecipeOutput.item("minecraft:red_candle", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/candle/white", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/candle"))
                    .input(Ingredient.chemical(256L, "mek:white"))
                    .output(RecipeOutput.item("minecraft:white_candle", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/candle/yellow", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/candle"))
                    .input(Ingredient.chemical(256L, "mek:yellow"))
                    .output(RecipeOutput.item("minecraft:yellow_candle", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/carpet/black", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/carpets"))
                    .input(Ingredient.chemical(32L, "mek:black"))
                    .output(RecipeOutput.item("minecraft:black_carpet", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/carpet/blue", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/carpets"))
                    .input(Ingredient.chemical(32L, "mek:blue"))
                    .output(RecipeOutput.item("minecraft:blue_carpet", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/carpet/brown", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/carpets"))
                    .input(Ingredient.chemical(32L, "mek:brown"))
                    .output(RecipeOutput.item("minecraft:brown_carpet", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/carpet/cyan", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/carpets"))
                    .input(Ingredient.chemical(32L, "mek:cyan"))
                    .output(RecipeOutput.item("minecraft:cyan_carpet", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/carpet/gray", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/carpets"))
                    .input(Ingredient.chemical(32L, "mek:gray"))
                    .output(RecipeOutput.item("minecraft:gray_carpet", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/carpet/green", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/carpets"))
                    .input(Ingredient.chemical(32L, "mek:green"))
                    .output(RecipeOutput.item("minecraft:green_carpet", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/carpet/light_blue", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/carpets"))
                    .input(Ingredient.chemical(32L, "mek:light_blue"))
                    .output(RecipeOutput.item("minecraft:light_blue_carpet", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/carpet/light_gray", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/carpets"))
                    .input(Ingredient.chemical(32L, "mek:light_gray"))
                    .output(RecipeOutput.item("minecraft:light_gray_carpet", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/carpet/lime", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/carpets"))
                    .input(Ingredient.chemical(32L, "mek:lime"))
                    .output(RecipeOutput.item("minecraft:lime_carpet", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/carpet/magenta", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/carpets"))
                    .input(Ingredient.chemical(32L, "mek:magenta"))
                    .output(RecipeOutput.item("minecraft:magenta_carpet", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/carpet/orange", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/carpets"))
                    .input(Ingredient.chemical(32L, "mek:orange"))
                    .output(RecipeOutput.item("minecraft:orange_carpet", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/carpet/pink", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/carpets"))
                    .input(Ingredient.chemical(32L, "mek:pink"))
                    .output(RecipeOutput.item("minecraft:pink_carpet", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/carpet/purple", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/carpets"))
                    .input(Ingredient.chemical(32L, "mek:purple"))
                    .output(RecipeOutput.item("minecraft:purple_carpet", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/carpet/red", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/carpets"))
                    .input(Ingredient.chemical(32L, "mek:red"))
                    .output(RecipeOutput.item("minecraft:red_carpet", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/carpet/white", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/carpets"))
                    .input(Ingredient.chemical(32L, "mek:white"))
                    .output(RecipeOutput.item("minecraft:white_carpet", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/carpet/yellow", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/carpets"))
                    .input(Ingredient.chemical(32L, "mek:yellow"))
                    .output(RecipeOutput.item("minecraft:yellow_carpet", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/black", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete"))
                    .input(Ingredient.chemical(32L, "mek:black"))
                    .output(RecipeOutput.item("minecraft:black_concrete", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/blue", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete"))
                    .input(Ingredient.chemical(32L, "mek:blue"))
                    .output(RecipeOutput.item("minecraft:blue_concrete", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/brown", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete"))
                    .input(Ingredient.chemical(32L, "mek:brown"))
                    .output(RecipeOutput.item("minecraft:brown_concrete", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/cyan", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete"))
                    .input(Ingredient.chemical(32L, "mek:cyan"))
                    .output(RecipeOutput.item("minecraft:cyan_concrete", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/gray", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete"))
                    .input(Ingredient.chemical(32L, "mek:gray"))
                    .output(RecipeOutput.item("minecraft:gray_concrete", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/green", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete"))
                    .input(Ingredient.chemical(32L, "mek:green"))
                    .output(RecipeOutput.item("minecraft:green_concrete", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/light_blue", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete"))
                    .input(Ingredient.chemical(32L, "mek:light_blue"))
                    .output(RecipeOutput.item("minecraft:light_blue_concrete", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/light_gray", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete"))
                    .input(Ingredient.chemical(32L, "mek:light_gray"))
                    .output(RecipeOutput.item("minecraft:light_gray_concrete", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/lime", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete"))
                    .input(Ingredient.chemical(32L, "mek:lime"))
                    .output(RecipeOutput.item("minecraft:lime_concrete", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/magenta", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete"))
                    .input(Ingredient.chemical(32L, "mek:magenta"))
                    .output(RecipeOutput.item("minecraft:magenta_concrete", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/orange", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete"))
                    .input(Ingredient.chemical(32L, "mek:orange"))
                    .output(RecipeOutput.item("minecraft:orange_concrete", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/pink", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete"))
                    .input(Ingredient.chemical(32L, "mek:pink"))
                    .output(RecipeOutput.item("minecraft:pink_concrete", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/purple", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete"))
                    .input(Ingredient.chemical(32L, "mek:purple"))
                    .output(RecipeOutput.item("minecraft:purple_concrete", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/red", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete"))
                    .input(Ingredient.chemical(32L, "mek:red"))
                    .output(RecipeOutput.item("minecraft:red_concrete", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/slabs/black", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete/slabs"))
                    .input(Ingredient.chemical(32L, "mek:black"))
                    .output(RecipeOutput.item("minecraft:black_concrete_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/slabs/blue", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete/slabs"))
                    .input(Ingredient.chemical(32L, "mek:blue"))
                    .output(RecipeOutput.item("minecraft:blue_concrete_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/slabs/brown", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete/slabs"))
                    .input(Ingredient.chemical(32L, "mek:brown"))
                    .output(RecipeOutput.item("minecraft:brown_concrete_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/slabs/cyan", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete/slabs"))
                    .input(Ingredient.chemical(32L, "mek:cyan"))
                    .output(RecipeOutput.item("minecraft:cyan_concrete_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/slabs/gray", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete/slabs"))
                    .input(Ingredient.chemical(32L, "mek:gray"))
                    .output(RecipeOutput.item("minecraft:gray_concrete_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/slabs/green", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete/slabs"))
                    .input(Ingredient.chemical(32L, "mek:green"))
                    .output(RecipeOutput.item("minecraft:green_concrete_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/slabs/light_blue", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete/slabs"))
                    .input(Ingredient.chemical(32L, "mek:light_blue"))
                    .output(RecipeOutput.item("minecraft:light_blue_concrete_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/slabs/light_gray", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete/slabs"))
                    .input(Ingredient.chemical(32L, "mek:light_gray"))
                    .output(RecipeOutput.item("minecraft:light_gray_concrete_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/slabs/lime", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete/slabs"))
                    .input(Ingredient.chemical(32L, "mek:lime"))
                    .output(RecipeOutput.item("minecraft:lime_concrete_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/slabs/magenta", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete/slabs"))
                    .input(Ingredient.chemical(32L, "mek:magenta"))
                    .output(RecipeOutput.item("minecraft:magenta_concrete_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/slabs/orange", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete/slabs"))
                    .input(Ingredient.chemical(32L, "mek:orange"))
                    .output(RecipeOutput.item("minecraft:orange_concrete_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/slabs/pink", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete/slabs"))
                    .input(Ingredient.chemical(32L, "mek:pink"))
                    .output(RecipeOutput.item("minecraft:pink_concrete_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/slabs/purple", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete/slabs"))
                    .input(Ingredient.chemical(32L, "mek:purple"))
                    .output(RecipeOutput.item("minecraft:purple_concrete_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/slabs/red", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete/slabs"))
                    .input(Ingredient.chemical(32L, "mek:red"))
                    .output(RecipeOutput.item("minecraft:red_concrete_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/slabs/white", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete/slabs"))
                    .input(Ingredient.chemical(32L, "mek:white"))
                    .output(RecipeOutput.item("minecraft:white_concrete_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/slabs/yellow", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete/slabs"))
                    .input(Ingredient.chemical(32L, "mek:yellow"))
                    .output(RecipeOutput.item("minecraft:yellow_concrete_slab", 1L))
                    .ticks(200)
                    .build());
    }

    private static void recipes5(Registrars r) {
        r.recipe(Recipe.builder("mek:painting/concrete/stairs/black", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete/stairs"))
                    .input(Ingredient.chemical(32L, "mek:black"))
                    .output(RecipeOutput.item("minecraft:black_concrete_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/stairs/blue", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete/stairs"))
                    .input(Ingredient.chemical(32L, "mek:blue"))
                    .output(RecipeOutput.item("minecraft:blue_concrete_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/stairs/brown", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete/stairs"))
                    .input(Ingredient.chemical(32L, "mek:brown"))
                    .output(RecipeOutput.item("minecraft:brown_concrete_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/stairs/cyan", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete/stairs"))
                    .input(Ingredient.chemical(32L, "mek:cyan"))
                    .output(RecipeOutput.item("minecraft:cyan_concrete_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/stairs/gray", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete/stairs"))
                    .input(Ingredient.chemical(32L, "mek:gray"))
                    .output(RecipeOutput.item("minecraft:gray_concrete_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/stairs/green", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete/stairs"))
                    .input(Ingredient.chemical(32L, "mek:green"))
                    .output(RecipeOutput.item("minecraft:green_concrete_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/stairs/light_blue", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete/stairs"))
                    .input(Ingredient.chemical(32L, "mek:light_blue"))
                    .output(RecipeOutput.item("minecraft:light_blue_concrete_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/stairs/light_gray", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete/stairs"))
                    .input(Ingredient.chemical(32L, "mek:light_gray"))
                    .output(RecipeOutput.item("minecraft:light_gray_concrete_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/stairs/lime", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete/stairs"))
                    .input(Ingredient.chemical(32L, "mek:lime"))
                    .output(RecipeOutput.item("minecraft:lime_concrete_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/stairs/magenta", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete/stairs"))
                    .input(Ingredient.chemical(32L, "mek:magenta"))
                    .output(RecipeOutput.item("minecraft:magenta_concrete_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/stairs/orange", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete/stairs"))
                    .input(Ingredient.chemical(32L, "mek:orange"))
                    .output(RecipeOutput.item("minecraft:orange_concrete_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/stairs/pink", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete/stairs"))
                    .input(Ingredient.chemical(32L, "mek:pink"))
                    .output(RecipeOutput.item("minecraft:pink_concrete_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/stairs/purple", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete/stairs"))
                    .input(Ingredient.chemical(32L, "mek:purple"))
                    .output(RecipeOutput.item("minecraft:purple_concrete_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/stairs/red", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete/stairs"))
                    .input(Ingredient.chemical(32L, "mek:red"))
                    .output(RecipeOutput.item("minecraft:red_concrete_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/stairs/white", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete/stairs"))
                    .input(Ingredient.chemical(32L, "mek:white"))
                    .output(RecipeOutput.item("minecraft:white_concrete_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/stairs/yellow", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete/stairs"))
                    .input(Ingredient.chemical(32L, "mek:yellow"))
                    .output(RecipeOutput.item("minecraft:yellow_concrete_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/white", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete"))
                    .input(Ingredient.chemical(32L, "mek:white"))
                    .output(RecipeOutput.item("minecraft:white_concrete", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete/yellow", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete"))
                    .input(Ingredient.chemical(32L, "mek:yellow"))
                    .output(RecipeOutput.item("minecraft:yellow_concrete", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete_powder/black", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete_powder"))
                    .input(Ingredient.chemical(32L, "mek:black"))
                    .output(RecipeOutput.item("minecraft:black_concrete_powder", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete_powder/blue", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete_powder"))
                    .input(Ingredient.chemical(32L, "mek:blue"))
                    .output(RecipeOutput.item("minecraft:blue_concrete_powder", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete_powder/brown", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete_powder"))
                    .input(Ingredient.chemical(32L, "mek:brown"))
                    .output(RecipeOutput.item("minecraft:brown_concrete_powder", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete_powder/cyan", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete_powder"))
                    .input(Ingredient.chemical(32L, "mek:cyan"))
                    .output(RecipeOutput.item("minecraft:cyan_concrete_powder", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete_powder/gray", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete_powder"))
                    .input(Ingredient.chemical(32L, "mek:gray"))
                    .output(RecipeOutput.item("minecraft:gray_concrete_powder", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete_powder/green", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete_powder"))
                    .input(Ingredient.chemical(32L, "mek:green"))
                    .output(RecipeOutput.item("minecraft:green_concrete_powder", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete_powder/light_blue", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete_powder"))
                    .input(Ingredient.chemical(32L, "mek:light_blue"))
                    .output(RecipeOutput.item("minecraft:light_blue_concrete_powder", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete_powder/light_gray", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete_powder"))
                    .input(Ingredient.chemical(32L, "mek:light_gray"))
                    .output(RecipeOutput.item("minecraft:light_gray_concrete_powder", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete_powder/lime", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete_powder"))
                    .input(Ingredient.chemical(32L, "mek:lime"))
                    .output(RecipeOutput.item("minecraft:lime_concrete_powder", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete_powder/magenta", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete_powder"))
                    .input(Ingredient.chemical(32L, "mek:magenta"))
                    .output(RecipeOutput.item("minecraft:magenta_concrete_powder", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete_powder/orange", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete_powder"))
                    .input(Ingredient.chemical(32L, "mek:orange"))
                    .output(RecipeOutput.item("minecraft:orange_concrete_powder", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete_powder/pink", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete_powder"))
                    .input(Ingredient.chemical(32L, "mek:pink"))
                    .output(RecipeOutput.item("minecraft:pink_concrete_powder", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete_powder/purple", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete_powder"))
                    .input(Ingredient.chemical(32L, "mek:purple"))
                    .output(RecipeOutput.item("minecraft:purple_concrete_powder", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete_powder/red", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete_powder"))
                    .input(Ingredient.chemical(32L, "mek:red"))
                    .output(RecipeOutput.item("minecraft:red_concrete_powder", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete_powder/white", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete_powder"))
                    .input(Ingredient.chemical(32L, "mek:white"))
                    .output(RecipeOutput.item("minecraft:white_concrete_powder", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/concrete_powder/yellow", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/concrete_powder"))
                    .input(Ingredient.chemical(32L, "mek:yellow"))
                    .output(RecipeOutput.item("minecraft:yellow_concrete_powder", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/cushion/black", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/cushions"))
                    .input(Ingredient.chemical(256L, "mek:black"))
                    .output(RecipeOutput.item("minecraft:black_cushion", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/cushion/blue", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/cushions"))
                    .input(Ingredient.chemical(256L, "mek:blue"))
                    .output(RecipeOutput.item("minecraft:blue_cushion", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/cushion/brown", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/cushions"))
                    .input(Ingredient.chemical(256L, "mek:brown"))
                    .output(RecipeOutput.item("minecraft:brown_cushion", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/cushion/cyan", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/cushions"))
                    .input(Ingredient.chemical(256L, "mek:cyan"))
                    .output(RecipeOutput.item("minecraft:cyan_cushion", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/cushion/gray", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/cushions"))
                    .input(Ingredient.chemical(256L, "mek:gray"))
                    .output(RecipeOutput.item("minecraft:gray_cushion", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/cushion/green", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/cushions"))
                    .input(Ingredient.chemical(256L, "mek:green"))
                    .output(RecipeOutput.item("minecraft:green_cushion", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/cushion/light_blue", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/cushions"))
                    .input(Ingredient.chemical(256L, "mek:light_blue"))
                    .output(RecipeOutput.item("minecraft:light_blue_cushion", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/cushion/light_gray", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/cushions"))
                    .input(Ingredient.chemical(256L, "mek:light_gray"))
                    .output(RecipeOutput.item("minecraft:light_gray_cushion", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/cushion/lime", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/cushions"))
                    .input(Ingredient.chemical(256L, "mek:lime"))
                    .output(RecipeOutput.item("minecraft:lime_cushion", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/cushion/magenta", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/cushions"))
                    .input(Ingredient.chemical(256L, "mek:magenta"))
                    .output(RecipeOutput.item("minecraft:magenta_cushion", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/cushion/orange", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/cushions"))
                    .input(Ingredient.chemical(256L, "mek:orange"))
                    .output(RecipeOutput.item("minecraft:orange_cushion", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/cushion/pink", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/cushions"))
                    .input(Ingredient.chemical(256L, "mek:pink"))
                    .output(RecipeOutput.item("minecraft:pink_cushion", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/cushion/purple", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/cushions"))
                    .input(Ingredient.chemical(256L, "mek:purple"))
                    .output(RecipeOutput.item("minecraft:purple_cushion", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/cushion/red", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/cushions"))
                    .input(Ingredient.chemical(256L, "mek:red"))
                    .output(RecipeOutput.item("minecraft:red_cushion", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/cushion/white", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/cushions"))
                    .input(Ingredient.chemical(256L, "mek:white"))
                    .output(RecipeOutput.item("minecraft:white_cushion", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/cushion/yellow", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/cushions"))
                    .input(Ingredient.chemical(256L, "mek:yellow"))
                    .output(RecipeOutput.item("minecraft:yellow_cushion", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/dye/black", "mek:painting_machine")
                    .input(ItemIngredient.item(1L, "mek:dye_base"))
                    .input(Ingredient.chemical(256L, "mek:black"))
                    .output(RecipeOutput.item("minecraft:black_dye", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/dye/blue", "mek:painting_machine")
                    .input(ItemIngredient.item(1L, "mek:dye_base"))
                    .input(Ingredient.chemical(256L, "mek:blue"))
                    .output(RecipeOutput.item("minecraft:blue_dye", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/dye/brown", "mek:painting_machine")
                    .input(ItemIngredient.item(1L, "mek:dye_base"))
                    .input(Ingredient.chemical(256L, "mek:brown"))
                    .output(RecipeOutput.item("minecraft:brown_dye", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/dye/cyan", "mek:painting_machine")
                    .input(ItemIngredient.item(1L, "mek:dye_base"))
                    .input(Ingredient.chemical(256L, "mek:cyan"))
                    .output(RecipeOutput.item("minecraft:cyan_dye", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/dye/gray", "mek:painting_machine")
                    .input(ItemIngredient.item(1L, "mek:dye_base"))
                    .input(Ingredient.chemical(256L, "mek:gray"))
                    .output(RecipeOutput.item("minecraft:gray_dye", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/dye/green", "mek:painting_machine")
                    .input(ItemIngredient.item(1L, "mek:dye_base"))
                    .input(Ingredient.chemical(256L, "mek:green"))
                    .output(RecipeOutput.item("minecraft:green_dye", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/dye/light_blue", "mek:painting_machine")
                    .input(ItemIngredient.item(1L, "mek:dye_base"))
                    .input(Ingredient.chemical(256L, "mek:light_blue"))
                    .output(RecipeOutput.item("minecraft:light_blue_dye", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/dye/light_gray", "mek:painting_machine")
                    .input(ItemIngredient.item(1L, "mek:dye_base"))
                    .input(Ingredient.chemical(256L, "mek:light_gray"))
                    .output(RecipeOutput.item("minecraft:light_gray_dye", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/dye/lime", "mek:painting_machine")
                    .input(ItemIngredient.item(1L, "mek:dye_base"))
                    .input(Ingredient.chemical(256L, "mek:lime"))
                    .output(RecipeOutput.item("minecraft:lime_dye", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/dye/magenta", "mek:painting_machine")
                    .input(ItemIngredient.item(1L, "mek:dye_base"))
                    .input(Ingredient.chemical(256L, "mek:magenta"))
                    .output(RecipeOutput.item("minecraft:magenta_dye", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/dye/orange", "mek:painting_machine")
                    .input(ItemIngredient.item(1L, "mek:dye_base"))
                    .input(Ingredient.chemical(256L, "mek:orange"))
                    .output(RecipeOutput.item("minecraft:orange_dye", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/dye/pink", "mek:painting_machine")
                    .input(ItemIngredient.item(1L, "mek:dye_base"))
                    .input(Ingredient.chemical(256L, "mek:pink"))
                    .output(RecipeOutput.item("minecraft:pink_dye", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/dye/purple", "mek:painting_machine")
                    .input(ItemIngredient.item(1L, "mek:dye_base"))
                    .input(Ingredient.chemical(256L, "mek:purple"))
                    .output(RecipeOutput.item("minecraft:purple_dye", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/dye/red", "mek:painting_machine")
                    .input(ItemIngredient.item(1L, "mek:dye_base"))
                    .input(Ingredient.chemical(256L, "mek:red"))
                    .output(RecipeOutput.item("minecraft:red_dye", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/dye/white", "mek:painting_machine")
                    .input(ItemIngredient.item(1L, "mek:dye_base"))
                    .input(Ingredient.chemical(256L, "mek:white"))
                    .output(RecipeOutput.item("minecraft:white_dye", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/dye/yellow", "mek:painting_machine")
                    .input(ItemIngredient.item(1L, "mek:dye_base"))
                    .input(Ingredient.chemical(256L, "mek:yellow"))
                    .output(RecipeOutput.item("minecraft:yellow_dye", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/glass/black", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glass"))
                    .input(Ingredient.chemical(32L, "mek:black"))
                    .output(RecipeOutput.item("minecraft:black_stained_glass", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/glass/blue", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glass"))
                    .input(Ingredient.chemical(32L, "mek:blue"))
                    .output(RecipeOutput.item("minecraft:blue_stained_glass", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/glass/brown", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glass"))
                    .input(Ingredient.chemical(32L, "mek:brown"))
                    .output(RecipeOutput.item("minecraft:brown_stained_glass", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/glass/cyan", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glass"))
                    .input(Ingredient.chemical(32L, "mek:cyan"))
                    .output(RecipeOutput.item("minecraft:cyan_stained_glass", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/glass/gray", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glass"))
                    .input(Ingredient.chemical(32L, "mek:gray"))
                    .output(RecipeOutput.item("minecraft:gray_stained_glass", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/glass/green", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glass"))
                    .input(Ingredient.chemical(32L, "mek:green"))
                    .output(RecipeOutput.item("minecraft:green_stained_glass", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/glass/light_blue", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glass"))
                    .input(Ingredient.chemical(32L, "mek:light_blue"))
                    .output(RecipeOutput.item("minecraft:light_blue_stained_glass", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/glass/light_gray", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glass"))
                    .input(Ingredient.chemical(32L, "mek:light_gray"))
                    .output(RecipeOutput.item("minecraft:light_gray_stained_glass", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/glass/lime", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glass"))
                    .input(Ingredient.chemical(32L, "mek:lime"))
                    .output(RecipeOutput.item("minecraft:lime_stained_glass", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/glass/magenta", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glass"))
                    .input(Ingredient.chemical(32L, "mek:magenta"))
                    .output(RecipeOutput.item("minecraft:magenta_stained_glass", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/glass/orange", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glass"))
                    .input(Ingredient.chemical(32L, "mek:orange"))
                    .output(RecipeOutput.item("minecraft:orange_stained_glass", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/glass/pink", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glass"))
                    .input(Ingredient.chemical(32L, "mek:pink"))
                    .output(RecipeOutput.item("minecraft:pink_stained_glass", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/glass/purple", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glass"))
                    .input(Ingredient.chemical(32L, "mek:purple"))
                    .output(RecipeOutput.item("minecraft:purple_stained_glass", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/glass/red", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glass"))
                    .input(Ingredient.chemical(32L, "mek:red"))
                    .output(RecipeOutput.item("minecraft:red_stained_glass", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/glass/white", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glass"))
                    .input(Ingredient.chemical(32L, "mek:white"))
                    .output(RecipeOutput.item("minecraft:white_stained_glass", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/glass/yellow", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glass"))
                    .input(Ingredient.chemical(32L, "mek:yellow"))
                    .output(RecipeOutput.item("minecraft:yellow_stained_glass", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/glass_pane/black", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glass_panes"))
                    .input(Ingredient.chemical(32L, "mek:black"))
                    .output(RecipeOutput.item("minecraft:black_stained_glass_pane", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/glass_pane/blue", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glass_panes"))
                    .input(Ingredient.chemical(32L, "mek:blue"))
                    .output(RecipeOutput.item("minecraft:blue_stained_glass_pane", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/glass_pane/brown", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glass_panes"))
                    .input(Ingredient.chemical(32L, "mek:brown"))
                    .output(RecipeOutput.item("minecraft:brown_stained_glass_pane", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/glass_pane/cyan", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glass_panes"))
                    .input(Ingredient.chemical(32L, "mek:cyan"))
                    .output(RecipeOutput.item("minecraft:cyan_stained_glass_pane", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/glass_pane/gray", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glass_panes"))
                    .input(Ingredient.chemical(32L, "mek:gray"))
                    .output(RecipeOutput.item("minecraft:gray_stained_glass_pane", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/glass_pane/green", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glass_panes"))
                    .input(Ingredient.chemical(32L, "mek:green"))
                    .output(RecipeOutput.item("minecraft:green_stained_glass_pane", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/glass_pane/light_blue", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glass_panes"))
                    .input(Ingredient.chemical(32L, "mek:light_blue"))
                    .output(RecipeOutput.item("minecraft:light_blue_stained_glass_pane", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/glass_pane/light_gray", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glass_panes"))
                    .input(Ingredient.chemical(32L, "mek:light_gray"))
                    .output(RecipeOutput.item("minecraft:light_gray_stained_glass_pane", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/glass_pane/lime", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glass_panes"))
                    .input(Ingredient.chemical(32L, "mek:lime"))
                    .output(RecipeOutput.item("minecraft:lime_stained_glass_pane", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/glass_pane/magenta", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glass_panes"))
                    .input(Ingredient.chemical(32L, "mek:magenta"))
                    .output(RecipeOutput.item("minecraft:magenta_stained_glass_pane", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/glass_pane/orange", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glass_panes"))
                    .input(Ingredient.chemical(32L, "mek:orange"))
                    .output(RecipeOutput.item("minecraft:orange_stained_glass_pane", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/glass_pane/pink", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glass_panes"))
                    .input(Ingredient.chemical(32L, "mek:pink"))
                    .output(RecipeOutput.item("minecraft:pink_stained_glass_pane", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/glass_pane/purple", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glass_panes"))
                    .input(Ingredient.chemical(32L, "mek:purple"))
                    .output(RecipeOutput.item("minecraft:purple_stained_glass_pane", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/glass_pane/red", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glass_panes"))
                    .input(Ingredient.chemical(32L, "mek:red"))
                    .output(RecipeOutput.item("minecraft:red_stained_glass_pane", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/glass_pane/white", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glass_panes"))
                    .input(Ingredient.chemical(32L, "mek:white"))
                    .output(RecipeOutput.item("minecraft:white_stained_glass_pane", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/glass_pane/yellow", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glass_panes"))
                    .input(Ingredient.chemical(32L, "mek:yellow"))
                    .output(RecipeOutput.item("minecraft:yellow_stained_glass_pane", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/harness/black", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/harnesses"))
                    .input(Ingredient.chemical(256L, "mek:black"))
                    .output(RecipeOutput.item("minecraft:black_harness", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/harness/blue", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/harnesses"))
                    .input(Ingredient.chemical(256L, "mek:blue"))
                    .output(RecipeOutput.item("minecraft:blue_harness", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/harness/brown", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/harnesses"))
                    .input(Ingredient.chemical(256L, "mek:brown"))
                    .output(RecipeOutput.item("minecraft:brown_harness", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/harness/cyan", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/harnesses"))
                    .input(Ingredient.chemical(256L, "mek:cyan"))
                    .output(RecipeOutput.item("minecraft:cyan_harness", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/harness/gray", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/harnesses"))
                    .input(Ingredient.chemical(256L, "mek:gray"))
                    .output(RecipeOutput.item("minecraft:gray_harness", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/harness/green", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/harnesses"))
                    .input(Ingredient.chemical(256L, "mek:green"))
                    .output(RecipeOutput.item("minecraft:green_harness", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/harness/light_blue", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/harnesses"))
                    .input(Ingredient.chemical(256L, "mek:light_blue"))
                    .output(RecipeOutput.item("minecraft:light_blue_harness", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/harness/light_gray", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/harnesses"))
                    .input(Ingredient.chemical(256L, "mek:light_gray"))
                    .output(RecipeOutput.item("minecraft:light_gray_harness", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/harness/lime", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/harnesses"))
                    .input(Ingredient.chemical(256L, "mek:lime"))
                    .output(RecipeOutput.item("minecraft:lime_harness", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/harness/magenta", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/harnesses"))
                    .input(Ingredient.chemical(256L, "mek:magenta"))
                    .output(RecipeOutput.item("minecraft:magenta_harness", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/harness/orange", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/harnesses"))
                    .input(Ingredient.chemical(256L, "mek:orange"))
                    .output(RecipeOutput.item("minecraft:orange_harness", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/harness/pink", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/harnesses"))
                    .input(Ingredient.chemical(256L, "mek:pink"))
                    .output(RecipeOutput.item("minecraft:pink_harness", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/harness/purple", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/harnesses"))
                    .input(Ingredient.chemical(256L, "mek:purple"))
                    .output(RecipeOutput.item("minecraft:purple_harness", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/harness/red", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/harnesses"))
                    .input(Ingredient.chemical(256L, "mek:red"))
                    .output(RecipeOutput.item("minecraft:red_harness", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/harness/white", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/harnesses"))
                    .input(Ingredient.chemical(256L, "mek:white"))
                    .output(RecipeOutput.item("minecraft:white_harness", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/harness/yellow", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/harnesses"))
                    .input(Ingredient.chemical(256L, "mek:yellow"))
                    .output(RecipeOutput.item("minecraft:yellow_harness", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/terracotta/black", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/terracotta"))
                    .input(Ingredient.chemical(32L, "mek:black"))
                    .output(RecipeOutput.item("minecraft:black_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/terracotta/blue", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/terracotta"))
                    .input(Ingredient.chemical(32L, "mek:blue"))
                    .output(RecipeOutput.item("minecraft:blue_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/terracotta/brown", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/terracotta"))
                    .input(Ingredient.chemical(32L, "mek:brown"))
                    .output(RecipeOutput.item("minecraft:brown_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/terracotta/cyan", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/terracotta"))
                    .input(Ingredient.chemical(32L, "mek:cyan"))
                    .output(RecipeOutput.item("minecraft:cyan_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/terracotta/glazed/black", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glazed_terracotta"))
                    .input(Ingredient.chemical(64L, "mek:black"))
                    .output(RecipeOutput.item("minecraft:black_glazed_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/terracotta/glazed/blue", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glazed_terracotta"))
                    .input(Ingredient.chemical(64L, "mek:blue"))
                    .output(RecipeOutput.item("minecraft:blue_glazed_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/terracotta/glazed/brown", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glazed_terracotta"))
                    .input(Ingredient.chemical(64L, "mek:brown"))
                    .output(RecipeOutput.item("minecraft:brown_glazed_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/terracotta/glazed/cyan", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glazed_terracotta"))
                    .input(Ingredient.chemical(64L, "mek:cyan"))
                    .output(RecipeOutput.item("minecraft:cyan_glazed_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/terracotta/glazed/gray", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glazed_terracotta"))
                    .input(Ingredient.chemical(64L, "mek:gray"))
                    .output(RecipeOutput.item("minecraft:gray_glazed_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/terracotta/glazed/green", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glazed_terracotta"))
                    .input(Ingredient.chemical(64L, "mek:green"))
                    .output(RecipeOutput.item("minecraft:green_glazed_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/terracotta/glazed/light_blue", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glazed_terracotta"))
                    .input(Ingredient.chemical(64L, "mek:light_blue"))
                    .output(RecipeOutput.item("minecraft:light_blue_glazed_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/terracotta/glazed/light_gray", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glazed_terracotta"))
                    .input(Ingredient.chemical(64L, "mek:light_gray"))
                    .output(RecipeOutput.item("minecraft:light_gray_glazed_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/terracotta/glazed/lime", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glazed_terracotta"))
                    .input(Ingredient.chemical(64L, "mek:lime"))
                    .output(RecipeOutput.item("minecraft:lime_glazed_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/terracotta/glazed/magenta", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glazed_terracotta"))
                    .input(Ingredient.chemical(64L, "mek:magenta"))
                    .output(RecipeOutput.item("minecraft:magenta_glazed_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/terracotta/glazed/orange", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glazed_terracotta"))
                    .input(Ingredient.chemical(64L, "mek:orange"))
                    .output(RecipeOutput.item("minecraft:orange_glazed_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/terracotta/glazed/pink", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glazed_terracotta"))
                    .input(Ingredient.chemical(64L, "mek:pink"))
                    .output(RecipeOutput.item("minecraft:pink_glazed_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/terracotta/glazed/purple", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glazed_terracotta"))
                    .input(Ingredient.chemical(64L, "mek:purple"))
                    .output(RecipeOutput.item("minecraft:purple_glazed_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/terracotta/glazed/red", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glazed_terracotta"))
                    .input(Ingredient.chemical(64L, "mek:red"))
                    .output(RecipeOutput.item("minecraft:red_glazed_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/terracotta/glazed/white", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glazed_terracotta"))
                    .input(Ingredient.chemical(64L, "mek:white"))
                    .output(RecipeOutput.item("minecraft:white_glazed_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/terracotta/glazed/yellow", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/glazed_terracotta"))
                    .input(Ingredient.chemical(64L, "mek:yellow"))
                    .output(RecipeOutput.item("minecraft:yellow_glazed_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/terracotta/gray", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/terracotta"))
                    .input(Ingredient.chemical(32L, "mek:gray"))
                    .output(RecipeOutput.item("minecraft:gray_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/terracotta/green", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/terracotta"))
                    .input(Ingredient.chemical(32L, "mek:green"))
                    .output(RecipeOutput.item("minecraft:green_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/terracotta/light_blue", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/terracotta"))
                    .input(Ingredient.chemical(32L, "mek:light_blue"))
                    .output(RecipeOutput.item("minecraft:light_blue_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/terracotta/light_gray", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/terracotta"))
                    .input(Ingredient.chemical(32L, "mek:light_gray"))
                    .output(RecipeOutput.item("minecraft:light_gray_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/terracotta/lime", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/terracotta"))
                    .input(Ingredient.chemical(32L, "mek:lime"))
                    .output(RecipeOutput.item("minecraft:lime_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/terracotta/magenta", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/terracotta"))
                    .input(Ingredient.chemical(32L, "mek:magenta"))
                    .output(RecipeOutput.item("minecraft:magenta_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/terracotta/orange", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/terracotta"))
                    .input(Ingredient.chemical(32L, "mek:orange"))
                    .output(RecipeOutput.item("minecraft:orange_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/terracotta/pink", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/terracotta"))
                    .input(Ingredient.chemical(32L, "mek:pink"))
                    .output(RecipeOutput.item("minecraft:pink_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/terracotta/purple", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/terracotta"))
                    .input(Ingredient.chemical(32L, "mek:purple"))
                    .output(RecipeOutput.item("minecraft:purple_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/terracotta/red", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/terracotta"))
                    .input(Ingredient.chemical(32L, "mek:red"))
                    .output(RecipeOutput.item("minecraft:red_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/terracotta/white", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/terracotta"))
                    .input(Ingredient.chemical(32L, "mek:white"))
                    .output(RecipeOutput.item("minecraft:white_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/terracotta/yellow", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/terracotta"))
                    .input(Ingredient.chemical(32L, "mek:yellow"))
                    .output(RecipeOutput.item("minecraft:yellow_terracotta", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/black", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool"))
                    .input(Ingredient.chemical(256L, "mek:black"))
                    .output(RecipeOutput.item("minecraft:black_wool", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/blue", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool"))
                    .input(Ingredient.chemical(256L, "mek:blue"))
                    .output(RecipeOutput.item("minecraft:blue_wool", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/brown", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool"))
                    .input(Ingredient.chemical(256L, "mek:brown"))
                    .output(RecipeOutput.item("minecraft:brown_wool", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/cyan", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool"))
                    .input(Ingredient.chemical(256L, "mek:cyan"))
                    .output(RecipeOutput.item("minecraft:cyan_wool", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/gray", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool"))
                    .input(Ingredient.chemical(256L, "mek:gray"))
                    .output(RecipeOutput.item("minecraft:gray_wool", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/green", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool"))
                    .input(Ingredient.chemical(256L, "mek:green"))
                    .output(RecipeOutput.item("minecraft:green_wool", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/light_blue", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool"))
                    .input(Ingredient.chemical(256L, "mek:light_blue"))
                    .output(RecipeOutput.item("minecraft:light_blue_wool", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/light_gray", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool"))
                    .input(Ingredient.chemical(256L, "mek:light_gray"))
                    .output(RecipeOutput.item("minecraft:light_gray_wool", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/lime", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool"))
                    .input(Ingredient.chemical(256L, "mek:lime"))
                    .output(RecipeOutput.item("minecraft:lime_wool", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/magenta", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool"))
                    .input(Ingredient.chemical(256L, "mek:magenta"))
                    .output(RecipeOutput.item("minecraft:magenta_wool", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/orange", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool"))
                    .input(Ingredient.chemical(256L, "mek:orange"))
                    .output(RecipeOutput.item("minecraft:orange_wool", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/pink", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool"))
                    .input(Ingredient.chemical(256L, "mek:pink"))
                    .output(RecipeOutput.item("minecraft:pink_wool", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/purple", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool"))
                    .input(Ingredient.chemical(256L, "mek:purple"))
                    .output(RecipeOutput.item("minecraft:purple_wool", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/red", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool"))
                    .input(Ingredient.chemical(256L, "mek:red"))
                    .output(RecipeOutput.item("minecraft:red_wool", 1L))
                    .ticks(200)
                    .build());
    }

    private static void recipes6(Registrars r) {
        r.recipe(Recipe.builder("mek:painting/wool/slabs/black", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool_slabs"))
                    .input(Ingredient.chemical(256L, "mek:black"))
                    .output(RecipeOutput.item("minecraft:black_wool_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/slabs/blue", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool_slabs"))
                    .input(Ingredient.chemical(256L, "mek:blue"))
                    .output(RecipeOutput.item("minecraft:blue_wool_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/slabs/brown", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool_slabs"))
                    .input(Ingredient.chemical(256L, "mek:brown"))
                    .output(RecipeOutput.item("minecraft:brown_wool_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/slabs/cyan", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool_slabs"))
                    .input(Ingredient.chemical(256L, "mek:cyan"))
                    .output(RecipeOutput.item("minecraft:cyan_wool_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/slabs/gray", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool_slabs"))
                    .input(Ingredient.chemical(256L, "mek:gray"))
                    .output(RecipeOutput.item("minecraft:gray_wool_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/slabs/green", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool_slabs"))
                    .input(Ingredient.chemical(256L, "mek:green"))
                    .output(RecipeOutput.item("minecraft:green_wool_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/slabs/light_blue", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool_slabs"))
                    .input(Ingredient.chemical(256L, "mek:light_blue"))
                    .output(RecipeOutput.item("minecraft:light_blue_wool_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/slabs/light_gray", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool_slabs"))
                    .input(Ingredient.chemical(256L, "mek:light_gray"))
                    .output(RecipeOutput.item("minecraft:light_gray_wool_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/slabs/lime", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool_slabs"))
                    .input(Ingredient.chemical(256L, "mek:lime"))
                    .output(RecipeOutput.item("minecraft:lime_wool_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/slabs/magenta", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool_slabs"))
                    .input(Ingredient.chemical(256L, "mek:magenta"))
                    .output(RecipeOutput.item("minecraft:magenta_wool_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/slabs/orange", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool_slabs"))
                    .input(Ingredient.chemical(256L, "mek:orange"))
                    .output(RecipeOutput.item("minecraft:orange_wool_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/slabs/pink", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool_slabs"))
                    .input(Ingredient.chemical(256L, "mek:pink"))
                    .output(RecipeOutput.item("minecraft:pink_wool_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/slabs/purple", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool_slabs"))
                    .input(Ingredient.chemical(256L, "mek:purple"))
                    .output(RecipeOutput.item("minecraft:purple_wool_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/slabs/red", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool_slabs"))
                    .input(Ingredient.chemical(256L, "mek:red"))
                    .output(RecipeOutput.item("minecraft:red_wool_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/slabs/white", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool_slabs"))
                    .input(Ingredient.chemical(256L, "mek:white"))
                    .output(RecipeOutput.item("minecraft:white_wool_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/slabs/yellow", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool_slabs"))
                    .input(Ingredient.chemical(256L, "mek:yellow"))
                    .output(RecipeOutput.item("minecraft:yellow_wool_slab", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/stairs/black", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool_stairs"))
                    .input(Ingredient.chemical(256L, "mek:black"))
                    .output(RecipeOutput.item("minecraft:black_wool_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/stairs/blue", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool_stairs"))
                    .input(Ingredient.chemical(256L, "mek:blue"))
                    .output(RecipeOutput.item("minecraft:blue_wool_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/stairs/brown", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool_stairs"))
                    .input(Ingredient.chemical(256L, "mek:brown"))
                    .output(RecipeOutput.item("minecraft:brown_wool_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/stairs/cyan", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool_stairs"))
                    .input(Ingredient.chemical(256L, "mek:cyan"))
                    .output(RecipeOutput.item("minecraft:cyan_wool_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/stairs/gray", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool_stairs"))
                    .input(Ingredient.chemical(256L, "mek:gray"))
                    .output(RecipeOutput.item("minecraft:gray_wool_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/stairs/green", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool_stairs"))
                    .input(Ingredient.chemical(256L, "mek:green"))
                    .output(RecipeOutput.item("minecraft:green_wool_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/stairs/light_blue", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool_stairs"))
                    .input(Ingredient.chemical(256L, "mek:light_blue"))
                    .output(RecipeOutput.item("minecraft:light_blue_wool_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/stairs/light_gray", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool_stairs"))
                    .input(Ingredient.chemical(256L, "mek:light_gray"))
                    .output(RecipeOutput.item("minecraft:light_gray_wool_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/stairs/lime", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool_stairs"))
                    .input(Ingredient.chemical(256L, "mek:lime"))
                    .output(RecipeOutput.item("minecraft:lime_wool_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/stairs/magenta", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool_stairs"))
                    .input(Ingredient.chemical(256L, "mek:magenta"))
                    .output(RecipeOutput.item("minecraft:magenta_wool_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/stairs/orange", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool_stairs"))
                    .input(Ingredient.chemical(256L, "mek:orange"))
                    .output(RecipeOutput.item("minecraft:orange_wool_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/stairs/pink", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool_stairs"))
                    .input(Ingredient.chemical(256L, "mek:pink"))
                    .output(RecipeOutput.item("minecraft:pink_wool_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/stairs/purple", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool_stairs"))
                    .input(Ingredient.chemical(256L, "mek:purple"))
                    .output(RecipeOutput.item("minecraft:purple_wool_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/stairs/red", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool_stairs"))
                    .input(Ingredient.chemical(256L, "mek:red"))
                    .output(RecipeOutput.item("minecraft:red_wool_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/stairs/white", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool_stairs"))
                    .input(Ingredient.chemical(256L, "mek:white"))
                    .output(RecipeOutput.item("minecraft:white_wool_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/stairs/yellow", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool_stairs"))
                    .input(Ingredient.chemical(256L, "mek:yellow"))
                    .output(RecipeOutput.item("minecraft:yellow_wool_stairs", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/white", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool"))
                    .input(Ingredient.chemical(256L, "mek:white"))
                    .output(RecipeOutput.item("minecraft:white_wool", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:painting/wool/yellow", "mek:painting_machine")
                    .input(ItemIngredient.tagged(1L, "mek:colorable/wool"))
                    .input(Ingredient.chemical(256L, "mek:yellow"))
                    .output(RecipeOutput.item("minecraft:yellow_wool", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_extracting/dye/black", "mek:pigment_extractor")
                    .input(ItemIngredient.tagged(1L, "mek:dyes/black"))
                    .output(RecipeOutput.chemical("mek:black", 256L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_extracting/dye/blue", "mek:pigment_extractor")
                    .input(ItemIngredient.tagged(1L, "mek:dyes/blue"))
                    .output(RecipeOutput.chemical("mek:blue", 256L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_extracting/dye/gray", "mek:pigment_extractor")
                    .input(ItemIngredient.tagged(1L, "mek:dyes/gray"))
                    .output(RecipeOutput.chemical("mek:gray", 256L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_extracting/dye/green", "mek:pigment_extractor")
                    .input(ItemIngredient.tagged(1L, "mek:dyes/green"))
                    .output(RecipeOutput.chemical("mek:green", 256L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_extracting/dye/orange", "mek:pigment_extractor")
                    .input(ItemIngredient.tagged(1L, "mek:dyes/orange"))
                    .output(RecipeOutput.chemical("mek:orange", 256L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_extracting/dye/pink", "mek:pigment_extractor")
                    .input(ItemIngredient.tagged(1L, "mek:dyes/pink"))
                    .output(RecipeOutput.chemical("mek:pink", 256L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_extracting/dye/purple", "mek:pigment_extractor")
                    .input(ItemIngredient.tagged(1L, "mek:dyes/purple"))
                    .output(RecipeOutput.chemical("mek:purple", 256L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_extracting/dye/red", "mek:pigment_extractor")
                    .input(ItemIngredient.tagged(1L, "mek:dyes/red"))
                    .output(RecipeOutput.chemical("mek:red", 256L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_extracting/dye/white", "mek:pigment_extractor")
                    .input(ItemIngredient.tagged(1L, "mek:dyes/white"))
                    .output(RecipeOutput.chemical("mek:white", 256L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_extracting/dye/yellow", "mek:pigment_extractor")
                    .input(ItemIngredient.tagged(1L, "mek:dyes/yellow"))
                    .output(RecipeOutput.chemical("mek:yellow", 256L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_extracting/flower/black", "mek:pigment_extractor")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("minecraft:ink_sac"), ItemMatcher.id("minecraft:wither_rose")), "minecraft:ink_sac"))
                    .output(RecipeOutput.chemical("mek:black", 768L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_extracting/flower/blue", "mek:pigment_extractor")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("minecraft:cornflower"), ItemMatcher.id("minecraft:lapis_lazuli")), "minecraft:cornflower"))
                    .output(RecipeOutput.chemical("mek:blue", 768L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_extracting/flower/brown", "mek:pigment_extractor")
                    .input(ItemIngredient.tagged(1L, "mek:crops/cocoa_bean"))
                    .output(RecipeOutput.chemical("mek:brown", 768L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_extracting/flower/gray", "mek:pigment_extractor")
                    .input(ItemIngredient.item(1L, "minecraft:closed_eyeblossom"))
                    .output(RecipeOutput.chemical("mek:gray", 768L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_extracting/flower/green", "mek:pigment_extractor")
                    .input(ItemIngredient.tagged(1L, "mek:crops/cactus"))
                    .output(RecipeOutput.chemical("mek:green", 768L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_extracting/flower/large_cyan", "mek:pigment_extractor")
                    .input(ItemIngredient.item(1L, "minecraft:pitcher_plant"))
                    .output(RecipeOutput.chemical("mek:cyan", 1536L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_extracting/flower/large_magenta", "mek:pigment_extractor")
                    .input(ItemIngredient.item(1L, "minecraft:lilac"))
                    .output(RecipeOutput.chemical("mek:magenta", 1536L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_extracting/flower/large_pink", "mek:pigment_extractor")
                    .input(ItemIngredient.item(1L, "minecraft:peony"))
                    .output(RecipeOutput.chemical("mek:pink", 1536L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_extracting/flower/large_red", "mek:pigment_extractor")
                    .input(ItemIngredient.item(1L, "minecraft:rose_bush"))
                    .output(RecipeOutput.chemical("mek:red", 1536L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_extracting/flower/large_yellow", "mek:pigment_extractor")
                    .input(ItemIngredient.item(1L, "minecraft:sunflower"))
                    .output(RecipeOutput.chemical("mek:yellow", 1536L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_extracting/flower/light_blue", "mek:pigment_extractor")
                    .input(ItemIngredient.item(1L, "minecraft:blue_orchid"))
                    .output(RecipeOutput.chemical("mek:light_blue", 768L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_extracting/flower/light_gray", "mek:pigment_extractor")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("minecraft:azure_bluet"), ItemMatcher.id("minecraft:oxeye_daisy"), ItemMatcher.id("minecraft:white_tulip")), "minecraft:azure_bluet"))
                    .output(RecipeOutput.chemical("mek:light_gray", 768L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_extracting/flower/lime", "mek:pigment_extractor")
                    .input(ItemIngredient.item(1L, "minecraft:sea_pickle"))
                    .output(RecipeOutput.chemical("mek:lime", 768L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_extracting/flower/orange", "mek:pigment_extractor")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("minecraft:open_eyeblossom"), ItemMatcher.id("minecraft:orange_tulip"), ItemMatcher.id("minecraft:torchflower")), "minecraft:open_eyeblossom"))
                    .output(RecipeOutput.chemical("mek:orange", 768L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_extracting/flower/small_magenta", "mek:pigment_extractor")
                    .input(ItemIngredient.item(1L, "minecraft:allium"))
                    .output(RecipeOutput.chemical("mek:magenta", 768L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_extracting/flower/small_pink", "mek:pigment_extractor")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("minecraft:cactus_flower"), ItemMatcher.id("minecraft:pink_petals"), ItemMatcher.id("minecraft:pink_tulip")), "minecraft:cactus_flower"))
                    .output(RecipeOutput.chemical("mek:pink", 768L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_extracting/flower/small_red", "mek:pigment_extractor")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("minecraft:beetroot"), ItemMatcher.id("minecraft:poppy"), ItemMatcher.id("minecraft:red_tulip")), "minecraft:beetroot"))
                    .output(RecipeOutput.chemical("mek:red", 768L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_extracting/flower/small_yellow", "mek:pigment_extractor")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("minecraft:dandelion"), ItemMatcher.id("minecraft:golden_dandelion"), ItemMatcher.id("minecraft:wildflowers")), "minecraft:dandelion"))
                    .output(RecipeOutput.chemical("mek:yellow", 768L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_extracting/flower/white", "mek:pigment_extractor")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("minecraft:bone_meal"), ItemMatcher.id("minecraft:lily_of_the_valley")), "minecraft:bone_meal"))
                    .output(RecipeOutput.chemical("mek:white", 768L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_mixing/black_white_to_gray", "mek:pigment_mixer")
                    .input(Ingredient.chemical(1L, "mek:black"))
                    .input(Ingredient.chemical(1L, "mek:white"))
                    .output(RecipeOutput.chemical("mek:gray", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_mixing/blue_green_to_cyan", "mek:pigment_mixer")
                    .input(Ingredient.chemical(1L, "mek:blue"))
                    .input(Ingredient.chemical(1L, "mek:green"))
                    .output(RecipeOutput.chemical("mek:cyan", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_mixing/blue_red_to_purple", "mek:pigment_mixer")
                    .input(Ingredient.chemical(1L, "mek:blue"))
                    .input(Ingredient.chemical(1L, "mek:red"))
                    .output(RecipeOutput.chemical("mek:purple", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_mixing/blue_white_to_light_blue", "mek:pigment_mixer")
                    .input(Ingredient.chemical(1L, "mek:blue"))
                    .input(Ingredient.chemical(1L, "mek:white"))
                    .output(RecipeOutput.chemical("mek:light_blue", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_mixing/blue_yellow_to_green", "mek:pigment_mixer")
                    .input(Ingredient.chemical(1L, "mek:blue"))
                    .input(Ingredient.chemical(1L, "mek:yellow"))
                    .output(RecipeOutput.chemical("mek:green", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_mixing/gray_white_to_light_gray", "mek:pigment_mixer")
                    .input(Ingredient.chemical(1L, "mek:gray"))
                    .input(Ingredient.chemical(1L, "mek:white"))
                    .output(RecipeOutput.chemical("mek:light_gray", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_mixing/green_white_to_lime", "mek:pigment_mixer")
                    .input(Ingredient.chemical(1L, "mek:green"))
                    .input(Ingredient.chemical(1L, "mek:white"))
                    .output(RecipeOutput.chemical("mek:lime", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_mixing/light_blue_red_to_magenta", "mek:pigment_mixer")
                    .input(Ingredient.chemical(1L, "mek:light_blue"))
                    .input(Ingredient.chemical(1L, "mek:red"))
                    .output(RecipeOutput.chemical("mek:magenta", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_mixing/purple_pink_to_magenta", "mek:pigment_mixer")
                    .input(Ingredient.chemical(1L, "mek:purple"))
                    .input(Ingredient.chemical(1L, "mek:pink"))
                    .output(RecipeOutput.chemical("mek:magenta", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_mixing/red_white_to_pink", "mek:pigment_mixer")
                    .input(Ingredient.chemical(1L, "mek:red"))
                    .input(Ingredient.chemical(1L, "mek:white"))
                    .output(RecipeOutput.chemical("mek:pink", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:pigment_mixing/red_yellow_to_orange", "mek:pigment_mixer")
                    .input(Ingredient.chemical(1L, "mek:red"))
                    .input(Ingredient.chemical(1L, "mek:yellow"))
                    .output(RecipeOutput.chemical("mek:orange", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/bronze/dust/from_infusing", "mek:metallurgic_infuser")
                    .input(ItemIngredient.tagged(3L, "mek:dusts/copper"))
                    .input(Ingredient.tagged(ResourceTypes.INFUSION, 10L, "mek:tin"))
                    .output(RecipeOutput.item("mek:dust_bronze", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/bronze/dust/from_ingot", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:ingots/bronze"))
                    .output(RecipeOutput.item("mek:dust_bronze", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/bronze/ingot/from_infusing", "mek:metallurgic_infuser")
                    .input(ItemIngredient.tagged(3L, "mek:ingots/copper"))
                    .input(Ingredient.tagged(ResourceTypes.INFUSION, 10L, "mek:tin"))
                    .output(RecipeOutput.item("mek:ingot_bronze", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/coal/from_dust", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:dusts/coal"))
                    .output(RecipeOutput.item("minecraft:coal", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/coal/from_ore", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:ores/coal"))
                    .output(RecipeOutput.item("minecraft:coal", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/coal/to_deepslate_ore", "mek:combiner")
                    .input(ItemIngredient.tagged(8L, "mek:dusts/coal"))
                    .input(ItemIngredient.tagged(1L, "mek:cobblestones/deepslate"))
                    .output(RecipeOutput.item("minecraft:deepslate_coal_ore", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/coal/to_dust", "mek:crusher")
                    .input(ItemIngredient.item(1L, "minecraft:coal"))
                    .output(RecipeOutput.item("mek:dust_coal", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/coal/to_ore", "mek:combiner")
                    .input(ItemIngredient.tagged(8L, "mek:dusts/coal"))
                    .input(ItemIngredient.tagged(1L, "mek:cobblestones/normal"))
                    .output(RecipeOutput.item("minecraft:coal_ore", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/copper/clump/from_ore", "mek:purification_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:ores/copper"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("mek:clump_copper", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/copper/clump/from_raw_block", "mek:purification_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:storage_blocks/raw_copper"))
                    .input(Ingredient.chemical(2L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("mek:clump_copper", 18L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/copper/clump/from_raw_ore", "mek:purification_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:raw_materials/copper"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("mek:clump_copper", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/copper/clump/from_shard", "mek:purification_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:shards/copper"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("mek:clump_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/copper/crystal/from_slurry", "mek:chemical_crystallizer")
                    .input(Ingredient.chemical(200L, "mek:clean_copper"))
                    .output(RecipeOutput.item("mek:crystal_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/copper/dirty_dust/from_clump", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:clumps/copper"))
                    .output(RecipeOutput.item("mek:dirty_dust_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/copper/dust/from_dirty_dust", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:dirty_dusts/copper"))
                    .output(RecipeOutput.item("mek:dust_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/copper/dust/from_ingot", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:ingots/copper"))
                    .output(RecipeOutput.item("mek:dust_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/copper/dust/from_ore", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:ores/copper"))
                    .output(RecipeOutput.item("mek:dust_copper", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/copper/dust/from_raw_block", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:storage_blocks/raw_copper"))
                    .output(RecipeOutput.item("mek:dust_copper", 12L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/copper/dust/from_raw_ore", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(3L, "mek:raw_materials/copper"))
                    .output(RecipeOutput.item("mek:dust_copper", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/copper/ore/deepslate_from_raw", "mek:combiner")
                    .input(ItemIngredient.tagged(20L, "mek:raw_materials/copper"))
                    .input(ItemIngredient.tagged(1L, "mek:cobblestones/deepslate"))
                    .output(RecipeOutput.item("minecraft:deepslate_copper_ore", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/copper/ore/from_raw", "mek:combiner")
                    .input(ItemIngredient.tagged(20L, "mek:raw_materials/copper"))
                    .input(ItemIngredient.tagged(1L, "mek:cobblestones/normal"))
                    .output(RecipeOutput.item("minecraft:copper_ore", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/copper/shard/from_crystal", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:crystals/copper"))
                    .input(Ingredient.chemical(1L, "mek:hydrogen_chloride").perTickUsage())
                    .output(RecipeOutput.item("mek:shard_copper", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/copper/shard/from_ore", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:ores/copper"))
                    .input(Ingredient.chemical(1L, "mek:hydrogen_chloride").perTickUsage())
                    .output(RecipeOutput.item("mek:shard_copper", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/copper/shard/from_raw_block", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:storage_blocks/raw_copper"))
                    .input(Ingredient.chemical(2L, "mek:hydrogen_chloride").perTickUsage())
                    .output(RecipeOutput.item("mek:shard_copper", 24L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/copper/shard/from_raw_ore", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.tagged(3L, "mek:raw_materials/copper"))
                    .input(Ingredient.chemical(1L, "mek:hydrogen_chloride").perTickUsage())
                    .output(RecipeOutput.item("mek:shard_copper", 8L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/copper/slurry/clean", "mek:chemical_washer")
                    .input(Ingredient.chemical(1L, "mek:dirty_copper"))
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 5L, "mek:water"))
                    .output(RecipeOutput.chemical("mek:clean_copper", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:processing/copper/slurry/dirty/from_ore", "mek:chemical_dissolution_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:ores/copper"))
                    .input(Ingredient.chemical(1L, "mek:sulfuric_acid").perTickUsage())
                    .output(RecipeOutput.chemical("mek:dirty_copper", 1000L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:processing/copper/slurry/dirty/from_raw_block", "mek:chemical_dissolution_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:storage_blocks/raw_copper"))
                    .input(Ingredient.chemical(2L, "mek:sulfuric_acid").perTickUsage())
                    .output(RecipeOutput.chemical("mek:dirty_copper", 6000L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:processing/copper/slurry/dirty/from_raw_ore", "mek:chemical_dissolution_chamber")
                    .input(ItemIngredient.tagged(3L, "mek:raw_materials/copper"))
                    .input(Ingredient.chemical(1L, "mek:sulfuric_acid").perTickUsage())
                    .output(RecipeOutput.chemical("mek:dirty_copper", 2000L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:processing/diamond/from_dust", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:dusts/diamond"))
                    .output(RecipeOutput.item("minecraft:diamond", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/diamond/from_ore", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:ores/diamond"))
                    .output(RecipeOutput.item("minecraft:diamond", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/diamond/to_deepslate_ore", "mek:combiner")
                    .input(ItemIngredient.tagged(5L, "mek:dusts/diamond"))
                    .input(ItemIngredient.tagged(1L, "mek:cobblestones/deepslate"))
                    .output(RecipeOutput.item("minecraft:deepslate_diamond_ore", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/diamond/to_dust", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:gems/diamond"))
                    .output(RecipeOutput.item("mek:dust_diamond", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/diamond/to_ore", "mek:combiner")
                    .input(ItemIngredient.tagged(5L, "mek:dusts/diamond"))
                    .input(ItemIngredient.tagged(1L, "mek:cobblestones/normal"))
                    .output(RecipeOutput.item("minecraft:diamond_ore", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/emerald/from_dust", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:dusts/emerald"))
                    .output(RecipeOutput.item("minecraft:emerald", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/emerald/from_ore", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:ores/emerald"))
                    .output(RecipeOutput.item("minecraft:emerald", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/emerald/to_deepslate_ore", "mek:combiner")
                    .input(ItemIngredient.tagged(5L, "mek:dusts/emerald"))
                    .input(ItemIngredient.tagged(1L, "mek:cobblestones/deepslate"))
                    .output(RecipeOutput.item("minecraft:deepslate_emerald_ore", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/emerald/to_dust", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:gems/emerald"))
                    .output(RecipeOutput.item("mek:dust_emerald", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/emerald/to_ore", "mek:combiner")
                    .input(ItemIngredient.tagged(5L, "mek:dusts/emerald"))
                    .input(ItemIngredient.tagged(1L, "mek:cobblestones/normal"))
                    .output(RecipeOutput.item("minecraft:emerald_ore", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/fluorite/from_dust", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:dusts/fluorite"))
                    .output(RecipeOutput.item("mek:fluorite_gem", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/fluorite/from_ore", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:ores/fluorite"))
                    .output(RecipeOutput.item("mek:fluorite_gem", 6L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/fluorite/to_deepslate_ore", "mek:combiner")
                    .input(ItemIngredient.tagged(14L, "mek:dusts/fluorite"))
                    .input(ItemIngredient.tagged(1L, "mek:cobblestones/deepslate"))
                    .output(RecipeOutput.item("mek:deepslate_fluorite_ore", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/fluorite/to_dust", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:gems/fluorite"))
                    .output(RecipeOutput.item("mek:dust_fluorite", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/fluorite/to_ore", "mek:combiner")
                    .input(ItemIngredient.tagged(14L, "mek:dusts/fluorite"))
                    .input(ItemIngredient.tagged(1L, "mek:cobblestones/normal"))
                    .output(RecipeOutput.item("mek:fluorite_ore", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/gold/clump/from_ore", "mek:purification_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:ores/gold"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("mek:clump_gold", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/gold/clump/from_raw_block", "mek:purification_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:storage_blocks/raw_gold"))
                    .input(Ingredient.chemical(2L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("mek:clump_gold", 18L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/gold/clump/from_raw_ore", "mek:purification_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:raw_materials/gold"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("mek:clump_gold", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/gold/clump/from_shard", "mek:purification_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:shards/gold"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("mek:clump_gold", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/gold/crystal/from_slurry", "mek:chemical_crystallizer")
                    .input(Ingredient.chemical(200L, "mek:clean_gold"))
                    .output(RecipeOutput.item("mek:crystal_gold", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/gold/dirty_dust/from_clump", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:clumps/gold"))
                    .output(RecipeOutput.item("mek:dirty_dust_gold", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/gold/dust/from_dirty_dust", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:dirty_dusts/gold"))
                    .output(RecipeOutput.item("mek:dust_gold", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/gold/dust/from_ingot", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:ingots/gold"))
                    .output(RecipeOutput.item("mek:dust_gold", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/gold/dust/from_ore", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:ores/gold"))
                    .output(RecipeOutput.item("mek:dust_gold", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/gold/dust/from_raw_block", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:storage_blocks/raw_gold"))
                    .output(RecipeOutput.item("mek:dust_gold", 12L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/gold/dust/from_raw_ore", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(3L, "mek:raw_materials/gold"))
                    .output(RecipeOutput.item("mek:dust_gold", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/gold/ore/deepslate_from_raw", "mek:combiner")
                    .input(ItemIngredient.tagged(8L, "mek:raw_materials/gold"))
                    .input(ItemIngredient.tagged(1L, "mek:cobblestones/deepslate"))
                    .output(RecipeOutput.item("minecraft:deepslate_gold_ore", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/gold/ore/from_raw", "mek:combiner")
                    .input(ItemIngredient.tagged(8L, "mek:raw_materials/gold"))
                    .input(ItemIngredient.tagged(1L, "mek:cobblestones/normal"))
                    .output(RecipeOutput.item("minecraft:gold_ore", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/gold/ore/nether_from_raw", "mek:combiner")
                    .input(ItemIngredient.tagged(8L, "mek:raw_materials/gold"))
                    .input(ItemIngredient.tagged(1L, "mek:netherracks"))
                    .output(RecipeOutput.item("minecraft:nether_gold_ore", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/gold/shard/from_crystal", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:crystals/gold"))
                    .input(Ingredient.chemical(1L, "mek:hydrogen_chloride").perTickUsage())
                    .output(RecipeOutput.item("mek:shard_gold", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/gold/shard/from_ore", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:ores/gold"))
                    .input(Ingredient.chemical(1L, "mek:hydrogen_chloride").perTickUsage())
                    .output(RecipeOutput.item("mek:shard_gold", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/gold/shard/from_raw_block", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:storage_blocks/raw_gold"))
                    .input(Ingredient.chemical(2L, "mek:hydrogen_chloride").perTickUsage())
                    .output(RecipeOutput.item("mek:shard_gold", 24L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/gold/shard/from_raw_ore", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.tagged(3L, "mek:raw_materials/gold"))
                    .input(Ingredient.chemical(1L, "mek:hydrogen_chloride").perTickUsage())
                    .output(RecipeOutput.item("mek:shard_gold", 8L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/gold/slurry/clean", "mek:chemical_washer")
                    .input(Ingredient.chemical(1L, "mek:dirty_gold"))
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 5L, "mek:water"))
                    .output(RecipeOutput.chemical("mek:clean_gold", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:processing/gold/slurry/dirty/from_ore", "mek:chemical_dissolution_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:ores/gold"))
                    .input(Ingredient.chemical(1L, "mek:sulfuric_acid").perTickUsage())
                    .output(RecipeOutput.chemical("mek:dirty_gold", 1000L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:processing/gold/slurry/dirty/from_raw_block", "mek:chemical_dissolution_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:storage_blocks/raw_gold"))
                    .input(Ingredient.chemical(2L, "mek:sulfuric_acid").perTickUsage())
                    .output(RecipeOutput.chemical("mek:dirty_gold", 6000L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:processing/gold/slurry/dirty/from_raw_ore", "mek:chemical_dissolution_chamber")
                    .input(ItemIngredient.tagged(3L, "mek:raw_materials/gold"))
                    .input(Ingredient.chemical(1L, "mek:sulfuric_acid").perTickUsage())
                    .output(RecipeOutput.chemical("mek:dirty_gold", 2000L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:processing/iron/clump/from_ore", "mek:purification_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:ores/iron"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("mek:clump_iron", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/iron/clump/from_raw_block", "mek:purification_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:storage_blocks/raw_iron"))
                    .input(Ingredient.chemical(2L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("mek:clump_iron", 18L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/iron/clump/from_raw_ore", "mek:purification_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:raw_materials/iron"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("mek:clump_iron", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/iron/clump/from_shard", "mek:purification_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:shards/iron"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("mek:clump_iron", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/iron/crystal/from_slurry", "mek:chemical_crystallizer")
                    .input(Ingredient.chemical(200L, "mek:clean_iron"))
                    .output(RecipeOutput.item("mek:crystal_iron", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/iron/dirty_dust/from_clump", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:clumps/iron"))
                    .output(RecipeOutput.item("mek:dirty_dust_iron", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/iron/dust/from_dirty_dust", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:dirty_dusts/iron"))
                    .output(RecipeOutput.item("mek:dust_iron", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/iron/dust/from_ingot", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:ingots/iron"))
                    .output(RecipeOutput.item("mek:dust_iron", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/iron/dust/from_ore", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:ores/iron"))
                    .output(RecipeOutput.item("mek:dust_iron", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/iron/dust/from_raw_block", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:storage_blocks/raw_iron"))
                    .output(RecipeOutput.item("mek:dust_iron", 12L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/iron/dust/from_raw_ore", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(3L, "mek:raw_materials/iron"))
                    .output(RecipeOutput.item("mek:dust_iron", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/iron/enriched", "mek:metallurgic_infuser")
                    .input(ItemIngredient.tagged(1L, "mek:ingots/iron"))
                    .input(Ingredient.tagged(ResourceTypes.INFUSION, 10L, "mek:carbon"))
                    .output(RecipeOutput.item("mek:enriched_iron", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/iron/enriched_dust", "mek:metallurgic_infuser")
                    .input(ItemIngredient.tagged(1L, "mek:dusts/iron"))
                    .input(Ingredient.tagged(ResourceTypes.INFUSION, 10L, "mek:carbon"))
                    .output(RecipeOutput.item("mek:enriched_iron", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/iron/ore/deepslate_from_raw", "mek:combiner")
                    .input(ItemIngredient.tagged(8L, "mek:raw_materials/iron"))
                    .input(ItemIngredient.tagged(1L, "mek:cobblestones/deepslate"))
                    .output(RecipeOutput.item("minecraft:deepslate_iron_ore", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/iron/ore/from_raw", "mek:combiner")
                    .input(ItemIngredient.tagged(8L, "mek:raw_materials/iron"))
                    .input(ItemIngredient.tagged(1L, "mek:cobblestones/normal"))
                    .output(RecipeOutput.item("minecraft:iron_ore", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/iron/shard/from_crystal", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:crystals/iron"))
                    .input(Ingredient.chemical(1L, "mek:hydrogen_chloride").perTickUsage())
                    .output(RecipeOutput.item("mek:shard_iron", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/iron/shard/from_ore", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:ores/iron"))
                    .input(Ingredient.chemical(1L, "mek:hydrogen_chloride").perTickUsage())
                    .output(RecipeOutput.item("mek:shard_iron", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/iron/shard/from_raw_block", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:storage_blocks/raw_iron"))
                    .input(Ingredient.chemical(2L, "mek:hydrogen_chloride").perTickUsage())
                    .output(RecipeOutput.item("mek:shard_iron", 24L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/iron/shard/from_raw_ore", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.tagged(3L, "mek:raw_materials/iron"))
                    .input(Ingredient.chemical(1L, "mek:hydrogen_chloride").perTickUsage())
                    .output(RecipeOutput.item("mek:shard_iron", 8L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/iron/slurry/clean", "mek:chemical_washer")
                    .input(Ingredient.chemical(1L, "mek:dirty_iron"))
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 5L, "mek:water"))
                    .output(RecipeOutput.chemical("mek:clean_iron", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
    }

    private static void recipes7(Registrars r) {
        r.recipe(Recipe.builder("mek:processing/iron/slurry/dirty/from_ore", "mek:chemical_dissolution_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:ores/iron"))
                    .input(Ingredient.chemical(1L, "mek:sulfuric_acid").perTickUsage())
                    .output(RecipeOutput.chemical("mek:dirty_iron", 1000L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:processing/iron/slurry/dirty/from_raw_block", "mek:chemical_dissolution_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:storage_blocks/raw_iron"))
                    .input(Ingredient.chemical(2L, "mek:sulfuric_acid").perTickUsage())
                    .output(RecipeOutput.chemical("mek:dirty_iron", 6000L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:processing/iron/slurry/dirty/from_raw_ore", "mek:chemical_dissolution_chamber")
                    .input(ItemIngredient.tagged(3L, "mek:raw_materials/iron"))
                    .input(Ingredient.chemical(1L, "mek:sulfuric_acid").perTickUsage())
                    .output(RecipeOutput.chemical("mek:dirty_iron", 2000L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:processing/lapis_lazuli/from_dust", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:dusts/lapis"))
                    .output(RecipeOutput.item("minecraft:lapis_lazuli", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/lapis_lazuli/from_ore", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:ores/lapis"))
                    .output(RecipeOutput.item("minecraft:lapis_lazuli", 12L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/lapis_lazuli/to_deepslate_ore", "mek:combiner")
                    .input(ItemIngredient.tagged(27L, "mek:dusts/lapis"))
                    .input(ItemIngredient.tagged(1L, "mek:cobblestones/deepslate"))
                    .output(RecipeOutput.item("minecraft:deepslate_lapis_ore", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/lapis_lazuli/to_dust", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:gems/lapis"))
                    .output(RecipeOutput.item("mek:dust_lapis_lazuli", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/lapis_lazuli/to_ore", "mek:combiner")
                    .input(ItemIngredient.tagged(27L, "mek:dusts/lapis"))
                    .input(ItemIngredient.tagged(1L, "mek:cobblestones/normal"))
                    .output(RecipeOutput.item("minecraft:lapis_ore", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/lategame/antimatter/from_pellet", "mek:chemical_oxidizer")
                    .input(ItemIngredient.tagged(1L, "mek:pellets/antimatter"))
                    .output(RecipeOutput.chemical("mek:antimatter", 1000L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:processing/lategame/antimatter_pellet/from_gas", "mek:chemical_crystallizer")
                    .input(Ingredient.chemical(1000L, "mek:antimatter"))
                    .output(RecipeOutput.item("mek:pellet_antimatter", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/lategame/plutonium", "mek:isotopic_centrifuge")
                    .input(Ingredient.chemical(10L, "mek:nuclear_waste"))
                    .output(RecipeOutput.chemical("mek:plutonium", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:processing/lategame/plutonium_pellet/from_reaction", "mek:pressurized_reaction_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:dusts/fluorite"))
                    .input(Ingredient.chemical(1000L, "mek:plutonium"))
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 1000L, "mek:water"))
                    .output(RecipeOutput.item("mek:pellet_plutonium", 1L))
                    .output(RecipeOutput.chemical("mek:spent_nuclear_waste", 1000L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:processing/lategame/polonium", "mek:solar_neutron_activator")
                    .input(Ingredient.chemical(10L, "mek:nuclear_waste"))
                    .output(RecipeOutput.chemical("mek:polonium", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:processing/lategame/polonium_pellet/from_reaction", "mek:pressurized_reaction_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:dusts/fluorite"))
                    .input(Ingredient.chemical(1000L, "mek:polonium"))
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 1000L, "mek:water"))
                    .output(RecipeOutput.item("mek:pellet_polonium", 1L))
                    .output(RecipeOutput.chemical("mek:spent_nuclear_waste", 1000L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:processing/lead/clump/from_ore", "mek:purification_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:ores/lead"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("mek:clump_lead", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/lead/clump/from_raw_block", "mek:purification_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:storage_blocks/raw_lead"))
                    .input(Ingredient.chemical(2L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("mek:clump_lead", 18L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/lead/clump/from_raw_ore", "mek:purification_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:raw_materials/lead"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("mek:clump_lead", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/lead/clump/from_shard", "mek:purification_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:shards/lead"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("mek:clump_lead", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/lead/crystal/from_slurry", "mek:chemical_crystallizer")
                    .input(Ingredient.chemical(200L, "mek:clean_lead"))
                    .output(RecipeOutput.item("mek:crystal_lead", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/lead/dirty_dust/from_clump", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:clumps/lead"))
                    .output(RecipeOutput.item("mek:dirty_dust_lead", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/lead/dust/from_dirty_dust", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:dirty_dusts/lead"))
                    .output(RecipeOutput.item("mek:dust_lead", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/lead/dust/from_ingot", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:ingots/lead"))
                    .output(RecipeOutput.item("mek:dust_lead", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/lead/dust/from_ore", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:ores/lead"))
                    .output(RecipeOutput.item("mek:dust_lead", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/lead/dust/from_raw_block", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:storage_blocks/raw_lead"))
                    .output(RecipeOutput.item("mek:dust_lead", 12L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/lead/dust/from_raw_ore", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(3L, "mek:raw_materials/lead"))
                    .output(RecipeOutput.item("mek:dust_lead", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/lead/ore/deepslate_from_raw", "mek:combiner")
                    .input(ItemIngredient.tagged(8L, "mek:raw_materials/lead"))
                    .input(ItemIngredient.tagged(1L, "mek:cobblestones/deepslate"))
                    .output(RecipeOutput.item("mek:deepslate_lead_ore", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/lead/ore/from_raw", "mek:combiner")
                    .input(ItemIngredient.tagged(8L, "mek:raw_materials/lead"))
                    .input(ItemIngredient.tagged(1L, "mek:cobblestones/normal"))
                    .output(RecipeOutput.item("mek:lead_ore", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/lead/shard/from_crystal", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:crystals/lead"))
                    .input(Ingredient.chemical(1L, "mek:hydrogen_chloride").perTickUsage())
                    .output(RecipeOutput.item("mek:shard_lead", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/lead/shard/from_ore", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:ores/lead"))
                    .input(Ingredient.chemical(1L, "mek:hydrogen_chloride").perTickUsage())
                    .output(RecipeOutput.item("mek:shard_lead", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/lead/shard/from_raw_block", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:storage_blocks/raw_lead"))
                    .input(Ingredient.chemical(2L, "mek:hydrogen_chloride").perTickUsage())
                    .output(RecipeOutput.item("mek:shard_lead", 24L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/lead/shard/from_raw_ore", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.tagged(3L, "mek:raw_materials/lead"))
                    .input(Ingredient.chemical(1L, "mek:hydrogen_chloride").perTickUsage())
                    .output(RecipeOutput.item("mek:shard_lead", 8L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/lead/slurry/clean", "mek:chemical_washer")
                    .input(Ingredient.chemical(1L, "mek:dirty_lead"))
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 5L, "mek:water"))
                    .output(RecipeOutput.chemical("mek:clean_lead", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:processing/lead/slurry/dirty/from_ore", "mek:chemical_dissolution_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:ores/lead"))
                    .input(Ingredient.chemical(1L, "mek:sulfuric_acid").perTickUsage())
                    .output(RecipeOutput.chemical("mek:dirty_lead", 1000L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:processing/lead/slurry/dirty/from_raw_block", "mek:chemical_dissolution_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:storage_blocks/raw_lead"))
                    .input(Ingredient.chemical(2L, "mek:sulfuric_acid").perTickUsage())
                    .output(RecipeOutput.chemical("mek:dirty_lead", 6000L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:processing/lead/slurry/dirty/from_raw_ore", "mek:chemical_dissolution_chamber")
                    .input(ItemIngredient.tagged(3L, "mek:raw_materials/lead"))
                    .input(Ingredient.chemical(1L, "mek:sulfuric_acid").perTickUsage())
                    .output(RecipeOutput.chemical("mek:dirty_lead", 2000L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:processing/netherite/ancient_debris_to_dirty_scrap", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:ores/netherite_scrap"))
                    .output(RecipeOutput.item("mek:dirty_netherite_scrap", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/netherite/ancient_debris_to_scrap", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:ores/netherite_scrap"))
                    .output(RecipeOutput.item("minecraft:netherite_scrap", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/netherite/dirty_scrap_to_scrap", "mek:enrichment_chamber")
                    .input(ItemIngredient.item(1L, "mek:dirty_netherite_scrap"))
                    .output(RecipeOutput.item("minecraft:netherite_scrap", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/netherite/dust_to_ancient_debris", "mek:combiner")
                    .input(ItemIngredient.tagged(2L, "mek:dusts/netherite"))
                    .input(ItemIngredient.item(1L, "minecraft:basalt"))
                    .output(RecipeOutput.item("minecraft:ancient_debris", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/netherite/ingot_to_dust", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:ingots/netherite"))
                    .output(RecipeOutput.item("mek:dust_netherite", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/netherite/scrap_to_dust", "mek:metallurgic_infuser")
                    .input(ItemIngredient.item(4L, "minecraft:netherite_scrap"))
                    .input(Ingredient.tagged(ResourceTypes.INFUSION, 40L, "mek:gold"))
                    .output(RecipeOutput.item("mek:dust_netherite", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/osmium/clump/from_ore", "mek:purification_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:ores/osmium"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("mek:clump_osmium", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/osmium/clump/from_raw_block", "mek:purification_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:storage_blocks/raw_osmium"))
                    .input(Ingredient.chemical(2L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("mek:clump_osmium", 18L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/osmium/clump/from_raw_ore", "mek:purification_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:raw_materials/osmium"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("mek:clump_osmium", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/osmium/clump/from_shard", "mek:purification_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:shards/osmium"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("mek:clump_osmium", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/osmium/crystal/from_slurry", "mek:chemical_crystallizer")
                    .input(Ingredient.chemical(200L, "mek:clean_osmium"))
                    .output(RecipeOutput.item("mek:crystal_osmium", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/osmium/dirty_dust/from_clump", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:clumps/osmium"))
                    .output(RecipeOutput.item("mek:dirty_dust_osmium", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/osmium/dust/from_dirty_dust", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:dirty_dusts/osmium"))
                    .output(RecipeOutput.item("mek:dust_osmium", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/osmium/dust/from_ingot", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:ingots/osmium"))
                    .output(RecipeOutput.item("mek:dust_osmium", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/osmium/dust/from_ore", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:ores/osmium"))
                    .output(RecipeOutput.item("mek:dust_osmium", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/osmium/dust/from_raw_block", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:storage_blocks/raw_osmium"))
                    .output(RecipeOutput.item("mek:dust_osmium", 12L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/osmium/dust/from_raw_ore", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(3L, "mek:raw_materials/osmium"))
                    .output(RecipeOutput.item("mek:dust_osmium", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/osmium/ore/deepslate_from_raw", "mek:combiner")
                    .input(ItemIngredient.tagged(8L, "mek:raw_materials/osmium"))
                    .input(ItemIngredient.tagged(1L, "mek:cobblestones/deepslate"))
                    .output(RecipeOutput.item("mek:deepslate_osmium_ore", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/osmium/ore/from_raw", "mek:combiner")
                    .input(ItemIngredient.tagged(8L, "mek:raw_materials/osmium"))
                    .input(ItemIngredient.tagged(1L, "mek:cobblestones/normal"))
                    .output(RecipeOutput.item("mek:osmium_ore", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/osmium/shard/from_crystal", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:crystals/osmium"))
                    .input(Ingredient.chemical(1L, "mek:hydrogen_chloride").perTickUsage())
                    .output(RecipeOutput.item("mek:shard_osmium", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/osmium/shard/from_ore", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:ores/osmium"))
                    .input(Ingredient.chemical(1L, "mek:hydrogen_chloride").perTickUsage())
                    .output(RecipeOutput.item("mek:shard_osmium", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/osmium/shard/from_raw_block", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:storage_blocks/raw_osmium"))
                    .input(Ingredient.chemical(2L, "mek:hydrogen_chloride").perTickUsage())
                    .output(RecipeOutput.item("mek:shard_osmium", 24L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/osmium/shard/from_raw_ore", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.tagged(3L, "mek:raw_materials/osmium"))
                    .input(Ingredient.chemical(1L, "mek:hydrogen_chloride").perTickUsage())
                    .output(RecipeOutput.item("mek:shard_osmium", 8L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/osmium/slurry/clean", "mek:chemical_washer")
                    .input(Ingredient.chemical(1L, "mek:dirty_osmium"))
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 5L, "mek:water"))
                    .output(RecipeOutput.chemical("mek:clean_osmium", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:processing/osmium/slurry/dirty/from_ore", "mek:chemical_dissolution_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:ores/osmium"))
                    .input(Ingredient.chemical(1L, "mek:sulfuric_acid").perTickUsage())
                    .output(RecipeOutput.chemical("mek:dirty_osmium", 1000L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:processing/osmium/slurry/dirty/from_raw_block", "mek:chemical_dissolution_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:storage_blocks/raw_osmium"))
                    .input(Ingredient.chemical(2L, "mek:sulfuric_acid").perTickUsage())
                    .output(RecipeOutput.chemical("mek:dirty_osmium", 6000L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:processing/osmium/slurry/dirty/from_raw_ore", "mek:chemical_dissolution_chamber")
                    .input(ItemIngredient.tagged(3L, "mek:raw_materials/osmium"))
                    .input(Ingredient.chemical(1L, "mek:sulfuric_acid").perTickUsage())
                    .output(RecipeOutput.chemical("mek:dirty_osmium", 2000L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:processing/quartz/from_dust", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:dusts/quartz"))
                    .output(RecipeOutput.item("minecraft:quartz", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/quartz/from_ore", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:ores/quartz"))
                    .output(RecipeOutput.item("minecraft:quartz", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/quartz/to_dust", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:gems/quartz"))
                    .output(RecipeOutput.item("mek:dust_quartz", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/quartz/to_ore", "mek:combiner")
                    .input(ItemIngredient.tagged(14L, "mek:dusts/quartz"))
                    .input(ItemIngredient.tagged(1L, "mek:netherracks"))
                    .output(RecipeOutput.item("minecraft:nether_quartz_ore", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/redstone/from_ore", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:ores/redstone"))
                    .output(RecipeOutput.item("minecraft:redstone", 12L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/redstone/to_deepslate_ore", "mek:combiner")
                    .input(ItemIngredient.tagged(16L, "mek:dusts/redstone"))
                    .input(ItemIngredient.tagged(1L, "mek:cobblestones/deepslate"))
                    .output(RecipeOutput.item("minecraft:deepslate_redstone_ore", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/redstone/to_ore", "mek:combiner")
                    .input(ItemIngredient.tagged(16L, "mek:dusts/redstone"))
                    .input(ItemIngredient.tagged(1L, "mek:cobblestones/normal"))
                    .output(RecipeOutput.item("minecraft:redstone_ore", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/refined_glowstone/ingot/from_dust", "mek:osmium_compressor")
                    .input(ItemIngredient.tagged(1L, "mek:dusts/glowstone"))
                    .input(Ingredient.chemical(200L, "mek:osmium"))
                    .output(RecipeOutput.item("mek:ingot_refined_glowstone", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/refined_glowstone/ingot_to_dust", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:ingots/refined_glowstone"))
                    .output(RecipeOutput.item("minecraft:glowstone_dust", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/refined_obsidian/dust/from_ingot", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:ingots/refined_obsidian"))
                    .output(RecipeOutput.item("mek:dust_refined_obsidian", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/refined_obsidian/dust/from_obsidian_dust", "mek:metallurgic_infuser")
                    .input(ItemIngredient.tagged(1L, "mek:dusts/obsidian"))
                    .input(Ingredient.tagged(ResourceTypes.INFUSION, 10L, "mek:diamond"))
                    .output(RecipeOutput.item("mek:dust_refined_obsidian", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/refined_obsidian/ingot/from_dust", "mek:osmium_compressor")
                    .input(ItemIngredient.tagged(1L, "mek:dusts/refined_obsidian"))
                    .input(Ingredient.chemical(200L, "mek:osmium"))
                    .output(RecipeOutput.item("mek:ingot_refined_obsidian", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/steel/enriched_iron_to_dust", "mek:metallurgic_infuser")
                    .input(ItemIngredient.item(1L, "mek:enriched_iron"))
                    .input(Ingredient.tagged(ResourceTypes.INFUSION, 10L, "mek:carbon"))
                    .output(RecipeOutput.item("mek:dust_steel", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/steel/ingot_to_dust", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:ingots/steel"))
                    .output(RecipeOutput.item("mek:dust_steel", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/tin/clump/from_ore", "mek:purification_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:ores/tin"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("mek:clump_tin", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/tin/clump/from_raw_block", "mek:purification_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:storage_blocks/raw_tin"))
                    .input(Ingredient.chemical(2L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("mek:clump_tin", 18L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/tin/clump/from_raw_ore", "mek:purification_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:raw_materials/tin"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("mek:clump_tin", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/tin/clump/from_shard", "mek:purification_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:shards/tin"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("mek:clump_tin", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/tin/crystal/from_slurry", "mek:chemical_crystallizer")
                    .input(Ingredient.chemical(200L, "mek:clean_tin"))
                    .output(RecipeOutput.item("mek:crystal_tin", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/tin/dirty_dust/from_clump", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:clumps/tin"))
                    .output(RecipeOutput.item("mek:dirty_dust_tin", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/tin/dust/from_dirty_dust", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:dirty_dusts/tin"))
                    .output(RecipeOutput.item("mek:dust_tin", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/tin/dust/from_ingot", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:ingots/tin"))
                    .output(RecipeOutput.item("mek:dust_tin", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/tin/dust/from_ore", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:ores/tin"))
                    .output(RecipeOutput.item("mek:dust_tin", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/tin/dust/from_raw_block", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:storage_blocks/raw_tin"))
                    .output(RecipeOutput.item("mek:dust_tin", 12L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/tin/dust/from_raw_ore", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(3L, "mek:raw_materials/tin"))
                    .output(RecipeOutput.item("mek:dust_tin", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/tin/ore/deepslate_from_raw", "mek:combiner")
                    .input(ItemIngredient.tagged(8L, "mek:raw_materials/tin"))
                    .input(ItemIngredient.tagged(1L, "mek:cobblestones/deepslate"))
                    .output(RecipeOutput.item("mek:deepslate_tin_ore", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/tin/ore/from_raw", "mek:combiner")
                    .input(ItemIngredient.tagged(8L, "mek:raw_materials/tin"))
                    .input(ItemIngredient.tagged(1L, "mek:cobblestones/normal"))
                    .output(RecipeOutput.item("mek:tin_ore", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/tin/shard/from_crystal", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:crystals/tin"))
                    .input(Ingredient.chemical(1L, "mek:hydrogen_chloride").perTickUsage())
                    .output(RecipeOutput.item("mek:shard_tin", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/tin/shard/from_ore", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:ores/tin"))
                    .input(Ingredient.chemical(1L, "mek:hydrogen_chloride").perTickUsage())
                    .output(RecipeOutput.item("mek:shard_tin", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/tin/shard/from_raw_block", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:storage_blocks/raw_tin"))
                    .input(Ingredient.chemical(2L, "mek:hydrogen_chloride").perTickUsage())
                    .output(RecipeOutput.item("mek:shard_tin", 24L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/tin/shard/from_raw_ore", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.tagged(3L, "mek:raw_materials/tin"))
                    .input(Ingredient.chemical(1L, "mek:hydrogen_chloride").perTickUsage())
                    .output(RecipeOutput.item("mek:shard_tin", 8L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/tin/slurry/clean", "mek:chemical_washer")
                    .input(Ingredient.chemical(1L, "mek:dirty_tin"))
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 5L, "mek:water"))
                    .output(RecipeOutput.chemical("mek:clean_tin", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:processing/tin/slurry/dirty/from_ore", "mek:chemical_dissolution_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:ores/tin"))
                    .input(Ingredient.chemical(1L, "mek:sulfuric_acid").perTickUsage())
                    .output(RecipeOutput.chemical("mek:dirty_tin", 1000L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:processing/tin/slurry/dirty/from_raw_block", "mek:chemical_dissolution_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:storage_blocks/raw_tin"))
                    .input(Ingredient.chemical(2L, "mek:sulfuric_acid").perTickUsage())
                    .output(RecipeOutput.chemical("mek:dirty_tin", 6000L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:processing/tin/slurry/dirty/from_raw_ore", "mek:chemical_dissolution_chamber")
                    .input(ItemIngredient.tagged(3L, "mek:raw_materials/tin"))
                    .input(Ingredient.chemical(1L, "mek:sulfuric_acid").perTickUsage())
                    .output(RecipeOutput.chemical("mek:dirty_tin", 2000L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:processing/uranium/clump/from_ore", "mek:purification_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:ores/uranium"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("mek:clump_uranium", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/uranium/clump/from_raw_block", "mek:purification_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:storage_blocks/raw_uranium"))
                    .input(Ingredient.chemical(2L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("mek:clump_uranium", 18L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/uranium/clump/from_raw_ore", "mek:purification_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:raw_materials/uranium"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("mek:clump_uranium", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/uranium/clump/from_shard", "mek:purification_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:shards/uranium"))
                    .input(Ingredient.chemical(1L, "mek:oxygen").perTickUsage())
                    .output(RecipeOutput.item("mek:clump_uranium", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/uranium/crystal/from_slurry", "mek:chemical_crystallizer")
                    .input(Ingredient.chemical(200L, "mek:clean_uranium"))
                    .output(RecipeOutput.item("mek:crystal_uranium", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/uranium/dirty_dust/from_clump", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:clumps/uranium"))
                    .output(RecipeOutput.item("mek:dirty_dust_uranium", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/uranium/dust/from_dirty_dust", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:dirty_dusts/uranium"))
                    .output(RecipeOutput.item("mek:dust_uranium", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/uranium/dust/from_ingot", "mek:crusher")
                    .input(ItemIngredient.tagged(1L, "mek:ingots/uranium"))
                    .output(RecipeOutput.item("mek:dust_uranium", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/uranium/dust/from_ore", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:ores/uranium"))
                    .output(RecipeOutput.item("mek:dust_uranium", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/uranium/dust/from_raw_block", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:storage_blocks/raw_uranium"))
                    .output(RecipeOutput.item("mek:dust_uranium", 12L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/uranium/dust/from_raw_ore", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(3L, "mek:raw_materials/uranium"))
                    .output(RecipeOutput.item("mek:dust_uranium", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/uranium/fissile_fuel", "mek:isotopic_centrifuge")
                    .input(Ingredient.chemical(1L, "mek:uranium_hexafluoride"))
                    .output(RecipeOutput.chemical("mek:fissile_fuel", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:processing/uranium/hydrofluoric_acid", "mek:chemical_dissolution_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:gems/fluorite"))
                    .input(Ingredient.chemical(1L, "mek:sulfuric_acid").perTickUsage())
                    .output(RecipeOutput.chemical("mek:hydrofluoric_acid", 1000L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:processing/uranium/hydrofluoric_acid_from_block", "mek:chemical_dissolution_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:storage_blocks/fluorite"))
                    .input(Ingredient.chemical(9L, "mek:sulfuric_acid").perTickUsage())
                    .output(RecipeOutput.chemical("mek:hydrofluoric_acid", 9000L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:processing/uranium/ore/deepslate_from_raw", "mek:combiner")
                    .input(ItemIngredient.tagged(8L, "mek:raw_materials/uranium"))
                    .input(ItemIngredient.tagged(1L, "mek:cobblestones/deepslate"))
                    .output(RecipeOutput.item("mek:deepslate_uranium_ore", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/uranium/ore/from_raw", "mek:combiner")
                    .input(ItemIngredient.tagged(8L, "mek:raw_materials/uranium"))
                    .input(ItemIngredient.tagged(1L, "mek:cobblestones/normal"))
                    .output(RecipeOutput.item("mek:uranium_ore", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/uranium/reprocessing/from_plutonium", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:pellets/plutonium"))
                    .input(Ingredient.chemical(1L, "mek:hydrogen_chloride").perTickUsage())
                    .output(RecipeOutput.item("mek:reprocessed_fissile_fragment", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/uranium/reprocessing/to_fuel", "mek:chemical_oxidizer")
                    .input(ItemIngredient.item(1L, "mek:reprocessed_fissile_fragment"))
                    .output(RecipeOutput.chemical("mek:fissile_fuel", 2000L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:processing/uranium/shard/from_crystal", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:crystals/uranium"))
                    .input(Ingredient.chemical(1L, "mek:hydrogen_chloride").perTickUsage())
                    .output(RecipeOutput.item("mek:shard_uranium", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/uranium/shard/from_ore", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:ores/uranium"))
                    .input(Ingredient.chemical(1L, "mek:hydrogen_chloride").perTickUsage())
                    .output(RecipeOutput.item("mek:shard_uranium", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/uranium/shard/from_raw_block", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:storage_blocks/raw_uranium"))
                    .input(Ingredient.chemical(2L, "mek:hydrogen_chloride").perTickUsage())
                    .output(RecipeOutput.item("mek:shard_uranium", 24L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/uranium/shard/from_raw_ore", "mek:chemical_injection_chamber")
                    .input(ItemIngredient.tagged(3L, "mek:raw_materials/uranium"))
                    .input(Ingredient.chemical(1L, "mek:hydrogen_chloride").perTickUsage())
                    .output(RecipeOutput.item("mek:shard_uranium", 8L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:processing/uranium/slurry/clean", "mek:chemical_washer")
                    .input(Ingredient.chemical(1L, "mek:dirty_uranium"))
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 5L, "mek:water"))
                    .output(RecipeOutput.chemical("mek:clean_uranium", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:processing/uranium/slurry/dirty/from_ore", "mek:chemical_dissolution_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:ores/uranium"))
                    .input(Ingredient.chemical(1L, "mek:sulfuric_acid").perTickUsage())
                    .output(RecipeOutput.chemical("mek:dirty_uranium", 1000L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:processing/uranium/slurry/dirty/from_raw_block", "mek:chemical_dissolution_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:storage_blocks/raw_uranium"))
                    .input(Ingredient.chemical(2L, "mek:sulfuric_acid").perTickUsage())
                    .output(RecipeOutput.chemical("mek:dirty_uranium", 6000L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:processing/uranium/slurry/dirty/from_raw_ore", "mek:chemical_dissolution_chamber")
                    .input(ItemIngredient.tagged(3L, "mek:raw_materials/uranium"))
                    .input(Ingredient.chemical(1L, "mek:sulfuric_acid").perTickUsage())
                    .output(RecipeOutput.chemical("mek:dirty_uranium", 2000L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:processing/uranium/sulfuric_acid", "mek:chemical_infuser")
                    .input(Ingredient.chemical(1L, "mek:hydrofluoric_acid"))
                    .input(Ingredient.chemical(1L, "mek:uranium_oxide"))
                    .output(RecipeOutput.chemical("mek:uranium_hexafluoride", 2L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:processing/uranium/uranium_oxide", "mek:chemical_oxidizer")
                    .input(ItemIngredient.item(1L, "mek:yellow_cake_uranium"))
                    .output(RecipeOutput.chemical("mek:uranium_oxide", 250L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:processing/uranium/yellow_cake_uranium", "mek:enrichment_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:ingots/uranium"))
                    .output(RecipeOutput.item("mek:yellow_cake_uranium", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:reaction/coal_gasification/blocks_coals", "mek:pressurized_reaction_chamber")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("mek:block_charcoal"), ItemMatcher.id("minecraft:coal_block")), "mek:block_charcoal"))
                    .input(Ingredient.chemical(1000L, "mek:oxygen"))
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 1000L, "mek:water"))
                    .output(RecipeOutput.item("mek:dust_sulfur", 9L))
                    .output(RecipeOutput.chemical("mek:hydrogen", 1000L))
                    .ticks(900)
                    .build());
        r.recipe(Recipe.builder("mek:reaction/coal_gasification/coals", "mek:pressurized_reaction_chamber")
                    .input(ItemIngredient.tagged(1L, "mek:coals"))
                    .input(Ingredient.chemical(100L, "mek:oxygen"))
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 100L, "mek:water"))
                    .output(RecipeOutput.item("mek:dust_sulfur", 1L))
                    .output(RecipeOutput.chemical("mek:hydrogen", 100L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:reaction/coal_gasification/dusts_coals", "mek:pressurized_reaction_chamber")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("mek:dust_charcoal"), ItemMatcher.id("mek:dust_coal")), "mek:dust_charcoal"))
                    .input(Ingredient.chemical(100L, "mek:oxygen"))
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 100L, "mek:water"))
                    .output(RecipeOutput.item("mek:dust_sulfur", 1L))
                    .output(RecipeOutput.chemical("mek:hydrogen", 100L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:reaction/substrate/ethene_oxygen", "mek:pressurized_reaction_chamber")
                    .input(ItemIngredient.item(1L, "mek:substrate"))
                    .input(Ingredient.chemical(10L, "mek:oxygen"))
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 50L, "mek:ethene"))
                    .output(RecipeOutput.item("mek:hdpe_pellet", 1L))
                    .ticks(60)
                    .build());
        r.recipe(Recipe.builder("mek:reaction/substrate/water_ethene", "mek:pressurized_reaction_chamber")
                    .input(ItemIngredient.item(1L, "mek:substrate"))
                    .input(Ingredient.chemical(100L, "mek:ethene"))
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 200L, "mek:water"))
                    .output(RecipeOutput.item("mek:substrate", 8L))
                    .output(RecipeOutput.chemical("mek:oxygen", 10L))
                    .ticks(400)
                    .build());
        r.recipe(Recipe.builder("mek:reaction/substrate/water_hydrogen", "mek:pressurized_reaction_chamber")
                    .input(ItemIngredient.tagged(2L, "mek:fuels/bio"))
                    .input(Ingredient.chemical(100L, "mek:hydrogen"))
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 10L, "mek:water"))
                    .output(RecipeOutput.item("mek:substrate", 1L))
                    .output(RecipeOutput.chemical("mek:ethene", 300L))
                    .ticks(100)
                    .build());
        r.recipe(Recipe.builder("mek:reaction/wood_gasification/dusts_wood", "mek:pressurized_reaction_chamber")
                    .input(ItemIngredient.tagged(8L, "mek:dusts/wood"))
                    .input(Ingredient.chemical(25L, "mek:oxygen"))
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 25L, "mek:water"))
                    .output(RecipeOutput.chemical("mek:hydrogen", 25L))
                    .ticks(37)
                    .build());
        r.recipe(Recipe.builder("mek:reaction/wood_gasification/logs", "mek:pressurized_reaction_chamber")
                    .input(ItemIngredient.tagged(4L, "mek:logs"))
                    .input(Ingredient.chemical(400L, "mek:oxygen"))
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 400L, "mek:water"))
                    .output(RecipeOutput.item("mek:dust_charcoal", 1L))
                    .output(RecipeOutput.chemical("mek:hydrogen", 400L))
                    .ticks(600)
                    .build());
        r.recipe(Recipe.builder("mek:reaction/wood_gasification/planks", "mek:pressurized_reaction_chamber")
                    .input(ItemIngredient.of(20L, ItemMatcher.or(ItemMatcher.id("minecraft:acacia_planks"), ItemMatcher.id("minecraft:bamboo_mosaic"), ItemMatcher.id("minecraft:bamboo_planks"), ItemMatcher.id("minecraft:birch_planks"), ItemMatcher.id("minecraft:cherry_planks"), ItemMatcher.id("minecraft:crimson_planks"), ItemMatcher.id("minecraft:dark_oak_planks"), ItemMatcher.id("minecraft:jungle_planks"), ItemMatcher.id("minecraft:mangrove_planks"), ItemMatcher.id("minecraft:oak_planks"), ItemMatcher.id("minecraft:pale_oak_planks"), ItemMatcher.id("minecraft:poplar_planks"), ItemMatcher.id("minecraft:spruce_planks"), ItemMatcher.id("minecraft:warped_planks")), "minecraft:acacia_planks"))
                    .input(Ingredient.chemical(400L, "mek:oxygen"))
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 400L, "mek:water"))
                    .output(RecipeOutput.item("mek:dust_charcoal", 1L))
                    .output(RecipeOutput.chemical("mek:hydrogen", 400L))
                    .ticks(600)
                    .build());
        r.recipe(Recipe.builder("mek:reaction/wood_gasification/rods_wooden", "mek:pressurized_reaction_chamber")
                    .input(ItemIngredient.tagged(3L, "mek:rods/wooden"))
                    .input(Ingredient.chemical(10L, "mek:oxygen"))
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 10L, "mek:water"))
                    .output(RecipeOutput.chemical("mek:hydrogen", 10L))
                    .ticks(15)
                    .build());
        r.recipe(Recipe.builder("mek:reaction/wood_gasification/wooden_slabs", "mek:pressurized_reaction_chamber")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("minecraft:acacia_slab"), ItemMatcher.id("minecraft:bamboo_mosaic_slab"), ItemMatcher.id("minecraft:bamboo_slab"), ItemMatcher.id("minecraft:birch_slab"), ItemMatcher.id("minecraft:cherry_slab"), ItemMatcher.id("minecraft:crimson_slab"), ItemMatcher.id("minecraft:dark_oak_slab"), ItemMatcher.id("minecraft:jungle_slab"), ItemMatcher.id("minecraft:mangrove_slab"), ItemMatcher.id("minecraft:oak_slab"), ItemMatcher.id("minecraft:pale_oak_slab"), ItemMatcher.id("minecraft:poplar_slab"), ItemMatcher.id("minecraft:spruce_slab"), ItemMatcher.id("minecraft:warped_slab")), "minecraft:acacia_slab"))
                    .input(Ingredient.chemical(10L, "mek:oxygen"))
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 10L, "mek:water"))
                    .output(RecipeOutput.chemical("mek:hydrogen", 10L))
                    .ticks(15)
                    .build());
        r.recipe(Recipe.builder("mek:reaction/wood_gasification/wooden_stairs", "mek:pressurized_reaction_chamber")
                    .input(ItemIngredient.of(1L, ItemMatcher.or(ItemMatcher.id("minecraft:acacia_stairs"), ItemMatcher.id("minecraft:bamboo_mosaic_stairs"), ItemMatcher.id("minecraft:bamboo_stairs"), ItemMatcher.id("minecraft:birch_stairs"), ItemMatcher.id("minecraft:cherry_stairs"), ItemMatcher.id("minecraft:crimson_stairs"), ItemMatcher.id("minecraft:dark_oak_stairs"), ItemMatcher.id("minecraft:jungle_stairs"), ItemMatcher.id("minecraft:mangrove_stairs"), ItemMatcher.id("minecraft:oak_stairs"), ItemMatcher.id("minecraft:pale_oak_stairs"), ItemMatcher.id("minecraft:poplar_stairs"), ItemMatcher.id("minecraft:spruce_stairs"), ItemMatcher.id("minecraft:warped_stairs")), "minecraft:acacia_stairs"))
                    .input(Ingredient.chemical(15L, "mek:oxygen"))
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 15L, "mek:water"))
                    .output(RecipeOutput.chemical("mek:hydrogen", 15L))
                    .ticks(22)
                    .build());
        r.recipe(Recipe.builder("mek:rotary/brine/condense", "mek:rotary_condensentrator")
                    .input(Ingredient.chemical(1L, "mek:brine"))
                    .output(RecipeOutput.fluid("mek:brine", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:rotary/brine/vaporize", "mek:rotary_condensentrator")
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 1L, "mek:brine"))
                    .output(RecipeOutput.chemical("mek:brine", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:rotary/chlorine/condense", "mek:rotary_condensentrator")
                    .input(Ingredient.chemical(1L, "mek:chlorine"))
                    .output(RecipeOutput.fluid("mek:chlorine", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:rotary/chlorine/vaporize", "mek:rotary_condensentrator")
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 1L, "mek:chlorine"))
                    .output(RecipeOutput.chemical("mek:chlorine", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:rotary/deuterium/condense", "mek:rotary_condensentrator")
                    .input(Ingredient.tagged(ResourceTypes.CHEMICAL, 1L, "mek:deuterium"))
                    .output(RecipeOutput.fluid("mek:deuterium", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:rotary/deuterium/vaporize", "mek:rotary_condensentrator")
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 1L, "mek:deuterium"))
                    .output(RecipeOutput.chemical("mek:deuterium", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:rotary/ethene/condense", "mek:rotary_condensentrator")
                    .input(Ingredient.chemical(1L, "mek:ethene"))
                    .output(RecipeOutput.fluid("mek:ethene", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:rotary/ethene/vaporize", "mek:rotary_condensentrator")
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 1L, "mek:ethene"))
                    .output(RecipeOutput.chemical("mek:ethene", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:rotary/fusion_fuel/condense", "mek:rotary_condensentrator")
                    .input(Ingredient.tagged(ResourceTypes.CHEMICAL, 1L, "mek:fusion_fuel"))
                    .output(RecipeOutput.fluid("mek:fusion_fuel", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:rotary/fusion_fuel/vaporize", "mek:rotary_condensentrator")
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 1L, "mek:fusion_fuel"))
                    .output(RecipeOutput.chemical("mek:fusion_fuel", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:rotary/hydrofluoric_acid/condense", "mek:rotary_condensentrator")
                    .input(Ingredient.chemical(1L, "mek:hydrofluoric_acid"))
                    .output(RecipeOutput.fluid("mek:hydrofluoric_acid", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:rotary/hydrofluoric_acid/vaporize", "mek:rotary_condensentrator")
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 1L, "mek:hydrofluoric_acid"))
                    .output(RecipeOutput.chemical("mek:hydrofluoric_acid", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:rotary/hydrogen/condense", "mek:rotary_condensentrator")
                    .input(Ingredient.chemical(1L, "mek:hydrogen"))
                    .output(RecipeOutput.fluid("mek:hydrogen", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:rotary/hydrogen/vaporize", "mek:rotary_condensentrator")
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 1L, "mek:hydrogen"))
                    .output(RecipeOutput.chemical("mek:hydrogen", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:rotary/hydrogen_chloride/condense", "mek:rotary_condensentrator")
                    .input(Ingredient.chemical(1L, "mek:hydrogen_chloride"))
                    .output(RecipeOutput.fluid("mek:hydrogen_chloride", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:rotary/hydrogen_chloride/vaporize", "mek:rotary_condensentrator")
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 1L, "mek:hydrogen_chloride"))
                    .output(RecipeOutput.chemical("mek:hydrogen_chloride", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:rotary/lithium/condense", "mek:rotary_condensentrator")
                    .input(Ingredient.chemical(1L, "mek:lithium"))
                    .output(RecipeOutput.fluid("mek:lithium", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:rotary/lithium/vaporize", "mek:rotary_condensentrator")
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 1L, "mek:lithium"))
                    .output(RecipeOutput.chemical("mek:lithium", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:rotary/oxygen/condense", "mek:rotary_condensentrator")
                    .input(Ingredient.chemical(1L, "mek:oxygen"))
                    .output(RecipeOutput.fluid("mek:oxygen", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:rotary/oxygen/vaporize", "mek:rotary_condensentrator")
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 1L, "mek:oxygen"))
                    .output(RecipeOutput.chemical("mek:oxygen", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:rotary/sodium/condense", "mek:rotary_condensentrator")
                    .input(Ingredient.chemical(1L, "mek:sodium"))
                    .output(RecipeOutput.fluid("mek:sodium", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:rotary/sodium/vaporize", "mek:rotary_condensentrator")
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 1L, "mek:sodium"))
                    .output(RecipeOutput.chemical("mek:sodium", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
    }

    private static void recipes8(Registrars r) {
        r.recipe(Recipe.builder("mek:rotary/steam/condense", "mek:rotary_condensentrator")
                    .input(Ingredient.chemical(1L, "mek:steam"))
                    .output(RecipeOutput.fluid("mek:steam", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:rotary/steam/vaporize", "mek:rotary_condensentrator")
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 1L, "mek:steam"))
                    .output(RecipeOutput.chemical("mek:steam", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:rotary/sulfur_dioxide/condense", "mek:rotary_condensentrator")
                    .input(Ingredient.chemical(1L, "mek:sulfur_dioxide"))
                    .output(RecipeOutput.fluid("mek:sulfur_dioxide", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:rotary/sulfur_dioxide/vaporize", "mek:rotary_condensentrator")
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 1L, "mek:sulfur_dioxide"))
                    .output(RecipeOutput.chemical("mek:sulfur_dioxide", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:rotary/sulfur_trioxide/condense", "mek:rotary_condensentrator")
                    .input(Ingredient.chemical(1L, "mek:sulfur_trioxide"))
                    .output(RecipeOutput.fluid("mek:sulfur_trioxide", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:rotary/sulfur_trioxide/vaporize", "mek:rotary_condensentrator")
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 1L, "mek:sulfur_trioxide"))
                    .output(RecipeOutput.chemical("mek:sulfur_trioxide", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:rotary/sulfuric_acid/condense", "mek:rotary_condensentrator")
                    .input(Ingredient.chemical(1L, "mek:sulfuric_acid"))
                    .output(RecipeOutput.fluid("mek:sulfuric_acid", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:rotary/sulfuric_acid/vaporize", "mek:rotary_condensentrator")
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 1L, "mek:sulfuric_acid"))
                    .output(RecipeOutput.chemical("mek:sulfuric_acid", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:rotary/superheated_sodium/condense", "mek:rotary_condensentrator")
                    .input(Ingredient.chemical(1L, "mek:superheated_sodium"))
                    .output(RecipeOutput.fluid("mek:superheated_sodium", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:rotary/superheated_sodium/vaporize", "mek:rotary_condensentrator")
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 1L, "mek:superheated_sodium"))
                    .output(RecipeOutput.chemical("mek:superheated_sodium", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:rotary/tritium/condense", "mek:rotary_condensentrator")
                    .input(Ingredient.tagged(ResourceTypes.CHEMICAL, 1L, "mek:tritium"))
                    .output(RecipeOutput.fluid("mek:tritium", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:rotary/tritium/vaporize", "mek:rotary_condensentrator")
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 1L, "mek:tritium"))
                    .output(RecipeOutput.chemical("mek:tritium", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:rotary/uranium_hexafluoride/condense", "mek:rotary_condensentrator")
                    .input(Ingredient.chemical(1L, "mek:uranium_hexafluoride"))
                    .output(RecipeOutput.fluid("mek:uranium_hexafluoride", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:rotary/uranium_hexafluoride/vaporize", "mek:rotary_condensentrator")
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 1L, "mek:uranium_hexafluoride"))
                    .output(RecipeOutput.chemical("mek:uranium_hexafluoride", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:rotary/uranium_oxide/condense", "mek:rotary_condensentrator")
                    .input(Ingredient.chemical(1L, "mek:uranium_oxide"))
                    .output(RecipeOutput.fluid("mek:uranium_oxide", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:rotary/uranium_oxide/vaporize", "mek:rotary_condensentrator")
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 1L, "mek:uranium_oxide"))
                    .output(RecipeOutput.chemical("mek:uranium_oxide", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:rotary/water_vapor/condense", "mek:rotary_condensentrator")
                    .input(Ingredient.chemical(1L, "mek:water_vapor"))
                    .output(RecipeOutput.fluid("minecraft:water", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:rotary/water_vapor/vaporize", "mek:rotary_condensentrator")
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 1L, "mek:water"))
                    .output(RecipeOutput.chemical("mek:water_vapor", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:sawing/bamboo_block", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:bamboo_block"))
                    .output(RecipeOutput.item("minecraft:bamboo", 9L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/barrel", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:barrel"))
                    .output(RecipeOutput.item("minecraft:oak_planks", 7L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/bed/black", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:black_bed"))
                    .output(RecipeOutput.item("minecraft:oak_planks", 3L))
                    .output(RecipeOutput.item("minecraft:black_wool", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/bed/blue", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:blue_bed"))
                    .output(RecipeOutput.item("minecraft:oak_planks", 3L))
                    .output(RecipeOutput.item("minecraft:blue_wool", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/bed/brown", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:brown_bed"))
                    .output(RecipeOutput.item("minecraft:oak_planks", 3L))
                    .output(RecipeOutput.item("minecraft:brown_wool", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/bed/cyan", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:cyan_bed"))
                    .output(RecipeOutput.item("minecraft:oak_planks", 3L))
                    .output(RecipeOutput.item("minecraft:cyan_wool", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/bed/gray", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:gray_bed"))
                    .output(RecipeOutput.item("minecraft:oak_planks", 3L))
                    .output(RecipeOutput.item("minecraft:gray_wool", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/bed/green", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:green_bed"))
                    .output(RecipeOutput.item("minecraft:oak_planks", 3L))
                    .output(RecipeOutput.item("minecraft:green_wool", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/bed/light_blue", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:light_blue_bed"))
                    .output(RecipeOutput.item("minecraft:oak_planks", 3L))
                    .output(RecipeOutput.item("minecraft:light_blue_wool", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/bed/light_gray", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:light_gray_bed"))
                    .output(RecipeOutput.item("minecraft:oak_planks", 3L))
                    .output(RecipeOutput.item("minecraft:light_gray_wool", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/bed/lime", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:lime_bed"))
                    .output(RecipeOutput.item("minecraft:oak_planks", 3L))
                    .output(RecipeOutput.item("minecraft:lime_wool", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/bed/magenta", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:magenta_bed"))
                    .output(RecipeOutput.item("minecraft:oak_planks", 3L))
                    .output(RecipeOutput.item("minecraft:magenta_wool", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/bed/orange", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:orange_bed"))
                    .output(RecipeOutput.item("minecraft:oak_planks", 3L))
                    .output(RecipeOutput.item("minecraft:orange_wool", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/bed/pink", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:pink_bed"))
                    .output(RecipeOutput.item("minecraft:oak_planks", 3L))
                    .output(RecipeOutput.item("minecraft:pink_wool", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/bed/purple", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:purple_bed"))
                    .output(RecipeOutput.item("minecraft:oak_planks", 3L))
                    .output(RecipeOutput.item("minecraft:purple_wool", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/bed/red", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:red_bed"))
                    .output(RecipeOutput.item("minecraft:oak_planks", 3L))
                    .output(RecipeOutput.item("minecraft:red_wool", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/bed/straw", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:straw_bed"))
                    .output(RecipeOutput.item("minecraft:wheat", 6L))
                    .output(RecipeOutput.chance(ItemRef.of("minecraft:wheat", 1L), 75.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/bed/white", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:white_bed"))
                    .output(RecipeOutput.item("minecraft:oak_planks", 3L))
                    .output(RecipeOutput.item("minecraft:white_wool", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/bed/yellow", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:yellow_bed"))
                    .output(RecipeOutput.item("minecraft:oak_planks", 3L))
                    .output(RecipeOutput.item("minecraft:yellow_wool", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/boat/acacia", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:acacia_boat"))
                    .output(RecipeOutput.item("minecraft:acacia_planks", 5L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/boat/bamboo", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:bamboo_raft"))
                    .output(RecipeOutput.item("minecraft:bamboo_planks", 5L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/boat/birch", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:birch_boat"))
                    .output(RecipeOutput.item("minecraft:birch_planks", 5L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/boat/cherry", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:cherry_boat"))
                    .output(RecipeOutput.item("minecraft:cherry_planks", 5L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/boat/dark_oak", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:dark_oak_boat"))
                    .output(RecipeOutput.item("minecraft:dark_oak_planks", 5L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/boat/jungle", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:jungle_boat"))
                    .output(RecipeOutput.item("minecraft:jungle_planks", 5L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/boat/mangrove", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:mangrove_boat"))
                    .output(RecipeOutput.item("minecraft:mangrove_planks", 5L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/boat/oak", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:oak_boat"))
                    .output(RecipeOutput.item("minecraft:oak_planks", 5L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/boat/pale_oak", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:pale_oak_boat"))
                    .output(RecipeOutput.item("minecraft:pale_oak_planks", 5L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/boat/poplar", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:poplar_boat"))
                    .output(RecipeOutput.item("minecraft:poplar_planks", 5L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/boat/spruce", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:spruce_boat"))
                    .output(RecipeOutput.item("minecraft:spruce_planks", 5L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/bookshelf", "mek:precision_sawmill")
                    .input(ItemIngredient.tagged(1L, "mek:bookshelves"))
                    .output(RecipeOutput.item("minecraft:oak_planks", 6L))
                    .output(RecipeOutput.item("minecraft:book", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/button", "mek:precision_sawmill")
                    .input(ItemIngredient.tagged(1L, "mek:wooden_buttons"))
                    .output(RecipeOutput.item("mek:sawdust", 1L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 1L), 25.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/chest", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:chest"))
                    .output(RecipeOutput.item("minecraft:oak_planks", 8L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/chest_boat/acacia", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:acacia_chest_boat"))
                    .output(RecipeOutput.item("minecraft:acacia_boat", 1L))
                    .output(RecipeOutput.item("minecraft:chest", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/chest_boat/bamboo", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:bamboo_chest_raft"))
                    .output(RecipeOutput.item("minecraft:bamboo_raft", 1L))
                    .output(RecipeOutput.item("minecraft:chest", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/chest_boat/birch", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:birch_chest_boat"))
                    .output(RecipeOutput.item("minecraft:birch_boat", 1L))
                    .output(RecipeOutput.item("minecraft:chest", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/chest_boat/cherry", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:cherry_chest_boat"))
                    .output(RecipeOutput.item("minecraft:cherry_boat", 1L))
                    .output(RecipeOutput.item("minecraft:chest", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/chest_boat/dark_oak", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:dark_oak_chest_boat"))
                    .output(RecipeOutput.item("minecraft:dark_oak_boat", 1L))
                    .output(RecipeOutput.item("minecraft:chest", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/chest_boat/jungle", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:jungle_chest_boat"))
                    .output(RecipeOutput.item("minecraft:jungle_boat", 1L))
                    .output(RecipeOutput.item("minecraft:chest", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/chest_boat/mangrove", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:mangrove_chest_boat"))
                    .output(RecipeOutput.item("minecraft:mangrove_boat", 1L))
                    .output(RecipeOutput.item("minecraft:chest", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/chest_boat/oak", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:oak_chest_boat"))
                    .output(RecipeOutput.item("minecraft:oak_boat", 1L))
                    .output(RecipeOutput.item("minecraft:chest", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/chest_boat/pale_oak", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:pale_oak_chest_boat"))
                    .output(RecipeOutput.item("minecraft:pale_oak_boat", 1L))
                    .output(RecipeOutput.item("minecraft:chest", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/chest_boat/poplar", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:poplar_chest_boat"))
                    .output(RecipeOutput.item("minecraft:poplar_boat", 1L))
                    .output(RecipeOutput.item("minecraft:chest", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/chest_boat/spruce", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:spruce_chest_boat"))
                    .output(RecipeOutput.item("minecraft:spruce_boat", 1L))
                    .output(RecipeOutput.item("minecraft:chest", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/chiseled_bookshelf", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:chiseled_bookshelf"))
                    .output(RecipeOutput.item("minecraft:oak_planks", 6L))
                    .output(RecipeOutput.item("minecraft:oak_slab", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/composter", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:composter"))
                    .output(RecipeOutput.item("minecraft:oak_slab", 7L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/crafting_table", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:crafting_table"))
                    .output(RecipeOutput.item("minecraft:oak_planks", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/creaking_heart", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:creaking_heart"))
                    .output(RecipeOutput.item("minecraft:pale_oak_log", 2L))
                    .output(RecipeOutput.item("minecraft:resin_block", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/door/acacia", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:acacia_door"))
                    .output(RecipeOutput.item("minecraft:acacia_planks", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/door/bamboo", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:bamboo_door"))
                    .output(RecipeOutput.item("minecraft:bamboo_planks", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/door/birch", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:birch_door"))
                    .output(RecipeOutput.item("minecraft:birch_planks", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/door/cherry", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:cherry_door"))
                    .output(RecipeOutput.item("minecraft:cherry_planks", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/door/crimson", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:crimson_door"))
                    .output(RecipeOutput.item("minecraft:crimson_planks", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/door/dark_oak", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:dark_oak_door"))
                    .output(RecipeOutput.item("minecraft:dark_oak_planks", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/door/jungle", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:jungle_door"))
                    .output(RecipeOutput.item("minecraft:jungle_planks", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/door/mangrove", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:mangrove_door"))
                    .output(RecipeOutput.item("minecraft:mangrove_planks", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/door/oak", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:oak_door"))
                    .output(RecipeOutput.item("minecraft:oak_planks", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/door/pale_oak", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:pale_oak_door"))
                    .output(RecipeOutput.item("minecraft:pale_oak_planks", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/door/poplar", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:poplar_door"))
                    .output(RecipeOutput.item("minecraft:poplar_planks", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/door/spruce", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:spruce_door"))
                    .output(RecipeOutput.item("minecraft:spruce_planks", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/door/warped", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:warped_door"))
                    .output(RecipeOutput.item("minecraft:warped_planks", 2L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/dried_ghast", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:dried_ghast"))
                    .output(RecipeOutput.item("minecraft:ghast_tear", 2L))
                    .output(RecipeOutput.chance(ItemRef.of("minecraft:ghast_tear", 3L), 33.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/fence_gate/acacia", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:acacia_fence_gate"))
                    .output(RecipeOutput.item("minecraft:acacia_planks", 2L))
                    .output(RecipeOutput.item("minecraft:stick", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/fence_gate/bamboo", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:bamboo_fence_gate"))
                    .output(RecipeOutput.item("minecraft:bamboo_planks", 2L))
                    .output(RecipeOutput.item("minecraft:stick", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/fence_gate/birch", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:birch_fence_gate"))
                    .output(RecipeOutput.item("minecraft:birch_planks", 2L))
                    .output(RecipeOutput.item("minecraft:stick", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/fence_gate/cherry", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:cherry_fence_gate"))
                    .output(RecipeOutput.item("minecraft:cherry_planks", 2L))
                    .output(RecipeOutput.item("minecraft:stick", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/fence_gate/crimson", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:crimson_fence_gate"))
                    .output(RecipeOutput.item("minecraft:crimson_planks", 2L))
                    .output(RecipeOutput.item("minecraft:stick", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/fence_gate/dark_oak", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:dark_oak_fence_gate"))
                    .output(RecipeOutput.item("minecraft:dark_oak_planks", 2L))
                    .output(RecipeOutput.item("minecraft:stick", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/fence_gate/jungle", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:jungle_fence_gate"))
                    .output(RecipeOutput.item("minecraft:jungle_planks", 2L))
                    .output(RecipeOutput.item("minecraft:stick", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/fence_gate/mangrove", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:mangrove_fence_gate"))
                    .output(RecipeOutput.item("minecraft:mangrove_planks", 2L))
                    .output(RecipeOutput.item("minecraft:stick", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/fence_gate/oak", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:oak_fence_gate"))
                    .output(RecipeOutput.item("minecraft:oak_planks", 2L))
                    .output(RecipeOutput.item("minecraft:stick", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/fence_gate/pale_oak", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:pale_oak_fence_gate"))
                    .output(RecipeOutput.item("minecraft:pale_oak_planks", 2L))
                    .output(RecipeOutput.item("minecraft:stick", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/fence_gate/poplar", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:poplar_fence_gate"))
                    .output(RecipeOutput.item("minecraft:poplar_planks", 2L))
                    .output(RecipeOutput.item("minecraft:stick", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/fence_gate/spruce", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:spruce_fence_gate"))
                    .output(RecipeOutput.item("minecraft:spruce_planks", 2L))
                    .output(RecipeOutput.item("minecraft:stick", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/fence_gate/warped", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:warped_fence_gate"))
                    .output(RecipeOutput.item("minecraft:warped_planks", 2L))
                    .output(RecipeOutput.item("minecraft:stick", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/fences", "mek:precision_sawmill")
                    .input(ItemIngredient.tagged(1L, "mek:fences/wooden"))
                    .output(RecipeOutput.item("minecraft:stick", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/hanging_sign/acacia", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:acacia_hanging_sign"))
                    .output(RecipeOutput.item("minecraft:acacia_planks", 2L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 1L), 50.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/hanging_sign/bamboo", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:bamboo_hanging_sign"))
                    .output(RecipeOutput.item("minecraft:bamboo_planks", 2L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 1L), 50.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/hanging_sign/birch", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:birch_hanging_sign"))
                    .output(RecipeOutput.item("minecraft:birch_planks", 2L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 1L), 50.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/hanging_sign/cherry", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:cherry_hanging_sign"))
                    .output(RecipeOutput.item("minecraft:cherry_planks", 2L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 1L), 50.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/hanging_sign/crimson", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:crimson_hanging_sign"))
                    .output(RecipeOutput.item("minecraft:crimson_planks", 2L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 1L), 50.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/hanging_sign/dark_oak", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:dark_oak_hanging_sign"))
                    .output(RecipeOutput.item("minecraft:dark_oak_planks", 2L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 1L), 50.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/hanging_sign/jungle", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:jungle_hanging_sign"))
                    .output(RecipeOutput.item("minecraft:jungle_planks", 2L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 1L), 50.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/hanging_sign/mangrove", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:mangrove_hanging_sign"))
                    .output(RecipeOutput.item("minecraft:mangrove_planks", 2L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 1L), 50.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/hanging_sign/oak", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:oak_hanging_sign"))
                    .output(RecipeOutput.item("minecraft:oak_planks", 2L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 1L), 50.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/hanging_sign/pale_oak", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:pale_oak_hanging_sign"))
                    .output(RecipeOutput.item("minecraft:pale_oak_planks", 2L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 1L), 50.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/hanging_sign/poplar", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:poplar_hanging_sign"))
                    .output(RecipeOutput.item("minecraft:poplar_planks", 2L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 1L), 50.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/hanging_sign/spruce", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:spruce_hanging_sign"))
                    .output(RecipeOutput.item("minecraft:spruce_planks", 2L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 1L), 50.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/hanging_sign/warped", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:warped_hanging_sign"))
                    .output(RecipeOutput.item("minecraft:warped_planks", 2L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 1L), 50.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/item_frame", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:item_frame"))
                    .output(RecipeOutput.item("minecraft:stick", 8L))
                    .output(RecipeOutput.item("minecraft:leather", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/jukebox", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:jukebox"))
                    .output(RecipeOutput.item("minecraft:oak_planks", 8L))
                    .output(RecipeOutput.item("minecraft:diamond", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/ladder", "mek:precision_sawmill")
                    .input(ItemIngredient.item(3L, "minecraft:ladder"))
                    .output(RecipeOutput.item("minecraft:stick", 7L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/lectern", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:lectern"))
                    .output(RecipeOutput.item("minecraft:oak_planks", 8L))
                    .output(RecipeOutput.item("minecraft:book", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/log/acacia", "mek:precision_sawmill")
                    .input(ItemIngredient.tagged(1L, "mek:acacia_logs"))
                    .output(RecipeOutput.item("minecraft:acacia_planks", 6L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 1L), 25.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/log/birch", "mek:precision_sawmill")
                    .input(ItemIngredient.tagged(1L, "mek:birch_logs"))
                    .output(RecipeOutput.item("minecraft:birch_planks", 6L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 1L), 25.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/log/cherry", "mek:precision_sawmill")
                    .input(ItemIngredient.tagged(1L, "mek:cherry_logs"))
                    .output(RecipeOutput.item("minecraft:cherry_planks", 6L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 1L), 25.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/log/crimson", "mek:precision_sawmill")
                    .input(ItemIngredient.tagged(1L, "mek:crimson_stems"))
                    .output(RecipeOutput.item("minecraft:crimson_planks", 6L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 1L), 25.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/log/dark_oak", "mek:precision_sawmill")
                    .input(ItemIngredient.tagged(1L, "mek:dark_oak_logs"))
                    .output(RecipeOutput.item("minecraft:dark_oak_planks", 6L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 1L), 25.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/log/jungle", "mek:precision_sawmill")
                    .input(ItemIngredient.tagged(1L, "mek:jungle_logs"))
                    .output(RecipeOutput.item("minecraft:jungle_planks", 6L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 1L), 25.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/log/mangrove", "mek:precision_sawmill")
                    .input(ItemIngredient.tagged(1L, "mek:mangrove_logs"))
                    .output(RecipeOutput.item("minecraft:mangrove_planks", 6L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 1L), 25.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/log/oak", "mek:precision_sawmill")
                    .input(ItemIngredient.tagged(1L, "mek:oak_logs"))
                    .output(RecipeOutput.item("minecraft:oak_planks", 6L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 1L), 25.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/log/pale_oak", "mek:precision_sawmill")
                    .input(ItemIngredient.tagged(1L, "mek:pale_oak_logs"))
                    .output(RecipeOutput.item("minecraft:pale_oak_planks", 6L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 1L), 25.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/log/poplar", "mek:precision_sawmill")
                    .input(ItemIngredient.tagged(1L, "mek:poplar_logs"))
                    .output(RecipeOutput.item("minecraft:poplar_planks", 6L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 1L), 25.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/log/spruce", "mek:precision_sawmill")
                    .input(ItemIngredient.tagged(1L, "mek:spruce_logs"))
                    .output(RecipeOutput.item("minecraft:spruce_planks", 6L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 1L), 25.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/log/warped", "mek:precision_sawmill")
                    .input(ItemIngredient.tagged(1L, "mek:warped_stems"))
                    .output(RecipeOutput.item("minecraft:warped_planks", 6L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 1L), 25.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/melon", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:melon"))
                    .output(RecipeOutput.item("minecraft:melon_slice", 9L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/note_block", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:note_block"))
                    .output(RecipeOutput.item("minecraft:oak_planks", 8L))
                    .output(RecipeOutput.item("minecraft:redstone", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/planks", "mek:precision_sawmill")
                    .input(ItemIngredient.tagged(1L, "mek:planks"))
                    .output(RecipeOutput.item("minecraft:stick", 6L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 1L), 25.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/pressure_plate/acacia", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:acacia_pressure_plate"))
                    .output(RecipeOutput.item("minecraft:acacia_planks", 1L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 2L), 25.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/pressure_plate/bamboo", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:bamboo_pressure_plate"))
                    .output(RecipeOutput.item("minecraft:bamboo_planks", 1L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 2L), 25.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/pressure_plate/birch", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:birch_pressure_plate"))
                    .output(RecipeOutput.item("minecraft:birch_planks", 1L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 2L), 25.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/pressure_plate/cherry", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:cherry_pressure_plate"))
                    .output(RecipeOutput.item("minecraft:cherry_planks", 1L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 2L), 25.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/pressure_plate/crimson", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:crimson_pressure_plate"))
                    .output(RecipeOutput.item("minecraft:crimson_planks", 1L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 2L), 25.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/pressure_plate/dark_oak", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:dark_oak_pressure_plate"))
                    .output(RecipeOutput.item("minecraft:dark_oak_planks", 1L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 2L), 25.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/pressure_plate/jungle", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:jungle_pressure_plate"))
                    .output(RecipeOutput.item("minecraft:jungle_planks", 1L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 2L), 25.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/pressure_plate/mangrove", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:mangrove_pressure_plate"))
                    .output(RecipeOutput.item("minecraft:mangrove_planks", 1L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 2L), 25.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/pressure_plate/oak", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:oak_pressure_plate"))
                    .output(RecipeOutput.item("minecraft:oak_planks", 1L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 2L), 25.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/pressure_plate/pale_oak", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:pale_oak_pressure_plate"))
                    .output(RecipeOutput.item("minecraft:pale_oak_planks", 1L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 2L), 25.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/pressure_plate/poplar", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:poplar_pressure_plate"))
                    .output(RecipeOutput.item("minecraft:poplar_planks", 1L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 2L), 25.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/pressure_plate/spruce", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:spruce_pressure_plate"))
                    .output(RecipeOutput.item("minecraft:spruce_planks", 1L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 2L), 25.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/pressure_plate/warped", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:warped_pressure_plate"))
                    .output(RecipeOutput.item("minecraft:warped_planks", 1L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 2L), 25.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/pumpkin", "mek:precision_sawmill")
                    .input(ItemIngredient.tagged(1L, "mek:pumpkins/normal"))
                    .output(RecipeOutput.item("minecraft:carved_pumpkin", 1L))
                    .output(RecipeOutput.item("minecraft:pumpkin_seeds", 4L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/redstone_torch", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:redstone_torch"))
                    .output(RecipeOutput.item("minecraft:stick", 1L))
                    .output(RecipeOutput.item("minecraft:redstone", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/saddle", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:saddle"))
                    .output(RecipeOutput.item("minecraft:leather", 3L))
                    .output(RecipeOutput.chance(ItemRef.of("minecraft:iron_ingot", 1L), 5.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/shelf/acacia", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:acacia_shelf"))
                    .output(RecipeOutput.item("minecraft:stripped_acacia_log", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/shelf/bamboo", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:bamboo_shelf"))
                    .output(RecipeOutput.item("minecraft:stripped_bamboo_block", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/shelf/birch", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:birch_shelf"))
                    .output(RecipeOutput.item("minecraft:stripped_birch_log", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/shelf/cherry", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:cherry_shelf"))
                    .output(RecipeOutput.item("minecraft:stripped_cherry_log", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/shelf/crimson", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:crimson_shelf"))
                    .output(RecipeOutput.item("minecraft:stripped_crimson_stem", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/shelf/dark_oak", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:dark_oak_shelf"))
                    .output(RecipeOutput.item("minecraft:stripped_dark_oak_log", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/shelf/jungle", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:jungle_shelf"))
                    .output(RecipeOutput.item("minecraft:stripped_jungle_log", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/shelf/mangrove", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:mangrove_shelf"))
                    .output(RecipeOutput.item("minecraft:stripped_mangrove_log", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/shelf/oak", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:oak_shelf"))
                    .output(RecipeOutput.item("minecraft:stripped_oak_log", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/shelf/pale_oak", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:pale_oak_shelf"))
                    .output(RecipeOutput.item("minecraft:stripped_pale_oak_log", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/shelf/poplar", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:poplar_shelf"))
                    .output(RecipeOutput.item("minecraft:stripped_poplar_log", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/shelf/spruce", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:spruce_shelf"))
                    .output(RecipeOutput.item("minecraft:stripped_spruce_log", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/shelf/warped", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:warped_shelf"))
                    .output(RecipeOutput.item("minecraft:stripped_warped_stem", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/sign", "mek:precision_sawmill")
                    .input(ItemIngredient.tagged(1L, "mek:signs"))
                    .output(RecipeOutput.item("minecraft:stick", 3L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 1L), 25.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/slabs", "mek:precision_sawmill")
                    .input(ItemIngredient.tagged(1L, "mek:wooden_slabs"))
                    .output(RecipeOutput.item("minecraft:stick", 3L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 1L), 12.5))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/soul_torch", "mek:precision_sawmill")
                    .input(ItemIngredient.item(4L, "minecraft:soul_torch"))
                    .output(RecipeOutput.item("minecraft:torch", 4L))
                    .output(RecipeOutput.item("minecraft:soul_soil", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/stairs", "mek:precision_sawmill")
                    .input(ItemIngredient.tagged(1L, "mek:wooden_stairs"))
                    .output(RecipeOutput.item("minecraft:stick", 9L))
                    .output(RecipeOutput.chance(ItemRef.of("mek:sawdust", 1L), 37.5))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/stick", "mek:precision_sawmill")
                    .input(ItemIngredient.tagged(1L, "mek:rods/wooden"))
                    .output(RecipeOutput.item("mek:sawdust", 1L))
                    .ticks(200)
                    .build());
    }

    private static void recipes9(Registrars r) {
        r.recipe(Recipe.builder("mek:sawing/torch", "mek:precision_sawmill")
                    .input(ItemIngredient.item(4L, "minecraft:torch"))
                    .output(RecipeOutput.item("minecraft:stick", 1L))
                    .output(RecipeOutput.item("minecraft:coal", 1L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/trapdoor/acacia", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:acacia_trapdoor"))
                    .output(RecipeOutput.item("minecraft:acacia_planks", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/trapdoor/bamboo", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:bamboo_trapdoor"))
                    .output(RecipeOutput.item("minecraft:bamboo_planks", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/trapdoor/birch", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:birch_trapdoor"))
                    .output(RecipeOutput.item("minecraft:birch_planks", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/trapdoor/cherry", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:cherry_trapdoor"))
                    .output(RecipeOutput.item("minecraft:cherry_planks", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/trapdoor/crimson", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:crimson_trapdoor"))
                    .output(RecipeOutput.item("minecraft:crimson_planks", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/trapdoor/dark_oak", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:dark_oak_trapdoor"))
                    .output(RecipeOutput.item("minecraft:dark_oak_planks", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/trapdoor/jungle", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:jungle_trapdoor"))
                    .output(RecipeOutput.item("minecraft:jungle_planks", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/trapdoor/mangrove", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:mangrove_trapdoor"))
                    .output(RecipeOutput.item("minecraft:mangrove_planks", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/trapdoor/oak", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:oak_trapdoor"))
                    .output(RecipeOutput.item("minecraft:oak_planks", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/trapdoor/pale_oak", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:pale_oak_trapdoor"))
                    .output(RecipeOutput.item("minecraft:pale_oak_planks", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/trapdoor/poplar", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:poplar_trapdoor"))
                    .output(RecipeOutput.item("minecraft:poplar_planks", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/trapdoor/spruce", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:spruce_trapdoor"))
                    .output(RecipeOutput.item("minecraft:spruce_planks", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/trapdoor/warped", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:warped_trapdoor"))
                    .output(RecipeOutput.item("minecraft:warped_planks", 3L))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:sawing/trapped_chest", "mek:precision_sawmill")
                    .input(ItemIngredient.item(1L, "minecraft:trapped_chest"))
                    .output(RecipeOutput.item("minecraft:oak_planks", 8L))
                    .output(RecipeOutput.chance(ItemRef.of("minecraft:tripwire_hook", 1L), 75.0))
                    .ticks(200)
                    .build());
        r.recipe(Recipe.builder("mek:separator/brine", "mek:electrolytic_separator")
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 10L, "mek:brine"))
                    .output(RecipeOutput.chemical("mek:sodium", 1L))
                    .output(RecipeOutput.chemical("mek:chlorine", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:separator/heavy_water", "mek:electrolytic_separator")
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 2L, "mek:heavy_water"))
                    .output(RecipeOutput.chemical("mek:deuterium", 2L))
                    .output(RecipeOutput.chemical("mek:oxygen", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
        r.recipe(Recipe.builder("mek:separator/water", "mek:electrolytic_separator")
                    .input(Ingredient.tagged(ResourceTypes.FLUID, 2L, "mek:water"))
                    .output(RecipeOutput.chemical("mek:hydrogen", 2L))
                    .output(RecipeOutput.chemical("mek:oxygen", 1L))
                    .ticks(1)
                    .continuous()
                    .build());
    }

    /** 配方标签：与 CE 侧同一批成员，类型由核心注册表决定。*/
    private static void recipeTags(Registrars r) {
        r.itemTag("mek:acacia_logs", "minecraft:acacia_log", "minecraft:acacia_wood", "minecraft:stripped_acacia_log", "minecraft:stripped_acacia_wood");
        r.itemTag("mek:alloys/atomic", "mek:alloy_atomic");
        r.itemTag("mek:alloys/basic", "minecraft:redstone");
        r.itemTag("mek:alloys/infused", "mek:alloy_infused");
        r.itemTag("mek:alloys/reinforced", "mek:alloy_reinforced");
        r.itemTag("mek:alloys/ultimate", "mek:alloy_atomic");
        r.itemTag("mek:barrels/wooden", "minecraft:barrel");
        r.itemTag("mek:bars", "minecraft:copper_bars", "minecraft:exposed_copper_bars", "minecraft:iron_bars", "minecraft:oxidized_copper_bars", "minecraft:waxed_copper_bars", "minecraft:waxed_exposed_copper_bars", "minecraft:waxed_oxidized_copper_bars", "minecraft:waxed_weathered_copper_bars", "minecraft:weathered_copper_bars");
        r.itemTag("mek:beds", "minecraft:black_bed", "minecraft:blue_bed", "minecraft:brown_bed", "minecraft:cyan_bed", "minecraft:gray_bed", "minecraft:green_bed", "minecraft:light_blue_bed", "minecraft:light_gray_bed", "minecraft:lime_bed", "minecraft:magenta_bed", "minecraft:orange_bed", "minecraft:pink_bed", "minecraft:purple_bed", "minecraft:red_bed", "minecraft:white_bed", "minecraft:yellow_bed");
        r.tag(ResourceTypes.INFUSION, "mek:bio", "mek:bio");
        r.itemTag("mek:birch_logs", "minecraft:birch_log", "minecraft:birch_wood", "minecraft:stripped_birch_log", "minecraft:stripped_birch_wood");
        r.itemTag("mek:bookshelves", "minecraft:bookshelf");
        r.tag(ResourceTypes.FLUID, "mek:brine", "mek:brine", "mek:flowing_brine");
        r.tag(ResourceTypes.INFUSION, "mek:carbon", "mek:carbon");
        r.itemTag("mek:cherry_logs", "minecraft:cherry_log", "minecraft:cherry_wood", "minecraft:stripped_cherry_log", "minecraft:stripped_cherry_wood");
        r.itemTag("mek:chests/wooden", "minecraft:chest");
        r.tag(ResourceTypes.FLUID, "mek:chlorine", "mek:chlorine", "mek:flowing_chlorine");
        r.itemTag("mek:circuits/advanced", "mek:advanced_control_circuit");
        r.itemTag("mek:circuits/basic", "mek:basic_control_circuit");
        r.itemTag("mek:circuits/elite", "mek:elite_control_circuit");
        r.itemTag("mek:circuits/ultimate", "mek:ultimate_control_circuit");
        r.itemTag("mek:clumps/copper", "mek:clump_copper");
        r.itemTag("mek:clumps/gold", "mek:clump_gold");
        r.itemTag("mek:clumps/iron", "mek:clump_iron");
        r.itemTag("mek:clumps/lead", "mek:clump_lead");
        r.itemTag("mek:clumps/osmium", "mek:clump_osmium");
        r.itemTag("mek:clumps/tin", "mek:clump_tin");
        r.itemTag("mek:clumps/uranium", "mek:clump_uranium");
        r.itemTag("mek:coals", "minecraft:charcoal", "minecraft:coal");
        r.itemTag("mek:cobblestones/deepslate", "minecraft:cobbled_deepslate");
        r.itemTag("mek:cobblestones/mossy", "minecraft:mossy_cobblestone");
        r.itemTag("mek:cobblestones/normal", "minecraft:blackstone", "minecraft:cobblestone");
        r.itemTag("mek:colorable/banners", "minecraft:black_banner", "minecraft:blue_banner", "minecraft:brown_banner", "minecraft:cyan_banner", "minecraft:gray_banner", "minecraft:green_banner", "minecraft:light_blue_banner", "minecraft:light_gray_banner", "minecraft:lime_banner", "minecraft:magenta_banner", "minecraft:orange_banner", "minecraft:pink_banner", "minecraft:purple_banner", "minecraft:red_banner", "minecraft:white_banner", "minecraft:yellow_banner");
        r.itemTag("mek:colorable/beds", "minecraft:black_bed", "minecraft:blue_bed", "minecraft:brown_bed", "minecraft:cyan_bed", "minecraft:gray_bed", "minecraft:green_bed", "minecraft:light_blue_bed", "minecraft:light_gray_bed", "minecraft:lime_bed", "minecraft:magenta_bed", "minecraft:orange_bed", "minecraft:pink_bed", "minecraft:purple_bed", "minecraft:red_bed", "minecraft:white_bed", "minecraft:yellow_bed");
        r.itemTag("mek:colorable/candle", "minecraft:black_candle", "minecraft:blue_candle", "minecraft:brown_candle", "minecraft:cyan_candle", "minecraft:gray_candle", "minecraft:green_candle", "minecraft:light_blue_candle", "minecraft:light_gray_candle", "minecraft:lime_candle", "minecraft:magenta_candle", "minecraft:orange_candle", "minecraft:pink_candle", "minecraft:purple_candle", "minecraft:red_candle", "minecraft:white_candle", "minecraft:yellow_candle");
        r.itemTag("mek:colorable/carpets", "minecraft:black_carpet", "minecraft:blue_carpet", "minecraft:brown_carpet", "minecraft:cyan_carpet", "minecraft:gray_carpet", "minecraft:green_carpet", "minecraft:light_blue_carpet", "minecraft:light_gray_carpet", "minecraft:lime_carpet", "minecraft:magenta_carpet", "minecraft:orange_carpet", "minecraft:pink_carpet", "minecraft:purple_carpet", "minecraft:red_carpet", "minecraft:white_carpet", "minecraft:yellow_carpet");
        r.itemTag("mek:colorable/concrete", "minecraft:black_concrete", "minecraft:blue_concrete", "minecraft:brown_concrete", "minecraft:cyan_concrete", "minecraft:gray_concrete", "minecraft:green_concrete", "minecraft:light_blue_concrete", "minecraft:light_gray_concrete", "minecraft:lime_concrete", "minecraft:magenta_concrete", "minecraft:orange_concrete", "minecraft:pink_concrete", "minecraft:purple_concrete", "minecraft:red_concrete", "minecraft:white_concrete", "minecraft:yellow_concrete");
        r.itemTag("mek:colorable/concrete/slabs", "minecraft:black_concrete_slab", "minecraft:blue_concrete_slab", "minecraft:brown_concrete_slab", "minecraft:cyan_concrete_slab", "minecraft:gray_concrete_slab", "minecraft:green_concrete_slab", "minecraft:light_blue_concrete_slab", "minecraft:light_gray_concrete_slab", "minecraft:lime_concrete_slab", "minecraft:magenta_concrete_slab", "minecraft:orange_concrete_slab", "minecraft:pink_concrete_slab", "minecraft:purple_concrete_slab", "minecraft:red_concrete_slab", "minecraft:white_concrete_slab", "minecraft:yellow_concrete_slab");
        r.itemTag("mek:colorable/concrete/stairs", "minecraft:black_concrete_stairs", "minecraft:blue_concrete_stairs", "minecraft:brown_concrete_stairs", "minecraft:cyan_concrete_stairs", "minecraft:gray_concrete_stairs", "minecraft:green_concrete_stairs", "minecraft:light_blue_concrete_stairs", "minecraft:light_gray_concrete_stairs", "minecraft:lime_concrete_stairs", "minecraft:magenta_concrete_stairs", "minecraft:orange_concrete_stairs", "minecraft:pink_concrete_stairs", "minecraft:purple_concrete_stairs", "minecraft:red_concrete_stairs", "minecraft:white_concrete_stairs", "minecraft:yellow_concrete_stairs");
        r.itemTag("mek:colorable/concrete_powder", "minecraft:black_concrete_powder", "minecraft:blue_concrete_powder", "minecraft:brown_concrete_powder", "minecraft:cyan_concrete_powder", "minecraft:gray_concrete_powder", "minecraft:green_concrete_powder", "minecraft:light_blue_concrete_powder", "minecraft:light_gray_concrete_powder", "minecraft:lime_concrete_powder", "minecraft:magenta_concrete_powder", "minecraft:orange_concrete_powder", "minecraft:pink_concrete_powder", "minecraft:purple_concrete_powder", "minecraft:red_concrete_powder", "minecraft:white_concrete_powder", "minecraft:yellow_concrete_powder");
        r.itemTag("mek:colorable/cushions", "minecraft:black_cushion", "minecraft:blue_cushion", "minecraft:brown_cushion", "minecraft:cyan_cushion", "minecraft:gray_cushion", "minecraft:green_cushion", "minecraft:light_blue_cushion", "minecraft:light_gray_cushion", "minecraft:lime_cushion", "minecraft:magenta_cushion", "minecraft:orange_cushion", "minecraft:pink_cushion", "minecraft:purple_cushion", "minecraft:red_cushion", "minecraft:white_cushion", "minecraft:yellow_cushion");
        r.itemTag("mek:colorable/glass", "minecraft:black_stained_glass", "minecraft:blue_stained_glass", "minecraft:brown_stained_glass", "minecraft:cyan_stained_glass", "minecraft:gray_stained_glass", "minecraft:green_stained_glass", "minecraft:light_blue_stained_glass", "minecraft:light_gray_stained_glass", "minecraft:lime_stained_glass", "minecraft:magenta_stained_glass", "minecraft:orange_stained_glass", "minecraft:pink_stained_glass", "minecraft:purple_stained_glass", "minecraft:red_stained_glass", "minecraft:white_stained_glass", "minecraft:yellow_stained_glass");
        r.itemTag("mek:colorable/glass_panes", "minecraft:black_stained_glass_pane", "minecraft:blue_stained_glass_pane", "minecraft:brown_stained_glass_pane", "minecraft:cyan_stained_glass_pane", "minecraft:gray_stained_glass_pane", "minecraft:green_stained_glass_pane", "minecraft:light_blue_stained_glass_pane", "minecraft:light_gray_stained_glass_pane", "minecraft:lime_stained_glass_pane", "minecraft:magenta_stained_glass_pane", "minecraft:orange_stained_glass_pane", "minecraft:pink_stained_glass_pane", "minecraft:purple_stained_glass_pane", "minecraft:red_stained_glass_pane", "minecraft:white_stained_glass_pane", "minecraft:yellow_stained_glass_pane");
        r.itemTag("mek:colorable/glazed_terracotta", "minecraft:black_glazed_terracotta", "minecraft:blue_glazed_terracotta", "minecraft:brown_glazed_terracotta", "minecraft:cyan_glazed_terracotta", "minecraft:gray_glazed_terracotta", "minecraft:green_glazed_terracotta", "minecraft:light_blue_glazed_terracotta", "minecraft:light_gray_glazed_terracotta", "minecraft:lime_glazed_terracotta", "minecraft:magenta_glazed_terracotta", "minecraft:orange_glazed_terracotta", "minecraft:pink_glazed_terracotta", "minecraft:purple_glazed_terracotta", "minecraft:red_glazed_terracotta", "minecraft:white_glazed_terracotta", "minecraft:yellow_glazed_terracotta");
        r.itemTag("mek:colorable/harnesses", "minecraft:black_harness", "minecraft:blue_harness", "minecraft:brown_harness", "minecraft:cyan_harness", "minecraft:gray_harness", "minecraft:green_harness", "minecraft:light_blue_harness", "minecraft:light_gray_harness", "minecraft:lime_harness", "minecraft:magenta_harness", "minecraft:orange_harness", "minecraft:pink_harness", "minecraft:purple_harness", "minecraft:red_harness", "minecraft:white_harness", "minecraft:yellow_harness");
        r.itemTag("mek:colorable/terracotta", "minecraft:black_terracotta", "minecraft:blue_terracotta", "minecraft:brown_terracotta", "minecraft:cyan_terracotta", "minecraft:gray_terracotta", "minecraft:green_terracotta", "minecraft:light_blue_terracotta", "minecraft:light_gray_terracotta", "minecraft:lime_terracotta", "minecraft:magenta_terracotta", "minecraft:orange_terracotta", "minecraft:pink_terracotta", "minecraft:purple_terracotta", "minecraft:red_terracotta", "minecraft:white_terracotta", "minecraft:yellow_terracotta");
        r.itemTag("mek:colorable/wool", "minecraft:black_wool", "minecraft:blue_wool", "minecraft:brown_wool", "minecraft:cyan_wool", "minecraft:gray_wool", "minecraft:green_wool", "minecraft:light_blue_wool", "minecraft:light_gray_wool", "minecraft:lime_wool", "minecraft:magenta_wool", "minecraft:orange_wool", "minecraft:pink_wool", "minecraft:purple_wool", "minecraft:red_wool", "minecraft:white_wool", "minecraft:yellow_wool");
        r.itemTag("mek:colorable/wool_slabs", "minecraft:black_wool_slab", "minecraft:blue_wool_slab", "minecraft:brown_wool_slab", "minecraft:cyan_wool_slab", "minecraft:gray_wool_slab", "minecraft:green_wool_slab", "minecraft:light_blue_wool_slab", "minecraft:light_gray_wool_slab", "minecraft:lime_wool_slab", "minecraft:magenta_wool_slab", "minecraft:orange_wool_slab", "minecraft:pink_wool_slab", "minecraft:purple_wool_slab", "minecraft:red_wool_slab", "minecraft:white_wool_slab", "minecraft:yellow_wool_slab");
        r.itemTag("mek:colorable/wool_stairs", "minecraft:black_wool_stairs", "minecraft:blue_wool_stairs", "minecraft:brown_wool_stairs", "minecraft:cyan_wool_stairs", "minecraft:gray_wool_stairs", "minecraft:green_wool_stairs", "minecraft:light_blue_wool_stairs", "minecraft:light_gray_wool_stairs", "minecraft:lime_wool_stairs", "minecraft:magenta_wool_stairs", "minecraft:orange_wool_stairs", "minecraft:pink_wool_stairs", "minecraft:purple_wool_stairs", "minecraft:red_wool_stairs", "minecraft:white_wool_stairs", "minecraft:yellow_wool_stairs");
        r.itemTag("mek:crimson_stems", "minecraft:crimson_hyphae", "minecraft:crimson_stem", "minecraft:stripped_crimson_hyphae", "minecraft:stripped_crimson_stem");
        r.itemTag("mek:crops/cactus", "minecraft:cactus");
        r.itemTag("mek:crops/cocoa_bean", "minecraft:cocoa_beans");
        r.itemTag("mek:crops/melon", "minecraft:melon");
        r.itemTag("mek:crops/pumpkin", "minecraft:pumpkin");
        r.itemTag("mek:crops/sugar_cane", "minecraft:sugar_cane");
        r.itemTag("mek:crops/wheat", "minecraft:wheat");
        r.itemTag("mek:crystals/copper", "mek:crystal_copper");
        r.itemTag("mek:crystals/gold", "mek:crystal_gold");
        r.itemTag("mek:crystals/iron", "mek:crystal_iron");
        r.itemTag("mek:crystals/lead", "mek:crystal_lead");
        r.itemTag("mek:crystals/osmium", "mek:crystal_osmium");
        r.itemTag("mek:crystals/tin", "mek:crystal_tin");
        r.itemTag("mek:crystals/uranium", "mek:crystal_uranium");
        r.itemTag("mek:cushions", "minecraft:black_cushion", "minecraft:blue_cushion", "minecraft:brown_cushion", "minecraft:cyan_cushion", "minecraft:gray_cushion", "minecraft:green_cushion", "minecraft:light_blue_cushion", "minecraft:light_gray_cushion", "minecraft:lime_cushion", "minecraft:magenta_cushion", "minecraft:orange_cushion", "minecraft:pink_cushion", "minecraft:purple_cushion", "minecraft:red_cushion", "minecraft:white_cushion", "minecraft:yellow_cushion");
        r.itemTag("mek:dark_oak_logs", "minecraft:dark_oak_log", "minecraft:dark_oak_wood", "minecraft:stripped_dark_oak_log", "minecraft:stripped_dark_oak_wood");
        r.tag(ResourceTypes.CHEMICAL, "mek:deuterium", "mek:deuterium", "mek:flowing_deuterium");
        r.tag(ResourceTypes.FLUID, "mek:deuterium", "mek:deuterium", "mek:flowing_deuterium");
        r.tag(ResourceTypes.INFUSION, "mek:diamond", "mek:diamond");
        r.itemTag("mek:dirty_dusts/copper", "mek:dirty_dust_copper");
        r.itemTag("mek:dirty_dusts/gold", "mek:dirty_dust_gold");
        r.itemTag("mek:dirty_dusts/iron", "mek:dirty_dust_iron");
        r.itemTag("mek:dirty_dusts/lead", "mek:dirty_dust_lead");
        r.itemTag("mek:dirty_dusts/osmium", "mek:dirty_dust_osmium");
        r.itemTag("mek:dirty_dusts/tin", "mek:dirty_dust_tin");
        r.itemTag("mek:dirty_dusts/uranium", "mek:dirty_dust_uranium");
        r.itemTag("mek:dusts/charcoal", "mek:dust_charcoal");
        r.itemTag("mek:dusts/coal", "mek:dust_coal");
        r.itemTag("mek:dusts/copper", "mek:dust_copper");
        r.itemTag("mek:dusts/diamond", "mek:dust_diamond");
        r.itemTag("mek:dusts/emerald", "mek:dust_emerald");
        r.itemTag("mek:dusts/fluorite", "mek:dust_fluorite");
        r.itemTag("mek:dusts/glowstone", "minecraft:glowstone_dust");
        r.itemTag("mek:dusts/gold", "mek:dust_gold");
        r.itemTag("mek:dusts/iron", "mek:dust_iron");
        r.itemTag("mek:dusts/lapis", "mek:dust_lapis_lazuli");
        r.itemTag("mek:dusts/lithium", "mek:dust_lithium");
        r.itemTag("mek:dusts/netherite", "mek:dust_netherite");
        r.itemTag("mek:dusts/obsidian", "mek:dust_obsidian");
        r.itemTag("mek:dusts/osmium", "mek:dust_osmium");
        r.itemTag("mek:dusts/quartz", "mek:dust_quartz");
        r.itemTag("mek:dusts/redstone", "minecraft:redstone");
        r.itemTag("mek:dusts/refined_obsidian", "mek:dust_refined_obsidian");
        r.itemTag("mek:dusts/salt", "mek:salt");
        r.itemTag("mek:dusts/sulfur", "mek:dust_sulfur");
        r.itemTag("mek:dusts/tin", "mek:dust_tin");
        r.itemTag("mek:dusts/wood", "mek:sawdust");
        r.itemTag("mek:dyes/black", "minecraft:black_dye");
        r.itemTag("mek:dyes/blue", "minecraft:blue_dye");
        r.itemTag("mek:dyes/gray", "minecraft:gray_dye");
        r.itemTag("mek:dyes/green", "minecraft:green_dye");
        r.itemTag("mek:dyes/orange", "minecraft:orange_dye");
        r.itemTag("mek:dyes/pink", "minecraft:pink_dye");
        r.itemTag("mek:dyes/purple", "minecraft:purple_dye");
        r.itemTag("mek:dyes/red", "minecraft:red_dye");
        r.itemTag("mek:dyes/white", "minecraft:white_dye");
        r.itemTag("mek:dyes/yellow", "minecraft:yellow_dye");
        r.itemTag("mek:eggs", "minecraft:blue_egg", "minecraft:brown_egg", "minecraft:egg");
        r.itemTag("mek:ender_pearls", "minecraft:ender_pearl");
        r.itemTag("mek:enriched/carbon", "mek:enriched_carbon");
        r.itemTag("mek:enriched/diamond", "mek:enriched_diamond");
        r.itemTag("mek:enriched/gold", "mek:enriched_gold");
        r.itemTag("mek:enriched/obsidian", "mek:enriched_refined_obsidian");
        r.itemTag("mek:enriched/redstone", "mek:enriched_redstone");
        r.itemTag("mek:enriched/tin", "mek:enriched_tin");
        r.tag(ResourceTypes.FLUID, "mek:ethene", "mek:ethene", "mek:flowing_ethene");
        r.itemTag("mek:fences/wooden", "minecraft:acacia_fence", "minecraft:bamboo_fence", "minecraft:birch_fence", "minecraft:cherry_fence", "minecraft:crimson_fence", "minecraft:dark_oak_fence", "minecraft:jungle_fence", "minecraft:mangrove_fence", "minecraft:oak_fence", "minecraft:pale_oak_fence", "minecraft:poplar_fence", "minecraft:spruce_fence", "minecraft:warped_fence");
        r.itemTag("mek:flowers/small", "minecraft:allium", "minecraft:azure_bluet", "minecraft:blue_orchid", "minecraft:closed_eyeblossom", "minecraft:cornflower", "minecraft:dandelion", "minecraft:golden_dandelion", "minecraft:lily_of_the_valley", "minecraft:open_eyeblossom", "minecraft:orange_tulip", "minecraft:oxeye_daisy", "minecraft:pink_tulip", "minecraft:poppy", "minecraft:red_tulip", "minecraft:torchflower", "minecraft:white_tulip", "minecraft:wither_rose");
        r.itemTag("mek:flowers/tall", "minecraft:lilac", "minecraft:peony", "minecraft:rose_bush", "minecraft:sunflower");
        r.itemTag("mek:foods/berry", "minecraft:glow_berries", "minecraft:sweet_berries");
        r.itemTag("mek:fuels/bio", "mek:bio_fuel");
        r.itemTag("mek:fuels/block/bio", "mek:block_bio_fuel");
        r.tag(ResourceTypes.INFUSION, "mek:fungi", "mek:fungi");
        r.tag(ResourceTypes.CHEMICAL, "mek:fusion_fuel", "mek:flowing_fusion_fuel", "mek:fusion_fuel");
        r.tag(ResourceTypes.FLUID, "mek:fusion_fuel", "mek:flowing_fusion_fuel", "mek:fusion_fuel");
        r.itemTag("mek:gems/diamond", "minecraft:diamond");
        r.itemTag("mek:gems/emerald", "minecraft:emerald");
        r.itemTag("mek:gems/fluorite", "mek:fluorite_gem");
        r.itemTag("mek:gems/lapis", "minecraft:lapis_lazuli");
        r.itemTag("mek:gems/quartz", "minecraft:quartz");
        r.itemTag("mek:glass_blocks/cheap", "minecraft:glass", "minecraft:glass_pane");
        r.itemTag("mek:glass_panes", "minecraft:black_stained_glass_pane", "minecraft:blue_stained_glass_pane", "minecraft:brown_stained_glass_pane", "minecraft:cyan_stained_glass_pane", "minecraft:glass_pane", "minecraft:gray_stained_glass_pane", "minecraft:green_stained_glass_pane", "minecraft:light_blue_stained_glass_pane", "minecraft:light_gray_stained_glass_pane", "minecraft:lime_stained_glass_pane", "minecraft:magenta_stained_glass_pane", "minecraft:orange_stained_glass_pane", "minecraft:pink_stained_glass_pane", "minecraft:purple_stained_glass_pane", "minecraft:red_stained_glass_pane", "minecraft:white_stained_glass_pane", "minecraft:yellow_stained_glass_pane");
        r.tag(ResourceTypes.INFUSION, "mek:gold", "mek:gold");
        r.itemTag("mek:gravels", "minecraft:gravel");
        r.itemTag("mek:gunpowders", "minecraft:gunpowder");
        r.tag(ResourceTypes.FLUID, "mek:heavy_water", "mek:flowing_heavy_water", "mek:heavy_water");
        r.tag(ResourceTypes.FLUID, "mek:hydrofluoric_acid", "mek:flowing_hydrofluoric_acid", "mek:hydrofluoric_acid");
        r.tag(ResourceTypes.FLUID, "mek:hydrogen", "mek:flowing_hydrogen", "mek:hydrogen");
        r.tag(ResourceTypes.FLUID, "mek:hydrogen_chloride", "mek:flowing_hydrogen_chloride", "mek:hydrogen_chloride");
        r.itemTag("mek:ingots/bronze", "mek:ingot_bronze");
        r.itemTag("mek:ingots/copper", "minecraft:copper_ingot");
        r.itemTag("mek:ingots/gold", "minecraft:gold_ingot");
        r.itemTag("mek:ingots/iron", "minecraft:iron_ingot");
        r.itemTag("mek:ingots/lead", "mek:ingot_lead");
        r.itemTag("mek:ingots/netherite", "minecraft:netherite_ingot");
        r.itemTag("mek:ingots/osmium", "mek:ingot_osmium");
        r.itemTag("mek:ingots/refined_glowstone", "mek:ingot_refined_glowstone");
        r.itemTag("mek:ingots/refined_obsidian", "mek:ingot_refined_obsidian");
        r.itemTag("mek:ingots/steel", "mek:ingot_steel");
        r.itemTag("mek:ingots/tin", "mek:ingot_tin");
        r.itemTag("mek:ingots/uranium", "mek:ingot_uranium");
        r.itemTag("mek:jungle_logs", "minecraft:jungle_log", "minecraft:jungle_wood", "minecraft:stripped_jungle_log", "minecraft:stripped_jungle_wood");
        r.itemTag("mek:leaves", "minecraft:acacia_leaves", "minecraft:azalea_leaves", "minecraft:birch_leaves", "minecraft:cherry_leaves", "minecraft:dark_oak_leaves", "minecraft:flowering_azalea_leaves", "minecraft:jungle_leaves", "minecraft:mangrove_leaves", "minecraft:oak_leaves", "minecraft:orange_poplar_leaves", "minecraft:pale_oak_leaves", "minecraft:red_poplar_leaves", "minecraft:spruce_leaves", "minecraft:yellow_poplar_leaves");
        r.tag(ResourceTypes.FLUID, "mek:lithium", "mek:flowing_lithium", "mek:lithium");
        r.itemTag("mek:logs", "minecraft:acacia_log", "minecraft:acacia_wood", "minecraft:birch_log", "minecraft:birch_wood", "minecraft:cherry_log", "minecraft:cherry_wood", "minecraft:crimson_hyphae", "minecraft:crimson_stem", "minecraft:dark_oak_log", "minecraft:dark_oak_wood", "minecraft:jungle_log", "minecraft:jungle_wood", "minecraft:mangrove_log", "minecraft:mangrove_wood", "minecraft:oak_log", "minecraft:oak_wood", "minecraft:pale_oak_log", "minecraft:pale_oak_wood", "minecraft:poplar_log", "minecraft:poplar_wood", "minecraft:spruce_log", "minecraft:spruce_wood", "minecraft:stripped_acacia_log", "minecraft:stripped_acacia_wood", "minecraft:stripped_birch_log", "minecraft:stripped_birch_wood", "minecraft:stripped_cherry_log", "minecraft:stripped_cherry_wood", "minecraft:stripped_crimson_hyphae", "minecraft:stripped_crimson_stem", "minecraft:stripped_dark_oak_log", "minecraft:stripped_dark_oak_wood", "minecraft:stripped_jungle_log", "minecraft:stripped_jungle_wood", "minecraft:stripped_mangrove_log", "minecraft:stripped_mangrove_wood", "minecraft:stripped_oak_log", "minecraft:stripped_oak_wood", "minecraft:stripped_pale_oak_log", "minecraft:stripped_pale_oak_wood", "minecraft:stripped_poplar_log", "minecraft:stripped_poplar_wood", "minecraft:stripped_spruce_log", "minecraft:stripped_spruce_wood", "minecraft:stripped_warped_hyphae", "minecraft:stripped_warped_stem", "minecraft:warped_hyphae", "minecraft:warped_stem");
        r.itemTag("mek:logs_that_burn", "minecraft:acacia_log", "minecraft:acacia_wood", "minecraft:birch_log", "minecraft:birch_wood", "minecraft:cherry_log", "minecraft:cherry_wood", "minecraft:dark_oak_log", "minecraft:dark_oak_wood", "minecraft:jungle_log", "minecraft:jungle_wood", "minecraft:mangrove_log", "minecraft:mangrove_wood", "minecraft:oak_log", "minecraft:oak_wood", "minecraft:pale_oak_log", "minecraft:pale_oak_wood", "minecraft:poplar_log", "minecraft:poplar_wood", "minecraft:spruce_log", "minecraft:spruce_wood", "minecraft:stripped_acacia_log", "minecraft:stripped_acacia_wood", "minecraft:stripped_birch_log", "minecraft:stripped_birch_wood", "minecraft:stripped_cherry_log", "minecraft:stripped_cherry_wood", "minecraft:stripped_dark_oak_log", "minecraft:stripped_dark_oak_wood", "minecraft:stripped_jungle_log", "minecraft:stripped_jungle_wood", "minecraft:stripped_mangrove_log", "minecraft:stripped_mangrove_wood", "minecraft:stripped_oak_log", "minecraft:stripped_oak_wood", "minecraft:stripped_pale_oak_log", "minecraft:stripped_pale_oak_wood", "minecraft:stripped_poplar_log", "minecraft:stripped_poplar_wood", "minecraft:stripped_spruce_log", "minecraft:stripped_spruce_wood");
        r.itemTag("mek:mangrove_logs", "minecraft:mangrove_log", "minecraft:mangrove_wood", "minecraft:stripped_mangrove_log", "minecraft:stripped_mangrove_wood");
        r.itemTag("mek:moss_blocks", "minecraft:moss_block", "minecraft:pale_moss_block");
        r.itemTag("mek:mushrooms", "minecraft:brown_mushroom", "minecraft:red_mushroom", "minecraft:shelf_mushroom");
        r.itemTag("mek:netherracks", "minecraft:netherrack");
        r.itemTag("mek:nuggets/bronze", "mek:nugget_bronze");
        r.itemTag("mek:nuggets/lead", "mek:nugget_lead");
        r.itemTag("mek:nuggets/osmium", "mek:nugget_osmium");
        r.itemTag("mek:nuggets/refined_glowstone", "mek:nugget_refined_glowstone");
        r.itemTag("mek:nuggets/refined_obsidian", "mek:nugget_refined_obsidian");
        r.itemTag("mek:nuggets/steel", "mek:nugget_steel");
        r.itemTag("mek:nuggets/tin", "mek:nugget_tin");
        r.itemTag("mek:nuggets/uranium", "mek:nugget_uranium");
        r.itemTag("mek:oak_logs", "minecraft:oak_log", "minecraft:oak_wood", "minecraft:stripped_oak_log", "minecraft:stripped_oak_wood");
        r.itemTag("mek:obsidians/normal", "minecraft:obsidian");
        r.itemTag("mek:ores/coal", "minecraft:coal_ore", "minecraft:deepslate_coal_ore");
        r.itemTag("mek:ores/copper", "minecraft:copper_ore", "minecraft:deepslate_copper_ore");
        r.itemTag("mek:ores/diamond", "minecraft:deepslate_diamond_ore", "minecraft:diamond_ore");
        r.itemTag("mek:ores/emerald", "minecraft:deepslate_emerald_ore", "minecraft:emerald_ore");
        r.itemTag("mek:ores/fluorite", "mek:deepslate_fluorite_ore", "mek:fluorite_ore");
        r.itemTag("mek:ores/gold", "minecraft:deepslate_gold_ore", "minecraft:gold_ore", "minecraft:nether_gold_ore");
        r.itemTag("mek:ores/iron", "minecraft:deepslate_iron_ore", "minecraft:iron_ore");
        r.itemTag("mek:ores/lapis", "minecraft:deepslate_lapis_ore", "minecraft:lapis_ore");
        r.itemTag("mek:ores/lead", "mek:deepslate_lead_ore", "mek:lead_ore");
        r.itemTag("mek:ores/netherite_scrap", "minecraft:ancient_debris");
        r.itemTag("mek:ores/osmium", "mek:deepslate_osmium_ore", "mek:osmium_ore");
        r.itemTag("mek:ores/quartz", "minecraft:nether_quartz_ore");
        r.itemTag("mek:ores/redstone", "minecraft:deepslate_redstone_ore", "minecraft:redstone_ore");
        r.itemTag("mek:ores/tin", "mek:deepslate_tin_ore", "mek:tin_ore");
        r.itemTag("mek:ores/uranium", "mek:deepslate_uranium_ore", "mek:uranium_ore");
        r.tag(ResourceTypes.FLUID, "mek:oxygen", "mek:flowing_oxygen", "mek:oxygen");
        r.itemTag("mek:pale_oak_logs", "minecraft:pale_oak_log", "minecraft:pale_oak_wood", "minecraft:stripped_pale_oak_log", "minecraft:stripped_pale_oak_wood");
        r.itemTag("mek:pellets/antimatter", "mek:pellet_antimatter");
        r.itemTag("mek:pellets/plutonium", "mek:pellet_plutonium");
        r.itemTag("mek:pellets/polonium", "mek:pellet_polonium");
        r.itemTag("mek:personal_storage", "mek:personal_barrel", "mek:personal_chest");
        r.itemTag("mek:planks", "minecraft:acacia_planks", "minecraft:bamboo_planks", "minecraft:birch_planks", "minecraft:cherry_planks", "minecraft:crimson_planks", "minecraft:dark_oak_planks", "minecraft:jungle_planks", "minecraft:mangrove_planks", "minecraft:oak_planks", "minecraft:pale_oak_planks", "minecraft:poplar_planks", "minecraft:spruce_planks", "minecraft:warped_planks");
        r.itemTag("mek:player_workstations/furnaces", "minecraft:blast_furnace", "minecraft:furnace", "minecraft:smoker");
        r.itemTag("mek:poplar_logs", "minecraft:poplar_log", "minecraft:poplar_wood", "minecraft:stripped_poplar_log", "minecraft:stripped_poplar_wood");
        r.itemTag("mek:pumpkins/carved", "minecraft:carved_pumpkin");
        r.itemTag("mek:pumpkins/normal", "minecraft:pumpkin");
        r.itemTag("mek:raw_materials/copper", "minecraft:raw_copper");
        r.itemTag("mek:raw_materials/gold", "minecraft:raw_gold");
        r.itemTag("mek:raw_materials/iron", "minecraft:raw_iron");
        r.itemTag("mek:raw_materials/lead", "mek:raw_lead");
        r.itemTag("mek:raw_materials/osmium", "mek:raw_osmium");
        r.itemTag("mek:raw_materials/tin", "mek:raw_tin");
        r.itemTag("mek:raw_materials/uranium", "mek:raw_uranium");
        r.tag(ResourceTypes.INFUSION, "mek:redstone", "mek:redstone");
        r.tag(ResourceTypes.INFUSION, "mek:refined_obsidian", "mek:refined_obsidian");
        r.itemTag("mek:rods/blaze", "minecraft:blaze_rod");
        r.itemTag("mek:rods/breeze", "minecraft:breeze_rod");
        r.itemTag("mek:rods/wooden", "minecraft:stick");
        r.itemTag("mek:sands", "minecraft:red_sand", "minecraft:sand", "minecraft:suspicious_sand");
        r.itemTag("mek:sandstone/red_blocks", "minecraft:red_sandstone");
        r.itemTag("mek:sandstone/uncolored_blocks", "minecraft:sandstone");
        r.itemTag("mek:saplings", "minecraft:acacia_sapling", "minecraft:azalea", "minecraft:birch_sapling", "minecraft:cherry_sapling", "minecraft:dark_oak_sapling", "minecraft:flowering_azalea", "minecraft:jungle_sapling", "minecraft:mangrove_propagule", "minecraft:oak_sapling", "minecraft:pale_oak_sapling", "minecraft:poplar_sapling", "minecraft:spruce_sapling");
        r.itemTag("mek:seeds", "minecraft:beetroot_seeds", "minecraft:melon_seeds", "minecraft:pumpkin_seeds", "minecraft:wheat_seeds");
        r.itemTag("mek:shards/copper", "mek:shard_copper");
        r.itemTag("mek:shards/gold", "mek:shard_gold");
        r.itemTag("mek:shards/iron", "mek:shard_iron");
        r.itemTag("mek:shards/lead", "mek:shard_lead");
        r.itemTag("mek:shards/osmium", "mek:shard_osmium");
        r.itemTag("mek:shards/tin", "mek:shard_tin");
        r.itemTag("mek:shards/uranium", "mek:shard_uranium");
        r.itemTag("mek:signs", "minecraft:acacia_sign", "minecraft:bamboo_sign", "minecraft:birch_sign", "minecraft:cherry_sign", "minecraft:crimson_sign", "minecraft:dark_oak_sign", "minecraft:jungle_sign", "minecraft:mangrove_sign", "minecraft:oak_sign", "minecraft:pale_oak_sign", "minecraft:poplar_sign", "minecraft:spruce_sign", "minecraft:warped_sign");
        r.itemTag("mek:smelts_to_glass", "minecraft:red_sand", "minecraft:sand");
        r.tag(ResourceTypes.FLUID, "mek:sodium", "mek:flowing_sodium", "mek:sodium");
        r.itemTag("mek:spruce_logs", "minecraft:spruce_log", "minecraft:spruce_wood", "minecraft:stripped_spruce_log", "minecraft:stripped_spruce_wood");
        r.tag(ResourceTypes.FLUID, "mek:steam", "mek:flowing_steam", "mek:steam");
        r.itemTag("mek:stone_crafting_materials", "minecraft:blackstone", "minecraft:cobbled_deepslate", "minecraft:cobblestone");
        r.itemTag("mek:storage_blocks/bone_meal", "minecraft:bone_block");
        r.itemTag("mek:storage_blocks/charcoal", "mek:block_charcoal");
        r.itemTag("mek:storage_blocks/coal", "minecraft:coal_block");
        r.itemTag("mek:storage_blocks/dried_kelp", "minecraft:dried_kelp_block");
        r.itemTag("mek:storage_blocks/fluorite", "mek:block_fluorite");
        r.itemTag("mek:storage_blocks/raw_copper", "minecraft:raw_copper_block");
        r.itemTag("mek:storage_blocks/raw_gold", "minecraft:raw_gold_block");
        r.itemTag("mek:storage_blocks/raw_iron", "minecraft:raw_iron_block");
        r.itemTag("mek:storage_blocks/raw_lead", "mek:block_raw_lead");
        r.itemTag("mek:storage_blocks/raw_osmium", "mek:block_raw_osmium");
        r.itemTag("mek:storage_blocks/raw_tin", "mek:block_raw_tin");
        r.itemTag("mek:storage_blocks/raw_uranium", "mek:block_raw_uranium");
        r.itemTag("mek:storage_blocks/redstone", "minecraft:redstone_block");
        r.itemTag("mek:storage_blocks/wheat", "minecraft:hay_block");
        r.tag(ResourceTypes.FLUID, "mek:sulfur_dioxide", "mek:flowing_sulfur_dioxide", "mek:sulfur_dioxide");
        r.tag(ResourceTypes.FLUID, "mek:sulfur_trioxide", "mek:flowing_sulfur_trioxide", "mek:sulfur_trioxide");
        r.tag(ResourceTypes.FLUID, "mek:sulfuric_acid", "mek:flowing_sulfuric_acid", "mek:sulfuric_acid");
        r.tag(ResourceTypes.FLUID, "mek:superheated_sodium", "mek:flowing_superheated_sodium", "mek:superheated_sodium");
        r.tag(ResourceTypes.INFUSION, "mek:tin", "mek:tin");
        r.tag(ResourceTypes.CHEMICAL, "mek:tritium", "mek:flowing_tritium", "mek:tritium");
        r.tag(ResourceTypes.FLUID, "mek:tritium", "mek:flowing_tritium", "mek:tritium");
        r.tag(ResourceTypes.FLUID, "mek:uranium_hexafluoride", "mek:flowing_uranium_hexafluoride", "mek:uranium_hexafluoride");
        r.tag(ResourceTypes.FLUID, "mek:uranium_oxide", "mek:flowing_uranium_oxide", "mek:uranium_oxide");
        r.itemTag("mek:warped_stems", "minecraft:stripped_warped_hyphae", "minecraft:stripped_warped_stem", "minecraft:warped_hyphae", "minecraft:warped_stem");
        r.tag(ResourceTypes.FLUID, "mek:water", "minecraft:water");
        r.tag(ResourceTypes.CHEMICAL, "mek:water_vapor", "mek:steam", "mek:water_vapor");
        r.itemTag("mek:wooden_buttons", "minecraft:acacia_button", "minecraft:bamboo_button", "minecraft:birch_button", "minecraft:cherry_button", "minecraft:crimson_button", "minecraft:dark_oak_button", "minecraft:jungle_button", "minecraft:mangrove_button", "minecraft:oak_button", "minecraft:pale_oak_button", "minecraft:poplar_button", "minecraft:spruce_button", "minecraft:warped_button");
        r.itemTag("mek:wooden_slabs", "minecraft:acacia_slab", "minecraft:bamboo_slab", "minecraft:birch_slab", "minecraft:cherry_slab", "minecraft:crimson_slab", "minecraft:dark_oak_slab", "minecraft:jungle_slab", "minecraft:mangrove_slab", "minecraft:oak_slab", "minecraft:pale_oak_slab", "minecraft:poplar_slab", "minecraft:spruce_slab", "minecraft:warped_slab");
        r.itemTag("mek:wooden_stairs", "minecraft:acacia_stairs", "minecraft:bamboo_stairs", "minecraft:birch_stairs", "minecraft:cherry_stairs", "minecraft:crimson_stairs", "minecraft:dark_oak_stairs", "minecraft:jungle_stairs", "minecraft:mangrove_stairs", "minecraft:oak_stairs", "minecraft:pale_oak_stairs", "minecraft:poplar_stairs", "minecraft:spruce_stairs", "minecraft:warped_stairs");
        r.itemTag("mek:wool", "minecraft:black_wool", "minecraft:blue_wool", "minecraft:brown_wool", "minecraft:cyan_wool", "minecraft:gray_wool", "minecraft:green_wool", "minecraft:light_blue_wool", "minecraft:light_gray_wool", "minecraft:lime_wool", "minecraft:magenta_wool", "minecraft:orange_wool", "minecraft:pink_wool", "minecraft:purple_wool", "minecraft:red_wool", "minecraft:white_wool", "minecraft:yellow_wool");
        r.itemTag("mek:wool_slabs", "minecraft:black_wool_slab", "minecraft:blue_wool_slab", "minecraft:brown_wool_slab", "minecraft:cyan_wool_slab", "minecraft:gray_wool_slab", "minecraft:green_wool_slab", "minecraft:light_blue_wool_slab", "minecraft:light_gray_wool_slab", "minecraft:lime_wool_slab", "minecraft:magenta_wool_slab", "minecraft:orange_wool_slab", "minecraft:pink_wool_slab", "minecraft:purple_wool_slab", "minecraft:red_wool_slab", "minecraft:white_wool_slab", "minecraft:yellow_wool_slab");
        r.itemTag("mek:wool_stairs", "minecraft:black_wool_stairs", "minecraft:blue_wool_stairs", "minecraft:brown_wool_stairs", "minecraft:cyan_wool_stairs", "minecraft:gray_wool_stairs", "minecraft:green_wool_stairs", "minecraft:light_blue_wool_stairs", "minecraft:light_gray_wool_stairs", "minecraft:lime_wool_stairs", "minecraft:magenta_wool_stairs", "minecraft:orange_wool_stairs", "minecraft:pink_wool_stairs", "minecraft:purple_wool_stairs", "minecraft:red_wool_stairs", "minecraft:white_wool_stairs", "minecraft:yellow_wool_stairs");
    }

    /** 物品中文名（机器界面与自检用）。*/
    public static void names(Registrars r) {
        r.itemName("mek:advanced_control_circuit", "高级控制电路");
        r.itemName("mek:advanced_control_circuit", "高级控制电路");
        r.itemName("mek:alloy_atomic", "原子合金");
        r.itemName("mek:alloy_atomic", "原子合金");
        r.itemName("mek:alloy_infused", "灌注合金");
        r.itemName("mek:alloy_reinforced", "强化合金");
        r.itemName("mek:alloy_reinforced", "强化合金");
        r.itemName("mek:basic_control_circuit", "基础控制电路");
        r.itemName("mek:bio_fuel", "生物燃料");
        r.itemName("mek:block_bio_fuel", "生物燃料块");
        r.itemName("mek:block_bronze", "青铜块");
        r.itemName("mek:block_charcoal", "木炭块");
        r.itemName("mek:block_fluorite", "氟石块");
        r.itemName("mek:block_lead", "铅块");
        r.itemName("mek:block_osmium", "锇块");
        r.itemName("mek:block_raw_lead", "粗铅块");
        r.itemName("mek:block_raw_osmium", "粗锇块");
        r.itemName("mek:block_raw_tin", "粗锡块");
        r.itemName("mek:block_raw_uranium", "粗铀块");
        r.itemName("mek:block_refined_glowstone", "强化荧石");
        r.itemName("mek:block_refined_obsidian", "强化黑曜石");
        r.itemName("mek:block_salt", "盐块");
        r.itemName("mek:block_steel", "钢块");
        r.itemName("mek:block_tin", "锡块");
        r.itemName("mek:block_uranium", "铀块");
        r.itemName("mek:configuration_card", "配置卡");
        r.itemName("mek:configurator", "配置器");
        r.itemName("mek:crafting_formula", "合成配方");
        r.itemName("mek:deepslate_fluorite_ore", "深层氟石矿石");
        r.itemName("mek:deepslate_lead_ore", "深层铅矿石");
        r.itemName("mek:deepslate_osmium_ore", "深层锇矿石");
        r.itemName("mek:deepslate_tin_ore", "深层锡矿石");
        r.itemName("mek:deepslate_uranium_ore", "深层铀矿石");
        r.itemName("mek:dictionary", "辞典");
        r.itemName("mek:dosimeter", "放射量测定器");
        r.itemName("mek:dust_bronze", "青铜粉");
        r.itemName("mek:dust_charcoal", "木炭粉");
        r.itemName("mek:dust_coal", "煤粉");
        r.itemName("mek:dust_copper", "铜粉");
        r.itemName("mek:dust_diamond", "钻石粉");
        r.itemName("mek:dust_emerald", "绿宝石粉");
        r.itemName("mek:dust_fluorite", "氟石粉");
        r.itemName("mek:dust_gold", "金粉");
        r.itemName("mek:dust_iron", "铁粉");
        r.itemName("mek:dust_lapis_lazuli", "青金石粉");
        r.itemName("mek:dust_lead", "铅粉");
        r.itemName("mek:dust_obsidian", "黑曜石粉");
        r.itemName("mek:dust_osmium", "锇粉");
        r.itemName("mek:dust_quartz", "石英粉");
        r.itemName("mek:dust_refined_obsidian", "强化黑曜石粉");
        r.itemName("mek:dust_steel", "钢粉");
        r.itemName("mek:dust_tin", "锡粉");
        r.itemName("mek:dust_uranium", "铀粉");
        r.itemName("mek:electrolytic_core", "电解核心");
        r.itemName("mek:elite_control_circuit", "精英控制电路");
        r.itemName("mek:elite_control_circuit", "精英控制电路");
        r.itemName("mek:energy_tablet", "能量板");
        r.itemName("mek:enriched_carbon", "富集碳");
        r.itemName("mek:enriched_diamond", "富集钻石");
        r.itemName("mek:enriched_diamond", "富集钻石");
        r.itemName("mek:enriched_gold", "富集金");
        r.itemName("mek:enriched_gold", "富集金");
        r.itemName("mek:enriched_iron", "富集铁");
        r.itemName("mek:enriched_redstone", "富集红石");
        r.itemName("mek:enriched_redstone", "富集红石");
        r.itemName("mek:enriched_tin", "富集锡");
        r.itemName("mek:enriched_tin", "富集锡");
        r.itemName("mek:fluorite_gem", "氟石");
        r.itemName("mek:fluorite_ore", "氟石矿石");
        r.itemName("mek:gauge_dropper", "滴管");
        r.itemName("mek:geiger_counter", "盖革计数器");
        r.itemName("mek:ingot_bronze", "青铜锭");
        r.itemName("mek:ingot_lead", "铅锭");
        r.itemName("mek:ingot_osmium", "锇锭");
        r.itemName("mek:ingot_refined_glowstone", "强化荧石锭");
        r.itemName("mek:ingot_refined_obsidian", "强化黑曜石锭");
        r.itemName("mek:ingot_steel", "钢锭");
        r.itemName("mek:ingot_tin", "锡锭");
        r.itemName("mek:ingot_uranium", "铀锭");
        r.itemName("mek:lead_ore", "铅矿石");
        r.itemName("mek:network_reader", "网络读取器");
        r.itemName("mek:osmium_ore", "锇矿石");
        r.itemName("mek:salt", "盐");
        r.itemName("mek:sawdust", "锯末");
        r.itemName("mek:solar_panel", "太阳能板");
        r.itemName("mek:steel_casing", "钢质机壳");
        r.itemName("mek:tin_ore", "锡矿石");
        r.itemName("mek:turbine_blade", "涡轮叶片");
        r.itemName("mek:ultimate_control_circuit", "终极控制电路");
        r.itemName("mek:ultimate_control_circuit", "终极控制电路");
        r.itemName("mek:upgrade_installer", "升级安装器");
        r.itemName("mek:uranium_ore", "铀矿石");
        r.itemName("mek:portable_qio_dashboard", "便携式QIO仪表板");
        r.itemName("mek:qio_drive_base", "QIO 驱动器");
        r.itemName("mek:qio_drive_hyper_dense", "超密集 QIO 驱动器");
        r.itemName("mek:qio_drive_hyper_dense", "超密集 QIO 驱动器");
        r.itemName("mek:qio_drive_supermassive", "超质量 QIO 驱动器");
        r.itemName("mek:qio_drive_supermassive", "超质量 QIO 驱动器");
        r.itemName("mek:qio_drive_time_dilating", "时间膨胀 QIO 驱动器");
        r.itemName("mek:qio_drive_time_dilating", "时间膨胀 QIO 驱动器");
        r.itemName("mek:robit", "机器人");
        r.itemName("mek:teleportation_core", "传送核心");
        r.itemName("mek:hohlraum", "黑体辐射腔");
        r.itemName("mek:clump_copper", "铜碎块");
        r.itemName("mek:clump_gold", "金碎块");
        r.itemName("mek:clump_iron", "铁碎块");
        r.itemName("mek:clump_lead", "铅碎块");
        r.itemName("mek:clump_osmium", "锇碎块");
        r.itemName("mek:clump_tin", "锡碎块");
        r.itemName("mek:clump_uranium", "铀碎块");
        r.itemName("mek:crystal_copper", "铜晶体");
        r.itemName("mek:crystal_gold", "金晶体");
        r.itemName("mek:crystal_iron", "铁晶体");
        r.itemName("mek:crystal_lead", "铅晶体");
        r.itemName("mek:crystal_osmium", "锇晶体");
        r.itemName("mek:crystal_tin", "锡晶体");
        r.itemName("mek:crystal_uranium", "铀晶体");
        r.itemName("mek:dirty_dust_copper", "污浊铜粉");
        r.itemName("mek:dirty_dust_gold", "污浊金粉");
        r.itemName("mek:dirty_dust_iron", "污浊铁粉");
        r.itemName("mek:dirty_dust_lead", "污浊铅粉");
        r.itemName("mek:dirty_dust_osmium", "污浊锇粉");
        r.itemName("mek:dirty_dust_tin", "污浊锡粉");
        r.itemName("mek:dirty_dust_uranium", "污浊铀粉");
        r.itemName("mek:dirty_netherite_scrap", "污浊下界合金碎片");
        r.itemName("mek:dust_lithium", "锂粉");
        r.itemName("mek:dust_netherite", "下界合金粉");
        r.itemName("mek:dust_sulfur", "硫粉");
        r.itemName("mek:dye_base", "染料基础");
        r.itemName("mek:enriched_refined_obsidian", "富集强化黑曜石");
        r.itemName("mek:enriched_refined_obsidian", "富集强化黑曜石");
        r.itemName("mek:hdpe_pellet", "高密度聚乙烯丸");
        r.itemName("mek:hdpe_rod", "高密度聚乙烯棒");
        r.itemName("mek:hdpe_rod", "高密度聚乙烯棒");
        r.itemName("mek:hdpe_sheet", "高密度聚乙烯片");
        r.itemName("mek:hdpe_sheet", "高密度聚乙烯片");
        r.itemName("mek:hdpe_stick", "高密度聚乙烯棍");
        r.itemName("mek:hdpe_stick", "高密度聚乙烯棍");
        r.itemName("mek:nugget_bronze", "青铜粒");
        r.itemName("mek:nugget_lead", "铅粒");
        r.itemName("mek:nugget_osmium", "锇粒");
        r.itemName("mek:nugget_refined_glowstone", "强化荧石粒");
        r.itemName("mek:nugget_refined_obsidian", "强化黑曜石粒");
        r.itemName("mek:nugget_steel", "钢粒");
        r.itemName("mek:nugget_tin", "锡粒");
        r.itemName("mek:nugget_uranium", "铀粒");
        r.itemName("mek:pellet_antimatter", "反物质球");
        r.itemName("mek:pellet_plutonium", "钚球");
        r.itemName("mek:pellet_polonium", "钋球");
        r.itemName("mek:raw_lead", "粗铅");
        r.itemName("mek:raw_osmium", "粗锇");
        r.itemName("mek:raw_tin", "粗锡");
        r.itemName("mek:raw_uranium", "粗铀");
        r.itemName("mek:reprocessed_fissile_fragment", "再处理裂变碎片");
        r.itemName("mek:shard_copper", "铜碎片");
        r.itemName("mek:shard_gold", "金碎片");
        r.itemName("mek:shard_iron", "铁碎片");
        r.itemName("mek:shard_lead", "铅碎片");
        r.itemName("mek:shard_osmium", "锇碎片");
        r.itemName("mek:shard_tin", "锡碎片");
        r.itemName("mek:shard_uranium", "铀碎片");
        r.itemName("mek:substrate", "基片");
        r.itemName("mek:yellow_cake_uranium", "铀黄饼");
    }

    /** 辅料来源（文档 07）：物品 → 辅料点数，来自上游 chemical_conversion。*/
    public record InfusionSource(String item, String infusion, long points) {}
    public static final List<InfusionSource> INFUSION_SOURCES = List.of(
            new InfusionSource("mek:bio_fuel", "mek:bio", 5L),
            new InfusionSource("mek:block_bio_fuel", "mek:bio", 45L),
            new InfusionSource("mek:block_charcoal", "mek:carbon", 180L),
            new InfusionSource("mek:dust_charcoal", "mek:carbon", 20L),
            new InfusionSource("mek:dust_coal", "mek:carbon", 10L),
            new InfusionSource("mek:dust_diamond", "mek:diamond", 10L),
            new InfusionSource("mek:dust_gold", "mek:gold", 10L),
            new InfusionSource("mek:dust_refined_obsidian", "mek:refined_obsidian", 10L),
            new InfusionSource("mek:dust_tin", "mek:tin", 10L),
            new InfusionSource("mek:enriched_carbon", "mek:carbon", 80L),
            new InfusionSource("mek:enriched_diamond", "mek:diamond", 80L),
            new InfusionSource("mek:enriched_gold", "mek:gold", 80L),
            new InfusionSource("mek:enriched_redstone", "mek:redstone", 80L),
            new InfusionSource("mek:enriched_refined_obsidian", "mek:refined_obsidian", 80L),
            new InfusionSource("mek:enriched_tin", "mek:tin", 80L),
            new InfusionSource("minecraft:brown_mushroom", "mek:fungi", 10L),
            new InfusionSource("minecraft:charcoal", "mek:carbon", 20L),
            new InfusionSource("minecraft:coal", "mek:carbon", 10L),
            new InfusionSource("minecraft:coal_block", "mek:carbon", 90L),
            new InfusionSource("minecraft:crimson_fungus", "mek:fungi", 10L),
            new InfusionSource("minecraft:red_mushroom", "mek:fungi", 10L),
            new InfusionSource("minecraft:redstone", "mek:redstone", 10L),
            new InfusionSource("minecraft:redstone_block", "mek:redstone", 90L),
            new InfusionSource("minecraft:shelf_mushroom", "mek:fungi", 10L),
            new InfusionSource("minecraft:warped_fungus", "mek:fungi", 10L));

    /** 气液转换对（文档 07）：化学品 ↔ 流体，1:1；来自上游 recipe/rotary。*/
    public record RotaryPair(String chemical, String fluid) {}
    public static final List<RotaryPair> ROTARY_PAIRS = List.of(
            new RotaryPair("mek:brine", "mek:brine"),
            new RotaryPair("mek:chlorine", "mek:chlorine"),
            new RotaryPair("mek:deuterium", "mek:deuterium"),
            new RotaryPair("mek:ethene", "mek:ethene"),
            new RotaryPair("mek:fusion_fuel", "mek:fusion_fuel"),
            new RotaryPair("mek:hydrofluoric_acid", "mek:hydrofluoric_acid"),
            new RotaryPair("mek:hydrogen", "mek:hydrogen"),
            new RotaryPair("mek:hydrogen_chloride", "mek:hydrogen_chloride"),
            new RotaryPair("mek:lithium", "mek:lithium"),
            new RotaryPair("mek:oxygen", "mek:oxygen"),
            new RotaryPair("mek:sodium", "mek:sodium"),
            new RotaryPair("mek:steam", "mek:steam"),
            new RotaryPair("mek:sulfur_dioxide", "mek:sulfur_dioxide"),
            new RotaryPair("mek:sulfur_trioxide", "mek:sulfur_trioxide"),
            new RotaryPair("mek:sulfuric_acid", "mek:sulfuric_acid"),
            new RotaryPair("mek:superheated_sodium", "mek:superheated_sodium"),
            new RotaryPair("mek:tritium", "mek:tritium"),
            new RotaryPair("mek:uranium_hexafluoride", "mek:uranium_hexafluoride"),
            new RotaryPair("mek:uranium_oxide", "mek:uranium_oxide"),
            new RotaryPair("mek:water_vapor", "minecraft:water"));

    /** 方块 id → 蓝图 id（工厂方块带 FactoryTier）。*/
    public static final Map<String, String> BLOCK_TO_BLUEPRINT = Map.ofEntries(
            Map.entry("mek:advanced_bin", "mek:advanced_bin"),
            Map.entry("mek:advanced_chemical_tank", "mek:advanced_chemical_tank"),
            Map.entry("mek:advanced_energy_cube", "mek:advanced_energy_cube"),
            Map.entry("mek:advanced_fluid_tank", "mek:advanced_fluid_tank"),
            Map.entry("mek:advanced_solar_generator", "mek:advanced_solar_generator"),
            Map.entry("mek:antiprotonic_nucleosynthesizer", "mek:antiprotonic_nucleosynthesizer"),
            Map.entry("mek:basic_bin", "mek:basic_bin"),
            Map.entry("mek:basic_chemical_tank", "mek:basic_chemical_tank"),
            Map.entry("mek:basic_energy_cube", "mek:basic_energy_cube"),
            Map.entry("mek:basic_fluid_tank", "mek:basic_fluid_tank"),
            Map.entry("mek:bio_generator", "mek:bio_generator"),
            Map.entry("mek:chargepad", "mek:chargepad"),
            Map.entry("mek:chemical_crystallizer", "mek:chemical_crystallizer"),
            Map.entry("mek:chemical_dissolution_chamber", "mek:chemical_dissolution_chamber"),
            Map.entry("mek:chemical_infuser", "mek:chemical_infuser"),
            Map.entry("mek:chemical_injection_chamber", "mek:chemical_injection_chamber"),
            Map.entry("mek:chemical_injection_chamber_factory_basic", "mek:chemical_injection_chamber"),
            Map.entry("mek:chemical_injection_chamber_factory_advanced", "mek:chemical_injection_chamber"),
            Map.entry("mek:chemical_injection_chamber_factory_elite", "mek:chemical_injection_chamber"),
            Map.entry("mek:chemical_injection_chamber_factory_ultimate", "mek:chemical_injection_chamber"),
            Map.entry("mek:chemical_oxidizer", "mek:chemical_oxidizer"),
            Map.entry("mek:chemical_washer", "mek:chemical_washer"),
            Map.entry("mek:combiner", "mek:combiner"),
            Map.entry("mek:combiner_factory_basic", "mek:combiner"),
            Map.entry("mek:combiner_factory_advanced", "mek:combiner"),
            Map.entry("mek:combiner_factory_elite", "mek:combiner"),
            Map.entry("mek:combiner_factory_ultimate", "mek:combiner"),
            Map.entry("mek:crusher", "mek:crusher"),
            Map.entry("mek:crusher_factory_basic", "mek:crusher"),
            Map.entry("mek:crusher_factory_advanced", "mek:crusher"),
            Map.entry("mek:crusher_factory_elite", "mek:crusher"),
            Map.entry("mek:crusher_factory_ultimate", "mek:crusher"),
            Map.entry("mek:digital_miner", "mek:digital_miner"),
            Map.entry("mek:electric_pump", "mek:electric_pump"),
            Map.entry("mek:electrolytic_separator", "mek:electrolytic_separator"),
            Map.entry("mek:elite_bin", "mek:elite_bin"),
            Map.entry("mek:elite_chemical_tank", "mek:elite_chemical_tank"),
            Map.entry("mek:elite_energy_cube", "mek:elite_energy_cube"),
            Map.entry("mek:elite_fluid_tank", "mek:elite_fluid_tank"),
            Map.entry("mek:energized_smelter", "mek:energized_smelter"),
            Map.entry("mek:energized_smelter_factory_basic", "mek:energized_smelter"),
            Map.entry("mek:energized_smelter_factory_advanced", "mek:energized_smelter"),
            Map.entry("mek:energized_smelter_factory_elite", "mek:energized_smelter"),
            Map.entry("mek:energized_smelter_factory_ultimate", "mek:energized_smelter"),
            Map.entry("mek:enrichment_chamber", "mek:enrichment_chamber"),
            Map.entry("mek:enrichment_chamber_factory_basic", "mek:enrichment_chamber"),
            Map.entry("mek:enrichment_chamber_factory_advanced", "mek:enrichment_chamber"),
            Map.entry("mek:enrichment_chamber_factory_elite", "mek:enrichment_chamber"),
            Map.entry("mek:enrichment_chamber_factory_ultimate", "mek:enrichment_chamber"),
            Map.entry("mek:fuelwood_heater", "mek:fuelwood_heater"),
            Map.entry("mek:gas_burning_generator", "mek:gas_burning_generator"),
            Map.entry("mek:heat_generator", "mek:heat_generator"),
            Map.entry("mek:isotopic_centrifuge", "mek:isotopic_centrifuge"),
            Map.entry("mek:laser", "mek:laser"),
            Map.entry("mek:laser_amplifier", "mek:laser_amplifier"),
            Map.entry("mek:logistical_sorter", "mek:logistical_sorter"),
            Map.entry("mek:metallurgic_infuser", "mek:metallurgic_infuser"),
            Map.entry("mek:metallurgic_infuser_factory_basic", "mek:metallurgic_infuser"),
            Map.entry("mek:metallurgic_infuser_factory_advanced", "mek:metallurgic_infuser"),
            Map.entry("mek:metallurgic_infuser_factory_elite", "mek:metallurgic_infuser"),
            Map.entry("mek:metallurgic_infuser_factory_ultimate", "mek:metallurgic_infuser"),
            Map.entry("mek:osmium_compressor", "mek:osmium_compressor"),
            Map.entry("mek:osmium_compressor_factory_basic", "mek:osmium_compressor"),
            Map.entry("mek:osmium_compressor_factory_advanced", "mek:osmium_compressor"),
            Map.entry("mek:osmium_compressor_factory_elite", "mek:osmium_compressor"),
            Map.entry("mek:osmium_compressor_factory_ultimate", "mek:osmium_compressor"),
            Map.entry("mek:painting_machine", "mek:painting_machine"),
            Map.entry("mek:personal_barrel", "mek:personal_barrel"),
            Map.entry("mek:personal_chest", "mek:personal_chest"),
            Map.entry("mek:pigment_extractor", "mek:pigment_extractor"),
            Map.entry("mek:pigment_mixer", "mek:pigment_mixer"),
            Map.entry("mek:precision_sawmill", "mek:precision_sawmill"),
            Map.entry("mek:precision_sawmill_factory_basic", "mek:precision_sawmill"),
            Map.entry("mek:precision_sawmill_factory_advanced", "mek:precision_sawmill"),
            Map.entry("mek:precision_sawmill_factory_elite", "mek:precision_sawmill"),
            Map.entry("mek:precision_sawmill_factory_ultimate", "mek:precision_sawmill"),
            Map.entry("mek:pressurized_reaction_chamber", "mek:pressurized_reaction_chamber"),
            Map.entry("mek:purification_chamber", "mek:purification_chamber"),
            Map.entry("mek:purification_chamber_factory_basic", "mek:purification_chamber"),
            Map.entry("mek:purification_chamber_factory_advanced", "mek:purification_chamber"),
            Map.entry("mek:purification_chamber_factory_elite", "mek:purification_chamber"),
            Map.entry("mek:purification_chamber_factory_ultimate", "mek:purification_chamber"),
            Map.entry("mek:qio_dashboard", "mek:qio_dashboard"),
            Map.entry("mek:qio_drive_array", "mek:qio_drive_array"),
            Map.entry("mek:qio_exporter", "mek:qio_exporter"),
            Map.entry("mek:qio_importer", "mek:qio_importer"),
            Map.entry("mek:qio_redstone_adapter", "mek:qio_redstone_adapter"),
            Map.entry("mek:radioactive_waste_barrel", "mek:radioactive_waste_barrel"),
            Map.entry("mek:resistive_heater", "mek:resistive_heater"),
            Map.entry("mek:rotary_condensentrator", "mek:rotary_condensentrator"),
            Map.entry("mek:solar_generator", "mek:solar_generator"),
            Map.entry("mek:solar_neutron_activator", "mek:solar_neutron_activator"),
            Map.entry("mek:ultimate_bin", "mek:ultimate_bin"),
            Map.entry("mek:ultimate_chemical_tank", "mek:ultimate_chemical_tank"),
            Map.entry("mek:ultimate_energy_cube", "mek:ultimate_energy_cube"),
            Map.entry("mek:ultimate_fluid_tank", "mek:ultimate_fluid_tank"),
            Map.entry("mek:wind_generator", "mek:wind_generator"));

    /** 工厂方块的工厂等级；单机方块不在表里。*/
    public static final Map<String, String> BLOCK_TO_FACTORY_TIER = Map.ofEntries(
            Map.entry("mek:chemical_injection_chamber_factory_basic", "BASIC"),
            Map.entry("mek:chemical_injection_chamber_factory_advanced", "ADVANCED"),
            Map.entry("mek:chemical_injection_chamber_factory_elite", "ELITE"),
            Map.entry("mek:chemical_injection_chamber_factory_ultimate", "ULTIMATE"),
            Map.entry("mek:combiner_factory_basic", "BASIC"),
            Map.entry("mek:combiner_factory_advanced", "ADVANCED"),
            Map.entry("mek:combiner_factory_elite", "ELITE"),
            Map.entry("mek:combiner_factory_ultimate", "ULTIMATE"),
            Map.entry("mek:crusher_factory_basic", "BASIC"),
            Map.entry("mek:crusher_factory_advanced", "ADVANCED"),
            Map.entry("mek:crusher_factory_elite", "ELITE"),
            Map.entry("mek:crusher_factory_ultimate", "ULTIMATE"),
            Map.entry("mek:energized_smelter_factory_basic", "BASIC"),
            Map.entry("mek:energized_smelter_factory_advanced", "ADVANCED"),
            Map.entry("mek:energized_smelter_factory_elite", "ELITE"),
            Map.entry("mek:energized_smelter_factory_ultimate", "ULTIMATE"),
            Map.entry("mek:enrichment_chamber_factory_basic", "BASIC"),
            Map.entry("mek:enrichment_chamber_factory_advanced", "ADVANCED"),
            Map.entry("mek:enrichment_chamber_factory_elite", "ELITE"),
            Map.entry("mek:enrichment_chamber_factory_ultimate", "ULTIMATE"),
            Map.entry("mek:metallurgic_infuser_factory_basic", "BASIC"),
            Map.entry("mek:metallurgic_infuser_factory_advanced", "ADVANCED"),
            Map.entry("mek:metallurgic_infuser_factory_elite", "ELITE"),
            Map.entry("mek:metallurgic_infuser_factory_ultimate", "ULTIMATE"),
            Map.entry("mek:osmium_compressor_factory_basic", "BASIC"),
            Map.entry("mek:osmium_compressor_factory_advanced", "ADVANCED"),
            Map.entry("mek:osmium_compressor_factory_elite", "ELITE"),
            Map.entry("mek:osmium_compressor_factory_ultimate", "ULTIMATE"),
            Map.entry("mek:precision_sawmill_factory_basic", "BASIC"),
            Map.entry("mek:precision_sawmill_factory_advanced", "ADVANCED"),
            Map.entry("mek:precision_sawmill_factory_elite", "ELITE"),
            Map.entry("mek:precision_sawmill_factory_ultimate", "ULTIMATE"),
            Map.entry("mek:purification_chamber_factory_basic", "BASIC"),
            Map.entry("mek:purification_chamber_factory_advanced", "ADVANCED"),
            Map.entry("mek:purification_chamber_factory_elite", "ELITE"),
            Map.entry("mek:purification_chamber_factory_ultimate", "ULTIMATE"));

    /** 传输元件（文档 06）：方块 id ↔ 族 ↔ 级 ↔ 中文名。
     *  数值不在这里：管道与物品管道的分级数由 mek-systems.yml 的
     *  transmitter:/item_pipe: 段覆盖核心 V10Tables（默认值就是上游 10.8 的分级表）。*/
    public static final List<TransmitterDef> TRANSMITTERS = List.of(
            new TransmitterDef("mek:basic_universal_cable", "universal_cable", "BASIC", "基础通用线缆"),
            new TransmitterDef("mek:advanced_universal_cable", "universal_cable", "ADVANCED", "高级通用线缆"),
            new TransmitterDef("mek:elite_universal_cable", "universal_cable", "ELITE", "精英通用线缆"),
            new TransmitterDef("mek:ultimate_universal_cable", "universal_cable", "ULTIMATE", "终极通用线缆"),
            new TransmitterDef("mek:basic_mechanical_pipe", "mechanical_pipe", "BASIC", "基础机械管道"),
            new TransmitterDef("mek:advanced_mechanical_pipe", "mechanical_pipe", "ADVANCED", "高级机械管道"),
            new TransmitterDef("mek:elite_mechanical_pipe", "mechanical_pipe", "ELITE", "精英机械管道"),
            new TransmitterDef("mek:ultimate_mechanical_pipe", "mechanical_pipe", "ULTIMATE", "终极机械管道"),
            new TransmitterDef("mek:basic_pressurized_tube", "pressurized_tube", "BASIC", "基础加压管道"),
            new TransmitterDef("mek:advanced_pressurized_tube", "pressurized_tube", "ADVANCED", "高级加压管道"),
            new TransmitterDef("mek:elite_pressurized_tube", "pressurized_tube", "ELITE", "精英加压管道"),
            new TransmitterDef("mek:ultimate_pressurized_tube", "pressurized_tube", "ULTIMATE", "终极加压管道"),
            new TransmitterDef("mek:basic_logistical_transporter", "logistical_transporter", "BASIC", "基础物流管道"),
            new TransmitterDef("mek:advanced_logistical_transporter", "logistical_transporter", "ADVANCED", "高级物流管道"),
            new TransmitterDef("mek:elite_logistical_transporter", "logistical_transporter", "ELITE", "精英物流管道"),
            new TransmitterDef("mek:ultimate_logistical_transporter", "logistical_transporter", "ULTIMATE", "终极物流管道"),
            new TransmitterDef("mek:basic_thermodynamic_conductor", "thermodynamic_conductor", "BASIC", "基础热导线缆"),
            new TransmitterDef("mek:advanced_thermodynamic_conductor", "thermodynamic_conductor", "ADVANCED", "高级热导线缆"),
            new TransmitterDef("mek:elite_thermodynamic_conductor", "thermodynamic_conductor", "ELITE", "精英热导线缆"),
            new TransmitterDef("mek:ultimate_thermodynamic_conductor", "thermodynamic_conductor", "ULTIMATE", "终极热导线缆"));

    /** 机器在电网里的角色：source（发电机）/ store（纯储能）/ machine（用电）。*/
    public static final Map<String, String> MACHINE_ROLES = Map.ofEntries(
            Map.entry("mek:advanced_bin", "machine"),
            Map.entry("mek:advanced_chemical_tank", "machine"),
            Map.entry("mek:advanced_energy_cube", "store"),
            Map.entry("mek:advanced_fluid_tank", "machine"),
            Map.entry("mek:advanced_solar_generator", "source"),
            Map.entry("mek:antiprotonic_nucleosynthesizer", "machine"),
            Map.entry("mek:basic_bin", "machine"),
            Map.entry("mek:basic_chemical_tank", "machine"),
            Map.entry("mek:basic_energy_cube", "store"),
            Map.entry("mek:basic_fluid_tank", "machine"),
            Map.entry("mek:bio_generator", "source"),
            Map.entry("mek:chargepad", "machine"),
            Map.entry("mek:chemical_crystallizer", "machine"),
            Map.entry("mek:chemical_dissolution_chamber", "machine"),
            Map.entry("mek:chemical_infuser", "machine"),
            Map.entry("mek:chemical_injection_chamber", "machine"),
            Map.entry("mek:chemical_oxidizer", "machine"),
            Map.entry("mek:chemical_washer", "machine"),
            Map.entry("mek:combiner", "machine"),
            Map.entry("mek:crusher", "machine"),
            Map.entry("mek:digital_miner", "machine"),
            Map.entry("mek:electric_pump", "machine"),
            Map.entry("mek:electrolytic_separator", "machine"),
            Map.entry("mek:elite_bin", "machine"),
            Map.entry("mek:elite_chemical_tank", "machine"),
            Map.entry("mek:elite_energy_cube", "store"),
            Map.entry("mek:elite_fluid_tank", "machine"),
            Map.entry("mek:energized_smelter", "machine"),
            Map.entry("mek:enrichment_chamber", "machine"),
            Map.entry("mek:fuelwood_heater", "machine"),
            Map.entry("mek:gas_burning_generator", "source"),
            Map.entry("mek:heat_generator", "source"),
            Map.entry("mek:isotopic_centrifuge", "machine"),
            Map.entry("mek:laser", "machine"),
            Map.entry("mek:laser_amplifier", "machine"),
            Map.entry("mek:logistical_sorter", "machine"),
            Map.entry("mek:metallurgic_infuser", "machine"),
            Map.entry("mek:osmium_compressor", "machine"),
            Map.entry("mek:painting_machine", "machine"),
            Map.entry("mek:personal_barrel", "machine"),
            Map.entry("mek:personal_chest", "machine"),
            Map.entry("mek:pigment_extractor", "machine"),
            Map.entry("mek:pigment_mixer", "machine"),
            Map.entry("mek:precision_sawmill", "machine"),
            Map.entry("mek:pressurized_reaction_chamber", "machine"),
            Map.entry("mek:purification_chamber", "machine"),
            Map.entry("mek:qio_dashboard", "machine"),
            Map.entry("mek:qio_drive_array", "machine"),
            Map.entry("mek:qio_exporter", "machine"),
            Map.entry("mek:qio_importer", "machine"),
            Map.entry("mek:qio_redstone_adapter", "machine"),
            Map.entry("mek:radioactive_waste_barrel", "machine"),
            Map.entry("mek:resistive_heater", "machine"),
            Map.entry("mek:rotary_condensentrator", "machine"),
            Map.entry("mek:solar_generator", "source"),
            Map.entry("mek:solar_neutron_activator", "machine"),
            Map.entry("mek:ultimate_bin", "machine"),
            Map.entry("mek:ultimate_chemical_tank", "machine"),
            Map.entry("mek:ultimate_energy_cube", "store"),
            Map.entry("mek:ultimate_fluid_tank", "machine"),
            Map.entry("mek:wind_generator", "source"));

    /** 发电机碰撞盒（文档 11）：上游 BlockShapes.java 的 box 列表，单位 1/16 格。
     *  服务端由插件绑到 DelegatingBlock.shapeDelegate；客户端选中框仍是 leaves 载体。*/
    public record ShapeBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {}
    public static final Map<String, List<ShapeBox>> GENERATOR_SHAPES = Map.ofEntries(
            Map.entry("mek:heat_generator", List.of(new ShapeBox(0.0, 7.0, 6.0, 16.0, 16.0, 15.0), new ShapeBox(0.0, 0.0, 0.0, 16.0, 6.0, 16.0), new ShapeBox(3.0, 6.0, 5.0, 5.0, 15.0, 16.0), new ShapeBox(11.0, 6.0, 5.0, 13.0, 15.0, 16.0), new ShapeBox(0.0, 6.0, 2.0, 16.0, 16.0, 5.0), new ShapeBox(3.0, 6.0, 1.0, 5.0, 15.0, 2.0), new ShapeBox(11.0, 6.0, 1.0, 13.0, 15.0, 2.0), new ShapeBox(4.0, 6.0, 0.0, 12.0, 12.0, 2.0), new ShapeBox(0.0, 13.0, 0.0, 16.0, 14.0, 2.0), new ShapeBox(0.0, 15.0, 0.0, 16.0, 16.0, 2.0), new ShapeBox(0.0, 11.0, 0.0, 4.0, 12.0, 2.0), new ShapeBox(0.0, 9.0, 0.0, 4.0, 10.0, 2.0), new ShapeBox(0.0, 7.0, 0.0, 4.0, 8.0, 2.0), new ShapeBox(12.0, 11.0, 0.0, 16.0, 12.0, 2.0), new ShapeBox(12.0, 9.0, 0.0, 16.0, 10.0, 2.0), new ShapeBox(12.0, 7.0, 0.0, 16.0, 8.0, 2.0))),
            Map.entry("mek:solar_generator", List.of(new ShapeBox(0.0, 6.0, 0.0, 16.0, 8.0, 16.0), new ShapeBox(4.0, 0.0, 4.0, 12.0, 1.0, 12.0), new ShapeBox(6.0, 4.0, 6.0, 10.0, 6.0, 10.0), new ShapeBox(7.0, 2.0, 7.0, 9.0, 4.0, 9.0), new ShapeBox(5.0, 1.0, 5.0, 6.0, 5.0, 6.0), new ShapeBox(10.0, 1.0, 5.0, 11.0, 5.0, 6.0), new ShapeBox(5.0, 1.0, 10.0, 6.0, 5.0, 11.0), new ShapeBox(10.0, 1.0, 10.0, 11.0, 5.0, 11.0), new ShapeBox(6.0, 1.0, 6.0, 10.0, 2.0, 10.0), new ShapeBox(1.0, 5.0, 1.0, 15.0, 6.0, 15.0))),
            Map.entry("mek:advanced_solar_generator", List.of(new ShapeBox(6.0, -3.0, 6.0, 10.0, 26.0, 10.0), new ShapeBox(4.0, 23.0, 2.0, 6.0, 25.0, 14.0), new ShapeBox(10.0, 23.0, 2.0, 12.0, 25.0, 14.0), new ShapeBox(4.0, 26.0, 5.0, 12.0, 32.0, 11.0), new ShapeBox(5.0, 25.0, 4.0, 6.0, 33.0, 12.0), new ShapeBox(10.0, 25.0, 4.0, 11.0, 33.0, 12.0), new ShapeBox(5.0, -10.0, 1.0, 11.0, -4.0, 4.0), new ShapeBox(0.0, -15.0, 0.0, 16.0, -13.0, 16.0), new ShapeBox(3.0, -13.0, 3.0, 13.0, -11.0, 13.0), new ShapeBox(4.0, -11.0, 4.0, 12.0, -3.0, 12.0), new ShapeBox(4.0, -11.0, 0.0, 12.0, -3.0, 1.0), new ShapeBox(14.0, 31.0, -16.0, 32.0, 32.0, 32.0), new ShapeBox(15.0, 30.0, -15.0, 31.0, 31.0, 31.0), new ShapeBox(12.0, 28.0, 7.0, 28.0, 30.0, 9.0), new ShapeBox(-16.0, 31.0, -16.0, 2.0, 32.0, 32.0), new ShapeBox(-15.0, 30.0, -15.0, 1.0, 31.0, 31.0), new ShapeBox(-12.0, 28.0, 7.0, 4.0, 30.0, 9.0))),
            Map.entry("mek:wind_generator", List.of(new ShapeBox(4.5, 68.5, 4.0, 11.5, 75.5, 13.0), new ShapeBox(5.0, 5.0, 1.0, 11.0, 11.0, 11.0), new ShapeBox(6.0, 3.0, 2.5, 10.0, 5.0, 4.5), new ShapeBox(4.0, 4.0, 0.0, 12.0, 12.0, 1.0), new ShapeBox(2.0, 1.0, 2.0, 14.0, 3.0, 14.0), new ShapeBox(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), new ShapeBox(5.5, 68.5, 13.0, 10.5, 74.82, 14.3), new ShapeBox(5.5, 68.75, 14.3, 10.5, 74.1, 14.9), new ShapeBox(6.5, 68.8, 14.9, 9.5, 73.8, 15.3), new ShapeBox(6.5, 69.0, 15.3, 9.5, 72.0, 15.6), new ShapeBox(6.5, 69.0, 15.6, 9.5, 70.3, 16.0), new ShapeBox(5.25, 67.0, 5.25, 10.75, 70.0, 10.75), new ShapeBox(5.0, 59.0, 5.0, 11.0, 67.0, 11.0), new ShapeBox(4.75, 51.0, 4.75, 11.25, 59.0, 11.25), new ShapeBox(4.5, 43.0, 4.5, 11.5, 51.0, 11.5), new ShapeBox(4.25, 35.0, 4.25, 11.75, 43.0, 11.75), new ShapeBox(4.0, 27.0, 4.0, 12.0, 35.0, 12.0), new ShapeBox(3.75, 19.0, 3.75, 12.25, 27.0, 12.25), new ShapeBox(3.5, 11.0, 3.5, 12.5, 19.0, 12.5), new ShapeBox(3.25, 15.0, 3.25, 12.75, 19.0, 12.75), new ShapeBox(3.0, 3.0, 3.0, 13.0, 15.0, 13.0))),
            Map.entry("mek:bio_generator", List.of(new ShapeBox(0.0, 0.0, 0.0, 16.0, 7.0, 16.0), new ShapeBox(3.0, 14.01, 14.01, 13.0, 15.01, 15.01), new ShapeBox(0.0, 7.0, 8.0, 3.0, 16.0, 16.0), new ShapeBox(13.0, 7.0, 8.0, 16.0, 16.0, 16.0), new ShapeBox(0.0, 7.0, 0.0, 16.0, 16.0, 8.0), new ShapeBox(2.0, 7.0, 8.0, 14.0, 15.0, 15.0))),
            Map.entry("mek:gas_burning_generator", List.of(new ShapeBox(4.0, 12.0, 13.0, 12.0, 13.0, 15.0), new ShapeBox(1.0, 12.0, 4.0, 3.0, 13.0, 12.0), new ShapeBox(1.0, 5.0, 4.0, 2.0, 5.0, 12.0), new ShapeBox(4.0, 5.0, 14.0, 12.0, 5.0, 15.0), new ShapeBox(14.0, 5.0, 4.0, 15.0, 5.0, 12.0), new ShapeBox(4.0, 5.0, 1.0, 12.0, 5.0, 2.0), new ShapeBox(4.0, 12.0, 1.0, 12.0, 13.0, 3.0), new ShapeBox(13.0, 12.0, 4.0, 15.0, 13.0, 12.0), new ShapeBox(3.0, 6.0, 3.0, 13.0, 16.0, 13.0), new ShapeBox(12.0, 5.0, 1.0, 15.0, 14.0, 4.0), new ShapeBox(1.0, 5.0, 1.0, 4.0, 14.0, 4.0), new ShapeBox(1.0, 5.0, 12.0, 4.0, 14.0, 15.0), new ShapeBox(12.0, 5.0, 12.0, 15.0, 14.0, 15.0), new ShapeBox(0.0, 0.0, 0.0, 16.0, 4.0, 16.0), new ShapeBox(2.0, 4.0, 2.0, 14.0, 5.0, 14.0), new ShapeBox(4.0, 4.0, 0.0, 12.0, 12.0, 1.0), new ShapeBox(15.0, 4.0, 4.0, 16.0, 12.0, 12.0), new ShapeBox(4.0, 4.0, 15.0, 12.0, 12.0, 16.0), new ShapeBox(0.0, 4.0, 4.0, 1.0, 12.0, 12.0))));

    /** 非满机器方块碰撞盒：上游 BlockShapes 的 box 列表，单位 1/16 格。*/
    public static final Map<String, List<ShapeBox>> MACHINE_SHAPES = Map.ofEntries(
            Map.entry("mek:advanced_chemical_tank", List.of(new ShapeBox(6.0, 13.0, 4.0, 10.0, 14.0, 5.0), new ShapeBox(10.0, 13.0, 4.0, 12.0, 16.0, 5.0), new ShapeBox(11.0, 13.0, 5.0, 12.0, 16.0, 11.0), new ShapeBox(4.0, 13.0, 11.0, 12.0, 16.0, 12.0), new ShapeBox(4.0, 13.0, 5.0, 5.0, 16.0, 11.0), new ShapeBox(4.0, 13.0, 4.0, 6.0, 16.0, 5.0), new ShapeBox(3.0, 1.0, 3.0, 13.0, 13.0, 13.0), new ShapeBox(4.0, 0.0, 4.0, 12.0, 1.0, 12.0), new ShapeBox(6.5, 14.0, 6.5, 9.5, 15.0, 9.5), new ShapeBox(7.0, 12.0, 7.0, 9.0, 14.0, 9.0))),
            Map.entry("mek:advanced_fluid_tank", List.of(new ShapeBox(2.0, 0.0, 2.0, 14.0, 16.0, 14.0))),
            Map.entry("mek:antiprotonic_nucleosynthesizer", List.of(new ShapeBox(0.0, 5.0, 0.0, 16.0, 8.0, 16.0), new ShapeBox(1.0, 3.0, 1.0, 15.0, 5.0, 15.0), new ShapeBox(0.0, 0.0, 0.0, 16.0, 3.0, 16.0), new ShapeBox(12.0, 8.0, 0.0, 16.0, 16.0, 10.0), new ShapeBox(11.0, 9.0, 4.0, 12.0, 14.0, 9.0), new ShapeBox(4.0, 9.0, 4.0, 5.0, 14.0, 9.0), new ShapeBox(5.0, 11.0, 6.0, 6.0, 12.0, 7.0), new ShapeBox(10.0, 11.0, 6.0, 11.0, 12.0, 7.0), new ShapeBox(0.0, 8.0, 10.0, 16.0, 16.0, 13.0), new ShapeBox(0.0, 13.0, 13.0, 16.0, 16.0, 16.0), new ShapeBox(1.0, 11.0, 13.0, 15.0, 12.0, 15.0), new ShapeBox(1.0, 9.0, 13.0, 15.0, 10.0, 15.0), new ShapeBox(0.0, 8.0, 0.0, 4.0, 16.0, 10.0), new ShapeBox(6.0, 8.0, 13.0, 10.0, 13.0, 16.0), new ShapeBox(6.0, 3.0, 15.0, 10.0, 5.0, 16.0), new ShapeBox(2.0, 8.0, 13.0, 3.0, 13.0, 14.0), new ShapeBox(4.0, 8.0, 13.0, 5.0, 13.0, 14.0), new ShapeBox(11.0, 8.0, 13.0, 12.0, 13.0, 14.0), new ShapeBox(13.0, 8.0, 13.0, 14.0, 13.0, 14.0), new ShapeBox(4.0, 4.0, 15.0, 12.0, 12.0, 16.0), new ShapeBox(4.0, 8.0, 1.0, 12.0, 15.0, 10.0))),
            Map.entry("mek:basic_chemical_tank", List.of(new ShapeBox(6.0, 13.0, 4.0, 10.0, 14.0, 5.0), new ShapeBox(10.0, 13.0, 4.0, 12.0, 16.0, 5.0), new ShapeBox(11.0, 13.0, 5.0, 12.0, 16.0, 11.0), new ShapeBox(4.0, 13.0, 11.0, 12.0, 16.0, 12.0), new ShapeBox(4.0, 13.0, 5.0, 5.0, 16.0, 11.0), new ShapeBox(4.0, 13.0, 4.0, 6.0, 16.0, 5.0), new ShapeBox(3.0, 1.0, 3.0, 13.0, 13.0, 13.0), new ShapeBox(4.0, 0.0, 4.0, 12.0, 1.0, 12.0), new ShapeBox(6.5, 14.0, 6.5, 9.5, 15.0, 9.5), new ShapeBox(7.0, 12.0, 7.0, 9.0, 14.0, 9.0))),
            Map.entry("mek:basic_fluid_tank", List.of(new ShapeBox(2.0, 0.0, 2.0, 14.0, 16.0, 14.0))),
            Map.entry("mek:chargepad", List.of(new ShapeBox(0.0, 0.0, 0.0, 16.0, 1.0, 16.0), new ShapeBox(9.0, 1.0, 12.0, 10.0, 5.0, 15.0), new ShapeBox(6.0, 1.0, 12.0, 7.0, 5.0, 15.0), new ShapeBox(5.0, 5.0, 12.0, 11.0, 11.0, 15.0), new ShapeBox(4.0, 4.0, 15.0, 12.0, 12.0, 16.0))),
            Map.entry("mek:chemical_crystallizer", List.of(new ShapeBox(0.0, 5.0, 14.0, 16.0, 7.0, 16.0), new ShapeBox(0.0, 5.0, 2.0, 2.0, 7.0, 14.0), new ShapeBox(0.0, 5.0, 0.0, 16.0, 7.0, 2.0), new ShapeBox(14.0, 5.0, 2.0, 16.0, 7.0, 14.0), new ShapeBox(3.0, 5.0, 3.0, 13.0, 6.0, 13.0), new ShapeBox(0.0, 4.0, 4.0, 1.0, 12.0, 12.0), new ShapeBox(15.0, 4.0, 4.0, 16.0, 12.0, 12.0), new ShapeBox(0.0, 7.0, 0.0, 1.0, 11.0, 1.0), new ShapeBox(0.0, 7.0, 15.0, 1.0, 11.0, 16.0), new ShapeBox(15.0, 7.0, 15.0, 16.0, 11.0, 16.0), new ShapeBox(15.0, 7.0, 0.0, 16.0, 11.0, 1.0), new ShapeBox(0.0, 0.0, 0.0, 16.0, 5.0, 16.0), new ShapeBox(0.0, 11.0, 0.0, 16.0, 16.0, 16.0), new ShapeBox(6.0, 9.0, 8.0, 7.0, 11.0, 9.0), new ShapeBox(9.0, 8.0, 9.0, 10.0, 11.0, 10.0), new ShapeBox(7.5, 7.0, 6.0, 8.5, 11.0, 7.0), new ShapeBox(1.0, 7.0, 1.0, 15.0, 11.0, 15.0))),
            Map.entry("mek:chemical_dissolution_chamber", List.of(new ShapeBox(0.0, 0.0, 0.0, 16.0, 7.0, 16.0), new ShapeBox(0.0, 3.0, 4.0, 1.0, 11.0, 12.0), new ShapeBox(15.0, 3.0, 3.0, 16.0, 13.0, 13.0), new ShapeBox(0.0, 7.0, 15.0, 16.0, 15.0, 16.0), new ShapeBox(1.0, 7.0, 14.0, 15.0, 15.0, 15.0), new ShapeBox(0.0, 15.0, 0.0, 16.0, 16.0, 16.0), new ShapeBox(0.0, 12.0, 0.0, 16.0, 13.0, 15.0), new ShapeBox(4.0, 13.0, 4.0, 12.0, 15.0, 14.0), new ShapeBox(1.0, 13.0, 1.0, 2.0, 15.0, 2.0), new ShapeBox(14.0, 13.0, 1.0, 15.0, 15.0, 2.0), new ShapeBox(1.0, 7.0, 1.0, 15.0, 12.0, 14.0))),
            Map.entry("mek:chemical_infuser", List.of(new ShapeBox(0.0, 4.0, 4.0, 1.0, 12.0, 12.0), new ShapeBox(15.0, 4.0, 4.0, 16.0, 12.0, 12.0), new ShapeBox(2.0, 5.0, 1.0, 14.0, 12.0, 8.0), new ShapeBox(9.0, 13.5, 4.0, 10.0, 14.5, 6.0), new ShapeBox(7.0, 5.0, 13.0, 9.0, 11.0, 15.0), new ShapeBox(3.0, 5.0, 6.0, 13.0, 11.0, 9.0), new ShapeBox(8.0, 8.0, 9.0, 9.0, 9.0, 13.0), new ShapeBox(1.0, 5.0, 5.0, 2.0, 11.0, 9.0), new ShapeBox(6.0, 13.5, 4.0, 7.0, 14.5, 6.0), new ShapeBox(7.0, 14.0, 13.0, 9.0, 15.0, 14.0), new ShapeBox(11.0, 13.5, 6.5, 13.0, 14.5, 7.5), new ShapeBox(4.0, 4.0, 0.0, 12.0, 12.0, 1.0), new ShapeBox(0.0, 0.0, 0.0, 16.0, 5.0, 16.0), new ShapeBox(3.0, 13.5, 7.5, 4.0, 14.5, 9.5), new ShapeBox(14.0, 5.0, 5.0, 15.0, 11.0, 9.0), new ShapeBox(12.0, 13.5, 7.5, 13.0, 14.5, 9.5), new ShapeBox(6.0, 11.5, 4.0, 7.0, 13.5, 5.0), new ShapeBox(4.0, 4.0, 15.0, 12.0, 12.0, 16.0), new ShapeBox(3.0, 13.5, 6.5, 5.0, 14.5, 7.5), new ShapeBox(7.0, 14.0, 10.0, 9.0, 15.0, 11.0), new ShapeBox(5.0, 12.5, 5.5, 11.0, 15.5, 8.5), new ShapeBox(9.0, 11.5, 4.0, 10.0, 13.5, 5.0), new ShapeBox(11.0, 12.0, 2.0, 12.0, 13.0, 3.0), new ShapeBox(9.0, 12.0, 2.0, 10.0, 13.0, 3.0), new ShapeBox(6.0, 12.0, 2.0, 7.0, 13.0, 3.0), new ShapeBox(4.0, 12.0, 2.0, 5.0, 13.0, 3.0), new ShapeBox(9.0, 5.0, 9.0, 15.0, 16.0, 15.0), new ShapeBox(1.0, 5.0, 9.0, 7.0, 16.0, 15.0))),
            Map.entry("mek:chemical_injection_chamber_factory_basic", List.of(new ShapeBox(0.0, 0.0, 0.0, 16.0, 16.0, 4.0), new ShapeBox(4.0, 4.0, 4.0, 12.0, 12.0, 16.0), new ShapeBox(0.0, 0.0, 4.0, 16.0, 4.0, 16.0), new ShapeBox(0.0, 4.0, 4.0, 7.0, 16.0, 16.0), new ShapeBox(9.0, 4.0, 4.0, 16.0, 16.0, 16.0), new ShapeBox(7.0, 12.0, 12.0, 9.0, 14.0, 13.0), new ShapeBox(7.0, 12.0, 9.0, 9.0, 14.0, 10.0), new ShapeBox(7.0, 12.0, 6.0, 9.0, 14.0, 7.0))),
            Map.entry("mek:chemical_injection_chamber_factory_advanced", List.of(new ShapeBox(0.0, 0.0, 0.0, 16.0, 16.0, 4.0), new ShapeBox(4.0, 4.0, 4.0, 12.0, 12.0, 16.0), new ShapeBox(0.0, 0.0, 4.0, 16.0, 4.0, 16.0), new ShapeBox(0.0, 4.0, 4.0, 7.0, 16.0, 16.0), new ShapeBox(9.0, 4.0, 4.0, 16.0, 16.0, 16.0), new ShapeBox(7.0, 12.0, 12.0, 9.0, 14.0, 13.0), new ShapeBox(7.0, 12.0, 9.0, 9.0, 14.0, 10.0), new ShapeBox(7.0, 12.0, 6.0, 9.0, 14.0, 7.0))),
            Map.entry("mek:chemical_injection_chamber_factory_elite", List.of(new ShapeBox(0.0, 0.0, 0.0, 16.0, 16.0, 4.0), new ShapeBox(4.0, 4.0, 4.0, 12.0, 12.0, 16.0), new ShapeBox(0.0, 0.0, 4.0, 16.0, 4.0, 16.0), new ShapeBox(0.0, 4.0, 4.0, 7.0, 16.0, 16.0), new ShapeBox(9.0, 4.0, 4.0, 16.0, 16.0, 16.0), new ShapeBox(7.0, 12.0, 12.0, 9.0, 14.0, 13.0), new ShapeBox(7.0, 12.0, 9.0, 9.0, 14.0, 10.0), new ShapeBox(7.0, 12.0, 6.0, 9.0, 14.0, 7.0))),
            Map.entry("mek:chemical_injection_chamber_factory_ultimate", List.of(new ShapeBox(0.0, 0.0, 0.0, 16.0, 16.0, 4.0), new ShapeBox(4.0, 4.0, 4.0, 12.0, 12.0, 16.0), new ShapeBox(0.0, 0.0, 4.0, 16.0, 4.0, 16.0), new ShapeBox(0.0, 4.0, 4.0, 7.0, 16.0, 16.0), new ShapeBox(9.0, 4.0, 4.0, 16.0, 16.0, 16.0), new ShapeBox(7.0, 12.0, 12.0, 9.0, 14.0, 13.0), new ShapeBox(7.0, 12.0, 9.0, 9.0, 14.0, 10.0), new ShapeBox(7.0, 12.0, 6.0, 9.0, 14.0, 7.0))),
            Map.entry("mek:chemical_oxidizer", List.of(new ShapeBox(9.0, 4.0, 2.0, 13.0, 5.0, 14.0), new ShapeBox(8.0, 5.0, 7.0, 14.0, 16.0, 15.0), new ShapeBox(8.0, 5.0, 1.0, 14.0, 16.0, 6.0), new ShapeBox(7.0, 7.0, 9.0, 8.0, 10.0, 12.0), new ShapeBox(0.0, 4.0, 0.0, 7.0, 16.0, 16.0), new ShapeBox(0.0, 4.0, 4.0, 1.0, 12.0, 12.0), new ShapeBox(9.0, 6.0, 6.0, 13.0, 15.0, 7.0), new ShapeBox(13.0, 5.0, 5.0, 15.0, 11.0, 11.0), new ShapeBox(15.0, 3.0, 3.0, 16.0, 13.0, 13.0), new ShapeBox(0.0, 0.0, 0.0, 16.0, 4.0, 16.0))),
            Map.entry("mek:chemical_washer", List.of(new ShapeBox(0.0, 0.0, 0.0, 16.0, 4.0, 16.0), new ShapeBox(4.0, 13.0, 4.0, 12.0, 15.0, 12.0), new ShapeBox(5.0, 4.0, 5.0, 11.0, 13.0, 10.0), new ShapeBox(0.0, 4.0, 0.0, 7.0, 14.0, 8.0), new ShapeBox(9.0, 4.0, 0.0, 16.0, 14.0, 8.0), new ShapeBox(0.0, 4.0, 10.0, 16.0, 14.0, 16.0), new ShapeBox(13.0, 5.0, 8.0, 15.0, 11.0, 10.0), new ShapeBox(7.0, 7.0, 3.0, 9.0, 9.0, 5.0), new ShapeBox(7.0, 6.0, 1.0, 9.0, 7.0, 2.0), new ShapeBox(7.0, 8.0, 1.0, 9.0, 9.0, 2.0), new ShapeBox(7.0, 10.0, 1.0, 9.0, 11.0, 2.0), new ShapeBox(7.0, 12.0, 1.0, 9.0, 13.0, 2.0), new ShapeBox(1.0, 14.0, 4.0, 2.0, 15.0, 5.0), new ShapeBox(1.0, 14.0, 6.0, 2.0, 15.0, 7.0), new ShapeBox(13.0, 14.0, 4.0, 14.0, 15.0, 12.0), new ShapeBox(1.0, 5.0, 8.0, 3.0, 11.0, 10.0), new ShapeBox(3.0, 15.0, 3.0, 13.0, 16.0, 13.0), new ShapeBox(0.0, 4.0, 4.0, 1.0, 12.0, 12.0), new ShapeBox(15.0, 4.0, 4.0, 16.0, 12.0, 12.0))),
            Map.entry("mek:combiner_factory_basic", List.of(new ShapeBox(0.0, 12.0, 4.0, 16.0, 16.0, 16.0), new ShapeBox(5.0, 4.0, 4.0, 11.0, 12.0, 14.0), new ShapeBox(0.0, 0.0, 0.0, 16.0, 16.0, 4.0), new ShapeBox(4.0, 4.0, 14.0, 12.0, 12.0, 16.0), new ShapeBox(0.0, 0.0, 4.0, 16.0, 4.0, 16.0), new ShapeBox(1.0, 4.0, 5.0, 15.0, 7.0, 15.0), new ShapeBox(1.0, 8.0, 5.0, 15.0, 11.0, 15.0), new ShapeBox(3.0, 11.0, 12.0, 13.0, 12.0, 14.0), new ShapeBox(3.0, 11.0, 6.0, 13.0, 12.0, 8.0))),
            Map.entry("mek:combiner_factory_advanced", List.of(new ShapeBox(0.0, 12.0, 4.0, 16.0, 16.0, 16.0), new ShapeBox(5.0, 4.0, 4.0, 11.0, 12.0, 14.0), new ShapeBox(0.0, 0.0, 0.0, 16.0, 16.0, 4.0), new ShapeBox(4.0, 4.0, 14.0, 12.0, 12.0, 16.0), new ShapeBox(0.0, 0.0, 4.0, 16.0, 4.0, 16.0), new ShapeBox(1.0, 4.0, 5.0, 15.0, 7.0, 15.0), new ShapeBox(1.0, 8.0, 5.0, 15.0, 11.0, 15.0), new ShapeBox(3.0, 11.0, 12.0, 13.0, 12.0, 14.0), new ShapeBox(3.0, 11.0, 6.0, 13.0, 12.0, 8.0))),
            Map.entry("mek:combiner_factory_elite", List.of(new ShapeBox(0.0, 12.0, 4.0, 16.0, 16.0, 16.0), new ShapeBox(5.0, 4.0, 4.0, 11.0, 12.0, 14.0), new ShapeBox(0.0, 0.0, 0.0, 16.0, 16.0, 4.0), new ShapeBox(4.0, 4.0, 14.0, 12.0, 12.0, 16.0), new ShapeBox(0.0, 0.0, 4.0, 16.0, 4.0, 16.0), new ShapeBox(1.0, 4.0, 5.0, 15.0, 7.0, 15.0), new ShapeBox(1.0, 8.0, 5.0, 15.0, 11.0, 15.0), new ShapeBox(3.0, 11.0, 12.0, 13.0, 12.0, 14.0), new ShapeBox(3.0, 11.0, 6.0, 13.0, 12.0, 8.0))),
            Map.entry("mek:combiner_factory_ultimate", List.of(new ShapeBox(0.0, 12.0, 4.0, 16.0, 16.0, 16.0), new ShapeBox(5.0, 4.0, 4.0, 11.0, 12.0, 14.0), new ShapeBox(0.0, 0.0, 0.0, 16.0, 16.0, 4.0), new ShapeBox(4.0, 4.0, 14.0, 12.0, 12.0, 16.0), new ShapeBox(0.0, 0.0, 4.0, 16.0, 4.0, 16.0), new ShapeBox(1.0, 4.0, 5.0, 15.0, 7.0, 15.0), new ShapeBox(1.0, 8.0, 5.0, 15.0, 11.0, 15.0), new ShapeBox(3.0, 11.0, 12.0, 13.0, 12.0, 14.0), new ShapeBox(3.0, 11.0, 6.0, 13.0, 12.0, 8.0))),
            Map.entry("mek:crusher_factory_basic", List.of(new ShapeBox(0.0, 0.0, 0.0, 16.0, 16.0, 4.0), new ShapeBox(2.0, 0.0, 7.0, 14.0, 4.0, 12.0), new ShapeBox(2.0, 10.0, 7.0, 14.0, 12.0, 12.0), new ShapeBox(3.0, 4.0, 7.0, 13.0, 12.0, 12.0), new ShapeBox(0.0, 12.0, 4.0, 16.0, 16.0, 16.0), new ShapeBox(1.0, 0.0, 4.0, 15.0, 12.0, 7.0), new ShapeBox(1.0, 0.0, 12.0, 15.0, 12.0, 15.0), new ShapeBox(4.0, 4.0, 14.0, 12.0, 12.0, 16.0))),
            Map.entry("mek:crusher_factory_advanced", List.of(new ShapeBox(0.0, 0.0, 0.0, 16.0, 16.0, 4.0), new ShapeBox(2.0, 0.0, 7.0, 14.0, 4.0, 12.0), new ShapeBox(2.0, 10.0, 7.0, 14.0, 12.0, 12.0), new ShapeBox(3.0, 4.0, 7.0, 13.0, 12.0, 12.0), new ShapeBox(0.0, 12.0, 4.0, 16.0, 16.0, 16.0), new ShapeBox(1.0, 0.0, 4.0, 15.0, 12.0, 7.0), new ShapeBox(1.0, 0.0, 12.0, 15.0, 12.0, 15.0), new ShapeBox(4.0, 4.0, 14.0, 12.0, 12.0, 16.0))),
            Map.entry("mek:crusher_factory_elite", List.of(new ShapeBox(0.0, 0.0, 0.0, 16.0, 16.0, 4.0), new ShapeBox(2.0, 0.0, 7.0, 14.0, 4.0, 12.0), new ShapeBox(2.0, 10.0, 7.0, 14.0, 12.0, 12.0), new ShapeBox(3.0, 4.0, 7.0, 13.0, 12.0, 12.0), new ShapeBox(0.0, 12.0, 4.0, 16.0, 16.0, 16.0), new ShapeBox(1.0, 0.0, 4.0, 15.0, 12.0, 7.0), new ShapeBox(1.0, 0.0, 12.0, 15.0, 12.0, 15.0), new ShapeBox(4.0, 4.0, 14.0, 12.0, 12.0, 16.0))),
            Map.entry("mek:crusher_factory_ultimate", List.of(new ShapeBox(0.0, 0.0, 0.0, 16.0, 16.0, 4.0), new ShapeBox(2.0, 0.0, 7.0, 14.0, 4.0, 12.0), new ShapeBox(2.0, 10.0, 7.0, 14.0, 12.0, 12.0), new ShapeBox(3.0, 4.0, 7.0, 13.0, 12.0, 12.0), new ShapeBox(0.0, 12.0, 4.0, 16.0, 16.0, 16.0), new ShapeBox(1.0, 0.0, 4.0, 15.0, 12.0, 7.0), new ShapeBox(1.0, 0.0, 12.0, 15.0, 12.0, 15.0), new ShapeBox(4.0, 4.0, 14.0, 12.0, 12.0, 16.0))),
            Map.entry("mek:digital_miner", List.of(new ShapeBox(16.0, 17.0, -13.0, 30.0, 27.0, -11.0), new ShapeBox(1.0, 17.0, -13.0, 15.0, 27.0, -11.0), new ShapeBox(-14.0, 17.0, -13.0, 0.0, 27.0, -11.0), new ShapeBox(17.0, 19.0, -11.0, 29.0, 25.0, -10.0), new ShapeBox(2.0, 19.0, -11.0, 14.0, 25.0, -10.0), new ShapeBox(-13.0, 19.0, -11.0, -1.0, 25.0, -10.0), new ShapeBox(-1.0, 21.0, -11.0, 2.0, 23.0, -10.0), new ShapeBox(14.0, 21.0, -11.0, 17.0, 23.0, -10.0), new ShapeBox(4.0, 21.0, -11.0, 12.0, 23.0, -9.0), new ShapeBox(3.0, 11.3498, -16.0, 13.0, 12.3498, -11.0), new ShapeBox(4.0, 10.3498, -15.0, 12.0, 11.3498, -11.0), new ShapeBox(5.0, 10.0, -13.25, 11.0, 12.0, -9.0), new ShapeBox(-8.0, 3.0, -9.0, 24.0, 32.0, 3.0), new ShapeBox(17.0, 26.99, 4.0, 24.0, 31.99, 19.0), new ShapeBox(-8.0, 26.99, 4.0, -1.0, 31.99, 19.0), new ShapeBox(0.0, 26.99, 3.0, 16.0, 31.99, 16.0), new ShapeBox(0.0, 26.99, 17.0, 16.0, 31.99, 20.0), new ShapeBox(-8.0, 21.0, 4.0, 24.0, 26.0, 19.0), new ShapeBox(-8.0, 15.0, 4.0, 24.0, 20.0, 19.0), new ShapeBox(-8.0, 9.0, 4.0, 24.0, 14.0, 19.0), new ShapeBox(-8.0, 3.0, 4.0, 24.0, 8.0, 19.0), new ShapeBox(-8.0, 3.0, 19.99, 24.0, 32.0, 31.99), new ShapeBox(-7.0, 15.0, 3.0, 23.0, 31.0, 20.0), new ShapeBox(-7.0, 4.0, 3.0, 23.0, 15.0, 20.0), new ShapeBox(-9.0, 24.0, -6.0, -8.0, 29.0, 0.0), new ShapeBox(-13.0, 24.0, -8.0, -8.0, 29.0, -6.0), new ShapeBox(-13.0, 24.0, 0.0, -8.0, 29.0, 2.0), new ShapeBox(-9.0, 24.0, 23.0, -8.0, 29.0, 29.0), new ShapeBox(-13.0, 24.0, 21.0, -8.0, 29.0, 23.0), new ShapeBox(-13.0, 24.0, 29.0, -8.0, 29.0, 31.0), new ShapeBox(24.0, 24.0, -6.0, 25.0, 29.0, 0.0), new ShapeBox(24.0, 24.0, 0.0, 29.0, 29.0, 2.0), new ShapeBox(24.0, 24.0, -8.0, 29.0, 29.0, -6.0), new ShapeBox(24.0, 24.0, 23.0, 25.0, 29.0, 29.0), new ShapeBox(24.0, 24.0, 29.0, 29.0, 29.0, 31.0), new ShapeBox(24.0, 24.0, 21.0, 29.0, 29.0, 23.0), new ShapeBox(5.0, 2.0, -6.0, 11.0, 4.0, 5.0), new ShapeBox(5.0, 1.0, 5.0, 11.0, 3.0, 11.0), new ShapeBox(23.0, 5.0, 5.0, 31.0, 11.0, 11.0), new ShapeBox(-15.0, 5.0, 5.0, -7.0, 11.0, 11.0), new ShapeBox(-14.0, 2.0, -7.0, -10.0, 30.0, 1.0), new ShapeBox(-14.0, 2.0, 22.0, -10.0, 30.0, 30.0), new ShapeBox(26.0, 2.0, 22.0, 30.0, 30.0, 30.0), new ShapeBox(26.0, 2.0, -7.0, 30.0, 30.0, 1.0), new ShapeBox(-15.0, 0.0, -8.0, -8.0, 2.0, 2.0), new ShapeBox(-15.0, 0.0, 21.0, -8.0, 2.0, 31.0), new ShapeBox(24.0, 0.0, 21.0, 31.0, 2.0, 31.0), new ShapeBox(24.0, 0.0, -8.0, 31.0, 2.0, 2.0), new ShapeBox(-9.0, 4.0, 4.0, -8.0, 12.0, 12.0), new ShapeBox(-16.0, 4.0, 4.0, -15.0, 12.0, 12.0), new ShapeBox(24.0, 4.0, 4.0, 25.0, 12.0, 12.0), new ShapeBox(31.0, 4.0, 4.0, 32.0, 12.0, 12.0), new ShapeBox(4.0, 0.0, 4.0, 12.0, 1.0, 12.0), new ShapeBox(17.0, 16.5, -12.005, 18.0, 17.5, -11.005), new ShapeBox(2.0, 16.5, -12.005, 3.0, 17.5, -11.005), new ShapeBox(-13.0, 16.5, -12.005, -12.0, 17.5, -11.005))),
            Map.entry("mek:electric_pump", List.of(new ShapeBox(4.0, 1.0, 4.0, 12.0, 12.0, 12.0), new ShapeBox(5.0, 10.0, 11.0, 6.0, 11.0, 15.0), new ShapeBox(10.0, 10.0, 11.0, 11.0, 11.0, 15.0), new ShapeBox(10.0, 5.0, 11.0, 11.0, 6.0, 15.0), new ShapeBox(5.0, 5.0, 11.0, 6.0, 6.0, 15.0), new ShapeBox(5.0, 0.0, 5.0, 11.0, 15.0, 11.0), new ShapeBox(4.0, 15.0, 4.0, 12.0, 16.0, 12.0), new ShapeBox(4.0, 13.0, 4.0, 12.0, 14.0, 12.0), new ShapeBox(6.0, 6.0, 12.0, 10.0, 10.0, 15.0), new ShapeBox(4.0, 4.0, 15.0, 12.0, 12.0, 16.0))),
            Map.entry("mek:electrolytic_separator", List.of(new ShapeBox(0.0, 0.0, 0.0, 16.0, 4.0, 16.0), new ShapeBox(15.0, 3.0, 3.0, 16.0, 13.0, 13.0), new ShapeBox(0.0, 4.0, 4.0, 1.0, 12.0, 12.0), new ShapeBox(4.0, 4.0, 0.0, 12.0, 12.0, 1.0), new ShapeBox(4.0, 4.0, 15.0, 12.0, 12.0, 16.0), new ShapeBox(1.0, 4.0, 7.0, 3.0, 11.0, 9.0), new ShapeBox(7.0, 4.0, 1.0, 8.0, 11.0, 3.0), new ShapeBox(7.0, 4.0, 13.0, 8.0, 11.0, 15.0), new ShapeBox(8.0, 4.0, 0.0, 16.0, 16.0, 16.0), new ShapeBox(0.0, 4.0, 9.0, 7.0, 14.0, 16.0), new ShapeBox(0.0, 4.0, 0.0, 7.0, 14.0, 7.0), new ShapeBox(7.0, 10.0, 7.5, 8.0, 11.0, 8.5), new ShapeBox(4.0, 12.0, 7.5, 7.0, 13.0, 8.5), new ShapeBox(3.0, 12.0, 7.5, 4.0, 15.0, 8.5), new ShapeBox(3.0, 15.0, 3.0, 4.0, 16.0, 13.0), new ShapeBox(3.0, 14.0, 3.0, 4.0, 15.0, 4.0), new ShapeBox(3.0, 14.0, 12.0, 4.0, 15.0, 13.0), new ShapeBox(6.0, 10.0, 7.5, 7.0, 12.0, 8.5))),
            Map.entry("mek:elite_chemical_tank", List.of(new ShapeBox(6.0, 13.0, 4.0, 10.0, 14.0, 5.0), new ShapeBox(10.0, 13.0, 4.0, 12.0, 16.0, 5.0), new ShapeBox(11.0, 13.0, 5.0, 12.0, 16.0, 11.0), new ShapeBox(4.0, 13.0, 11.0, 12.0, 16.0, 12.0), new ShapeBox(4.0, 13.0, 5.0, 5.0, 16.0, 11.0), new ShapeBox(4.0, 13.0, 4.0, 6.0, 16.0, 5.0), new ShapeBox(3.0, 1.0, 3.0, 13.0, 13.0, 13.0), new ShapeBox(4.0, 0.0, 4.0, 12.0, 1.0, 12.0), new ShapeBox(6.5, 14.0, 6.5, 9.5, 15.0, 9.5), new ShapeBox(7.0, 12.0, 7.0, 9.0, 14.0, 9.0))),
            Map.entry("mek:elite_fluid_tank", List.of(new ShapeBox(2.0, 0.0, 2.0, 14.0, 16.0, 14.0))),
            Map.entry("mek:energized_smelter_factory_basic", List.of(new ShapeBox(0.0, 0.0, 0.0, 16.0, 16.0, 4.0), new ShapeBox(0.0, 0.0, 5.0, 16.0, 4.0, 16.0), new ShapeBox(1.0, 0.0, 4.0, 15.0, 15.0, 15.0), new ShapeBox(4.0, 4.0, 14.0, 12.0, 12.0, 16.0), new ShapeBox(11.0, 4.0, 5.0, 16.0, 16.0, 10.0), new ShapeBox(0.0, 4.0, 5.0, 5.0, 16.0, 10.0), new ShapeBox(11.0, 4.0, 11.0, 16.0, 16.0, 16.0), new ShapeBox(0.0, 4.0, 11.0, 5.0, 16.0, 16.0))),
            Map.entry("mek:energized_smelter_factory_advanced", List.of(new ShapeBox(0.0, 0.0, 0.0, 16.0, 16.0, 4.0), new ShapeBox(0.0, 0.0, 5.0, 16.0, 4.0, 16.0), new ShapeBox(1.0, 0.0, 4.0, 15.0, 15.0, 15.0), new ShapeBox(4.0, 4.0, 14.0, 12.0, 12.0, 16.0), new ShapeBox(11.0, 4.0, 5.0, 16.0, 16.0, 10.0), new ShapeBox(0.0, 4.0, 5.0, 5.0, 16.0, 10.0), new ShapeBox(11.0, 4.0, 11.0, 16.0, 16.0, 16.0), new ShapeBox(0.0, 4.0, 11.0, 5.0, 16.0, 16.0))),
            Map.entry("mek:energized_smelter_factory_elite", List.of(new ShapeBox(0.0, 0.0, 0.0, 16.0, 16.0, 4.0), new ShapeBox(0.0, 0.0, 5.0, 16.0, 4.0, 16.0), new ShapeBox(1.0, 0.0, 4.0, 15.0, 15.0, 15.0), new ShapeBox(4.0, 4.0, 14.0, 12.0, 12.0, 16.0), new ShapeBox(11.0, 4.0, 5.0, 16.0, 16.0, 10.0), new ShapeBox(0.0, 4.0, 5.0, 5.0, 16.0, 10.0), new ShapeBox(11.0, 4.0, 11.0, 16.0, 16.0, 16.0), new ShapeBox(0.0, 4.0, 11.0, 5.0, 16.0, 16.0))),
            Map.entry("mek:energized_smelter_factory_ultimate", List.of(new ShapeBox(0.0, 0.0, 0.0, 16.0, 16.0, 4.0), new ShapeBox(0.0, 0.0, 5.0, 16.0, 4.0, 16.0), new ShapeBox(1.0, 0.0, 4.0, 15.0, 15.0, 15.0), new ShapeBox(4.0, 4.0, 14.0, 12.0, 12.0, 16.0), new ShapeBox(11.0, 4.0, 5.0, 16.0, 16.0, 10.0), new ShapeBox(0.0, 4.0, 5.0, 5.0, 16.0, 10.0), new ShapeBox(11.0, 4.0, 11.0, 16.0, 16.0, 16.0), new ShapeBox(0.0, 4.0, 11.0, 5.0, 16.0, 16.0))),
            Map.entry("mek:enrichment_chamber_factory_basic", List.of(new ShapeBox(0.0, 0.0, 0.0, 16.0, 16.0, 4.0), new ShapeBox(4.0, 4.0, 14.0, 12.0, 12.0, 16.0), new ShapeBox(1.0, 7.0, 4.0, 15.0, 14.0, 14.0), new ShapeBox(2.0, 4.0, 4.0, 14.0, 7.0, 10.0), new ShapeBox(5.0, 5.0, 10.0, 11.0, 7.0, 14.0), new ShapeBox(0.0, 0.0, 4.0, 16.0, 4.0, 16.0), new ShapeBox(0.0, 14.0, 4.0, 16.0, 16.0, 16.0), new ShapeBox(0.0, 6.0, 4.0, 2.0, 14.0, 16.0), new ShapeBox(14.0, 6.0, 4.0, 16.0, 14.0, 16.0))),
            Map.entry("mek:enrichment_chamber_factory_advanced", List.of(new ShapeBox(0.0, 0.0, 0.0, 16.0, 16.0, 4.0), new ShapeBox(4.0, 4.0, 14.0, 12.0, 12.0, 16.0), new ShapeBox(1.0, 7.0, 4.0, 15.0, 14.0, 14.0), new ShapeBox(2.0, 4.0, 4.0, 14.0, 7.0, 10.0), new ShapeBox(5.0, 5.0, 10.0, 11.0, 7.0, 14.0), new ShapeBox(0.0, 0.0, 4.0, 16.0, 4.0, 16.0), new ShapeBox(0.0, 14.0, 4.0, 16.0, 16.0, 16.0), new ShapeBox(0.0, 6.0, 4.0, 2.0, 14.0, 16.0), new ShapeBox(14.0, 6.0, 4.0, 16.0, 14.0, 16.0))),
            Map.entry("mek:enrichment_chamber_factory_elite", List.of(new ShapeBox(0.0, 0.0, 0.0, 16.0, 16.0, 4.0), new ShapeBox(4.0, 4.0, 14.0, 12.0, 12.0, 16.0), new ShapeBox(1.0, 7.0, 4.0, 15.0, 14.0, 14.0), new ShapeBox(2.0, 4.0, 4.0, 14.0, 7.0, 10.0), new ShapeBox(5.0, 5.0, 10.0, 11.0, 7.0, 14.0), new ShapeBox(0.0, 0.0, 4.0, 16.0, 4.0, 16.0), new ShapeBox(0.0, 14.0, 4.0, 16.0, 16.0, 16.0), new ShapeBox(0.0, 6.0, 4.0, 2.0, 14.0, 16.0), new ShapeBox(14.0, 6.0, 4.0, 16.0, 14.0, 16.0))),
            Map.entry("mek:enrichment_chamber_factory_ultimate", List.of(new ShapeBox(0.0, 0.0, 0.0, 16.0, 16.0, 4.0), new ShapeBox(4.0, 4.0, 14.0, 12.0, 12.0, 16.0), new ShapeBox(1.0, 7.0, 4.0, 15.0, 14.0, 14.0), new ShapeBox(2.0, 4.0, 4.0, 14.0, 7.0, 10.0), new ShapeBox(5.0, 5.0, 10.0, 11.0, 7.0, 14.0), new ShapeBox(0.0, 0.0, 4.0, 16.0, 4.0, 16.0), new ShapeBox(0.0, 14.0, 4.0, 16.0, 16.0, 16.0), new ShapeBox(0.0, 6.0, 4.0, 2.0, 14.0, 16.0), new ShapeBox(14.0, 6.0, 4.0, 16.0, 14.0, 16.0))),
            Map.entry("mek:isotopic_centrifuge", List.of(new ShapeBox(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), new ShapeBox(1.0, 2.0, 14.0, 2.0, 3.0, 15.0), new ShapeBox(1.0, 2.0, 1.0, 2.0, 3.0, 2.0), new ShapeBox(14.0, 2.0, 1.0, 15.0, 3.0, 2.0), new ShapeBox(14.0, 2.0, 14.0, 15.0, 3.0, 15.0), new ShapeBox(3.0, 2.0, 3.0, 13.0, 16.0, 13.0), new ShapeBox(3.0, 27.0, 3.0, 13.0, 30.0, 13.0), new ShapeBox(3.0, 16.0, 3.0, 6.0, 27.0, 6.0), new ShapeBox(10.0, 16.0, 3.0, 13.0, 27.0, 6.0), new ShapeBox(3.0, 16.0, 10.0, 6.0, 27.0, 13.0), new ShapeBox(10.0, 16.0, 10.0, 13.0, 27.0, 13.0), new ShapeBox(4.0, 16.0, 4.0, 12.0, 27.0, 12.0), new ShapeBox(2.0, 9.0, 2.0, 14.0, 10.0, 14.0), new ShapeBox(2.0, 7.0, 2.0, 14.0, 8.0, 14.0), new ShapeBox(2.0, 5.0, 2.0, 14.0, 6.0, 14.0), new ShapeBox(2.0, 3.0, 2.0, 14.0, 4.0, 14.0), new ShapeBox(2.0, 10.0, 10.0, 3.0, 14.0, 11.0), new ShapeBox(4.0, 30.0, 4.0, 6.0, 32.0, 6.0), new ShapeBox(5.0, 30.0, 10.0, 6.0, 32.0, 11.0), new ShapeBox(6.0, 30.0, 6.0, 10.0, 31.0, 10.0), new ShapeBox(4.0, 4.0, 0.0, 12.0, 12.0, 1.0), new ShapeBox(5.0, 5.0, 1.0, 11.0, 11.0, 3.0), new ShapeBox(4.0, 4.0, 15.0, 12.0, 12.0, 16.0), new ShapeBox(5.0, 5.0, 13.0, 11.0, 11.0, 15.0), new ShapeBox(6.0, 18.0, 7.0, 7.0, 25.0, 8.0), new ShapeBox(6.0, 17.0, 7.0, 9.0, 18.0, 8.0), new ShapeBox(8.0, 15.0, 7.0, 9.0, 17.0, 8.0))),
            Map.entry("mek:laser", List.of(new ShapeBox(5.0, 2.0, 4.0, 11.0, 9.0, 12.0), new ShapeBox(8.995, 10.0, 7.5, 9.995, 13.0, 8.5), new ShapeBox(11.0, 8.0, 4.0, 12.0, 9.0, 12.0), new ShapeBox(11.0, 6.0, 4.0, 12.0, 7.0, 12.0), new ShapeBox(11.0, 4.0, 4.0, 12.0, 5.0, 12.0), new ShapeBox(11.0, 2.0, 4.0, 12.0, 3.0, 12.0), new ShapeBox(4.0, 8.0, 4.0, 5.0, 9.0, 12.0), new ShapeBox(4.0, 6.0, 4.0, 5.0, 7.0, 12.0), new ShapeBox(4.0, 4.0, 4.0, 5.0, 5.0, 12.0), new ShapeBox(4.0, 2.0, 4.0, 5.0, 3.0, 12.0), new ShapeBox(5.0, 9.0, 5.0, 11.0, 10.0, 11.0), new ShapeBox(7.0, 10.0, 7.0, 9.0, 16.0, 9.0), new ShapeBox(10.995, 3.0, 9.0, 11.995, 8.0, 10.0), new ShapeBox(10.995, 3.0, 6.0, 11.995, 8.0, 7.0), new ShapeBox(4.005, 3.0, 6.0, 5.005, 8.0, 7.0), new ShapeBox(4.005, 3.0, 9.0, 5.005, 8.0, 10.0), new ShapeBox(4.0, 0.0, 4.0, 12.0, 1.0, 12.0), new ShapeBox(5.0, 1.0, 5.0, 11.0, 2.0, 11.0), new ShapeBox(6.0, 13.0, 6.0, 10.0, 14.0, 10.0), new ShapeBox(6.0, 11.0, 6.0, 10.0, 12.0, 10.0))),
            Map.entry("mek:laser_amplifier", List.of(new ShapeBox(1.0, 1.0, 1.0, 15.0, 15.0, 15.0), new ShapeBox(0.0, 3.0, 3.0, 1.0, 13.0, 13.0), new ShapeBox(3.0, 3.0, 15.0, 13.0, 13.0, 16.0), new ShapeBox(15.0, 3.0, 3.0, 16.0, 13.0, 13.0), new ShapeBox(3.0, 0.0, 3.0, 13.0, 1.0, 13.0), new ShapeBox(3.0, 3.0, 0.0, 13.0, 13.0, 1.0), new ShapeBox(3.0, 15.0, 3.0, 13.0, 16.0, 13.0))),
            Map.entry("mek:logistical_sorter", List.of(new ShapeBox(2.0, 2.0, 15.0, 14.0, 14.0, 16.0), new ShapeBox(7.0, 10.0, 2.0, 9.0, 12.0, 3.0), new ShapeBox(7.0, 4.0, 2.0, 9.0, 6.0, 3.0), new ShapeBox(4.0, 4.0, 13.0, 12.0, 12.0, 14.0), new ShapeBox(4.0, 4.0, 11.0, 12.0, 12.0, 12.0), new ShapeBox(4.0, 4.0, 9.0, 12.0, 12.0, 10.0), new ShapeBox(4.0, 4.0, 7.0, 12.0, 12.0, 8.0), new ShapeBox(4.0, 4.0, 5.0, 12.0, 12.0, 6.0), new ShapeBox(4.0, 4.0, 3.0, 12.0, 12.0, 4.0), new ShapeBox(4.0, 4.0, 1.0, 12.0, 12.0, 2.0), new ShapeBox(7.0, 11.0, 4.0, 9.0, 13.0, 5.0), new ShapeBox(7.0, 3.0, 4.0, 9.0, 5.0, 5.0), new ShapeBox(3.0, 3.0, 14.0, 13.0, 13.0, 15.0), new ShapeBox(7.0, 11.0, 9.01, 9.0, 13.0, 14.01), new ShapeBox(7.0, 3.0, 9.01, 9.0, 5.0, 14.01), new ShapeBox(3.0, 3.0, 0.0, 13.0, 13.0, 1.0), new ShapeBox(5.0, 5.0, 1.0, 11.0, 11.0, 15.0), new ShapeBox(7.0, 12.0, 5.0, 9.0, 13.0, 9.0), new ShapeBox(7.0, 3.0, 5.0, 9.0, 4.0, 9.0), new ShapeBox(11.005, 6.5, 4.0, 12.005, 9.5, 11.0), new ShapeBox(3.995, 6.5, 4.0, 4.995, 9.5, 11.0), new ShapeBox(4.0, 4.0, 16.0, 12.0, 12.0, 17.0), new ShapeBox(11.5, 7.5, 8.0, 12.5, 8.5, 9.0), new ShapeBox(3.5, 7.5, 8.0, 4.5, 8.5, 9.0), new ShapeBox(3.5, 7.5, 6.0, 4.5, 8.5, 7.0), new ShapeBox(11.5, 7.5, 6.0, 12.5, 8.5, 7.0))),
            Map.entry("mek:metallurgic_infuser", List.of(new ShapeBox(11.0, 11.0, 5.0, 12.0, 16.0, 8.0), new ShapeBox(4.0, 11.0, 5.0, 5.0, 16.0, 8.0), new ShapeBox(2.0, 7.0, 2.0, 14.0, 8.0, 8.0), new ShapeBox(0.0, 0.0, 0.0, 16.0, 4.0, 16.0), new ShapeBox(0.0, 4.0, 8.0, 16.0, 16.0, 16.0), new ShapeBox(1.0, 12.0, 1.0, 15.0, 15.0, 8.0), new ShapeBox(1.0, 8.0, 1.0, 15.0, 11.0, 8.0), new ShapeBox(1.0, 4.0, 1.0, 15.0, 7.0, 8.0), new ShapeBox(13.0, 11.0, 2.0, 14.0, 12.0, 3.0), new ShapeBox(2.0, 11.0, 2.0, 3.0, 12.0, 3.0))),
            Map.entry("mek:metallurgic_infuser_factory_basic", List.of(new ShapeBox(0.0, 0.0, 4.0, 16.0, 4.0, 16.0), new ShapeBox(2.0, 4.0, 4.0, 14.0, 13.0, 14.0), new ShapeBox(0.0, 0.0, 0.0, 16.0, 16.0, 4.0), new ShapeBox(4.0, 4.0, 14.0, 12.0, 12.0, 16.0), new ShapeBox(0.0, 13.0, 4.0, 16.0, 16.0, 16.0), new ShapeBox(0.0, 9.0, 4.0, 4.0, 12.0, 16.0), new ShapeBox(12.0, 9.0, 4.0, 16.0, 12.0, 16.0), new ShapeBox(0.0, 5.0, 4.0, 5.0, 8.0, 16.0), new ShapeBox(11.0, 5.0, 4.0, 16.0, 8.0, 16.0))),
            Map.entry("mek:metallurgic_infuser_factory_advanced", List.of(new ShapeBox(0.0, 0.0, 4.0, 16.0, 4.0, 16.0), new ShapeBox(2.0, 4.0, 4.0, 14.0, 13.0, 14.0), new ShapeBox(0.0, 0.0, 0.0, 16.0, 16.0, 4.0), new ShapeBox(4.0, 4.0, 14.0, 12.0, 12.0, 16.0), new ShapeBox(0.0, 13.0, 4.0, 16.0, 16.0, 16.0), new ShapeBox(0.0, 9.0, 4.0, 4.0, 12.0, 16.0), new ShapeBox(12.0, 9.0, 4.0, 16.0, 12.0, 16.0), new ShapeBox(0.0, 5.0, 4.0, 5.0, 8.0, 16.0), new ShapeBox(11.0, 5.0, 4.0, 16.0, 8.0, 16.0))),
            Map.entry("mek:metallurgic_infuser_factory_elite", List.of(new ShapeBox(0.0, 0.0, 4.0, 16.0, 4.0, 16.0), new ShapeBox(2.0, 4.0, 4.0, 14.0, 13.0, 14.0), new ShapeBox(0.0, 0.0, 0.0, 16.0, 16.0, 4.0), new ShapeBox(4.0, 4.0, 14.0, 12.0, 12.0, 16.0), new ShapeBox(0.0, 13.0, 4.0, 16.0, 16.0, 16.0), new ShapeBox(0.0, 9.0, 4.0, 4.0, 12.0, 16.0), new ShapeBox(12.0, 9.0, 4.0, 16.0, 12.0, 16.0), new ShapeBox(0.0, 5.0, 4.0, 5.0, 8.0, 16.0), new ShapeBox(11.0, 5.0, 4.0, 16.0, 8.0, 16.0))),
            Map.entry("mek:metallurgic_infuser_factory_ultimate", List.of(new ShapeBox(0.0, 0.0, 4.0, 16.0, 4.0, 16.0), new ShapeBox(2.0, 4.0, 4.0, 14.0, 13.0, 14.0), new ShapeBox(0.0, 0.0, 0.0, 16.0, 16.0, 4.0), new ShapeBox(4.0, 4.0, 14.0, 12.0, 12.0, 16.0), new ShapeBox(0.0, 13.0, 4.0, 16.0, 16.0, 16.0), new ShapeBox(0.0, 9.0, 4.0, 4.0, 12.0, 16.0), new ShapeBox(12.0, 9.0, 4.0, 16.0, 12.0, 16.0), new ShapeBox(0.0, 5.0, 4.0, 5.0, 8.0, 16.0), new ShapeBox(11.0, 5.0, 4.0, 16.0, 8.0, 16.0))),
            Map.entry("mek:osmium_compressor_factory_basic", List.of(new ShapeBox(0.0, 0.0, 0.0, 16.0, 16.0, 4.0), new ShapeBox(0.0, 0.0, 12.0, 16.0, 16.0, 16.0), new ShapeBox(0.0, 12.0, 4.0, 16.0, 16.0, 11.0), new ShapeBox(0.0, 0.0, 4.0, 16.0, 4.0, 11.0), new ShapeBox(1.0, 0.0, 4.0, 15.0, 15.0, 12.0), new ShapeBox(0.0, 5.0, 5.0, 16.0, 11.0, 12.0), new ShapeBox(4.0, 4.0, 14.0, 12.0, 12.0, 16.0))),
            Map.entry("mek:osmium_compressor_factory_advanced", List.of(new ShapeBox(0.0, 0.0, 0.0, 16.0, 16.0, 4.0), new ShapeBox(0.0, 0.0, 12.0, 16.0, 16.0, 16.0), new ShapeBox(0.0, 12.0, 4.0, 16.0, 16.0, 11.0), new ShapeBox(0.0, 0.0, 4.0, 16.0, 4.0, 11.0), new ShapeBox(1.0, 0.0, 4.0, 15.0, 15.0, 12.0), new ShapeBox(0.0, 5.0, 5.0, 16.0, 11.0, 12.0), new ShapeBox(4.0, 4.0, 14.0, 12.0, 12.0, 16.0))),
            Map.entry("mek:osmium_compressor_factory_elite", List.of(new ShapeBox(0.0, 0.0, 0.0, 16.0, 16.0, 4.0), new ShapeBox(0.0, 0.0, 12.0, 16.0, 16.0, 16.0), new ShapeBox(0.0, 12.0, 4.0, 16.0, 16.0, 11.0), new ShapeBox(0.0, 0.0, 4.0, 16.0, 4.0, 11.0), new ShapeBox(1.0, 0.0, 4.0, 15.0, 15.0, 12.0), new ShapeBox(0.0, 5.0, 5.0, 16.0, 11.0, 12.0), new ShapeBox(4.0, 4.0, 14.0, 12.0, 12.0, 16.0))),
            Map.entry("mek:osmium_compressor_factory_ultimate", List.of(new ShapeBox(0.0, 0.0, 0.0, 16.0, 16.0, 4.0), new ShapeBox(0.0, 0.0, 12.0, 16.0, 16.0, 16.0), new ShapeBox(0.0, 12.0, 4.0, 16.0, 16.0, 11.0), new ShapeBox(0.0, 0.0, 4.0, 16.0, 4.0, 11.0), new ShapeBox(1.0, 0.0, 4.0, 15.0, 15.0, 12.0), new ShapeBox(0.0, 5.0, 5.0, 16.0, 11.0, 12.0), new ShapeBox(4.0, 4.0, 14.0, 12.0, 12.0, 16.0))),
            Map.entry("mek:personal_chest", List.of(new ShapeBox(1.0, 0.0, 1.0, 15.0, 14.0, 15.0), new ShapeBox(7.0, 7.0, 0.0, 9.0, 11.0, 1.0))),
            Map.entry("mek:pigment_mixer", List.of(new ShapeBox(0.0, 0.0, 0.0, 16.0, 16.0, 16.0), new ShapeBox(5.0, 27.0, 4.0, 11.0, 28.0, 16.0), new ShapeBox(6.0, 28.0, 5.0, 10.0, 30.0, 9.0), new ShapeBox(1.0, 16.0, 1.0, 15.0, 18.0, 13.0), new ShapeBox(2.0, 16.0, 12.0, 14.0, 20.0, 16.0), new ShapeBox(6.0, 20.0, 13.0, 10.0, 30.0, 15.0), new ShapeBox(5.0, 30.0, 4.0, 11.0, 32.0, 16.0), new ShapeBox(7.0, 18.0, 6.0, 9.0, 27.0, 8.0))),
            Map.entry("mek:precision_sawmill_factory_basic", List.of(new ShapeBox(0.0, 0.0, 0.0, 16.0, 16.0, 4.0), new ShapeBox(4.0, 4.0, 14.0, 12.0, 12.0, 16.0), new ShapeBox(0.0, 5.0, 5.0, 7.0, 16.0, 16.0), new ShapeBox(9.0, 5.0, 5.0, 16.0, 16.0, 16.0), new ShapeBox(0.0, 0.0, 4.0, 16.0, 4.0, 16.0), new ShapeBox(1.0, 4.0, 4.0, 15.0, 15.0, 15.0))),
            Map.entry("mek:precision_sawmill_factory_advanced", List.of(new ShapeBox(0.0, 0.0, 0.0, 16.0, 16.0, 4.0), new ShapeBox(4.0, 4.0, 14.0, 12.0, 12.0, 16.0), new ShapeBox(0.0, 5.0, 5.0, 7.0, 16.0, 16.0), new ShapeBox(9.0, 5.0, 5.0, 16.0, 16.0, 16.0), new ShapeBox(0.0, 0.0, 4.0, 16.0, 4.0, 16.0), new ShapeBox(1.0, 4.0, 4.0, 15.0, 15.0, 15.0))),
            Map.entry("mek:precision_sawmill_factory_elite", List.of(new ShapeBox(0.0, 0.0, 0.0, 16.0, 16.0, 4.0), new ShapeBox(4.0, 4.0, 14.0, 12.0, 12.0, 16.0), new ShapeBox(0.0, 5.0, 5.0, 7.0, 16.0, 16.0), new ShapeBox(9.0, 5.0, 5.0, 16.0, 16.0, 16.0), new ShapeBox(0.0, 0.0, 4.0, 16.0, 4.0, 16.0), new ShapeBox(1.0, 4.0, 4.0, 15.0, 15.0, 15.0))),
            Map.entry("mek:precision_sawmill_factory_ultimate", List.of(new ShapeBox(0.0, 0.0, 0.0, 16.0, 16.0, 4.0), new ShapeBox(4.0, 4.0, 14.0, 12.0, 12.0, 16.0), new ShapeBox(0.0, 5.0, 5.0, 7.0, 16.0, 16.0), new ShapeBox(9.0, 5.0, 5.0, 16.0, 16.0, 16.0), new ShapeBox(0.0, 0.0, 4.0, 16.0, 4.0, 16.0), new ShapeBox(1.0, 4.0, 4.0, 15.0, 15.0, 15.0))),
            Map.entry("mek:pressurized_reaction_chamber", List.of(new ShapeBox(0.0, 4.0, 6.0, 16.0, 16.0, 16.0), new ShapeBox(10.0, 4.0, 1.0, 12.0, 15.0, 6.0), new ShapeBox(13.0, 4.0, 1.0, 15.0, 15.0, 6.0), new ShapeBox(1.0, 4.0, 2.0, 10.0, 14.0, 6.0), new ShapeBox(0.0, 0.0, 0.0, 16.0, 4.0, 16.0), new ShapeBox(12.0, 13.0, 2.0, 13.0, 14.0, 6.0), new ShapeBox(12.0, 11.0, 2.0, 13.0, 12.0, 6.0), new ShapeBox(12.0, 9.0, 2.0, 13.0, 10.0, 6.0), new ShapeBox(12.0, 7.0, 2.0, 13.0, 8.0, 6.0), new ShapeBox(12.0, 5.0, 2.0, 13.0, 6.0, 6.0))),
            Map.entry("mek:purification_chamber_factory_basic", List.of(new ShapeBox(0.0, 12.0, 4.0, 16.0, 16.0, 16.0), new ShapeBox(1.0, 0.0, 4.0, 15.0, 15.0, 15.0), new ShapeBox(0.0, 0.0, 0.0, 16.0, 16.0, 4.0), new ShapeBox(4.0, 4.0, 14.0, 12.0, 12.0, 16.0), new ShapeBox(11.0, 1.0, 5.0, 16.0, 12.0, 10.0), new ShapeBox(0.0, 1.0, 5.0, 5.0, 12.0, 10.0), new ShapeBox(0.0, 1.0, 11.0, 16.0, 12.0, 16.0))),
            Map.entry("mek:purification_chamber_factory_advanced", List.of(new ShapeBox(0.0, 12.0, 4.0, 16.0, 16.0, 16.0), new ShapeBox(1.0, 0.0, 4.0, 15.0, 15.0, 15.0), new ShapeBox(0.0, 0.0, 0.0, 16.0, 16.0, 4.0), new ShapeBox(4.0, 4.0, 14.0, 12.0, 12.0, 16.0), new ShapeBox(11.0, 1.0, 5.0, 16.0, 12.0, 10.0), new ShapeBox(0.0, 1.0, 5.0, 5.0, 12.0, 10.0), new ShapeBox(0.0, 1.0, 11.0, 16.0, 12.0, 16.0))),
            Map.entry("mek:purification_chamber_factory_elite", List.of(new ShapeBox(0.0, 12.0, 4.0, 16.0, 16.0, 16.0), new ShapeBox(1.0, 0.0, 4.0, 15.0, 15.0, 15.0), new ShapeBox(0.0, 0.0, 0.0, 16.0, 16.0, 4.0), new ShapeBox(4.0, 4.0, 14.0, 12.0, 12.0, 16.0), new ShapeBox(11.0, 1.0, 5.0, 16.0, 12.0, 10.0), new ShapeBox(0.0, 1.0, 5.0, 5.0, 12.0, 10.0), new ShapeBox(0.0, 1.0, 11.0, 16.0, 12.0, 16.0))),
            Map.entry("mek:purification_chamber_factory_ultimate", List.of(new ShapeBox(0.0, 12.0, 4.0, 16.0, 16.0, 16.0), new ShapeBox(1.0, 0.0, 4.0, 15.0, 15.0, 15.0), new ShapeBox(0.0, 0.0, 0.0, 16.0, 16.0, 4.0), new ShapeBox(4.0, 4.0, 14.0, 12.0, 12.0, 16.0), new ShapeBox(11.0, 1.0, 5.0, 16.0, 12.0, 10.0), new ShapeBox(0.0, 1.0, 5.0, 5.0, 12.0, 10.0), new ShapeBox(0.0, 1.0, 11.0, 16.0, 12.0, 16.0))),
            Map.entry("mek:qio_dashboard", List.of(new ShapeBox(1.0, 15.0, 1.0, 15.0, 16.0, 15.0))),
            Map.entry("mek:qio_drive_array", List.of(new ShapeBox(0.0, 12.0, 0.0, 16.0, 16.0, 9.0), new ShapeBox(0.0, 0.0, 9.0, 16.0, 16.0, 16.0), new ShapeBox(0.0, 6.0, 0.0, 16.0, 9.0, 9.0), new ShapeBox(0.0, 0.0, 0.0, 16.0, 3.0, 9.0), new ShapeBox(0.0, 3.0, 0.0, 1.0, 6.0, 1.0), new ShapeBox(0.0, 9.0, 0.0, 1.0, 12.0, 1.0), new ShapeBox(15.0, 3.0, 0.0, 16.0, 6.0, 1.0), new ShapeBox(15.0, 9.0, 0.0, 16.0, 12.0, 1.0))),
            Map.entry("mek:qio_exporter", List.of(new ShapeBox(4.0, 0.0, 4.0, 12.0, 1.0, 12.0), new ShapeBox(5.0, 4.0, 5.0, 11.0, 5.0, 6.0), new ShapeBox(5.0, 4.0, 10.0, 11.0, 5.0, 11.0), new ShapeBox(5.0, 4.0, 6.0, 6.0, 5.0, 10.0), new ShapeBox(10.0, 4.0, 6.0, 11.0, 5.0, 10.0), new ShapeBox(10.0, 2.0, 6.0, 11.0, 3.0, 10.0), new ShapeBox(5.0, 2.0, 6.0, 6.0, 3.0, 10.0), new ShapeBox(5.0, 2.0, 10.0, 11.0, 3.0, 11.0), new ShapeBox(5.0, 2.0, 5.0, 11.0, 3.0, 6.0), new ShapeBox(9.0, 1.0, 6.0, 10.0, 6.0, 7.0), new ShapeBox(9.0, 1.0, 9.0, 10.0, 6.0, 10.0), new ShapeBox(6.0, 1.0, 9.0, 7.0, 6.0, 10.0), new ShapeBox(6.0, 1.0, 6.0, 7.0, 6.0, 7.0), new ShapeBox(7.0, 7.0, 7.0, 9.0, 8.0, 9.0), new ShapeBox(7.0, 8.01, 7.0, 9.0, 9.01, 9.0), new ShapeBox(7.5, 1.0, 7.5, 8.5, 6.0, 8.5), new ShapeBox(5.0, 6.0, 5.0, 11.0, 7.0, 11.0), new ShapeBox(6.0, 8.0, 6.0, 10.0, 9.0, 10.0), new ShapeBox(9.0, 9.0, 9.0, 10.0, 10.0, 10.0), new ShapeBox(6.0, 9.0, 9.0, 7.0, 10.0, 10.0), new ShapeBox(6.0, 9.0, 6.0, 7.0, 10.0, 7.0), new ShapeBox(9.0, 9.0, 6.0, 10.0, 10.0, 7.0))),
            Map.entry("mek:qio_importer", List.of(new ShapeBox(4.0, 0.0, 4.0, 12.0, 1.0, 12.0), new ShapeBox(5.0, 4.0, 5.0, 11.0, 5.0, 6.0), new ShapeBox(5.0, 4.0, 10.0, 11.0, 5.0, 11.0), new ShapeBox(5.0, 4.0, 6.0, 6.0, 5.0, 10.0), new ShapeBox(10.0, 4.0, 6.0, 11.0, 5.0, 10.0), new ShapeBox(10.0, 2.0, 6.0, 11.0, 3.0, 10.0), new ShapeBox(5.0, 2.0, 6.0, 6.0, 3.0, 10.0), new ShapeBox(5.0, 2.0, 10.0, 11.0, 3.0, 11.0), new ShapeBox(5.0, 2.0, 5.0, 11.0, 3.0, 6.0), new ShapeBox(9.0, 1.0, 6.0, 10.0, 6.0, 7.0), new ShapeBox(9.0, 1.0, 9.0, 10.0, 6.0, 10.0), new ShapeBox(6.0, 1.0, 9.0, 7.0, 6.0, 10.0), new ShapeBox(6.0, 1.0, 6.0, 7.0, 6.0, 7.0), new ShapeBox(9.0, 8.0, 9.0, 10.0, 9.0, 10.0), new ShapeBox(6.0, 9.0, 6.0, 7.0, 10.0, 7.0), new ShapeBox(7.5, 1.0, 7.5, 8.5, 6.0, 8.5), new ShapeBox(5.0, 6.0, 5.0, 11.0, 7.0, 11.0), new ShapeBox(9.0, 7.0, 9.0, 10.0, 8.0, 10.0), new ShapeBox(6.0, 7.0, 6.0, 7.0, 9.0, 7.0))),
            Map.entry("mek:qio_redstone_adapter", List.of(new ShapeBox(4.0, 0.0, 4.0, 12.0, 1.0, 12.0), new ShapeBox(5.0, 2.0, 5.0, 11.0, 3.0, 6.0), new ShapeBox(5.0, 2.0, 10.0, 11.0, 3.0, 11.0), new ShapeBox(5.0, 2.0, 6.0, 6.0, 3.0, 10.0), new ShapeBox(10.0, 2.0, 6.0, 11.0, 3.0, 10.0), new ShapeBox(9.0, 1.0, 6.0, 10.0, 4.0, 7.0), new ShapeBox(9.0, 1.0, 9.0, 10.0, 4.0, 10.0), new ShapeBox(6.0, 1.0, 9.0, 7.0, 4.0, 10.0), new ShapeBox(6.0, 1.0, 6.0, 7.0, 4.0, 7.0), new ShapeBox(7.5, 1.0, 7.5, 8.5, 4.0, 8.5), new ShapeBox(5.0, 4.0, 5.0, 11.0, 5.0, 11.0), new ShapeBox(6.0, 8.0, 6.0, 10.0, 9.0, 10.0), new ShapeBox(7.0, 5.0, 7.0, 9.0, 11.0, 9.0))),
            Map.entry("mek:radioactive_waste_barrel", List.of(new ShapeBox(2.0, 0.0, 2.0, 14.0, 16.0, 14.0))),
            Map.entry("mek:resistive_heater", List.of(new ShapeBox(0.0, 4.0, 4.0, 1.0, 12.0, 12.0), new ShapeBox(15.0, 4.0, 4.0, 16.0, 12.0, 12.0), new ShapeBox(4.0, 13.0, 1.5, 5.0, 14.0, 14.5), new ShapeBox(6.0, 13.0, 1.5, 7.0, 14.0, 14.5), new ShapeBox(9.0, 13.0, 1.5, 10.0, 14.0, 14.5), new ShapeBox(11.0, 13.0, 1.5, 12.0, 14.0, 14.5), new ShapeBox(0.0, 0.0, 0.0, 16.0, 7.0, 16.0), new ShapeBox(13.0, 7.0, 0.0, 16.0, 16.0, 16.0), new ShapeBox(0.0, 7.0, 0.0, 3.0, 16.0, 16.0), new ShapeBox(3.0, 6.0, 1.0, 13.0, 15.0, 2.0), new ShapeBox(3.0, 6.0, 3.0, 13.0, 15.0, 4.0), new ShapeBox(3.0, 6.0, 5.0, 13.0, 15.0, 6.0), new ShapeBox(3.0, 6.0, 7.0, 13.0, 15.0, 8.0), new ShapeBox(3.0, 6.0, 8.0, 13.0, 15.0, 9.0), new ShapeBox(3.0, 6.0, 10.0, 13.0, 15.0, 11.0), new ShapeBox(3.0, 6.0, 12.0, 13.0, 15.0, 13.0), new ShapeBox(3.0, 6.0, 14.0, 13.0, 15.0, 15.0))),
            Map.entry("mek:rotary_condensentrator", List.of(new ShapeBox(0.0, 0.0, 0.0, 16.0, 5.0, 16.0), new ShapeBox(0.0, 15.0, 0.0, 16.0, 16.0, 16.0), new ShapeBox(0.0, 13.0, 0.0, 16.0, 14.0, 16.0), new ShapeBox(7.5, 11.0, 7.5, 8.5, 13.0, 8.5), new ShapeBox(4.0, 14.0, 4.0, 12.0, 15.0, 12.0), new ShapeBox(7.0, 5.0, 5.0, 9.0, 11.0, 11.0), new ShapeBox(9.0, 5.0, 1.0, 15.0, 13.0, 15.0), new ShapeBox(1.0, 5.0, 1.0, 7.0, 13.0, 15.0), new ShapeBox(15.0, 4.0, 4.0, 16.0, 12.0, 12.0), new ShapeBox(0.0, 3.0, 3.0, 1.0, 13.0, 13.0), new ShapeBox(14.0, 14.0, 14.0, 15.0, 15.0, 15.0), new ShapeBox(14.0, 14.0, 1.0, 15.0, 15.0, 2.0), new ShapeBox(1.0, 14.0, 1.0, 2.0, 15.0, 2.0), new ShapeBox(1.0, 14.0, 14.0, 2.0, 15.0, 15.0), new ShapeBox(7.0, 11.0, 2.0, 9.0, 12.0, 3.0), new ShapeBox(7.0, 9.0, 2.0, 9.0, 10.0, 3.0), new ShapeBox(7.0, 7.0, 2.0, 9.0, 8.0, 3.0), new ShapeBox(7.0, 5.0, 2.0, 9.0, 6.0, 3.0), new ShapeBox(7.0, 7.0, 13.0, 9.0, 8.0, 14.0), new ShapeBox(7.0, 9.0, 13.0, 9.0, 10.0, 14.0), new ShapeBox(7.0, 11.0, 13.0, 9.0, 12.0, 14.0), new ShapeBox(7.0, 5.0, 13.0, 9.0, 6.0, 14.0))),
            Map.entry("mek:solar_neutron_activator", List.of(new ShapeBox(6.0, 27.0, 4.0, 10.0, 28.0, 14.0), new ShapeBox(7.0, 26.0, 8.0, 9.0, 27.0, 14.0), new ShapeBox(0.0, 0.0, 0.0, 16.0, 4.0, 16.0), new ShapeBox(0.0, 5.0, 0.0, 16.0, 14.0, 16.0), new ShapeBox(2.0, 4.0, 2.0, 14.0, 5.0, 14.0), new ShapeBox(1.0, 4.0, 3.0, 2.0, 5.0, 5.0), new ShapeBox(1.0, 4.0, 6.0, 2.0, 5.0, 10.0), new ShapeBox(1.0, 4.0, 11.0, 2.0, 5.0, 13.0), new ShapeBox(14.0, 4.0, 11.0, 15.0, 5.0, 13.0), new ShapeBox(14.0, 4.0, 6.0, 15.0, 5.0, 10.0), new ShapeBox(14.0, 4.0, 3.0, 15.0, 5.0, 5.0), new ShapeBox(6.0, 4.0, 14.0, 10.0, 5.0, 16.0), new ShapeBox(4.0, 4.0, 0.0, 12.0, 12.0, 1.0), new ShapeBox(6.0, 14.0, 14.0, 10.0, 28.0, 16.0), new ShapeBox(5.0, 14.0, 3.0, 11.0, 15.0, 9.0), new ShapeBox(7.0, 14.0, 9.0, 9.0, 15.0, 14.0), new ShapeBox(6.0, 14.0, 12.0, 10.0, 16.0, 13.0), new ShapeBox(6.0, 14.0, 10.0, 10.0, 16.0, 11.0), new ShapeBox(5.0, 14.0, 10.0, 6.0, 15.0, 11.0), new ShapeBox(5.0, 14.0, 12.0, 6.0, 15.0, 13.0), new ShapeBox(10.0, 14.0, 10.0, 11.0, 15.0, 11.0), new ShapeBox(10.0, 14.0, 12.0, 11.0, 15.0, 13.0), new ShapeBox(7.0, 25.0, 5.0, 9.0, 26.0, 7.0), new ShapeBox(6.0, 26.0, 4.0, 10.0, 27.0, 8.0), new ShapeBox(4.0, 28.0, 0.0, 12.0, 29.0, 16.0), new ShapeBox(12.0, 29.0, 0.0, 14.0, 30.0, 16.0), new ShapeBox(14.0, 30.0, 0.0, 16.5, 31.0, 16.0), new ShapeBox(2.0, 29.0, 0.0, 4.0, 30.0, 16.0), new ShapeBox(-0.5, 30.0, 0.0, 2.0, 31.0, 16.0))),
            Map.entry("mek:ultimate_chemical_tank", List.of(new ShapeBox(6.0, 13.0, 4.0, 10.0, 14.0, 5.0), new ShapeBox(10.0, 13.0, 4.0, 12.0, 16.0, 5.0), new ShapeBox(11.0, 13.0, 5.0, 12.0, 16.0, 11.0), new ShapeBox(4.0, 13.0, 11.0, 12.0, 16.0, 12.0), new ShapeBox(4.0, 13.0, 5.0, 5.0, 16.0, 11.0), new ShapeBox(4.0, 13.0, 4.0, 6.0, 16.0, 5.0), new ShapeBox(3.0, 1.0, 3.0, 13.0, 13.0, 13.0), new ShapeBox(4.0, 0.0, 4.0, 12.0, 1.0, 12.0), new ShapeBox(6.5, 14.0, 6.5, 9.5, 15.0, 9.5), new ShapeBox(7.0, 12.0, 7.0, 9.0, 14.0, 9.0))),
            Map.entry("mek:ultimate_fluid_tank", List.of(new ShapeBox(2.0, 0.0, 2.0, 14.0, 16.0, 14.0))));

    /** 发电机方块实体用的隐藏渲染物品；缺一个，对应方块的 ItemDisplay 就没模型。*/
    public static final List<String> GENERATOR_RENDER_ITEMS = List.of(
            "mek:render/advanced_solar_generator",
            "mek:render/bio_generator",
            "mek:render/gas_burning_generator",
            "mek:render/heat_generator",
            "mek:render/heat_generator_active",
            "mek:render/solar_generator",
            "mek:render/wind_blades",
            "mek:render/wind_generator");

    /** 非满机器方块的隐藏渲染物品（含 active 变体）。*/
    public static final List<String> MACHINE_RENDER_ITEMS = List.of(
            "mek:render/advanced_chemical_tank",
            "mek:render/advanced_energy_cube",
            "mek:render/advanced_fluid_tank",
            "mek:render/advanced_fluid_tank_inactive",
            "mek:render/advanced_fluid_tank_active",
            "mek:render/antiprotonic_nucleosynthesizer",
            "mek:render/basic_chemical_tank",
            "mek:render/basic_energy_cube",
            "mek:render/basic_fluid_tank",
            "mek:render/basic_fluid_tank_inactive",
            "mek:render/basic_fluid_tank_active",
            "mek:render/chargepad",
            "mek:render/chemical_crystallizer",
            "mek:render/chemical_dissolution_chamber",
            "mek:render/chemical_infuser",
            "mek:render/chemical_injection_chamber_factory_basic",
            "mek:render/chemical_injection_chamber_factory_basic_inactive",
            "mek:render/chemical_injection_chamber_factory_basic_active",
            "mek:render/chemical_injection_chamber_factory_advanced",
            "mek:render/chemical_injection_chamber_factory_advanced_inactive",
            "mek:render/chemical_injection_chamber_factory_advanced_active",
            "mek:render/chemical_injection_chamber_factory_elite",
            "mek:render/chemical_injection_chamber_factory_elite_inactive",
            "mek:render/chemical_injection_chamber_factory_elite_active",
            "mek:render/chemical_injection_chamber_factory_ultimate",
            "mek:render/chemical_injection_chamber_factory_ultimate_inactive",
            "mek:render/chemical_injection_chamber_factory_ultimate_active",
            "mek:render/chemical_oxidizer",
            "mek:render/chemical_washer",
            "mek:render/combiner_factory_basic",
            "mek:render/combiner_factory_basic_inactive",
            "mek:render/combiner_factory_basic_active",
            "mek:render/combiner_factory_advanced",
            "mek:render/combiner_factory_advanced_inactive",
            "mek:render/combiner_factory_advanced_active",
            "mek:render/combiner_factory_elite",
            "mek:render/combiner_factory_elite_inactive",
            "mek:render/combiner_factory_elite_active",
            "mek:render/combiner_factory_ultimate",
            "mek:render/combiner_factory_ultimate_inactive",
            "mek:render/combiner_factory_ultimate_active",
            "mek:render/crusher_factory_basic",
            "mek:render/crusher_factory_basic_inactive",
            "mek:render/crusher_factory_basic_active",
            "mek:render/crusher_factory_advanced",
            "mek:render/crusher_factory_advanced_inactive",
            "mek:render/crusher_factory_advanced_active",
            "mek:render/crusher_factory_elite",
            "mek:render/crusher_factory_elite_inactive",
            "mek:render/crusher_factory_elite_active",
            "mek:render/crusher_factory_ultimate",
            "mek:render/crusher_factory_ultimate_inactive",
            "mek:render/crusher_factory_ultimate_active",
            "mek:render/digital_miner",
            "mek:render/digital_miner_inactive",
            "mek:render/digital_miner_active",
            "mek:render/electric_pump",
            "mek:render/electrolytic_separator",
            "mek:render/elite_chemical_tank",
            "mek:render/elite_energy_cube",
            "mek:render/elite_fluid_tank",
            "mek:render/elite_fluid_tank_inactive",
            "mek:render/elite_fluid_tank_active",
            "mek:render/energized_smelter_factory_basic",
            "mek:render/energized_smelter_factory_basic_inactive",
            "mek:render/energized_smelter_factory_basic_active",
            "mek:render/energized_smelter_factory_advanced",
            "mek:render/energized_smelter_factory_advanced_inactive",
            "mek:render/energized_smelter_factory_advanced_active",
            "mek:render/energized_smelter_factory_elite",
            "mek:render/energized_smelter_factory_elite_inactive",
            "mek:render/energized_smelter_factory_elite_active",
            "mek:render/energized_smelter_factory_ultimate",
            "mek:render/energized_smelter_factory_ultimate_inactive",
            "mek:render/energized_smelter_factory_ultimate_active",
            "mek:render/enrichment_chamber_factory_basic",
            "mek:render/enrichment_chamber_factory_basic_inactive",
            "mek:render/enrichment_chamber_factory_basic_active",
            "mek:render/enrichment_chamber_factory_advanced",
            "mek:render/enrichment_chamber_factory_advanced_inactive",
            "mek:render/enrichment_chamber_factory_advanced_active",
            "mek:render/enrichment_chamber_factory_elite",
            "mek:render/enrichment_chamber_factory_elite_inactive",
            "mek:render/enrichment_chamber_factory_elite_active",
            "mek:render/enrichment_chamber_factory_ultimate",
            "mek:render/enrichment_chamber_factory_ultimate_inactive",
            "mek:render/enrichment_chamber_factory_ultimate_active",
            "mek:render/isotopic_centrifuge",
            "mek:render/laser",
            "mek:render/laser_amplifier",
            "mek:render/logistical_sorter",
            "mek:render/logistical_sorter_inactive",
            "mek:render/logistical_sorter_active",
            "mek:render/metallurgic_infuser",
            "mek:render/metallurgic_infuser_factory_basic",
            "mek:render/metallurgic_infuser_factory_basic_inactive",
            "mek:render/metallurgic_infuser_factory_basic_active",
            "mek:render/metallurgic_infuser_factory_advanced",
            "mek:render/metallurgic_infuser_factory_advanced_inactive",
            "mek:render/metallurgic_infuser_factory_advanced_active",
            "mek:render/metallurgic_infuser_factory_elite",
            "mek:render/metallurgic_infuser_factory_elite_inactive",
            "mek:render/metallurgic_infuser_factory_elite_active",
            "mek:render/metallurgic_infuser_factory_ultimate",
            "mek:render/metallurgic_infuser_factory_ultimate_inactive",
            "mek:render/metallurgic_infuser_factory_ultimate_active",
            "mek:render/osmium_compressor_factory_basic",
            "mek:render/osmium_compressor_factory_basic_inactive",
            "mek:render/osmium_compressor_factory_basic_active",
            "mek:render/osmium_compressor_factory_advanced",
            "mek:render/osmium_compressor_factory_advanced_inactive",
            "mek:render/osmium_compressor_factory_advanced_active",
            "mek:render/osmium_compressor_factory_elite",
            "mek:render/osmium_compressor_factory_elite_inactive",
            "mek:render/osmium_compressor_factory_elite_active",
            "mek:render/osmium_compressor_factory_ultimate",
            "mek:render/osmium_compressor_factory_ultimate_inactive",
            "mek:render/osmium_compressor_factory_ultimate_active",
            "mek:render/personal_chest",
            "mek:render/pigment_mixer",
            "mek:render/pigment_mixer_inactive",
            "mek:render/pigment_mixer_active",
            "mek:render/precision_sawmill_factory_basic",
            "mek:render/precision_sawmill_factory_basic_inactive",
            "mek:render/precision_sawmill_factory_basic_active",
            "mek:render/precision_sawmill_factory_advanced",
            "mek:render/precision_sawmill_factory_advanced_inactive",
            "mek:render/precision_sawmill_factory_advanced_active",
            "mek:render/precision_sawmill_factory_elite",
            "mek:render/precision_sawmill_factory_elite_inactive",
            "mek:render/precision_sawmill_factory_elite_active",
            "mek:render/precision_sawmill_factory_ultimate",
            "mek:render/precision_sawmill_factory_ultimate_inactive",
            "mek:render/precision_sawmill_factory_ultimate_active",
            "mek:render/pressurized_reaction_chamber",
            "mek:render/purification_chamber_factory_basic",
            "mek:render/purification_chamber_factory_basic_inactive",
            "mek:render/purification_chamber_factory_basic_active",
            "mek:render/purification_chamber_factory_advanced",
            "mek:render/purification_chamber_factory_advanced_inactive",
            "mek:render/purification_chamber_factory_advanced_active",
            "mek:render/purification_chamber_factory_elite",
            "mek:render/purification_chamber_factory_elite_inactive",
            "mek:render/purification_chamber_factory_elite_active",
            "mek:render/purification_chamber_factory_ultimate",
            "mek:render/purification_chamber_factory_ultimate_inactive",
            "mek:render/purification_chamber_factory_ultimate_active",
            "mek:render/qio_dashboard",
            "mek:render/qio_dashboard_inactive",
            "mek:render/qio_dashboard_active",
            "mek:render/qio_drive_array",
            "mek:render/qio_drive_array_inactive",
            "mek:render/qio_drive_array_active",
            "mek:render/qio_exporter",
            "mek:render/qio_exporter_inactive",
            "mek:render/qio_exporter_active",
            "mek:render/qio_importer",
            "mek:render/qio_importer_inactive",
            "mek:render/qio_importer_active",
            "mek:render/qio_redstone_adapter",
            "mek:render/radioactive_waste_barrel",
            "mek:render/resistive_heater",
            "mek:render/resistive_heater_inactive",
            "mek:render/resistive_heater_active",
            "mek:render/rotary_condensentrator",
            "mek:render/solar_neutron_activator",
            "mek:render/solar_neutron_activator_inactive",
            "mek:render/solar_neutron_activator_active",
            "mek:render/ultimate_chemical_tank",
            "mek:render/ultimate_energy_cube",
            "mek:render/ultimate_fluid_tank",
            "mek:render/ultimate_fluid_tank_inactive",
            "mek:render/ultimate_fluid_tank_active");

    /** 普通资源方块，无机器控制器。*/
    public static final List<String> RESOURCE_BLOCKS = List.of(
            "mek:block_bio_fuel",
            "mek:block_bronze",
            "mek:block_charcoal",
            "mek:block_fluorite",
            "mek:block_lead",
            "mek:block_osmium",
            "mek:block_raw_lead",
            "mek:block_raw_osmium",
            "mek:block_raw_tin",
            "mek:block_raw_uranium",
            "mek:block_refined_glowstone",
            "mek:block_refined_obsidian",
            "mek:block_salt",
            "mek:block_steel",
            "mek:block_tin",
            "mek:block_uranium",
            "mek:deepslate_fluorite_ore",
            "mek:deepslate_lead_ore",
            "mek:deepslate_osmium_ore",
            "mek:deepslate_tin_ore",
            "mek:deepslate_uranium_ore",
            "mek:fluorite_ore",
            "mek:lead_ore",
            "mek:osmium_ore",
            "mek:tin_ore",
            "mek:uranium_ore"
    );

    /** 本批生成的 CE 物品 id 清单（注册自检逐条查）。*/
    public static final List<String> ITEMS = List.of(
            "mek:advanced_bin",
            "mek:advanced_chemical_tank",
            "mek:advanced_control_circuit",
            "mek:advanced_control_circuit",
            "mek:advanced_energy_cube",
            "mek:advanced_fluid_tank",
            "mek:advanced_induction_cell",
            "mek:advanced_induction_provider",
            "mek:advanced_logistical_transporter",
            "mek:advanced_mechanical_pipe",
            "mek:advanced_pressurized_tube",
            "mek:advanced_solar_generator",
            "mek:advanced_thermodynamic_conductor",
            "mek:advanced_universal_cable",
            "mek:alloy_atomic",
            "mek:alloy_atomic",
            "mek:alloy_infused",
            "mek:alloy_reinforced",
            "mek:alloy_reinforced",
            "mek:anchor_upgrade",
            "mek:antiprotonic_nucleosynthesizer",
            "mek:basic_bin",
            "mek:basic_chemical_tank",
            "mek:basic_control_circuit",
            "mek:basic_energy_cube",
            "mek:basic_fluid_tank",
            "mek:basic_induction_cell",
            "mek:basic_induction_provider",
            "mek:basic_logistical_transporter",
            "mek:basic_mechanical_pipe",
            "mek:basic_pressurized_tube",
            "mek:basic_thermodynamic_conductor",
            "mek:basic_universal_cable",
            "mek:bio_fuel",
            "mek:bio_generator",
            "mek:block_bio_fuel",
            "mek:block_bronze",
            "mek:block_charcoal",
            "mek:block_fluorite",
            "mek:block_lead",
            "mek:block_osmium",
            "mek:block_raw_lead",
            "mek:block_raw_osmium",
            "mek:block_raw_tin",
            "mek:block_raw_uranium",
            "mek:block_refined_glowstone",
            "mek:block_refined_obsidian",
            "mek:block_salt",
            "mek:block_steel",
            "mek:block_tin",
            "mek:block_uranium",
            "mek:boiler_casing",
            "mek:boiler_valve",
            "mek:chargepad",
            "mek:chemical_crystallizer",
            "mek:chemical_dissolution_chamber",
            "mek:chemical_infuser",
            "mek:chemical_injection_chamber",
            "mek:chemical_injection_chamber_factory_advanced",
            "mek:chemical_injection_chamber_factory_basic",
            "mek:chemical_injection_chamber_factory_elite",
            "mek:chemical_injection_chamber_factory_ultimate",
            "mek:chemical_oxidizer",
            "mek:chemical_upgrade",
            "mek:chemical_washer",
            "mek:clump_copper",
            "mek:clump_gold",
            "mek:clump_iron",
            "mek:clump_lead",
            "mek:clump_osmium",
            "mek:clump_tin",
            "mek:clump_uranium",
            "mek:combiner",
            "mek:combiner_factory_advanced",
            "mek:combiner_factory_basic",
            "mek:combiner_factory_elite",
            "mek:combiner_factory_ultimate",
            "mek:configuration_card",
            "mek:configurator",
            "mek:control_rod_assembly",
            "mek:crafting_formula",
            "mek:crusher",
            "mek:crusher_factory_advanced",
            "mek:crusher_factory_basic",
            "mek:crusher_factory_elite",
            "mek:crusher_factory_ultimate",
            "mek:crystal_copper",
            "mek:crystal_gold",
            "mek:crystal_iron",
            "mek:crystal_lead",
            "mek:crystal_osmium",
            "mek:crystal_tin",
            "mek:crystal_uranium",
            "mek:deepslate_fluorite_ore",
            "mek:deepslate_lead_ore",
            "mek:deepslate_osmium_ore",
            "mek:deepslate_tin_ore",
            "mek:deepslate_uranium_ore",
            "mek:dictionary",
            "mek:digital_miner",
            "mek:dirty_dust_copper",
            "mek:dirty_dust_gold",
            "mek:dirty_dust_iron",
            "mek:dirty_dust_lead",
            "mek:dirty_dust_osmium",
            "mek:dirty_dust_tin",
            "mek:dirty_dust_uranium",
            "mek:dirty_netherite_scrap",
            "mek:dosimeter",
            "mek:dust_bronze",
            "mek:dust_charcoal",
            "mek:dust_coal",
            "mek:dust_copper",
            "mek:dust_diamond",
            "mek:dust_emerald",
            "mek:dust_fluorite",
            "mek:dust_gold",
            "mek:dust_iron",
            "mek:dust_lapis_lazuli",
            "mek:dust_lead",
            "mek:dust_lithium",
            "mek:dust_netherite",
            "mek:dust_obsidian",
            "mek:dust_osmium",
            "mek:dust_quartz",
            "mek:dust_refined_obsidian",
            "mek:dust_steel",
            "mek:dust_sulfur",
            "mek:dust_tin",
            "mek:dust_uranium",
            "mek:dye_base",
            "mek:dynamic_tank",
            "mek:dynamic_valve",
            "mek:electric_pump",
            "mek:electrolytic_core",
            "mek:electrolytic_separator",
            "mek:electromagnetic_coil",
            "mek:elite_bin",
            "mek:elite_chemical_tank",
            "mek:elite_control_circuit",
            "mek:elite_control_circuit",
            "mek:elite_energy_cube",
            "mek:elite_fluid_tank",
            "mek:elite_induction_cell",
            "mek:elite_induction_provider",
            "mek:elite_logistical_transporter",
            "mek:elite_mechanical_pipe",
            "mek:elite_pressurized_tube",
            "mek:elite_thermodynamic_conductor",
            "mek:elite_universal_cable",
            "mek:energized_smelter",
            "mek:energized_smelter_factory_advanced",
            "mek:energized_smelter_factory_basic",
            "mek:energized_smelter_factory_elite",
            "mek:energized_smelter_factory_ultimate",
            "mek:energy_tablet",
            "mek:energy_upgrade",
            "mek:enriched_carbon",
            "mek:enriched_diamond",
            "mek:enriched_diamond",
            "mek:enriched_gold",
            "mek:enriched_gold",
            "mek:enriched_iron",
            "mek:enriched_redstone",
            "mek:enriched_redstone",
            "mek:enriched_refined_obsidian",
            "mek:enriched_refined_obsidian",
            "mek:enriched_tin",
            "mek:enriched_tin",
            "mek:enrichment_chamber",
            "mek:enrichment_chamber_factory_advanced",
            "mek:enrichment_chamber_factory_basic",
            "mek:enrichment_chamber_factory_elite",
            "mek:enrichment_chamber_factory_ultimate",
            "mek:filter_upgrade",
            "mek:fission_fuel_assembly",
            "mek:fission_reactor_casing",
            "mek:fission_reactor_logic_adapter",
            "mek:fission_reactor_port",
            "mek:fluorite_gem",
            "mek:fluorite_ore",
            "mek:fuelwood_heater",
            "mek:fusion_reactor_controller",
            "mek:fusion_reactor_frame",
            "mek:fusion_reactor_logic_adapter",
            "mek:fusion_reactor_port",
            "mek:gas_burning_generator",
            "mek:gauge_dropper",
            "mek:geiger_counter",
            "mek:hdpe_pellet",
            "mek:hdpe_rod",
            "mek:hdpe_rod",
            "mek:hdpe_sheet",
            "mek:hdpe_sheet",
            "mek:hdpe_stick",
            "mek:hdpe_stick",
            "mek:heat_generator",
            "mek:hohlraum",
            "mek:induction_casing",
            "mek:induction_port",
            "mek:ingot_bronze",
            "mek:ingot_lead",
            "mek:ingot_osmium",
            "mek:ingot_refined_glowstone",
            "mek:ingot_refined_obsidian",
            "mek:ingot_steel",
            "mek:ingot_tin",
            "mek:ingot_uranium",
            "mek:isotopic_centrifuge",
            "mek:laser",
            "mek:laser_amplifier",
            "mek:laser_focus_matrix",
            "mek:lead_ore",
            "mek:logistical_sorter",
            "mek:metallurgic_infuser",
            "mek:metallurgic_infuser_factory_advanced",
            "mek:metallurgic_infuser_factory_basic",
            "mek:metallurgic_infuser_factory_elite",
            "mek:metallurgic_infuser_factory_ultimate",
            "mek:muffling_upgrade",
            "mek:network_reader",
            "mek:nugget_bronze",
            "mek:nugget_lead",
            "mek:nugget_osmium",
            "mek:nugget_refined_glowstone",
            "mek:nugget_refined_obsidian",
            "mek:nugget_steel",
            "mek:nugget_tin",
            "mek:nugget_uranium",
            "mek:osmium_compressor",
            "mek:osmium_compressor_factory_advanced",
            "mek:osmium_compressor_factory_basic",
            "mek:osmium_compressor_factory_elite",
            "mek:osmium_compressor_factory_ultimate",
            "mek:osmium_ore",
            "mek:painting_machine",
            "mek:pellet_antimatter",
            "mek:pellet_plutonium",
            "mek:pellet_polonium",
            "mek:personal_barrel",
            "mek:personal_chest",
            "mek:pigment_extractor",
            "mek:pigment_mixer",
            "mek:portable_qio_dashboard",
            "mek:precision_sawmill",
            "mek:precision_sawmill_factory_advanced",
            "mek:precision_sawmill_factory_basic",
            "mek:precision_sawmill_factory_elite",
            "mek:precision_sawmill_factory_ultimate",
            "mek:pressure_disperser",
            "mek:pressurized_reaction_chamber",
            "mek:purification_chamber",
            "mek:purification_chamber_factory_advanced",
            "mek:purification_chamber_factory_basic",
            "mek:purification_chamber_factory_elite",
            "mek:purification_chamber_factory_ultimate",
            "mek:qio_dashboard",
            "mek:qio_drive_array",
            "mek:qio_drive_base",
            "mek:qio_drive_hyper_dense",
            "mek:qio_drive_hyper_dense",
            "mek:qio_drive_supermassive",
            "mek:qio_drive_supermassive",
            "mek:qio_drive_time_dilating",
            "mek:qio_drive_time_dilating",
            "mek:qio_exporter",
            "mek:qio_importer",
            "mek:qio_redstone_adapter",
            "mek:radioactive_waste_barrel",
            "mek:raw_lead",
            "mek:raw_osmium",
            "mek:raw_tin",
            "mek:raw_uranium",
            "mek:reactor_glass",
            "mek:reprocessed_fissile_fragment",
            "mek:resistive_heater",
            "mek:robit",
            "mek:rotary_condensentrator",
            "mek:rotational_complex",
            "mek:salt",
            "mek:saturating_condenser",
            "mek:sawdust",
            "mek:shard_copper",
            "mek:shard_gold",
            "mek:shard_iron",
            "mek:shard_lead",
            "mek:shard_osmium",
            "mek:shard_tin",
            "mek:shard_uranium",
            "mek:solar_generator",
            "mek:solar_neutron_activator",
            "mek:solar_panel",
            "mek:speed_upgrade",
            "mek:sps_casing",
            "mek:sps_port",
            "mek:steel_casing",
            "mek:structural_glass",
            "mek:substrate",
            "mek:supercharged_coil",
            "mek:superheating_element",
            "mek:teleportation_core",
            "mek:teleporter",
            "mek:teleporter_frame",
            "mek:thermal_evaporation_block",
            "mek:thermal_evaporation_controller",
            "mek:thermal_evaporation_valve",
            "mek:tin_ore",
            "mek:turbine_blade",
            "mek:turbine_casing",
            "mek:turbine_rotor",
            "mek:turbine_valve",
            "mek:turbine_vent",
            "mek:ultimate_bin",
            "mek:ultimate_chemical_tank",
            "mek:ultimate_control_circuit",
            "mek:ultimate_control_circuit",
            "mek:ultimate_energy_cube",
            "mek:ultimate_fluid_tank",
            "mek:ultimate_induction_cell",
            "mek:ultimate_induction_provider",
            "mek:ultimate_logistical_transporter",
            "mek:ultimate_mechanical_pipe",
            "mek:ultimate_pressurized_tube",
            "mek:ultimate_thermodynamic_conductor",
            "mek:ultimate_universal_cable",
            "mek:upgrade_installer",
            "mek:uranium_ore",
            "mek:wind_generator",
            "mek:yellow_cake_uranium");
}
