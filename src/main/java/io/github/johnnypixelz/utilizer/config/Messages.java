package io.github.johnnypixelz.utilizer.config;

import com.cryptomorin.xseries.XSound;
import io.github.johnnypixelz.utilizer.text.Colors;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

public class Messages {

    public static void send(CommandSender commandSender, String message) {
        commandSender.sendMessage(Colors.color(message));
    }

    public static void send(CommandSender commandSender, List<String> message) {
        for (String line : message) {
            send(commandSender, line);
        }
    }

    public static void send(CommandSender commandSender, String... message) {
        for (String line : message) {
            send(commandSender, line);
        }
    }

    public static void send(List<CommandSender> commandSenders, String message) {
        for (CommandSender commandSender : commandSenders) {
            send(commandSender, message);
        }
    }

    public static void send(List<CommandSender> commandSenders, List<String> message) {
        for (CommandSender commandSender : commandSenders) {
            send(commandSender, message);
        }
    }

    public static void send(List<CommandSender> commandSenders, String... message) {
        for (CommandSender commandSender : commandSenders) {
            send(commandSender, message);
        }
    }

    public static void send(CommandSender commandSender, String message, Function<String, String> mapper) {
        send(commandSender, mapper.apply(message));
    }

    public static void send(CommandSender commandSender, List<String> messages, Function<String, String> mapper) {
        for (String line : messages) {
            send(commandSender, mapper.apply(line));
        }
    }

    public static void send(List<CommandSender> commandSenders, String message, Function<String, String> mapper) {
        send(commandSenders, mapper.apply(message));
    }

    public static void send(List<CommandSender> commandSenders, List<String> messages, Function<String, String> mapper) {
        for (String line : messages) {
            send(commandSenders, mapper.apply(line));
        }
    }

    @NotNull
    public static Message cfg(@NotNull String configPath) {
        return cfg("config", configPath);
    }

    @NotNull
    public static Message cfg(@NotNull String config, @NotNull String configPath) {
        FileConfiguration fileConfiguration = Configs.get(config);

        Message message = new Message();

        if (fileConfiguration.isConfigurationSection(configPath)) {
            ConfigurationSection section = fileConfiguration.getConfigurationSection(configPath);
            for (String key : section.getKeys(false)) {
                switch (key.toLowerCase()) {
                    case "message":
                        if (section.isList(key)) {
                            message.setMessageList(section.getStringList(key));
                            break;
                        }
                        if (section.isString(key)) {
                            message.setMessage(section.getString(key));
                            break;
                        }
                        break;
                    case "title":
                        if (!section.isString(key)) break;
                        message.setTitle(section.getString(key));
                        break;
                    case "subtitle":
                        if (!section.isString(key)) break;
                        message.setSubtitle(section.getString(key));
                        break;
                    case "actionbar":
                        if (!section.isString(key)) break;
                        message.setActionbar(section.getString(key));
                        break;
                    case "sound":
                        if (!section.isString(key)) break;
                        String soundString = section.getString(key);
                        XSound.matchXSound(soundString).ifPresent(xSound -> message.setSound(xSound.parseSound()));
                        break;
                }
            }
        } else if (fileConfiguration.isList(configPath)) {
            message.setMessageList(fileConfiguration.getStringList(configPath));
        } else if (fileConfiguration.isString(configPath)) {
            message.setMessage(fileConfiguration.getString(configPath));
        }

        return message;
    }

}
