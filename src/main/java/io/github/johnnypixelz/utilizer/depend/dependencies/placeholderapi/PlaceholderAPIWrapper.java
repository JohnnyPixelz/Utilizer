package io.github.johnnypixelz.utilizer.depend.dependencies.placeholderapi;

import io.github.johnnypixelz.utilizer.depend.dependencies.placeholderapi.callback.*;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlaceholderAPIWrapper {
    private static Expansion expansion;

    private void initializeExpansion() {
        expansion = new Expansion();
        expansion.register();
    }

    @NotNull
    public PlaceholderAPIWrapper registerPlaceholder(@NotNull String params, @NotNull PlaceholderCallback callback) {
        if (expansion == null) {
            initializeExpansion();
        }

        expansion.getPlaceholderMap().put(params, callback);
        return this;
    }

    @NotNull
    public PlaceholderAPIWrapper registerParameterizedPlaceholder(@NotNull String params, @NotNull ParameterizedPlaceholderCallback callback) {
        if (expansion == null) {
            initializeExpansion();
        }

        expansion.getParameterizedPlaceholderMap().put(params, callback);
        return this;
    }

    @NotNull
    public PlaceholderAPIWrapper registerRelationalPlaceholder(@NotNull String params, @NotNull RelationalPlaceholderCallback callback) {
        if (expansion == null) {
            initializeExpansion();
        }

        expansion.getRelationalPlaceholderMap().put(params, callback);
        return this;
    }

    @NotNull
    public PlaceholderAPIWrapper registerParameterizedRelationalPlaceholder(@NotNull String params, @NotNull ParameterizedRelationalPlaceholderCallback callback) {
        if (expansion == null) {
            initializeExpansion();
        }

        expansion.getParameterizedRelationalPlaceholderMap().put(params, callback);
        return this;
    }

    @NotNull
    public PlaceholderAPIWrapper registerSystemPlaceholder(@NotNull String params, @NotNull SystemPlaceholderCallback callback) {
        if (expansion == null) {
            initializeExpansion();
        }

        expansion.getSystemPlaceholderMap().put(params, callback);
        return this;
    }

    public PlaceholderAPIWrapper resetRegisteredPlaceholders() {
        if (expansion != null) {
            expansion.getSystemPlaceholderMap().clear();
            expansion.getPlaceholderMap().clear();
            expansion.getParameterizedPlaceholderMap().clear();
            expansion.getRelationalPlaceholderMap().clear();
            expansion.getParameterizedRelationalPlaceholderMap().clear();
        }

        return this;
    }

    @NotNull
    public String setPlaceholders(@NotNull Player player, @NotNull String text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }

    @NotNull
    public List<String> setPlaceholders(@NotNull Player player, @NotNull List<String> text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }

    @NotNull
    public String setPlaceholders(@NotNull OfflinePlayer player, @NotNull String text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }

    @NotNull
    public List<String> setPlaceholders(@NotNull OfflinePlayer player, @NotNull List<String> text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }

    @NotNull
    public String setRelationalPlaceholders(@NotNull Player one, @NotNull Player two, @NotNull String text) {
        return PlaceholderAPI.setRelationalPlaceholders(one, two, text);
    }

    @NotNull
    public List<String> setRelationalPlaceholders(@NotNull Player one, @NotNull Player two, @NotNull List<String> text) {
        return PlaceholderAPI.setRelationalPlaceholders(one, two, text);
    }

}
