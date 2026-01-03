package io.github.johnnypixelz.utilizer.command.internal.completer;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry for tab completion handlers.
 *
 * <p>Completers are registered with IDs (like "players", "worlds") and can be
 * referenced in {@code @CommandCompletion} annotations using the @ prefix.</p>
 */
public final class TabCompleterRegistry {

    private final Map<String, TabCompleter> completers = new HashMap<>();

    public TabCompleterRegistry() {
        BuiltinCompleters.registerAll(this);
    }

    /**
     * Registers a tab completer.
     *
     * @param id        the completer ID (e.g., "players", "worlds")
     * @param completer the completer
     */
    public void register(String id, TabCompleter completer) {
        completers.put(id.toLowerCase(), completer);
    }

    /**
     * Gets a tab completer by ID.
     *
     * @param id the completer ID
     * @return the completer, or null if not found
     */
    public TabCompleter getCompleter(String id) {
        return completers.get(id.toLowerCase());
    }

    /**
     * Checks if a completer exists.
     *
     * @param id the completer ID
     * @return true if registered
     */
    public boolean hasCompleter(String id) {
        return completers.containsKey(id.toLowerCase());
    }

    /**
     * Executes a completion.
     *
     * @param id      the completer ID
     * @param context the completion context
     * @return the completions, or empty list if completer not found
     */
    public List<String> complete(String id, TabCompleterContext context) {
        TabCompleter completer = getCompleter(id);
        if (completer == null) {
            return Collections.emptyList();
        }
        return completer.complete(context);
    }

}
