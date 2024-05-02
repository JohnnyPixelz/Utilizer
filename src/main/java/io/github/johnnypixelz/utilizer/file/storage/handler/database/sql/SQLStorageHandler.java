package io.github.johnnypixelz.utilizer.file.storage.handler.database.sql;

import com.google.gson.Gson;
import io.github.johnnypixelz.utilizer.file.storage.container.database.sql.SQLStorageContainer;
import io.github.johnnypixelz.utilizer.file.storage.handler.database.DatabaseStorageHandler;
import io.github.johnnypixelz.utilizer.sql.DatabaseCredentials;
import io.github.johnnypixelz.utilizer.sql.SQLClient;

import java.util.Map;
import java.util.function.Supplier;

public abstract class SQLStorageHandler<K, V> extends DatabaseStorageHandler<K, V> {
    protected final String table;
    protected final SQLClient sqlClient;
    protected final Class<K> keyType;
    protected final Class<V> valueType;
    protected final Gson gson;

    public SQLStorageHandler(DatabaseCredentials credentials, String table, Gson gson, Class<K> keyType, Class<V> valueType) {
        super(credentials);
        this.table = table;
        this.sqlClient = new SQLClient(credentials);
        this.gson = gson;
        this.keyType = keyType;
        this.valueType = valueType;
    }

    public abstract void insert(K key, V value);

    public Class<K> getKeyType() {
        return keyType;
    }

    public Class<V> getValueType() {
        return valueType;
    }

    public String getTable() {
        return table;
    }

    public SQLClient getSqlClient() {
        return sqlClient;
    }

    public SQLStorageContainer<K, V> container(Supplier<Map<K, V>> supplier) {
        return new SQLStorageContainer<>(this, supplier, gson);
    }

}
