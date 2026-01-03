package io.github.johnnypixelz.utilizer.command;

import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Functional interface for handling command execution programmatically.
 *
 * @param <T> the sender type (CommandSender, Player, etc.)
 */
@FunctionalInterface
public interface CommandHandler<T extends CommandSender> {

    /**
     * Handles command execution.
     *
     * @param sender the command sender
     * @param args   the command arguments
     */
    void execute(T sender, List<String> args);

}
