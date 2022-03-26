package io.github.johnnypixelz.utilizer.config;

import com.cryptomorin.xseries.messages.ActionBar;
import com.cryptomorin.xseries.messages.Titles;
import io.github.johnnypixelz.utilizer.text.Colors;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class Message {
    private String message;
    private List<String> messageList;
    private Sound sound;
    private String title;
    private String subtitle;
    private String actionbar;

    @NotNull
    public Message send(@NotNull CommandSender commandSender) {
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
            if (subtitle != null) {
                Titles.sendTitle(player, Colors.color(title), Colors.color(subtitle));
            } else {
                Titles.sendTitle(player, Colors.color(title), null);
            }
        }

        if (actionbar != null) {
            ActionBar.sendActionBar(player, Colors.color(actionbar));
        }

        return this;
    }

    @NotNull
    public Message send(@NotNull List<CommandSender> commandSenders) {
        for (CommandSender commandSender : commandSenders) {
            send(commandSender);
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
}
