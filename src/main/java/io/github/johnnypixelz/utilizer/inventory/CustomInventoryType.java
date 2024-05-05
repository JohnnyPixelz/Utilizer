package io.github.johnnypixelz.utilizer.inventory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.github.johnnypixelz.utilizer.inventory.slot.PositionedSlot;
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
        final List<PositionedSlot> slots = computeSlots(3, 3);
        slots.add(new PositionedSlot(3, 0));
        return slots;
    });

    private static List<PositionedSlot> computeSlots(int rows, int columns) {
        List<PositionedSlot> slots = new ArrayList<>();

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                slots.add(new PositionedSlot(row, column));
            }
        }

        return slots;
    }

    private static List<PositionedSlot> computeBorderSlots(int rows, int columns) {
        List<PositionedSlot> slots = new ArrayList<>();

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                if (row != 0 && row != rows - 1 && column != 0 && column != columns - 1) continue;
                slots.add(new PositionedSlot(row, column));
            }
        }

        return slots;
    }

    private final InventoryType inventoryType;
    private final Map<PositionedSlot, Integer> slotToRawMap;
    private final Map<Integer, PositionedSlot> rawToSlotMap;
    private final List<PositionedSlot> slotList;
    private final int size;
    private final Supplier<List<PositionedSlot>> borderSlotsSupplier;

    CustomInventoryType(InventoryType inventoryType, PositionedSlot... slots) {
        this(inventoryType, () -> Arrays.asList(slots));
    }

    CustomInventoryType(InventoryType inventoryType, Supplier<List<PositionedSlot>> slotSupplier) {
        this(inventoryType, slotSupplier, null);
    }

    CustomInventoryType(InventoryType inventoryType, Supplier<List<PositionedSlot>> slotSupplier, Supplier<List<PositionedSlot>> borderSlotsSupplier) {
        Map<PositionedSlot, Integer> temporarySlotsMap = new HashMap<>();

        final List<PositionedSlot> slots = slotSupplier.get();
        for (int i = 0; i < slots.size(); i++) {
            temporarySlotsMap.put(slots.get(i), i);
        }

        this.slotToRawMap = ImmutableMap.copyOf(temporarySlotsMap);

        final Map<Integer, PositionedSlot> slotToRawMapReversed = slotToRawMap.entrySet()
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

    public Map<PositionedSlot, Integer> getSlotToRawMap() {
        return slotToRawMap;
    }

    public Map<Integer, PositionedSlot> getRawToSlotMap() {
        return rawToSlotMap;
    }

    public List<PositionedSlot> getSlots() {
        return slotList;
    }

    public PositionedSlot getSlotFromRaw(int rawSlot) {
        if (rawSlot >= size) {
            throw new IllegalStateException("rawSlot %d out of bounds".formatted(rawSlot));
        }

        return rawToSlotMap.get(rawSlot);
    }

    public Supplier<List<PositionedSlot>> getBorderSlots() {
        if (borderSlotsSupplier == null) {
            throw new IllegalStateException("Unsupported operation");
        }

        return borderSlotsSupplier;
    }

}
