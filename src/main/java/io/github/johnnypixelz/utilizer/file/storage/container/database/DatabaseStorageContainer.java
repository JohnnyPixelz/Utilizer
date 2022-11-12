package io.github.johnnypixelz.utilizer.file.storage.container.database;

import io.github.johnnypixelz.utilizer.file.storage.container.StorageContainer;

import java.util.function.Supplier;

public abstract class DatabaseStorageContainer<T> extends StorageContainer<T> {

    public DatabaseStorageContainer(Supplier<T> supplier) {
        super(supplier);
    }

}
