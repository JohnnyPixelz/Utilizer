package io.github.johnnypixelz.utilizer.inventories.config.conditions;

import io.github.johnnypixelz.utilizer.config.evaluate.Evaluate;
import io.github.johnnypixelz.utilizer.plugin.Logs;
import org.bukkit.entity.Player;

public class BooleanExpressionCondition implements Condition {
    private final String expression;

    public BooleanExpressionCondition(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    public boolean evaluate(Player player) {
        try {
            if (player != null) {
                return Evaluate.toBoolean(expression, player);
            } else {
                return Evaluate.toBoolean(expression);
            }
        } catch (Exception exception) {
            Logs.warn(exception.getMessage());
            return false;
        }
    }

}
