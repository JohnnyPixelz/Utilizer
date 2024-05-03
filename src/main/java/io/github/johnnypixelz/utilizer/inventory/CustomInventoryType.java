package io.github.johnnypixelz.utilizer.inventory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.bukkit.event.inventory.InventoryType;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;


public enum CustomInventoryType {
    CHEST_1(InventoryType.CHEST, () -> computeSlots(1, 9), () -> computeBorderSlots(1, 9)),
    CHEST_2(InventoryType.CHEST, () -> computeSlots(2, 9), () -> computeBorderSlots(2, 9)),
    CHEST_3(InventoryType.CHEST, () -> computeSlots(3, 9), () -> computeBorderSlots(3, 9)),
    CHEST_4(InventoryType.CHEST, () -> computeSlots(4, 9), () -> computeBorderSlots(4, 9)),
    CHEST_5(InventoryType.CHEST, () -> computeSlots(5, 9), () -> computeBorderSlots(5, 9)),
    CHEST_6(InventoryType.CHEST, () -> computeSlots(6, 9), () -> computeBorderSlots(6, 9)),
    WORKBENCH(InventoryType.WORKBENCH, () -> {
        final List<Slot> slots = computeSlots(3, 3);
        slots.add(Slot.of(3, 0));
        return slots;
    });

    private static List<Slot> computeSlots(int rows, int columns) {
        List<Slot> slots = new ArrayList<>();

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                slots.add(Slot.of(row, column));
            }
        }

        return slots;
    }

    private static List<Slot> computeBorderSlots(int rows, int columns) {
        List<Slot> slots = new ArrayList<>();

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                if (row != 0 && row != rows - 1 && column != 0 && column != columns - 1) continue;
                slots.add(Slot.of(row, column));
            }
        }

        return slots;
    }

    private final InventoryType inventoryType;
    private final Map<Slot, Integer> slotToRawMap;
    private final Map<Integer, Slot> rawToSlotMap;
    private final List<Slot> slotList;
    private final int size;
    private final Supplier<List<Slot>> borderSlotsSupplier;

    CustomInventoryType(InventoryType inventoryType, Slot... slots) {
        this(inventoryType, () -> Arrays.asList(slots));
    }

    CustomInventoryType(InventoryType inventoryType, Supplier<List<Slot>> slotSupplier) {
        this(inventoryType, slotSupplier, null);
    }

    CustomInventoryType(InventoryType inventoryType, Supplier<List<Slot>> slotSupplier, Supplier<List<Slot>> borderSlotsSupplier) {
        Map<Slot, Integer> temporarySlotsMap = new HashMap<>();

        final List<Slot> slots = slotSupplier.get();
        for (int i = 0; i < slots.size(); i++) {
            temporarySlotsMap.put(slots.get(i), i);
        }

        this.slotToRawMap = ImmutableMap.copyOf(temporarySlotsMap);

        final Map<Integer, Slot> slotToRawMapReversed = slotToRawMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        this.rawToSlotMap = ImmutableMap.copyOf(slotToRawMapReversed);

        this.slotList = ImmutableList.copyOf(temporarySlotsMap.keySet());
        this.size = this.slotToRawMap.size();
        this.borderSlotsSupplier = borderSlotsSupplier;
        this.inventoryType = inventoryType;
    }

    public InventoryType getInventoryType() {
        return inventoryType;
    }

    public int getSize() {
        return size;
    }

    public Map<Slot, Integer> getSlotToRawMap() {
        return slotToRawMap;
    }

    public Map<Integer, Slot> getRawToSlotMap() {
        return rawToSlotMap;
    }

    public List<Slot> getSlots() {
        return slotList;
    }

    public Optional<Integer> getRawSlot(Slot slot) {
        return Optional.ofNullable(slotToRawMap.get(slot));
    }

    public Slot getSlotFromRaw(int rawSlot) {
        if (rawSlot >= size) {
            throw new IllegalStateException("rawSlot out of bounds");
        }

        return rawToSlotMap.get(rawSlot);
    }

    public Supplier<List<Slot>> getBorderSlots() {
        if (borderSlotsSupplier == null) {
            throw new IllegalStateException("Unsupported operation");
        }

        return borderSlotsSupplier;
    }

}
