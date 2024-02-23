package io.github.johnnypixelz.utilizer.gui;

import io.github.johnnypixelz.utilizer.config.Parse;
import io.github.johnnypixelz.utilizer.text.Colors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Objects;

public class Screen extends SizedElement {
    private Inventory inventory;
    private final ElementValue<String> title;
    private final ElementValue<Integer> rows;
    private boolean initialRendered;

    public Screen() {
        super(ElementSize.of(1, 9));

        this.title = ElementValue.of("&r")
                .setOnUpdate(this::updateInventoryRender);
        this.rows = ElementValue.of(1)
                .setOnUpdate(rows -> {
                    getSize().setValueSilently(ElementSize.of(rows, 9));
                    updateInventoryRender();
                });

        this.initialRendered = false;
    }

    public void setTitle(String title) {
        final String newTitle = Objects.requireNonNullElse(title, "&r");
        this.title.setValue(newTitle);
    }

    public void setRows(int rows) {
        int newRows = Parse.constrain(1, 6, rows);
        this.rows.setValue(newRows);
    }

    public void show(Player player) {
        if (inventory == null) {
            createInventory();
        }

        if (!initialRendered) {
            render();
            initialRendered = true;
        }

        player.openInventory(inventory);
        ScreenManager.registerViewer(player, this);
    }

    private void createInventory() {
        int size = getSize().getValue().getRows() * 9;

        inventory = Bukkit.createInventory(
                null,
                size,
                Colors.color(title.getValue())
        );
    }

    private void updateInventoryRender() {
        final List<Player> viewers = ScreenManager.getViewers(this);

        if (inventory == null) return;
        createInventory();

        if (!initialRendered) return;
        render();

        viewers.forEach(this::show);
    }

    public Inventory getInventory() {
        return inventory;
    }

}
