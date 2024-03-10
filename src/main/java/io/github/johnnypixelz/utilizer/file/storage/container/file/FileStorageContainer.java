package io.github.johnnypixelz.utilizer.file.storage.container.file;

import io.github.johnnypixelz.utilizer.file.storage.container.StorageContainer;
import io.github.johnnypixelz.utilizer.file.storage.handler.file.FileStorageHandler;
import io.github.johnnypixelz.utilizer.tasks.Tasks;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Supplier;

public class FileStorageContainer<T> extends StorageContainer<T> {
    private final FileStorageHandler<T> storageHandler;

    public FileStorageContainer(FileStorageHandler<T> storageHandler, Supplier<T> supplier) {
        super(supplier);

        this.storageHandler = storageHandler;
        load();
    }

    public FileStorageHandler<T> getStorageHandler() {
        return storageHandler;
    }

    public void set(T value) {
        this.value.set(value);
    }

    public FileStorageContainer<T> load() {
        value.set(storageHandler.load(supplier));
        return this;
    }

    public FileStorageContainer<T> save() {
        storageHandler.save(value.get());
        return this;
    }

    public BukkitTask autoSave(long ticks) {
        return Tasks.sync().timer(this::save, ticks);
    }

}
