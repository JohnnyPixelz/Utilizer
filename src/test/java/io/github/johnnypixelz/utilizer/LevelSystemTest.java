package io.github.johnnypixelz.utilizer;

import io.github.johnnypixelz.utilizer.features.levelsystem.ExponentialLevelSystem;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LevelSystemTest {

    @Test
    public void testLinearLevelSystem() {
        final ExponentialLevelSystem exponentialLevelSystem = new ExponentialLevelSystem(true, 100, 100, 2);

        for (int i = 1; i <= 10; i++) {
            final long expForLevel = exponentialLevelSystem.getExpForLevel(i);
            final long levelFromExp = exponentialLevelSystem.getLevelFromExp(expForLevel);

//            assertEquals(i, levelFromExp);
            System.out.printf("%d %d %d\n", i, expForLevel, levelFromExp);
        }
    }

}
