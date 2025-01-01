package io.github.johnnypixelz.utilizer.loottable;

import java.util.ArrayList;
import java.util.List;

public class LootTable {
    private LootTableMode lootTableMode;
    private final List<LootTable> nestedLootTables;
    private int rolls;

    public LootTable() {
        this.lootTableMode = LootTableMode.WEIGHTED;
        this.nestedLootTables = new ArrayList<>();
        this.rolls = 1;
    }

    public LootTableMode getLootTableMode() {
        return lootTableMode;
    }

    public void setLootTableMode(LootTableMode lootTableMode) {
        this.lootTableMode = lootTableMode;
    }

    public List<LootTable> getNestedLootTables() {
        return nestedLootTables;
    }

    public int getRolls() {
        return rolls;
    }

    public void setRolls(int rolls) {
        this.rolls = rolls;
    }

}
