package io.github.johnnypixelz.utilizer.bossbar;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Represents a boss bar that can be displayed to players.
 * Supports per-player placeholder resolution, auto-updates, and fluent builder pattern.
 * <p>
 * Unlike Bukkit's built-in BossBar which shares title across all viewers,
 * this implementation creates separate instances per player to support
 * per-player placeholder resolution.
 * <p>
 * <b>Display Limits:</b> While the API allows unlimited bossbars per player,
 * the Minecraft client stacks bossbars vertically from the top of the screen.
 * Practically, 5-6 bossbars is the recommended maximum before they cover too much
 * screen space and overlap with other UI elements.
 * <p>
 * Example usage:
 * <pre>
 * Bossbar bar = Bossbars.create("&c&lBOSS FIGHT")
 *     .color(BarColor.RED)
 *     .style(BarStyle.SEGMENTED_10)
 *     .progress(1.0);
 *
 * bar.show(player);
 * </pre>
 */
public interface Bossbar {

    /**
     * Get the unique identifier for this bossbar.
     *
     * @return The bossbar ID
     */
    @NotNull
    String getId();

    /**
     * Set the title of this bossbar.
     * Supports color codes and PlaceholderAPI placeholders.
     *
     * @param title The title text
     * @return This bossbar for chaining
     */
    @NotNull
    Bossbar title(@NotNull String title);

    /**
     * Get the raw title template (before placeholder processing).
     *
     * @return The title template
     */
    @NotNull
    String getTitle();

    /**
     * Set the progress of this bossbar.
     *
     * @param progress Progress value between 0.0 and 1.0
     * @return This bossbar for chaining
     * @throws IllegalArgumentException if progress is not between 0.0 and 1.0
     */
    @NotNull
    Bossbar progress(double progress);

    /**
     * Get the current progress value.
     *
     * @return Progress between 0.0 and 1.0
     */
    double getProgress();

    /**
     * Set the color of this bossbar.
     *
     * @param color The bar color
     * @return This bossbar for chaining
     */
    @NotNull
    Bossbar color(@NotNull BarColor color);

    /**
     * Get the current color.
     *
     * @return The bar color
     */
    @NotNull
    BarColor getColor();

    /**
     * Set the style (segmentation) of this bossbar.
     *
     * @param style The bar style
     * @return This bossbar for chaining
     */
    @NotNull
    Bossbar style(@NotNull BarStyle style);

    /**
     * Get the current style.
     *
     * @return The bar style
     */
    @NotNull
    BarStyle getStyle();

    /**
     * Add a flag to this bossbar.
     *
     * @param flag The flag to add
     * @return This bossbar for chaining
     */
    @NotNull
    Bossbar addFlag(@NotNull BarFlag flag);

    /**
     * Remove a flag from this bossbar.
     *
     * @param flag The flag to remove
     * @return This bossbar for chaining
     */
    @NotNull
    Bossbar removeFlag(@NotNull BarFlag flag);

    /**
     * Set the flags for this bossbar, replacing any existing flags.
     *
     * @param flags The flags to set
     * @return This bossbar for chaining
     */
    @NotNull
    Bossbar flags(@NotNull BarFlag... flags);

    /**
     * Check if this bossbar has a specific flag.
     *
     * @param flag The flag to check
     * @return true if the flag is set
     */
    boolean hasFlag(@NotNull BarFlag flag);

    /**
     * Show this bossbar to a player.
     * If already showing to the player, this will update the display.
     *
     * @param player The player to show to
     */
    void show(@NotNull Player player);

    /**
     * Hide this bossbar from a player.
     *
     * @param player The player to hide from
     */
    void hide(@NotNull Player player);

    /**
     * Update this bossbar for a specific player.
     * Re-processes placeholders and applies any changes.
     *
     * @param player The player to update for
     */
    void update(@NotNull Player player);

    /**
     * Update this bossbar for all current viewers.
     */
    void updateAll();

    /**
     * Check if this bossbar is currently showing to a player.
     *
     * @param player The player to check
     * @return true if showing to the player
     */
    boolean isShowing(@NotNull Player player);

    /**
     * Get all players currently viewing this bossbar.
     *
     * @return Set of viewers (may be empty, never null)
     */
    @NotNull
    Set<Player> getViewers();

    /**
     * Enable auto-update for this bossbar.
     * Automatically refreshes placeholders at the specified interval.
     *
     * @param intervalTicks Update interval in ticks (20 ticks = 1 second)
     * @return This bossbar for chaining
     * @throws IllegalStateException if the bossbar is destroyed
     */
    @NotNull
    Bossbar autoUpdate(long intervalTicks);

    /**
     * Stop auto-updating this bossbar.
     *
     * @return This bossbar for chaining
     */
    @NotNull
    Bossbar stopAutoUpdate();

    /**
     * Check if this bossbar is auto-updating.
     *
     * @return true if auto-update is enabled
     */
    boolean isAutoUpdating();

    /**
     * Destroy this bossbar, removing it from all viewers and cleaning up resources.
     * A destroyed bossbar cannot be used again.
     */
    void destroy();

    /**
     * Check if this bossbar has been destroyed.
     *
     * @return true if destroyed
     */
    boolean isDestroyed();

    /**
     * Create a new bossbar with the specified title.
     *
     * @param title The title text
     * @return A new Bossbar instance
     */
    @NotNull
    static Bossbar create(@NotNull String title) {
        return new BossbarImpl(title);
    }

    /**
     * Create a new bossbar with the specified title and progress.
     *
     * @param title The title text
     * @param progress Progress value between 0.0 and 1.0
     * @return A new Bossbar instance
     */
    @NotNull
    static Bossbar create(@NotNull String title, double progress) {
        return new BossbarImpl(title).progress(progress);
    }

    /**
     * Create a new bossbar with the specified title and color.
     *
     * @param title The title text
     * @param color The bar color
     * @return A new Bossbar instance
     */
    @NotNull
    static Bossbar create(@NotNull String title, @NotNull BarColor color) {
        return new BossbarImpl(title).color(color);
    }

    /**
     * Create a new bossbar with the specified title, color, and style.
     *
     * @param title The title text
     * @param color The bar color
     * @param style The bar style
     * @return A new Bossbar instance
     */
    @NotNull
    static Bossbar create(@NotNull String title, @NotNull BarColor color, @NotNull BarStyle style) {
        return new BossbarImpl(title).color(color).style(style);
    }

}
