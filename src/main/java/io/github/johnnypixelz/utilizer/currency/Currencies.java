package io.github.johnnypixelz.utilizer.currency;

import io.github.johnnypixelz.utilizer.plugin.Provider;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

/**
 * Static utility class for managing currencies.
 * Provides a simple API for interacting with different economy plugins through a unified interface.
 * <p>
 * Example usage:
 * <pre>
 * // Get a currency by ID
 * Optional&lt;Currency&gt; vault = Currencies.get("vault");
 * vault.ifPresent(c -&gt; c.deposit(player, 100));
 *
 * // CoinsEngine multi-currency
 * Optional&lt;Currency&gt; gems = Currencies.get("coinsengine:gems");
 * gems.ifPresent(c -&gt; c.withdraw(player, 50));
 *
 * // Convenience methods
 * Currencies.deposit("playerpoints", player, 500);
 * double balance = Currencies.getBalance("vault", player);
 *
 * // Check all available currencies
 * for (Currency currency : Currencies.getAvailable()) {
 *     System.out.println(currency.getId() + ": " + currency.getBalance(player));
 * }
 *
 * // Register a custom currency
 * Currencies.register(myCurrency);
 * </pre>
 */
public final class Currencies {

    private static CurrencyService service;

    private Currencies() {
    }

    /**
     * Get or create the currency service for the current plugin.
     *
     * @return The currency service
     */
    public static synchronized CurrencyService service() {
        if (service == null) {
            service = new CurrencyService(Provider.getPlugin());
        }
        return service;
    }

    /**
     * Get a currency by ID.
     *
     * @param id Currency ID (e.g., "vault", "playerpoints", "coinsengine:gems")
     * @return Optional containing the currency if found
     */
    @NotNull
    public static Optional<Currency> get(@NotNull String id) {
        return Optional.ofNullable(service().getCurrency(id));
    }

    /**
     * Get all registered currencies.
     *
     * @return Unmodifiable collection of all currencies
     */
    @NotNull
    public static Collection<Currency> getAll() {
        return service().getAllCurrencies();
    }

    /**
     * Get all available currencies (where the backing plugin is loaded).
     *
     * @return Collection of available currencies
     */
    @NotNull
    public static Collection<Currency> getAvailable() {
        return service().getAvailableCurrencies();
    }

    /**
     * Register a custom currency.
     *
     * @param currency The currency to register
     * @throws IllegalArgumentException if a currency with the same ID already exists
     */
    public static void register(@NotNull Currency currency) {
        service().register(currency);
    }

    /**
     * Unregister a currency by ID.
     *
     * @param id The currency ID
     * @return true if the currency was unregistered
     */
    public static boolean unregister(@NotNull String id) {
        return service().unregister(id);
    }

    /**
     * Check if a currency exists.
     *
     * @param id Currency ID
     * @return true if the currency exists
     */
    public static boolean exists(@NotNull String id) {
        return service().hasCurrency(id);
    }

    /**
     * Get the balance of a player for a specific currency.
     *
     * @param id Currency ID
     * @param player The player
     * @return The balance, or 0 if the currency doesn't exist
     */
    public static double getBalance(@NotNull String id, @NotNull OfflinePlayer player) {
        Currency currency = service().getCurrency(id);
        return currency != null ? currency.getBalance(player) : 0;
    }

    /**
     * Check if a player has at least the specified amount.
     *
     * @param id Currency ID
     * @param player The player
     * @param amount The amount to check
     * @return true if the player has at least the specified amount, false if currency doesn't exist
     */
    public static boolean has(@NotNull String id, @NotNull OfflinePlayer player, double amount) {
        Currency currency = service().getCurrency(id);
        return currency != null && currency.has(player, amount);
    }

    /**
     * Withdraw an amount from a player's balance.
     *
     * @param id Currency ID
     * @param player The player
     * @param amount The amount to withdraw
     * @return TransactionResult with success/failure details
     */
    @NotNull
    public static TransactionResult withdraw(@NotNull String id, @NotNull OfflinePlayer player, double amount) {
        Currency currency = service().getCurrency(id);
        if (currency == null) {
            return TransactionResult.failure(TransactionResult.Reason.CURRENCY_UNAVAILABLE);
        }
        return currency.withdraw(player, amount);
    }

    /**
     * Deposit an amount to a player's balance.
     *
     * @param id Currency ID
     * @param player The player
     * @param amount The amount to deposit
     * @return TransactionResult with success/failure details
     */
    @NotNull
    public static TransactionResult deposit(@NotNull String id, @NotNull OfflinePlayer player, double amount) {
        Currency currency = service().getCurrency(id);
        if (currency == null) {
            return TransactionResult.failure(TransactionResult.Reason.CURRENCY_UNAVAILABLE);
        }
        return currency.deposit(player, amount);
    }

    /**
     * Set a player's balance to a specific amount.
     *
     * @param id Currency ID
     * @param player The player
     * @param amount The amount to set
     * @return TransactionResult with success/failure details
     */
    @NotNull
    public static TransactionResult setBalance(@NotNull String id, @NotNull OfflinePlayer player, double amount) {
        Currency currency = service().getCurrency(id);
        if (currency == null) {
            return TransactionResult.failure(TransactionResult.Reason.CURRENCY_UNAVAILABLE);
        }
        return currency.setBalance(player, amount);
    }

    /**
     * Get the number of registered currencies.
     *
     * @return Number of currencies
     */
    public static int count() {
        return service().getCurrencyCount();
    }

}
