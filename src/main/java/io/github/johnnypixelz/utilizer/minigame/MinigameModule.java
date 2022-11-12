package io.github.johnnypixelz.utilizer.minigame;

import io.github.johnnypixelz.utilizer.minigame.arena.Arena;
import io.github.johnnypixelz.utilizer.minigame.module.BroadcastModule;
import io.github.johnnypixelz.utilizer.text.Colors;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.List;

public abstract class MinigameModule implements Listener {
    private Minigame<? extends Arena> minigame;

    public void injectMinigameInstance(Minigame<? extends Arena> minigame) {
        this.minigame = minigame;
    }

    protected boolean isInMinigame(Player player) {
        return getMinigame().getPlayers().contains(player.getUniqueId());
    }

    protected Minigame<? extends Arena> getMinigame() {
        return minigame;
    }

    protected <T extends MinigameModule> T getModule(Class<T> module) {
        return (T) getMinigame().getModule(module);
    }

    protected List<Player> getCurrentPlayers() {
        return getMinigame().getPlayerObjects();
    }

    protected int getCurrentPlayerAmount() {
        return getMinigame().getPlayerCount();
    }

    protected int getPlayerLimit() {
        return getMinigame().getPlayerLimit();
    }

    protected void broadcast(String message) {
        final BroadcastModule module = getModule(BroadcastModule.class);

        if (module != null) {
            module.broadcast(message);
        } else {
            getCurrentPlayers().forEach(player -> player.sendMessage(Colors.color(message)));
        }
    }

    protected MinigameEventManager getEventManager() {
        return minigame.getEventManager();
    }

    protected void init() {
    }

}
