package net.kuzuanpa.cemekanism.multiblock;

import mekcore.api.MekCore;
import mekcore.item.*;
import mekcore.machine.*;
import mekcore.multiblock.*;
import mekcore.persist.MapDataTag;
import mekcore.resource.*;
import net.kuzuanpa.cemekanism.generated.GeneratedContent;
import net.kuzuanpa.cemekanism.multiblock.logic.*;

import java.util.List;
import java.util.Map;

/** Headless plant regressions using the real generated recipes and device logics. */
public final class PlantSelfTest {
    private static int checks;
    private static final MekCore CORE=MekCore.create(GeneratedContent::register);
    private static void check(boolean ok,String message){checks++;if(!ok)throw new AssertionError(message);}
    public static void main(String[] args)throws Exception{
        portModes();filters();evaporation();blockedCooling();largeReload();osmiumCost();oreYield(0);oreYield(4);oreYield(8);
        System.out.println("Plant self-test: "+checks+" checks passed");
    }
    private static MultiblockInstance structure(String id,int w,int h,int d){return CORE.factories().multiblock("mek:"+id,w,h,d);}
    private static void portModes(){
        var def=MultiblockBlocks.require("mek:thermoelectric_boiler");
        var roles=def.rolesOf("mek:boiler_valve");
        check(MultiblockPortModes.choices(def,roles,true).equals(List.of("RESOURCE","INPUT","OUTPUT","HEAT")),
                "boiler exposes both, input, output and heat without losing legacy default");
        for(String assigned:roles)for(String mode:new String[]{"INPUT","OUTPUT","HEAT"})for(String port:def.ports()){
            boolean expected=!mode.equals("HEAT")&&(mode.equals("INPUT")?!def.flow(port).equals("out"):!def.flow(port).equals("in"));
            check(MultiblockPortModes.allows(def,port,assigned,roles,mode)==expected,
                    "all physical boiler valves enforce selected direction "+assigned+" "+mode+" "+port);
        }
        check(MultiblockPortModes.allows(def,"water","valve",roles,"RESOURCE"),"old bidirectional mode retains water");
        check(MultiblockPortModes.allows(def,"steam","valve",roles,"RESOURCE"),"old bidirectional mode retains steam");
        check(!MultiblockPortModes.allows(def,"cooled_coolant","valve",roles,"RESOURCE"),"old default retains role separation");
        check(MultiblockPortModes.flow("store","INPUT").equals("in"),"storage input creates only a consumer");
        check(MultiblockPortModes.flow("both","OUTPUT").equals("out"),"bidirectional tank output creates only a source");
        check(MultiblockPortModes.flow("both","RESOURCE").equals("both"),"legacy bidirectional flow unchanged");
        check(MultiblockPortModes.choices(def, List.of("frame"),true).isEmpty(),"casing has no valve modes");
        var fission=MultiblockBlocks.require("mek:fission_reactor");
        var reactorRoles=fission.rolesOf("mek:fission_reactor_port");
        check(MultiblockPortModes.choices(fission,reactorRoles,true).equals(List.of("INPUT","OUTPUT_WASTE","OUTPUT_COOLANT","HEAT")),
                "upstream reactor mode cycle preserved");
        check(!MultiblockPortModes.allows(fission,"fuel","port",reactorRoles,"OUTPUT_WASTE"),"waste mode cannot receive fuel");
        check(MultiblockPortModes.allows(fission,"waste","port",reactorRoles,"OUTPUT_WASTE"),"waste mode still exports waste");
    }
    private static void filters(){
        var fusion=structure("fusion_reactor",5,5,5);
        for(String port:new String[]{"deuterium","tritium","fuel"}){
            String correct="mek:"+(port.equals("fuel")?"fusion_fuel":port);
            for(String candidate:new String[]{"mek:deuterium","mek:tritium","mek:fusion_fuel","mek:oxygen"}){
                long simulation=fusion.tank(port).insert(ResourceStack.chemical(candidate,1),Transaction.SIMULATE);
                check(simulation==(candidate.equals(correct)?1:0),"fusion intrinsic filter "+port+" "+candidate);
            }
        }
        check(fusion.tank("tritium").insert(ResourceStack.chemical("mek:deuterium",10),Transaction.EXECUTE)==0,"real insertion rejects wrong fusion chemical");
        var fission=structure("fission_reactor",5,4,5);
        check(fission.tank("fuel").insert(ResourceStack.chemical("mek:steam",10),Transaction.EXECUTE)==0,"fission refuses steam as fuel");
        check(fission.tank("water").insert(ResourceStack.fluid("minecraft:lava",10),Transaction.EXECUTE)==0,"fission refuses lava cooling");
    }
    private static void evaporation(){
        var instance=structure("thermal_evaporation_plant",4,5,4);
        var logic=(EvaporationLogic)instance.blueprint().logic();
        instance.tank("water").resize(100000);instance.tank("water").set("minecraft:water",100000);
        instance.tank("output").resize(10000);
        double factor=.25;
        instance.heatNode().setTemperatureK(MultiblockConfig.ambientK()+factor/(MultiblockConfig.evaporationTempMultiplier()*5/18.0));
        logic.tick(instance);logic.tick(instance);var tag=new MapDataTag();instance.save(tag);
        var loaded=structure("thermal_evaporation_plant",4,5,4);loaded.load(tag);logic=(EvaporationLogic)loaded.blueprint().logic();
        logic.tick(loaded);logic.tick(loaded);
        check(loaded.tank("output").amount()==1 && loaded.tank("water").amount()==99990,"fractional evaporation and credit survive reload");
        loaded.heatNode().setTemperatureK(MultiblockConfig.ambientK()+20/(MultiblockConfig.evaporationTempMultiplier()*5/18.0));
        logic.tick(loaded);check(loaded.tank("output").amount()==21,"20x evaporation actually makes 20 operations per tick");
        loaded.tank("output").set("mek:brine",loaded.tank("output").capacity());long before=loaded.tank("water").amount();
        logic.tick(loaded);check(loaded.tank("water").amount()==before,"full output never consumes evaporation input");
    }
    private static void blockedCooling()throws Exception{
        var instance=structure("fission_reactor",5,4,5);var logic=(FissionReactorLogic)instance.blueprint().logic();
        var field=MultiblockInstance.class.getDeclaredField("validation");field.setAccessible(true);
        field.set(instance,ValidationResult.formed(5,4,5,Map.of(BlockRole.of("fuel","fuel"),9),Map.of()));
        instance.heatNode().setHeatCapacity(100);instance.heatNode().setTemperatureK(1000);
        instance.tank("water").set("minecraft:water",10000);instance.tank("steam").set("mek:steam",instance.tank("steam").capacity());
        var boil=FissionReactorLogic.class.getDeclaredMethod("boilWater",MultiblockInstance.class,FissionReactorLogic.State.class);boil.setAccessible(true);
        var state=logic.state(instance);boil.invoke(null,instance,state);
        check(instance.tank("water").amount()==10000 && instance.heatNode().temperatureK()==1000,"blocked steam means no water consumed and no cooling");
        instance.tank("fuel").resize(8000);instance.tank("waste").resize(8000);
        instance.tank("fuel").set("mek:fissile_fuel",100);state.rateLimit=.05;
        var burn=FissionReactorLogic.class.getDeclaredMethod("burnFuel",MultiblockHosts.Host.class,MultiblockInstance.class,FissionReactorLogic.State.class);burn.setAccessible(true);
        for(int i=0;i<10;i++){burn.invoke(null,null,instance,state);boil.invoke(null,instance,state);}
        check(instance.heatNode().temperatureK()>1000 && instance.tank("water").amount()==10000,"fuel continues heating after cooling failure");
        instance.tank("steam").extract(7,Transaction.EXECUTE);boil.invoke(null,instance,state);
        check(instance.tank("water").amount()==9993,"partial steam room consumes exactly the cooling water accepted");
    }
    private static void largeReload(){
        var reactor=structure("fission_reactor",5,4,5);reactor.tank("fuel").resize(72000);reactor.tank("water").resize(1800000);
        reactor.tank("fuel").set("mek:fissile_fuel",60000);reactor.tank("water").set("minecraft:water",1500000);
        reactor.tank("steam").setDumpMode(DumpMode.DUMPING_EXCESS);var tag=new MapDataTag();reactor.save(tag);
        var loaded=structure("fission_reactor",5,4,5);loaded.load(tag);
        check(loaded.tank("fuel").amount()==60000 && loaded.tank("water").amount()==1500000,"restore capacities before contents, no startup truncation");
        check(loaded.tank("steam").dumpMode()==DumpMode.DUMPING_EXCESS,"empty port still retains dump mode");
        check(loaded.tank("fuel").insert(ResourceStack.chemical("mek:oxygen",1),Transaction.EXECUTE)==0,"intrinsic filtering remains after reload");
    }
    private static Machine machine(String id,int speed){
        var m=new Machine(CORE.registrars().machines().require("mek:"+id),FactoryTier.SINGLE,CORE.registrars().recipes(),123,null);
        m.installUpgrade("mek:speed_upgrade",speed);m.installUpgrade("mek:energy_upgrade",8);m.installUpgrade("mek:chemical_upgrade",8);return m;
    }
    private static void osmiumCost(){
        for(int speed:new int[]{0,4,8}){
            var m=machine("osmium_compressor",speed);
            m.items().insert(ItemRef.of("minecraft:glowstone_dust",1),Transaction.EXECUTE);
            m.tank("gas").orElseThrow().set("mek:osmium",210);
            for(int tick=0;tick<300;tick++){m.energyTank().orElseThrow().set(null,m.energyCapacity());m.tick();}
            check(m.operations()==1 && m.tank("gas").orElseThrow().amount()==10,"osmium compressor fixed 200 mB at speed "+speed);
        }
    }
    private static void oreYield(int speed){
        Machine[] line={machine("chemical_dissolution_chamber",speed),machine("chemical_washer",speed),machine("chemical_crystallizer",speed),
                machine("chemical_injection_chamber",speed),machine("purification_chamber",speed),machine("crusher",speed),machine("enrichment_chamber",speed),machine("energized_smelter",speed)};
        line[0].items().insert(ItemRef.of("minecraft:iron_ore",1),Transaction.EXECUTE);
        line[0].tank("chemical").orElseThrow().set("mek:sulfuric_acid",10000);
        line[1].tank("fluid").orElseThrow().set("minecraft:water",10000);
        line[3].tank("gas").orElseThrow().set("mek:hydrogen_chloride",210);line[4].tank("gas").orElseThrow().set("mek:oxygen",210);
        for(int tick=0;tick<10000;tick++){
            line[3].tank("gas").orElseThrow().set("mek:hydrogen_chloride",210);
            line[4].tank("gas").orElseThrow().set("mek:oxygen",210);
            for(var m:line){m.energyTank().orElseThrow().set(null,m.energyCapacity());m.tick();}
            for(int i=0;i<2;i++){
                var from=line[i].tank("output").orElseThrow();var to=line[i+1].tank("chemical").orElseThrow();
                if(from.amount()>0){var stack=ResourceStack.chemical(from.storedId(),from.amount());long moved=to.insert(stack,Transaction.EXECUTE);from.extract(moved,Transaction.EXECUTE);}
            }
            for(int i=2;i<line.length-1;i++){
                var from=line[i].items();var to=line[i+1].items();var candidate=from.extractCandidate(ref->true);
                if(candidate!=null){long n=to.insert(candidate,Transaction.EXECUTE);from.extract(candidate.withAmount(n),Transaction.EXECUTE);}
            }
        }
        check(line[7].items().count(ItemSlotKind.OUTPUT,ItemMatcher.id("minecraft:iron_ingot"))==5,"one real iron ore produces exactly five ingots at speed "+speed);
        check(line[0].operations()==1 && line[2].operations()==5,"one dissolution and five crystallizations at speed "+speed);
        for(var m:line)check(m.lostOutputs()==0,"no lost plant outputs "+m.blueprint().id());
    }
}
