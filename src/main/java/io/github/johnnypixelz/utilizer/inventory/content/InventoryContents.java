package io.github.johnnypixelz.utilizer.inventory.content;

import io.github.johnnypixelz.utilizer.inventory.InventoryItem;
import io.github.johnnypixelz.utilizer.inventory.InventoryManager;
import io.github.johnnypixelz.utilizer.inventory.CustomInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface InventoryContents {

    CustomInventory inventory();

    Pagination pagination();

    Optional<SlotIterator> iterator(String id);

    SlotIterator newIterator(String id, SlotIterator.Type type, int startRow, int startColumn);

    SlotIterator newIterator(SlotIterator.Type type, int startRow, int startColumn);

    SlotIterator newIterator(String id, SlotIterator.Type type, SlotPos startPos);

    SlotIterator newIterator(SlotIterator.Type type, SlotPos startPos);

    InventoryItem[][] all();

    Optional<SlotPos> firstEmpty();

    Optional<InventoryItem> get(int row, int column);

    Optional<InventoryItem> get(SlotPos slotPos);

    InventoryContents set(int row, int column, InventoryItem item);

    InventoryContents set(SlotPos slotPos, InventoryItem item);

    InventoryContents add(InventoryItem item);

    InventoryContents fill(InventoryItem item);

    InventoryContents fillRow(int row, InventoryItem item);

    InventoryContents fillColumn(int column, InventoryItem item);

    InventoryContents fillBorders(InventoryItem item);

    InventoryContents fillRect(int fromRow, int fromColumn,
                               int toRow, int toColumn, InventoryItem item);

    InventoryContents fillRect(SlotPos fromPos, SlotPos toPos, InventoryItem item);

    <T> T property(String name);

    <T> T property(String name, T def);

    InventoryContents setProperty(String name, Object value);

    class Impl implements InventoryContents {

        private CustomInventory inv;
        private UUID player;

        private InventoryItem[][] contents;

        private Pagination pagination = new Pagination.Impl();
        private Map<String, SlotIterator> iterators = new HashMap<>();
        private Map<String, Object> properties = new HashMap<>();

        public Impl(CustomInventory inv, UUID player) {
            this.inv = inv;
            this.player = player;
            this.contents = new InventoryItem[inv.getRows()][inv.getColumns()];
        }

        @Override
        public CustomInventory inventory() {
            return inv;
        }

        @Override
        public Pagination pagination() {
            return pagination;
        }

        @Override
        public Optional<SlotIterator> iterator(String id) {
            return Optional.ofNullable(this.iterators.get(id));
        }

        @Override
        public SlotIterator newIterator(String id, SlotIterator.Type type, int startRow, int startColumn) {
            SlotIterator iterator = new SlotIterator.Impl(this, inv,
                    type, startRow, startColumn);

            this.iterators.put(id, iterator);
            return iterator;
        }

        @Override
        public SlotIterator newIterator(String id, SlotIterator.Type type, SlotPos startPos) {
            return newIterator(id, type, startPos.getRow(), startPos.getColumn());
        }

        @Override
        public SlotIterator newIterator(SlotIterator.Type type, int startRow, int startColumn) {
            return new SlotIterator.Impl(this, inv, type, startRow, startColumn);
        }

        @Override
        public SlotIterator newIterator(SlotIterator.Type type, SlotPos startPos) {
            return newIterator(type, startPos.getRow(), startPos.getColumn());
        }

        @Override
        public InventoryItem[][] all() {
            return contents;
        }

        @Override
        public Optional<SlotPos> firstEmpty() {
            for (int row = 0; row < contents.length; row++) {
                for (int column = 0; column < contents[0].length; column++) {
                    if (!this.get(row, column).isPresent())
                        return Optional.of(new SlotPos(row, column));
                }
            }

            return Optional.empty();
        }

        @Override
        public Optional<InventoryItem> get(int row, int column) {
            if (row >= contents.length)
                return Optional.empty();
            if (column >= contents[row].length)
                return Optional.empty();

            return Optional.ofNullable(contents[row][column]);
        }

        @Override
        public Optional<InventoryItem> get(SlotPos slotPos) {
            return get(slotPos.getRow(), slotPos.getColumn());
        }

        @Override
        public InventoryContents set(int row, int column, InventoryItem item) {
            if (row >= contents.length)
                return this;
            if (column >= contents[row].length)
                return this;

            contents[row][column] = item;
            update(row, column, item != null ? item.getItem() : null);
            return this;
        }

        @Override
        public InventoryContents set(SlotPos slotPos, InventoryItem item) {
            return set(slotPos.getRow(), slotPos.getColumn(), item);
        }

        @Override
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

        @Override
        public InventoryContents fill(InventoryItem item) {
            for (int row = 0; row < contents.length; row++)
                for (int column = 0; column < contents[row].length; column++)
                    set(row, column, item);

            return this;
        }

        @Override
        public InventoryContents fillRow(int row, InventoryItem item) {
            if (row >= contents.length)
                return this;

            for (int column = 0; column < contents[row].length; column++)
                set(row, column, item);

            return this;
        }

        @Override
        public InventoryContents fillColumn(int column, InventoryItem item) {
            for (int row = 0; row < contents.length; row++)
                set(row, column, item);

            return this;
        }

        @Override
        public InventoryContents fillBorders(InventoryItem item) {
            fillRect(0, 0, inv.getRows() - 1, inv.getColumns() - 1, item);
            return this;
        }

        @Override
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

        @Override
        public InventoryContents fillRect(SlotPos fromPos, SlotPos toPos, InventoryItem item) {
            return fillRect(fromPos.getRow(), fromPos.getColumn(), toPos.getRow(), toPos.getColumn(), item);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T property(String name) {
            return (T) properties.get(name);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T property(String name, T def) {
            return properties.containsKey(name) ? (T) properties.get(name) : def;
        }

        @Override
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

}