package io.github.johnnypixelz.utilizer.config.reference;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ConfigDynamicReference extends ConfigReference {

    public ConfigDynamicReference(@NotNull String file, @NotNull String path) {
        super(file, path);
    }

    public boolean isSection() {
        return getConfig().isConfigurationSection(path);
    }

    public boolean isInt() {
        return getConfig().isInt(path);
    }

    public boolean isLong() {
        return getConfig().isLong(path);
    }

    public boolean isDouble() {
        return getConfig().isDouble(path);
    }

    public boolean isString() {
        return getConfig().isString(path);
    }

    public boolean isList() {
        return getConfig().isList(path);
    }

    public ConfigSectionReference getAsSection() {
        return new ConfigSectionReference(file, path);
    }

    public ConfigIntReference getAsInt() {
        return new ConfigIntReference(file, path);
    }

    public ConfigLongReference getAsLong() {
        return new ConfigLongReference(file, path);
    }

    public ConfigDoubleReference getAsDouble() {
        return new ConfigDoubleReference(file, path);
    }

    public ConfigBooleanReference getAsBoolean() {
        return new ConfigBooleanReference(file, path);
    }

    public ConfigStringReference getAsString() {
        return new ConfigStringReference(file, path);
    }
    
    public ConfigStringListReference getAsStringList() {
        return new ConfigStringListReference(file, path);
    }

}
