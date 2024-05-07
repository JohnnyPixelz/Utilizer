package io.github.johnnypixelz.utilizer.inventory;

import com.google.common.collect.ImmutableMap;
import io.github.johnnypixelz.utilizer.event.StatefulEventEmitter;
import io.github.johnnypixelz.utilizer.inventory.shape.InventoryShape;
import io.github.johnnypixelz.utilizer.inventory.slot.PositionedSlot;
import io.github.johnnypixelz.utilizer.inventory.slot.Slot;
import io.github.johnnypixelz.utilizer.plugin.Logs;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Supplier;

public class Pane implements ContentHolder {

    private final InventoryShape inventoryShape;

    private final Map<Integer, InventoryItem> inventoryItems; // InventoryItem objects set to a specific position.
    private final Map<Integer, ItemStack> renderedItems;

    private final Map<Integer, Pane> panes; // Panes and their rawSlot

    private Pane parentPane;
    private boolean mounted;
    private int priority;

    private final StatefulEventEmitter<Integer> renderSignaller;

    public Pane(InventoryShape inventoryShape) {
        this.inventoryItems = new HashMap<>();
        this.renderedItems = new HashMap<>();
        this.inventoryShape = inventoryShape;
        this.panes = new HashMap<>();
        this.priority = 0;

        this.parentPane = null;
        this.mounted = false;

        this.renderSignaller = new StatefulEventEmitter<>();
    }

    StatefulEventEmitter<Integer> getRenderSignaller() {
        return renderSignaller;
    }

    protected void onMount() {
        // here is where rendering happens
    }

    protected void onUnmount() {
        // here is where unrendering happens (items are automatically cleared, you just gotta remove your running tasks)
    }

    protected void onDraw() {

    }

    public boolean isMounted() {
        return this.mounted;
    }

    public void draw() {

    }

    public void mount(Pane parentPane) {
        if (this.mounted) {
            throw new IllegalStateException("cannot mount an already mounted pane");
        }

        this.parentPane = parentPane;
        this.mounted = true;

        this.panes.forEach((rawSlot, pane) -> pane.mount(this));

        onMount();

        this.renderedItems.forEach((integer, itemStack) -> renderSignaller.emit(integer)); // Rendering old items first, if any
        onDraw();
    }

    public void unmount() {
        if (!this.mounted) {
            throw new IllegalStateException("cannot unmount a non-mounted pane");
        }

        this.panes.forEach((rawSlot, pane) -> pane.unmount());

        onUnmount();

        this.mounted = false;
        this.renderedItems.forEach((integer, itemStack) -> renderSignaller.emit(integer)); // Triggering the re-rendering of those item positions to exclude rendered items of this unmounted pane
    }

    @Override
    public InventoryShape getInventoryShape() {
        return inventoryShape;
    }

    @Override
    public Map<Integer, ItemStack> getRenderedItems() {
        return ImmutableMap.copyOf(renderedItems);
    }

    @Override
    public Map<Integer, Pane> getPanes() {
        return panes;
    }

    @Override
    public void addPane(int rawSlot, Pane pane) {
        pane.setPriority(priority + 1);
        this.panes.put(rawSlot, pane);

        pane.getRenderSignaller().listen(integer -> {
            this.getRenderSignaller().emit(integer - rawSlot);
        });
    }

    @Override
    public void removePane(Pane pane) {
        pane.getRenderSignaller().getListeners().clear();
        this.panes.values().remove(pane);
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
        this.panes.forEach((rawSlot, pane) -> pane.setPriority(priority + 1));
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public Optional<Integer> firstEmptyRawSlot() {
        for (int i = 0; i < inventoryItems.size(); i++) {
            final Optional<InventoryItem> inventoryItem = getInventoryItem(i);
            if (inventoryItem.isEmpty()) return Optional.of(i);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Pane> getTopPane(int rawSlot) {
        return this.panes.entrySet()
                .stream()
                .filter(paneEntry -> paneEntry.getKey() <= rawSlot && paneEntry.getValue().inventoryShape.getSize() + paneEntry.getKey() > rawSlot)
                .max(Comparator.comparingInt(value -> value.getValue().getPriority()))
                .map(Map.Entry::getValue);
    }

    @Override
    public Optional<InventoryItem> getTopInventoryItem(int rawSlot) {
        return this.panes.entrySet()
                .stream()
                .filter(paneEntry -> paneEntry.getValue().isMounted())
                .filter(paneEntry -> paneEntry.getKey() <= rawSlot && paneEntry.getValue().inventoryShape.getSize() + paneEntry.getKey() > rawSlot)
                .map(paneEntry -> Map.entry(paneEntry, paneEntry.getValue().getTopInventoryItem(rawSlot - paneEntry.getKey())))
                .filter(paneEntry -> paneEntry.getValue().isPresent())
                .map(paneEntry -> Map.entry(paneEntry.getKey(), paneEntry.getValue().get()))
                .max(Comparator.comparingInt(paneEntry -> paneEntry.getKey().getValue().getPriority()))
                .map(Map.Entry::getValue)
                .or(() -> Optional.ofNullable(inventoryItems.get(rawSlot)));
    }

    @Override
    public Optional<ItemStack> getTopRenderedItem(int rawSlot) {
        return this.panes.entrySet()
                .stream()
                .filter(paneEntry -> paneEntry.getValue().isMounted())
                .filter(paneEntry -> paneEntry.getKey() <= rawSlot && paneEntry.getValue().inventoryShape.getSize() + paneEntry.getKey() > rawSlot)
                .map(paneEntry -> Map.entry(paneEntry, paneEntry.getValue().getTopRenderedItem(rawSlot - paneEntry.getKey())))
                .filter(paneEntry -> paneEntry.getValue().isPresent())
                .map(paneEntry -> Map.entry(paneEntry.getKey(), paneEntry.getValue().get()))
                .max(Comparator.comparingInt(value -> value.getKey().getValue().getPriority()))
                .map(Map.Entry::getValue)
                .or(() -> getRenderedItem(rawSlot));
    }

    @Override
    public Optional<ItemStack> getRenderedItem(int rawSlot) {
        return Optional.ofNullable(this.renderedItems.get(rawSlot));
    }

    @Override
    public Optional<InventoryItem> getInventoryItem(int row, int column) {
        return getInventoryItem(Slot.of(row, column));
    }

    @Override
    public Optional<InventoryItem> getInventoryItem(Slot slot) {
        final int rawSlot = slot.getRawSlot(inventoryShape);

        return getInventoryItem(rawSlot);
    }

    @Override
    public Optional<InventoryItem> getInventoryItem(int rawSlot) {
        if (inventoryShape.getSize() <= rawSlot) {
            Logs.warn("Attempted to get item from slot %d while the maximum slots are %d.".formatted(rawSlot, inventoryShape.getSize()));
            return Optional.empty();
        }

        return Optional.ofNullable(inventoryItems.get(rawSlot));
    }

    @Override
    public Map<Integer, InventoryItem> getInventoryItems() {
        return ImmutableMap.copyOf(inventoryItems);
    }

    @Override
    public void setInventoryItem(int row, int column, InventoryItem item) {
        setInventoryItem(Slot.of(row, column), item);
    }

    @Override
    public void setInventoryItem(Slot slot, InventoryItem item) {
        final int rawSlot = slot.getRawSlot(inventoryShape);

        setInventoryItem(rawSlot, item);
    }

    @Override
    public void setInventoryItem(int rawSlot, InventoryItem item) {
        if (inventoryShape.getSize() <= rawSlot) {
            throw new IllegalArgumentException("rawSlot %d out of bounds".formatted(rawSlot));
        }

        inventoryItems.put(rawSlot, item);

        item.mount(this, rawSlot);
    }

    @Override
    public void removeInventoryItem(int rawSlot) {
        if (inventoryShape.getSize() <= rawSlot) {
            throw new IllegalArgumentException("rawSlot %d out of bounds".formatted(rawSlot));
        }

        final InventoryItem removedItem = inventoryItems.remove(rawSlot);

        if (removedItem != null) {
            removedItem.unmount();
            removeRenderedItem(rawSlot);
        }
    }

    @Override
    public void setRenderedItem(int rawSlot, ItemStack itemStack) {
        this.renderedItems.put(rawSlot, itemStack);
        renderSignaller.emit(rawSlot);
    }

    @Override
    public void removeRenderedItem(int rawSlot) {
        this.renderedItems.remove(rawSlot);
        renderSignaller.emit(rawSlot);
    }

    @Override
    public void addInventoryItem(InventoryItem item) {
        final Optional<Integer> optionalEmptySlot = firstEmptyRawSlot();
        if (optionalEmptySlot.isEmpty()) return;

        setInventoryItem(optionalEmptySlot.get(), item);
    }

    @Override
    public void fill(Supplier<InventoryItem> item) {
        for (int i = 0; i < inventoryShape.getSize(); i++) {
            setInventoryItem(i, item.get());
        }
    }

    @Override
    public void fillRow(int row, Supplier<InventoryItem>  item) {
        for (PositionedSlot slot : inventoryShape.getSlots()) {
            if (slot.getRow() != row) continue;

            setInventoryItem(slot, item.get());
        }
    }

    @Override
    public void fillColumn(int column, Supplier<InventoryItem>  item) {
        for (PositionedSlot slot : inventoryShape.getSlots()) {
            if (slot.getColumn() != column) continue;

            setInventoryItem(slot, item.get());
        }
    }

    @Override
    public void fillBorders(Supplier<InventoryItem>  item) {
        final List<PositionedSlot> slots = inventoryShape.getBorderSlots();
        for (Slot slot : slots) {
            setInventoryItem(slot, item.get());
        }
    }

    @Override
    public void fillRect(int fromRow, int fromColumn, int toRow, int toColumn, Supplier<InventoryItem>  item) {
        fillRect(Slot.of(fromRow, fromColumn), Slot.of(toRow, toColumn), item);
    }

    @Override
    public void fillRect(Slot fromSlot, Slot toSlot, Supplier<InventoryItem>  item) {
        final int fromRawSlot = fromSlot.getRawSlot(inventoryShape);
        final int toRawSlot = toSlot.getRawSlot(inventoryShape);

        fillRect(fromRawSlot, toRawSlot, item);
    }

    @Override
    public void fillRect(int fromRawSlot, int toRawSlot, Supplier<InventoryItem>  item) {
        if (fromRawSlot > toRawSlot) {
            throw new IllegalArgumentException("fromRawSlot %d cannot be bigger than toRawSlot %d".formatted(fromRawSlot, toRawSlot));
        }

        List<Integer> slotsToFill = new ArrayList<>();

        final int inventorySize = inventoryShape.getSize();
        if (fromRawSlot < 0 || fromRawSlot >= inventorySize || toRawSlot >= inventorySize) {
            throw new IllegalArgumentException("rect out of bounds");
        }

        for (int i = fromRawSlot; i <= toRawSlot; i++) {
            slotsToFill.add(i);
        }

        slotsToFill.forEach(integer -> setInventoryItem(integer, item.get()));
    }

    @Override
    public void clear() {
        getInventoryItems().forEach((integer, item) -> {
            removeInventoryItem(integer);
        });
    }

}
