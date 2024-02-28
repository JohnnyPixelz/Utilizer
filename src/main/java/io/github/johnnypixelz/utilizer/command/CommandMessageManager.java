package io.github.johnnypixelz.utilizer.command;

import io.github.johnnypixelz.utilizer.config.Message;
import io.github.johnnypixelz.utilizer.config.Messages;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CommandMessageManager {
    private static final Map<CommandMessage, Supplier<Message>> customMessageMap = new HashMap<>();

    public static Message getMessage(CommandMessage commandMessage) {
        if (customMessageMap.containsKey(commandMessage)) {
            return customMessageMap.get(commandMessage).get();
        } else {
            return commandMessage.getMessage();
        }
    }

    public static void setMessage(CommandMessage commandMessage, Message message) {
        customMessageMap.put(commandMessage, () -> message);
    }

    public static void setMessage(CommandMessage commandMessage, String config, String path) {
        customMessageMap.put(commandMessage, () -> Messages.cfg(config, path));
    }

}
