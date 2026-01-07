package io.github.johnnypixelz.utilizer.inventories.config;

import io.github.johnnypixelz.utilizer.depend.Placeholders;
import io.github.johnnypixelz.utilizer.inventories.inventories.ConfigInventory;
import io.github.johnnypixelz.utilizer.text.Colors;
import org.bukkit.Bukkit;

import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public enum ActionType {

    CLOSE_INVENTORY(List.of("close", "closeinv", "closeinventory"), 0, actionContext -> {
        actionContext.getPlayer().closeInventory();
    }),
    OPEN_INVENTORY(List.of("open", "openinv", "openinventory"), 2, actionContext -> {
        final String arguments = actionContext.getArguments();
        final String[] args = arguments.split(" ");

        ConfigInventory.from(args[0], args[1])
                .open(actionContext.getPlayer());
    }),
    OPEN_CHILD_INVENTORY(List.of("openc", "openchild", "opencinv", "openchildinv", "openchildinventory"), 2, actionContext -> {
        final String arguments = actionContext.getArguments();
        final String[] args = arguments.split(" ");

        ConfigInventory.from(args[0], args[1])
                .openParentInventoryOnClose(actionContext.getInventory())
                .open(actionContext.getPlayer());
    }),
    PERFORM_CONSOLE_COMMAND(List.of("cmd", "command"), 1, actionContext -> {
        final String command = Placeholders.set(actionContext.getPlayer(), actionContext.getArguments());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }),
    PERFORM_PLAYER_COMMAND(List.of("playercmd", "playercommand"), 1, actionContext -> {
        final String command = Placeholders.set(actionContext.getPlayer(), actionContext.getArguments());
        Bukkit.dispatchCommand(actionContext.getPlayer(), command);
    }),
    MESSAGE(List.of("msg", "message", "text"), 1, actionContext -> {
        final String message = Placeholders.set(actionContext.getPlayer(), actionContext.getArguments());
        actionContext.getPlayer().sendMessage(Colors.color(message));
    });

    public static Optional<ActionType> getActionByAlias(@Nullable String alias) {
        if (alias == null) return Optional.empty();

        for (ActionType actionType : values()) {
            for (String actionAlias : actionType.getAliases()) {
                if (alias.equalsIgnoreCase(actionAlias)) {
                    return Optional.of(actionType);
                }
            }
        }

        return Optional.empty();
    }

    private final List<String> aliases;
    private final int minimumArguments;
    private final Consumer<ActionContext> executor;

    ActionType(List<String> aliases, int minimumArguments, Consumer<ActionContext> executor) {
        this.aliases = aliases;
        this.minimumArguments = minimumArguments;
        this.executor = executor;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public int getMinimumArguments() {
        return minimumArguments;
    }

    public Consumer<ActionContext> getExecutor() {
        return executor;
    }

}
