package io.github.johnnypixelz.utilizer.command.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Applies a cooldown to a command, preventing rapid repeated execution.
 *
 * <p>Cooldowns are tracked per-player by default. When a player attempts to
 * use a command while on cooldown, they receive a message and the command
 * is not executed.</p>
 *
 * <h2>Basic Example:</h2>
 * <pre>{@code
 * @Subcommand("heal")
 * @Cooldown(30)  // 30 second cooldown
 * public void heal(Player player) {
 *     player.setHealth(player.getMaxHealth());
 *     player.sendMessage("You have been healed!");
 * }
 * }</pre>
 *
 * <h2>Custom Message:</h2>
 * <pre>{@code
 * @Subcommand("teleport")
 * @Cooldown(value = 60, message = "&cYou must wait %time% seconds before teleporting again!")
 * public void teleport(Player player) { ... }
 * }</pre>
 *
 * <h2>Config Message:</h2>
 * <pre>{@code
 * @Subcommand("fly")
 * @Cooldown(value = 120, messageConfig = "config", messagePath = "messages.cooldown")
 * public void fly(Player player) { ... }
 * }</pre>
 *
 * <h2>Global Cooldown:</h2>
 * <pre>{@code
 * @Subcommand("broadcast")
 * @Cooldown(value = 300, global = true)  // Shared across all players
 * public void broadcast(CommandSender sender, String message) { ... }
 * }</pre>
 *
 * <h2>Bypass Permission:</h2>
 * <pre>{@code
 * @Subcommand("reward")
 * @Cooldown(value = 3600, bypass = "myplugin.cooldown.bypass")
 * public void reward(Player player) { ... }
 * }</pre>
 *
 * <p>The {@code %time%} placeholder in messages is replaced with the remaining cooldown time.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cooldown {

    /**
     * The cooldown duration in seconds.
     *
     * @return cooldown duration in seconds
     */
    long value();

    /**
     * Custom message to show when the command is on cooldown.
     * Supports color codes and the {@code %time%} placeholder.
     *
     * @return the cooldown message, or empty to use default
     */
    String message() default "";

    /**
     * Name of the config file to load the cooldown message from.
     * Used with {@link #messagePath()}.
     *
     * @return config name, or empty to not use config
     */
    String messageConfig() default "";

    /**
     * Path in the config file for the cooldown message.
     * Used with {@link #messageConfig()}.
     *
     * @return config path, or empty to not use config
     */
    String messagePath() default "";

    /**
     * If true, the cooldown is shared across all players (global).
     * If false (default), each player has their own cooldown.
     *
     * @return true for global cooldown
     */
    boolean global() default false;

    /**
     * Permission node that bypasses this cooldown.
     * Players with this permission can use the command without cooldown.
     *
     * @return bypass permission, or empty for no bypass
     */
    String bypass() default "";

}
