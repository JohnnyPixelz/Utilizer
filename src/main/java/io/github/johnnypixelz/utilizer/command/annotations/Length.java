package io.github.johnnypixelz.utilizer.command.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates that a String parameter has a length within the specified bounds.
 *
 * <h2>Example:</h2>
 * <pre>{@code
 * @Subcommand("rename")
 * public void rename(
 *     Player player,
 *     @Length(min = 3, max = 16) String newName
 * ) {
 *     player.setDisplayName(newName);
 * }
 *
 * @Subcommand("setmotd")
 * public void setMotd(
 *     CommandSender sender,
 *     @Length(max = 100, message = "&cMOTD cannot exceed 100 characters!") String motd
 * ) {
 *     // Set MOTD
 * }
 *
 * @Subcommand("password")
 * public void setPassword(
 *     Player player,
 *     @Length(min = 8, message = "&cPassword must be at least 8 characters!") String password
 * ) {
 *     // Set password
 * }
 * }</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Length {

    /**
     * The minimum allowed length (inclusive).
     *
     * @return minimum length
     */
    int min() default 0;

    /**
     * The maximum allowed length (inclusive).
     *
     * @return maximum length
     */
    int max() default Integer.MAX_VALUE;

    /**
     * Custom error message when validation fails.
     * Supports color codes and placeholders: %value%, %length%, %min%, %max%
     *
     * @return the error message, or empty to use default
     */
    String message() default "";

}
