package io.github.johnnypixelz.utilizer.hologram.impl;

import io.github.johnnypixelz.utilizer.hologram.Hologram;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for native hologram implementations.
 */
public abstract class AbstractNativeHologram implements Hologram {

    protected final String id;
    protected Location location;
    protected List<String> lines;

    protected AbstractNativeHologram(String id, Location location, List<String> lines) {
        this.id = id;
        this.location = location.clone();
        this.lines = new ArrayList<>(lines);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Location getLocation() {
        return location.clone();
    }

    @Override
    public List<String> getLines() {
        return new ArrayList<>(lines);
    }

}
