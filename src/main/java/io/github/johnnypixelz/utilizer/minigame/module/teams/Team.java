package io.github.johnnypixelz.utilizer.minigame.module.teams;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Team {
    private final String id;
    private String friendlyName;
    private final List<UUID> players;
    private final int playerLimit;
    private ChatColor color;

    public Team(String id) {
        this(id, -1);
    }

    public Team(String id, int playerLimit) {
        this.id = id;
        this.players = new ArrayList<>();
        this.playerLimit = playerLimit;
        this.friendlyName = id;
        this.color = null;
    }

    public String getId() {
        return id;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public Team setFriendlyName(String name) {
        this.friendlyName = name;
        return this;
    }

    public ChatColor getColor() {
        return color;
    }

    public Team setColor(ChatColor color) {
        this.color = color;
        return this;
    }

    public int getPlayerLimit() {
        return playerLimit;
    }

    public boolean hasPlayerLimit() {
        return playerLimit != -1;
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public List<UUID> getOnlinePlayers() {
        return players.stream()
                .filter(uuid -> Bukkit.getPlayer(uuid).isOnline())
                .collect(Collectors.toList());
    }

    public List<Player> getPlayerObjects() {
        return players.stream()
                .map(Bukkit::getPlayer)
                .collect(Collectors.toList());
    }

    public List<Player> getOnlinePlayerObjects() {
        return players.stream()
                .map(Bukkit::getPlayer)
                .filter(OfflinePlayer::isOnline)
                .collect(Collectors.toList());
    }

    public void addPlayer(Player player) {
        players.add(player.getUniqueId());
    }

    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
    }

    public boolean areTeammates(Player one, Player two) {
        return containsPlayer(one) && containsPlayer(two);
    }

    public boolean containsPlayer(Player player) {
        return players.contains(player.getUniqueId());
    }

}
