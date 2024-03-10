package io.github.johnnypixelz.utilizer.plugin;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class Provider {
    private static JavaPlugin plugin;

    public static synchronized JavaPlugin getPlugin() {
        if (plugin == null) {
            plugin = JavaPlugin.getProvidingPlugin(Provider.class);
        }

        return plugin;
    }

    public static NamespacedKey getNamespacedKey(String key) {
        return new NamespacedKey(getPlugin(), key);
    }

}
