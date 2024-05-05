package io.github.johnnypixelz.utilizer.inventory.slot;

import io.github.johnnypixelz.utilizer.inventory.CustomInventoryType;

import java.util.Optional;

public class RawSlot implements Slot {
    private final int rawSlot;

    public RawSlot(int rawSlot) {
        this.rawSlot = rawSlot;
    }

    public int getRawSlot() {
        return rawSlot;
    }

    @Override
    public Optional<Integer> getRawSlot(CustomInventoryType customInventoryType) {
        return rawSlot < 0 || rawSlot >= customInventoryType.getSize() ? Optional.empty() : Optional.of(rawSlot);
    }

    @Override
    public String toString() {
        return "RawSlot{" +
                "rawSlot=" + rawSlot +
                '}';
    }

}
