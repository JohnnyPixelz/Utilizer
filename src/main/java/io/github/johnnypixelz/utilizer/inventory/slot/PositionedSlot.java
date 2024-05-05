package io.github.johnnypixelz.utilizer.inventory.slot;

import io.github.johnnypixelz.utilizer.inventory.CustomInventoryType;

import java.util.Objects;
import java.util.Optional;

public class PositionedSlot implements Slot {
    private final int row;
    private final int column;

    public PositionedSlot(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public Optional<Integer> getRawSlot(CustomInventoryType customInventoryType) {
        return Optional.ofNullable(customInventoryType.getSlotToRawMap().get(this));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PositionedSlot that)) return false;
        return getRow() == that.getRow() && getColumn() == that.getColumn();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRow(), getColumn());
    }

    @Override
    public String toString() {
        return "PositionedSlot{" +
                "row=" + row +
                ", column=" + column +
                '}';
    }

}
