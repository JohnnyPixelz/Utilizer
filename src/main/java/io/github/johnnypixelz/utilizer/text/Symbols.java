package io.github.johnnypixelz.utilizer.text;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.awt.*;

public class Symbols {
    public static final char CHECK_MARK = '✔';
    public static final char CROSS = '✖';
    public static final char LEFT_DOUBLE_ARROW = '«';
    public static final char RIGHT_DOUBLE_ARROW = '»';
    public static final char SQUARE_ROOT = '√';
    public static final char SQUARE = '▇';
    public static final char DOT = '●';

    /**
     *
     * @param beginColor starting stage of our shade
     * @param endColor final stage of our shade
     * @param currentShade current iteration of our shade [0, maxShades]
     * @param maxShades the total amount of shades
     * @return the transition shade
     */
    public static ChatColor getColorTransition(ChatColor beginColor, ChatColor endColor, int currentShade, int maxShades) {
        double percentage = currentShade / (double) maxShades;

        final Color bColor = beginColor.getColor();
        final Color eColor = endColor.getColor();

        int r = (int) (bColor.getRed() * (1 - percentage) + eColor.getRed() * percentage);
        int g = (int) (bColor.getGreen() * (1 - percentage) + eColor.getGreen() * percentage);
        int b = (int) (bColor.getBlue() * (1 - percentage) + eColor.getBlue() * percentage);

        return Colors.rgb(r, g, b);
    }

    public static String getBar(String tileSymbol, ChatColor beginFilledColor, ChatColor endFilledColor, ChatColor unfilledColor, int current, int max) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < current; i++) {
            ChatColor color = getColorTransition(beginFilledColor, endFilledColor, i, max);
            stringBuilder.append(color.toString());
            stringBuilder.append(tileSymbol);
        }

        if (current < max) {
            stringBuilder.append(unfilledColor.toString());
            stringBuilder.append(tileSymbol.repeat(max - current));
        }

        return stringBuilder.toString();
    }

    public static String getBar(String tileSymbol, ChatColor filledColor, ChatColor unfilledColor, int current, int max) {
        StringBuilder stringBuilder = new StringBuilder();

        if (current != 0) {
            stringBuilder.append(filledColor.toString());
            stringBuilder.append(tileSymbol.repeat(current));
        }

        if (current < max) {
            stringBuilder.append(unfilledColor.toString());
            stringBuilder.append(tileSymbol.repeat(max - current));
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
