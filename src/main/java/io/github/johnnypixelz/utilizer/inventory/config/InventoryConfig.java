package io.github.johnnypixelz.utilizer.inventory.config;

import com.google.common.collect.ImmutableList;
import io.github.johnnypixelz.utilizer.config.Configs;
import io.github.johnnypixelz.utilizer.config.Message;
import io.github.johnnypixelz.utilizer.config.Messages;
import io.github.johnnypixelz.utilizer.config.Parse;
import io.github.johnnypixelz.utilizer.inventory.CustomInventory;
import io.github.johnnypixelz.utilizer.inventory.CustomInventoryType;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InventoryConfig {

    @Nullable
    public static InventoryConfig parse(@Nonnull String configFile, @Nonnull String configPath) {
        final ConfigurationSection configurationSection = Configs.get(configFile).getConfigurationSection(configPath);
        return parse(configurationSection);
    }

    @Nullable
    public static InventoryConfig parse(@Nullable ConfigurationSection configurationSection) {
        final InventoryConfig inventoryConfig = new InventoryConfig();

        if (configurationSection == null) return null;

        inventoryConfig.configurationSection = configurationSection;

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
            final int rows = configurationSection.getInt("size", -1);
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

        final long refresh = configurationSection.getLong("refresh", -1);
        if (refresh != -1) {
            inventoryConfig.refresh = Parse.constrain(1, Long.MAX_VALUE, refresh);
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

    public static void load(CustomInventory customInventory, InventoryConfig inventoryConfig) {
        customInventory.title(inventoryConfig.title);
        customInventory.type(inventoryConfig.customInventoryType);
        customInventory.refresh(inventoryConfig.refresh);
    }

    public static void draw(CustomInventory customInventory, InventoryConfig inventoryConfig) {
        inventoryConfig.inventoryConfigItemMap.forEach((key, configItem) -> {
            try {
                switch (key) {
                    case "border" -> customInventory.getRootPane().fillBorders(() -> configItem.getInventoryItem(customInventory));
                    case "fill" -> customInventory.getRootPane().fill(() -> configItem.getInventoryItem(customInventory));
                    default -> {
                        configItem.getSlot().ifPresent(slot -> {
                            customInventory.getRootPane().setInventoryItem(slot, configItem.getInventoryItem(customInventory));
                        });
                    }
                }
            } catch (Exception exception) {
                throw new IllegalStateException("Error while setting config item %s".formatted(key), exception);
            }
        });
    }

    private String title;
    private CustomInventoryType customInventoryType;
    private Long refresh;
    private final Map<String, InventoryConfigItem> inventoryConfigItemMap;
    private final Map<String, Message> messages;
    private ConfigurationSection configurationSection;

    private InventoryConfig() {
        this.title = null;
        this.customInventoryType = null;
        this.refresh = null;
        this.inventoryConfigItemMap = new HashMap<>();
        this.messages = new HashMap<>();
        this.configurationSection = null;
    }

    public Optional<Message> getMessage(String messageId) {
        return Optional.ofNullable(messages.get(messageId));
    }

    public Optional<InventoryConfigItem> getItem(String itemId) {
        return Optional.ofNullable(inventoryConfigItemMap.get(itemId));
    }

    public List<InventoryConfigItem> getItems() {
        return ImmutableList.copyOf(inventoryConfigItemMap.values());
    }

    public ConfigurationSection getConfigurationSection() {
        return configurationSection;
    }

}
