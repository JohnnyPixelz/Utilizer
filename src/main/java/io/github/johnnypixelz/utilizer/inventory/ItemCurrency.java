package io.github.johnnypixelz.utilizer.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

public class ItemCurrency {

    public static int amount(Player player, Predicate<ItemStack> predicate) {
        return Arrays.stream(player.getInventory().getContents())
                .filter(Objects::nonNull)
                .filter(predicate)
                .mapToInt(ItemStack::getAmount).sum();
    }

    public static boolean has(Player player, Predicate<ItemStack> predicate, int amount) {
        if (amount <= 0) return true;

        return amount(player, predicate) >= amount;
    }

    public static boolean withdraw(Player player, Predicate<ItemStack> predicate, int amount) {
        if (amount <= 0) return true;
        if (!has(player, predicate, amount)) return false;

        Inventory inv = player.getInventory();

        int toBeRemoved = amount;

        for (int i = 0; i < inv.getContents().length; i++) {
            ItemStack stack = inv.getItem(i);

            if (stack == null || !predicate.test(stack)) continue;

            if (stack.getAmount() < toBeRemoved) {
                toBeRemoved -= stack.getAmount();
                inv.clear(i);
            } else if (stack.getAmount() == toBeRemoved) {
                inv.clear(i);
                return true;
            } else {
                stack.setAmount(stack.getAmount() - toBeRemoved);
                inv.setItem(i, stack);
                return true;
            }
        }

        return false;
    }
}
