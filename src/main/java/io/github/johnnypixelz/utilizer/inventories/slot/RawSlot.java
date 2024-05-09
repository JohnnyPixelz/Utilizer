package io.github.johnnypixelz.utilizer.inventories.slot;

import io.github.johnnypixelz.utilizer.inventories.shape.InventoryShape;

public class RawSlot implements Slot {
    private final int rawSlot;

    public RawSlot(int rawSlot) {
        this.rawSlot = rawSlot;
    }

    public int getRawSlot() {
        return rawSlot;
    }

    @Override
    public int getRawSlot(InventoryShape inventoryShape) {
        if (rawSlot < 0 || rawSlot >= inventoryShape.getSize()) {
            throw new IllegalArgumentException("rawSlot %s out of bounds [%d, %d]".formatted(this, 0, inventoryShape.getSize() - 1));
        }

        return rawSlot;
    }

    @Override
    public String toString() {
        return "RawSlot{" +
                "rawSlot=" + rawSlot +
                '}';
    }

}
