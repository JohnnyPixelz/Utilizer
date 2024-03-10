package io.github.johnnypixelz.utilizer.tasks;

import io.github.johnnypixelz.utilizer.cache.Cache;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Field;
import java.util.function.Consumer;

public class Task extends BukkitRunnable {
    private final Cache<BukkitTask> taskCache;
    private final Field taskField;

    public static Task of(Consumer<BukkitTask> consumer) {
        return new Task(consumer);
    }

    public static Task of(Runnable runnable) {
        return new Task(bukkitTask -> runnable.run());
    }

    private final Consumer<BukkitTask> consumer;

    private Task(Consumer<BukkitTask> consumer) {
        this.consumer = consumer;

        try {
            taskField = getClass().getSuperclass().getDeclaredField("task");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        this.taskCache = Cache.suppliedBy(() -> {
            try {
                taskField.setAccessible(true);
                final Object o = taskField.get(this);
                taskField.setAccessible(false);

                return (BukkitTask) o;
            } catch (IllegalAccessException | ClassCastException e) {
                return null;
            }
        });
    }

    @Override
    public void run() {
        consumer.accept(taskCache.get());
    }

}
