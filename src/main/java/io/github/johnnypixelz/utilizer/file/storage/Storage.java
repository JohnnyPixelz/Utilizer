package io.github.johnnypixelz.utilizer.file.storage;

import io.github.johnnypixelz.utilizer.file.storage.builder.ListStorageBuilder;
import io.github.johnnypixelz.utilizer.file.storage.builder.MapStorageBuilder;
import io.github.johnnypixelz.utilizer.file.storage.builder.SingleStorageBuilder;

public final class Storage {

    public static <T> SingleStorageBuilder<T> type(Class<T> type) {
        return new SingleStorageBuilder<>(type);
    }

    public static <T> ListStorageBuilder<T> list(Class<T> type) {
        return new ListStorageBuilder<>(type);
    }

    public static <K, V> MapStorageBuilder<K, V> map(Class<K> keyType, Class<V> valueType) {
        return new MapStorageBuilder<>(keyType, valueType);
    }

}
