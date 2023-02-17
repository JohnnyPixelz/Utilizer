package io.github.johnnypixelz.utilizer.serialize.player;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerInventorySnapshot {
    private final ItemStack[] storageContents;
    private final ItemStack[] armorContents;
    private final ItemStack[] extraContents;

    public PlayerInventorySnapshot(Player player) {
        final PlayerInventory inventory = player.getInventory();

        storageContents = inventory.getStorageContents().clone();
        armorContents = inventory.getArmorContents().clone();
        extraContents = inventory.getExtraContents().clone();
    }

    public ItemStack[] getStorageContents() {
        return storageContents;
    }

    public ItemStack[] getArmorContents() {
        return armorContents;
    }

    public ItemStack[] getExtraContents() {
        return extraContents;
    }

    public void apply(Player player) {
        final PlayerInventory inventory = player.getInventory();

        inventory.setStorageContents(storageContents);
        inventory.setArmorContents(armorContents);
        inventory.setExtraContents(extraContents);
    }

}
