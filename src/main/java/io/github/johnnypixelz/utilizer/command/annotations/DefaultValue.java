package io.github.johnnypixelz.utilizer.command.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides a default value for a command parameter when no argument is provided.
 *
 * <p>The default value is specified as a string and will be resolved using
 * the same argument resolver as if the user had typed it.</p>
 *
 * <h2>Example:</h2>
 * <pre>{@code
 * @Subcommand("spawn")
 * public void spawn(
 *     Player player,
 *     @DefaultValue("10") int count,
 *     @DefaultValue("zombie") EntityType type
 * ) {
 *     // If user types /spawn: count=10, type=ZOMBIE
 *     // If user types /spawn 5: count=5, type=ZOMBIE
 *     // If user types /spawn 5 skeleton: count=5, type=SKELETON
 * }
 * // Syntax: /spawn [count] [type]
 * }</pre>
 *
 * <p>Note: Parameters with {@code @DefaultValue} are implicitly optional and will
 * show with square brackets in syntax messages.</p>
 *
 * @see Optional
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface DefaultValue {

    /**
     * The default value as a string.
     *
     * <p>This will be parsed using the appropriate argument resolver
     * for the parameter type.</p>
     *
     * @return the default value string
     */
    String value();

}
