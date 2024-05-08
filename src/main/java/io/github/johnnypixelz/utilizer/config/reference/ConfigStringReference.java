package io.github.johnnypixelz.utilizer.config.reference;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class ConfigStringReference extends ConfigReference {

    public ConfigStringReference(@NotNull String file, @NotNull String path) {
        super(file, path);
    }

    public boolean isString() {
        return getConfig().isString(path);
    }

    @Nullable
    public String get() {
        return getConfig().getString(path);
    }

    @Nonnull
    public String get(String defaultValue) {
        return getConfig().getString(path, defaultValue);
    }

    public Optional<String> getIfExists() {
        if (!isString()) return Optional.empty();
        return Optional.ofNullable(get());
    }

}
