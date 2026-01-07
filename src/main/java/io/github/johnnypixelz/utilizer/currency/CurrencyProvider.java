package io.github.johnnypixelz.utilizer.currency;

import java.util.Collection;

/**
 * Interface for currency providers that bridge to external economy plugins.
 * Each provider is responsible for detecting if its plugin is available
 * and providing Currency implementations for it.
 */
public interface CurrencyProvider {

    /**
     * Get the name of the plugin this provider integrates with.
     *
     * @return Plugin name (e.g., "Vault", "PlayerPoints")
     */
    String getPluginName();

    /**
     * Check if the backing plugin is available on the server.
     *
     * @return true if the plugin is loaded and accessible
     */
    boolean isAvailable();

    /**
     * Get all currencies provided by this provider.
     * Some providers (like CoinsEngine) may provide multiple currencies.
     *
     * @return Collection of currencies
     */
    Collection<Currency> getCurrencies();

}
