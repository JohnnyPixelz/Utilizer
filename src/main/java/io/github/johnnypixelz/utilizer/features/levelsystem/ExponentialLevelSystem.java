package io.github.johnnypixelz.utilizer.features.levelsystem;

public class ExponentialLevelSystem extends LevelSystem {
    private long base;
    private long increment;
    private double multiplier;

    public ExponentialLevelSystem(boolean keepOldExp, long base, long increment, double multiplier) {
        super(keepOldExp);
        this.base = base;
        this.increment = increment;
        this.multiplier = multiplier;
    }

    @Override
    public long getExpForLevel(long level) {
        if (level <= 1) return 0;

        long exp = (long) (base + increment * Math.pow(level - 2, multiplier));
        return exp;
    }

    @Override
    public long getLevelFromExp(long exp) {
        if (exp <= 0) return 1;

        long level = (exp - base) / increment;
        return level;
    }
}

