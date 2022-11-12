package io.github.johnnypixelz.utilizer.event;

import org.bukkit.event.EventPriority;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class StatelessEventEmitter {
    private final Map<Runnable, EventPriority> listeners;

    public StatelessEventEmitter() {
        this.listeners = new HashMap<>();
    }

    public void emit() {
        listeners.entrySet()
                .stream()
                .sorted(Comparator.comparingInt(o -> o.getValue().getSlot()))
                .map(Map.Entry::getKey)
                .forEachOrdered(Runnable::run);
    }

    public void listen(Runnable listener) {
        listen(listener, EventPriority.NORMAL);
    }

    public void listen(Runnable listener, EventPriority priority) {
        listeners.put(listener, priority);
    }

}
