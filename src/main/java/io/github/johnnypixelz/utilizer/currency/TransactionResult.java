package io.github.johnnypixelz.utilizer.currency;

import org.jetbrains.annotations.NotNull;

/**
 * Represents the result of a currency transaction (withdraw, deposit, or setBalance).
 * Provides detailed information about the transaction outcome.
 */
public class TransactionResult {

    /**
     * The reason for the transaction outcome.
     */
    public enum Reason {
        /**
         * The transaction was successful.
         */
        SUCCESS,
        /**
         * The player does not have enough balance for the withdrawal.
         */
        INSUFFICIENT_FUNDS,
        /**
         * The currency is not available (backing plugin not loaded).
         */
        CURRENCY_UNAVAILABLE,
        /**
         * The amount specified was negative.
         */
        NEGATIVE_AMOUNT,
        /**
         * The player account was not found.
         */
        PLAYER_NOT_FOUND,
        /**
         * The external provider threw an error.
         */
        PROVIDER_ERROR,
        /**
         * The transaction would exceed the maximum balance allowed.
         */
        MAX_BALANCE_EXCEEDED
    }

    private final boolean success;
    private final Reason reason;
    private final double balanceBefore;
    private final double balanceAfter;

    private TransactionResult(boolean success, Reason reason, double balanceBefore, double balanceAfter) {
        this.success = success;
        this.reason = reason;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
    }

    /**
     * Create a successful transaction result.
     *
     * @param balanceBefore Balance before the transaction
     * @param balanceAfter Balance after the transaction
     * @return Successful TransactionResult
     */
    @NotNull
    public static TransactionResult success(double balanceBefore, double balanceAfter) {
        return new TransactionResult(true, Reason.SUCCESS, balanceBefore, balanceAfter);
    }

    /**
     * Create a failed transaction result.
     *
     * @param reason The reason for failure
     * @param currentBalance The current balance (unchanged)
     * @return Failed TransactionResult
     */
    @NotNull
    public static TransactionResult failure(@NotNull Reason reason, double currentBalance) {
        return new TransactionResult(false, reason, currentBalance, currentBalance);
    }

    /**
     * Create a failed transaction result with unknown balance.
     *
     * @param reason The reason for failure
     * @return Failed TransactionResult
     */
    @NotNull
    public static TransactionResult failure(@NotNull Reason reason) {
        return new TransactionResult(false, reason, 0, 0);
    }

    /**
     * Check if the transaction was successful.
     *
     * @return true if successful
     */
    public boolean success() {
        return success;
    }

    /**
     * Get the reason for the transaction outcome.
     *
     * @return The reason
     */
    @NotNull
    public Reason reason() {
        return reason;
    }

    /**
     * Get the balance before the transaction.
     *
     * @return Balance before
     */
    public double balanceBefore() {
        return balanceBefore;
    }

    /**
     * Get the balance after the transaction.
     *
     * @return Balance after
     */
    public double balanceAfter() {
        return balanceAfter;
    }

    /**
     * Get the amount that changed (positive for deposits, negative for withdrawals).
     *
     * @return The change in balance
     */
    public double amountChanged() {
        return balanceAfter - balanceBefore;
    }

    @Override
    public String toString() {
        return "TransactionResult{" +
                "success=" + success +
                ", reason=" + reason +
                ", balanceBefore=" + balanceBefore +
                ", balanceAfter=" + balanceAfter +
                '}';
    }

}
