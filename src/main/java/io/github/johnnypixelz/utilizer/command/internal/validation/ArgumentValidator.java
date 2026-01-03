package io.github.johnnypixelz.utilizer.command.internal.validation;

import io.github.johnnypixelz.utilizer.command.annotations.Length;
import io.github.johnnypixelz.utilizer.command.annotations.Range;
import io.github.johnnypixelz.utilizer.command.internal.resolver.ArgumentResolutionException;

import java.lang.reflect.Parameter;

/**
 * Validates resolved arguments against their validation annotations.
 */
public final class ArgumentValidator {

    private ArgumentValidator() {
    }

    /**
     * Validates a resolved argument against its parameter's validation annotations.
     *
     * @param param the parameter with validation annotations
     * @param value the resolved value to validate
     * @throws ArgumentResolutionException if validation fails
     */
    public static void validate(Parameter param, Object value) throws ArgumentResolutionException {
        if (value == null) {
            return; // Null values are handled by @Optional
        }

        validateRange(param, value);
        validateLength(param, value);
    }

    /**
     * Validates a numeric value against @Range constraints.
     */
    private static void validateRange(Parameter param, Object value) throws ArgumentResolutionException {
        Range range = param.getAnnotation(Range.class);
        if (range == null) {
            return;
        }

        double numericValue;
        if (value instanceof Number number) {
            numericValue = number.doubleValue();
        } else {
            return; // Not a number, skip validation
        }

        if (numericValue < range.min() || numericValue > range.max()) {
            String message = range.message();
            if (message.isEmpty()) {
                if (range.min() == Double.NEGATIVE_INFINITY) {
                    message = "Value must be at most " + formatNumber(range.max());
                } else if (range.max() == Double.POSITIVE_INFINITY) {
                    message = "Value must be at least " + formatNumber(range.min());
                } else {
                    message = "Value must be between " + formatNumber(range.min()) + " and " + formatNumber(range.max());
                }
            } else {
                message = message
                        .replace("%value%", String.valueOf(numericValue))
                        .replace("%min%", formatNumber(range.min()))
                        .replace("%max%", formatNumber(range.max()));
            }
            throw new ArgumentResolutionException(message);
        }
    }

    /**
     * Validates a string value against @Length constraints.
     */
    private static void validateLength(Parameter param, Object value) throws ArgumentResolutionException {
        Length length = param.getAnnotation(Length.class);
        if (length == null) {
            return;
        }

        if (!(value instanceof String str)) {
            return; // Not a string, skip validation
        }

        int strLength = str.length();

        if (strLength < length.min() || strLength > length.max()) {
            String message = length.message();
            if (message.isEmpty()) {
                if (length.min() == 0) {
                    message = "Text must be at most " + length.max() + " characters";
                } else if (length.max() == Integer.MAX_VALUE) {
                    message = "Text must be at least " + length.min() + " characters";
                } else {
                    message = "Text must be between " + length.min() + " and " + length.max() + " characters";
                }
            } else {
                message = message
                        .replace("%value%", str)
                        .replace("%length%", String.valueOf(strLength))
                        .replace("%min%", String.valueOf(length.min()))
                        .replace("%max%", String.valueOf(length.max()));
            }
            throw new ArgumentResolutionException(message);
        }
    }

    /**
     * Formats a number for display, removing unnecessary decimal places.
     */
    private static String formatNumber(double value) {
        if (value == Double.NEGATIVE_INFINITY) {
            return "-infinity";
        }
        if (value == Double.POSITIVE_INFINITY) {
            return "infinity";
        }
        if (value == (long) value) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }

}
