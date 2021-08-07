package io.github.johnnypixelz.utilizer.cooldown;

import io.github.johnnypixelz.utilizer.plugin.Provider;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DynamicCooldown<T> extends Cooldown<T> {
    private BukkitTask cooldownTask;
    private Consumer<T> onDone;
    private Consumer<T> onDoneAsync;

    public DynamicCooldown() {
        cooldownTask = Bukkit.getScheduler().runTaskTimerAsynchronously(Provider.getPlugin(), () -> {
            List<T> toRemove = new ArrayList<>();

            cooldowns.forEach((t, ms) -> {
                if (ms < System.currentTimeMillis()) {
                    toRemove.add(t);
                }
            });

            toRemove.forEach(this::remove);
        }, 0, 1);
    }

    @Override
    public void remove(T t) {
        if (onDone != null) {
            Bukkit.getScheduler().runTask(Provider.getPlugin(), () -> onDone.accept(t));
        }

        if (onDoneAsync != null) {
            onDoneAsync.accept(t);
        }
        cooldowns.remove(t);
    }

    public void removeWithoutExecuting(T t) {
        cooldowns.remove(t);
    }

    public void terminate() {
        cooldownTask.cancel();
    }

    public void setOnDone(Consumer<T> onDone) {
        this.onDone = onDone;
    }

    public void setOnDoneAsync(Consumer<T> onDoneAsync) {
        this.onDoneAsync = onDoneAsync;
    }

}
