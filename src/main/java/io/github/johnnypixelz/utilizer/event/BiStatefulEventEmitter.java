package io.github.johnnypixelz.utilizer.event;

import org.bukkit.event.EventPriority;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class BiStatefulEventEmitter<T1, T2> {
    private final Map<BiConsumer<T1, T2>, EventPriority> listeners;

    public BiStatefulEventEmitter() {
        this.listeners = new HashMap<>();
    }

    public void emit(T1 t1, T2 t2) {
        listeners.entrySet()
                .stream()
                .sorted(Comparator.comparingInt(o -> o.getValue().getSlot()))
                .map(Map.Entry::getKey)
                .forEachOrdered(consumer -> consumer.accept(t1, t2));
    }

    public void listen(BiConsumer<T1, T2> listener) {
        listen(listener, EventPriority.NORMAL);
    }

    public void listen(BiConsumer<T1, T2> listener, EventPriority priority) {
        listeners.put(listener, priority);
    }

}
