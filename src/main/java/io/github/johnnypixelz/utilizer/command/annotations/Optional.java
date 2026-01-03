package io.github.johnnypixelz.utilizer.command.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a command parameter as optional.
 *
 * <p>Optional parameters will receive {@code null} if no argument is provided.
 * They must come after all required parameters in the method signature.</p>
 *
 * <h2>Example:</h2>
 * <pre>{@code
 * @Default
 * public void teleport(
 *     Player player,
 *     @Optional Player target,  // null if not provided
 *     @Optional World world     // null if not provided
 * ) {
 *     if (target == null) {
 *         target = player;
 *     }
 * }
 * // Syntax: /teleport [target] [world]
 * }</pre>
 *
 * <p>Note: Primitive types cannot be marked as optional since they cannot be null.
 * Use their boxed equivalents (Integer, Double, etc.) or use {@link Default} instead.</p>
 *
 * @see Default
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Optional {

}
