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

public class Evaluate {

    private static final ExpressionConfiguration EXPRESSION_CONFIGURATION = ExpressionConfiguration.builder()
            .singleQuoteStringLiteralsAllowed(true)
            .dataAccessorSupplier(CustomDataAccessor::new)
            .build();

    public static boolean toBoolean(String expression) {
        final Expression exp = new Expression(expression, EXPRESSION_CONFIGURATION);

        try {
            return exp.evaluate().getBooleanValue();
        } catch (Exception exception) {
            throw new IllegalArgumentException(exception);
        }
    }

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

    public static Optional<Boolean> toSafeBoolean(String expression) {
        try {
            return Optional.of(toBoolean(expression));
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    public static Optional<Boolean> toSafeBoolean(String expression, Player player) {
        try {
            return Optional.of(toBoolean(expression, player));
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    public static BigDecimal toNumeric(String expression) {
        final Expression exp = new Expression(expression);

        try {
            return exp.evaluate().getNumberValue();
        } catch (Exception exception) {
            throw new IllegalArgumentException(exception);
        }
    }

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

    public static Optional<BigDecimal> toSafeNumeric(String expression) {
        try {
            return Optional.of(toNumeric(expression));
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    public static Optional<BigDecimal> toSafeNumeric(String expression, Player player) {
        try {
            return Optional.of(toNumeric(expression, player));
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

}
