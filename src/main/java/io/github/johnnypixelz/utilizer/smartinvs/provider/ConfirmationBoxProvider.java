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

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class ConfirmationBoxProvider implements InventoryProvider {
    private Consumer<Player> onConfirm;
    private Consumer<Player> onDecline;
    private Consumer<Player> onClose;
    private ItemStack preview;
    private String title;

    public static ConfirmationBoxProviderBuilder builder() {
        return new ConfirmationBoxProviderBuilder();
    }

    private ConfirmationBoxProvider() {
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        ItemStack confirmStack = Items.edit(PremadeItems.getPane(PaneType.GREEN))
                .setDisplayName("&aConfirm")
                .getItem();
        ItemStack declineStack = Items.edit(PremadeItems.getPane(PaneType.RED))
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

    public static class ConfirmationBoxProviderBuilder {
        private final ConfirmationBoxProvider provider;

        private ConfirmationBoxProviderBuilder() {
            this.provider = new ConfirmationBoxProvider();
        }

        public ConfirmationBoxProviderBuilder setOnConfirm(Consumer<Player> onConfirm) {
            provider.onConfirm = onConfirm;
            return this;
        }

        public ConfirmationBoxProviderBuilder setOnDecline(Consumer<Player> onDecline) {
            provider.onDecline = onDecline;
            return this;
        }

        public ConfirmationBoxProviderBuilder setOnClose(Consumer<Player> onClose) {
            provider.onClose = onClose;
            return this;
        }

        public ConfirmationBoxProviderBuilder setPreview(ItemStack stack) {
            provider.preview = stack;
            return this;
        }

        public ConfirmationBoxProviderBuilder setTitle(String title) {
            provider.title = title;
            return this;
        }

        @Nonnull
        public SmartInventory build() {
            SmartInventory.Builder builder = SmartInventory.builder()
                    .provider(provider)
                    .size(3, 9)
                    .title(Colors.color(provider.title == null ? "Confirmation Prompt" : provider.title));

            if (provider.onClose != null) {
                builder.listener(new InventoryCloseListener(event -> {
                    if (provider.onClose == null) return;
                    provider.onClose.accept((Player) event.getPlayer());
                }));
            }

            return builder.build();
        }
    }
}
