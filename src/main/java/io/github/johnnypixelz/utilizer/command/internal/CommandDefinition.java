package io.github.johnnypixelz.utilizer.command.internal;

import io.github.johnnypixelz.utilizer.command.CommandMessage;
import io.github.johnnypixelz.utilizer.command.CommandMessageManager;
import io.github.johnnypixelz.utilizer.command.permissions.CommandPermission;
import io.github.johnnypixelz.utilizer.command.permissions.CommandPermissionMessage;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Immutable representation of a parsed command.
 * Created via {@link CommandBuilder}.
 */
public final class CommandDefinition {

    private final List<String> labels;
    private final String description;
    private final CommandMethodDefinition defaultMethod;
    private final CommandMethodDefinition unknownMethod;
    private final List<CommandDefinition> subcommands;
    private final List<CommandPermission> permissions;
    private final CommandPermissionMessage permissionMessage;
    private final CommandDefinition parent;
    private final SyntaxGenerator syntax;
    private final boolean isPrivate;
    private final String completionSpec;

    CommandDefinition(CommandBuilder builder) {
        this.labels = Collections.unmodifiableList(new ArrayList<>(builder.getLabels()));
        this.description = builder.getDescription();
        this.defaultMethod = builder.getDefaultMethod();
        this.unknownMethod = builder.getUnknownMethod();
        this.subcommands = Collections.unmodifiableList(new ArrayList<>(builder.getSubcommands()));
        this.permissions = Collections.unmodifiableList(new ArrayList<>(builder.getPermissions()));
        this.permissionMessage = builder.getPermissionMessage();
        this.parent = builder.getParent();
        this.syntax = SyntaxGenerator.forCommand(this);
        this.isPrivate = builder.isPrivate();
        this.completionSpec = builder.getCompletionSpec();
    }

    /**
     * @return all labels/aliases for this command
     */
    public List<String> getLabels() {
        return labels;
    }

    /**
     * @return the primary label (first one)
     */
    public String getPrimaryLabel() {
        return labels.isEmpty() ? "" : labels.get(0);
    }

    /**
     * @return the command description, or null if none set
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the default method to execute, or null if none
     */
    public CommandMethodDefinition getDefaultMethod() {
        return defaultMethod;
    }

    /**
     * @return the unknown command handler method, or null if none
     */
    public CommandMethodDefinition getUnknownMethod() {
        return unknownMethod;
    }

    /**
     * @return all subcommands
     */
    public List<CommandDefinition> getSubcommands() {
        return subcommands;
    }

    /**
     * @return all required permissions
     */
    public List<CommandPermission> getPermissions() {
        return permissions;
    }

    /**
     * @return the custom permission message, or null if using default
     */
    public CommandPermissionMessage getPermissionMessage() {
        return permissionMessage;
    }

    /**
     * @return the parent command, or null if this is a root command
     */
    public CommandDefinition getParent() {
        return parent;
    }

    /**
     * @return the syntax generator for this command
     */
    public SyntaxGenerator getSyntax() {
        return syntax;
    }

    /**
     * @return true if this command is private (hidden from tab completion)
     */
    public boolean isPrivate() {
        return isPrivate;
    }

    /**
     * @return the completion specification string, or null if none set
     */
    public String getCompletionSpec() {
        return completionSpec;
    }

    /**
     * Checks if the sender has all required permissions.
     *
     * @param sender the command sender
     * @return true if permitted
     */
    public boolean isPermitted(CommandSender sender) {
        for (CommandPermission permission : permissions) {
            if (!sender.hasPermission(permission.getPermission())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks permission and sends a denial message if not permitted.
     *
     * @param sender the command sender
     * @return true if permitted, false if denied
     */
    public boolean checkPermissionAndNotify(CommandSender sender) {
        for (CommandPermission permission : permissions) {
            if (!sender.hasPermission(permission.getPermission())) {
                permission.getPermissionMessage()
                        .or(() -> Optional.ofNullable(permissionMessage))
                        .map(CommandPermissionMessage::getMessage)
                        .orElse(CommandMessageManager.getMessage(CommandMessage.NO_PERMISSION))
                        .send(sender);
                return false;
            }
        }
        return true;
    }

    /**
     * Finds a subcommand by label.
     *
     * @param label the label to search for
     * @return the subcommand, or empty if not found
     */
    public Optional<CommandDefinition> findSubcommand(String label) {
        String lowerLabel = label.toLowerCase();
        return subcommands.stream()
                .filter(sub -> sub.getLabels().stream()
                        .anyMatch(l -> l.equalsIgnoreCase(lowerLabel)))
                .findFirst();
    }

}
