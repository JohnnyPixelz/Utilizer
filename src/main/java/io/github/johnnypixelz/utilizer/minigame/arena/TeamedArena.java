package io.github.johnnypixelz.utilizer.minigame.arena;

import io.github.johnnypixelz.utilizer.minigame.module.teams.TeamsModule;

import java.util.*;

public class TeamedArena extends Arena {
    private final Map<String, List<Position>> spawnPositions;

    public TeamedArena() {
        this.spawnPositions = new HashMap<>();
        registerRequiredModule(TeamsModule.class);
    }

    public void addPosition(String group, Position position) {
        if (!spawnPositions.containsKey(group)) {
            spawnPositions.put(group, new ArrayList<>());
        }

        spawnPositions.get(group).add(position);
    }

    public void removePosition(Position position) {
        for (String group : spawnPositions.keySet()) {
            List<Position> positions = spawnPositions.get(group);
            if (positions.contains(position)) {
                positions.remove(position);
                return;
            }
        }
    }

    public void removeGroup(String group) {
        spawnPositions.remove(group);
    }

    public List<Position> getPositions(String group) {
        List<Position> positions = spawnPositions.getOrDefault(group, new ArrayList<>());
        return Collections.unmodifiableList(positions);
    }

    public List<String> getGroups() {
        return Collections.unmodifiableList(new ArrayList<>(spawnPositions.keySet()));
    }

}
