package io.github.johnnypixelz.utilizer.command;

import io.github.johnnypixelz.utilizer.command.internal.CommandBuilder;
import io.github.johnnypixelz.utilizer.command.internal.CommandDefinition;
import io.github.johnnypixelz.utilizer.command.internal.ProgrammaticMethodDefinition;
import io.github.johnnypixelz.utilizer.command.permissions.CommandPermission;
import io.github.johnnypixelz.utilizer.command.permissions.CommandPermissionMessage;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Fluent builder for creating commands programmatically without annotations.
 *
 * <h2>Example Usage:</h2>
 * <pre>{@code
 * Commands.create("greet", "hello", "hi")
 *     .description("Greets the sender")
 *     .permission("myplugin.greet")
 *     .executes((sender, args) -> {
 *         String name = args.isEmpty() ? "World" : args.get(0);
 *         sender.sendMessage("Hello, " + name + "!");
 *     })
 *     .subcommand("fancy", sub -> sub
 *         .description("Fancy greeting")
 *         .executes((sender, args) -> {
 *             sender.sendMessage("✨ Greetings! ✨");
 *         })
 *     )
 *     .register();
 * }</pre>
 */
public final class ProgrammaticCommand {

    private final List<String> labels = new ArrayList<>();
    private String description;
    private final List<CommandPermission> permissions = new ArrayList<>();
    private CommandPermissionMessage permissionMessage;
    private CommandHandler<CommandSender> handler;
    private Class<? extends CommandSender> senderType = CommandSender.class;
    private final List<ProgrammaticCommand> subcommands = new ArrayList<>();
    private ProgrammaticCommand parent;

    private ProgrammaticCommand() {
    }

    /**
     * Creates a new programmatic command builder.
     *
     * @param label   the primary command label
     * @param aliases additional aliases
     * @return a new builder
     */
    public static ProgrammaticCommand create(String label, String... aliases) {
        ProgrammaticCommand cmd = new ProgrammaticCommand();
        cmd.labels.add(label);
        cmd.labels.addAll(Arrays.asList(aliases));
        return cmd;
    }

    /**
     * Sets the command description.
     *
     * @param description the description
     * @return this builder
     */
    public ProgrammaticCommand description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Adds a required permission.
     *
     * @param permission the permission node
     * @return this builder
     */
    public ProgrammaticCommand permission(String permission) {
        this.permissions.add(CommandPermission.literal(permission));
        return this;
    }

    /**
     * Adds a required permission with a custom denial message.
     *
     * @param permission the permission node
     * @param message    the denial message
     * @return this builder
     */
    public ProgrammaticCommand permission(String permission, String message) {
        this.permissions.add(CommandPermission.literal(permission, message));
        return this;
    }

    /**
     * Adds a permission loaded from config.
     *
     * @param config the config file name
     * @param path   the config path
     * @return this builder
     */
    public ProgrammaticCommand configPermission(String config, String path) {
        this.permissions.add(CommandPermission.config(config, path));
        return this;
    }

    /**
     * Sets the default permission denial message.
     *
     * @param message the message
     * @return this builder
     */
    public ProgrammaticCommand permissionMessage(String message) {
        this.permissionMessage = CommandPermissionMessage.literal(message);
        return this;
    }

    /**
     * Sets the permission denial message from config.
     *
     * @param config the config file name
     * @param path   the config path
     * @return this builder
     */
    public ProgrammaticCommand permissionMessageConfig(String config, String path) {
        this.permissionMessage = CommandPermissionMessage.config(config, path);
        return this;
    }

    /**
     * Sets the command execution handler.
     *
     * @param handler the handler
     * @return this builder
     */
    public ProgrammaticCommand executes(CommandHandler<CommandSender> handler) {
        this.handler = handler;
        this.senderType = CommandSender.class;
        return this;
    }

    /**
     * Sets the command execution handler with a specific sender type.
     *
     * @param senderType the required sender type
     * @param handler    the handler
     * @param <T>        the sender type
     * @return this builder
     */
    @SuppressWarnings("unchecked")
    public <T extends CommandSender> ProgrammaticCommand executes(Class<T> senderType, CommandHandler<T> handler) {
        this.handler = (CommandHandler<CommandSender>) handler;
        this.senderType = senderType;
        return this;
    }

    /**
     * Adds a subcommand.
     *
     * @param label   the subcommand label
     * @param builder a consumer to configure the subcommand
     * @return this builder
     */
    public ProgrammaticCommand subcommand(String label, Consumer<ProgrammaticCommand> builder) {
        ProgrammaticCommand sub = create(label);
        sub.parent = this;
        builder.accept(sub);
        this.subcommands.add(sub);
        return this;
    }

    /**
     * Adds a subcommand with aliases.
     *
     * @param label   the subcommand label
     * @param aliases additional aliases
     * @param builder a consumer to configure the subcommand
     * @return this builder
     */
    public ProgrammaticCommand subcommand(String label, String[] aliases, Consumer<ProgrammaticCommand> builder) {
        ProgrammaticCommand sub = create(label, aliases);
        sub.parent = this;
        builder.accept(sub);
        this.subcommands.add(sub);
        return this;
    }

    /**
     * Registers this command.
     *
     * @return the registered command definition
     */
    public CommandDefinition register() {
        CommandDefinition definition = build(null);
        Commands.getRegistry().registerInternal(definition);
        return definition;
    }

    /**
     * Builds the command definition without registering.
     *
     * @return the command definition
     */
    public CommandDefinition build() {
        return build(null);
    }

    private CommandDefinition build(CommandDefinition parentDef) {
        CommandBuilder builder = CommandBuilder.create()
                .labels(labels)
                .description(description)
                .parent(parentDef);

        // Add permissions
        for (CommandPermission perm : permissions) {
            builder.addPermission(perm);
        }

        if (permissionMessage != null) {
            builder.permissionMessage(permissionMessage);
        }

        // Add handler as default method
        if (handler != null) {
            builder.defaultMethod(new ProgrammaticMethodDefinition(handler, senderType));
        }

        // Build subcommands (need to build parent first for proper syntax generation)
        CommandDefinition def = builder.build();

        // Now add subcommands with this as parent
        if (!subcommands.isEmpty()) {
            builder = CommandBuilder.create()
                    .labels(labels)
                    .description(description)
                    .parent(parentDef);

            for (CommandPermission perm : permissions) {
                builder.addPermission(perm);
            }
            if (permissionMessage != null) {
                builder.permissionMessage(permissionMessage);
            }
            if (handler != null) {
                builder.defaultMethod(new ProgrammaticMethodDefinition(handler, senderType));
            }

            for (ProgrammaticCommand sub : subcommands) {
                builder.addSubcommand(sub.build(def));
            }

            def = builder.build();
        }

        return def;
    }

    /**
     * @return the primary label
     */
    public String getPrimaryLabel() {
        return labels.isEmpty() ? "" : labels.get(0);
    }

    /**
     * @return all labels
     */
    public List<String> getLabels() {
        return labels;
    }

}
