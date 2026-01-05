package io.github.johnnypixelz.utilizer.features.customblocks;
import io.github.johnnypixelz.utilizer.plugin.Provider;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Fluent builder for creating CustomBlockManagers.
 * <p>
 * This single builder replaces the previous six builder classes and provides
 * a unified, intuitive API for configuring custom blocks.
 * <p>
 * Usage examples:
 * <pre>{@code
 * // Basic block (no item)
 * CustomBlockManager<BasicBlock> manager = CustomBlocks.create(BasicBlock.class)
 *     .storage("basicblocks.json")
 *     .build()
 *     .init();
 *
 * // Block with item
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
 *
 * // Full configuration
 * CustomBlockManager<MyBlock> manager = CustomBlocks.create(MyBlock.class)
 *     .storage("myblocks.json")
 *     .item("my_block", block -> new ItemStack(Material.GOLD_BLOCK))
 *     .data(MyBlockData.class)
 *     .factory(ctx -> new MyBlock(ctx.position(), ctx.getDataAs(MyBlockData.class)))
 *     .breakable(true)
 *     .interactable(true)
 *     .movable(false)
 *     .explodable(false)
 *     .placeable(true)
 *     .droppable(true)
 *     .stackable(false)
 *     .tickInterval(20L)
 *     .autoSave(6000L)
 *     .build()
 *     .init();
 * }</pre>
 *
 * @param <CB> the custom block type
 */
public class CustomBlockBuilder<CB extends CustomBlock> {

    private final Class<CB> blockType;
    private String fileName;
    private final CustomBlockSettings.Builder<CB> settingsBuilder;

    /**
     * Creates a new builder for the specified block type.
     *
     * @param blockType the custom block class
     */
    public CustomBlockBuilder(Class<CB> blockType) {
        this.blockType = Objects.requireNonNull(blockType, "blockType");
        this.settingsBuilder = new CustomBlockSettings.Builder<>();
        this.settingsBuilder.blockType = blockType;
    }

    // Required: Storage configuration

    /**
     * Sets the storage file name for persisting blocks.
     * This is required before calling {@link #build()}.
     *
     * @param fileName the JSON file name (e.g., "myblocks.json")
     * @return this builder for chaining
     */
    public CustomBlockBuilder<CB> storage(String fileName) {
        this.fileName = Objects.requireNonNull(fileName, "fileName");
        return this;
    }

    // Optional: Item configuration

    /**
     * Configures item representation for this block type.
     * When configured, blocks can be placed by players using items
     * and will drop items when broken.
     *
     * @param key      the unique identifier for this item type
     * @param supplier function that creates an ItemStack for a block
     * @return this builder for chaining
     */
    public CustomBlockBuilder<CB> item(String key, Function<CB, ItemStack> supplier) {
        return item(Provider.getNamespacedKey(key), supplier);
    }

    /**
     * Configures item representation for this block type.
     *
     * @param key      the namespaced key for this item type
     * @param supplier function that creates an ItemStack for a block
     * @return this builder for chaining
     */
    public CustomBlockBuilder<CB> item(NamespacedKey key, Function<CB, ItemStack> supplier) {
        settingsBuilder.itemKey = Objects.requireNonNull(key, "key");
        settingsBuilder.itemSupplier = Objects.requireNonNull(supplier, "supplier");
        return this;
    }

    /**
     * Sets whether items of this type can stack.
     * Default is true.
     *
     * @param stackable true to allow stacking
     * @return this builder for chaining
     */
    public CustomBlockBuilder<CB> stackable(boolean stackable) {
        settingsBuilder.stackable = stackable;
        return this;
    }

    // Optional: Data configuration

    /**
     * Configures data serialization for blocks that implement {@link io.github.johnnypixelz.utilizer.features.customblocks.data.BlockDataHolder}.
     * When configured, block data will be serialized to/from items.
     *
     * @param dataType the data class type
     * @param <T>      the data type
     * @return this builder for chaining
     */
    public <T> CustomBlockBuilder<CB> data(Class<T> dataType) {
        settingsBuilder.dataType = Objects.requireNonNull(dataType, "dataType");
        return this;
    }

    // Optional: Factory configuration

    /**
     * Sets a custom factory for creating blocks on placement.
     * If not set and item representation is configured, a reflection-based
     * factory will be used that looks for constructors with BlockPosition
     * and/or data type parameters.
     *
     * @param factory the factory for creating blocks
     * @return this builder for chaining
     */
    public CustomBlockBuilder<CB> factory(CustomBlockFactory<CB> factory) {
        settingsBuilder.factory = Objects.requireNonNull(factory, "factory");
        return this;
    }

    /**
     * Sets a simple factory that doesn't depend on placement context.
     *
     * @param simpleFactory supplier that creates new blocks
     * @return this builder for chaining
     */
    public CustomBlockBuilder<CB> factory(Supplier<CB> simpleFactory) {
        Objects.requireNonNull(simpleFactory, "simpleFactory");
        return factory(ctx -> simpleFactory.get());
    }

    // Optional: Behavior configuration

    /**
     * Sets whether the block can be broken by players.
     * Default is true.
     *
     * @param breakable true to allow breaking
     * @return this builder for chaining
     */
    public CustomBlockBuilder<CB> breakable(boolean breakable) {
        settingsBuilder.breakable = breakable;
        return this;
    }

    /**
     * Sets whether the block can be interacted with.
     * Default is true.
     *
     * @param interactable true to allow interaction
     * @return this builder for chaining
     */
    public CustomBlockBuilder<CB> interactable(boolean interactable) {
        settingsBuilder.interactable = interactable;
        return this;
    }

    /**
     * Sets whether the block can be moved by pistons.
     * Default is false.
     *
     * @param movable true to allow piston movement
     * @return this builder for chaining
     */
    public CustomBlockBuilder<CB> movable(boolean movable) {
        settingsBuilder.movable = movable;
        return this;
    }

    /**
     * Sets whether the block can be destroyed by explosions.
     * Default is false.
     *
     * @param explodable true to allow explosion destruction
     * @return this builder for chaining
     */
    public CustomBlockBuilder<CB> explodable(boolean explodable) {
        settingsBuilder.explodable = explodable;
        return this;
    }

    /**
     * Sets whether the block can be placed by players.
     * Only relevant when item representation is configured.
     * Default is true.
     *
     * @param placeable true to allow placement
     * @return this builder for chaining
     */
    public CustomBlockBuilder<CB> placeable(boolean placeable) {
        settingsBuilder.placeable = placeable;
        return this;
    }

    /**
     * Sets whether the block drops an item when broken.
     * Only relevant when item representation is configured.
     * Default is true.
     *
     * @param droppable true to drop items on break
     * @return this builder for chaining
     */
    public CustomBlockBuilder<CB> droppable(boolean droppable) {
        settingsBuilder.droppable = droppable;
        return this;
    }

    // Optional: Tick configuration

    /**
     * Sets whether the block's onTick() method is called.
     * Default is true.
     *
     * @param enabled true to enable ticking
     * @return this builder for chaining
     */
    public CustomBlockBuilder<CB> tick(boolean enabled) {
        settingsBuilder.tickEnabled = enabled;
        return this;
    }

    /**
     * Sets the tick interval in server ticks.
     * Default is 1 (every tick).
     *
     * @param ticks the interval in ticks
     * @return this builder for chaining
     */
    public CustomBlockBuilder<CB> tickInterval(long ticks) {
        if (ticks < 1) {
            throw new IllegalArgumentException("Tick interval must be at least 1");
        }
        settingsBuilder.tickInterval = ticks;
        return this;
    }

    // Optional: Auto-save configuration

    /**
     * Sets the auto-save interval in server ticks.
     * Set to 0 to disable auto-save (default).
     *
     * @param ticks the interval in ticks, or 0 to disable
     * @return this builder for chaining
     */
    public CustomBlockBuilder<CB> autoSave(long ticks) {
        if (ticks < 0) {
            throw new IllegalArgumentException("Auto-save interval cannot be negative");
        }
        settingsBuilder.autoSaveTicks = ticks;
        return this;
    }

    // Build

    /**
     * Builds the CustomBlockManager with the configured settings.
     * Call {@link CustomBlockManager#init()} on the returned manager
     * to activate it.
     *
     * @return the configured manager (not yet initialized)
     * @throws IllegalStateException if required settings are missing
     */
    public CustomBlockManager<CB> build() {
        // Validation
        if (fileName == null) {
            throw new IllegalStateException("Storage file name is required. Call .storage(fileName)");
        }

        CustomBlockSettings<CB> settings = settingsBuilder.build();
        return new CustomBlockManager<>(blockType, fileName, settings);
    }

}
