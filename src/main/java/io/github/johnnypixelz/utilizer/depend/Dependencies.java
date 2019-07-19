package io.github.johnnypixelz.utilizer.depend;

import org.bukkit.Bukkit;

public class Dependencies {

    public static boolean PlaceholderAPI() {
        return Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }
}
