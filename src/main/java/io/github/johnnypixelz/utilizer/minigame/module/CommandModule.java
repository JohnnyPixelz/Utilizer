package io.github.johnnypixelz.utilizer.minigame.module;

import io.github.johnnypixelz.utilizer.event.StatefulEventEmitter;
import io.github.johnnypixelz.utilizer.minigame.MinigameModule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CommandModule extends MinigameModule {
    private final StatefulEventEmitter<Player> onBlockedCommand;
    private final List<String> whitelistedCommands;
    private final Map<String, Consumer<Player>> commands;

    public CommandModule(String... whitelistedCommands) {
        this();
        whitelistCommands(whitelistedCommands);
    }

    public CommandModule() {
        this.whitelistedCommands = new ArrayList<>();
        this.onBlockedCommand = new StatefulEventEmitter<>();
        this.commands = new HashMap<>();
    }

    public CommandModule whitelistCommand(String command) {
        command = command.toLowerCase();
        if (!whitelistedCommands.contains(command)) {
            whitelistedCommands.add(command);
        }

        return this;
    }

    public CommandModule whitelistCommands(String... command) {
        for (String cmd : command) {
            whitelistCommand(cmd);
        }

        return this;
    }

    public CommandModule onBlockedCommand(Consumer<Player> onBlockedCommand) {
        this.onBlockedCommand.listen(onBlockedCommand);
        return this;
    }

    public CommandModule onCommand(String command, Consumer<Player> consumer) {
        commands.put(command.toLowerCase().trim(), consumer);
        return this;
    }

    public CommandModule addLeaveCommand() {
        onCommand("leave", player -> {
            getMinigame().remove(player);
        });
        return this;
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    private void onCommand(PlayerCommandPreprocessEvent event) {
        if (!isInMinigame(event.getPlayer())) return;

        String command = event.getMessage().toLowerCase().trim().substring(1);
        if (commands.containsKey(command)) {
            commands.get(command).accept(event.getPlayer());
            event.setCancelled(true);
            return;
        }

        for (String whitelistedCommand : whitelistedCommands) {
            if (command.startsWith(whitelistedCommand)) return;
        }

        event.setCancelled(true);
        onBlockedCommand.emit(event.getPlayer());
    }
}
