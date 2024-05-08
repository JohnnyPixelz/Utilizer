package io.github.johnnypixelz.utilizer.config.reference;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ConfigLongReference extends ConfigReference {

    public ConfigLongReference(@NotNull String file, @NotNull String path) {
        super(file, path);
    }

    public boolean isLong() {
        return getConfig().isLong(path);
    }

    public long get() {
        return getConfig().getLong(path);
    }

    public long get(long defaultValue) {
        return getConfig().getLong(path, defaultValue);
    }

    public Optional<Long> getIfExists() {
        if (!isLong()) return Optional.empty();
        return Optional.of(get());
    }

}
