package io.github.johnnypixelz.utilizer.inventory.shape;

import io.github.johnnypixelz.utilizer.inventory.slot.PositionedSlot;

import java.util.List;

public interface InventoryShape {

    static InventoryShape rectangular(int rows, int columns) {
        return new RectangularShape(rows, columns);
    }

    static InventoryShape custom(List<PositionedSlot> slots, List<PositionedSlot> borderSlots) {
        return new CustomShape(slots, borderSlots);
    }

    int getSize();

    List<PositionedSlot> getSlots();

    PositionedSlot getPositionedSlotFromRawSlot(int rawSlot);

    List<PositionedSlot> getBorderSlots();

    int getRawSlotFromPositionedSlot(PositionedSlot slot);

    InventoryShape getSubShape(int fromRawSlot, int toRawSlot);

}
