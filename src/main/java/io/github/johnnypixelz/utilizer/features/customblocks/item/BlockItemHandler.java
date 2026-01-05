package io.github.johnnypixelz.utilizer.features.customblocks.item;

import io.github.johnnypixelz.utilizer.features.customblocks.CustomBlock;
import io.github.johnnypixelz.utilizer.features.customblocks.CustomBlockSettings;
import io.github.johnnypixelz.utilizer.features.customblocks.data.BlockDataHolder;
import io.github.johnnypixelz.utilizer.gson.GsonProvider;
import io.github.johnnypixelz.utilizer.itemstack.Items;
import io.github.johnnypixelz.utilizer.plugin.Provider;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;
import java.util.UUID;

/**
 * Handles conversion between CustomBlocks and ItemStacks.
 * <p>
 * Responsible for:
 * <ul>
 *   <li>Creating ItemStacks from blocks</li>
 *   <li>Identifying items as custom block items via PDC</li>
 *   <li>Serializing/deserializing BlockDataHolder data to/from items</li>
 *   <li>Handling stackability via UUID in PDC</li>
 * </ul>
 *
 * @param <CB> the custom block type
 */
public class BlockItemHandler<CB extends CustomBlock> {

    private final CustomBlockSettings<CB> settings;
    private final NamespacedKey typeKey;       // Marks item as this block type
    private final NamespacedKey dataKey;       // Stores serialized data
    private final NamespacedKey unstackKey;    // Makes non-stackable items unique

    public BlockItemHandler(CustomBlockSettings<CB> settings) {
        this.settings = settings;
        this.typeKey = settings.getItemKey();
        this.dataKey = Provider.getNamespacedKey(typeKey.getKey() + "_data");
        this.unstackKey = Provider.getNamespacedKey(typeKey.getKey() + "_unstack");
    }

    /**
     * Creates an ItemStack representing the given custom block.
     *
     * @param block the custom block to create an item for
     * @return the created ItemStack
     */
    public ItemStack createItem(CB block) {
        ItemStack base = settings.getItemSupplier().apply(block);

        return Items.edit(base)
                .meta(meta -> {
                    PersistentDataContainer pdc = meta.getPersistentDataContainer();

                    // Mark as this block type
                    pdc.set(typeKey, PersistentDataType.BYTE, (byte) 1);

                    // Handle non-stackable items
                    if (!settings.isStackable()) {
                        pdc.set(unstackKey, PersistentDataType.STRING, UUID.randomUUID().toString());
                    }

                    // Serialize data if block implements BlockDataHolder
                    if (block instanceof BlockDataHolder<?> holder) {
                        Object data = holder.getData();
                        if (data != null) {
                            String json = GsonProvider.standard().toJson(data, holder.getDataType());
                            pdc.set(dataKey, PersistentDataType.STRING, json);
                        }
                    }
                })
                .getItem();
    }

    /**
     * Creates an ItemStack from raw data (without a block instance).
     * Used when the data type is known but no block exists yet.
     *
     * @param data the data to serialize into the item
     * @param <T>  the data type
     * @return the created ItemStack
     */
    public <T> ItemStack createItemFromData(T data) {
        if (!settings.hasData()) {
            throw new IllegalStateException("Cannot create item from data: no data type configured");
        }

        // We need a block to get the base item, so create a temporary representation
        // This method is typically used with a supplier that doesn't depend on block state
        throw new UnsupportedOperationException("createItemFromData requires a block-independent item supplier");
    }

    /**
     * Checks if an ItemStack represents this custom block type.
     *
     * @param item the item to check
     * @return true if the item is a custom block item of this type
     */
    public boolean isCustomBlockItem(ItemStack item) {
        if (Items.isNull(item) || !item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        return meta.getPersistentDataContainer().has(typeKey, PersistentDataType.BYTE);
    }

    /**
     * Extracts serialized data from an ItemStack.
     *
     * @param item the item to extract data from
     * @return optional containing the deserialized data, or empty if not present
     */
    public Optional<Object> extractData(ItemStack item) {
        if (!settings.hasData()) return Optional.empty();
        if (!isCustomBlockItem(item)) return Optional.empty();

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return Optional.empty();

        String json = meta.getPersistentDataContainer().get(dataKey, PersistentDataType.STRING);
        if (json == null) return Optional.empty();

        Object data = GsonProvider.standard().fromJson(json, settings.getDataType());
        return Optional.ofNullable(data);
    }

    /**
     * Extracts typed data from an ItemStack.
     *
     * @param item the item to extract data from
     * @param type the expected data type class
     * @param <T>  the data type
     * @return optional containing the typed data, or empty if not present or wrong type
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> extractData(ItemStack item, Class<T> type) {
        return extractData(item)
                .filter(type::isInstance)
                .map(data -> (T) data);
    }

    /**
     * Gets the namespaced key used to identify items of this block type.
     *
     * @return the type key
     */
    public NamespacedKey getTypeKey() {
        return typeKey;
    }

}
