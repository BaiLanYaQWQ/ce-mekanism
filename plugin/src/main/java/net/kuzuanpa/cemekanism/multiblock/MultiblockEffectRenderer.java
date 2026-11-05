package net.kuzuanpa.cemekanism.multiblock;

import mekcore.chemical.Chemicals;
import mekcore.resource.ResourceTank;
import net.kuzuanpa.cemekanism.multiblock.logic.FusionReactorLogic;
import net.kuzuanpa.cemekanism.multiblock.logic.SpsLogic;
import net.kuzuanpa.cemekanism.platform.BukkitItems;
import net.kuzuanpa.cemekanism.platform.ResourceColors;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/** One renderer per shared host, independent of the simulation's sleep predicate. All calls are MAIN. */
public final class MultiblockEffectRenderer {
    public static final String ENTITY_TAG = "cemekanism_multiblock_effect";
    public static final List<String> ITEMS = items();
    private record Volume(String port, int bottom, int height, boolean gas, float inset) { }
    private record Shown(ItemDisplay entity, String item, int color) { }
    private final MultiblockHosts.Host host;
    private final Map<String, Shown> displays = new LinkedHashMap<>();
    private final List<Volume> volumes = new ArrayList<>();
    private final List<Vector3f> coils = new ArrayList<>();
    private final Set<String> wanted = new HashSet<>();
    private final Random random = new Random();
    private boolean layoutValid;
    private long observedTickCount;
    private long lastSimulationTick = Long.MIN_VALUE;
    private long boltFrame = Long.MIN_VALUE;
    private final Map<String, Transformation> boltTransforms = new HashMap<>();

    MultiblockEffectRenderer(MultiblockHosts.Host host) { this.host = host; }

    private static List<String> items() {
        List<String> result = new ArrayList<>(List.of("mek:render/multiblock_water",
                "mek:render/multiblock_liquid", "mek:render/fusion_core",
                "mek:render/sps_bolt", "mek:render/sps_core"));
        for (int i = 1; i <= 8; i++) result.add("mek:render/multiblock_steam_" + i);
        return List.copyOf(result);
    }

    void invalidate() { this.layoutValid = false; }

    private boolean loaded() {
        StructureBox box = this.host.box();
        for (int x = box.originX() >> 4; x <= (box.originX() + box.sizeX() - 1) >> 4; x++)
            for (int z = box.originZ() >> 4; z <= (box.originZ() + box.sizeZ() - 1) >> 4; z++)
                if (!this.host.world().isChunkLoaded(x, z)) return false;
        return true;
    }

    void tick() {
        if (!this.host.formed() || !loaded()) { remove(); return; }
        if (!this.layoutValid) layout();
        this.wanted.clear();
        for (Volume volume : this.volumes) contents(volume);
        var logic = this.host.instance().blueprint().logic();
        if (logic instanceof FusionReactorLogic fusion) {
            var state = fusion.state(this.host.instance());
            if (state.burning) {
                for (int i = 0; i < 3; i++) show("fusion_" + i, "mek:render/fusion_core",
                        MultiblockEffectGeometry.FUSION_COLORS[i], center(),
                        MultiblockEffectGeometry.fusion(i, state.plasmaTemperatureK, this.host.world().getFullTime()),
                        true, false);
            }
        } else if (logic instanceof SpsLogic sps) {
            // lastEnergy is saved by existing logic; a saved/last successful tick must not animate forever.
            long count = this.host.instance().tickCount();
            if (count != this.observedTickCount) {
                this.observedTickCount = count;
                this.lastSimulationTick = Bukkit.getCurrentTick();
            }
            long age = this.lastSimulationTick == Long.MIN_VALUE ? Long.MAX_VALUE
                    : (long) Bukkit.getCurrentTick() - this.lastSimulationTick;
            if (age >= 0 && age <= 4 && sps.state(this.host.instance()).lastEnergy > 0)
                sps(sps.state(this.host.instance()).lastEnergy);
        }
        this.displays.entrySet().removeIf(entry -> {
            if (this.wanted.contains(entry.getKey())) return false;
            entry.getValue().entity().remove();
            return true;
        });
    }

    private void layout() {
        this.volumes.clear(); this.coils.clear(); this.boltTransforms.clear();
        StructureBox box = this.host.box();
        String family = this.host.def().family();
        int interior = box.sizeY() - 2, disperser = -1, complex = -1;
        if (family.equals("boiler") || family.equals("turbine") || family.equals("sps")) {
            for (int y = 1; y < box.sizeY()-1; y++) for (int x = 1; x < box.sizeX()-1; x++)
                for (int z = 1; z < box.sizeZ()-1; z++) {
                    String role = box.roleAt(x, y, z).id();
                    if (role.equals("disperser")) disperser = y;
                    if (role.equals("complex")) complex = y;
                    if (family.equals("sps") && role.equals("coil")) {
                        Vector3f start = new Vector3f(x + .5f - box.sizeX()/2f,
                                y + .5f - box.sizeY()/2f, z + .5f - box.sizeZ()/2f);
                        // The inward coil tip is half a block closer to the chamber center.
                        Vector3f inward = new Vector3f(start).negate();
                        if (inward.lengthSquared() > 0) start.add(inward.normalize(.5f));
                        this.coils.add(start);
                    }
                }
        }
        switch (family) {
            case "boiler" -> {
                if (disperser > 1) this.volumes.add(new Volume("water", 1, disperser-1, false, .01f));
                if (disperser >= 1 && disperser < box.sizeY()-2)
                    this.volumes.add(new Volume("steam", disperser+1, box.sizeY()-disperser-2, true, .01f));
            }
            case "turbine" -> {
                if (complex > 1) this.volumes.add(new Volume("steam", 1, complex-1, true, .01f));
            }
            case "evaporation" -> this.volumes.add(new Volume("water", 1, interior, false, .01f));
            case "fission_reactor" -> {
                this.volumes.add(new Volume("water", 1, interior, false, .01f));
                this.volumes.add(new Volume("steam", 1, interior, true, .02f));
            }
            default -> { }
        }
        this.layoutValid = true;
    }

    private void contents(Volume volume) {
        ResourceTank tank = this.host.instance().tank(volume.port());
        String id;
        long amount, capacity;
        // Network transfers may run on CORE while the display updates on MAIN.
        synchronized (tank) {
            id = tank.storedId(); amount = tank.amount(); capacity = tank.capacity();
        }
        if (amount <= 0 || id == null || volume.height() <= 0) return;
        double ratio = MultiblockEffectGeometry.ratio(amount, capacity);
        float height = MultiblockEffectGeometry.height(volume.height(), ratio, volume.gas());
        float inset = volume.inset();
        StructureBox box = this.host.box();
        float width = box.sizeX()-2-2*inset, depth = box.sizeZ()-2-2*inset;
        String item = volume.gas() ? "mek:render/multiblock_steam_" + MultiblockEffectGeometry.density(ratio)
                : "minecraft:water".equals(id) ? "mek:render/multiblock_water"
                : "mek:render/multiblock_liquid";
        int color;
        if (volume.gas()) {
            var chemical = Chemicals.registry().find(id).orElse(null);
            color = chemical == null ? 0xFFFFFF : chemical.colorArgb() & 0xFFFFFF;
        } else {
            Integer tint = ResourceColors.fluid(id);
            color = tint == null ? 0x3F76E4 : tint;
        }
        // Slightly shrink the heated coolant envelope to avoid coplanar transparent faces.
        float actualHeight = Math.max(.01f, height - (inset - .01f));
        Location at = new Location(this.host.world(), box.originX()+box.sizeX()/2.0,
                box.originY()+volume.bottom()+inset+actualHeight/2, box.originZ()+box.sizeZ()/2.0);
        show("contents_" + volume.port(), item, color, at,
                new Transformation(new Vector3f(), new Quaternionf(),
                        new Vector3f(width, actualHeight, depth), new Quaternionf()), false, false);
    }

    private void sps(long energy) {
        float power = MultiblockEffectGeometry.energyScale(energy / (double) MultiblockConfig.spsEnergyPerInputJ());
        float scale = .1f + 3.9f * power;
        show("sps_core", "mek:render/sps_core", 0xFFFFFF, center(),
                new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(scale), new Quaternionf()), true, true);
        long frame = this.host.world().getFullTime() / 4;
        if (frame != this.boltFrame || this.boltTransforms.isEmpty()) {
            this.boltFrame = frame;
            this.boltTransforms.clear();
            for (int i = 0; i < this.coils.size(); i++)
                this.boltTransforms.put("coil_"+i, MultiblockEffectGeometry.bolt(this.coils.get(i), new Vector3f(), .18f+.28f*power));
            // Ambient discharges terminate on the chamber's inner faces, like RenderSPS.
            int count = 1 + (int) (power * 5);
            StructureBox box = this.host.box();
            for (int i = 0; i < count; i++) {
                Vector3f half = new Vector3f((box.sizeX()-2)/2f-.05f,
                        (box.sizeY()-2)/2f-.05f, (box.sizeZ()-2)/2f-.05f);
                Vector3f end = new Vector3f((this.random.nextFloat()*2-1)*half.x,
                        (this.random.nextFloat()*2-1)*half.y, (this.random.nextFloat()*2-1)*half.z);
                int axis = this.random.nextInt(3);
                end.setComponent(axis, half.get(axis) * (this.random.nextBoolean() ? 1 : -1));
                this.boltTransforms.put("arc_"+i, MultiblockEffectGeometry.bolt(new Vector3f(), end, .12f+.2f*power));
            }
        }
        for (var entry : this.boltTransforms.entrySet())
            show(entry.getKey(), "mek:render/sps_bolt", 0xFFFFFF, center(), entry.getValue(), true, false);
    }

    private Location center() {
        StructureBox box = this.host.box();
        return new Location(this.host.world(), box.originX()+box.sizeX()/2.0,
                box.originY()+box.sizeY()/2.0, box.originZ()+box.sizeZ()/2.0);
    }

    private void show(String key, String item, int color, Location location,
                      Transformation transform, boolean emissive, boolean billboard) {
        this.wanted.add(key);
        Shown shown = this.displays.get(key);
        ItemDisplay display = shown == null ? null : shown.entity();
        boolean created = display == null || !display.isValid();
        boolean changed = created || !shown.item().equals(item) || shown.color() != color;
        ItemStack stack = changed ? BukkitItems.create(item) : null;
        if (changed && stack == null) return; // CE may not have finished loading its pack yet.
        if (stack != null && stack.getItemMeta() instanceof LeatherArmorMeta meta) {
            meta.setColor(Color.fromRGB(color)); stack.setItemMeta(meta);
        }
        if (created) {
            display = this.host.world().spawn(location, ItemDisplay.class, entity -> {
                entity.setPersistent(false); entity.setInvulnerable(true); entity.setSilent(true);
                entity.setGravity(false); entity.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.NONE);
                entity.setBillboard(billboard ? Display.Billboard.CENTER : Display.Billboard.FIXED);
                if (emissive) entity.setBrightness(new Display.Brightness(15, 15));
                entity.setInterpolationDuration(2); entity.setTeleportDuration(2);
                entity.setViewRange(1.0f); entity.setDisplayWidth(0); entity.setDisplayHeight(0);
                entity.addScoreboardTag(ENTITY_TAG); entity.addScoreboardTag("cemekanism_effect_" + key);
                entity.setTransformation(transform);
            });
        }
        if (changed) {
            display.setItemStack(stack);
            this.displays.put(key, new Shown(display, item, color));
        }
        if (!display.getLocation().equals(location)) display.teleport(location);
        if (!display.getTransformation().equals(transform)) {
            display.setInterpolationDelay(0); display.setTransformation(transform);
        }
    }

    void remove() {
        this.displays.values().forEach(shown -> shown.entity().remove());
        this.displays.clear(); this.boltTransforms.clear();
        this.lastSimulationTick = Long.MIN_VALUE;
        this.observedTickCount = this.host.instance().tickCount();
    }
}
