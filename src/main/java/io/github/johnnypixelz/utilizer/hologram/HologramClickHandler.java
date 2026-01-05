package io.github.johnnypixelz.utilizer.hologram;

import org.bukkit.entity.Player;

/**
 * Functional interface for handling hologram click events.
 * Implementations receive callbacks when a player clicks on a hologram.
 */
@FunctionalInterface
public interface HologramClickHandler {

    /**
     * Called when a player clicks on a hologram.
     *
     * @param player The player who clicked
     * @param hologramId The ID of the hologram that was clicked
     * @param clickType The type of click (left, right, shift variants)
     */
    void onClick(Player player, String hologramId, ClickType clickType);

    /**
     * Types of clicks that can be detected on holograms.
     * Not all hologram providers support all click types.
     */
    enum ClickType {
        /** Left mouse button click */
        LEFT,
        /** Right mouse button click */
        RIGHT,
        /** Left click while sneaking */
        SHIFT_LEFT,
        /** Right click while sneaking */
        SHIFT_RIGHT
    }

}
