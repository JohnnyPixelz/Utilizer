package io.github.johnnypixelz.utilizer.config.reference;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ConfigIntReference extends ConfigReference {

    public ConfigIntReference(@NotNull String file, @NotNull String path) {
        super(file, path);
    }

    public boolean isInt() {
        return getConfig().isInt(path);
    }

    public int get() {
        return getConfig().getInt(path);
    }

    public int get(int defaultValue) {
        return getConfig().getInt(path, defaultValue);
    }

    public Optional<Integer> getIfExists() {
        if (!isInt()) return Optional.empty();
        return Optional.of(get());
    }

}
