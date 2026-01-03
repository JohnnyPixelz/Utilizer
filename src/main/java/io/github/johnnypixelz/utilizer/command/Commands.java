package io.github.johnnypixelz.utilizer.command;

import io.github.johnnypixelz.utilizer.command.internal.CommandDefinition;
import io.github.johnnypixelz.utilizer.command.internal.CommandRegistry;
import io.github.johnnypixelz.utilizer.command.internal.DebugLogger;
import io.github.johnnypixelz.utilizer.command.internal.completer.TabCompleter;
import io.github.johnnypixelz.utilizer.command.internal.resolver.ArgumentResolver;

import java.util.List;

/**
 * Static facade for the command system.
 *
 * <h2>Registering Annotation-Based Commands:</h2>
 * <pre>{@code
 * Commands.register(new MyCommand());
 * Commands.register(new AdminCommand(), new HelpCommand());
 * }</pre>
 *
 * <h2>Creating Commands Programmatically:</h2>
 * <pre>{@code
 * Commands.create("greet", "hello")
 *     .description("Greets the sender")
 *     .permission("myplugin.greet")
 *     .executes((sender, args) -> {
 *         sender.sendMessage("Hello, " + (args.isEmpty() ? "World" : args.get(0)) + "!");
 *     })
 *     .subcommand("fancy", sub -> sub
 *         .executes((sender, args) -> sender.sendMessage("✨ Fancy hello! ✨"))
 *     )
 *     .register();
 * }</pre>
 *
 * <h2>Unregistering Commands:</h2>
 * <pre>{@code
 * Commands.unregister("mycommand");  // By label
 * Commands.unregisterAll();          // All commands
 * }</pre>
 *
 * <h2>Custom Argument Resolvers:</h2>
 * <pre>{@code
 * Commands.registerResolver(MyCustomType.class, context -> {
 *     String arg = context.getArgument();
 *     return MyCustomType.parse(arg);
 * });
 * }</pre>
 *
 * @see CommandBase
 * @see ProgrammaticCommand
 */
public final class Commands {

    private static CommandRegistry registry = new CommandRegistry();

    private Commands() {
    }

    // ==================== Registration ====================

    /**
     * Registers one or more annotation-based commands.
     *
     * @param commands the command instances to register
     */
    public static void register(CommandBase... commands) {
        registry.register(commands);
    }

    /**
     * Creates a new programmatic command builder.
     *
     * <p>Use this to create commands without annotations:</p>
     * <pre>{@code
     * Commands.create("mycommand", "mc")
     *     .description("My command")
     *     .executes((sender, args) -> sender.sendMessage("Hello!"))
     *     .register();
     * }</pre>
     *
     * @param label   the primary command label
     * @param aliases additional aliases
     * @return a new programmatic command builder
     */
    public static ProgrammaticCommand create(String label, String... aliases) {
        return ProgrammaticCommand.create(label, aliases);
    }

    // ==================== Unregistration ====================

    /**
     * Unregisters a command by its label.
     *
     * <p>This cleanly removes the command from Bukkit's command map,
     * so it will no longer be executable or appear in tab completion.</p>
     *
     * @param label the command label (or any of its aliases)
     * @return true if the command was found and unregistered
     */
    public static boolean unregister(String label) {
        return registry.unregister(label);
    }

    /**
     * Unregisters a command by its definition.
     *
     * @param definition the command definition
     * @return true if the command was unregistered
     */
    public static boolean unregister(CommandDefinition definition) {
        return registry.unregister(definition);
    }

    /**
     * Unregisters all commands registered through this library.
     *
     * <p>Useful for plugin reload or shutdown cleanup.</p>
     */
    public static void unregisterAll() {
        registry.unregisterAll();
    }

    // ==================== Query ====================

    /**
     * Finds a registered command by its label.
     *
     * @param label the command label
     * @return the command definition, or null if not found
     */
    public static CommandDefinition findCommand(String label) {
        return registry.findCommand(label);
    }

    /**
     * @return all registered commands
     */
    public static List<CommandDefinition> getRegisteredCommands() {
        return registry.getRegisteredCommands();
    }

    // ==================== Resolvers ====================

    /**
     * Registers a custom argument resolver for a type.
     *
     * <p>Resolvers are used to convert command argument strings into typed objects.
     * Built-in resolvers exist for primitives, String, Player, OfflinePlayer, and Enums.</p>
     *
     * @param type     the type to register the resolver for
     * @param resolver the resolver function
     * @param <T>      the type
     */
    public static <T> void registerResolver(Class<T> type, ArgumentResolver<T> resolver) {
        registry.getResolverRegistry().register(type, resolver);
    }

    // ==================== Tab Completers ====================

    /**
     * Registers a custom tab completer.
     *
     * <p>Tab completers provide suggestions for command arguments. Use them with
     * the {@code @CommandCompletion} annotation:</p>
     * <pre>{@code
     * // Register a custom completer
     * Commands.registerCompleter("colors", context ->
     *     Stream.of("red", "green", "blue")
     *         .filter(c -> c.startsWith(context.getPartial().toLowerCase()))
     *         .toList());
     *
     * // Use it in a command
     * @Subcommand("setcolor")
     * @CommandCompletion("@colors")
     * public void setColor(Player player, String color) { }
     * }</pre>
     *
     * <p>Built-in completers include: {@code @players}, {@code @worlds}, {@code @materials},
     * {@code @sounds}, {@code @entities}, {@code @enchantments}, {@code @potions},
     * {@code @gamemodes}, {@code @boolean}, {@code @nothing}, and {@code @range:min-max}.</p>
     *
     * @param id        the completer identifier (without the @ prefix)
     * @param completer the completer function
     */
    public static void registerCompleter(String id, TabCompleter completer) {
        registry.getCompleterRegistry().register(id, completer);
    }

    // ==================== Debug ====================

    /**
     * Enables or disables debug logging for the command system.
     *
     * <p>When enabled, logs detailed information about command registration,
     * execution, subcommand resolution, and argument parsing.</p>
     *
     * @param enabled true to enable debug logging
     */
    public static void setDebugEnabled(boolean enabled) {
        DebugLogger.setEnabled(enabled);
    }

    /**
     * @return true if debug logging is enabled
     */
    public static boolean isDebugEnabled() {
        return DebugLogger.isEnabled();
    }

    // ==================== Internal ====================

    /**
     * Sets the command registry. For testing purposes only.
     *
     * @param registry the registry to use
     */
    static void setRegistry(CommandRegistry registry) {
        Commands.registry = registry;
    }

    /**
     * @return the command registry
     */
    static CommandRegistry getRegistry() {
        return registry;
    }

}
