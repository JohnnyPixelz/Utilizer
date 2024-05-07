package io.github.johnnypixelz.utilizer.inventory.parser;

import io.github.johnnypixelz.utilizer.config.Configs;
import io.github.johnnypixelz.utilizer.config.Message;
import io.github.johnnypixelz.utilizer.config.Messages;
import io.github.johnnypixelz.utilizer.config.Parse;
import io.github.johnnypixelz.utilizer.inventory.CustomInventory;
import io.github.johnnypixelz.utilizer.inventory.CustomInventoryType;
import io.github.johnnypixelz.utilizer.inventory.items.SimpleItem;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InventoryConfig {

    public static InventoryConfig parse(@Nonnull String configFile, @Nonnull String configPath) {
        final ConfigurationSection configurationSection = Configs.get(configFile).getConfigurationSection(configPath);
        return parse(configurationSection);
    }

    public static InventoryConfig parse(@Nullable ConfigurationSection configurationSection) {
        final InventoryConfig inventoryConfig = new InventoryConfig();

        if (configurationSection == null) return inventoryConfig;

        boolean initializedType = false;

        final String inventoryType = configurationSection.getString("type");
        if (inventoryType != null) {
            try {
                inventoryConfig.customInventoryType = CustomInventoryType.valueOf(inventoryType.toUpperCase());
                initializedType = true;
            } catch (Exception ignored) {
            }
        }

        if (!initializedType) {
            final int rows = configurationSection.getInt("rows", -1);
            if (rows != -1) {
                final int rowConstraint = Parse.constrain(1, 6, rows);
                try {
                    inventoryConfig.customInventoryType = CustomInventoryType.valueOf("CHEST_" + rowConstraint);
                    initializedType = true;
                } catch (Exception ignored) {
                }
            }
        }

        if (!initializedType) {
            inventoryConfig.customInventoryType = CustomInventoryType.CHEST_3;
            initializedType = true;
        }

        final String title = configurationSection.getString("title");
        if (title != null) {
            inventoryConfig.title = title;
        }

        final ConfigurationSection itemsSection = configurationSection.getConfigurationSection("items");
        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                final ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);
                if (itemSection == null) continue;

                final InventoryConfigItem parsedConfigItem = InventoryConfigItem.parse(itemSection);
                inventoryConfig.inventoryConfigItemMap.put(key, parsedConfigItem);
            }
        }

        final ConfigurationSection messagesSection = configurationSection.getConfigurationSection("messages");
        if (messagesSection != null) {
            for (String messageSectionKey : messagesSection.getKeys(false)) {
                final Message parsedMessage = Messages.parse(messagesSection, messageSectionKey);
                inventoryConfig.messages.put(messageSectionKey, parsedMessage);
            }
        }

        return inventoryConfig;
    }

    private String title;
    private CustomInventoryType customInventoryType;
    private final Map<String, InventoryConfigItem> inventoryConfigItemMap;
    private final Map<String, Message> messages;

    private InventoryConfig() {
        this.title = null;
        this.customInventoryType = null;
        this.inventoryConfigItemMap = new HashMap<>();
        this.messages = new HashMap<>();
    }

    public Optional<Message> getMessage(String messageId) {
        return Optional.ofNullable(messages.get(messageId));
    }

    public void load(CustomInventory customInventory) {
        customInventory.title(title);
        customInventory.type(customInventoryType);
    }

    public void draw(CustomInventory customInventory) {
        inventoryConfigItemMap.forEach((key, configItem) -> {
            try {
                switch (key) {
                    case "border" ->
                            customInventory.getRootPane().fillBorders(() -> new SimpleItem(configItem.getItemStack()));
                    case "fill" -> customInventory.getRootPane().fill(() -> new SimpleItem(configItem.getItemStack()));
                    default -> {
                        configItem.getSlot().ifPresent(slot -> {
                            customInventory.getRootPane().setInventoryItem(slot, new SimpleItem(configItem.getItemStack()));
                        });
                    }
                }
            } catch (Exception exception) {
                throw new IllegalStateException("Error while setting config item %s".formatted(key), exception);
            }
        });
    }

    public Optional<InventoryConfigItem> getConfigItem(String itemId) {
        return Optional.ofNullable(inventoryConfigItemMap.get(itemId));
    }

}
