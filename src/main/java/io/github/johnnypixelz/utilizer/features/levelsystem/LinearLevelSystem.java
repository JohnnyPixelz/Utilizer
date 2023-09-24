package io.github.johnnypixelz.utilizer.features.levelsystem;

public class LinearLevelSystem extends LevelSystem {
    private final long base;
    private final long increment;

    public LinearLevelSystem(boolean keepOldExp, long base, long increment) {
        super(keepOldExp);
        this.base = base;
        this.increment = increment;
    }

    @Override
    public long getExpForLevel(long level) {
        if (level <= 1) return 0;

        if (keepOldExp) {
            long n = level - 1;
            return ((2 * base) + (n - 1) * increment) * n / 2;
        }

        return base + increment * (level - 2);
    }

    @Override
    public long getLevelFromExp(long exp) {
        if (!keepOldExp) throw new IllegalStateException("getLevelFromExp cannot be called if keepOldExp is false. You cannot get level from exp if you're not keeping old exp.");
        if (exp <= 0) return 1;

        long a = increment;
        long b = 2 * base - increment;
        long c = -2 * exp - 1;
        double discriminant = Math.sqrt(b * b - 4 * a * c);

        // Use the positive root of the quadratic formula
        return (long) Math.ceil((-b + discriminant) / (2 * a));
    }

}
