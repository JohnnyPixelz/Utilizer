package io.github.johnnypixelz.utilizer.serialize.player;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

public class PlayerSnapshot {
    private final Location location;
    private final ItemStack[] inventoryContents;
    private final ItemStack[] armorContents;
    private final double health;
    private final int foodLevel;
    private final float saturation;
    private final Collection<PotionEffect> potionEffects;
    private final GameMode gameMode;

    public PlayerSnapshot(Player player) {
        this.location = player.getLocation();
        this.inventoryContents = player.getInventory().getContents();
        this.armorContents = player.getInventory().getArmorContents();
        this.health = player.getHealth();
        this.foodLevel = player.getFoodLevel();
        this.saturation = player.getSaturation();
        this.potionEffects = player.getActivePotionEffects();
        this.gameMode = player.getGameMode();
    }

    public static void clean(Player player) {
        player.getInventory().clear();
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setSaturation(4);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
    }

    public static PlayerSnapshot getAndClean(Player player) {
        PlayerSnapshot snapshot = new PlayerSnapshot(player);
        clean(player);
        return snapshot;
    }

    public void restore(Player player) {
        player.teleport(location);
        player.getInventory().setContents(inventoryContents);
        player.getInventory().setArmorContents(armorContents);
        player.setHealth(health);
        player.setFoodLevel(foodLevel);
        player.setSaturation(saturation);
        potionEffects.forEach(player::addPotionEffect);
        player.setGameMode(gameMode);
    }
}
