package io.github.johnnypixelz.utilizer.loottable;

import io.github.johnnypixelz.utilizer.loottable.entry.LootEntry;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * LootTable Configuration Schema:
 *
 * loot-table:
 *   mode: INDEPENDENT          # Required. INDEPENDENT (each entry rolls independently) or WEIGHTED (pick one based on weights)
 *   chance: 1.0                # Optional. Chance this entire loot table rolls (0.0-1.0). Default: 1.0
 *   rolls: 1                   # Optional. Number of times to roll this loot table. Default: 1
 *   entries:                   # Required. Map of loot entries
 *     entry-name:
 *       type: ITEM             # Required. Entry type: ITEM, COMMAND, EXP/XP, or LOOT_TABLE (nested)
 *       chance: 0.5            # Optional. Chance this entry is selected (0.0-1.0). Default: 1.0
 *
 *       # For type: ITEM
 *       material: DIAMOND      # Required for ITEM. Material type
 *       amount: "1-3"          # Optional. Amount (supports ranges like "1-3" or fixed "5"). Default: "1"
 *       name: "&aCustom Name"  # Optional. Display name (supports color codes)
 *       lore:                  # Optional. Lore lines
 *         - "&7Line 1"
 *         - "&7Line 2"
 *       glow: true             # Optional. Make item glow. Default: false
 *       custom-model-data: 1   # Optional. Custom model data value
 *       # ... other ItemStack properties supported by Items.parse()
 *
 *       # For type: COMMAND
 *       command: "give %player_name% diamond 1"  # Required for COMMAND. Console command to execute
 *
 *       # For type: EXP or XP
 *       amount: "30-50"        # Required for EXP. Experience amount (supports ranges)
 *
 *       # For type: LOOT_TABLE (nested loot tables)
 *       mode: INDEPENDENT      # Nested loot table follows same schema
 *       entries:
 *         # ... nested entries
 *
 * Modes:
 *   - INDEPENDENT: Each entry in the loot table rolls independently based on its chance
 *   - WEIGHTED: One entry is selected based on weighted probabilities (chance values act as weights)
 *
 * Example:
 *   my-loot:
 *     mode: INDEPENDENT
 *     rolls: 2
 *     entries:
 *       diamonds:
 *         type: ITEM
 *         material: DIAMOND
 *         amount: "1-3"
 *         chance: 0.5
 *       experience:
 *         type: EXP
 *         amount: "30-50"
 *         chance: 0.8
 */
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

    @Override
    public void giveLoot(Player player) {
        roll().forEach(lootEntry -> lootEntry.giveLoot(player));
    }

    @Override
    public void dropLootAt(Location location) {
        roll().forEach(lootEntry -> lootEntry.dropLootAt(location));
    }

    @Override
    public void dropLootAt(Player player, Location dropLocation) {
        roll().forEach(lootEntry -> lootEntry.dropLootAt(player, dropLocation));
    }

}
