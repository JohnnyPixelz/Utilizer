package io.github.johnnypixelz.utilizer.amount;

public class StaticAmount implements Amount {
    private final int amount;

    public StaticAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public int getAmount() {
        return amount;
    }

}
