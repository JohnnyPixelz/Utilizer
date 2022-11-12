package io.github.johnnypixelz.utilizer.config;

import io.github.johnnypixelz.utilizer.plugin.Provider;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class Configs {
    private static final HashMap<String, Config> configMap = new HashMap<>();

    @NotNull
    public static FileConfiguration getDefault() {
        return load("config").getConfig();
    }

    @NotNull
    public static UtilConfiguration get(@NotNull String config) {
        return load(config).getConfig();
    }

    @NotNull
    public static Collection<Config> getConfigs() {
        return Collections.unmodifiableCollection(configMap.values());
    }

    @NotNull
    public static Config load(@NotNull String config) {
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
}
