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
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Items {

    public static boolean isNull(ItemStack item) {
        return item == null || item.getType().equals(Material.AIR);
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
        return create(material, displayName, Arrays.asList(lore));
    }

    public static ItemStack color(@NotNull ItemStack stack) {
        return meta(stack, itemMeta -> {
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

    public static ItemStack setDisplayName(@NotNull ItemStack stack, @NotNull String name) {
        return meta(stack, itemMeta -> {
            itemMeta.setDisplayName(Colors.color(name));
        });
    }

    public static ItemStack setLore(@NotNull ItemStack stack, @NotNull List<String> lore) {
        return meta(stack, itemMeta -> {
            final List<String> newLore = lore.stream()
                    .map(Colors::color)
                    .collect(Collectors.toList());

            itemMeta.setLore(newLore);
        });
    }

    public static ItemStack setLore(@NotNull ItemStack stack, @NotNull String... lore) {
        return setLore(stack, Arrays.asList(lore));
    }

    public static ItemStack addLore(@NotNull ItemStack stack, @NotNull List<String> lore) {
        return meta(stack, itemMeta -> {
            if (!itemMeta.hasLore()) {
                setLore(stack, lore);
                return;
            }

            final List<String> newLore = Objects.requireNonNull(itemMeta.getLore());
            newLore.addAll(lore.stream().map(Colors::color).collect(Collectors.toList()));
            itemMeta.setLore(newLore);
        });
    }

    public static ItemStack addLore(@NotNull ItemStack stack, @NotNull String... lore) {
        return addLore(stack, Arrays.asList(lore));
    }

    public static ItemStack removeLore(@NotNull ItemStack stack) {
        return meta(stack, itemMeta -> {
            itemMeta.setLore(null);
        });
    }

    public static ItemStack setFlags(@NotNull ItemStack stack, @NotNull List<ItemFlag> flags) {
        return meta(stack, itemMeta -> {
            itemMeta.getItemFlags().forEach(itemMeta::removeItemFlags);
            flags.forEach(itemMeta::addItemFlags);
        });
    }

    public static ItemStack setFlags(@NotNull ItemStack stack, @NotNull ItemFlag... flags) {
        return setFlags(stack, Arrays.asList(flags));
    }

    public static ItemStack addFlags(@NotNull ItemStack stack, @NotNull List<ItemFlag> flags) {
        return meta(stack, itemMeta -> {
            flags.forEach(itemMeta::addItemFlags);
        });
    }

    public static ItemStack addFlags(@NotNull ItemStack stack, @NotNull ItemFlag... flags) {
        return addFlags(stack, Arrays.asList(flags));
    }

    public static ItemStack removeFlags(@NotNull ItemStack stack, @NotNull List<ItemFlag> flags) {
        return meta(stack, itemMeta -> {
            flags.forEach(itemMeta::removeItemFlags);
        });
    }

    public static ItemStack removeFlags(@NotNull ItemStack stack, @NotNull ItemFlag... flags) {
        return removeFlags(stack, Arrays.asList(flags));
    }

    public static ItemStack clearFlags(@NotNull ItemStack stack) {
        return meta(stack, itemMeta -> {
            itemMeta.removeItemFlags(itemMeta.getItemFlags().toArray(new ItemFlag[0]));
        });
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

        stack.removeEnchantment(
                stack.getType() == Material.BOW
                ? Enchantment.LUCK
                : Enchantment.ARROW_INFINITE
        );
        removeFlags(stack, ItemFlag.HIDE_ENCHANTS);

        return stack;
    }

    public static ItemStack map(@NotNull ItemStack stack, @NotNull String target, @NotNull String replacement) {
        mapName(stack, target, replacement);
        mapLore(stack, target, replacement);

        return stack;
    }

    public static ItemStack map(@NotNull ItemStack stack, @NotNull Function<String, String> mapper) {
        mapName(stack, mapper);
        mapLore(stack, mapper);

        return stack;
    }

    public static ItemStack mapName(@NotNull ItemStack stack, @NotNull String target, @NotNull String replacement) {
        return meta(stack, itemMeta -> {
            if (!itemMeta.hasDisplayName()) return;

            itemMeta.setDisplayName(itemMeta.getDisplayName().replace(target, replacement));
        });
    }

    public static ItemStack mapName(@NotNull ItemStack stack, @NotNull Function<String, String> mapper) {
        return meta(stack, itemMeta -> {
            if (!itemMeta.hasDisplayName()) return;

            itemMeta.setDisplayName(mapper.apply(itemMeta.getDisplayName()));
        });
    }

    public static ItemStack mapLore(@NotNull ItemStack stack, @NotNull String target, @NotNull String replacement) {
        return meta(stack, itemMeta -> {
            if (!itemMeta.hasLore()) return;

            List<String> lore = itemMeta.getLore();
            Objects.requireNonNull(lore);

            for (int index = 0; index < lore.size(); index++) {
                lore.set(index, lore.get(index).replace(target, replacement));
            }

            itemMeta.setLore(lore);
        });
    }

    public static ItemStack mapLore(@NotNull ItemStack stack, @NotNull Function<String, String> mapper) {
        return meta(stack, itemMeta -> {
            if (!itemMeta.hasLore()) return;

            List<String> lore = itemMeta.getLore();
            Objects.requireNonNull(lore);

            for (int index = 0; index < lore.size(); index++) {
                lore.set(index, mapper.apply(lore.get(index)));
            }

            itemMeta.setLore(lore);
        });
    }

    public static ItemStack mapLore(@NotNull ItemStack stack, @NotNull String target, @NotNull List<String> replacement) {
        return meta(stack, itemMeta -> {
            if (!itemMeta.hasLore()) return;

            List<String> lore = itemMeta.getLore();
            Objects.requireNonNull(lore);

            final List<String> newLore = lore.stream()
                    .flatMap(loreLine -> {
                        if (!loreLine.contains(target)) return Stream.of(loreLine);
                        return replacement.stream().map(line -> loreLine.replace(target, line));
                    })
                    .collect(Collectors.toList());

            itemMeta.setLore(newLore);
        });
    }

    public static ItemStack meta(@NotNull ItemStack stack, @NotNull Consumer<ItemMeta> metaConsumer) {
        ItemMeta itemMeta = stack.getItemMeta();
        if (itemMeta == null) return stack;

        metaConsumer.accept(itemMeta);

        stack.setItemMeta(itemMeta);
        return stack;
    }

    public static <T extends ItemMeta> ItemStack meta(@NotNull ItemStack stack, @NotNull Class<T> metaClass, @NotNull Consumer<T> metaConsumer) {
        ItemMeta itemMeta = stack.getItemMeta();
        if (itemMeta == null) return stack;

        if (!itemMeta.getClass().isAssignableFrom(metaClass)) {
            throw new IllegalArgumentException("Meta class type different than actual type");
        }

        T meta = metaClass.cast(itemMeta);
        metaConsumer.accept(meta);

        stack.setItemMeta(meta);
        return stack;
    }

    public static ItemEditor edit(@NotNull ItemStack stack) {
        return new ItemEditor(stack);
    }

    public static ItemEditor edit(@NotNull Material material) {
        return new ItemEditor(material);
    }
}
