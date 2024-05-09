package io.github.johnnypixelz.utilizer.itemstack;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Deprecated
public class InventoryUtils {

    public static void giveItem(Player player, ItemStack stack, int amount, boolean overflow) {
        ItemStack uStack = stack.clone();
        if (amount <= 64) {
            uStack.setAmount(amount);

            giveItem(player, uStack, overflow);
        } else {
            int stacks = amount / 64;
            int leftover = amount % 64;

            uStack.setAmount(64);

            for (int i = 0; i < stacks; i++) {
                giveItem(player, uStack.clone(), overflow);
            }

            uStack.setAmount(leftover);

            if (leftover == 0) return;

            giveItem(player, uStack.clone(), overflow);
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

    public static Item dropItem(Entity entity, ItemStack stack) {
        return entity.getWorld().dropItem(entity.getLocation(), stack);
    }

    public static Item dropItemNaturally(Entity entity, ItemStack stack) {
        return entity.getWorld().dropItemNaturally(entity.getLocation(), stack);
    }
}
