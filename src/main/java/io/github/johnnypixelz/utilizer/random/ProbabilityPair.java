package io.github.johnnypixelz.utilizer.random;

public class ProbabilityPair<T> {
    private final T value;
    private final double probability;

    public static <T> ProbabilityPair<T> of(T value, double probability) {
        return new ProbabilityPair<>(value, probability);
    }

    private ProbabilityPair(T value, double probability) {
        this.value = value;
        this.probability = probability;
    }

    public T getValue() {
        return value;
    }

    public double getProbability() {
        return probability;
    }
}