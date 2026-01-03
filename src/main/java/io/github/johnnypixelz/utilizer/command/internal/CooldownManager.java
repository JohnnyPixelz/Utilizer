package io.github.johnnypixelz.utilizer.command.internal;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages command cooldowns for players.
 *
 * <p>Cooldowns are tracked using a composite key of player UUID and command identifier.
 * For global cooldowns, a special "global" UUID is used.</p>
 */
public final class CooldownManager {

    private static final UUID GLOBAL_UUID = new UUID(0, 0);
    private static final CooldownManager INSTANCE = new CooldownManager();

    private final Map<String, Long> cooldowns = new ConcurrentHashMap<>();

    private CooldownManager() {
    }

    /**
     * @return the singleton instance
     */
    public static CooldownManager getInstance() {
        return INSTANCE;
    }

    /**
     * Creates a cooldown key for a player and command.
     *
     * @param playerId  the player UUID
     * @param commandId the command identifier
     * @return the composite key
     */
    private String makeKey(UUID playerId, String commandId) {
        return playerId.toString() + ":" + commandId;
    }

    /**
     * Checks if a player is on cooldown for a command.
     *
     * @param playerId  the player UUID
     * @param commandId the command identifier
     * @return true if on cooldown
     */
    public boolean isOnCooldown(UUID playerId, String commandId) {
        String key = makeKey(playerId, commandId);
        Long expiry = cooldowns.get(key);
        if (expiry == null) {
            return false;
        }
        if (System.currentTimeMillis() >= expiry) {
            cooldowns.remove(key);
            return false;
        }
        return true;
    }

    /**
     * Checks if a global cooldown is active for a command.
     *
     * @param commandId the command identifier
     * @return true if on cooldown
     */
    public boolean isOnGlobalCooldown(String commandId) {
        return isOnCooldown(GLOBAL_UUID, commandId);
    }

    /**
     * Gets the remaining cooldown time in seconds.
     *
     * @param playerId  the player UUID
     * @param commandId the command identifier
     * @return remaining time in seconds, or 0 if not on cooldown
     */
    public long getRemainingTime(UUID playerId, String commandId) {
        String key = makeKey(playerId, commandId);
        Long expiry = cooldowns.get(key);
        if (expiry == null) {
            return 0;
        }
        long remaining = expiry - System.currentTimeMillis();
        if (remaining <= 0) {
            cooldowns.remove(key);
            return 0;
        }
        return (remaining + 999) / 1000; // Round up to seconds
    }

    /**
     * Gets the remaining global cooldown time in seconds.
     *
     * @param commandId the command identifier
     * @return remaining time in seconds, or 0 if not on cooldown
     */
    public long getGlobalRemainingTime(String commandId) {
        return getRemainingTime(GLOBAL_UUID, commandId);
    }

    /**
     * Sets a cooldown for a player.
     *
     * @param playerId  the player UUID
     * @param commandId the command identifier
     * @param seconds   the cooldown duration in seconds
     */
    public void setCooldown(UUID playerId, String commandId, long seconds) {
        String key = makeKey(playerId, commandId);
        cooldowns.put(key, System.currentTimeMillis() + (seconds * 1000));
    }

    /**
     * Sets a global cooldown.
     *
     * @param commandId the command identifier
     * @param seconds   the cooldown duration in seconds
     */
    public void setGlobalCooldown(String commandId, long seconds) {
        setCooldown(GLOBAL_UUID, commandId, seconds);
    }

    /**
     * Clears a player's cooldown for a command.
     *
     * @param playerId  the player UUID
     * @param commandId the command identifier
     */
    public void clearCooldown(UUID playerId, String commandId) {
        String key = makeKey(playerId, commandId);
        cooldowns.remove(key);
    }

    /**
     * Clears a global cooldown.
     *
     * @param commandId the command identifier
     */
    public void clearGlobalCooldown(String commandId) {
        clearCooldown(GLOBAL_UUID, commandId);
    }

    /**
     * Clears all cooldowns for a player.
     *
     * @param playerId the player UUID
     */
    public void clearAllCooldowns(UUID playerId) {
        String prefix = playerId.toString() + ":";
        cooldowns.keySet().removeIf(key -> key.startsWith(prefix));
    }

    /**
     * Clears all cooldowns.
     */
    public void clearAll() {
        cooldowns.clear();
    }

    /**
     * Cleans up expired cooldowns to prevent memory leaks.
     * Called periodically or on plugin disable.
     */
    public void cleanup() {
        long now = System.currentTimeMillis();
        cooldowns.entrySet().removeIf(entry -> entry.getValue() <= now);
    }

}
