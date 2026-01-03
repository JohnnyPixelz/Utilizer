package io.github.johnnypixelz.utilizer.command.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a command method to execute asynchronously off the main server thread.
 *
 * <p>Use this annotation for commands that perform blocking operations like:</p>
 * <ul>
 *   <li>Database queries</li>
 *   <li>HTTP requests</li>
 *   <li>File I/O operations</li>
 *   <li>Any long-running computations</li>
 * </ul>
 *
 * <h2>Important:</h2>
 * <p>When running async, you cannot directly interact with Bukkit API that requires
 * main thread access. Use {@code Scheduler.sync()} to switch back to the main thread
 * when needed.</p>
 *
 * <h2>Example:</h2>
 * <pre>{@code
 * @Subcommand("backup")
 * @Async
 * public void backup(CommandSender sender) {
 *     sender.sendMessage("Starting backup...");
 *
 *     // This runs off the main thread
 *     performLongBackupOperation();
 *
 *     // Switch back to main thread for Bukkit API calls
 *     Scheduler.sync(() -> {
 *         sender.sendMessage("Backup complete!");
 *     });
 * }
 *
 * @Subcommand("stats")
 * @Async
 * public void stats(Player player) {
 *     // Query database asynchronously
 *     PlayerStats stats = database.getStats(player.getUniqueId());
 *
 *     Scheduler.sync(() -> {
 *         player.sendMessage("Your stats: " + stats);
 *     });
 * }
 * }</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Async {

}
