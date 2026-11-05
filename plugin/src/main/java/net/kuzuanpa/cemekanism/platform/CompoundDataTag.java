package net.kuzuanpa.cemekanism.platform;

import mekcore.persist.DataTag;
import net.momirealms.craftengine.libraries.nbt.ByteTag;
import net.momirealms.craftengine.libraries.nbt.CompoundTag;
import net.momirealms.craftengine.libraries.nbt.ListTag;
import net.momirealms.craftengine.libraries.nbt.NumericTag;
import net.momirealms.craftengine.libraries.nbt.StringTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * CE 的 CompoundTag 适配成核心的 DataTag（文档 03 适配层 1）。
 * ⚠ 类型名是 CE 重定位过的 {@code net.momirealms.craftengine.libraries.nbt}，不是 sparrow 原名。
 */
public final class CompoundDataTag implements DataTag {
    private final CompoundTag tag;

    public CompoundDataTag(CompoundTag tag) {
        this.tag = tag;
    }

    public CompoundTag tag() {
        return this.tag;
    }

    @Override
    public Set<String> keys() {
        return this.tag.keySet();
    }

    @Override
    public boolean has(String key) {
        return this.tag.containsKey(key);
    }

    @Override
    public Optional<String> getString(String key) {
        return this.tag.get(key) instanceof StringTag text ? Optional.of(text.value()) : Optional.empty();
    }

    @Override
    public Optional<Long> getLong(String key) {
        return numeric(key).map(NumericTag::getAsLong);
    }

    @Override
    public Optional<Integer> getInt(String key) {
        return numeric(key).map(NumericTag::getAsInt);
    }

    @Override
    public Optional<Double> getDouble(String key) {
        return numeric(key).map(NumericTag::getAsDouble);
    }

    @Override
    public Optional<Boolean> getBoolean(String key) {
        return this.tag.get(key) instanceof ByteTag flag
                ? Optional.of(flag.booleanValue())
                : numeric(key).map(number -> number.getAsLong() != 0L);
    }

    @Override
    public void putString(String key, String value) {
        this.tag.putString(key, value);
    }

    @Override
    public void putLong(String key, long value) {
        this.tag.putLong(key, value);
    }

    @Override
    public void putInt(String key, int value) {
        this.tag.putInt(key, value);
    }

    @Override
    public void putDouble(String key, double value) {
        this.tag.putDouble(key, value);
    }

    @Override
    public void putBoolean(String key, boolean value) {
        this.tag.putBoolean(key, value);
    }

    @Override
    public void remove(String key) {
        this.tag.remove(key);
    }

    @Override
    public DataTag child(String key) {
        CompoundTag child = this.tag.getCompound(key);
        if (child == null) {
            child = new CompoundTag();
            this.tag.put(key, child);
        }
        return new CompoundDataTag(child);
    }

    @Override
    public List<DataTag> list(String key) {
        ListTag list = this.tag.getList(key);
        if (list == null || list.isEmpty()) {
            return List.of();
        }
        List<DataTag> result = new ArrayList<>(list.size());
        for (int index = 0; index < list.size(); index++) {
            CompoundTag element = list.getCompound(index);
            if (element != null) {
                result.add(new CompoundDataTag(element));
            }
        }
        return result;
    }

    @Override
    public DataTag newElement(String key) {
        ListTag list = this.tag.getList(key);
        if (list == null) {
            list = new ListTag();
            this.tag.put(key, list);
        }
        CompoundTag element = new CompoundTag();
        list.add(element);
        return new CompoundDataTag(element);
    }

    private Optional<NumericTag> numeric(String key) {
        return this.tag.get(key) instanceof NumericTag number ? Optional.of(number) : Optional.empty();
    }

    @Override
    public String toString() {
        return this.tag.toString();
    }
}
