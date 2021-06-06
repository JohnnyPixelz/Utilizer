package io.github.johnnypixelz.utilizer.reflection;

import org.bukkit.Bukkit;

public class Versions {

    public static int getVersion() {
        return Integer.parseInt(Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].split("_")[1]);
    }
}
