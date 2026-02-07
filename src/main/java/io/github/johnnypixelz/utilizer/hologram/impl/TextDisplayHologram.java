package io.github.johnnypixelz.utilizer.hologram.impl;

import io.github.johnnypixelz.utilizer.text.Colors;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Display;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.TextDisplay;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Hologram implementation using TextDisplay entities.
 * Requires Minecraft 1.19.4+ (when TextDisplay was added).
 * Supports both Paper (Adventure API) and Spigot (legacy strings).
 */
public class TextDisplayHologram extends AbstractNativeHologram {

    private final NamespacedKey hologramIdKey;
    private TextDisplay textDisplay;
    private Interaction interaction;

    // Stored display properties for respawn
    private Display.Billboard billboard = Display.Billboard.CENTER;
    private Color backgroundColor = Color.fromARGB(0, 0, 0, 0);
    private boolean shadowed = true;
    private TextDisplay.TextAlignment alignment = TextDisplay.TextAlignment.CENTER;
    private int lineWidth = 200;
    private byte textOpacity = (byte) -1;
    private boolean seeThrough = false;
    private float viewRange = 1.0f;
    private float scale = 1.0f;

    public TextDisplayHologram(String id, Location location, List<String> lines, NamespacedKey hologramIdKey) {
        super(id, location, lines);
        this.hologramIdKey = hologramIdKey;
        spawnEntities(lines);
    }

    private void spawnEntities(List<String> lines) {
        textDisplay = location.getWorld().spawn(location, TextDisplay.class, entity -> {
            entity.setPersistent(false);
            setTextDisplayText(entity, lines);
            entity.setBillboard(billboard);
            entity.setBackgroundColor(backgroundColor);
            entity.setShadowed(shadowed);
            entity.setAlignment(alignment);
            entity.setLineWidth(lineWidth);
            entity.setTextOpacity(textOpacity);
            entity.setSeeThrough(seeThrough);
            entity.setViewRange(viewRange);
            entity.getPersistentDataContainer().set(hologramIdKey, PersistentDataType.STRING, id);
        });

        if (scale != 1.0f) {
            applyScale();
        }

        interaction = location.getWorld().spawn(location, Interaction.class, entity -> {
            entity.setPersistent(false);
            entity.setInteractionWidth(2.0f);
            entity.setInteractionHeight(calculateInteractionHeight(lines.size()));
            entity.getPersistentDataContainer().set(hologramIdKey, PersistentDataType.STRING, id);
        });
    }

    @SuppressWarnings("deprecation")
    private void setTextDisplayText(TextDisplay display, List<String> lines) {
        String combined = String.join("\n", lines.stream().map(Colors::color).toList());
        display.setText(combined);
    }

    private float calculateInteractionHeight(int lineCount) {
        return Math.max(0.5f, lineCount * 0.3f);
    }

    private void applyScale() {
        if (textDisplay == null || textDisplay.isDead()) return;
        Transformation transformation = textDisplay.getTransformation();
        textDisplay.setTransformation(new Transformation(
                transformation.getTranslation(),
                transformation.getLeftRotation(),
                new Vector3f(scale, scale, scale),
                transformation.getRightRotation()
        ));
    }

    @Override
    public void updateLines(List<String> lines) {
        this.lines = new ArrayList<>(lines);
        if (textDisplay != null && !textDisplay.isDead()) {
            setTextDisplayText(textDisplay, lines);
        }
        if (interaction != null && !interaction.isDead()) {
            interaction.setInteractionHeight(calculateInteractionHeight(lines.size()));
        }
    }

    @Override
    public void teleport(Location location) {
        this.location = location.clone();
        if (textDisplay != null && !textDisplay.isDead()) {
            textDisplay.teleport(location);
        }
        if (interaction != null && !interaction.isDead()) {
            interaction.teleport(location);
        }
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

    // ==================== Lifecycle ====================

    @Override
    public boolean isValid() {
        return textDisplay != null && !textDisplay.isDead()
                && interaction != null && !interaction.isDead();
    }

    @Override
    public void despawn() {
        textDisplay = null;
        interaction = null;
    }

    @Override
    public void respawn() {
        // Clean up any remaining alive entities
        if (textDisplay != null && !textDisplay.isDead()) {
            textDisplay.remove();
        }
        if (interaction != null && !interaction.isDead()) {
            interaction.remove();
        }
        spawnEntities(lines);
    }

    // ==================== Display Properties ====================

    @Override
    public void setScale(float scale) {
        this.scale = scale;
        applyScale();
    }

    @Override
    public void setBillboard(Display.Billboard billboard) {
        this.billboard = billboard;
        if (textDisplay != null && !textDisplay.isDead()) {
            textDisplay.setBillboard(billboard);
        }
    }

    @Override
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        if (textDisplay != null && !textDisplay.isDead()) {
            textDisplay.setBackgroundColor(color);
        }
    }

    @Override
    public void setShadowed(boolean shadowed) {
        this.shadowed = shadowed;
        if (textDisplay != null && !textDisplay.isDead()) {
            textDisplay.setShadowed(shadowed);
        }
    }

    @Override
    public void setAlignment(TextDisplay.TextAlignment alignment) {
        this.alignment = alignment;
        if (textDisplay != null && !textDisplay.isDead()) {
            textDisplay.setAlignment(alignment);
        }
    }

    @Override
    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
        if (textDisplay != null && !textDisplay.isDead()) {
            textDisplay.setLineWidth(lineWidth);
        }
    }

    @Override
    public void setTextOpacity(byte opacity) {
        this.textOpacity = opacity;
        if (textDisplay != null && !textDisplay.isDead()) {
            textDisplay.setTextOpacity(opacity);
        }
    }

    @Override
    public void setSeeThrough(boolean seeThrough) {
        this.seeThrough = seeThrough;
        if (textDisplay != null && !textDisplay.isDead()) {
            textDisplay.setSeeThrough(seeThrough);
        }
    }

    @Override
    public void setViewRange(float range) {
        this.viewRange = range;
        if (textDisplay != null && !textDisplay.isDead()) {
            textDisplay.setViewRange(range);
        }
    }

}
