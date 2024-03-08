package io.github.johnnypixelz.utilizer.command.permissions;

import io.github.johnnypixelz.utilizer.config.Message;

public class CommandPermissionLiteralMessage implements CommandPermissionMessage {
    private final Message message;

    CommandPermissionLiteralMessage(String message) {
        this.message = new Message().setMessage(message);
    }

    @Override
    public Message getMessage() {
        return message;
    }

}
