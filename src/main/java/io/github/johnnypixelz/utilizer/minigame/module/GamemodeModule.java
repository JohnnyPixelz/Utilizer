package io.github.johnnypixelz.utilizer.minigame.module;

import io.github.johnnypixelz.utilizer.minigame.MinigameModule;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class GamemodeModule extends MinigameModule {
    private final Map<Player, GameMode> gameModeMap;
    private final GameMode gameMode;

    public GamemodeModule(GameMode gameMode) {
        this.gameMode = gameMode;
        this.gameModeMap = new HashMap<>();
    }

    @Override
    protected void init() {
        getEventManager().getOnPlayerJoin().listen(player -> {
            gameModeMap.put(player, player.getGameMode());
        });

        getEventManager().getOnPlayerRemove().listen(player -> {
            gameModeMap.computeIfPresent(player, (player1, gameMode1) -> {
                player1.setGameMode(gameMode1);
                return null;
            });
        });

        getEventManager().getOnMinigameStart().listen(() -> {
            getCurrentPlayers().forEach(player -> player.setGameMode(gameMode));
        });
    }

}
