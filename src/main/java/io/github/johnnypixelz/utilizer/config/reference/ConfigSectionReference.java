package io.github.johnnypixelz.utilizer.config.reference;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;

public class ConfigSectionReference extends ConfigReference {

    public ConfigSectionReference(@NotNull String file, @NotNull String path) {
        super(file, path);
    }

    public boolean isSection() {
        return getConfig().isConfigurationSection(path);
    }

    @Nullable
    public ConfigurationSection get() {
        return getConfig().getConfigurationSection(path);
    }

    public Optional<ConfigurationSection> getIfExists() {
        if (!isSection()) return Optional.empty();
        return Optional.ofNullable(get());
    }

}
