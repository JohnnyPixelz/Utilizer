package io.github.johnnypixelz.utilizer.minigame;

import io.github.johnnypixelz.utilizer.Scheduler;
import io.github.johnnypixelz.utilizer.minigame.arena.Arena;
import io.github.johnnypixelz.utilizer.plugin.Provider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class Minigame<ExtendedArena extends Arena> implements Listener {
    // State variables
    private GameState state;
    private final List<UUID> players;
    private final List<MinigameModule> modules;
    private final ExtendedArena arena;
    private final MinigameEventManager eventManager;

    // Internal Variables (time values in seconds)
    protected int maximumPlayerLimit = 4;
    protected int postGameCleanUpThreshold = 7;

    public Minigame(ExtendedArena arena) {
        Bukkit.getPluginManager().registerEvents(this, Provider.getPlugin());
        this.state = GameState.WAITING;
        this.eventManager = new MinigameEventManager();
        this.arena = arena;
        this.players = new ArrayList<>();
        this.modules = new ArrayList<>();
    }

    // Module methods

    public void registerModules(MinigameModule... modules) {
        for (MinigameModule module : modules) {
            registerModule(module);
        }
    }

    public void registerModule(MinigameModule module) {
        module.injectMinigameInstance(this);
        modules.add(module);
        Bukkit.getPluginManager().registerEvents(module, Provider.getPlugin());

        module.init();
    }

    private void unregisterModule(MinigameModule module) {
        HandlerList.unregisterAll(module);
    }

    protected List<MinigameModule> getModules() {
        return modules;
    }

    protected <T extends MinigameModule> T getModule(Class<T> module) {
        for (MinigameModule minigameModule : getModules()) {
            if (minigameModule.getClass() == module) {
                return (T) minigameModule;
            }
        }

        return null;
    }

    public MinigameEventManager getEventManager() {
        return eventManager;
    }

    // Minigame management methods

    /**
     * Adds player to the minigame
     *
     * @param player target player
     */
    public void join(Player player) {
        if (state != GameState.WAITING) {
            throw new IllegalStateException("An attempt to join a player to a minigame was made while the minigame had started.");
        }

        if (players.contains(player.getUniqueId())) {
            throw new IllegalStateException("An attempt to join a player to a minigame was made while the minigame already contained that player.");
        }

        players.add(player.getUniqueId());
        eventManager.getOnPlayerJoin().emit(player);
    }

    /**
     * Removes player from the minigame
     * Called at cleanup or disconnection of the player
     *
     * @param player target player
     */
    public void remove(Player player) {
        players.remove(player.getUniqueId());
        eventManager.getOnPlayerRemove().emit(player);
    }

    public void start() {
        if (state != GameState.WAITING) {
            throw new IllegalStateException("An attempt to start a minigame was made while the minigame had already started.");
        }

        state = GameState.STARTED;
        eventManager.getOnMinigameStart().emit();
    }

    /**
     * Marks the game as finished and
     * starts the cleanup task
     */
    public void finish() {
        if (state != GameState.STARTED) return;
        state = GameState.FINISHED;
        eventManager.getOnMinigameFinish().emit();

        Scheduler.syncDelayed(this::cleanUp, postGameCleanUpThreshold * 20L);
    }

    private void cleanUp() {
        getPlayerObjects().forEach(this::remove);

        eventManager.getOnMinigameCleanup().emit();
        modules.forEach(this::unregisterModule);

        HandlerList.unregisterAll(this);
        state = GameState.CLEANED_UP;
    }

    public boolean isJoinable() {
        return state == GameState.WAITING
                && getPlayerCount() < getPlayerLimit();
    }

    public int getPlayerLimit() {
        return maximumPlayerLimit;
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public List<Player> getPlayerObjects() {
        return players.stream()
                .map(Bukkit::getPlayer)
                .collect(Collectors.toList());
    }

    public ExtendedArena getArena() {
        return arena;
    }

    public GameState getState() {
        return state;
    }

    public int getPlayerCount() {
        return players.size();
    }

    // Event Listeners

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!players.contains(event.getPlayer().getUniqueId())) return;

        eventManager.getOnPlayerDisconnect().emit(event.getPlayer());
        remove(event.getPlayer());
    }

    public enum GameState {
        WAITING,
        STARTED,
        FINISHED,
        CLEANED_UP;
    }

}
