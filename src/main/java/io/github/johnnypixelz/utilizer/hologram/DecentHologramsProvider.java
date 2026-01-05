package io.github.johnnypixelz.utilizer.hologram;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.actions.ClickType;
import eu.decentsoftware.holograms.event.HologramClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Hologram provider for DecentHolograms plugin.
 */
public class DecentHologramsProvider implements HologramProvider, Listener {

    private final Map<String, HologramClickHandler> clickHandlers = new ConcurrentHashMap<>();

    public DecentHologramsProvider(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onHologramClick(HologramClickEvent event) {
        String hologramName = event.getHologram().getName();
        HologramClickHandler handler = clickHandlers.get(hologramName);
        if (handler != null) {
            HologramClickHandler.ClickType clickType = mapClickType(event.getClick());
            handler.onClick(event.getPlayer(), hologramName, clickType);
        }
    }

    private HologramClickHandler.ClickType mapClickType(ClickType click) {
        return switch (click) {
            case LEFT -> HologramClickHandler.ClickType.LEFT;
            case RIGHT -> HologramClickHandler.ClickType.RIGHT;
            case SHIFT_LEFT -> HologramClickHandler.ClickType.SHIFT_LEFT;
            case SHIFT_RIGHT -> HologramClickHandler.ClickType.SHIFT_RIGHT;
        };
    }

    @Override
    public Hologram createHologram(String id, Location location, List<String> lines) {
        eu.decentsoftware.holograms.api.holograms.Hologram hologram = DHAPI.createHologram(id, location, lines);
        hologram.setSaveToFile(false);
        return new DecentHologramWrapper(id, hologram);
    }

    @Override
    public void removeHologram(Hologram hologram) {
        if (hologram instanceof DecentHologramWrapper) {
            clickHandlers.remove(hologram.getId());
            ((DecentHologramWrapper) hologram).remove();
        }
    }

    @Override
    public String getProviderName() {
        return "DecentHolograms";
    }

    @Override
    public String getPluginDependency() {
        return "DecentHolograms";
    }

    @Override
    public boolean supportsClickHandling() {
        return true;
    }

    @Override
    public void setClickHandler(Hologram hologram, HologramClickHandler handler) {
        if (handler != null) {
            clickHandlers.put(hologram.getId(), handler);
        } else {
            clickHandlers.remove(hologram.getId());
        }
    }

    private static class DecentHologramWrapper implements Hologram {
        private final String id;
        private final eu.decentsoftware.holograms.api.holograms.Hologram hologram;

        public DecentHologramWrapper(String id, eu.decentsoftware.holograms.api.holograms.Hologram hologram) {
            this.id = id;
            this.hologram = hologram;
        }

        @Override
        public void updateLines(List<String> lines) {
            DHAPI.setHologramLines(hologram, lines);
        }

        @Override
        public List<String> getLines() {
            List<String> lines = new ArrayList<>();
            if (hologram.getPage(0) != null) {
                hologram.getPage(0).getLines().forEach(line -> lines.add(line.getContent()));
            }
            return lines;
        }

        @Override
        public Location getLocation() {
            return hologram.getLocation();
        }

        @Override
        public void teleport(Location location) {
            DHAPI.moveHologram(hologram, location);
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
