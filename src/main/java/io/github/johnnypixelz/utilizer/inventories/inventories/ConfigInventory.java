package io.github.johnnypixelz.utilizer.inventories.inventories;

import io.github.johnnypixelz.utilizer.inventories.CustomInventory;
import io.github.johnnypixelz.utilizer.inventories.config.InventoryConfig;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ConfigInventory extends CustomInventory {

    public static ConfigInventory from(@Nullable String configFile, @Nullable String configPath) {
        final InventoryConfig inventoryConfig = InventoryConfig.parse(configFile, configPath);
        return new ConfigInventory(inventoryConfig);
    }

    public static ConfigInventory from(@Nullable ConfigurationSection configurationSection) {
        final InventoryConfig inventoryConfig = InventoryConfig.parse(configurationSection);
        return new ConfigInventory(inventoryConfig);
    }

    private final InventoryConfig inventoryConfig;

    public ConfigInventory(@Nonnull InventoryConfig inventoryConfig) {
        this.inventoryConfig = inventoryConfig;
    }

    @Override
    protected void onLoad() {
        config(inventoryConfig);
    }

}
