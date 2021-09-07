package io.github.johnnypixelz.utilizer.depend.dependencies.placeholderapi;

import org.bukkit.entity.Player;

public interface RelationalExpansionCallback {

    String run(Player player, Player otherPlayer, String params);

}