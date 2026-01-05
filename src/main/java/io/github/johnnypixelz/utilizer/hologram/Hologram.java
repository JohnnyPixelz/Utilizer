package io.github.johnnypixelz.utilizer.hologram;

import org.bukkit.Location;

import java.util.List;

/**
 * Hologram wrapper interface.
 * Provides a unified API across different hologram implementations.
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

}
