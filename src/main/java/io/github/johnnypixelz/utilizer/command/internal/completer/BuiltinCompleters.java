package io.github.johnnypixelz.utilizer.command.internal.completer;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Built-in tab completers for common Bukkit types.
 */
public final class BuiltinCompleters {

    private BuiltinCompleters() {
    }

    /**
     * Registers all built-in completers.
     *
     * @param registry the registry to register to
     */
    public static void registerAll(TabCompleterRegistry registry) {
        // Players
        registry.register("players", ctx ->
                Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(ctx::matches)
                        .toList()
        );

        // Worlds
        registry.register("worlds", ctx ->
                Bukkit.getWorlds().stream()
                        .map(World::getName)
                        .filter(ctx::matches)
                        .toList()
        );

        // Materials
        registry.register("materials", ctx ->
                Arrays.stream(Material.values())
                        .filter(m -> !m.isLegacy())
                        .map(m -> m.name().toLowerCase())
                        .filter(ctx::matches)
                        .limit(50)  // Limit to prevent lag
                        .toList()
        );

        // Sounds
        registry.register("sounds", ctx ->
                Arrays.stream(Sound.values())
                        .map(s -> s.name().toLowerCase())
                        .filter(ctx::matches)
                        .limit(50)
                        .toList()
        );

        // Entity types
        registry.register("entities", ctx ->
                Arrays.stream(EntityType.values())
                        .filter(e -> e != EntityType.UNKNOWN)
                        .map(e -> e.name().toLowerCase())
                        .filter(ctx::matches)
                        .toList()
        );

        // Enchantments
        registry.register("enchantments", ctx -> {
            List<String> result = new ArrayList<>();
            for (Enchantment enchant : Enchantment.values()) {
                String name = enchant.getKey().getKey();
                if (ctx.matches(name)) {
                    result.add(name);
                }
            }
            return result;
        });

        // Potion effects
        registry.register("potions", ctx -> {
            List<String> result = new ArrayList<>();
            for (PotionEffectType type : PotionEffectType.values()) {
                if (type != null) {
                    String name = type.getName().toLowerCase();
                    if (ctx.matches(name)) {
                        result.add(name);
                    }
                }
            }
            return result;
        });

        // Game modes
        registry.register("gamemodes", ctx ->
                Arrays.stream(GameMode.values())
                        .map(g -> g.name().toLowerCase())
                        .filter(ctx::matches)
                        .toList()
        );

        // Boolean
        registry.register("boolean", ctx -> {
            List<String> options = Arrays.asList("true", "false", "yes", "no");
            return options.stream().filter(ctx::matches).toList();
        });

        // Nothing - returns empty list
        registry.register("nothing", ctx -> Collections.emptyList());

        // Range - generates numbers in a range (e.g., @range:1-10)
        registry.register("range", ctx -> {
            String config = ctx.getConfig();
            if (config == null || !config.contains("-")) {
                return Collections.emptyList();
            }
            try {
                String[] parts = config.split("-");
                int min = Integer.parseInt(parts[0].trim());
                int max = Integer.parseInt(parts[1].trim());

                // Limit range to prevent excessive completions
                if (max - min > 100) {
                    max = min + 100;
                }

                int finalMax = max;
                return IntStream.rangeClosed(min, finalMax)
                        .mapToObj(String::valueOf)
                        .filter(ctx::matches)
                        .limit(20)
                        .toList();
            } catch (NumberFormatException e) {
                return Collections.emptyList();
            }
        });
    }

}
