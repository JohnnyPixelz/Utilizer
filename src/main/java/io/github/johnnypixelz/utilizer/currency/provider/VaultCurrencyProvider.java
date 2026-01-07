package io.github.johnnypixelz.utilizer.currency.provider;

import io.github.johnnypixelz.utilizer.currency.Currency;
import io.github.johnnypixelz.utilizer.currency.CurrencyProvider;
import io.github.johnnypixelz.utilizer.currency.TransactionResult;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Collection;
import java.util.Collections;

/**
 * Currency provider for Vault economy.
 */
public class VaultCurrencyProvider implements CurrencyProvider {

    private static final String CURRENCY_ID = "vault";
    private Economy economy;

    @Override
    public String getPluginName() {
        return "Vault";
    }

    @Override
    public boolean isAvailable() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }

        economy = rsp.getProvider();
        return economy != null;
    }

    @Override
    public Collection<Currency> getCurrencies() {
        return Collections.singletonList(new VaultCurrency());
    }

    private class VaultCurrency implements Currency {

        @Override
        public String getId() {
            return CURRENCY_ID;
        }

        @Override
        public String getName() {
            return economy != null ? economy.currencyNamePlural() : "Money";
        }

        @Override
        public boolean isAvailable() {
            return economy != null;
        }

        @Override
        public double getBalance(OfflinePlayer player) {
            if (economy == null) return 0;
            return economy.getBalance(player);
        }

        @Override
        public boolean has(OfflinePlayer player, double amount) {
            if (economy == null) return false;
            return economy.has(player, amount);
        }

        @Override
        public TransactionResult withdraw(OfflinePlayer player, double amount) {
            if (economy == null) {
                return TransactionResult.failure(TransactionResult.Reason.CURRENCY_UNAVAILABLE);
            }
            if (amount < 0) {
                return TransactionResult.failure(TransactionResult.Reason.NEGATIVE_AMOUNT, economy.getBalance(player));
            }
            double balanceBefore = economy.getBalance(player);
            if (balanceBefore < amount) {
                return TransactionResult.failure(TransactionResult.Reason.INSUFFICIENT_FUNDS, balanceBefore);
            }
            try {
                EconomyResponse response = economy.withdrawPlayer(player, amount);
                if (response.transactionSuccess()) {
                    return TransactionResult.success(balanceBefore, economy.getBalance(player));
                }
                return TransactionResult.failure(TransactionResult.Reason.PROVIDER_ERROR, balanceBefore);
            } catch (Exception e) {
                return TransactionResult.failure(TransactionResult.Reason.PROVIDER_ERROR, balanceBefore);
            }
        }

        @Override
        public TransactionResult deposit(OfflinePlayer player, double amount) {
            if (economy == null) {
                return TransactionResult.failure(TransactionResult.Reason.CURRENCY_UNAVAILABLE);
            }
            if (amount < 0) {
                return TransactionResult.failure(TransactionResult.Reason.NEGATIVE_AMOUNT, economy.getBalance(player));
            }
            double balanceBefore = economy.getBalance(player);
            try {
                EconomyResponse response = economy.depositPlayer(player, amount);
                if (response.transactionSuccess()) {
                    return TransactionResult.success(balanceBefore, economy.getBalance(player));
                }
                return TransactionResult.failure(TransactionResult.Reason.PROVIDER_ERROR, balanceBefore);
            } catch (Exception e) {
                return TransactionResult.failure(TransactionResult.Reason.PROVIDER_ERROR, balanceBefore);
            }
        }

        @Override
        public TransactionResult setBalance(OfflinePlayer player, double amount) {
            if (economy == null) {
                return TransactionResult.failure(TransactionResult.Reason.CURRENCY_UNAVAILABLE);
            }
            if (amount < 0) {
                return TransactionResult.failure(TransactionResult.Reason.NEGATIVE_AMOUNT, economy.getBalance(player));
            }
            double balanceBefore = economy.getBalance(player);
            try {
                double diff = amount - balanceBefore;
                EconomyResponse response;
                if (diff > 0) {
                    response = economy.depositPlayer(player, diff);
                } else if (diff < 0) {
                    response = economy.withdrawPlayer(player, -diff);
                } else {
                    return TransactionResult.success(balanceBefore, balanceBefore);
                }
                if (response.transactionSuccess()) {
                    return TransactionResult.success(balanceBefore, economy.getBalance(player));
                }
                return TransactionResult.failure(TransactionResult.Reason.PROVIDER_ERROR, balanceBefore);
            } catch (Exception e) {
                return TransactionResult.failure(TransactionResult.Reason.PROVIDER_ERROR, balanceBefore);
            }
        }
    }

}
