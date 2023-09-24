package io.github.johnnypixelz.utilizer.features.levelsystem;

import io.github.johnnypixelz.utilizer.config.Parse;

public abstract class LevelSystem {

    protected boolean keepOldExp;

    public LevelSystem(boolean keepOldExp) {
        this.keepOldExp = keepOldExp;
    }

    public boolean isKeepOldExp() {
        return keepOldExp;
    }

    public abstract long getExpForLevel(long level);

    public abstract long getLevelFromExp(long exp);

    public long getRemainingExp(long currentExp, long level) {
        return Math.max(getExpForLevel(level) - currentExp, 0);
    }

    public float getPercentageToNextLevel(long exp) {
        long level = getLevelFromExp(exp);

        final long fromLevel = getExpForLevel(level);
        final long toLevel = getExpForLevel(level + 1);

        float currentExp = exp - fromLevel;
        float targetExp = toLevel - fromLevel;

        return Parse.constrain(0, 1, currentExp / targetExp);
    }

}
