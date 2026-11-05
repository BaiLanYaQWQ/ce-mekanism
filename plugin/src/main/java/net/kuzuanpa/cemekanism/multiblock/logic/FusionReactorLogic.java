package net.kuzuanpa.cemekanism.multiblock.logic;

import mekcore.multiblock.MultiblockInstance;
import mekcore.multiblock.MultiblockLogic;
import mekcore.persist.DataTag;
import mekcore.resource.ResourceStack;
import mekcore.resource.Transaction;
import net.kuzuanpa.cemekanism.multiblock.MultiblockConfig;
import net.kuzuanpa.cemekanism.multiblock.MultiblockContexts;
import net.kuzuanpa.cemekanism.multiblock.MultiblockHosts;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.WeakHashMap;

/** 聚变反应堆：激光预热、D/T 注入、等离子体换热、水冷蒸汽与热电偶发电。 */
public final class FusionReactorLogic implements MultiblockLogic {
    @Override public void configure(MultiblockInstance instance) {
        net.kuzuanpa.cemekanism.multiblock.MultiblockResources.configure(instance);
    }


    public static final String DEUTERIUM = "mek:deuterium";
    public static final String TRITIUM = "mek:tritium";
    public static final String FUSION_FUEL = "mek:fusion_fuel";

    public static final class State {
        public boolean initialized;
        public boolean burning;
        public double plasmaTemperatureK;
        public int injectionRate = MultiblockConfig.fusionDefaultInjection();
        public ItemStack hohlraum;
    }

    private final Map<MultiblockInstance, State> states = new WeakHashMap<>();

    public State state(MultiblockInstance instance) {
        return this.states.computeIfAbsent(instance, value -> {
            State state = new State();
            state.plasmaTemperatureK = value.heatNode() == null
                    ? MultiblockConfig.ambientK() : value.heatNode().temperatureK();
            state.initialized = true;
            return state;
        });
    }

    /** 激光聚焦矩阵把本刻光束能量直接交给等离子体；不经过普通能量线缆。 */
    public void addLaserEnergy(MultiblockInstance instance, long energy) {
        if (energy <= 0) {
            return;
        }
        State state = state(instance);
        double factor = state.burning ? 1.0 : 10.0;
        state.plasmaTemperatureK += energy * factor / MultiblockConfig.fusionPlasmaHeatCapacity();
    }

    public void setInjectionRate(MultiblockInstance instance, int rate) {
        int even = Math.max(2, rate);
        even -= even % 2;
        even = Math.min(even, MultiblockConfig.fusionMaxInjection());
        State state = state(instance);
        state.injectionRate = even;
        instance.tank("water").resize(Math.max(1,
                even * MultiblockConfig.fusionWaterPerInjection()));
        instance.tank("steam").resize(Math.max(1,
                even * MultiblockConfig.fusionSteamPerInjection()));
    }

    public boolean insertHohlraum(MultiblockInstance instance, ItemStack stack) {
        if (!HohlraumItems.isHohlraum(stack)) {
            return false;
        }
        State state = state(instance);
        if (state.hohlraum != null) {
            return false;
        }
        state.hohlraum = stack.clone();
        state.hohlraum.setAmount(1);
        return true;
    }

    public ItemStack takeHohlraum(MultiblockInstance instance) {
        State state = state(instance);
        ItemStack result = state.hohlraum;
        state.hohlraum = null;
        return result;
    }

    public void setPlasmaTemperature(MultiblockInstance instance, double temperatureK) {
        state(instance).plasmaTemperatureK = Math.max(MultiblockConfig.ambientK(), temperatureK);
    }

    @Override
    public long tick(MultiblockInstance instance) {
        MultiblockHosts.Host host = MultiblockContexts.hostOf(instance);
        if (host == null || instance.heatNode() == null) {
            return 0;
        }
        State state = state(instance);
        int burned = 0;
        if (state.plasmaTemperatureK >= MultiblockConfig.fusionBurnTemperatureK()) {
            if (ignite(instance, state)) {
                injectFuel(instance, state);
                burned = burnFuel(instance, state);
            }
            if (burned == 0) {
                state.burning = false;
            }
        } else {
            state.burning = false;
        }

        transferPlasmaToCase(instance, state);
        long generated = transferCaseHeat(instance);
        if (state.burning && host.world().getFullTime() % 20 == 0) {
            damageInterior(host);
        }
        return generated;
    }

    @Override
    public boolean needsTick(MultiblockInstance instance) {
        State state = state(instance);
        double ambient = instance.heatNode() == null
                ? MultiblockConfig.ambientK() : instance.heatNode().config().ambientK();
        double caseTemperature = instance.heatNode() == null
                ? ambient : instance.heatNode().temperatureK();
        return state.burning || state.plasmaTemperatureK > ambient + 0.01
                || caseTemperature > ambient + 0.01
                || instance.tank("fuel").amount() > 0
                || instance.tank("deuterium").amount() > 0
                || instance.tank("tritium").amount() > 0;
    }

    private static boolean ignite(MultiblockInstance instance, State state) {
        if (state.burning) {
            return true;
        }
        var fuel = instance.tank("fuel");
        ItemStack hohlraum = state.hohlraum;
        if (hohlraum != null) {
            long payload = HohlraumItems.amount(hohlraum);
            if (payload >= MultiblockConfig.hohlraumCapacity()
                    && FUSION_FUEL.equals(HohlraumItems.chemical(hohlraum))) {
                long accepted = fuel.insert(ResourceStack.chemical(FUSION_FUEL, payload), Transaction.EXECUTE);
                if (accepted == payload) {
                    state.hohlraum = null;
                    state.burning = true;
                    return true;
                }
            }
        }
        // 无载荷黑体辐射腔也允许在内部燃料罐已有 D-T 时点火，便于管道供料。
        if (hohlraum != null && FUSION_FUEL.equals(fuel.storedId()) && fuel.amount() > 0) {
            state.hohlraum = null;
            state.burning = true;
            return true;
        }
        return false;
    }

    private static void injectFuel(MultiblockInstance instance, State state) {
        var deuterium = instance.tank("deuterium");
        var tritium = instance.tank("tritium");
        var fuel = instance.tank("fuel");
        long available = 2L * Math.min(deuterium.amount(), tritium.amount());
        long room = Math.max(0, fuel.capacity() - fuel.amount());
        long amount = Math.min(Math.min(available, state.injectionRate), room);
        amount -= amount % 2;
        if (amount <= 0 || !DEUTERIUM.equals(deuterium.storedId()) || !TRITIUM.equals(tritium.storedId())) {
            return;
        }
        long half = amount / 2;
        deuterium.extract(half, Transaction.EXECUTE);
        tritium.extract(half, Transaction.EXECUTE);
        fuel.insert(ResourceStack.chemical(FUSION_FUEL, amount), Transaction.EXECUTE);
    }

    private static int burnFuel(MultiblockInstance instance, State state) {
        var fuel = instance.tank("fuel");
        if (!FUSION_FUEL.equals(fuel.storedId()) || fuel.amount() <= 0) {
            return 0;
        }
        int amount = (int) Math.min(fuel.amount(),
                Math.max(0, Math.floor(state.plasmaTemperatureK
                        - MultiblockConfig.fusionBurnTemperatureK())));
        if (amount <= 0) {
            return 0;
        }
        long used = fuel.extract(amount, Transaction.EXECUTE);
        if (used <= 0) {
            return 0;
        }
        state.plasmaTemperatureK += used * (double) MultiblockConfig.fusionEnergyPerFuelJ()
                / MultiblockConfig.fusionPlasmaHeatCapacity();
        return (int) used;
    }

    private static void transferPlasmaToCase(MultiblockInstance instance, State state) {
        double heat = MultiblockConfig.fusionPlasmaCaseConductivity()
                * (state.plasmaTemperatureK - instance.heatNode().temperatureK());
        state.plasmaTemperatureK -= heat / MultiblockConfig.fusionPlasmaHeatCapacity();
        instance.heatNode().addHeat(heat);
    }

    private static long transferCaseHeat(MultiblockInstance instance) {
        double caseWaterHeat = MultiblockConfig.fusionWaterHeatingRatio()
                * (instance.heatNode().temperatureK() - instance.heatNode().config().ambientK());
        if (caseWaterHeat > 0) {
            long vaporize = (long) Math.floor(0.2 * caseWaterHeat / 10.0);
            vaporize = Math.min(vaporize, instance.tank("water").amount());
            if (vaporize > 0) {
                instance.tank("water").extract(vaporize, Transaction.EXECUTE);
                instance.tank("steam").insert(
                        ResourceStack.chemical(MultiblockConfig.STEAM, vaporize), Transaction.EXECUTE);
                instance.heatNode().addHeat(-vaporize * 50.0);
            }
        }
        double caseAirHeat = MultiblockConfig.fusionCasingThermalConductivity()
                * (instance.heatNode().temperatureK() - instance.heatNode().config().ambientK());
        if (caseAirHeat <= 0) {
            return 0;
        }
        instance.heatNode().addHeat(-caseAirHeat);
        long power = Math.max(0,
                (long) Math.floor(caseAirHeat * MultiblockConfig.fusionThermocoupleEfficiency()));
        if (power <= 0) {
            return 0;
        }
        return instance.tank("energy").insert(
                ResourceStack.energy(power), Transaction.EXECUTE);
    }

    private static void damageInterior(MultiblockHosts.Host host) {
        int x = host.box().originX() + 1;
        int y = host.box().originY() + 1;
        int z = host.box().originZ() + 1;
        int sx = Math.max(1, host.box().sizeX() - 2);
        int sy = Math.max(1, host.box().sizeY() - 2);
        int sz = Math.max(1, host.box().sizeZ() - 2);
        Location center = new Location(host.world(), x + sx / 2.0, y + sy / 2.0, z + sz / 2.0);
        for (Entity entity : host.world().getNearbyEntities(center, sx / 2.0, sy / 2.0, sz / 2.0)) {
            if (entity instanceof LivingEntity living) {
                living.damage(50_000.0);
            }
        }
    }

    @Override
    public void save(MultiblockInstance instance, DataTag tag) {
        State state = state(instance);
        tag.putBoolean("burning", state.burning);
        tag.putDouble("plasma_temperature_k", state.plasmaTemperatureK);
        tag.putInt("injection_rate", state.injectionRate);
        tag.putString("hohlraum", HohlraumItems.encode(state.hohlraum));
    }

    @Override
    public void load(MultiblockInstance instance, DataTag tag) {
        State state = state(instance);
        state.burning = tag.getBoolean("burning", false);
        state.plasmaTemperatureK = tag.getDouble("plasma_temperature_k", state.plasmaTemperatureK);
        setInjectionRate(instance, tag.getInt("injection_rate",
                MultiblockConfig.fusionDefaultInjection()));
        state.hohlraum = HohlraumItems.decode(tag.getString("hohlraum", ""));
    }
}
