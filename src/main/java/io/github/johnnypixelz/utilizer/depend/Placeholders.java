package io.github.johnnypixelz.utilizer.depend;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Unified placeholder system with internal registry and PlaceholderAPI integration.
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Register custom placeholders</li>
 *   <li>Player-based and static (global) placeholders</li>
 *   <li>Built-in default placeholders</li>
 *   <li>Automatic PlaceholderAPI integration when available</li>
 * </ul>
 *
 * <h2>Built-in Placeholders</h2>
 * <ul>
 *   <li>{@code %player%} - Player's name</li>
 *   <li>{@code %player_name%} - Player's name</li>
 *   <li>{@code %player_displayname%} - Player's display name</li>
 *   <li>{@code %player_uuid%} - Player's UUID</li>
 *   <li>{@code %player_world%} - Player's current world</li>
 *   <li>{@code %player_health%} - Player's health (integer)</li>
 *   <li>{@code %player_food%} - Player's food level</li>
 *   <li>{@code %player_level%} - Player's XP level</li>
 *   <li>{@code %player_gamemode%} - Player's gamemode</li>
 *   <li>{@code %player_x%} - Player's X coordinate</li>
 *   <li>{@code %player_y%} - Player's Y coordinate</li>
 *   <li>{@code %player_z%} - Player's Z coordinate</li>
 *   <li>{@code %server_online%} - Online player count</li>
 *   <li>{@code %server_max%} - Max player count</li>
 *   <li>{@code %server_name%} - Server name</li>
 *   <li>{@code %server_version%} - Server version</li>
 *   <li>{@code %server_motd%} - Server MOTD</li>
 * </ul>
 *
 * <h2>Example Usage</h2>
 * <pre>
 * // Register custom placeholders
 * Placeholders.register("coins", player -> String.valueOf(getCoins(player)));
 * Placeholders.register("server_tps", () -> String.format("%.1f", getTPS()));
 *
 * // Use placeholders
 * String message = Placeholders.set(player, "Hello %player%! You have %coins% coins.");
 * </pre>
 */
public final class Placeholders {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("%([^%]+)%");

    private static final Map<String, Function<Player, String>> playerPlaceholders = new HashMap<>();
    private static final Map<String, Supplier<String>> staticPlaceholders = new HashMap<>();

    static {
        // Register default player placeholders
        register("player", Player::getName);
        register("player_name", Player::getName);
        register("player_displayname", player -> player.getDisplayName());
        register("player_uuid", player -> player.getUniqueId().toString());
        register("player_world", player -> player.getWorld().getName());
        register("player_health", player -> String.valueOf((int) player.getHealth()));
        register("player_food", player -> String.valueOf(player.getFoodLevel()));
        register("player_level", player -> String.valueOf(player.getLevel()));
        register("player_gamemode", player -> player.getGameMode().name().toLowerCase());
        register("player_x", player -> String.valueOf(player.getLocation().getBlockX()));
        register("player_y", player -> String.valueOf(player.getLocation().getBlockY()));
        register("player_z", player -> String.valueOf(player.getLocation().getBlockZ()));

        // Register default static placeholders
        register("server_online", () -> String.valueOf(Bukkit.getOnlinePlayers().size()));
        register("server_max", () -> String.valueOf(Bukkit.getMaxPlayers()));
        register("server_name", () -> Bukkit.getServer().getName());
        register("server_version", () -> Bukkit.getServer().getVersion());
        register("server_motd", () -> Bukkit.getServer().getMotd());
    }

    private Placeholders() {
    }

    // ==================== Registration ====================

    /**
     * Register a player-based placeholder.
     * The function receives the player and returns the replacement value.
     *
     * @param name     Placeholder name without percent signs (e.g., "coins" for %coins%)
     * @param resolver Function that takes a Player and returns the replacement string
     */
    public static void register(@NotNull String name, @NotNull Function<Player, String> resolver) {
        playerPlaceholders.put(name.toLowerCase(), resolver);
    }

    /**
     * Register a static (global) placeholder that doesn't require a player.
     * Useful for server-wide values like TPS, online count, etc.
     *
     * @param name     Placeholder name without percent signs (e.g., "server_tps" for %server_tps%)
     * @param resolver Supplier that returns the replacement string
     */
    public static void register(@NotNull String name, @NotNull Supplier<String> resolver) {
        staticPlaceholders.put(name.toLowerCase(), resolver);
    }

    /**
     * Unregister a placeholder.
     *
     * @param name Placeholder name to unregister
     * @return true if a placeholder was removed
     */
    public static boolean unregister(@NotNull String name) {
        String key = name.toLowerCase();
        boolean removed = playerPlaceholders.remove(key) != null;
        removed |= staticPlaceholders.remove(key) != null;
        return removed;
    }

    /**
     * Check if a placeholder is registered.
     *
     * @param name Placeholder name
     * @return true if registered
     */
    public static boolean isRegistered(@NotNull String name) {
        String key = name.toLowerCase();
        return playerPlaceholders.containsKey(key) || staticPlaceholders.containsKey(key);
    }

    // ==================== Processing ====================

    /**
     * Process all placeholders in the text for the given player.
     * Processes internal placeholders first, then PlaceholderAPI if available.
     *
     * @param player The player context for placeholder resolution
     * @param text   The text containing placeholders
     * @return The text with all placeholders replaced
     */
    @NotNull
    public static String set(@Nullable Player player, @NotNull String text) {
        if (text.isEmpty() || !text.contains("%")) {
            return text;
        }

        // Process internal placeholders first
        String result = processInternal(player, text);

        // Process PlaceholderAPI placeholders if available
        if (player != null && isPlaceholderAPIEnabled()) {
            result = PlaceholderAPI.setPlaceholders(player, result);
        }

        return result;
    }

    /**
     * Process all placeholders in the text for the given offline player.
     * Internal placeholders (static only) are processed first, then PlaceholderAPI if available.
     *
     * @param player The offline player context
     * @param text   The text containing placeholders
     * @return The text with all placeholders replaced
     */
    @NotNull
    public static String set(@Nullable OfflinePlayer player, @NotNull String text) {
        if (player instanceof Player) {
            return set((Player) player, text);
        }

        if (text.isEmpty() || !text.contains("%")) {
            return text;
        }

        // Process only static placeholders for offline players
        String result = processStatic(text);

        // Process PlaceholderAPI placeholders if available
        if (player != null && isPlaceholderAPIEnabled()) {
            result = PlaceholderAPI.setPlaceholders(player, result);
        }

        return result;
    }

    /**
     * Process only static placeholders (no player required).
     *
     * @param text The text containing placeholders
     * @return The text with static placeholders replaced
     */
    @NotNull
    public static String setStatic(@NotNull String text) {
        return processStatic(text);
    }

    // ==================== Internal Processing ====================

    private static String processInternal(@Nullable Player player, @NotNull String text) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String placeholder = matcher.group(1).toLowerCase();
            String replacement = null;

            // Try player placeholder first
            if (player != null) {
                Function<Player, String> playerResolver = playerPlaceholders.get(placeholder);
                if (playerResolver != null) {
                    try {
                        replacement = playerResolver.apply(player);
                    } catch (Exception e) {
                        replacement = null; // Keep original on error
                    }
                }
            }

            // Try static placeholder if no player replacement found
            if (replacement == null) {
                Supplier<String> staticResolver = staticPlaceholders.get(placeholder);
                if (staticResolver != null) {
                    try {
                        replacement = staticResolver.get();
                    } catch (Exception e) {
                        replacement = null; // Keep original on error
                    }
                }
            }

            // Replace or keep original
            if (replacement != null) {
                matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
            } else {
                matcher.appendReplacement(result, Matcher.quoteReplacement(matcher.group(0)));
            }
        }
        matcher.appendTail(result);

        return result.toString();
    }

    private static String processStatic(@NotNull String text) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String placeholder = matcher.group(1).toLowerCase();
            Supplier<String> resolver = staticPlaceholders.get(placeholder);

            if (resolver != null) {
                try {
                    String replacement = resolver.get();
                    matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
                } catch (Exception e) {
                    matcher.appendReplacement(result, Matcher.quoteReplacement(matcher.group(0)));
                }
            } else {
                matcher.appendReplacement(result, Matcher.quoteReplacement(matcher.group(0)));
            }
        }
        matcher.appendTail(result);

        return result.toString();
    }

    // ==================== PlaceholderAPI Integration ====================

    private static Boolean placeholderAPIEnabled = null;

    /**
     * Check if PlaceholderAPI is available and enabled.
     * Result is cached for performance.
     *
     * @return true if PlaceholderAPI is enabled
     */
    private static boolean isPlaceholderAPIEnabled() {
        if (placeholderAPIEnabled == null) {
            placeholderAPIEnabled = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        }
        return placeholderAPIEnabled;
    }

}
