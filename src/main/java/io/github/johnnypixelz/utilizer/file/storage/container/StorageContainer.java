package io.github.johnnypixelz.utilizer.file.storage.container;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public abstract class StorageContainer<T> {
    protected final AtomicReference<T> value;
    protected final Supplier<T> supplier;

    public StorageContainer(Supplier<T> supplier) {
        this.value = new AtomicReference<>();
        this.supplier = supplier;
    }

    public T get() {
        return value.get();
    }

}
