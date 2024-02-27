package io.github.johnnypixelz.utilizer.command;

import io.github.johnnypixelz.utilizer.command.exceptions.CommandAnnotationParseException;
import io.github.johnnypixelz.utilizer.command.exceptions.UnsupportedCommandArgumentException;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CommandManager {
    private static final List<Command> registeredCommands = new ArrayList<>();

    public static void registerCommands(Class<? extends Command>... commands) {
        for (Class<? extends Command> command : commands) {
            try {
                final Constructor<? extends Command> declaredConstructor = command.getDeclaredConstructor();
                declaredConstructor.setAccessible(true);

                final Command newCommand = declaredConstructor.newInstance();
                newCommand.parseAnnotations();

                declaredConstructor.setAccessible(false);

                registeredCommands.add(newCommand);
            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            } catch (CommandAnnotationParseException e) {
                e.printStackTrace();
            }
        }
    }

    public static void executeCommand(CommandSender sender, List<String> args) {
        if (args.isEmpty()) {
            throw new IllegalArgumentException("'args' cannot be empty.");
        }

        final String label = args.get(0).toLowerCase();
        for (Command registeredCommand : registeredCommands) {
            if (!registeredCommand.getLabels().contains(label)) continue;

            deepSearchForSubcommand(registeredCommand, args.subList(1, args.size()), (command, remainingArgs) -> {
                final CommandMethod defaultMethod = command.getDefaultMethod();
                try {
                    defaultMethod.execute(command, sender, remainingArgs);
                } catch (UnsupportedCommandArgumentException e) {
                    throw new RuntimeException(e);
                }
            });

        }
    }

    private static void deepSearchForSubcommand(Command command, List<String> args, BiConsumer<Command, List<String>> foundCommand) {
        if (args.isEmpty()) foundCommand.accept(command, args);
        String label = args.get(0).toLowerCase();
        for (Command subcommand : command.getSubcommands()) {
            if (subcommand.getLabels().contains(label)) {
                deepSearchForSubcommand(command, args.subList(1, args.size()), foundCommand);
            }
        }

        foundCommand.accept(command, args);;
    }

}
