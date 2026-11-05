package net.kuzuanpa.cemekanism.multiblock;

import java.util.ArrayList;
import java.util.List;

/** Shared policy for saved valve modes, resource eligibility and real pipe direction. */
public final class MultiblockPortModes {
    private MultiblockPortModes() { }

    public static List<String> choices(MultiblockDef def, List<String> roles, boolean heat) {
        List<String> modes = new ArrayList<>();
        boolean resourcePort = false;
        for (String port : def.ports()) {
            if (!roles.contains(def.portRole(port))
                    || List.of("laser_focus", "coil", "controller").contains(def.portRole(port))) continue;
            resourcePort = true;
            String mode = def.portMode(port);
            if (!mode.isEmpty() && !modes.contains(mode)) modes.add(mode);
        }
        if (!resourcePort) return List.of();
        // RESOURCE is the persisted legacy bidirectional mode; keep its meaning and default.
        if (modes.isEmpty()) modes.addAll(List.of("RESOURCE", "INPUT", "OUTPUT"));
        if (heat) modes.add("HEAT");
        return List.copyOf(modes);
    }

    public static boolean allows(MultiblockDef def, String port, String role, List<String> roles, String mode) {
        if ("HEAT".equals(mode)) return false;
        String required = def.portMode(port);
        if (!required.isEmpty()) return def.portRole(port).equals(role) && required.equals(mode);
        if ("INPUT".equals(mode) || "OUTPUT".equals(mode)) {
            // Explicit direction applies to every resource role supported by this physical valve.
            return roles.contains(def.portRole(port)) && ("INPUT".equals(mode)
                    ? !"out".equals(def.flow(port)) : !"in".equals(def.flow(port)));
        }
        return def.portRole(port).equals(role);
    }

    public static String flow(String declared, String mode) {
        if ("INPUT".equals(mode)) return "in";
        if ("OUTPUT".equals(mode)) return "out";
        return declared;
    }
}
