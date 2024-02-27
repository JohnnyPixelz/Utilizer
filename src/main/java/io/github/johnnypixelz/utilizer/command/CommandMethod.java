package io.github.johnnypixelz.utilizer.command;

import io.github.johnnypixelz.utilizer.command.exceptions.UnsupportedCommandArgumentException;
import org.bukkit.command.CommandSender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class CommandMethod {
    private final Method method;
    private Class<? extends CommandSender> senderType;

    public CommandMethod(Method method) {
        this.method = method;

        final Parameter[] parameters = method.getParameters();

        if (parameters.length >= 1) {
            final Parameter firstParameter = parameters[0];
            final Class<?> firstParameterType = firstParameter.getType();

//            Checking if the parameter is a command sender
            if (CommandSender.class.isAssignableFrom(firstParameterType) || CommandSender.class.isAssignableFrom(firstParameterType.getSuperclass())) {
                @SuppressWarnings("unchecked")
                Class<? extends CommandSender> senderType = (Class<? extends CommandSender>) firstParameterType;
                this.senderType = senderType;
            }
        }
    }

    public void execute(Command command, CommandSender sender, List<String> arguments) throws UnsupportedCommandArgumentException {
        Stream<Parameter> parameterStream = Arrays.stream(method.getParameters());
        if (senderType != null) {
            parameterStream = parameterStream.skip(1);
        }

        final Parameter[] parameters = parameterStream.toArray(Parameter[]::new);

        if (arguments.size() < parameters.length) {
            throw new IllegalArgumentException("Not enough arguments");
        }

        List<Object> resolvedArguments = new ArrayList<>();

        for (int index = 0; index < parameters.length; index++) {
            final Parameter parameter = parameters[index];
            final String argument = arguments.get(index);

            final Object resolvedArgument = CommandArgumentResolverManager.resolve(parameter.getType(), argument);
            resolvedArguments.add(resolvedArgument);
        }

        final Object[] resolvedArgumentsArray = resolvedArguments.toArray(Object[]::new);

        if (senderType == null) {
            try {
                method.invoke(command, resolvedArgumentsArray);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                method.invoke(command, sender, resolvedArgumentsArray);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
