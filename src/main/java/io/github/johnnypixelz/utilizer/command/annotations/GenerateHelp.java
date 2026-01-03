package io.github.johnnypixelz.utilizer.command.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables automatic help generation for a command.
 *
 * <p>When this annotation is present on a command class:</p>
 * <ul>
 *   <li>Executing the command without arguments (and no @Default) shows available subcommands</li>
 *   <li>A "help" subcommand is automatically registered</li>
 * </ul>
 *
 * <h2>Example:</h2>
 * <pre>{@code
 * @Label("shop")
 * @GenerateHelp
 * public class ShopCommand extends CommandBase {
 *
 *     @Subcommand("buy")
 *     @Description("Buy an item from the shop")
 *     public void buy(Player player, String item) { ... }
 *
 *     @Subcommand("sell")
 *     @Description("Sell an item to the shop")
 *     public void sell(Player player, String item) { ... }
 * }
 * }</pre>
 *
 * <p>Running {@code /shop} or {@code /shop help} would display:</p>
 * <pre>
 * /shop - Available commands:
 *   /shop buy - Buy an item from the shop
 *   /shop sell - Sell an item to the shop
 *   /shop help - Shows this help message
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GenerateHelp {

    /**
     * Custom header message for the help output.
     * Use %command% as placeholder for the command name.
     *
     * @return the header message
     */
    String header() default "&6/%command% &7- Available commands:";

    /**
     * Format for each subcommand entry.
     * Placeholders: %command%, %subcommand%, %syntax%, %description%
     *
     * @return the entry format
     */
    String format() default "  &e/%command% %subcommand% &7- %description%";

    /**
     * Message shown when there are no available subcommands.
     *
     * @return the no commands message
     */
    String noCommands() default "&7No commands available.";

}
