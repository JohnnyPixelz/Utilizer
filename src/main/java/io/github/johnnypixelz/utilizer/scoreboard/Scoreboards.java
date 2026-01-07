package io.github.johnnypixelz.utilizer.scoreboard;

import io.github.johnnypixelz.utilizer.plugin.Provider;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * Static utility class for managing scoreboards.
 * Provides a simple API for creating and managing per-player scoreboards (sidebars).
 *
 * <p>Example usage:
 * <pre>
 * // Create and show a scoreboard
 * Scoreboard scoreboard = Scoreboards.create("&amp;6My Server")
 *     .line(0, "&amp;7Welcome, &amp;e%player_name%")
 *     .line(1, "&amp;7Balance: &amp;a$%vault_eco_balance%")
 *     .line(2, "")
 *     .line(3, "&amp;ewww.example.com");
 *
 * Scoreboards.show(player, scoreboard);
 *
 * // Later, update placeholders
 * Scoreboards.update(player);
 *
 * // Hide when done
 * Scoreboards.hide(player);
 * </pre>
 */
public final class Scoreboards {

    private static ScoreboardService service;

    private Scoreboards() {
    }

    /**
     * Get or create the scoreboard service for the current plugin.
     *
     * @return The scoreboard service
     */
    @NotNull
    public static synchronized ScoreboardService service() {
        if (service == null) {
            service = new ScoreboardService(Provider.getPlugin());
        }
        return service;
    }

    /**
     * Create a new scoreboard with the given title.
     *
     * @param title The scoreboard title (supports color codes)
     * @return A new Scoreboard instance
     */
    @NotNull
    public static Scoreboard create(@NotNull String title) {
        return Scoreboard.create(title);
    }

    /**
     * Create a scoreboard with title and lines.
     *
     * @param title The scoreboard title
     * @param lines The lines to display
     * @return A new Scoreboard instance
     */
    @NotNull
    public static Scoreboard create(@NotNull String title, @NotNull List<String> lines) {
        return Scoreboard.create(title).lines(lines);
    }

    /**
     * Create a scoreboard with title and lines (varargs).
     *
     * @param title The scoreboard title
     * @param lines The lines to display
     * @return A new Scoreboard instance
     */
    @NotNull
    public static Scoreboard create(@NotNull String title, @NotNull String... lines) {
        return Scoreboard.create(title).lines(lines);
    }

    /**
     * Show a scoreboard to a player.
     *
     * @param player     The player
     * @param scoreboard The scoreboard to show
     */
    public static void show(@NotNull Player player, @NotNull Scoreboard scoreboard) {
        scoreboard.show(player);
    }

    /**
     * Hide the current scoreboard from a player.
     *
     * @param player The player
     */
    public static void hide(@NotNull Player player) {
        service().hideFromPlayer(player);
    }

    /**
     * Update the scoreboard for a player (refresh PAPI placeholders).
     *
     * @param player The player
     */
    public static void update(@NotNull Player player) {
        Scoreboard scoreboard = service().getPlayerScoreboard(player);
        if (scoreboard != null) {
            scoreboard.update(player);
        }
    }

    /**
     * Get a scoreboard by ID.
     *
     * @param id Scoreboard ID
     * @return The scoreboard, or null if not found
     */
    @Nullable
    public static Scoreboard get(@NotNull String id) {
        return service().getScoreboard(id);
    }

    /**
     * Get the scoreboard currently showing to a player.
     *
     * @param player The player
     * @return The scoreboard, or null if none
     */
    @Nullable
    public static Scoreboard getFor(@NotNull Player player) {
        return service().getPlayerScoreboard(player);
    }

    /**
     * Check if a player has a scoreboard showing.
     *
     * @param player The player
     * @return true if they have a scoreboard
     */
    public static boolean isShowing(@NotNull Player player) {
        return service().hasPlayerScoreboard(player);
    }

    /**
     * Get all registered scoreboards.
     *
     * @return Collection of scoreboards
     */
    @NotNull
    public static Collection<Scoreboard> getAll() {
        return service().getAllScoreboards();
    }

    /**
     * Get the number of active scoreboards.
     *
     * @return Scoreboard count
     */
    public static int count() {
        return service().getScoreboardCount();
    }

    /**
     * Shutdown and cleanup all scoreboards.
     * Should be called when the plugin is disabled.
     */
    public static void shutdown() {
        if (service != null) {
            service.shutdown();
            service = null;
        }
    }

}
