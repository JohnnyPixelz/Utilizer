package io.github.johnnypixelz.utilizer.hologram;

import io.github.johnnypixelz.utilizer.plugin.Provider;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Static utility class for managing holograms.
 * Provides a simple API for creating and managing holograms using the automatically detected provider.
 * <p>
 * Example usage:
 * <pre>
 * // Create a hologram with a specific ID
 * Holograms.create("my-hologram", location, List.of("&6Hello", "&eWorld"));
 *
 * // Create a hologram with an auto-generated ID
 * Hologram hologram = Holograms.create(location, "&6Hello", "&eWorld");
 * String id = hologram.getId(); // Get the generated ID if needed
 *
 * // Update lines
 * Holograms.get("my-hologram").updateLines(List.of("&aUpdated!"));
 *
 * // Add click handler
 * Holograms.onClick("my-hologram", (player, id, clickType) -> {
 *     player.sendMessage("You clicked the hologram!");
 * });
 *
 * // Remove by ID
 * Holograms.remove("my-hologram");
 *
 * // Or remove via the hologram object
 * hologram.remove();
 * </pre>
 */
public final class Holograms {

    private static HologramService service;

    private Holograms() {
    }

    /**
     * Get or create the hologram service for the current plugin.
     *
     * @return The hologram service
     */
    public static synchronized HologramService service() {
        if (service == null) {
            service = new HologramService(Provider.getPlugin());
        }
        return service;
    }

    /**
     * Check if holograms are supported.
     *
     * @return true if a hologram provider is available
     */
    public static boolean isSupported() {
        return service().isSupported();
    }

    /**
     * Get the name of the active hologram provider.
     *
     * @return Provider name (e.g., "DecentHolograms", "Native (TextDisplay)")
     */
    public static String getProviderName() {
        return service().getProviderName();
    }

    /**
     * Create a hologram at the specified location.
     *
     * @param id Unique identifier for the hologram
     * @param location Location to spawn the hologram
     * @param lines Lines of text to display (supports color codes)
     * @return The created hologram, or null if not supported
     */
    @Nullable
    public static Hologram create(String id, Location location, List<String> lines) {
        return service().createHologram(id, location, lines);
    }

    /**
     * Create a hologram at the specified location with varargs.
     *
     * @param id Unique identifier for the hologram
     * @param location Location to spawn the hologram
     * @param lines Lines of text to display (supports color codes)
     * @return The created hologram, or null if not supported
     */
    @Nullable
    public static Hologram create(String id, Location location, String... lines) {
        return create(id, location, List.of(lines));
    }

    /**
     * Create a hologram at the specified location with an auto-generated ID.
     *
     * @param location Location to spawn the hologram
     * @param lines Lines of text to display (supports color codes)
     * @return The created hologram, or null if not supported
     */
    @Nullable
    public static Hologram create(Location location, List<String> lines) {
        return create(UUID.randomUUID().toString(), location, lines);
    }

    /**
     * Create a hologram at the specified location with an auto-generated ID (varargs).
     *
     * @param location Location to spawn the hologram
     * @param lines Lines of text to display (supports color codes)
     * @return The created hologram, or null if not supported
     */
    @Nullable
    public static Hologram create(Location location, String... lines) {
        return create(UUID.randomUUID().toString(), location, List.of(lines));
    }

    /**
     * Get a hologram by ID.
     *
     * @param id Hologram ID
     * @return The hologram, or null if not found
     */
    @Nullable
    public static Hologram get(String id) {
        return service().getHologram(id);
    }

    /**
     * Check if a hologram exists.
     *
     * @param id Hologram ID
     * @return true if the hologram exists
     */
    public static boolean exists(String id) {
        return service().hasHologram(id);
    }

    /**
     * Remove a hologram by ID.
     *
     * @param id Hologram ID
     * @return true if the hologram was removed
     */
    public static boolean remove(String id) {
        return service().removeHologram(id);
    }

    /**
     * Remove all holograms.
     */
    public static void removeAll() {
        service().removeAllHolograms();
    }

    /**
     * Get the number of active holograms.
     *
     * @return Number of holograms
     */
    public static int count() {
        return service().getHologramCount();
    }

    /**
     * Check if click handling is supported by the current provider.
     *
     * @return true if click handlers can be registered
     */
    public static boolean supportsClickHandling() {
        return service().supportsClickHandling();
    }

    /**
     * Set a click handler for a hologram.
     *
     * @param hologramId The ID of the hologram
     * @param handler The handler to call when clicked
     */
    public static void onClick(String hologramId, HologramClickHandler handler) {
        service().setClickHandler(hologramId, handler);
    }

    /**
     * Remove a click handler for a hologram.
     *
     * @param hologramId The ID of the hologram
     */
    public static void removeClickHandler(String hologramId) {
        service().removeClickHandler(hologramId);
    }

    /**
     * Shutdown and cleanup all holograms.
     * Should be called when the plugin is disabled.
     */
    public static void shutdown() {
        if (service != null) {
            service.shutdown();
            service = null;
        }
    }

}
