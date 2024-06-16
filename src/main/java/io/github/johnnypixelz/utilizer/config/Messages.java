package io.github.johnnypixelz.utilizer.config;

import com.cryptomorin.xseries.XSound;
import io.github.johnnypixelz.utilizer.serialize.world.Point;
import io.github.johnnypixelz.utilizer.text.Colors;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class Messages {

    public static void broadcast(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            send(player, message);
        }
    }

    public static void broadcast(List<String> message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            send(player, message);
        }
    }

    public static void broadcast(String message, Point point, double radius) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            final Point playerPoint = Point.of(player.getLocation());
            if (!playerPoint.getWorld().equals(point.getWorld())) continue;

            final double playerDistanceSquared = point.distanceSquared(playerPoint);
            if (playerDistanceSquared > Math.pow(radius, 2)) continue;

            send(player, message);
        }
    }

    public static void broadcast(List<String> message, Point point, double radius) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            final Point playerPoint = Point.of(player.getLocation());
            if (!playerPoint.getWorld().equals(point.getWorld())) continue;

            final double playerDistanceSquared = point.distanceSquared(playerPoint);
            if (playerDistanceSquared > Math.pow(radius, 2)) continue;

            send(player, message);
        }
    }

    public static void broadcast(String message, Point point, double radius, Predicate<Player> predicate) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            final Point playerPoint = Point.of(player.getLocation());
            if (!playerPoint.getWorld().equals(point.getWorld())) continue;

            final double playerDistanceSquared = point.distanceSquared(playerPoint);
            if (playerDistanceSquared > Math.pow(radius, 2)) continue;

            if (!predicate.test(player)) continue;
            send(player, message);
        }
    }

    public static void broadcast(List<String> message, Point point, double radius, Predicate<Player> predicate) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            final Point playerPoint = Point.of(player.getLocation());
            if (!playerPoint.getWorld().equals(point.getWorld())) continue;

            final double playerDistanceSquared = point.distanceSquared(playerPoint);
            if (playerDistanceSquared > Math.pow(radius, 2)) continue;

            if (!predicate.test(player)) continue;
            send(player, message);
        }
    }

    public static void send(CommandSender commandSender, String message) {
        commandSender.sendMessage(Colors.color(message));
    }

    public static void send(CommandSender commandSender, List<String> message) {
        for (String line : message) {
            send(commandSender, line);
        }
    }

    public static void send(List<? extends CommandSender> commandSenders, String message) {
        for (CommandSender commandSender : commandSenders) {
            send(commandSender, message);
        }
    }

    public static void send(List<? extends CommandSender> commandSenders, List<String> message) {
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

    public static void send(List<? extends CommandSender> commandSenders, String message, Function<String, String> mapper) {
        send(commandSenders, mapper.apply(message));
    }

    public static void send(List<? extends CommandSender> commandSenders, List<String> messages, Function<String, String> mapper) {
        for (String line : messages) {
            send(commandSenders, mapper.apply(line));
        }
    }

    @Nonnull
    public static Message parse(ConfigurationSection section, String path) {
        Message message = new Message();

        if (section.isConfigurationSection(path)) {
            ConfigurationSection messageSection = section.getConfigurationSection(path);
            if (messageSection == null) return message;

            for (String key : messageSection.getKeys(false)) {
                switch (key.toLowerCase()) {
                    case "message":
                        if (messageSection.isList(key)) {
                            message.setMessageList(messageSection.getStringList(key));
                            break;
                        }
                        if (messageSection.isString(key)) {
                            message.setMessage(messageSection.getString(key));
                            break;
                        }
                        break;
                    case "title":
                        if (!messageSection.isString(key)) break;
                        message.setTitle(messageSection.getString(key));
                        break;
                    case "subtitle":
                        if (!messageSection.isString(key)) break;
                        message.setSubtitle(messageSection.getString(key));
                        break;
                    case "actionbar":
                        if (!messageSection.isString(key)) break;
                        message.setActionbar(messageSection.getString(key));
                        break;
                    case "sound":
                        if (!messageSection.isString(key)) break;
                        String soundString = messageSection.getString(key);
                        XSound.matchXSound(soundString).ifPresent(xSound -> message.setSound(xSound.parseSound()));
                        break;
                    case "fade-in":
                        if (!messageSection.isInt(key)) break;
                        if (message.getTitleSettings() == null) {
                            message.setTitleSettings(new TitleSettings());
                        }

                        message.getTitleSettings().setTitleFadeIn(messageSection.getInt(key));
                        break;
                    case "stay":
                        if (!messageSection.isInt(key)) break;
                        if (message.getTitleSettings() == null) {
                            message.setTitleSettings(new TitleSettings());
                        }

                        message.getTitleSettings().setTitleStay(messageSection.getInt(key));
                        break;
                    case "fade-out":
                        if (!messageSection.isInt(key)) break;
                        if (message.getTitleSettings() == null) {
                            message.setTitleSettings(new TitleSettings());
                        }

                        message.getTitleSettings().setTitleFadeOut(messageSection.getInt(key));
                        break;
                }
            }
        } else if (section.isList(path)) {
            message.setMessageList(section.getStringList(path));
        } else if (section.isString(path)) {
            message.setMessage(section.getString(path));
        }

        return message;
    }

    @Nonnull
    public static Message cfg(@Nonnull String configPath) {
        return cfg("config", configPath);
    }

    @Nonnull
    public static Message cfg(@Nonnull String config, @Nonnull String configPath) {
        FileConfiguration fileConfiguration = Configs.get(config);

        return parse(fileConfiguration, configPath);
    }

}
