package io.github.johnnypixelz.utilizer.file.storage.handler;

import java.util.Optional;
import java.util.function.Supplier;

public interface StorageHandler<T> {

    Optional<T> load();

    default T load(Supplier<T> supplier) {
        return load().orElseGet(supplier);
    }

}
