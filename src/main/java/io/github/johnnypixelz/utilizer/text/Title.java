package io.github.johnnypixelz.utilizer.text;

import org.bukkit.entity.Player;

public class Title {

    public static void title(Player player, String title) {
        title(player, title, null);
    }

    public static void title(Player player, String title, String subtitle) {
        title(player, title, subtitle, 20, 50, 30);
    }

    public static void title(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

}
