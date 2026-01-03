package io.github.johnnypixelz.utilizer.command.internal;

import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

/**
 * Analyzes command methods to extract parameter information.
 * Shared logic used by both CommandMethodDefinition and SyntaxGenerator.
 */
public final class MethodAnalyzer {

    private MethodAnalyzer() {
    }

    /**
     * Detects if the first parameter is a CommandSender type.
     *
     * @param parameters the method parameters
     * @return the CommandSender subclass, or null if first param is not a sender
     */
    public static Class<? extends CommandSender> detectSenderType(Parameter[] parameters) {
        if (parameters.length == 0) {
            return null;
        }

        Class<?> firstType = parameters[0].getType();

        if (CommandSender.class.isAssignableFrom(firstType)) {
            @SuppressWarnings("unchecked")
            Class<? extends CommandSender> senderType = (Class<? extends CommandSender>) firstType;
            return senderType;
        }

        return null;
    }

    /**
     * Extracts the command parameters (excluding the sender parameter if present).
     *
     * @param allParameters all method parameters
     * @param hasSender     whether the first parameter is a sender
     * @return the command parameters only
     */
    public static Parameter[] extractCommandParameters(Parameter[] allParameters, boolean hasSender) {
        if (!hasSender || allParameters.length == 0) {
            return allParameters;
        }
        return Arrays.copyOfRange(allParameters, 1, allParameters.length);
    }

    /**
     * Extracts the command parameters from a method, automatically detecting the sender.
     *
     * @param method the method to analyze
     * @return the command parameters (excluding sender)
     */
    public static Parameter[] extractCommandParameters(Method method) {
        Parameter[] allParams = method.getParameters();
        boolean hasSender = detectSenderType(allParams) != null;
        return extractCommandParameters(allParams, hasSender);
    }

}
