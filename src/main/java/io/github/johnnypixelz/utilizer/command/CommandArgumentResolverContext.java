package io.github.johnnypixelz.utilizer.command;

import org.bukkit.command.CommandSender;

import java.lang.reflect.Parameter;

public class CommandArgumentResolverContext {
    private final CommandSender sender;
    private final Parameter parameter;
    private final String argument;

    public CommandArgumentResolverContext(CommandSender sender, Parameter parameter, String argument) {
        this.sender = sender;
        this.parameter = parameter;
        this.argument = argument;
    }

    public CommandSender getSender() {
        return sender;
    }

    public String getArgument() {
        return argument;
    }

    public Parameter getParameter() {
        return parameter;
    }

}
