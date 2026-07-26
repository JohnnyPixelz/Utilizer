package io.github.johnnypixelz.utilizer.command;

import io.github.johnnypixelz.utilizer.config.Message;

public enum CommandMessage {
    NO_PERMISSION(new Message().setMessage("&cYou do not have permission to execute this command.")),
    NOT_ENOUGH_ARGUMENTS(new Message().setMessage("&cNot enough arguments.")),
    PLAYERS_ONLY(new Message().setMessage("&cThis command can only be run by a player.")),
    WRONG_SENDER(new Message().setMessage("&cThis command cannot be run from here.")),
    INTERNAL_ERROR(new Message().setMessage("&cThere was an internal error. Please contact the administrators."));

    private final Message message;

    CommandMessage(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

}
