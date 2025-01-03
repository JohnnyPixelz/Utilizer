package io.github.johnnypixelz.utilizer.loottable;

import io.github.johnnypixelz.utilizer.depend.Placeholders;
import io.github.johnnypixelz.utilizer.inventory.Inventories;
import io.github.johnnypixelz.utilizer.itemstack.Items;
import io.github.johnnypixelz.utilizer.loottable.entry.CommandEntry;
import io.github.johnnypixelz.utilizer.loottable.entry.ItemEntry;
import io.github.johnnypixelz.utilizer.loottable.entry.LootEntry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class LootTable implements LootEntry {

    public static LootTable parse(@Nullable ConfigurationSection section) {
        if (section == null) {
            return new LootTable();
        }

        String modeString = section.getString("mode", "INDEPENDENT").toLowerCase();

        LootTableMode lootTableMode = switch (modeString) {
            case "independent" -> LootTableMode.INDEPENDENT;
            case "weighted" -> LootTableMode.WEIGHTED;
            default -> LootTableMode.INDEPENDENT;
        };

        double chance = section.getDouble("chance", 1);
        int rolls = section.getInt("rolls", 1);

        List<LootEntry> lootEntries = new ArrayList<>();

        ConfigurationSection entriesSection = section.getConfigurationSection("entries");
        if (entriesSection == null) return new LootTable(lootTableMode, chance, rolls, new ArrayList<>());

        for (String entryKey : entriesSection.getKeys(false)) {
            ConfigurationSection entrySection = entriesSection.getConfigurationSection(entryKey);
            if (entrySection == null) continue;

            LootTables.resolveEntry(entrySection).ifPresent(lootEntries::add);
        }

        return new LootTable(lootTableMode, chance, rolls, lootEntries);
    }

    private final LootTableMode lootTableMode;
    private final List<LootEntry> nestedLootEntries;
    private final int rolls;
    private final double chance;

    public LootTable(LootTableMode lootTableMode, double chance, int rolls, List<LootEntry> nestedLootEntries) {
        this.lootTableMode = lootTableMode;
        this.chance = chance;
        this.rolls = rolls;
        this.nestedLootEntries = nestedLootEntries;
    }

    public LootTable() {
        this.lootTableMode = LootTableMode.INDEPENDENT;
        this.chance = 1;
        this.rolls = 1;
        this.nestedLootEntries = new ArrayList<>();
    }

    public LootTableMode getLootTableMode() {
        return lootTableMode;
    }

    public List<LootEntry> getNestedLootEntries() {
        return nestedLootEntries;
    }

    public int getRolls() {
        return rolls;
    }

    @Override
    public @NotNull List<LootEntry> roll() {
        List<LootEntry> result = new ArrayList<>();

        // Perform the rolls
        for (int i = 0; i < rolls; i++) {
            switch (lootTableMode) {
                case INDEPENDENT -> {
                    // Each nested loot table rolls independently
                    result.addAll(rollIndependent());
                }
                case WEIGHTED -> {
                    // Choose one nested loot table based on weighted chance
                    LootEntry lootEntry = rollWeighted();
                    if (lootEntry != null) {
                        result.addAll(lootEntry.roll());
                    }
                }
            }
        }

        return result;
    }

    private List<LootEntry> rollIndependent() {
        List<LootEntry> results = new ArrayList<>();

        for (LootEntry nestedLootEntry : nestedLootEntries) {
            if (ThreadLocalRandom.current().nextDouble() <= nestedLootEntry.getChance()) {
                results.addAll(nestedLootEntry.roll());
            }
        }

        return results;
    }

    private LootEntry rollWeighted() {
        double totalWeight = nestedLootEntries.stream()
                .mapToDouble(LootEntry::getChance)
                .sum();

        if (totalWeight == 0) {
            return null; // No eligible loot table
        }

        double randomWeight = ThreadLocalRandom.current().nextDouble() * totalWeight;
        double currentWeight = 0;

        for (LootEntry lootEntry : nestedLootEntries) {
            currentWeight += lootEntry.getChance();
            if (randomWeight <= currentWeight) {
                return lootEntry; // Selected loot table
            }
        }

        // This should not happen, but log it if it does for debugging
        throw new IllegalStateException("Weighted roll failed unexpectedly.");
    }

    @Override
    public double getChance() {
        return chance;
    }

    public List<ItemStack> getDroppedItems() {
        return roll().stream()
                .filter(ItemEntry.class::isInstance)
                .map(ItemEntry.class::cast)
                .map(ItemEntry::getGeneratedItemStack)
                .toList();
    }

    public List<String> getDroppedCommands() {
        return roll().stream()
                .filter(CommandEntry.class::isInstance)
                .map(CommandEntry.class::cast)
                .map(CommandEntry::getCommand)
                .toList();
    }

    @Override
    public void giveLoot(Player player) {
        getDroppedItems().stream()
                .map(itemStack -> Items.map(itemStack, text -> Placeholders.set(player, text)))
                .forEach(itemStack -> {
                    Inventories.giveOrDrop(player, itemStack);
                });

        getDroppedCommands().stream()
                .map(command -> Placeholders.set(player, command))
                .forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
    }

    @Override
    public void dropLootAt(Location location) {
        World world = location.getWorld();
        if (world == null) return;

        getDroppedItems()
                .forEach(itemStack -> {
                    world.dropItemNaturally(location, itemStack);
                });
    }

    @Override
    public void dropLootAt(Player player, Location dropLocation) {
        World world = dropLocation.getWorld();

        getDroppedItems().stream()
                .map(itemStack -> Items.map(itemStack, text -> Placeholders.set(player, text)))
                .forEach(itemStack -> {
                    if (world == null) {
                        Inventories.giveOrDrop(player, itemStack);
                    } else {
                        world.dropItemNaturally(dropLocation, itemStack);
                    }
                });

        getDroppedCommands().stream()
                .map(command -> Placeholders.set(player, command))
                .forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
    }

}