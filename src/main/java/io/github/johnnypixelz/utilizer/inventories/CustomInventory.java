package io.github.johnnypixelz.utilizer.inventories;

import io.github.johnnypixelz.utilizer.config.Message;
import io.github.johnnypixelz.utilizer.config.Parse;
import io.github.johnnypixelz.utilizer.config.reference.ConfigSectionReference;
import io.github.johnnypixelz.utilizer.depend.Placeholders;
import io.github.johnnypixelz.utilizer.inventories.config.InventoryConfig;
import io.github.johnnypixelz.utilizer.inventories.config.InventoryConfigItem;
import io.github.johnnypixelz.utilizer.inventories.items.ClickableItem;
import io.github.johnnypixelz.utilizer.inventories.items.CloseItem;
import io.github.johnnypixelz.utilizer.inventories.items.SimpleItem;
import io.github.johnnypixelz.utilizer.inventories.items.SwitchItem;
import io.github.johnnypixelz.utilizer.inventories.panes.PaginatedPane;
import io.github.johnnypixelz.utilizer.inventories.slot.Slot;
import io.github.johnnypixelz.utilizer.itemstack.Items;
import io.github.johnnypixelz.utilizer.smartinvs.PaneType;
import io.github.johnnypixelz.utilizer.smartinvs.PremadeItems;
import io.github.johnnypixelz.utilizer.tasks.Tasks;
import io.github.johnnypixelz.utilizer.text.Colors;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CustomInventory {

    private Inventory bukkitInventory;
    private Pane rootPane;

    // Inventory Options
    private String title;
    private CustomInventoryType inventoryType;

    private ConfigSectionReference inventoryConfigSectionReference;
    private InventoryConfig inventoryConfig;

    private boolean hasInventoryLoaded;
    private long refreshInterval;

    private BukkitTask refreshTask;

    private CustomInventory parentInventory;
    private boolean openParentInventoryOnClose;

    private boolean placeholderApiSupport;
    private Player mainViewer;

    public CustomInventory() {
        this.bukkitInventory = null;
        this.rootPane = null;

        this.title = null;
        this.inventoryType = null;

        this.inventoryConfigSectionReference = null;
        this.inventoryConfig = null;

        this.hasInventoryLoaded = false;
        this.refreshInterval = -1;
        this.refreshTask = null;

        this.placeholderApiSupport = true;
        this.mainViewer = null;
    }

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

    public Player getMainViewer() {
        return mainViewer;
    }

    public boolean isOpenParentInventoryOnClose() {
        return openParentInventoryOnClose;
    }

    public void setOpenParentInventoryOnClose(boolean openParentInventoryOnClose) {
        this.openParentInventoryOnClose = openParentInventoryOnClose;
    }

    public CustomInventory setParentInventory(CustomInventory parentInventory) {
        this.parentInventory = parentInventory;
        return this;
    }

    @Deprecated(forRemoval = true)
    public CustomInventory openInventoryOnClose(CustomInventory parentInventory) {
        return openParentInventoryOnClose(parentInventory);
    }

    public CustomInventory openParentInventoryOnClose(CustomInventory parentInventory) {
        if (parentInventory == null) {
            this.parentInventory = null;
            this.openParentInventoryOnClose = false;
            return this;
        }

        this.parentInventory = parentInventory;
        this.openParentInventoryOnClose = true;
        return this;
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
            if (itemStack == null) {
                bukkitInventory.setItem(integer, null);
                return;
            }

            if (placeholderApiSupport && mainViewer != null) {
                Items.map(itemStack, line -> Placeholders.set(mainViewer, line));
            }

            bukkitInventory.setItem(integer, itemStack);
        });
    }

    public void redraw() {
        rootPane.clear();

        if (inventoryConfig != null) {
            InventoryConfig.draw(this, inventoryConfig);
        }

        onDraw();
    }

    public void open(Player player) {
        if (this.mainViewer == null) {
            this.mainViewer = player;
        }

        if (!hasInventoryLoaded) {
            onLoad();
            if (inventoryConfig != null) {
                InventoryConfig.load(this, inventoryConfig);
            }

            init();

            this.hasInventoryLoaded = true;

            if (inventoryConfig != null) {
                InventoryConfig.draw(this, inventoryConfig);
            }

            onDraw();
        }

        // Closing the player's old custom inventory, if exists
        InventoryManager.getInventory(player).ifPresent(customInventory -> {
            customInventory.close(player);
        });

        try {
            InventoryManager.setInventory(player, this);
            final InventoryView inventoryView = player.openInventory(bukkitInventory);

            if (inventoryView != null) {
                if (placeholderApiSupport) {
                    inventoryView.setTitle(Colors.color(Placeholders.set(mainViewer, title)));
                } else {
                    inventoryView.setTitle(Colors.color(title));
                }
            }

            onOpen(player);
            ensureRefreshTask();
        } catch (Exception exception) {
            InventoryManager.handleInventoryOpenError(this, player, exception);
        }
    }

    public void close(Player player) {
        InventoryManager.setInventory(player, null);

        if (player.getOpenInventory().getTopInventory() == this.bukkitInventory) {
            player.closeInventory();
        }

        onClose(player);

        this.mainViewer = getViewers().stream()
                .findFirst()
                .orElse(null);

        if (this.openParentInventoryOnClose && this.parentInventory != null) {
            Tasks.sync().run(() -> {
                if (!player.isOnline()) return;
                this.parentInventory.open(player);
            });
        }
    }

    public CustomInventory title(String title) {
        if (Objects.equals(this.title, title)) return this;

        this.title = title;

        // Update title to all viewers
        if (bukkitInventory != null) {
            bukkitInventory.getViewers().forEach(humanEntity -> {
                try {
                    if (placeholderApiSupport) {
                        humanEntity.getOpenInventory().setTitle(Colors.color(Placeholders.set(mainViewer, title)));
                    } else {
                        humanEntity.getOpenInventory().setTitle(Colors.color(title));
                    }
                } catch (Exception ignored) {
                }
            });
        }

        return this;
    }

    public CustomInventory type(CustomInventoryType type) {
        this.inventoryType = type;
        return this;
    }

    public CustomInventory refresh(@Nullable Long refreshInterval) {
        this.refreshInterval = refreshInterval == null ? -1 : Parse.constrain(1, Long.MAX_VALUE, refreshInterval);

        // Cancel old task
        if (this.refreshTask != null && !this.refreshTask.isCancelled()) {
            this.refreshTask.cancel();
            this.refreshTask = null;
        }

        // Create a new task if applicable
        ensureRefreshTask();

        return this;
    }

    public CustomInventory refreshConfig() {
        if (inventoryConfigSectionReference == null) return this;

        final Optional<ConfigurationSection> optionalSection = inventoryConfigSectionReference.getIfExists();
        if (optionalSection.isEmpty()) return this;

        final ConfigurationSection section = optionalSection.get();
        this.inventoryConfig = InventoryConfig.parse(section);

        InventoryConfig.load(this, inventoryConfig);

        return this;
    }

    public CustomInventory disablePAPI() {
        this.placeholderApiSupport = false;
        return this;
    }

    @Deprecated(forRemoval = true)
    public CustomInventory papi() {
        this.placeholderApiSupport = true;
        return this;
    }

    @Deprecated(forRemoval = true)
    public CustomInventory papi(boolean toggle) {
        this.placeholderApiSupport = toggle;
        return this;
    }

    @Deprecated(forRemoval = true)
    public CustomInventory papi(Player player) {
        this.placeholderApiSupport = true;
        return this;
    }

    @Deprecated(forRemoval = true)
    public CustomInventory papiPlayer(Player player) {
        return this;
    }

    private void ensureRefreshTask() {
        // Cancel task if refresh interval is -1
        if (this.refreshInterval == -1 && this.refreshTask != null) {
            if (!this.refreshTask.isCancelled()) {
                this.refreshTask.cancel();
            }

            this.refreshTask = null;
        }

        // Set task as null if it's cancelled
        if (this.refreshTask != null && this.refreshTask.isCancelled()) {
            this.refreshTask = null;
        }

        // Create task if refreshInterval is positive and if there's no current task and if there are viewers
        if (this.refreshInterval != -1 && this.refreshTask == null && !getViewers().isEmpty()) {
            this.refreshTask = Tasks.sync().delayedTimer(task -> {
                if (getViewers().isEmpty()) {
                    task.cancel();
                    return;
                }

                if (placeholderApiSupport && bukkitInventory != null) {
                    String newTitle = Colors.color(Placeholders.set(mainViewer, this.title));
                    bukkitInventory.getViewers().forEach(humanEntity -> {
                        humanEntity.getOpenInventory().setTitle(newTitle);
                    });
                }

                this.redraw();
            }, 1L, this.refreshInterval);
        }
    }

    protected InventoryConfig config() {
        return this.inventoryConfig;
    }

    protected CustomInventory config(ConfigurationSection section) {
        this.inventoryConfig = InventoryConfig.parse(section);
        return this;
    }

    protected CustomInventory config(String configFile, String configPath) {
        this.inventoryConfigSectionReference = new ConfigSectionReference(configFile, configPath);
        this.inventoryConfig = InventoryConfig.parse(configFile, configPath);
        return this;
    }

    protected CustomInventory config(InventoryConfig inventoryConfig) {
        this.inventoryConfig = inventoryConfig;
        return this;
    }

    public CustomInventoryType getCustomInventoryType() {
        return inventoryType;
    }

    public List<Player> getViewers() {
        return InventoryManager.getOpenedPlayers(this);
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
        return inventoryConfig.getItem(configItemId);
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

    protected void fillRow(int row, Supplier<InventoryItem> item) {
        rootPane.fillRow(row, item);
    }

    protected void fillColumn(int column, Supplier<InventoryItem> item) {
        rootPane.fillColumn(column, item);
    }

    protected void fillBorders(Supplier<InventoryItem> item) {
        rootPane.fillBorders(item);
    }

    protected void fillRect(int fromRow, int fromColumn, int toRow, int toColumn, Supplier<InventoryItem> item) {
        rootPane.fillRect(fromRow, fromColumn, toRow, toColumn, item);
    }

    protected void fillRect(Slot fromPos, Slot toPos, Supplier<InventoryItem> item) {
        rootPane.fillRect(fromPos, toPos, item);
    }

    protected void clear() {
        rootPane.clear();
    }

}
