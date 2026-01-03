package io.github.johnnypixelz.utilizer.command.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a command or subcommand as private.
 *
 * <p>Private commands are hidden from tab completion but still executable
 * if the user knows the command. This is useful for:</p>
 * <ul>
 *   <li>Admin/debug commands that shouldn't be discoverable</li>
 *   <li>Commands triggered by clicking chat components</li>
 *   <li>Internal commands used by the plugin</li>
 * </ul>
 *
 * <h2>Example:</h2>
 * <pre>{@code
 * @Label("admin")
 * public class AdminCommand extends CommandBase {
 *
 *     @Subcommand("debug")
 *     @Private
 *     public void debug(Player player) {
 *         // Hidden from tab completion
 *     }
 *
 *     @Subcommand("reload")
 *     public void reload(CommandSender sender) {
 *         // Visible in tab completion
 *     }
 * }
 * }</pre>
 *
 * <p>When applied to a class, all subcommands of that command are also hidden.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Private {

}
