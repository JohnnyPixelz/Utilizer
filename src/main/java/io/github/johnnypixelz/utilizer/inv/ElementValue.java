package io.github.johnnypixelz.utilizer.inv;

import io.github.johnnypixelz.utilizer.event.StatefulEventEmitter;

import java.util.function.Consumer;

public class ElementValue<T> {
    private T value;
//    private boolean updated;
    private final StatefulEventEmitter<T> updateEventEmitter;

    public static <T> ElementValue<T> of(T defaultValue) {
        return new ElementValue<>(defaultValue);
    }

    protected ElementValue(T defaultValue) {
        this.value = defaultValue;
//        this.updated = false;
        this.updateEventEmitter = new StatefulEventEmitter<>();
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
//        this.updated = true;
        this.value = value;
        updateEventEmitter.emit(value);
    }

    public void setValueSilently(T value) {
        this.value = value;
    }

    public ElementValue<T> setOnUpdate(Runnable onUpdate) {
        return setOnUpdate(c -> onUpdate.run());
    }

    public ElementValue<T> setOnUpdate(Consumer<T> onUpdate) {
        this.updateEventEmitter.listen(onUpdate);
        return this;
    }

    public StatefulEventEmitter<T> getUpdateEventEmitter() {
        return updateEventEmitter;
    }

//    public boolean hasUpdate() {
//        return updated;
//    }
//
//    public void markUpdateCompleted() {
//        this.updated = false;
//    }

}
