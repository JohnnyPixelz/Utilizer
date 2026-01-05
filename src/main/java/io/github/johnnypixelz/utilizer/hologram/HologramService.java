package io.github.johnnypixelz.utilizer.hologram;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Service for managing holograms across different hologram plugins.
 * Automatically detects and uses available hologram plugins, falling back to native implementation.
 * <p>
 * For static access, use the {@link Holograms} class instead.
 */
public class HologramService {

    private final Plugin plugin;
    private final Map<String, Hologram> holograms;
    private HologramProvider provider;

    public HologramService(Plugin plugin) {
        this.plugin = plugin;
        this.holograms = new HashMap<>();
        this.provider = detectProvider();
    }

    @Nullable
    private HologramProvider detectProvider() {
        // Try DecentHolograms first
        if (isPluginAvailable("DecentHolograms")) {
            try {
                HologramProvider provider = new DecentHologramsProvider(plugin);
                plugin.getLogger().info("Using DecentHolograms for holograms");
                return provider;
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Failed to initialize DecentHolograms provider", e);
            }
        }

        // Try HolographicDisplays
        if (isPluginAvailable("HolographicDisplays")) {
            try {
                HologramProvider provider = new HolographicDisplaysProvider(plugin);
                plugin.getLogger().info("Using HolographicDisplays for holograms");
                return provider;
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Failed to initialize HolographicDisplays provider", e);
            }
        }

        // Try FancyHolograms
        if (isPluginAvailable("FancyHolograms")) {
            try {
                HologramProvider provider = new FancyHologramsProvider();
                plugin.getLogger().info("Using FancyHolograms for holograms");
                return provider;
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Failed to initialize FancyHolograms provider", e);
            }
        }

        // Fallback to native provider
        try {
            HologramProvider provider = new NativeHologramProvider(plugin);
            plugin.getLogger().info("Using " + provider.getProviderName() + " for holograms (no external plugin)");
            return provider;
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Failed to initialize native hologram provider", e);
        }

        plugin.getLogger().info("Failed to initialize any hologram provider. Holograms will be disabled.");
        return null;
    }

    private boolean isPluginAvailable(String pluginName) {
        return Bukkit.getPluginManager().getPlugin(pluginName) != null;
    }

    /**
     * Check if holograms are supported.
     *
     * @return true if a hologram provider is available
     */
    public boolean isSupported() {
        return provider != null;
    }

    /**
     * Get the current provider.
     *
     * @return Current hologram provider, or null if not supported
     */
    @Nullable
    public HologramProvider getProvider() {
        return provider;
    }

    /**
     * Get the name of the active hologram provider.
     *
     * @return Provider name, or "None" if not supported
     */
    public String getProviderName() {
        return provider != null ? provider.getProviderName() : "None";
    }

    /**
     * Create a hologram.
     *
     * @param id Unique identifier for the hologram
     * @param location Location to spawn the hologram
     * @param lines Lines of text to display
     * @return The created hologram, or null if not supported
     */
    @Nullable
    public Hologram createHologram(String id, Location location, List<String> lines) {
        if (provider == null) {
            return null;
        }

        removeHologram(id);

        try {
            Hologram hologram = provider.createHologram(id, location, lines);
            if (hologram != null) {
                holograms.put(id, hologram);
            }
            return hologram;
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Failed to create hologram: " + id, e);
            return null;
        }
    }

    /**
     * Get a hologram by ID.
     *
     * @param id Hologram ID
     * @return The hologram, or null if not found
     */
    @Nullable
    public Hologram getHologram(String id) {
        return holograms.get(id);
    }

    /**
     * Check if a hologram exists.
     *
     * @param id Hologram ID
     * @return true if the hologram exists
     */
    public boolean hasHologram(String id) {
        return holograms.containsKey(id);
    }

    /**
     * Remove a hologram by ID.
     *
     * @param id Hologram ID
     * @return true if the hologram was removed
     */
    public boolean removeHologram(String id) {
        Hologram hologram = holograms.remove(id);
        if (hologram != null) {
            removeHologram(hologram);
            return true;
        }
        return false;
    }

    /**
     * Remove a hologram.
     *
     * @param hologram The hologram to remove
     */
    public void removeHologram(Hologram hologram) {
        if (provider == null || hologram == null) {
            return;
        }

        try {
            holograms.remove(hologram.getId());
            provider.removeHologram(hologram);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Failed to remove hologram: " + hologram.getId(), e);
        }
    }

    /**
     * Remove all holograms managed by this service.
     */
    public void removeAllHolograms() {
        for (Hologram hologram : holograms.values()) {
            try {
                if (provider != null) {
                    provider.removeHologram(hologram);
                }
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Failed to remove hologram: " + hologram.getId(), e);
            }
        }
        holograms.clear();
    }

    /**
     * Get the number of active holograms.
     *
     * @return Number of holograms
     */
    public int getHologramCount() {
        return holograms.size();
    }

    /**
     * Cleanup resources when the plugin is disabled.
     */
    public void shutdown() {
        removeAllHolograms();
    }

    /**
     * Check if the current provider supports click handling.
     *
     * @return true if click handlers can be registered
     */
    public boolean supportsClickHandling() {
        return provider != null && provider.supportsClickHandling();
    }

    /**
     * Set a click handler for a hologram.
     *
     * @param hologramId The ID of the hologram
     * @param handler The handler to call when clicked, or null to remove
     */
    public void setClickHandler(String hologramId, HologramClickHandler handler) {
        if (provider == null || !provider.supportsClickHandling()) {
            return;
        }

        Hologram hologram = holograms.get(hologramId);
        if (hologram != null) {
            provider.setClickHandler(hologram, handler);
        }
    }

    /**
     * Remove a click handler for a hologram.
     *
     * @param hologramId The ID of the hologram
     */
    public void removeClickHandler(String hologramId) {
        setClickHandler(hologramId, null);
    }

}
