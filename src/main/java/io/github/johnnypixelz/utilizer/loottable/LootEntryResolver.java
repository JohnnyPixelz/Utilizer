package io.github.johnnypixelz.utilizer.loottable;

import io.github.johnnypixelz.utilizer.loottable.entry.LootEntry;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nullable;

public interface LootEntryResolver {

    @Nullable
    LootEntry resolve(@Nullable ConfigurationSection section);

}
