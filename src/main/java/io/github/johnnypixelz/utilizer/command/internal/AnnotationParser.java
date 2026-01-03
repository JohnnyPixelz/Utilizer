package io.github.johnnypixelz.utilizer.command.internal;

import io.github.johnnypixelz.utilizer.command.CommandBase;
import io.github.johnnypixelz.utilizer.command.annotations.*;
import io.github.johnnypixelz.utilizer.command.exceptions.CommandAnnotationParseException;
import io.github.johnnypixelz.utilizer.command.permissions.CommandPermission;
import io.github.johnnypixelz.utilizer.command.permissions.CommandPermissionMessage;
import io.github.johnnypixelz.utilizer.plugin.Logs;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Parses annotations from CommandBase classes to create CommandDefinition objects.
 */
public final class AnnotationParser {

    private final CommandRegistry registry;

    public AnnotationParser(CommandRegistry registry) {
        this.registry = registry;
    }

    /**
     * Parses a command instance and returns its definition.
     *
     * @param instance the command instance
     * @return the parsed command definition
     * @throws CommandAnnotationParseException if required annotations are missing
     */
    public CommandDefinition parse(CommandBase instance) throws CommandAnnotationParseException {
        Class<? extends CommandBase> clazz = instance.getClass();

        DebugLogger.annotationParsing(clazz.getSimpleName());

        Label labelAnnotation = clazz.getAnnotation(Label.class);
        if (labelAnnotation == null) {
            throw new CommandAnnotationParseException(
                    "Missing @Label annotation on class " + clazz.getCanonicalName()
            );
        }

        CommandBuilder builder = CommandBuilder.create()
                .labels(parseLabels(labelAnnotation.value()));

        // Parse optional description
        parseDescription(clazz).ifPresent(builder::description);

        // Parse permissions
        parsePermissions(builder, clazz);

        // Parse @Private and @CommandCompletion
        parseCompletionAndPrivate(builder, clazz);

        // Parse methods
        parseMethods(builder, instance, clazz);

        // Parse nested classes (subcommands)
        parseNestedClasses(builder, instance, clazz);

        return builder.build();
    }

    /**
     * Parses a subcommand from a method.
     */
    private CommandDefinition parseSubcommandMethod(CommandBase parentInstance, Method method, CommandDefinition parent) {
        Subcommand annotation = method.getAnnotation(Subcommand.class);

        CommandBuilder builder = CommandBuilder.create()
                .labels(parseLabels(annotation.value()))
                .parent(parent)
                .defaultMethod(new CommandMethodDefinition(parentInstance, method));

        parseDescription(method).ifPresent(builder::description);
        parsePermissions(builder, method);
        parseCompletionAndPrivate(builder, method);

        return builder.build();
    }

    /**
     * Parses a standalone command from a method with @Label.
     */
    private CommandDefinition parseLabelMethod(CommandBase parentInstance, Method method) {
        Label annotation = method.getAnnotation(Label.class);

        CommandBuilder builder = CommandBuilder.create()
                .labels(parseLabels(annotation.value()))
                .defaultMethod(new CommandMethodDefinition(parentInstance, method));

        parseDescription(method).ifPresent(builder::description);
        parsePermissions(builder, method);
        parseCompletionAndPrivate(builder, method);

        return builder.build();
    }

    private void parseMethods(CommandBuilder builder, CommandBase instance, Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            DebugLogger.methodParsing(clazz.getSimpleName(), method.getName());

            // @Default - marks the default handler
            if (method.isAnnotationPresent(Default.class)) {
                builder.defaultMethod(new CommandMethodDefinition(instance, method));
            }

            // @UnknownCommand - marks the unknown subcommand handler
            if (method.isAnnotationPresent(UnknownCommand.class)) {
                builder.unknownMethod(new CommandMethodDefinition(instance, method));
            }

            // @Subcommand - creates a subcommand from this method
            if (method.isAnnotationPresent(Subcommand.class)) {
                // Build a temporary parent to pass to subcommand (will be replaced in build)
                CommandDefinition tempParent = builder.build();
                CommandDefinition subcommand = parseSubcommandMethod(instance, method, tempParent);
                builder.addSubcommand(subcommand);
            }

            // @Label on method - registers as a separate command
            if (method.isAnnotationPresent(Label.class)) {
                CommandDefinition labelCommand = parseLabelMethod(instance, method);
                registry.registerInternal(labelCommand);
            }
        }
    }

    private void parseNestedClasses(CommandBuilder parentBuilder, CommandBase parentInstance, Class<?> parentClass) {
        for (Class<?> nestedClass : parentClass.getDeclaredClasses()) {
            if (!CommandBase.class.isAssignableFrom(nestedClass)) {
                continue;
            }

            @SuppressWarnings("unchecked")
            Class<? extends CommandBase> commandClass = (Class<? extends CommandBase>) nestedClass;

            // Try to instantiate the nested class
            CommandBase nestedInstance = instantiateNestedClass(commandClass, parentInstance);
            if (nestedInstance == null) {
                continue;
            }

            // Check if it has @Subcommand
            if (nestedClass.isAnnotationPresent(Subcommand.class)) {
                CommandDefinition parentDef = parentBuilder.build();
                CommandDefinition subcommand = parseNestedSubcommand(nestedInstance, commandClass, parentDef);
                parentBuilder.addSubcommand(subcommand);
            }

            // Check if it has @Label (registers as separate command)
            if (nestedClass.isAnnotationPresent(Label.class) && !nestedClass.isAnnotationPresent(Subcommand.class)) {
                try {
                    CommandDefinition labelCommand = parse(nestedInstance);
                    registry.registerInternal(labelCommand);
                } catch (CommandAnnotationParseException e) {
                    Logs.severe("Failed to parse nested command class: " + nestedClass.getCanonicalName());
                    e.printStackTrace();
                }
            }
        }
    }

    private CommandDefinition parseNestedSubcommand(CommandBase instance, Class<? extends CommandBase> clazz, CommandDefinition parent) {
        Subcommand annotation = clazz.getAnnotation(Subcommand.class);

        CommandBuilder builder = CommandBuilder.create()
                .labels(parseLabels(annotation.value()))
                .parent(parent);

        parseDescription(clazz).ifPresent(builder::description);
        parsePermissions(builder, clazz);
        parseCompletionAndPrivate(builder, clazz);

        // Parse the nested class's methods
        parseMethods(builder, instance, clazz);

        // Recursively parse nested classes
        parseNestedClasses(builder, instance, clazz);

        return builder.build();
    }

    private CommandBase instantiateNestedClass(Class<? extends CommandBase> nestedClass, CommandBase parentInstance) {
        try {
            // Try to find constructor that takes the parent
            Constructor<? extends CommandBase> constructor = nestedClass.getDeclaredConstructor(parentInstance.getClass());
            constructor.setAccessible(true);
            return constructor.newInstance(parentInstance);
        } catch (NoSuchMethodException e) {
            // Try no-arg constructor
            try {
                Constructor<? extends CommandBase> constructor = nestedClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                return constructor.newInstance();
            } catch (Exception ex) {
                Logs.severe("Could not instantiate nested command class: " + nestedClass.getCanonicalName());
                Logs.severe("Nested classes must have either a no-arg constructor or a constructor taking the parent instance.");
                return null;
            }
        } catch (Exception e) {
            Logs.severe("Failed to instantiate nested command class: " + nestedClass.getCanonicalName());
            e.printStackTrace();
            return null;
        }
    }

    private void parsePermissions(CommandBuilder builder, AnnotatedElement element) {
        // @Permission annotations
        Permission[] permissions = element.getAnnotationsByType(Permission.class);
        for (Permission permission : permissions) {
            builder.addPermission(CommandPermission.processPermissionAnnotation(permission));
        }

        // @ConfigPermission annotations
        ConfigPermission[] configPermissions = element.getAnnotationsByType(ConfigPermission.class);
        for (ConfigPermission configPermission : configPermissions) {
            builder.addPermission(CommandPermission.processConfigPermissionAnnotation(configPermission));
        }

        // @PermissionMessage
        PermissionMessage permissionMessage = element.getAnnotation(PermissionMessage.class);
        if (permissionMessage != null) {
            builder.permissionMessage(CommandPermissionMessage.literal(permissionMessage.value()));
        }

        // @PermissionConfigMessage
        PermissionConfigMessage configMessage = element.getAnnotation(PermissionConfigMessage.class);
        if (configMessage != null) {
            builder.permissionMessage(CommandPermissionMessage.config(configMessage.config(), configMessage.path()));
        }
    }

    private Optional<String> parseDescription(AnnotatedElement element) {
        Description annotation = element.getAnnotation(Description.class);
        return annotation != null ? Optional.of(annotation.value()) : Optional.empty();
    }

    private void parseCompletionAndPrivate(CommandBuilder builder, AnnotatedElement element) {
        // @Private
        if (element.isAnnotationPresent(Private.class)) {
            builder.isPrivate(true);
        }

        // @CommandCompletion
        CommandCompletion completion = element.getAnnotation(CommandCompletion.class);
        if (completion != null) {
            builder.completionSpec(completion.value());
        }
    }

    private List<String> parseLabels(String labelString) {
        return Arrays.stream(labelString.split("\\|"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

}
