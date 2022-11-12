package io.github.johnnypixelz.utilizer.file.storage.handler.database.sql;

import com.google.gson.Gson;
import io.github.johnnypixelz.utilizer.sql.DatabaseCredentials;
import org.jetbrains.annotations.NotNull;
import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.impl.SQLDataType;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

public class StringSQLStorageHandler<V> extends SQLStorageHandler<String, V> {
    private static final Field<String> ID = field("id", SQLDataType.VARCHAR(256).notNull());
    private static final Field<JSON> DATA = field("data", SQLDataType.JSON.notNull());

    private boolean initializedTable = false;

    public StringSQLStorageHandler(DatabaseCredentials credentials, String table, Gson gson, Class<V> valueType) {
        super(credentials, table, gson, String.class, valueType);
    }

    private void checkTableInitialization() {
        if (initializedTable) return;

        sql.execute(dslContext -> {
            dslContext.createTableIfNotExists(table)
                    .columns(ID, DATA)
                    .primaryKey(ID)
                    .execute();
        });
        initializedTable = true;
    }

    @Override
    public Optional<Map<String, V>> load() {
        checkTableInitialization();

        return sql.query(
                dslContext -> {
                    final @NotNull Result<Record2<String, JSON>> fetch = dslContext.select(ID, DATA).from(table).fetch();
                    return fetch.stream()
                            .map(record -> {
                                final String idString = record.value1();
                                final JSON dataString = record.value2();
                                final V data = gson.fromJson(dataString.data(), valueType);
                                return Map.entry(idString, data);
                            })
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                }
        );
    }

    public void insert(String key, V value) {
        checkTableInitialization();

        final String jsonString = gson.toJson(value, valueType);
        final JSON jooqJson = JSON.json(jsonString);

        sql.execute(dslContext -> {
            dslContext.insertInto(table(table))
                    .columns(ID, DATA)
                    .values(key, jooqJson)
                    .onDuplicateKeyUpdate()
                    .set(DATA, jooqJson)
                    .executeAsync();
        });
    }

}
