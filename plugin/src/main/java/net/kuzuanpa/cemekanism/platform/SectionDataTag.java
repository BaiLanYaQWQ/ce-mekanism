package net.kuzuanpa.cemekanism.platform;

import mekcore.persist.DataTag;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 配置文件的只读 DataTag（系统参数那份 yml 用）。
 * 键用斜杠拼路径（`energy_cube/basic/capacity`），因为核心的 {@code V10Tables.load} 就是按这个口径读的；
 * 配置里没写的键一律"不在"，核心那边落回内置默认。
 */
public final class SectionDataTag implements DataTag {
    private final ConfigurationSection section;

    public SectionDataTag(ConfigurationSection section) {
        this.section = section;
    }

    @Override
    public Set<String> keys() {
        return this.section == null ? Set.of() : this.section.getKeys(false);
    }

    @Override
    public boolean has(String key) {
        return this.section != null && this.section.contains(path(key), true);
    }

    @Override
    public Optional<String> getString(String key) {
        return this.section != null && this.section.get(path(key)) instanceof String text
                ? Optional.of(text)
                : Optional.empty();
    }

    @Override
    public Optional<Long> getLong(String key) {
        return number(key).map(Number::longValue);
    }

    @Override
    public Optional<Integer> getInt(String key) {
        return number(key).map(Number::intValue);
    }

    @Override
    public Optional<Double> getDouble(String key) {
        return number(key).map(Number::doubleValue);
    }

    @Override
    public Optional<Boolean> getBoolean(String key) {
        return this.section != null && this.section.get(path(key)) instanceof Boolean flag
                ? Optional.of(flag)
                : Optional.empty();
    }

    @Override
    public void putString(String key, String value) {
        throw readOnly();
    }

    @Override
    public void putLong(String key, long value) {
        throw readOnly();
    }

    @Override
    public void putInt(String key, int value) {
        throw readOnly();
    }

    @Override
    public void putDouble(String key, double value) {
        throw readOnly();
    }

    @Override
    public void putBoolean(String key, boolean value) {
        throw readOnly();
    }

    @Override
    public void remove(String key) {
        throw readOnly();
    }

    @Override
    public DataTag child(String key) {
        ConfigurationSection child = this.section == null ? null : this.section.getConfigurationSection(path(key));
        return new SectionDataTag(child);
    }

    @Override
    public List<DataTag> list(String key) {
        return List.of();
    }

    @Override
    public DataTag newElement(String key) {
        throw readOnly();
    }

    private Optional<Number> number(String key) {
        return this.section != null && this.section.get(path(key)) instanceof Number value
                ? Optional.of(value)
                : Optional.empty();
    }

    private static String path(String key) {
        return key.replace('/', '.');
    }

    private static UnsupportedOperationException readOnly() {
        return new UnsupportedOperationException("系统参数配置是只读的，核心不会往它写东西");
    }
}
