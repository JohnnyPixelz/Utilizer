package io.github.johnnypixelz.utilizer.file.storage.handler.file;

import io.github.johnnypixelz.utilizer.file.storage.container.file.FileStorageContainer;
import io.github.johnnypixelz.utilizer.file.storage.handler.StorageHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.function.Supplier;

public abstract class FileStorageHandler<T> implements StorageHandler<T> {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm");

    private final String fileName;
    private final String fileExtension;
    private final File dataFolder;

    public FileStorageHandler(String fileName, String fileExtension, File dataFolder) {
        this.fileName = fileName;
        this.fileExtension = fileExtension;
        this.dataFolder = dataFolder;
    }

    protected abstract T loadData(Path path);

    protected abstract void saveData(Path path, T t);

    public Optional<T> load() {
        File file = new File(this.dataFolder, this.fileName + this.fileExtension);
        final T t = loadData(file.toPath());
        if (file.exists()) {
            return Optional.ofNullable(t);
        } else {
            return Optional.empty();
        }
    }

    public T load(Supplier<T> supplier) {
        File file = new File(this.dataFolder, this.fileName + this.fileExtension);
        if (!file.exists()) return supplier.get();

        final T t = loadData(file.toPath());
        return Optional.ofNullable(t).orElseGet(supplier);
    }

    public void save(T data) {
        this.dataFolder.mkdirs();
        File file = new File(this.dataFolder, this.fileName + this.fileExtension);
        if (file.exists()) {
            file.delete();
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        saveData(file.toPath(), data);
    }

    public void saveAndBackup(T data) {
        this.dataFolder.mkdirs();
        File file = new File(this.dataFolder, this.fileName + this.fileExtension);
        if (file.exists()) {
            File backupDir = new File(this.dataFolder, "backups");
            backupDir.mkdirs();

            File backupFile = new File(backupDir, this.fileName + "-" + DATE_FORMAT.format(new Date(System.currentTimeMillis())) + this.fileExtension);

            try {
                Files.move(file.toPath(), backupFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        saveData(file.toPath(), data);
    }

    public FileStorageContainer<T> container(Supplier<T> supplier) {
        return new FileStorageContainer<>(this, supplier);
    }

}
