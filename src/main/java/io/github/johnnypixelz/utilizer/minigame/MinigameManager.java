package io.github.johnnypixelz.utilizer.minigame;

import io.github.johnnypixelz.utilizer.minigame.arena.Arena;

import java.util.*;

public class MinigameManager {
    private final Map<UUID, Minigame<? extends Arena>> activeMinigames;

    public MinigameManager() {
        activeMinigames = new HashMap<>();
    }

    public Map<UUID, Minigame<? extends Arena>> getActiveMinigameMap() {
        return activeMinigames;
    }

    public List<Minigame<? extends Arena>> getActiveMinigames() {
        return new ArrayList<>(activeMinigames.values());
    }

    public void registerMinigame(Minigame<? extends Arena> minigame) {
        if (activeMinigames.containsValue(minigame)) return;
        activeMinigames.put(UUID.randomUUID(), minigame);

        minigame.getEventManager().getOnMinigameCleanup().listen(() -> unregisterMinigame(minigame));
    }

    private void unregisterMinigame(Minigame<? extends Arena> minigame) {
        activeMinigames.values().remove(minigame);
    }

    public Minigame<? extends Arena> getActiveMinigame(UUID uuid) {
        return activeMinigames.get(uuid);
    }
}
