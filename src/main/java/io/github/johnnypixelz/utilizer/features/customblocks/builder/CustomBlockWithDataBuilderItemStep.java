package io.github.johnnypixelz.utilizer.features.customblocks.builder;

import io.github.johnnypixelz.utilizer.features.customblocks.CustomBlock;
import io.github.johnnypixelz.utilizer.features.customblocks.customblockcustomitem.CustomBlockData;
import io.github.johnnypixelz.utilizer.features.customblocks.customblockcustomitem.StatefulCustomBlock;
import io.github.johnnypixelz.utilizer.plugin.Provider;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public class CustomBlockWithDataBuilderItemStep<CB extends StatefulCustomBlock<CBD>, CBD extends CustomBlockData> {
    private final Class<CB> customBlockType;
    private final Class<CBD> customBlockDataType;
    private final String fileName;

    public CustomBlockWithDataBuilderItemStep(Class<CB> customBlockType, Class<CBD> customBlockDataType, String fileName) {
        this.customBlockType = customBlockType;
        this.customBlockDataType = customBlockDataType;
        this.fileName = fileName;
    }

    public CustomBlockWithDataBuilderDetailStep<CB, CBD> itemed(Function<CBD, ItemStack> itemSupplier) {
        return itemed(fileName, itemSupplier);
    }

    public CustomBlockWithDataBuilderDetailStep<CB, CBD> itemed(String key, Function<CBD, ItemStack> itemSupplier) {
        return itemed(Provider.getNamespacedKey(key), itemSupplier);
    }

    public CustomBlockWithDataBuilderDetailStep<CB, CBD> itemed(NamespacedKey key, Function<CBD, ItemStack> itemSupplier) {
        return new CustomBlockWithDataBuilderDetailStep<>(customBlockType, customBlockDataType, fileName, key, itemSupplier);
    }

}
