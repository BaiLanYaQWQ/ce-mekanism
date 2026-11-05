package net.kuzuanpa.cemekanism.platform;

import mekcore.item.ItemHandler;
import mekcore.item.ItemMatcher;
import mekcore.item.ItemRef;
import mekcore.item.ItemSlotKind;
import mekcore.item.ItemSlotSpec;
import mekcore.item.ItemSlotView;
import mekcore.resource.Transaction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * 机器的物品面（文档 03 适配层 2）：真实物品堆在 Bukkit 容器的格子里，这里把它们映射成核心的槽位。
 * 规则与核心自带的 SimpleItemStore 一致：自动化只进输入槽、抽取只出输出槽、扣料只扣输入槽，
 * 槽位顺序 = ItemSlotSpec 声明顺序（并行位按组展开），GUI 第几格由前端自己映射。
 */
public final class BukkitItemHandler implements ItemHandler {

    private record Slot(String name, ItemSlotKind kind, ItemMatcher accepts, long stackLimit,
                        int group, int index) {
    }

    private final String id;
    private final Inventory inventory;
    private final List<Slot> slots = new ArrayList<>();
    private final int groupCount;
    private final Runnable onChanged;

    public BukkitItemHandler(String id, Inventory inventory, int groupCount, ItemSlotSpec... specs) {
        this(id, inventory, groupCount, () -> { }, specs);
    }

    /** onChanged：物品真动了时回调（机器用它标记方块实体待保存）。 */
    public BukkitItemHandler(String id, Inventory inventory, int groupCount, Runnable onChanged,
                             ItemSlotSpec... specs) {
        this(id, inventory, groupCount, onChanged, null, specs);
    }

    /**
     * containerIndices：核心槽位 → Bukkit 容器格号。面板画在哪一格，槽位就必须落到哪一格；
     * 顺序与展开后的 ItemSlotSpec 一致。null 表示按声明顺序连续编号（旧行为）。
     */
    public BukkitItemHandler(String id, Inventory inventory, int groupCount, Runnable onChanged,
                             List<Integer> containerIndices, ItemSlotSpec... specs) {
        this.id = id;
        this.inventory = inventory;
        this.groupCount = Math.max(1, groupCount);
        this.onChanged = onChanged;
        int index = 0;
        for (ItemSlotSpec spec : specs) {
            if (spec.perParallel()) {
                for (int group = 0; group < this.groupCount; group++) {
                    int containerIndex = containerIndices == null ? index : containerIndices.get(index);
                    this.slots.add(new Slot(spec.slotName(group), spec.kind(), spec.accepts(),
                            spec.stackLimit(), group, containerIndex));
                    index++;
                }
            } else {
                int containerIndex = containerIndices == null ? index : containerIndices.get(index);
                this.slots.add(new Slot(spec.name(), spec.kind(), spec.accepts(), spec.stackLimit(),
                        -1, containerIndex));
                index++;
            }
        }
        if (containerIndices != null && containerIndices.size() != index) {
            throw new IllegalArgumentException("槽位映射长度 " + containerIndices.size()
                    + " 与展开槽位 " + index + " 不一致");
        }
    }

    /**
     * 容器要开多大：并行位展开后的格子总数，再向上取整到 9 的倍数
     * （Bukkit 的自定义容器只收 9..54 且必须是 9 的倍数；多出来的空格是留给前端摆位的）。
     */
    public static int containerSize(int groupCount, List<ItemSlotSpec> specs) {
        int total = 0;
        for (ItemSlotSpec spec : specs) {
            total += spec.slotCount(Math.max(1, groupCount));
        }
        return Math.max(9, (total + 8) / 9 * 9);
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public int groupCount() {
        return this.groupCount;
    }

    /** 槽位名 → 容器里的格子号（前端按名字摆位、升级槽要按它读写）。 */
    public int indexOf(String slotName) {
        for (Slot slot : this.slots) {
            if (slot.name.equals(slotName)) {
                return slot.index;
            }
        }
        // 调试/命令里习惯写 in/out；工厂展开后的真实槽位是 in#0/out#0。
        for (Slot slot : this.slots) {
            if (slot.name.equals(slotName + "#0")) {
                return slot.index;
            }
        }
        return -1;
    }

    /** 容器格子号 → 槽位种类；空出来的摆位格返回 null。 */
    public ItemSlotKind kindAt(int index) {
        for (Slot slot : this.slots) {
            if (slot.index == index) {
                return slot.kind;
            }
        }
        return null;
    }

    /** 玩家手动放料算"输入"的格子：INPUT 与灌注机的 infuse_in（核心把它当 EXTRA）。 */
    public boolean inputLike(int index) {
        for (Slot slot : this.slots) {
            if (slot.index != index) {
                continue;
            }
            return slot.kind == ItemSlotKind.INPUT || "infuse_in".equals(slot.name);
        }
        return false;
    }

    public String nameAt(int index) {
        for (Slot slot : this.slots) {
            if (slot.index == index) {
                return slot.name;
            }
        }
        return null;
    }

    @Override
    public List<ItemSlotView> slots() {
        return this.slots.stream().map(this::view).toList();
    }

    @Override
    public List<ItemSlotView> slots(ItemSlotKind kind) {
        return this.slots.stream().filter(slot -> slot.kind == kind).map(this::view).toList();
    }

    @Override
    public List<ItemSlotView> slots(int group, ItemSlotKind kind) {
        return this.slots.stream().filter(slot -> slot.kind == kind && inGroup(slot, group))
                .map(this::view).toList();
    }

    @Override
    public long count(int group, ItemSlotKind kind, ItemMatcher matcher) {
        long total = 0;
        for (Slot slot : this.slots) {
            ItemStack stack = content(slot);
            if (slot.kind == kind && inGroup(slot, group) && stack != null
                    && matcher.matches(BukkitItemHandler.ref(stack))) {
                total += stack.getAmount();
            }
        }
        return total;
    }

    @Override
    public long roomFor(ItemRef ref) {
        ItemStack template = templateOf(ref);
        if (template == null) {
            return 0;
        }
        long total = 0;
        for (Slot slot : this.slots) {
            if (slot.kind == ItemSlotKind.INPUT && accepts(slot, ref)) {
                total += freeSpace(slot, template);
            }
        }
        return total;
    }

    @Override
    public long insert(ItemRef ref, Transaction tx) {
        return place(ref, tx, slot -> slot.kind == ItemSlotKind.INPUT, -1);
    }

    @Override
    public ItemRef extractCandidate(Predicate<ItemRef> accepted) {
        for (Slot slot : this.slots) {
            ItemStack stack = content(slot);
            if ((slot.kind == ItemSlotKind.OUTPUT || net.kuzuanpa.cemekanism.storage.PersonalStorageLogic.isStorage(this.id)) && stack != null) {
                ItemRef candidate = ref(stack);
                if (accepted.test(candidate)) {
                    return candidate;
                }
            }
        }
        return null;
    }

    @Override
    public long extract(ItemRef wanted, Transaction tx) {
        long left = wanted.amount();
        boolean changed = false;
        for (Slot slot : this.slots) {
            if (left == 0) {
                break;
            }
            ItemStack stack = content(slot);
            if ((slot.kind != ItemSlotKind.OUTPUT && !net.kuzuanpa.cemekanism.storage.PersonalStorageLogic.isStorage(this.id)) || stack == null || !sameContent(stack, wanted)) {
                continue;
            }
            long taken = Math.min(left, stack.getAmount());
            if (tx.execute()) {
                if (taken == stack.getAmount()) {
                    this.inventory.setItem(slot.index, null);
                } else {
                    stack.setAmount(stack.getAmount() - (int) taken);
                }
                changed = true;
            }
            left -= taken;
        }
        if (changed) {
            this.onChanged.run();
        }
        return wanted.amount() - left;
    }

    @Override
    public long roomForOutput(int group, ItemRef ref) {
        ItemStack template = templateOf(ref);
        if (template == null) {
            return 0;
        }
        long total = 0;
        for (Slot slot : this.slots) {
            if (slot.kind == ItemSlotKind.OUTPUT && inGroup(slot, group) && accepts(slot, ref)) {
                total += freeSpace(slot, template);
            }
        }
        return total;
    }

    @Override
    public long insertOutput(int group, ItemRef ref, Transaction tx) {
        return place(ref, tx, slot -> slot.kind == ItemSlotKind.OUTPUT, group);
    }

    @Override
    public boolean consume(int group, ItemMatcher matcher, long amount) {
        if (count(group, ItemSlotKind.INPUT, matcher) < amount) {
            return false;
        }
        long left = amount;
        // 先扣本组的槽位，再扣整机共享槽位。
        for (int pass = 0; pass < 2 && left > 0; pass++) {
            for (Slot slot : this.slots) {
                if (left == 0) {
                    break;
                }
                ItemStack stack = content(slot);
                if (slot.kind != ItemSlotKind.INPUT || stack == null
                        || !matcher.matches(ref(stack))) {
                    continue;
                }
                if (pass == 0 != (slot.group == group)) {
                    continue;
                }
                long taken = Math.min(left, stack.getAmount());
                if (taken == stack.getAmount()) {
                    this.inventory.setItem(slot.index, null);
                } else {
                    stack.setAmount(stack.getAmount() - (int) taken);
                }
                left -= taken;
            }
        }
        this.onChanged.run();
        return left == 0;
    }

    @Override
    public String describe() {
        StringBuilder sb = new StringBuilder();
        for (Slot slot : this.slots) {
            ItemStack stack = content(slot);
            if (stack == null) {
                continue;
            }
            if (!sb.isEmpty()) {
                sb.append(", ");
            }
            sb.append(slot.name).append('=').append(stack.getType()).append(" ×").append(stack.getAmount());
        }
        return sb.isEmpty() ? "(空)" : sb.toString();
    }

    /** 真正落格子：一格里放不下就顺延到下一个合用的格子。 */
    private long place(ItemRef ref, Transaction tx, Predicate<Slot> usable, int group) {
        ItemStack template = templateOf(ref);
        if (template == null) {
            return 0;
        }
        long left = ref.amount();
        boolean changed = false;
        for (Slot slot : this.slots) {
            if (left == 0) {
                break;
            }
            if (!usable.test(slot) || !inGroup(slot, group) || !accepts(slot, ref)) {
                continue;
            }
            long accepted = Math.min(freeSpace(slot, template), left);
            if (accepted <= 0) {
                continue;
            }
            if (tx.execute()) {
                ItemStack current = content(slot);
                if (current == null) {
                    ItemStack placed = template.clone();
                    placed.setAmount((int) accepted);
                    this.inventory.setItem(slot.index, placed);
                } else {
                    current.setAmount(current.getAmount() + (int) accepted);
                }
                changed = true;
            }
            left -= accepted;
        }
        if (changed) {
            this.onChanged.run();
        }
        return ref.amount() - left;
    }

    /** 槽位吃的 id 与载荷都对得上：载荷为 null（核心的配方产出、管道请求）当通配。 */
    private boolean accepts(Slot slot, ItemRef ref) {
        if (!slot.accepts.matches(ref)) {
            return false;
        }
        ItemStack current = content(slot);
        return current == null || sameContent(current, ref);
    }

    private long freeSpace(Slot slot, ItemStack template) {
        ItemStack current = content(slot);
        long limit = Math.min(slot.stackLimit,
                current == null ? template.getMaxStackSize() : current.getMaxStackSize());
        return Math.max(0L, limit - (current == null ? 0 : current.getAmount()));
    }

    private static boolean sameContent(ItemStack current, ItemRef wanted) {
        if (!BukkitItems.idOf(current).equals(wanted.id())) {
            return false;
        }
        return !(wanted.payload() instanceof ItemStack template) || current.isSimilar(template);
    }

    private static ItemRef ref(ItemStack stack) {
        ItemStack payload = stack.clone();
        payload.setAmount(1);
        return ItemRef.withPayload(BukkitItems.idOf(stack), stack.getAmount(), payload);
    }

    private ItemStack templateOf(ItemRef ref) {
        if (ref.payload() instanceof ItemStack stack) {
            ItemStack template = stack.clone();
            template.setAmount(1);
            return template;
        }
        return BukkitItems.create(ref.id());
    }

    private ItemStack content(Slot slot) {
        ItemStack stack = this.inventory.getItem(slot.index);
        return stack == null || stack.getType().isAir() ? null : stack;
    }

    private ItemSlotView view(Slot slot) {
        ItemStack stack = content(slot);
        return new ItemSlotView(slot.name, slot.kind, slot.group,
                stack == null ? null : ref(stack), slot.stackLimit);
    }

    private static boolean inGroup(Slot slot, int group) {
        return slot.group < 0 || group < 0 || slot.group == group;
    }
}
