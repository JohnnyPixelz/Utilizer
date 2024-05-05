package io.github.johnnypixelz.utilizer.inventory;

import io.github.johnnypixelz.utilizer.config.Message;
import io.github.johnnypixelz.utilizer.inventory.items.ClickableItem;
import io.github.johnnypixelz.utilizer.inventory.items.CloseItem;
import io.github.johnnypixelz.utilizer.inventory.items.SimpleItem;
import io.github.johnnypixelz.utilizer.inventory.items.SwitchItem;
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

public class CustomInventory {

    private Inventory inventory;
    private InventoryContents contents;

    // Inventory Options
    private String title;
    private CustomInventoryType type;
    private InventoryConfig config;

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
            inventory = Bukkit.createInventory(null, size, Colors.color(title));
        } else {
            inventory = Bukkit.createInventory(null, type.getInventoryType());
        }

        this.contents = new InventoryContents(this);
    }

    public void redraw() {
        contents.clear();
        config.draw(this);
        onDraw();
    }

    public void open(Player player) {
        open(player, 0);
    }

    public void open(Player player, int page) {
        if (!loaded) {
            onLoad();
            if (config != null) {
                config.load(this);
            }

            init();

            this.loaded = true;

            if (config != null) {
                config.draw(this);
            }

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

    protected CustomInventory config(ConfigurationSection section) {
        this.config = InventoryConfig.parse(section);
        return this;
    }

    protected CustomInventory config(String configFile, String configPath) {
        this.config = InventoryConfig.parse(configFile, configPath);
        return this;
    }

    public CustomInventoryType getType() {
        return type;
    }

    // Protected methods

    protected ItemStack paneStack(PaneType paneType) {
        return PremadeItems.getCustomPane(paneType);
    }

    protected Optional<InventoryConfigItem> configItem(String configItemId) {
        if (config == null) return Optional.empty();
        return config.getConfigItem(configItemId);
    }

    protected Optional<Message> configMessage(String messageId) {
        if (config == null) return Optional.empty();
        return config.getMessage(messageId);
    }

    protected SimpleItem display(ItemStack itemStack) {
        return new SimpleItem(itemStack);
    }

    protected SimpleItem pane(PaneType paneType) {
        return display(PremadeItems.getCustomPane(paneType));
    }

    protected ClickableItem clickable(ItemStack itemStack) {
        return new ClickableItem(itemStack);
    }

    protected ClickableItem clickable(ItemStack itemStack, Consumer<InventoryClickEvent> leftClick) {
        return new ClickableItem(itemStack).leftClick(leftClick);
    }

    protected ClickableItem clickable(ItemStack itemStack, Consumer<InventoryClickEvent> leftClick, Consumer<InventoryClickEvent> rightClick) {
        return new ClickableItem(itemStack).leftClick(leftClick).rightClick(rightClick);
    }

    protected CloseItem closeButton(ItemStack itemStack) {
        return new CloseItem(itemStack);
    }

    protected SwitchItem switchButton(ItemStack offStack, ItemStack onStack) {
        return new SwitchItem(offStack, onStack);
    }

    protected SwitchItem switchButton(ItemStack offStack, ItemStack onStack, boolean state) {
        return new SwitchItem(offStack, onStack, state);
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
