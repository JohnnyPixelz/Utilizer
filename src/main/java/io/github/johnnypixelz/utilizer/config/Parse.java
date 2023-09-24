package io.github.johnnypixelz.utilizer.config;

import com.google.common.base.Strings;
import io.github.johnnypixelz.utilizer.text.Colors;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Parse {

    /**
     * Parses RGB color codes from a string.
     *
     * @param color the RGB string, format: "int, int, int"
     * @return a color based on the RGB.
     */
    @Nonnull
    public static Color color(@Nullable String color) {
        if (Strings.isNullOrEmpty(color)) return Color.BLACK;

        final String[] splitColor = color.replace(" ", "").split(",");
        if (splitColor.length < 3) return Color.WHITE;

        return Color.fromRGB(
                integer(splitColor[0], 0),
                integer(splitColor[1], 0),
                integer(splitColor[2], 0)
        );
    }

    /**
     * Parses RGB chat color codes from a string.
     *
     * @param color the RGB string, format: "int, int, int"
     * @return a chat color based on the RGB.
     */
    @Nonnull
    public static ChatColor chatColor(@Nullable String color) {
        if (Strings.isNullOrEmpty(color)) return ChatColor.BLACK;

        final String[] splitColor = color.replace(" ", "").split(",");
        if (splitColor.length < 3) return ChatColor.WHITE;

        return Colors.rgb(
                integer(splitColor[0], 0),
                integer(splitColor[1], 0),
                integer(splitColor[2], 0)
        );
    }

    public static Optional<Integer> integer(@Nonnull String string) {
        try {
            final int number = Integer.parseInt(string);
            return Optional.of(number);
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    public static int integer(@Nonnull String string, int defaultValue) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException exception) {
            return defaultValue;
        }
    }

    public static List<String> stringOrList(ConfigurationSection section, String path) {
        return section.isString(path)
                ? new ArrayList<>(Collections.singletonList(section.getString(path)))
                : section.getStringList(path);
    }

    /**
     * Restricts a number to a given range
     *
     * @param min   minimum range value
     * @param max   maximum range value
     * @param value value to constrain
     * @return the existing value if it was
     * included in the range or new if it was altered
     */
    public static int constrain(int min, int max, int value) {
        if (min > max) {
            throw new IllegalArgumentException("min cannot be bigger than max");
        }

        return Math.max(min, Math.min(max, value));
    }

    /**
     * Restricts a number to a given range
     *
     * @param min   minimum range value
     * @param max   maximum range value
     * @param value value to constrain
     * @return the existing value if it was
     * included in the range or new if it was altered
     */
    public static double constrain(double min, double max, double value) {
        if (min > max) {
            throw new IllegalArgumentException("min cannot be bigger than max");
        }

        return Math.max(min, Math.min(max, value));
    }

    /**
     * Restricts a number to a given range
     *
     * @param min   minimum range value
     * @param max   maximum range value
     * @param value value to constrain
     * @return the existing value if it was
     * included in the range or new if it was altered
     */
    public static float constrain(float min, float max, float value) {
        if (min > max) {
            throw new IllegalArgumentException("min cannot be bigger than max");
        }

        return Math.max(min, Math.min(max, value));
    }

}
