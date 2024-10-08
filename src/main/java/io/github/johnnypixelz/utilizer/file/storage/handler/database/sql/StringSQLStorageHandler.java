package io.github.johnnypixelz.utilizer.file.storage.handler.database.sql;

import com.google.gson.Gson;
import io.github.johnnypixelz.utilizer.sql.DatabaseCredentials;
import io.github.johnnypixelz.utilizer.tasks.Tasks;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class StringSQLStorageHandler<V> extends SQLStorageHandler<String, V> {
    private boolean initializedTable = false;

    public StringSQLStorageHandler(DatabaseCredentials credentials, String table, Gson gson, Class<V> valueType) {
        super(credentials, table, gson, String.class, valueType);
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
    public Optional<Map<String, V>> load() {
        checkTableInitialization();

        return sqlClient.executeQuery("""
                SELECT id, data
                FROM %s
                """.formatted(table), preparedStatement -> {
        }, resultSet -> {
            final Map<String, V> dataMap = new HashMap<>();
            while (resultSet.next()) {
                final String id = resultSet.getString("id");
                final String data = resultSet.getString("data");
                final V parsedData = gson.fromJson(data, valueType);
                dataMap.put(id, parsedData);
            }

            return dataMap;
        });
    }

    public void insert(String key, V value) {
        Tasks.async().run(() -> {
            checkTableInitialization();

            if (value == null) {
                sqlClient.executeAsync("""
                    DELETE FROM %s
                    WHERE id = ?
                    """.formatted(table), preparedStatement -> {
                    preparedStatement.setString(1, key);
                });

                return;
            }

            final String jsonString = gson.toJson(value, valueType);

            sqlClient.executeAsync("""
                INSERT INTO %s (ID, DATA)
                VALUES (?, ?)
                ON DUPLICATE KEY UPDATE DATA = VALUES(DATA);
                """.formatted(table), preparedStatement -> {
                preparedStatement.setString(1, key);
                preparedStatement.setString(2, jsonString);
            });
        });
    }

}
