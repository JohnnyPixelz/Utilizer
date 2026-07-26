package io.github.johnnypixelz.utilizer.file.storage.handler.file.json;

import com.google.gson.Gson;
import io.github.johnnypixelz.utilizer.file.storage.handler.file.FileStorageHandler;
import io.github.johnnypixelz.utilizer.plugin.Logs;
import io.github.johnnypixelz.utilizer.plugin.Provider;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GsonStorageHandler<T> extends FileStorageHandler<T> {
    private static final SimpleDateFormat BROKEN_SUFFIX = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    private final Type type;
    private final Gson gson;

    public GsonStorageHandler(String fileName, Type type, Gson gson) {
        super(fileName, ".json", Provider.getPlugin().getDataFolder());
        this.type = type;
        this.gson = gson;
    }

    @Override
    protected T loadData(Path path) {
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return this.gson.fromJson(reader, this.type);
        } catch (IOException exception) {
            return null;
        } catch (Exception exception) {
//            Unparseable rather than missing. Containers read their file in the
//            constructor, so letting this out takes the whole plugin down with
//            it -- one broken file should cost its own data, not everything
//            else the plugin does.
            Logs.severe("Could not read " + path.getFileName() + ": " + exception.getMessage());
            quarantine(path);

            return null;
        }
    }

    /**
     * Renames a file we could not parse, so the next save does not overwrite
     * whatever is still recoverable from it.
     */
    private void quarantine(Path path) {
        final Path broken = path.resolveSibling(path.getFileName() + ".broken-" + BROKEN_SUFFIX.format(new Date()));

        try {
            Files.move(path, broken, StandardCopyOption.REPLACE_EXISTING);
            Logs.severe("Moved it to " + broken.getFileName() + " and carried on with empty data.");
        } catch (IOException exception) {
            Logs.severe("Could not move it aside: " + exception.getMessage());
        }
    }

    @Override
    protected void saveData(Path path, T t) {
        final String json;

        try {
//            Serialized in full before the file is opened. Streaming straight
//            into the writer means a throw partway through the object graph
//            leaves a truncated document behind, and only IOException was
//            being caught, so the failure itself went unreported.
            json = this.gson.toJson(t, this.type);
        } catch (Exception exception) {
            Logs.severe("Failed to serialize " + path.getFileName() + ": " + exception.getMessage());
            exception.printStackTrace();
            return;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(json);
        } catch (IOException exception) {
            Logs.severe("Failed to write " + path.getFileName() + ": " + exception.getMessage());
            exception.printStackTrace();

//            Whatever landed on disk is incomplete, and leaving it there would
//            let it be swapped over a good file.
            try {
                Files.deleteIfExists(path);
            } catch (IOException ignored) {
            }
        }
    }

}
