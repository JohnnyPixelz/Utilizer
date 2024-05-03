package io.github.johnnypixelz.utilizer.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class InventoryItem {
    private InventoryContents inventoryContents;
    private Integer rawSlot;

    void mount(InventoryContents inventoryContents, int rawSlot) {
        this.inventoryContents = inventoryContents;
        this.rawSlot = rawSlot;

        onMount();
    }

    void unmount() {
        onUnmount();

        this.inventoryContents = null;
        this.rawSlot = null;
    }

    protected InventoryContents contents() {
        return inventoryContents;
    }

    protected Inventory inventory() {
        return inventoryContents.inventory().getInventory();
    }

    protected void set(ItemStack itemStack) {
        inventory().setItem(rawSlot, itemStack);
    }

    protected void remove() {
        inventory().setItem(rawSlot, null);
    }

    void handleClick(InventoryClickEvent event) {
        onClick(event);
    }

    protected void onMount() {

    }

    protected void onUnmount() {

    }

    protected void onClick(InventoryClickEvent event) {

    }

}
