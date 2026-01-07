package io.github.johnnypixelz.utilizer.scoreboard;

import io.github.johnnypixelz.utilizer.depend.Dependencies;
import io.github.johnnypixelz.utilizer.tasks.Tasks;
import io.github.johnnypixelz.utilizer.text.Colors;
import io.papermc.paper.scoreboard.numbers.NumberFormat;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Internal implementation of the Scoreboard interface.
 * Uses Team prefix/suffix technique for flicker-free updates.
 */
class ScoreboardImpl implements Scoreboard {

    // Unique invisible characters for each line (using color codes)
    private static final String[] LINE_IDENTIFIERS = new String[MAX_LINES];

    static {
        // Generate unique invisible strings using color code combinations
        // Each line gets a unique ChatColor combination that's invisible but unique
        for (int i = 0; i < MAX_LINES; i++) {
            // Use combinations like "§0§r", "§1§r", etc.
            LINE_IDENTIFIERS[i] = "" + ChatColor.COLOR_CHAR + Integer.toHexString(i) + ChatColor.RESET;
        }
    }

    private final String id;
    private String title;
    private final String[] lines;
    private final Map<UUID, PlayerScoreboardData> playerData;
    private boolean destroyed;
    private BukkitTask autoUpdateTask;

    ScoreboardImpl(@NotNull String title) {
        this.id = UUID.randomUUID().toString();
        this.title = Objects.requireNonNull(title, "Title cannot be null");
        this.lines = new String[MAX_LINES];
        this.playerData = new HashMap<>();
        this.destroyed = false;

        // Register with service for tracking
        Scoreboards.service().register(this);
    }

    @Override
    @NotNull
    public String getId() {
        return id;
    }

    @Override
    @NotNull
    public Scoreboard title(@NotNull String title) {
        this.title = Objects.requireNonNull(title, "Title cannot be null");
        // Update title for all viewing players
        for (Map.Entry<UUID, PlayerScoreboardData> entry : playerData.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null && player.isOnline()) {
                String processedTitle = Colors.color(processPlaceholders(player, this.title));
                entry.getValue().objective.setDisplayName(processedTitle);
            }
        }
        return this;
    }

    @Override
    @NotNull
    public String getTitle() {
        return title;
    }

    @Override
    @NotNull
    public Scoreboard line(int index, @NotNull String text) {
        validateIndex(index);
        lines[index] = Objects.requireNonNull(text, "Text cannot be null");
        // Update this line for all viewing players
        for (Map.Entry<UUID, PlayerScoreboardData> entry : playerData.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null && player.isOnline()) {
                updateLine(player, entry.getValue(), index);
            }
        }
        return this;
    }

    @Override
    @NotNull
    public Scoreboard lines(@NotNull List<String> lines) {
        Objects.requireNonNull(lines, "Lines cannot be null");
        if (lines.size() > MAX_LINES) {
            throw new IllegalArgumentException("Cannot have more than " + MAX_LINES + " lines");
        }
        // Clear existing lines
        Arrays.fill(this.lines, null);
        // Set new lines
        for (int i = 0; i < lines.size(); i++) {
            this.lines[i] = lines.get(i);
        }
        // Update all viewers
        updateAll();
        return this;
    }

    @Override
    @NotNull
    public Scoreboard lines(@NotNull String... lines) {
        return lines(Arrays.asList(lines));
    }

    @Override
    @Nullable
    public String getLine(int index) {
        validateIndex(index);
        return lines[index];
    }

    @Override
    @NotNull
    public List<String> getLines() {
        return Collections.unmodifiableList(Arrays.asList(lines.clone()));
    }

    @Override
    @NotNull
    public Scoreboard removeLine(int index) {
        validateIndex(index);
        lines[index] = null;
        // Remove from all viewing players
        for (PlayerScoreboardData data : playerData.values()) {
            removeLine(data, index);
        }
        return this;
    }

    @Override
    @NotNull
    public Scoreboard clearLines() {
        Arrays.fill(lines, null);
        // Clear all lines for all viewers
        for (PlayerScoreboardData data : playerData.values()) {
            for (int i = 0; i < MAX_LINES; i++) {
                removeLine(data, i);
            }
        }
        return this;
    }

    @Override
    public void show(@NotNull Player player) {
        Objects.requireNonNull(player, "Player cannot be null");
        if (destroyed) {
            throw new IllegalStateException("Cannot show destroyed scoreboard");
        }

        // Hide any existing scoreboard from this player (from any Scoreboard instance)
        Scoreboards.service().hideFromPlayer(player);

        // Create new scoreboard for this player
        org.bukkit.scoreboard.Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective(
                "sb_" + id.substring(0, 8),
                "dummy",
                Colors.color(processPlaceholders(player, title))
        );
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        hideScoreNumbers(objective);

        // Store player data
        PlayerScoreboardData data = new PlayerScoreboardData(scoreboard, objective);
        playerData.put(player.getUniqueId(), data);

        // Create teams for each line (for flicker-free updates)
        for (int i = 0; i < MAX_LINES; i++) {
            Team team = scoreboard.registerNewTeam("line_" + i);
            team.addEntry(LINE_IDENTIFIERS[i]);
            data.teams[i] = team;
        }

        // Set the scoreboard on the player
        player.setScoreboard(scoreboard);

        // Register with service
        Scoreboards.service().registerPlayer(player, this);

        // Render initial lines
        for (int i = 0; i < MAX_LINES; i++) {
            if (lines[i] != null) {
                updateLine(player, data, i);
            }
        }
    }

    @Override
    public void hide(@NotNull Player player) {
        Objects.requireNonNull(player, "Player cannot be null");

        PlayerScoreboardData data = playerData.remove(player.getUniqueId());
        if (data != null) {
            // Restore main scoreboard
            if (player.isOnline()) {
                player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            }
            // Unregister from service
            Scoreboards.service().unregisterPlayer(player);
        }
    }

    @Override
    public void update(@NotNull Player player) {
        Objects.requireNonNull(player, "Player cannot be null");

        PlayerScoreboardData data = playerData.get(player.getUniqueId());
        if (data == null) {
            return;
        }

        // Update title
        data.objective.setDisplayName(Colors.color(processPlaceholders(player, title)));

        // Update all lines
        for (int i = 0; i < MAX_LINES; i++) {
            if (lines[i] != null) {
                updateLine(player, data, i);
            } else {
                removeLine(data, i);
            }
        }
    }

    @Override
    public void updateAll() {
        for (UUID uuid : playerData.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                update(player);
            }
        }
    }

    @Override
    public boolean isShowing(@NotNull Player player) {
        Objects.requireNonNull(player, "Player cannot be null");
        return playerData.containsKey(player.getUniqueId());
    }

    @Override
    @NotNull
    public Set<Player> getViewers() {
        Set<Player> viewers = new HashSet<>();
        for (UUID uuid : playerData.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                viewers.add(player);
            }
        }
        return Collections.unmodifiableSet(viewers);
    }

    @Override
    public void destroy() {
        if (destroyed) {
            return;
        }
        destroyed = true;

        // Stop auto-update task
        stopAutoUpdate();

        // Hide from all players
        for (UUID uuid : new HashSet<>(playerData.keySet())) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                hide(player);
            }
        }
        playerData.clear();

        // Unregister from service
        Scoreboards.service().unregister(this);
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    @NotNull
    public Scoreboard autoUpdate(long intervalTicks) {
        if (destroyed) {
            throw new IllegalStateException("Cannot auto-update destroyed scoreboard");
        }
        // Stop existing task if any
        stopAutoUpdate();
        // Start new task
        autoUpdateTask = Tasks.sync().timer(this::updateAll, intervalTicks);
        return this;
    }

    @Override
    @NotNull
    public Scoreboard stopAutoUpdate() {
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

    // ==================== Internal Methods ====================

    /**
     * Called by ScoreboardService when a player quits.
     */
    void onPlayerQuit(@NotNull Player player) {
        playerData.remove(player.getUniqueId());
    }

    private void validateIndex(int index) {
        if (index < 0 || index >= MAX_LINES) {
            throw new IllegalArgumentException("Line index must be between 0 and " + (MAX_LINES - 1));
        }
    }

    private void updateLine(@NotNull Player player, @NotNull PlayerScoreboardData data, int index) {
        String text = lines[index];
        if (text == null) {
            removeLine(data, index);
            return;
        }

        // Process placeholders and colors
        String processed = Colors.color(processPlaceholders(player, text));

        // Score determines position: higher score = higher on sidebar
        // We use (MAX_LINES - index) so index 0 appears at top
        int score = MAX_LINES - index;

        // Get the team for this line
        Team team = data.teams[index];
        String entry = LINE_IDENTIFIERS[index];

        // For modern MC (1.13+), prefix can hold up to 64 chars
        // Split if needed for longer text
        if (processed.length() <= 64) {
            team.setPrefix(processed);
            team.setSuffix("");
        } else {
            // Split intelligently to preserve color codes
            String prefix = processed.substring(0, 64);
            String suffix = processed.substring(64);

            // Get last color from prefix to apply to suffix
            String lastColors = ChatColor.getLastColors(prefix);
            suffix = lastColors + suffix;
            if (suffix.length() > 64) {
                suffix = suffix.substring(0, 64);
            }

            team.setPrefix(prefix);
            team.setSuffix(suffix);
        }

        // Set the score (this makes the line visible)
        data.objective.getScore(entry).setScore(score);
    }

    private void removeLine(@NotNull PlayerScoreboardData data, int index) {
        String entry = LINE_IDENTIFIERS[index];
        data.scoreboard.resetScores(entry);
    }

    private String processPlaceholders(@NotNull Player player, @NotNull String text) {
        return Dependencies.getPlaceholderAPI()
                .map(papi -> papi.setPlaceholders(player, text))
                .orElse(text);
    }

    /**
     * Hides the score numbers on the right side of the scoreboard.
     * Only works on Paper 1.20.3+, silently ignored on older versions or Spigot.
     */
    private void hideScoreNumbers(@NotNull Objective objective) {
        try {
            objective.numberFormat(NumberFormat.blank());
        } catch (NoSuchMethodError | NoClassDefFoundError ignored) {
            // Not supported on this server version (pre-1.20.3 or Spigot)
        }
    }

    /**
     * Internal class to store per-player scoreboard data.
     */
    private static class PlayerScoreboardData {
        final org.bukkit.scoreboard.Scoreboard scoreboard;
        final Objective objective;
        final Team[] teams;

        PlayerScoreboardData(org.bukkit.scoreboard.Scoreboard scoreboard, Objective objective) {
            this.scoreboard = scoreboard;
            this.objective = objective;
            this.teams = new Team[MAX_LINES];
        }
    }

}
