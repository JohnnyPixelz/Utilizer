package io.github.johnnypixelz.utilizer.itemstack.supplier;

import io.github.johnnypixelz.utilizer.itemstack.Items;
import io.github.johnnypixelz.utilizer.plugin.Provider;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;
import java.util.function.Supplier;

public class ItemSupplier {
    private final boolean stackable;
    private final Supplier<ItemStack> supplier;
    private final NamespacedKey namespacedKey;

    public ItemSupplier(String itemId, boolean stackable, Supplier<ItemStack> supplier) {
        this(new NamespacedKey(Provider.getPlugin(), itemId + "_supplier"), stackable, supplier);
    }

    public ItemSupplier(NamespacedKey namespacedKey, boolean stackable, Supplier<ItemStack> supplier) {
        this.stackable = stackable;
        this.supplier = supplier;
        this.namespacedKey = namespacedKey;
    }

    public ItemStack getItemStack() {
        return Items.edit(supplier.get())
                .meta(itemMeta -> {
                    final String value = stackable ? "" : UUID.randomUUID().toString();
                    itemMeta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, value);
                })
                .getItem();
    }

    public boolean isSameType(ItemStack stack) {
        if (Items.isNull(stack)) return false;
        if (!stack.hasItemMeta()) return false;

        final ItemMeta itemMeta = stack.getItemMeta();
        if (itemMeta == null) return false;

        return itemMeta.getPersistentDataContainer().has(namespacedKey, PersistentDataType.STRING);
    }

    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

}
