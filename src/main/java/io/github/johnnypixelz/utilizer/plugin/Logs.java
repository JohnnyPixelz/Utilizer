package io.github.johnnypixelz.utilizer.plugin;

import javax.annotation.Nonnull;

public class Logs {

    public static void info(@Nonnull String message) {
        Provider.getPlugin().getLogger().info(message);
    }

    public static void warn(@Nonnull String message) {
        Provider.getPlugin().getLogger().warning(message);
    }

    public static void severe(@Nonnull String message) {
        Provider.getPlugin().getLogger().severe(message);
    }

}
