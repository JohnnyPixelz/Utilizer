package io.github.johnnypixelz.utilizer.loottable.entry;

import io.github.johnnypixelz.utilizer.amount.Amount;
import io.github.johnnypixelz.utilizer.config.Parse;
import io.github.johnnypixelz.utilizer.depend.Placeholders;
import io.github.johnnypixelz.utilizer.inventory.Inventories;
import io.github.johnnypixelz.utilizer.itemstack.Items;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemEntry implements LootEntry {

    public static ItemEntry parse(@Nullable ConfigurationSection section) {
        if (section == null) return new ItemEntry();

        ItemStack parsedStack = Items.parse(section);

        String amountString = section.getString("amount", "1");
        Amount parsedAmount = Amount.parse(amountString);

        double chance = section.getDouble("chance", 1);

        return new ItemEntry(parsedStack, parsedAmount, chance);
    }

    private final ItemStack itemStack;
    private final Amount amount; // Can be "1-3" or fixed like "5"
    private final double chance;

    public ItemEntry(ItemStack itemStack, Amount amount, double chance) {
        this.itemStack = itemStack;
        this.amount = amount;
        this.chance = chance;
    }

    public ItemEntry() {
        this.itemStack = new ItemStack(Material.STONE);
        this.amount = Amount.of(1);
        this.chance = 1;
    }

    @Override
    public @NotNull List<LootEntry> roll() {
        return List.of(this);
    }

    @Override
    public double getChance() {
        return chance;
    }

    public ItemStack getGeneratedItemStack() {
        ItemStack clonedStack = itemStack.clone();
        int amount = this.amount.getAmount();
        int constrainedAmount = Parse.constrain(1, 64, amount);
        clonedStack.setAmount(constrainedAmount);

        return clonedStack;
    }

    @Override
    public void giveLoot(Player player) {
        ItemStack generatedItemStack = Items.edit(getGeneratedItemStack())
                .map(stack -> Placeholders.set(player, stack))
                .getItem();

        Inventories.giveOrDrop(player, generatedItemStack);
    }

    @Override
    public void dropLootAt(Location location) {
        ItemStack generatedItemStack = getGeneratedItemStack();

        World world = location.getWorld();
        if (world == null) return;

        world.dropItemNaturally(location, generatedItemStack);
    }

    @Override
    public void dropLootAt(Player player, Location location) {
        ItemStack generatedItemStack = Items.edit(getGeneratedItemStack())
                .map(stack -> Placeholders.set(player, stack))
                .getItem();

        World world = location.getWorld();
        if (world == null) return;

        world.dropItemNaturally(location, generatedItemStack);
    }

}
