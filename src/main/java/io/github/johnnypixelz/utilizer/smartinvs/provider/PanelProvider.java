package io.github.johnnypixelz.utilizer.smartinvs.provider;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import io.github.johnnypixelz.utilizer.itemstack.Items;
import io.github.johnnypixelz.utilizer.smartinvs.PaneType;
import io.github.johnnypixelz.utilizer.smartinvs.PremadeItems;
import io.github.johnnypixelz.utilizer.smartinvs.listener.InventoryCloseListener;
import io.github.johnnypixelz.utilizer.text.Colors;
import io.github.johnnypixelz.utilizer.text.Numbers;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class PanelProvider<T> implements InventoryProvider {
    private final List<T> elements;
    private final Function<T, ItemStack> stackGenerator;
    private ItemStack arrow;
    private BiConsumer<InventoryClickEvent, T> callback;
    private Consumer<T> leftClick;
    private Consumer<T> rightClick;
    private Consumer<T> middleClick;
    private Consumer<Player> onClose;
    private String title;
    private int size = 5;

    private PanelProvider(List<T> elements, Function<T, ItemStack> stackGenerator) {
        this.elements = elements;
        this.stackGenerator = stackGenerator;
    }

    @Nonnull
    public static <T> PanelProvider<T> of(@Nonnull List<T> elements, @Nonnull Function<T, ItemStack> stackGenerator) {
        return new PanelProvider<>(elements, stackGenerator);
    }

    @Nonnull
    public PanelProvider<T> setSize(int size) {
        if (size >= 3 && size <= 6) {
            this.size = size;
        }
        return this;
    }

    // Placeholders: %navigation% (Previous/Next) %page% %maxpage%
    @Nonnull
    public PanelProvider<T> setArrow(@Nullable ItemStack stack) {
        this.arrow = stack;
        return this;
    }

    @Nonnull
    public PanelProvider<T> setCallback(@Nullable BiConsumer<InventoryClickEvent, T> callback) {
        this.callback = callback;
        return this;
    }

    @Nonnull
    public PanelProvider<T> onLeftClick(@Nullable Consumer<T> leftClick) {
        this.leftClick = leftClick;
        return this;
    }

    @Nonnull
    public PanelProvider<T> onRightClick(@Nullable Consumer<T> rightClick) {
        this.rightClick = rightClick;
        return this;
    }

    @Nonnull
    public PanelProvider<T> onMiddleClick(@Nullable Consumer<T> middleClick) {
        this.middleClick = middleClick;
        return this;
    }

    @Nonnull
    public PanelProvider<T> setOnClose(@Nullable Consumer<Player> onClose) {
        this.onClose = onClose;
        return this;
    }

    @Nonnull
    public PanelProvider<T> setTitle(@Nullable String title) {
        this.title = title;
        return this;
    }

    @Nonnull
    public SmartInventory build() {
        SmartInventory.Builder builder = SmartInventory.builder()
                .provider(this)
                .size(size, 9)
                .title(Colors.color(this.title == null ? "&7" : this.title));

        if (onClose != null) {
            builder.listener(new InventoryCloseListener(event -> {
                if (onClose == null) return;
                onClose.accept((Player) event.getPlayer());
            }));
        }

        return builder.build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fillBorders(ClickableItem.empty(PremadeItems.getCustomPane(PaneType.GRAY)));

        Pagination page = contents.pagination();
        page.setItemsPerPage((size - 2) * 7);
        ClickableItem[] items = new ClickableItem[elements.size()];

        for (int i = 0; i < elements.size(); i++) {
            T element = elements.get(i);
            ItemStack item = stackGenerator.apply(element);
            if (item == null) continue;

            items[i] = ClickableItem.of(item, click -> {
                onClose = null;

                if (callback != null) {
                    callback.accept(click, element);
                }

                if (click.getClick() == ClickType.LEFT && leftClick != null) {
                    leftClick.accept(element);
                }

                if (click.getClick() == ClickType.RIGHT && rightClick != null) {
                    rightClick.accept(element);
                }

                if (click.getClick() == ClickType.MIDDLE && middleClick != null) {
                    middleClick.accept(element);
                }
            });
        }

        page.setItems(items);
        page.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0).allowOverride(false));

        ItemStack arrow = this.arrow != null
                ? this.arrow
                : Items.edit(Material.ARROW).setDisplayName("&7%navigation%").setLore("", "&7You're viewing page %page%/%maxpage%").getItem();

        if (!page.isFirst()) {
            ItemStack previous = Items.edit(arrow.clone())
                    .map(s -> s.replace("%navigation%", "Previous")
                            .replace("%page%", Numbers.toFormatted(page.getPage() + 1))
                            .replace("%maxpage%", Numbers.toFormatted((elements.size() / ((size - 2) * 7L)) + 1))
                    )
                    .getItem();
            contents.set(size - 1, 3, ClickableItem.of(previous, click -> contents.inventory().open(player, page.previous().getPage())));
        }

        if (!page.isLast()) {
            ItemStack previous = Items.edit(arrow.clone())
                    .map(s -> s.replace("%navigation%", "Next")
                            .replace("%page%", Numbers.toFormatted(page.getPage() + 1))
                            .replace("%maxpage%", Numbers.toFormatted((elements.size() / ((size - 2) * 7L)) + 1))
                    )
                    .getItem();
            contents.set(size - 1, 5, ClickableItem.of(previous, click -> contents.inventory().open(player, page.next().getPage())));
        }
    }

}
