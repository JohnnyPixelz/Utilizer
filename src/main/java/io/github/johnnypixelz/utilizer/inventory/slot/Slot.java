package io.github.johnnypixelz.utilizer.inventory.slot;

import io.github.johnnypixelz.utilizer.inventory.CustomInventoryType;

import java.util.Optional;

public interface Slot {

    Optional<Integer> getRawSlot(CustomInventoryType customInventoryType);

    static Slot of(int row, int column) {
        return new PositionedSlot(row, column);
    }

    static Slot of(int rawSlot) {
        return new RawSlot(rawSlot);
    }

}
