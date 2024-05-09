package io.github.johnnypixelz.utilizer.config;

import com.cryptomorin.xseries.XMaterial;
import io.github.johnnypixelz.utilizer.itemstack.Items;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;

@Deprecated
public class UtilConfiguration extends YamlConfiguration {

    public static UtilConfiguration loadConfiguration(File file) {
        Objects.requireNonNull(file, "File cannot be null");

        UtilConfiguration config = new UtilConfiguration();

        try {
            config.load(file);
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
        } catch (InvalidConfigurationException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
        }

        return config;
    }

    @Override
    public ItemStack getItemStack(String path) {
        if (!isConfigurationSection(path)) return null;
        final ConfigurationSection section = getConfigurationSection(path);

        String materialString;
        if (section.isString("type")) {
            materialString = section.getString("type");
        } else if (section.isString("material")) {
            materialString = section.getString("material");
        } else return null;

        final Optional<XMaterial> xMaterial = XMaterial.matchXMaterial(materialString);
        if (!xMaterial.isPresent()) return null;

        final ItemStack stack = xMaterial.get().parseItem();
        Objects.requireNonNull(stack);

        if (section.isString("name")) {
            Items.setDisplayName(stack, section.getString("name"));
        } else if (section.isString("displayname")) {
            Items.setDisplayName(stack, section.getString("displayname"));
        }

        if (section.isList("lore")) {
            Items.setLore(stack, section.getStringList("lore"));
        }

        if (section.isBoolean("glow")) {
            final boolean glow = section.getBoolean("glow");
            Items.setGlow(stack, glow);
        }

        if (section.isSet("amount")) {
            final int amount = section.getInt("amount");
            if (amount >= 1 && amount <= 64) {
                stack.setAmount(amount);
            }
        }

//        TODO implement 1.9+ features when I update the lib
        if (section.isSet("custom-model-data")) {
            final int data = section.getInt("custom-model-data");
            Items.meta(stack, itemMeta -> itemMeta.setCustomModelData(data));
        }

        return stack;
    }
}
