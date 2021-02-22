package io.github.johnnypixelz.utilizer.depend;

import org.bukkit.Bukkit;

public class Dependencies {

    public static boolean PlaceholderAPI() {
        return Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    public static boolean isLoaded(String plugin) {
        return Bukkit.getPluginManager().getPlugin(plugin) != null;
    }

    public static boolean isEnabled(String plugin) {
        return Bukkit.getPluginManager().isPluginEnabled(plugin);
    }
}
