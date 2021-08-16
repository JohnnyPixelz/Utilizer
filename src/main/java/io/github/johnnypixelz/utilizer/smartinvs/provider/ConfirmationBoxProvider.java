package io.github.johnnypixelz.utilizer.smartinvs.provider;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import io.github.johnnypixelz.utilizer.itemstack.Items;
import io.github.johnnypixelz.utilizer.itemstack.PaneType;
import io.github.johnnypixelz.utilizer.itemstack.PremadeItems;
import io.github.johnnypixelz.utilizer.smartinvs.listener.InventoryCloseListener;
import io.github.johnnypixelz.utilizer.text.Colors;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class ConfirmationBoxProvider implements InventoryProvider {
    private Consumer<Player> onConfirm;
    private Consumer<Player> onDecline;
    private Consumer<Player> onClose;
    private ItemStack preview;
    private String title;

    public ConfirmationBoxProvider setOnConfirm(Consumer<Player> onConfirm) {
        this.onConfirm = onConfirm;
        return this;
    }

    public ConfirmationBoxProvider setOnDecline(Consumer<Player> onDecline) {
        this.onDecline = onDecline;
        return this;
    }

    public ConfirmationBoxProvider setOnClose(Consumer<Player> onClose) {
        this.onClose = onClose;
        return this;
    }

    public ConfirmationBoxProvider setPreview(ItemStack stack) {
        preview = stack;
        return this;
    }

    public void show(Player player) {
        SmartInventory.Builder builder = SmartInventory.builder()
                .provider(this)
                .title(Colors.color(this.title == null ? "&7Confirmation Prompt" : this.title))
                .size(3, 9);

        if (onClose != null) {
            builder.listener(new InventoryCloseListener(event -> {
                if (onClose == null) return;
                onClose.accept((Player) event.getPlayer());
            }));
        }

        builder.build().open(player);
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        ItemStack confirmStack = Items.edit(PremadeItems.getPane(PaneType.GREEN))
                .setDisplayName("&aConfirm")
                .getItem();
        ItemStack declineStack = Items.edit(PremadeItems.getPane(PaneType.GREEN))
                .setDisplayName("&cDecline")
                .getItem();

        contents.set(1, 2, ClickableItem.of(declineStack, click -> {
            if (!click.isLeftClick()) return;
            if (onDecline != null) {
                onClose = null;
                onDecline.accept(player);
            }
        }));

        contents.set(1, 6, ClickableItem.of(confirmStack, click -> {
            if (!click.isLeftClick()) return;
            if (onConfirm != null) {
                onClose = null;
                onConfirm.accept(player);
            }
        }));

        if (preview != null) {
            contents.set(1, 4, ClickableItem.empty(preview));
        }
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
