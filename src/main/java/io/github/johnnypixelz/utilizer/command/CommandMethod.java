package io.github.johnnypixelz.utilizer.command;

import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

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

    public void execute(CommandSender sender, String[] arguments) {
//        if (senderType != null) {
//            method.invoke()
//        }
    }

}
