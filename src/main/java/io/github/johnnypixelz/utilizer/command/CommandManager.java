package io.github.johnnypixelz.utilizer.command;

import io.github.johnnypixelz.utilizer.command.exceptions.CommandAnnotationParseException;
import io.github.johnnypixelz.utilizer.command.exceptions.NotEnoughArgumentsException;
import io.github.johnnypixelz.utilizer.command.exceptions.UnsupportedCommandArgumentException;
import io.github.johnnypixelz.utilizer.plugin.Logs;
import io.github.johnnypixelz.utilizer.plugin.Provider;
import io.github.johnnypixelz.utilizer.text.Colors;
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
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

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

    @SafeVarargs
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

        final String parameterSentence = String.join(" ", args);
        Pattern argsPattern = Pattern.compile("\".+?\"|[^ ]+");
        List<String> currentArgs = argsPattern.matcher(parameterSentence)
                .results()
                .map(MatchResult::group)
                .map(String::trim)
                .toList();

        if (!command.checkIfPermittedAndInform(sender)) return;

        Logs.info("Executing command " + command.getLabels().get(0));
        while (!currentArgs.isEmpty()) {
            String label = currentArgs.get(0).toLowerCase();
            boolean found = false;

            Logs.info("Searching for subcommands named " + label);
            for (Command subcommand : currentCommand.getSubcommands()) {
                Logs.info("Checking for subcommand " + subcommand.getLabels().get(0));
                if (subcommand.getLabels().contains(label)) {
                    Logs.info("Found subcommand " + label);

                    if (!subcommand.checkIfPermittedAndInform(sender)) return;

                    currentCommand = subcommand;
                    currentArgs = currentArgs.subList(1, currentArgs.size());
                    found = true;
                    break;
                }
            }

            if (!found) {
                Logs.info("Didn't find subcommand with name " + label + ", current command is " + currentCommand.getLabels().get(0));
                break;
            }
        }

        try {
            final CommandMethod defaultMethod = currentCommand.getDefaultMethod();
            if (defaultMethod == null) {
                final String unknownCommand = Bukkit.spigot().getConfig().getString("messages.unknown-command", "Unknown command. Type \"/help\" for help.");
                sender.sendMessage(Colors.color(unknownCommand));
                return;
            }

            defaultMethod.execute(sender, currentArgs);
        } catch (UnsupportedCommandArgumentException exception) {
            CommandMessageManager.getMessage(CommandMessage.INTERNAL_ERROR).send(sender);
            exception.printStackTrace();
        } catch (NotEnoughArgumentsException exception) {
            CommandMessageManager.getMessage(CommandMessage.NOT_ENOUGH_ARGUMENTS).send(sender);
            sender.sendMessage(currentCommand.getSyntax().generateSyntax());
        }
    }

//    private static void deepSearchForSubcommand(Command command, List<String> args, BiConsumer<Command, List<String>> foundCommand) {
//        Command currentCommand = command;
//        List<String> currentArgs = args;
//
//        while (!currentArgs.isEmpty()) {
//            String label = currentArgs.get(0).toLowerCase();
//            boolean found = false;
//
//            for (Command subcommand : currentCommand.getSubcommands()) {
//                if (subcommand.getLabels().contains(label)) {
//                    currentCommand = subcommand;
//                    currentArgs = currentArgs.subList(1, currentArgs.size());
//                    found = true;
//                    break;
//                }
//            }
//
//            if (!found) {
//                break;
//            }
//        }
//
//        foundCommand.accept(currentCommand, currentArgs);
//    }

}
