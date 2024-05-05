package io.github.johnnypixelz.utilizer.inventory.suppliers;

import java.util.function.Supplier;

public class StaticSupplier<T> implements Supplier<T> {

    public static <T> StaticSupplier<T> of(T value) {
        return new StaticSupplier<>(value);
    }

    private final T value;

    private StaticSupplier(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }

}
