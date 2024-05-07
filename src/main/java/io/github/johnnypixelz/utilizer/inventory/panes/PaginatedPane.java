package io.github.johnnypixelz.utilizer.inventory.panes;

import io.github.johnnypixelz.utilizer.inventory.InventoryItem;
import io.github.johnnypixelz.utilizer.inventory.Pane;
import io.github.johnnypixelz.utilizer.inventory.shape.InventoryShape;
import io.github.johnnypixelz.utilizer.plugin.Logs;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class PaginatedPane extends Pane {

    public static <T> PaginatedPane of(InventoryShape shape, List<T> items, Function<T, InventoryItem> converter) {
        return new PaginatedPane(shape)
                .setItems(items, converter);
    }

    private int currentPage;
    private List<InventoryItem> inventoryItems;

    public PaginatedPane(InventoryShape inventoryShape) {
        super(inventoryShape);
        this.currentPage = 0;
    }

    public <T> PaginatedPane setItems(List<T> items, Function<T, InventoryItem> converter) {
        inventoryItems = items.stream()
                .map(converter)
                .toList();

        Logs.info("Filled paginated pane with " + inventoryItems.size() + " items");

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
        Logs.info("Clicked next, getMaxPage is " + getMaxPage());
        if (this.currentPage >= getMaxPage()) return this.currentPage;

        this.currentPage += 1;

        renderCurrentPage();

        return this.currentPage;
    }

    private int getPageSize() {
        return getInventoryShape().getSize();
    }

    private List<InventoryItem> getInventoryItemsForPage(int page) {
        Logs.info("Getting inventory items for page " + page);

        int fromIncluded = page * getPageSize();
        int toExcluded = (page + 1) * getPageSize();

        Logs.info("Page " + page + ", index from " + fromIncluded + " to " + toExcluded + ", items " + inventoryItems.size());

        if (fromIncluded > inventoryItems.size()) {
            return Collections.emptyList();
        }

        return inventoryItems.subList(fromIncluded, Math.min(inventoryItems.size(), toExcluded));
    }

    private PaginatedPane renderCurrentPage() {
        Logs.info("Rendering current page");
        clear();

        final List<InventoryItem> inventoryItemsForPage = getInventoryItemsForPage(currentPage);
        Logs.info("Inventory Items for current page " + currentPage + " is " + inventoryItemsForPage.size());

//        inventoryItemsForPage.forEach(this::add);
        for (int i = 0; i < inventoryItemsForPage.size(); i++) {
            setInventoryItem(i, inventoryItemsForPage.get(i));
        }
        return this;
    }

    @Override
    protected void onDraw() {
        Logs.info("onDraw");
        renderCurrentPage();
    }

}
