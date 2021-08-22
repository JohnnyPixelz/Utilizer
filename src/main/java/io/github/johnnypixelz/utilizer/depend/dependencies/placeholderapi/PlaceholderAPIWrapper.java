package io.github.johnnypixelz.utilizer.depend.dependencies.placeholderapi;

import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIWrapper {
    private static Expansion expansion;

    @NotNull
    public PlaceholderAPIWrapper registerPlaceholder(@NotNull String params, @NotNull ExpansionCallback callback) {
        if (expansion == null) {
            expansion = new Expansion();
        }

        expansion.getPlaceholderMap().put(params, callback);
        return this;
    }

    @NotNull
    public PlaceholderAPIWrapper registerRelationalPlaceholder(@NotNull String params, @NotNull RelationalExpansionCallback callback) {
        if (expansion == null) {
            expansion = new Expansion();
        }

        expansion.getRelationalPlaceholderMap().put(params, callback);
        return this;
    }

}
