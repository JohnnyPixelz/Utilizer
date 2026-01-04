package io.github.johnnypixelz.utilizer.config.evaluate;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.config.FunctionDictionaryIfc;
import io.github.johnnypixelz.utilizer.config.evaluate.dataaccessors.CustomDataAccessor;
import io.github.johnnypixelz.utilizer.config.evaluate.functions.HasPermissionFunction;
import io.github.johnnypixelz.utilizer.depend.Placeholders;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Utility class for evaluating mathematical and boolean expressions.
 * 
 * <p>This class provides methods to evaluate string expressions and convert them to
 * boolean or numeric values. It supports standard mathematical operations, boolean logic,
 * and can integrate with PlaceholderAPI when a Player context is provided.
 * 
 * <p>Expression examples:
 * <ul>
 *   <li>Boolean: "5 > 3", "true && false", "'hello' == 'hello'"</li>
 *   <li>Numeric: "2 + 2", "10 * 5", "(100 - 20) / 4"</li>
 *   <li>With placeholders: "%player_health% > 10", "%vault_eco_balance% >= 1000"</li>
 * </ul>
 */
public class Evaluate {

    private static final ExpressionConfiguration EXPRESSION_CONFIGURATION = ExpressionConfiguration.builder()
            .singleQuoteStringLiteralsAllowed(true)
            .dataAccessorSupplier(CustomDataAccessor::new)
            .build();

    /**
     * Evaluates a boolean expression without player context.
     * 
     * <p>This method parses and evaluates expressions that result in a boolean value.
     * It supports standard comparison operators (&gt;, &lt;, ==, !=, &gt;=, &lt;=) and
     * boolean operators (&&, ||, !).
     * 
     * <p><b>Examples:</b>
     * <pre>
     * boolean result1 = Evaluate.toBoolean("5 > 3");              // true
     * boolean result2 = Evaluate.toBoolean("true && false");      // false
     * boolean result3 = Evaluate.toBoolean("'apple' == 'apple'"); // true
     * boolean result4 = Evaluate.toBoolean("10 >= 5 && 3 < 7");   // true
     * </pre>
     * 
     * @param expression the boolean expression to evaluate
     * @return the boolean result of the evaluation
     * @throws IllegalArgumentException if the expression cannot be evaluated
     */
    public static boolean toBoolean(String expression) {
        final Expression exp = new Expression(expression, EXPRESSION_CONFIGURATION);

        try {
            return exp.evaluate().getBooleanValue();
        } catch (Exception exception) {
            throw new IllegalArgumentException(exception);
        }
    }

    /**
     * Evaluates a boolean expression with player context.
     * 
     * <p>This method extends {@link #toBoolean(String)} by supporting:
     * <ul>
     *   <li>PlaceholderAPI placeholders (e.g., %player_health%, %vault_eco_balance%)</li>
     *   <li>Permission checks via hasPermission() function</li>
     *   <li>All standard boolean operations</li>
     * </ul>
     * 
     * <p>The hasPermission() function can be called with the following aliases:
     * hasperm, perm, has_perm, permission, has_permission, haspermission
     * 
     * <p><b>Examples:</b>
     * <pre>
     * Player player = ...; // some player instance
     * 
     * // Using placeholders
     * boolean hasEnoughHealth = Evaluate.toBoolean("%player_health% > 10", player);
     * boolean isRich = Evaluate.toBoolean("%vault_eco_balance% >= 1000", player);
     * 
     * // Using permission checks
     * boolean canFly = Evaluate.toBoolean("hasPermission('essentials.fly')", player);
     * boolean isVIP = Evaluate.toBoolean("perm('vip.rank')", player);
     * 
     * // Combining placeholders and permissions
     * boolean canBuy = Evaluate.toBoolean("%vault_eco_balance% >= 500 && hasperm('shop.buy')", player);
     * </pre>
     * 
     * @param expression the boolean expression to evaluate
     * @param player the player context for placeholders and permissions
     * @return the boolean result of the evaluation
     * @throws IllegalArgumentException if the expression cannot be evaluated
     */
    public static boolean toBoolean(String expression, Player player) {
        // Get a default function dictionary and add the custom function
        final FunctionDictionaryIfc functionDictionary = ExpressionConfiguration.defaultConfiguration()
                .getFunctionDictionary();

        final HasPermissionFunction hasPermissionFunction = new HasPermissionFunction(player);
        String[] aliases = {"hasperm", "perm", "has_perm", "permission", "has_permission", "haspermission"};

        for (String alias : aliases) {
            functionDictionary.addFunction(alias, hasPermissionFunction);
        }

        final ExpressionConfiguration expressionConfiguration = EXPRESSION_CONFIGURATION.toBuilder()
                .functionDictionary(functionDictionary)
                .build();

        // Parsing typical placeholders & PlaceholderAPI
        final String placeholderedExpression = Placeholders.set(player, expression);

        // Create and evaluate the expression
        Expression exp = new Expression(placeholderedExpression, expressionConfiguration);
        try {
            return exp.evaluate().getBooleanValue();
        } catch (Exception e) {
            throw new IllegalArgumentException("Error evaluating expression: " + placeholderedExpression, e);
        }
    }

    /**
     * Safely evaluates a boolean expression without player context.
     * 
     * <p>This method is a safe variant of {@link #toBoolean(String)} that returns
     * an Optional instead of throwing an exception on evaluation errors.
     * 
     * <p><b>Examples:</b>
     * <pre>
     * Optional&lt;Boolean&gt; result1 = Evaluate.toSafeBoolean("5 > 3");
     * if (result1.isPresent()) {
     *     boolean value = result1.get(); // true
     * }
     * 
     * Optional&lt;Boolean&gt; result2 = Evaluate.toSafeBoolean("invalid expression");
     * // result2.isEmpty() == true
     * 
     * // Using with default value
     * boolean value = Evaluate.toSafeBoolean("10 > 5").orElse(false);
     * </pre>
     * 
     * @param expression the boolean expression to evaluate
     * @return an Optional containing the boolean result, or empty if evaluation fails
     */
    public static Optional<Boolean> toSafeBoolean(String expression) {
        try {
            return Optional.of(toBoolean(expression));
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    /**
     * Safely evaluates a boolean expression with player context.
     * 
     * <p>This method is a safe variant of {@link #toBoolean(String, Player)} that returns
     * an Optional instead of throwing an exception on evaluation errors.
     * 
     * <p><b>Examples:</b>
     * <pre>
     * Player player = ...; // some player instance
     * 
     * Optional&lt;Boolean&gt; result = Evaluate.toSafeBoolean("%player_health% > 10", player);
     * if (result.isPresent()) {
     *     boolean hasHealth = result.get();
     * }
     * 
     * // Using with permission check and default value
     * boolean canUse = Evaluate.toSafeBoolean("hasperm('special.command')", player).orElse(false);
     * 
     * // Handling invalid placeholders gracefully
     * boolean isValid = Evaluate.toSafeBoolean("%invalid_placeholder% > 0", player).orElse(false);
     * </pre>
     * 
     * @param expression the boolean expression to evaluate
     * @param player the player context for placeholders and permissions
     * @return an Optional containing the boolean result, or empty if evaluation fails
     */
    public static Optional<Boolean> toSafeBoolean(String expression, Player player) {
        try {
            return Optional.of(toBoolean(expression, player));
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    /**
     * Evaluates a numeric expression without player context.
     * 
     * <p>This method parses and evaluates mathematical expressions, returning the result
     * as a BigDecimal for precision. Supports standard arithmetic operators (+, -, *, /, %)
     * and parentheses for grouping.
     * 
     * <p><b>Examples:</b>
     * <pre>
     * BigDecimal result1 = Evaluate.toNumeric("2 + 2");           // 4
     * BigDecimal result2 = Evaluate.toNumeric("10 * 5");          // 50
     * BigDecimal result3 = Evaluate.toNumeric("(100 - 20) / 4");  // 20
     * BigDecimal result4 = Evaluate.toNumeric("15 % 4");          // 3
     * BigDecimal result5 = Evaluate.toNumeric("2.5 * 4");         // 10.0
     * 
     * // Converting to int or double
     * int intValue = Evaluate.toNumeric("10 + 5").intValue();     // 15
     * double doubleValue = Evaluate.toNumeric("7 / 2").doubleValue(); // 3.5
     * </pre>
     * 
     * @param expression the numeric expression to evaluate
     * @return the BigDecimal result of the evaluation
     * @throws IllegalArgumentException if the expression cannot be evaluated
     */
    public static BigDecimal toNumeric(String expression) {
        final Expression exp = new Expression(expression);

        try {
            return exp.evaluate().getNumberValue();
        } catch (Exception exception) {
            throw new IllegalArgumentException(exception);
        }
    }

    /**
     * Evaluates a numeric expression with player context.
     * 
     * <p>This method extends {@link #toNumeric(String)} by supporting PlaceholderAPI
     * placeholders. The placeholders are replaced before the expression is evaluated.
     * 
     * <p><b>Examples:</b>
     * <pre>
     * Player player = ...; // some player instance
     * 
     * // Using player placeholders
     * BigDecimal health = Evaluate.toNumeric("%player_health%", player);
     * BigDecimal balance = Evaluate.toNumeric("%vault_eco_balance%", player);
     * 
     * // Mathematical operations with placeholders
     * BigDecimal result1 = Evaluate.toNumeric("%player_health% + 10", player);
     * BigDecimal result2 = Evaluate.toNumeric("%vault_eco_balance% * 0.1", player); // 10% of balance
     * BigDecimal result3 = Evaluate.toNumeric("(%player_level% * 100) + 50", player);
     * 
     * // Converting to primitive types
     * int playerLevel = Evaluate.toNumeric("%player_level%", player).intValue();
     * double healthPercent = Evaluate.toNumeric("%player_health% / %player_max_health% * 100", player).doubleValue();
     * </pre>
     * 
     * @param expression the numeric expression to evaluate
     * @param player the player context for placeholders
     * @return the BigDecimal result of the evaluation
     * @throws IllegalArgumentException if the expression cannot be evaluated
     */
    public static BigDecimal toNumeric(String expression, Player player) {
        // Parsing typical placeholders & PlaceholderAPI
        final String placeholderedExpression = Placeholders.set(player, expression);

        // Create and evaluate the expression
        Expression exp = new Expression(placeholderedExpression);


        try {
            return exp.evaluate().getNumberValue();
        } catch (Exception exception) {
            throw new IllegalArgumentException(exception);
        }
    }

    /**
     * Safely evaluates a numeric expression without player context.
     * 
     * <p>This method is a safe variant of {@link #toNumeric(String)} that returns
     * an Optional instead of throwing an exception on evaluation errors.
     * 
     * <p><b>Examples:</b>
     * <pre>
     * Optional&lt;BigDecimal&gt; result1 = Evaluate.toSafeNumeric("10 + 5");
     * if (result1.isPresent()) {
     *     BigDecimal value = result1.get(); // 15
     * }
     * 
     * Optional&lt;BigDecimal&gt; result2 = Evaluate.toSafeNumeric("invalid expression");
     * // result2.isEmpty() == true
     * 
     * // Using with default value
     * BigDecimal value = Evaluate.toSafeNumeric("20 * 3").orElse(BigDecimal.ZERO);
     * int intValue = Evaluate.toSafeNumeric("100 / 4").orElse(BigDecimal.ZERO).intValue();
     * </pre>
     * 
     * @param expression the numeric expression to evaluate
     * @return an Optional containing the BigDecimal result, or empty if evaluation fails
     */
    public static Optional<BigDecimal> toSafeNumeric(String expression) {
        try {
            return Optional.of(toNumeric(expression));
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    /**
     * Safely evaluates a numeric expression with player context.
     * 
     * <p>This method is a safe variant of {@link #toNumeric(String, Player)} that returns
     * an Optional instead of throwing an exception on evaluation errors.
     * 
     * <p><b>Examples:</b>
     * <pre>
     * Player player = ...; // some player instance
     * 
     * Optional&lt;BigDecimal&gt; result = Evaluate.toSafeNumeric("%player_health%", player);
     * if (result.isPresent()) {
     *     double health = result.get().doubleValue();
     * }
     * 
     * // Using with default value for missing/invalid placeholders
     * BigDecimal balance = Evaluate.toSafeNumeric("%vault_eco_balance%", player)
     *     .orElse(BigDecimal.ZERO);
     * 
     * int level = Evaluate.toSafeNumeric("%player_level% + 1", player)
     *     .orElse(BigDecimal.ONE)
     *     .intValue();
     * 
     * // Gracefully handling calculation errors
     * double percentage = Evaluate.toSafeNumeric("%custom_stat% / %custom_max% * 100", player)
     *     .orElse(BigDecimal.ZERO)
     *     .doubleValue();
     * </pre>
     * 
     * @param expression the numeric expression to evaluate
     * @param player the player context for placeholders
     * @return an Optional containing the BigDecimal result, or empty if evaluation fails
     */
    public static Optional<BigDecimal> toSafeNumeric(String expression, Player player) {
        try {
            return Optional.of(toNumeric(expression, player));
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

}
