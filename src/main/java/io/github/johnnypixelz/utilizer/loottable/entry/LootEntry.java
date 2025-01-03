package io.github.johnnypixelz.utilizer.loottable.entry;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

public interface LootEntry {

    @Nonnull
    List<LootEntry> roll(); // Returns the result of rolling this entry

    double getChance(); // Used for weighted mode

    void giveLoot(Player player);

    void dropLootAt(Location location);

    default void dropLootAt(Player player, Location location) {
        dropLootAt(location);
    }

}
