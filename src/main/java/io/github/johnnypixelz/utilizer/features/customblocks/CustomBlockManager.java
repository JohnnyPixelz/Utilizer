package io.github.johnnypixelz.utilizer.features.customblocks;

import io.github.johnnypixelz.utilizer.features.customblocks.item.BlockItemHandler;
import io.github.johnnypixelz.utilizer.file.storage.Storage;
import io.github.johnnypixelz.utilizer.file.storage.container.file.FileStorageContainer;
import io.github.johnnypixelz.utilizer.gson.GsonProvider;
import io.github.johnnypixelz.utilizer.plugin.Provider;
import io.github.johnnypixelz.utilizer.serialize.world.BlockPosition;
import io.github.johnnypixelz.utilizer.tasks.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * Unified manager for all custom block types.
 * <p>
 * Handles storage, lifecycle, ticking, and event registration for custom blocks.
 * This single manager replaces the previous three managers (CustomBlockManager,
 * CustomBlockItemManager, CustomBlockCustomItemManager).
 * <p>
 * Example usage:
 * <pre>{@code
 * CustomBlockManager<MyBlock> manager = CustomBlocks.create(MyBlock.class)
 *     .storage("myblocks.json")
 *     .item("my_block", block -> new ItemStack(Material.DIAMOND_BLOCK))
 *     .build()
 *     .init();
 * }</pre>
 *
 * @param <CB> the custom block type
 */
public class CustomBlockManager<CB extends CustomBlock> {

    private final Class<CB> blockType;
    private final CustomBlockSettings<CB> settings;
    private final FileStorageContainer<Map<BlockPosition, CB>> storage;
    private final CustomBlockListener<CB> listener;
    private final BlockItemHandler<CB> itemHandler;

    private BukkitTask tickTask;
    private BukkitTask autoSaveTask;
    private boolean initialized = false;

    /**
     * Creates a new CustomBlockManager.
     * Use {@link io.github.johnnypixelz.utilizer.features.customblocks.CustomBlocks#create(Class)}
     * and the builder pattern instead of calling this constructor directly.
     *
     * @param blockType the custom block class
     * @param fileName  the storage file name
     * @param settings  the block settings
     */
    public CustomBlockManager(Class<CB> blockType, String fileName, CustomBlockSettings<CB> settings) {
        this.blockType = blockType;
        this.settings = settings;

        this.storage = Storage.map(BlockPosition.class, blockType)
                .json(fileName, GsonProvider.builder().enableComplexMapKeySerialization().create())
                .container(HashMap::new);

        this.itemHandler = settings.hasItemRepresentation()
                ? new BlockItemHandler<>(settings)
                : null;

        this.listener = new CustomBlockListener<>(this);
    }

    /**
     * Initializes the manager: loads blocks, starts ticking, registers events.
     * Must be called after construction to activate the manager.
     *
     * @return this manager for chaining
     * @throws IllegalStateException if already initialized
     */
    public CustomBlockManager<CB> init() {
        if (initialized) {
            throw new IllegalStateException("Manager already initialized");
        }
        initialized = true;

        // Set manager reference on loaded blocks and call onLoad
        storage.get().values().forEach(block -> {
            block.setManager(this);
            block.onLoad();
        });

        // Start tick task if enabled
        if (settings.isTickEnabled()) {
            tickTask = Tasks.sync().timer(task -> {
                storage.get().values().forEach(CustomBlock::onTick);
            }, settings.getTickInterval());
        }

        // Start auto-save if configured
        if (settings.getAutoSaveTicks() > 0) {
            autoSaveTask = Tasks.sync().delayedTimer(
                    task -> save(),
                    settings.getAutoSaveTicks(),
                    settings.getAutoSaveTicks()
            );
        }

        // Register events
        Bukkit.getPluginManager().registerEvents(listener, Provider.getPlugin());

        return this;
    }

    /**
     * Unloads the manager: stops tasks, unloads blocks, saves data.
     * Should be called on plugin disable.
     */
    public void unload() {
        if (tickTask != null && !tickTask.isCancelled()) {
            tickTask.cancel();
            tickTask = null;
        }

        if (autoSaveTask != null && !autoSaveTask.isCancelled()) {
            autoSaveTask.cancel();
            autoSaveTask = null;
        }

        storage.get().values().forEach(CustomBlock::onUnload);
        save();
    }

    /**
     * Saves all blocks to storage.
     */
    public void save() {
        storage.save();
    }

    // Block operations

    /**
     * Gets all registered custom blocks.
     *
     * @return unmodifiable collection of all blocks
     */
    public Collection<CB> getCustomBlocks() {
        return Collections.unmodifiableCollection(storage.get().values());
    }

    /**
     * Gets a custom block at the specified position.
     *
     * @param position the block position
     * @return optional containing the block, or empty if none exists
     */
    public Optional<CB> getCustomBlock(@Nonnull BlockPosition position) {
        Objects.requireNonNull(position, "position");
        return Optional.ofNullable(storage.get().get(position));
    }

    /**
     * Registers a custom block.
     * Calls onRegister() and onLoad() on the block.
     *
     * @param block the block to register
     * @return the registered block
     * @throws IllegalStateException if a block already exists at the position
     */
    public CB registerCustomBlock(CB block) {
        Objects.requireNonNull(block, "block");
        BlockPosition position = block.getBlockPosition();

        if (storage.get().containsKey(position)) {
            throw new IllegalStateException("Block already exists at " + position);
        }

        block.setManager(this);
        storage.get().put(position, block);
        block.onRegister();
        block.onLoad();

        return block;
    }

    /**
     * Unregisters a custom block.
     * Calls onUnload() and onUnregister() on the block.
     *
     * @param block the block to unregister
     * @throws IllegalStateException if the block is not registered
     */
    public void unregisterCustomBlock(CB block) {
        Objects.requireNonNull(block, "block");

        if (!storage.get().containsValue(block)) {
            throw new IllegalStateException("Block not registered: " + block.getBlockPosition());
        }

        block.onUnload();
        block.onUnregister();
        storage.get().remove(block.getBlockPosition());
    }

    // Accessors

    /**
     * Gets the custom block class type.
     *
     * @return the block type class
     */
    public Class<CB> getBlockType() {
        return blockType;
    }

    /**
     * Gets the settings for this manager.
     *
     * @return the settings
     */
    public CustomBlockSettings<CB> getSettings() {
        return settings;
    }

    /**
     * Gets the event listener for this manager.
     * Use this to subscribe to block events.
     *
     * @return the listener
     */
    public CustomBlockListener<CB> getListener() {
        return listener;
    }

    /**
     * Gets the item handler for this manager.
     *
     * @return the item handler, or null if no item representation is configured
     */
    public BlockItemHandler<CB> getItemHandler() {
        return itemHandler;
    }

    // Item operations

    /**
     * Creates an ItemStack representing the given block.
     * Only available if item representation is configured.
     *
     * @param block the block to create an item for
     * @return optional containing the item, or empty if no item representation
     */
    public Optional<ItemStack> createItem(CB block) {
        if (itemHandler == null) {
            return Optional.empty();
        }
        return Optional.of(itemHandler.createItem(block));
    }

}
