package io.github.johnnypixelz.utilizer.features.customblocks;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * Immutable configuration for custom block behavior.
 * Created via {@link io.github.johnnypixelz.utilizer.features.customblocks.builder.CustomBlockBuilder}.
 *
 * @param <CB> the custom block type
 */
public final class CustomBlockSettings<CB extends CustomBlock> {

    // Behavior flags
    private final boolean breakable;
    private final boolean interactable;
    private final boolean movable;
    private final boolean explodable;
    private final boolean placeable;
    private final boolean droppable;

    // Item configuration (null if no item representation)
    private final NamespacedKey itemKey;
    private final boolean stackable;
    private final Function<CB, ItemStack> itemSupplier;

    // Data configuration (null if no custom data)
    private final Class<?> dataType;

    // Factory for creating blocks on placement
    private final CustomBlockFactory<CB> factory;

    // Tick configuration
    private final boolean tickEnabled;
    private final long tickInterval;

    // Auto-save configuration
    private final long autoSaveTicks;

    // Block type for reflection-based factory
    private final Class<CB> blockType;

    CustomBlockSettings(Builder<CB> builder) {
        this.breakable = builder.breakable;
        this.interactable = builder.interactable;
        this.movable = builder.movable;
        this.explodable = builder.explodable;
        this.placeable = builder.placeable;
        this.droppable = builder.droppable;
        this.itemKey = builder.itemKey;
        this.stackable = builder.stackable;
        this.itemSupplier = builder.itemSupplier;
        this.dataType = builder.dataType;
        this.factory = builder.factory;
        this.tickEnabled = builder.tickEnabled;
        this.tickInterval = builder.tickInterval;
        this.autoSaveTicks = builder.autoSaveTicks;
        this.blockType = builder.blockType;
    }

    // Behavior getters

    public boolean isBreakable() {
        return breakable;
    }

    public boolean isInteractable() {
        return interactable;
    }

    public boolean isMovable() {
        return movable;
    }

    public boolean isExplodable() {
        return explodable;
    }

    public boolean isPlaceable() {
        return placeable;
    }

    public boolean isDroppable() {
        return droppable;
    }

    // Item getters

    public boolean hasItemRepresentation() {
        return itemKey != null;
    }

    @Nullable
    public NamespacedKey getItemKey() {
        return itemKey;
    }

    public boolean isStackable() {
        return stackable;
    }

    @Nullable
    public Function<CB, ItemStack> getItemSupplier() {
        return itemSupplier;
    }

    // Data getters

    public boolean hasData() {
        return dataType != null;
    }

    @Nullable
    public Class<?> getDataType() {
        return dataType;
    }

    // Factory getters

    @Nullable
    public CustomBlockFactory<CB> getFactory() {
        return factory;
    }

    public Class<CB> getBlockType() {
        return blockType;
    }

    // Tick getters

    public boolean isTickEnabled() {
        return tickEnabled;
    }

    public long getTickInterval() {
        return tickInterval;
    }

    // Auto-save getters

    public long getAutoSaveTicks() {
        return autoSaveTicks;
    }

    /**
     * Builder for CustomBlockSettings.
     * Package-private, only accessible through CustomBlockBuilder.
     */
    static class Builder<CB extends CustomBlock> {
        boolean breakable = true;
        boolean interactable = true;
        boolean movable = false;
        boolean explodable = false;
        boolean placeable = true;
        boolean droppable = true;
        NamespacedKey itemKey = null;
        boolean stackable = true;
        Function<CB, ItemStack> itemSupplier = null;
        Class<?> dataType = null;
        CustomBlockFactory<CB> factory = null;
        boolean tickEnabled = true;
        long tickInterval = 1L;
        long autoSaveTicks = 0L;
        Class<CB> blockType = null;

        CustomBlockSettings<CB> build() {
            return new CustomBlockSettings<>(this);
        }
    }

}
