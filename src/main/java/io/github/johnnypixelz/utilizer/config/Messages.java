package io.github.johnnypixelz.utilizer.config;

import com.cryptomorin.xseries.XSound;
import io.github.johnnypixelz.utilizer.text.Colors;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

public class Messages {

    public static void send(Player player, String message) {
        player.sendMessage(Colors.color(message));
    }

    public static void send(Player player, List<String> message) {
        for (String line : message) {
            send(player, line);
        }
    }

    public static void send(Player player, String... message) {
        for (String line : message) {
            send(player, line);
        }
    }

    public static void send(List<Player> players, String message) {
        for (Player player : players) {
            send(player, message);
        }
    }

    public static void send(List<Player> players, List<String> message) {
        for (Player player : players) {
            send(player, message);
        }
    }

    public static void send(List<Player> players, String... message) {
        for (Player player : players) {
            send(player, message);
        }
    }

    public static void send(Player player, String message, Function<String, String> mapper) {
        send(player, mapper.apply(message));
    }

    public static void send(Player player, List<String> messages, Function<String, String> mapper) {
        for (String line : messages) {
            send(player, mapper.apply(line));
        }
    }

    public static void send(List<Player> players, String message, Function<String, String> mapper) {
        send(players, mapper.apply(message));
    }

    public static void send(List<Player> players, List<String> messages, Function<String, String> mapper) {
        for (String line : messages) {
            send(players, mapper.apply(line));
        }
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
