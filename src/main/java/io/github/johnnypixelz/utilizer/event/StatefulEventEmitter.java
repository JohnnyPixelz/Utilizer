package io.github.johnnypixelz.utilizer.event;

import org.bukkit.event.EventPriority;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class StatefulEventEmitter<T> {
    private final Map<Consumer<T>, EventPriority> listeners;

    public StatefulEventEmitter() {
        this.listeners = new HashMap<>();
    }

    public void emit(T data) {
        listeners.entrySet()
                .stream()
                .sorted(Comparator.comparingInt(o -> o.getValue().getSlot()))
                .map(Map.Entry::getKey)
                .forEachOrdered(consumer -> consumer.accept(data));
    }

    public void listen(Consumer<T> listener) {
        listen(listener, EventPriority.NORMAL);
    }

    public void listen(Consumer<T> listener, EventPriority priority) {
        listeners.put(listener, priority);
    }

}
