package io.github.johnnypixelz.utilizer.hologram;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;

import java.util.List;

/**
 * Hologram wrapper interface.
 * Provides a unified API across different hologram implementations.
 * <p>
 * Display property methods (scale, billboard, background color, etc.) have default
 * no-op implementations. They are supported by native TextDisplay holograms and
 * silently ignored by providers that don't support them.
 */
public interface Hologram {

    /**
     * Update the lines of the hologram.
     *
     * @param lines New lines
     */
    void updateLines(List<String> lines);

    /**
     * Get the current lines of the hologram.
     *
     * @return Current lines
     */
    List<String> getLines();

    /**
     * Get the location of the hologram.
     *
     * @return Location
     */
    Location getLocation();

    /**
     * Teleport the hologram to a new location.
     *
     * @param location New location
     */
    void teleport(Location location);

    /**
     * Remove this hologram.
     */
    void remove();

    /**
     * Get the unique identifier of this hologram.
     *
     * @return Hologram ID
     */
    String getId();

    // ==================== Lifecycle ====================

    /**
     * Check if this hologram's underlying entities are still alive and valid.
     * For external providers this always returns true (they manage their own lifecycle).
     *
     * @return true if the hologram is valid
     */
    default boolean isValid() { return true; }

    /**
     * Respawn the hologram's underlying entities if they were killed or removed.
     * For external providers this is a no-op (they manage their own lifecycle).
     */
    default void respawn() {}

    /**
     * Called when the hologram's chunk is unloaded.
     * Clears entity references without trying to remove them (the server handles that).
     * For external providers this is a no-op.
     */
    default void despawn() {}

    // ==================== Display Properties ====================

    /**
     * Set the uniform scale of the hologram.
     * Only supported by native TextDisplay holograms.
     *
     * @param scale Scale factor (1.0 = default)
     */
    default void setScale(float scale) {}

    /**
     * Set the billboard mode of the hologram.
     * Only supported by native TextDisplay holograms.
     *
     * @param billboard Billboard mode
     */
    default void setBillboard(Display.Billboard billboard) {}

    /**
     * Set the background color of the hologram.
     * Only supported by native TextDisplay holograms.
     *
     * @param color Background color (use Color.fromARGB for transparency)
     */
    default void setBackgroundColor(Color color) {}

    /**
     * Set whether the hologram text is shadowed.
     * Only supported by native TextDisplay holograms.
     *
     * @param shadowed true to enable text shadow
     */
    default void setShadowed(boolean shadowed) {}

    /**
     * Set the text alignment of the hologram.
     * Only supported by native TextDisplay holograms.
     *
     * @param alignment Text alignment
     */
    default void setAlignment(TextDisplay.TextAlignment alignment) {}

    /**
     * Set the line width of the hologram before wrapping.
     * Only supported by native TextDisplay holograms.
     *
     * @param lineWidth Line width in pixels
     */
    default void setLineWidth(int lineWidth) {}

    /**
     * Set the text opacity of the hologram.
     * Only supported by native TextDisplay holograms.
     *
     * @param opacity Opacity value (-128 to 127, where -128 is fully opaque)
     */
    default void setTextOpacity(byte opacity) {}

    /**
     * Set whether the hologram text is see-through.
     * Only supported by native TextDisplay holograms.
     *
     * @param seeThrough true to make text see-through
     */
    default void setSeeThrough(boolean seeThrough) {}

    /**
     * Set the view range of the hologram.
     * Only supported by native TextDisplay holograms.
     *
     * @param range View range multiplier (1.0 = default)
     */
    default void setViewRange(float range) {}

}
