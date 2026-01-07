package io.github.johnnypixelz.utilizer.currency.provider;

import io.github.johnnypixelz.utilizer.currency.Currency;
import io.github.johnnypixelz.utilizer.currency.CurrencyProvider;
import io.github.johnnypixelz.utilizer.currency.TransactionResult;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Currency provider for CoinsEngine.
 * Supports multiple currencies configured in CoinsEngine.
 */
public class CoinsEngineCurrencyProvider implements CurrencyProvider {

    private static final String CURRENCY_PREFIX = "coinsengine:";

    @Override
    public String getPluginName() {
        return "CoinsEngine";
    }

    @Override
    public boolean isAvailable() {
        return Bukkit.getPluginManager().getPlugin("CoinsEngine") != null;
    }

    @Override
    public Collection<Currency> getCurrencies() {
        List<Currency> currencies = new ArrayList<>();

        try {
            for (su.nightexpress.coinsengine.api.currency.Currency ceCurrency : CoinsEngineAPI.getCurrencyManager().getCurrencies()) {
                currencies.add(new CoinsEngineCurrency(ceCurrency));
            }
        } catch (Exception e) {
            // CoinsEngine not fully loaded yet or API changed
        }

        return currencies;
    }

    private static class CoinsEngineCurrency implements Currency {

        private final String id;
        private final String name;

        public CoinsEngineCurrency(su.nightexpress.coinsengine.api.currency.Currency ceCurrency) {
            this.id = CURRENCY_PREFIX + ceCurrency.getId();
            this.name = ceCurrency.getName();
        }

        private su.nightexpress.coinsengine.api.currency.Currency getCECurrency() {
            String currencyId = id.substring(CURRENCY_PREFIX.length());
            return CoinsEngineAPI.getCurrency(currencyId);
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean isAvailable() {
            return getCECurrency() != null;
        }

        @Override
        public double getBalance(OfflinePlayer player) {
            su.nightexpress.coinsengine.api.currency.Currency currency = getCECurrency();
            if (currency == null) return 0;
            return CoinsEngineAPI.getBalance(player.getUniqueId(), currency);
        }

        @Override
        public boolean has(OfflinePlayer player, double amount) {
            return getBalance(player) >= amount;
        }

        @Override
        public TransactionResult withdraw(OfflinePlayer player, double amount) {
            su.nightexpress.coinsengine.api.currency.Currency currency = getCECurrency();
            if (currency == null) {
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
                CoinsEngineAPI.removeBalance(player.getUniqueId(), currency, amount);
                return TransactionResult.success(balanceBefore, getBalance(player));
            } catch (Exception e) {
                return TransactionResult.failure(TransactionResult.Reason.PROVIDER_ERROR, balanceBefore);
            }
        }

        @Override
        public TransactionResult deposit(OfflinePlayer player, double amount) {
            su.nightexpress.coinsengine.api.currency.Currency currency = getCECurrency();
            if (currency == null) {
                return TransactionResult.failure(TransactionResult.Reason.CURRENCY_UNAVAILABLE);
            }
            if (amount < 0) {
                return TransactionResult.failure(TransactionResult.Reason.NEGATIVE_AMOUNT, getBalance(player));
            }
            double balanceBefore = getBalance(player);
            try {
                CoinsEngineAPI.addBalance(player.getUniqueId(), currency, amount);
                return TransactionResult.success(balanceBefore, getBalance(player));
            } catch (Exception e) {
                return TransactionResult.failure(TransactionResult.Reason.PROVIDER_ERROR, balanceBefore);
            }
        }

        @Override
        public TransactionResult setBalance(OfflinePlayer player, double amount) {
            su.nightexpress.coinsengine.api.currency.Currency currency = getCECurrency();
            if (currency == null) {
                return TransactionResult.failure(TransactionResult.Reason.CURRENCY_UNAVAILABLE);
            }
            if (amount < 0) {
                return TransactionResult.failure(TransactionResult.Reason.NEGATIVE_AMOUNT, getBalance(player));
            }
            double balanceBefore = getBalance(player);
            try {
                CoinsEngineAPI.setBalance(player.getUniqueId(), currency, amount);
                return TransactionResult.success(balanceBefore, getBalance(player));
            } catch (Exception e) {
                return TransactionResult.failure(TransactionResult.Reason.PROVIDER_ERROR, balanceBefore);
            }
        }
    }

}
