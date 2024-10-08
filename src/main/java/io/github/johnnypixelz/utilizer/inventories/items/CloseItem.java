package io.github.johnnypixelz.utilizer.inventories.items;

import org.bukkit.inventory.ItemStack;

public class CloseItem extends SimpleItem {

    public CloseItem(ItemStack itemStack) {
        super(itemStack);

        getOnLeftClick().listen(event -> {
            event.getWhoClicked().closeInventory();
        });
    }

}
