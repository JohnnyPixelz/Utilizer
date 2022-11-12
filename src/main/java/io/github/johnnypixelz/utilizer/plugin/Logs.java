package io.github.johnnypixelz.utilizer.plugin;

import org.jetbrains.annotations.NotNull;

public class Logs {

    public static void info(@NotNull String message) {
        Provider.getPlugin().getLogger().info(message);
    }

    public static void warn(@NotNull String message) {
        Provider.getPlugin().getLogger().warning(message);
    }

    public static void severe(@NotNull String message) {
        Provider.getPlugin().getLogger().severe(message);
    }

}
