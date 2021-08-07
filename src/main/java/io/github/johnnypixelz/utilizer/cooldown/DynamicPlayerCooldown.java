package io.github.johnnypixelz.utilizer.cooldown;

import io.github.johnnypixelz.utilizer.plugin.Provider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class DynamicPlayerCooldown extends DynamicCooldown<Player> implements Listener {

    public DynamicPlayerCooldown() {
        Bukkit.getPluginManager().registerEvents(this, Provider.getPlugin());
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onLeave(PlayerQuitEvent event) {
        removeWithoutExecuting(event.getPlayer());
    }

}