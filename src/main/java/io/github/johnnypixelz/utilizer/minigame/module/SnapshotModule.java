package io.github.johnnypixelz.utilizer.minigame.module;

import io.github.johnnypixelz.utilizer.minigame.MinigameModule;
import io.github.johnnypixelz.utilizer.serialize.player.PlayerSnapshot;
import org.bukkit.event.EventPriority;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SnapshotModule extends MinigameModule {
    private final Map<UUID, PlayerSnapshot> snapshots;

    public SnapshotModule() {
        this.snapshots = new HashMap<>();
    }

    @Override
    protected void init() {
        getEventManager().getOnPlayerJoin().listen(player -> {
            PlayerSnapshot snapshot = PlayerSnapshot.getAndClean(player);
            snapshots.put(player.getUniqueId(), snapshot);
        }, EventPriority.LOWEST);

        getEventManager().getOnPlayerRemove().listen(player -> {
            snapshots.get(player.getUniqueId()).restore(player);
        }, EventPriority.HIGHEST);
    }

}
