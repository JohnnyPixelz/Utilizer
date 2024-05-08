package io.github.johnnypixelz.utilizer.inventory.config;

import io.github.johnnypixelz.utilizer.depend.Dependencies;
import org.bukkit.Bukkit;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public enum ActionType {

    CLOSE_INVENTORY(List.of("close", "closeinv", "closeinventory"), 0, actionContext -> {
        actionContext.getPlayer().closeInventory();
    }),
    PERFORM_COMMAND(List.of("cmd", "command"), 1, actionContext -> {
        final String command = actionContext.getArguments()
                .replace("%player%", actionContext.getPlayer().getName());

        final String placeholderedCommand = Dependencies.getPlaceholderAPI()
                .map(papi -> papi.setPlaceholders(actionContext.getPlayer(), command))
                .orElse(command);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), placeholderedCommand);
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
