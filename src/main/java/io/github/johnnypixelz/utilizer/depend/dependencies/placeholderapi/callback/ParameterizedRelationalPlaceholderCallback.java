package io.github.johnnypixelz.utilizer.depend.dependencies.placeholderapi.callback;

import org.bukkit.entity.Player;

public interface ParameterizedRelationalPlaceholderCallback {

    String run(Player player, Player otherPlayer, String params);

}
