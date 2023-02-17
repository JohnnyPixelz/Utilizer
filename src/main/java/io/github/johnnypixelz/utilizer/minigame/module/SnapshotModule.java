package io.github.johnnypixelz.utilizer.minigame.module;

import io.github.johnnypixelz.utilizer.minigame.MinigameModule;
import io.github.johnnypixelz.utilizer.serialize.player.PlayerSnapshot;
import org.bukkit.attribute.Attribute;
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
            final PlayerSnapshot playerSnapshot = new PlayerSnapshot(player);
            snapshots.put(player.getUniqueId(), playerSnapshot);

            player.getInventory().clear();
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            player.setFoodLevel(20);
            player.setSaturation(4);
        }, EventPriority.LOWEST);

        getEventManager().getOnPlayerRemove().listen(player -> {
            snapshots.get(player.getUniqueId()).apply(player);
        }, EventPriority.HIGHEST);
    }

}
