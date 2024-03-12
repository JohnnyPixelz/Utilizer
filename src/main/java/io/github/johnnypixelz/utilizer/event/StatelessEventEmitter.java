package io.github.johnnypixelz.utilizer.event;

import org.bukkit.event.EventPriority;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StatelessEventEmitter {
    private final List<StatelessEventListener> listeners;

    public StatelessEventEmitter() {
        this.listeners = new ArrayList<>();
    }

    public void emit() {
        listeners.stream()
                .sorted(Comparator.comparingInt(o -> o.getPriority().getSlot()))
                .map(StatelessEventListener::getRunnable)
                .forEachOrdered(Runnable::run);
    }

    public StatelessEventListener listen(Runnable listener) {
        return listen(listener, EventPriority.NORMAL);
    }

    public StatelessEventListener listen(Runnable listener, EventPriority priority) {
        final StatelessEventListener statelessEventListener = new StatelessEventListener(this, listener, priority);
        listeners.add(statelessEventListener);

        return statelessEventListener;
    }

    public StatelessEventEmitter unregister(StatelessEventListener listener) {
        this.listeners.remove(listener);
        return this;
    }

    public List<StatelessEventListener> getListeners() {
        return listeners;
    }

}
