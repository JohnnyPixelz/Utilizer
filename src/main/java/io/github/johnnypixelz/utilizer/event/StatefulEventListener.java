package io.github.johnnypixelz.utilizer.event;

import org.bukkit.event.EventPriority;

import java.util.function.Consumer;

public class StatefulEventListener<T> {
    private final StatefulEventEmitter<T> eventEmitter;
    private final Consumer<T> consumer;
    private final EventPriority priority;

    StatefulEventListener(StatefulEventEmitter<T> eventEmitter, Consumer<T> consumer, EventPriority priority) {
        this.eventEmitter = eventEmitter;
        this.consumer = consumer;
        this.priority = priority;
    }

    public StatefulEventEmitter<T> getEventEmitter() {
        return eventEmitter;
    }

    public Consumer<T> getConsumer() {
        return consumer;
    }

    public EventPriority getPriority() {
        return priority;
    }

    public StatefulEventEmitter<T> unregister() {
        eventEmitter.unregister(this);
        return eventEmitter;
    }

}
