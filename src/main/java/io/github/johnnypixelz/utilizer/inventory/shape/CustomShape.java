package io.github.johnnypixelz.utilizer.inventory.shape;

import com.google.common.collect.ImmutableList;
import io.github.johnnypixelz.utilizer.inventory.slot.PositionedSlot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomShape implements InventoryShape {
    private final List<PositionedSlot> slots;
    private final List<PositionedSlot> borderSlots;

    public CustomShape(List<PositionedSlot> slots, List<PositionedSlot> borderSlots) {
        this.slots = ImmutableList.copyOf(slots);
        this.borderSlots = ImmutableList.copyOf(borderSlots);
    }

    @Override
    public int getSize() {
        return slots.size();
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
