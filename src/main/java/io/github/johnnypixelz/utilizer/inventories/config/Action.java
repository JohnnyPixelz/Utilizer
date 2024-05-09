package io.github.johnnypixelz.utilizer.inventories.config;

import io.github.johnnypixelz.utilizer.inventories.CustomInventory;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Optional;
import java.util.stream.Collectors;

public class Action {

    public static Optional<Action> parse(@Nullable String input) {
        if (input == null) return Optional.empty();

        final Deque<String> arguments = Arrays.stream(input.split(" "))
                .collect(Collectors.toCollection(ArrayDeque::new));

        if (arguments.isEmpty()) return Optional.empty();

        String actionId = arguments.removeFirst();

        final Optional<ActionType> optionalActionType = ActionType.getActionByAlias(actionId);
        if (optionalActionType.isEmpty()) return Optional.empty();

        final ActionType actionType = optionalActionType.get();
        if (actionType.getMinimumArguments() > arguments.size()) return Optional.empty();

        return Optional.of(new Action(actionType, String.join(" ", arguments)));
    }

    private final ActionType actionType;
    private final String arguments;

    public Action(ActionType actionType, String arguments) {
        this.actionType = actionType;
        this.arguments = arguments;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public String getArguments() {
        return arguments;
    }

    public void execute(CustomInventory customInventory, Player player) {
        final ActionContext actionContext = new ActionContext(customInventory, player, arguments);
        actionType.getExecutor().accept(actionContext);
    }

}
