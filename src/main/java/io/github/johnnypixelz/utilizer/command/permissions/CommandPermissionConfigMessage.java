package io.github.johnnypixelz.utilizer.command.permissions;

import io.github.johnnypixelz.utilizer.config.Message;
import io.github.johnnypixelz.utilizer.config.Messages;
import org.jetbrains.annotations.Nullable;

public class CommandPermissionConfigMessage implements CommandPermissionMessage {
    private final String config;
    private final String path;

    CommandPermissionConfigMessage(String config, String path) {
        this.config = config;
        this.path = path;
    }

    @Override
    @Nullable
    public Message getMessage() {
        return Messages.cfg(config, path);
    }

}
