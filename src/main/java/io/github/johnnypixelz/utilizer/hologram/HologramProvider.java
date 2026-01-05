package io.github.johnnypixelz.utilizer.hologram;

import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Provider interface for hologram implementations.
 * Implementations handle specific hologram plugin integrations or native entity-based holograms.
 */
public interface HologramProvider {

    /**
     * Create a hologram at the specified location with the given lines.
     *
     * @param id Unique identifier for the hologram
     * @param location Location to spawn the hologram
     * @param lines Lines of text to display
     * @return The created hologram wrapper
     */
    Hologram createHologram(String id, Location location, List<String> lines);

    /**
     * Remove a hologram.
     *
     * @param hologram The hologram to remove
     */
    void removeHologram(Hologram hologram);

    /**
     * Get the name of this provider (e.g., "DecentHolograms", "Native (TextDisplay)").
     *
     * @return Provider name
     */
    String getProviderName();

    /**
     * Get the plugin name that this provider depends on.
     *
     * @return Plugin dependency name, or null if no external dependency
     */
    @Nullable
    String getPluginDependency();

    /**
     * Check if this provider supports click handling.
     * Providers without native click APIs should return false.
     *
     * @return true if click handlers can be registered
     */
    boolean supportsClickHandling();

    /**
     * Set a click handler for a hologram.
     * If click handling is not supported, this method does nothing.
     *
     * @param hologram The hologram to set the handler for
     * @param handler The handler to call when clicked, or null to remove
     */
    void setClickHandler(Hologram hologram, HologramClickHandler handler);

    /**
     * Remove a click handler for a hologram.
     *
     * @param hologram The hologram to remove the handler from
     */
    default void removeClickHandler(Hologram hologram) {
        setClickHandler(hologram, null);
    }

}
