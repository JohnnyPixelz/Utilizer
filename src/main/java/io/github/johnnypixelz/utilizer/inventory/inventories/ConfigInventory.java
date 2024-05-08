package io.github.johnnypixelz.utilizer.inventory.inventories;

import io.github.johnnypixelz.utilizer.inventory.CustomInventory;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;

public class ConfigInventory extends CustomInventory {

    private final ConfigurationSection section;

    public ConfigInventory(@Nonnull ConfigurationSection section) {
        this.section = section;
    }

    @Override
    protected void onLoad() {
        config(section);
    }

}
