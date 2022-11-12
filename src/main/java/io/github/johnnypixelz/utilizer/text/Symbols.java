package io.github.johnnypixelz.utilizer.text;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class Symbols {
    public static final char CHECK_MARK = '✔';
    public static final char CROSS = '✖';
    public static final char LEFT_DOUBLE_ARROW = '«';
    public static final char RIGHT_DOUBLE_ARROW = '»';
    public static final char SQUARE_ROOT = '√';

    public static String getBar(String tileSymbol, ChatColor filledColor, ChatColor unfilledColor, int current, int max) {
        StringBuilder stringBuilder = new StringBuilder();

        if (current != 0) {
            stringBuilder.append(filledColor.toString());
            stringBuilder.append(tileSymbol.repeat(current));
        }

        if (current < max) {
            stringBuilder.append(unfilledColor.toString());
            stringBuilder.append(max - current);
        }

        return stringBuilder.toString();
    }

    public static String getHealthBar(LivingEntity damageable) {
        String format = "&8[%bar%&8]";

        StringBuilder bar = new StringBuilder();
        final int health = (int) damageable.getHealth();
        final int maxHealth = (int) damageable.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

        format = format.replace("%bar%", getBar("|", ChatColor.RED, ChatColor.GRAY, health, maxHealth));
        return format;
    }

    public static String getPlayerHealthBar(Player player) {
        String format = "&c%name% &7- &8[%bar%&8]";

        StringBuilder bar = new StringBuilder();
        final int health = (int) player.getHealth();
        final int maxHealth = (int) player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

        format = format.replace("%name%", player.getName())
                .replace("%bar%", getBar("|", ChatColor.RED, ChatColor.GRAY, health, maxHealth));
        return format;
    }

}
