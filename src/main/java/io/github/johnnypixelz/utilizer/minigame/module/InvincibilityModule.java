package io.github.johnnypixelz.utilizer.minigame.module;

import io.github.johnnypixelz.utilizer.minigame.MinigameModule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;

public class InvincibilityModule extends MinigameModule {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        if (!isInMinigame(player)) return;

        event.setCancelled(true);
    }
}
