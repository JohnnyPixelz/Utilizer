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

    public void createTable() throws Exception {
        Table tableSchema = type.getAnnotation(Table.class);
        StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableSchema.name() + " (");

        for (Field field : type.getDeclaredFields()) {
            field.setAccessible(true);
            Column columnSchema = field.getAnnotation(Column.class);
            if (columnSchema == null) continue;

            query.append(columnSchema.name()).append(" ").append(columnSchema.type());
            if (columnSchema.primaryKey()) {
                query.append(" PRIMARY KEY");
            }
            query.append(", ");
        }

        query.setLength(query.length() - 2);
        query.append(")");

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(query.toString());
        }
    }

    public List<T> selectAll() throws Exception {
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

    public T selectByKey(Object key) throws Exception {
        Table tableSchema = type.getAnnotation(Table.class);
        StringBuilder query = new StringBuilder("SELECT * FROM " + tableSchema.name());

        Field primaryKeyField = null;

        for (Field field : type.getDeclaredFields()) {
            field.setAccessible(true);
            Column columnSchema = field.getAnnotation(Column.class);
            if (columnSchema == null) continue;

            if (columnSchema.primaryKey()) {
                primaryKeyField = field;
                break;
            }

            query.append(columnSchema.name()).append(" = ?, ");
        }

        if (primaryKeyField == null) {
            throw new IllegalArgumentException("Primary key not found");
        }

        query.append(" WHERE ").append(primaryKeyField.getAnnotation(Column.class).name()).append(" = ?");

        try (PreparedStatement stmt = connection.prepareStatement(query.toString())) {
            stmt.setObject(1, key);

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    T obj = type.getDeclaredConstructor().newInstance();

                    for (Field field : type.getDeclaredFields()) {
                        field.setAccessible(true);
                        Column columnSchema = field.getAnnotation(Column.class);
                        if (columnSchema == null) continue;

                        if (field.getType() == String.class) {
                            field.set(obj, resultSet.getString(columnSchema.name()));
                        } else if (field.getType() == double.class || field.getType() == Double.class) {
                            field.set(obj, resultSet.getDouble(columnSchema.name()));
                        } else if (field.getType() == float.class || field.getType() == Float.class) {
                            field.set(obj, resultSet.getFloat(columnSchema.name()));
                        } else if (field.getType() == int.class || field.getType() == Integer.class) {
                            field.set(obj, resultSet.getInt(columnSchema.name()));
                        } else if (field.getType() == long.class || field.getType() == Long.class) {
                            field.set(obj, resultSet.getLong(columnSchema.name()));
                        } else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                            field.set(obj, resultSet.getBoolean(columnSchema.name()));
                        } else if (field.getType() == byte.class || field.getType() == Byte.class) {
                            field.set(obj, resultSet.getByte(columnSchema.name()));
                        } else if (field.getType() == UUID.class) {
                            field.set(obj, UUID.fromString(resultSet.getString(columnSchema.name())));
                        } else if (field.getType() == Date.class) {
                            field.set(obj, resultSet.getDate(columnSchema.name()));
                        } else if (field.getType() == Timestamp.class) {
                            field.set(obj, resultSet.getTimestamp(columnSchema.name()));
                        } else {
                            throw new IllegalArgumentException("Unsupported field type: " + field.getType());
                        }
                    }

                    return obj;
                }
            }
        }

        return null;
    }

    public T selectByColumn(String column, Object value) throws Exception {
        Table tableSchema = type.getAnnotation(Table.class);
        String query = "SELECT * FROM " + tableSchema.name() + " WHERE " + column + " = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, value);

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    T obj = type.getDeclaredConstructor().newInstance();

                    for (Field field : type.getDeclaredFields()) {
                        field.setAccessible(true);
                        Column columnSchema = field.getAnnotation(Column.class);
                        if (columnSchema == null) continue;

                        if (field.getType() == String.class) {
                            field.set(obj, resultSet.getString(columnSchema.name()));
                        } else if (field.getType() == double.class || field.getType() == Double.class) {
                            field.set(obj, resultSet.getDouble(columnSchema.name()));
                        } else if (field.getType() == float.class || field.getType() == Float.class) {
                            field.set(obj, resultSet.getFloat(columnSchema.name()));
                        } else if (field.getType() == int.class || field.getType() == Integer.class) {
                            field.set(obj, resultSet.getInt(columnSchema.name()));
                        } else if (field.getType() == long.class || field.getType() == Long.class) {
                            field.set(obj, resultSet.getLong(columnSchema.name()));
                        } else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                            field.set(obj, resultSet.getBoolean(columnSchema.name()));
                        } else if (field.getType() == byte.class || field.getType() == Byte.class) {
                            field.set(obj, resultSet.getByte(columnSchema.name()));
                        } else if (field.getType() == UUID.class) {
                            field.set(obj, UUID.fromString(resultSet.getString(columnSchema.name())));
                        } else if (field.getType() == Date.class) {
                            field.set(obj, resultSet.getDate(columnSchema.name()));
                        } else if (field.getType() == Timestamp.class) {
                            field.set(obj, resultSet.getTimestamp(columnSchema.name()));
                        } else {
                            throw new IllegalArgumentException("Unsupported field type: " + field.getType());
                        }
                    }

                    return obj;
                }
            }
        }

        return null;
    }

    public void insert(T obj) throws Exception {
        Table tableSchema = type.getAnnotation(Table.class);
        StringBuilder query = new StringBuilder("INSERT INTO " + tableSchema.name() + " (");
        StringBuilder values = new StringBuilder("VALUES (");

        for (Field field : type.getDeclaredFields()) {
            field.setAccessible(true);
            Column columnSchema = field.getAnnotation(Column.class);
            if (columnSchema == null) continue;

            query.append(columnSchema.name()).append(", ");
            values.append("?, ");
        }

        query.setLength(query.length() - 2);
        values.setLength(values.length() - 2);
        query.append(") ").append(values).append(")");

        try (PreparedStatement stmt = connection.prepareStatement(query.toString())) {
            int i = 1;
            for (Field field : type.getDeclaredFields()) {
                field.setAccessible(true);
                Column columnSchema = field.getAnnotation(Column.class);
                if (columnSchema == null) continue;

                stmt.setObject(i++, field.get(obj));
            }

            stmt.executeUpdate();
        }
    }

    public void updateByObject(T obj) throws Exception {
        Table tableSchema = type.getAnnotation(Table.class);
        StringBuilder query = new StringBuilder("UPDATE " + tableSchema.name() + " SET ");

        Field primaryKeyField = null;
        Object primaryKeyValue = null;

        for (Field field : type.getDeclaredFields()) {
            field.setAccessible(true);
            Column columnSchema = field.getAnnotation(Column.class);
            if (columnSchema == null) continue;

            if (columnSchema.primaryKey()) {
                primaryKeyField = field;
                primaryKeyValue = field.get(obj);
                if (primaryKeyValue == null) {
                    throw new IllegalArgumentException("Primary key value cannot be null");
                }
                continue;
            }

            query.append(columnSchema.name()).append(" = ?, ");
        }

        if (primaryKeyField == null) {
            throw new IllegalArgumentException("Primary key not found");
        }

        query.setLength(query.length() - 2);
        query.append(" WHERE ").append(primaryKeyField.getAnnotation(Column.class).name()).append(" = ?");

        try (PreparedStatement stmt = connection.prepareStatement(query.toString())) {
            int i = 1;
            for (Field field : type.getDeclaredFields()) {
                field.setAccessible(true);
                Column columnSchema = field.getAnnotation(Column.class);
                if (columnSchema == null || columnSchema.primaryKey()) continue;

                stmt.setObject(i++, field.get(obj));
            }

            stmt.setObject(i, primaryKeyValue);
            stmt.executeUpdate();
        }
    }

    public void deleteByObject(T obj) throws Exception {
        Table tableSchema = type.getAnnotation(Table.class);
        Field primaryKeyField = null;
        Object primaryKeyValue = null;

        for (Field field : type.getDeclaredFields()) {
            field.setAccessible(true);
            Column columnSchema = field.getAnnotation(Column.class);
            if (columnSchema == null) continue;

            if (columnSchema.primaryKey()) {
                primaryKeyField = field;
                primaryKeyValue = field.get(obj);
                if (primaryKeyValue == null) {
                    throw new IllegalArgumentException("Primary key value cannot be null");
                }
                break;
            }
        }

        if (primaryKeyField == null) {
            throw new IllegalArgumentException("Primary key not found");
        }

        String query = "DELETE FROM " + tableSchema.name() + " WHERE " + primaryKeyField.getAnnotation(Column.class).name() + " = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, primaryKeyValue);
            stmt.executeUpdate();
        }
    }

    public void deleteByKey(Object key) throws Exception {
        Table tableSchema = type.getAnnotation(Table.class);
        String query = "DELETE FROM " + tableSchema.name() + " WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, key);
            stmt.executeUpdate();
        }
    }

    public void deleteAll() throws Exception {
        Table tableSchema = type.getAnnotation(Table.class);
        String query = "DELETE FROM " + tableSchema.name();

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(query);
        }
    }

}
