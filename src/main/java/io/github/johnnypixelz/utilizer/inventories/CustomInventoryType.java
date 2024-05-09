package io.github.johnnypixelz.utilizer.inventories;

import io.github.johnnypixelz.utilizer.inventory.shape.InventoryShape;
import io.github.johnnypixelz.utilizer.inventory.slot.PositionedSlot;
import org.bukkit.event.inventory.InventoryType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public enum CustomInventoryType {
    CHEST_1(InventoryType.CHEST, InventoryShape.rectangular(1, 9)),
    CHEST_2(InventoryType.CHEST, InventoryShape.rectangular(2, 9)),
    CHEST_3(InventoryType.CHEST, InventoryShape.rectangular(3, 9)),
    CHEST_4(InventoryType.CHEST, InventoryShape.rectangular(4, 9)),
    CHEST_5(InventoryType.CHEST, InventoryShape.rectangular(5, 9)),
    CHEST_6(InventoryType.CHEST, InventoryShape.rectangular(6, 9)),
    WORKBENCH(InventoryType.WORKBENCH, () -> {
        final List<PositionedSlot> slots = computeSlots(3, 3);
        slots.add(new PositionedSlot(3, 0));

        final List<PositionedSlot> borderSlots = computeBorderSlots(3, 3);

        return InventoryShape.custom(slots, borderSlots);
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
    private final InventoryShape inventoryShape;

    CustomInventoryType(InventoryType inventoryType, InventoryShape inventoryShape) {
        this.inventoryType = inventoryType;
        this.inventoryShape = inventoryShape;
    }

    CustomInventoryType(InventoryType inventoryType, Supplier<InventoryShape> inventoryShapeSupplier) {
        this.inventoryType = inventoryType;
        this.inventoryShape = inventoryShapeSupplier.get();
    }

    public InventoryType getInventoryType() {
        return inventoryType;
    }

    public InventoryShape getInventoryShape() {
        return inventoryShape;
    }
}
