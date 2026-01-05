package io.github.johnnypixelz.utilizer.hologram.impl;

import io.github.johnnypixelz.utilizer.text.Colors;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ArmorStand;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

/**
 * Hologram implementation using ArmorStand entities.
 * Compatible with all server versions that support PersistentDataContainer (1.14+).
 */
public class ArmorStandHologram extends AbstractNativeHologram {

    private static final double LINE_HEIGHT = 0.25;

    private final NamespacedKey hologramIdKey;
    private List<ArmorStand> armorStands;

    public ArmorStandHologram(String id, Location location, List<String> lines, NamespacedKey hologramIdKey) {
        super(id, location, lines);
        this.hologramIdKey = hologramIdKey;
        this.armorStands = new ArrayList<>();
        spawnArmorStands(lines);
    }

    private void spawnArmorStands(List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            Location lineLocation = location.clone().add(0, -i * LINE_HEIGHT, 0);
            String line = lines.get(i);

            ArmorStand stand = location.getWorld().spawn(lineLocation, ArmorStand.class, entity -> {
                entity.setVisible(false);
                entity.setGravity(false);
                entity.setMarker(true);
                entity.setCustomName(Colors.color(line));
                entity.setCustomNameVisible(true);
                entity.setSmall(true);
                entity.setCollidable(false);
                entity.setInvulnerable(true);
                entity.getPersistentDataContainer().set(hologramIdKey, PersistentDataType.STRING, id);
            });

            armorStands.add(stand);
        }
    }

    @Override
    public void updateLines(List<String> lines) {
        this.lines = new ArrayList<>(lines);

        // Remove excess armor stands
        while (armorStands.size() > lines.size()) {
            ArmorStand stand = armorStands.remove(armorStands.size() - 1);
            if (stand != null && !stand.isDead()) {
                stand.remove();
            }
        }

        // Update existing armor stands
        for (int i = 0; i < armorStands.size(); i++) {
            ArmorStand stand = armorStands.get(i);
            if (stand != null && !stand.isDead()) {
                stand.setCustomName(Colors.color(lines.get(i)));
            }
        }

        // Add new armor stands if needed
        for (int i = armorStands.size(); i < lines.size(); i++) {
            Location lineLocation = location.clone().add(0, -i * LINE_HEIGHT, 0);
            String line = lines.get(i);

            ArmorStand stand = location.getWorld().spawn(lineLocation, ArmorStand.class, entity -> {
                entity.setVisible(false);
                entity.setGravity(false);
                entity.setMarker(true);
                entity.setCustomName(Colors.color(line));
                entity.setCustomNameVisible(true);
                entity.setSmall(true);
                entity.setCollidable(false);
                entity.setInvulnerable(true);
                entity.getPersistentDataContainer().set(hologramIdKey, PersistentDataType.STRING, id);
            });

            armorStands.add(stand);
        }
    }

    @Override
    public void teleport(Location location) {
        this.location = location.clone();
        for (int i = 0; i < armorStands.size(); i++) {
            ArmorStand stand = armorStands.get(i);
            if (stand != null && !stand.isDead()) {
                Location lineLocation = location.clone().add(0, -i * LINE_HEIGHT, 0);
                stand.teleport(lineLocation);
            }
        }
    }

    @Override
    public void remove() {
        for (ArmorStand stand : armorStands) {
            if (stand != null && !stand.isDead()) {
                stand.remove();
            }
        }
        armorStands.clear();
    }

}
