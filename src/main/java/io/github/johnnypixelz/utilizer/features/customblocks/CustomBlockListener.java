package io.github.johnnypixelz.utilizer.features.customblocks;

import io.github.johnnypixelz.utilizer.event.BiStatefulEventEmitter;
import io.github.johnnypixelz.utilizer.event.StatefulEventEmitter;
import io.github.johnnypixelz.utilizer.features.customblocks.data.BlockDataHolder;
import io.github.johnnypixelz.utilizer.features.customblocks.item.BlockItemHandler;
import io.github.johnnypixelz.utilizer.plugin.Logs;
import io.github.johnnypixelz.utilizer.serialize.world.BlockPosition;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;

/**
 * Unified event listener for all custom block interactions.
 * <p>
 * Handles:
 * <ul>
 *   <li>Block breaking (with optional item drops)</li>
 *   <li>Block placement (item detection, factory invocation)</li>
 *   <li>Player interaction (left/right click)</li>
 *   <li>Piston protection (extend/retract)</li>
 *   <li>Explosion protection</li>
 * </ul>
 *
 * @param <CB> the custom block type
 */
public class CustomBlockListener<CB extends CustomBlock> implements Listener {

    private final CustomBlockManager<CB> manager;
    private final CustomBlockSettings<CB> settings;

    // Event emitters
    private final BiStatefulEventEmitter<BlockBreakEvent, CB> breakEmitter;
    private final StatefulEventEmitter<BlockPlaceEvent> prePlaceEmitter;
    private final BiStatefulEventEmitter<BlockPlaceEvent, CB> postPlaceEmitter;
    private final BiStatefulEventEmitter<PlayerInteractEvent, CB> leftClickEmitter;
    private final BiStatefulEventEmitter<PlayerInteractEvent, CB> rightClickEmitter;

    // Custom factory (uses reflection-based default if not set)
    private CustomBlockFactory<CB> customFactory;

    public CustomBlockListener(CustomBlockManager<CB> manager) {
        this.manager = manager;
        this.settings = manager.getSettings();

        this.breakEmitter = new BiStatefulEventEmitter<>();
        this.prePlaceEmitter = new StatefulEventEmitter<>();
        this.postPlaceEmitter = new BiStatefulEventEmitter<>();
        this.leftClickEmitter = new BiStatefulEventEmitter<>();
        this.rightClickEmitter = new BiStatefulEventEmitter<>();

        // Initialize with settings factory or create default reflection-based factory
        this.customFactory = settings.getFactory();
        if (this.customFactory == null && settings.hasItemRepresentation()) {
            this.customFactory = createReflectionBasedFactory();
        }
    }

    // Event emitter accessors

    /**
     * Gets the event emitter for block break events.
     *
     * @return the break event emitter
     */
    public BiStatefulEventEmitter<BlockBreakEvent, CB> onBreak() {
        return breakEmitter;
    }

    /**
     * Gets the event emitter for pre-place events (before block is created).
     *
     * @return the pre-place event emitter
     */
    public StatefulEventEmitter<BlockPlaceEvent> onPrePlace() {
        return prePlaceEmitter;
    }

    /**
     * Gets the event emitter for post-place events (after block is created).
     *
     * @return the post-place event emitter
     */
    public BiStatefulEventEmitter<BlockPlaceEvent, CB> onPostPlace() {
        return postPlaceEmitter;
    }

    /**
     * Gets the event emitter for left-click interaction events.
     *
     * @return the left-click event emitter
     */
    public BiStatefulEventEmitter<PlayerInteractEvent, CB> onLeftClick() {
        return leftClickEmitter;
    }

    /**
     * Gets the event emitter for right-click interaction events.
     *
     * @return the right-click event emitter
     */
    public BiStatefulEventEmitter<PlayerInteractEvent, CB> onRightClick() {
        return rightClickEmitter;
    }

    /**
     * Sets a custom factory for creating blocks on placement.
     * Overrides the default reflection-based factory.
     *
     * @param factory the custom factory
     */
    public void setCustomBlockFactory(CustomBlockFactory<CB> factory) {
        this.customFactory = factory;
    }

    /**
     * Gets the current block factory.
     *
     * @return the factory, or null if none configured
     */
    public CustomBlockFactory<CB> getCustomBlockFactory() {
        return customFactory;
    }

    // Event handlers

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        BlockPosition position = BlockPosition.of(block);

        Optional<CB> optional = manager.getCustomBlock(position);
        if (optional.isEmpty()) return;

        CB customBlock = optional.get();

        // Check if breaking is allowed
        if (!settings.isBreakable()) {
            event.setCancelled(true);
            return;
        }

        // Unregister the block
        manager.unregisterCustomBlock(customBlock);

        // Emit break event
        breakEmitter.emit(event, customBlock);

        // Handle item drops
        BlockItemHandler<CB> itemHandler = manager.getItemHandler();
        if (!event.isCancelled() && settings.isDroppable() && event.isDropItems() && itemHandler != null) {
            event.setDropItems(false);
            ItemStack drop = itemHandler.createItem(customBlock);
            block.getWorld().dropItemNaturally(block.getLocation(), drop);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onBlockPlace(BlockPlaceEvent event) {
        // Only handle if we have item representation
        BlockItemHandler<CB> itemHandler = manager.getItemHandler();
        if (itemHandler == null) return;

        ItemStack item = event.getItemInHand();
        if (!itemHandler.isCustomBlockItem(item)) return;

        // Check if placing is allowed
        if (!settings.isPlaceable()) {
            event.setCancelled(true);
            return;
        }

        // Emit pre-place event
        prePlaceEmitter.emit(event);
        if (event.isCancelled()) return;

        // Create the block via factory
        if (customFactory == null) return;

        BlockPosition position = BlockPosition.of(event.getBlockPlaced());
        Object deserializedData = itemHandler.extractData(item).orElse(null);

        CustomBlockFactory.PlacementContext context = new CustomBlockFactory.PlacementContext(
                event, position, item, deserializedData
        );

        CB createdBlock = customFactory.create(context);
        if (createdBlock != null) {
            manager.registerCustomBlock(createdBlock);
            postPlaceEmitter.emit(event, createdBlock);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK && action != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null) return;

        Optional<CB> optional = manager.getCustomBlock(BlockPosition.of(block));
        if (optional.isEmpty()) return;

        CB customBlock = optional.get();

        if (!settings.isInteractable()) {
            event.setCancelled(true);
        }

        if (action == Action.LEFT_CLICK_BLOCK) {
            leftClickEmitter.emit(event, customBlock);
        } else {
            rightClickEmitter.emit(event, customBlock);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onPistonExtend(BlockPistonExtendEvent event) {
        if (settings.isMovable()) return;

        for (Block block : event.getBlocks()) {
            if (manager.getCustomBlock(BlockPosition.of(block)).isPresent()) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onPistonRetract(BlockPistonRetractEvent event) {
        if (settings.isMovable()) return;

        for (Block block : event.getBlocks()) {
            if (manager.getCustomBlock(BlockPosition.of(block)).isPresent()) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onExplode(EntityExplodeEvent event) {
        if (settings.isExplodable()) return;

        Iterator<Block> iterator = event.blockList().iterator();
        while (iterator.hasNext()) {
            Block block = iterator.next();
            if (manager.getCustomBlock(BlockPosition.of(block)).isPresent()) {
                iterator.remove();
            }
        }
    }

    // Reflection-based factory

    /**
     * Creates a default factory that uses reflection to find and invoke
     * an appropriate constructor on the custom block class.
     * <p>
     * Looks for constructors with BlockPosition and/or data type parameters.
     */
    private CustomBlockFactory<CB> createReflectionBasedFactory() {
        return context -> {
            Class<CB> blockType = settings.getBlockType();
            Class<?> dataType = settings.getDataType();

            try {
                // Find eligible constructor
                Optional<Constructor<CB>> optionalConstructor = findEligibleConstructor(blockType, dataType);

                if (optionalConstructor.isEmpty()) {
                    Logs.severe("No eligible constructor found for " + blockType.getName() +
                            ". Expected constructor with (BlockPosition) or (BlockPosition, " +
                            (dataType != null ? dataType.getSimpleName() : "DataType") + ") parameters.");
                    return null;
                }

                Constructor<CB> constructor = optionalConstructor.get();
                Object[] args = buildConstructorArgs(constructor, context, dataType);

                return constructor.newInstance(args);

            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                Logs.severe("Failed to instantiate " + blockType.getName() + ": " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        };
    }

    @SuppressWarnings("unchecked")
    private Optional<Constructor<CB>> findEligibleConstructor(Class<CB> blockType, Class<?> dataType) {
        return Arrays.stream((Constructor<CB>[]) blockType.getConstructors())
                .filter(constructor -> {
                    // Filter to constructors with only BlockPosition and optionally dataType params
                    return Arrays.stream(constructor.getParameterTypes())
                            .allMatch(paramType ->
                                    paramType == BlockPosition.class ||
                                            (dataType != null && paramType == dataType));
                })
                .filter(constructor -> {
                    // Ensure at most one of each parameter type
                    long blockPosCount = Arrays.stream(constructor.getParameterTypes())
                            .filter(p -> p == BlockPosition.class)
                            .count();
                    long dataCount = dataType == null ? 0 :
                            Arrays.stream(constructor.getParameterTypes())
                                    .filter(p -> p == dataType)
                                    .count();
                    return blockPosCount <= 1 && dataCount <= 1;
                })
                .findFirst();
    }

    private Object[] buildConstructorArgs(Constructor<CB> constructor,
                                          CustomBlockFactory.PlacementContext context,
                                          Class<?> dataType) {
        return Arrays.stream(constructor.getParameterTypes())
                .map(paramType -> {
                    if (paramType == BlockPosition.class) {
                        return context.position();
                    } else if (dataType != null && paramType == dataType) {
                        return context.deserializedData();
                    }
                    throw new IllegalStateException("Unexpected parameter type: " + paramType);
                })
                .toArray();
    }

}
