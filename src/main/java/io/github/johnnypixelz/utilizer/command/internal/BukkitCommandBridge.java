package io.github.johnnypixelz.utilizer.command.internal;

import io.github.johnnypixelz.utilizer.command.annotations.Values;
import io.github.johnnypixelz.utilizer.command.internal.completer.TabCompleterContext;
import io.github.johnnypixelz.utilizer.command.internal.completer.TabCompleterRegistry;
import io.github.johnnypixelz.utilizer.command.internal.resolver.ArgumentResolverRegistry;
import io.github.johnnypixelz.utilizer.plugin.Provider;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Bridges the command system to Bukkit's command infrastructure.
 */
public final class BukkitCommandBridge extends Command implements PluginIdentifiableCommand {

    private static CommandMap commandMap;
    private static Map<String, Command> knownCommands;

    static {
        initializeCommandMap();
    }

    private final CommandDefinition definition;
    private final CommandExecutor executor;
    private final TabCompleterRegistry completerRegistry;

    public BukkitCommandBridge(CommandDefinition definition, ArgumentResolverRegistry resolverRegistry, TabCompleterRegistry completerRegistry) {
        super(definition.getPrimaryLabel());
        this.definition = definition;
        this.executor = new CommandExecutor(resolverRegistry);
        this.completerRegistry = completerRegistry;

        // Set aliases (all labels except the first)
        if (definition.getLabels().size() > 1) {
            setAliases(definition.getLabels().subList(1, definition.getLabels().size()));
        }

        // Set description
        if (definition.getDescription() != null) {
            setDescription(definition.getDescription());
        }
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        DebugLogger.commandExecution(commandLabel, args.length);
        executor.execute(definition, sender, List.of(args));
        return true;
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        DebugLogger.tabComplete(alias, args);

        CommandDefinition current = definition;
        List<String> consumedArgs = new ArrayList<>();
        Queue<String> arguments = new LinkedList<>(List.of(args));
        int argIndex = 0;

        while (!arguments.isEmpty()) {
            String argument = arguments.poll();

            // If this is the last argument being typed
            if (arguments.isEmpty()) {
                // First try subcommand completion
                List<String> subcommandCompletions = current.getSubcommands().stream()
                        .filter(sub -> sub.isPermitted(sender))
                        .filter(sub -> !sub.isPrivate())  // Filter out @Private commands
                        .map(CommandDefinition::getLabels)
                        .flatMap(List::stream)
                        .filter(label -> label.toLowerCase().startsWith(argument.toLowerCase()))
                        .toList();

                if (!subcommandCompletions.isEmpty()) {
                    return subcommandCompletions;
                }

                // If no subcommands match, try argument completion
                return getArgumentCompletions(current, sender, argIndex, argument, consumedArgs);
            }

            // Try to find a subcommand matching this argument
            Optional<CommandDefinition> subcommand = current.findSubcommand(argument);
            if (subcommand.isPresent()) {
                current = subcommand.get();
                argIndex = 0;  // Reset for the subcommand
                consumedArgs.clear();
            } else {
                // This argument is consumed by a parameter
                argIndex++;
                consumedArgs.add(argument);
            }
        }

        // If no arguments at all, return all subcommand labels
        List<String> completions = current.getSubcommands().stream()
                .filter(sub -> sub.isPermitted(sender))
                .filter(sub -> !sub.isPrivate())  // Filter out @Private commands
                .map(CommandDefinition::getLabels)
                .flatMap(List::stream)
                .toList();

        if (!completions.isEmpty()) {
            return completions;
        }

        // If no subcommands, try argument completion for first argument
        return getArgumentCompletions(current, sender, 0, "", Collections.emptyList());
    }

    /**
     * Gets completions for a specific argument position.
     */
    private List<String> getArgumentCompletions(CommandDefinition command, CommandSender sender, int argIndex, String partial, List<String> previousArgs) {
        CommandMethodDefinition method = command.getDefaultMethod();
        if (method == null) {
            return Collections.emptyList();
        }

        Parameter[] params = method.getCommandParameters();
        if (argIndex >= params.length) {
            return Collections.emptyList();
        }

        Parameter param = params[argIndex];

        // Check for @Values annotation first
        Values values = param.getAnnotation(Values.class);
        if (values != null) {
            return Arrays.stream(values.value().split("\\|"))
                    .map(String::trim)
                    .filter(v -> v.toLowerCase().startsWith(partial.toLowerCase()))
                    .toList();
        }

        // Check for @CommandCompletion specification
        String completionSpec = command.getCompletionSpec();
        if (completionSpec != null) {
            String[] specs = completionSpec.split("\\s+");
            if (argIndex < specs.length) {
                String spec = specs[argIndex];
                if (spec.startsWith("@")) {
                    return executeCompleter(spec, sender, param, partial, previousArgs);
                }
            }
        }

        // Fall back to type-based completion
        return getTypeBasedCompletions(param, sender, partial, previousArgs);
    }

    /**
     * Executes a completer by its spec (e.g., "@players" or "@range:1-64").
     */
    private List<String> executeCompleter(String spec, CommandSender sender, Parameter param, String partial, List<String> previousArgs) {
        String id;
        String config = null;

        // Parse spec like "@range:1-64"
        int colonIndex = spec.indexOf(':');
        if (colonIndex > 0) {
            id = spec.substring(1, colonIndex);
            config = spec.substring(colonIndex + 1);
        } else {
            id = spec.substring(1);  // Remove @ prefix
        }

        TabCompleterContext context = new TabCompleterContext(sender, param, partial, previousArgs, config);
        return completerRegistry.complete(id, context);
    }

    /**
     * Gets completions based on parameter type.
     */
    private List<String> getTypeBasedCompletions(Parameter param, CommandSender sender, String partial, List<String> previousArgs) {
        Class<?> type = param.getType();
        TabCompleterContext context = new TabCompleterContext(sender, param, partial, previousArgs, null);

        // Player type
        if (org.bukkit.entity.Player.class.isAssignableFrom(type)) {
            return completerRegistry.complete("players", context);
        }

        // Boolean type
        if (type == boolean.class || type == Boolean.class) {
            return completerRegistry.complete("boolean", context);
        }

        // Enum types
        if (type.isEnum()) {
            return Arrays.stream(type.getEnumConstants())
                    .map(e -> ((Enum<?>) e).name().toLowerCase())
                    .filter(name -> name.startsWith(partial.toLowerCase()))
                    .toList();
        }

        return Collections.emptyList();
    }

    @NotNull
    @Override
    public String getDescription() {
        return definition.getDescription() != null ? definition.getDescription() : "No description available";
    }

    @NotNull
    @Override
    public Plugin getPlugin() {
        return Provider.getPlugin();
    }

    /**
     * Registers a command definition with Bukkit.
     *
     * @param definition        the command definition
     * @param resolverRegistry  the resolver registry
     * @param completerRegistry the tab completer registry
     * @return the created bridge command
     */
    public static BukkitCommandBridge register(CommandDefinition definition, ArgumentResolverRegistry resolverRegistry, TabCompleterRegistry completerRegistry) {
        if (commandMap == null) {
            throw new IllegalStateException("CommandMap not available - command registration disabled");
        }

        BukkitCommandBridge bridge = new BukkitCommandBridge(definition, resolverRegistry, completerRegistry);
        commandMap.register(definition.getPrimaryLabel(), Provider.getPlugin().getName(), bridge);

        DebugLogger.commandRegistration(definition.getPrimaryLabel());
        return bridge;
    }

    /**
     * Unregisters a command from Bukkit.
     * Removes all labels and aliases from the known commands map.
     *
     * @param definition the command definition to unregister
     * @return true if the command was unregistered successfully
     */
    public static boolean unregister(CommandDefinition definition) {
        if (knownCommands == null) {
            return false;
        }

        String pluginPrefix = Provider.getPlugin().getName().toLowerCase() + ":";
        boolean removed = false;

        for (String label : definition.getLabels()) {
            String lowerLabel = label.toLowerCase();

            // Remove the plain label
            Command cmd = knownCommands.remove(lowerLabel);
            if (cmd != null) {
                cmd.unregister(commandMap);
                removed = true;
            }

            // Remove the prefixed label (pluginname:command)
            cmd = knownCommands.remove(pluginPrefix + lowerLabel);
            if (cmd != null) {
                cmd.unregister(commandMap);
                removed = true;
            }
        }

        if (removed) {
            DebugLogger.commandRegistration("Unregistered: " + definition.getPrimaryLabel());
        }

        return removed;
    }

    /**
     * Unregisters a command by its label.
     *
     * @param label the command label
     * @return true if the command was found and unregistered
     */
    public static boolean unregister(String label) {
        if (knownCommands == null) {
            return false;
        }

        String lowerLabel = label.toLowerCase();
        String pluginPrefix = Provider.getPlugin().getName().toLowerCase() + ":";

        Command cmd = knownCommands.remove(lowerLabel);
        if (cmd != null) {
            cmd.unregister(commandMap);
            knownCommands.remove(pluginPrefix + lowerLabel);

            // Also remove aliases
            for (String alias : cmd.getAliases()) {
                knownCommands.remove(alias.toLowerCase());
                knownCommands.remove(pluginPrefix + alias.toLowerCase());
            }

            DebugLogger.commandRegistration("Unregistered: " + label);
            return true;
        }

        return false;
    }

    /**
     * @return the Bukkit command map, or null if unavailable
     */
    public static CommandMap getCommandMap() {
        return commandMap;
    }

    /**
     * @return the known commands map, or null if unavailable
     */
    public static Map<String, Command> getKnownCommands() {
        return knownCommands;
    }

    private static void initializeCommandMap() {
        try {
            Server server = Bukkit.getServer();
            Method getCommandMap = server.getClass().getDeclaredMethod("getCommandMap");
            getCommandMap.setAccessible(true);
            commandMap = (CommandMap) getCommandMap.invoke(server);

            if (!SimpleCommandMap.class.isAssignableFrom(commandMap.getClass())) {
                commandMap = null;
                return;
            }

            Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, Command> commands = (Map<String, Command>) knownCommandsField.get(commandMap);
            knownCommands = commands;
        } catch (Exception e) {
            commandMap = null;
            knownCommands = null;
        }
    }

}
