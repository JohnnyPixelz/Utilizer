package io.github.johnnypixelz.utilizer.event;

import org.bukkit.event.EventPriority;

import java.util.function.BiConsumer;

public class BiStatefulEventListener<T1, T2> {
    private final BiStatefulEventEmitter<T1, T2> eventEmitter;
    private final BiConsumer<T1, T2> consumer;
    private final EventPriority priority;

    BiStatefulEventListener(BiStatefulEventEmitter<T1, T2> eventEmitter, BiConsumer<T1, T2> consumer, EventPriority priority) {
        this.eventEmitter = eventEmitter;
        this.consumer = consumer;
        this.priority = priority;
    }

    public BiStatefulEventEmitter<T1, T2> getEventEmitter() {
        return eventEmitter;
    }

    public BiConsumer<T1, T2> getConsumer() {
        return consumer;
    }

    public EventPriority getPriority() {
        return priority;
    }

    public BiStatefulEventEmitter<T1, T2> unregister() {
        eventEmitter.unregister(this);
        return eventEmitter;
    }

}
