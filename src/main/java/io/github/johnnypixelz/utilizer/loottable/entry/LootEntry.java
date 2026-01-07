package io.github.johnnypixelz.utilizer.loottable.entry;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import java.util.List;

public interface LootEntry {

    @NotNull
    List<LootEntry> roll(); // Returns the result of rolling this entry

    double getChance(); // Used for weighted mode

    void giveLoot(Player player);

    void dropLootAt(Location location);

    default void dropLootAt(Player player, Location location) {
        dropLootAt(location);
    }

}
