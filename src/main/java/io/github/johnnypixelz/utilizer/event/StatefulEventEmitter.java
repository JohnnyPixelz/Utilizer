package io.github.johnnypixelz.utilizer.event;

import org.bukkit.event.EventPriority;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class StatefulEventEmitter<T> {
    private final List<StatefulEventListener<T>> listeners;

    public StatefulEventEmitter() {
        this.listeners = new ArrayList<>();
    }

    public void emit(T data) {
        listeners.stream()
                .sorted(Comparator.comparingInt(o -> o.getPriority().getSlot()))
                .map(StatefulEventListener::getConsumer)
                .forEachOrdered(consumer -> consumer.accept(data));
    }

    public StatefulEventListener<T> listen(Consumer<T> listener) {
        return listen(listener, EventPriority.NORMAL);
    }

    public StatefulEventListener<T> listen(Consumer<T> listener, EventPriority priority) {
        final StatefulEventListener<T> statefulEventListener = new StatefulEventListener<>(this, listener, priority);
        listeners.add(statefulEventListener);

        return statefulEventListener;
    }

    public StatefulEventEmitter<T> unregister(StatefulEventListener<T> listener) {
        this.listeners.remove(listener);
        return this;
    }

    public List<StatefulEventListener<T>> getListeners() {
        return listeners;
    }

}
