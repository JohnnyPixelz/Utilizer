package io.github.johnnypixelz.utilizer.inventory.items;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class ClickableItem extends SimpleItem {

    public ClickableItem(ItemStack item) {
        super(item);
    }

    public ClickableItem click(Consumer<InventoryClickEvent> click) {
        getOnClick().listen(click);
        return this;
    }

    public ClickableItem leftClick(Consumer<InventoryClickEvent> click) {
        getOnLeftClick().listen(click);
        return this;
    }

    public ClickableItem rightClick(Consumer<InventoryClickEvent> click) {
        getOnRightClick().listen(click);
        return this;
    }

    public ClickableItem shiftLeftClick(Consumer<InventoryClickEvent> click) {
        getOnShiftLeftClick().listen(click);
        return this;
    }

    public ClickableItem shiftRightClick(Consumer<InventoryClickEvent> click) {
        getOnShiftRightClick().listen(click);
        return this;
    }

}
