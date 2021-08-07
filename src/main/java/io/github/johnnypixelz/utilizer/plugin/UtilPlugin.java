package io.github.johnnypixelz.utilizer.plugin;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class UtilPlugin extends JavaPlugin {
    private static UtilPlugin plugin;

    /**
     * Returns the main plugin's instance
     * @return Plugin's instance
     */
    public static UtilPlugin getInstance() {
        if (plugin == null) {
            plugin = (UtilPlugin) JavaPlugin.getProvidingPlugin(UtilPlugin.class);
        }

        return plugin;
    }

    public <T extends Listener> T registerListener(@NotNull T listener) {
        getServer().getPluginManager().registerEvents(listener, this);
        return listener;
    }

    public Listener[] registerListener(@NotNull Listener... listener) {
        for (Listener listener1 : listener) {
            getServer().getPluginManager().registerEvents(listener1, this);
        }
        return listener;
    }

    public <T extends Listener> T unregisterListener(@NotNull T listener) {
        HandlerList.unregisterAll(listener);
        return listener;
    }

    public Listener[] unregisterListener(@NotNull Listener... listener) {
        for (Listener listener1 : listener) {
            HandlerList.unregisterAll(listener1);
        }
        return listener;
    }

    public void unregisterAllListeners() {
        HandlerList.unregisterAll(this);
    }

    public boolean isPluginPresent(@NotNull String name) {
        return getServer().getPluginManager().getPlugin(name) != null;
    }

    public <T> T getPlugin(@NotNull String name, @NotNull Class<T> pluginClass) {
        return (T) getServer().getPluginManager().getPlugin(name);
    }

    /**
     * Attempts to find and return a file with a name that matches
     * the given variable, and returns the file object.
     *
     * @param name File name
     * @return Associated file
     */
    private File getRelativeFile(@NotNull String name) {
        getDataFolder().mkdirs();
        return new File(getDataFolder(), name);
    }

    public File getBundledFile(@NotNull String name) {
        File file = getRelativeFile(name);
        if (!file.exists()) {
            saveResource(name, false);
        }
        return file;
    }

    public YamlConfiguration loadConfig(@NotNull String file) {
        return YamlConfiguration.loadConfiguration(getBundledFile(file));
    }

}
