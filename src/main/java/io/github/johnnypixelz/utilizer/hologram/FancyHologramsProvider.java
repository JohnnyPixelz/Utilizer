package io.github.johnnypixelz.utilizer.hologram;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.TextHologramData;
import io.github.johnnypixelz.utilizer.text.Colors;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Hologram provider for FancyHolograms plugin.
 * Note: FancyHolograms does not support click handling.
 */
public class FancyHologramsProvider implements HologramProvider {

    public FancyHologramsProvider() {
    }

    @Override
    public Hologram createHologram(String id, Location location, List<String> lines) {
        List<String> coloredLines = new ArrayList<>();
        for (String line : lines) {
            coloredLines.add(Colors.color(line));
        }

        TextHologramData data = new TextHologramData(id, location);
        data.setText(coloredLines);

        HologramManager manager = FancyHologramsPlugin.get().getHologramManager();
        de.oliver.fancyholograms.api.hologram.Hologram hologram = manager.create(data);
        hologram.getData().setPersistent(false);
        manager.addHologram(hologram);

        return new FancyHologramWrapper(id, hologram);
    }

    @Override
    public void removeHologram(Hologram hologram) {
        if (hologram instanceof FancyHologramWrapper wrapper) {
            wrapper.remove();
        }
    }

    @Override
    public String getProviderName() {
        return "FancyHolograms";
    }

    @Override
    public String getPluginDependency() {
        return "FancyHolograms";
    }

    @Override
    public boolean supportsClickHandling() {
        return false;
    }

    @Override
    public void setClickHandler(Hologram hologram, HologramClickHandler handler) {
        // FancyHolograms does not support click handling
    }

    private static class FancyHologramWrapper implements Hologram {
        private final String id;
        private final de.oliver.fancyholograms.api.hologram.Hologram hologram;

        public FancyHologramWrapper(String id, de.oliver.fancyholograms.api.hologram.Hologram hologram) {
            this.id = id;
            this.hologram = hologram;
        }

        @Override
        public void updateLines(List<String> lines) {
            if (hologram.getData() instanceof TextHologramData textData) {
                List<String> coloredLines = new ArrayList<>();
                for (String line : lines) {
                    coloredLines.add(Colors.color(line));
                }
                textData.setText(coloredLines);
                hologram.forceUpdate();
            }
        }

        @Override
        public List<String> getLines() {
            if (hologram.getData() instanceof TextHologramData textData) {
                return new ArrayList<>(textData.getText());
            }
            return new ArrayList<>();
        }

        @Override
        public Location getLocation() {
            return hologram.getData().getLocation();
        }

        @Override
        public void teleport(Location location) {
            hologram.getData().setLocation(location);
            hologram.forceUpdate();
        }

        @Override
        public void remove() {
            HologramManager manager = FancyHologramsPlugin.get().getHologramManager();
            manager.removeHologram(hologram);
        }

        @Override
        public String getId() {
            return id;
        }
    }

}
