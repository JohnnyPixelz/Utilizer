package io.github.johnnypixelz.utilizer.papi;

import io.github.johnnypixelz.utilizer.plugin.Provider;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Internal PAPI expansion that handles all registered placeholders.
 */
class InternalExpansion extends PlaceholderExpansion implements Relational {

    private final List<RegisteredPlaceholder> placeholders;
    private final List<RegisteredRelational> relationals;

    InternalExpansion(
            @NotNull List<RegisteredPlaceholder> placeholders,
            @NotNull List<RegisteredRelational> relationals
    ) {
        this.placeholders = placeholders;
        this.relationals = relationals;
    }

    @Override
    public @NotNull String getIdentifier() {
        return Provider.getPlugin().getName().toLowerCase();
    }

    @Override
    public @NotNull String getAuthor() {
        List<String> authors = Provider.getPlugin().getDescription().getAuthors();
        if (authors.isEmpty()) {
            return "Unknown";
        }
        return String.join(", ", authors);
    }

    @Override
    public @NotNull String getVersion() {
        return Provider.getPlugin().getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @NotNull List<String> getPlaceholders() {
        return placeholders.stream()
                .map(p -> p.pattern().getTemplate())
                .toList();
    }

    @Override
    @Nullable
    public String onPlaceholderRequest(@Nullable Player player, @NotNull String params) {
        // Try each registered placeholder pattern
        for (RegisteredPlaceholder placeholder : placeholders) {
            Optional<PlaceholderArgs> match = placeholder.pattern().match(params);
            if (match.isPresent()) {
                try {
                    return placeholder.handler().apply(player, match.get());
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    @Nullable
    public String onPlaceholderRequest(@Nullable Player one, @Nullable Player two, @NotNull String params) {
        if (one == null || two == null) {
            return null;
        }

        // Try each registered relational placeholder pattern
        for (RegisteredRelational relational : relationals) {
            Optional<PlaceholderArgs> match = relational.pattern().match(params);
            if (match.isPresent()) {
                try {
                    return relational.handler().apply(one, two, match.get());
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * Internal record for registered placeholders.
     */
    record RegisteredPlaceholder(
            PlaceholderPattern pattern,
            BiFunction<Player, PlaceholderArgs, String> handler
    ) {}

    /**
     * Internal record for registered relational placeholders.
     */
    record RegisteredRelational(
            PlaceholderPattern pattern,
            TriFunction<Player, Player, PlaceholderArgs, String> handler
    ) {}

}
