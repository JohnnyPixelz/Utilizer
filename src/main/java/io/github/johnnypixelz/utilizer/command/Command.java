package io.github.johnnypixelz.utilizer.command;

import io.github.johnnypixelz.utilizer.command.annotations.*;
import io.github.johnnypixelz.utilizer.command.exceptions.CommandAnnotationParseException;
import io.github.johnnypixelz.utilizer.command.permissions.CommandPermission;
import io.github.johnnypixelz.utilizer.command.permissions.CommandPermissionMessage;
import io.github.johnnypixelz.utilizer.plugin.Logs;
import org.bukkit.command.CommandSender;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class Command {
    private List<String> labels;
    private CommandMethod defaultMethod;
    private final List<Command> subcommands;
    private String description;
    private final List<CommandPermission> requiredPermissions;
    private CommandPermissionMessage permissionMessage;
    private Command parent;
    private CommandSyntax syntax;

    public Command() {
        this.labels = new ArrayList<>();
        this.defaultMethod = null;
        this.subcommands = new ArrayList<>();
        this.description = null;
        this.requiredPermissions = new ArrayList<>();
        this.permissionMessage = null;
        this.parent = null;
        this.syntax = null;
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

    public Command getParent() {
        return parent;
    }

    public CommandSyntax getSyntax() {
        if (syntax == null) {
            Logs.severe("Attempted to get syntax for command " + labels.get(0) + " but syntax is null.");
        }

        return syntax;
    }

    private void generateSyntax() {
        if (defaultMethod == null) {
            Logs.severe("Attempted to generate syntax for command " + labels.get(0) + " with default method null.");
            return;
        }
        this.syntax = new CommandSyntax(this);
    }

    public List<CommandPermission> getRequiredPermissions() {
        return requiredPermissions;
    }

    public CommandPermissionMessage getPermissionMessage() {
        return permissionMessage;
    }

    public boolean isPermitted(CommandSender sender) {
        for (CommandPermission requiredPermission : requiredPermissions) {
            if (!sender.hasPermission(requiredPermission.getPermission())) {
                return false;
            }
        }

        return true;
    }

    public boolean checkIfPermittedAndInform(CommandSender sender) {
        for (CommandPermission requiredPermission : requiredPermissions) {
            if (!sender.hasPermission(requiredPermission.getPermission())) {
                requiredPermission.getPermissionMessage()
                        .or(() -> Optional.ofNullable(permissionMessage))
                        .map(CommandPermissionMessage::getMessage)
                        .orElse(CommandMessageManager.getMessage(CommandMessage.NO_PERMISSION))
                        .send(sender);
                return false;
            }
        }

        return true;
    }

    private void parseMethodAnnotations(Command command, Class<? extends Command> clazz) {
        Logs.info("Parsing method annotations for command " + command.getLabels().get(0) + ", class " + clazz.getCanonicalName());
        for (Method declaredMethod : clazz.getDeclaredMethods()) {
            Logs.info("Method " + declaredMethod.getName());
//            Processing Default annotations
            if (declaredMethod.isAnnotationPresent(Default.class)) {
                command.defaultMethod = new CommandMethod(command, declaredMethod);
            }

            final String description = Optional.ofNullable(declaredMethod.getAnnotation(Description.class))
                    .map(Description::value)
                    .orElse(null);

//            Processing method Subcommand annotations
            if (declaredMethod.isAnnotationPresent(Subcommand.class)) {
                final Command subcommand = new Command();

                final Subcommand subcommandAnnotation = declaredMethod.getAnnotation(Subcommand.class);
                final String label = subcommandAnnotation.value();
                final List<String> labels = CommandUtil.parseLabel(label);
                subcommand.labels = labels;

                subcommand.defaultMethod = new CommandMethod(command, declaredMethod);
                subcommand.description = description;
                subcommand.parent = command;

                parsePermissionAnnotations(subcommand, declaredMethod);

                subcommand.generateSyntax();

                command.subcommands.add(subcommand);
            }

//            Processing method Label annotations
            if (declaredMethod.isAnnotationPresent(Label.class)) {
                final Command labelCommand = new Command();

                final Label subcommandLabelAnnotation = declaredMethod.getAnnotation(Label.class);
                final String label = subcommandLabelAnnotation.value();
                final List<String> labels = CommandUtil.parseLabel(label);
                labelCommand.labels = labels;

                labelCommand.defaultMethod = new CommandMethod(command, declaredMethod);
                labelCommand.description = description;

                parsePermissionAnnotations(labelCommand, declaredMethod);

                labelCommand.generateSyntax();

                CommandManager.registerCommand(labelCommand);
            }
        }
    }

    private void parseClassAnnotations(Command command, Class<? extends Command> clazz) {
        Arrays.stream(clazz.getDeclaredClasses())
                .filter(declaredClass -> {
                    final boolean assignableFrom = Command.class.isAssignableFrom(declaredClass);

                    if (!assignableFrom) {
                        Logs.severe("Class " + declaredClass.getCanonicalName() + " is not extending " + Command.class.getCanonicalName() + ".");
                        Logs.severe("Will ignore it.");
                        return false;
                    }

                    return true;
                })
                .forEach(declaredClass -> {
                    @SuppressWarnings("unchecked")
                    final Class<? extends Command> declaredCommandClass = (Class<? extends Command>) declaredClass;

                    Supplier<Command> commandSupplier = () -> {
                        try {
                            final Constructor<? extends Command> constructor = declaredCommandClass.getDeclaredConstructor(command.getClass());

                            constructor.setAccessible(true);

                            final Command newCommand;
                            try {
                                newCommand = constructor.newInstance(command);
                            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                                throw new RuntimeException(e);
                            }

                            constructor.setAccessible(false);

                            return newCommand;
                        } catch (NoSuchMethodException exception) {
                            Logs.severe("Could not find default constructor for class " + declaredCommandClass.getCanonicalName());
                            Logs.severe("Will ignore it.");
                            exception.printStackTrace();
                        }

                        return null;
                    };

                    final String description = Optional.ofNullable(declaredCommandClass.getAnnotation(Description.class))
                            .map(Description::value)
                            .orElse(null);

                    Optional.ofNullable(declaredCommandClass.getAnnotation(Label.class))
                            .map(Label::value)
                            .map(CommandUtil::parseLabel)
                            .ifPresent(labels -> {
                                final Command newCommand = commandSupplier.get();

                                newCommand.labels.addAll(labels);
                                newCommand.description = description;

                                CommandManager.registerCommand(newCommand);

                                parsePermissionAnnotations(newCommand, declaredCommandClass);
                                parseMethodAnnotations(newCommand, declaredCommandClass);
                                parseClassAnnotations(newCommand, declaredCommandClass);

                                newCommand.generateSyntax();
                            });

                    Optional.ofNullable(declaredCommandClass.getAnnotation(Subcommand.class))
                            .map(Subcommand::value)
                            .map(CommandUtil::parseLabel)
                            .ifPresent(subcommandLabels -> {
                                final Command newCommand = commandSupplier.get();

                                newCommand.labels.addAll(subcommandLabels);
                                newCommand.parent = command;
                                newCommand.description = description;

                                command.subcommands.add(newCommand);

                                parsePermissionAnnotations(newCommand, declaredCommandClass);
                                parseMethodAnnotations(newCommand, declaredCommandClass);
                                parseClassAnnotations(newCommand, declaredCommandClass);

                                newCommand.generateSyntax();
                            });

                });

    }

    public void parseAnnotations() throws CommandAnnotationParseException {
        final Label labelAnnotation = getClass().getAnnotation(Label.class);
        if (labelAnnotation == null) {
            throw new CommandAnnotationParseException("Missing label annotation for class " + this.getClass().getCanonicalName());
        }

        this.labels.addAll(CommandUtil.parseLabel(labelAnnotation.value()));

        Optional.ofNullable(getClass().getAnnotation(Description.class))
                .map(Description::value)
                .ifPresent(description -> this.description = description);

        parsePermissionAnnotations(this, this.getClass());
        parseMethodAnnotations(this, this.getClass());
        parseClassAnnotations(this, this.getClass());

        this.generateSyntax();
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
