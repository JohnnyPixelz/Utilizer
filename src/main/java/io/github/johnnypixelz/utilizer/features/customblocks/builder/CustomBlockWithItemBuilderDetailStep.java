package io.github.johnnypixelz.utilizer.features.customblocks.builder;

import io.github.johnnypixelz.utilizer.features.customblocks.CustomBlock;
import io.github.johnnypixelz.utilizer.features.customblocks.customblockitem.CustomBlockItemManager;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public class CustomBlockWithItemBuilderDetailStep<CB extends CustomBlock> {
    private final Class<CB> customBlockType;
    private final String fileName;
    private final NamespacedKey key;
    private final Function<CB, ItemStack> itemSupplier;
    private boolean stackable;

    public CustomBlockWithItemBuilderDetailStep(Class<CB> customBlockType, String fileName, NamespacedKey key, Function<CB, ItemStack> itemSupplier) {
        this.customBlockType = customBlockType;
        this.fileName = fileName;
        this.key = key;
        this.itemSupplier = itemSupplier;
        this.stackable = true;
    }

    public CustomBlockWithItemBuilderDetailStep<CB> stackable(boolean stackable) {
        this.stackable = stackable;
        return this;
    }

    public CustomBlockItemManager<CB> build() {
        return new CustomBlockItemManager<>(customBlockType, fileName, key, stackable, itemSupplier);
    }

}
