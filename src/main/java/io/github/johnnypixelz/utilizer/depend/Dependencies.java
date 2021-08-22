package io.github.johnnypixelz.utilizer.depend;

import io.github.johnnypixelz.utilizer.depend.dependencies.VaultWrapper;
import io.github.johnnypixelz.utilizer.depend.dependencies.placeholderapi.PlaceholderAPIWrapper;
import org.bukkit.Bukkit;

import java.util.Optional;

public class Dependencies {

    public static Optional<PlaceholderAPIWrapper> getPlaceholderAPI() {
        if (isEnabled("PlaceholderAPI")) {
            return Optional.of(new PlaceholderAPIWrapper());
        }

        return Optional.empty();
    }

    public static Optional<VaultWrapper> getVault() {
        if (isEnabled("Vault")) {
            return Optional.of(new VaultWrapper());
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
