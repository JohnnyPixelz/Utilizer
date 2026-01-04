package io.github.johnnypixelz.utilizer.text;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;

import java.awt.*;
import java.util.regex.Pattern;

public class Colors {
    private static final Pattern HEX_PATTERN = Pattern.compile("&(#\\w{6})");
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder()
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    public static String color(String text) {
        // Convert legacy § codes to & codes before MiniMessage parsing
        // This prevents MiniMessage from throwing errors when placeholders contain legacy colors
        text = text.replace('§', '&');

        // Process MiniMessage tags: <green>, </green>, <#FFFFFF>, <bold>, etc.
        var component = MINI_MESSAGE.deserialize(text);
        text = LEGACY_SERIALIZER.serialize(component);

        // Process legacy & color codes: &a, &3, &l, etc.
        text = ChatColor.translateAlternateColorCodes('&', text);

        // Process hex color codes: &#FFFFFF
        var matcher = HEX_PATTERN.matcher(text);
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
