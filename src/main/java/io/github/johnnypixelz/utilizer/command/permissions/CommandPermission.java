package io.github.johnnypixelz.utilizer.command.permissions;

import io.github.johnnypixelz.utilizer.command.annotations.ConfigPermission;
import io.github.johnnypixelz.utilizer.command.annotations.Permission;

import java.util.Optional;

public interface CommandPermission {

    static CommandPermission literal(String permission) {
        return new CommandLiteralPermission(permission, null);
    }

    static CommandPermission literal(String permission, String message) {
        return new CommandLiteralPermission(permission, CommandPermissionMessage.literal(message));
    }

    static CommandPermission literal(String permission, String config, String path) {
        return new CommandLiteralPermission(permission, CommandPermissionMessage.config(config, path));
    }

    static CommandPermission config(String config, String path) {
        return new CommandConfigPermission(config, path, null);
    }

    static CommandPermission config(String config, String path, String message) {
        return new CommandConfigPermission(config, path, CommandPermissionMessage.literal(message));
    }

    static CommandPermission config(String config, String path, String messageConfig, String messagePath) {
        return new CommandConfigPermission(config, path, CommandPermissionMessage.config(messageConfig, messagePath));
    }

    static CommandPermission processPermissionAnnotation(Permission permissionAnnotation) {
        final String permission = permissionAnnotation.value();

        if (permissionAnnotation.message() != null) {
            return literal(permission, permissionAnnotation.message());
        } else if (permissionAnnotation.messageConfig() != null && permissionAnnotation.messagePath() != null) {
            return literal(permission, permissionAnnotation.messageConfig(), permissionAnnotation.messagePath());
        } else {
            return literal(permission);
        }
    }

    static CommandPermission processConfigPermissionAnnotation(ConfigPermission permissionAnnotation) {
        final String config = permissionAnnotation.config();
        final String path = permissionAnnotation.path();

        if (permissionAnnotation.message() != null) {
            return config(config, path, permissionAnnotation.message());
        } else if (permissionAnnotation.messageConfig() != null && permissionAnnotation.messagePath() != null) {
            return config(config, path, permissionAnnotation.messageConfig(), permissionAnnotation.messagePath());
        } else {
            return config(config, path);
        }
    }

    String getPermission();

    Optional<CommandPermissionMessage> getPermissionMessage();

}
