package io.github.johnnypixelz.utilizer.command;

import io.github.johnnypixelz.utilizer.command.exceptions.CommandAnnotationParseException;
import io.github.johnnypixelz.utilizer.command.exceptions.UnsupportedCommandArgumentException;
import io.github.johnnypixelz.utilizer.plugin.Logs;
import io.github.johnnypixelz.utilizer.plugin.Provider;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CommandManager {
    private static final List<Command> registeredCommands = new ArrayList<>();
    private static Map<String, org.bukkit.command.Command> knownCommands;
    private static CommandMap commandMap;

    static {
        try {
            Server server = Bukkit.getServer();
            Method getCommandMap = server.getClass().getDeclaredMethod("getCommandMap");
            getCommandMap.setAccessible(true);
            commandMap = (CommandMap) getCommandMap.invoke(server);
            if (!SimpleCommandMap.class.isAssignableFrom(commandMap.getClass())) {
                Logs.severe("Unable to register commands due to the CommandMap being found hijacked. Hijacked command map: " + commandMap.getClass().getName());
                Logs.severe("Command functionality will be disabled.");
            }
            Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);
            //noinspection unchecked
            knownCommands = (Map<String, org.bukkit.command.Command>) knownCommandsField.get(commandMap);
        } catch (Exception e) {
            Logs.severe("Failed to get Command Map. Command functionality will be disabled");
        }
    }

    public static void registerCommands(Class<? extends Command>... commands) {
        for (Class<? extends Command> command : commands) {
            try {
                final Constructor<? extends Command> declaredConstructor = command.getDeclaredConstructor();
                declaredConstructor.setAccessible(true);

                final Command newCommand = declaredConstructor.newInstance();
                newCommand.parseAnnotations();

                declaredConstructor.setAccessible(false);

                registeredCommands.add(newCommand);
                commandMap.register(newCommand.getLabels().get(0), Provider.getPlugin().getName(), new BukkitCommand(newCommand));
            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            } catch (CommandAnnotationParseException e) {
                e.printStackTrace();
            }
        }
    }

    public static void executeCommand(Command command, CommandSender sender, List<String> args) {
        deepSearchForSubcommand(command, args, (subcommand, remainingArgs) -> {
            final CommandMethod defaultMethod = subcommand.getDefaultMethod();
            try {
//                Passing command since that is the class which contains the declared method
                defaultMethod.execute(command, sender, remainingArgs);
            } catch (UnsupportedCommandArgumentException e) {
                throw new RuntimeException(e);
            }
        });

//        final String label = args.get(0).toLowerCase();
//        for (Command registeredCommand : registeredCommands) {
//            if (!registeredCommand.getLabels().contains(label)) continue;
//
//            deepSearchForSubcommand(registeredCommand, args.subList(1, args.size()), (command, remainingArgs) -> {
//                final CommandMethod defaultMethod = command.getDefaultMethod();
//                try {
//                    defaultMethod.execute(command, sender, remainingArgs);
//                } catch (UnsupportedCommandArgumentException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//
//        }
    }

    private static void deepSearchForSubcommand(Command command, List<String> args, BiConsumer<Command, List<String>> foundCommand) {
        if (args.isEmpty()) {
            Logs.info("[1] Found subcommand " + command.getLabels().get(0));
            foundCommand.accept(command, args);
            return;
        }

        String label = args.get(0).toLowerCase();
        for (Command subcommand : command.getSubcommands()) {
            if (subcommand.getLabels().contains(label)) {
                deepSearchForSubcommand(subcommand, args.subList(1, args.size()), foundCommand);
                return;
            }
        }

        Logs.info("[2] Found subcommand " + command.getLabels().get(0));
        foundCommand.accept(command, args);
    }

}
