package io.github.johnnypixelz.utilizer.config.evaluate.dataaccessors;

import com.ezylang.evalex.data.DataAccessorIfc;
import com.ezylang.evalex.data.EvaluationValue;

import java.util.Map;
import java.util.TreeMap;

public class CustomDataAccessor implements DataAccessorIfc {
    private final Map<String, EvaluationValue> variables = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public CustomDataAccessor() {
        // Map specific string literals to boolean values
        variables.put("yes", EvaluationValue.booleanValue(true));
        variables.put("no", EvaluationValue.booleanValue(false));

        variables.put("on", EvaluationValue.booleanValue(true));
        variables.put("off", EvaluationValue.booleanValue(false));
    }

    @Override
    public EvaluationValue getData(String variableName) {
        // If the variable exists, return its value
        if (variables.containsKey(variableName)) {
            return variables.get(variableName);
        }
        // If the variable doesn't exist, treat the variable name as a string literal
        return EvaluationValue.stringValue(variableName);
    }

    @Override
    public void setData(String variableName, EvaluationValue value) {
        variables.put(variableName, value);
    }

}
