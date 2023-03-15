package io.github.johnnypixelz.utilizer.file.storage.builder;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;
import io.github.johnnypixelz.utilizer.file.storage.handler.file.FileStorageHandler;
import io.github.johnnypixelz.utilizer.file.storage.handler.file.json.GsonStorageHandler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ListStorageBuilder<T> implements StorageBuilder<List<T>> {
    private final Type type;

    public ListStorageBuilder(Type type) {
        this.type = type;
    }

    @Override
    public FileStorageHandler<List<T>> json(String fileName, Gson gson) {
        final ParameterizedType listType = $Gson$Types.newParameterizedTypeWithOwner(null, ArrayList.class, type);
        return new GsonStorageHandler<>(fileName, listType, gson);
    }

}