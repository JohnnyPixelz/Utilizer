package io.github.johnnypixelz.utilizer.serialize.player;

import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

public class AttributeSnapshot {
    private final float exp;
    private final int level;
    private final int totalExperience;
    private final float exhaustion;
    private final float saturation;
    private final int foodLevel;
    private final boolean allowFlight;
    private final float flySpeed;
    private final float walkSpeed;
    private final double healthScale;
    private final double health;
    private final boolean flying;
    private final boolean gliding;
    private final GameMode gameMode;
    private final Collection<PotionEffect> effects;

    public AttributeSnapshot(Player player) {
        exp = player.getExp();
        level = player.getLevel();
        totalExperience = player.getTotalExperience();
        exhaustion = player.getExhaustion();
        saturation = player.getSaturation();
        foodLevel = player.getFoodLevel();
        allowFlight = player.getAllowFlight();
        flySpeed = player.getFlySpeed();
        walkSpeed = player.getWalkSpeed();
        healthScale = player.getHealthScale();
        health = player.getHealth();
        flying = player.isFlying();
        gliding = player.isGliding();
        gameMode = player.getGameMode();
        effects = player.getActivePotionEffects();
    }

    public void apply(Player player) {
        player.setExp(exp);
        player.setLevel(level);
        player.setTotalExperience(totalExperience);
        player.setExhaustion(exhaustion);
        player.setSaturation(saturation);
        player.setFoodLevel(foodLevel);
        player.setAllowFlight(allowFlight);
        player.setFlySpeed(flySpeed);
        player.setWalkSpeed(walkSpeed);
        player.setHealthScale(healthScale);
        player.setHealth(Math.min(health, player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
        player.setFlying(flying);
        player.setGliding(gliding);
        player.setGameMode(gameMode);

        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        effects.forEach(player::addPotionEffect);
    }

}
