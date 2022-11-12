package io.github.johnnypixelz.utilizer.cache;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public final class Cache<T> implements Supplier<T> {

    public static <T> Cache<T> suppliedBy(Supplier<T> supplier) {
        return new Cache<>(Objects.requireNonNull(supplier, "supplier"));
    }

    private final Supplier<T> supplier;
    private volatile T value = null;

    private Cache(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public final T get() {
        T val = this.value;

        // double-checked locking
        if (val == null) {
            synchronized (this) {
                val = this.value;
                if (val == null) {
                    val = this.supplier.get();
                    this.value = val;
                }
            }
        }

        return val;
    }

    public final Optional<T> getIfPresent() {
        return Optional.ofNullable(this.value);
    }

    public final void invalidate() {
        this.value = null;
    }
}