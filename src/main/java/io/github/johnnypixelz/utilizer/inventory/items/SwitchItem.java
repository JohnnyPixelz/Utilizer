package io.github.johnnypixelz.utilizer.inventory.items;

import io.github.johnnypixelz.utilizer.event.BiStatefulEventEmitter;
import io.github.johnnypixelz.utilizer.event.StatefulEventEmitter;
import io.github.johnnypixelz.utilizer.inventory.InventoryItem;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SwitchItem extends InventoryItem {
    private final ItemStack offStack;
    private final ItemStack onStack;
    private final BiStatefulEventEmitter<Boolean, InventoryClickEvent> onSwitch;
    private boolean state;

    public SwitchItem(ItemStack offStack, ItemStack onStack) {
        this(offStack, onStack, false);
    }

    public SwitchItem(ItemStack offStack, ItemStack onStack, boolean state) {
        this.offStack = offStack;
        this.onStack = onStack;
        this.state = state;

        this.onSwitch = new BiStatefulEventEmitter<>();

        getOnLeftClick().listen(event -> {
            flipState();
            onSwitch.emit(state, event);
        });
    }

    public void flipState() {
        this.state = !state;
        setAll(state ? onStack : offStack);
    }

    public SwitchItem onSwitch(Consumer<Boolean> onSwitch) {
        this.onSwitch.listen((state, event) -> onSwitch.accept(state));
        return this;
    }

    public SwitchItem onSwitch(BiConsumer<Boolean, InventoryClickEvent> onSwitch) {
        this.onSwitch.listen(onSwitch);
        return this;
    }

    @Override
    protected void onMount(int slot) {
        set(slot, state ? onStack : offStack);
    }

    @Override
    protected void onUnmount(int slot) {
        remove(slot);
    }

}
