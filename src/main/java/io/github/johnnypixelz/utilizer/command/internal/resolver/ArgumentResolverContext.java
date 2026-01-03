package io.github.johnnypixelz.utilizer.command.internal.resolver;

import org.bukkit.command.CommandSender;

import java.lang.reflect.Parameter;

/**
 * Context provided to argument resolvers containing all information needed to resolve an argument.
 */
public final class ArgumentResolverContext {

    private final CommandSender sender;
    private final Parameter parameter;
    private final String argument;

    public ArgumentResolverContext(CommandSender sender, Parameter parameter, String argument) {
        this.sender = sender;
        this.parameter = parameter;
        this.argument = argument;
    }

    /**
     * @return the command sender who executed the command
     */
    public CommandSender getSender() {
        return sender;
    }

    /**
     * @return the method parameter being resolved
     */
    public Parameter getParameter() {
        return parameter;
    }

    /**
     * @return the raw argument string to resolve
     */
    public String getArgument() {
        return argument;
    }

    /**
     * @return the expected type of the resolved value
     */
    public Class<?> getTargetType() {
        return parameter.getType();
    }

}
