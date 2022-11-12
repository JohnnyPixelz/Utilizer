package io.github.johnnypixelz.utilizer.depend;

import io.github.johnnypixelz.utilizer.depend.dependencies.VaultWrapper;
import io.github.johnnypixelz.utilizer.depend.dependencies.placeholderapi.PlaceholderAPIWrapper;
import io.github.johnnypixelz.utilizer.depend.dependencies.worldedit.WorldEditWrapper;
import org.bukkit.Bukkit;

import java.util.Optional;

public class Dependencies {
    private static PlaceholderAPIWrapper placeholderAPIWrapper = null;

    public static Optional<PlaceholderAPIWrapper> getPlaceholderAPI() {
        if (isEnabled("PlaceholderAPI")) {
            if (placeholderAPIWrapper == null) placeholderAPIWrapper = new PlaceholderAPIWrapper();
            return Optional.of(placeholderAPIWrapper);
        }

        return Optional.empty();
    }

    public static Optional<VaultWrapper> getVault() {
        if (isEnabled("Vault")) {
            return Optional.of(new VaultWrapper());
        }

        return Optional.empty();
    }

    public static Optional<WorldEditWrapper> getWorldEdit() {
        if (isEnabled("WorldEdit")) {
            return Optional.of(new WorldEditWrapper());
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
