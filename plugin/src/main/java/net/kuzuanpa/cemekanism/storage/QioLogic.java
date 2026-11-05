package net.kuzuanpa.cemekanism.storage;

import mekcore.machine.*;
import mekcore.persist.DataTag;
import java.util.Map;
import java.util.WeakHashMap;

public final class QioLogic implements MachineLogic {
    public static final class State {
        public String frequency = "";
        public final ItemSelection filter = new ItemSelection();
        public long threshold = 1;
        public boolean inverseSignal, signal, signalInitialized;
        public boolean importWithoutFilter = true, exportWithoutFilter;
        public int ticks;
    }
    private final Map<Machine, State> states = new WeakHashMap<>();
    public State state(Machine m) { return states.computeIfAbsent(m, k -> new State()); }
    public long energyPerTick(Machine m) { return 0; }
    public boolean needsTick(Machine m) {
        return StorageRuntime.of(m) != null && (!state(m).frequency.isEmpty() || state(m).signal
                || m.blueprint().id().equals("mek:qio_redstone_adapter") && !state(m).signalInitialized)
                && !m.blueprint().id().equals("mek:qio_drive_array") && !m.blueprint().id().equals("mek:qio_dashboard");
    }
    public long tick(Machine m) {
        var c = StorageRuntime.of(m); if (c == null) return 0;
        State s = state(m);
        if (++s.ticks >= 10) { s.ticks = 0; c.plugin().qio().tick(c); }
        return 0;
    }
    public void save(Machine m, DataTag t) {
        State s = state(m); t.putString("frequency", s.frequency); t.putLong("threshold", s.threshold);
        t.putBoolean("import_without_filter", s.importWithoutFilter);
        t.putBoolean("export_without_filter", s.exportWithoutFilter);
        t.putBoolean("inverse_signal", s.inverseSignal); s.filter.save(t.child("filter"));
    }
    public void load(Machine m, DataTag t) {
        State s = state(m); s.frequency = t.getString("frequency", ""); s.threshold = Math.max(0, t.getLong("threshold", 1));
        s.inverseSignal = t.getBoolean("inverse_signal", false); if (t.has("filter")) s.filter.load(t.child("filter"));
        s.importWithoutFilter = t.getBoolean("import_without_filter", true);
        s.exportWithoutFilter = t.getBoolean("export_without_filter", false);
    }
}
