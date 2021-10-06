package io.github.johnnypixelz.utilizer.random;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ProbabilityList<T> {
    private final List<ProbabilityPair<T>> pairs;
    private double chanceSum;

    public ProbabilityList() {
        this.pairs = new ArrayList<>();
        this.chanceSum = 0;
    }

    public ProbabilityList<T> add(T object, double probability) {
        if (probability < 0) {
            throw new NumberFormatException("Probability number must be positive or zero");
        }

        pairs.add(ProbabilityPair.of(object, probability));
        chanceSum += probability;
        return this;
    }

    public ProbabilityList<T> remove(ProbabilityPair<T> pair) {
        boolean success = pairs.remove(pair);
        if (success) chanceSum -= pair.getProbability();
        return this;
    }

    public List<ProbabilityPair<T>> getPairs() {
        return pairs;
    }

    public T random() {
        if (pairs.isEmpty()) return null;

        double totalChance = 0;
        double randomChance = ThreadLocalRandom.current().nextDouble(chanceSum);
        for (ProbabilityPair<T> pair : pairs) {
            totalChance += pair.getProbability();
            if (randomChance > totalChance) continue;

            return pair.getValue();
        }

        return null;
    }

    public T randomPop() {
        if (pairs.isEmpty()) return null;

        double totalChance = 0;
        double randomChance = ThreadLocalRandom.current().nextDouble(chanceSum);
        for (ProbabilityPair<T> pair : pairs) {
            totalChance += pair.getProbability();
            if (randomChance > totalChance) continue;

            remove(pair);
            return pair.getValue();
        }

        return null;
    }
}