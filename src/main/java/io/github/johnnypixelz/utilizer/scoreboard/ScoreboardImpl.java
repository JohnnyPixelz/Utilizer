package io.github.johnnypixelz.utilizer.scoreboard;

import io.github.johnnypixelz.utilizer.depend.Placeholders;
import io.github.johnnypixelz.utilizer.tasks.Tasks;
import io.github.johnnypixelz.utilizer.text.Colors;
import io.github.johnnypixelz.utilizer.version.Versions;
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

    // Legacy team prefix/suffix character cap enforced by CraftTeam before Minecraft 1.20.1.
    private static final int LEGACY_PREFIX_LIMIT = 64;

    // The 64-char prefix/suffix cap was removed in Minecraft 1.20.1 (CraftTeam#setPrefix, Spigot and
    // Paper alike). From 1.20.1 the prefix is an unbounded chat component and the whole line is written
    // at once; below 1.20.1 the cap is still enforced, so the line is split across prefix and suffix.
    private static final boolean PREFIX_LENGTH_LIMITED = Versions.isBelow(1, 20, 1);

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

        // Write the whole line into the team prefix (see applyLineText).
        applyLineText(team, processed);

        // Set the score (this makes the line visible)
        data.objective.getScore(entry).setScore(score);
    }

    /**
     * Writes the fully-processed line into the team.
     * <p>
     * From Minecraft 1.20.1 onward a team's prefix is an unbounded chat component: {@code
     * CraftTeam#setPrefix} only null-checks the string and then parses it in full via {@code
     * CraftChatMessage} (§x hex-gradient sequences included), on both Spigot and Paper, so the entire
     * line goes into the prefix and renders intact however long it is.
     * <p>
     * Before 1.20.1 CraftTeam caps prefix/suffix at {@value #LEGACY_PREFIX_LIMIT} characters, so the
     * line is split across prefix and suffix on a colour-code boundary — never inside a §x hex sequence,
     * so it is not corrupted like the old fixed-offset split did. Each character of a hex gradient
     * carries its own colour, so the suffix resumes correctly; anything past what prefix + suffix hold
     * is truncated. Keeping lines short enough on legacy servers is left to the developer.
     * <p>
     * Example — gradient "Herobrine" serialises to 135 legacy chars (each visible character is prefixed
     * by its own §x§R§R§G§G§B§B colour, i.e. up to 15 chars per character):
     * <pre>
     *   on >= 1.20.1:  setPrefix gets all 135 chars  ->  "Herobrine"  (full gradient)
     *   on <  1.20.1:  prefix (60 chars) -> "Hero",  suffix (60 chars) -> "brin"
     *                  result -> "Herobrin"   ("e" is past the ~128-char prefix+suffix room, dropped)
     * </pre>
     * The cut falls between whole §x groups, so every shown character keeps its own colour, never the
     * corrupted mid-sequence colour the old fixed-offset split produced.
     */
    private void applyLineText(@NotNull Team team, @NotNull String processed) {
        if (!PREFIX_LENGTH_LIMITED) {
            team.setPrefix(processed);
            team.setSuffix("");
            return;
        }

        String prefix = trimToBoundary(processed, LEGACY_PREFIX_LIMIT);
        String remainder = processed.substring(prefix.length());
        team.setPrefix(prefix);
        team.setSuffix(remainder.isEmpty() ? "" : trimToBoundary(remainder, LEGACY_PREFIX_LIMIT));
    }

    /**
     * Returns the longest leading portion of {@code text} that is at most {@code cap} characters long
     * and never ends inside a colour code or a §x hex sequence. It walks whole tokens (14 chars for a
     * §x hex colour, 2 for a §-code, 1 for a visible char) and stops before the first token that would
     * cross {@code cap}.
     * <p>
     * Example — a hex gradient prefixes every visible character with its own 14-char §x§R§R§G§G§B§B
     * colour, so trimming a "Herobrine" gradient at cap 64 walks whole tokens:
     * <pre>
     *   §x(->14) H(->15) §x(->29) e(->30) §x(->44) r(->45) §x(->59) o(->60)   (all fit in 64)
     *   the next §x would reach 74 > 64   ->   stop, cut at 60
     *   returns the 60-char prefix (shows "Hero"); the cut is between §x groups, never inside one
     * </pre>
     */
    private static String trimToBoundary(@NotNull String text, int cap) {
        int i = 0;
        int lastBoundary = 0;
        while (i < text.length()) {
            int token = tokenLength(text, i);
            if (i + token > cap) {
                break;
            }
            i += token;
            lastBoundary = i;
        }
        return text.substring(0, lastBoundary);
    }

    /** Length of the formatting/character token starting at {@code i}: 14 for a §x hex sequence, 2 for a §code, else 1. */
    private static int tokenLength(@NotNull String text, int i) {
        if (text.charAt(i) == ChatColor.COLOR_CHAR && i + 1 < text.length()) {
            char next = text.charAt(i + 1);
            if ((next == 'x' || next == 'X') && i + 14 <= text.length()) {
                return 14; // §x§R§R§G§G§B§B
            }
            return 2; // §<code>
        }
        return 1;
    }

    private void removeLine(@NotNull PlayerScoreboardData data, int index) {
        String entry = LINE_IDENTIFIERS[index];
        data.scoreboard.resetScores(entry);
    }

    private String processPlaceholders(@NotNull Player player, @NotNull String text) {
        return Placeholders.set(player, text);
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
