package io.github.johnnypixelz.utilizer.inventory.items;

import io.github.johnnypixelz.utilizer.inventory.InventoryItem;
import org.bukkit.inventory.ItemStack;

public class DisplayItem extends InventoryItem {
    private final ItemStack itemStack;

    public DisplayItem(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    protected void onMount() {
        set(itemStack);
    }

    @Override
    protected void onUnmount() {
        remove();
    }

}
