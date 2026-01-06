package io.github.johnnypixelz.utilizer.config;

import io.github.johnnypixelz.utilizer.plugin.Provider;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class Configs {
    private static final HashMap<String, Config> configMap = new HashMap<>();

    @Nonnull
    public static FileConfiguration getDefault() {
        return load("config").getConfig();
    }

    @Nonnull
    public static YamlConfiguration get(@Nonnull String config) {
        return load(config).getConfig();
    }

    @Nonnull
    public static Collection<Config> getConfigs() {
        return Collections.unmodifiableCollection(configMap.values());
    }

    @Nonnull
    public static Config load(@Nonnull String config) {
        if (!config.endsWith(".yml")) {
            config = config + ".yml";
        }

        if (configMap.containsKey(config)) {
            return configMap.get(config);
        }

        File dataFolder = Provider.getPlugin().getDataFolder();
        dataFolder.mkdirs();

        File file = new File(dataFolder, config);
        if (!file.exists()) {
            try {
                Provider.getPlugin().saveResource(config, false);
            } catch (IllegalArgumentException ignored) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Config configObj = new Config(file);
        configMap.put(config, configObj);
        return configObj;
    }

    public static boolean isLoaded(String configPath) {
        if (!configPath.endsWith(".yml")) {
            configPath += "yml";
        }

        return configMap.containsKey(configPath);
    }

    public static boolean isConfig(String configPath) {
        if (!configPath.endsWith(".yml")) {
            configPath += "yml";
        }

        if (configMap.containsKey(configPath)) return true;

        final File dataFolder = Provider.getPlugin().getDataFolder();
        final File file = new File(dataFolder, configPath);

        return file.exists();
    }

}
