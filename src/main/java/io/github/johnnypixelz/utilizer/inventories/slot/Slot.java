package io.github.johnnypixelz.utilizer.inventories.slot;

import io.github.johnnypixelz.utilizer.inventories.shape.InventoryShape;

public interface Slot {

    int getRawSlot(InventoryShape inventoryShape);

    static Slot of(int row, int column) {
        return new PositionedSlot(row, column);
    }

    static Slot of(int rawSlot) {
        return new RawSlot(rawSlot);
    }

}
