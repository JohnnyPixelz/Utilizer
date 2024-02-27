package io.github.johnnypixelz.utilizer.command;

import io.github.johnnypixelz.utilizer.command.annotations.Default;
import io.github.johnnypixelz.utilizer.command.annotations.Label;
import io.github.johnnypixelz.utilizer.command.annotations.Subcommand;
import io.github.johnnypixelz.utilizer.command.exceptions.CommandAnnotationParseException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Command {
    private List<String> labels;
    private CommandMethod defaultMethod;
    private List<Command> subcommands;

    private Command() {
        this.labels = new ArrayList<>();
        this.defaultMethod = null;
        this.subcommands = new ArrayList<>();
    }

    public List<String> getLabels() {
        return labels;
    }

    public CommandMethod getDefaultMethod() {
        return defaultMethod;
    }

    public List<Command> getSubcommands() {
        return subcommands;
    }

    public void parseAnnotations() throws CommandAnnotationParseException {
        final Label labelAnnotation = getClass().getAnnotation(Label.class);
        if (labelAnnotation == null) {
            throw new CommandAnnotationParseException("Missing label annotation.");
        }

        this.labels = CommandUtil.parseLabel(labelAnnotation.value());

        this.subcommands = new ArrayList<>();

        for (Method declaredMethod : getClass().getDeclaredMethods()) {
//            Processing Default annotations
            if (declaredMethod.isAnnotationPresent(Default.class)) {
                defaultMethod = new CommandMethod(declaredMethod);
            }

//            Processing Subcommand annotations
            if (declaredMethod.isAnnotationPresent(Subcommand.class)) {
                final Subcommand subcommandAnnotation = declaredMethod.getAnnotation(Subcommand.class);
                final String label = subcommandAnnotation.value();
                final List<String> labels = CommandUtil.parseLabel(label);
                final Command command = new Command();
                command.labels = labels;
                command.defaultMethod = new CommandMethod(declaredMethod);
                subcommands.add(command);
            }
        }


    }

}
