package net.kuzuanpa.cemekanism.storage;

import mekcore.machine.*;
import net.kuzuanpa.cemekanism.CeMekanismPlugin;
import net.kuzuanpa.cemekanism.machine.MachineBlockEntityController;
import net.kuzuanpa.cemekanism.platform.BukkitItems;
import net.kuzuanpa.cemekanism.platform.CompoundDataTag;
import net.momirealms.craftengine.libraries.nbt.*;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.inventory.ItemStack;
import java.io.File;
import java.nio.file.*;
import java.util.*;

/** Cross-world frequency directory. Items live on removable drives, never in a phantom global inventory. */
public final class QioService {
    public record DriveTier(long count, int types) { }
    public static final Map<String, DriveTier> TIERS = Map.of(
            "mek:qio_drive_base", new DriveTier(16000, 128),
            "mek:qio_drive_hyper_dense", new DriveTier(128000, 256),
            "mek:qio_drive_time_dilating", new DriveTier(1048000, 1024),
            "mek:qio_drive_supermassive", new DriveTier(16000000000L, 8192));
    private static final NamespacedKey CONTENT = new NamespacedKey("cemekanism", "qio_contents");
    private static final NamespacedKey COUNT = new NamespacedKey("cemekanism", "qio_drive_count");
    private static final NamespacedKey ID = new NamespacedKey("cemekanism", "qio_drive_id");
    private final CeMekanismPlugin plugin;
    private final Set<MachineBlockEntityController> arrays = new LinkedHashSet<>();
    private final Map<String, Frequency> frequencies = new LinkedHashMap<>();
    private File file;
    private record CachedDrive(DriveTier tier, CountedStore store) { }
    private final Map<String, CachedDrive> driveCache = new HashMap<>();
    private final Set<String> invalidDisks = new HashSet<>();
    private Path drivePath(String uuid) {
        if (!uuid.matches("[a-f0-9-]{36}")) throw new IllegalArgumentException("无效驱动器身份");
        return plugin.getDataFolder().toPath().resolve("qio-drives").resolve(uuid + ".dat");
    }
    private void saveDrive(String uuid, DriveTier tier, CountedStore store) {
        CompoundTag tag = new CompoundTag(); store.save(new CompoundDataTag(tag));
        try {
            Path target = drivePath(uuid); Files.createDirectories(target.getParent());
            Path temporary = target.resolveSibling(target.getFileName() + ".tmp");
            Files.write(temporary, NBT.writeCompressed(tag, false));
            Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            driveCache.put(uuid, new CachedDrive(tier, store));
        } catch (java.io.IOException e) { throw new IllegalStateException("驱动器存档失败，操作中止", e); }
    }

    private CountedStore readDrive(ItemStack item, DriveTier tier) {
        var data = item.getItemMeta().getPersistentDataContainer();
        String uuid = data.get(ID, PersistentDataType.STRING);
        CountedStore store = new CountedStore(tier.count(), tier.types());
        CachedDrive cached = uuid == null ? null : driveCache.get(uuid);
        if (cached != null && cached.tier.equals(tier)) {
            var snapshot = new mekcore.persist.MapDataTag(); cached.store.save(snapshot); store.load(snapshot);
        } else {
            byte[] bytes = data.get(CONTENT, PersistentDataType.BYTE_ARRAY);
            if (uuid != null && Files.isRegularFile(drivePath(uuid))) {
                try { bytes = Files.readAllBytes(drivePath(uuid)); }
                catch (java.io.IOException e) { throw new IllegalStateException("无法读取 QIO 驱动器", e); }
            }
            if (bytes != null) store.load(new CompoundDataTag(NBT.readCompressed(bytes, false)));
            else if (uuid != null) throw new IllegalArgumentException("驱动器数据文件缺失：" + uuid);
        }
        return store;
    }

    /** Upstream QIORecipeData merges all four drives, rejecting combined type overflow. */
    public ItemStack mergeDrives(ItemStack result, ItemStack[] matrix, boolean execute) {
        DriveTier tier = TIERS.get(BukkitItems.idOf(result));
        if (tier == null) throw new IllegalArgumentException("产物不是驱动器");
        List<CountedStore> sources = new ArrayList<>(); Set<String> seen = new HashSet<>();
        for (ItemStack item : matrix) {
            if (StoredItems.empty(item)) continue;
            DriveTier sourceTier = TIERS.get(BukkitItems.idOf(item)); if (sourceTier == null) continue;
            if (item.getAmount() != 1) throw new IllegalArgumentException("驱动器不能堆叠");
            String uuid = item.getItemMeta().getPersistentDataContainer().get(ID, PersistentDataType.STRING);
            if (uuid != null) {
                if (!seen.add(uuid)) throw new IllegalArgumentException("不能合并重复身份的驱动器");
                for (var array : arrays) for (int i = 0; i < 12; i++) {
                    ItemStack online = array.slotContent("drive" + i);
                    if (!StoredItems.empty(online) && uuid.equals(online.getItemMeta().getPersistentDataContainer().get(ID, PersistentDataType.STRING)))
                        throw new IllegalArgumentException("该驱动器身份仍在线，无法合并");
                }
            }
            sources.add(readDrive(item, sourceTier));
        }
        if (sources.size() != 4) throw new IllegalArgumentException("升级需要四个驱动器");
        CountedStore merged = CountedStore.merge(tier.count(), tier.types(), sources);
        ItemStack upgraded = BukkitItems.create(BukkitItems.idOf(result));
        var meta = upgraded.getItemMeta();
        if (execute && merged.total() > 0) {
            String identity = UUID.randomUUID().toString(); saveDrive(identity, tier, merged);
            meta.getPersistentDataContainer().set(ID, PersistentDataType.STRING, identity);
        }
        meta.getPersistentDataContainer().set(COUNT, PersistentDataType.LONG, merged.total());
        meta.setMaxStackSize(1); upgraded.setItemMeta(meta); return upgraded;
    }

    public QioService(CeMekanismPlugin plugin) { this.plugin = plugin; }
    public void install() {
        file = new File(plugin.getDataFolder(), "qio-frequencies.yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        for (Map<?, ?> row : yaml.getMapList("frequencies")) {
            String key = Objects.toString(row.get("id"), ""); String owner = Objects.toString(row.get("owner"), "");
            if (key.isEmpty() || owner.isEmpty()) throw new IllegalStateException("损坏的 QIO 频率记录");
            Set<String> trusted = new LinkedHashSet<>();
            if (row.get("trusted") instanceof List<?> list) for (Object value : list) trusted.add(value.toString());
            frequencies.put(key, new Frequency(key, owner, SecurityLevel.valueOf(row.get("security").toString()), 0, trusted));
        }
    }
    private void persist() {
        YamlConfiguration yaml = new YamlConfiguration(); List<Map<String, Object>> rows = new ArrayList<>();
        for (Frequency f : frequencies.values()) rows.add(Map.of("id", f.id(), "owner", f.owner(), "security", f.security().name(), "trusted", new ArrayList<>(f.trusted())));
        yaml.set("frequencies", rows);
        try {
            Files.createDirectories(file.toPath().getParent()); Path temporary = file.toPath().resolveSibling(file.getName() + ".tmp");
            yaml.save(temporary.toFile()); Files.move(temporary, file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (Exception e) { throw new IllegalStateException("QIO 频率存档失败", e); }
    }
    public String create(String name, String actor) {
        if (!name.matches("[A-Za-z0-9_-]{1,48}")) throw new IllegalArgumentException("频率名只能含字母、数字、下划线或连字符");
        String id = actor + '/' + name;
        if (!frequencies.containsKey(id)) { frequencies.put(id, Frequency.of(id, actor)); persist(); }
        return id;
    }
    public Frequency require(String key, String actor) {
        Frequency f = frequencies.get(key);
        if (f == null || !f.accessibleBy(actor)) throw new IllegalArgumentException("频率不存在或无权访问");
        return f;
    }
    public List<Frequency> available(String actor) {
        return frequencies.values().stream().filter(f -> f.accessibleBy(actor))
                .sorted(Comparator.comparing(Frequency::id)).toList();
    }
    public void security(String key, String actor, SecurityLevel level, String trust, boolean add) {
        Frequency old = require(key, actor);
        if (!old.owner().equals(actor)) throw new IllegalArgumentException("仅频率所有者可修改权限");
        Frequency next = old;
        if (level != null) next = next.withSecurity(level);
        if (trust != null) { Set<String> names = new LinkedHashSet<>(next.trusted()); if (add) names.add(trust); else names.remove(trust); next = next.withTrusted(names); }
        frequencies.put(key, next);
        try { persist(); } catch (RuntimeException e) { frequencies.put(key, old); throw e; }
    }
    public static QioLogic.State state(MachineBlockEntityController c) { return ((QioLogic) c.machine().blueprint().logic()).state(c.machine()); }
    public void attach(MachineBlockEntityController c) { if (c.blockId().equals("mek:qio_drive_array")) arrays.add(c); }
    public void detach(MachineBlockEntityController c) { arrays.remove(c); driveCache.clear(); }
    public void join(MachineBlockEntityController c, String key, String actor) {
        require(key, actor); require(key, c.machine().owner()); state(c).frequency = key; c.markChanged();
    }
    private boolean accessible(MachineBlockEntityController c) {
        Frequency f = frequencies.get(state(c).frequency); return f != null && f.accessibleBy(c.machine().owner());
    }
    public boolean online(MachineBlockEntityController c) {
        return accessible(c);
    }
    public final class Disk {
        final MachineBlockEntityController array;
        final String slot;
        final ItemStack item;
        final CountedStore store;
        final String uuid;
        final DriveTier tier;
        Disk(MachineBlockEntityController array, String slot, ItemStack item, DriveTier tier) {
            this.array = array; this.slot = slot; this.item = item.clone(); this.tier = tier;
            var meta = item.getItemMeta(); uuid = meta.getPersistentDataContainer().get(ID, PersistentDataType.STRING);
            store = readDrive(item, tier);
            if (uuid != null) driveCache.put(uuid, new CachedDrive(tier, store));
        }
        void save() {
            String identity = uuid == null ? UUID.randomUUID().toString() : uuid;
            // Persist the full inventory in its own atomic file; packets and carried items contain only a stable disk id.
            saveDrive(identity, tier, store);
            var meta = item.getItemMeta();
            meta.getPersistentDataContainer().remove(CONTENT);
            meta.getPersistentDataContainer().set(ID, PersistentDataType.STRING, identity);
            meta.getPersistentDataContainer().set(COUNT, PersistentDataType.LONG, store.total());
            meta.setMaxStackSize(1); item.setItemMeta(meta); array.putInSlot(slot, item);
        }
    }
    private List<Disk> disks(String frequency, String actor) {
        require(frequency, actor);
        List<Disk> all = new ArrayList<>(); Map<String, Integer> ids = new HashMap<>();
        for (var c : arrays) {
            for (int i = 0; i < 12; i++) {
                String slot = "drive" + i; ItemStack item = c.slotContent(slot);
                if (StoredItems.empty(item)) continue;
                DriveTier tier = TIERS.get(BukkitItems.idOf(item)); if (tier == null || item.getAmount() != 1) continue;
                String location = c.taskId() + ':' + slot;
                try {
                    Disk disk = new Disk(c, slot, item, tier); all.add(disk); invalidDisks.remove(location);
                    if (disk.uuid != null) ids.merge(disk.uuid, 1, Integer::sum);
                } catch (RuntimeException e) {
                    if (invalidDisks.add(location)) plugin.getLogger().warning("QIO 驱动器隔离 " + location + "：" + e.getMessage());
                }
            }
        }
        driveCache.keySet().retainAll(ids.keySet());
        // Duplicate physical drive identities are unavailable on every frequency, including cross-world arrays.
        return all.stream().filter(d -> (d.uuid == null || ids.get(d.uuid) == 1) && state(d.array).frequency.equals(frequency)
                && accessible(d.array)).toList();
    }
    public long insert(String frequency, String actor, ItemStack item, long amount, boolean execute) {
        if (amount <= 0 || StoredItems.empty(item) || TIERS.containsKey(BukkitItems.idOf(item))) return 0;
        String key = StoredItems.key(item); long left = Math.max(0, amount);
        List<Disk> disks = disks(frequency, actor);
        // Existing item types first so that empty type slots remain available for new types.
        for (int pass = 0; pass < 2; pass++) for (Disk disk : disks) {
            if ((disk.store.count(key) > 0) != (pass == 0)) continue;
            long n = disk.store.insert(key, left, execute); if (execute && n > 0) disk.save(); left -= n;
            if (left == 0) return amount;
        }
        return amount - left;
    }
    public long extract(String frequency, String actor, String key, long amount, boolean execute) {
        if (amount <= 0) return 0;
        long left = Math.max(0, amount);
        for (Disk disk : disks(frequency, actor)) {
            long n = disk.store.extract(key, left, execute); if (execute && n > 0) disk.save(); left -= n;
            if (left == 0) break;
        }
        return amount - left;
    }
    public Map<String, Long> entries(String frequency, String actor) {
        Map<String, Long> result = new LinkedHashMap<>();
        for (Disk disk : disks(frequency, actor)) disk.store.entries().forEach((k, n) -> result.merge(k, n, Math::addExact));
        return result;
    }
    public String describe(MachineBlockEntityController c, String actor) {
        String key = state(c).frequency; if (key.isEmpty()) return "未选择频率";
        long count = 0, capacity = 0; int types = 0, disks = 0;
        for (Disk d : disks(key, actor)) { count += d.store.total(); capacity += d.store.capacity(); types += d.store.typeLimit(); disks++; }
        return key + " · 在线盘 " + disks + " · " + count + '/' + capacity + " 个 · 类型槽合计 " + types;
    }
    public void tick(MachineBlockEntityController c) {
        if (!accessible(c)) { setSignal(c, false); c.machine().setState(WorkState.DISABLED); return; }
        var s = state(c); String actor = c.machine().owner(); String frequency = s.frequency;
        if (c.blockId().equals("mek:qio_redstone_adapter")) {
            long count = 0;
            if (!s.filter.rules.isEmpty()) for (var e : entries(frequency, actor).entrySet())
                if (s.filter.matches(StoredItems.item(e.getKey(), 1))) count = Math.addExact(count, e.getValue());
            setSignal(c, (count >= s.threshold) != s.inverseSignal); return;
        }
        boolean importer = c.blockId().equals("mek:qio_importer");
        InventoryAccess inv = InventoryAccess.adjacent(c, c.facing().opposite(), !importer);
        if (inv == null) { c.machine().setState(WorkState.NO_INPUT); return; }
        int speed = c.machine().upgrades().count("mek:speed_upgrade");
        int remaining = 64 + 32 * speed, maxTypes = Math.round(1 + speed / 2F);
        Set<String> movedTypes = new HashSet<>();
        boolean withoutFilter = importer ? s.importWithoutFilter : s.exportWithoutFilter;
        if (s.filter.rules.isEmpty() && !withoutFilter) { c.machine().setState(WorkState.IDLE); return; }
        if (importer) {
            for (int slot : inv.slots()) {
                ItemStack item = inv.inventory().getItem(slot);
                if (StoredItems.empty(item) || (!s.filter.rules.isEmpty() && !s.filter.matches(item))) continue;
                String key = StoredItems.key(item);
                if (movedTypes.size() >= maxTypes && !movedTypes.contains(key)) continue;
                int n = (int) insert(frequency, actor, item, Math.min(remaining, item.getAmount()), true);
                if (n > 0) { inv.take(slot, n); movedTypes.add(key); remaining -= n; }
                if (remaining == 0) break;
            }
        } else {
            for (var e : entries(frequency, actor).entrySet()) {
                ItemStack item = StoredItems.item(e.getKey(), 1);
                if (!s.filter.rules.isEmpty() && !s.filter.matches(item)) continue;
                int n = inv.insert(item, (int) Math.min(remaining, e.getValue()), false);
                if (n > 0) {
                    int removed = (int) extract(frequency, actor, e.getKey(), n, true);
                    int accepted = inv.insert(item, removed, true);
                    if (accepted < removed) insert(frequency, actor, item, removed - accepted, true);
                    if (accepted > 0) { movedTypes.add(e.getKey()); remaining -= accepted; }
                }
                if (remaining == 0 || movedTypes.size() >= maxTypes) break;
            }
        }
        c.machine().setState(movedTypes.isEmpty() ? WorkState.IDLE : WorkState.RUNNING);
    }
    private void setSignal(MachineBlockEntityController c, boolean signal) {
        var s = state(c); if (s.signalInitialized && s.signal == signal) return;
        s.signal = signal; StorageRedstone.update(c); s.signalInitialized = true; c.markChanged();
    }
    public void clearSignal(MachineBlockEntityController c) { setSignal(c, false); }
}
