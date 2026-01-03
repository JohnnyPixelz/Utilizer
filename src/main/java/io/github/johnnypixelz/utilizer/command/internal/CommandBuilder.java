package io.github.johnnypixelz.utilizer.command.internal;

import io.github.johnnypixelz.utilizer.command.permissions.CommandPermission;
import io.github.johnnypixelz.utilizer.command.permissions.CommandPermissionMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fluent builder for creating {@link CommandDefinition} instances.
 */
public final class CommandBuilder {

    private final List<String> labels = new ArrayList<>();
    private String description;
    private CommandMethodDefinition defaultMethod;
    private CommandMethodDefinition unknownMethod;
    private final List<CommandDefinition> subcommands = new ArrayList<>();
    private final List<CommandPermission> permissions = new ArrayList<>();
    private CommandPermissionMessage permissionMessage;
    private CommandDefinition parent;
    private boolean isPrivate;
    private String completionSpec;

    private CommandBuilder() {
    }

    /**
     * Creates a new builder.
     *
     * @return a new builder instance
     */
    public static CommandBuilder create() {
        return new CommandBuilder();
    }

    /**
     * Sets the command labels/aliases.
     *
     * @param labels the labels
     * @return this builder
     */
    public CommandBuilder labels(String... labels) {
        this.labels.clear();
        this.labels.addAll(Arrays.asList(labels));
        return this;
    }

    /**
     * Sets the command labels/aliases.
     *
     * @param labels the labels
     * @return this builder
     */
    public CommandBuilder labels(List<String> labels) {
        this.labels.clear();
        this.labels.addAll(labels);
        return this;
    }

    /**
     * Adds a single label.
     *
     * @param label the label to add
     * @return this builder
     */
    public CommandBuilder addLabel(String label) {
        this.labels.add(label);
        return this;
    }

    /**
     * Sets the command description.
     *
     * @param description the description
     * @return this builder
     */
    public CommandBuilder description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Sets the default method to execute.
     *
     * @param method the method definition
     * @return this builder
     */
    public CommandBuilder defaultMethod(CommandMethodDefinition method) {
        this.defaultMethod = method;
        return this;
    }

    /**
     * Sets the unknown command handler method.
     *
     * @param method the method definition
     * @return this builder
     */
    public CommandBuilder unknownMethod(CommandMethodDefinition method) {
        this.unknownMethod = method;
        return this;
    }

    /**
     * Adds a subcommand.
     *
     * @param subcommand the subcommand definition
     * @return this builder
     */
    public CommandBuilder addSubcommand(CommandDefinition subcommand) {
        this.subcommands.add(subcommand);
        return this;
    }

    /**
     * Adds a permission requirement.
     *
     * @param permission the permission
     * @return this builder
     */
    public CommandBuilder addPermission(CommandPermission permission) {
        this.permissions.add(permission);
        return this;
    }

    /**
     * Sets the custom permission denial message.
     *
     * @param message the message
     * @return this builder
     */
    public CommandBuilder permissionMessage(CommandPermissionMessage message) {
        this.permissionMessage = message;
        return this;
    }

    /**
     * Sets the parent command.
     *
     * @param parent the parent command
     * @return this builder
     */
    public CommandBuilder parent(CommandDefinition parent) {
        this.parent = parent;
        return this;
    }

    /**
     * Sets whether this command is private (hidden from tab completion).
     *
     * @param isPrivate true to hide from tab completion
     * @return this builder
     */
    public CommandBuilder isPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
        return this;
    }

    /**
     * Sets the tab completion specification.
     *
     * @param completionSpec the completion spec (e.g., "@players @materials @range:1-64")
     * @return this builder
     */
    public CommandBuilder completionSpec(String completionSpec) {
        this.completionSpec = completionSpec;
        return this;
    }

    // Getters for CommandDefinition constructor

    List<String> getLabels() {
        return labels;
    }

    String getDescription() {
        return description;
    }

    CommandMethodDefinition getDefaultMethod() {
        return defaultMethod;
    }

    CommandMethodDefinition getUnknownMethod() {
        return unknownMethod;
    }

    List<CommandDefinition> getSubcommands() {
        return subcommands;
    }

    List<CommandPermission> getPermissions() {
        return permissions;
    }

    CommandPermissionMessage getPermissionMessage() {
        return permissionMessage;
    }

    CommandDefinition getParent() {
        return parent;
    }

    boolean isPrivate() {
        return isPrivate;
    }

    String getCompletionSpec() {
        return completionSpec;
    }

    /**
     * Builds the immutable command definition.
     *
     * @return the built command definition
     */
    public CommandDefinition build() {
        return new CommandDefinition(this);
    }

}
