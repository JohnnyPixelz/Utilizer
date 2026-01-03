package io.github.johnnypixelz.utilizer.command.internal;

import io.github.johnnypixelz.utilizer.command.CommandHandler;
import io.github.johnnypixelz.utilizer.command.exceptions.NotEnoughArgumentsException;
import io.github.johnnypixelz.utilizer.command.exceptions.UnsupportedCommandArgumentException;
import io.github.johnnypixelz.utilizer.command.internal.resolver.ArgumentResolutionException;
import io.github.johnnypixelz.utilizer.command.internal.resolver.ArgumentResolverRegistry;
import io.github.johnnypixelz.utilizer.text.Colors;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 * A method definition that wraps a programmatic {@link CommandHandler}.
 */
public final class ProgrammaticMethodDefinition extends CommandMethodDefinition {

    private final CommandHandler<CommandSender> handler;
    private final Class<? extends CommandSender> senderType;

    public ProgrammaticMethodDefinition(CommandHandler<CommandSender> handler, Class<? extends CommandSender> senderType) {
        super(null, getDummyMethod());
        this.handler = handler;
        this.senderType = senderType;
    }

    @Override
    public Class<? extends CommandSender> getSenderType() {
        return senderType;
    }

    @Override
    public Parameter[] getCommandParameters() {
        return new Parameter[0]; // Programmatic commands handle args manually
    }

    @Override
    public boolean hasSenderParameter() {
        return true;
    }

    @Override
    public int getRequiredArgumentCount() {
        return 0; // Programmatic commands handle validation manually
    }

    @Override
    public void invoke(CommandSender sender, List<String> arguments, ArgumentResolverRegistry resolverRegistry)
            throws NotEnoughArgumentsException, UnsupportedCommandArgumentException, ArgumentResolutionException {

        // Check sender type
        if (!senderType.isInstance(sender)) {
            sender.sendMessage(Colors.color("&cThis command can only be used by " + senderType.getSimpleName() + "."));
            return;
        }

        handler.execute(sender, arguments);
    }

    // Dummy method for parent constructor (we override all the methods anyway)
    private static Method getDummyMethod() {
        try {
            return ProgrammaticMethodDefinition.class.getDeclaredMethod("dummyMethod");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unused")
    private void dummyMethod() {
        // Dummy method for reflection
    }

}
