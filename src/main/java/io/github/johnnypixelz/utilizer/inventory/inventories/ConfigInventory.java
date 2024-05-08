package io.github.johnnypixelz.utilizer.inventory.inventories;

import io.github.johnnypixelz.utilizer.config.Configs;
import io.github.johnnypixelz.utilizer.inventory.CustomInventory;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class ConfigInventory extends CustomInventory {

    public static Optional<ConfigInventory> from(@Nullable String configFile, @Nullable String configPath) {
        if (configFile == null || configPath == null) return Optional.empty();

        final ConfigurationSection configurationSection = Configs.get(configFile).getConfigurationSection(configPath);
        if (configurationSection == null) return Optional.empty();

        return Optional.of(new ConfigInventory(configurationSection));
    }

    public static ConfigInventory from(@Nonnull ConfigurationSection section) {
        return new ConfigInventory(section);
    }

    private final ConfigurationSection section;

    private ConfigInventory(ConfigurationSection section) {
        this.section = section;
    }

    @Override
    protected void onLoad() {
        config(section);
    }

}
