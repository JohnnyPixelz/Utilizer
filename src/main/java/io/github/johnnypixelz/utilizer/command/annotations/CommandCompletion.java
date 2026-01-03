package io.github.johnnypixelz.utilizer.command.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines tab completion for command arguments.
 *
 * <p>The value is a space-separated list of completer IDs, one per argument.
 * Completer IDs start with {@code @} and can include configuration after a colon.</p>
 *
 * <h2>Built-in Completers:</h2>
 * <ul>
 *   <li>{@code @players} - Online player names</li>
 *   <li>{@code @worlds} - World names</li>
 *   <li>{@code @materials} - Material names</li>
 *   <li>{@code @sounds} - Sound names</li>
 *   <li>{@code @entities} - Entity type names</li>
 *   <li>{@code @enchantments} - Enchantment names</li>
 *   <li>{@code @potions} - Potion effect names</li>
 *   <li>{@code @gamemodes} - Game mode names</li>
 *   <li>{@code @boolean} - true/false/yes/no</li>
 *   <li>{@code @nothing} - No completions</li>
 *   <li>{@code @range:min-max} - Numbers in range (e.g., @range:1-64)</li>
 * </ul>
 *
 * <h2>Example:</h2>
 * <pre>{@code
 * @Subcommand("give")
 * @CommandCompletion("@players @materials @range:1-64")
 * public void give(Player target, Material item, int amount) { }
 *
 * @Subcommand("teleport")
 * @CommandCompletion("@players @worlds")
 * public void teleport(Player target, World world) { }
 * }</pre>
 *
 * <p>Custom completers can be registered via
 * {@code Commands.registerCompleter("myid", ctx -> ...)}.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CommandCompletion {

    /**
     * Space-separated list of completer IDs.
     *
     * <p>Each ID should start with @ and corresponds to one argument position.
     * Use @nothing for arguments that shouldn't have completions.</p>
     *
     * @return the completer specification
     */
    String value();

}
