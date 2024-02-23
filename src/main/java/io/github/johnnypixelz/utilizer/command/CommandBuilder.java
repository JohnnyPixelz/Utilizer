package io.github.johnnypixelz.utilizer.command;

import java.util.ArrayList;
import java.util.List;

public class CommandBuilder {
    private final CommandBuilder parentBuilder;

    private final String root;
    private String description;
    private final List<Class<?>> arguments;
    private final List<Command> subcommands;
    private final List<String> permissions;

    public CommandBuilder(String root) {
        this(root, null);
    }

    public CommandBuilder(String root, CommandBuilder parentBuilder) {
        this.parentBuilder = parentBuilder;
        this.root = root;
        this.description = null;
        this.arguments = new ArrayList<>();
        this.subcommands = new ArrayList<>();
        this.permissions = new ArrayList<>();
    }

    public CommandBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public CommandBuilder addSubcommand(Command command) {
        subcommands.add(command);
        return this;
    }

    public CommandBuilder addPermission(String permission) {
        permissions.add(permission);
        return this;
    }

    public <A1> CommandBuilder setExecution(Class<A1> argument1, CommandArguments1<A1> commandArguments) {
        return this;
    }

    public <A1, A2> CommandBuilder setExecution(Class<A1> argument1, Class<A2> argument2, CommandArguments2<A1, A2> commandArguments) {
        return this;
    }

    public Command build() {
        if (parentBuilder != null) throw new IllegalStateException("CommandBuilder#build was called while in a subcommand builder.");

        final Command command = new Command(root);
        command.setDescription(description);
        subcommands.forEach(command::addSubcommand);

        return command;
    }

}
