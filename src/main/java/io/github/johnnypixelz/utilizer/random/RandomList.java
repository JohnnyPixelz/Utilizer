package io.github.johnnypixelz.utilizer.random;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

public class RandomList<T> extends ArrayList<T> {

    @Deprecated
    public static <T> RandomList<T> of(Collection<T> collection) {
        if (collection.size() < 1) throw new IllegalArgumentException("Collection needs to have at least one element");

        return new RandomList<>(collection);
    }

    public RandomList() {
        super();
    }

    public RandomList(Collection<T> collection) {
        super(collection);
    }

    public void shuffle() {
        Collections.shuffle(this, ThreadLocalRandom.current());
    }

    @Override
    public boolean add(T t) {
        boolean success = super.add(t);
        shuffle();
        return success;
    }

    @Override
    public void add(int index, T element) {
        super.add(index, element);
        shuffle();
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean success = super.addAll(c);
        shuffle();
        return success;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        boolean success = super.addAll(index, c);
        shuffle();
        return success;
    }
}
