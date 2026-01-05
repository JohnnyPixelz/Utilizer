package io.github.johnnypixelz.utilizer.hologram;

import io.github.johnnypixelz.utilizer.hologram.impl.AbstractNativeHologram;
import io.github.johnnypixelz.utilizer.hologram.impl.ArmorStandHologram;
import io.github.johnnypixelz.utilizer.version.Versions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Native hologram provider using vanilla Minecraft entities.
 * Uses TextDisplay entities on 1.19.4+ and ArmorStand entities on older versions.
 * No external hologram plugin dependency required.
 * <p>
 * Supports both Paper and Spigot servers.
 */
public class NativeHologramProvider implements HologramProvider, Listener {

    private final Plugin plugin;
    private final NamespacedKey hologramIdKey;
    private final Map<String, HologramClickHandler> clickHandlers = new ConcurrentHashMap<>();
    private final Map<String, AbstractNativeHologram> holograms = new ConcurrentHashMap<>();
    private final boolean useTextDisplay;

    public NativeHologramProvider(Plugin plugin) {
        this.plugin = plugin;
        this.hologramIdKey = new NamespacedKey(plugin, "hologram_id");
        this.useTextDisplay = Versions.supportsTextDisplays();

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public Hologram createHologram(String id, Location location, List<String> lines) {
        AbstractNativeHologram existing = holograms.remove(id);
        if (existing != null) {
            existing.remove();
        }

        AbstractNativeHologram hologram;
        if (useTextDisplay) {
            hologram = createTextDisplayHologram(id, location, lines);
        } else {
            hologram = new ArmorStandHologram(id, location, lines, hologramIdKey);
        }

        holograms.put(id, hologram);
        return hologram;
    }

    /**
     * Creates a TextDisplay hologram. This method is separate to ensure the TextDisplayHologram
     * class is only loaded when actually needed (1.19.4+), preventing ClassNotFoundException
     * on older server versions.
     */
    private AbstractNativeHologram createTextDisplayHologram(String id, Location location, List<String> lines) {
        // This import happens lazily when this method is first called
        return new io.github.johnnypixelz.utilizer.hologram.impl.TextDisplayHologram(id, location, lines, hologramIdKey);
    }

    @Override
    public void removeHologram(Hologram hologram) {
        if (hologram instanceof AbstractNativeHologram nativeHologram) {
            clickHandlers.remove(hologram.getId());
            holograms.remove(hologram.getId());
            nativeHologram.remove();
        }
    }

    @Override
    public String getProviderName() {
        return useTextDisplay ? "Native (TextDisplay)" : "Native (ArmorStand)";
    }

    @Override
    @Nullable
    public String getPluginDependency() {
        return null;
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

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityInteract(PlayerInteractAtEntityEvent event) {
        Entity entity = event.getRightClicked();
        String hologramId = getHologramIdFromEntity(entity);
        if (hologramId == null) return;

        HologramClickHandler handler = clickHandlers.get(hologramId);
        if (handler != null) {
            event.setCancelled(true);
            HologramClickHandler.ClickType clickType = event.getPlayer().isSneaking()
                    ? HologramClickHandler.ClickType.SHIFT_RIGHT
                    : HologramClickHandler.ClickType.RIGHT;
            handler.onClick(event.getPlayer(), hologramId, clickType);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;

        Entity entity = event.getEntity();
        String hologramId = getHologramIdFromEntity(entity);
        if (hologramId == null) return;

        HologramClickHandler handler = clickHandlers.get(hologramId);
        if (handler != null) {
            event.setCancelled(true);
            HologramClickHandler.ClickType clickType = player.isSneaking()
                    ? HologramClickHandler.ClickType.SHIFT_LEFT
                    : HologramClickHandler.ClickType.LEFT;
            handler.onClick(player, hologramId, clickType);
        }
    }

    private String getHologramIdFromEntity(Entity entity) {
        if (entity == null) return null;
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        return pdc.get(hologramIdKey, PersistentDataType.STRING);
    }

}
