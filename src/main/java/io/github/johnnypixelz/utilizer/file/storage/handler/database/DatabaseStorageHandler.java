package io.github.johnnypixelz.utilizer.file.storage.handler.database;

import io.github.johnnypixelz.utilizer.file.storage.handler.StorageHandler;
import io.github.johnnypixelz.utilizer.sql.DatabaseCredentials;

import java.util.Map;

public abstract class DatabaseStorageHandler<K, V> implements StorageHandler<Map<K, V>> {
    protected final DatabaseCredentials credentials;

    public DatabaseStorageHandler(DatabaseCredentials credentials) {
        this.credentials = credentials;
    }

    public DatabaseCredentials getCredentials() {
        return credentials;
    }
    
}
