package io.github.johnnypixelz.utilizer.inventories.config.conditions;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface Condition {

    boolean evaluate(@Nullable Player player);

}
