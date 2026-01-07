package io.github.johnnypixelz.utilizer.input;

import io.github.johnnypixelz.utilizer.plugin.Provider;
import io.github.johnnypixelz.utilizer.tasks.Tasks;
import io.github.johnnypixelz.utilizer.text.Colors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import org.jetbrains.annotations.NotNull;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ChatInput<T> implements Listener {

    private final Player player;
    private Consumer<T> messageHandler;
    private Runnable cancelHandler;
    private String cancelWord;
    private long timeoutTicks = -1;
    private BukkitTask timeoutTask;
    private Function<String, T> parser;
    private Predicate<T> validator;
    private String errorMessage;
    private boolean retryOnInvalid = false;
    private boolean runSync = true;

    private ChatInput(@NotNull Player player) {
        this.player = Objects.requireNonNull(player, "Player cannot be null");
    }

    public static ChatInput<String> of(@NotNull Player player) {
        ChatInput<String> input = new ChatInput<>(player);
        input.parser = Function.identity();
        return input;
    }

    public ChatInput<T> onMessage(@NotNull Consumer<T> handler) {
        this.messageHandler = Objects.requireNonNull(handler, "Message handler cannot be null");
        return this;
    }

    public ChatInput<T> onCancel(@NotNull Runnable handler) {
        this.cancelHandler = Objects.requireNonNull(handler, "Cancel handler cannot be null");
        return this;
    }

    public ChatInput<T> timeout(long duration, @NotNull TimeUnit unit) {
        Objects.requireNonNull(unit, "TimeUnit cannot be null");
        this.timeoutTicks = unit.toSeconds(duration) * 20;
        return this;
    }

    public ChatInput<T> cancelWord() {
        return cancelWord("cancel");
    }

    public ChatInput<T> cancelWord(@NotNull String word) {
        this.cancelWord = Objects.requireNonNull(word, "Cancel word cannot be null");
        return this;
    }

    public ChatInput<T> retryOnInvalid() {
        this.retryOnInvalid = true;
        return this;
    }

    // String validation methods (only valid for ChatInput<String>)
    @SuppressWarnings("unchecked")
    public ChatInput<String> validate(@NotNull String errorMessage, @NotNull Predicate<String> validator) {
        this.errorMessage = Objects.requireNonNull(errorMessage, "Error message cannot be null");
        this.validator = (Predicate<T>) Objects.requireNonNull(validator, "Validator cannot be null");
        return (ChatInput<String>) this;
    }

    public ChatInput<String> validateEnum(@NotNull String errorMessage, @NotNull String... options) {
        return validateEnum(errorMessage, Arrays.asList(options));
    }

    public ChatInput<String> validateEnum(@NotNull String errorMessage, @NotNull List<String> options) {
        Objects.requireNonNull(options, "Options cannot be null");
        Set<String> optionSet = options.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        return validate(errorMessage, msg -> optionSet.contains(msg.toLowerCase()));
    }

    // Generic parsing method
    public <R> ChatInput<R> parseAs(@NotNull String errorMessage, @NotNull Function<String, R> parser) {
        Objects.requireNonNull(errorMessage, "Error message cannot be null");
        Objects.requireNonNull(parser, "Parser cannot be null");

        ChatInput<R> typed = new ChatInput<>(this.player);
        typed.copySettingsFrom(this);
        typed.errorMessage = errorMessage;
        typed.parser = msg -> {
            try {
                return parser.apply(msg);
            } catch (Exception e) {
                return null;
            }
        };
        return typed;
    }

    // Typed parsing methods
    public ChatInput<Integer> parseAsInt(@NotNull String errorMessage) {
        return parseAs(errorMessage, Integer::parseInt);
    }

    public ChatInput<Integer> parseAsInt(@NotNull String errorMessage, int min, int max) {
        ChatInput<Integer> typed = parseAs(errorMessage, Integer::parseInt);
        typed.validator = value -> value >= min && value <= max;
        return typed;
    }

    public ChatInput<Long> parseAsLong(@NotNull String errorMessage) {
        return parseAs(errorMessage, Long::parseLong);
    }

    public ChatInput<Long> parseAsLong(@NotNull String errorMessage, long min, long max) {
        ChatInput<Long> typed = parseAs(errorMessage, Long::parseLong);
        typed.validator = value -> value >= min && value <= max;
        return typed;
    }

    public ChatInput<Double> parseAsDouble(@NotNull String errorMessage) {
        return parseAs(errorMessage, Double::parseDouble);
    }

    public ChatInput<Double> parseAsDouble(@NotNull String errorMessage, double min, double max) {
        ChatInput<Double> typed = parseAs(errorMessage, Double::parseDouble);
        typed.validator = value -> value >= min && value <= max;
        return typed;
    }

    private void copySettingsFrom(ChatInput<?> other) {
        this.messageHandler = null;
        this.cancelHandler = other.cancelHandler;
        this.cancelWord = other.cancelWord;
        this.timeoutTicks = other.timeoutTicks;
        this.retryOnInvalid = other.retryOnInvalid;
        this.runSync = other.runSync;
    }

    public ChatInput<T> sync() {
        this.runSync = true;
        register();
        return this;
    }

    public ChatInput<T> async() {
        this.runSync = false;
        register();
        return this;
    }

    public void cancel() {
        unregister();
    }

    private void register() {
        Bukkit.getPluginManager().registerEvents(this, Provider.getPlugin());

        if (timeoutTicks > 0) {
            timeoutTask = Tasks.sync().delayed(this::handleTimeout, timeoutTicks);
        }
    }

    private void unregister() {
        HandlerList.unregisterAll(this);
        if (timeoutTask != null) {
            timeoutTask.cancel();
            timeoutTask = null;
        }
    }

    private void handleTimeout() {
        unregister();
        if (cancelHandler != null) {
            cancelHandler.run();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onChat(AsyncPlayerChatEvent event) {
        if (!player.getUniqueId().equals(event.getPlayer().getUniqueId())) return;

        String message = event.getMessage();
        event.setCancelled(true);

        // Check cancel word
        if (cancelWord != null && message.equalsIgnoreCase(cancelWord)) {
            unregister();
            if (cancelHandler != null) {
                if (runSync) Tasks.sync().run(() -> cancelHandler.run());
                else cancelHandler.run();
            }
            return;
        }

        // Parse
        T parsed;
        try {
            parsed = parser.apply(message);
            if (parsed == null) throw new RuntimeException("Parse returned null");
        } catch (Exception e) {
            if (errorMessage != null) {
                player.sendMessage(Colors.color(errorMessage));
            }
            if (retryOnInvalid) return;
            unregister();
            return;
        }

        // Validate parsed value
        if (validator != null && !validator.test(parsed)) {
            if (errorMessage != null) {
                player.sendMessage(Colors.color(errorMessage));
            }
            if (retryOnInvalid) return;
            unregister();
            return;
        }

        // Success
        unregister();
        if (messageHandler != null) {
            if (runSync) Tasks.sync().run(() -> messageHandler.accept(parsed));
            else messageHandler.accept(parsed);
        }
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        if (player.getUniqueId().equals(event.getPlayer().getUniqueId())) {
            unregister();
            if (cancelHandler != null) {
                if (runSync) Tasks.sync().run(() -> cancelHandler.run());
                else cancelHandler.run();
            }
        }
    }

}
