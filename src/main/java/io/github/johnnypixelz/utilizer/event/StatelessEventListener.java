package io.github.johnnypixelz.utilizer.event;

import org.bukkit.event.EventPriority;

public class StatelessEventListener {
    private final StatelessEventEmitter eventEmitter;
    private final Runnable runnable;
    private final EventPriority priority;

    StatelessEventListener(StatelessEventEmitter eventEmitter, Runnable runnable, EventPriority priority) {
        this.eventEmitter = eventEmitter;
        this.runnable = runnable;
        this.priority = priority;
    }

    public StatelessEventEmitter getEventEmitter() {
        return eventEmitter;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public EventPriority getPriority() {
        return priority;
    }

    public StatelessEventEmitter unregister() {
        eventEmitter.unregister(this);
        return eventEmitter;
    }

}
