package io.github.johnnypixelz.utilizer.features.customblocks;

import io.github.johnnypixelz.utilizer.itemstack.ItemSupplier;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public class CustomBlockItemSupplier<T extends CustomBlock> extends ItemSupplier {
    private final Function<T, ItemStack> customBlockSupplier;

    public CustomBlockItemSupplier(String itemId, boolean stackable, Function<T, ItemStack> supplier) {
        super(itemId, stackable, () -> supplier.apply(null));
        this.customBlockSupplier = supplier;
    }

    public ItemStack getItemStack(T customBlock) {
        return customBlockSupplier.apply(customBlock);
    }

}
