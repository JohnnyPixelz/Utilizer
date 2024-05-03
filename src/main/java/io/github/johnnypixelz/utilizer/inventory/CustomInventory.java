package io.github.johnnypixelz.utilizer.inventory;

import io.github.johnnypixelz.utilizer.inventory.items.ClickableItem;
import io.github.johnnypixelz.utilizer.inventory.items.DisplayItem;
import io.github.johnnypixelz.utilizer.smartinvs.PaneType;
import io.github.johnnypixelz.utilizer.smartinvs.PremadeItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class CustomInventory {

    private Inventory inventory;
    private InventoryContents contents;

    // Inventory Options
    private String title;
    private CustomInventoryType type;
//    private boolean closeable;
//    private boolean openParentOnClose;

    private boolean loaded = false;

    protected void onLoad() {

    }

    protected void onDraw() {

    }

    protected void onOpen(Player player) {

    }

    protected void onClose(Player player) {

    }

    void handleClick(InventoryClickEvent event) {
        contents.handleClick(event);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public InventoryContents getContents() {
        return contents;
    }

    private void init() {
        if (type == null) {
            throw new IllegalStateException("Inventory type not configured.");
        }

        if (title == null) {
            this.title = type.getInventoryType().getDefaultTitle();
        }

        if (type.getInventoryType() == InventoryType.CHEST) {
            int size = switch (type) {
                case CHEST_1 -> 9;
                case CHEST_2 -> 18;
                case CHEST_3 -> 27;
                case CHEST_4 -> 36;
                case CHEST_5 -> 45;
                case CHEST_6 -> 54;
                default -> throw new IllegalStateException("Unreachable code");
            };
            inventory = Bukkit.createInventory(null, size, title);
        } else {
            inventory = Bukkit.createInventory(null, type.getInventoryType());
        }

        this.contents = new InventoryContents(this);
    }

    public void redraw() {
        contents.clear();
        onDraw();
    }

    public void open(Player player) {
        open(player, 0);
    }

    public void open(Player player, int page) {
        if (!loaded) {
            onLoad();

            init();

            this.loaded = true;

            onDraw();
        }

        contents.pagination().page(page);

        InventoryManager.getInventory(player).ifPresent(customInventory -> {
            customInventory.close(player);
        });

        try {
            InventoryManager.setInventory(player, this);
            player.openInventory(inventory);
            onOpen(player);
        } catch (Exception exception) {
            InventoryManager.handleInventoryOpenError(this, player, exception);
        }
    }

    public void close(Player player) {
        InventoryManager.setInventory(player, null);
        player.closeInventory();

        onClose(player);
    }

    public CustomInventory title(String title) {
        this.title = title;
        return this;
    }

    public CustomInventory type(CustomInventoryType type) {
        this.type = type;
        return this;
    }

    public CustomInventoryType getType() {
        return type;
    }

    // Protected methods

    protected DisplayItem display(ItemStack itemStack) {
        return new DisplayItem(itemStack);
    }

    protected DisplayItem pane(PaneType paneType) {
        return display(PremadeItems.getCustomPane(paneType));
    }

    protected ClickableItem clickable(ItemStack itemStack) {
        return new ClickableItem(itemStack);
    }

    protected ClickableItem clickable(ItemStack itemStack, Consumer<InventoryClickEvent> click) {
        return new ClickableItem(itemStack, click);
    }

    protected void add(InventoryItem item) {
        contents.add(item);
    }

    protected void set(int row, int column, InventoryItem item) {
        contents.set(row, column, item);
    }

    protected void set(Slot slot, InventoryItem item) {
        contents.set(slot, item);
    }

    protected void set(int rawSlot, InventoryItem item) {
        contents.set(rawSlot, item);
    }

    protected Slot slot(int row, int column) {
        return Slot.of(row, column);
    }

    protected void fill(InventoryItem item) {
        contents.fill(item);
    }

    protected void fillRow(int row, InventoryItem item) {
        contents.fillRow(row, item);
    }

    protected void fillColumn(int column, InventoryItem item) {
        contents.fillColumn(column, item);
    }

    protected void fillBorders(InventoryItem item) {
        contents.fillBorders(item);
    }

    protected void fillRect(int fromRow, int fromColumn, int toRow, int toColumn, InventoryItem item) {
        contents.fillRect(fromRow, fromColumn, toRow, toColumn, item);
    }

    protected void fillRect(Slot fromPos, Slot toPos, InventoryItem item) {
        contents.fillRect(fromPos, toPos, item);
    }

    protected void clear() {
        contents.clear();
    }

}
