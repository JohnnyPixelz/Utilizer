package io.github.johnnypixelz.utilizer.cooldown;

import io.github.johnnypixelz.utilizer.provider.Provider;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class DynamicCooldown extends Cooldown {
    private BukkitTask cooldownTask;
    private Consumer<UUID> onDone;
    private Consumer<UUID> onDoneAsync;

    public DynamicCooldown() {
        cooldownTask = Bukkit.getScheduler().runTaskTimerAsynchronously(Provider.getPlugin(), () -> {
            List<UUID> toRemove = new ArrayList<>();

            cooldowns.forEach((uuid, ms) -> {
                if (ms < System.currentTimeMillis()) {
                    toRemove.add(uuid);
                }
            });

            toRemove.forEach(uuid -> remove(uuid));
        }, 0, 1);
    }

    private void remove(UUID uuid) {
        if (onDone != null) {
            Bukkit.getScheduler().runTask(Provider.getPlugin(), () -> onDone.accept(uuid));
        }

        if (onDoneAsync != null) {
            onDoneAsync.accept(uuid);
        }
        cooldowns.remove(uuid);
    }

    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent event) {
        remove(event.getPlayer().getUniqueId());
    }

    public void terminate() {
        cooldownTask.cancel();
    }

    public void setOnDone(Consumer<UUID> onDone) {
        this.onDone = onDone;
    }

    public void setOnDoneAsync(Consumer<UUID> onDoneAsync) {
        this.onDoneAsync = onDoneAsync;
    }

}
