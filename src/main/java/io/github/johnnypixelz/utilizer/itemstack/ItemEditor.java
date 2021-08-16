package io.github.johnnypixelz.utilizer.itemstack;

import io.github.johnnypixelz.utilizer.text.Colors;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
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

    public ItemEditor removeLore() {
        if (!stack.hasItemMeta()) {
            return this;
        }

        ItemMeta itemMeta = stack.getItemMeta();

        if (!itemMeta.hasLore()) {
            return this;
        }

        itemMeta.setLore(null);

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

        itemMeta.addItemFlags(flags);

        stack.setItemMeta(itemMeta);
        return this;
    }

    public ItemEditor removeFlags(@NotNull ItemFlag... flags) {
        ItemMeta itemMeta = stack.getItemMeta();

        itemMeta.removeItemFlags(flags);

        stack.setItemMeta(itemMeta);
        return this;
    }

    public ItemEditor clearFlags() {
        ItemMeta itemMeta = stack.getItemMeta();

        itemMeta.removeItemFlags(itemMeta.getItemFlags().toArray(new ItemFlag[0]));

        stack.setItemMeta(itemMeta);
        return this;
    }

    public ItemEditor glow() {
        stack.addUnsafeEnchantment(stack.getType() != Material.BOW ? Enchantment.ARROW_INFINITE : Enchantment.LUCK, 10);
        addFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public ItemEditor setGlow(boolean glow) {
        if (glow) {
            return glow();
        }

        stack.removeEnchantment(stack.getType() == Material.BOW ? Enchantment.LUCK : Enchantment.ARROW_INFINITE);
        removeFlags(ItemFlag.HIDE_ENCHANTS);

        return this;
    }

    public ItemEditor setAmount(int amount) {
        stack.setAmount(amount);
        return this;
    }

    public ItemStack getItem() {
        return stack;
    }
}
