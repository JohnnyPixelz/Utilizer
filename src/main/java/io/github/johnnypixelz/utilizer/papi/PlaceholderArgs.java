package io.github.johnnypixelz.utilizer.papi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Provides access to captured template arguments from a placeholder pattern.
 *
 * <p>When using template patterns like {@code "leaderboard_{skill}_top_{position}"},
 * the captured values can be accessed through this interface:
 *
 * <pre>
 * String skill = args.getString("skill");      // "mining"
 * int position = args.getInt("position");      // 1
 * </pre>
 */
public interface PlaceholderArgs {

    /**
     * Get a string argument by key.
     *
     * @param key the argument name
     * @return the value, or null if not present
     */
    @Nullable
    String getString(@NotNull String key);

    /**
     * Get a string argument by key with a default value.
     *
     * @param key          the argument name
     * @param defaultValue the default value if not present
     * @return the value or default
     */
    @NotNull
    String getString(@NotNull String key, @NotNull String defaultValue);

    /**
     * Get an integer argument by key.
     *
     * @param key the argument name
     * @return the parsed integer, or 0 if not present or not a valid integer
     */
    int getInt(@NotNull String key);

    /**
     * Get an integer argument by key with a default value.
     *
     * @param key          the argument name
     * @param defaultValue the default value if not present or not a valid integer
     * @return the parsed integer or default
     */
    int getInt(@NotNull String key, int defaultValue);

    /**
     * Get a long argument by key.
     *
     * @param key the argument name
     * @return the parsed long, or 0 if not present or not a valid long
     */
    long getLong(@NotNull String key);

    /**
     * Get a long argument by key with a default value.
     *
     * @param key          the argument name
     * @param defaultValue the default value if not present or not a valid long
     * @return the parsed long or default
     */
    long getLong(@NotNull String key, long defaultValue);

    /**
     * Get a double argument by key.
     *
     * @param key the argument name
     * @return the parsed double, or 0.0 if not present or not a valid double
     */
    double getDouble(@NotNull String key);

    /**
     * Get a double argument by key with a default value.
     *
     * @param key          the argument name
     * @param defaultValue the default value if not present or not a valid double
     * @return the parsed double or default
     */
    double getDouble(@NotNull String key, double defaultValue);

    /**
     * Check if an argument exists.
     *
     * @param key the argument name
     * @return true if the argument exists
     */
    boolean has(@NotNull String key);

    /**
     * Get all argument keys.
     *
     * @return set of all keys
     */
    @NotNull
    Set<String> keys();

    /**
     * Get the original unparsed params string.
     *
     * @return the raw params string as received from PlaceholderAPI
     */
    @NotNull
    String raw();

}
