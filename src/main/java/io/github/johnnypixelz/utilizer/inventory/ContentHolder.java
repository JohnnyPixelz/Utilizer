package io.github.johnnypixelz.utilizer.inventory;

import io.github.johnnypixelz.utilizer.inventory.shape.InventoryShape;
import io.github.johnnypixelz.utilizer.inventory.slot.Slot;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public interface ContentHolder {

    int getPriority();

    void setPriority(int priority);

    List<ContainedPane> getPanes();

    void addPane(int rawSlot, Pane pane);

    void removePane(Pane pane);

    Optional<Integer> firstEmptyRawSlot();

    Optional<Pane> getTopPane(int rawSlot);

    Optional<InventoryItem> getTopInventoryItem(int rawSlot);

    Optional<ItemStack> getTopRenderedItem(int rawSlot);

    void addInventoryItem(InventoryItem item);

    Optional<InventoryItem> getInventoryItem(int row, int column);

    Optional<InventoryItem> getInventoryItem(Slot slot);

    Optional<InventoryItem> getInventoryItem(int rawSlot);

    Map<Integer, InventoryItem> getInventoryItems();

    void setInventoryItem(int row, int column, InventoryItem item);

    void setInventoryItem(Slot slot, InventoryItem item);

    void setInventoryItem(int rawSlot, InventoryItem item);

    Optional<ItemStack> getRenderedItem(int rawSlot);

    InventoryShape getInventoryShape();

    Map<Integer, ItemStack> getRenderedItems();

    void removeInventoryItem(int rawSlot);

    void setRenderedItem(int rawSlot, ItemStack itemStack);

    void removeRenderedItem(int rawSlot);

    void fill(Supplier<InventoryItem> item);

    void fillRow(int row, Supplier<InventoryItem> item);

    void fillColumn(int column, Supplier<InventoryItem> item);

    void fillBorders(Supplier<InventoryItem> item);

    void fillRect(int fromRow, int fromColumn, int toRow, int toColumn, Supplier<InventoryItem> item);

    void fillRect(Slot fromSlot, Slot toSlot, Supplier<InventoryItem> item);

    void fillRect(int fromRawSlot, int toRawSlot, Supplier<InventoryItem> item);

    void clear();

}
