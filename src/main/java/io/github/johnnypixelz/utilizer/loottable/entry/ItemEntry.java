package io.github.johnnypixelz.utilizer.loottable.entry;

import io.github.johnnypixelz.utilizer.amount.Amount;
import io.github.johnnypixelz.utilizer.config.Parse;
import io.github.johnnypixelz.utilizer.inventory.Inventories;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ItemEntry implements LootEntry {
    private ItemStack itemStack;
    private Amount amount; // Can be "1-3" or fixed like "5"
    private double chance;

    public ItemEntry(ItemStack itemStack, Amount amount, double chance) {
        this.itemStack = itemStack;
        this.amount = amount;
        this.chance = chance;
    }

    @Override
    public List<LootEntry> roll() {
        return List.of(this);
    }

    private int parseAmount(String amount) {
        if (amount.contains("-")) {
            String[] range = amount.split("-");
            int min = Integer.parseInt(range[0]);
            int max = Integer.parseInt(range[1]);
            return new Random().nextInt(max - min + 1) + min;
        } else {
            return Integer.parseInt(amount);
        }
    }

    @Override
    public double getChance() {
        return chance;
    }

    @Override
    public void applyLoot(@Nullable Player player, @Nullable Location location) {
        ItemStack clonedStack = itemStack.clone();
        int amount = this.amount.getAmount();
        int constrainedAmount = Parse.constrain(1, 64, amount);
        clonedStack.setAmount(constrainedAmount);

        if (player != null && location == null) {
            Inventories.giveOrDrop(player, clonedStack);
        } else if (player == null && location != null) {
            World world = location.getWorld();
            if (world == null) return;

            world.dropItem(location, clonedStack);
        } else {
            return;
        }
    }

}
