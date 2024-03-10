package io.github.johnnypixelz.utilizer.itemstack;

import io.github.johnnypixelz.utilizer.gson.GsonProvider;
import io.github.johnnypixelz.utilizer.plugin.Provider;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class CustomItemSupplier<T> {
    private final Class<T> type;
    private final boolean stackable;
    private final NamespacedKey namespacedKey;
    private final Function<T, ItemStack> supplier;

    public CustomItemSupplier(String itemId, boolean stackable, Function<T, ItemStack> supplier, Class<T> type) {
        this(new NamespacedKey(Provider.getPlugin(), itemId + "_supplier"), stackable, supplier, type);
    }

    public CustomItemSupplier(NamespacedKey namespacedKey, boolean stackable, Function<T, ItemStack> supplier, Class<T> type) {
        this.stackable = stackable;
        this.supplier = supplier;
        this.namespacedKey = namespacedKey;
        this.type = type;
    }

    public ItemStack getItemStack(T data) {
        return Items.edit(supplier.apply(data))
                .meta(itemMeta -> {
                    if (!stackable) {
                        itemMeta.getPersistentDataContainer().set(Provider.getNamespacedKey(namespacedKey.getKey() + "_stackable"), PersistentDataType.STRING, UUID.randomUUID().toString());
                    }

                    final String json = GsonProvider.standard().toJson(data, type);
                    itemMeta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, json);
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

    public Optional<T> getCustomData(ItemStack stack) {
        if (Items.isNull(stack)) return Optional.empty();
        if (!stack.hasItemMeta()) return Optional.empty();

        final ItemMeta itemMeta = stack.getItemMeta();
        if (itemMeta == null) return Optional.empty();

        final String json = itemMeta.getPersistentDataContainer().get(namespacedKey, PersistentDataType.STRING);
        if (json == null) return Optional.empty();

        final T customData = GsonProvider.standard().fromJson(json, type);
        return Optional.of(customData);
    }

}
