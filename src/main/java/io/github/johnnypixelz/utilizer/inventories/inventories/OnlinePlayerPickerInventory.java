package io.github.johnnypixelz.utilizer.inventories.inventories;

import com.cryptomorin.xseries.XMaterial;
import io.github.johnnypixelz.utilizer.inventories.CustomInventory;
import io.github.johnnypixelz.utilizer.inventories.CustomInventoryType;
import io.github.johnnypixelz.utilizer.inventories.panes.PaginatedPane;
import io.github.johnnypixelz.utilizer.inventories.slot.Slot;
import io.github.johnnypixelz.utilizer.itemstack.Items;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class OnlinePlayerPickerInventory extends CustomInventory {
    private final String title;
    private final CustomInventoryType type;
    private final Slot fromSlot;
    private final Slot toSlot;

    private final ItemStack previousButtonStack;
    private final ItemStack nextButtonStack;

    private final Slot previousButtonSlot;
    private final Slot nextButtonSlot;

    private final Predicate<Player> filter;
    private final Function<Player, ItemStack> converter;
    private final Consumer<Player> onPick;

    private final boolean closeInventoryOnPick;
    private final long refreshInterval;

    private OnlinePlayerPickerInventory(OnlinePlayerPickerInventoryBuilder builder) {
        this.title = builder.title;
        this.type = builder.type;
        this.fromSlot = builder.fromSlot;
        this.toSlot = builder.toSlot;

        this.previousButtonSlot = builder.previousButtonSlot;
        this.nextButtonSlot = builder.nextButtonSlot;

        this.previousButtonStack = builder.previousButtonStack;
        this.nextButtonStack = builder.nextButtonStack;

        this.filter = builder.filter;
        this.converter = builder.converter;
        this.onPick = builder.onPick;

        this.closeInventoryOnPick = builder.closeInventoryOnPick;
        this.refreshInterval = builder.refreshInterval;
    }

    @Override
    protected void onLoad() {
        title(title);
        type(type);

        if (refreshInterval > 0) {
            refresh(refreshInterval);
        }
    }

    @Override
    protected void onDraw() {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

        if (filter != null) {
            players = players.stream()
                    .filter(filter)
                    .toList();
        }

        paginatedPane(fromSlot, toSlot)
                .setItems(players, player -> {
                    return clickableItem(converter.apply(player)).leftClick(event -> {
                        if (!player.isOnline()) return;

                        if (this.closeInventoryOnPick) {
                            event.getWhoClicked().closeInventory();
                        }

                        if (onPick != null) {
                            onPick.accept(player);
                        }
                    });
                })
                .pageButtons(previousButtonStack, nextButtonStack, (previousItem, nextItem) -> {
                    set(previousButtonSlot, previousItem);
                    set(nextButtonSlot, nextItem);
                });
    }

    public static OnlinePlayerPickerInventoryBuilder builder() {
        return new OnlinePlayerPickerInventoryBuilder();
    }

    public static class OnlinePlayerPickerInventoryBuilder {
        private String title = "Pick a player";
        private CustomInventoryType type = CustomInventoryType.CHEST_6;
        private Slot fromSlot = Slot.of(0, 0);
        private Slot toSlot = Slot.of(4, 8);

        private ItemStack previousButtonStack = PaginatedPane.ButtonDirection.PREVIOUS.getDefaultItemStack();
        private ItemStack nextButtonStack = PaginatedPane.ButtonDirection.NEXT.getDefaultItemStack();

        private Slot previousButtonSlot = Slot.of(5, 0);
        private Slot nextButtonSlot = Slot.of(5, 8);

        private Predicate<Player> filter = null;
        private Function<Player, ItemStack> converter = player -> Items.edit(XMaterial.PLAYER_HEAD.parseItem())
                .setDisplayName("&7" + player.getName())
                .getItem();
        private Consumer<Player> onPick = null;

        private boolean closeInventoryOnPick = true;
        private long refreshInterval = 20L;

        public OnlinePlayerPickerInventoryBuilder setTitle(String title) {
            this.title = title;
            return this;
        }

        public OnlinePlayerPickerInventoryBuilder setType(CustomInventoryType type) {
            this.type = type;
            return this;
        }

        public OnlinePlayerPickerInventoryBuilder setDimensions(Slot fromSlot, Slot toSlot) {
            this.fromSlot = fromSlot;
            this.toSlot = toSlot;
            return this;
        }

        public OnlinePlayerPickerInventoryBuilder setPreviousButtonSlot(Slot previousButtonSlot) {
            this.previousButtonSlot = previousButtonSlot;
            return this;
        }

        public OnlinePlayerPickerInventoryBuilder setNextButtonSlot(Slot nextButtonSlot) {
            this.nextButtonSlot = nextButtonSlot;
            return this;
        }

        public OnlinePlayerPickerInventoryBuilder setPreviousButtonStack(ItemStack previousButtonStack) {
            this.previousButtonStack = previousButtonStack;
            return this;
        }

        public OnlinePlayerPickerInventoryBuilder setNextButtonStack(ItemStack nextButtonStack) {
            this.nextButtonStack = nextButtonStack;
            return this;
        }

        public OnlinePlayerPickerInventoryBuilder setFilter(Predicate<Player> filter) {
            this.filter = filter;
            return this;
        }

        public OnlinePlayerPickerInventoryBuilder setConverter(Function<Player, ItemStack> converter) {
            this.converter = converter;
            return this;
        }

        public OnlinePlayerPickerInventoryBuilder setOnPick(Consumer<Player> onPick) {
            this.onPick = onPick;
            return this;
        }

        public OnlinePlayerPickerInventoryBuilder setCloseInventoryOnPick(boolean closeInventoryOnPick) {
            this.closeInventoryOnPick = closeInventoryOnPick;
            return this;
        }

        public OnlinePlayerPickerInventoryBuilder setRefreshInterval(long refreshInterval) {
            this.refreshInterval = refreshInterval;
            return this;
        }

        public OnlinePlayerPickerInventoryBuilder disableRefresh() {
            this.refreshInterval = -1;
            return this;
        }

        public OnlinePlayerPickerInventory build() {
            return new OnlinePlayerPickerInventory(this);
        }
    }
}
