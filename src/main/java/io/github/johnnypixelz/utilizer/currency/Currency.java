package io.github.johnnypixelz.utilizer.currency;

import org.bukkit.OfflinePlayer;

/**
 * Represents a currency that can be used to store and transfer value.
 * Provides a unified API across different economy implementations.
 */
public interface Currency {

    /**
     * Get the unique identifier for this currency.
     * Examples: "vault", "playerpoints", "coinsengine:gems"
     *
     * @return Currency ID
     */
    String getId();

    /**
     * Get the display name of this currency.
     *
     * @return Display name
     */
    String getName();

    /**
     * Check if this currency is currently available.
     * A currency may be unavailable if its backing plugin is not loaded.
     *
     * @return true if the currency is available for use
     */
    boolean isAvailable();

    /**
     * Get the balance of a player.
     *
     * @param player The player
     * @return The player's balance
     */
    double getBalance(OfflinePlayer player);

    /**
     * Check if a player has at least the specified amount.
     *
     * @param player The player
     * @param amount The amount to check
     * @return true if the player has at least the specified amount
     */
    boolean has(OfflinePlayer player, double amount);

    /**
     * Withdraw an amount from a player's balance.
     *
     * @param player The player
     * @param amount The amount to withdraw
     * @return TransactionResult with success/failure details
     */
    TransactionResult withdraw(OfflinePlayer player, double amount);

    /**
     * Deposit an amount to a player's balance.
     *
     * @param player The player
     * @param amount The amount to deposit
     * @return TransactionResult with success/failure details
     */
    TransactionResult deposit(OfflinePlayer player, double amount);

    /**
     * Set a player's balance to a specific amount.
     *
     * @param player The player
     * @param amount The amount to set
     * @return TransactionResult with success/failure details
     */
    TransactionResult setBalance(OfflinePlayer player, double amount);

}
