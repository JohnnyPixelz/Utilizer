package io.github.johnnypixelz.utilizer.minigame.module;

import io.github.johnnypixelz.utilizer.minigame.Minigame;
import io.github.johnnypixelz.utilizer.minigame.MinigameModule;
import io.github.johnnypixelz.utilizer.text.Colors;

public class BroadcastModule extends MinigameModule {
    private final String prefix;
    private boolean defaultJoinMessage;
    private boolean defaultQuitMessage;

    public BroadcastModule() {
        this(null);
    }

    public BroadcastModule(String prefix) {
        this.prefix = prefix;
        this.defaultJoinMessage = false;
        this.defaultQuitMessage = false;
    }

    @Override
    protected void init() {
        getEventManager().getOnPlayerJoin().listen(player -> {
            if (!defaultJoinMessage) return;
            broadcast("&a" + player.getName() + " &fhas joined &8[&7" + getCurrentPlayerAmount() + "&8/&7" + getPlayerLimit() + "&8]");
        });

        getEventManager().getOnPlayerRemove().listen(player -> {
            if (!defaultQuitMessage) return;
            if (getMinigame().getState() != Minigame.GameState.WAITING) return;
            broadcast("&a" + player.getName() + " &fhas left &8[&7" + getCurrentPlayerAmount() + "&8/&7" + getPlayerLimit() + "&8]");
        });
    }

    @Override
    public void broadcast(String message) {
        if (prefix != null) {
            getCurrentPlayers().forEach(player -> player.sendMessage(Colors.color(prefix + message)));
        } else {
            getCurrentPlayers().forEach(player -> player.sendMessage(Colors.color(message)));
        }
    }

    public BroadcastModule enableDefaultJoinMessage() {
        defaultJoinMessage = true;
        return this;
    }

    public BroadcastModule enableDefaultQuitMessage() {
        defaultQuitMessage = true;
        return this;
    }

}
