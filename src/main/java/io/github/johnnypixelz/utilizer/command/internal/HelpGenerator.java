package io.github.johnnypixelz.utilizer.command.internal;

import io.github.johnnypixelz.utilizer.command.annotations.GenerateHelp;
import io.github.johnnypixelz.utilizer.text.Colors;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates help messages for commands with @GenerateHelp.
 */
public final class HelpGenerator {

    private HelpGenerator() {
    }

    /**
     * Sends the help message for a command to the sender.
     *
     * @param command the command definition
     * @param sender  the command sender
     */
    public static void sendHelp(CommandDefinition command, CommandSender sender) {
        GenerateHelp config = command.getGenerateHelp();
        if (config == null) {
            return;
        }

        String rootLabel = getRootLabel(command);
        String commandPath = getCommandPath(command);

        // Send header
        String header = config.header()
                .replace("%command%", commandPath);
        sender.sendMessage(Colors.color(header));

        // Collect available subcommands
        List<CommandDefinition> available = command.getSubcommands().stream()
                .filter(sub -> sub.isPermitted(sender))
                .filter(sub -> !sub.isPrivate())
                .toList();

        if (available.isEmpty() && command.getDefaultMethod() == null) {
            sender.sendMessage(Colors.color(config.noCommands()));
            return;
        }

        // Show default method syntax if exists
        if (command.getDefaultMethod() != null) {
            String syntax = command.getSyntax().generate();
            sender.sendMessage(Colors.color("  &e" + syntax));
        }

        // Show subcommands
        for (CommandDefinition sub : available) {
            String description = sub.getDescription() != null ? sub.getDescription() : "No description";
            String subLabel = sub.getPrimaryLabel();

            // Build syntax for subcommand
            String subSyntax = "";
            if (sub.getDefaultMethod() != null) {
                SyntaxGenerator subSyntaxGen = sub.getSyntax();
                List<String> params = subSyntaxGen.getParameterNames();
                if (!params.isEmpty()) {
                    subSyntax = " " + String.join(" ", params.stream()
                            .map(p -> "<" + p + ">")
                            .toList());
                }
            }

            String line = config.format()
                    .replace("%command%", commandPath)
                    .replace("%subcommand%", subLabel + subSyntax)
                    .replace("%syntax%", subSyntax)
                    .replace("%description%", description);
            sender.sendMessage(Colors.color(line));
        }

        // Show help command itself
        String helpLine = config.format()
                .replace("%command%", commandPath)
                .replace("%subcommand%", "help")
                .replace("%syntax%", "")
                .replace("%description%", "Shows this help message");
        sender.sendMessage(Colors.color(helpLine));
    }

    /**
     * Gets the full command path (e.g., "parent subcommand").
     */
    private static String getCommandPath(CommandDefinition command) {
        List<String> parts = new ArrayList<>();
        CommandDefinition current = command;
        while (current != null) {
            parts.add(0, current.getPrimaryLabel());
            current = current.getParent();
        }
        return String.join(" ", parts);
    }

    /**
     * Gets the root command label.
     */
    private static String getRootLabel(CommandDefinition command) {
        CommandDefinition current = command;
        while (current.getParent() != null) {
            current = current.getParent();
        }
        return current.getPrimaryLabel();
    }

}
