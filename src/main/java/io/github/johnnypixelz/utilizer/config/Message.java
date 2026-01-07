package io.github.johnnypixelz.utilizer.config;

import io.github.johnnypixelz.utilizer.depend.Placeholders;
import io.github.johnnypixelz.utilizer.serialize.world.Point;
import io.github.johnnypixelz.utilizer.text.ActionBar;
import io.github.johnnypixelz.utilizer.text.Colors;
import io.github.johnnypixelz.utilizer.text.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Message {
    private String message;
    private List<String> messageList;
    private Sound sound;
    private String title;
    private String subtitle;
    private String actionbar;
    private TitleSettings titleSettings;

    public Message() {
        this.message = null;
        this.messageList = null;
        this.sound = null;
        this.title = null;
        this.subtitle = null;
        this.actionbar = null;
        this.titleSettings = null;
    }

    public Message(Message message) {
        this.message = message.message;
        this.messageList = message.messageList == null ? null : new ArrayList<>(message.messageList);
        this.sound = message.sound;
        this.title = message.title;
        this.subtitle = message.subtitle;
        this.actionbar = message.actionbar;
        this.titleSettings = message.titleSettings == null ? null : new TitleSettings(message.titleSettings);
    }

    @NotNull
    public Message send(@NotNull CommandSender commandSender) {
        // Determine if we should process placeholders (only for Players)
        Player player = commandSender instanceof Player ? (Player) commandSender : null;

        if (message != null) {
            String processed = player != null ? Placeholders.set(player, message) : message;
            commandSender.sendMessage(Colors.color(processed));
        }

        if (messageList != null) {
            for (String line : messageList) {
                String processed = player != null ? Placeholders.set(player, line) : line;
                commandSender.sendMessage(Colors.color(processed));
            }
        }

        if (player == null) return this;

        if (sound != null) {
            player.playSound(player.getLocation(), sound, 1, 1);
        }

        if (title != null) {
            String processedTitle = Placeholders.set(player, title);
            String processedSubtitle = subtitle != null ? Placeholders.set(player, subtitle) : "";

            if (titleSettings != null) {
                Title.title(
                        player,
                        Colors.color(processedTitle),
                        Colors.color(processedSubtitle),
                        titleSettings.getTitleFadeIn(),
                        titleSettings.getTitleStay(),
                        titleSettings.getTitleFadeOut()
                );
            } else {
                Title.title(player, Colors.color(processedTitle), Colors.color(processedSubtitle));
            }
        }

        if (actionbar != null) {
            String processedActionbar = Placeholders.set(player, actionbar);
            ActionBar.coloredActionbar(player, processedActionbar);
        }

        return this;
    }

    @NotNull
    public Message send(@NotNull List<? extends CommandSender> commandSenders) {
        for (CommandSender commandSender : commandSenders) {
            send(commandSender);
        }

        return this;
    }

    @NotNull
    public Message broadcast() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            send(player);
        }

        return this;
    }

    @NotNull
    public Message broadcast(Point point, double radius) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            final Point playerPoint = Point.of(player.getLocation());
            if (!playerPoint.getWorld().equals(point.getWorld())) continue;

            final double playerDistanceSquared = point.distanceSquared(playerPoint);
            if (playerDistanceSquared > Math.pow(radius, 2)) continue;

            send(player);
        }

        return this;
    }

    @NotNull
    public Message broadcast(Point point, double radius, Predicate<Player> predicate) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            final Point playerPoint = Point.of(player.getLocation());
            if (!playerPoint.getWorld().equals(point.getWorld())) continue;

            final double playerDistanceSquared = point.distanceSquared(playerPoint);
            if (playerDistanceSquared > Math.pow(radius, 2)) continue;

            if (!predicate.test(player)) continue;
            send(player);
        }

        return this;
    }

    @NotNull
    public Message map(@NotNull String target, @NotNull String replacement) {
        return map(line -> line.replace(target, replacement));
    }

    @NotNull
    public Message map(@NotNull Function<String, String> mapper) {
        if (message != null) {
            message = mapper.apply(message);
        }

        if (messageList != null) {
            for (int index = 0; index < messageList.size(); index++) {
                messageList.set(index, mapper.apply(messageList.get(index)));
            }
        }

        if (title != null) {
            if (subtitle != null) {
                subtitle = mapper.apply(subtitle);
            }

            title = mapper.apply(title);
        }

        if (actionbar != null) {
            actionbar = mapper.apply(actionbar);
        }

        return this;
    }

    @NotNull
    public Message map(@NotNull String target, @NotNull List<String> replacement) {
        if (messageList != null) {
            messageList = messageList.stream()
                    .flatMap(placeholderLine -> {
                        if (!placeholderLine.contains(target)) return Stream.of(placeholderLine);
                        return replacement.stream().map(line -> placeholderLine.replace(target, line));
                    })
                    .collect(Collectors.toList());
        }

        return this;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    public Message setMessage(@Nullable String message) {
        this.message = message;
        return this;
    }

    @Nullable
    public List<String> getMessageList() {
        return messageList;
    }

    public Message setMessageList(@Nullable List<String> messageList) {
        this.messageList = messageList;
        return this;
    }

    @Nullable
    public Sound getSound() {
        return sound;
    }

    public Message setSound(@Nullable Sound sound) {
        this.sound = sound;
        return this;
    }

    @Nullable
    public String getTitle() {
        return title;
    }

    public Message setTitle(@Nullable String title) {
        this.title = title;
        return this;
    }

    @Nullable
    public String getSubtitle() {
        return subtitle;
    }

    public Message setSubtitle(@Nullable String subtitle) {
        this.subtitle = subtitle;
        return this;
    }

    @Nullable
    public String getActionbar() {
        return actionbar;
    }

    public Message setActionbar(@Nullable String actionbar) {
        this.actionbar = actionbar;
        return this;
    }

    @Nullable
    public TitleSettings getTitleSettings() {
        return titleSettings;
    }

    public Message setTitleSettings(TitleSettings titleSettings) {
        this.titleSettings = titleSettings;
        return this;
    }

}
