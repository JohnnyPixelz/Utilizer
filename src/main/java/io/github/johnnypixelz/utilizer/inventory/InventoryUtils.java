package io.github.johnnypixelz.utilizer.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {

    public static void giveItem(Player player, ItemStack stack, int amount, boolean overflow) {
        if (amount <= 64) {
            stack.setAmount(amount);

            giveItem(player, stack, overflow);
        } else {
            int stacks = amount / 64;
            int leftover = amount % 64;

            stack.setAmount(64);

            for (int i = 0; i < stacks; i++) {
                giveItem(player, stack, overflow);
            }

            stack.setAmount(leftover);

            if (leftover == 0) return;

            giveItem(player, stack, overflow);
        }
    }

    public static void giveItem(Player player, ItemStack stack, boolean overflow) {
        if (isInventoryFull(player) && overflow) {
            dropItemNaturally(player, stack);
        } else {
            player.getInventory().addItem(stack);
        }
    }

    public static boolean isInventoryFull(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }

    public static void dropItem(Player player, ItemStack stack) {
        player.getWorld().dropItem(player.getLocation(), stack);
    }

    public static void dropItemNaturally(Player player, ItemStack stack) {
        player.getWorld().dropItemNaturally(player.getLocation(), stack);
    }
}
