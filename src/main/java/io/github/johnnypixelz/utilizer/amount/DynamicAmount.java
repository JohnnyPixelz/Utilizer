package io.github.johnnypixelz.utilizer.amount;

import java.util.concurrent.ThreadLocalRandom;

public class DynamicAmount implements Amount {
    private final int min;
    private final int max;

    public DynamicAmount(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public int getAmount() {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

}
