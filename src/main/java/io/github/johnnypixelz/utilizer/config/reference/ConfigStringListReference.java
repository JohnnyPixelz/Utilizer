package io.github.johnnypixelz.utilizer.config.reference;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class ConfigStringListReference extends ConfigReference {

    public ConfigStringListReference(@NotNull String file, @NotNull String path) {
        super(file, path);
    }

    public boolean isStringList() {
        return isSet() && getConfig().isList(path);
    }

    public List<String> get() {
        return getConfig().getStringList(path);
    }

    public List<String> get(List<String> defaultValue) {
        return isStringList() ? get() : defaultValue;
    }

    public Optional<List<String>> getIfExists() {
        if (!isStringList()) return Optional.empty();
        return Optional.of(get());
    }

}
