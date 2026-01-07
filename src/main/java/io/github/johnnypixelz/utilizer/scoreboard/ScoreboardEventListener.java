package io.github.johnnypixelz.utilizer.scoreboard;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Internal event listener for scoreboard cleanup.
 */
class ScoreboardEventListener implements Listener {

    private final ScoreboardService service;

    ScoreboardEventListener(ScoreboardService service) {
        this.service = service;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        service.onPlayerQuit(player);
    }

}
