package io.github.johnnypixelz.utilizer.depend;

import io.github.johnnypixelz.utilizer.depend.dependencies.Essentials;
import org.bukkit.Bukkit;

import java.util.Optional;

public class Dependencies {

    public static Optional<Essentials> getEssentials() {
        if (isEnabled("Essentials")) {
            return Optional.of(new Essentials());
        }

        return Optional.empty();
    }

    public static boolean isLoaded(String plugin) {
        return Bukkit.getPluginManager().getPlugin(plugin) != null;
    }

    public static boolean isEnabled(String plugin) {
        return Bukkit.getPluginManager().isPluginEnabled(plugin);
    }
}
