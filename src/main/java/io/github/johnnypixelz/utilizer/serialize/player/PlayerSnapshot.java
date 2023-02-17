package io.github.johnnypixelz.utilizer.serialize.player;

import io.github.johnnypixelz.utilizer.serialize.world.Position;
import org.bukkit.entity.Player;

public class PlayerSnapshot {
    private final Position location;
    private final PlayerInventorySnapshot inventorySnapshot;
    private final AttributeSnapshot attributeSnapshot;

    public PlayerSnapshot(Player player) {
        this.location = Position.of(player);
        this.inventorySnapshot = new PlayerInventorySnapshot(player);
        this.attributeSnapshot = new AttributeSnapshot(player);
    }

    public void apply(Player player) {
        inventorySnapshot.apply(player);
        attributeSnapshot.apply(player);

        player.teleport(location.toLocation());
    }

    public Position getPosition() {
        return location;
    }

    public AttributeSnapshot getAttributeSnapshot() {
        return attributeSnapshot;
    }

    public PlayerInventorySnapshot getInventorySnapshot() {
        return inventorySnapshot;
    }

}
