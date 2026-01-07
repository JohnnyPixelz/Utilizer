package io.github.johnnypixelz.utilizer.depend;

import io.github.johnnypixelz.utilizer.depend.dependencies.VaultWrapper;
import io.github.johnnypixelz.utilizer.depend.dependencies.placeholderapi.PlaceholderAPIWrapper;
import org.bukkit.Bukkit;

import java.util.Optional;

public class Dependencies {
    private static PlaceholderAPIWrapper placeholderAPIWrapper = null;

    /**
     * Get the PlaceholderAPI wrapper for registering placeholders.
     *
     * @return Optional containing the wrapper if PlaceholderAPI is enabled
     * @deprecated Use {@link io.github.johnnypixelz.utilizer.papi.PlaceholderExpansion} instead
     */
    @Deprecated
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

    public static boolean isLoaded(String plugin) {
        return Bukkit.getPluginManager().getPlugin(plugin) != null;
    }

    public static boolean isEnabled(String plugin) {
        return Bukkit.getPluginManager().isPluginEnabled(plugin);
    }
}
