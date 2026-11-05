package net.kuzuanpa.cemekanism.machine;

import mekcore.machine.Direction;
import mekcore.machine.Machine;
import mekcore.machine.SideMode;
import mekcore.resource.ResourceType;
import mekcore.resource.ResourceTypes;
import net.kuzuanpa.cemekanism.i18n.I18n;
import net.kuzuanpa.cemekanism.transmitter.TransmitterBlockEntityController;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/** The selected function belongs to the tool; each resource function only edits its own ports. */
public enum ConfiguratorMode {
    ITEMS(null),
    FLUID(ResourceTypes.FLUID),
    CHEMICAL(ResourceTypes.CHEMICAL),
    ENERGY(ResourceTypes.ENERGY),
    HEAT(ResourceTypes.HEAT),
    INFUSION(ResourceTypes.INFUSION),
    REDSTONE(null),
    ROTATE(null);

    private static final NamespacedKey KEY = new NamespacedKey("cemekanism", "configurator_mode");
    private final ResourceType resource;

    ConfiguratorMode(ResourceType resource) {
        this.resource = resource;
    }

    public String zhName() {
        return I18n.tr("configurator.mode." + name().toLowerCase(Locale.ROOT));
    }

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
        if (this == ROTATE) return I18n.tr("configurator.unsupported");
        if (this == ITEMS) return I18n.enumName(machine.itemSide(face), machine.itemSide(face).zhName());
        if (this == REDSTONE) {
            return I18n.enumName(machine.redstoneControl(), machine.redstoneControl().zhName());
        }
        List<String> ports = ports(machine);
        if (ports.isEmpty()) return I18n.tr("configurator.unsupported");
        List<SideMode> modes = ports.stream().map(port -> machine.side(port, face)).distinct().toList();
        if (modes.size() == 1) return I18n.enumName(modes.getFirst(), modes.getFirst().zhName());
        return ports.stream().map(port -> port + " "
                        + I18n.enumName(machine.side(port, face), machine.side(port, face).zhName()))
                .collect(Collectors.joining(" · "));
    }

    public boolean cycle(MachineBlockEntityController controller, Direction face) {
        return this == ROTATE ? controller.face(face) : cycle(controller.machine(), face);
    }

    public String status(MachineBlockEntityController controller, Direction face) {
        if (this != ROTATE) return status(controller.machine(), face);
        String current = I18n.tr("configurator.facing", ConfiguratorGrid.name(controller.facing()));
        return current + (controller.canFace(face)
                ? I18n.tr("configurator.facing_target", ConfiguratorGrid.name(face))
                : I18n.tr("configurator.unsupported_direction"));
    }

    public boolean supports(TransmitterBlockEntityController pipe) {
        if (this == REDSTONE) return true;
        if (pipe.itemPipe()) return this == ITEMS;
        return pipe.family().resource().equals(this.resource);
    }

    public String status(TransmitterBlockEntityController pipe, Direction face) {
        if (this == REDSTONE) return I18n.tr(pipe.redstoneWatched() ? "state.on" : "state.off");
        return supports(pipe) ? I18n.enumName(pipe.port(face), pipe.port(face).zhName())
                : I18n.tr("configurator.unsupported");
    }

    public Component actionBar(Direction face, String status) {
        String text = face == null ? zhName() : I18n.tr("configurator.mode_status", zhName(), status);
        return Component.text(text, NamedTextColor.GREEN);
    }

    public Component actionBar(Player player, Direction face, String status) {
        String mode = I18n.tr(player, "configurator.mode." + name().toLowerCase(Locale.ROOT));
        String text = face == null ? mode : I18n.tr(player, "configurator.mode_status", mode, status);
        return Component.text(text, NamedTextColor.GREEN);
    }
}
