package io.github.johnnypixelz.utilizer.command;

import java.util.ArrayList;
import java.util.List;

public class Command {
    private final String root;
    private String description;
    private final List<Class<?>> arguments;
    private final List<Command> subcommands;

    public Command(String root) {
        this.root = root;
        this.description = null;
        this.arguments = new ArrayList<>();
        this.subcommands = new ArrayList<>();
    }

    public String getRoot() {
        return root;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Command> getSubcommands() {
        return subcommands;
    }

    public void addSubcommand(Command command) {
        subcommands.add(command);
    }

    public <T> void setArguments(Class<T> argumentClass, CommandArguments1<T> argument1) {

    }

}
