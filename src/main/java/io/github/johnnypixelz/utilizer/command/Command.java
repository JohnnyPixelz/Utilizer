package io.github.johnnypixelz.utilizer.command;

import io.github.johnnypixelz.utilizer.command.annotations.*;
import io.github.johnnypixelz.utilizer.command.exceptions.CommandAnnotationParseException;
import io.github.johnnypixelz.utilizer.command.permissions.CommandPermission;
import io.github.johnnypixelz.utilizer.command.permissions.CommandPermissionMessage;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Command {
    private List<String> labels;
    private CommandMethod defaultMethod;
    private List<Command> subcommands;
    private String description;
    private final List<CommandPermission> requiredPermissions;
    private CommandPermissionMessage permissionMessage;

    public Command() {
        this.labels = new ArrayList<>();
        this.defaultMethod = null;
        this.subcommands = new ArrayList<>();
        this.description = null;
        this.requiredPermissions = new ArrayList<>();
        this.permissionMessage = null;
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

    public String getDescription() {
        return description;
    }

    public List<CommandPermission> getRequiredPermissions() {
        return requiredPermissions;
    }

    public CommandPermissionMessage getPermissionMessage() {
        return permissionMessage;
    }

    public void parseAnnotations() throws CommandAnnotationParseException {
        final Label labelAnnotation = getClass().getAnnotation(Label.class);
        if (labelAnnotation == null) {
            throw new CommandAnnotationParseException("Missing label annotation.");
        }

        this.labels.addAll(CommandUtil.parseLabel(labelAnnotation.value()));
        
        parsePermissionAnnotations(this, getClass());

        for (Method declaredMethod : getClass().getDeclaredMethods()) {
//            Processing Default annotations
            if (declaredMethod.isAnnotationPresent(Default.class)) {
                defaultMethod = new CommandMethod(this, declaredMethod);
            }

            final String description = Optional.ofNullable(declaredMethod.getAnnotation(Description.class))
                    .map(Description::value)
                    .orElse(null);

//            Processing Subcommand annotations
            if (declaredMethod.isAnnotationPresent(Subcommand.class)) {
                final Command command = new Command();

                final Subcommand subcommandAnnotation = declaredMethod.getAnnotation(Subcommand.class);
                final String label = subcommandAnnotation.value();
                final List<String> labels = CommandUtil.parseLabel(label);
                command.labels = labels;

                command.defaultMethod = new CommandMethod(this, declaredMethod);
                command.description = description;

                parsePermissionAnnotations(command, declaredMethod);

                subcommands.add(command);
            }

//            Processing Label annotations
            if (declaredMethod.isAnnotationPresent(Label.class)) {
                final Command command = new Command();

                final Label subcommandLabelAnnotation = declaredMethod.getAnnotation(Label.class);
                final String label = subcommandLabelAnnotation.value();
                final List<String> labels = CommandUtil.parseLabel(label);
                command.labels = labels;

                command.defaultMethod = new CommandMethod(this, declaredMethod);
                command.description = description;

                parsePermissionAnnotations(command, declaredMethod);

                CommandManager.registerCommand(command);
            }
        }


    }

    private void parsePermissionAnnotations(Command command, AnnotatedElement annotatedElement) {
        final Permission[] permissionAnnotations = annotatedElement.getAnnotationsByType(Permission.class);
        Arrays.stream(permissionAnnotations)
                .map(CommandPermission::processPermissionAnnotation)
                .forEach(command.requiredPermissions::add);

        final ConfigPermission[] configPermissionAnnotations = annotatedElement.getAnnotationsByType(ConfigPermission.class);
        Arrays.stream(configPermissionAnnotations)
                .map(CommandPermission::processConfigPermissionAnnotation)
                .forEach(command.requiredPermissions::add);

        final PermissionMessage permissionMessageAnnotation = annotatedElement.getAnnotation(PermissionMessage.class);
        if (permissionMessageAnnotation != null) {
            final String permissionMessage = permissionMessageAnnotation.value();
            command.permissionMessage = CommandPermissionMessage.literal(permissionMessage);
        }

        final PermissionConfigMessage permissionConfigMessageAnnotation = annotatedElement.getAnnotation(PermissionConfigMessage.class);
        if (permissionConfigMessageAnnotation != null) {
            final String config = permissionConfigMessageAnnotation.config();
            final String path = permissionConfigMessageAnnotation.path();
            command.permissionMessage = CommandPermissionMessage.config(config, path);
        }
    }

}
