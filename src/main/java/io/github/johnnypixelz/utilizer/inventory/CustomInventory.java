package io.github.johnnypixelz.utilizer.inventory;

import io.github.johnnypixelz.utilizer.inventory.content.InventoryContents;
import io.github.johnnypixelz.utilizer.inventory.content.InventoryProvider;
import io.github.johnnypixelz.utilizer.inventory.openers.InventoryOpener;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unchecked")
public class CustomInventory {

    private String id;
    private String title;
    private InventoryType type;
    private int rows, columns;
    private boolean closeable;
    private boolean openParentOnClose;

    private InventoryProvider provider;
    private CustomInventory parent;

    private List<InventoryListener<? extends Event>> listeners;

    private CustomInventory() {
    }

    public Inventory open(Player player) {
        return open(player, 0);
    }

    public Inventory open(Player player, int page) {
        Optional<CustomInventory> oldInv = InventoryManager.getInventory(player);

        oldInv.ifPresent(inv -> {
            inv.getListeners().stream()
                    .filter(listener -> listener.getType() == InventoryCloseEvent.class)
                    .forEach(listener -> ((InventoryListener<InventoryCloseEvent>) listener)
                            .accept(new InventoryCloseEvent(player.getOpenInventory())));

            InventoryManager.setInventory(player, null);
        });

        InventoryContents contents = new InventoryContents(this, player.getUniqueId());
        contents.pagination().page(page);

        InventoryManager.setContents(player, contents);

        try {
            this.provider.init(player, contents);

            // If the current inventory has been closed or replaced within the init method, returns
            if (!InventoryManager.getContents(player).equals(Optional.of(contents))) {
                return null;
            }

            InventoryOpener opener = InventoryManager.findOpener(type)
                    .orElseThrow(() -> new IllegalStateException("No opener found for the inventory type " + type.name()));
            Inventory handle = opener.open(this, player);

            InventoryManager.setInventory(player, this);

            return handle;
        } catch (Exception e) {
            InventoryManager.handleInventoryOpenError(this, player, e);
            return null;
        }
    }

    public void close(Player player) {
        listeners.stream()
                .filter(listener -> listener.getType() == InventoryCloseEvent.class)
                .forEach(listener -> ((InventoryListener<InventoryCloseEvent>) listener)
                        .accept(new InventoryCloseEvent(player.getOpenInventory())));

        InventoryManager.setInventory(player, null);
        player.closeInventory();

        InventoryManager.setContents(player, null);
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public InventoryType getType() {
        return type;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public boolean doesOpenParentOnClose() {
        return openParentOnClose;
    }

    public void setOpenParentOnClose(boolean openParentOnClose) {
        this.openParentOnClose = openParentOnClose;
    }

    public boolean isCloseable() {
        return closeable;
    }

    public void setCloseable(boolean closeable) {
        this.closeable = closeable;
    }

    public InventoryProvider getProvider() {
        return provider;
    }

    public Optional<CustomInventory> getParent() {
        return Optional.ofNullable(parent);
    }

    List<InventoryListener<? extends Event>> getListeners() {
        return listeners;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String id = "unknown";
        private String title = "";
        private InventoryType type = InventoryType.CHEST;
        private int rows = 6, columns = 9;
        private boolean closeable = true;
        private boolean openParentOnClose = false;

        private InventoryProvider provider;
        private CustomInventory parent;

        private final List<InventoryListener<? extends Event>> listeners = new ArrayList<>();

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder type(InventoryType type) {
            this.type = type;
            return this;
        }

        public Builder size(int rows, int columns) {
            this.rows = rows;
            this.columns = columns;
            return this;
        }

        public Builder closeable(boolean closeable) {
            this.closeable = closeable;
            return this;
        }

        public Builder provider(InventoryProvider provider) {
            this.provider = provider;
            return this;
        }

        public Builder parent(CustomInventory parent) {
            this.parent = parent;
            return this;
        }

        public Builder openParentOnClose() {
            this.openParentOnClose = true;
            return this;
        }

        public Builder listener(InventoryListener<? extends Event> listener) {
            this.listeners.add(listener);
            return this;
        }

        public CustomInventory build() {
            if (this.provider == null)
                throw new IllegalStateException("The provider of the SmartInventory.Builder must be set.");

            CustomInventory inv = new CustomInventory();
            inv.id = this.id;
            inv.title = this.title;
            inv.type = this.type;
            inv.rows = this.rows;
            inv.columns = this.columns;
            inv.closeable = this.closeable;
            inv.provider = this.provider;
            inv.parent = this.parent;
            inv.listeners = this.listeners;
            inv.openParentOnClose = this.openParentOnClose;

            return inv;
        }
    }

}
