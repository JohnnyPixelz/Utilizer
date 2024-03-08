package io.github.johnnypixelz.utilizer.command;

import io.github.johnnypixelz.utilizer.command.exceptions.NotEnoughArgumentsException;
import io.github.johnnypixelz.utilizer.command.exceptions.UnsupportedCommandArgumentException;
import io.github.johnnypixelz.utilizer.plugin.Logs;
import org.bukkit.command.CommandSender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class CommandMethod {
    private final Command methodParent;
    private final Method method;
    private Class<? extends CommandSender> senderType;

    public CommandMethod(Command methodParent, Method method) {
        this.methodParent = methodParent;
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

    public Command getMethodParent() {
        return methodParent;
    }

    public Method getMethod() {
        return method;
    }

    public void execute(CommandSender sender, List<String> arguments) throws UnsupportedCommandArgumentException, NotEnoughArgumentsException {
        final Queue<String> args = new LinkedList<>(arguments);
        final Queue<Parameter> parameters = new LinkedList<>(List.of(method.getParameters()));

        if (senderType != null) {
            parameters.poll();
        }

        List<Object> resolvedArguments = new ArrayList<>();

        while (!parameters.isEmpty()) {
            final Parameter parameter = parameters.poll();
            String argument = args.poll();

            if (argument == null) {
                throw new NotEnoughArgumentsException("Not enough arguments");
            }

//            If last parameter, and parameter is of type String, join all rest of arguments.
            if (parameters.isEmpty() && parameter.getType() == String.class) {
                while (!args.isEmpty()) {
                    argument += " " + args.poll();
                }
            }

            final Object resolvedArgument = CommandArgumentResolverManager.resolve(sender, argument, parameter);
            resolvedArguments.add(resolvedArgument);
        }

        final Object[] resolvedArgumentsArray = resolvedArguments.toArray(Object[]::new);

        if (senderType == null) {
            try {
                method.invoke(methodParent, resolvedArgumentsArray);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                List<Object> objectList = new ArrayList<>();
                objectList.add(sender);
                objectList.addAll(List.of(resolvedArgumentsArray));
                method.invoke(methodParent, objectList.toArray(Object[]::new));
            } catch (IllegalArgumentException e) {
                Logs.severe("Attempted to invoke command method for command " + methodParent.getLabels().get(0));
                Logs.severe("Method required " + method.getParameterCount() + " arguments and provided " + (resolvedArgumentsArray.length + 1));
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
