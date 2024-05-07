package io.github.johnnypixelz.utilizer.inventory.shape;

import com.google.common.collect.ImmutableList;
import io.github.johnnypixelz.utilizer.inventory.slot.PositionedSlot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RectangularShape implements InventoryShape {
    private final int rows;
    private final int columns;
    private final List<PositionedSlot> slots;
    private final List<PositionedSlot> borderSlots;

    public RectangularShape(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;

        final List<PositionedSlot> positionedSlots = new ArrayList<>();

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                positionedSlots.add(new PositionedSlot(row, column));
            }
        }

        this.slots = ImmutableList.copyOf(positionedSlots);

        final List<PositionedSlot> borderSlots = new ArrayList<>();

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                if (row != 0 && row != rows - 1 && column != 0 && column != columns - 1) continue;
                borderSlots.add(new PositionedSlot(row, column));
            }
        }

        this.borderSlots = ImmutableList.copyOf(borderSlots);
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    @Override
    public int getSize() {
        return rows * columns;
    }

    @Override
    public List<PositionedSlot> getSlots() {
        return slots;
    }

    @Override
    public PositionedSlot getPositionedSlotFromRawSlot(int rawSlot) {
        if (rawSlot < 0 || rawSlot >= getSize()) {
            throw new IllegalArgumentException("rawSlot %d out of bounds [%d, %d]".formatted(rawSlot, 0, getSize() - 1));
        }

        return slots.get(rawSlot);
    }

    @Override
    public int getRawSlotFromPositionedSlot(PositionedSlot slot) {
        final int i = slots.indexOf(slot);

        if (i == -1) {
            throw new IllegalArgumentException("slot %s out of bounds".formatted(slot));
        }

        return slots.indexOf(slot);
    }

    @Override
    public List<PositionedSlot> getBorderSlots() {
        return borderSlots;
    }

    @Override
    public InventoryShape getSubShape(int fromRawSlot, int toRawSlot) {
        final List<PositionedSlot> positionedSlots = new ArrayList<>();

        for (int rawSlot = fromRawSlot; rawSlot <= toRawSlot; rawSlot++) {
            final PositionedSlot positionedSlot = getPositionedSlotFromRawSlot(rawSlot);
            positionedSlots.add(positionedSlot);
        }

        return new CustomShape(positionedSlots, Collections.emptyList());
    }

}
