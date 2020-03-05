package io.github.johnnypixelz.utilizer.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveItems {

    public static void giveItem(Player player, ItemStack item) {
        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        } else {
            player.getInventory().addItem(item);
        }
    }

}
