package io.github.johnnypixelz.utilizer.command.internal;

import io.github.johnnypixelz.utilizer.command.annotations.Async;
import io.github.johnnypixelz.utilizer.command.annotations.Cooldown;
import io.github.johnnypixelz.utilizer.command.annotations.DefaultValue;
import io.github.johnnypixelz.utilizer.command.annotations.Optional;
import io.github.johnnypixelz.utilizer.command.annotations.Single;
import io.github.johnnypixelz.utilizer.command.annotations.Values;
import io.github.johnnypixelz.utilizer.command.exceptions.NotEnoughArgumentsException;
import io.github.johnnypixelz.utilizer.command.exceptions.UnsupportedCommandArgumentException;
import io.github.johnnypixelz.utilizer.command.internal.resolver.ArgumentResolutionException;
import io.github.johnnypixelz.utilizer.command.internal.resolver.ArgumentResolverRegistry;
import io.github.johnnypixelz.utilizer.command.internal.validation.ArgumentValidator;
import org.bukkit.command.CommandSender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * Represents a command method that can be invoked.
 * Immutable - created once during annotation parsing.
 */
public class CommandMethodDefinition {

    private final Object instance;
    private final Method method;
    private final Class<? extends CommandSender> senderType;
    private final Parameter[] commandParameters;

    public CommandMethodDefinition(Object instance, Method method) {
        this.instance = instance;
        this.method = method;
        this.method.setAccessible(true);

        Parameter[] allParams = method.getParameters();
        this.senderType = MethodAnalyzer.detectSenderType(allParams);
        this.commandParameters = MethodAnalyzer.extractCommandParameters(allParams, senderType != null);
    }

    /**
     * @return the instance that owns this method
     */
    public Object getInstance() {
        return instance;
    }

    /**
     * @return the underlying method
     */
    public Method getMethod() {
        return method;
    }

    /**
     * @return the expected sender type, or null if method doesn't take a sender
     */
    public Class<? extends CommandSender> getSenderType() {
        return senderType;
    }

    /**
     * @return the command parameters (excluding sender)
     */
    public Parameter[] getCommandParameters() {
        return commandParameters;
    }

    /**
     * @return true if this method accepts a sender as its first parameter
     */
    public boolean hasSenderParameter() {
        return senderType != null;
    }

    /**
     * @return the number of required command arguments (excludes @Optional and @DefaultValue parameters)
     */
    public int getRequiredArgumentCount() {
        int count = 0;
        for (Parameter param : commandParameters) {
            if (!isOptionalParameter(param)) {
                count++;
            }
        }
        return count;
    }

    /**
     * @return true if this method should execute asynchronously
     */
    public boolean isAsync() {
        return method.isAnnotationPresent(Async.class);
    }

    /**
     * @return the cooldown annotation if present, or null
     */
    public Cooldown getCooldown() {
        return method.getAnnotation(Cooldown.class);
    }

    /**
     * Checks if a parameter is optional (has @Optional or @DefaultValue).
     *
     * @param param the parameter to check
     * @return true if the parameter is optional
     */
    private static boolean isOptionalParameter(Parameter param) {
        return param.isAnnotationPresent(Optional.class) || param.isAnnotationPresent(DefaultValue.class);
    }

    /**
     * Checks if a String parameter should be treated as single-word (not greedy).
     *
     * @param param the parameter to check
     * @return true if marked with @Single
     */
    private static boolean isSingleParameter(Parameter param) {
        return param.isAnnotationPresent(Single.class);
    }

    /**
     * Gets the allowed values for a parameter, if restricted.
     *
     * @param param the parameter to check
     * @return the allowed values, or null if not restricted
     */
    private static Set<String> getAllowedValues(Parameter param) {
        Values values = param.getAnnotation(Values.class);
        if (values == null) {
            return null;
        }
        Set<String> allowed = new HashSet<>();
        for (String value : values.value().split("\\|")) {
            allowed.add(value.trim().toLowerCase());
        }
        return allowed;
    }

    /**
     * Invokes this command method with the given sender and arguments.
     *
     * @param sender           the command sender
     * @param arguments        the raw argument strings
     * @param resolverRegistry the registry to resolve arguments
     * @throws NotEnoughArgumentsException         if not enough arguments provided
     * @throws UnsupportedCommandArgumentException if an argument type is unsupported
     * @throws ArgumentResolutionException         if argument resolution fails
     */
    public void invoke(CommandSender sender, List<String> arguments, ArgumentResolverRegistry resolverRegistry)
            throws NotEnoughArgumentsException, UnsupportedCommandArgumentException, ArgumentResolutionException {

        Queue<String> args = new LinkedList<>(arguments);
        List<Object> resolvedArguments = new ArrayList<>();

        for (int i = 0; i < commandParameters.length; i++) {
            Parameter parameter = commandParameters[i];
            boolean isLast = (i == commandParameters.length - 1);
            String argument = args.poll();

            // Handle missing argument
            if (argument == null) {
                // Check for @DefaultValue
                DefaultValue defaultValue = parameter.getAnnotation(DefaultValue.class);
                if (defaultValue != null) {
                    argument = defaultValue.value();
                }
                // Check for @Optional
                else if (parameter.isAnnotationPresent(Optional.class)) {
                    resolvedArguments.add(null);
                    continue;
                }
                // Required argument missing
                else {
                    throw new NotEnoughArgumentsException(
                            getRequiredArgumentCount(),
                            arguments.size()
                    );
                }
            }

            // Handle greedy String (last String parameter without @Single)
            if (isLast && parameter.getType() == String.class && !isSingleParameter(parameter)) {
                StringBuilder joined = new StringBuilder(argument);
                while (!args.isEmpty()) {
                    joined.append(" ").append(args.poll());
                }
                argument = joined.toString();
            }

            // Validate against @Values if present
            Set<String> allowedValues = getAllowedValues(parameter);
            if (allowedValues != null && !allowedValues.contains(argument.toLowerCase())) {
                Values valuesAnnotation = parameter.getAnnotation(Values.class);
                throw new ArgumentResolutionException(
                        "Invalid value '" + argument + "'. Allowed values: " + valuesAnnotation.value().replace("|", ", ")
                );
            }

            Object resolved = resolverRegistry.resolve(sender, argument, parameter);

            // Validate resolved argument against @Range, @Length, etc.
            ArgumentValidator.validate(parameter, resolved);

            resolvedArguments.add(resolved);
        }

        invokeMethod(sender, resolvedArguments);
    }

    private void invokeMethod(CommandSender sender, List<Object> resolvedArguments) {
        try {
            if (senderType != null) {
                List<Object> allArgs = new ArrayList<>();
                allArgs.add(sender);
                allArgs.addAll(resolvedArguments);
                method.invoke(instance, allArgs.toArray());
            } else {
                method.invoke(instance, resolvedArguments.toArray());
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to invoke command method: " + method.getName(), e);
        }
    }

}
