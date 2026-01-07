package io.github.johnnypixelz.utilizer.papi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of {@link PlaceholderArgs}.
 */
class PlaceholderArgsImpl implements PlaceholderArgs {

    private final Map<String, String> args;
    private final String raw;

    PlaceholderArgsImpl(@NotNull Map<String, String> args, @NotNull String raw) {
        this.args = args;
        this.raw = raw;
    }

    @Override
    @Nullable
    public String getString(@NotNull String key) {
        return args.get(key);
    }

    @Override
    @NotNull
    public String getString(@NotNull String key, @NotNull String defaultValue) {
        String value = args.get(key);
        return value != null ? value : defaultValue;
    }

    @Override
    public int getInt(@NotNull String key) {
        return getInt(key, 0);
    }

    @Override
    public int getInt(@NotNull String key, int defaultValue) {
        String value = args.get(key);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    public long getLong(@NotNull String key) {
        return getLong(key, 0L);
    }

    @Override
    public long getLong(@NotNull String key, long defaultValue) {
        String value = args.get(key);
        if (value == null) return defaultValue;
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    public double getDouble(@NotNull String key) {
        return getDouble(key, 0.0);
    }

    @Override
    public double getDouble(@NotNull String key, double defaultValue) {
        String value = args.get(key);
        if (value == null) return defaultValue;
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    public boolean has(@NotNull String key) {
        return args.containsKey(key);
    }

    @Override
    @NotNull
    public Set<String> keys() {
        return Collections.unmodifiableSet(args.keySet());
    }

    @Override
    @NotNull
    public String raw() {
        return raw;
    }

}
