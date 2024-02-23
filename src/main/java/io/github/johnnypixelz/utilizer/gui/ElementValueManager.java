package io.github.johnnypixelz.utilizer.gui;

import io.github.johnnypixelz.utilizer.event.StatelessEventEmitter;
import org.bukkit.event.EventPriority;

import java.util.ArrayList;
import java.util.List;

public class ElementValueManager {
    private final List<ElementValue<?>> elementValues;
    private final StatelessEventEmitter updateEventEmitter;

    public ElementValueManager() {
        this.elementValues = new ArrayList<>();
        this.updateEventEmitter = new StatelessEventEmitter();
    }

    public ElementValueManager setOnUpdate(Runnable onUpdate) {
        this.updateEventEmitter.listen(onUpdate);
        return this;
    }

    public StatelessEventEmitter getUpdateEventEmitter() {
        return updateEventEmitter;
    }

    public void addValue(ElementValue<?> value) {
        this.elementValues.add(value);

        value.getUpdateEventEmitter()
                .listen(_value -> {
                    updateEventEmitter.emit();
//                    value.markUpdateCompleted();
                }, EventPriority.MONITOR);
    }

//    public boolean hasUpdate() {
//        return elementValues.stream()
//                .anyMatch(ElementValue::hasUpdate);
//    }
//
//    public void consumeUpdate() {
//        elementValues.forEach(ElementValue::markUpdateCompleted);
//    }

}
