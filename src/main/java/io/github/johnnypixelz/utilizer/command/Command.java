package io.github.johnnypixelz.utilizer.command;

import io.github.johnnypixelz.utilizer.command.annotations.Default;
import io.github.johnnypixelz.utilizer.command.annotations.Label;
import io.github.johnnypixelz.utilizer.command.annotations.Subcommand;
import io.github.johnnypixelz.utilizer.command.exceptions.CommandAnnotationParseException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Command {
    private List<String> rootLabels;
    private List<String> subcommandLabels;
    private Method defaultMethod;
    private List<Command> subcommands;

    private Command() {
        this.rootLabels = new ArrayList<>();
        this.subcommandLabels = new ArrayList<>();
        this.defaultMethod = null;
        this.subcommands = new ArrayList<>();
    }

    public List<String> getRootLabels() {
        return rootLabels;
    }

    public List<String> getSubcommandLabels() {
        return subcommandLabels;
    }

    public Method getDefaultMethod() {
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

        this.rootLabels = CommandUtil.parseLabel(labelAnnotation.value());

        this.subcommands = new ArrayList<>();

        for (Method declaredMethod : getClass().getDeclaredMethods()) {
//            Processing Default annotations
            if (declaredMethod.isAnnotationPresent(Default.class)) {
                defaultMethod = declaredMethod;
            }

//            Processing Subcommand annotations
            if (declaredMethod.isAnnotationPresent(Subcommand.class)) {
                final Subcommand subcommandAnnotation = declaredMethod.getAnnotation(Subcommand.class);
                final String subcommandLabel = subcommandAnnotation.value();
                final List<String> subcommandLabels = CommandUtil.parseLabel(subcommandLabel);
                final Command command = new Command();
                command.subcommandLabels = subcommandLabels;
                command.defaultMethod = declaredMethod;
                subcommands.add(command);
            }
        }


    }

}
