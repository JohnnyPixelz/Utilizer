package io.github.johnnypixelz.utilizer.config.reference;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ConfigBooleanReference extends ConfigReference {

    public ConfigBooleanReference(@NotNull String file, @NotNull String path) {
        super(file, path);
    }

    public boolean isBoolean() {
        return getConfig().isBoolean(path);
    }

    public boolean get() {
        return getConfig().getBoolean(path);
    }

    public boolean get(boolean defaultValue) {
        return getConfig().getBoolean(path, defaultValue);
    }

    public Optional<Boolean> getIfExists() {
        if (!isBoolean()) return Optional.empty();
        return Optional.of(get());
    }

}
