package io.github.johnnypixelz.utilizer.text;

import net.md_5.bungee.api.ChatColor;

import java.awt.*;
import java.util.regex.Pattern;

public class Colors {
    private static final Pattern HEX_PATTERN = Pattern.compile("&(#\\w{6})");

    public static String color(String text) {
        var matcher = HEX_PATTERN.matcher(ChatColor.translateAlternateColorCodes('&', text));
        var builder = new StringBuilder();

        while (matcher.find()) {
            matcher.appendReplacement(builder, ChatColor.of(matcher.group(1)).toString());
        }

        return matcher.appendTail(builder).toString();
    }

    public static ChatColor rgb(int red, int green, int blue) {
        return ChatColor.of(new Color(red, green, blue));
    }

}
