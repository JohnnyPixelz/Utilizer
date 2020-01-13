package io.github.johnnypixelz.utilizer.cooldown;

import io.github.johnnypixelz.utilizer.provider.Provider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DynamicPlayerCooldown extends DynamicCooldown<Player> implements Listener {

    public DynamicPlayerCooldown() {
        Bukkit.getPluginManager().registerEvents(this, Provider.getPlugin());
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onLeave(PlayerQuitEvent event) {
        removeWithoutExecuting(event.getPlayer());
    }

}