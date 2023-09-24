package io.github.johnnypixelz.utilizer.itemstack;

import io.github.johnnypixelz.utilizer.text.Colors;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemEditor {
    private final ItemStack stack;

    ItemEditor(@Nonnull ItemStack stack) {
        this.stack = stack;
    }

    ItemEditor(@Nonnull Material material) {
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

    public ItemEditor setDisplayName(@Nonnull String name) {
        return meta(itemMeta -> {
            itemMeta.setDisplayName(Colors.color(name));
        });
    }

    public ItemEditor setType(@Nonnull Material material) {
        stack.setType(material);

        return this;
    }

    public ItemEditor setCustomModelData(int customModelData) {
        return meta(itemMeta -> {
            itemMeta.setCustomModelData(customModelData == 0 ? null : customModelData);
        });
    }

    public ItemEditor removeCustomModelData() {
        return meta(itemMeta -> {
            itemMeta.setCustomModelData(null);
        });
    }

    public ItemEditor setDurability(int durability) {
        if (stack.getItemMeta() instanceof Damageable) {
            return meta(Damageable.class, damageable -> {
                damageable.setDamage(durability);
            });
        }

        return this;
    }

    public ItemEditor setLore(@Nonnull List<String> lore) {
        return meta(itemMeta -> {
            final List<String> newLore = lore.stream()
                    .map(Colors::color)
                    .collect(Collectors.toList());

            itemMeta.setLore(newLore);
        });
    }

    public ItemEditor setLore(@Nonnull String... lore) {
        return setLore(Arrays.asList(lore));
    }

    public ItemEditor addLore(@Nonnull List<String> lore) {
        if (!stack.getItemMeta().hasLore()) return setLore(lore);

        return meta(itemMeta -> {
            final List<String> newLore = Objects.requireNonNull(itemMeta.getLore());
            newLore.addAll(lore.stream().map(Colors::color).collect(Collectors.toList()));
            itemMeta.setLore(newLore);
        });
    }

    public ItemEditor addLore(@Nonnull String... lore) {
        return addLore(Arrays.asList(lore));
    }

    public ItemEditor removeLore() {
        return meta(itemMeta -> {
            itemMeta.setLore(null);
        });
    }

    public ItemEditor setFlags(@Nonnull List<ItemFlag> flags) {
        return meta(itemMeta -> {
           itemMeta.getItemFlags().forEach(itemMeta::removeItemFlags);
           flags.forEach(itemMeta::addItemFlags);
        });
    }

    public ItemEditor setFlags(@Nonnull ItemFlag... flags) {
        return setFlags(Arrays.asList(flags));
    }

    public ItemEditor addFlags(@Nonnull List<ItemFlag> flags) {
        return meta(itemMeta -> {
            flags.forEach(itemMeta::addItemFlags);
        });
    }

    public ItemEditor addFlags(@Nonnull ItemFlag... flags) {
        return addFlags(Arrays.asList(flags));
    }

    public ItemEditor removeFlags(@Nonnull List<ItemFlag> flags) {
        ItemMeta itemMeta = stack.getItemMeta();

        flags.forEach(itemMeta::removeItemFlags);

        stack.setItemMeta(itemMeta);
        return this;
    }

    public ItemEditor removeFlags(@Nonnull ItemFlag... flags) {
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

    public ItemEditor map(@Nonnull Function<String, String> mapper) {
        mapName(mapper);
        mapLore(mapper);

        return this;
    }

    public ItemEditor map(@Nonnull String target, @Nonnull String replacement) {
        mapName(target, replacement);
        mapLore(target, replacement);

        return this;
    }

    public ItemEditor mapName(@Nonnull Function<String, String> mapper) {
        if (!stack.hasItemMeta()) return this;

        ItemMeta itemMeta = stack.getItemMeta();
        if (!itemMeta.hasDisplayName()) return this;

        itemMeta.setDisplayName(mapper.apply(itemMeta.getDisplayName()));

        stack.setItemMeta(itemMeta);
        return this;
    }

    public ItemEditor mapName(@Nonnull String target, @Nonnull String replacement) {
        if (!stack.hasItemMeta()) return this;

        ItemMeta itemMeta = stack.getItemMeta();
        if (!itemMeta.hasDisplayName()) return this;

        itemMeta.setDisplayName(itemMeta.getDisplayName().replace(target, replacement));

        stack.setItemMeta(itemMeta);
        return this;
    }

    public ItemEditor mapLore(@Nonnull String target, @Nonnull String replacement) {
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

    public ItemEditor mapLore(@Nonnull Function<String, String> mapper) {
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

    public ItemEditor mapLore(@Nonnull String target, @Nonnull List<String> replacement) {
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

    public ItemEditor meta(@Nonnull Consumer<ItemMeta> metaConsumer) {
        ItemMeta itemMeta = stack.getItemMeta();

        metaConsumer.accept(itemMeta);

        stack.setItemMeta(itemMeta);
        return this;
    }

    public <T extends ItemMeta> ItemEditor meta(@Nonnull Class<T> metaClass, @Nonnull Consumer<T> metaConsumer) {
        ItemMeta itemMeta = stack.getItemMeta();
        if (!metaClass.isAssignableFrom(itemMeta.getClass())) {
            throw new IllegalArgumentException("Meta class type different than actual type. Expected " + itemMeta.getClass().getSimpleName() + " but got " + metaClass.getSimpleName() + ".");
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
