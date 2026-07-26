package io.github.johnnypixelz.utilizer.file.storage.handler.file;

import io.github.johnnypixelz.utilizer.file.storage.container.file.FileStorageContainer;
import io.github.johnnypixelz.utilizer.file.storage.handler.StorageHandler;
import io.github.johnnypixelz.utilizer.plugin.Logs;

import java.io.File;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
        writeAtomically(new File(this.dataFolder, this.fileName + this.fileExtension).toPath(), data);
    }

    public void saveAndBackup(T data) {
        this.dataFolder.mkdirs();
        File file = new File(this.dataFolder, this.fileName + this.fileExtension);

        if (file.exists()) {
            File backupDir = new File(this.dataFolder, "backups");
            backupDir.mkdirs();

            File backupFile = new File(backupDir, this.fileName + "-" + DATE_FORMAT.format(new Date(System.currentTimeMillis())) + this.fileExtension);

            try {
                Files.copy(file.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        writeAtomically(file.toPath(), data);
    }

    /**
     * Writes beside the target and swaps it in once the file is complete.
     * <p>
     * Deleting the old file first and writing in its place loses everything the
     * moment serialization fails partway -- there is no copy left and what did
     * get flushed is half a document. Building the replacement separately means
     * a failure leaves the previous file exactly as it was.
     */
    private void writeAtomically(Path target, T data) {
        final Path temporary = target.resolveSibling(target.getFileName() + ".tmp");

        try {
            Files.deleteIfExists(temporary);
        } catch (IOException e) {
            e.printStackTrace();
        }

        saveData(temporary, data);

        if (!Files.exists(temporary)) {
            Logs.severe("Nothing was written for " + target.getFileName() + ", keeping the previous file");
            return;
        }

        try {
            Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException exception) {
            // Some filesystems cannot promise it; a plain replace is still
            // better than having deleted the original up front.
            try {
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                Logs.severe("Failed to replace " + target.getFileName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        } catch (IOException exception) {
            Logs.severe("Failed to replace " + target.getFileName() + ": " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    public FileStorageContainer<T> container(Supplier<T> supplier) {
        return new FileStorageContainer<>(this, supplier);
    }

}
