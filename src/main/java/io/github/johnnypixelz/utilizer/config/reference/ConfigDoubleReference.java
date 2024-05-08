package io.github.johnnypixelz.utilizer.config.reference;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ConfigDoubleReference extends ConfigReference {

    public ConfigDoubleReference(@NotNull String file, @NotNull String path) {
        super(file, path);
    }

    public boolean isDouble() {
        return getConfig().isDouble(path);
    }

    public double get() {
        return getConfig().getDouble(path);
    }

    public double get(double defaultValue) {
        return getConfig().getDouble(path, defaultValue);
    }

    public Optional<Double> getIfExists() {
        if (!isDouble()) return Optional.empty();
        return Optional.of(get());
    }

}
