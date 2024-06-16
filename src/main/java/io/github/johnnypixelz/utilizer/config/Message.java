package io.github.johnnypixelz.utilizer.config;

import com.cryptomorin.xseries.messages.ActionBar;
import io.github.johnnypixelz.utilizer.serialize.world.Point;
import io.github.johnnypixelz.utilizer.text.Colors;
import io.github.johnnypixelz.utilizer.text.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
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
        this.messageList = message.messageList;
        this.sound = message.sound;
        this.title = message.title;
        this.subtitle = message.subtitle;
        this.actionbar = message.actionbar;
        this.titleSettings = message.titleSettings == null ? null : new TitleSettings(message.titleSettings);
    }

    @Nonnull
    public Message send(@Nonnull CommandSender commandSender) {
        if (message != null) {
            commandSender.sendMessage(Colors.color(message));
        }

        if (messageList != null) {
            for (String line : messageList) {
                commandSender.sendMessage(Colors.color(line));
            }
        }

        if (!(commandSender instanceof Player)) return this;

        Player player = (Player) commandSender;

        if (sound != null) {
            player.playSound(player.getLocation(), sound, 1, 1);
        }

        if (title != null) {
            if (titleSettings != null) {
                Title.title(
                        player,
                        Colors.color(title),
                        subtitle == null ? "" : Colors.color(subtitle),
                        titleSettings.getTitleFadeIn(),
                        titleSettings.getTitleStay(),
                        titleSettings.getTitleFadeOut()
                );
            } else {
                if (subtitle != null) {
                    Title.title(player, Colors.color(title), Colors.color(subtitle));
                } else {
                    Title.title(player, Colors.color(title), "");
                }
            }
        }

        if (actionbar != null) {
            ActionBar.sendActionBar(player, Colors.color(actionbar));
        }

        return this;
    }

    @Nonnull
    public Message send(@Nonnull List<? extends CommandSender> commandSenders) {
        for (CommandSender commandSender : commandSenders) {
            send(commandSender);
        }

        return this;
    }

    @Nonnull
    public Message broadcast() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            send(player);
        }

        return this;
    }

    @Nonnull
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

    @Nonnull
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

    @Nonnull
    public Message map(@Nonnull String target, @Nonnull String replacement) {
        return map(line -> line.replace(target, replacement));
    }

    @Nonnull
    public Message map(@Nonnull Function<String, String> mapper) {
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

    @Nonnull
    public Message map(@Nonnull String target, @Nonnull List<String> replacement) {
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
