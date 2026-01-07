package io.github.johnnypixelz.utilizer.currency.provider;

import com.vk2gpz.tokenenchant.api.TokenEnchantAPI;
import io.github.johnnypixelz.utilizer.currency.Currency;
import io.github.johnnypixelz.utilizer.currency.CurrencyProvider;
import io.github.johnnypixelz.utilizer.currency.TransactionResult;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Collection;
import java.util.Collections;

/**
 * Currency provider for TokenEnchant.
 */
public class TokenEnchantCurrencyProvider implements CurrencyProvider {

    private static final String CURRENCY_ID = "tokenenchant";

    @Override
    public String getPluginName() {
        return "TokenEnchant";
    }

    @Override
    public boolean isAvailable() {
        return Bukkit.getPluginManager().getPlugin("TokenEnchant") != null;
    }

    @Override
    public Collection<Currency> getCurrencies() {
        return Collections.singletonList(new TokenEnchantCurrency());
    }

    private static class TokenEnchantCurrency implements Currency {

        @Override
        public String getId() {
            return CURRENCY_ID;
        }

        @Override
        public String getName() {
            return "Tokens";
        }

        @Override
        public boolean isAvailable() {
            return Bukkit.getPluginManager().getPlugin("TokenEnchant") != null;
        }

        @Override
        public double getBalance(OfflinePlayer player) {
            try {
                return TokenEnchantAPI.getInstance().getTokens(player);
            } catch (Exception e) {
                return 0;
            }
        }

        @Override
        public boolean has(OfflinePlayer player, double amount) {
            return getBalance(player) >= amount;
        }

        @Override
        public TransactionResult withdraw(OfflinePlayer player, double amount) {
            if (!isAvailable()) {
                return TransactionResult.failure(TransactionResult.Reason.CURRENCY_UNAVAILABLE);
            }
            if (amount < 0) {
                return TransactionResult.failure(TransactionResult.Reason.NEGATIVE_AMOUNT, getBalance(player));
            }
            double balanceBefore = getBalance(player);
            if (balanceBefore < amount) {
                return TransactionResult.failure(TransactionResult.Reason.INSUFFICIENT_FUNDS, balanceBefore);
            }
            try {
                TokenEnchantAPI.getInstance().removeTokens(player, amount);
                return TransactionResult.success(balanceBefore, getBalance(player));
            } catch (Exception e) {
                return TransactionResult.failure(TransactionResult.Reason.PROVIDER_ERROR, balanceBefore);
            }
        }

        @Override
        public TransactionResult deposit(OfflinePlayer player, double amount) {
            if (!isAvailable()) {
                return TransactionResult.failure(TransactionResult.Reason.CURRENCY_UNAVAILABLE);
            }
            if (amount < 0) {
                return TransactionResult.failure(TransactionResult.Reason.NEGATIVE_AMOUNT, getBalance(player));
            }
            double balanceBefore = getBalance(player);
            try {
                TokenEnchantAPI.getInstance().addTokens(player, amount);
                return TransactionResult.success(balanceBefore, getBalance(player));
            } catch (Exception e) {
                return TransactionResult.failure(TransactionResult.Reason.PROVIDER_ERROR, balanceBefore);
            }
        }

        @Override
        public TransactionResult setBalance(OfflinePlayer player, double amount) {
            if (!isAvailable()) {
                return TransactionResult.failure(TransactionResult.Reason.CURRENCY_UNAVAILABLE);
            }
            if (amount < 0) {
                return TransactionResult.failure(TransactionResult.Reason.NEGATIVE_AMOUNT, getBalance(player));
            }
            double balanceBefore = getBalance(player);
            try {
                TokenEnchantAPI.getInstance().setTokens(player, amount);
                return TransactionResult.success(balanceBefore, getBalance(player));
            } catch (Exception e) {
                return TransactionResult.failure(TransactionResult.Reason.PROVIDER_ERROR, balanceBefore);
            }
        }
    }

}
