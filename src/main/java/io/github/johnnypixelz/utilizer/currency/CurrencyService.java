package io.github.johnnypixelz.utilizer.currency;

import io.github.johnnypixelz.utilizer.currency.provider.CoinsEngineCurrencyProvider;
import io.github.johnnypixelz.utilizer.currency.provider.PlayerPointsCurrencyProvider;
import io.github.johnnypixelz.utilizer.currency.provider.TokenEnchantCurrencyProvider;
import io.github.johnnypixelz.utilizer.currency.provider.VaultCurrencyProvider;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Level;

/**
 * Service for managing currencies across different economy plugins.
 * Automatically detects and registers currencies from available plugins.
 * <p>
 * For static access, use the {@link Currencies} class instead.
 */
public class CurrencyService {

    private final Plugin plugin;
    private final Map<String, Currency> currencies;
    private final List<CurrencyProvider> providers;

    public CurrencyService(Plugin plugin) {
        this.plugin = plugin;
        this.currencies = new HashMap<>();
        this.providers = new ArrayList<>();
        detectProviders();
    }

    private void detectProviders() {
        // Try Vault
        tryRegisterProvider(new VaultCurrencyProvider());

        // Try PlayerPoints
        tryRegisterProvider(new PlayerPointsCurrencyProvider());

        // Try CoinsEngine
        tryRegisterProvider(new CoinsEngineCurrencyProvider());

        // Try TokenEnchant
        tryRegisterProvider(new TokenEnchantCurrencyProvider());

        if (currencies.isEmpty()) {
            plugin.getLogger().info("No currency providers detected.");
        } else {
            plugin.getLogger().info("Registered " + currencies.size() + " currency/currencies from " + providers.size() + " provider(s).");
        }
    }

    private void tryRegisterProvider(CurrencyProvider provider) {
        try {
            if (provider.isAvailable()) {
                providers.add(provider);
                for (Currency currency : provider.getCurrencies()) {
                    currencies.put(currency.getId(), currency);
                    plugin.getLogger().info("Registered currency: " + currency.getId() + " (" + provider.getPluginName() + ")");
                }
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Failed to initialize " + provider.getPluginName() + " currency provider", e);
        }
    }

    /**
     * Register a custom currency.
     *
     * @param currency The currency to register
     * @throws IllegalArgumentException if a currency with the same ID already exists
     */
    public void register(@NotNull Currency currency) {
        Objects.requireNonNull(currency, "Currency cannot be null");
        if (currencies.containsKey(currency.getId())) {
            throw new IllegalArgumentException("Currency with ID '" + currency.getId() + "' is already registered");
        }
        currencies.put(currency.getId(), currency);
        plugin.getLogger().info("Registered custom currency: " + currency.getId());
    }

    /**
     * Unregister a currency by ID.
     *
     * @param id The currency ID
     * @return true if the currency was unregistered
     */
    public boolean unregister(@NotNull String id) {
        Objects.requireNonNull(id, "Currency ID cannot be null");
        Currency removed = currencies.remove(id);
        if (removed != null) {
            plugin.getLogger().info("Unregistered currency: " + id);
            return true;
        }
        return false;
    }

    /**
     * Get a currency by ID.
     *
     * @param id Currency ID
     * @return The currency, or null if not found
     */
    @Nullable
    public Currency getCurrency(@NotNull String id) {
        Objects.requireNonNull(id, "Currency ID cannot be null");
        return currencies.get(id);
    }

    /**
     * Get all registered currencies.
     *
     * @return Unmodifiable collection of all currencies
     */
    @NotNull
    public Collection<Currency> getAllCurrencies() {
        return Collections.unmodifiableCollection(currencies.values());
    }

    /**
     * Get all available currencies (where the backing plugin is loaded).
     *
     * @return Collection of available currencies
     */
    @NotNull
    public Collection<Currency> getAvailableCurrencies() {
        List<Currency> available = new ArrayList<>();
        for (Currency currency : currencies.values()) {
            if (currency.isAvailable()) {
                available.add(currency);
            }
        }
        return Collections.unmodifiableCollection(available);
    }

    /**
     * Check if a currency exists.
     *
     * @param id Currency ID
     * @return true if the currency exists
     */
    public boolean hasCurrency(@NotNull String id) {
        Objects.requireNonNull(id, "Currency ID cannot be null");
        return currencies.containsKey(id);
    }

    /**
     * Get the number of registered currencies.
     *
     * @return Number of currencies
     */
    public int getCurrencyCount() {
        return currencies.size();
    }

    /**
     * Get all registered providers.
     *
     * @return Unmodifiable list of providers
     */
    @NotNull
    public List<CurrencyProvider> getProviders() {
        return Collections.unmodifiableList(providers);
    }

}
