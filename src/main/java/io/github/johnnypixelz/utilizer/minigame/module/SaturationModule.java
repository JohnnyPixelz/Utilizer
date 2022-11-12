package io.github.johnnypixelz.utilizer.minigame.module;

import io.github.johnnypixelz.utilizer.minigame.MinigameModule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class SaturationModule extends MinigameModule {

    @Override
    protected void init() {
        getEventManager().getOnPlayerJoin()
                .listen(player -> player.setFoodLevel(20));
    }

    @EventHandler
    private void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        final Player player = (Player) event.getEntity();
        if (!isInMinigame(player)) return;

        event.setFoodLevel(20);
    }
}
