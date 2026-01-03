package io.github.johnnypixelz.utilizer.command.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as the handler for unknown subcommands.
 *
 * <p>When a user enters a subcommand that doesn't match any defined subcommand,
 * the method annotated with @UnknownCommand will be called instead of showing
 * an error message.</p>
 *
 * <h2>Method Signature:</h2>
 * <p>The method should accept a CommandSender and a String array of arguments:</p>
 * <pre>{@code
 * @UnknownCommand
 * public void onUnknown(CommandSender sender, String[] args) {
 *     sender.sendMessage("Unknown subcommand: " + args[0]);
 *     sender.sendMessage("Use /mycommand help for available commands.");
 * }
 * }</pre>
 *
 * <h2>Example:</h2>
 * <pre>{@code
 * @Label("shop")
 * public class ShopCommand extends CommandBase {
 *
 *     @Subcommand("buy")
 *     public void buy(Player player, String item) { ... }
 *
 *     @Subcommand("sell")
 *     public void sell(Player player, String item) { ... }
 *
 *     @UnknownCommand
 *     public void onUnknown(CommandSender sender, String[] args) {
 *         sender.sendMessage("Unknown action: " + args[0]);
 *         sender.sendMessage("Available: buy, sell");
 *     }
 * }
 * }</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface UnknownCommand {

}
