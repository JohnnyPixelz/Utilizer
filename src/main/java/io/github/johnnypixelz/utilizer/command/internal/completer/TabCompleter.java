package io.github.johnnypixelz.utilizer.command.internal.completer;

import java.util.List;

/**
 * Interface for tab completion handlers.
 *
 * <p>Tab completers provide suggestions for command arguments during tab completion.
 * They are registered with IDs like "players", "worlds", "materials" and referenced
 * in {@code @CommandCompletion} annotations.</p>
 *
 * <h2>Example:</h2>
 * <pre>{@code
 * Commands.registerCompleter("myitems", ctx -> {
 *     return myItemList.stream()
 *         .filter(item -> item.startsWith(ctx.getPartial()))
 *         .toList();
 * });
 * }</pre>
 */
@FunctionalInterface
public interface TabCompleter {

    /**
     * Provides tab completion suggestions.
     *
     * @param context the completion context
     * @return list of suggestions (may be empty, never null)
     */
    List<String> complete(TabCompleterContext context);

}
