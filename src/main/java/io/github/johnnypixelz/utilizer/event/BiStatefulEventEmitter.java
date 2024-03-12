package io.github.johnnypixelz.utilizer.event;

import org.bukkit.event.EventPriority;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;

public class BiStatefulEventEmitter<T1, T2> {
    private final List<BiStatefulEventListener<T1, T2>> listeners;

    public BiStatefulEventEmitter() {
        this.listeners = new ArrayList<>();
    }

    public void emit(T1 t1, T2 t2) {
        listeners.stream()
                .sorted(Comparator.comparingInt(o -> o.getPriority().getSlot()))
                .map(BiStatefulEventListener::getConsumer)
                .forEachOrdered(consumer -> consumer.accept(t1, t2));
    }

    public BiStatefulEventListener<T1, T2> listen(BiConsumer<T1, T2> listener) {
        return listen(listener, EventPriority.NORMAL);
    }

    public BiStatefulEventListener<T1, T2> listen(BiConsumer<T1, T2> listener, EventPriority priority) {
        final BiStatefulEventListener<T1, T2> biStatefulEventListener = new BiStatefulEventListener<>(this, listener, priority);
        listeners.add(biStatefulEventListener);

        return biStatefulEventListener;
    }

    public BiStatefulEventEmitter<T1, T2> unregister(BiStatefulEventListener<T1, T2> listener) {
        this.listeners.remove(listener);
        return this;
    }

    public List<BiStatefulEventListener<T1, T2>> getListeners() {
        return listeners;
    }

}
