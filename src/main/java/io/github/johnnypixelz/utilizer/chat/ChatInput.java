package io.github.johnnypixelz.utilizer.chat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

public class ChatInput implements Listener {

    /**
     * The player that ChatInput will listen to
     */
    private Player player;

    /**
     * The plugin's instance
     */
    private JavaPlugin instance;

    /**
     * The consumer that will be called once a player sends a message
     */
    private Consumer<AsyncPlayerChatEvent> onChatEvent;

    /**
     * The consumer that will be called once a player sends a message but is also synchronized
     */
    private Consumer<AsyncPlayerChatEvent> onChatEventSynchronized;

    /**
     * @param plugin the main plugin instance
     * @param player the player that ChatInput will listen to
     */
    public ChatInput(JavaPlugin plugin, Player player) {
        this.player = player;
        this.instance = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Set the consumer that should be called whenever the player sends a message
     */
    public void setOnChatEvent(Consumer<AsyncPlayerChatEvent> onChatEvent) {
        this.onChatEvent = onChatEvent;
    }

    /**
     * Set the consumer that should be called whenever the player sends a message but is also synchronized
     */
    public void setOnChatEventSynchronized(Consumer<AsyncPlayerChatEvent> onChatEventSynchronized) {
        this.onChatEventSynchronized = onChatEventSynchronized;
    }

    /**
     * Handles the messages sent
     *
     * @param event the chat event
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    private void onAsyncChatEvent(AsyncPlayerChatEvent event) {
        if (!player.getUniqueId().equals(event.getPlayer().getUniqueId())) return;

        if (onChatEvent != null) {
            onChatEvent.accept(event);
            event.setCancelled(true);
        }

        if (onChatEventSynchronized != null) {
            Bukkit.getScheduler().runTask(instance, () -> onChatEventSynchronized.accept(event));
            event.setCancelled(true);
        }

        HandlerList.unregisterAll(this);
    }

    /**
     * Handles player disconnections
     *
     * @param event the disconnect event
     */
    @EventHandler
    private void onPlayerDisconnect(PlayerQuitEvent event) {
        if (player.getUniqueId().equals(event.getPlayer().getUniqueId())) {
            HandlerList.unregisterAll(this);
        }
    }

}
