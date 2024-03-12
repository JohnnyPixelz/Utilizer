package io.github.johnnypixelz.utilizer.itemstack;

import io.github.johnnypixelz.utilizer.cache.Cache;
import io.github.johnnypixelz.utilizer.text.Colors;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
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
                final List<String> lore = Objects.requireNonNull(itemMeta.getLore()).stream().map(Colors::color).collect(Collectors.toList());

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

    public ItemEditor setLoreIf(boolean condition, @Nonnull List<String> lore) {
        if (condition) return setLore(lore);
        return this;
    }

    public ItemEditor setLoreIf(boolean condition, @Nonnull String... lore) {
        return setLoreIf(condition, Arrays.asList(lore));
    }

    public ItemEditor addLore(@Nonnull List<String> lore) {
        return meta(itemMeta -> {
            if (!itemMeta.hasLore()) {
                setLore(lore);
                return;
            }

            final List<String> oldLore = itemMeta.getLore();
            if (oldLore == null) {
                setLore(lore);
                return;
            }

            oldLore.addAll(lore.stream().map(Colors::color).toList());
            itemMeta.setLore(oldLore);
        });
    }

    public ItemEditor addLore(@Nonnull String... lore) {
        return addLore(Arrays.asList(lore));
    }

    public ItemEditor addLoreIf(boolean condition, @Nonnull List<String> lore) {
        if (condition) return addLore(lore);
        return this;
    }

    public ItemEditor addLoreIf(boolean condition, @Nonnull String... lore) {
        return addLoreIf(condition, Arrays.asList(lore));
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
        return meta(itemMeta -> {
            flags.forEach(itemMeta::removeItemFlags);
        });
    }

    public ItemEditor removeFlags(@Nonnull ItemFlag... flags) {
        return meta(itemMeta -> {
            itemMeta.removeItemFlags(flags);
        });
    }

    public ItemEditor clearFlags() {
        return meta(itemMeta -> {
            itemMeta.removeItemFlags(itemMeta.getItemFlags().toArray(new ItemFlag[0]));
        });
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

    public ItemEditor map(@Nonnull String target, @Nonnull Supplier<String> replacement) {
        mapName(target, replacement);
        mapLore(target, replacement);

        return this;
    }

    public ItemEditor mapName(@Nonnull String target, @Nonnull String replacement) {
        return meta(itemMeta -> {
            if (!itemMeta.hasDisplayName()) return;

            itemMeta.setDisplayName(itemMeta.getDisplayName().replace(target, replacement));
        });
    }

    public ItemEditor mapName(@Nonnull String target, @Nonnull Supplier<String> replacement) {
        return meta(itemMeta -> {
            if (!itemMeta.hasDisplayName()) return;
            if (!itemMeta.getDisplayName().contains(target)) return;

            itemMeta.setDisplayName(itemMeta.getDisplayName().replace(target, replacement.get()));
        });
    }

    public ItemEditor mapName(@Nonnull Function<String, String> mapper) {
        return meta(itemMeta -> {
            if (!itemMeta.hasDisplayName()) return;

            itemMeta.setDisplayName(mapper.apply(itemMeta.getDisplayName()));
        });
    }

    public ItemEditor mapLore(@Nonnull String target, @Nonnull String replacement) {
        return meta(itemMeta -> {
            if (!itemMeta.hasLore()) return;

            List<String> lore = itemMeta.getLore();
            if (lore == null) return;

            lore.replaceAll(line -> line.replace(target, replacement));

            itemMeta.setLore(lore);
        });
    }

    public ItemEditor mapLore(@Nonnull String target, @Nonnull Supplier<String> replacement) {
        return meta(itemMeta -> {
            if (!itemMeta.hasLore()) return;

            List<String> lore = itemMeta.getLore();
            if (lore == null) return;

            final Cache<String> replacementCache = Cache.suppliedBy(replacement);

            final List<String> list = lore.stream()
                    .map(line -> {
                        if (!line.contains(target)) return line;
                        return line.replace(target, replacementCache.get());
                    })
                    .toList();

            itemMeta.setLore(list);
        });
    }

    public ItemEditor mapLore(@Nonnull Function<String, String> mapper) {
        return meta(itemMeta -> {
            if (!itemMeta.hasLore()) return;

            final List<String> lore = itemMeta.getLore();
            if (lore == null) return;

            lore.replaceAll(mapper::apply);

            itemMeta.setLore(lore);
        });
    }

    public ItemEditor mapLoreMulti(@Nonnull String target, @Nonnull List<String> replacement) {
        return meta(itemMeta -> {
            if (!itemMeta.hasLore()) return;

            final List<String> lore = itemMeta.getLore();
            if (lore == null) return;

            final List<String> newLore = lore.stream()
                    .flatMap(loreLine -> {
                        if (!loreLine.contains(target)) return Stream.of(loreLine);
                        return replacement.stream().map(line -> loreLine.replace(target, line));
                    })
                    .toList();

            itemMeta.setLore(newLore);
        });
    }

    public ItemEditor mapLoreMulti(@Nonnull String target, @Nonnull Supplier<List<String>> replacement) {
        return meta(itemMeta -> {
            if (!itemMeta.hasLore()) return;

            final List<String> lore = itemMeta.getLore();
            if (lore == null) return;

            final Cache<List<String>> replacementCache = Cache.suppliedBy(replacement);

            final List<String> newLore = lore.stream()
                    .flatMap(loreLine -> {
                        if (!loreLine.contains(target)) return Stream.of(loreLine);
                        return replacementCache.get().stream().map(line -> loreLine.replace(target, line));
                    })
                    .toList();

            itemMeta.setLore(newLore);
        });
    }

    public ItemEditor meta(@Nonnull Consumer<ItemMeta> metaConsumer) {
        ItemMeta itemMeta = stack.getItemMeta();
        if (itemMeta == null) return this;

        metaConsumer.accept(itemMeta);

        stack.setItemMeta(itemMeta);
        return this;
    }

    public <T extends ItemMeta> ItemEditor meta(@Nonnull Class<T> metaClass, @Nonnull Consumer<T> metaConsumer) {
        ItemMeta itemMeta = stack.getItemMeta();
        if (itemMeta == null) return this;

        if (!metaClass.isAssignableFrom(itemMeta.getClass())) {
            throw new IllegalArgumentException("Meta class type different than actual type. Expected " + itemMeta.getClass().getSimpleName() + " but got " + metaClass.getSimpleName() + ".");
        }

        T meta = metaClass.cast(itemMeta);
        metaConsumer.accept(meta);

        stack.setItemMeta(meta);
        return this;
    }

    public ItemEditor pdc(@Nonnull Consumer<PersistentDataContainer> container) {
        return meta(itemMeta -> {
            container.accept(itemMeta.getPersistentDataContainer());
        });
    }

    public ItemStack getItem() {
        return stack;
    }

}
