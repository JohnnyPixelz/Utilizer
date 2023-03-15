package io.github.johnnypixelz.utilizer.tasks;

import io.github.johnnypixelz.utilizer.tasks.schedulers.AsyncScheduler;
import io.github.johnnypixelz.utilizer.tasks.schedulers.SyncScheduler;

public class Tasks {

    public static TaskBuilder builder() {
        return new TaskBuilder();
    }

    public static SyncScheduler sync() {
        return new SyncScheduler();
    }

    public static AsyncScheduler async() {
        return new AsyncScheduler();
    }

}
