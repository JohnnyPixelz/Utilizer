package io.github.johnnypixelz.utilizer.loottable.entry;

import io.github.johnnypixelz.utilizer.depend.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class CommandEntry implements LootEntry {

    public static Optional<CommandEntry> parse(@Nullable ConfigurationSection section) {
        if (section == null) return Optional.empty();

        String command = section.getString("command");
        double chance = section.getDouble("chance", 1);

        return Optional.of(new CommandEntry(command, chance));
    }

    private final String command;
    private final double chance;

    public CommandEntry(String command, double chance) {
        this.command = command;
        this.chance = chance;
    }

    @Override
    public @NotNull List<LootEntry> roll() {
        return List.of(this);
    }

    @Override
    public double getChance() {
        return chance;
    }

    public String getCommand() {
        return command;
    }

    @Override
    public void giveLoot(Player player) {
        String placeholderedCommand = Placeholders.set(player, command);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), placeholderedCommand);
    }

    @Override
    public void dropLootAt(Location location) {
        // Do nothing
    }

    @Override
    public void dropLootAt(Player player, Location location) {
        String placeholderedCommand = Placeholders.set(player, command);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), placeholderedCommand);
    }

}
