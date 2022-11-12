package io.github.johnnypixelz.utilizer.file.storage.handler.file.json;

import com.google.gson.Gson;
import io.github.johnnypixelz.utilizer.file.storage.handler.file.FileStorageHandler;
import io.github.johnnypixelz.utilizer.plugin.Provider;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class GsonStorageHandler<T> extends FileStorageHandler<T> {
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
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void saveData(Path path, T t) {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            this.gson.toJson(t, this.type, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
