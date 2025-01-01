package io.github.johnnypixelz.utilizer.loottable.entry;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.List;

public interface LootEntry {

    List<LootEntry> roll(); // Returns the result of rolling this entry

    double getChance(); // Used for weighted mode

    /**
     * Apply the loot of this entry
     *
     * @param player The player who "initiated" this appliance of loot, or null if none. Used to run commands
     * @param location The location to drop any loot, or null if none. Used to drop itemstacks.
     */
    void applyLoot(@Nullable Player player, @Nullable Location location);

}
