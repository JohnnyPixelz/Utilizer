package io.github.johnnypixelz.utilizer.inventory.config;

import io.github.johnnypixelz.utilizer.config.Message;
import io.github.johnnypixelz.utilizer.config.Messages;
import io.github.johnnypixelz.utilizer.inventory.CustomInventory;
import io.github.johnnypixelz.utilizer.inventory.InventoryItem;
import io.github.johnnypixelz.utilizer.inventory.items.ClickableItem;
import io.github.johnnypixelz.utilizer.inventory.items.SimpleItem;
import io.github.johnnypixelz.utilizer.inventory.slot.Slot;
import io.github.johnnypixelz.utilizer.itemstack.Items;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class InventoryConfigItem {

    public static InventoryConfigItem parse(@Nonnull ConfigurationSection section) {
        final ItemStack parsedStack = Items.parse(section);

        final Map<String, Message> messages = new HashMap<>();

        final ConfigurationSection messagesSection = section.getConfigurationSection("messages");
        if (messagesSection != null) {
            for (String messageSectionKey : messagesSection.getKeys(false)) {
                final Message parsedMessage = Messages.parse(messagesSection, messageSectionKey);
                messages.put(messageSectionKey, parsedMessage);
            }
        }

        final Slot slot = getSlot(section).orElse(null);

        final List<Action> actions = section.getStringList("actions")
                .stream()
                .map(Action::parse)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        final int priority = section.getInt("priority", 0);

        return new InventoryConfigItem(section, parsedStack, messages, slot, actions, priority);
    }

    private static Optional<Slot> getSlot(ConfigurationSection section) {
        return extractSlot(section, "position")
                .or(() -> extractSlot(section, "pos"))
                .or(() -> extractSlot(section, "slot"))
                .or(() -> extractSlot(section.getConfigurationSection("position"), "row", "column"))
                .or(() -> extractSlot(section.getConfigurationSection("pos"), "row", "column"))
                .or(() -> extractSlot(section.getConfigurationSection("slot"), "row", "column"))
                .or(() -> extractSlot(section.getConfigurationSection("position"), "row", "col"))
                .or(() -> extractSlot(section.getConfigurationSection("pos"), "row", "col"))
                .or(() -> extractSlot(section.getConfigurationSection("slot"), "row", "col"))
                .or(() -> extractSlot(section.getConfigurationSection("position"), "y", "x"))
                .or(() -> extractSlot(section.getConfigurationSection("pos"), "y", "x"))
                .or(() -> extractSlot(section.getConfigurationSection("slot"), "y", "x"))
                .or(() -> extractSlot(section, "y", "x"))
                .or(() -> extractSlot(section, "row", "col"))
                .or(() -> extractSlot(section, "row", "column"));
    }

    private static Optional<Slot> extractSlot(@Nullable ConfigurationSection section, @Nonnull String key) {
        if (section == null) return Optional.empty();

        if (section.isString(key)) {
            final String position = section.getString(key);
            Objects.requireNonNull(position);

            final Optional<Slot> slot = parseSlot(position);
            if (slot.isPresent()) return slot;
        }

        return Optional.empty();
    }

    private static Optional<Slot> extractSlot(@Nullable ConfigurationSection section, @Nonnull String rowKey, @Nonnull String columnKey) {
        if (section == null) return Optional.empty();

        if (section.isInt(rowKey) && section.isInt(columnKey)) {
            final int row = section.getInt(rowKey);
            final int column = section.getInt(columnKey);

            return Optional.of(Slot.of(row, column));
        }

        return Optional.empty();
    }

    private static Optional<Slot> parseSlot(@Nullable String input) {
        if (input == null) return Optional.empty();

        String[] parts = input.split(",");
        if (parts.length == 1) {
            try {
                int number = Integer.parseInt(parts[0].trim());
                return Optional.of(Slot.of(number));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        } else if (parts.length == 2) {
            try {
                int number1 = Integer.parseInt(parts[0].trim());
                int number2 = Integer.parseInt(parts[1].trim());
                return Optional.of(Slot.of(number1, number2));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    private final ConfigurationSection configurationSection;
    private final ItemStack itemStack;
    private final Map<String, Message> messages;
    private final Slot slot;
    private final List<Action> actions;
    int priority;

    private InventoryConfigItem(ConfigurationSection configurationSection, ItemStack itemStack, Map<String, Message> messages, Slot slot, List<Action> actions, int priority) {
        this.configurationSection = configurationSection;
        this.itemStack = itemStack;
        this.messages = messages;
        this.slot = slot;
        this.actions = actions;
        this.priority = priority;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public Optional<Message> getMessage(String messageId) {
        return Optional.ofNullable(messages.get(messageId));
    }

    public Optional<Slot> getSlot() {
        return Optional.ofNullable(slot);
    }

    public ConfigurationSection getConfigurationSection() {
        return configurationSection;
    }

    public List<Action> getActions() {
        return actions;
    }

    public int getPriority() {
        return priority;
    }

    public InventoryItem getInventoryItem(CustomInventory customInventory) {
        if (actions.isEmpty()) {
            return new SimpleItem(getItemStack());
        }

        return new ClickableItem(getItemStack())
                .leftClick(event -> {
                    for (Action action : actions) {
                        action.execute(customInventory, (Player) event.getWhoClicked());
                    }
                });
    }

}
