package io.github.johnnypixelz.utilizer.command.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates that a numeric parameter is within a specified range.
 *
 * <p>Works with all numeric types: int, long, float, double, and their wrapper classes.</p>
 *
 * <h2>Example:</h2>
 * <pre>{@code
 * @Subcommand("setlevel")
 * public void setLevel(
 *     Player player,
 *     @Range(min = 0, max = 100) int level
 * ) {
 *     player.setLevel(level);
 * }
 *
 * @Subcommand("setspeed")
 * public void setSpeed(
 *     Player player,
 *     @Range(min = 0.0, max = 1.0) double speed
 * ) {
 *     player.setWalkSpeed((float) speed);
 * }
 *
 * @Subcommand("give")
 * public void give(
 *     Player player,
 *     Material item,
 *     @Range(min = 1, max = 64, message = "&cAmount must be between 1 and 64!") int amount
 * ) {
 *     player.getInventory().addItem(new ItemStack(item, amount));
 * }
 * }</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Range {

    /**
     * The minimum allowed value (inclusive).
     *
     * @return minimum value
     */
    double min() default Double.NEGATIVE_INFINITY;

    /**
     * The maximum allowed value (inclusive).
     *
     * @return maximum value
     */
    double max() default Double.POSITIVE_INFINITY;

    /**
     * Custom error message when validation fails.
     * Supports color codes and placeholders: %value%, %min%, %max%
     *
     * @return the error message, or empty to use default
     */
    String message() default "";

}
