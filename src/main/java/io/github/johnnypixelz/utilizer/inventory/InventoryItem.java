package io.github.johnnypixelz.utilizer.inventory;

import io.github.johnnypixelz.utilizer.event.StatefulEventEmitter;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class InventoryItem {
    private InventoryContents inventoryContents;
    private final List<Integer> mountedSlots = new ArrayList<>();

    private final StatefulEventEmitter<InventoryClickEvent> onClick = new StatefulEventEmitter<>();
    private final StatefulEventEmitter<InventoryClickEvent> onLeftClick = new StatefulEventEmitter<>();
    private final StatefulEventEmitter<InventoryClickEvent> onRightClick = new StatefulEventEmitter<>();
    private final StatefulEventEmitter<InventoryClickEvent> onShiftLeftClick = new StatefulEventEmitter<>();
    private final StatefulEventEmitter<InventoryClickEvent> onShiftRightClick = new StatefulEventEmitter<>();

    void mount(InventoryContents inventoryContents, int rawSlot) {
        this.inventoryContents = inventoryContents;
        this.mountedSlots.add(rawSlot);

        onMount(rawSlot);
    }

    void unmount(int rawSlot) {
        onUnmount(rawSlot);

        this.mountedSlots.remove((Integer) rawSlot);
    }

    protected InventoryContents contents() {
        return inventoryContents;
    }

    protected Inventory inventory() {
        return inventoryContents.inventory().getInventory();
    }

    protected void set(int rawSlot, ItemStack itemStack) {
        inventory().setItem(rawSlot, itemStack);
    }

    protected void setAll(ItemStack itemStack) {
        this.mountedSlots.forEach(integer -> inventory().setItem(integer, itemStack));
    }

    protected void remove(int rawSlot) {
        inventory().setItem(rawSlot, null);
    }

    protected void removeAll() {
        this.mountedSlots.forEach(integer -> inventory().setItem(integer, null));
    }

    void handleClick(InventoryClickEvent event) {
        switch (event.getClick()) {
            case LEFT -> {
                onClick.emit(event);
                onLeftClick.emit(event);
            }
            case RIGHT -> {
                onClick.emit(event);
                onRightClick.emit(event);
            }
            case SHIFT_LEFT -> {
                onClick.emit(event);
                onShiftLeftClick.emit(event);
            }
            case SHIFT_RIGHT -> {
                onClick.emit(event);
                onShiftRightClick.emit(event);
            }
        }
    }

    protected void onMount(int slot) {

    }

    protected void onUnmount(int slot) {

    }

    protected StatefulEventEmitter<InventoryClickEvent> getOnClick() {
        return onClick;
    }

    protected StatefulEventEmitter<InventoryClickEvent> getOnLeftClick() {
        return onLeftClick;
    }

    protected StatefulEventEmitter<InventoryClickEvent> getOnRightClick() {
        return onRightClick;
    }

    protected StatefulEventEmitter<InventoryClickEvent> getOnShiftLeftClick() {
        return onShiftLeftClick;
    }

    protected StatefulEventEmitter<InventoryClickEvent> getOnShiftRightClick() {
        return onShiftRightClick;
    }

}
