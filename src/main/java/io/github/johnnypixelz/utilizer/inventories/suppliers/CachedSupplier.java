package io.github.johnnypixelz.utilizer.inventories.suppliers;

import java.util.function.Supplier;

public class CachedSupplier<T> implements Supplier<T> {

    public static <T> CachedSupplier<T> of(Supplier<T> supplier) {
        return new CachedSupplier<>(supplier);
    }

    private final Supplier<T> supplier;
    private T cachedValue;

    private CachedSupplier(Supplier<T> supplier) {
        this.supplier = supplier;
        this.cachedValue = null;
    }

    @Override
    public T get() {
        if (cachedValue == null) {
            cachedValue = supplier.get();
        }

        return cachedValue;
    }

}
