package io.github.johnnypixelz.utilizer.amount;

import org.jetbrains.annotations.NotNull;

public interface Amount {

    static Amount of(int number) {
        return new StaticAmount(number);
    }

    static Amount range(int min, int max) {
        if (min > max) throw new IllegalArgumentException("Cannot create a range where min is bigger than max.");

        return new DynamicAmount(min, max);
    }

    static Amount parse(@NotNull String amount) {
        final String[] split = amount.split("-");

        if (split.length == 1) {
            try {
                final int number = Integer.parseInt(split[0]);
                return of(number);
            } catch (NumberFormatException ex) {
                return of(1);
            }
        } else if (split.length == 2) {
            try {
                final int min = Integer.parseInt(split[0]);
                final int max = Integer.parseInt(split[1]);
                return range(min, max);
            } catch (NumberFormatException ex) {
                return of(1);
            }
        } else {
            return of(1);
        }
    }

    int getAmount();

}
