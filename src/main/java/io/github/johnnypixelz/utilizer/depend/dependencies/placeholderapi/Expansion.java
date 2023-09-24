package io.github.johnnypixelz.utilizer.depend.dependencies.placeholderapi;

import io.github.johnnypixelz.utilizer.depend.dependencies.placeholderapi.callback.*;
import io.github.johnnypixelz.utilizer.plugin.Provider;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Expansion extends PlaceholderExpansion implements Relational {
    private final HashMap<String, PlaceholderCallback> placeholders;
    private final HashMap<String, ParameterizedPlaceholderCallback> parameterizedPlaceholders;
    private final HashMap<String, RelationalPlaceholderCallback> relationalPlaceholders;
    private final HashMap<String, ParameterizedRelationalPlaceholderCallback> parameterizedRelationalPlaceholders;
    private final HashMap<String, SystemPlaceholderCallback> systemPlaceholders;

    public Expansion() {
        placeholders = new HashMap<>();
        parameterizedPlaceholders = new HashMap<>();
        relationalPlaceholders = new HashMap<>();
        parameterizedRelationalPlaceholders = new HashMap<>();
        systemPlaceholders = new HashMap<>();
    }

    public HashMap<String, PlaceholderCallback> getPlaceholderMap() {
        return placeholders;
    }

    public HashMap<String, ParameterizedPlaceholderCallback> getParameterizedPlaceholderMap() {
        return parameterizedPlaceholders;
    }

    public HashMap<String, RelationalPlaceholderCallback> getRelationalPlaceholderMap() {
        return relationalPlaceholders;
    }

    public HashMap<String, ParameterizedRelationalPlaceholderCallback> getParameterizedRelationalPlaceholderMap() {
        return parameterizedRelationalPlaceholders;
    }

    public HashMap<String, SystemPlaceholderCallback> getSystemPlaceholderMap() {
        return systemPlaceholders;
    }

    @Override
    public @Nonnull List<String> getPlaceholders() {
        return Collections.unmodifiableList(new ArrayList<>(placeholders.keySet()));
    }

    @Override
    public @Nonnull String getIdentifier() {
        return Provider.getPlugin().getName().toLowerCase();
    }

    @Override
    public @Nonnull String getAuthor() {
        List<String> authors = Provider.getPlugin().getDescription().getAuthors();
        if (authors.isEmpty()) {
            return "N/A";
        } else {
            return String.join(", ", authors);
        }
    }

    @Override
    public @Nonnull String getVersion() {
        return Provider.getPlugin().getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(@Nullable Player player, @Nonnull String params) {
        SystemPlaceholderCallback systemPlaceholderCallback = systemPlaceholders.get(params);
        if (systemPlaceholderCallback != null) return systemPlaceholderCallback.run();

        if (player == null) return "";

        PlaceholderCallback placeholderCallback = placeholders.get(params);
        if (placeholderCallback != null) return placeholderCallback.run(player);

        for (String param : parameterizedPlaceholders.keySet()) {
            if (!params.startsWith(param)) continue;

            params = params.length() == param.length() ? "" : params.substring(param.length() + 1);

            ParameterizedPlaceholderCallback callback = parameterizedPlaceholders.get(param);
            return callback.run(player, params);
        }

        return null;
    }

    @Override
    public String onPlaceholderRequest(@Nullable Player player, @Nullable Player otherPlayer, @Nonnull String params) {
        if (player == null || otherPlayer == null) return "";

        RelationalPlaceholderCallback relationalPlaceholderCallback = relationalPlaceholders.get(params);
        if (relationalPlaceholderCallback != null) return relationalPlaceholderCallback.run(player, otherPlayer);

        final String[] splitParams = params.split("_");
        String firstParam = splitParams[0];

        for (String param : parameterizedRelationalPlaceholders.keySet()) {
            if (!firstParam.equalsIgnoreCase(param)) continue;

            params = splitParams.length == 1 ? "" : params.substring(firstParam.length() + 1);

            ParameterizedRelationalPlaceholderCallback callback = parameterizedRelationalPlaceholders.get(param);
            return callback.run(player, otherPlayer, params);
        }

        return null;
    }
}