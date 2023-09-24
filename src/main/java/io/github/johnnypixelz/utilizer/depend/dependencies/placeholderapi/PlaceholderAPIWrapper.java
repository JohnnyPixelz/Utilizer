package io.github.johnnypixelz.utilizer.depend.dependencies.placeholderapi;

import io.github.johnnypixelz.utilizer.depend.dependencies.placeholderapi.callback.*;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

public class PlaceholderAPIWrapper {
    private static Expansion expansion;

    private void initializeExpansion() {
        expansion = new Expansion();
        expansion.register();
    }

    @Nonnull
    public PlaceholderAPIWrapper registerPlaceholder(@Nonnull String params, @Nonnull PlaceholderCallback callback) {
        if (expansion == null) {
            initializeExpansion();
        }

        expansion.getPlaceholderMap().put(params, callback);
        return this;
    }

    @Nonnull
    public PlaceholderAPIWrapper registerParameterizedPlaceholder(@Nonnull String params, @Nonnull ParameterizedPlaceholderCallback callback) {
        if (expansion == null) {
            initializeExpansion();
        }

        expansion.getParameterizedPlaceholderMap().put(params, callback);
        return this;
    }

    @Nonnull
    public PlaceholderAPIWrapper registerRelationalPlaceholder(@Nonnull String params, @Nonnull RelationalPlaceholderCallback callback) {
        if (expansion == null) {
            initializeExpansion();
        }

        expansion.getRelationalPlaceholderMap().put(params, callback);
        return this;
    }

    @Nonnull
    public PlaceholderAPIWrapper registerParameterizedRelationalPlaceholder(@Nonnull String params, @Nonnull ParameterizedRelationalPlaceholderCallback callback) {
        if (expansion == null) {
            initializeExpansion();
        }

        expansion.getParameterizedRelationalPlaceholderMap().put(params, callback);
        return this;
    }

    @Nonnull
    public PlaceholderAPIWrapper registerSystemPlaceholder(@Nonnull String params, @Nonnull SystemPlaceholderCallback callback) {
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

    @Nonnull
    public String setPlaceholders(@Nonnull Player player, @Nonnull String text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }

    @Nonnull
    public List<String> setPlaceholders(@Nonnull Player player, @Nonnull List<String> text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }

    @Nonnull
    public String setPlaceholders(@Nonnull OfflinePlayer player, @Nonnull String text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }

    @Nonnull
    public List<String> setPlaceholders(@Nonnull OfflinePlayer player, @Nonnull List<String> text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }

    @Nonnull
    public String setRelationalPlaceholders(@Nonnull Player one, @Nonnull Player two, @Nonnull String text) {
        return PlaceholderAPI.setRelationalPlaceholders(one, two, text);
    }

    @Nonnull
    public List<String> setRelationalPlaceholders(@Nonnull Player one, @Nonnull Player two, @Nonnull List<String> text) {
        return PlaceholderAPI.setRelationalPlaceholders(one, two, text);
    }

}
