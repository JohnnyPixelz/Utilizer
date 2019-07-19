package io.github.johnnypixelz.utilizer.message;

import org.bukkit.ChatColor;

public class Colors {

    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
