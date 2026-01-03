package io.github.johnnypixelz.utilizer.command.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Restricts a parameter to a specific set of allowed values.
 *
 * <p>If the user provides a value not in the list, an error message is shown.
 * Values are checked case-insensitively.</p>
 *
 * <h2>Example:</h2>
 * <pre>{@code
 * @Subcommand("difficulty")
 * public void setDifficulty(
 *     CommandSender sender,
 *     @Values("easy|normal|hard|peaceful") String difficulty
 * ) {
 *     // Only accepts: easy, normal, hard, peaceful
 *     // Error if user types something else
 * }
 *
 * @Subcommand("mode")
 * public void setMode(
 *     Player player,
 *     @Values("survival|creative|adventure|spectator") String mode
 * ) { }
 * }</pre>
 *
 * <p>The allowed values are automatically used for tab completion.</p>
 *
 * <p>Note: For enum types, you don't need this annotation - enum values
 * are automatically validated and completed.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Values {

    /**
     * Pipe-separated list of allowed values.
     *
     * <p>Example: "easy|normal|hard"</p>
     *
     * @return the allowed values
     */
    String value();

}
