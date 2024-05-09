package io.github.johnnypixelz.utilizer.inventories.items;

import io.github.johnnypixelz.utilizer.event.StatefulEventEmitter;
import io.github.johnnypixelz.utilizer.inventories.InventoryItem;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class SwitchItem extends InventoryItem {
    private final ItemStack offStack;
    private final ItemStack onStack;
    private final StatefulEventEmitter<Boolean> onSwitch;
    private boolean state;

    public SwitchItem(ItemStack offStack, ItemStack onStack) {
        this(offStack, onStack, false);
    }

    public SwitchItem(ItemStack offStack, ItemStack onStack, boolean state) {
        this.offStack = offStack;
        this.onStack = onStack;
        this.state = state;

        this.onSwitch = new StatefulEventEmitter<>();

        getOnLeftClick().listen(event -> {
            flipState();
            onSwitch.emit(getState());
        });
    }

    public boolean getState() {
        return state;
    }

    public boolean setState(boolean state) {
        if (this.state != state) {
            flipState();
            this.onSwitch.emit(state);
        }

        return state;
    }

    public void flipState() {
        this.state = !state;
        set(state ? onStack : offStack);
    }

    public SwitchItem onSwitch(Consumer<Boolean> onSwitch) {
        this.onSwitch.listen(onSwitch);
        return this;
    }

    @Override
    protected void onMount() {
        set(state ? onStack : offStack);
    }

}
