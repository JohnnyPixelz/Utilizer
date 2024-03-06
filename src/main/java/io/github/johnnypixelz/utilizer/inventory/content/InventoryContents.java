package io.github.johnnypixelz.utilizer.inventory.content;

import io.github.johnnypixelz.utilizer.inventory.CustomInventory;
import io.github.johnnypixelz.utilizer.inventory.InventoryItem;
import io.github.johnnypixelz.utilizer.inventory.InventoryManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InventoryContents {

    private final CustomInventory inv;
    private final UUID player;

    private final InventoryItem[][] contents;

    private final Pagination pagination = new Pagination();
    private final Map<String, SlotIterator> iterators = new HashMap<>();
    private final Map<String, Object> properties = new HashMap<>();

    public InventoryContents(CustomInventory inv, UUID player) {
        this.inv = inv;
        this.player = player;
        this.contents = new InventoryItem[inv.getRows()][inv.getColumns()];
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

    public InventoryItem[][] all() {
        return contents;
    }

    public Optional<Slot> firstEmpty() {
        for (int row = 0; row < contents.length; row++) {
            for (int column = 0; column < contents[0].length; column++) {
                if (!this.get(row, column).isPresent())
                    return Optional.of(new Slot(row, column));
            }
        }

        return Optional.empty();
    }

    public Optional<InventoryItem> get(int row, int column) {
        if (row >= contents.length)
            return Optional.empty();
        if (column >= contents[row].length)
            return Optional.empty();

        return Optional.ofNullable(contents[row][column]);
    }

    public Optional<InventoryItem> get(Slot slotPos) {
        return get(slotPos.getRow(), slotPos.getColumn());
    }

    public InventoryContents set(int row, int column, InventoryItem item) {
        if (row >= contents.length)
            return this;
        if (column >= contents[row].length)
            return this;

        contents[row][column] = item;
        update(row, column, item != null ? item.getItem() : null);
        return this;
    }

    public InventoryContents set(Slot slotPos, InventoryItem item) {
        return set(slotPos.getRow(), slotPos.getColumn(), item);
    }

    public InventoryContents add(InventoryItem item) {
        for (int row = 0; row < contents.length; row++) {
            for (int column = 0; column < contents[0].length; column++) {
                if (contents[row][column] == null) {
                    set(row, column, item);
                    return this;
                }
            }
        }

        return this;
    }

    public InventoryContents fill(InventoryItem item) {
        for (int row = 0; row < contents.length; row++)
            for (int column = 0; column < contents[row].length; column++)
                set(row, column, item);

        return this;
    }

    public InventoryContents fillRow(int row, InventoryItem item) {
        if (row >= contents.length)
            return this;

        for (int column = 0; column < contents[row].length; column++)
            set(row, column, item);

        return this;
    }

    public InventoryContents fillColumn(int column, InventoryItem item) {
        for (int row = 0; row < contents.length; row++)
            set(row, column, item);

        return this;
    }

    public InventoryContents fillBorders(InventoryItem item) {
        fillRect(0, 0, inv.getRows() - 1, inv.getColumns() - 1, item);
        return this;
    }

    public InventoryContents fillRect(int fromRow, int fromColumn, int toRow, int toColumn, InventoryItem item) {
        for (int row = fromRow; row <= toRow; row++) {
            for (int column = fromColumn; column <= toColumn; column++) {
                if (row != fromRow && row != toRow && column != fromColumn && column != toColumn)
                    continue;

                set(row, column, item);
            }
        }

        return this;
    }

    public InventoryContents fillRect(Slot fromPos, Slot toPos, InventoryItem item) {
        return fillRect(fromPos.getRow(), fromPos.getColumn(), toPos.getRow(), toPos.getColumn(), item);
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

    private void update(int row, int column, ItemStack item) {
        Player currentPlayer = Bukkit.getPlayer(player);
        if (!InventoryManager.getOpenedPlayers(inv).contains(currentPlayer))
            return;

        Inventory topInventory = currentPlayer.getOpenInventory().getTopInventory();
        topInventory.setItem(inv.getColumns() * row + column, item);
    }

}
