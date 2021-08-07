package io.github.johnnypixelz.utilizer.input;

import io.github.johnnypixelz.utilizer.Scheduler;
import io.github.johnnypixelz.utilizer.plugin.Provider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.function.Consumer;

public class ChatInput implements Listener {

    /**
     * The player that ChatInput will listen to
     */
    private final Player player;

    /**
     * The consumer that will be called once a player sends a message
     */
    private Consumer<String> messageAsync;

    /**
     * The consumer that will be called once a player sends a message but is also synchronized
     */
    private Consumer<String> messageSync;

    /**
     * @param player the player that ChatInput will listen to
     */
    private ChatInput(Player player) {
        this.player = player;

        Bukkit.getPluginManager().registerEvents(this, Provider.getPlugin());
    }

    public static void sync(Player player, Consumer<String> message) {
        ChatInput chatInput = new ChatInput(player);
        chatInput.setMessageSync(message);
    }

    public static void async(Player player, Consumer<String> message) {
        ChatInput chatInput = new ChatInput(player);
        chatInput.setMessageAsync(message);
    }

    public void setMessageAsync(Consumer<String> messageAsync) {
        this.messageAsync = messageAsync;
    }

    public void setMessageSync(Consumer<String> messageSync) {
        this.messageSync = messageSync;
    }

    /**
     * Handles the messages sent
     *
     * @param event the chat event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void onAsyncChatEvent(AsyncPlayerChatEvent event) {
        if (!player.getUniqueId().equals(event.getPlayer().getUniqueId())) return;

        if (messageAsync != null) {
            messageAsync.accept(event.getMessage());
            event.setCancelled(true);
        }

        if (messageSync != null) {
            Scheduler.sync(() -> messageSync.accept(event.getMessage()));
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
