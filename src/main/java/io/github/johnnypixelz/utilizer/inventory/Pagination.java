package io.github.johnnypixelz.utilizer.inventory;

import java.util.ArrayList;
import java.util.List;

public class Pagination {
    private int currentPage;

    private List<InventoryItem> items = new ArrayList<>();
    private int itemsPerPage = 5;

    public List<InventoryItem> getPageItems() {
        return items.subList(currentPage * itemsPerPage, (currentPage + 1) * itemsPerPage);
    }

    public int getPage() {
        return this.currentPage;
    }

    public Pagination page(int page) {
        this.currentPage = page;
        return this;
    }

    public boolean isFirst() {
        return this.currentPage == 0;
    }

    public boolean isLast() {
        int pageCount = (int) Math.ceil((double) this.items.size() / this.itemsPerPage);
        return this.currentPage >= pageCount - 1;
    }

    public Pagination first() {
        this.currentPage = 0;
        return this;
    }

    public Pagination previous() {
        if (!isFirst())
            this.currentPage--;

        return this;
    }

    public Pagination next() {
        if (!isLast())
            this.currentPage++;

        return this;
    }

    public Pagination last() {
        this.currentPage = this.items.size() / this.itemsPerPage;
        return this;
    }

    public Pagination setItems(List<InventoryItem> items) {
        this.items = items;
        return this;
    }

    public Pagination setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
        return this;
    }

}
