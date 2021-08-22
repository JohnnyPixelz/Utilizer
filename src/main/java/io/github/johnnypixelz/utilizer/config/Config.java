package io.github.johnnypixelz.utilizer.config;

import io.github.johnnypixelz.utilizer.file.FileWatcher;
import io.github.johnnypixelz.utilizer.plugin.Provider;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {
    private final File file;
    private final YamlConfiguration config;
    private FileWatcher fileWatcher;

    public Config(File file) {
        this.file = file;
        config = YamlConfiguration.loadConfiguration(file);
    }

    public Config watch() {
        fileWatcher = FileWatcher.watchFile(file, file -> reload());
        return this;
    }

    public Config unwatch() {
        if (fileWatcher == null) {
            return this;
        }

        fileWatcher.cancel();
        return this;
    }

    public Config reload() {
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            Provider.getPlugin()
                    .getLogger()
                    .severe("Unable to load configuration file " + file.getName());
            e.printStackTrace();
        }

        return this;
    }

    public File getFile() {
        return file;
    }

    public YamlConfiguration getConfig() {
        return config;
    }
}