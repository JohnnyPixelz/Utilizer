package io.github.johnnypixelz.utilizer.inventory.items;

import io.github.johnnypixelz.utilizer.inventory.InventoryItem;
import org.bukkit.inventory.ItemStack;

public class SimpleItem extends InventoryItem {
    private final ItemStack item;

    public SimpleItem(ItemStack item) {
        this.item = item;
    }

    @Override
    protected void onMount() {
        set(item);
    }

}