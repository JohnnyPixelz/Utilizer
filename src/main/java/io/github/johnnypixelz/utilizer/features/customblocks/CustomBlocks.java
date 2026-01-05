package io.github.johnnypixelz.utilizer.features.customblocks;

/**
 * Entry point for creating custom block managers.
 * <p>
 * Use the fluent builder API to configure and create custom block managers:
 * <pre>{@code
 * // Basic block (no item representation)
 * CustomBlockManager<BasicBlock> manager = CustomBlocks.create(BasicBlock.class)
 *     .storage("basicblocks.json")
 *     .build()
 *     .init();
 *
 * // Block with item (can be placed/broken as items)
 * CustomBlockManager<ItemBlock> manager = CustomBlocks.create(ItemBlock.class)
 *     .storage("itemblocks.json")
 *     .item("item_block", block -> new ItemStack(Material.DIAMOND_BLOCK))
 *     .build()
 *     .init();
 *
 * // Block with data (implements BlockDataHolder)
 * CustomBlockManager<DataBlock> manager = CustomBlocks.create(DataBlock.class)
 *     .storage("datablocks.json")
 *     .item("data_block", block -> new ItemStack(Material.EMERALD_BLOCK))
 *     .data(MyBlockData.class)
 *     .build()
 *     .init();
 * }</pre>
 *
 * @see CustomBlockBuilder
 * @see CustomBlockManager
 */
public final class CustomBlocks {

    private CustomBlocks() {
        // Utility class
    }

    /**
     * Creates a builder for the specified custom block type.
     *
     * @param blockType the custom block class
     * @param <CB>      the custom block type
     * @return a new builder for configuring the manager
     */
    public static <CB extends CustomBlock> CustomBlockBuilder<CB> create(Class<CB> blockType) {
        return new CustomBlockBuilder<>(blockType);
    }

}
