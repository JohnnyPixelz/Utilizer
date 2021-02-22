package io.github.johnnypixelz.utilizer;

import io.github.johnnypixelz.utilizer.provider.Provider;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Scheduler {

    public BukkitTask sync(Runnable runnable) {
        return Bukkit.getScheduler().runTask(Provider.getPlugin(), runnable);
    }

    public BukkitTask syncDelayed(Runnable runnable, long delay) {
        return Bukkit.getScheduler().runTaskLater(Provider.getPlugin(), runnable, delay);
    }

    public BukkitTask syncDelayedTimer(Runnable runnable, long delay, long timer) {
        return Bukkit.getScheduler().runTaskTimer(Provider.getPlugin(), runnable, delay, timer);
    }

    public BukkitTask syncTimer(Runnable runnable, long timer) {
        return Bukkit.getScheduler().runTaskTimer(Provider.getPlugin(), runnable, 0L, timer);
    }

    public BukkitTask syncDelayedTimed(Runnable runnable, long delay, long timer, int iterations) {
        return new BukkitRunnable() {
            int iter = iterations;

            @Override
            public void run() {
                if (iter-- <= 0) {
                    cancel();
                    return;
                }

                runnable.run();
            }
        }.runTaskTimer(Provider.getPlugin(), delay, timer);
    }

    public BukkitTask syncTimed(Runnable runnable, long timer, int iterations) {
        return syncDelayedTimed(runnable, 0L, timer, iterations);
    }

    public BukkitTask async(Runnable runnable) {
        return Bukkit.getScheduler().runTaskAsynchronously(Provider.getPlugin(), runnable);
    }

    public BukkitTask asyncDelayed(Runnable runnable, long delay) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(Provider.getPlugin(), runnable, delay);
    }

    public BukkitTask asyncDelayedTimer(Runnable runnable, long delay, long timer) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(Provider.getPlugin(), runnable, delay, timer);
    }

    public BukkitTask asyncTimer(Runnable runnable, long timer) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(Provider.getPlugin(), runnable, 0L, timer);
    }

    public BukkitTask asyncDelayedTimed(Runnable runnable, long delay, long timer, int iterations) {
        return new BukkitRunnable() {
            int iter = iterations;

            @Override
            public void run() {
                if (iter-- <= 0) {
                    cancel();
                    return;
                }

                runnable.run();
            }
        }.runTaskTimerAsynchronously(Provider.getPlugin(), delay, timer);
    }

    public BukkitTask asyncTimed(Runnable runnable, long timer, int iterations) {
        return asyncDelayedTimed(runnable, 0L, timer, iterations);
    }

}