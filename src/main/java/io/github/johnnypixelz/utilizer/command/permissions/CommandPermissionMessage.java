package io.github.johnnypixelz.utilizer.command.permissions;

import io.github.johnnypixelz.utilizer.command.annotations.PermissionConfigMessage;
import io.github.johnnypixelz.utilizer.command.annotations.PermissionMessage;
import io.github.johnnypixelz.utilizer.config.Message;

public interface CommandPermissionMessage {

    static CommandPermissionMessage literal(String message) {
        return new CommandPermissionLiteralMessage(message);
    }

    static CommandPermissionMessage config(String config, String path) {
        return new CommandPermissionConfigMessage(config, path);
    }

    static CommandPermissionMessage processPermissionMessageAnnotation(PermissionMessage permissionMessageAnnotation) {
        return literal(permissionMessageAnnotation.value());
    }

    static CommandPermissionMessage processPermissionConfigMessageAnnotation(PermissionConfigMessage permissionConfigMessageAnnotation) {
        return config(permissionConfigMessageAnnotation.config(), permissionConfigMessageAnnotation.path());
    }

    Message getMessage();

}
