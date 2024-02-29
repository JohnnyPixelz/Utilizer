package io.github.johnnypixelz.utilizer.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class InventoryItem {

    private final ItemStack item;
    private final Consumer<InventoryClickEvent> consumer;

    private InventoryItem(ItemStack item, Consumer<InventoryClickEvent> consumer) {
        this.item = item;
        this.consumer = consumer;
    }

    public static InventoryItem dummy(ItemStack item) {
        return clickable(item, e -> {
        });
    }

    public static InventoryItem clickable(ItemStack item, Consumer<InventoryClickEvent> consumer) {
        return new InventoryItem(item, consumer);
    }

    public void run(InventoryClickEvent event) {
        consumer.accept(event);
    }

    public ItemStack getItem() {
        return item;
    }

}
