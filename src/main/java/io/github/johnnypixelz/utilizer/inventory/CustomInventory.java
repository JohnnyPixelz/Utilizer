package io.github.johnnypixelz.utilizer.inventory;

import io.github.johnnypixelz.utilizer.config.Message;
import io.github.johnnypixelz.utilizer.inventory.items.ClickableItem;
import io.github.johnnypixelz.utilizer.inventory.items.CloseItem;
import io.github.johnnypixelz.utilizer.inventory.items.SimpleItem;
import io.github.johnnypixelz.utilizer.inventory.items.SwitchItem;
import io.github.johnnypixelz.utilizer.inventory.panes.PaginatedPane;
import io.github.johnnypixelz.utilizer.inventory.parser.InventoryConfig;
import io.github.johnnypixelz.utilizer.inventory.parser.InventoryConfigItem;
import io.github.johnnypixelz.utilizer.inventory.slot.Slot;
import io.github.johnnypixelz.utilizer.smartinvs.PaneType;
import io.github.johnnypixelz.utilizer.smartinvs.PremadeItems;
import io.github.johnnypixelz.utilizer.text.Colors;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CustomInventory {

    private Inventory bukkitInventory;
    private Pane rootPane;

    // Inventory Options
    private String title;
    private CustomInventoryType inventoryType;
    private InventoryConfig inventoryConfig;

    private boolean loaded = false;

    protected void onLoad() {

    }

    protected void onDraw() {

    }

    protected void onOpen(Player player) {

    }

    protected void onClose(Player player) {

    }

    protected void onQuit(Player player) {

    }

    void handleClick(InventoryClickEvent event) {
        final Optional<InventoryItem> topInventoryItem = rootPane.getTopInventoryItem(event.getRawSlot());
        topInventoryItem.ifPresent(item -> item.handleClick(event));
    }

    public Inventory getBukkitInventory() {
        return bukkitInventory;
    }

    public Pane getRootPane() {
        return rootPane;
    }

    private void init() {
        if (inventoryType == null) {
            throw new IllegalStateException("Inventory type not configured.");
        }

        if (title == null) {
            this.title = inventoryType.getInventoryType().getDefaultTitle();
        }

        if (inventoryType.getInventoryType() == InventoryType.CHEST) {
            int size = switch (inventoryType) {
                case CHEST_1 -> 9;
                case CHEST_2 -> 18;
                case CHEST_3 -> 27;
                case CHEST_4 -> 36;
                case CHEST_5 -> 45;
                case CHEST_6 -> 54;
                default -> throw new IllegalStateException("Unreachable code");
            };
            bukkitInventory = Bukkit.createInventory(null, size, Colors.color(title));
        } else {
            bukkitInventory = Bukkit.createInventory(null, inventoryType.getInventoryType());
        }

        this.rootPane = new Pane(inventoryType.getInventoryShape());
        rootPane.getRenderSignaller().listen(integer -> {
            final ItemStack itemStack = rootPane.getTopRenderedItem(integer).orElse(null);
            bukkitInventory.setItem(integer, itemStack);
        });
    }

    public void redraw() {
        rootPane.clear();
        inventoryConfig.draw(this);
        onDraw();
    }

    public void open(Player player) {
        if (!loaded) {
            onLoad();
            if (inventoryConfig != null) {
                inventoryConfig.load(this);
            }

            init();

            this.loaded = true;

            if (inventoryConfig != null) {
                inventoryConfig.draw(this);
            }

            onDraw();
        }

        InventoryManager.getInventory(player).ifPresent(customInventory -> {
            customInventory.close(player);
        });

        try {
            InventoryManager.setInventory(player, this);
            player.openInventory(bukkitInventory);
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
        this.inventoryType = type;
        return this;
    }

    protected CustomInventory config(ConfigurationSection section) {
        this.inventoryConfig = InventoryConfig.parse(section);
        return this;
    }

    protected CustomInventory config(String configFile, String configPath) {
        this.inventoryConfig = InventoryConfig.parse(configFile, configPath);
        return this;
    }

    public CustomInventoryType getCustomInventoryType() {
        return inventoryType;
    }

    // Protected methods

    protected Pane pane(int rawSlot) {
        final Pane pane = new Pane(inventoryType.getInventoryShape());
        rootPane.addPane(rawSlot, pane);
        pane.mount(rootPane);

        return pane;
    }

    protected Pane pane(Pane pane) {
        rootPane.addPane(0, pane);
        pane.mount(rootPane);

        return pane;
    }

    protected PaginatedPane paginatedPane() {
        return paginatedPane(0, inventoryType.getInventoryShape().getSize() - 1);
    }

    protected PaginatedPane paginatedPane(int fromRow, int fromColumn, int toRow, int toColumn) {
        return paginatedPane(Slot.of(fromRow, fromColumn), Slot.of(toRow, toColumn));
    }

    protected PaginatedPane paginatedPane(Slot fromSlot, Slot toSlot) {
        final int fromRawSlot = fromSlot.getRawSlot(inventoryType.getInventoryShape());
        final int toRawSlot = toSlot.getRawSlot(inventoryType.getInventoryShape());

        return paginatedPane(fromRawSlot, toRawSlot);
    }

    protected PaginatedPane paginatedPane(int fromRawSlot, int toRawSlot) {
        final PaginatedPane paginatedPane = new PaginatedPane(inventoryType.getInventoryShape().getSubShape(fromRawSlot, toRawSlot));

        rootPane.addPane(fromRawSlot, paginatedPane);
        paginatedPane.mount(rootPane);

        return paginatedPane;
    }

    protected ItemStack paneStack(PaneType paneType) {
        return PremadeItems.getCustomPane(paneType);
    }

    protected Optional<InventoryConfigItem> configItem(String configItemId) {
        if (inventoryConfig == null) return Optional.empty();
        return inventoryConfig.getConfigItem(configItemId);
    }

    protected Optional<Message> configMessage(String messageId) {
        if (inventoryConfig == null) return Optional.empty();
        return inventoryConfig.getMessage(messageId);
    }

    protected SimpleItem displayItem(ItemStack itemStack) {
        return new SimpleItem(itemStack);
    }

    protected SimpleItem paneItem(PaneType paneType) {
        return displayItem(PremadeItems.getCustomPane(paneType));
    }

    protected ClickableItem clickableItem(ItemStack itemStack) {
        return new ClickableItem(itemStack);
    }

    protected ClickableItem clickableItem(ItemStack itemStack, Consumer<InventoryClickEvent> leftClick) {
        return new ClickableItem(itemStack).leftClick(leftClick);
    }

    protected ClickableItem clickableItem(ItemStack itemStack, Consumer<InventoryClickEvent> leftClick, Consumer<InventoryClickEvent> rightClick) {
        return new ClickableItem(itemStack).leftClick(leftClick).rightClick(rightClick);
    }

    protected CloseItem closeItem(ItemStack itemStack) {
        return new CloseItem(itemStack);
    }

    protected SwitchItem switchItem(ItemStack offStack, ItemStack onStack) {
        return new SwitchItem(offStack, onStack);
    }

    protected SwitchItem switchItem(ItemStack offStack, ItemStack onStack, boolean state) {
        return new SwitchItem(offStack, onStack, state);
    }

    protected void add(InventoryItem item) {
        rootPane.addInventoryItem(item);
    }

    protected void set(int row, int column, InventoryItem item) {
        rootPane.setInventoryItem(row, column, item);
    }

    protected void set(Slot slot, InventoryItem item) {
        rootPane.setInventoryItem(slot, item);
    }

    protected void set(int rawSlot, InventoryItem item) {
        rootPane.setInventoryItem(rawSlot, item);
    }

    protected Slot slot(int row, int column) {
        return Slot.of(row, column);
    }

    protected void fill(Supplier<InventoryItem> item) {
        rootPane.fill(item);
    }

    protected void fillRow(int row, Supplier<InventoryItem>  item) {
        rootPane.fillRow(row, item);
    }

    protected void fillColumn(int column, Supplier<InventoryItem>  item) {
        rootPane.fillColumn(column, item);
    }

    protected void fillBorders(Supplier<InventoryItem>  item) {
        rootPane.fillBorders(item);
    }

    protected void fillRect(int fromRow, int fromColumn, int toRow, int toColumn, Supplier<InventoryItem>  item) {
        rootPane.fillRect(fromRow, fromColumn, toRow, toColumn, item);
    }

    protected void fillRect(Slot fromPos, Slot toPos, Supplier<InventoryItem>  item) {
        rootPane.fillRect(fromPos, toPos, item);
    }

    protected void clear() {
        rootPane.clear();
    }

}
