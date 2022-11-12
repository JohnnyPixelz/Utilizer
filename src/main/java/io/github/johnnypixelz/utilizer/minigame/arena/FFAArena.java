package io.github.johnnypixelz.utilizer.minigame.arena;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class FFAArena extends Arena {
    private final List<Position> spawnPositions;

    public FFAArena() {
        this.spawnPositions = new ArrayList<>();
    }

    private <T> T getRandomElement(List<T> list) {
        if (list.isEmpty()) return null;
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

    public Position getRandomSpawnPosition() {
        return getRandomElement(spawnPositions);
    }

    public List<Position> getSpawnPositions() {
        return spawnPositions;
    }

    public void addSpawnPosition(Position position) {
        spawnPositions.add(position);
    }

    public void removeSpawnPosition(Position position) {
        spawnPositions.remove(position);
    }

}
