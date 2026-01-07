package io.github.johnnypixelz.utilizer.papi;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Builder for creating PlaceholderAPI expansions with template pattern support.
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Fluent builder API</li>
 *   <li>Template patterns with named captures (e.g., {@code "{skill}"}, {@code "{position}"})</li>
 *   <li>Type-safe argument access via {@link PlaceholderArgs}</li>
 *   <li>Support for both regular and relational placeholders</li>
 * </ul>
 *
 * <h2>Example Usage</h2>
 * <pre>
 * PlaceholderExpansion.create()
 *     // Simple: %plugin_coins%
 *     .placeholder("coins", player -> String.valueOf(getCoins(player)))
 *
 *     // With args: %plugin_stat_mining%
 *     .placeholder("stat_{type}", (player, args) -> {
 *         return getStat(player, args.getString("type"));
 *     })
 *
 *     // Complex: %plugin_leaderboard_mining_top_1%
 *     .placeholder("leaderboard_{skill}_top_{position}", (player, args) -> {
 *         String skill = args.getString("skill");
 *         int position = args.getInt("position");
 *         return getLeaderboardEntry(skill, position);
 *     })
 *
 *     // Relational: %rel_plugin_canattack%
 *     .relational("canattack", (p1, p2) -> canAttack(p1, p2) ? "yes" : "no")
 *
 *     .register();
 * </pre>
 *
 * <h2>Template Patterns</h2>
 * <p>Use {@code {name}} syntax to capture dynamic parts:
 * <ul>
 *   <li>{@code "stat_{type}"} - captures "type" from "stat_mining"</li>
 *   <li>{@code "top_{category}_{position}"} - captures "category" and "position"</li>
 * </ul>
 *
 * <p>Access captured values through {@link PlaceholderArgs}:
 * <ul>
 *   <li>{@code args.getString("type")} - get as string</li>
 *   <li>{@code args.getInt("position")} - get as int</li>
 *   <li>{@code args.getDouble("amount")} - get as double</li>
 * </ul>
 */
public class PlaceholderExpansion {

    private final List<InternalExpansion.RegisteredPlaceholder> placeholders = new ArrayList<>();
    private final List<InternalExpansion.RegisteredRelational> relationals = new ArrayList<>();
    private boolean registered = false;

    private PlaceholderExpansion() {
    }

    /**
     * Create a new placeholder expansion builder.
     *
     * @return new builder instance
     */
    @NotNull
    public static PlaceholderExpansion create() {
        return new PlaceholderExpansion();
    }

    /**
     * Register a simple placeholder without template arguments.
     *
     * <p>Example: {@code .placeholder("coins", player -> String.valueOf(getCoins(player)))}
     *
     * @param pattern the placeholder pattern (e.g., "coins" for %plugin_coins%)
     * @param handler function that receives the player and returns the replacement string
     * @return this builder for chaining
     */
    @NotNull
    public PlaceholderExpansion placeholder(@NotNull String pattern, @NotNull Function<Player, String> handler) {
        return placeholder(pattern, (player, args) -> handler.apply(player));
    }

    /**
     * Register a placeholder with template arguments.
     *
     * <p>Example: {@code .placeholder("stat_{type}", (player, args) -> getStat(player, args.getString("type")))}
     *
     * @param pattern the placeholder pattern with optional template variables (e.g., "stat_{type}")
     * @param handler function that receives the player and captured arguments
     * @return this builder for chaining
     */
    @NotNull
    public PlaceholderExpansion placeholder(@NotNull String pattern, @NotNull BiFunction<Player, PlaceholderArgs, String> handler) {
        if (registered) {
            throw new IllegalStateException("Cannot add placeholders after register() has been called");
        }
        placeholders.add(new InternalExpansion.RegisteredPlaceholder(
                new PlaceholderPattern(pattern),
                handler
        ));
        return this;
    }

    /**
     * Register a simple relational placeholder without template arguments.
     *
     * <p>Relational placeholders compare two players and are accessed via %rel_plugin_name%.
     *
     * <p>Example: {@code .relational("canattack", (p1, p2) -> canAttack(p1, p2) ? "yes" : "no")}
     *
     * @param pattern the placeholder pattern (e.g., "canattack" for %rel_plugin_canattack%)
     * @param handler function that receives both players and returns the replacement string
     * @return this builder for chaining
     */
    @NotNull
    public PlaceholderExpansion relational(@NotNull String pattern, @NotNull BiFunction<Player, Player, String> handler) {
        return relational(pattern, (p1, p2, args) -> handler.apply(p1, p2));
    }

    /**
     * Register a relational placeholder with template arguments.
     *
     * <p>Example: {@code .relational("relationship_{type}", (p1, p2, args) -> getRelationship(p1, p2, args.getString("type")))}
     *
     * @param pattern the placeholder pattern with optional template variables
     * @param handler function that receives both players and captured arguments
     * @return this builder for chaining
     */
    @NotNull
    public PlaceholderExpansion relational(@NotNull String pattern, @NotNull TriFunction<Player, Player, PlaceholderArgs, String> handler) {
        if (registered) {
            throw new IllegalStateException("Cannot add placeholders after register() has been called");
        }
        relationals.add(new InternalExpansion.RegisteredRelational(
                new PlaceholderPattern(pattern),
                handler
        ));
        return this;
    }

    /**
     * Get all registered placeholder patterns.
     *
     * @return unmodifiable list of placeholder patterns
     */
    @NotNull
    public List<String> getPlaceholders() {
        return placeholders.stream()
                .map(p -> p.pattern().getTemplate())
                .toList();
    }

    /**
     * Get all registered relational placeholder patterns.
     *
     * @return unmodifiable list of relational placeholder patterns
     */
    @NotNull
    public List<String> getRelationals() {
        return relationals.stream()
                .map(r -> r.pattern().getTemplate())
                .toList();
    }

    /**
     * Register this expansion with PlaceholderAPI.
     *
     * <p>If PlaceholderAPI is not installed, this method does nothing.
     *
     * @throws IllegalStateException if register() has already been called
     */
    public void register() {
        if (registered) {
            throw new IllegalStateException("This expansion has already been registered");
        }

        // Check if PAPI is available
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return;
        }

        registered = true;

        // Create and register the expansion
        InternalExpansion expansion = new InternalExpansion(
                Collections.unmodifiableList(placeholders),
                Collections.unmodifiableList(relationals)
        );
        expansion.register();
    }

    /**
     * Check if this expansion has been registered.
     *
     * @return true if register() has been called
     */
    public boolean isRegistered() {
        return registered;
    }

}
