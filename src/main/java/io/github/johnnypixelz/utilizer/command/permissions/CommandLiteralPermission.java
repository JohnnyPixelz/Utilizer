package io.github.johnnypixelz.utilizer.command.permissions;

import java.util.Optional;

public class CommandLiteralPermission implements CommandPermission {
    private final String permission;
    private final CommandPermissionMessage permissionMessage;

    CommandLiteralPermission(String permission, CommandPermissionMessage permissionMessage) {
        this.permission = permission;
        this.permissionMessage = permissionMessage;
    }

    @Override
    public String getPermission() {
        return permission;
    }

    @Override
    public Optional<CommandPermissionMessage> getPermissionMessage() {
        return Optional.ofNullable(permissionMessage);
    }

}
