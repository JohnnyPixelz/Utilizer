package io.github.johnnypixelz.utilizer.file.storage.builder;

import com.google.gson.Gson;
import io.github.johnnypixelz.utilizer.file.storage.handler.StorageHandler;
import io.github.johnnypixelz.utilizer.gson.GsonProvider;

public interface StorageBuilder<T> {

    default StorageHandler<T> json(String fileName) {
        return json(fileName, GsonProvider.standard());
    }

    StorageHandler<T> json(String fileName, Gson gson);

}
