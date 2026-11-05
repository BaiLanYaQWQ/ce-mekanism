package net.kuzuanpa.cemekanism.multiblock.logic;

import mekcore.chemical.Chemical;
import mekcore.chemical.Chemicals;
import mekcore.chemical.Radiation;
import mekcore.machine.Direction;
import mekcore.multiblock.BlockRole;
import mekcore.multiblock.MultiblockInstance;
import mekcore.multiblock.MultiblockLogic;
import mekcore.persist.DataTag;
import mekcore.resource.ResourceStack;
import mekcore.resource.Transaction;
import net.kuzuanpa.cemekanism.multiblock.MultiblockConfig;
import net.kuzuanpa.cemekanism.multiblock.MultiblockContexts;
import net.kuzuanpa.cemekanism.multiblock.MultiblockHosts;
import net.kuzuanpa.cemekanism.multiblock.MultiblockResources;
import org.bukkit.Location;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ThreadLocalRandom;

/** 裂变反应堆：燃料 -> 热、核废料与蒸汽；B6 第一版只实现水冷。 */
public final class FissionReactorLogic implements MultiblockLogic {
    @Override public void configure(MultiblockInstance instance) {
        MultiblockResources.configure(instance);
    }


    public static final String FISSILE_FUEL = "mek:fissile_fuel";
    public static final String NUCLEAR_WASTE = "mek:nuclear_waste";
    private static final double MAX_DAMAGE = 100.0;
    private static final double WATER_ENTHALPY_J_PER_MB = 50.0;
    private static final double WATER_CONDUCTIVITY = 0.5;
    private static final double BASE_BOIL_TEMPERATURE_K = 373.15;
    private static final double INVERSE_ENVIRONMENT = 10_000.0 + 10_000.0 + 10.0;

    public static final class State {
        public boolean active;
        public boolean forceDisabled;
        public double rateLimit = MultiblockConfig.fissionDefaultBurnRate();
        public double burnRemaining;
        public double partialWaste;
        public double damage;
        public String fuelId = FISSILE_FUEL;
    }

    private final Map<MultiblockInstance, State> states = new WeakHashMap<>();

    public State state(MultiblockInstance instance) {
        return this.states.computeIfAbsent(instance, ignored -> new State());
    }

    public void setActive(MultiblockInstance instance, boolean active) {
        State state = state(instance);
        if (!active || !state.forceDisabled) {
            state.active = active;
        }
    }

    public void setRate(MultiblockInstance instance, double rate) {
        State state = state(instance);
        state.rateLimit = Math.max(0, Math.min(rate, maxBurnRate(instance)));
    }

    @Override
    public long tick(MultiblockInstance instance) {
        MultiblockHosts.Host host = MultiblockContexts.hostOf(instance);
        if (host == null || instance.heatNode() == null) {
            return 0;
        }
        State state = state(instance);
        if (hasActivationAdapter(host)) {
            boolean powered = activationPowered(host);
            if (powered != state.active && !state.forceDisabled) {
                state.active = powered;
            }
        }
        if (state.active && !state.forceDisabled) {
            burnFuel(host, instance, state);
        }
        boilWater(instance, state);
        environmentLoss(instance);
        updateDamage(host, instance, state);
        return 0;
    }

    @Override
    public boolean needsTick(MultiblockInstance instance) {
        MultiblockHosts.Host host = MultiblockContexts.hostOf(instance);
        if (host == null || instance.heatNode() == null) {
            return false;
        }
        State state = state(instance);
        return state.active || state.damage > 0.0
                || instance.heatNode().temperatureK() > instance.heatNode().config().ambientK() + 0.01
                || hasActivationAdapter(host) && activationPowered(host);
    }

    private static double maxBurnRate(MultiblockInstance instance) {
        return Math.max(0, instance.roleCount(BlockRole.of("fuel", "fuel")))
                * MultiblockConfig.fissionBurnPerAssembly();
    }

    private static void burnFuel(MultiblockHosts.Host host, MultiblockInstance instance, State state) {
        var fuel = instance.tank("fuel");
        if (fuel.amount() > 0 && fuel.storedId() != null) {
            state.fuelId = fuel.storedId();
        }
        if (!FISSILE_FUEL.equals(state.fuelId) && !fuel.isEmpty()) {
            return;
        }
        double stored = fuel.amount() + state.burnRemaining;
        double toBurn = Math.min(Math.min(state.rateLimit, stored), maxBurnRate(instance));
        if (toBurn <= 0) {
            return;
        }
        double remain = stored - toBurn;
        long whole = (long) Math.floor(remain);
        state.burnRemaining = remain - whole;
        fuel.set(whole > 0 ? state.fuelId : null, whole);
        instance.heatNode().addHeat(toBurn * MultiblockConfig.fissionEnergyPerFuelJ());

        state.partialWaste += toBurn;
        long newWaste = (long) Math.floor(state.partialWaste);
        if (newWaste <= 0) {
            return;
        }
        state.partialWaste -= newWaste;
        var waste = instance.tank("waste");
        String wasteId = waste.storedId() == null ? NUCLEAR_WASTE : waste.storedId();
        if (!NUCLEAR_WASTE.equals(wasteId)) {
            wasteId = NUCLEAR_WASTE;
        }
        long accepted = waste.insert(ResourceStack.chemical(wasteId, newWaste), Transaction.EXECUTE);
        long overflow = newWaste - accepted;
        if (overflow > 0) {
            Chemical chemical = Chemicals.registry().find(NUCLEAR_WASTE).orElse(null);
            double magnitude = chemical == null ? 0 : Radiation.dose(chemical, overflow);
            if (magnitude > 0) {
                host.plugin().radiation(host.world()).pulse(center(host), magnitude);
            }
        }
    }

    private static void boilWater(MultiblockInstance instance, State state) {
        var water = instance.tank("water");
        var steam = instance.tank("steam");
        double capacity = instance.heatNode().heatCapacity();
        double heatPerKelvin = instance.heatNode().config().joulesPerKelvin();
        double available = Math.max(0, instance.heatNode().temperatureK() - BASE_BOIL_TEMPERATURE_K)
                * capacity * heatPerKelvin;
        double efficiency = boilEfficiency(instance);
        long toBoil = (long) Math.floor(available * efficiency * WATER_CONDUCTIVITY
                / WATER_ENTHALPY_J_PER_MB);
        toBoil = Math.min(toBoil, water.amount());
        if (toBoil <= 0) {
            return;
        }
        toBoil = Math.min(toBoil, steam.insert(ResourceStack.chemical(MultiblockConfig.STEAM, toBoil), Transaction.SIMULATE));
        if (toBoil <= 0) return; // A blocked steam outlet means cooling has failed; fuel heat remains.
        water.extract(toBoil, Transaction.EXECUTE);
        steam.insert(ResourceStack.chemical(MultiblockConfig.STEAM, toBoil), Transaction.EXECUTE);
        instance.heatNode().addHeat(-toBoil * WATER_ENTHALPY_J_PER_MB);
    }

    private static double boilEfficiency(MultiblockInstance instance) {
        long assemblies = instance.roleCount(BlockRole.of("fuel", "fuel"));
        if (assemblies <= 0) {
            return 0;
        }
        long surface = surfaceArea(instance);
        return Math.min(1.0, surface / (double) assemblies / MultiblockConfig.fissionSurfaceAreaTarget());
    }

    private static long surfaceArea(MultiblockInstance instance) {
        MultiblockHosts.Host host = MultiblockContexts.hostOf(instance);
        if (host == null) {
            return instance.roleCount(BlockRole.of("fuel", "fuel")) * 6L;
        }
        long area = 0;
        for (int y = 1; y < instance.sizeY() - 1; y++) {
            for (int x = 1; x < instance.sizeX() - 1; x++) {
                for (int z = 1; z < instance.sizeZ() - 1; z++) {
                    if (!"fuel".equals(host.box().roleAt(x, y, z).id())) {
                        continue;
                    }
                    area += 6;
                    for (Direction side : Direction.values()) {
                        int nx = x + deltaX(side);
                        int ny = y + deltaY(side);
                        int nz = z + deltaZ(side);
                        if (nx >= 0 && nx < instance.sizeX() && ny >= 0 && ny < instance.sizeY()
                                && nz >= 0 && nz < instance.sizeZ()
                                && "fuel".equals(host.box().roleAt(nx, ny, nz).id())) {
                            area -= 2;
                        }
                    }
                }
            }
        }
        return area;
    }

    private static int deltaX(Direction direction) {
        return direction == Direction.WEST ? -1 : direction == Direction.EAST ? 1 : 0;
    }

    private static int deltaY(Direction direction) {
        return direction == Direction.DOWN ? -1 : direction == Direction.UP ? 1 : 0;
    }

    private static int deltaZ(Direction direction) {
        return direction == Direction.NORTH ? -1 : direction == Direction.SOUTH ? 1 : 0;
    }

    private static void environmentLoss(MultiblockInstance instance) {
        double delta = instance.heatNode().temperatureK() - instance.heatNode().config().ambientK();
        double transfer = delta / INVERSE_ENVIRONMENT;
        instance.heatNode().addHeat(
                -transfer * instance.heatNode().heatCapacity() * instance.heatNode().config().joulesPerKelvin());
    }

    private static void updateDamage(MultiblockHosts.Host host, MultiblockInstance instance, State state) {
        double temperature = instance.heatNode().temperatureK();
        if (temperature > MultiblockConfig.fissionMinDamageTemperatureK()) {
            state.damage += Math.min(temperature, MultiblockConfig.fissionMaxDamageTemperatureK())
                    / (MultiblockConfig.fissionMinDamageTemperatureK() * 10.0);
        } else {
            state.damage = Math.max(0, state.damage
                    - (MultiblockConfig.fissionMinDamageTemperatureK() - temperature)
                    / (MultiblockConfig.fissionMinDamageTemperatureK() * 100.0));
        }
        if (state.damage < MAX_DAMAGE || temperature < MultiblockConfig.fissionMinDamageTemperatureK()) {
            if (state.damage < MAX_DAMAGE && temperature < MultiblockConfig.fissionMinDamageTemperatureK()) {
                state.forceDisabled = false;
            }
            return;
        }
        double chance = state.damage / MAX_DAMAGE * MultiblockConfig.fissionMeltdownChance();
        if (ThreadLocalRandom.current().nextDouble() >= chance) {
            return;
        }
        if (MultiblockConfig.fissionMeltdownsEnabled()) {
            meltdown(host, instance, state);
        } else {
            state.forceDisabled = true;
            state.active = false;
        }
    }

    private static void meltdown(MultiblockHosts.Host host, MultiblockInstance instance, State state) {
        Location center = center(host);
        double radioactivity = 0;
        for (String port : instance.blueprint().ports().stream().map(p -> p.name()).toList()) {
            var tank = instance.tank(port);
            if (!tank.type().equals(mekcore.resource.ResourceTypes.CHEMICAL)
                    || tank.storedId() == null || tank.amount() <= 0) {
                continue;
            }
            Chemical chemical = Chemicals.registry().find(tank.storedId()).orElse(null);
            if (chemical != null) {
                radioactivity += Radiation.dose(chemical, tank.amount());
            }
        }
        if (radioactivity > 0) {
            host.plugin().radiation(host.world()).pulse(center,
                    radioactivity * MultiblockConfig.fissionMeltdownRadiationMultiplier());
        }
        host.world().createExplosion(center, MultiblockConfig.fissionMeltdownRadius(),
                true, true);
        for (String port : instance.blueprint().ports().stream().map(p -> p.name()).toList()) {
            instance.tank(port).set(null, 0);
        }
        state.active = false;
        state.damage = MultiblockConfig.fissionPostMeltdownDamage();
        state.burnRemaining = 0;
        state.partialWaste = 0;
        instance.heatNode().setTemperatureK(instance.heatNode().config().ambientK());
    }

    private static boolean activationPowered(MultiblockHosts.Host host) {
        for (int y = 0; y < host.box().sizeY(); y++) {
            for (int x = 0; x < host.box().sizeX(); x++) {
                for (int z = 0; z < host.box().sizeZ(); z++) {
                    if (!"logic_adapter".equals(host.box().roleAt(x, y, z).id())) {
                        continue;
                    }
                    if (host.world().getBlockAt(host.box().originX() + x, host.box().originY() + y,
                            host.box().originZ() + z).getBlockPower() > 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean hasActivationAdapter(MultiblockHosts.Host host) {
        return host.instance().roleCount(BlockRole.of("logic_adapter", "logic_adapter")) > 0;
    }

    private static Location center(MultiblockHosts.Host host) {
        return new Location(host.world(),
                host.box().originX() + host.box().sizeX() / 2.0,
                host.box().originY() + host.box().sizeY() / 2.0,
                host.box().originZ() + host.box().sizeZ() / 2.0);
    }

    @Override
    public void save(MultiblockInstance instance, DataTag tag) {
        State state = state(instance);
        tag.putBoolean("active", state.active);
        tag.putBoolean("force_disabled", state.forceDisabled);
        tag.putDouble("rate_limit", state.rateLimit);
        tag.putDouble("burn_remaining", state.burnRemaining);
        tag.putDouble("partial_waste", state.partialWaste);
        tag.putDouble("damage", state.damage);
        tag.putString("fuel_id", state.fuelId);
    }

    @Override
    public void load(MultiblockInstance instance, DataTag tag) {
        State state = state(instance);
        state.active = tag.getBoolean("active", false);
        state.forceDisabled = tag.getBoolean("force_disabled", false);
        state.rateLimit = tag.getDouble("rate_limit", MultiblockConfig.fissionDefaultBurnRate());
        state.burnRemaining = tag.getDouble("burn_remaining", 0);
        state.partialWaste = tag.getDouble("partial_waste", 0);
        state.damage = tag.getDouble("damage", 0);
        state.fuelId = tag.getString("fuel_id", FISSILE_FUEL);
    }
}
