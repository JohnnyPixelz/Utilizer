package io.github.johnnypixelz.utilizer.inventories.inventories;

import io.github.johnnypixelz.utilizer.inventories.CustomInventory;
import io.github.johnnypixelz.utilizer.inventories.CustomInventoryType;
import io.github.johnnypixelz.utilizer.inventories.slot.Slot;
import io.github.johnnypixelz.utilizer.itemstack.Items;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ConfirmationInventory extends CustomInventory {
    private final String title;
    private final CustomInventoryType type;

    private final ItemStack confirmButtonStack;
    private final ItemStack cancelButtonStack;
    private final Slot confirmButtonSlot;
    private final Slot cancelButtonSlot;

    private final ItemStack infoStack;
    private final Slot infoSlot;

    private final Runnable onConfirm;
    private final Runnable onCancel;

    private final boolean closeInventoryOnPick;

    public ConfirmationInventory(ConfirmationInventoryBuilder builder) {
        this.title = builder.title;
        this.type = builder.type;

        this.confirmButtonStack = builder.confirmButtonStack;
        this.cancelButtonStack = builder.cancelButtonStack;
        this.confirmButtonSlot = builder.confirmButtonSlot;
        this.cancelButtonSlot = builder.cancelButtonSlot;

        this.infoStack = builder.infoStack;
        this.infoSlot = builder.infoSlot;

        this.onConfirm = builder.onConfirm;
        this.onCancel = builder.onCancel;

        this.closeInventoryOnPick = builder.closeInventoryOnPick;
    }

    @Override
    protected void onLoad() {
        title(title);
        type(type);
    }

    @Override
    protected void onDraw() {
        set(cancelButtonSlot, clickableItem(cancelButtonStack, event -> {
            if (onCancel != null) {
                onCancel.run();
            }

            if (closeInventoryOnPick && event.getWhoClicked() instanceof Player player) {
                close(player);
            }
        }));

        set(confirmButtonSlot, clickableItem(confirmButtonStack, event -> {
            if (onConfirm != null) {
                onConfirm.run();
            }

            if (closeInventoryOnPick && event.getWhoClicked() instanceof Player player) {
                close(player);
            }
        }));

        if (infoStack != null) {
            set(infoSlot, displayItem(infoStack));
        }
    }

    public static class ConfirmationInventoryBuilder {
        private String title = "Confirmation Box";
        private CustomInventoryType type = CustomInventoryType.CHEST_5;

        private ItemStack confirmButtonStack = Items.edit(Material.LIME_STAINED_GLASS_PANE)
                .setDisplayName("&aConfirm")
                .getItem();
        private ItemStack cancelButtonStack = Items.edit(Material.RED_STAINED_GLASS_PANE)
                .setDisplayName("&cCancel")
                .getItem();
        private Slot confirmButtonSlot = Slot.of(3, 6);
        private Slot cancelButtonSlot = Slot.of(3, 2);

        private ItemStack infoStack = null;
        private Slot infoSlot = Slot.of(1, 4);

        private Runnable onConfirm = () -> {};
        private Runnable onCancel = () -> {};

        private boolean closeInventoryOnPick = true;

        public ConfirmationInventoryBuilder setTitle(String title) {
            this.title = title;
            return this;
        }

        public ConfirmationInventoryBuilder setType(CustomInventoryType type) {
            this.type = type;
            return this;
        }

        public ConfirmationInventoryBuilder setConfirmButtonStack(ItemStack confirmButtonStack) {
            this.confirmButtonStack = confirmButtonStack;
            return this;
        }

        public ConfirmationInventoryBuilder setCancelButtonStack(ItemStack cancelButtonStack) {
            this.cancelButtonStack = cancelButtonStack;
            return this;
        }

        public ConfirmationInventoryBuilder setConfirmButtonSlot(Slot confirmButtonSlot) {
            this.confirmButtonSlot = confirmButtonSlot;
            return this;
        }

        public ConfirmationInventoryBuilder setCancelButtonSlot(Slot cancelButtonSlot) {
            this.cancelButtonSlot = cancelButtonSlot;
            return this;
        }

        public ConfirmationInventoryBuilder setInfoStack(ItemStack infoStack) {
            this.infoStack = infoStack;
            return this;
        }

        public ConfirmationInventoryBuilder setInfoSlot(Slot infoSlot) {
            this.infoSlot = infoSlot;
            return this;
        }

        public ConfirmationInventoryBuilder setOnConfirm(Runnable onConfirm) {
            this.onConfirm = onConfirm;
            return this;
        }

        public ConfirmationInventoryBuilder setOnCancel(Runnable onCancel) {
            this.onCancel = onCancel;
            return this;
        }

        public ConfirmationInventoryBuilder setCloseInventoryOnPick(boolean closeInventoryOnPick) {
            this.closeInventoryOnPick = closeInventoryOnPick;
            return this;
        }

        public ConfirmationInventory build() {
            return new ConfirmationInventory(this);
        }

    }
}
