package io.github.johnnypixelz.utilizer.depend;

import org.bukkit.entity.Player;

public class Placeholders {

    public static String set(Player player, String text) {
        return Dependencies.getPlaceholderAPI()
                .map(placeholderAPIWrapper -> placeholderAPIWrapper.setPlaceholders(player, text.replace("%player%", player.getName())))
                .orElseGet(() -> text.replace("%player%", player.getName()));
    }

}
