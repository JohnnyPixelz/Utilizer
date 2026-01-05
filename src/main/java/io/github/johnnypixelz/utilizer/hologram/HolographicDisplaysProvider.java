package io.github.johnnypixelz.utilizer.hologram;

import io.github.johnnypixelz.utilizer.text.Colors;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.line.HologramLine;
import me.filoghost.holographicdisplays.api.hologram.line.TextHologramLine;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Hologram provider for HolographicDisplays plugin.
 */
public class HolographicDisplaysProvider implements HologramProvider {

    private final HolographicDisplaysAPI api;
    private final Map<String, HologramClickHandler> clickHandlers = new ConcurrentHashMap<>();

    public HolographicDisplaysProvider(Plugin plugin) {
        this.api = HolographicDisplaysAPI.get(plugin);
    }

    @Override
    public Hologram createHologram(String id, Location location, List<String> lines) {
        me.filoghost.holographicdisplays.api.hologram.Hologram hologram = api.createHologram(location);

        for (String line : lines) {
            hologram.getLines().appendText(Colors.color(line));
        }

        return new HolographicDisplaysHologramWrapper(id, hologram, this);
    }

    @Override
    public void removeHologram(Hologram hologram) {
        if (hologram instanceof HolographicDisplaysHologramWrapper wrapper) {
            clickHandlers.remove(hologram.getId());
            wrapper.getInternalHologram().delete();
        }
    }

    @Override
    public String getProviderName() {
        return "HolographicDisplays";
    }

    @Override
    public String getPluginDependency() {
        return "HolographicDisplays";
    }

    @Override
    public boolean supportsClickHandling() {
        return true;
    }

    @Override
    public void setClickHandler(Hologram hologram, HologramClickHandler handler) {
        if (!(hologram instanceof HolographicDisplaysHologramWrapper wrapper)) {
            return;
        }

        if (handler != null) {
            clickHandlers.put(hologram.getId(), handler);
            applyClickListeners(wrapper.getInternalHologram(), hologram.getId());
        } else {
            clickHandlers.remove(hologram.getId());
        }
    }

    private void applyClickListeners(me.filoghost.holographicdisplays.api.hologram.Hologram hologram, String id) {
        for (int i = 0; i < hologram.getLines().size(); i++) {
            HologramLine line = hologram.getLines().get(i);
            if (line instanceof TextHologramLine textLine) {
                textLine.setClickListener(clickEvent -> {
                    HologramClickHandler handler = clickHandlers.get(id);
                    if (handler != null) {
                        // HolographicDisplays doesn't distinguish click types
                        handler.onClick(clickEvent.getPlayer(), id, HologramClickHandler.ClickType.RIGHT);
                    }
                });
            }
        }
    }

    private static class HolographicDisplaysHologramWrapper implements Hologram {
        private final String id;
        private final me.filoghost.holographicdisplays.api.hologram.Hologram hologram;
        private final HolographicDisplaysProvider provider;

        public HolographicDisplaysHologramWrapper(String id, me.filoghost.holographicdisplays.api.hologram.Hologram hologram, HolographicDisplaysProvider provider) {
            this.id = id;
            this.hologram = hologram;
            this.provider = provider;
        }

        me.filoghost.holographicdisplays.api.hologram.Hologram getInternalHologram() {
            return hologram;
        }

        @Override
        public void updateLines(List<String> lines) {
            hologram.getLines().clear();
            for (String line : lines) {
                hologram.getLines().appendText(Colors.color(line));
            }
            // Re-apply click listeners after update
            if (provider.clickHandlers.containsKey(id)) {
                provider.applyClickListeners(hologram, id);
            }
        }

        @Override
        public List<String> getLines() {
            List<String> lines = new ArrayList<>();
            for (int i = 0; i < hologram.getLines().size(); i++) {
                HologramLine line = hologram.getLines().get(i);
                if (line instanceof TextHologramLine textLine) {
                    lines.add(textLine.getText());
                }
            }
            return lines;
        }

        @Override
        public Location getLocation() {
            return hologram.getPosition().toLocation();
        }

        @Override
        public void teleport(Location location) {
            hologram.setPosition(location);
        }

        @Override
        public void remove() {
            hologram.delete();
        }

        @Override
        public String getId() {
            return id;
        }
    }

}
