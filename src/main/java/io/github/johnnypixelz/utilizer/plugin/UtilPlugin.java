package io.github.johnnypixelz.utilizer.plugin;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public class UtilPlugin extends JavaPlugin {
    private static UtilPlugin plugin;

    public static UtilPlugin getInstance() {
        if (plugin == null) {
            plugin = (UtilPlugin) JavaPlugin.getProvidingPlugin(UtilPlugin.class);
        }

        return plugin;
    }

    public <T extends Listener> T registerListener(T listener) {
        Objects.requireNonNull(listener, "listener");
        getServer().getPluginManager().registerEvents(listener, this);
        return listener;
    }

    public boolean isPluginPresent(String name) {
        return getServer().getPluginManager().getPlugin(name) != null;
    }

    public <T> T getPlugin(String name, Class<T> pluginClass) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(pluginClass, "pluginClass");
        return (T) getServer().getPluginManager().getPlugin(name);
    }

    private File getRelativeFile(String name) {
        getDataFolder().mkdirs();
        return new File(getDataFolder(), name);
    }

    public File getBundledFile(String name) {
        Objects.requireNonNull(name, "name");
        File file = getRelativeFile(name);
        if (!file.exists()) {
            saveResource(name, false);
        }
        return file;
    }

    public YamlConfiguration loadConfig(String file) {
        Objects.requireNonNull(file, "file");
        return YamlConfiguration.loadConfiguration(getBundledFile(file));
    }

}
