package io.github.johnnypixelz.utilizer.file.storage.container.database.sql;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;
import io.github.johnnypixelz.utilizer.file.storage.container.database.DatabaseStorageContainer;
import io.github.johnnypixelz.utilizer.file.storage.handler.database.sql.SQLStorageHandler;
import io.github.johnnypixelz.utilizer.sql.SQLPoller;

import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class SQLStorageContainer<K, V> extends DatabaseStorageContainer<Map<K, V>> {
    protected final SQLStorageHandler<K, V> storageHandler;
    private final Gson gson;
    private SQLPoller sqlPoller;

    public SQLStorageContainer(SQLStorageHandler<K, V> storageHandler, Supplier<Map<K, V>> supplier, Gson gson) {
        super(supplier);

        this.storageHandler = storageHandler;
        this.gson = gson;
        value.set(storageHandler.load(supplier));
    }

    public void update(K key, V value) {
        storageHandler.insert(key, value);

        if (value == null) {
            get().remove(key);
        } else {
            get().put(key, value);
        }

        if (sqlPoller != null) {
            final SQLMessage<K, V> message = new SQLMessage<>(key, value);
            final String payload = gson.toJson(message);
            sqlPoller.sendMessage(payload);
        }
    }

    public SQLStorageContainer<K, V> setupSync() {
        return setupSync(null);
    }

    public SQLStorageContainer<K, V> setupSync(long tickInterval) {
        return setupSync(null, tickInterval);
    }

    public SQLStorageContainer<K, V> setupSync(BiConsumer<K, V> callback) {
        return setupSync(callback, 20);
    }

    public SQLStorageContainer<K, V> setupSync(BiConsumer<K, V> callback, long tickInterval) {
        if (sqlPoller != null) return this;

        sqlPoller = SQLPoller.setup(storageHandler.getCredentials(), storageHandler.getTable() + "_messages", tickInterval);
        sqlPoller.getEventEmitter().listen(payload -> {
            final ParameterizedType type = $Gson$Types.newParameterizedTypeWithOwner(null, SQLMessage.class, storageHandler.getKeyType(), storageHandler.getValueType());
            final SQLMessage<K, V> sqlMessage = gson.fromJson(payload, type);
            if (sqlMessage.value == null) {
                get().remove(sqlMessage.key);
            } else {
                get().put(sqlMessage.key, sqlMessage.value);
            }

            if (callback != null) {
                callback.accept(sqlMessage.key, sqlMessage.value);
            }
        });

        return this;
    }

    private static class SQLMessage<K, V> {
        private final K key;
        private final V value;

        public SQLMessage(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

    }

}
