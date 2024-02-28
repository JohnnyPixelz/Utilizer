package io.github.johnnypixelz.utilizer.command;

import io.github.johnnypixelz.utilizer.config.Message;

public enum CommandMessage {
    NO_PERMISSION(new Message().setMessage("&cYou do not have permission to execute this command."));

    private final Message message;

    CommandMessage(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

}
