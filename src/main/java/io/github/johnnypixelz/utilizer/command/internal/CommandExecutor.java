package io.github.johnnypixelz.utilizer.command.internal;

import io.github.johnnypixelz.utilizer.command.CommandMessage;
import io.github.johnnypixelz.utilizer.command.CommandMessageManager;
import io.github.johnnypixelz.utilizer.command.annotations.Cooldown;
import io.github.johnnypixelz.utilizer.command.exceptions.NotEnoughArgumentsException;
import io.github.johnnypixelz.utilizer.command.exceptions.UnsupportedCommandArgumentException;
import io.github.johnnypixelz.utilizer.command.internal.resolver.ArgumentResolutionException;
import io.github.johnnypixelz.utilizer.command.internal.resolver.ArgumentResolverRegistry;
import io.github.johnnypixelz.utilizer.plugin.Logs;
import io.github.johnnypixelz.utilizer.Scheduler;
import io.github.johnnypixelz.utilizer.config.Configs;
import io.github.johnnypixelz.utilizer.text.Colors;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

/**
 * Executes commands with proper error handling.
 */
public final class CommandExecutor {

    private static final Pattern QUOTED_ARGS_PATTERN = Pattern.compile("\".+?\"|[^ ]+");

    private final ArgumentResolverRegistry resolverRegistry;

    public CommandExecutor(ArgumentResolverRegistry resolverRegistry) {
        this.resolverRegistry = resolverRegistry;
    }

    /**
     * Executes a command.
     *
     * @param command the command definition to execute
     * @param sender  the command sender
     * @param args    the raw arguments
     */
    public void execute(CommandDefinition command, CommandSender sender, List<String> args) {
        DebugLogger.commandExecution(command.getPrimaryLabel(), args.size());

        // Check root command permission
        if (!command.checkPermissionAndNotify(sender)) {
            return;
        }

        // Parse quoted arguments
        List<String> parsedArgs = parseQuotedArguments(args);

        // Resolve to deepest subcommand
        CommandResolver.Result resolved = CommandResolver.resolve(command, parsedArgs);
        CommandDefinition targetCommand = resolved.command();
        List<String> remainingArgs = resolved.remainingArgs();

        // Check subcommand permission (if different from root)
        if (targetCommand != command && !targetCommand.checkPermissionAndNotify(sender)) {
            return;
        }

        // Get the method to execute
        CommandMethodDefinition method = targetCommand.getDefaultMethod();

        // If no default method, check for unknown handler or suggest similar subcommands
        if (method == null) {
            // Try to find unknown handler in the target command or its parents
            CommandMethodDefinition unknownMethod = findUnknownHandler(targetCommand);
            if (unknownMethod != null) {
                executeUnknownHandler(unknownMethod, sender, remainingArgs);
                return;
            }

            // Try fuzzy matching if there are remaining args that might be a mistyped subcommand
            if (!remainingArgs.isEmpty()) {
                String attempted = remainingArgs.get(0);
                List<String> subcommandLabels = targetCommand.getSubcommands().stream()
                        .filter(sub -> sub.isPermitted(sender))
                        .flatMap(sub -> sub.getLabels().stream())
                        .toList();

                FuzzyMatcher.suggestMessage(subcommandLabels, attempted)
                        .ifPresent(suggestion -> sender.sendMessage(Colors.color("&e" + suggestion)));
            }

            sendUnknownCommandMessage(sender);
            return;
        }

        // Execute the method with cooldown and async handling
        executeMethod(method, targetCommand, sender, remainingArgs);
    }

    /**
     * Finds the unknown command handler for a command, checking parent commands if needed.
     */
    private CommandMethodDefinition findUnknownHandler(CommandDefinition command) {
        CommandDefinition current = command;
        while (current != null) {
            if (current.getUnknownMethod() != null) {
                return current.getUnknownMethod();
            }
            current = current.getParent();
        }
        return null;
    }

    /**
     * Executes the unknown command handler.
     */
    private void executeUnknownHandler(CommandMethodDefinition method, CommandSender sender, List<String> args) {
        try {
            String[] argsArray = args.toArray(new String[0]);
            method.getMethod().invoke(method.getInstance(), sender, argsArray);
        } catch (Exception e) {
            Logs.severe("Failed to execute unknown command handler: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Executes a command method with cooldown and async support.
     */
    private void executeMethod(CommandMethodDefinition method, CommandDefinition command, CommandSender sender, List<String> args) {
        // Check cooldown
        Cooldown cooldown = method.getCooldown();
        if (cooldown != null && !checkCooldown(cooldown, command, sender)) {
            return;
        }

        // Execute (async or sync)
        Runnable execution = () -> {
            try {
                method.invoke(sender, args, resolverRegistry);

                // Set cooldown after successful execution
                if (cooldown != null) {
                    setCooldown(cooldown, command, sender);
                }
            } catch (NotEnoughArgumentsException e) {
                CommandMessageManager.getMessage(CommandMessage.NOT_ENOUGH_ARGUMENTS).send(sender);
                sender.sendMessage(command.getSyntax().generate());
            } catch (UnsupportedCommandArgumentException e) {
                CommandMessageManager.getMessage(CommandMessage.INTERNAL_ERROR).send(sender);
                Logs.severe("Unsupported argument type: " + e.getMessage());
                e.printStackTrace();
            } catch (ArgumentResolutionException e) {
                sender.sendMessage(Colors.color("&c" + e.getMessage()));
            }
        };

        if (method.isAsync()) {
            Scheduler.async(execution);
        } else {
            execution.run();
        }
    }

    /**
     * Checks if the sender can execute due to cooldown.
     *
     * @return true if allowed, false if on cooldown
     */
    private boolean checkCooldown(Cooldown cooldown, CommandDefinition command, CommandSender sender) {
        // Check bypass permission
        if (!cooldown.bypass().isEmpty() && sender.hasPermission(cooldown.bypass())) {
            return true;
        }

        String commandId = getCommandId(command);
        CooldownManager manager = CooldownManager.getInstance();

        boolean onCooldown;
        long remaining;

        if (cooldown.global()) {
            onCooldown = manager.isOnGlobalCooldown(commandId);
            remaining = manager.getGlobalRemainingTime(commandId);
        } else {
            if (!(sender instanceof Player player)) {
                return true; // Console has no cooldown
            }
            UUID playerId = player.getUniqueId();
            onCooldown = manager.isOnCooldown(playerId, commandId);
            remaining = manager.getRemainingTime(playerId, commandId);
        }

        if (onCooldown) {
            sendCooldownMessage(cooldown, sender, remaining);
            return false;
        }

        return true;
    }

    /**
     * Sets the cooldown after successful command execution.
     */
    private void setCooldown(Cooldown cooldown, CommandDefinition command, CommandSender sender) {
        String commandId = getCommandId(command);
        CooldownManager manager = CooldownManager.getInstance();

        if (cooldown.global()) {
            manager.setGlobalCooldown(commandId, cooldown.value());
        } else if (sender instanceof Player player) {
            manager.setCooldown(player.getUniqueId(), commandId, cooldown.value());
        }
    }

    /**
     * Sends the cooldown message to the sender.
     */
    private void sendCooldownMessage(Cooldown cooldown, CommandSender sender, long remainingSeconds) {
        String message;

        // Try config message first
        if (!cooldown.messageConfig().isEmpty() && !cooldown.messagePath().isEmpty()) {
            message = Configs.load(cooldown.messageConfig()).getConfig().getString(cooldown.messagePath());
        }
        // Then try literal message
        else if (!cooldown.message().isEmpty()) {
            message = cooldown.message();
        }
        // Fall back to default
        else {
            message = CommandMessageManager.getMessage(CommandMessage.ON_COOLDOWN).getMessage();
            if (message == null || message.isEmpty()) {
                message = "&cYou must wait %time% seconds before using this command again.";
            }
        }

        // Replace placeholder
        message = message.replace("%time%", String.valueOf(remainingSeconds));
        sender.sendMessage(Colors.color(message));
    }

    /**
     * Creates a unique identifier for a command.
     */
    private String getCommandId(CommandDefinition command) {
        StringBuilder id = new StringBuilder();
        CommandDefinition current = command;
        while (current != null) {
            if (!id.isEmpty()) {
                id.insert(0, ":");
            }
            id.insert(0, current.getPrimaryLabel());
            current = current.getParent();
        }
        return id.toString();
    }

    /**
     * Parses arguments, handling quoted strings as single arguments.
     *
     * @param args the raw arguments
     * @return the parsed arguments
     */
    private List<String> parseQuotedArguments(List<String> args) {
        if (args.isEmpty()) {
            return args;
        }

        String sentence = String.join(" ", args);
        return QUOTED_ARGS_PATTERN.matcher(sentence)
                .results()
                .map(MatchResult::group)
                .map(String::trim)
                .map(s -> {
                    // Remove surrounding quotes if present
                    if (s.startsWith("\"") && s.endsWith("\"") && s.length() > 1) {
                        return s.substring(1, s.length() - 1);
                    }
                    return s;
                })
                .toList();
    }

    private void sendUnknownCommandMessage(CommandSender sender) {
        String message = Bukkit.spigot().getConfig()
                .getString("messages.unknown-command", "Unknown command. Type \"/help\" for help.");
        sender.sendMessage(Colors.color(message));
    }

}
