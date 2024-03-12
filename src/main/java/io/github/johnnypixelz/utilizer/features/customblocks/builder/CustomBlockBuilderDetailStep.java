package io.github.johnnypixelz.utilizer.features.customblocks.builder;

import io.github.johnnypixelz.utilizer.features.customblocks.CustomBlock;
import io.github.johnnypixelz.utilizer.features.customblocks.customblock.CustomBlockManager;
import io.github.johnnypixelz.utilizer.plugin.Provider;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public class CustomBlockBuilderDetailStep<CB extends CustomBlock> {
    private final Class<CB> customBlockType;
    private final String fileName;

    public CustomBlockBuilderDetailStep(Class<CB> customBlockType, String fileName) {
        this.customBlockType = customBlockType;
        this.fileName = fileName;
    }

    public CustomBlockWithItemBuilderDetailStep<CB> itemed(Function<CB, ItemStack> itemSupplier) {
        return itemed(fileName, itemSupplier);
    }

    public CustomBlockWithItemBuilderDetailStep<CB> itemed(String key, Function<CB, ItemStack> itemSupplier) {
        return itemed(Provider.getNamespacedKey(key), itemSupplier);
    }

    public CustomBlockWithItemBuilderDetailStep<CB> itemed(NamespacedKey key, Function<CB, ItemStack> itemSupplier) {
        return new CustomBlockWithItemBuilderDetailStep<>(customBlockType, fileName, key, itemSupplier);
    }

    public CustomBlockManager<CB> build() {
        return new CustomBlockManager<>(customBlockType, fileName);
    }

}
