package net.kuzuanpa.cemekanism.machine;

import mekcore.api.MekCore;
import mekcore.machine.*;
import mekcore.persist.MapDataTag;
import net.kuzuanpa.cemekanism.machine.logic.ResistiveHeaterLogic;

/** Exercises heating, adjustable buffer restoration and redstone without a server. */
public final class HeaterSelfTest {
    private static int checks;
    private static void check(boolean ok, String text) { checks++; if (!ok) throw new AssertionError(text); }
    public static void main(String[] args) {
        var heater = new ResistiveHeaterLogic();
        var blueprint = MachineBlueprint.builder("test:heater", "heater").energy(250, 4000)
                .heat(new HeatSpec(.1, 3000, 0)).logic(heater).build();
        var core = MekCore.create(r -> r.machine(blueprint));
        Machine a = new Machine(blueprint, FactoryTier.SINGLE, core.registrars().recipes(), 1, null);
        Machine b = new Machine(blueprint, FactoryTier.SINGLE, core.registrars().recipes(), 2, null);
        check(heater.usage(a) == 250, "default power");
        heater.setUsage(a, 1000);
        check(a.energyCapacity() == 16000 && heater.usage(b) == 250, "per-instance power and proportional capacity");
        check(a.snapshot().energyPerTickJ() == 1000, "GUI reports selected power");
        a.energyTank().orElseThrow().set(null, 100);
        double before = a.heatNode().temperatureK(); a.tick();
        check(a.lastEnergyUsed() == 100 && a.energyTank().orElseThrow().amount() == 0, "partial supply must heat");
        check(Math.abs(a.heatNode().temperatureK() - before - .6) < 1e-6, "60% actual heat conversion");
        a.energyTank().orElseThrow().set(null, 15000);
        MapDataTag tag = new MapDataTag(); a.save(tag);
        Machine restored = new Machine(blueprint, FactoryTier.SINGLE, core.registrars().recipes(), 3, null);
        restored.load(tag);
        check(heater.usage(restored) == 1000 && restored.energyTank().orElseThrow().amount() == 15000,
                "restore dynamic capacity before energy, no truncation");
        a.setRedstoneControl(RedstoneControl.HIGH); a.setPowered(false); before = a.heatNode().temperatureK(); a.tick();
        check(a.lastEnergyUsed() == 0 && a.heatNode().temperatureK() == before, "redstone disable");
        heater.setUsage(a, 0); a.tick();
        check(a.energyCapacity() == 0 && !heater.needsTick(a) && heater.energyDemand(a) == 0, "zero power");
        try { heater.setUsage(b, -1); throw new AssertionError("negative power accepted"); }
        catch (IllegalArgumentException expected) { checks++; }
        try { heater.setUsage(b, Long.MAX_VALUE); throw new AssertionError("capacity overflow accepted"); }
        catch (IllegalArgumentException expected) { check(heater.usage(b) == 250, "invalid power changed state"); }
        System.out.println("Heater self-test: " + checks + " checks passed");
    }
}
