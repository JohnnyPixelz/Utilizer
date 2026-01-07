package io.github.johnnypixelz.utilizer.random;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utility class for random operations.
 * Provides static methods for random selection, weighted selection, and random numbers.
 */
public final class Randoms {

    private Randoms() {
    }

    // ===== Collection Selection =====

    /**
     * Picks a random element from the list with equal probability.
     *
     * @param list the list to pick from
     * @param <T>  the element type
     * @return a random element, or null if the list is null or empty
     */
    @Nullable
    public static <T> T pick(@Nullable List<T> list) {
        if (list == null || list.isEmpty()) return null;
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

    /**
     * Picks a random element from the set with equal probability.
     *
     * @param set the set to pick from
     * @param <T> the element type
     * @return a random element, or null if the set is null or empty
     */
    @Nullable
    public static <T> T pick(@Nullable Set<T> set) {
        if (set == null || set.isEmpty()) return null;
        int index = ThreadLocalRandom.current().nextInt(set.size());
        int i = 0;
        for (T element : set) {
            if (i == index) return element;
            i++;
        }
        return null;
    }

    /**
     * Picks a random element from the collection with equal probability.
     *
     * @param collection the collection to pick from
     * @param <T>        the element type
     * @return a random element, or null if the collection is null or empty
     */
    @Nullable
    public static <T> T pick(@Nullable Collection<T> collection) {
        if (collection == null || collection.isEmpty()) return null;
        if (collection instanceof List) {
            return pick((List<T>) collection);
        }
        int index = ThreadLocalRandom.current().nextInt(collection.size());
        int i = 0;
        for (T element : collection) {
            if (i == index) return element;
            i++;
        }
        return null;
    }

    /**
     * Picks a random element from the provided items with equal probability.
     *
     * @param items the items to pick from
     * @param <T>   the element type
     * @return a random element, or null if no items provided
     */
    @SafeVarargs
    @Nullable
    public static <T> T pick(@NotNull T... items) {
        if (items.length == 0) return null;
        return items[ThreadLocalRandom.current().nextInt(items.length)];
    }

    // ===== Weighted Selection =====

    /**
     * Picks a random element using weighted probability.
     *
     * @param items the weighted items
     * @param <T>   the element type
     * @return a random element based on weights, or null if empty
     */
    @Nullable
    public static <T> T weighted(@Nullable Collection<Weighted<T>> items) {
        if (items == null || items.isEmpty()) return null;

        double totalWeight = 0;
        for (Weighted<T> item : items) {
            totalWeight += item.getWeight();
        }

        if (totalWeight <= 0) return null;

        double randomValue = ThreadLocalRandom.current().nextDouble(totalWeight);
        double cumulative = 0;

        for (Weighted<T> item : items) {
            cumulative += item.getWeight();
            if (randomValue < cumulative) {
                return item.getValue();
            }
        }

        return null;
    }

    /**
     * Picks a random element using weighted probability.
     *
     * @param items the weighted items
     * @param <T>   the element type
     * @return a random element based on weights, or null if empty
     */
    @SafeVarargs
    @Nullable
    public static <T> T weighted(@NotNull Weighted<T>... items) {
        if (items.length == 0) return null;
        return weighted(Arrays.asList(items));
    }

    // ===== Chance/Probability =====

    /**
     * Returns true with the given probability (0.0 to 1.0).
     *
     * @param probability the probability (0.0 = never, 1.0 = always)
     * @return true if the random check passes
     */
    public static boolean chance(double probability) {
        if (probability <= 0) return false;
        if (probability >= 1) return true;
        return ThreadLocalRandom.current().nextDouble() < probability;
    }

    /**
     * Returns true with the given percentage (0 to 100).
     *
     * @param percentage the percentage (0 = never, 100 = always)
     * @return true if the random check passes
     */
    public static boolean percent(double percentage) {
        return chance(percentage / 100.0);
    }

}
