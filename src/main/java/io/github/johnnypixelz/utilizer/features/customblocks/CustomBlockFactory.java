package io.github.johnnypixelz.utilizer.features.customblocks;

import io.github.johnnypixelz.utilizer.serialize.world.BlockPosition;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.Nullable;

/**
 * Factory for creating CustomBlock instances when a block is placed.
 * <p>
 * This functional interface can be implemented as a lambda for concise block creation:
 * <pre>{@code
 * .factory(ctx -> new MyBlock(ctx.position()))
 * }</pre>
 * <p>
 * For blocks with data:
 * <pre>{@code
 * .factory(ctx -> {
 *     MyData data = ctx.getDataAs(MyData.class);
 *     return new MyDataBlock(ctx.position(), data != null ? data : new MyData());
 * })
 * }</pre>
 *
 * @param <CB> the custom block type to create
 */
@FunctionalInterface
public interface CustomBlockFactory<CB extends CustomBlock> {

    /**
     * Creates a new CustomBlock instance.
     *
     * @param context the placement context containing all relevant data
     * @return the created CustomBlock, or null to cancel placement
     */
    @Nullable
    CB create(PlacementContext context);

    /**
     * Context object containing all placement data.
     * Using a record allows the interface to be extended with new data
     * without changing the method signature.
     */
    record PlacementContext(
            BlockPlaceEvent event,
            BlockPosition position,
            ItemStack itemStack,
            @Nullable Object deserializedData
    ) {
        /**
         * Convenience method to get typed data from the item.
         *
         * @param type the expected data type class
         * @param <T>  the data type
         * @return the typed data, or null if data is null or wrong type
         */
        @SuppressWarnings("unchecked")
        @Nullable
        public <T> T getDataAs(Class<T> type) {
            if (deserializedData == null) return null;
            if (!type.isInstance(deserializedData)) {
                return null;
            }
            return (T) deserializedData;
        }

        /**
         * Checks if this context has data available.
         *
         * @return true if deserialized data is present
         */
        public boolean hasData() {
            return deserializedData != null;
        }
    }

}
