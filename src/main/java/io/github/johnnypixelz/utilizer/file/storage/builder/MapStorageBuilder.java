package io.github.johnnypixelz.utilizer.file.storage.builder;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;
import io.github.johnnypixelz.utilizer.file.storage.handler.database.sql.DynamicSQLStorageHandler;
import io.github.johnnypixelz.utilizer.file.storage.handler.database.sql.SQLStorageHandler;
import io.github.johnnypixelz.utilizer.file.storage.handler.database.sql.StringSQLStorageHandler;
import io.github.johnnypixelz.utilizer.file.storage.handler.file.FileStorageHandler;
import io.github.johnnypixelz.utilizer.file.storage.handler.file.json.GsonStorageHandler;
import io.github.johnnypixelz.utilizer.gson.GsonProvider;
import io.github.johnnypixelz.utilizer.sql.DatabaseCredentials;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

public class MapStorageBuilder<K, V> implements StorageBuilder<Map<K, V>> {
    private final Class<K> keyType;
    private final Class<V> valueType;

    public MapStorageBuilder(Class<K> keyType, Class<V> valueType) {
        this.keyType = keyType;
        this.valueType = valueType;
    }

    @Override
    public FileStorageHandler<Map<K, V>> json(String fileName) {
        return json(fileName, GsonProvider.standard());
    }

    @Override
    public FileStorageHandler<Map<K, V>> json(String fileName, Gson gson) {
        final ParameterizedType type = $Gson$Types.newParameterizedTypeWithOwner(null, HashMap.class, keyType, valueType);
        return new GsonStorageHandler<>(fileName, type, gson);
    }

    public SQLStorageHandler<K, V> sql(DatabaseCredentials credentials, String table) {
        return sql(credentials, table, GsonProvider.standard());
    }

    public SQLStorageHandler<K, V> sql(DatabaseCredentials credentials, String table, Gson gson) {
        if (keyType == String.class) {
            return (SQLStorageHandler<K, V>) new StringSQLStorageHandler<V>(credentials, table, gson, valueType);
        } else {
            return new DynamicSQLStorageHandler<>(credentials, table, gson, keyType, valueType);
        }
    }

}
