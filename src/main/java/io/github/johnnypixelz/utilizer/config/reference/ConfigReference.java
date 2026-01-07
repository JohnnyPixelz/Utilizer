package io.github.johnnypixelz.utilizer.config.reference;

import io.github.johnnypixelz.utilizer.config.Configs;
import org.bukkit.configuration.file.YamlConfiguration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Optional;

public abstract class ConfigReference {

    public static ConfigDynamicReference dynamic(String file, String path) {
        return new ConfigDynamicReference(file, path);
    }

    public static Optional<ConfigDynamicReference> parse(@Nullable String input) {
        if (input == null) return Optional.empty();

        final String[] splitInput = input.split(">");

        if (splitInput.length == 2) {
            final String parsedInput1 = splitInput[0].trim();
            final String parsedInput2 = splitInput[1].trim();
            return Optional.of(new ConfigDynamicReference(parsedInput1, parsedInput2));
        } else {
            return Optional.empty();
        }
    }

    public static Optional<ConfigDynamicReference> parse(@Nullable String fileName, @Nullable String input) {
        if (input == null) return Optional.empty();

        final String[] splitInput = input.split(">");

        if (splitInput.length == 1 && fileName != null) {
            final String parsedInput = splitInput[0].trim();
            return Optional.of(new ConfigDynamicReference(fileName, parsedInput));
        } else if (splitInput.length == 2) {
            final String parsedInput1 = splitInput[0].trim();
            final String parsedInput2 = splitInput[1].trim();
            return Optional.of(new ConfigDynamicReference(parsedInput1, parsedInput2));
        } else {
            return Optional.empty();
        }
    }

    protected String file; // Config file path, e.g. "config", "messages"
    protected String path; // Config path, e.g. "gui.help", "messages.on-join"

    protected ConfigReference(@NotNull String file, @NotNull String path) {
        this.file = file;
        this.path = path;
    }

    @NotNull
    public String getFile() {
        return file;
    }

    @NotNull
    public String getPath() {
        return path;
    }

    @NotNull
    public YamlConfiguration getConfig() {
        return Configs.get(file);
    }

    public boolean isSet() {
        return getConfig().isSet(path);
    }

}
