package io.github.johnnypixelz.utilizer.itemstack;

import com.cryptomorin.xseries.XItemStack;
import com.cryptomorin.xseries.XPotion;
import com.google.common.base.Enums;
import com.google.common.base.Strings;
import io.github.johnnypixelz.utilizer.config.Parse;
import io.github.johnnypixelz.utilizer.file.adapters.GraphAdapterBuilder;
import io.github.johnnypixelz.utilizer.text.Colors;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.block.Banner;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TropicalFish;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.map.MapView;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cryptomorin.xseries.XMaterial.supports;

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

    public static ItemStack parse(@NotNull ConfigurationSection section) {
        final String materialString;
        if (section.isString("type")) {
            materialString = section.getString("type", "STONE");
        } else if (section.isString("material")) {
            materialString = section.getString("material", "STONE");
        } else {
            materialString = "STONE";
        }

        final Material material = Optional.ofNullable(Material.matchMaterial(materialString))
                .orElse(Material.STONE);

        final ItemEditor itemEditor = Items.edit(material);
        Optional.ofNullable(section.getString("name"))
                .or(() -> Optional.ofNullable(section.getString("displayname")))
                .ifPresent(itemEditor::setDisplayName);

        if (section.isList("lore")) {
            itemEditor.setLore(section.getStringList("lore"));
        }

        if (section.isBoolean("glow")) {
            final boolean glow = section.getBoolean("glow", false);
            itemEditor.setGlow(glow);
        }

        if (section.isSet("amount")) {
            final int amount = section.getInt("amount", 1);
            if (amount >= 1 && amount <= 64) {
                itemEditor.setAmount(amount);
            }
        }

        final int damage = section.getInt("damage");
        final int durability = section.getInt("durability");
        if (damage > 0) itemEditor.setDurability(damage);
        else if (durability > 0) itemEditor.setDurability(durability);

        final int customModelData = section.getInt("custom-model-data");
        if (customModelData > 0) itemEditor.setCustomModelData(customModelData);

        // Meta Handling
        final ItemMeta meta = itemEditor.getItem().getItemMeta();

        if (meta instanceof SkullMeta) {
            String skull = section.getString("skull");
            if (skull != null) {
                Skulls.mutateSkull(itemEditor.getItem(), skull);
            }
        } else if (meta instanceof BannerMeta bannerMeta) {
            // TODO needs work
            ConfigurationSection patterns = section.getConfigurationSection("patterns");

            if (patterns != null) {
                for (String pattern : patterns.getKeys(false)) {
                    PatternType type = PatternType.getByIdentifier(pattern);
                    if (type == null) type = Enums.getIfPresent(PatternType.class, pattern.toUpperCase(Locale.ENGLISH)).or(PatternType.BASE);
                    DyeColor color = Enums.getIfPresent(DyeColor.class, patterns.getString(pattern).toUpperCase(Locale.ENGLISH)).or(DyeColor.WHITE);

                    bannerMeta.addPattern(new Pattern(color, type));
                }
            }
        } else if (meta instanceof LeatherArmorMeta leatherArmorMeta) {
            final String colorString = section.getString("color");
            if (colorString != null) {
                final Color color = Parse.color(colorString);
                leatherArmorMeta.setColor(color);
            }
        } else if (meta instanceof PotionMeta potionMeta) {
            // TODO needs work
            for (String effects : section.getStringList("effects")) {
                XPotion.Effect effect = XPotion.parseEffect(effects);
                if (effect.hasChance()) potionMeta.addCustomEffect(effect.getEffect(), true);
            }

            String baseEffect = section.getString("base-effect");
            if (!Strings.isNullOrEmpty(baseEffect)) {
                List<String> split = Arrays.asList(baseEffect.split(","));
                PotionType type = Enums.getIfPresent(PotionType.class, split.get(0).trim().toUpperCase(Locale.ENGLISH)).or(PotionType.UNCRAFTABLE);
                boolean extended = split.size() != 1 && Boolean.parseBoolean(split.get(1).trim());
                boolean upgraded = split.size() > 2 && Boolean.parseBoolean(split.get(2).trim());
                PotionData potionData = new PotionData(type, extended, upgraded);
                potionMeta.setBasePotionData(potionData);
            }

            if (section.contains("color")) {
                potionMeta.setColor(Color.fromRGB(section.getInt("color")));
            }
        } else if (meta instanceof BlockStateMeta blockStateMeta) {
            BlockState state = blockStateMeta.getBlockState();

            if (state instanceof CreatureSpawner creatureSpawner) {
                creatureSpawner.setSpawnedType(Enums.getIfPresent(EntityType.class, section.getString("spawner").toUpperCase(Locale.ENGLISH)).orNull());
                creatureSpawner.update(true);
                blockStateMeta.setBlockState(creatureSpawner);
            } else if (state instanceof ShulkerBox shulkerBox) {
                ConfigurationSection contentsSection = section.getConfigurationSection("contents");
                if (contentsSection != null) {

                    for (String key : contentsSection.getKeys(false)) {
                        final ConfigurationSection contentSection = contentsSection.getConfigurationSection(key);
                        if (contentSection == null) continue;

                        ItemStack boxItem = parse(contentSection);
                        int slot = Parse.integer(key, 0);
                        shulkerBox.getInventory().setItem(slot, boxItem);
                    }

                    shulkerBox.update(true);
                    blockStateMeta.setBlockState(shulkerBox);
                }
            } else if (state instanceof Banner banner) {
                ConfigurationSection patterns = section.getConfigurationSection("patterns");

                if (patterns != null) {
                    for (String pattern : patterns.getKeys(false)) {
                        PatternType type = PatternType.getByIdentifier(pattern);
                        if (type == null) type = Enums.getIfPresent(PatternType.class, pattern.toUpperCase(Locale.ENGLISH)).or(PatternType.BASE);
                        DyeColor color = Enums.getIfPresent(DyeColor.class, patterns.getString(pattern).toUpperCase(Locale.ENGLISH)).or(DyeColor.WHITE);

                        banner.addPattern(new Pattern(color, type));
                    }

                    banner.update(true);
                    blockStateMeta.setBlockState(banner);
                }
            }
        } else if (meta instanceof FireworkMeta fireworkMeta) {
            fireworkMeta.setPower(section.getInt("power"));

            ConfigurationSection fireworkSection = section.getConfigurationSection("firework");
            if (fireworkSection != null) {
                FireworkEffect.Builder builder = FireworkEffect.builder();
                for (String fws : fireworkSection.getKeys(false)) {
                    ConfigurationSection fw = section.getConfigurationSection("firework." + fws);

                    builder.flicker(fw.getBoolean("flicker"));
                    builder.trail(fw.getBoolean("trail"));
                    builder.with(Enums.getIfPresent(FireworkEffect.Type.class, fw.getString("type")
                                    .toUpperCase(Locale.ENGLISH))
                            .or(FireworkEffect.Type.STAR));

                    ConfigurationSection colorsSection = fw.getConfigurationSection("colors");
                    List<String> fwColors = colorsSection.getStringList("base");
                    List<Color> colors = new ArrayList<>(fwColors.size());
                    for (String colorStr : fwColors) colors.add(Parse.color(colorStr));
                    builder.withColor(colors);

                    fwColors = colorsSection.getStringList("fade");
                    colors = new ArrayList<>(fwColors.size());
                    for (String colorStr : fwColors) colors.add(Parse.color(colorStr));
                    builder.withFade(colors);

                    fireworkMeta.addEffect(builder.build());
                }
            }
        } else if (meta instanceof BookMeta bookMeta) {
        } else if (meta instanceof MapMeta mapMeta) {
            ConfigurationSection mapSection = section.getConfigurationSection("map");

            if (mapSection != null) {
                mapMeta.setScaling(mapSection.getBoolean("scaling"));
                if (supports(11)) {
                    if (mapSection.isSet("location")) mapMeta.setLocationName(mapSection.getString("location"));
                    if (mapSection.isSet("color")) {
                        Color color = Parse.color(mapSection.getString("color"));
                        mapMeta.setColor(color);
                    }
                }

                if (supports(14)) {
                    ConfigurationSection view = mapSection.getConfigurationSection("view");
                    if (view != null) {
                        World world = Bukkit.getWorld(view.getString("world"));
                        if (world != null) {
                            MapView mapView = Bukkit.createMap(world);
                            mapView.setWorld(world);
                            mapView.setScale(Enums.getIfPresent(MapView.Scale.class, view.getString("scale")).or(MapView.Scale.NORMAL));
                            mapView.setLocked(view.getBoolean("locked"));
                            mapView.setTrackingPosition(view.getBoolean("tracking-position"));
                            mapView.setUnlimitedTracking(view.getBoolean("unlimited-tracking"));

                            ConfigurationSection centerSection = view.getConfigurationSection("center");
                            mapView.setCenterX(centerSection.getInt("x"));
                            mapView.setCenterZ(centerSection.getInt("z"));

                            mapMeta.setMapView(mapView);
                        }
                    }
                }
            }
        } else if (meta instanceof AxolotlBucketMeta axolotlBucketMeta) {
            String variantStr = section.getString("color");
            if (variantStr != null) {
                Axolotl.Variant variant = Enums.getIfPresent(Axolotl.Variant.class, variantStr.toUpperCase(Locale.ENGLISH)).or(Axolotl.Variant.BLUE);
                axolotlBucketMeta.setVariant(variant);
            }
        } else if (meta instanceof CompassMeta compassMeta) {
            compassMeta.setLodestoneTracked(section.getBoolean("tracked"));

            ConfigurationSection lodestone = section.getConfigurationSection("lodestone");
            if (lodestone != null) {
                World world = Bukkit.getWorld(lodestone.getString("world"));
                double x = lodestone.getDouble("x");
                double y = lodestone.getDouble("y");
                double z = lodestone.getDouble("z");
                compassMeta.setLodestone(new Location(world, x, y, z));
            }
        } else if (meta instanceof SuspiciousStewMeta suspiciousStewMeta) {
            for (String effects : section.getStringList("effects")) {
                XPotion.Effect effect = XPotion.parseEffect(effects);
                if (effect.hasChance()) suspiciousStewMeta.addCustomEffect(effect.getEffect(), true);
            }
        } else if (meta instanceof CrossbowMeta crossbowMeta) {
            final ConfigurationSection crossbowSection = section.getConfigurationSection("projectiles");
            if (crossbowSection != null) {
                for (String projectiles : crossbowSection.getKeys(false)) {
                    final ConfigurationSection projectileSection = crossbowSection.getConfigurationSection(projectiles);
                    if (projectileSection == null) continue;

                    ItemStack projectile = parse(projectileSection);
                    crossbowMeta.addChargedProjectile(projectile);
                }
            }
        } else if (meta instanceof TropicalFishBucketMeta tropicalFishBucketMeta) {
            DyeColor color = Enums.getIfPresent(DyeColor.class, section.getString("color")).or(DyeColor.WHITE);
            DyeColor patternColor = Enums.getIfPresent(DyeColor.class, section.getString("pattern-color")).or(DyeColor.WHITE);
            TropicalFish.Pattern pattern = Enums.getIfPresent(TropicalFish.Pattern.class, section.getString("pattern")).or(TropicalFish.Pattern.BETTY);

            tropicalFishBucketMeta.setBodyColor(color);
            tropicalFishBucketMeta.setPatternColor(patternColor);
            tropicalFishBucketMeta.setPattern(pattern);
        }

        final ItemStack item = itemEditor.getItem();
        item.setItemMeta(meta);

        return itemEditor.getItem();
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

    public static ItemStack setDurability(@NotNull ItemStack stack, int durability) {
        if (stack.getItemMeta() instanceof Damageable) {
            return meta(stack, Damageable.class, damageable -> {
                damageable.setDamage(durability);
            });
        }

        return stack;
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
        if (!stack.getItemMeta().hasLore()) return setLore(stack, lore);

        return meta(stack, itemMeta -> {
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
        if (!stack.getItemMeta().hasLore()) return stack;

        return meta(stack, itemMeta -> {
            List<String> lore = itemMeta.getLore();
            Objects.requireNonNull(lore);

            for (int index = 0; index < lore.size(); index++) {
                lore.set(index, lore.get(index).replace(target, replacement));
            }

            itemMeta.setLore(lore);
        });
    }

    public static ItemStack mapLore(@NotNull ItemStack stack, @NotNull Function<String, String> mapper) {
        if (!stack.getItemMeta().hasLore()) return stack;

        return meta(stack, itemMeta -> {
            List<String> lore = itemMeta.getLore();
            Objects.requireNonNull(lore);

            for (int index = 0; index < lore.size(); index++) {
                lore.set(index, mapper.apply(lore.get(index)));
            }

            itemMeta.setLore(lore);
        });
    }

    public static ItemStack mapLore(@NotNull ItemStack stack, @NotNull String target, @NotNull List<String> replacement) {
        if (!stack.getItemMeta().hasLore()) return stack;

        return meta(stack, itemMeta -> {
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
