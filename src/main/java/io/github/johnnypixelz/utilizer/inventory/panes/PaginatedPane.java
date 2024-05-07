package io.github.johnnypixelz.utilizer.inventory.panes;

import io.github.johnnypixelz.utilizer.inventory.InventoryItem;
import io.github.johnnypixelz.utilizer.inventory.Pane;
import io.github.johnnypixelz.utilizer.inventory.items.ClickableItem;
import io.github.johnnypixelz.utilizer.inventory.shape.InventoryShape;
import io.github.johnnypixelz.utilizer.itemstack.Items;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PaginatedPane extends Pane {

    public static <T> PaginatedPane of(InventoryShape shape, List<T> items, Function<T, InventoryItem> converter) {
        return new PaginatedPane(shape)
                .setItems(items, converter);
    }

    public static InventoryItem pageButton(ButtonDirection buttonDirection, PaginatedPane paginatedPane) {
        final ItemStack itemStack = Items.create(buttonDirection.getDefaultIcon(), buttonDirection.getDefaultName());
        return new ClickableItem(itemStack)
                .leftClick(event -> {
                    switch (buttonDirection) {
                        case PREVIOUS -> paginatedPane.previous();
                        case NEXT -> paginatedPane.next();
                    }
                });
    }

    private int currentPage;
    private List<InventoryItem> inventoryItems;

    public PaginatedPane(InventoryShape inventoryShape) {
        super(inventoryShape);
        this.currentPage = 0;
        this.inventoryItems = Collections.emptyList();
    }

    public <T> PaginatedPane setItems(List<T> items, Function<T, InventoryItem> converter) {
        inventoryItems = items.stream()
                .map(converter)
                .toList();

        renderCurrentPage();

        return this;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getMinPage() {
        return 0;
    }

    public int getMaxPage() {
        return Math.max(0, (inventoryItems.size() - 1) / getInventoryShape().getSize());
    }

    public PaginatedPane setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
        return this;
    }

    public int previous() {
        if (this.currentPage == 0) return 0;

        this.currentPage -= 1;

        renderCurrentPage();

        return this.currentPage;
    }

    public int next() {
        if (this.currentPage >= getMaxPage()) return this.currentPage;

        this.currentPage += 1;

        renderCurrentPage();

        return this.currentPage;
    }

    public InventoryItem pageButton(ButtonDirection buttonDirection) {
        return pageButton(buttonDirection, buttonDirection.getDefaultIcon(), buttonDirection.getDefaultName());
    }

    public InventoryItem pageButton(ButtonDirection buttonDirection, Material material, String displayName) {
        return pageButton(buttonDirection, Items.create(material, displayName));
    }

    public InventoryItem pageButton(ButtonDirection buttonDirection, ItemStack itemStack) {
        return new ClickableItem(itemStack)
                .leftClick(event -> {
                    switch (buttonDirection) {
                        case PREVIOUS -> this.previous();
                        case NEXT -> this.next();
                    }
                });
    }

    public PaginatedPane pageButtons(Material material, String previousDisplayName, String nextDisplayName, BiConsumer<InventoryItem, InventoryItem> buttonsConsumer) {
        return pageButtons(material, material, previousDisplayName, nextDisplayName, buttonsConsumer);
    }

    public PaginatedPane pageButtons(Material previousMaterial, Material nextMaterial, String previousDisplayName, String nextDisplayName, BiConsumer<InventoryItem, InventoryItem> buttonsConsumer) {
        return pageButtons(
                Items.create(previousMaterial, previousDisplayName),
                Items.create(nextMaterial, nextDisplayName),
                buttonsConsumer
        );
    }

    public PaginatedPane pageButtons(ItemStack previousStack, ItemStack nextStack, BiConsumer<InventoryItem, InventoryItem> buttonsConsumer) {
        final InventoryItem previousItem = pageButton(ButtonDirection.PREVIOUS, previousStack);
        final InventoryItem nextItem = pageButton(ButtonDirection.NEXT, nextStack);

        buttonsConsumer.accept(previousItem, nextItem);

        return this;
    }

    public PaginatedPane pageButtons(BiConsumer<InventoryItem, InventoryItem> buttonsConsumer) {
        final InventoryItem previousItem = pageButton(ButtonDirection.PREVIOUS);
        final InventoryItem nextItem = pageButton(ButtonDirection.NEXT);

        buttonsConsumer.accept(previousItem, nextItem);

        return this;
    }

    private int getPageSize() {
        return getInventoryShape().getSize();
    }

    private List<InventoryItem> getInventoryItemsForPage(int page) {
        int fromIncluded = page * getPageSize();
        int toExcluded = (page + 1) * getPageSize();

        if (fromIncluded > inventoryItems.size()) {
            return Collections.emptyList();
        }

        return inventoryItems.subList(fromIncluded, Math.min(inventoryItems.size(), toExcluded));
    }

    private PaginatedPane renderCurrentPage() {
        if (inventoryItems == null) {
            throw new IllegalStateException("Attempted to draw page without initializing items. Please do so using PaginatedPane#setItems");
        }

        clear();

        final List<InventoryItem> inventoryItemsForPage = getInventoryItemsForPage(currentPage);

        for (int i = 0; i < inventoryItemsForPage.size(); i++) {
            setInventoryItem(i, inventoryItemsForPage.get(i));
        }

        return this;
    }

    @Override
    protected void onDraw() {
        renderCurrentPage();
    }

    public enum ButtonDirection {
        PREVIOUS(Material.ARROW, "&8Previous Page"),
        NEXT(Material.ARROW, "&8Next Page");

        private final Material defaultIcon;
        private final String defaultName;

        ButtonDirection(Material defaultIcon, String defaultName) {
            this.defaultIcon = defaultIcon;
            this.defaultName = defaultName;
        }

        public Material getDefaultIcon() {
            return defaultIcon;
        }

        public String getDefaultName() {
            return defaultName;
        }

        public ItemStack getDefaultItemStack() {
            return Items.create(getDefaultIcon(), getDefaultName());
        }

    }

}
