package io.github.johnnypixelz.utilizer.currency.provider;

import io.github.johnnypixelz.utilizer.currency.Currency;
import io.github.johnnypixelz.utilizer.currency.CurrencyProvider;
import io.github.johnnypixelz.utilizer.currency.TransactionResult;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Collection;
import java.util.Collections;

/**
 * Currency provider for PlayerPoints.
 */
public class PlayerPointsCurrencyProvider implements CurrencyProvider {

    private static final String CURRENCY_ID = "playerpoints";
    private PlayerPointsAPI api;

    @Override
    public String getPluginName() {
        return "PlayerPoints";
    }

    @Override
    public boolean isAvailable() {
        if (Bukkit.getPluginManager().getPlugin("PlayerPoints") == null) {
            return false;
        }

        try {
            api = PlayerPoints.getInstance().getAPI();
            return api != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Collection<Currency> getCurrencies() {
        return Collections.singletonList(new PlayerPointsCurrency());
    }

    private class PlayerPointsCurrency implements Currency {

        @Override
        public String getId() {
            return CURRENCY_ID;
        }

        @Override
        public String getName() {
            return "Points";
        }

        @Override
        public boolean isAvailable() {
            return api != null;
        }

        @Override
        public double getBalance(OfflinePlayer player) {
            if (api == null) return 0;
            return api.look(player.getUniqueId());
        }

        @Override
        public boolean has(OfflinePlayer player, double amount) {
            return getBalance(player) >= amount;
        }

        @Override
        public TransactionResult withdraw(OfflinePlayer player, double amount) {
            if (api == null) {
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
                boolean success = api.take(player.getUniqueId(), (int) amount);
                if (success) {
                    return TransactionResult.success(balanceBefore, getBalance(player));
                }
                return TransactionResult.failure(TransactionResult.Reason.PROVIDER_ERROR, balanceBefore);
            } catch (Exception e) {
                return TransactionResult.failure(TransactionResult.Reason.PROVIDER_ERROR, balanceBefore);
            }
        }

        @Override
        public TransactionResult deposit(OfflinePlayer player, double amount) {
            if (api == null) {
                return TransactionResult.failure(TransactionResult.Reason.CURRENCY_UNAVAILABLE);
            }
            if (amount < 0) {
                return TransactionResult.failure(TransactionResult.Reason.NEGATIVE_AMOUNT, getBalance(player));
            }
            double balanceBefore = getBalance(player);
            try {
                boolean success = api.give(player.getUniqueId(), (int) amount);
                if (success) {
                    return TransactionResult.success(balanceBefore, getBalance(player));
                }
                return TransactionResult.failure(TransactionResult.Reason.PROVIDER_ERROR, balanceBefore);
            } catch (Exception e) {
                return TransactionResult.failure(TransactionResult.Reason.PROVIDER_ERROR, balanceBefore);
            }
        }

        @Override
        public TransactionResult setBalance(OfflinePlayer player, double amount) {
            if (api == null) {
                return TransactionResult.failure(TransactionResult.Reason.CURRENCY_UNAVAILABLE);
            }
            if (amount < 0) {
                return TransactionResult.failure(TransactionResult.Reason.NEGATIVE_AMOUNT, getBalance(player));
            }
            double balanceBefore = getBalance(player);
            try {
                api.set(player.getUniqueId(), (int) amount);
                return TransactionResult.success(balanceBefore, getBalance(player));
            } catch (Exception e) {
                return TransactionResult.failure(TransactionResult.Reason.PROVIDER_ERROR, balanceBefore);
            }
        }
    }

}
