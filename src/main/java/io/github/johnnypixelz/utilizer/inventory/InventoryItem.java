package io.github.johnnypixelz.utilizer.inventory;

import io.github.johnnypixelz.utilizer.event.StatefulEventEmitter;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public abstract class InventoryItem {
    private final StatefulEventEmitter<InventoryClickEvent> onClick;
    private final StatefulEventEmitter<InventoryClickEvent> onLeftClick;
    private final StatefulEventEmitter<InventoryClickEvent> onRightClick;
    private final StatefulEventEmitter<InventoryClickEvent> onShiftLeftClick;
    private final StatefulEventEmitter<InventoryClickEvent> onShiftRightClick;

    private MountedSlot mountedSlot;

    public InventoryItem() {
        this.onClick = new StatefulEventEmitter<>();
        this.onLeftClick = new StatefulEventEmitter<>();
        this.onRightClick = new StatefulEventEmitter<>();
        this.onShiftLeftClick = new StatefulEventEmitter<>();
        this.onShiftRightClick = new StatefulEventEmitter<>();

        this.mountedSlot = null;
    }

    void mount(ContentHolder contentHolder, int rawSlot) {
        if (this.mountedSlot != null) {
            throw new IllegalStateException("attempted to mount an already mounted item");
        }

        this.mountedSlot = new MountedSlot(contentHolder, rawSlot);

        onMount();
    }

    void unmount() {
        onUnmount();

        this.mountedSlot = null;
    }

    public MountedSlot getMountedSlot() {
        return mountedSlot;
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

    protected void set(ItemStack itemStack) {
        if (mountedSlot == null) {
            throw new IllegalStateException("cannot use set(ItemStack) when not mounted");
        }

        this.mountedSlot.getContentHolder().setRenderedItem(mountedSlot.getRawSlot(), itemStack);
    }

    protected void onMount() {

    }

    protected void onUnmount() {

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
