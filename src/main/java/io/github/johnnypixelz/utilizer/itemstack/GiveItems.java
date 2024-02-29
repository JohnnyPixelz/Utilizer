package io.github.johnnypixelz.utilizer.itemstack;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Deprecated
public class GiveItems {

    @Deprecated
    public static void giveItem(Player player, ItemStack item) {
        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        } else {
            player.getInventory().addItem(item);
        }
    }

}
