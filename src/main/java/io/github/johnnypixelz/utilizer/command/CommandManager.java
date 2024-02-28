package io.github.johnnypixelz.utilizer.command;

import io.github.johnnypixelz.utilizer.command.exceptions.CommandAnnotationParseException;
import io.github.johnnypixelz.utilizer.command.exceptions.UnsupportedCommandArgumentException;
import io.github.johnnypixelz.utilizer.command.permissions.CommandPermission;
import io.github.johnnypixelz.utilizer.command.permissions.CommandPermissionMessage;
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
import java.util.Optional;
import java.util.function.BiConsumer;

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
            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException |
                     IllegalAccessException e) {
                e.printStackTrace();
            } catch (CommandAnnotationParseException e) {
                e.printStackTrace();
            }
        }
    }

    static void registerCommand(Command command) {
        registeredCommands.add(command);
        commandMap.register(command.getLabels().get(0), Provider.getPlugin().getName(), new BukkitCommand(command));
    }

    static void executeCommand(Command command, CommandSender sender, List<String> args) {
        Command currentCommand = command;
        List<String> currentArgs = args;

        for (CommandPermission requiredPermission : command.getRequiredPermissions()) {
            if (!sender.hasPermission(requiredPermission.getPermission())) {
                requiredPermission.getPermissionMessage()
                        .or(() -> Optional.ofNullable(command.getPermissionMessage()))
                        .map(CommandPermissionMessage::getMessage)
                        .orElse(CommandMessageManager.getMessage(CommandMessage.NO_PERMISSION))
                        .send(sender);
                return;
            }
        }

        while (!currentArgs.isEmpty()) {
            String label = currentArgs.get(0).toLowerCase();
            boolean found = false;

            for (Command subcommand : currentCommand.getSubcommands()) {
                if (subcommand.getLabels().contains(label)) {

                    for (CommandPermission requiredPermission : subcommand.getRequiredPermissions()) {
                        if (!sender.hasPermission(requiredPermission.getPermission())) {
                            requiredPermission.getPermissionMessage()
                                    .or(() -> Optional.ofNullable(subcommand.getPermissionMessage()))
                                    .map(CommandPermissionMessage::getMessage)
                                    .orElse(CommandMessageManager.getMessage(CommandMessage.NO_PERMISSION))
                                    .send(sender);
                            return;
                        }
                    }

                    currentCommand = subcommand;
                    currentArgs = currentArgs.subList(1, currentArgs.size());
                    found = true;
                    break;
                }
            }

            if (!found) {
                break;
            }
        }

        try {
            final CommandMethod defaultMethod = currentCommand.getDefaultMethod();
            defaultMethod.execute(sender, currentArgs);
        } catch (UnsupportedCommandArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    private static void deepSearchForSubcommand(Command command, List<String> args, BiConsumer<Command, List<String>> foundCommand) {
        Command currentCommand = command;
        List<String> currentArgs = args;

        while (!currentArgs.isEmpty()) {
            String label = currentArgs.get(0).toLowerCase();
            boolean found = false;

            for (Command subcommand : currentCommand.getSubcommands()) {
                if (subcommand.getLabels().contains(label)) {
                    currentCommand = subcommand;
                    currentArgs = currentArgs.subList(1, currentArgs.size());
                    found = true;
                    break;
                }
            }

            if (!found) {
                break;
            }
        }

        foundCommand.accept(currentCommand, currentArgs);
    }

}
