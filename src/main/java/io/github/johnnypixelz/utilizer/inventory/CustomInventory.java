package io.github.johnnypixelz.utilizer.inventory;

import io.github.johnnypixelz.utilizer.inventory.content.InventoryContents;
import io.github.johnnypixelz.utilizer.inventory.content.Slot;
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

    public Inventory getInventory() {
        return inventory;
    }

    public InventoryContents getContents() {
        return contents;
    }

    public void open(Player player) {
        open(player, 0);
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

    public void open(Player player, int page) {
        if (!loaded) {
            onLoad();

            init();

            this.loaded = true;

            onDraw();
        }

        contents.pagination().page(page);

        try {
            player.openInventory(inventory);
            InventoryManager.setInventory(player, this);
        } catch (Exception exception) {
            InventoryManager.handleInventoryOpenError(this, player, exception);
        }
    }

    public void close(Player player) {
        InventoryManager.setInventory(player, null);
        player.closeInventory();
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

    protected InventoryItem item(ItemStack itemStack) {
        return InventoryItem.dummy(itemStack);
    }

    protected InventoryItem item(ItemStack itemStack, Consumer<InventoryClickEvent> event) {
        return InventoryItem.clickable(itemStack, event);
    }

    protected void set(InventoryItem item, int row, int column) {

    }

    protected void set(InventoryItem item, Slot slot) {

    }

    protected Slot slot(int row, int column) {
        return Slot.of(row, column);
    }

    protected void fill(InventoryItem item) {

    }

    protected void fillRow(int row, InventoryItem item) {

    }

    protected void fillColumn(int column, InventoryItem item) {

    }

    protected void fillBorders(InventoryItem item) {

    }

    protected void fillRect(int fromRow, int fromColumn, int toRow, int toColumn, InventoryItem item) {

    }

    protected void fillRect(Slot fromPos, Slot toPos, InventoryItem item) {

    }

    protected void clear() {

    }

//    public Optional<CustomInventory> getParent() {
//        return Optional.ofNullable(parent);
//    }

}
