package io.github.johnnypixelz.utilizer.command.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Forces a String parameter to consume only a single word.
 *
 * <p>By default, the last String parameter in a command method is "greedy"
 * and consumes all remaining arguments joined by spaces. This annotation
 * overrides that behavior.</p>
 *
 * <h2>Example:</h2>
 * <pre>{@code
 * // Without @Single - lastName gets all remaining words
 * @Default
 * public void name(Player player, String firstName, String lastName) {
 *     // /name John Doe Smith -> firstName="John", lastName="Doe Smith"
 * }
 *
 * // With @Single - lastName gets only one word
 * @Default
 * public void name(Player player, @Single String firstName, String lastName) {
 *     // /name John Doe Smith -> firstName="John", lastName="Doe Smith"
 *     // (same because lastName is still last and greedy)
 * }
 *
 * // Multiple singles with greedy last
 * @Default
 * public void msg(Player player, @Single String target, String message) {
 *     // /msg John Hello World -> target="John", message="Hello World"
 * }
 * }</pre>
 *
 * <p>Note: This annotation only affects String parameters. Other types
 * always consume exactly one argument.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Single {

}
