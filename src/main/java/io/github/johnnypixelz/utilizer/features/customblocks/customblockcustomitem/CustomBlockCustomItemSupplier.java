package io.github.johnnypixelz.utilizer.features.customblocks.customblockcustomitem;

import io.github.johnnypixelz.utilizer.features.customblocks.CustomBlock;
import io.github.johnnypixelz.utilizer.itemstack.CustomItemSupplier;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public class CustomBlockCustomItemSupplier<CB extends StatefulCustomBlock<CBD>, CBD extends CustomBlockData> extends CustomItemSupplier<CBD> {

    public CustomBlockCustomItemSupplier(String itemId, boolean stackable, Function<CBD, ItemStack> dataToStack, Class<CBD> type) {
        super(itemId, stackable, dataToStack, type);
    }

    public CustomBlockCustomItemSupplier(NamespacedKey namespacedKey, boolean stackable, Function<CBD, ItemStack> supplier, Class<CBD> type) {
        super(namespacedKey, stackable, supplier, type);
    }

}
