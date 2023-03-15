package io.github.johnnypixelz.utilizer.tasks.schedulers;

import io.github.johnnypixelz.utilizer.plugin.Provider;
import io.github.johnnypixelz.utilizer.tasks.Task;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class AsyncScheduler implements Scheduler {

    @Override
    public BukkitTask run(Consumer<BukkitTask> task) {
        return Task.of(task).runTaskAsynchronously(Provider.getPlugin());
    }

    @Override
    public BukkitTask delayed(Consumer<BukkitTask> task, long delay) {
        return Task.of(task).runTaskLaterAsynchronously(Provider.getPlugin(), delay);
    }

    @Override
    public BukkitTask timer(Consumer<BukkitTask> task, long timer) {
        return Task.of(task).runTaskTimerAsynchronously(Provider.getPlugin(), 0L, timer);
    }

    @Override
    public BukkitTask timed(Consumer<BukkitTask> task, long timer, long iterations) {
        return delayedTimed(task, 0L, timer, iterations);
    }

    @Override
    public BukkitTask delayedTimer(Consumer<BukkitTask> task, long delay, long timer) {
        return Task.of(task).runTaskTimerAsynchronously(Provider.getPlugin(), delay, timer);
    }

    @Override
    public BukkitTask delayedTimed(Consumer<BukkitTask> task, long delay, long timer, long iterations) {
        AtomicLong atomicLong = new AtomicLong(iterations);
        return Task.of(bukkitTask -> {
                    if (atomicLong.getAndDecrement() <= 0) {
                        bukkitTask.cancel();
                        return;
                    }

                    task.accept(bukkitTask);
                })
                .runTaskTimerAsynchronously(Provider.getPlugin(), 0L, timer);
    }

}
