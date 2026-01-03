package io.github.johnnypixelz.utilizer.command.internal;

import io.github.johnnypixelz.utilizer.command.CommandBase;
import io.github.johnnypixelz.utilizer.command.exceptions.CommandAnnotationParseException;
import io.github.johnnypixelz.utilizer.command.internal.completer.BuiltinCompleters;
import io.github.johnnypixelz.utilizer.command.internal.completer.TabCompleterRegistry;
import io.github.johnnypixelz.utilizer.command.internal.resolver.ArgumentResolverRegistry;
import io.github.johnnypixelz.utilizer.plugin.Logs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Registry for commands. Manages command registration and lookup.
 */
public final class CommandRegistry {

    private final List<CommandDefinition> registeredCommands = new ArrayList<>();
    private final ArgumentResolverRegistry resolverRegistry;
    private final TabCompleterRegistry completerRegistry;
    private final AnnotationParser parser;

    public CommandRegistry() {
        this.resolverRegistry = new ArgumentResolverRegistry();
        this.completerRegistry = new TabCompleterRegistry();
        BuiltinCompleters.registerAll(completerRegistry);
        this.parser = new AnnotationParser(this);
    }

    /**
     * Constructor for testing with custom registries.
     */
    CommandRegistry(ArgumentResolverRegistry resolverRegistry, TabCompleterRegistry completerRegistry) {
        this.resolverRegistry = resolverRegistry;
        this.completerRegistry = completerRegistry;
        this.parser = new AnnotationParser(this);
    }

    /**
     * Registers one or more command instances.
     *
     * @param commands the command instances to register
     */
    public void register(CommandBase... commands) {
        for (CommandBase command : commands) {
            try {
                CommandDefinition definition = parser.parse(command);
                registerInternal(definition);
            } catch (CommandAnnotationParseException e) {
                Logs.severe("Failed to register command: " + command.getClass().getCanonicalName());
                Logs.severe(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Registers a pre-parsed command definition.
     * Used internally by AnnotationParser for nested commands and by ProgrammaticCommand.
     *
     * @param definition the command definition
     */
    public void registerInternal(CommandDefinition definition) {
        registeredCommands.add(definition);
        BukkitCommandBridge.register(definition, resolverRegistry, completerRegistry);
    }

    /**
     * Unregisters a command by its definition.
     *
     * @param definition the command definition to unregister
     * @return true if the command was unregistered successfully
     */
    public boolean unregister(CommandDefinition definition) {
        boolean removed = registeredCommands.remove(definition);
        if (removed) {
            BukkitCommandBridge.unregister(definition);
        }
        return removed;
    }

    /**
     * Unregisters a command by its label.
     *
     * @param label the command label
     * @return true if the command was unregistered successfully
     */
    public boolean unregister(String label) {
        String lowerLabel = label.toLowerCase();

        CommandDefinition toRemove = registeredCommands.stream()
                .filter(def -> def.getLabels().stream()
                        .anyMatch(l -> l.equalsIgnoreCase(lowerLabel)))
                .findFirst()
                .orElse(null);

        if (toRemove != null) {
            registeredCommands.remove(toRemove);
            BukkitCommandBridge.unregister(toRemove);
            return true;
        }

        return false;
    }

    /**
     * Unregisters all commands registered through this registry.
     */
    public void unregisterAll() {
        for (CommandDefinition definition : new ArrayList<>(registeredCommands)) {
            BukkitCommandBridge.unregister(definition);
        }
        registeredCommands.clear();
    }

    /**
     * Finds a registered command by its label.
     *
     * @param label the command label
     * @return the command definition, or null if not found
     */
    public CommandDefinition findCommand(String label) {
        String lowerLabel = label.toLowerCase();
        return registeredCommands.stream()
                .filter(def -> def.getLabels().stream()
                        .anyMatch(l -> l.equalsIgnoreCase(lowerLabel)))
                .findFirst()
                .orElse(null);
    }

    /**
     * @return all registered commands (unmodifiable)
     */
    public List<CommandDefinition> getRegisteredCommands() {
        return Collections.unmodifiableList(registeredCommands);
    }

    /**
     * @return the resolver registry
     */
    public ArgumentResolverRegistry getResolverRegistry() {
        return resolverRegistry;
    }

    /**
     * @return the tab completer registry
     */
    public TabCompleterRegistry getCompleterRegistry() {
        return completerRegistry;
    }

}
