package io.github.johnnypixelz.utilizer.minigame;

import io.github.johnnypixelz.utilizer.event.StatefulEventEmitter;
import io.github.johnnypixelz.utilizer.event.StatelessEventEmitter;
import org.bukkit.entity.Player;

public class MinigameEventManager {
    private final StatelessEventEmitter onMinigameStart = new StatelessEventEmitter();
    private final StatelessEventEmitter onMinigameFinish = new StatelessEventEmitter();
    private final StatelessEventEmitter onMinigameCleanup = new StatelessEventEmitter();
    private final StatefulEventEmitter<Player> onPlayerJoin = new StatefulEventEmitter<>();
    private final StatefulEventEmitter<Player> onPlayerDisconnect = new StatefulEventEmitter<>();
    private final StatefulEventEmitter<Player> onPlayerRemove = new StatefulEventEmitter<>();

    public StatelessEventEmitter getOnMinigameStart() {
        return onMinigameStart;
    }

    public StatelessEventEmitter getOnMinigameFinish() {
        return onMinigameFinish;
    }

    public StatelessEventEmitter getOnMinigameCleanup() {
        return onMinigameCleanup;
    }

    public StatefulEventEmitter<Player> getOnPlayerJoin() {
        return onPlayerJoin;
    }

    public StatefulEventEmitter<Player> getOnPlayerDisconnect() {
        return onPlayerDisconnect;
    }

    public StatefulEventEmitter<Player> getOnPlayerRemove() {
        return onPlayerRemove;
    }
}
