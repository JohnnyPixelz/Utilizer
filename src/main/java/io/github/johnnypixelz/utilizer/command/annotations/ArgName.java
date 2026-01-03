package io.github.johnnypixelz.utilizer.command.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Overrides the display name of a command parameter in syntax messages.
 *
 * <p>By default, parameter names are inferred from the actual Java parameter names
 * (requires -parameters compiler flag) or fall back to the type name.</p>
 *
 * <h2>Example:</h2>
 * <pre>{@code
 * @Default
 * public void give(
 *     Player sender,
 *     @ArgName("target") Player other,
 *     @ArgName("amount") int count
 * ) { }
 * // Syntax: /give <target> <amount>
 * }</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ArgName {

    /**
     * The display name for this parameter.
     *
     * @return the parameter name to show in syntax
     */
    String value();

}
