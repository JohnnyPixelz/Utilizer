package io.github.johnnypixelz.utilizer.inventories.inventories;

import io.github.johnnypixelz.utilizer.inventories.CustomInventory;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nullable;

public class ConfigInventory extends CustomInventory {

    public static ConfigInventory from(@Nullable String configFile, @Nullable String configPath) {
        final ConfigInventory configInventory = new ConfigInventory();
        configInventory.config(configFile, configPath);

        return configInventory;
    }

    public static ConfigInventory from(@Nullable ConfigurationSection configurationSection) {
        final ConfigInventory configInventory = new ConfigInventory();
        configInventory.config(configurationSection);

        return configInventory;
    }

}
