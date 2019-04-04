package io.github.johnnypixelz.utilizer.messagelistener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;

public class MessageListener implements Listener {

    /**
     * The player that MessageListener will listen to
     */
    private Player player;

    /**
     * The consumer that will be called once a player sends a message
     */
    private Consumer<AsyncPlayerChatEvent> onChatEvent;

    /**
     * @param plugin the main plugin instance
     * @param player the player that MessageListener will listen to
     */
    public MessageListener(Plugin plugin, Player player) {
        this.player = player;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Set the consumer that should be called whenever the player sends a message
     */
    public void setOnChatEvent(Consumer<AsyncPlayerChatEvent> onChatEvent) {
        this.onChatEvent = onChatEvent;
    }

    /**
     * Handles the messages sent
     *
     * @param event the chat event
     */
    @EventHandler(ignoreCancelled = true)
    public void onAsyncChatEvent(AsyncPlayerChatEvent event) {
        if (!player.getUniqueId().equals(event.getPlayer().getUniqueId())) return;

        if (onChatEvent != null) {
            onChatEvent.accept(event);
            onChatEvent = null;
            HandlerList.unregisterAll(this);
        }
    }

}
