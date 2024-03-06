package io.github.johnnypixelz.utilizer.smartinvs.provider;

import com.cryptomorin.xseries.XMaterial;
import io.github.johnnypixelz.utilizer.inventory.content.InventoryProvider;
import io.github.johnnypixelz.utilizer.itemstack.Items;
import io.github.johnnypixelz.utilizer.smartinvs.listener.InventoryCloseListener;
import io.github.johnnypixelz.utilizer.text.Colors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class OnlinePlayerPickerProvider implements InventoryProvider {
    private Consumer<Player> callback;
    private Predicate<Player> filter;
    private Consumer<Player> onClose;
    private String title;
    private int timer;

    public OnlinePlayerPickerProvider setCallback(@Nonnull Consumer<Player> callback) {
        this.callback = callback;
        return this;
    }

    public OnlinePlayerPickerProvider setPlayerFilter(@Nullable Predicate<Player> filter) {
        this.filter = filter;
        return this;
    }

    public OnlinePlayerPickerProvider setOnClose(@Nullable Consumer<Player> onClose) {
        this.onClose = onClose;
        return this;
    }

    public OnlinePlayerPickerProvider setTitle(String title) {
        this.title = title;
        return this;
    }

    public void open(Player player) {
        if (callback == null) return;

        SmartInventory.Builder builder = SmartInventory.builder()
                .provider(this)
                .size(6, 9)
                .title(Colors.color(this.title == null ? "&7Pick a player" : this.title));

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
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

        if (filter != null) {
            players = players.stream()
                    .filter(filter)
                    .collect(Collectors.toList());
        }

        Pagination page = contents.pagination();
        page.setItemsPerPage(45);
        ClickableItem[] items = new ClickableItem[players.size()];

        for (int i = 0; i < players.size(); i++) {
            Player onlinePlayer = players.get(i);
            ItemStack item = Items.edit(XMaterial.PLAYER_HEAD.parseItem())
                    .setDisplayName("&7" + onlinePlayer.getName())
                    .getItem();

            items[i] = ClickableItem.of(item, click -> {
                if (!onlinePlayer.isOnline()) return;
                if (!click.isLeftClick()) return;
                onClose = null;
                callback.accept(onlinePlayer);
            });
        }

        page.setItems(items);
        page.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0));

        ItemStack previous = Items.edit(Material.ARROW).setDisplayName("&7Previous").getItem();
        contents.set(5, 0, ClickableItem.of(previous, click -> {
            contents.inventory().open(player, page.previous().getPage());
        }));
        ItemStack next = Items.edit(Material.ARROW).setDisplayName("&7Next").getItem();
        contents.set(5, 8, ClickableItem.of(next, click -> {
            contents.inventory().open(player, page.next().getPage());
        }));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        if (timer++ < 5) return;

        timer = 0;
        contents.fill(null);
        init(player, contents);
    }
}
