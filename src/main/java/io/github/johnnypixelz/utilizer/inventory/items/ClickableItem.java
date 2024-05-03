package io.github.johnnypixelz.utilizer.inventory.items;

import io.github.johnnypixelz.utilizer.inventory.InventoryItem;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class ClickableItem extends InventoryItem {
    private final ItemStack item;
    private Consumer<InventoryClickEvent> mainClick;
    private Consumer<InventoryClickEvent> leftClick;
    private Consumer<InventoryClickEvent> rightClick;
    private Consumer<InventoryClickEvent> shiftLeftClick;
    private Consumer<InventoryClickEvent> shiftRightClick;

    public ClickableItem(ItemStack item) {
        this.item = item;
    }

    public ClickableItem(ItemStack item, Consumer<InventoryClickEvent> click) {
        this.item = item;
        this.mainClick = click;
    }

    @Override
    protected void onMount() {
        set(item);
    }

    @Override
    protected void onUnmount() {
        remove();
    }

    public ClickableItem click(Consumer<InventoryClickEvent> click) {
        this.mainClick = click;
        return this;
    }

    public ClickableItem leftClick(Consumer<InventoryClickEvent> click) {
        this.leftClick = click;
        return this;
    }

    public ClickableItem rightClick(Consumer<InventoryClickEvent> click) {
        this.rightClick = click;
        return this;
    }

    public ClickableItem shiftLeftClick(Consumer<InventoryClickEvent> click) {
        this.shiftLeftClick = click;
        return this;
    }

    public ClickableItem shiftRightClick(Consumer<InventoryClickEvent> click) {
        this.shiftRightClick = click;
        return this;
    }

    @Override
    protected void onClick(InventoryClickEvent event) {
        switch (event.getClick()) {
            case LEFT -> {
                if (mainClick != null) mainClick.accept(event);
                if (leftClick != null) leftClick.accept(event);
            }
            case RIGHT -> {
                if (mainClick != null) mainClick.accept(event);
                if (rightClick != null) rightClick.accept(event);
            }
            case SHIFT_LEFT -> {
                if (mainClick != null) mainClick.accept(event);
                if (shiftLeftClick != null) shiftLeftClick.accept(event);
            }
            case SHIFT_RIGHT -> {
                if (mainClick != null) mainClick.accept(event);
                if (shiftRightClick != null) shiftRightClick.accept(event);
            }
        }
    }

}
