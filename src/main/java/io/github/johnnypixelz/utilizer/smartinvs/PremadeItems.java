package io.github.johnnypixelz.utilizer.smartinvs;

import io.github.johnnypixelz.utilizer.itemstack.Items;
import org.bukkit.inventory.ItemStack;

public class PremadeItems {

    public static ItemStack getPane(PaneType type) {
        return type.getMaterial().parseItem();
    }

    public static ItemStack getCustomPane(PaneType type) {
        return Items.setDisplayName(getPane(type), " ");
    }

}
