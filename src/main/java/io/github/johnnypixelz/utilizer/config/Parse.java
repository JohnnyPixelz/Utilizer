package io.github.johnnypixelz.utilizer.config;

import com.google.common.base.Strings;
import io.github.johnnypixelz.utilizer.text.Colors;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;

public class Parse {

    /**
     * Parses RGB color codes from a string.
     *
     * @param color the RGB string, format: "int, int, int"
     *
     * @return a color based on the RGB.
     */
    @NotNull
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
     *
     * @return a chat color based on the RGB.
     */
    @NotNull
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

    public static Optional<Integer> integer(@NotNull String string) {
        try {
            final int number = Integer.parseInt(string);
            return Optional.of(number);
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    public static int integer(@NotNull String string, int defaultValue) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException exception) {
            return defaultValue;
        }
    }

}
