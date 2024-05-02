package io.github.johnnypixelz.utilizer.file.storage.handler.database.sql;

import com.google.gson.Gson;
import io.github.johnnypixelz.utilizer.sql.DatabaseCredentials;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DynamicSQLStorageHandler<K, V> extends SQLStorageHandler<K, V> {
    private boolean initializedTable = false;

    public DynamicSQLStorageHandler(DatabaseCredentials credentials, String table, Gson gson, Class<K> keyType, Class<V> valueType) {
        super(credentials, table, gson, keyType, valueType);
    }

    private void checkTableInitialization() {
        if (initializedTable) return;

        sqlClient.execute("""
                CREATE TABLE IF NOT EXISTS %s (
                    id VARCHAR(256) NOT NULL,
                    data VARCHAR(65535) NOT NULL,
                    PRIMARY KEY (id)
                );
                """.formatted(table));

        initializedTable = true;
    }

    @Override
    public Optional<Map<K, V>> load() {
        checkTableInitialization();

        return sqlClient.executeQuery("""
                SELECT id, data
                FROM %s
                """.formatted(table), preparedStatement -> {
        }, resultSet -> {
            final Map<K, V> dataMap = new HashMap<>();
            while (resultSet.next()) {
                final String id = resultSet.getString("id");
                final String data = resultSet.getString("data");
                final K parsedKey = gson.fromJson(id, keyType);
                final V parsedData = gson.fromJson(data, valueType);
                dataMap.put(parsedKey, parsedData);
            }

            return dataMap;
        });
    }

    @Override
    public void insert(K key, V value) {
        checkTableInitialization();

        if (value == null) {
            sqlClient.executeAsync("""
                    DELETE FROM %s
                    WHERE id = ?
                    """.formatted(table), preparedStatement -> {
                final String keyJson = gson.toJson(key, keyType);
                preparedStatement.setString(1, keyJson);
            });

            return;
        }

        final String keyJson = gson.toJson(key, keyType);
        final String valueJson = gson.toJson(value, valueType);

        sqlClient.executeAsync("""
                INSERT INTO %s (ID, DATA)
                VALUES (?, ?)
                ON DUPLICATE KEY UPDATE DATA = VALUES(DATA);
                """.formatted(table), preparedStatement -> {
            preparedStatement.setString(1, keyJson);
            preparedStatement.setString(2, valueJson);
        });
    }

}
