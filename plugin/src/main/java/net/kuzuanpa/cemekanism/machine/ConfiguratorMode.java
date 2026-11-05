package net.kuzuanpa.cemekanism.machine;

import mekcore.machine.Direction;
import mekcore.machine.Machine;
import mekcore.machine.SideMode;
import mekcore.resource.ResourceType;
import mekcore.resource.ResourceTypes;
import net.kuzuanpa.cemekanism.transmitter.TransmitterBlockEntityController;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/** The selected function belongs to the tool; each resource function only edits its own ports. */
public enum ConfiguratorMode {
    ITEMS("物品", null),
    FLUID("流体", ResourceTypes.FLUID),
    CHEMICAL("气体／化学品", ResourceTypes.CHEMICAL),
    ENERGY("能源", ResourceTypes.ENERGY),
    HEAT("热量", ResourceTypes.HEAT),
    INFUSION("辅料", ResourceTypes.INFUSION),
    REDSTONE("红石控制", null),
    ROTATE("旋转", null);

    private static final NamespacedKey KEY = new NamespacedKey("cemekanism", "configurator_mode");
    private final String zhName;
    private final ResourceType resource;

    ConfiguratorMode(String zhName, ResourceType resource) {
        this.zhName = zhName;
        this.resource = resource;
    }

    public String zhName() { return this.zhName; }

    public ConfiguratorMode next() {
        return values()[(ordinal() + 1) % values().length];
    }

    public static ConfiguratorMode of(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return ITEMS;
        String id = item.getItemMeta().getPersistentDataContainer().get(KEY, PersistentDataType.STRING);
        for (ConfiguratorMode mode : values()) {
            if (mode.name().toLowerCase(Locale.ROOT).equals(id)) return mode;
        }
        return ITEMS;
    }

    public void store(ItemStack item) {
        var meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(KEY, PersistentDataType.STRING, name().toLowerCase(Locale.ROOT));
        item.setItemMeta(meta);
    }

    private List<String> ports(Machine machine) {
        return machine.portNames().stream()
                .filter(port -> machine.handler(port).type().equals(this.resource)).toList();
    }

    /** False means this machine has no ports of the selected type. */
    public boolean cycle(Machine machine, Direction face) {
        if (this == ROTATE) return false; // Facing belongs to the CE block, not the core port table.
        if (this == ITEMS) {
            machine.cycleItemSide(face);
        } else if (this == REDSTONE) {
            machine.cycleRedstoneControl();
        } else {
            List<String> ports = ports(machine);
            if (ports.isEmpty()) return false;
            for (String port : ports) machine.cycleSide(port, face);
        }
        return true;
    }

    public String status(Machine machine, Direction face) {
        if (this == ROTATE) return "不支持";
        if (this == ITEMS) return machine.itemSide(face).zhName();
        if (this == REDSTONE) return machine.redstoneControl().zhName();
        List<String> ports = ports(machine);
        if (ports.isEmpty()) return "不支持";
        List<SideMode> modes = ports.stream().map(port -> machine.side(port, face)).distinct().toList();
        if (modes.size() == 1) return modes.getFirst().zhName();
        return ports.stream().map(port -> port + " " + machine.side(port, face).zhName())
                .collect(Collectors.joining(" · "));
    }

    public boolean cycle(MachineBlockEntityController controller, Direction face) {
        return this == ROTATE ? controller.face(face) : cycle(controller.machine(), face);
    }

    public String status(MachineBlockEntityController controller, Direction face) {
        if (this != ROTATE) return status(controller.machine(), face);
        String current = "朝向" + ConfiguratorGrid.name(controller.facing());
        return current + (controller.canFace(face) ? " → " + ConfiguratorGrid.name(face) : "（不支持该方向）");
    }

    public boolean supports(TransmitterBlockEntityController pipe) {
        if (this == REDSTONE) return true;
        if (pipe.itemPipe()) return this == ITEMS;
        return pipe.family().resource().equals(this.resource);
    }

    public String status(TransmitterBlockEntityController pipe, Direction face) {
        if (this == REDSTONE) return pipe.redstoneWatched() ? "开" : "关";
        return supports(pipe) ? pipe.port(face).zhName() : "不支持";
    }

    public Component actionBar(Direction face, String status) {
        String text = face == null ? this.zhName : this.zhName + " · " + status;
        return Component.text(text, NamedTextColor.GREEN);
    }
}
