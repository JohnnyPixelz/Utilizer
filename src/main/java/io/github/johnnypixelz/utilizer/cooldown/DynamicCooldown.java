package io.github.johnnypixelz.utilizer.cooldown;

import io.github.johnnypixelz.utilizer.plugin.Provider;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DynamicCooldown<T> extends Cooldown<T> {
    private final transient BukkitTask cooldownTask;
    private transient Consumer<T> onDone;
    private transient Consumer<T> onDoneAsync;

    public DynamicCooldown() {
        cooldownTask = Bukkit.getScheduler().runTaskTimerAsynchronously(Provider.getPlugin(), () -> {
            List<T> toRemove = new ArrayList<>();

            cooldownMap.forEach((object, ms) -> {
                if (ms < System.currentTimeMillis()) {
                    toRemove.add(object);
                }
            });

            toRemove.forEach(this::remove);
        }, 0, 1);
    }

    @Override
    public void remove(T object) {
        if (onDone != null) {
            Bukkit.getScheduler().runTask(Provider.getPlugin(), () -> onDone.accept(object));
        }

        if (onDoneAsync != null) {
            onDoneAsync.accept(object);
        }
        cooldownMap.remove(object);
    }

    public void removeWithoutExecuting(T object) {
        cooldownMap.remove(object);
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
