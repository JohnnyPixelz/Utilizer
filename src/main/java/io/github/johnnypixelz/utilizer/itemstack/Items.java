package io.github.johnnypixelz.utilizer.itemstack;

import io.github.johnnypixelz.utilizer.message.Colors;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Items {

    public static boolean isNull(ItemStack item) {
        return item == null || item.getType().equals(Material.AIR);
    }

    public static ItemStack color(@NotNull ItemStack stack) {
        ItemMeta itemMeta = stack.getItemMeta();

        // Coloring item's display name
        if (itemMeta.hasDisplayName()) {
            itemMeta.setDisplayName(Colors.color(itemMeta.getDisplayName()));
        }

        // Coloring item's lore
        if (itemMeta.hasLore()) {
            itemMeta.setLore(itemMeta.getLore()
                    .stream()
                    .map(Colors::color)
                    .collect(Collectors.toList()));
        }

        stack.setItemMeta(itemMeta);
        return stack;
    }

    public static ItemStack setDisplayName(@NotNull ItemStack stack, @NotNull String name) {
        ItemMeta itemMeta = stack.getItemMeta();
        itemMeta.setDisplayName(Colors.color(name));

        stack.setItemMeta(itemMeta);
        return stack;
    }

    public static ItemStack setLore(@NotNull ItemStack stack, @NotNull List<String> lore) {
        ItemMeta itemMeta = stack.getItemMeta();
        itemMeta.setLore(lore
                .stream()
                .map(Colors::color)
                .collect(Collectors.toList()));

        stack.setItemMeta(itemMeta);
        return stack;
    }

    public static ItemStack setLore(@NotNull ItemStack stack, @NotNull String... lines) {
        ItemMeta itemMeta = stack.getItemMeta();

        itemMeta.setLore(Arrays.stream(lines).map(Colors::color).collect(Collectors.toList()));

        stack.setItemMeta(itemMeta);
        return stack;
    }

    public static ItemStack addLore(@NotNull ItemStack stack, @NotNull List<String> lore) {
        ItemMeta itemMeta = stack.getItemMeta();

        if (!itemMeta.hasLore()) {
            return setLore(stack, lore);
        }

        List<String> oldLore = itemMeta.getLore();
        oldLore.addAll(lore);

        itemMeta.setLore(oldLore.stream().map(Colors::color).collect(Collectors.toList()));

        stack.setItemMeta(itemMeta);
        return stack;
    }

    public static ItemStack addLore(@NotNull ItemStack stack, @NotNull String... lines) {
        ItemMeta itemMeta = stack.getItemMeta();

        if (!itemMeta.hasLore()) {
            return setLore(stack, lines);
        }

        List<String> oldLore = itemMeta.getLore();
        oldLore.addAll(Arrays.asList(lines));

        itemMeta.setLore(oldLore.stream().map(Colors::color).collect(Collectors.toList()));

        stack.setItemMeta(itemMeta);
        return stack;
    }

    public static ItemStack setFlags(@NotNull ItemStack stack, @NotNull ItemFlag... flags) {
        ItemMeta itemMeta = stack.getItemMeta();

        itemMeta.getItemFlags().forEach(itemMeta::removeItemFlags);
        itemMeta.addItemFlags(flags);

        stack.setItemMeta(itemMeta);
        return stack;
    }

    public static ItemStack addFlags(@NotNull ItemStack stack, @NotNull ItemFlag... flags) {
        ItemMeta itemMeta = stack.getItemMeta();

        for (ItemFlag flag : flags) {
            if (itemMeta.hasItemFlag(flag)) continue;

            itemMeta.addItemFlags(flag);
        }

        stack.setItemMeta(itemMeta);
        return stack;
    }
}
