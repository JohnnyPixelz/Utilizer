package io.github.johnnypixelz.utilizer.bossbar;

import io.github.johnnypixelz.utilizer.plugin.Provider;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

/**
 * Static utility class for managing bossbars.
 * Provides factory methods and convenience functions for bossbar operations.
 * <p>
 * Example usage:
 * <pre>
 * // Create a simple bossbar
 * Bossbar bar = Bossbars.create("&c&lBOSS FIGHT")
 *     .color(BarColor.RED)
 *     .style(BarStyle.SEGMENTED_10)
 *     .progress(1.0);
 *
 * bar.show(player);
 *
 * // With placeholders and auto-update
 * Bossbar levelBar = Bossbars.create("&eLevel: &f%player_level%")
 *     .color(BarColor.YELLOW)
 *     .autoUpdate(20L);  // Update every second
 *
 * levelBar.show(player);
 *
 * // Multiple bossbars for same player
 * Bossbar quest = Bossbars.create("&aQuest Progress")
 *     .color(BarColor.GREEN)
 *     .progress(0.5);
 *
 * Bossbar event = Bossbars.create("&dDouble XP Event!")
 *     .color(BarColor.PINK);
 *
 * quest.show(player);
 * event.show(player);  // Both visible
 *
 * // Query player's bossbars
 * List&lt;Bossbar&gt; bars = Bossbars.getFor(player);
 *
 * // Hide all from player
 * Bossbars.hideAll(player);
 *
 * // Cleanup
 * bar.destroy();
 * Bossbars.shutdown();
 * </pre>
 */
public final class Bossbars {

    private static BossbarService service;

    private Bossbars() {
    }

    /**
     * Get or create the bossbar service for the current plugin.
     *
     * @return The bossbar service
     */
    public static synchronized BossbarService service() {
        if (service == null) {
            service = new BossbarService(Provider.getPlugin());
        }
        return service;
    }

    // ==================== Factory Methods ====================

    /**
     * Create a new bossbar with the specified title.
     *
     * @param title The title text (supports color codes and placeholders)
     * @return A new Bossbar instance
     */
    @NotNull
    public static Bossbar create(@NotNull String title) {
        return Bossbar.create(title);
    }

    /**
     * Create a new bossbar with the specified title and progress.
     *
     * @param title The title text
     * @param progress Progress value between 0.0 and 1.0
     * @return A new Bossbar instance
     */
    @NotNull
    public static Bossbar create(@NotNull String title, double progress) {
        return Bossbar.create(title, progress);
    }

    /**
     * Create a new bossbar with the specified title and color.
     *
     * @param title The title text
     * @param color The bar color
     * @return A new Bossbar instance
     */
    @NotNull
    public static Bossbar create(@NotNull String title, @NotNull BarColor color) {
        return Bossbar.create(title, color);
    }

    /**
     * Create a new bossbar with the specified title, color, and style.
     *
     * @param title The title text
     * @param color The bar color
     * @param style The bar style (segmentation)
     * @return A new Bossbar instance
     */
    @NotNull
    public static Bossbar create(@NotNull String title, @NotNull BarColor color, @NotNull BarStyle style) {
        return Bossbar.create(title, color, style);
    }

    // ==================== Display Management ====================

    /**
     * Show a bossbar to a player.
     *
     * @param player The player to show to
     * @param bossbar The bossbar to show
     */
    public static void show(@NotNull Player player, @NotNull Bossbar bossbar) {
        bossbar.show(player);
    }

    /**
     * Hide a specific bossbar from a player.
     *
     * @param player The player to hide from
     * @param bossbar The bossbar to hide
     */
    public static void hide(@NotNull Player player, @NotNull Bossbar bossbar) {
        bossbar.hide(player);
    }

    /**
     * Hide all bossbars from a player.
     *
     * @param player The player to hide all bossbars from
     */
    public static void hideAll(@NotNull Player player) {
        service().hideAllFromPlayer(player);
    }

    /**
     * Update all bossbars for a player (re-process placeholders).
     *
     * @param player The player to update for
     */
    public static void update(@NotNull Player player) {
        for (Bossbar bossbar : service().getPlayerBossbars(player)) {
            bossbar.update(player);
        }
    }

    // ==================== Query Methods ====================

    /**
     * Get all bossbars currently shown to a player.
     *
     * @param player The player
     * @return List of bossbars (may be empty, never null)
     */
    @NotNull
    public static List<Bossbar> getFor(@NotNull Player player) {
        return service().getPlayerBossbars(player);
    }

    /**
     * Check if a player is viewing any bossbars.
     *
     * @param player The player
     * @return true if viewing at least one bossbar
     */
    public static boolean isShowing(@NotNull Player player) {
        return service().hasPlayerBossbars(player);
    }

    /**
     * Get all registered bossbars.
     *
     * @return Unmodifiable collection of all bossbars
     */
    @NotNull
    public static Collection<Bossbar> getAll() {
        return service().getAllBossbars();
    }

    /**
     * Get the number of registered bossbars.
     *
     * @return Bossbar count
     */
    public static int count() {
        return service().getBossbarCount();
    }

    // ==================== Lifecycle ====================

    /**
     * Shutdown the bossbar service, destroying all bossbars.
     * Should be called when the plugin is disabled.
     */
    public static void shutdown() {
        if (service != null) {
            service.shutdown();
            service = null;
        }
    }

}
