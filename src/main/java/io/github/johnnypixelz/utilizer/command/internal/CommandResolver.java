package io.github.johnnypixelz.utilizer.command.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Resolves the target command by traversing the subcommand tree.
 */
public final class CommandResolver {

    private CommandResolver() {
    }

    /**
     * Result of command resolution containing the target command and remaining arguments.
     */
    public record Result(CommandDefinition command, List<String> remainingArgs) {
    }

    /**
     * Resolves the deepest matching command from the argument list.
     *
     * @param root the root command
     * @param args the command arguments
     * @return the resolution result
     */
    public static Result resolve(CommandDefinition root, List<String> args) {
        CommandDefinition current = root;
        List<String> remaining = new ArrayList<>(args);

        while (!remaining.isEmpty()) {
            String label = remaining.get(0).toLowerCase();

            DebugLogger.subcommandSearch(label, current.getPrimaryLabel());

            Optional<CommandDefinition> subcommand = current.findSubcommand(label);

            if (subcommand.isEmpty()) {
                DebugLogger.subcommandNotFound(label, current.getPrimaryLabel());
                break;
            }

            DebugLogger.subcommandFound(label);
            current = subcommand.get();
            remaining = remaining.subList(1, remaining.size());
        }

        return new Result(current, remaining);
    }

}
