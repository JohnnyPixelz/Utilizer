package io.github.johnnypixelz.utilizer.scoreboard;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

/**
 * Per-player scoreboard (sidebar) interface.
 * Provides flicker-free updates and automatic PlaceholderAPI integration.
 *
 * <h2>Features</h2>
 * <ul>
 *   <li><b>Flicker-free updates</b> - Uses team prefix/suffix technique</li>
 *   <li><b>PlaceholderAPI support</b> - Automatically parses %placeholders% per-player</li>
 *   <li><b>Multi-player</b> - One scoreboard can be shown to multiple players</li>
 *   <li><b>Dynamic lines</b> - Only set lines are displayed (not all 15)</li>
 *   <li><b>Hidden scores</b> - Score numbers on the right are hidden (Paper 1.20.3+)</li>
 *   <li><b>Auto-cleanup</b> - Automatic cleanup on player quit</li>
 * </ul>
 *
 * <h2>Line Ordering</h2>
 * <p>Lines are displayed top-to-bottom: index 0 is at the top, index 14 at the bottom.
 * Use an empty string ({@code ""}) for blank spacer lines.
 *
 * <h2>Example Usage</h2>
 * <pre>
 * // Create a scoreboard
 * Scoreboard scoreboard = Scoreboard.create("&amp;6&amp;lMY SERVER")
 *     .line(0, "&amp;7Welcome, &amp;e%player_name%")
 *     .line(1, "")
 *     .line(2, "&amp;7Balance: &amp;a$%vault_eco_balance%")
 *     .line(3, "&amp;7Online: &amp;e%server_online%")
 *     .line(4, "")
 *     .line(5, "&amp;ewww.example.com")
 *     .autoUpdate(20L); // Refresh PAPI every second
 *
 * // Show to players
 * scoreboard.show(player1);
 * scoreboard.show(player2);
 *
 * // Update lines dynamically
 * scoreboard.line(2, "&amp;7Balance: &amp;a$" + newBalance);
 *
 * // Cleanup when done
 * scoreboard.destroy();
 * </pre>
 *
 * @see Scoreboards Static utility class for convenience methods
 */
public interface Scoreboard {

    /**
     * Maximum number of lines supported (Bukkit limitation).
     */
    int MAX_LINES = 15;

    /**
     * Create a new scoreboard with the given title.
     *
     * @param title The scoreboard title (supports color codes)
     * @return A new Scoreboard instance
     */
    @NotNull
    static Scoreboard create(@NotNull String title) {
        return new ScoreboardImpl(title);
    }

    /**
     * Get the unique identifier for this scoreboard.
     *
     * @return The scoreboard ID
     */
    @NotNull
    String getId();

    /**
     * Set the scoreboard title.
     *
     * @param title The new title (supports color codes)
     * @return This scoreboard for chaining
     */
    @NotNull
    Scoreboard title(@NotNull String title);

    /**
     * Get the current title.
     *
     * @return The current title (unprocessed)
     */
    @NotNull
    String getTitle();

    /**
     * Set a line at the specified index.
     * Lines are displayed top-to-bottom, with index 0 at the top.
     *
     * @param index The line index (0-14)
     * @param text  The line text (supports color codes and PAPI placeholders)
     * @return This scoreboard for chaining
     * @throws IllegalArgumentException if index is out of range [0, 14]
     */
    @NotNull
    Scoreboard line(int index, @NotNull String text);

    /**
     * Set all lines at once, replacing any existing lines.
     * Lines are displayed in list order (index 0 = first element).
     *
     * @param lines The lines to set (max 15)
     * @return This scoreboard for chaining
     * @throws IllegalArgumentException if lines exceeds 15 elements
     */
    @NotNull
    Scoreboard lines(@NotNull List<String> lines);

    /**
     * Set all lines at once using varargs.
     *
     * @param lines The lines to set (max 15)
     * @return This scoreboard for chaining
     * @throws IllegalArgumentException if lines exceeds 15 elements
     */
    @NotNull
    Scoreboard lines(@NotNull String... lines);

    /**
     * Get the text at a specific line index.
     *
     * @param index The line index
     * @return The line text, or null if not set
     */
    @Nullable
    String getLine(int index);

    /**
     * Get all current lines.
     *
     * @return Unmodifiable list of lines (includes nulls for empty slots)
     */
    @NotNull
    List<String> getLines();

    /**
     * Remove a line at the specified index.
     *
     * @param index The line index to remove
     * @return This scoreboard for chaining
     */
    @NotNull
    Scoreboard removeLine(int index);

    /**
     * Clear all lines.
     *
     * @return This scoreboard for chaining
     */
    @NotNull
    Scoreboard clearLines();

    /**
     * Show the scoreboard to a player.
     * If the player already has a scoreboard showing, it will be replaced.
     *
     * @param player The player to show the scoreboard to
     */
    void show(@NotNull Player player);

    /**
     * Hide the scoreboard from a player, restoring their previous scoreboard.
     *
     * @param player The player to hide the scoreboard from
     */
    void hide(@NotNull Player player);

    /**
     * Update the scoreboard content for a player.
     * Re-parses PlaceholderAPI placeholders and refreshes the display.
     *
     * @param player The player to update
     */
    void update(@NotNull Player player);

    /**
     * Update the scoreboard for all players currently viewing it.
     */
    void updateAll();

    /**
     * Check if the scoreboard is currently showing to a player.
     *
     * @param player The player to check
     * @return true if the scoreboard is showing
     */
    boolean isShowing(@NotNull Player player);

    /**
     * Get all players currently viewing this scoreboard.
     *
     * @return Unmodifiable set of players
     */
    @NotNull
    Set<Player> getViewers();

    /**
     * Destroy this scoreboard, removing it from all players and cleaning up resources.
     * After calling this, the scoreboard instance should not be reused.
     */
    void destroy();

    /**
     * Check if this scoreboard has been destroyed.
     *
     * @return true if destroyed
     */
    boolean isDestroyed();

    /**
     * Enable automatic PlaceholderAPI refresh at the specified interval.
     * Calls {@link #updateAll()} periodically.
     *
     * <p>The task is automatically cancelled when {@link #destroy()} is called
     * or when {@link #stopAutoUpdate()} is called.
     *
     * @param intervalTicks The interval in ticks (20 ticks = 1 second)
     * @return This scoreboard for chaining
     */
    @NotNull
    Scoreboard autoUpdate(long intervalTicks);

    /**
     * Stop automatic PlaceholderAPI refresh.
     *
     * @return This scoreboard for chaining
     */
    @NotNull
    Scoreboard stopAutoUpdate();

    /**
     * Check if auto-update is currently enabled.
     *
     * @return true if auto-updating
     */
    boolean isAutoUpdating();

}
