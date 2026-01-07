package io.github.johnnypixelz.utilizer.bossbar;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Internal service for managing bossbar lifecycle and player associations.
 * Unlike ScoreboardService, players can view multiple bossbars simultaneously.
 */
public class BossbarService {

    private final Plugin plugin;
    private final Map<String, Bossbar> bossbars;
    private final Map<UUID, Set<Bossbar>> playerBossbars;
    private boolean listenerRegistered;

    public BossbarService(@NotNull Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "Plugin cannot be null");
        this.bossbars = new HashMap<>();
        this.playerBossbars = new HashMap<>();
        this.listenerRegistered = false;
    }

    /**
     * Register a bossbar with the service.
     *
     * @param bossbar The bossbar to register
     */
    void register(@NotNull Bossbar bossbar) {
        Objects.requireNonNull(bossbar, "Bossbar cannot be null");
        bossbars.put(bossbar.getId(), bossbar);
        ensureListenerRegistered();
    }

    /**
     * Unregister a bossbar from the service.
     *
     * @param bossbar The bossbar to unregister
     */
    void unregister(@NotNull Bossbar bossbar) {
        Objects.requireNonNull(bossbar, "Bossbar cannot be null");
        bossbars.remove(bossbar.getId());
    }

    /**
     * Register a player as viewing a bossbar.
     *
     * @param player The player
     * @param bossbar The bossbar being viewed
     */
    void registerPlayer(@NotNull Player player, @NotNull Bossbar bossbar) {
        Objects.requireNonNull(player, "Player cannot be null");
        Objects.requireNonNull(bossbar, "Bossbar cannot be null");

        playerBossbars.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>())
                .add(bossbar);
    }

    /**
     * Unregister a player from viewing a specific bossbar.
     *
     * @param player The player
     * @param bossbar The bossbar to unregister from
     */
    void unregisterPlayer(@NotNull Player player, @NotNull Bossbar bossbar) {
        Objects.requireNonNull(player, "Player cannot be null");
        Objects.requireNonNull(bossbar, "Bossbar cannot be null");

        Set<Bossbar> bars = playerBossbars.get(player.getUniqueId());
        if (bars != null) {
            bars.remove(bossbar);
            if (bars.isEmpty()) {
                playerBossbars.remove(player.getUniqueId());
            }
        }
    }

    /**
     * Hide all bossbars from a player.
     *
     * @param player The player to hide bossbars from
     */
    public void hideAllFromPlayer(@NotNull Player player) {
        Objects.requireNonNull(player, "Player cannot be null");

        Set<Bossbar> bars = playerBossbars.get(player.getUniqueId());
        if (bars != null) {
            // Create copy to avoid concurrent modification
            for (Bossbar bossbar : new HashSet<>(bars)) {
                bossbar.hide(player);
            }
        }
    }

    /**
     * Called when a player quits the server.
     *
     * @param player The player who quit
     */
    void onPlayerQuit(@NotNull Player player) {
        Objects.requireNonNull(player, "Player cannot be null");

        Set<Bossbar> bars = playerBossbars.remove(player.getUniqueId());
        if (bars != null) {
            for (Bossbar bossbar : bars) {
                if (bossbar instanceof BossbarImpl) {
                    ((BossbarImpl) bossbar).onPlayerQuit(player);
                }
            }
        }
    }

    /**
     * Get a bossbar by ID.
     *
     * @param id The bossbar ID
     * @return The bossbar, or null if not found
     */
    @Nullable
    public Bossbar getBossbar(@NotNull String id) {
        Objects.requireNonNull(id, "ID cannot be null");
        return bossbars.get(id);
    }

    /**
     * Get all bossbars a player is viewing.
     *
     * @param player The player
     * @return List of bossbars (may be empty, never null)
     */
    @NotNull
    public List<Bossbar> getPlayerBossbars(@NotNull Player player) {
        Objects.requireNonNull(player, "Player cannot be null");
        Set<Bossbar> bars = playerBossbars.get(player.getUniqueId());
        return bars != null ? new ArrayList<>(bars) : Collections.emptyList();
    }

    /**
     * Check if a player is viewing any bossbars.
     *
     * @param player The player
     * @return true if viewing at least one bossbar
     */
    public boolean hasPlayerBossbars(@NotNull Player player) {
        Objects.requireNonNull(player, "Player cannot be null");
        Set<Bossbar> bars = playerBossbars.get(player.getUniqueId());
        return bars != null && !bars.isEmpty();
    }

    /**
     * Get all registered bossbars.
     *
     * @return Unmodifiable collection of all bossbars
     */
    @NotNull
    public Collection<Bossbar> getAllBossbars() {
        return Collections.unmodifiableCollection(bossbars.values());
    }

    /**
     * Get the number of registered bossbars.
     *
     * @return Bossbar count
     */
    public int getBossbarCount() {
        return bossbars.size();
    }

    /**
     * Shutdown the service, destroying all bossbars.
     */
    public void shutdown() {
        // Destroy all bossbars
        for (Bossbar bossbar : new ArrayList<>(bossbars.values())) {
            bossbar.destroy();
        }
        bossbars.clear();
        playerBossbars.clear();
    }

    private void ensureListenerRegistered() {
        if (!listenerRegistered) {
            Bukkit.getPluginManager().registerEvents(new BossbarEventListener(this), plugin);
            listenerRegistered = true;
        }
    }

}
