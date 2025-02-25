package io.github.johnnypixelz.utilizer.inventories.config.conditions;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class ConstantCondition implements Condition {
    private final boolean constant;

    public ConstantCondition(boolean constant) {
        this.constant = constant;
    }

    @Override
    public boolean evaluate(@Nullable Player player) {
        return constant;
    }

}
