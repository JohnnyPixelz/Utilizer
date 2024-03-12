package io.github.johnnypixelz.utilizer.features.customblocks.builder;

import io.github.johnnypixelz.utilizer.features.customblocks.CustomBlock;
import io.github.johnnypixelz.utilizer.features.customblocks.customblockcustomitem.CustomBlockCustomItemManager;
import io.github.johnnypixelz.utilizer.features.customblocks.customblockcustomitem.CustomBlockData;
import io.github.johnnypixelz.utilizer.features.customblocks.customblockcustomitem.StatefulCustomBlock;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public class CustomBlockWithDataBuilderDetailStep<CB extends StatefulCustomBlock<CBD>, CBD extends CustomBlockData> {
    private final Class<CB> customBlockType;
    private final Class<CBD> customBlockDataType;
    private final String fileName;
    private final NamespacedKey key;
    private final Function<CBD, ItemStack> itemSupplier;
    private boolean stackable;

    public CustomBlockWithDataBuilderDetailStep(Class<CB> customBlockType, Class<CBD> customBlockDataType, String fileName, NamespacedKey key, Function<CBD, ItemStack> itemSupplier) {
        this.customBlockType = customBlockType;
        this.customBlockDataType = customBlockDataType;
        this.fileName = fileName;
        this.key = key;
        this.itemSupplier = itemSupplier;
        this.stackable = true;
    }

    public CustomBlockWithDataBuilderDetailStep<CB, CBD> setStackable(boolean stackable) {
        this.stackable = stackable;
        return this;
    }

    public CustomBlockCustomItemManager<CB, CBD> build() {
        return new CustomBlockCustomItemManager<>(customBlockType, customBlockDataType, fileName, key, stackable, itemSupplier);
    }

}
