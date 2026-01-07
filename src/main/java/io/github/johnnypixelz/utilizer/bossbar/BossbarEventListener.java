package io.github.johnnypixelz.utilizer.bossbar;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Internal event listener for bossbar cleanup.
 * Handles player quit to clean up bossbar resources.
 */
class BossbarEventListener implements Listener {

    private final BossbarService service;

    BossbarEventListener(BossbarService service) {
        this.service = service;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        service.onPlayerQuit(player);
    }

}
