package io.github.johnnypixelz.utilizer.itemstack;

import io.github.johnnypixelz.utilizer.text.Colors;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ItemEditor {
    private final ItemStack stack;

    ItemEditor(@NotNull ItemStack stack) {
        this.stack = stack;
    }

    ItemEditor(@NotNull Material material) {
        this.stack = new ItemStack(material);
    }

    public ItemEditor setDisplayName(@NotNull String name) {
        ItemMeta itemMeta = stack.getItemMeta();
        itemMeta.setDisplayName(Colors.color(name));

        stack.setItemMeta(itemMeta);
        return this;
    }

    public ItemEditor setLore(@NotNull List<String> lore) {
        ItemMeta itemMeta = stack.getItemMeta();
        itemMeta.setLore(lore
                .stream()
                .map(Colors::color)
                .collect(Collectors.toList()));

        stack.setItemMeta(itemMeta);
        return this;
    }

    public ItemEditor setType(@NotNull Material material) {
        stack.setType(material);

        return this;
    }

    public ItemEditor setLore(@NotNull String... lore) {
        ItemMeta itemMeta = stack.getItemMeta();

        itemMeta.setLore(Arrays.stream(lore).map(Colors::color).collect(Collectors.toList()));

        stack.setItemMeta(itemMeta);
        return this;
    }

    public ItemEditor addLore(@NotNull List<String> lore) {
        ItemMeta itemMeta = stack.getItemMeta();

        if (!itemMeta.hasLore()) {
            return setLore(lore);
        }

        List<String> oldLore = itemMeta.getLore();
        oldLore.addAll(lore);

        itemMeta.setLore(oldLore.stream().map(Colors::color).collect(Collectors.toList()));

        stack.setItemMeta(itemMeta);
        return this;
    }

    public ItemEditor addLore(@NotNull String... lore) {
        ItemMeta itemMeta = stack.getItemMeta();

        if (!itemMeta.hasLore()) {
            return setLore(lore);
        }

        List<String> oldLore = itemMeta.getLore();
        oldLore.addAll(Arrays.asList(lore));

        itemMeta.setLore(oldLore.stream().map(Colors::color).collect(Collectors.toList()));

        stack.setItemMeta(itemMeta);
        return this;
    }

    public ItemEditor setFlags(@NotNull ItemFlag... flags) {
        ItemMeta itemMeta = stack.getItemMeta();

        itemMeta.getItemFlags().forEach(itemMeta::removeItemFlags);
        itemMeta.addItemFlags(flags);

        stack.setItemMeta(itemMeta);
        return this;
    }

    public ItemEditor addFlags(@NotNull ItemFlag... flags) {
        ItemMeta itemMeta = stack.getItemMeta();

        for (ItemFlag flag : flags) {
            if (itemMeta.hasItemFlag(flag)) continue;

            itemMeta.addItemFlags(flag);
        }

        stack.setItemMeta(itemMeta);
        return this;
    }

    public ItemStack getItem() {
        return stack;
    }
}
