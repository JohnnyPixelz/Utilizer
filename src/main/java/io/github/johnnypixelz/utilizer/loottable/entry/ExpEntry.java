package io.github.johnnypixelz.utilizer.loottable.entry;

import io.github.johnnypixelz.utilizer.amount.Amount;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ExpEntry implements LootEntry {

    public static Optional<ExpEntry> parse(@Nullable ConfigurationSection section) {
        if (section == null) return Optional.empty();

        String amountString = section.getString("amount", "1");
        Amount parsedAmount = Amount.parse(amountString);

        double chance = section.getDouble("chance", 1);

        return Optional.of(new ExpEntry(parsedAmount, chance));
    }

    private final Amount amount;
    private final double chance;

    public ExpEntry(Amount amount, double chance) {
        this.amount = amount;
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

    public Amount getAmount() {
        return amount;
    }

    @Override
    public void giveLoot(Player player) {
        int expAmount = amount.getAmount();
        PlayerExpChangeEvent playerExpChangeEvent = new PlayerExpChangeEvent(player, expAmount);
        Bukkit.getPluginManager().callEvent(playerExpChangeEvent);

        int newAmount = playerExpChangeEvent.getAmount();
        if (newAmount <= 0) return;

        player.giveExp(newAmount);
    }

    @Override
    public void dropLootAt(Location location) {
        if (location == null) return;

        World world = location.getWorld();
        if (world == null) return;

        ExperienceOrb experienceOrb = world.spawn(location, ExperienceOrb.class);
        experienceOrb.setExperience(amount.getAmount());
    }

}
