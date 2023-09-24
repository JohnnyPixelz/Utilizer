package io.github.johnnypixelz.utilizer.inv.elements;

import io.github.johnnypixelz.utilizer.inv.Element;
import io.github.johnnypixelz.utilizer.inv.ElementValue;
import io.github.johnnypixelz.utilizer.itemstack.Items;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Button extends Element {

    public static Button of(Supplier<ItemStack> stack) {
        final Button button = new Button();

        button.getIcon().setValueSilently(stack);

        return button;
    }

    private final ElementValue<Supplier<ItemStack>> icon = v(() -> Items.AIR);
    private final ElementValue<Consumer<Player>> leftClickRunnable = v(player -> {});
    private final ElementValue<Consumer<Player>> rightClickRunnable = v(player -> {});
    private final ElementValue<Consumer<Player>> shiftClickRunnable = v(player -> {});
    private final ElementValue<Consumer<Player>> middleClickRunnable = v(player -> {});

    private Button() {
    }

    public ElementValue<Supplier<ItemStack>> getIcon() {
        return icon;
    }

    public Button onLeftClick(Consumer<Player> onLeftClick) {
        this.leftClickRunnable.setValue(onLeftClick);
        return this;
    }

    public Button onRightClick(Consumer<Player> onRightClick) {
        this.rightClickRunnable.setValue(onRightClick);
        return this;
    }

    public Button onShiftClick(Consumer<Player> onShiftClick) {
        this.shiftClickRunnable.setValue(onShiftClick);
        return this;
    }

    public Button onMiddleClick(Consumer<Player> onMiddleClick) {
        this.middleClickRunnable.setValue(onMiddleClick);
        return this;
    }

    @Override
    protected void handleClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
            shiftClickRunnable.getValue().accept(player);
        } else if (event.getClick() == ClickType.LEFT) {
            leftClickRunnable.getValue().accept(player);
        } else if (event.getClick() == ClickType.RIGHT) {
            rightClickRunnable.getValue().accept(player);
        } else if (event.getClick() == ClickType.MIDDLE) {
            middleClickRunnable.getValue().accept(player);
        }
    }

    @Override
    public void render() {
        setItem(icon.getValue().get());
    }

}
