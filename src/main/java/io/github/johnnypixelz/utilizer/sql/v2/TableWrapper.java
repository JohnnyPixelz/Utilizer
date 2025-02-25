package io.github.johnnypixelz.utilizer.sql.v2;

import io.github.johnnypixelz.utilizer.sql.v2.annotations.Column;
import io.github.johnnypixelz.utilizer.sql.v2.annotations.Table;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TableWrapper<T> {
    private final Class<T> type;
    private final Connection connection;

    public TableWrapper(Class<T> type, Connection connection) {
        this.type = type;
        this.connection = connection;

        // check if type has @Table annotation
        if (type.getAnnotation(Table.class) == null) {
            throw new IllegalArgumentException("Class must have @Table annotation");
        }
    }

    public List<T> getAllRows() throws Exception {
        List<T> results = new ArrayList<>();
        Table tableSchema = type.getAnnotation(Table.class);
        String query = "SELECT * FROM " + tableSchema.name();

        try (Statement stmt = connection.createStatement();
             ResultSet resultSet = stmt.executeQuery(query)) {

            while (resultSet.next()) {
                T obj = type.getDeclaredConstructor().newInstance();

                for (Field field : type.getDeclaredFields()) {
                    field.setAccessible(true);
                    Column column = field.getAnnotation(Column.class);
                    if (column == null) continue;

                    if (field.getType() == String.class) {
                        field.set(obj, resultSet.getString(column.name()));
                    } else if (field.getType() == double.class || field.getType() == Double.class) {
                        field.set(obj, resultSet.getDouble(column.name()));
                    } else if (field.getType() == float.class || field.getType() == Float.class) {
                        field.set(obj, resultSet.getFloat(column.name()));
                    } else if (field.getType() == int.class || field.getType() == Integer.class) {
                        field.set(obj, resultSet.getInt(column.name()));
                    } else if (field.getType() == long.class || field.getType() == Long.class) {
                        field.set(obj, resultSet.getLong(column.name()));
                    } else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                        field.set(obj, resultSet.getBoolean(column.name()));
                    } else if (field.getType() == byte.class || field.getType() == Byte.class) {
                        field.set(obj, resultSet.getByte(column.name()));
                    } else if (field.getType() == UUID.class) {
                        field.set(obj, UUID.fromString(resultSet.getString(column.name())));
                    } else if (field.getType() == Date.class) {
                        field.set(obj, resultSet.getDate(column.name()));
                    } else if (field.getType() == Timestamp.class) {
                        field.set(obj, resultSet.getTimestamp(column.name()));
                    } else {
                        throw new IllegalArgumentException("Unsupported field type: " + field.getType());
                    }
                }

                results.add(obj);
            }
        }

        return results;
    }
}
