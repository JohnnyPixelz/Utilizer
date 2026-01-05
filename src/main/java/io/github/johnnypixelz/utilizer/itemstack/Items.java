package io.github.johnnypixelz.utilizer.itemstack;

import com.cryptomorin.xseries.XPotion;
import com.google.common.base.Enums;
import com.google.common.base.Strings;
import dev.lone.itemsadder.api.CustomStack;
import io.github.johnnypixelz.utilizer.amount.Amount;
import io.github.johnnypixelz.utilizer.cache.Cache;
import io.github.johnnypixelz.utilizer.config.Parse;
import io.github.johnnypixelz.utilizer.depend.Dependencies;
import io.github.johnnypixelz.utilizer.gson.GsonProvider;
import io.github.johnnypixelz.utilizer.text.Colors;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cryptomorin.xseries.XMaterial.supports;


@SuppressWarnings("unused UnusedReturnValue")
public class Items {

    public static final ItemStack AIR = new ItemStack(Material.AIR);

    public static boolean isNull(ItemStack item) {
        return item == null || item.getType().equals(Material.AIR);
    }

    public static ItemStack create(@Nonnull Material material, @Nullable String displayName, @Nullable List<String> lore) {
        ItemStack stack = new ItemStack(material);

        if (displayName != null) {
            setDisplayName(stack, displayName);
        }

        if (lore != null) {
            setLore(stack, lore);
        }

        return stack;
    }

    public static ItemStack create(@Nonnull Material material, @Nullable String displayName, @Nullable String... lore) {
        return create(material, displayName, lore == null ? null : Arrays.asList(lore));
    }

    public static ItemStack parse(@Nullable ConfigurationSection section) {
        if (section == null) {
            return new ItemStack(Material.STONE);
        }

        final String materialString;
        if (section.isSet("material") && section.isString("material")) {
            materialString = section.getString("material", "STONE");
        } else if (section.isSet("type") && section.isString("type")) {
            materialString = section.getString("type", "STONE");
        } else {
            materialString = "STONE";
        }

        final ItemStack stack = Optional.ofNullable(Material.matchMaterial(materialString))
                .map(ItemStack::new)
                .or(() -> {
                    if (Dependencies.isEnabled("ItemsAdder")) {
                        return Optional.ofNullable(CustomStack.getInstance(materialString))
                                .map(CustomStack::getItemStack);
                    }

                    return Optional.empty();
                })
                .or(() -> {
                    if (Dependencies.isEnabled("Oraxen")) {
                        return OraxenItems.getOptionalItemById(materialString)
                                .map(ItemBuilder::build);
                    }

                    return Optional.empty();
                })
                .orElse(new ItemStack(Material.STONE));

        final ItemEditor itemEditor = Items.edit(stack);
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
            final String configAmount = section.getString("amount", "1");
            final Amount amount = Amount.parse(configAmount);
            final int finalAmount = amount.getAmount();
            if (finalAmount >= 1 && finalAmount <= 64) {
                itemEditor.setAmount(finalAmount);
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
                    if (type == null)
                        type = Enums.getIfPresent(PatternType.class, pattern.toUpperCase(Locale.ENGLISH)).or(PatternType.BASE);
                    DyeColor color = Enums.getIfPresent(DyeColor.class, patterns.getString(pattern, "").toUpperCase(Locale.ENGLISH)).or(DyeColor.WHITE);

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
                if (effect == null) continue;
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
                creatureSpawner.setSpawnedType(Enums.getIfPresent(EntityType.class, section.getString("spawner", "").toUpperCase(Locale.ENGLISH)).orNull());
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
                        if (type == null)
                            type = Enums.getIfPresent(PatternType.class, pattern.toUpperCase(Locale.ENGLISH)).or(PatternType.BASE);
                        DyeColor color = Enums.getIfPresent(DyeColor.class, patterns.getString(pattern, "").toUpperCase(Locale.ENGLISH)).or(DyeColor.WHITE);

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
                    if (fw == null) continue;

                    builder.flicker(fw.getBoolean("flicker", false));
                    builder.trail(fw.getBoolean("trail"));
                    builder.with(Enums.getIfPresent(FireworkEffect.Type.class, fw.getString("type", "")
                                    .toUpperCase(Locale.ENGLISH))
                            .or(FireworkEffect.Type.STAR));

                    ConfigurationSection colorsSection = fw.getConfigurationSection("colors");
                    if (colorsSection == null) continue;

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
                        World world = Bukkit.getWorld(view.getString("world", ""));
                        if (world != null) {
                            MapView mapView = Bukkit.createMap(world);
                            mapView.setWorld(world);
                            mapView.setScale(Enums.getIfPresent(MapView.Scale.class, view.getString("scale", "")).or(MapView.Scale.NORMAL));
                            mapView.setLocked(view.getBoolean("locked"));
                            mapView.setTrackingPosition(view.getBoolean("tracking-position"));
                            mapView.setUnlimitedTracking(view.getBoolean("unlimited-tracking"));

                            ConfigurationSection centerSection = view.getConfigurationSection("center");
                            if (centerSection != null) {
                                mapView.setCenterX(centerSection.getInt("x"));
                                mapView.setCenterZ(centerSection.getInt("z"));

                                mapMeta.setMapView(mapView);
                            }
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
                World world = Bukkit.getWorld(lodestone.getString("world", ""));
                double x = lodestone.getDouble("x");
                double y = lodestone.getDouble("y");
                double z = lodestone.getDouble("z");
                compassMeta.setLodestone(new Location(world, x, y, z));
            }
        } else if (meta instanceof SuspiciousStewMeta suspiciousStewMeta) {
            for (String effects : section.getStringList("effects")) {
                XPotion.Effect effect = XPotion.parseEffect(effects);
                if (effect == null) continue;
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
            DyeColor color = Enums.getIfPresent(DyeColor.class, section.getString("color", "")).or(DyeColor.WHITE);
            DyeColor patternColor = Enums.getIfPresent(DyeColor.class, section.getString("pattern-color", "")).or(DyeColor.WHITE);
            TropicalFish.Pattern pattern = Enums.getIfPresent(TropicalFish.Pattern.class, section.getString("pattern", "")).or(TropicalFish.Pattern.BETTY);

            tropicalFishBucketMeta.setBodyColor(color);
            tropicalFishBucketMeta.setPatternColor(patternColor);
            tropicalFishBucketMeta.setPattern(pattern);
        }

        return itemEditor.getItem();
    }

    public static ItemStack color(@Nonnull ItemStack stack) {
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

    public static ItemStack setDisplayName(@Nonnull ItemStack stack, @Nonnull String name) {
        return meta(stack, itemMeta -> {
            itemMeta.setDisplayName(Colors.color(name));
        });
    }

    public static ItemStack setDurability(@Nonnull ItemStack stack, int durability) {
        if (stack.getItemMeta() instanceof Damageable) {
            return meta(stack, Damageable.class, damageable -> {
                damageable.setDamage(durability);
            });
        }

        return stack;
    }

    public static ItemStack setLore(@Nonnull ItemStack stack, @Nonnull List<String> lore) {
        return meta(stack, itemMeta -> {
            final List<String> newLore = lore.stream()
                    .map(Colors::color)
                    .collect(Collectors.toList());

            itemMeta.setLore(newLore);
        });
    }

    public static ItemStack setLore(@Nonnull ItemStack stack, @Nonnull String... lore) {
        return setLore(stack, Arrays.asList(lore));
    }

    public static ItemStack setLoreIf(boolean condition, @Nonnull ItemStack stack, @Nonnull List<String> lore) {
        if (condition) return setLore(stack, lore);
        return stack;
    }

    public static ItemStack setLoreIf(boolean condition, @Nonnull ItemStack stack, @Nonnull String... lore) {
        return setLoreIf(condition, stack, Arrays.asList(lore));
    }

    public static ItemStack addLore(@Nonnull ItemStack stack, @Nonnull List<String> lore) {
        final ItemMeta existingMeta = stack.getItemMeta();
        if (existingMeta == null) return setLore(stack, lore);
        if (!existingMeta.hasLore()) return setLore(stack, lore);

        final List<String> oldLore = existingMeta.getLore();
        if (oldLore == null) return setLore(stack, lore);

        return meta(stack, itemMeta -> {
            oldLore.addAll(lore.stream().map(Colors::color).toList());
            itemMeta.setLore(oldLore);
        });
    }

    public static ItemStack addLore(@Nonnull ItemStack stack, @Nonnull String... lore) {
        return addLore(stack, Arrays.asList(lore));
    }

    public static ItemStack addLoreIf(boolean condition, @Nonnull ItemStack stack, @Nonnull List<String> lore) {
        if (condition) return addLore(stack, lore);
        return stack;
    }

    public static ItemStack addLoreIf(boolean condition, @Nonnull ItemStack stack, @Nonnull String... lore) {
        return addLoreIf(condition, stack, Arrays.asList(lore));
    }

    public static ItemStack removeLore(@Nonnull ItemStack stack) {
        return meta(stack, itemMeta -> {
            itemMeta.setLore(null);
        });
    }

    public static ItemStack setFlags(@Nonnull ItemStack stack, @Nonnull List<ItemFlag> flags) {
        return meta(stack, itemMeta -> {
            itemMeta.getItemFlags().forEach(itemMeta::removeItemFlags);
            flags.forEach(itemMeta::addItemFlags);
        });
    }

    public static ItemStack setFlags(@Nonnull ItemStack stack, @Nonnull ItemFlag... flags) {
        return setFlags(stack, Arrays.asList(flags));
    }

    public static ItemStack addFlags(@Nonnull ItemStack stack, @Nonnull List<ItemFlag> flags) {
        return meta(stack, itemMeta -> {
            flags.forEach(itemMeta::addItemFlags);
        });
    }

    public static ItemStack addFlags(@Nonnull ItemStack stack, @Nonnull ItemFlag... flags) {
        return addFlags(stack, Arrays.asList(flags));
    }

    public static ItemStack removeFlags(@Nonnull ItemStack stack, @Nonnull List<ItemFlag> flags) {
        return meta(stack, itemMeta -> {
            flags.forEach(itemMeta::removeItemFlags);
        });
    }

    public static ItemStack removeFlags(@Nonnull ItemStack stack, @Nonnull ItemFlag... flags) {
        return meta(stack, itemMeta -> {
            itemMeta.removeItemFlags(flags);
        });
    }

    public static ItemStack clearFlags(@Nonnull ItemStack stack) {
        return meta(stack, itemMeta -> {
            itemMeta.removeItemFlags(itemMeta.getItemFlags().toArray(new ItemFlag[0]));
        });
    }

    public static ItemStack glow(@Nonnull ItemStack stack) {
        stack.addUnsafeEnchantment(stack.getType() != Material.BOW ? Enchantment.ARROW_INFINITE : Enchantment.LUCK, 10);
        addFlags(stack, ItemFlag.HIDE_ENCHANTS);

        return stack;
    }

    public static ItemStack setGlow(@Nonnull ItemStack stack, boolean glow) {
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

    public static ItemStack map(@Nonnull ItemStack stack, @Nonnull String target, @Nonnull String replacement) {
        mapName(stack, target, replacement);
        mapLore(stack, target, replacement);

        return stack;
    }

    public static ItemStack map(@Nonnull ItemStack stack, @Nonnull String target, @Nonnull Supplier<String> replacement) {
        mapName(stack, target, replacement);
        mapLore(stack, target, replacement);

        return stack;
    }

    public static ItemStack map(@Nonnull ItemStack stack, @Nonnull Function<String, String> mapper) {
        mapName(stack, mapper);
        mapLore(stack, mapper);

        return stack;
    }

    public static ItemStack mapName(@Nonnull ItemStack stack, @Nonnull String target, @Nonnull String replacement) {
        return meta(stack, itemMeta -> {
            if (!itemMeta.hasDisplayName()) return;

            itemMeta.setDisplayName(itemMeta.getDisplayName().replace(target, replacement));
        });
    }

    public static ItemStack mapName(@Nonnull ItemStack stack, @Nonnull String target, @Nonnull Supplier<String> replacement) {
        return meta(stack, itemMeta -> {
            if (!itemMeta.hasDisplayName()) return;

            if (!itemMeta.getDisplayName().contains(target)) return;

            itemMeta.setDisplayName(itemMeta.getDisplayName().replace(target, replacement.get()));
        });
    }

    public static ItemStack mapName(@Nonnull ItemStack stack, @Nonnull Function<String, String> mapper) {
        return meta(stack, itemMeta -> {
            if (!itemMeta.hasDisplayName()) return;

            itemMeta.setDisplayName(mapper.apply(itemMeta.getDisplayName()));
        });
    }

    public static ItemStack mapLore(@Nonnull ItemStack stack, @Nonnull String target, @Nonnull String replacement) {
        return meta(stack, itemMeta -> {
            if (!itemMeta.hasLore()) return;

            List<String> lore = itemMeta.getLore();
            if (lore == null) return;

            lore.replaceAll(line -> line.replace(target, replacement));

            itemMeta.setLore(lore);
        });
    }

    public static ItemStack mapLore(@Nonnull ItemStack stack, @Nonnull String target, @Nonnull Supplier<String> replacement) {
        return meta(stack, itemMeta -> {
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

    public static ItemStack mapLore(@Nonnull ItemStack stack, @Nonnull Function<String, String> mapper) {
        return meta(stack, itemMeta -> {
            if (!itemMeta.hasLore()) return;

            final List<String> lore = itemMeta.getLore();
            if (lore == null) return;

            lore.replaceAll(mapper::apply);

            itemMeta.setLore(lore);
        });
    }

    public static ItemStack mapLoreMulti(@Nonnull ItemStack stack, @Nonnull String target, @Nonnull List<String> replacement) {
        return meta(stack, itemMeta -> {
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

    public static ItemStack mapLoreMulti(@Nonnull ItemStack stack, @Nonnull String target, @Nonnull Supplier<List<String>> replacement) {
        return meta(stack, itemMeta -> {
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

    public static ItemStack meta(@Nonnull ItemStack stack, @Nonnull Consumer<ItemMeta> metaConsumer) {
        ItemMeta itemMeta = stack.getItemMeta();
        if (itemMeta == null) return stack;

        metaConsumer.accept(itemMeta);

        stack.setItemMeta(itemMeta);
        return stack;
    }

    public static <T extends ItemMeta> ItemStack meta(@Nonnull ItemStack stack, @Nonnull Class<T> metaClass, @Nonnull Consumer<T> metaConsumer) {
        ItemMeta itemMeta = stack.getItemMeta();
        if (itemMeta == null) return stack;

        if (!metaClass.isAssignableFrom(itemMeta.getClass())) {
            return stack;
        }

        T meta = metaClass.cast(itemMeta);
        metaConsumer.accept(meta);

        stack.setItemMeta(meta);
        return stack;
    }

    public static <T extends ItemMeta> ItemStack metaOrThrow(@Nonnull ItemStack stack, @Nonnull Class<T> metaClass, @Nonnull Consumer<T> metaConsumer) {
        ItemMeta itemMeta = stack.getItemMeta();
        if (itemMeta == null) return stack;

        if (!metaClass.isAssignableFrom(itemMeta.getClass())) {
            throw new IllegalArgumentException("Meta class type different than actual type. Expected " + itemMeta.getClass().getSimpleName() + " but got " + metaClass.getSimpleName() + ".");
        }

        T meta = metaClass.cast(itemMeta);
        metaConsumer.accept(meta);

        stack.setItemMeta(meta);
        return stack;
    }

    public static ItemStack pdc(@Nonnull ItemStack stack, @Nonnull Consumer<PersistentDataContainer> container) {
        return meta(stack, itemMeta -> {
            container.accept(itemMeta.getPersistentDataContainer());
        });
    }

    public static ItemEditor edit(@Nonnull ItemStack stack) {
        return new ItemEditor(stack);
    }

    public static ItemEditor edit(@Nonnull Material material) {
        return new ItemEditor(material);
    }

    public static ItemEditor edit(@Nonnull ConfigurationSection section) {
        return new ItemEditor(parse(section));
    }

    @Nonnull
    public static String toJson(@Nonnull ItemStack itemStack) {
        Objects.requireNonNull(itemStack, "ItemStack cannot be null");
        return GsonProvider.standard().toJson(itemStack);
    }

    @Nullable
    public static ItemStack fromJson(@Nonnull String json) {
        Objects.requireNonNull(json, "JSON string cannot be null");
        return GsonProvider.standard().fromJson(json, ItemStack.class);
    }

}
