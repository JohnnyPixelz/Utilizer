package io.github.johnnypixelz.utilizer.loottable;

import io.github.johnnypixelz.utilizer.loottable.entry.CommandEntry;
import io.github.johnnypixelz.utilizer.loottable.entry.ExpEntry;
import io.github.johnnypixelz.utilizer.loottable.entry.ItemEntry;
import io.github.johnnypixelz.utilizer.loottable.entry.LootEntry;
import org.bukkit.configuration.ConfigurationSection;

import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LootTables {
    private static final Map<String, LootEntryResolver> lootEntryMapResolver = new HashMap<>();

    static {
        registerLootEntryResolver("LOOT_TABLE", LootTable::parse);
        registerLootEntryResolver("ITEM", ItemEntry::parse);
        registerLootEntryResolver("COMMAND", section -> CommandEntry.parse(section).orElse(null));
        registerLootEntryResolver("XP", section -> ExpEntry.parse(section).orElse(null));
        registerLootEntryResolver("EXP", section -> ExpEntry.parse(section).orElse(null));
    }

    public static void registerLootEntryResolver(String id, LootEntryResolver lootEntryResolver) {
        lootEntryMapResolver.put(id.toLowerCase(), lootEntryResolver);
    }

    public static Optional<LootEntry> resolveEntry(@Nullable ConfigurationSection section) {
        if (section == null) return Optional.empty();

        String type = section.getString("type");
        if (type == null) return Optional.empty();

        LootEntryResolver lootEntryResolver = lootEntryMapResolver.get(type.toLowerCase());
        if (lootEntryResolver == null) return Optional.empty();

        LootEntry resolvedLootEntry = lootEntryResolver.resolve(section);

        return Optional.ofNullable(resolvedLootEntry);
    }

    public static LootTable parseLootTable(@Nullable ConfigurationSection section) {
        return LootTable.parse(section);
    }

}
