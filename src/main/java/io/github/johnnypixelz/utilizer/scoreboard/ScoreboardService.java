package io.github.johnnypixelz.utilizer.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Service for managing scoreboards.
 * Handles registration, lookup, and player lifecycle events.
 *
 * <p>For static access, use the {@link Scoreboards} class instead.
 */
public class ScoreboardService {

    private final Plugin plugin;
    private final Map<String, Scoreboard> scoreboards;
    private final Map<UUID, Scoreboard> playerScoreboards;
    private boolean listenerRegistered;

    public ScoreboardService(Plugin plugin) {
        this.plugin = plugin;
        this.scoreboards = new HashMap<>();
        this.playerScoreboards = new HashMap<>();
        this.listenerRegistered = false;
    }

    /**
     * Register a scoreboard with the service.
     *
     * @param scoreboard The scoreboard to register
     */
    void register(@NotNull Scoreboard scoreboard) {
        ensureListenerRegistered();
        scoreboards.put(scoreboard.getId(), scoreboard);
    }

    /**
     * Unregister a scoreboard from the service.
     *
     * @param scoreboard The scoreboard to unregister
     */
    void unregister(@NotNull Scoreboard scoreboard) {
        scoreboards.remove(scoreboard.getId());
    }

    /**
     * Register a player as viewing a scoreboard.
     *
     * @param player     The player
     * @param scoreboard The scoreboard they're viewing
     */
    void registerPlayer(@NotNull Player player, @NotNull Scoreboard scoreboard) {
        playerScoreboards.put(player.getUniqueId(), scoreboard);
    }

    /**
     * Unregister a player from viewing any scoreboard.
     *
     * @param player The player
     */
    void unregisterPlayer(@NotNull Player player) {
        playerScoreboards.remove(player.getUniqueId());
    }

    /**
     * Hide any scoreboard currently showing to a player.
     *
     * @param player The player
     */
    void hideFromPlayer(@NotNull Player player) {
        Scoreboard current = playerScoreboards.get(player.getUniqueId());
        if (current != null) {
            current.hide(player);
        }
    }

    /**
     * Called when a player quits the server.
     *
     * @param player The player who quit
     */
    void onPlayerQuit(@NotNull Player player) {
        Scoreboard scoreboard = playerScoreboards.remove(player.getUniqueId());
        if (scoreboard instanceof ScoreboardImpl) {
            ((ScoreboardImpl) scoreboard).onPlayerQuit(player);
        }
    }

    /**
     * Get a scoreboard by ID.
     *
     * @param id Scoreboard ID
     * @return The scoreboard, or null if not found
     */
    @Nullable
    public Scoreboard getScoreboard(@NotNull String id) {
        return scoreboards.get(id);
    }

    /**
     * Get the scoreboard currently showing to a player.
     *
     * @param player The player
     * @return The scoreboard, or null if none
     */
    @Nullable
    public Scoreboard getPlayerScoreboard(@NotNull Player player) {
        return playerScoreboards.get(player.getUniqueId());
    }

    /**
     * Check if a player has a scoreboard showing.
     *
     * @param player The player
     * @return true if they have a scoreboard
     */
    public boolean hasPlayerScoreboard(@NotNull Player player) {
        return playerScoreboards.containsKey(player.getUniqueId());
    }

    /**
     * Get all registered scoreboards.
     *
     * @return Unmodifiable collection of scoreboards
     */
    @NotNull
    public Collection<Scoreboard> getAllScoreboards() {
        return Collections.unmodifiableCollection(scoreboards.values());
    }

    /**
     * Get the number of active scoreboards.
     *
     * @return Scoreboard count
     */
    public int getScoreboardCount() {
        return scoreboards.size();
    }

    /**
     * Destroy all scoreboards and cleanup resources.
     */
    public void shutdown() {
        for (Scoreboard scoreboard : new ArrayList<>(scoreboards.values())) {
            scoreboard.destroy();
        }
        scoreboards.clear();
        playerScoreboards.clear();
    }

    private void ensureListenerRegistered() {
        if (!listenerRegistered) {
            Bukkit.getPluginManager().registerEvents(
                    new ScoreboardEventListener(this),
                    plugin
            );
            listenerRegistered = true;
        }
    }

}
