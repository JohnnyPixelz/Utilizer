package io.github.johnnypixelz.utilizer.hologram.impl;

import io.github.johnnypixelz.utilizer.text.Colors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Display;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.TextDisplay;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

/**
 * Hologram implementation using TextDisplay entities.
 * Requires Minecraft 1.19.4+ (when TextDisplay was added).
 * Supports both Paper (Adventure API) and Spigot (legacy strings).
 */
public class TextDisplayHologram extends AbstractNativeHologram {

    private static final boolean IS_PAPER = checkPaper();

    private static boolean checkPaper() {
        try {
            Class.forName("io.papermc.paper.adventure.PaperAdventure");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private final NamespacedKey hologramIdKey;
    private TextDisplay textDisplay;
    private Interaction interaction;

    public TextDisplayHologram(String id, Location location, List<String> lines, NamespacedKey hologramIdKey) {
        super(id, location, lines);
        this.hologramIdKey = hologramIdKey;
        spawnEntities(lines);
    }

    private void spawnEntities(List<String> lines) {
        textDisplay = location.getWorld().spawn(location, TextDisplay.class, entity -> {
            setTextDisplayText(entity, lines);
            entity.setBillboard(Display.Billboard.CENTER);
            entity.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
            entity.setShadowed(true);
            entity.setAlignment(TextDisplay.TextAlignment.CENTER);
            entity.setLineWidth(200);
            entity.getPersistentDataContainer().set(hologramIdKey, PersistentDataType.STRING, id);
        });

        interaction = location.getWorld().spawn(location, Interaction.class, entity -> {
            entity.setInteractionWidth(2.0f);
            entity.setInteractionHeight(calculateInteractionHeight(lines.size()));
            entity.getPersistentDataContainer().set(hologramIdKey, PersistentDataType.STRING, id);
        });
    }

    private void setTextDisplayText(TextDisplay display, List<String> lines) {
        String combined = String.join("\n", lines.stream().map(Colors::color).toList());

        if (IS_PAPER) {
            // Paper: Use Adventure Component API
            Component component = LegacyComponentSerializer.legacySection().deserialize(combined);
            display.text(component);
        } else {
            // Spigot: Use legacy setText method
            display.setText(combined);
        }
    }

    private float calculateInteractionHeight(int lineCount) {
        return Math.max(0.5f, lineCount * 0.3f);
    }

    @Override
    public void updateLines(List<String> lines) {
        this.lines = new ArrayList<>(lines);
        setTextDisplayText(textDisplay, lines);
        interaction.setInteractionHeight(calculateInteractionHeight(lines.size()));
    }

    @Override
    public void teleport(Location location) {
        this.location = location.clone();
        textDisplay.teleport(location);
        interaction.teleport(location);
    }

    @Override
    public void remove() {
        if (textDisplay != null && !textDisplay.isDead()) {
            textDisplay.remove();
        }
        if (interaction != null && !interaction.isDead()) {
            interaction.remove();
        }
        textDisplay = null;
        interaction = null;
    }

}
