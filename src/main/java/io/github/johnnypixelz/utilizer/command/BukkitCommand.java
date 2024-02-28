package io.github.johnnypixelz.utilizer.command;

import io.github.johnnypixelz.utilizer.plugin.Logs;
import io.github.johnnypixelz.utilizer.plugin.Provider;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BukkitCommand extends org.bukkit.command.Command implements PluginIdentifiableCommand {
    private final Command command;


    protected BukkitCommand(Command command) {
        super(command.getLabels().get(0));
        this.command = command;
    }

    @NotNull
    @Override
    public String getDescription() {
        return command.getDescription() != null ? command.getDescription() : "No description available";
    }

    @NotNull
    @Override
    public List<String> getAliases() {
        return command.getLabels().subList(1, command.getLabels().size());
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        Logs.info("Executing command " + commandLabel + " with " + args.length + " args.");
        CommandManager.executeCommand(command, sender, List.of(args));
        return true;
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        return super.tabComplete(sender, alias, args);
    }

    @NotNull
    @Override
    public Plugin getPlugin() {
        return Provider.getPlugin();
    }

}
