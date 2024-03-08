package io.github.johnnypixelz.utilizer.command;

import io.github.johnnypixelz.utilizer.plugin.Logs;
import io.github.johnnypixelz.utilizer.plugin.Provider;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BukkitCommand extends org.bukkit.command.Command implements PluginIdentifiableCommand {
    private final Command command;


    protected BukkitCommand(Command command) {
        super(command.getLabels().get(0));
        this.command = command;
    }

    @NotNull
    @Override
    public String getDescription() {
        return command.getDescription() != null ? command.getDescription() : "No description available";
    }

    @NotNull
    @Override
    public List<String> getAliases() {
        return command.getLabels().subList(1, command.getLabels().size());
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        Logs.info("Executing command " + commandLabel + " with " + args.length + " args.");
        CommandManager.executeCommand(command, sender, List.of(args));
        return true;
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        Logs.info("Tab Complete");
        Logs.info("Alias: " + alias);
        Logs.info("Args: " + String.join(", ", args));

        Command currentCommand = command;

        final Queue<String> arguments = new LinkedList<>(List.of(args));
        while (!arguments.isEmpty()) {
            final String argument = arguments.poll();

            if (arguments.isEmpty()) {
                return currentCommand.getSubcommands()
                        .stream()
                        .map(Command::getLabels)
                        .flatMap(List::stream)
                        .filter(subcommand -> subcommand.startsWith(argument))
                        .toList();
            }

            final Optional<Command> firstSubcommandFound = currentCommand.getSubcommands()
                    .stream()
                    .filter(subcommand -> subcommand.getLabels().contains(argument))
                    .findFirst();
            if (firstSubcommandFound.isEmpty()) return Collections.emptyList();

            currentCommand = firstSubcommandFound.get();
        }

        return Collections.emptyList();
    }

    @NotNull
    @Override
    public Plugin getPlugin() {
        return Provider.getPlugin();
    }

}
