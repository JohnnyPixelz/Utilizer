package io.github.johnnypixelz.utilizer.minigame.arena;

import io.github.johnnypixelz.utilizer.minigame.MinigameModule;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Arena {
    private Position lobbyPosition;
    private final transient List<Class<? extends MinigameModule>> requiredModules = new ArrayList<>();

    public Arena() {
        this.lobbyPosition = null;
    }

    public List<Class<? extends MinigameModule>> getRequiredModules() {
        return requiredModules;
    }

    protected void registerRequiredModule(Class<? extends MinigameModule> module) {
        if (requiredModules.contains(module)) return;
        requiredModules.add(module);
    }

    public boolean areRequiredModulesPresent(List<? extends MinigameModule> modules) {
        return modules.stream().map(MinigameModule::getClass).collect(Collectors.toList()).containsAll(requiredModules);
    }

    public boolean hasLobbyPosition() {
        return lobbyPosition != null;
    }

    public Position getLobbyPosition() {
        return lobbyPosition;
    }

    public void setLobbyPosition(Position lobbyPosition) {
        this.lobbyPosition = lobbyPosition;
    }

}
