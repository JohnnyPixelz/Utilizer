package io.github.johnnypixelz.utilizer.command;

import io.github.johnnypixelz.utilizer.command.exceptions.CommandAnnotationParseException;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class CommandManager {
    private static List<Command> registeredCommands;

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
            if (!registeredCommand.getRootLabels().contains(label)) continue;

        }
    }

}
