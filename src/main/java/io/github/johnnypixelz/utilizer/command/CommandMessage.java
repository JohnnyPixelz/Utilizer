package io.github.johnnypixelz.utilizer.command;

import io.github.johnnypixelz.utilizer.config.Message;

public enum CommandMessage {
    NO_PERMISSION(new Message().setMessage("&cYou do not have permission to execute this command.")),
    NOT_ENOUGH_ARGUMENTS(new Message().setMessage("&cNot enough arguments.")),
    INTERNAL_ERROR(new Message().setMessage("&cThere was an internal error. Please contact the administrators."));

    private final Message message;

    CommandMessage(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

}
