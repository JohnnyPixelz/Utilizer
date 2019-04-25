package io.github.johnnypixelz.utilizer.itemstack;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PremadeItems {

    public static ItemStack getPane(int data) {
        return new ItemBuilder(new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"), 1, (byte) data)).build();
    }

    public static ItemStack getCustomPane(int data) {
        return new ItemBuilder(new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"), 1, (byte) data)).displayname(" ").build();
    }

    public static ItemStack getPane(PaneType type) {
        return new ItemBuilder(new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"), 1, (byte) type.data)).build();
    }

    public static ItemStack getCustomPane(PaneType type) {
        return new ItemBuilder(new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"), 1, (byte) type.data)).displayname(" ").build();
    }

}
