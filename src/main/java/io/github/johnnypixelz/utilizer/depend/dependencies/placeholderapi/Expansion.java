package io.github.johnnypixelz.utilizer.depend.dependencies.placeholderapi;

import io.github.johnnypixelz.utilizer.plugin.Provider;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Expansion extends PlaceholderExpansion implements Relational {
    private final HashMap<String, ExpansionCallback> placeholders;
    private final HashMap<String, RelationalExpansionCallback> relationalPlaceholders;

    public Expansion() {
        placeholders = new HashMap<>();
        relationalPlaceholders = new HashMap<>();
    }

    public HashMap<String, ExpansionCallback> getPlaceholderMap() {
        return placeholders;
    }

    public HashMap<String, RelationalExpansionCallback> getRelationalPlaceholderMap() {
        return relationalPlaceholders;
    }

    @Override
    public @NotNull List<String> getPlaceholders() {
        return Collections.unmodifiableList(new ArrayList<>(placeholders.keySet()));
    }

    @Override
    public @NotNull String getIdentifier() {
        return Provider.getPlugin().getName().toLowerCase();
    }

    @Override
    public @NotNull String getAuthor() {
        List<String> authors = Provider.getPlugin().getDescription().getAuthors();
        if (authors.isEmpty()) {
            return "N/A";
        } else {
            return String.join(", ", authors);
        }
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
    public String onPlaceholderRequest(@Nullable Player player, @NotNull String params) {
        for (String param : placeholders.keySet()) {
            if (!params.startsWith(param)) continue;

            params = params.length() == param.length() ? "" : params.substring(param.length() + 1);

            ExpansionCallback callback = placeholders.get(param);
            return callback.run(player, params);
        }

        return null;
    }

    @Override
    public String onPlaceholderRequest(@Nullable Player player, @Nullable Player otherPlayer, @NotNull String params) {
        for (String param : placeholders.keySet()) {
            if (!params.startsWith(param)) continue;

            params = params.length() == param.length() ? "" : params.substring(param.length() + 1);

            RelationalExpansionCallback callback = relationalPlaceholders.get(param);
            return callback.run(player, otherPlayer, params);
        }

        return null;
    }
}