package io.github.johnnypixelz.utilizer.config.evaluate.functions;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.Token;
import org.bukkit.entity.Player;

@FunctionParameter(name = "permission")
public class HasPermissionFunction extends AbstractFunction {
    private final Player player;

    public HasPermissionFunction(Player player) {
        this.player = player;
    }

    @Override
    public EvaluationValue evaluate(Expression expression, Token functionToken, EvaluationValue... parameterValues) {
        String permission = parameterValues[0].getStringValue();
        boolean hasPermission = player.hasPermission(permission);
        return EvaluationValue.booleanValue(hasPermission);
    }

}
