package io.github.johnnypixelz.utilizer.inventories.slot;

import io.github.johnnypixelz.utilizer.inventories.shape.InventoryShape;

import java.util.Objects;

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
    public int getRawSlot(InventoryShape inventoryShape) {
        return inventoryShape.getRawSlotFromPositionedSlot(this);
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
