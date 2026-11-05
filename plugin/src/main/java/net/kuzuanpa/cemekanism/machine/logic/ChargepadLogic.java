package net.kuzuanpa.cemekanism.machine.logic;

import mekcore.machine.Machine;
import mekcore.machine.MachineLogic;
import mekcore.machine.WorkState;
import net.kuzuanpa.cemekanism.machine.MachineContexts;
import net.kuzuanpa.cemekanism.platform.EnergyItems;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * 充电台（文档 04 第三节的逻辑机器分支）：站上去的玩家，每人每刻给一件随身储电物品充
 * {@code 单刻耗能} 那么多 J —— 上游 TileEntityChargepad 就是"每个目标每刻的限额"，
 * 所以这里的 energyPerTick 覆盖成"蓝图耗能 × 站着的人数"（没人站着时不耗电、直接睡）。
 */
public final class ChargepadLogic implements MachineLogic {

    @Override
    public long energyPerTick(Machine machine) {
        int players = playersOn(machine).size();
        return players == 0 ? 0L : machine.energyPerTick() * players;
    }

    @Override
    public boolean needsTick(Machine machine) {
        return !playersOn(machine).isEmpty();
    }

    @Override
    public long tick(Machine machine) {
        List<Player> players = playersOn(machine);
        if (players.isEmpty()) {
            machine.setState(WorkState.IDLE);
            return 0L;
        }
        long perPlayer = machine.energyPerTick();
        boolean charged = false;
        for (Player player : players) {
            charged |= chargeOne(player, perPlayer);
        }
        machine.setState(charged ? WorkState.RUNNING : WorkState.IDLE);
        return 0L;
    }

    @Override
    public String name() {
        return "充电台";
    }

    /** 每人每刻只充一件（上游就是这么写的：一件充满再换下一件）。 */
    private static boolean chargeOne(Player player, long joules) {
        ItemStack[] contents = player.getInventory().getContents();
        for (ItemStack stack : contents) {
            if (stack == null || stack.getType().isAir()) {
                continue;
            }
            if (EnergyItems.charge(stack, joules) > 0) {
                return true;
            }
        }
        return false;
    }

    /** 站在台上的玩家：上游取的是方块上表面 0.4 格高的那一块（背面挡板上的也够得着）。 */
    private static List<Player> playersOn(Machine machine) {
        MachineContexts.Place place = MachineContexts.of(machine);
        if (place == null) {
            return List.of();
        }
        World world = place.world();
        return List.copyOf(world.getNearbyPlayers(new org.bukkit.Location(world,
                place.x() + 0.5, place.y() + 0.2, place.z() + 0.5), 0.9));
    }
}
