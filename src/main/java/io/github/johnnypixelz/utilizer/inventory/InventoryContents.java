package io.github.johnnypixelz.utilizer.inventory;

import io.github.johnnypixelz.utilizer.plugin.Logs;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.*;

public class InventoryContents {

    private final CustomInventory inv;

    private final Map<Integer, InventoryItem> contents;

    private final Pagination pagination = new Pagination();
    private final Map<String, Object> properties = new HashMap<>();

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
        final Optional<Integer> rawSlot = inv.getType().getRawSlot(Slot.of(row, column));
        if (rawSlot.isEmpty()) {
            throw new IllegalStateException("Slot out of bounds");
        }

        return Optional.ofNullable(contents.get(rawSlot.get()));
    }

    public Optional<InventoryItem> get(int slot) {
        if (inv.getType().getSize() <= slot) {
            Logs.warn("Attempted to get item from slot " + slot + " while the maximum slots are " + inv.getType().getSize() + ".");
            return Optional.empty();
        }

        return Optional.ofNullable(contents.get(slot));
    }

    public Optional<InventoryItem> get(Slot slotPos) {
        return get(slotPos.getRow(), slotPos.getColumn());
    }

    public void set(int rawSlot, InventoryItem item) {
        if (inv.getType().getSize() <= rawSlot) {
            throw new IllegalArgumentException("rawSlot out of bounds");
        }

        get(rawSlot).ifPresent(InventoryItem::unmount);

        contents.put(rawSlot, item);

        item.mount(this, rawSlot);
    }

    public void set(int row, int column, InventoryItem item) {
        final Optional<Integer> rawSlot = inv.getType().getRawSlot(Slot.of(row, column));
        if (rawSlot.isEmpty()) {
            throw new IllegalArgumentException("Slot position out of bounds");
        }

        set(rawSlot.get(), item);
    }

    public void set(Slot slotPos, InventoryItem item) {
        set(slotPos.getRow(), slotPos.getColumn(), item);
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
        for (Slot slot : inv.getType().getSlots()) {
            if (slot.getRow() != row) continue;

            set(slot, item);
        }
    }

    public void fillColumn(int column, InventoryItem item) {
        for (Slot slot : inv.getType().getSlots()) {
            if (slot.getColumn() != column) continue;

            set(slot, item);
        }
    }

    public void fillBorders(InventoryItem item) {
        final List<Slot> slots = inv.getType().getBorderSlots().get();
        for (Slot slot : slots) {
            set(slot, item);
        }
    }

    public void fillRect(int fromRow, int fromColumn, int toRow, int toColumn, InventoryItem item) {
        List<Integer> slotsToFill = new ArrayList<>();

        for (int row = fromRow; row <= toRow; row++) {
            for (int column = fromColumn; column <= toColumn; column++) {
                final Optional<Integer> optionalRawSlot = inv.getType().getRawSlot(Slot.of(row, column));
                if (optionalRawSlot.isEmpty()) {
                    throw new IllegalArgumentException("rect out of bounds");
                }

                slotsToFill.add(optionalRawSlot.get());
            }
        }

        slotsToFill.forEach(integer -> set(integer, item));
    }

    public void fillRect(Slot fromPos, Slot toPos, InventoryItem item) {
        fillRect(fromPos.getRow(), fromPos.getColumn(), toPos.getRow(), toPos.getColumn(), item);
    }

    @SuppressWarnings("unchecked")
    public <T> T property(String name) {
        return (T) properties.get(name);
    }

    @SuppressWarnings("unchecked")
    public <T> T property(String name, T def) {
        return properties.containsKey(name) ? (T) properties.get(name) : def;
    }

    public InventoryContents setProperty(String name, Object value) {
        properties.put(name, value);
        return this;
    }
    
    public void update() {
        for (int i = 0; i < contents.size(); i++) {
            final InventoryItem inventoryItem = contents.get(i);
            if (inventoryItem == null) continue;

            inventoryItem.unmount();
            inventoryItem.mount(this, i);
        }
    }

    public void clear() {
        contents.values().forEach(InventoryItem::unmount);
        contents.clear();
    }

}
