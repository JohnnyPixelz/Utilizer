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
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemEditor {
    private final ItemStack stack;

    ItemEditor(@NotNull ItemStack stack) {
        this.stack = stack;
    }

    ItemEditor(@NotNull Material material) {
        this.stack = new ItemStack(material);
    }

    public ItemEditor color() {
        return meta(itemMeta -> {
            // Coloring item's display name
            if (itemMeta.hasDisplayName()) {
                itemMeta.setDisplayName(Colors.color(itemMeta.getDisplayName()));
            }

            // Coloring item's lore
            if (itemMeta.hasLore()) {
                final List<String> lore = Objects.requireNonNull(itemMeta.getLore())
                        .stream()
                        .map(Colors::color)
                        .collect(Collectors.toList());

                itemMeta.setLore(lore);
            }
        });
    }

    public ItemEditor setDisplayName(@NotNull String name) {
        return meta(itemMeta -> {
            itemMeta.setDisplayName(Colors.color(name));
        });
    }

    public ItemEditor setType(@NotNull Material material) {
        stack.setType(material);

        return this;
    }

    public ItemEditor setLore(@NotNull List<String> lore) {
        return meta(itemMeta -> {
            final List<String> newLore = lore.stream()
                    .map(Colors::color)
                    .collect(Collectors.toList());

            itemMeta.setLore(newLore);
        });
    }

    public ItemEditor setLore(@NotNull String... lore) {
        return setLore(Arrays.asList(lore));
    }

    public ItemEditor addLore(@NotNull List<String> lore) {
        return meta(itemMeta -> {
            if (!itemMeta.hasLore()) {
                setLore(lore);
                return;
            }

            final List<String> newLore = Objects.requireNonNull(itemMeta.getLore());
            newLore.addAll(lore.stream().map(Colors::color).collect(Collectors.toList()));
            itemMeta.setLore(newLore);
        });
    }

    public ItemEditor addLore(@NotNull String... lore) {
        return addLore(Arrays.asList(lore));
    }

    public ItemEditor removeLore() {
        return meta(itemMeta -> {
            itemMeta.setLore(null);
        });
    }

    public ItemEditor setFlags(@NotNull List<ItemFlag> flags) {
        return meta(itemMeta -> {
           itemMeta.getItemFlags().forEach(itemMeta::removeItemFlags);
           flags.forEach(itemMeta::addItemFlags);
        });
    }

    public ItemEditor setFlags(@NotNull ItemFlag... flags) {
        return setFlags(Arrays.asList(flags));
    }

    public ItemEditor addFlags(@NotNull List<ItemFlag> flags) {
        return meta(itemMeta -> {
            flags.forEach(itemMeta::addItemFlags);
        });
    }

    public ItemEditor addFlags(@NotNull ItemFlag... flags) {
        return addFlags(Arrays.asList(flags));
    }

    public ItemEditor removeFlags(@NotNull List<ItemFlag> flags) {
        ItemMeta itemMeta = stack.getItemMeta();

        flags.forEach(itemMeta::removeItemFlags);

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

    public ItemEditor map(@NotNull Function<String, String> mapper) {
        mapName(mapper);
        mapLore(mapper);

        return this;
    }

    public ItemEditor map(@NotNull String target, @NotNull String replacement) {
        mapName(target, replacement);
        mapLore(target, replacement);

        return this;
    }

    public ItemEditor mapName(@NotNull Function<String, String> mapper) {
        if (!stack.hasItemMeta()) return this;

        ItemMeta itemMeta = stack.getItemMeta();
        if (!itemMeta.hasDisplayName()) return this;

        itemMeta.setDisplayName(mapper.apply(itemMeta.getDisplayName()));

        stack.setItemMeta(itemMeta);
        return this;
    }

    public ItemEditor mapName(@NotNull String target, @NotNull String replacement) {
        if (!stack.hasItemMeta()) return this;

        ItemMeta itemMeta = stack.getItemMeta();
        if (!itemMeta.hasDisplayName()) return this;

        itemMeta.setDisplayName(itemMeta.getDisplayName().replace(target, replacement));

        stack.setItemMeta(itemMeta);
        return this;
    }

    public ItemEditor mapLore(@NotNull String target, @NotNull String replacement) {
        if (!stack.hasItemMeta()) return this;

        ItemMeta itemMeta = stack.getItemMeta();
        if (!itemMeta.hasLore()) return this;

        List<String> lore = itemMeta.getLore();

        for (int index = 0; index < lore.size(); index++) {
            lore.set(index, lore.get(index).replace(target, replacement));
        }

        itemMeta.setLore(lore);

        stack.setItemMeta(itemMeta);
        return this;
    }

    public ItemEditor mapLore(@NotNull Function<String, String> mapper) {
        if (!stack.hasItemMeta()) return this;

        ItemMeta itemMeta = stack.getItemMeta();
        if (!itemMeta.hasLore()) return this;

        List<String> lore = itemMeta.getLore();

        for (int index = 0; index < lore.size(); index++) {
            lore.set(index, mapper.apply(lore.get(index)));
        }

        itemMeta.setLore(lore);

        stack.setItemMeta(itemMeta);
        return this;
    }

    public ItemEditor mapLore(@NotNull String target, @NotNull List<String> replacement) {
        if (!stack.hasItemMeta()) return this;

        ItemMeta itemMeta = stack.getItemMeta();
        if (!itemMeta.hasLore()) return this;

        List<String> lore = itemMeta.getLore();

        final List<String> newLore = lore.stream()
                .flatMap(loreLine -> {
                    if (!loreLine.contains(target)) return Stream.of(loreLine);
                    return replacement.stream().map(line -> loreLine.replace(target, line));
                })
                .collect(Collectors.toList());

        itemMeta.setLore(newLore);

        stack.setItemMeta(itemMeta);
        return this;
    }

    public ItemEditor meta(@NotNull Consumer<ItemMeta> metaConsumer) {
        ItemMeta itemMeta = stack.getItemMeta();

        metaConsumer.accept(itemMeta);

        stack.setItemMeta(itemMeta);
        return this;
    }

    public <T extends ItemMeta> ItemEditor meta(@NotNull Class<T> metaClass, @NotNull Consumer<T> metaConsumer) {
        ItemMeta itemMeta = stack.getItemMeta();
        if (!itemMeta.getClass().isAssignableFrom(metaClass)) {
            throw new IllegalArgumentException("Meta class type different than actual type");
        }

        T meta = metaClass.cast(itemMeta);
        metaConsumer.accept(meta);

        stack.setItemMeta(meta);
        return this;
    }

    public ItemStack getItem() {
        return stack;
    }
}
