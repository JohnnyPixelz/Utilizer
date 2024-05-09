package io.github.johnnypixelz.utilizer.inventory.slot;

import io.github.johnnypixelz.utilizer.inventory.shape.InventoryShape;

public interface Slot {

    int getRawSlot(InventoryShape inventoryShape);

    static Slot of(int row, int column) {
        return new PositionedSlot(row, column);
    }

    static Slot of(int rawSlot) {
        return new RawSlot(rawSlot);
    }

}
