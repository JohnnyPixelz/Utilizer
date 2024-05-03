package io.github.johnnypixelz.utilizer.inventory.content;

import io.github.johnnypixelz.utilizer.inventory.CustomInventory;
import io.github.johnnypixelz.utilizer.inventory.InventoryItem;
import io.github.johnnypixelz.utilizer.inventory.InventoryManager;
import io.github.johnnypixelz.utilizer.plugin.Logs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class InventoryContents {

    private final CustomInventory inv;

    private final List<InventoryItem> contents;

    private final Pagination pagination = new Pagination();
    private final Map<String, SlotIterator> iterators = new HashMap<>();
    private final Map<String, Object> properties = new HashMap<>();

    public InventoryContents(CustomInventory inv) {
        this.inv = inv;
        this.contents = new ArrayList<>();
    }

    public CustomInventory inventory() {
        return inv;
    }

    public Pagination pagination() {
        return pagination;
    }

    public Optional<SlotIterator> iterator(String id) {
        return Optional.ofNullable(this.iterators.get(id));
    }

    public SlotIterator newIterator(String id, SlotIterator.Type type, int startRow, int startColumn) {
        SlotIterator iterator = new SlotIterator(this, inv,
                type, startRow, startColumn);

        this.iterators.put(id, iterator);
        return iterator;
    }

    public SlotIterator newIterator(String id, SlotIterator.Type type, Slot startPos) {
        return newIterator(id, type, startPos.getRow(), startPos.getColumn());
    }

    public SlotIterator newIterator(SlotIterator.Type type, int startRow, int startColumn) {
        return new SlotIterator(this, inv, type, startRow, startColumn);
    }

    public SlotIterator newIterator(SlotIterator.Type type, Slot startPos) {
        return newIterator(type, startPos.getRow(), startPos.getColumn());
    }

    public List<InventoryItem> all() {
        return contents;
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

        contents.set(rawSlot, item);

        update(rawSlot, item.getItem());
    }

    public void set(int row, int column, InventoryItem item) {
        final Optional<Integer> rawSlot = inv.getType().getRawSlot(Slot.of(row, column));
        if (rawSlot.isEmpty()) {
            throw new IllegalArgumentException("Slot position out of bounds");
        }

        contents.set(rawSlot.get(), item);

        update(row, column, item != null ? item.getItem() : null);
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

    private void update(int rawSlot, ItemStack item) {
        if (rawSlot < 0 || rawSlot >= inventory().getType().getSize()) {
            throw new IllegalArgumentException("rawSlot out of bounds");
        }

        inventory().getInventory().setItem(rawSlot, item);
    }

    private void update(int row, int column, ItemStack item) {
        final Optional<Integer> optionalRawSlot = inv.getType().getRawSlot(Slot.of(row, column));
        if (optionalRawSlot.isEmpty()) {
            throw new IllegalArgumentException("slot out of bounds");
        }

        final Integer rawSlot = optionalRawSlot.get();

        update(rawSlot, item);
    }
    
    public void update() {
        inv.getInventory().clear();
        for (int i = 0; i < contents.size(); i++) {
            final InventoryItem inventoryItem = contents.get(i);
            if (inventoryItem == null) continue;

            inv.getInventory().setItem(i, inventoryItem.getItem());
        }
    }

    public void clear() {
        contents.clear();
        update();
    }

}
