package io.github.johnnypixelz.utilizer.inventory.inventories;

import io.github.johnnypixelz.utilizer.inventory.CustomInventory;
import io.github.johnnypixelz.utilizer.inventory.CustomInventoryType;
import io.github.johnnypixelz.utilizer.inventory.panes.PaginatedPane;
import io.github.johnnypixelz.utilizer.inventory.slot.Slot;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class PickerInventory<T> extends CustomInventory {
    private final String title;
    private final CustomInventoryType type;
    private final Slot fromSlot;
    private final Slot toSlot;

    private final ItemStack previousButtonStack;
    private final ItemStack nextButtonStack;

    private final Slot previousButtonSlot;
    private final Slot nextButtonSlot;

    private final List<T> items;
    private final Function<T, ItemStack> converter;
    private final Consumer<T> onPick;

    private final boolean closeInventoryOnPick;

    private PickerInventory(PickerInventoryBuilder<T> builder) {
        this.title = builder.title;
        this.type = builder.type;
        this.fromSlot = builder.fromSlot;
        this.toSlot = builder.toSlot;

        this.previousButtonSlot = builder.previousButtonSlot;
        this.nextButtonSlot = builder.nextButtonSlot;

        this.previousButtonStack = builder.previousButtonStack;
        this.nextButtonStack = builder.nextButtonStack;

        this.items = builder.items;
        this.converter = builder.converter;
        this.onPick = builder.onPick;

        this.closeInventoryOnPick = builder.closeInventoryOnPick;
    }

    @Override
    protected void onLoad() {
        title(title);
        type(type);
    }

    @Override
    protected void onDraw() {
        paginatedPane(fromSlot, toSlot)
                .setItems(items, t -> {
                    return clickableItem(converter.apply(t)).leftClick(event -> {
                        if (this.closeInventoryOnPick) {
                            event.getWhoClicked().closeInventory();
                        }

                        onPick.accept(t);
                    });
                })
                .pageButtons(previousButtonStack, nextButtonStack, (previousItem, nextItem) -> {
                    set(previousButtonSlot, previousItem);
                    set(nextButtonSlot, nextItem);
                });
    }

    public static class PickerInventoryBuilder<T> {
        private String title = "Picker";
        private CustomInventoryType type = CustomInventoryType.CHEST_6;
        private Slot fromSlot = Slot.of(0, 0);
        private Slot toSlot = Slot.of(4, 8);

        private ItemStack previousButtonStack = PaginatedPane.ButtonDirection.PREVIOUS.getDefaultItemStack();
        private ItemStack nextButtonStack = PaginatedPane.ButtonDirection.NEXT.getDefaultItemStack();

        private Slot previousButtonSlot = Slot.of(5, 0);
        private Slot nextButtonSlot = Slot.of(5, 8);

        private List<T> items = Collections.emptyList();
        private Function<T, ItemStack> converter;
        private Consumer<T> onPick = null;

        private boolean closeInventoryOnPick = true;

        public PickerInventoryBuilder<T> setTitle(String title) {
            this.title = title;
            return this;
        }

        public PickerInventoryBuilder<T> setType(CustomInventoryType type) {
            this.type = type;
            return this;
        }

        public PickerInventoryBuilder<T> setDimensions(Slot fromSlot, Slot toSlot) {
            this.fromSlot = fromSlot;
            this.toSlot = toSlot;
            return this;
        }

        public PickerInventoryBuilder<T> setPreviousButtonSlot(Slot previousButtonSlot) {
            this.previousButtonSlot = previousButtonSlot;
            return this;
        }

        public PickerInventoryBuilder<T> setNextButtonSlot(Slot nextButtonSlot) {
            this.nextButtonSlot = nextButtonSlot;
            return this;
        }

        public PickerInventoryBuilder<T> setPreviousButtonStack(ItemStack previousButtonStack) {
            this.previousButtonStack = previousButtonStack;
            return this;
        }

        public PickerInventoryBuilder<T> setNextButtonStack(ItemStack nextButtonStack) {
            this.nextButtonStack = nextButtonStack;
            return this;
        }

        public PickerInventoryBuilder<T> setItems(List<T> items) {
            this.items = items;
            return this;
        }

        public PickerInventoryBuilder<T> setConverter(Function<T, ItemStack> converter) {
            this.converter = converter;
            return this;
        }

        public PickerInventoryBuilder<T> setOnPick(Consumer<T> onPick) {
            this.onPick = onPick;
            return this;
        }

        public PickerInventoryBuilder<T> setCloseInventoryOnPick(boolean closeInventoryOnPick) {
            this.closeInventoryOnPick = closeInventoryOnPick;
            return this;
        }

        public PickerInventory<T> build() {
            return new PickerInventory<>(this);
        }

    }

}
