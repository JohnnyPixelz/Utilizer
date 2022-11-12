package io.github.johnnypixelz.utilizer.minigame.module;

import io.github.johnnypixelz.utilizer.minigame.MinigameModule;
import io.github.johnnypixelz.utilizer.minigame.arena.Arena;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.*;

public class SpectateModule extends MinigameModule {
    private final Map<UUID, GameMode> spectators;

    public SpectateModule() {
        this.spectators = new HashMap<>();
    }

    @Override
    protected void init() {
        getEventManager().getOnPlayerJoin().listen(player -> {
            Arena arena = getMinigame().getArena();
            if (!arena.hasLobbyPosition()) return;

            Location location = getMinigame().getArena().getLobbyPosition().toLocation();
            player.teleport(location);
        });

        getEventManager().getOnPlayerRemove().listen(this::exitSpectatorMode);
    }

    public List<UUID> getSpectators() {
        return new ArrayList<>(spectators.keySet());
    }

    public void enterSpectatorMode(Player player) {
        spectators.put(player.getUniqueId(), player.getGameMode());
        player.setGameMode(GameMode.SPECTATOR);
    }

    public void exitSpectatorMode(Player player) {
        if (!spectators.containsKey(player.getUniqueId())) return;
        player.setGameMode(spectators.get(player.getUniqueId()));
        spectators.remove(player.getUniqueId());
    }

    public boolean isSpectator(Player player) {
        return spectators.containsKey(player.getUniqueId());
    }

    @EventHandler
    private void onSpectatorDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        if (!isSpectator(player)) return;
        event.setCancelled(true);
    }

}
