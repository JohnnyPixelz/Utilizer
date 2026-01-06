package io.github.johnnypixelz.utilizer.config;

import io.github.johnnypixelz.utilizer.event.StatefulEventEmitter;
import io.github.johnnypixelz.utilizer.file.FileWatcher;
import io.github.johnnypixelz.utilizer.plugin.Provider;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public class Config {
    private final File file;
    private final YamlConfiguration config;
    private FileWatcher fileWatcher;
    private final StatefulEventEmitter<File> onSave;
    private final StatefulEventEmitter<Configuration> onReload;

    public Config(File file) {
        this.file = file;
        this.config = YamlConfiguration.loadConfiguration(file);
        this.onReload = new StatefulEventEmitter<>();
        this.onSave = new StatefulEventEmitter<>();
    }

    public Config watch() {
        fileWatcher = FileWatcher.watchFile(file, file -> reload());
        return this;
    }

    public Config watch(Consumer<File> callback) {
        fileWatcher = FileWatcher.watchFile(file, onSave::emit);
        return this;
    }

    public Config unwatch() {
        if (fileWatcher == null) {
            return this;
        }

        fileWatcher.cancel();
        return this;
    }

    public Config onReload(Runnable onReload) {
        this.onReload.listen(yamlConfiguration -> onReload.run());
        return this;
    }

    public Config onReload(Consumer<Configuration> onReload) {
        this.onReload.listen(onReload);
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

        onReload.emit(config);
        return this;
    }

    public Config save() {
        try {
            config.save(file);
        } catch (IOException e) {
            Provider.getPlugin()
                    .getLogger()
                    .severe("Unable to save configuration file " + file.getName());
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
