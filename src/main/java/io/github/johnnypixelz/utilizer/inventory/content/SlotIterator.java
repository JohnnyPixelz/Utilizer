package io.github.johnnypixelz.utilizer.inventory.content;

import io.github.johnnypixelz.utilizer.inventory.CustomInventory;
import io.github.johnnypixelz.utilizer.inventory.InventoryItem;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class SlotIterator {

    public enum Type {
        HORIZONTAL,
        VERTICAL
    }

    private final InventoryContents contents;
    private final CustomInventory inventory;

    private final Type type;
    private boolean started = false;
    private boolean allowOverride = true;
    private int row, column;

    private final Set<Slot> blacklisted = new HashSet<>();

    public SlotIterator(InventoryContents contents, CustomInventory inventory, Type type, int startRow, int startColumn) {
        this.contents = contents;
        this.inventory = inventory;
        this.type = type;
        this.row = startRow;
        this.column = startColumn;
    }

    public SlotIterator(InventoryContents contents, CustomInventory inventory, Type type) {
        this(contents, inventory, type, 0, 0);
    }

    public Optional<InventoryItem> get() {
        return contents.get(row, column);
    }

    public SlotIterator set(InventoryItem item) {
        if (canPlace())
            contents.set(row, column, item);

        return this;
    }

    public SlotIterator previous() {
        if (row == 0 && column == 0) {
            this.started = true;
            return this;
        }

        do {
            if (!this.started) {
                this.started = true;
            } else {
                switch (type) {
                    case HORIZONTAL:
                        column--;

                        if (column == 0) {
                            column = inventory.getColumns() - 1;
                            row--;
                        }
                        break;
                    case VERTICAL:
                        row--;

                        if (row == 0) {
                            row = inventory.getRows() - 1;
                            column--;
                        }
                        break;
                }
            }
        }
        while (!canPlace() && (row != 0 || column != 0));

        return this;
    }

    public SlotIterator next() {
        if (ended()) {
            this.started = true;
            return this;
        }

        do {
            if (!this.started) {
                this.started = true;
            } else {
                switch (type) {
                    case HORIZONTAL:
                        column = ++column % inventory.getColumns();

                        if (column == 0)
                            row++;
                        break;
                    case VERTICAL:
                        row = ++row % inventory.getRows();

                        if (row == 0)
                            column++;
                        break;
                }
            }
        }
        while (!canPlace() && !ended());

        return this;
    }

    public SlotIterator blacklist(int row, int column) {
        this.blacklisted.add(Slot.of(row, column));
        return this;
    }

    public SlotIterator blacklist(Slot slotPos) {
        return blacklist(slotPos.getRow(), slotPos.getColumn());
    }

    public int row() {
        return row;
    }

    public SlotIterator row(int row) {
        this.row = row;
        return this;
    }

    public int column() {
        return column;
    }

    public SlotIterator column(int column) {
        this.column = column;
        return this;
    }

    public boolean started() {
        return this.started;
    }

    public boolean ended() {
        return row == inventory.getRows() - 1
                && column == inventory.getColumns() - 1;
    }

    public boolean doesAllowOverride() {
        return allowOverride;
    }

    public SlotIterator allowOverride(boolean override) {
        this.allowOverride = override;
        return this;
    }

    private boolean canPlace() {
        return !blacklisted.contains(Slot.of(row, column)) && (allowOverride || this.get().isEmpty());
    }

}
