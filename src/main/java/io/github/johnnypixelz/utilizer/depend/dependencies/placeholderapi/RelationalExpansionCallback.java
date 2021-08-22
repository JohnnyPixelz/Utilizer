package io.github.johnnypixelz.utilizer.depend.dependencies.placeholderapi;

import org.bukkit.entity.Player;

public abstract class RelationalExpansionCallback {

    public abstract String run(Player player, Player otherPlayer, String params);

}