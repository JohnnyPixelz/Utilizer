package io.github.johnnypixelz.utilizer.features.customblocks;

import io.github.johnnypixelz.utilizer.serialize.world.BlockPosition;

/**
 * Base class for all custom blocks.
 * <p>
 * Extend this class to create custom block types. Override the lifecycle
 * methods to implement custom behavior:
 * <ul>
 *   <li>{@link #onTick()} - Called each tick (configurable interval)</li>
 *   <li>{@link #onRegister()} - Called when block is first registered</li>
 *   <li>{@link #onUnregister()} - Called when block is unregistered</li>
 *   <li>{@link #onLoad()} - Called when block is loaded from storage or registered</li>
 *   <li>{@link #onUnload()} - Called when block is unloaded or unregistered</li>
 * </ul>
 * <p>
 * For blocks with serializable data, implement {@link io.github.johnnypixelz.utilizer.features.customblocks.data.BlockDataHolder}.
 * <p>
 * Example:
 * <pre>{@code
 * public class MyBlock extends CustomBlock {
 *     public MyBlock(BlockPosition position) {
 *         super(position);
 *     }
 *
 *     @Override
 *     public void onTick() {
 *         // Called every tick
 *     }
 * }
 * }</pre>
 */
public abstract class CustomBlock {
    private final BlockPosition blockPosition;

    /**
     * Reference to the manager that owns this block.
     * Transient to prevent serialization.
     */
    private transient CustomBlockManager<?> manager;

    public CustomBlock(BlockPosition blockPosition) {
        this.blockPosition = blockPosition;
    }

    /**
     * Gets the position of this block in the world.
     *
     * @return the block position
     */
    public BlockPosition getBlockPosition() {
        return blockPosition;
    }

    /**
     * Called each tick while the block is loaded.
     * The tick interval is configurable via the builder.
     */
    public void onTick() {
    }

    /**
     * Called when the block is first registered (created/placed).
     * Not called when loaded from storage.
     */
    public void onRegister() {
    }

    /**
     * Called when the block is unregistered (removed/broken).
     * Not called when unloaded for server shutdown.
     */
    public void onUnregister() {
    }

    /**
     * Called when the block is loaded, either from storage or after registration.
     */
    public void onLoad() {
    }

    /**
     * Called when the block is unloaded, either for server shutdown or before unregistration.
     */
    public void onUnload() {
    }

    /**
     * Gets the manager that owns this block.
     * Can be used to unregister the block from within itself.
     *
     * @return the manager, or null if not yet registered
     */
    protected final CustomBlockManager<?> getManager() {
        return manager;
    }

    /**
     * Sets the manager reference. Called internally by the manager.
     *
     * @param manager the manager
     */
    final void setManager(CustomBlockManager<?> manager) {
        this.manager = manager;
    }

}
