package io.github.johnnypixelz.utilizer.command;

/**
 * Base class for all commands.
 *
 * <p>Extend this class and use annotations to define your command:</p>
 *
 * <pre>{@code
 * @Label("mycommand|mc")
 * @Description("My awesome command")
 * @Permission("myplugin.mycommand")
 * public class MyCommand extends CommandBase {
 *
 *     @Default
 *     public void execute(Player player) {
 *         player.sendMessage("Hello!");
 *     }
 *
 *     @Subcommand("reload")
 *     @Permission("myplugin.mycommand.reload")
 *     public void reload(CommandSender sender) {
 *         // Reload logic
 *         sender.sendMessage("Reloaded!");
 *     }
 * }
 * }</pre>
 *
 * <p>Then register your command:</p>
 * <pre>{@code
 * Commands.register(new MyCommand());
 * }</pre>
 *
 * <h2>Available Annotations:</h2>
 * <ul>
 *   <li>{@code @Label} - Command name and aliases (e.g., "help|?")</li>
 *   <li>{@code @Subcommand} - Marks a method or class as a subcommand</li>
 *   <li>{@code @Default} - Marks the default method to execute</li>
 *   <li>{@code @Description} - Command description for help</li>
 *   <li>{@code @Permission} - Required permission node</li>
 *   <li>{@code @PermissionMessage} - Custom message when permission denied</li>
 *   <li>{@code @ConfigPermission} - Permission from config file</li>
 *   <li>{@code @PermissionConfigMessage} - Permission message from config</li>
 * </ul>
 *
 * <h2>Method Parameters:</h2>
 * <p>The first parameter can be a CommandSender (or Player, ConsoleCommandSender, etc.).
 * Subsequent parameters are resolved from command arguments.</p>
 *
 * <p>Supported argument types:</p>
 * <ul>
 *   <li>Primitives: int, long, double, float, boolean, etc.</li>
 *   <li>String (last String parameter captures remaining args)</li>
 *   <li>Player, OfflinePlayer</li>
 *   <li>Enums</li>
 *   <li>Custom types via {@code Commands.registerResolver()}</li>
 * </ul>
 *
 * @see Commands#register(CommandBase...)
 * @see io.github.johnnypixelz.utilizer.command.annotations.Label
 * @see io.github.johnnypixelz.utilizer.command.annotations.Subcommand
 * @see io.github.johnnypixelz.utilizer.command.annotations.Default
 */
public abstract class CommandBase {

    // This class is intentionally empty.
    // All command behavior is defined via annotations and methods.
    // This provides a clean extension point for commands.

}
