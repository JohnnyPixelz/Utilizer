package io.github.johnnypixelz.utilizer.itemstack;

import io.github.johnnypixelz.utilizer.text.Colors;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
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

    public static ItemStack create(@NotNull Material material, @Nullable String displayName, @Nullable List<String> lore) {
        ItemStack stack = new ItemStack(material);

        if (displayName != null) {
            setDisplayName(stack, displayName);
        }

        if (lore != null) {
            setLore(stack, lore);
        }

        return stack;
    }

    public static ItemStack create(@NotNull Material material, @Nullable String displayName, @Nullable String... lore) {
        ItemStack stack = new ItemStack(material);

        if (displayName != null) {
            setDisplayName(stack, displayName);
        }

        if (lore != null) {
            setLore(stack, lore);
        }

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

    public static ItemStack setLore(@NotNull ItemStack stack, @NotNull String... lore) {
        ItemMeta itemMeta = stack.getItemMeta();

        itemMeta.setLore(Arrays.stream(lore).map(Colors::color).collect(Collectors.toList()));

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

    public static ItemStack addLore(@NotNull ItemStack stack, @NotNull String... lore) {
        ItemMeta itemMeta = stack.getItemMeta();

        if (!itemMeta.hasLore()) {
            return setLore(stack, lore);
        }

        List<String> oldLore = itemMeta.getLore();
        oldLore.addAll(Arrays.asList(lore));

        itemMeta.setLore(oldLore.stream().map(Colors::color).collect(Collectors.toList()));

        stack.setItemMeta(itemMeta);
        return stack;
    }

    public static ItemStack removeLore(@NotNull ItemStack stack) {
        if (!stack.hasItemMeta()) {
            return stack;
        }

        ItemMeta itemMeta = stack.getItemMeta();

        if (!itemMeta.hasLore()) {
            return stack;
        }

        itemMeta.setLore(null);

        stack.setItemMeta(itemMeta);
        return stack;
    }

    public static ItemStack setFlags(@NotNull ItemStack stack, @NotNull List<ItemFlag> flags) {
        ItemMeta itemMeta = stack.getItemMeta();

        itemMeta.getItemFlags().forEach(itemMeta::removeItemFlags);
        flags.forEach(itemMeta::addItemFlags);

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

    public static ItemStack addFlags(@NotNull ItemStack stack, @NotNull List<ItemFlag> flags) {
        ItemMeta itemMeta = stack.getItemMeta();

        flags.forEach(itemMeta::addItemFlags);

        stack.setItemMeta(itemMeta);
        return stack;
    }

    public static ItemStack addFlags(@NotNull ItemStack stack, @NotNull ItemFlag... flags) {
        ItemMeta itemMeta = stack.getItemMeta();

        itemMeta.addItemFlags(flags);

        stack.setItemMeta(itemMeta);
        return stack;
    }

    public static ItemStack removeFlags(@NotNull ItemStack stack, @NotNull List<ItemFlag> flags) {
        ItemMeta itemMeta = stack.getItemMeta();

        flags.forEach(itemMeta::removeItemFlags);

        stack.setItemMeta(itemMeta);
        return stack;
    }

    public static ItemStack removeFlags(@NotNull ItemStack stack, @NotNull ItemFlag... flags) {
        ItemMeta itemMeta = stack.getItemMeta();

        itemMeta.removeItemFlags(flags);

        stack.setItemMeta(itemMeta);
        return stack;
    }

    public static ItemStack clearFlags(@NotNull ItemStack stack) {
        ItemMeta itemMeta = stack.getItemMeta();

        itemMeta.removeItemFlags(itemMeta.getItemFlags().toArray(new ItemFlag[0]));

        stack.setItemMeta(itemMeta);
        return stack;
    }

    public static ItemStack glow(@NotNull ItemStack stack) {
        stack.addUnsafeEnchantment(stack.getType() != Material.BOW ? Enchantment.ARROW_INFINITE : Enchantment.LUCK, 10);
        addFlags(stack, ItemFlag.HIDE_ENCHANTS);

        return stack;
    }

    public static ItemStack setGlow(@NotNull ItemStack stack, boolean glow) {
        if (glow) {
            return glow(stack);
        }

        stack.removeEnchantment(stack.getType() == Material.BOW ? Enchantment.LUCK : Enchantment.ARROW_INFINITE);
        removeFlags(stack, ItemFlag.HIDE_ENCHANTS);

        return stack;
    }

    public static ItemStack map(@NotNull ItemStack stack, @NotNull Function<String, String> mapper) {
        mapName(stack, mapper);
        mapLore(stack, mapper);

        return stack;
    }

    public static ItemStack mapName(@NotNull ItemStack stack, @NotNull Function<String, String> mapper) {
        if (!stack.hasItemMeta()) return stack;

        ItemMeta itemMeta = stack.getItemMeta();

        itemMeta.setDisplayName(mapper.apply(itemMeta.getDisplayName()));

        stack.setItemMeta(itemMeta);
        return stack;
    }

    public static ItemStack mapLore(@NotNull ItemStack stack, @NotNull Function<String, String> mapper) {
        if (!stack.hasItemMeta()) return stack;

        ItemMeta itemMeta = stack.getItemMeta();

        if (!itemMeta.hasLore()) return stack;
        List<String> lore = itemMeta.getLore();

        for (int index = 0; index < lore.size(); index++) {
            lore.set(index, mapper.apply(lore.get(index)));
        }

        itemMeta.setLore(lore);

        stack.setItemMeta(itemMeta);
        return stack;
    }

    public static ItemEditor edit(@NotNull ItemStack stack) {
        return new ItemEditor(stack);
    }

    public static ItemEditor edit(@NotNull Material material) {
        return new ItemEditor(material);
    }
}
