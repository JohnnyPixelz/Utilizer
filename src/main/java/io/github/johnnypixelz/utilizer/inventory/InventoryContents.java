package io.github.johnnypixelz.utilizer.inventory;

import io.github.johnnypixelz.utilizer.inventory.slot.PositionedSlot;
import io.github.johnnypixelz.utilizer.inventory.slot.Slot;
import io.github.johnnypixelz.utilizer.plugin.Logs;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.*;

public class InventoryContents {

    private final CustomInventory inv;

    private final Map<Integer, InventoryItem> contents;

    private final Pagination pagination = new Pagination();

    InventoryContents(CustomInventory inv) {
        this.inv = inv;
        this.contents = new HashMap<>();
    }

    void handleClick(InventoryClickEvent event) {
        final int slot = event.getSlot();
        get(slot).ifPresent(item -> item.handleClick(event));
    }

    public CustomInventory inventory() {
        return inv;
    }

    public Pagination pagination() {
        return pagination;
    }

    public Optional<Slot> firstEmpty() {
        for (int i = 0; i < contents.size(); i++) {
            final Optional<InventoryItem> inventoryItem = get(i);
            if (inventoryItem.isEmpty()) return Optional.ofNullable(inv.getType().getSlotFromRaw(i));
        }

        return Optional.empty();
    }

    public Optional<InventoryItem> get(int row, int column) {
        return get(Slot.of(row, column));
    }

    public Optional<InventoryItem> get(int slot) {
        if (inv.getType().getSize() <= slot) {
            Logs.warn("Attempted to get item from slot %d while the maximum slots are %d.".formatted(slot, inventory().getInventory().getSize()));
            return Optional.empty();
        }

        return Optional.ofNullable(contents.get(slot));
    }

    public Optional<InventoryItem> get(Slot slot) {
        final Optional<Integer> rawSlot = slot.getRawSlot(inventory().getType());
        if (rawSlot.isEmpty()) {
            throw new IllegalArgumentException("slot %s out of bounds".formatted(slot));
        }

        return get(rawSlot.get());
    }

    public void set(int rawSlot, InventoryItem item) {
        if (inv.getType().getSize() <= rawSlot) {
            throw new IllegalArgumentException("rawSlot %d out of bounds".formatted(rawSlot));
        }

        get(rawSlot).ifPresent(mountedItem -> mountedItem.unmount(rawSlot));

        contents.put(rawSlot, item);

        item.mount(this, rawSlot);
    }

    public void set(int row, int column, InventoryItem item) {
        set(Slot.of(row, column), item);
    }

    public void set(Slot slot, InventoryItem item) {
        final Optional<Integer> rawSlot = slot.getRawSlot(inventory().getType());
        if (rawSlot.isEmpty()) {
            throw new IllegalArgumentException("slot %s out of bounds".formatted(slot));
        }
        set(rawSlot.get(), item);
    }

    public void add(InventoryItem item) {
        final Optional<Slot> optionalEmptySlot = firstEmpty();
        if (optionalEmptySlot.isEmpty()) return;

        set(optionalEmptySlot.get(), item);
    }

    public void fill(InventoryItem item) {
        for (int i = 0; i < inv.getType().getSize(); i++) {
            set(i, item);
        }
    }

    public void fillRow(int row, InventoryItem item) {
        for (PositionedSlot slot : inv.getType().getSlots()) {
            if (slot.getRow() != row) continue;

            set(slot, item);
        }
    }

    public void fillColumn(int column, InventoryItem item) {
        for (PositionedSlot slot : inv.getType().getSlots()) {
            if (slot.getColumn() != column) continue;

            set(slot, item);
        }
    }

    public void fillBorders(InventoryItem item) {
        final List<PositionedSlot> slots = inv.getType().getBorderSlots().get();
        for (Slot slot : slots) {
            set(slot, item);
        }
    }

    public void fillRect(int fromRow, int fromColumn, int toRow, int toColumn, InventoryItem item) {
        fillRect(Slot.of(fromRow, fromColumn), Slot.of(toRow, toColumn), item);
    }

    public void fillRect(Slot fromSlot, Slot toSlot, InventoryItem item) {
        final Optional<Integer> fromRawSlot = fromSlot.getRawSlot(inventory().getType());
        if (fromRawSlot.isEmpty()) {
            throw new IllegalArgumentException("fromRawSlot %s out of bounds".formatted(fromSlot));
        }

        final Optional<Integer> toRawSlot = toSlot.getRawSlot(inventory().getType());
        if (toRawSlot.isEmpty()) {
            throw new IllegalArgumentException("toRawSlot %s out of bounds".formatted(toSlot));
        }

        fillRect(fromRawSlot.get(), toRawSlot.get(), item);
    }

    public void fillRect(int fromRawSlot, int toRawSlot, InventoryItem item) {
        if (fromRawSlot > toRawSlot) {
            throw new IllegalArgumentException("fromRawSlot %d cannot be bigger than toRawSlot %d".formatted(fromRawSlot, toRawSlot));
        }

        List<Integer> slotsToFill = new ArrayList<>();

        final int inventorySize = inventory().getType().getSize();
        if (fromRawSlot < 0 || fromRawSlot >= inventorySize || toRawSlot >= inventorySize) {
            throw new IllegalArgumentException("rect out of bounds");
        }

        for (int i = fromRawSlot; i <= toRawSlot; i++) {
            slotsToFill.add(i);
        }

        slotsToFill.forEach(integer -> set(integer, item));
    }

    public void update() {
        for (int i = 0; i < contents.size(); i++) {
            final InventoryItem inventoryItem = contents.get(i);
            if (inventoryItem == null) continue;

            inventoryItem.unmount(i);
            inventoryItem.mount(this, i);
        }
    }

    public void clear() {
        contents.forEach((integer, item) -> item.unmount(integer));
        contents.clear();
    }

}
