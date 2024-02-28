package io.github.johnnypixelz.utilizer.command.permissions;

import io.github.johnnypixelz.utilizer.config.Configs;
import io.github.johnnypixelz.utilizer.config.UtilConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CommandConfigPermission implements CommandPermission {
    private final String config;
    private final String path;
    private final CommandPermissionMessage permissionMessage;

    CommandConfigPermission(String config, String path, CommandPermissionMessage permissionMessage) {
        this.config = config;
        this.path = path;
        this.permissionMessage = permissionMessage;
    }

    @Override
    @Nullable
    public String getPermission() {
        final UtilConfiguration configuration = Configs.get(config);
        final boolean isString = configuration.isString(path);
        if (!isString) return null;

        return configuration.getString(path);
    }

    @Override
    public Optional<CommandPermissionMessage> getPermissionMessage() {
        return Optional.ofNullable(permissionMessage);
    }

}
