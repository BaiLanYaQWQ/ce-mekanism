package net.kuzuanpa.cemekanism.storage;

import mekcore.item.ItemRef;
import mekcore.machine.*;
import mekcore.resource.*;
import mekcore.spec.V10Tables;
import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.kuzuanpa.cemekanism.gui.GuiText;
import net.kuzuanpa.cemekanism.gui.MachineGui;
import net.kuzuanpa.cemekanism.i18n.I18n;
import net.kuzuanpa.cemekanism.machine.MachineBlockEntityController;
import net.kuzuanpa.cemekanism.machine.logic.ResistiveHeaterLogic;
import net.kuzuanpa.cemekanism.multiblock.*;
import net.kuzuanpa.cemekanism.platform.BukkitItems;
import net.kuzuanpa.cemekanism.platform.CompoundDataTag;
import net.kuzuanpa.cemekanism.platform.EnergyItems;
import net.momirealms.craftengine.bukkit.api.*;
import net.momirealms.craftengine.core.block.parser.BlockStateParser;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.libraries.nbt.CompoundTag;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import java.util.*;
import java.util.logging.Level;

/** Explicit destructive acceptance fixture at dedicated coordinates. Exercises real CE entities and Paper item serialization. */
public final class B5Acceptance {
    private final CeMekanismPlugin plugin;
    private final World world;
    private final int x, y, z;
    private int checks;
    private B5Acceptance(CeMekanismPlugin plugin, World world, int x, int y, int z) {
        this.plugin=plugin; this.world=world; this.x=x; this.y=y; this.z=z;
    }
    private void check(boolean condition, String label) {
        checks++; if (!condition) throw new IllegalStateException(I18n.tr("acceptance.failed", label));
    }
    public static void run(CeMekanismPlugin plugin, CommandSender sender, String[] args) {
        if (sender instanceof org.bukkit.entity.Player && !sender.hasPermission("cemekanism.admin")) {
            I18n.send(sender, "acceptance.admin_required");
            return;
        }
        int x=args.length>1?Integer.parseInt(args[1]):5000, y=args.length>2?Integer.parseInt(args[2]):120, z=args.length>3?Integer.parseInt(args[3]):5000;
        World world=sender instanceof org.bukkit.entity.Player p?p.getWorld():Bukkit.getWorlds().get(0);
        B5Acceptance test=new B5Acceptance(plugin,world,x,y,z);
        try {
            test.storage(); I18n.send(sender, "acceptance.b5.storage", test.checks);
            test.matrix(); I18n.send(sender, "acceptance.b5.matrix", test.checks);
            test.qio(); I18n.send(sender, "acceptance.b5.qio", test.checks);
            test.miner(); I18n.send(sender, "acceptance.b5.miner", test.checks);
            test.pumpAndSorter(); test.heaterAndLaser();
            I18n.send(sender, "acceptance.b5.complete", test.checks, x, y, z);
        } catch (RuntimeException e) {
            I18n.send(sender, "acceptance.b5.aborted", test.checks, e.getMessage());
            plugin.getLogger().log(Level.SEVERE, "B5 acceptance", e);
        }
    }
    private void place(World w, int a, int b, int c, String state) {
        w.getChunkAt(a>>4,c>>4).setForceLoaded(true);
        var parsed=BlockStateParser.deserialize(state);
        if(parsed==null) throw new IllegalArgumentException(I18n.tr("acceptance.unknown_block", state));
        var existing=plugin.machineAtQuiet(w,a,b,c);
        if(existing!=null) existing.setSuppressStorageDrops(true);
        CraftEngineBlocks.remove(w.getBlockAt(a,b,c));
        CraftEngineBlocks.place(new Location(w,a,b,c),parsed,false);
        BukkitAdaptor.adapt(w).storageWorld().setBlockStateAtIfLoaded(new BlockPos(a,b,c),parsed);
    }
    private MachineBlockEntityController machine(World w,int a,int b,int c,String id) {
        place(w,a,b,c,id+"[facing=north]"); var result=plugin.machineAtQuiet(w,a,b,c);
        if(result==null) throw new IllegalStateException(I18n.tr("acceptance.machine_entity_missing", id));
        result.claim("console"); return result;
    }
    private void storage() {
        int offset=0;
        for(MachineTier tier:MachineTier.values()) {
            String prefix=tier.name().toLowerCase();
            var cube=machine(world,x+offset++,y,z,"mek:"+prefix+"_energy_cube");
            var profile=V10Tables.energyCube(tier); var energy=cube.machine().energyTank().orElseThrow();
            check(energy.capacity()==profile.capacityJ() && energy.rateLimit()==profile.ratePerTick(),prefix+" cube profile");
            check(energy.insert(ResourceStack.energy(Long.MAX_VALUE),Transaction.EXECUTE)==profile.ratePerTick(),"cube input rate");
            check(!cube.machine().needsTick(),"passive storage sleeps");
            ItemStack carried=BukkitItems.create(cube.blockId());
            check(EnergyItems.charge(carried,Long.MAX_VALUE)==profile.ratePerTick(),"carried cube shares tier rate");
            check(EnergyItems.stored(carried)==profile.ratePerTick(),"carried cube energy model");
            CompoundTag root=new CompoundTag(); cube.saveCustomData(root); energy.drain(); cube.loadCustomData(root);
            check(energy.amount()==profile.ratePerTick(),"cube restart energy");
            for(String family:List.of("fluid_tank","chemical_tank")) {
                var tank=machine(world,x+offset++,y,z,"mek:"+prefix+'_'+family);
                var p=family.equals("fluid_tank")?V10Tables.fluidTank(tier):V10Tables.chemicalTank(tier);
                var t=tank.machine().tank("stored").orElseThrow();
                check(t.capacity()==p.capacity() && t.rateLimit()==p.outputRatePerTick(),prefix+' '+family);
                String id=family.equals("fluid_tank")?"minecraft:water":"mek:hydrogen";
                check(t.insert(new ResourceStack(t.type(),id,Long.MAX_VALUE),Transaction.EXECUTE)==p.outputRatePerTick(),"tank rate");
                check(t.insert(new ResourceStack(t.type(),"mek:other",1),Transaction.SIMULATE)==0,"mixed resources refused");
            }
        }
        for (String id : List.of("mek:personal_chest", "mek:personal_barrel")) {
            var personal = machine(world, x+offset++, y, z, id);
            check(personal.inventory().getSize()==54 && personal.machine().items().slots().size()==54, "personal storage has 54 real slots");
            personal.machine().items().insert(ItemRef.of("minecraft:iron_ingot",64),Transaction.EXECUTE);
            check(personal.machine().items().extract(ItemRef.of("minecraft:iron_ingot",64),Transaction.EXECUTE)==64, "personal storage automatic input/output");
            personal.putInSlot("storage0", new ItemStack(Material.IRON_INGOT, 64));
            check(preview(personal).getType()==Material.IRON_INGOT,id+" front content preview");
            personal.putInSlot("storage0", null);
            check(preview(personal)==null,id+" preview clears with contents");
        }
        ItemStack first=new ItemStack(Material.DIAMOND_SWORD); var meta=first.getItemMeta(); meta.displayName(net.kyori.adventure.text.Component.text("B5 A")); first.setItemMeta(meta);
        ItemStack second=first.clone(); var other=second.getItemMeta(); other.displayName(net.kyori.adventure.text.Component.text("B5 B")); second.setItemMeta(other);
        check(!StoredItems.key(first).equals(StoredItems.key(second)),"NBT variants remain distinct");
        check(StoredItems.item(StoredItems.key(first),1).isSimilar(first),"Paper item roundtrip");
        var bin=machine(world,x+offset,y,z,"mek:ultimate_bin");
        bin.putInSlot("in",new ItemStack(Material.COBBLESTONE,128)); bin.machine().tick();
        check(bin.slotContent("out").getAmount()==64,"bin staging output");
        check(((BinLogic)bin.machine().blueprint().logic()).store(bin.machine()).capacity()==262144,"bin upstream capacity");
        check(preview(bin).getType()==Material.COBBLESTONE,"bin front content preview");
    }
    private ItemStack preview(MachineBlockEntityController controller) {
        return world.getNearbyEntities(new Location(world,controller.x()+.5,controller.y()+.5,controller.z()+.5),
                        .75,.75,.75).stream()
                .filter(ItemDisplay.class::isInstance).map(ItemDisplay.class::cast)
                .filter(display->display.getPersistentDataContainer().has(
                        StorageContentRenderer.PREVIEW_KEY,PersistentDataType.BYTE))
                .map(ItemDisplay::getItemStack).filter(Objects::nonNull).findFirst().orElse(null);
    }
    private void matrix() {
        var def=MultiblockBlocks.require("mek:induction_matrix");
        MultiblockPlacements.build(plugin,world,x+30,y,z,def,5,5,5);
        // Replace the complete interior so the fixture doesn't depend on generated starter cell positions.
        for(int a=1;a<4;a++) for(int b=1;b<4;b++) for(int c=1;c<4;c++) world.getBlockAt(x+30+a,y+b,z+c).setType(Material.AIR,false);
        place(world,x+31,y+1,z+1,"mek:basic_induction_cell");
        place(world,x+32,y+1,z+1,"mek:ultimate_induction_cell");
        place(world,x+31,y+1,z+2,"mek:basic_induction_provider");
        place(world,x+32,y+1,z+2,"mek:advanced_induction_provider");
        var host=plugin.multiblocks().acquire(world,x+30,y,z,def,new int[]{5,5,5},()->{});
        check(host.refresh() && host.formed(),"mixed matrix forms");
        var tank=host.instance().tank("energy");
        long capacity=V10Tables.inductionElement(MachineTier.BASIC).capacityJ()+V10Tables.inductionElement(MachineTier.ULTIMATE).capacityJ();
        long rate=V10Tables.inductionProvider(MachineTier.BASIC).ratePerTick()+V10Tables.inductionProvider(MachineTier.ADVANCED).ratePerTick();
        check(tank.capacity()==capacity && tank.rateLimit()==rate,"matrix sums independent tier capacities/rates");
        tank.drain();
        check(tank.insert(ResourceStack.energy(Long.MAX_VALUE),Transaction.EXECUTE)==rate,"matrix provider input rate");
        CompoundTag snapshot=new CompoundTag(); host.save(new CompoundDataTag(snapshot)); tank.drain(); host.load(new CompoundDataTag(snapshot));
        check(tank.amount()==rate,"matrix state roundtrip");
        world.getBlockAt(x+31,y+1,z+2).setType(Material.AIR,false); world.getBlockAt(x+32,y+1,z+2).setType(Material.AIR,false);
        check(!host.refresh(),"provider-less matrix rejected");
        place(world,x+31,y+1,z+2,"mek:basic_induction_provider"); check(host.refresh(),"matrix reforms");
        long before=tank.capacity(); host.refresh(); host.refresh();
        check(tank.capacity()==before,"repeat matrix validation never multiplies capacity");
    }
    private void qio() {
        World other=Bukkit.getWorlds().stream().filter(w->!w.equals(world)).findFirst().orElseThrow();
        var b=machine(other,x,y,z+10,"mek:qio_drive_array");
        var a=machine(world,x,y,z+10,"mek:qio_drive_array"); var q=plugin.qio();
        String frequency=q.create("b5_acceptance_" + x + '_' + y + '_' + z,"console"); q.join(a,frequency,"console");
        a.putInSlot("drive0",BukkitItems.create("mek:qio_drive_supermassive"));
        ItemStack iron=new ItemStack(Material.IRON_INGOT);
        check(q.insert(frequency,"console",iron,5_000_000_000L,true)==5_000_000_000L,"QIO long quantities");
        check(q.extract(frequency,"console",StoredItems.key(iron),1,false)==1,"QIO simulate extraction");
        check(q.entries(frequency,"console").get(StoredItems.key(iron))==5_000_000_000L,"QIO simulation preserves count");
        ItemStack disk=a.slotContent("drive0").clone(); a.putInSlot("drive0",null);
        check(q.entries(frequency,"console").isEmpty(),"removing drive removes all capacity/items");
        a.putInSlot("drive0",disk); check(q.entries(frequency,"console").get(StoredItems.key(iron))==5_000_000_000L,"drive reinsert restores items");
        CompoundTag snapshot=new CompoundTag(); a.saveCustomData(snapshot); a.inventory().clear(); a.loadCustomData(snapshot);
        check(q.entries(frequency,"console").get(StoredItems.key(iron))==5_000_000_000L,"array inventory persistence");
        q.join(b,frequency,"console");
        check(a.registered() && b.registered() && !a.taskId().equals(b.taskId()),"same-coordinate cross-world scheduler ids unique");
        a.putInSlot("drive0",null); b.putInSlot("drive0",disk);
        check(q.entries(frequency,"console").get(StoredItems.key(iron))==5_000_000_000L,"cross-world drive membership");
        a.putInSlot("drive0",disk.clone()); check(q.entries(frequency,"console").isEmpty(),"duplicate drive ids quarantined");
        a.putInSlot("drive0",null); b.putInSlot("drive0",null); a.putInSlot("drive0",disk);
        try { q.require(frequency,"outsider"); throw new IllegalStateException("private frequency leaked"); }
        catch(IllegalArgumentException expected){checks++;}
        q.security(frequency,"console",SecurityLevel.PUBLIC,null,false); check(q.require(frequency,"outsider")!=null,"public frequency");
        q.security(frequency,"console",SecurityLevel.TRUSTED,"friend",true); check(q.require(frequency,"friend")!=null,"trusted frequency");
        q.security(frequency,"console",SecurityLevel.PRIVATE,null,false);
        try { q.require(frequency,"friend"); throw new IllegalStateException("revocation failed"); } catch(IllegalArgumentException expected){checks++;}
        // Physical importer/exporter targets, including full destination backpressure.
        var importer=machine(world,x+3,y,z+10,"mek:qio_importer"); q.join(importer,frequency,"console");
        world.getBlockAt(x+3,y,z+11).setType(Material.CHEST); var input=(org.bukkit.block.Chest)world.getBlockAt(x+3,y,z+11).getState();
        input.getInventory().clear(); input.getInventory().setItem(0,new ItemStack(Material.GOLD_INGOT,12)); q.tick(importer);
        check(input.getInventory().isEmpty() && q.entries(frequency,"console").get(StoredItems.key(new ItemStack(Material.GOLD_INGOT)))==12,"QIO importer transfers real items");
        var exporter=machine(world,x+6,y,z+10,"mek:qio_exporter"); q.join(exporter,frequency,"console");
        world.getBlockAt(x+6,y,z+11).setType(Material.CHEST); var output=(org.bukkit.block.Chest)world.getBlockAt(x+6,y,z+11).getState(); output.getInventory().clear(); q.tick(exporter);
        check(output.getInventory().isEmpty(), "QIO exporter defaults to no unfiltered export");
        QioService.state(exporter).filter.inverse=false; QioService.state(exporter).filter.rules.add("minecraft:gold_ingot"); q.tick(exporter);
        check(output.getInventory().getItem(0).getAmount()==12 && !q.entries(frequency,"console").containsKey(StoredItems.key(new ItemStack(Material.GOLD_INGOT))),"QIO exporter filtered transfer");
        for(int i=0;i<output.getInventory().getSize();i++) output.getInventory().setItem(i,new ItemStack(Material.COBBLESTONE,64));
        q.insert(frequency,"console",new ItemStack(Material.GOLD_INGOT),1,true); q.tick(exporter);
        check(q.entries(frequency,"console").get(StoredItems.key(new ItemStack(Material.GOLD_INGOT)))==1,"QIO full target does not void items");
        var adapter=machine(world,x+24,y,z+10,"mek:qio_redstone_adapter"); q.join(adapter,frequency,"console");
        var signal=QioService.state(adapter); signal.filter.inverse=false; signal.filter.rules.add("minecraft:iron_ingot"); signal.threshold=100;
        world.getBlockAt(x+24,y-1,z+9).setType(Material.STONE,false);
        world.getBlockAt(x+24,y,z+9).setType(Material.REDSTONE_WIRE,true);
        q.tick(adapter); check(signal.signal,"QIO threshold asserts signal");
        check(((org.bukkit.block.data.AnaloguePowerable)world.getBlockAt(x+24,y,z+9).getBlockData()).getPower()==15,"QIO threshold notifies real redstone wire on");
        q.extract(frequency,"console",StoredItems.key(iron),Long.MAX_VALUE,true); q.tick(adapter); check(!signal.signal,"QIO threshold clears signal");
        check(((org.bukkit.block.data.AnaloguePowerable)world.getBlockAt(x+24,y,z+9).getBlockData()).getPower()==0,"QIO threshold notifies real redstone wire off");
        String upgradedFrequency=q.create("b5_upgrade_" + x + '_' + y + '_' + z,"console"); q.join(b,upgradedFrequency,"console");
        b.putInSlot("drive0",BukkitItems.create("mek:qio_drive_base")); q.insert(upgradedFrequency,"console",new ItemStack(Material.COPPER_INGOT),16000,true);
        ItemStack upgraded=StorageCrafting.upgrade(b.slotContent("drive0"),BukkitItems.create("mek:qio_drive_hyper_dense")); b.putInSlot("drive0",upgraded);
        check(q.entries(upgradedFrequency,"console").get(StoredItems.key(new ItemStack(Material.COPPER_INGOT)))==16000,"drive tier upgrade keeps items and identity");
        check(q.insert(upgradedFrequency,"console",new ItemStack(Material.COPPER_INGOT),Long.MAX_VALUE,true)==112000,"drive upgrade applies larger capacity");
        ItemStack corrupt=BukkitItems.create("mek:qio_drive_base"); var corruptMeta=corrupt.getItemMeta();
        corruptMeta.getPersistentDataContainer().set(new NamespacedKey("cemekanism","qio_contents"),org.bukkit.persistence.PersistentDataType.BYTE_ARRAY,new byte[]{1,2,3}); corrupt.setItemMeta(corruptMeta);
        a.putInSlot("drive1",corrupt);
        check(q.entries(frequency,"console").get(StoredItems.key(new ItemStack(Material.GOLD_INGOT)))==1,"corrupt drive isolated without losing healthy drive contents");
        a.putInSlot("drive1",null);
        // Four independent physical drives must merge, including repeated types and full metadata.
        List<ItemStack> sources = new ArrayList<>(); String mergeFrequency = q.create("b5_merge_" + x + '_' + y + '_' + z, "console"); q.join(b, mergeFrequency, "console");
        for (int i = 0; i < 4; i++) {
            b.putInSlot("drive0", BukkitItems.create("mek:qio_drive_base"));
            q.insert(mergeFrequency, "console", new ItemStack(Material.DIAMOND), i + 1, true);
            sources.add(b.slotContent("drive0").clone()); b.putInSlot("drive0", null);
        }
        ItemStack[] matrix = sources.toArray(ItemStack[]::new);
        ItemStack merged = q.mergeDrives(BukkitItems.create("mek:qio_drive_hyper_dense"), matrix, true);
        b.putInSlot("drive0", merged);
        check(q.entries(mergeFrequency, "console").get(StoredItems.key(new ItemStack(Material.DIAMOND))) == 10,
                "four-drive upgrade merges every disk");
        CompoundTag mergedSnapshot = new CompoundTag(); b.saveCustomData(mergedSnapshot); b.putInSlot("drive0", null); b.loadCustomData(mergedSnapshot);
        check(q.entries(mergeFrequency, "console").get(StoredItems.key(new ItemStack(Material.DIAMOND))) == 10,
                "merged drive survives array reload");
        try { q.mergeDrives(BukkitItems.create("mek:qio_drive_hyper_dense"), new ItemStack[]{sources.getFirst(), sources.getFirst(), sources.get(2), sources.get(3)}, false);
            throw new IllegalStateException("duplicate source disk upgrade accepted");
        } catch (IllegalArgumentException expected) { checks++; }
        // One upgraded batch spans multiple slots and types but obeys the common 320-item budget.
        check(importer.machine().installUpgrade("mek:speed_upgrade", 8) == 8, "QIO speed upgrade support");
        input.getInventory().clear();
        for (int i = 0; i < 4; i++) input.getInventory().setItem(i, new ItemStack(Material.IRON_INGOT, 64));
        input.getInventory().setItem(4, new ItemStack(Material.GOLD_INGOT, 64));
        q.tick(importer); check(input.getInventory().isEmpty(), "QIO multi-slot multi-type upgraded batch");
        QioService.state(importer).importWithoutFilter = false;
        input.getInventory().setItem(0, new ItemStack(Material.COAL, 1)); q.tick(importer);
        check(!input.getInventory().isEmpty(), "QIO disabled unfiltered import");
        QioService.state(importer).importWithoutFilter = true;
    }
    private void heaterAndLaser() {
        var heater = machine(world, x + 40, y, z + 20, "mek:resistive_heater");
        var logic = (ResistiveHeaterLogic) heater.machine().blueprint().logic();
        logic.setUsage(heater.machine(), 10000); heater.machine().energyTank().orElseThrow().set(null, 150000);
        CompoundTag snapshot = new CompoundTag(); heater.saveCustomData(snapshot); logic.setUsage(heater.machine(), 250); heater.loadCustomData(snapshot);
        check(logic.usage(heater.machine()) == 10000 && heater.machine().energyTank().orElseThrow().amount() == 150000,
                "heater dynamic buffer and power persist through CE NBT");
        double before = heater.machine().heatNode().temperatureK(); heater.machine().tick();
        check(Math.abs(heater.machine().heatNode().temperatureK() - before - 60) < 1e-6, "heater power converts to heat");
        check(heater.machine().side("energy", Direction.EAST) == SideMode.INPUT
                && heater.machine().side("energy", Direction.NORTH) == SideMode.NONE, "heater upstream left/right energy faces");
        heater.machine().heatNode().setTemperatureK(4000);
        check(heater.machine().heatNode().temperatureK() == 4000, "heater has no artificial 3000K cap");
        var laser = machine(world, x + 45, y, z + 20, "mek:laser");
        var amplifier = machine(world, x + 45, y, z + 17, "mek:laser_amplifier");
        world.getBlockAt(x + 45, y, z + 19).setType(Material.AIR, false);
        world.getBlockAt(x + 45, y, z + 18).setType(Material.AIR, false);
        laser.machine().energyTank().orElseThrow().set(null, 20);
        long stored = amplifier.machine().energyTank().orElseThrow().amount(); laser.machine().tick();
        check(amplifier.machine().energyTank().orElseThrow().amount() == stored + 20
                && laser.machine().energyTank().orElseThrow().amount() == 0, "basic laser feeds amplifier");
        world.getBlockAt(x + 45, y, z + 19).setType(Material.STONE, false);
        laser.machine().energyTank().orElseThrow().set(null, 20); laser.machine().tick();
        check(amplifier.machine().energyTank().orElseThrow().amount() == stored + 20, "solid block occludes laser");
    }
    private void miner() {
        for(int a=(x-32)>>4;a<=(x+32)>>4;a++) for(int c=(z-32)>>4;c<=(z+32)>>4;c++) world.getChunkAt(a,c).setForceLoaded(true);
        var c=machine(world,x,y,z+20,"mek:digital_miner"); var logic=(DigitalMinerLogic)c.machine().blueprint().logic(); var s=logic.state(c.machine());
        for(int a=-32;a<=32;a++) for(int b=-32;b<=32;b++) world.getBlockAt(x+a,y-1,z+20+b).setType(Material.AIR,false);
        logic.configure(c.machine(),32,y-1,y-1); s.filter.rules.add("minecraft:diamond_ore"); s.silk=true; s.replace="minecraft:cobblestone"; s.running=true;
        world.getBlockAt(x-32,y-1,z-12).setType(Material.DIAMOND_ORE,false); world.getBlockAt(x+32,y-1,z+52).setType(Material.DIAMOND_ORE,false);
        c.putInSlot("replace",new ItemStack(Material.COBBLESTONE,1));
        for(int i=0;i<10;i++){c.machine().energyTank().orElseThrow().set(null,50000);c.machine().tick();}
        int progress=s.progress; CompoundTag snapshot=new CompoundTag(); c.saveCustomData(snapshot); c.loadCustomData(snapshot);
        check(s.progress==progress && progress>0,"mining progress survives save/load");
        for(int i=0;i<400;i++){c.machine().energyTank().orElseThrow().set(null,50000);c.machine().tick();}
        check(s.mined==1 && world.getBlockAt(x-32,y-1,z-12).getType()==Material.COBBLESTONE,"65x65 min edge mined and backfilled");
        check(world.getBlockAt(x+32,y-1,z+52).getType()==Material.DIAMOND_ORE
                && s.status.equals("missing_material"), "missing replacement preserves max edge");
        c.putInSlot("replace",new ItemStack(Material.COBBLESTONE,1));
        for(int i=0;i<100;i++){c.machine().energyTank().orElseThrow().set(null,50000);c.machine().tick();}
        check(s.mined==2 && world.getBlockAt(x+32,y-1,z+52).getType()==Material.COBBLESTONE,"65x65 max edge resumed");
        check(c.slotContent("out").getType()==Material.DIAMOND_ORE && c.slotContent("out").getAmount()==2,"silk-touch real drops");
        logic.configure(c.machine(),0,y-2,y-2); s.running=true; s.replace=""; s.filter.inverse=true;
        world.getBlockAt(x,y-2,z+20).setType(Material.DIAMOND_ORE,false); c.machine().tick();
        check(world.getBlockAt(x,y-2,z+20).getType()==Material.DIAMOND_ORE,"blacklist preserves selected ore");
        logic.reset(c.machine()); s.filter.inverse=false; s.running=true; s.buffer.insert(StoredItems.key(new ItemStack(Material.COAL)),1728,true);
        c.putInSlot("out",new ItemStack(Material.COBBLESTONE,64)); long energy=c.machine().energyTank().orElseThrow().amount(); c.machine().tick();
        check(s.cursor==0 && c.machine().energyTank().orElseThrow().amount()==energy && world.getBlockAt(x,y-2,z+20).getType()==Material.DIAMOND_ORE,"full buffer no destruction or energy draw");
        s.running=false;

        // Per-filter replacement and auto-pull: the rule, not the global replace field, chooses cobblestone.
        c.putInSlot("out",null);
        for(var entry:new ArrayList<>(s.buffer.entries().entrySet()))s.buffer.extract(entry.getKey(),entry.getValue(),true);
        world.getBlockAt(x,y+1,z+20).setType(Material.CHEST,false);
        var pull=(org.bukkit.block.Chest)world.getBlockAt(x,y+1,z+20).getState();
        pull.getSnapshotInventory().clear(); pull.getSnapshotInventory().setItem(0,new ItemStack(Material.COBBLESTONE,2)); pull.update(true,false);
        logic.configure(c.machine(),1,y-1,y-1); s.filter.rules.clear(); s.filter.add("minecraft:diamond_ore");
        s.filter.setReplacementTarget("minecraft:diamond_ore","minecraft:cobblestone");
        s.filter.setRequiresReplacement("minecraft:diamond_ore",true);
        s.replace=""; s.autoPull=true; s.autoEject=false; s.running=true;
        world.getBlockAt(x+1,y-1,z+20).setType(Material.DIAMOND_ORE,false);
        for(int i=0;i<100;i++){c.machine().energyTank().orElseThrow().set(null,50000);c.machine().tick();}
        pull=(org.bukkit.block.Chest)world.getBlockAt(x,y+1,z+20).getState();
        check(world.getBlockAt(x+1,y-1,z+20).getType()==Material.COBBLESTONE
                        && pull.getInventory().getItem(0).getAmount()==1,
                "per-filter replacement auto-pulls from top inventory");

        // Auto eject sends the staged output to the machine's back container without voiding leftovers.
        world.getBlockAt(x,y,z+21).setType(Material.CHEST,false);
        var eject=(org.bukkit.block.Chest)world.getBlockAt(x,y,z+21).getState();
        eject.getSnapshotInventory().clear(); eject.update(true,false);
        s.running=false; s.autoEject=true; c.machine().tick();
        eject=(org.bukkit.block.Chest)world.getBlockAt(x,y,z+21).getState();
        check(eject.getInventory().getItem(0)!=null && eject.getInventory().getItem(0).getType()==Material.DIAMOND_ORE
                        && StoredItems.empty(c.slotContent("out")),
                "auto eject transfers real output to back container");

        // Container blocks contribute both the block item and their inventory exactly once.
        for(var entry:new ArrayList<>(s.buffer.entries().entrySet()))s.buffer.extract(entry.getKey(),entry.getValue(),true);
        c.putInSlot("out",null);
        logic.configure(c.machine(),0,y-3,y-3); s.filter.rules.clear(); s.filter.add("minecraft:chest");
        s.filter.inverse=false; s.replace=""; s.autoPull=false; s.autoEject=false; s.running=true;
        world.getBlockAt(x,y-3,z+20).setType(Material.CHEST,false);
        var minedChest=(org.bukkit.block.Chest)world.getBlockAt(x,y-3,z+20).getState();
        minedChest.getSnapshotInventory().clear(); minedChest.getSnapshotInventory().setItem(0,new ItemStack(Material.GOLD_INGOT,3)); minedChest.update(true,false);
        for(int i=0;i<100;i++){c.machine().energyTank().orElseThrow().set(null,50000);c.machine().tick();}
        ItemStack staged=c.slotContent("out");
        long stagedChest=!StoredItems.empty(staged)&&staged.getType()==Material.CHEST?staged.getAmount():0;
        long stagedGold=!StoredItems.empty(staged)&&staged.getType()==Material.GOLD_INGOT?staged.getAmount():0;
        check(world.getBlockAt(x,y-3,z+20).getType()==Material.AIR
                        && s.buffer.count(StoredItems.key(new ItemStack(Material.CHEST)))+stagedChest==1
                        && s.buffer.count(StoredItems.key(new ItemStack(Material.GOLD_INGOT)))+stagedGold==3,
                "container block drops include contents exactly once");
        MachineGui.updateAnchors(c);
        check(GuiText.isOverlay(c.inventory().getItem(0)) && GuiText.isOverlay(c.inventory().getItem(8)),
                "miner main controls and filter button render");
        s.filter.setReplacementTarget("minecraft:chest","minecraft:cobblestone");
        s.filter.setRequiresReplacement("minecraft:chest",true);
        s.autoPull=true; s.autoEject=true; s.inverseRequiresReplacement=true; s.visuals=true;
        CompoundTag minerSnapshot=new CompoundTag(); c.saveCustomData(minerSnapshot);
        s.autoPull=false; s.autoEject=false; s.inverseRequiresReplacement=false; s.visuals=false;
        s.filter.setReplacementTarget("minecraft:chest","");
        c.loadCustomData(minerSnapshot);
        check(s.autoPull && s.autoEject && s.inverseRequiresReplacement && s.visuals
                        && s.filter.replacementTarget("minecraft:chest").equals("minecraft:cobblestone")
                        && s.filter.requiresReplacement("minecraft:chest"),
                "miner switches and per-filter settings survive restart");

        check(c.machine().installUpgrade("mek:anchor_upgrade",1)==1,"miner anchor upgrade allowed");
        logic.configure(c.machine(),32,y-1,y-1); s.running=true;
        int width=65;
        long edge=(32L+32L)*width+(32L+32L);
        s.cursor=new MiningCursor(32,y-1,y-1,edge).index();
        MiningChunkTickets.sync(c);
        check(world.getPluginChunkTickets(c.x()>>4,c.z()>>4).contains(plugin)
                        && world.getPluginChunkTickets((c.x()+32)>>4,(c.z()+32)>>4).contains(plugin),
                "anchor follows miner and active target chunk");
        check(!world.getPluginChunkTickets((c.x()-32)>>4,(c.z()-32)>>4).contains(plugin),
                "anchor does not pin the whole configured square");
        c.machine().removeUpgrade("mek:anchor_upgrade",1); MiningChunkTickets.sync(c);
        check(!world.getPluginChunkTickets(c.x()>>4,c.z()>>4).contains(plugin),"removing anchor releases chunk ticket");
    }
    private void pumpAndSorter() {
        var pump=machine(world,x+10,y,z+10,"mek:electric_pump"); world.getBlockAt(x+10,y-1,z+10).setType(Material.WATER,false);
        check(pump.machine().installUpgrade("mek:filter_upgrade",1)==1,"pump filter upgrade allowed");
        for(int i=0;i<19;i++){pump.machine().energyTank().orElseThrow().set(null,50000);pump.machine().tick();}
        check(pump.machine().tank("fluid").orElseThrow().amount()==10 && pump.machine().tank("fluid").orElseThrow().resourceId().equals("mek:heavy_water"),"pump heavy water source conversion");
        var sorter=machine(world,x+15,y,z+10,"mek:logistical_sorter");
        world.getBlockAt(x+15,y,z+11).setType(Material.CHEST); var source=(org.bukkit.block.Chest)world.getBlockAt(x+15,y,z+11).getState(); source.getInventory().clear();
        world.getBlockAt(x+15,y,z+9).setType(Material.CHEST); var dest=(org.bukkit.block.Chest)world.getBlockAt(x+15,y,z+9).getState(); dest.getInventory().clear();
        source.getInventory().setItem(0,new ItemStack(Material.IRON_INGOT,8)); for(int i=0;i<10;i++)sorter.machine().tick();
        check(source.getInventory().isEmpty() && dest.getInventory().getItem(0).getAmount()==8,"sorter real inventory transfer");
        place(world,x+20,y,z+10,"mek:basic_logistical_transporter"); var pipe=plugin.transmitterAt(world,x+20,y,z+10);
        ItemStack named=new ItemStack(Material.PAPER); var meta=named.getItemMeta();meta.displayName(net.kyori.adventure.text.Component.text("B5 payload")); named.setItemMeta(meta);
        pipe.itemNode().insert(ItemRef.withPayload("minecraft:paper",1,named)); CompoundTag snapshot=new CompoundTag();pipe.saveCustomData(snapshot);pipe.loadCustomData(snapshot);
        check(pipe.itemNode().buffer().get(0).payload() instanceof ItemStack restored && restored.isSimilar(named),"transporter payload persists");
    }
}
