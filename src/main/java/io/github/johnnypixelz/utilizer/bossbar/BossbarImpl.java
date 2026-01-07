package io.github.johnnypixelz.utilizer.bossbar;

import io.github.johnnypixelz.utilizer.depend.Placeholders;
import io.github.johnnypixelz.utilizer.tasks.Tasks;
import io.github.johnnypixelz.utilizer.text.Colors;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Internal implementation of the Bossbar interface.
 * Creates per-player BossBar instances to support placeholder resolution.
 */
class BossbarImpl implements Bossbar {

    private final String id;
    private String titleTemplate;
    private double progress;
    private BarColor color;
    private BarStyle style;
    private final Set<BarFlag> flags;

    // Per-player Bukkit BossBar instances (for placeholder resolution)
    private final Map<UUID, BossBar> playerBars;

    private BukkitTask autoUpdateTask;
    private boolean destroyed;

    BossbarImpl(@NotNull String title) {
        this.id = UUID.randomUUID().toString();
        this.titleTemplate = Objects.requireNonNull(title, "Title cannot be null");
        this.progress = 1.0;
        this.color = BarColor.PURPLE;
        this.style = BarStyle.SOLID;
        this.flags = EnumSet.noneOf(BarFlag.class);
        this.playerBars = new HashMap<>();
        this.destroyed = false;

        // Register with service for tracking
        Bossbars.service().register(this);
    }

    @Override
    @NotNull
    public String getId() {
        return id;
    }

    @Override
    @NotNull
    public Bossbar title(@NotNull String title) {
        this.titleTemplate = Objects.requireNonNull(title, "Title cannot be null");
        // Update title for all viewing players
        for (Map.Entry<UUID, BossBar> entry : playerBars.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null && player.isOnline()) {
                entry.getValue().setTitle(processTitle(player));
            }
        }
        return this;
    }

    @Override
    @NotNull
    public String getTitle() {
        return titleTemplate;
    }

    @Override
    @NotNull
    public Bossbar progress(double progress) {
        if (progress < 0.0 || progress > 1.0) {
            throw new IllegalArgumentException("Progress must be between 0.0 and 1.0");
        }
        this.progress = progress;
        // Update progress for all viewing players
        for (BossBar bar : playerBars.values()) {
            bar.setProgress(progress);
        }
        return this;
    }

    @Override
    public double getProgress() {
        return progress;
    }

    @Override
    @NotNull
    public Bossbar color(@NotNull BarColor color) {
        this.color = Objects.requireNonNull(color, "Color cannot be null");
        // Update color for all viewing players
        for (BossBar bar : playerBars.values()) {
            bar.setColor(color);
        }
        return this;
    }

    @Override
    @NotNull
    public BarColor getColor() {
        return color;
    }

    @Override
    @NotNull
    public Bossbar style(@NotNull BarStyle style) {
        this.style = Objects.requireNonNull(style, "Style cannot be null");
        // Update style for all viewing players
        for (BossBar bar : playerBars.values()) {
            bar.setStyle(style);
        }
        return this;
    }

    @Override
    @NotNull
    public BarStyle getStyle() {
        return style;
    }

    @Override
    @NotNull
    public Bossbar addFlag(@NotNull BarFlag flag) {
        Objects.requireNonNull(flag, "Flag cannot be null");
        flags.add(flag);
        // Add flag to all viewing players
        for (BossBar bar : playerBars.values()) {
            bar.addFlag(flag);
        }
        return this;
    }

    @Override
    @NotNull
    public Bossbar removeFlag(@NotNull BarFlag flag) {
        Objects.requireNonNull(flag, "Flag cannot be null");
        flags.remove(flag);
        // Remove flag from all viewing players
        for (BossBar bar : playerBars.values()) {
            bar.removeFlag(flag);
        }
        return this;
    }

    @Override
    @NotNull
    public Bossbar flags(@NotNull BarFlag... flags) {
        Objects.requireNonNull(flags, "Flags cannot be null");
        this.flags.clear();
        this.flags.addAll(Arrays.asList(flags));
        // Update flags for all viewing players
        for (BossBar bar : playerBars.values()) {
            // Clear existing flags
            for (BarFlag flag : BarFlag.values()) {
                bar.removeFlag(flag);
            }
            // Add new flags
            for (BarFlag flag : this.flags) {
                bar.addFlag(flag);
            }
        }
        return this;
    }

    @Override
    public boolean hasFlag(@NotNull BarFlag flag) {
        Objects.requireNonNull(flag, "Flag cannot be null");
        return flags.contains(flag);
    }

    @Override
    public void show(@NotNull Player player) {
        Objects.requireNonNull(player, "Player cannot be null");
        if (destroyed) {
            throw new IllegalStateException("Cannot show destroyed bossbar");
        }

        BossBar bar = playerBars.get(player.getUniqueId());
        if (bar == null) {
            // Create new BossBar for this player
            bar = Bukkit.createBossBar(
                    processTitle(player),
                    color,
                    style,
                    flags.toArray(new BarFlag[0])
            );
            bar.setProgress(progress);
            playerBars.put(player.getUniqueId(), bar);

            // Register with service
            Bossbars.service().registerPlayer(player, this);
        } else {
            // Update existing bar
            bar.setTitle(processTitle(player));
            bar.setProgress(progress);
            bar.setColor(color);
            bar.setStyle(style);
        }

        // Show to player
        bar.addPlayer(player);
    }

    @Override
    public void hide(@NotNull Player player) {
        Objects.requireNonNull(player, "Player cannot be null");

        BossBar bar = playerBars.remove(player.getUniqueId());
        if (bar != null) {
            bar.removePlayer(player);
            bar.removeAll();
            // Unregister from service
            Bossbars.service().unregisterPlayer(player, this);
        }
    }

    @Override
    public void update(@NotNull Player player) {
        Objects.requireNonNull(player, "Player cannot be null");

        BossBar bar = playerBars.get(player.getUniqueId());
        if (bar != null) {
            bar.setTitle(processTitle(player));
            bar.setProgress(progress);
            bar.setColor(color);
            bar.setStyle(style);
        }
    }

    @Override
    public void updateAll() {
        for (UUID uuid : playerBars.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                update(player);
            }
        }
    }

    @Override
    public boolean isShowing(@NotNull Player player) {
        Objects.requireNonNull(player, "Player cannot be null");
        return playerBars.containsKey(player.getUniqueId());
    }

    @Override
    @NotNull
    public Set<Player> getViewers() {
        Set<Player> viewers = new HashSet<>();
        for (UUID uuid : playerBars.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                viewers.add(player);
            }
        }
        return Collections.unmodifiableSet(viewers);
    }

    @Override
    @NotNull
    public Bossbar autoUpdate(long intervalTicks) {
        if (destroyed) {
            throw new IllegalStateException("Cannot auto-update destroyed bossbar");
        }
        // Stop existing task if any
        stopAutoUpdate();
        // Start new task
        autoUpdateTask = Tasks.sync().timer(this::updateAll, intervalTicks);
        return this;
    }

    @Override
    @NotNull
    public Bossbar stopAutoUpdate() {
        if (autoUpdateTask != null) {
            autoUpdateTask.cancel();
            autoUpdateTask = null;
        }
        return this;
    }

    @Override
    public boolean isAutoUpdating() {
        return autoUpdateTask != null && !autoUpdateTask.isCancelled();
    }

    @Override
    public void destroy() {
        if (destroyed) {
            return;
        }
        destroyed = true;

        // Stop auto-update task
        stopAutoUpdate();

        // Hide from all players and clean up bars
        for (Map.Entry<UUID, BossBar> entry : new HashMap<>(playerBars).entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            BossBar bar = entry.getValue();
            bar.removeAll();
            if (player != null) {
                Bossbars.service().unregisterPlayer(player, this);
            }
        }
        playerBars.clear();

        // Unregister from service
        Bossbars.service().unregister(this);
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    // ==================== Internal Methods ====================

    /**
     * Called by BossbarService when a player quits.
     */
    void onPlayerQuit(@NotNull Player player) {
        BossBar bar = playerBars.remove(player.getUniqueId());
        if (bar != null) {
            bar.removeAll();
        }
    }

    private String processTitle(@NotNull Player player) {
        return Colors.color(Placeholders.set(player, titleTemplate));
    }

}
