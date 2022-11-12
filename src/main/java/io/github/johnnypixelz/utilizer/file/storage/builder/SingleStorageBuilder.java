package io.github.johnnypixelz.utilizer.file.storage.builder;

import com.google.gson.Gson;
import io.github.johnnypixelz.utilizer.file.storage.handler.file.FileStorageHandler;
import io.github.johnnypixelz.utilizer.file.storage.handler.file.json.GsonStorageHandler;

import java.lang.reflect.Type;

public class SingleStorageBuilder<T> implements StorageBuilder<T> {
    private final Type type;

    public SingleStorageBuilder(Type type) {
        this.type = type;
    }

    public FileStorageHandler<T> json(String fileName, Gson gson) {
        return new GsonStorageHandler<>(fileName, type, gson);
    }

}
