package net.kuzuanpa.cemekanism.storage;

import mekcore.persist.DataTag;
import net.kuzuanpa.cemekanism.i18n.I18n;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** A drive owns its count and type limits. Keys represent complete normalized item data, not item ids. */
public final class CountedStore {
    private final long capacity;
    private final int typeLimit;
    private final Map<String, Long> counts = new LinkedHashMap<>();
    private long total;

    public CountedStore(long capacity, int typeLimit) {
        if (capacity < 0 || typeLimit < 0) throw new IllegalArgumentException("negative capacity");
        this.capacity = capacity;
        this.typeLimit = typeLimit;
    }
    public long capacity() { return capacity; }
    public int typeLimit() { return typeLimit; }
    public long total() { return total; }
    public long count(String key) { return counts.getOrDefault(key, 0L); }
    public Map<String, Long> entries() { return Collections.unmodifiableMap(new LinkedHashMap<>(counts)); }
    public long insert(String key, long amount, boolean execute) {
        if (key == null || key.isEmpty() || amount <= 0 || (!counts.containsKey(key) && counts.size() >= typeLimit)) return 0;
        long accepted = Math.min(amount, capacity - total);
        if (execute && accepted > 0) { counts.merge(key, accepted, Long::sum); total += accepted; }
        return accepted;
    }
    public long extract(String key, long amount, boolean execute) {
        long taken = Math.min(Math.max(0, amount), count(key));
        if (execute && taken > 0) {
            long rest = count(key) - taken;
            if (rest == 0) counts.remove(key); else counts.put(key, rest);
            total -= taken;
        }
        return taken;
    }
    /** Merge into a fresh target, validating combined count and distinct types before publishing it. */
    public static CountedStore merge(long capacity, int types, Iterable<CountedStore> sources) {
        CountedStore result = new CountedStore(capacity, types);
        for (CountedStore source : sources) for (var entry : source.entries().entrySet()) {
            if (result.insert(entry.getKey(), entry.getValue(), true) != entry.getValue())
                throw new IllegalArgumentException(I18n.tr("qio.error.merge_capacity"));
        }
        return result;
    }
    public void save(DataTag tag) {
        tag.remove("entries");
        for (var entry : counts.entrySet()) {
            DataTag row = tag.newElement("entries");
            row.putString("item", entry.getKey()); row.putLong("count", entry.getValue());
        }
    }
    public void load(DataTag tag) {
        // Validate into a new store first: a corrupt/undersized drive must never silently truncate items.
        CountedStore restored = new CountedStore(capacity, typeLimit);
        for (DataTag row : tag.list("entries")) {
            String key = row.getString("item", ""); long n = row.getLong("count", 0);
            if (n <= 0 || restored.insert(key, n, true) != n) {
                throw new IllegalArgumentException(I18n.tr("qio.error.invalid_drive_contents"));
            }
        }
        counts.clear(); counts.putAll(restored.counts); total = restored.total;
    }
}
