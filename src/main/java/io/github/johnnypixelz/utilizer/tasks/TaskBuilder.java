package io.github.johnnypixelz.utilizer.tasks;

import io.github.johnnypixelz.utilizer.tasks.schedulers.Scheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

public class TaskBuilder {
    private long timer;
    private long iterations;
    private long delay;

    public TaskBuilder() {
        this.timer = 0;
        this.iterations = 0;
        this.delay = 0;
    }

    public TaskBuilder timer(long timer) {
        this.timer = timer;
        return this;
    }

    public TaskBuilder iterations(long iterations) {
        this.iterations = iterations;
        return this;
    }

    public TaskBuilder delay(long delay) {
        this.delay = delay;
        return this;
    }

    public BukkitTask sync(Consumer<BukkitTask> task) {
        return execute(task, Tasks.sync());
    }

    public BukkitTask async(Consumer<BukkitTask> task) {
        return execute(task, Tasks.async());
    }

    private BukkitTask execute(Consumer<BukkitTask> task, Scheduler scheduler) {
        if (delay <= 0 && timer <= 0 && iterations <= 0) {
            return scheduler.run(task);
        } else if (timer <= 0 && iterations <= 0) {
            return scheduler.delayed(task, delay);
        } else if (delay <= 0 && timer <= 0) {
            return scheduler.timed(task, 1L, iterations);
        } else if (delay <= 0 && iterations <= 0) {
            return scheduler.timer(task, timer);
        } else if (delay <= 0) {
            return scheduler.timed(task, timer, iterations);
        } else if (iterations <= 0) {
            return scheduler.delayedTimer(task, delay, timer);
        } else {
            return scheduler.delayedTimed(task, delay, timer, iterations);
        }
    }

}
