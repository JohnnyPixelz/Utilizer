package io.github.johnnypixelz.utilizer.random;

/**
 * A value paired with a probability weight for use with {@link Randoms#weighted}.
 *
 * @param <T> the value type
 */
public class Weighted<T> {
    private final T value;
    private final double weight;

    public static <T> Weighted<T> of(T value, double weight) {
        return new Weighted<>(value, weight);
    }

    private Weighted(T value, double weight) {
        this.value = value;
        this.weight = weight;
    }

    public T getValue() {
        return value;
    }

    public double getWeight() {
        return weight;
    }

}
