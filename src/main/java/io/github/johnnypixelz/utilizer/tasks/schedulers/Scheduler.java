package io.github.johnnypixelz.utilizer.tasks.schedulers;

import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

public interface Scheduler {

    BukkitTask run(Consumer<BukkitTask> task);

    BukkitTask run(Runnable task);

    BukkitTask delayed(Consumer<BukkitTask> task, long delay);

    BukkitTask delayed(Runnable task, long delay);

    BukkitTask timer(Consumer<BukkitTask> task, long timer);

    BukkitTask timer(Runnable task, long timer);

    BukkitTask timed(Consumer<BukkitTask> task, long timer, long iterations);

    BukkitTask timed(Runnable task, long timer, long iterations);

    BukkitTask delayedTimer(Consumer<BukkitTask> task, long delay, long timer);

    BukkitTask delayedTimer(Runnable task, long delay, long timer);

    BukkitTask delayedTimed(Consumer<BukkitTask> task, long delay, long timer, long iterations);

    BukkitTask delayedTimed(Runnable task, long delay, long timer, long iterations);

}
